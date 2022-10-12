package com.arc_e_tect.blog.phonebook.commons;

import lombok.extern.flogger.Flogger;
import org.mockserver.integration.ClientAndServer;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

@Flogger
public class MockServerExpectations {
    private static ClientAndServer mockServer = startClientAndServer(1080, 9090, 9091);

    public static void resetMockServer() {
        mockServer.reset();
    }

}
