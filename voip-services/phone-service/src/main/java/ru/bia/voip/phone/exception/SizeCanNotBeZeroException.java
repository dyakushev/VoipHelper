package ru.bia.voip.phone.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Size should be more than zero")
public class SizeCanNotBeZeroException extends RuntimeException {

}
