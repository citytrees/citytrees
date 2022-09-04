package io.citytrees.controller;

import io.citytrees.v1.controller.HelloControllerApiDelegate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelloController extends BaseController implements HelloControllerApiDelegate {

}
