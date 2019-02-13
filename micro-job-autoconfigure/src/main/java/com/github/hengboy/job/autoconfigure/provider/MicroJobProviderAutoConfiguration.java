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

package com.github.hengboy.job.autoconfigure.provider;

import com.github.hengboy.job.autoconfigure.registry.MicroJobRegistryProperties;
import com.github.hengboy.job.core.http.MicroJobRestTemplate;
import com.github.hengboy.job.provider.MicroJobProvider;
import com.github.hengboy.job.provider.MicroJobProviderFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 任务生产者自动配置
 *
 * @author：恒宇少年 - 于起宇
 * <p>
 * DateTime：2019-01-29 14:50
 * Blog：http://blog.yuqiyu.com
 * WebSite：http://www.jianshu.com/u/092df3f77bca
 * Gitee：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 */
@Configuration
@ConditionalOnClass(MicroJobProviderFactoryBean.class)
@EnableConfigurationProperties({MicroJobProviderProperties.class, MicroJobRegistryProperties.class})
public class MicroJobProviderAutoConfiguration {
    /**
     * 任务生产者配置注入
     */
    MicroJobProviderProperties microJobProviderProperties;
    /**
     * 任务注册中心注入
     */
    MicroJobRegistryProperties microJobRegistryProperties;

    /**
     * 构造函数初始化相关配置
     *
     * @param microJobProviderProperties 任务生产者
     * @param microJobRegistryProperties 任务注册中心
     */
    public MicroJobProviderAutoConfiguration(MicroJobProviderProperties microJobProviderProperties, MicroJobRegistryProperties microJobRegistryProperties) {
        this.microJobProviderProperties = microJobProviderProperties;
        this.microJobRegistryProperties = microJobRegistryProperties;
    }

    /**
     * 实例化任务生产者对象
     * 设置注册中心方式、调度器调用负载均衡策略
     *
     * @return
     */
    @Bean
    MicroJobProviderFactoryBean microJobProviderFactoryBean() {
        MicroJobProviderFactoryBean factoryBean = new MicroJobProviderFactoryBean();
        factoryBean.setRegistryAway(microJobRegistryProperties.getAway());
        factoryBean.setScheduleLbStrategy(microJobProviderProperties.getScheduleLbStrategy());
        factoryBean.setSyncRegistryScheduleIntervalSeconds(microJobProviderProperties.getSyncRegistryScheduleIntervalSeconds());
        factoryBean.setRegistryIpAddress(microJobRegistryProperties.getIpAddress());
        factoryBean.setRegistryPort(microJobRegistryProperties.getPort());
        return factoryBean;
    }

    /**
     * 任务操作生产者类实例化
     * 提供对任务的添加、删除、暂停、是否存在验证等方法
     * 每一个方法都是通过负载均衡策略进行远程调用
     *
     * @return
     */
    @Bean
    MicroJobProvider microJobProvider() {
        return new MicroJobProvider();
    }

    /**
     * 实例化restTemplate
     * 用于消费者、提供者、调度器、注册中心ws请求交互
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public MicroJobRestTemplate restTemplate() {
        return new MicroJobRestTemplate();
    }
}
