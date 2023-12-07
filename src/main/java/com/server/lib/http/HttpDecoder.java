package com.server.lib.http;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import com.server.lib.runAndmethods.HttpMethods;
import com.server.lib.services.HttpRequest;
import com.server.lib.services.HttpRequest.Builder;

public class HttpDecoder {


    public static Optional<HttpRequest> decode(final InputStream inputStream){
        return HttpDecoder.readMessage(inputStream).flatMap(
            HttpDecoder::buildRequest
        );
    }



    public static Optional<List<String>> readMessage(InputStream inputStream){
        try {
            if(!(inputStream.available() > 0)){ //verifica si viene data
                return Optional.empty();
            }


            final char[] inBuffer  = new char[inputStream.available()];

            final InputStreamReader inReader = new InputStreamReader(inputStream);

            //no se pa que xd
            final int read = inReader.read(inBuffer, 0, 0); 

            List<String> message = new ArrayList<>();

            try(Scanner nc = new Scanner(new String(inBuffer))) {
                while (nc.hasNextLine()) {
                    String line = nc.nextLine();
                    message.add(line);
                }
                return Optional.of(message);
            } catch (Exception e) {
                return Optional.empty();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    private static Optional<HttpRequest> buildRequest(List<String> message){
        if(message.isEmpty()){
            return Optional.empty();
        }

        String fistLine = message.get(0); 
        String[] httpInfo = fistLine.split(" ");

        //No entiendo por que si es diferente a tres la verdad xd
        if(httpInfo.length != 3){
            return Optional.empty();
        }

        String protocolVersion = httpInfo[2];
        if(!protocolVersion.equals("HTTP/1.1")){
            return Optional.empty();
        }


        /* 
         * 
         * 0 -> method
         * 1 -> uri
         * 2 -> protocol version
         */

        try {
            Builder requestBuilder = new Builder();
            requestBuilder.setHttpMethod(
                HttpMethods.valueOf(httpInfo[0])
            );
            requestBuilder.setUri(
                new URI(httpInfo[1])
            );

            return Optional.of(
                HttpDecoder.addHttpRequest(message, requestBuilder)
            );


        } catch (URISyntaxException | IllegalArgumentException e) {
           return Optional.empty();
        }
    }


    private static HttpRequest addHttpRequest(final List<String> message, final Builder builder){
        final Map<String, List<String>> requestHeaders = new HashMap<>();

        if(message.size() > 1){ //supongo que es por que hay data
            for(int i = 0; i < message.size(); i++){
                String header = message.get(i);

                //no estoy muy seguto que le hace index of a ese string
                int colonIndex = header.indexOf(":");

                //no entiendo por que detiene el bucle aca
                if(!(colonIndex > 0 && header.length() > colonIndex + 1)){
                    break;
                }

                String headerName = header.substring(0, colonIndex);
                String headerValue = header.substring(colonIndex + 1);

                requestHeaders.compute(headerName, (key, value) -> {
                    if(value != null){
                        value.add(headerValue);
                    }else{
                        value = new ArrayList<>();
                    }
                    return value;
                });
            }
        }

        builder.setHeaders(requestHeaders);
        return builder.build();
    }
}
