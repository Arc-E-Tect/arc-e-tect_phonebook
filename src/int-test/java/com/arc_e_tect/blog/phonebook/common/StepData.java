package com.arc_e_tect.blog.phonebook.common;

import com.arc_e_tect.blog.phonebook.web.error.CPWebError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.flogger.Flogger;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Flogger
@Data
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class StepData {

    private ResponseEntity<JsonNode> responseEntity;
    private HttpEntity<String> request;
    private HttpStatus httpStatus;

    private ObjectMapper mapper;

    public StepData() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        mapper.registerModule(new Jackson2HalModule());
    }

    /**
     * Returns the response data as CPWebError when valid, {@code null} otherwise.
     * @return The response data as {@link CPWebError}
     */
    public CPWebError getWebError() {
        try {
            return new ObjectMapper().readValue(responseEntity.getBody().toString(), CPWebError.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public void setResponseEntity(ResponseEntity<JsonNode> responseEntity) {
        this.responseEntity = responseEntity;
        this.httpStatus = (responseEntity != null ? responseEntity.getStatusCode() : null);
    }

    public JsonNode getResponseJsonNode() {
        return responseEntity.getBody();
    }

    public RepresentationModel getResponseRepresentationModel(RepresentationModel modelRef) {
        try {
            log.atInfo().log("\t Decoding [%s] as %s.", getResponseString(),modelRef.getClass());
            return mapper.readValue(getResponseString(), modelRef.getClass());
        } catch (JsonProcessingException e) {
            log.atSevere().withCause(e).log("Could not decode json data as ErrorDetails");
            return null;
        }
    }

    public String getResponseString() {
        return (responseEntity != null && responseEntity.getBody() != null ? responseEntity.getBody().toString() : "");
    }

    public HttpHeaders getResponseHeaders() { return (responseEntity != null ? responseEntity.getHeaders() : null); }
    public URI getLocation() { return (responseEntity == null ? null : responseEntity.getHeaders().getLocation()); }

}