package com.arc_e_tect.blog.phonebook;

import lombok.extern.flogger.Flogger;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

@Flogger
public class TestSupportFunctions {
    private TestSupportFunctions() {
        // nothing to do
    }

    public static void matchJson(String expectedJson, String actualJson) throws JSONException {
        try {
            JSONAssert.assertEquals(expectedJson, actualJson, false);
        } catch (JSONException e) {
            log.atSevere().withCause(e).log("X--> An exception was thrown while matching two json documents");
            log.atSevere().log("X--> Expected: %s", expectedJson);
            log.atSevere().log("X--> Provided: %s", actualJson);
            log.atSevere().log("X--> Going to throw the exception once again, so the normal flow is not messed with. BYE BYE :'(");
            throw e;
        }
    }

}
