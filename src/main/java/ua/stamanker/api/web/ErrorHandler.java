package ua.stamanker.api.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ua.stamanker.api.exceptions.AppException;

import java.util.Locale;

@ControllerAdvice
@Slf4j
public class ErrorHandler extends ResponseEntityExceptionHandler {

    private MessageSource messageSource;

    @Autowired
    public ErrorHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(value = {AppException.class})
    protected ResponseEntity<Object> handleLocalized409(RuntimeException ex, WebRequest request, Locale locale) {
        log.error("Error: " + ex.getMessage());
        String message = messageSource.getMessage(ex.getMessage(), null, locale);
        log.error("Error: " + message);
        return handleExceptionInternal(ex, message, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = {Throwable.class})
    protected ResponseEntity<Object> handle500(Exception ex, WebRequest request) {
        log.error("GENERAL ERROR(500): " + ex.getMessage(), ex);
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }


}
