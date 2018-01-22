package executors;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dto.TaskData;
import exceptions.ActivityExecutionException;
import log.Logger;
import utils.ThreadSleep;

public class ProcessSimpleTaskExecutor extends ActivityExecutor {

    public ProcessSimpleTaskExecutor(String arn, Logger logger) {
        super(arn, logger);
    }

    public String execute(String input) throws JsonParseException, IOException, ActivityExecutionException {
        ObjectMapper mapper = new ObjectMapper();
        TaskData data = mapper.readValue(input, TaskData.class);

        logger.log("ProcessSimpleTaskExecutor. Begin processing arn: " + getArn());
        if (!data.value.equalsIgnoreCase("Provide reply")) {
            throw new ActivityExecutionException(data.id, "Was not able to process the task");
        }

        data.additionalData = "Simple Task Processed";
        ThreadSleep.sleep(700);
        logger.log("ProcessSimpleTaskExecutor. End processing arn: " + getArn());
        return mapper.writer().writeValueAsString(data);
    }
}
