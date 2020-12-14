/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.repository.metric;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.dashboard.es.dao.MetricRepository;
import com.alibaba.csp.sentinel.dashboard.es.util.CreateIdTool;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.csp.sentinel.util.TimeUtil;
import com.alibaba.csp.sentinel.log.RecordLog;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Caches metrics data in a period of time in memory.（优先从es取值，若es取值失败，再查map）
 *
 * @author Carpenter Lee
 * @author Eric Zhao
 */
@Component
public class InMemoryMetricsRepository implements MetricsRepository<MetricEntity> {

    private static final long MAX_METRIC_LIVE_TIME_MS = 1000 * 60 * 5;

    @Resource
    MetricRepository metricRepository;

    /**
     * {@code app -> resource -> timestamp -> metric}
     */
    private Map<String, Map<String, LinkedHashMap<Long, MetricEntity>>> allMetrics = new ConcurrentHashMap<>();

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();


    @Override
    public void save(MetricEntity entity) {
        if (entity == null || StringUtil.isBlank(entity.getApp())) {
            return;
        }
        readWriteLock.writeLock().lock();
        try {
            if (entity.getId() == null) {
                entity.setId(CreateIdTool.nextId());
            }
            try {
                metricRepository.save(entity);
            } catch (Exception e) {
                RecordLog.info("es存储异常");
            }

            allMetrics.computeIfAbsent(entity.getApp(), e -> new HashMap<>(16))
                    .computeIfAbsent(entity.getResource(), e -> new LinkedHashMap<Long, MetricEntity>() {
                        @Override
                        protected boolean removeEldestEntry(Entry<Long, MetricEntity> eldest) {
                            // Metric older than {@link #MAX_METRIC_LIVE_TIME_MS} will be removed.
                            return eldest.getKey() < TimeUtil.currentTimeMillis() - MAX_METRIC_LIVE_TIME_MS;
                        }
                    }).put(entity.getTimestamp().getTime(), entity);
        } finally {
            readWriteLock.writeLock().unlock();
        }

    }

    @Override
    public void saveAll(Iterable<MetricEntity> metrics) {
        if (metrics == null) {
            return;
        }
        readWriteLock.writeLock().lock();
        try {
            metrics.forEach(this::save);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public List<MetricEntity> queryByAppAndResourceBetween(String app, String resource,
                                                           long startTime, long endTime) {
        List<MetricEntity> results = new ArrayList<>();
        if (StringUtil.isBlank(app)) {
            return results;
        }

        try {
            results = metricRepository.findByAppAndResourceAndGmtCreateBetweenOrderByGmtCreateAsc(app, resource.replaceAll("/", "\\\\/"), startTime, endTime);
            return results;
        } catch (Exception e) {
            results = new ArrayList<>();
            RecordLog.info("es读取异常");
        }

        Map<String, LinkedHashMap<Long, MetricEntity>> resourceMap = allMetrics.get(app);
        if (resourceMap == null) {
            return results;
        }
        LinkedHashMap<Long, MetricEntity> metricsMap = resourceMap.get(resource);
        if (metricsMap == null) {
            return results;
        }
        readWriteLock.readLock().lock();
        try {
            for (Entry<Long, MetricEntity> entry : metricsMap.entrySet()) {
                if (entry.getKey() >= startTime && entry.getKey() <= endTime) {
                    results.add(entry.getValue());
                }
            }
            return results;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<String> listResourcesOfApp(String app) {
        List<String> results = new ArrayList<>();
        if (StringUtil.isBlank(app)) {
            return results;
        }
        final long minTimeMs = System.currentTimeMillis() - 1000 * 60;
        Map<String, MetricEntity> resourceCount = new ConcurrentHashMap<>(32);


        List<MetricEntity> metricEntityList = new ArrayList<>();
        try {
            metricEntityList = metricRepository.findByAppAndTimestampAfter(app, minTimeMs);
        } catch (Exception e) {
            metricEntityList = null;
            RecordLog.info("es读取异常");
        }

        readWriteLock.readLock().lock();
        try {
            if (metricEntityList != null) {
                for (MetricEntity metricEntity : metricEntityList) {
                    if (resourceCount.containsKey(metricEntity.getResource())) {
                        MetricEntity oldEntity = resourceCount.get(metricEntity.getResource());
                        oldEntity.addPassQps(metricEntity.getPassQps());
                        oldEntity.addRtAndSuccessQps(metricEntity.getRt(), metricEntity.getSuccessQps());
                        oldEntity.addBlockQps(metricEntity.getBlockQps());
                        oldEntity.addExceptionQps(metricEntity.getExceptionQps());
                        oldEntity.addCount(1);
                    } else {
                        resourceCount.put(metricEntity.getResource(), MetricEntity.copyOf(metricEntity));
                    }
                }
            } else {
                // resource -> timestamp -> metric
                Map<String, LinkedHashMap<Long, MetricEntity>> resourceMap = allMetrics.get(app);
                if (resourceMap == null) {
                    return results;
                }
                for (Entry<String, LinkedHashMap<Long, MetricEntity>> resourceMetrics : resourceMap.entrySet()) {
                    for (Entry<Long, MetricEntity> metrics : resourceMetrics.getValue().entrySet()) {
                        if (metrics.getKey() < minTimeMs) {
                            continue;
                        }
                        MetricEntity newEntity = metrics.getValue();
                        if (resourceCount.containsKey(resourceMetrics.getKey())) {
                            MetricEntity oldEntity = resourceCount.get(resourceMetrics.getKey());
                            oldEntity.addPassQps(newEntity.getPassQps());
                            oldEntity.addRtAndSuccessQps(newEntity.getRt(), newEntity.getSuccessQps());
                            oldEntity.addBlockQps(newEntity.getBlockQps());
                            oldEntity.addExceptionQps(newEntity.getExceptionQps());
                            oldEntity.addCount(1);
                        } else {
                            resourceCount.put(resourceMetrics.getKey(), MetricEntity.copyOf(newEntity));
                        }
                    }
                }
            }

            // Order by last minute b_qps DESC.
            return resourceCount.entrySet()
                    .stream()
                    .sorted((o1, o2) -> {
                        MetricEntity e1 = o1.getValue();
                        MetricEntity e2 = o2.getValue();
                        int t = e2.getBlockQps().compareTo(e1.getBlockQps());
                        if (t != 0) {
                            return t;
                        }
                        return e2.getPassQps().compareTo(e1.getPassQps());
                    })
                    .map(Entry::getKey)
                    .collect(Collectors.toList());
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}