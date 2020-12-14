package com.alibaba.csp.sentinel.dashboard.es.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @Title: Config
 * @description:
 * @author: wqiuping@linewell.com
 * @since: 2020年12月09日 20:01
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.alibaba.csp.sentinel.dashboard.es.dao")
class Config {

}
