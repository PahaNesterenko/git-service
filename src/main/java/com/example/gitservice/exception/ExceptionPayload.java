package com.example.gitservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExceptionPayload {
    private Integer status;
    private String message;
}
