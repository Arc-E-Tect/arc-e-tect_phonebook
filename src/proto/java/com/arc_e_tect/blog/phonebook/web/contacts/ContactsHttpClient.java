package com.arc_e_tect.blog.phonebook.web.contacts;

import com.arc_e_tect.blog.phonebook.resource.ContactResource;
import com.arc_e_tect.blog.phonebook.web.AbstractHttpClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.flogger.Flogger;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Flogger
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class ContactsHttpClient extends AbstractHttpClient {

    @PostConstruct
    public WebClient initApiClient() {
        baseUrl = String.format("%s:%d", SERVER_URL, getPort());

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(300))
                                .addHandlerLast(new WriteTimeoutHandler(300)));

        apiClient = WebClient.builder()
                .baseUrl(String.format(baseUrl))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/hal+json")
                .defaultHeader(HttpHeaders.USER_AGENT, "Arc-E-Tect Phonebook")
                .clientConnector(new ReactorClientHttpConnector(httpClient))  // timeout
                .build();

        return apiClient;
    }

    @Override
    public WebClient getApiClient() {
        return apiClient;
    }

    protected final String getApiEndpoint() {return "/contacts";}

    public void getAll() throws IOException {
        executeGet(apiEndpoint());
    }

    public void getSingleByName(String name) throws IOException {
        String url = String.format("%s/%s", apiEndpoint(), name);
        executeGet(url);
    }

    public void postNewContact(ContactResource resource) throws IOException {
        String jsonDocument = new ObjectMapper().writeValueAsString(resource);

        executePost(jsonDocument);
    }

    public void postNewContact(long id, String jsonDoc) {
        executePost(apiEndpoint(), jsonDoc);
    }

    public void patchContact(String name, ContactResource resource) throws JsonProcessingException {
        String url = String.format("%s/%s", apiEndpoint(), name);
        String jsonDocument = new ObjectMapper().writeValueAsString(resource);

        executePatch(url, jsonDocument);
    }

    public void deleteContact(String contactName) {
        String url = String.format("%s/%s", apiEndpoint(), contactName);
        executeDelete(url);
    }

}
