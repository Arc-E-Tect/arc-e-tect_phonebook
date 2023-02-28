package com.arc_e_tect.blog.phonebook.commons;

import com.arc_e_tect.blog.phonebook.resource.ContactResource;
import lombok.extern.flogger.Flogger;
import org.mockserver.client.MockServerClient;
import org.springframework.http.HttpStatus;

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

    public static String contactPostTemplate =
            """
            {"id":%d,"name":"%s","phone":"%s","links":[]}
            """;
    public static String contactPatchPhoneTemplate ="{\"id\":%d,\"phone\":\"%s\",\"links\":[]}";
    public static String contactPatchNameTemplate = "{\"id\":%d,\"name\":\"%s\",\"links\":[]}";
    public static String contactPatchNamePhoneTemplate = "{\"id\":%d,\"name\":\"%s\",\"phone\":\"%s\",\"links\":[]}";

    public static void create_GetContacts(List<ContactResource> contactList) {
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

    public static void create_PostContact(ContactResource contactResource) {
        String serviceUrl = "contacts";
        String requestJson = String.format(contactPostTemplate,
                contactResource.getId(), contactResource.getName(), contactResource.getPhone());
        String responseJson = String.format(contactTemplate,
                contactResource.getId(), contactResource.getName(), contactResource.getPhone(),contactResource.getId());

        new MockServerClient("localhost",9091).when(request().withMethod("POST")
                        .withPath(String.format("/%s",serviceUrl))
                        .withBody(requestJson))
                .respond(response().withStatusCode(201)
                        .withHeader("Content-Type", "application/hal+json")
                        .withBody(responseJson));
    }

    public static void create_PatchPhone(ContactResource contactResource) {
        String serviceUrl = "contacts";
        String requestJson = String.format(contactPatchPhoneTemplate,
                contactResource.getId(), contactResource.getPhone());
        String responseJson = String.format(contactTemplate,
                contactResource.getId(), contactResource.getName(), contactResource.getPhone(),contactResource.getId());

        new MockServerClient("localhost",9091).when(request().withMethod("PATCH")
                        .withPath(String.format("/%s/%d",serviceUrl, contactResource.getId()))
                        .withBody(requestJson))
                .respond(response().withStatusCode(200)
                        .withHeader("Content-Type", "application/hal+json")
                        .withBody(responseJson));


    }

    public static void create_PatchName(ContactResource contactResource) {
        String serviceUrl = "contacts";
        String requestJson = String.format(contactPatchNameTemplate,
                contactResource.getId(), contactResource.getName());
        String responseJson = String.format(contactTemplate,
                contactResource.getId(), contactResource.getName(), contactResource.getPhone(),contactResource.getId());

        new MockServerClient("localhost",9091).when(request().withMethod("PATCH")
                        .withPath(String.format("/%s/%d",serviceUrl, contactResource.getId()))
                        .withBody(requestJson))
                .respond(response().withStatusCode(200)
                        .withHeader("Content-Type", "application/hal+json")
                        .withBody(responseJson));
    }

    public static void create_PatchContactUnprocessable(ContactResource contactResource, int origId) {
        String serviceUrl = "contacts";
        String requestJson = String.format(contactPatchNamePhoneTemplate,
                contactResource.getId(), contactResource.getName(), contactResource.getPhone());

        new MockServerClient("localhost",9091).when(request().withMethod("PATCH")
                        .withPath(String.format("/%s/%d",serviceUrl, origId))
                        .withBody(requestJson))
                .respond(response().withStatusCode(422));
    }

    public static void create_PostContactConflict(ContactResource contactResource) {
        String serviceUrl = "contacts";
        String requestJson = String.format(contactPostTemplate,
                contactResource.getId(), contactResource.getName(), contactResource.getPhone());
        String responseJson = String.format(contactTemplate,
                contactResource.getId(), contactResource.getName(), contactResource.getPhone(),contactResource.getId());

        new MockServerClient("localhost",9091).when(request().withMethod("POST")
                        .withPath(String.format("/%s",serviceUrl))
                        .withBody(requestJson))
                .respond(response().withStatusCode(409));
    }

    public static void create_GetContacts(String name, List<ContactResource> contactList) {
        String contacts = "";
        boolean isFirst = true;
        for(ContactResource contactResource : contactList) {
            if (name.equals(contactResource.getName())) {
                String contact = String.format(contactTemplate,
                        contactResource.getId(), contactResource.getName(), contactResource.getPhone(), contactResource.getId());
                contacts += (isFirst ? contact : ", " + contact);
                isFirst = false;
            }
        }
        String jsonDoc = String.format(contactListTemplate,contacts);

        String serviceUrl = "contacts";

        String encodedName = name.replace(" ", "%20");
        new MockServerClient("localhost",9091).when(request().withMethod("GET")
                        .withPath(String.format("/%s",serviceUrl))
                        .withQueryStringParameter("contactName", name))
                .respond(response().withStatusCode(200)
                        .withHeader("Content-Type", "application/hal+json")
                        .withBody(jsonDoc));
    }

    public static void create_GetContact(long id, List<ContactResource> contactList) {
        String serviceUrl = "contacts";
        for(ContactResource contactResource : contactList) {
            if (id == contactResource.getId()) {
                String jsonDoc = String.format(contactTemplate,
                        contactResource.getId(), contactResource.getName(), contactResource.getPhone(), contactResource.getId());

                new MockServerClient("localhost",9091).when(request().withMethod("GET")
                                .withPath(String.format("/%s/%d",serviceUrl, id)))
                        .respond(response().withStatusCode(200)
                                .withHeader("Content-Type", "application/hal+json")
                                .withBody(jsonDoc));
            }
        }
    }

    public static void create_ContactNotFound(String verb, long id) {
        String serviceUrl = "contacts";
        new MockServerClient("localhost",9091).when(request().withMethod(verb)
                        .withPath(String.format("/%s/%d",serviceUrl,id)))
                .respond(response().withStatusCode(404));
    }

    public static void create_ContactNotFound(String verb, String name) {
        String serviceUrl = "contacts";
        new MockServerClient("localhost",9091).when(request().withMethod(verb)
                        .withPath(String.format("/%s?contactName=%s",serviceUrl,name)))
                .respond(response().withStatusCode(404));
    }

    public static void create_DeleteContactByName(String name) {
        String serviceUrl = "contacts";

        String encodedName = name.replace(" ", "%20");
        new MockServerClient("localhost",9091).when(request().withMethod("DELETE")
                        .withPath(String.format("/%s",serviceUrl))
                        .withQueryStringParameter("contactName", name))
                .respond(response().withStatusCode(HttpStatus.NO_CONTENT.value()));
    }

    public static void create_DeleteContactById(int id) {
        String serviceUrl = "contacts";

        new MockServerClient("localhost",9091).when(request().withMethod("DELETE")
                        .withPath(String.format("/%s/%d",serviceUrl,id)))
                .respond(response().withStatusCode(HttpStatus.NO_CONTENT.value()));
    }
}
