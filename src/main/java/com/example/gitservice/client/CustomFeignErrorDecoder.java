package com.example.gitservice.client;

import com.example.gitservice.exception.GitServiceCommonException;
import com.example.gitservice.exception.ResourceNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class CustomFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() >= 400 && response.status() <= 499) {
            return new ResourceNotFoundException(response.status(), response.reason());
        } else {
            return new GitServiceCommonException(response.status(), response.reason());
        }
    }
}
