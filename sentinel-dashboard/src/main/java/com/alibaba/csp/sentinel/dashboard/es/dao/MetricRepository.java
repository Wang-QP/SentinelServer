package com.alibaba.csp.sentinel.dashboard.es.dao;

/**
 * @Title: MetricRepository
 * @description:
 * @author: wqiuping@linewell.com
 * @since: 2020年12月09日 19:54
 */

import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetricRepository extends ElasticsearchRepository<MetricEntity, Long> {
    List<MetricEntity> findByAppAndResource(String app, String resource);
    List<MetricEntity> findByAppAndResourceAndGmtCreateBetweenOrderByGmtCreateAsc(String app, String resource, long startTime, long endTime);
    List<MetricEntity> findByAppAndTimestampAfter(String app, long time);
}
