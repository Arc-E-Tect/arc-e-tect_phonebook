package com.arc_e_tect.blog.phonebook.web.contacts.advisories;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter @ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor @AllArgsConstructor
public class ErrorDetails extends RepresentationModel<ErrorDetails> {
    /**
     * Timestamp indicating the moment on which the error was registered.
     * The timestamp is used when comparing to {@code CPErrorDetails} instances for equality.
     * @param timestamp Date and time to which the error occurence must be set. May not be {@code null}
     * @return the date and time of the error occurence.
     */
    @EqualsAndHashCode.Include
    private Date timestamp;
    /**
     * The http status associated with the error. Typically a 4xx or 5xx value.
     * @param status The status to be associated with the error. May not be {@code null}
     * @return the status associated with the error.
     */
    @EqualsAndHashCode.Include
    private HttpStatus status;
    /**
     * Short description of the error.
     * @param title the new title for the error. May not be {@code null}
     * @return the title of the error.
     */
    private String title;
    /**
     * Free format detailed information regarding the error.
     * @param detail new value of the details related to the error. May not be {@code null}
     * @return the details of the error.
     */
    private String detail;
}
