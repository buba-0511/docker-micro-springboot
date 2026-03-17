package com.mycompany.httpserver;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que implementa un servidor http en un puerto dado respondinedo varias
 * solicitudes y llamando archivos
 *
 */
public class HttpServer {

    /**
     * Inicia el servidor en el puerto 35000 y permite que soporte múlltiples
     * solicitudes seguidas no concurrentes
     *
     * @throws IOException Si ocurre un error de entrada/salida al crear el
     * socket.
     * @throws URISyntaxException Si ocurre un error con la URI solicitada.
     */
    public static Map<String, Method> services = new HashMap();
    private static String folder;
    private static volatile boolean running = true;
    private static ServerSocket serverSocket;

    public static void startServer(String[] args) throws IOException, URISyntaxException {

        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running = false;
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing server socket on shutdown.");
            }
            System.out.println("Servidor apagado correctamente.");
        }));

        while (running) {
            Socket clientSocket;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                if (!running) break;
                System.err.println("Accept failed.");
                break;
            }

            new Thread(() -> handleClient(clientSocket)).start();
        }
        if (!serverSocket.isClosed()) serverSocket.close();
    }

    private static void handleClient(Socket clientSocket) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedOutputStream dataOut = new BufferedOutputStream(clientSocket.getOutputStream());
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            String path = null;
            URI requesturi = null;
            boolean firstline = true;
            while ((inputLine = in.readLine()) != null) {
                if (firstline) {
                    requesturi = new URI(inputLine.split(" ")[1]);
                    path = requesturi.getPath();
                    System.out.println("Path: " + path);
                    firstline = false;
                }
                if (!in.ready()) {
                    break;
                }
            }

            if (path == null || path.equals("/")) {
                path = "/index.html";
            }

            if (path.startsWith("/app")) {
                String response = processRequest(requesturi);
                out.print(response);
                out.flush();
            } else {
                serveStaticFile(out, dataOut, path);
            }

            out.close();
            dataOut.close();
            in.close();
            clientSocket.close();
        } catch (IOException | URISyntaxException e) {
            Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Retorna el tipo correspondiente a un archivo según su extensión.
     *
     * @param fileName Nombre del archivo solicitado
     * @return Cadena con el tipo de archivo
     */
    private static String tipoDeArchivo(String fileName) {
        if (fileName.endsWith(".html")) {
            return "text/html";
        }
        if (fileName.endsWith(".css")) {
            return "text/css";
        }
        if (fileName.endsWith(".js")) {
            return "application/javascript";
        }
        if (fileName.endsWith(".png")) {
            return "image/png";
        }
        return "application/octet-stream";
    }

    private static String processRequest(URI requesturi) {
        try {
            String serviceRoute = requesturi.getPath().substring(4);
            HttpRequest req = new HttpRequest(requesturi);
            String key = requesturi.getPath().substring(4);
            HttpResponse res = new HttpResponse(requesturi);
            Method m = services.get(key);

            String header = "HTTP/1.1 200 OK\n\r"
                    + "content-type: application/json\n\r"
                    + "\n\r";

            
            String paramValue = null;
            String query = requesturi.getQuery();
            if (m.getParameterCount() == 1) {
                com.mycompany.httpserver.RequestParam rp = m.getParameters()[0]
                        .getAnnotation(com.mycompany.httpserver.RequestParam.class);
                String paramName = rp != null ? rp.value() : "";
                String defaultValue = rp != null ? rp.defaultValue() : "";
                if (query != null) {
                    for (String param : query.split("&")) {
                        if (param.startsWith(paramName + "=")) {
                            paramValue = param.substring(paramName.length() + 1);
                            break;
                        }
                    }
                }
                if (paramValue == null) paramValue = defaultValue;
                return header + m.invoke(null, paramValue);
            } else {
                return header + m.invoke(null);
            }
        } catch (IllegalAccessException ex) {
            Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "404";
    }
   

    private static void serveStaticFile(PrintWriter out, BufferedOutputStream dataOut, String path) throws IOException {
        File file = new File(System.getProperty("user.dir"), "www/" + folder + path);

        if (file.exists() && !file.isDirectory()) {
            byte[] fileData = Files.readAllBytes(file.toPath());

            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: " + tipoDeArchivo(file.getName()));
            out.println("Content-Length: " + fileData.length);
            out.println();
            out.flush();

            dataOut.write(fileData, 0, fileData.length);
            dataOut.flush();
        } else {
            out.println("HTTP/1.1 404 Not Found");
            out.println("Content-Type: text/html");
            out.println();
            out.println("<h1>404 Not Found</h1>");
        }
    }

    public static void staticFiles(String path) {
        folder = path;
    }

    private static void loadComponents(String[] args) {
        try {
            Class c = Class.forName(args[0]);
            if (c.isAnnotationPresent(RestController.class)) {
                Method[] methods = c.getDeclaredMethods();
                for (Method m : methods) {
                    if (m.isAnnotationPresent(GetMapping.class)) {
                        String mapping = m.getAnnotation(GetMapping.class).value();
                        services.put(mapping, m);
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
