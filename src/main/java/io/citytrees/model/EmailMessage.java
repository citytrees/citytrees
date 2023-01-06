package io.citytrees.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EmailMessage {

    String address;

    String subject;

    String text;
}
