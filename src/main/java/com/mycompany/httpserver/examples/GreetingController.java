/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.httpserver.examples;

import com.mycompany.httpserver.GetMapping;
import com.mycompany.httpserver.RestController;
import com.mycompany.httpserver.RequestParam;
/**
 *
 */
@RestController
public class GreetingController {
    
    @GetMapping("/greeting")
    public static String greeting(@RequestParam(value = "name", defaultValue = "mundo") String name) {
        return "Hola " + name;
    }
    

}
