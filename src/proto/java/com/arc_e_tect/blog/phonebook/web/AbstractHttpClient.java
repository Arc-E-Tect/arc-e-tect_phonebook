package com.arc_e_tect.blog.phonebook.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.http.HttpStatusCode;
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

    protected AbstractHttpClient() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        mapper.registerModule(new Jackson2HalModule());
    }

    @Autowired
    @Getter
    protected StepData stepData;

    @Getter
    @Value("${local.server.port}")
    protected int port;

    public String getBody() {
        return stepData.getResponseString();
    }

    public HttpStatusCode getHttpStatus() {
        return stepData.getHttpStatus();
    }

    public RepresentationModel getBodyAsResource(RepresentationModel representationModel) {
        RepresentationModel result = null;

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        mapper.registerModule(new Jackson2HalModule());

        try {
            result = mapper.readValue(getBody(), representationModel.getClass());
        } catch (IOException e) {
            log.atInfo().withCause(e).log(e.getMessage());
        }

        return result;
    }

    abstract protected String getApiEndpoint();
    public String apiEndpoint() {
        return SERVER_URL + ":" + stepData.getMockPort() + getApiEndpoint();
    }

    protected void executeGet() {
        executeGet(apiEndpoint());
    }

    public void executeGet(String url) {
        WebClient.ResponseSpec spec = getApiClient().get()
                .uri(url)
                .retrieve();

        ResponseEntity<JsonNode> response = spec
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.empty())
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.empty())
                .toEntity(JsonNode.class)
                .block();

        stepData.setResponseEntity(response);
    }

    protected void executePost(String jsonDocument) {
        executePost(apiEndpoint(), jsonDocument);
    }

    protected void executePost(String url, String jsonDocument) {
        ResponseEntity<JsonNode> response = getApiClient().post()
                .uri(url)
                .bodyValue(jsonDocument)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.empty())
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.empty())
                .toEntity(JsonNode.class)
                .block();

        stepData.setResponseEntity(response);
    }

    protected void executePatch(String url, String jsonDocument) {
        ResponseEntity<JsonNode> response = getApiClient().patch()
                .uri(url)
                .bodyValue(jsonDocument)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.empty())
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.empty())
                .toEntity(JsonNode.class)
                .block();

        stepData.setResponseEntity(response);
    }

    protected void executeDelete(String url) {
        log.atInfo().log("Executing DELETE on URI %s", url);

        ResponseEntity<JsonNode> response = getApiClient().delete()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.empty())
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.empty())
                .toEntity(JsonNode.class)
                .block();

        stepData.setResponseEntity(response);

        log.atInfo().log("\t responseEntity received: %s", stepData.getResponseEntity().toString());
        log.atInfo().log("\t HTTP Status is : %s", stepData.getHttpStatus());
        log.atInfo().log("\t StepData: %s", stepData);

        log.atInfo().log("Delete data :");
        log.atInfo().log("\t url        :%s", url);
        log.atInfo().log("\t response   :%s", stepData.getResponseString());
        log.atInfo().log("\t httpStatus :%s", stepData.getHttpStatus());
        log.atInfo().log("\t headers    :%s", (stepData.getResponseHeaders() != null ? stepData.getResponseHeaders() : "NO HEADERS"));
        log.atInfo().log(" ===> Done executing the Delete on URL: %s, received body: %s", url, stepData.getResponseString());
    }

}
