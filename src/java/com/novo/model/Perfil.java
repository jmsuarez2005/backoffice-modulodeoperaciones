/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.model;

import java.util.ArrayList;

/**
 *
 * @author jorojas
 */
public class Perfil {
    private String idPerfil;
    private String descripcion;
    private ArrayList<Funcion> funciones;

    public Perfil() {
        funciones = new ArrayList();
    }

    public Perfil(String idPerfil, String descripcion, ArrayList<Funcion> funciones) {
        this.idPerfil = idPerfil;
        this.descripcion = descripcion;
        this.funciones = funciones;
    }

    public String getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(String idPerfil) {
        this.idPerfil = idPerfil;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ArrayList<Funcion> getFunciones() {
        return funciones;
    }

    public void setFunciones(ArrayList<Funcion> funciones) {
        this.funciones = funciones;
    }
    
}
