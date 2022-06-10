package com.arc_e_tect.blog.phonebook.web.contacts.advisories;

import com.arc_e_tect.blog.phonebook.service.exception.ContactNotFoundException;
import com.arc_e_tect.blog.phonebook.service.exception.DuplicateContactException;
import lombok.extern.flogger.Flogger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@Flogger
@RestControllerAdvice
class ContactAdvisoryHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ContactNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ResponseEntity<ErrorDetails> contactNotFoundHandler(ContactNotFoundException ex, WebRequest request) {
        ErrorDetails error = new ErrorDetails(new Date(),HttpStatus.NOT_FOUND, "Contact not found", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateContactException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public final ResponseEntity<ErrorDetails> duplicateNamespaceHandler(DuplicateContactException ex, WebRequest request) {
        ErrorDetails error = new ErrorDetails(new Date(),HttpStatus.CONFLICT, "Contact already exists", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
}
