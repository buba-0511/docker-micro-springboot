/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.httpserver.MicroSpringBoot;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mycompany.httpserver.HttpServer;

public class MicroSpringBoot {
    
    private static void loadControllers(String paquete) throws Exception {
        String ruta = paquete.replace(".", "/");
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        java.net.URL url = loader.getResource(ruta);
        if (url == null) return;
        java.io.File dir = new java.io.File(url.toURI());
        for (java.io.File file : java.util.Objects.requireNonNull(dir.listFiles())) {
            if (file.getName().endsWith(".class")) {
                String claseNombre = paquete + "." + file.getName().replace(".class", "");
                Class<?> clazz = Class.forName(claseNombre);
                if (clazz.isAnnotationPresent(com.mycompany.httpserver.RestController.class)) {
                    for (java.lang.reflect.Method metodo : clazz.getDeclaredMethods()) {
                        if (metodo.isAnnotationPresent(com.mycompany.httpserver.GetMapping.class)) {
                            String rutaMetodo = metodo.getAnnotation(com.mycompany.httpserver.GetMapping.class).value();
                            com.mycompany.httpserver.HttpServer.services.put(rutaMetodo, metodo);
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            loadControllers("com.mycompany.httpserver.examples");
            HttpServer.staticFiles("webroot");
            HttpServer.startServer(args);
        } catch (IOException ex) {
            Logger.getLogger(MicroSpringBoot.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(MicroSpringBoot.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
