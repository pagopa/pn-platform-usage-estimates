package it.pagopa.pn.platform.config;

import it.pagopa.pn.platform.LocalStackTestConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;


@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(LocalStackTestConfig.class)
public abstract class BaseTest {


    @Slf4j
    @SpringBootTest
    @ActiveProfiles("test")
    @Import(LocalStackTestConfig.class)
    public static class WithMockServer {
        @Autowired
        private MockServerBean mockServer;
        @BeforeEach
        public void init(){
            log.info(this.getClass().getSimpleName());
            //TODO set name file with name class + ".json";
            setExpection(this.getClass().getSimpleName() + "-webhook.json");
        }

        @AfterEach
        public void kill(){
            log.info("Killed");
            this.mockServer.stop();
        }

        public void setExpection(String file){
            this.mockServer.initializationExpection(file);
        }
    }


}