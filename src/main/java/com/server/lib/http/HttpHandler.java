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
        


        /* 
         * 
         * Esto al final de cuenta tiene la informacion de la peticion
         * en forma de estructura de clase, para sacar los headers
         * el metodo, la uri, todo se decodifica del String que se manda 
         * en una peticion HTTP normal que es el siguiente: 
         * 
         * POST /api/autores HTTP/1.1
         * Host: test.com
         * Content-Type: application/json
         * Cache-Control: no-cache
         * 
         * con request guarda un optinal de la clase HttpRequest, por lo tanto
         * se tiene acceder al optional para acceder a metodos como:
         * HttpRequest.getMethod() para obtener el metodo
         * HttpRequest.getUri() para obtener la uri, entre otros
         * metodos, puedes ver directamente los metodos accediendo
         * al archivo
         */
        System.out.println("New connection client");
        Optional<HttpRequest> request = HttpDecoder.decode(inputStream);
         
        

        /* 
         * Si el optional esta vacio, construye una nueva
         * respuesta ejecutanto el metodo handleInvalidRequest
         * de esta misma clase, este metodo lo que hace, como habia dicho
         * es contruir un nueva respuesta de peticion invalida, y se
         * la manda al cliente.
         * 
         * Si el optional no esta vacio, ejecuta el metodo handleRequest
         * y si le pasa lo que tiene el Optional, en este caso
         * el optional tiene un tipo de HttpRequest, asi que le pasa
         * eso como parametro, y el buffer de escritura para enviar 
         * respuesta
         */
        
        Runnable action = (request.isPresent()) ?
        () -> this.handleRequest(request.get(), bufferedWriter) :
        () -> this.handleInvalidRequest(bufferedWriter);

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

        
        /* 
         * 
         * Aca contruye el key del map para
         * acceder a su callback mas adelante,
         * la sintanxis es la siguiente:
         * 
         * METHOD/ROUTE -> callback
         */
        final String routeKey = request
        .getMethod()
        .name()
        .concat(
            request.getUri().getRawPath()
        );


        /* 
         * Busca en la key en el mapa de rutas
         */
        if(this.routes.containsKey(routeKey)){
            /* 
             * Si la ruta se encuentra con su
             * respectivo metodo, se ejecuta su
             * callback, entiendo que ese callback 
             * siempre retornara un string, asi que de una vez
             * se escribe lo que se retorna
             */
            ResponseWriter.write(bufferedWriter, 
            this.routes.get(routeKey).run(request));
        }else{
            /* 
             * Si la ruta no se encontro, se manda
             * una respuesta con codigo de error 
             * 404
             */
            ResponseWriter.write(bufferedWriter, 
            new  HttpResponse
            .Builder()
            .setStatusCode(404)
            .setEntity("Route Not Found...")
            .build());
        }
    }
}
