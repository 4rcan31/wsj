package com.server.lib.services;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpStatusCode {
    public static final Map<Integer, String> STATUS_CODES;

    static {
        Map<Integer, String> statusCodes = new HashMap<>();
        statusCodes.put(200, "OK");
        statusCodes.put(400, "BAD_REQUEST");
        statusCodes.put(404, "NOT_FOUND");
        statusCodes.put(500, "INTERNAL_SERVER_ERROR");

        STATUS_CODES = Collections.unmodifiableMap(statusCodes);
    }
}

