## Proyecto Java: Servidor HTTP 1.1 (HTTP/1.1)

Este proyecto tiene como objetivo brindar una comprensión práctica del lenguaje Java, específicamente en el contexto del manejo de solicitudes HTTP mediante TCP y sockets. Se trata de un servidor HTTP simple pero funcional.

### Cómo usar

El archivo clave para ejecutar el servidor se encuentra en `src/main/java/App/Main.java`.

```java
    public static void main(String[] args) {
        try {
            Serve server = new Serve(8081);
    
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
```

### Clase `Serve`

La clase `Serve` permite configurar y ejecutar el servidor web. Su constructor requiere un parámetro `Integer` que indica el puerto:

```java
Serve server = new Serve(8081);
```

### Definición de Rutas

Como cualquier servidor HTTP, se necesitan rutas. Para agregar rutas, se utiliza el método `addRoute` de la clase `Serve`, que toma tres parámetros y no retorna nada:

```java
void com.server.lib.Serve.addRoute(HttpMethods method, String Route, RequestRunner callback)
```

- El primer parámetro es un `Enum` llamado `HttpMethods`, que contiene los métodos HTTP disponibles: `GET`, `POST`, `PUT` y `DELETE`.

- El segundo parámetro, de tipo `String`, representa la ruta.

- El tercer parámetro es una función anónima que se ejecuta cuando se visita la ruta especificada.

### Construcción de Respuestas

Cuando una ruta se visita, se ejecuta la función anónima que recibe un objeto `HttpRequest`. Con este objeto, puedes acceder a datos del request, como la URI, los headers y el método.

La construcción de la respuesta se realiza a través de la clase `HttpResponse.Builder()`, que ofrece métodos encadenados:

- `setStatusCode(int statusCode)`: Define el estado de la respuesta.
- `addHeader(String keyName, String value)`: Agrega headers a la respuesta.
- `setEntity(Object entity)`: Define el cuerpo de la respuesta (puede ser un String HTML u otro contenido).

Al finalizar, el método `build()` construye la respuesta y devuelve un objeto `HttpResponse`.

### Iniciar el Servidor

Una vez configuradas las rutas, el servidor se inicia mediante el método `start()` de la clase `Serve`:

```java
serve.start()
```

¡El servidor estará en funcionamiento!
