package com.arc_e_tect.blog.phonebook.commons;

import com.arc_e_tect.blog.phonebook.resource.ContactResource;
import lombok.extern.flogger.Flogger;
import org.mockserver.client.MockServerClient;

import java.util.List;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@Flogger
public class MockServerExpectations {
    public static void create_GetRootInfo() {
        String jsonDoc = "{\"_links\":{\"self\":{\"href\":\"http://localhost:9091\"},\"contacts\":{\"href\":\"http://localhost:9091/contacts\"}}}";
        String serviceUrl = "";

        new MockServerClient("localhost",9091).when(request().withMethod("GET")
                        .withPath(String.format("/%s",serviceUrl)))
                .respond(response().withStatusCode(200)
                        .withHeader("Content-Type", "application/hal+json")
                        .withBody(jsonDoc));
    }

    public static void create_GetIndexInfo() {
        String jsonDoc = "{\"_links\":{\"self\":{\"href\":\"http://localhost:9091\"},\"contacts\":{\"href\":\"http://localhost:9091/contacts\"}}}";
        String serviceUrl = "index";

        new MockServerClient("localhost",9091).when(request().withMethod("GET")
                        .withPath(String.format("/%s",serviceUrl)))
                .respond(response().withStatusCode(200)
                        .withHeader("Content-Type", "application/hal+json")
                        .withBody(jsonDoc));
    }

    public static void remove_GetRootInfo() {
        String jsonDoc = "{\"_links\":{\"self\":{\"href\":\"http://localhost:9091\"},\"contacts\":{\"href\":\"http://localhost:9091/contacts\"}}}";
        String serviceUrl = "";

        new MockServerClient("localhost",9091).clear(request()
                .withPath(String.format("/%s",serviceUrl))
        );
    }

    public static void remove_GetIndexInfo() {
        String jsonDoc = "{\"_links\":{\"self\":{\"href\":\"http://localhost:9091\"},\"contacts\":{\"href\":\"http://localhost:9091/contacts\"}}}";
        String serviceUrl = "index";;

        new MockServerClient("localhost",9091).clear(request()
                .withPath(String.format("/%s",serviceUrl))
        );
    }

    public static String contactListTemplate =
            """
            { "_embedded": {"contacts": [%s]}, "_links": {"self": {"href": "http://localhost:9090/contacts{?contactName}", "templated": true}}}
            """;

    public static String contactTemplate =
            """
            { "id": %d, "name": "%s", "phone": "%s", "_links": { "self": { "href": "http://localhost:9090/contacts/%d" }, "contacts": { "href": "http://localhost:9090/contacts{?contactName}", "templated": true } } }
            """;

    public static void create_ListedContacts(List<ContactResource> contactList) {
        String contacts = "";
        boolean isFirst = true;
        for(ContactResource contactResource : contactList) {
            String contact = String.format(contactTemplate,
                    contactResource.getId(), contactResource.getName(), contactResource.getPhone(), contactResource.getId());
            contacts += (isFirst ? contact : ", " + contact);
            isFirst=false;
        }
        String jsonDoc = String.format(contactListTemplate,contacts);

        String serviceUrl = "contacts";

        new MockServerClient("localhost",9091).when(request().withMethod("GET")
                        .withPath(String.format("/%s",serviceUrl)))
                .respond(response().withStatusCode(200)
                        .withHeader("Content-Type", "application/hal+json")
                        .withBody(jsonDoc));
    }
}
