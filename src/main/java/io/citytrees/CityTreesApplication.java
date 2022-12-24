package io.citytrees;

import io.citytrees.configuration.properties.GeoProperties;
import io.citytrees.configuration.properties.S3Properties;
import io.citytrees.configuration.properties.SecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    GeoProperties.class,
    SecurityProperties.class,
    S3Properties.class
})
public class CityTreesApplication {

    public static void main(String[] args) {
        SpringApplication.run(CityTreesApplication.class, args);
    }

}
