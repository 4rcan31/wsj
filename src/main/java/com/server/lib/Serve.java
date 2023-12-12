package com.server.lib;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.server.lib.http.HttpHandler;
import com.server.lib.runAndmethods.HttpMethods;
import com.server.lib.runAndmethods.RequestRunner;



public class Serve {

    //aca se guarda las rutas definidas
    private final Map<String, RequestRunner> routes;

    //aca se guarda el socket tcp para http
    private final ServerSocket socket;


    /* 
     * En Java, un Executor es una interfaz en el paquete 
     * java.util.concurrent que proporciona una forma de 
     * ejecutar tareas de forma asíncrona utilizando hilos.
     */
    private final Executor thereadPool;


    private HttpHandler httpHandler;

    private int port;

    //se inicia el constructor
    public Serve(int port) throws IOException {
        this.routes = new HashMap<>();
        this.thereadPool = Executors.newFixedThreadPool(100);
        this.socket = new ServerSocket(port);
        this.port = port;
    }

    public void start() throws IOException{
        this.httpHandler = new HttpHandler(this.routes);
        System.out.println("Server listening in " + this.port);

        while (true) {
            Socket clientConnection = this.socket.accept();
            this.handlerConnection(clientConnection);
        }
    }

    /*
     * Capture each Request / Response lifecycle in a thread
     * executed on the threadPool.
     */

     private void handlerConnection(Socket clienSocketConnection){
       
        

        /* 
         * 
         * Entiendo que runnable es encapsular un proceso en una variable
         * por eso se setea un callback (lamda) para guardarse y ejecutarse
         * en un proceso aparte, en este caso en un hilo, programacion
         * asincrona
         */


        Runnable httpRequestRunner = () -> {
            try {
                this.httpHandler.handlerConnection(
                    clienSocketConnection.getInputStream(), 
                    clienSocketConnection.getOutputStream());
            } catch (Exception e) {
                // TODO: handle exception
            }
        };

        //se ejecuta asincronamente en algun hilo
        this.thereadPool.execute(httpRequestRunner);
     }


     public void addRoute(final HttpMethods method, final String Route, final RequestRunner callback){

        /* 
         * 
         * Esto recive un metodo http desde el enum, saca el nombre
         * string y luego lo concatena a la ruta string que se le pasa
         * 
         * y construye su structura de datos de la siguiente manera:
         * 
         * [
         *  METHOD/ROUTE -> CALLBACK
         * ]
         */
        this.routes.put(method.name().concat(Route), callback);
     }
}
