package com.arc_e_tect.blog.phonebook.web.index;

import com.arc_e_tect.blog.phonebook.web.AbstractHttpClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.annotation.PostConstruct;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class IndexHttpClient extends AbstractHttpClient {

    @PostConstruct
    public WebClient initApiClient() {
        baseUrl = String.format("%s:%d", SERVER_URL, getPort());

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(3))
                                .addHandlerLast(new WriteTimeoutHandler(3)));

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

    @Override
    public RepresentationModel getBodyAsResource(RepresentationModel representationModel) {
        throw new UnsupportedOperationException();
    }

    protected final String getApiEndpoint() {return "/";}

    public void getRoot() {
        executeGet();
    }

    public void getIndex() {
        executeGet("/index");
    }
}
