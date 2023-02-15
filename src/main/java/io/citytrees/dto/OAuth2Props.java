package io.citytrees.dto;

import io.citytrees.v1.model.AuthGetAllProviderResponseItem;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OAuth2Props {

    String id;

    String label;

    public AuthGetAllProviderResponseItem toResponse() {
        return new AuthGetAllProviderResponseItem().id(getId()).label(getLabel());
    }
}
