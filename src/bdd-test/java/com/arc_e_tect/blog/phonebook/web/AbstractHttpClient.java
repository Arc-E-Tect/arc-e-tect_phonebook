package com.arc_e_tect.blog.phonebook.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Flogger
public abstract class AbstractHttpClient {
    protected WebClient apiClient;

    public abstract WebClient getApiClient();

    protected static final String SERVER_URL = "http://localhost";

    @Getter
    protected String baseUrl;

    protected ObjectMapper mapper = new ObjectMapper();

    AbstractHttpClient() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        mapper.registerModule(new Jackson2HalModule());
    }

    @Autowired
    @Getter
    protected StepData stepData;

    @Getter
    @LocalServerPort
    protected int port;

    public String getBody() {
        return stepData.getResponseString();
    }

    public HttpStatus getHttpStatus() {
        return stepData.getHttpStatus();
    }

    public RepresentationModel getBodyAsResource(RepresentationModel representationModel) {
        RepresentationModel result = null;

        try {
            result = mapper.readValue(getBody(), representationModel.getClass());
        } catch (IOException e) {
            log.atWarning().withCause(e).log(e.getMessage());
        }

        return result;
    }

    abstract String getApiEndpoint();
    public String apiEndpoint() {
        return SERVER_URL + ":" + port + getApiEndpoint();
    }

    protected void executeGet() {
        executeGet(apiEndpoint());
    }

    public void executeGet(String url) {
        ResponseEntity<JsonNode> response = getApiClient().get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.empty())
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.empty())
                .toEntity(JsonNode.class)
                .block();

        stepData.setResponseEntity(response);
    }

}
