/*
 *    Copyright [2019] [恒宇少年 - 于起宇]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.hengboy.job.autoconfigure.schedule;

import lombok.Data;
import org.springframework.boot.autoconfigure.quartz.JobStoreType;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceInitializationMode;

/**
 * 分布式调度器属性相关配置
 *
 * @author：恒宇少年 - 于起宇
 * <p>
 * DateTime：2019-01-28 10:33
 * Blog：http://blog.yuqiyu.com
 * WebSite：http://www.jianshu.com/u/092df3f77bca
 * Gitee：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 */
@Data
@ConfigurationProperties(prefix = "hengboy.job.schedule")
public class MicroJobScheduleProperties {
    /**
     * 最大重试次数
     */
    private int maxRetryTimes = 2;
    /**
     * 调度器负载的权重
     */
    private int loadBalanceWeight = 1;
    /**
     * 心跳同步执行间隔时间，单位：秒
     */
    private int heartDelaySeconds = 5;
    /**
     * quartz Config Properties
     */
    private MicroJobScheduleProperties.QuartzConfigProperties quartz;
    /**
     * 任务数据源类型，默认使用内存方式
     */
    private JobStoreType jobStoreType = JobStoreType.MEMORY;

    /**
     * 如果并未自定义配置信息
     * 使用默认的配置信息
     *
     * @return
     */
    public MicroJobScheduleProperties.QuartzConfigProperties getQuartz() {
        if (quartz == null) {
            // init
            quartz = new MicroJobScheduleProperties.QuartzConfigProperties();

            // 设置任务存储方式为数据库方式
            quartz.setJobStoreType(jobStoreType);

            // 数据源方式，设置相关属性
            if (JobStoreType.JDBC.toString().equals(jobStoreType.toString())) {
                quartz.getJdbc().setInitializeSchema(DataSourceInitializationMode.EMBEDDED);
                quartz.getProperties().put("org.quartz.scheduler.instanceName", "jobScheduler");
                quartz.getProperties().put("org.quartz.scheduler.instanceId", "AUTO");
                quartz.getProperties().put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
                quartz.getProperties().put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
                quartz.getProperties().put("org.quartz.jobStore.tablePrefix", "MICRO_JOB_QRTZ_");
                quartz.getProperties().put("org.quartz.jobStore.isClustered", "true");
                quartz.getProperties().put("org.quartz.jobStore.clusterCheckinInterval", "20000");
                quartz.getProperties().put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
            }
        }
        return quartz;
    }

    /**
     * quartz config
     */
    @Data
    public static class QuartzConfigProperties extends QuartzProperties {
        public QuartzConfigProperties() {
            super();
        }
    }
}
