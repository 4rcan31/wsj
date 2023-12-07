package com.server.lib.writes;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import com.server.lib.services.HttpResponse;
import com.server.lib.services.HttpStatusCode;



/**
 * Class used for writing a HTTPResponse objects to the outputstream.
 * This will write responses as 'text/plain'.
 */
public class ResponseWriter {


    /**
     * Write a HTTPResponse to an outputstream
     * @param outputStream - the outputstream
     * @param response - the HTTPResponse
     */

     public static void write(final BufferedWriter outputStream, final HttpResponse response ){
        try {
            final int statusCode = response.getStatusCode();
            final String statusCodeMeaning = HttpStatusCode.STATUS_CODES.get(
                statusCode
            ) ;

            final List<String> responseHeaders = ResponseWriter.buildHeaderStrings(
                response.getResponseHeaders()
            );

            outputStream.write("HTTP/1.1 " + statusCode + " " + statusCodeMeaning);

            for(String header : responseHeaders){
                outputStream.write(header);
            }

            /* 
             * Aun no entiendo que es lo que significa la entidad
             * en el contexto del server http
             */
            final Optional<String> entityString = response.getEntity().flatMap(ResponseWriter::getResponseString);
            if(entityString.isPresent()){ //que significa esto? 
                final String encondeString = new String(
                    entityString.get().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8
                );
                outputStream.write("Content-Length: " + encondeString.getBytes().length + "\r\n");
                outputStream.write("\r\n");
                outputStream.write(encondeString);
            }else{
                 outputStream.write("\r\n");
            }

        } catch (Exception e) {
           System.out.println("Algo fallo en el writer: " + e.getMessage());
        }
     }

     private static List<String> buildHeaderStrings(final Map<String, List<String>> responseHeaders){
        final List<String> responseHeaderList = new ArrayList<>();

        /* 
         * Entiendo que "->" es para definir un callback (una funcion anonima)
         */
        responseHeaders.forEach((name, values) -> {
            final StringBuilder valuesMerge = new StringBuilder();
            values.forEach(valuesMerge::append);
            valuesMerge.append(";");
            responseHeaderList.add(name + ": " + valuesMerge + "\r\n");
        });

        return responseHeaderList;
     }

     private static Optional<String> getResponseString(final Object entity){
        if(entity instanceof String){
            try {
                return Optional.of(entity.toString());   
            } catch (Exception ignore) {
                System.out.println("Algo fallo al querer obtener la cadena de respues con la entidad: " + ignore.getMessage());
            }
        }
        return Optional.empty();
     }
}
