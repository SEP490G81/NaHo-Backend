package org.naho.config.aws;

import lombok.RequiredArgsConstructor;
import org.naho.file.constant.S3Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@RequiredArgsConstructor
public class AwsConfig {
    private final S3Properties s3Properties;

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        return DefaultCredentialsProvider.builder().build();
    }

    @Bean
    public Region region() {
        return Region.of(s3Properties.getRegion());
    }

    @Bean
    public S3Client s3Client(
            AwsCredentialsProvider credentialsProvider,
            Region region
    ) {
        return S3Client.builder()
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(
            AwsCredentialsProvider credentialsProvider,
            Region region
    ) {
        return S3Presigner.builder()
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();
    }

    @Bean
    public CostExplorerClient costExplorerClient(
            AwsCredentialsProvider credentialsProvider,
            Region region
    ) {
        return CostExplorerClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();
    }
}
