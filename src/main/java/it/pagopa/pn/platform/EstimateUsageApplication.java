package it.pagopa.pn.platform;

import it.pagopa.pn.platform.S3.S3Bucket;
import it.pagopa.pn.platform.service.impl.DeanonymizingServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@SpringBootApplication
public class EstimateUsageApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder app = new SpringApplicationBuilder(EstimateUsageApplication.class);
        if (System.getenv().containsKey("BATCHMODE")) {
            app.web(WebApplicationType.NONE);
        }
        app.run(args);
    }


    @RestController
    public static class HomeController {

        @Autowired
        DeanonymizingServiceImpl deanonymizingService;

        @Autowired
        S3Bucket s3Bucket;

        @GetMapping("")
        public Mono<String> home() {
            //return this.s3Bucket.getPresignedUrlFile("local-doc-bucket", "test_anonymized.csv");

            this.deanonymizingService.execute("PA-1234", "test_anonymized.csv")
                    .subscribe();
            return Mono.just("OK");

                    }
    }

}
