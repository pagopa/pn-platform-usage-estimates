package it.pagopa.pn.platform;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

        @GetMapping("")
        public String home() {
            return "Ok";
        }
    }

}
