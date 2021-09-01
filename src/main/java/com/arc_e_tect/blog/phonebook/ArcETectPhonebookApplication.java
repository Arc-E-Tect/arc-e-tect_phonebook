package com.arc_e_tect.blog.phonebook;

import org.mockserver.integration.ClientAndServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootApplication
public class ArcETectPhonebookApplication {

    private static ClientAndServer mockServer = startClientAndServer(9090);

	public static void main(String[] args) {
		String bodyContacts = "[ { \"id\": 1, \"name\": \"Arc-E-Tec\", \"phone\": 55521212346 }," +
				"{ \"id\": 2, \"name\": \"Three Axis\", \"phone\": 55521467754 }," +
				"{ \"id\": 3, \"name\": \"E-One\",\"phone\": 55523173566 } ]";
		String[] bodyContact = {
				"{\"id\":1,\"name\":\"Arc-E-Tec\",\"phone\":55521212346}"
				,"{\"id\":2,\"name\":\"Three Axis\",\"phone\":55521467754}"
				,"{\"id\":3,\"name\":\"E-One\",\"phone\":55523173566}"
		};

		mockServer.when(request().withMethod("GET")
				.withPath("/contacts/1"))
				.respond(response().withStatusCode(200)
						.withHeader("Content-Type", "application/hal+json")
						.withBody(bodyContact[0]));
/*
		mockServer.when(request().withMethod("GET")
				.withPath("/contacts/2"))
				.respond(response().withStatusCode(200)
						.withHeader("Content-Type", "application/hal+json")
						.withBody(bodyContact[1]));
		mockServer.when(request().withMethod("GET")
				.withPath("/contacts/3"))
				.respond(response().withStatusCode(200)
						.withHeader("Content-Type", "application/hal+json")
						.withBody(bodyContact[2]));
*/

/*
		mockServer.when(request().withMethod("GET")
						.withPath("/contacts/Arc-E-Tect"))
				.respond(response().withStatusCode(200)
						.withHeader("Content-Type", "application/hal+json")
						.withBody(bodyContact[0]));
		mockServer.when(request().withMethod("GET")
						.withPath("/contacts/names/Arc-E-Tect"))
				.respond(response().withStatusCode(200)
						.withHeader("Content-Type", "application/hal+json")
						.withBody(bodyContact[0]));
		mockServer.when(request().withMethod("GET")
						.withPath("/contacts").withQueryStringParameter("name","Arc-E-Tect"))
				.respond(response().withStatusCode(200)
						.withHeader("Content-Type", "application/hal+json")
						.withBody(bodyContact[0]));
*/
		mockServer.when(request().withMethod("GET")
						.withPath("/contacts"))
				.respond(response().withStatusCode(200)
						.withHeader("Content-Type", "application/hal+json")
						.withBody(bodyContacts));

		mockServer.when(request().withMethod("GET")
						.withPath("/contacts/.*"))
				.respond(response().withStatusCode(404)
						.withHeader("Content-Type", "application/hal+json")
						.withBody("{\"error\" : \"Contact not found\"}"));
	}
}
