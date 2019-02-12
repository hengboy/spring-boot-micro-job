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

package com.github.hengboy.job.autoconfigure.consumer;

import com.github.hengboy.job.autoconfigure.registry.MicroJobRegistryProperties;
import com.github.hengboy.job.consumer.MicroJobConsumerFactoryBean;
import com.github.hengboy.job.core.http.MicroJobRestTemplate;
import com.github.hengboy.job.core.tools.JobSpringContext;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * 任务消费者自动化配置
 *
 * @author：恒宇少年 - 于起宇
 * <p>
 * DateTime：2019-01-30 15:26
 * Blog：http://blog.yuqiyu.com
 * WebSite：http://www.jianshu.com/u/092df3f77bca
 * Gitee：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 */
@Configuration
@ConditionalOnClass(MicroJobConsumerFactoryBean.class)
@EnableConfigurationProperties({MicroJobConsumerProperties.class, MicroJobRegistryProperties.class})
public class MicroJobConsumerAutoConfiguration {
    /**
     * 任务消费者属性配置
     */
    private MicroJobConsumerProperties microJobConsumerProperties;
    /**
     * 任务注册中心属性配置
     */
    private MicroJobRegistryProperties microJobRegistryProperties;
    /**
     * 注入spring bean factory
     * 用于获取springboot默认的package
     */
    private BeanFactory beanFactory;

    /**
     * 构造函数自动实例化相关属性配置
     *
     * @param microJobConsumerProperties 任务消费者属性配置
     * @param microJobRegistryProperties 任务注册中心属性配置
     */
    public MicroJobConsumerAutoConfiguration(MicroJobConsumerProperties microJobConsumerProperties, MicroJobRegistryProperties microJobRegistryProperties, BeanFactory beanFactory) {
        this.microJobConsumerProperties = microJobConsumerProperties;
        this.microJobRegistryProperties = microJobRegistryProperties;
        this.beanFactory = beanFactory;
    }

    /**
     * 实例化micro-job所需要操作spring ioc的类
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    JobSpringContext jobSpringContext() {
        return new JobSpringContext();
    }

    /**
     * 实例化任务消费者工厂实例
     * - 注册中心配置信息
     * - 消费者配置信息
     * - 扫描microJob配置信息
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public MicroJobConsumerFactoryBean microJobConsumerFactoryBean() {
        MicroJobConsumerFactoryBean factoryBean = new MicroJobConsumerFactoryBean();
        factoryBean.setRegistryIpAddress(microJobRegistryProperties.getIpAddress());
        factoryBean.setRegistryPort(microJobRegistryProperties.getListenPort());
        factoryBean.setHeartDelaySeconds(microJobConsumerProperties.getHeartDelaySeconds());
        factoryBean.setListenPort(microJobConsumerProperties.getListenPort());
        factoryBean.setLoadBalanceWeight(microJobConsumerProperties.getLoadBalanceWeight());
        // 使用配置文件配置的路径
        String scanMicroJobPackage = microJobConsumerProperties.getBaseScanMicroJobPackage();
        // 如果并未配置，则使用springboot默认扫描的package
        if (StringUtils.isEmpty(scanMicroJobPackage)) {
            scanMicroJobPackage = AutoConfigurationPackages.get(beanFactory).get(0);
        }
        factoryBean.setJobScanBasePackage(scanMicroJobPackage);
        return factoryBean;
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
