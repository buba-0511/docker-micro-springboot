package com.mycompany.httpserver.examples;

import com.mycompany.httpserver.GetMapping;
import com.mycompany.httpserver.RequestParam;
import com.mycompany.httpserver.RestController;

@RestController
public class PromedioController {

    @GetMapping("/promedio")
    public static String promedio(@RequestParam(value = "numeros", defaultValue = "0") String numeros) {
        try {
            String[] partes = numeros.split(",");
            double suma = 0;
            for (String n : partes) {
                suma += Double.parseDouble(n.trim());
            }
            double resultado = suma / partes.length;
            return String.format("Promedio de [%s] = %.2f", numeros, resultado);
        } catch (NumberFormatException e) {
            return "Error: ingresa numeros separados por coma. Ejemplo: ?numeros=5,3,4";
        }
    }
}
