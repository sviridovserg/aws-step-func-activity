import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.List;

import org.apache.http.util.ExceptionUtils;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.stepfunctions.model.GetActivityTaskRequest;
import com.amazonaws.services.stepfunctions.model.GetActivityTaskResult;
import com.amazonaws.services.stepfunctions.model.SendTaskFailureRequest;
import com.amazonaws.services.stepfunctions.model.SendTaskSuccessRequest;

import config.Configuration;
import executors.*;
import jdk.nashorn.internal.ir.ReturnNode;
import log.Logger;

public class ActivityPoller {
    private final Configuration config;

    private List<ActivityExecutor> executors;

    private final Logger logger;

    public ActivityPoller(final Configuration config, final Logger logger) {
        this.config = config;
        this.logger = logger;

        executors = new ArrayList() {{
            add(new GetDataExecutor(config.arn.getData, logger));
            add(new ProcessSimpleTaskExecutor(config.arn.processSimpleTask, logger));
            add(new ProcessValidatedTaskExecutor(config.arn.processValidatedTask, logger));
        }};
    }

    private AWSStepFunctions getClient() {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setSocketTimeout((int) TimeUnit.SECONDS.toMillis(70));

        final AWSCredentials creds = new BasicAWSCredentials(config.accessKey, config.secretKey);
        AWSCredentialsProvider credProvider = new AWSCredentialsProvider() {
            public AWSCredentials getCredentials() {
                return creds;
            }

            public void refresh() {

            }
        };

        return AWSStepFunctionsClientBuilder.standard()
            .withRegion(config.region)
            .withCredentials(credProvider)
            .withClientConfiguration(clientConfiguration)
            .build();
    }

    private SendTaskSuccessRequest buildSuccessRequest(GetActivityTaskResult activityResult, String output) {
        SendTaskSuccessRequest successRequest = new SendTaskSuccessRequest()
            .withOutput(Optional.ofNullable(output).orElse("{}"))
            .withTaskToken(activityResult.getTaskToken());
        return successRequest;
    }

    private SendTaskFailureRequest buildFailureRequest(GetActivityTaskResult activityResult, Exception ex) {
        return new SendTaskFailureRequest()
            .withCause(ex.getMessage())
            .withTaskToken(activityResult.getTaskToken());
    }

    public void poll() {
        try {
            AWSStepFunctions client = getClient();
            logger.log("Strat polling");

            for (ActivityExecutor e: executors) {
                logger.log("Poll activity arn: " + e.getArn());

                GetActivityTaskResult activityResult = client.getActivityTask(new GetActivityTaskRequest().withActivityArn(e.getArn()));


                if (activityResult.getTaskToken() != null) {
                    logger.log(String.format("Begin executing activity arn: %s, input: %s", e.getArn(), activityResult.getInput()));
                    try {
                        String output = e.execute(activityResult.getInput());
                        client.sendTaskSuccess(buildSuccessRequest(activityResult, output));
                        logger.log(String.format("Activity execution successful arn: %s, output: %s", e.getArn(), output));
                    } catch (Exception ex) {
                        client.sendTaskFailure(buildFailureRequest(activityResult, ex));
                        logger.log("ERROR: " + ex.toString());
                    } finally {
                        logger.log("End executing activity arn: " + e.getArn());
                    }

                } else {
                    logger.log("No work for activity arn: " + e.getArn());
                }

            }
        } catch (SdkClientException ex) {
            logger.log("No execution is running currently");
            return;
        }
        finally {
            logger.log("End polling");
        }
    }

}
