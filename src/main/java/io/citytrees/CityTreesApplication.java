package io.citytrees;

import io.citytrees.configuration.properties.PasswordProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    PasswordProperties.class
})
public class CityTreesApplication {

    public static void main(String[] args) {
        SpringApplication.run(CityTreesApplication.class, args);
    }

}
