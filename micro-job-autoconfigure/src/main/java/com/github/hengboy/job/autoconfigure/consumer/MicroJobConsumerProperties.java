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

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 任务消费者属性配置
 *
 * @author：恒宇少年 - 于起宇
 * <p>
 * DateTime：2019-01-30 15:26
 * Blog：http://blog.yuqiyu.com
 * WebSite：http://www.jianshu.com/u/092df3f77bca
 * Gitee：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 */
@Data
@ConfigurationProperties(prefix = "hengboy.job.consumer")
public class MicroJobConsumerProperties {
    /**
     * 调度器负载的权重
     */
    private int loadBalanceWeight = 1;
    /**
     * 心跳同步执行间隔时间，单位：秒
     */
    private int heartDelaySeconds = 5;
    /**
     * 扫描microJob接口实现类的package
     * 默认使用springboot默认扫描bean的package
     */
    private String baseScanMicroJobPackage;
}
