package com.server.lib.http;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Optional;

import javax.swing.text.html.Option;

import com.server.lib.runAndmethods.RequestRunner;
import com.server.lib.services.HttpRequest;
import com.server.lib.services.HttpResponse;
import com.server.lib.writes.ResponseWriter;

public class HttpHandler {


    private final Map<String, RequestRunner> routes;

    public HttpHandler(Map<String, RequestRunner> routes){
        this.routes = routes;
    }

    public void handlerConnection(final InputStream inputStream, final OutputStream outputStream) throws IOException{
        final BufferedWriter bufferedWriter  = new BufferedWriter(
            new OutputStreamWriter(outputStream)
        );

        Optional<HttpRequest> request = HttpDecoder.decode(inputStream);
        
        Runnable action = (request.isPresent()) ?
        () -> handleRequest(request.get(), bufferedWriter) :
        () -> handleInvalidRequest(bufferedWriter);

        action.run();
        bufferedWriter.close();
        inputStream.close();
    }


    private void handleInvalidRequest(final BufferedWriter bufferedWriter){
        HttpResponse notFoundResponse = 
        new HttpResponse.Builder()
        .setStatusCode(400)
        .setEntity("Invalid Request...")
        .build();
        ResponseWriter.write(bufferedWriter, notFoundResponse);
    }


    private void handleRequest(final HttpRequest request, final BufferedWriter bufferedWriter){
        final String routeKey = request
        .getMethod()
        .name()
        .concat(
            request.getUri().getRawPath()
        );

        if(this.routes.containsKey(routeKey)){
            ResponseWriter.write(bufferedWriter, 
            this.routes.get(routeKey).run(request));
        }else{
            //no se encontro
            ResponseWriter.write(bufferedWriter, 
            new  HttpResponse
            .Builder()
            .setStatusCode(404)
            .setEntity("Route Not Found...")
            .build());
        }
    }
}
