package com.server.lib.runAndmethods;

import com.server.lib.services.HttpRequest;
import com.server.lib.services.HttpResponse;

public interface RequestRunner {
    HttpResponse run(HttpRequest request);
}
