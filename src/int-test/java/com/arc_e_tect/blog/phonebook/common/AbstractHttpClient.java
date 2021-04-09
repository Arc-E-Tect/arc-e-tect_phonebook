package com.arc_e_tect.blog.phonebook.common;

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
    protected StepData stepData;

    @Getter
    @LocalServerPort
    protected int port;

    public RepresentationModel getBodyAsResource(RepresentationModel representationModel) {
        RepresentationModel result = null;

        try {
            result = mapper.readValue(getBody(), representationModel.getClass());
        } catch (IOException e) {
            log.atInfo().withCause(e).log(e.getMessage());
        }

        return result;
    }

    abstract String getApiEndpoint();
    public String apiEndpoint() {
        return SERVER_URL + ":" + getPort() + getApiEndpoint();
    }

    protected void executeGet() {
        executeGet(apiEndpoint());
    }

    protected void executeGet(String url) {
        log.atInfo().log(" ===> Going to execute the Get on URL: %s", url);

        ResponseEntity<JsonNode> response = getApiClient().get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.empty())
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.empty())
                .toEntity(JsonNode.class)
                .block();

        stepData.setResponseEntity(response);

        log.atInfo().log("\t responseEntity received: %s", stepData.getResponseEntity().toString());
        log.atInfo().log("\t HTTP Status is : %s", stepData.getHttpStatus());
        log.atInfo().log("\t StepData: %s", stepData);

        log.atInfo().log("Get data :");
        log.atInfo().log("\t url        :%s", url);
        log.atInfo().log("\t response   :%s", stepData.getResponseString());
        log.atInfo().log("\t httpStatus :%s", stepData.getHttpStatus());
        log.atInfo().log("\t headers    :%s", (stepData.getResponseHeaders() != null ? stepData.getResponseHeaders() : "NO HEADERS"));

        log.atInfo().log(" ===> Done executing the Get on URL: %s, received body: %s", url, stepData.getResponseString());
    }

    protected void executePost(String jsonDocument) {
        executePost(apiEndpoint(), jsonDocument);
    }

    protected void executePost(Long namespaceId, String jsonDocument) {
        executePost(String.format(apiEndpoint(), namespaceId), jsonDocument);
    }

    protected void executePost(String url, String jsonDocument) {
        log.atInfo().log("Executing POST on URI %s with document %s", url, jsonDocument);

        ResponseEntity<JsonNode> response = getApiClient().post()
                .uri(url)
                .bodyValue(jsonDocument)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.empty())
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.empty())
                .toEntity(JsonNode.class)
                .block();

        stepData.setResponseEntity(response);

        log.atInfo().log("\t responseEntity received: %s", stepData.getResponseEntity().toString());
        log.atInfo().log("\t HTTP Status is : %s", stepData.getHttpStatus());
        log.atInfo().log("\t StepData: %s", stepData);

        log.atInfo().log("Post data :");
        log.atInfo().log("\t request    :%s", jsonDocument);
        log.atInfo().log("\t url        :%s", url);
        log.atInfo().log("\t response   :%s", stepData.getResponseString());
        log.atInfo().log("\t httpStatus :%s", stepData.getHttpStatus());
        log.atInfo().log("\t headers    :%s", (stepData.getResponseHeaders() != null ? stepData.getResponseHeaders() : "NO HEADERS"));
        log.atInfo().log(" ===> Done executing the Post on URL: %s, received body: %s", url, stepData.getResponseString());
    }

    protected void executeDelete(String url) {
        log.atInfo().log("Executing DELETE on URI %s", url);

        ResponseEntity<JsonNode> response = getApiClient().delete()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.empty())
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.empty())
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

    protected void executePut(String url, String jsonDocument) {
        log.atInfo().log("Executing PUT on URI %s with document %s", url, jsonDocument);

        ResponseEntity<JsonNode> response = getApiClient().put()
                .uri(url)
                .bodyValue(jsonDocument)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.empty())
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.empty())
                .toEntity(JsonNode.class)
                .block();

        stepData.setResponseEntity(response);

        log.atInfo().log("\t responseEntity received: %s", stepData.getResponseEntity().toString());
        log.atInfo().log("\t HTTP Status is : %s", stepData.getHttpStatus());
        log.atInfo().log("\t StepData: %s", stepData);

        log.atInfo().log("Put data :");
        log.atInfo().log("\t url        :%s", url);
        log.atInfo().log("\t request    :%s", jsonDocument);
        log.atInfo().log("\t response   :%s", stepData.getResponseString());
        log.atInfo().log("\t httpStatus :%s", stepData.getHttpStatus());
        log.atInfo().log("\t headers    :%s", (stepData.getResponseHeaders() != null ? stepData.getResponseHeaders() : "NO HEADERS"));

        log.atInfo().log(" ===> Done executing the Put on URL: %s, received body: %s", url, stepData.getResponseString());
    }

    protected void executePatch(String url, String jsonDocument) {
        log.atInfo().log("Executing PATCH on URI %s with document %s", url, jsonDocument);

        ResponseEntity<JsonNode> response = getApiClient().patch()
                .uri(url)
                .bodyValue(jsonDocument)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.empty())
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.empty())
                .toEntity(JsonNode.class)
                .block();

        stepData.setResponseEntity(response);

        log.atInfo().log("\t responseEntity received: %s", stepData.getResponseEntity().toString());
        log.atInfo().log("\t HTTP Status is : %s", stepData.getHttpStatus());
        log.atInfo().log("\t StepData: %s", stepData);

        log.atInfo().log("Patch data :");
        log.atInfo().log("\t url        :%s", url);
        log.atInfo().log("\t request    :%s", jsonDocument);
        log.atInfo().log("\t response   :%s", stepData.getResponseString());
        log.atInfo().log("\t httpStatus :%s", stepData.getHttpStatus());
        log.atInfo().log("\t headers    :%s", (stepData.getResponseHeaders() != null ? stepData.getResponseHeaders() : "NO HEADERS"));

        log.atInfo().log(" ===> Done executing the Patch on URL: %s, received body: %s", url, stepData.getResponseString());
    }

    public void postNewNamespace(long id, String jsonDoc) {
        executePost(apiEndpoint() + "/" + id, jsonDoc);
    }

    public void putNewNamespace(Long id, String jsonDoc) {
        executePut(apiEndpoint() + "/" + id, jsonDoc);
    }

    public String getBody() {
        return stepData.getResponseString();
    }

    public HttpStatus getHttpStatus() {
        return stepData.getHttpStatus();
    }
}
