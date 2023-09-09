package com.explorer.infrastructure.handler;

import lombok.*;
import java.io.Serializable;

@Getter
@Builder
public class ExceptionDto implements Serializable {
    private int statusCode;
    private String message;
}
