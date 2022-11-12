package io.citytrees.configuration.properties;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Value
@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "s3")
public class S3Properties {
    @NotEmpty
    String accessKey;

    @NotEmpty
    String secretKey;

    @NotEmpty
    String region;

    @NotEmpty
    String serviceEndpoint;

    @NotEmpty
    String bucket;

    @Bean
    public AmazonS3 s3Client() {
        return AmazonS3ClientBuilder
            .standard()
            .withCredentials(
                new AWSStaticCredentialsProvider(
                    new BasicAWSCredentials(accessKey, secretKey)
                )
            )
            .withEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration(
                    serviceEndpoint,
                    region
                )
            )
            .build();
    }
}
