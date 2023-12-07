package com.server.lib.services;

import java.net.URI;
import java.util.List;
import java.util.Map;

import com.server.lib.runAndmethods.HttpMethods;

public class HttpRequest {
    private final HttpMethods httpMethods;  //todos los metodos aca del enum

    private final URI uri; //creo que es la uri visitada

    //un mapa que guarda un string y una lista de string 
    //pra guardar los headers de la peticion
    private final Map<String, List<String>> headers; 

    private HttpRequest(HttpMethods opCode, URI uri, Map<String, List<String>> headers){
        this.httpMethods = opCode;
        this.uri = uri;
        this.headers = headers;
    }

    public URI getUri(){
        return this.uri;
    }

    public Map<String, List<String>> getHeaders(){
        return this.headers;
    }

    public HttpMethods getMethod(){
        return this.httpMethods;
    }


    //No sabia que se podia crear una clase dentro de otra clase XD
    // no se para que hacer esto xd
    public static class Builder {
        private HttpMethods httpMethods;
        private URI uri;
        private Map<String, List<String>> headers;

        public Builder(){
            //empty, i dont know why xd
        }

        public void setHttpMethod(HttpMethods httpMethods){
            this.httpMethods = httpMethods;
        }

        public void setUri(URI uri){
            this.uri = uri;
        }

        public void setHeaders(Map<String, List<String>> headers){
            this.headers = headers;
        }

        public HttpRequest build(){
            return new HttpRequest(this.httpMethods, this.uri, this.headers);
        }

    
        
    }
}
