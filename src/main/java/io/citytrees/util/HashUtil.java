package io.citytrees.util;

import io.citytrees.configuration.properties.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

@Component
@RequiredArgsConstructor
public class HashUtil {

    private final SecurityProperties securityProperties;

    public String md5WithSalt(String source) {
        return md5WithSalt(source, securityProperties.getPasswordSalt());
    }

    public String md5WithSalt(String source, String salt) {
        return DigestUtils.md5DigestAsHex((source + salt).getBytes());
    }
}
