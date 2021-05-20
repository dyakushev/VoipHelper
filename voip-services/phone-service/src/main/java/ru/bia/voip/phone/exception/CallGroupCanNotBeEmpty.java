package ru.bia.voip.phone.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Call group should not be empty")
public class CallGroupCanNotBeEmpty extends RuntimeException {

}
