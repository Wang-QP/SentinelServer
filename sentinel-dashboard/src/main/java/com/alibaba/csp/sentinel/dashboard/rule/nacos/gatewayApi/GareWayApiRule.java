package com.alibaba.csp.sentinel.dashboard.rule.nacos.gatewayApi;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.ApiPredicateItemEntity;
import com.alibaba.csp.sentinel.slots.block.AbstractRule;

import java.util.Objects;
import java.util.Set;

/**
 * @Title: GareWayApiRule
 * @description:
 * @author: wqiuping@linewell.com
 * @since: 2020年10月30日 16:46
 */
public class GareWayApiRule extends AbstractRule {
    private String apiName;
    private Set<ApiPredicateItemEntity> predicateItems;

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public Set<ApiPredicateItemEntity> getPredicateItems() {
        return predicateItems;
    }

    public void setPredicateItems(Set<ApiPredicateItemEntity> predicateItems) {
        this.predicateItems = predicateItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GareWayApiRule that = (GareWayApiRule) o;
        return apiName.equals(that.apiName) &&
                predicateItems.equals(that.predicateItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), apiName, predicateItems);
    }

    @Override
    public String toString() {
        return "GareWayApiRule{" +
                "apiName='" + apiName + '\'' +
                ", predicateItems=" + predicateItems +
                '}';
    }
}
