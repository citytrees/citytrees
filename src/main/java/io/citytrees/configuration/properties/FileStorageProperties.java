package io.citytrees.configuration.properties;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Value
@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "file-storage")
public class FileStorageProperties {

    @NotEmpty
    String type;
}
