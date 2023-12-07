package App;

import com.server.lib.Serve;
import com.server.lib.runAndmethods.HttpMethods; // Importa la clase del enumerador HttpMethods
import com.server.lib.services.HttpResponse;

public class Main {

    public static void main(String[] args) {
        try {
            Serve server = new Serve(8080);

            server.addRoute(HttpMethods.GET, "/test",
            (request) -> new HttpResponse.Builder()
                        .setStatusCode(200)
                        .addHeader("Content-Type", "text/html")
                        .setEntity("<HTML> <P> Hello There... </P> </HTML>")
                        .build());


            server.start();
        } catch (Exception e) {
           System.out.println("Hubo un error en el servidor: " + e.getMessage());
        }
    }
}
