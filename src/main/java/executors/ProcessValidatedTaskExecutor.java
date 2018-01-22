package executors;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dto.TaskData;
import exceptions.ActivityExecutionException;
import log.Logger;
import utils.ThreadSleep;

public class ProcessValidatedTaskExecutor extends ActivityExecutor {

    public ProcessValidatedTaskExecutor(String arn, Logger logger) {
        super(arn, logger);
    }

    @Override
    public String execute(String input) throws JsonParseException, IOException, ActivityExecutionException {
        ObjectMapper mapper = new ObjectMapper();
        TaskData data = mapper.readValue(input, TaskData.class);

        logger.log("ProcessValidatedTaskExecutor. Begin processing arn: " + getArn());

        data.additionalData = "Validated Task Processed";
        ThreadSleep.sleep(1400);

        logger.log("ProcessValidatedTaskExecutor. End processing arn: " + getArn());
        return mapper.writer().writeValueAsString(data);
    }
}
