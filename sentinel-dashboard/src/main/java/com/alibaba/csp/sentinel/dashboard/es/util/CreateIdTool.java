package com.alibaba.csp.sentinel.dashboard.es.util;

import java.util.Random;

/**
 * @Title: CreateIdTool
 * @description:
 * @author: wqiuping@linewell.com
 * @since: 2020年12月10日 20:45
 */
public class CreateIdTool {

    /**
     * 获取下一个Long类型主键
     * @return
     */
    public static Long nextId() {
        long time = System.currentTimeMillis();
        Random r = new Random(1);
        time = time * 1000 + r.nextInt(100);
        return time;
    }

}
