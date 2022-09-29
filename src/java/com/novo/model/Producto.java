/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.model;

/**
 *
 * @author ggutierrez
 */
public class Producto {
    
    private String nombre;
    private String prefix;
    private String cardProgram;
    private String descripcion;
    public Producto() {
    }
        
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getCardProgram() {
        return cardProgram;
    }

    public void setCardProgram(String cardProgram) {
        this.cardProgram = cardProgram;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    
}
