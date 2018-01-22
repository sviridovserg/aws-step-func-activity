package executors;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;

import exceptions.ActivityExecutionException;
import log.Logger;

public abstract class ActivityExecutor {
    private final String arn;
    protected final Logger logger;

    public ActivityExecutor(String arn, Logger logger) {
        this.arn = arn;
        this.logger = logger;
    }

    public String getArn() {
        return this.arn;
    }

    public abstract String execute(String input) throws JsonParseException, IOException, ActivityExecutionException;
}
