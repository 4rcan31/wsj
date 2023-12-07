package com.server.lib.services;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class HttpResponse {


    private final Map<String, List<String>> headers;
    private final int statusCode;

    /* 
     * Optional sirve para guardar cosa nullas, esto significa que 
     * entity puede guardar un objeto o puede guardar null
     */
    private final Optional<Object> entity; 


    /**
     * Headers should contain the following:
     * Date: < date >
     * Server: < my server name >
     * Content-Type: text/plain, application/json etc...
     * Content-Length: size of payload
     */


     private HttpResponse(final Map<String, List<String>> headers, int statusCode, Optional<Object> entity){
        this.headers = headers;
        this.statusCode = statusCode;
        this.entity = entity;
     }

     public Map<String, List<String>> getResponseHeaders(){
        return this.headers;
     }

     public int getStatusCode(){
        return this.statusCode;
     }

     public Optional<Object> getEntity(){
        return this.entity;
     }

     public class Builder {
        
        private final Map<String, List<String>> headers;
        private int statusCode;
        private Optional<Object> entity;

        public Builder(){
            this.headers = new HashMap<>(); //para guardar data
            this.headers.put("Server", Arrays.asList("WSJ - 4rcan31"));
            this.headers.put("Date", 
                Arrays.asList(
                    DateTimeFormatter.RFC_1123_DATE_TIME.format(
                        ZonedDateTime.now(ZoneOffset.UTC)
                    )
                )
            ); 
            this.entity = Optional.empty();
        }

        public Builder setStatusCode(final int statusCode){
            this.statusCode = statusCode;
            return this;
        }

        public Builder addHeader(final String keyName, final String value){
            this.headers.put(keyName, Arrays.asList(value));
            return this;
        }

        public Builder serEntity(final Optional<Object> entity){
            if(this.entity != null){
                this.entity = Optional.of(entity);
            }
            return this;
        }

        public HttpResponse build(){
            return new HttpResponse(this.headers, this.statusCode, this.entity);
        }
        
     }
}
