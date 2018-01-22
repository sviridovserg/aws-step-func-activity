package config;

import com.amazonaws.services.s3.model.Region;

public final class Configuration {
    public final class Arn {
        public final String getData = "arn:aws:states:activity:GetTaskData";
        public final String processSimpleTask = "arn:aws:states:activity:ProcessSimpleTask";
        public final String processValidatedTask = "arn:aws:states:activity:ProcessValidatedTask";
    }

    public final String accessKey = "";
    public final String secretKey = "";
    public final String region = Region.US_West_2.getFirstRegionId();

    public final Arn arn = new Arn();

}



