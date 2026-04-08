
package com.portfolio.exception;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class Global{
@ExceptionHandler(Exception.class)
public String err(Exception e){return e.getMessage();}
}
