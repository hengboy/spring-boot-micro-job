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

import com.github.hengboy.job.core.enums.LoadBalanceStrategy;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 任务生产者属性配置
 *
 * @author：恒宇少年 - 于起宇
 * <p>
 * DateTime：2019-01-29 14:49
 * Blog：http://blog.yuqiyu.com
 * WebSite：http://www.jianshu.com/u/092df3f77bca
 * Gitee：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 */
@Data
@ConfigurationProperties(prefix = "hengboy.job.provider")
public class MicroJobProviderProperties {
    /**
     * 调度器调用负载均衡策略
     */
    private LoadBalanceStrategy scheduleLbStrategy = LoadBalanceStrategy.POLL_WEIGHT;
    /**
     * 同步注册中心调度器间隔时间
     * 单位：秒
     */
    private int syncRegistryScheduleIntervalSeconds = 5;
}
