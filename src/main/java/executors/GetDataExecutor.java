package executors;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dto.TaskData;
import dto.TaskType;
import exceptions.ActivityExecutionException;
import log.Logger;
import utils.ThreadSleep;

public class GetDataExecutor extends ActivityExecutor {

    public GetDataExecutor(String arn, Logger logger) {
        super(arn, logger);
    }

    @Override
    public String execute(String input) throws JsonParseException, IOException, ActivityExecutionException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(input);

        logger.log(String.format("GetData activity arn: %s. Begin get data", getArn()));
        int id = node.get("id").asInt();
        logger.log(String.format("GetData activity arn: %s. End get data", getArn()));

        return mapper.writer().writeValueAsString(getData(id));
    }

    private TaskData getData(int idx) throws ActivityExecutionException{
        ThreadSleep.sleep(500);

        switch (idx) {
            case 1:
                return new TaskData() {{
                    id = idx;
                    title = "My simple task";
                    type = TaskType.Simple;
                    value = "Provide reply";
                }};
            case 2:
                return new TaskData() {{
                    id = idx;
                    title = "My validated task";
                    type = TaskType.ValidatedTask;
                    value = "Increase value to 100";
                }};

            case 3:
                return new TaskData() {{
                    id = idx;
                    title = "My another validated task";
                    type = TaskType.ValidatedTask;
                    value = "task is not valid";
                }};
            case 4:
                throw new ActivityExecutionException(4);
            default:
                return null;
        }
    }
}
