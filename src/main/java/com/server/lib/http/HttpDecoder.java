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
        /* 
         * flatMap se ejecutara cuando readMessage devuelva un valor
         * que no sea vacio, cuando flatMap se ejecute, ejecutara el metodo
         * que se le pase por parametro, y a ese metodo, le pasara como argumento
         * lo que tenga el Optional donde se ejecuto, si quitamos los niveles
         * de abstracción del codigo escrito, quedaria de esta manera:
         * 
         * Optional<List<String>> decoderHttp = HttpDecoder.readMessage(inputStream);
         * 
         * if(!(decoderHttp.isPresent())){
         *      return Optional.empty();
         * }else{
         *       return HttpDecoder.buildRequest(decoderHttp.orElse(null));
         * }
         */

        return HttpDecoder.readMessage(inputStream).flatMap(
            HttpDecoder::buildRequest
        );
    }



    public static Optional<List<String>> readMessage(InputStream inputStream) {
        try {
            if (!(inputStream.available() > 0)) {
                return Optional.empty();
            }
    
            List<String> message = new ArrayList<>();
    
            InputStreamReader inReader = new InputStreamReader(inputStream);
    
            // Crea un Scanner a partir del InputStreamReader para leer líneas del inputStream
            try (Scanner scanner = new Scanner(inReader)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    message.add(line);
                }
            }
    
            return Optional.of(message);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    



    /* 
     * 
     * Message en este caso tiene toda la peticion Http, linea por linea
     * en una lista
     */
    private static Optional<HttpRequest> buildRequest(List<String> message){
        if(message.isEmpty()){
            return Optional.empty();
        }

        String fistLine = message.get(0); 
        /* 
         * 
         * La informacion se saca de la primera linea, por que una
         * estructura de peticion http queda de esta manera:
         * 
         * POST /api/autores HTTP/1.1
         * Host: miWebApi.com
         * Content-Type: application/json
         * Cache-Control: no-cache
         * 
         * en la primera linea se encuentra el metodo
         * la ruta y la version de protocolo
         */
        String[] httpInfo = fistLine.split(" ");

        
        /* 
         * Si es diferente a 3 significa que no es un formato 
         * de peticion http, por lo tanto, no es valida la peticion
         */
        if(httpInfo.length != 3){
            return Optional.empty();
        }

        String protocolVersion = httpInfo[2];

        /* 
         * Si el protocolo es diferente a 1.1 no se puede procesar
         * la peticion
         */
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

            /* 
             * Se empieza a construir la data para el request
             */
            requestBuilder.setHttpMethod(
                  /* 
                    Se setea el metodo que se visito
                    desde el enum
                  */            
                HttpMethods.valueOf(httpInfo[0])            
            );

            /* 
             * Se setea la url visitada
             */
            requestBuilder.setUri(
                new URI(httpInfo[1])
            );


            /* 
             * 
             * El metodo of es para setear un valor en Optional
             */
            return Optional.of(
                HttpDecoder.addHttpRequest(message, requestBuilder)
            );


        } catch (URISyntaxException | IllegalArgumentException e) {
           return Optional.empty();
        }
    }


    /* 
     * Recibe la peticion http en Lista, y el constructor 
     * del Request
     */
    private static HttpRequest addHttpRequest(final List<String> message, final Builder builder){
        final Map<String, List<String>> requestHeaders = new HashMap<>();

        if(message.size() > 1){ //supongo que es por que hay data
            for(int i = 0; i < message.size(); i++){
                String header = message.get(i);

                /* 
                 * indexOf devuelve un int, del numero de caracteres
                 * hasta donde esta ese caracter que se le paso por parametro
                 * ejemplo:
                 * 
                 * String test = "Host: test.com";
                 * test.indexOf(":");
                 * 
                 * esto devuelve: 4
                 */
                int colonIndex = header.indexOf(":");

                
                if(!(colonIndex > 0 && header.length() > colonIndex + 1)){
                    /* 
                     * La primera linea: GET /test HTTP/1.1
                     * no tiene el caracter ":" asi que 
                     * siempre entrara a esta condicion
                     * asi que el bucle continua ignorando el demas
                     * codigo, ya que no e sun header
                     */
                    continue;
                }


                /* 
                 * Obtiene el String antes de los dos puntos
                 */
                String headerName = header.substring(0, colonIndex);

                /* 
                 * Obtiene el String despues de los dos puntos
                 */
                String headerValue = header.substring(colonIndex + 1);


                /* 
                 * El metodo compute verifica si 
                 * headerName ya existe en el 
                 * Map requestHeaders, si existe,
                 * solamente le agrega el headervalue
                 * al existente, si no existe, crea
                 * una nueva clave
                 */
                requestHeaders.compute(headerName, (key, value) -> {
                    value = (value != null) ? value : new ArrayList<>();
                    if (value != null) {
                        value.add(headerValue);
                    }
                    return value;
                });
            }
        }
        

        /* Se setea los header en el built */
        builder.setHeaders(requestHeaders);

        /* 
         * Se retorna un HttpRequesst, para luego inyectarlo
         * dentro de un Optional
         */
        return builder.build();
    }
}
