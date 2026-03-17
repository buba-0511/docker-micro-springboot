/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.httpserver;

import java.net.URI;

public class HttpRequest {
    
    URI requri = null;
    
    public HttpRequest(URI requri){
        this.requri = requri;
    }
    
    public String getValue(String paramName){
        String name  = requri.getQuery().split("=")[1];
        return name;
    }
}
