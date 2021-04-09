package com.arc_e_tect.blog.phonebook.web.error;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter @ToString @NoArgsConstructor
public class CPWebError {
    private String apiVersion;
    private ErrorBlock error;

    public CPWebError(final String apiVersion, final String code, final String message, final String domain,
                      final String reason, final String errorMessage, final String errorReportUri) {
        this.apiVersion = apiVersion;
        this.error = new ErrorBlock(code, message, domain, reason, errorMessage, errorReportUri);
    }

    public static CPWebError fromDefaultAttributeMap(final String apiVersion,
                                                     final Map<String, Object> defaultErrorAttributes,
                                                     final String sendReportBaseUri) {
        // original attribute values are documented in org.springframework.boot.web.servlet.error.DefaultErrorAttributes
        return new CPWebError(
                apiVersion,
                ((Integer) defaultErrorAttributes.get("status")).toString(),
                (String) defaultErrorAttributes.getOrDefault("message", "no message available"),
                (String) defaultErrorAttributes.getOrDefault("path", "no domain available"),
                (String) defaultErrorAttributes.getOrDefault("error", "no reason available"),
                (String) defaultErrorAttributes.get("message"),
                sendReportBaseUri
        );
    }

    // utility method to return a map of serialized root attributes,
    // see the last part of the guide for more details
    public Map<String, Object> toAttributeMap() {
        return Map.of(
                "apiVersion", apiVersion,
                "error", error
        );
    }

    @ToString @NoArgsConstructor
    public static final class ErrorBlock {
        @JsonIgnore
        @Getter private UUID uniqueId;
        @Getter private String code;
        @Getter private String message;
        @Getter private List<Error> errors;

        public ErrorBlock(final String code, final String message, final String domain,
                          final String reason, final String errorMessage, final String errorReportUri) {
            this.code = code;
            this.message = message;
            this.uniqueId = UUID.randomUUID();
            this.errors = List.of(
                    new Error(domain, reason, errorMessage, errorReportUri + "?id=" + uniqueId)
            );
        }

        private ErrorBlock(final UUID uniqueId, final String code, final String message, final List<Error> errors) {
            this.uniqueId = uniqueId;
            this.code = code;
            this.message = message;
            this.errors = errors;
        }

        public static ErrorBlock copyWithMessage(final ErrorBlock s, final String message) {
            return new ErrorBlock(s.uniqueId, s.code, message, s.errors);
        }
    }

    @AllArgsConstructor @NoArgsConstructor
    @ToString
    public static final class Error {
        @Getter private String domain;
        @Getter private String reason;
        @Getter private String message;
        @Getter private String sendReport;
    }
}
