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

package com.github.hengboy.sample.consumer.jobs;

import com.github.hengboy.job.core.annotation.Job;
import com.github.hengboy.job.core.exception.JobException;
import com.github.hengboy.job.core.model.MicroJob;
import com.github.hengboy.job.core.model.execute.JobExecuteParam;
import com.github.hengboy.job.core.model.execute.JobExecuteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * job sample
 *
 * @author：恒宇少年 - 于起宇
 * <p>
 * DateTime：2019-02-22 15:20
 * Blog：http://blog.yuqiyu.com
 * WebSite：http://www.jianshu.com/u/092df3f77bca
 * Gitee：https://gitee.com/hengboy
 * GitHub：https://github.com/hengyuboy
 */
@Job
public class SampleJob implements MicroJob {
    /**
     * logger instance
     */
    static Logger logger = LoggerFactory.getLogger(SampleJob.class);

    @Override
    public JobExecuteResult execute(JobExecuteParam param) throws JobException {
        logger.info("jobKey -> {}", param.getJobKey());
        logger.info("jobQueueId -> {}", param.getJobQueueId());
        logger.info("jsonParam -> {}", param.getJsonParam());

        // job logic..

        return JobExecuteResult.JOB_EXECUTE_SUCCESS;
    }
}
