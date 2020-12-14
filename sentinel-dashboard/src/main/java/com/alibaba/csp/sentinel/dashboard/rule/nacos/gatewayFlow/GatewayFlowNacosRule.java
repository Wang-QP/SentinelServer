package com.alibaba.csp.sentinel.dashboard.rule.nacos.gatewayFlow;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayParamFlowItemEntity;
import com.alibaba.csp.sentinel.slots.block.AbstractRule;

import java.util.Objects;

/**
 * @Title: GatewayFlowNacosRule
 * @description:
 * @author: wqiuping@linewell.com
 * @since: 2020年10月30日 17:19
 */
public class GatewayFlowNacosRule extends AbstractRule {
    private String refResource;
    private Integer resourceMode;

    private Integer grade;
    private Double count;
    private Long interval;
    private Integer intervalUnit;

    private Integer controlBehavior;
    private Integer burst;

    private Integer maxQueueingTimeoutMs;

    private GatewayParamFlowItemEntity paramItem;
    
    public String getRefResource() {
        return refResource;
    }
    
    public void setRefResource(String refResource) {
        this.refResource = refResource;
    }

    public Integer getResourceMode() {
        return resourceMode;
    }

    public void setResourceMode(Integer resourceMode) {
        this.resourceMode = resourceMode;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    public Long getInterval() {
        return interval;
    }

    public void setInterval(Long interval) {
        this.interval = interval;
    }

    public Integer getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(Integer intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public Integer getControlBehavior() {
        return controlBehavior;
    }

    public void setControlBehavior(Integer controlBehavior) {
        this.controlBehavior = controlBehavior;
    }

    public Integer getBurst() {
        return burst;
    }

    public void setBurst(Integer burst) {
        this.burst = burst;
    }

    public Integer getMaxQueueingTimeoutMs() {
        return maxQueueingTimeoutMs;
    }

    public void setMaxQueueingTimeoutMs(Integer maxQueueingTimeoutMs) {
        this.maxQueueingTimeoutMs = maxQueueingTimeoutMs;
    }

    public GatewayParamFlowItemEntity getParamItem() {
        return paramItem;
    }

    public void setParamItem(GatewayParamFlowItemEntity paramItem) {
        this.paramItem = paramItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GatewayFlowNacosRule that = (GatewayFlowNacosRule) o;
        return Objects.equals(refResource, that.refResource) &&
                Objects.equals(resourceMode, that.resourceMode) &&
                Objects.equals(grade, that.grade) &&
                Objects.equals(count, that.count) &&
                Objects.equals(interval, that.interval) &&
                Objects.equals(intervalUnit, that.intervalUnit) &&
                Objects.equals(controlBehavior, that.controlBehavior) &&
                Objects.equals(burst, that.burst) &&
                Objects.equals(maxQueueingTimeoutMs, that.maxQueueingTimeoutMs) &&
                Objects.equals(paramItem, that.paramItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), refResource, resourceMode, grade, count, interval, intervalUnit, controlBehavior, burst, maxQueueingTimeoutMs, paramItem);
    }

    @Override
    public String toString() {
        return "GatewayFlowNacosRule{" +
                "resource='" + refResource + '\'' +
                ", resourceMode=" + resourceMode +
                ", grade=" + grade +
                ", count=" + count +
                ", interval=" + interval +
                ", intervalUnit=" + intervalUnit +
                ", controlBehavior=" + controlBehavior +
                ", burst=" + burst +
                ", maxQueueingTimeoutMs=" + maxQueueingTimeoutMs +
                ", paramItem=" + paramItem +
                '}';
    }
}
