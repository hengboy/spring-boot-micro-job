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

import com.github.hengboy.job.autoconfigure.registry.MicroJobRegistryProperties;
import com.github.hengboy.job.schedule.MicroJobScheduleFactoryBean;
import com.github.hengboy.job.schedule.store.JobStore;
import com.github.hengboy.job.schedule.store.customizer.JobStoreCustomizer;
import com.github.hengboy.job.schedule.store.jdbc.JdbcJobStore;
import org.quartz.Scheduler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 分布式调度器自动化配置
 *
 * @author：恒宇少年 - 于起宇
 * <p>
 * DateTime：2019-01-28 10:37
 * Blog：http://blog.yuqiyu.com
 * WebSite：http://www.jianshu.com/u/092df3f77bca
 * Gitee：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 */
@Configuration
@EnableConfigurationProperties({MicroJobScheduleProperties.class, MicroJobRegistryProperties.class})
@ConditionalOnClass({Scheduler.class, MicroJobScheduleFactoryBean.class})
@ConditionalOnBean(DataSource.class)
public class MicroJobScheduleAutoConfiguration {
    /**
     * 调度器属性配置
     */
    private MicroJobScheduleProperties microJobScheduleProperties;
    /**
     * 任务注册中心属性配置
     */
    private MicroJobRegistryProperties microJobRegistryProperties;
    /**
     * 自定义jobStore配置
     */
    private final ObjectProvider<JobStoreCustomizer> customizers;

    /**
     * 构造函数初始化注入对象信息
     *
     * @param microJobScheduleProperties 调度配置文件内容
     * @param customizers                自定义jobStore配置实现
     */
    public MicroJobScheduleAutoConfiguration(MicroJobScheduleProperties microJobScheduleProperties, MicroJobRegistryProperties microJobRegistryProperties, ObjectProvider<JobStoreCustomizer> customizers) {
        this.microJobScheduleProperties = microJobScheduleProperties;
        this.microJobRegistryProperties = microJobRegistryProperties;
        this.customizers = customizers;
    }

    /**
     * 实例化任务数据源对象
     * 配置使用数据库方式
     *
     * @param dataSource 数据源对象
     * @return
     */
    @Bean
    JobStore jobStore(DataSource dataSource) {
        JdbcJobStore jdbcJobStore = new JdbcJobStore();
        jdbcJobStore.setDataSource(dataSource);

        // 自定义配置
        this.customize(jdbcJobStore);
        return jdbcJobStore;
    }

    /**
     * 创建任务调度工厂对象
     *
     * @return
     */
    @Bean
    MicroJobScheduleFactoryBean microJobScheduleFactoryBean() {
        MicroJobScheduleFactoryBean factoryBean = new MicroJobScheduleFactoryBean();
        factoryBean.setListenPort(microJobScheduleProperties.getListenPort());
        factoryBean.setRequestTimeOutMillisSecond(microJobScheduleProperties.getRequestTimeOutMilliSecond());
        factoryBean.setHeartDelaySeconds(microJobScheduleProperties.getHeartDelaySeconds());
        factoryBean.setLoadBalanceWeight(microJobScheduleProperties.getLoadBalanceWeight());
        factoryBean.setRegistryIpAddress(microJobRegistryProperties.getIpAddress());
        factoryBean.setRegistryListenPort(microJobRegistryProperties.getListenPort());
        factoryBean.setMaxRetryTimes(microJobScheduleProperties.getMaxRetryTimes());
        return factoryBean;
    }


    /**
     * 任务数据源的自定义配置
     *
     * @param jobStore 任务数据源
     */
    private void customize(JobStore jobStore) {
        this.customizers.orderedStream().forEach((customizer) -> customizer.customize(jobStore));
    }
}
