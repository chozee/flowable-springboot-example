/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.examples.spring.boot;

import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Filip Hrisafov
 */
@SpringBootApplication(proxyBeanMethods = false)
@RestController
public class FlowableSpringBootExampleApplication {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    private Logger logger = LoggerFactory.getLogger(FlowableSpringBootExampleApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(FlowableSpringBootExampleApplication.class, args);
    }

    public String startProcess() {
        Map<String, Object> param = new HashMap<>();
        param.put("employeeName", "zhaoyy");
        param.put("numberOfDays", 11);
        param.put("vacationMotivation", 1);
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("vacationRequest", param);
        logger.info("start process {}", instance.getId());
        return instance.getId();
    }

    public void dealTask(String intanceId) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(intanceId).listPage(0, 10);
        if (!CollectionUtils.isEmpty(tasks)) {
            logger.info("task list {}", tasks.size());
            for (Task task : tasks) {
                taskService.complete(task.getId());
            }
//            logger.info("after deal task {}", taskService.createTaskQuery().count());
        }
    }


    @PostMapping("/test")
    @ResponseBody
    public String test() {
        try {
            dealTask(startProcess());
        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }

        return "success";
    }
}