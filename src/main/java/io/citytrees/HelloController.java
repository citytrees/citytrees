package io.citytrees;

import io.citytrees.v1.controller.HelloControllerApiDelegate;
import io.citytrees.v1.model.HelloWorld200Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelloController extends BaseController implements HelloControllerApiDelegate {

    @Override
    public ResponseEntity<HelloWorld200Response> helloWorld() {
        return HelloControllerApiDelegate.super.helloWorld();
    }
}
