package log;

import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.DescribeLogStreamsRequest;
import com.amazonaws.services.logs.model.InputLogEvent;
import com.amazonaws.services.logs.model.PutLogEventsRequest;
import com.amazonaws.services.logs.model.PutLogEventsResult;

import config.Configuration;

public class CloudLogger implements Logger {
    private Configuration config;
    private String sequenceToken = "";


    public CloudLogger(Configuration config) {
        this.config = config;
        this.sequenceToken = getSequenceToken();
    }

    private AWSLogs getClient() {

        final AWSCredentials creds = new BasicAWSCredentials(config.accessKey, config.secretKey);
        AWSCredentialsProvider credProvider = new AWSCredentialsProvider() {
            public AWSCredentials getCredentials() {
                return creds;
            }

            public void refresh() {

            }
        };


        return AWSLogsClientBuilder.standard()
            .withRegion(config.region)
            .withCredentials(credProvider)
            .build();
    }

    private String getSequenceToken() {
        return getClient().describeLogStreams(
            new DescribeLogStreamsRequest()
                .withLogGroupName("/poc/activity-processor")
                .withLogStreamNamePrefix("/processing-logs")).getLogStreams().get(0).getUploadSequenceToken();
    }

    public void log(String message) {
        try {
            PutLogEventsRequest request = new PutLogEventsRequest()
                .withLogGroupName("/poc/activity-processor")
                .withLogStreamName("/processing-logs")
                .withLogEvents(
                    new InputLogEvent()
                        .withMessage(message)
                        .withTimestamp(DateTime.now(DateTimeZone.UTC).getMillis()));
            if (!"".equals(sequenceToken)) {
                request.setSequenceToken(sequenceToken);
            }
            PutLogEventsResult result = getClient().putLogEvents(request);
            sequenceToken = result.getNextSequenceToken();
        } catch (Exception ex) {
            // CloudWatch log died consider writing to another region about that and alarm
        }
    }
}
