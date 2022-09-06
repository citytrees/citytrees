package io.citytrees.util;

import io.citytrees.configuration.properties.PasswordProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

@Component
@RequiredArgsConstructor
public class HashUtil {

    private final PasswordProperties passwordProperties;

    public String md5WithSalt(String source) {
        return DigestUtils.md5DigestAsHex((source + passwordProperties.getSalt()).getBytes());
    }
}
