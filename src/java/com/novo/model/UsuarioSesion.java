/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.model;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author jorojas
 */
public class UsuarioSesion{
    
    private String idUsuario;
    private String acNombre;
    private String password;   
    private String acEstatus;
    private String acTipo;
    private String acUbicacion;
    private String acEmail;
    private String modificadoPor;
    private String idPersona;
    private Date modificado;
    private ArrayList<Perfil> perfiles;

    public UsuarioSesion() {
        this.idUsuario = "";
        this.acNombre = "";
        this.password = "";   
        this.acEstatus = "";
        this.acTipo = "";
        this.acUbicacion = "";
        this.acEmail = "";
        this.modificadoPor = "";
        this.idPersona = "";
        this.modificado = new Date();
        this.perfiles = new ArrayList();
    }

    public UsuarioSesion(String user, String password) {
        this.idUsuario = user;
        this.password = password;
    }
    
    public String print(){
        return    " idUsuario: "+this.idUsuario
                + " acNombre: "+this.acNombre
                + " password: "+this.password
                + " acEstatus: "+this.acEstatus
                + " acTipo: "+this.acTipo
                + " acUbicacion: "+this.acUbicacion
                + " acEmail: "+this.acEmail
                + " modificadoPor: "+this.modificadoPor
                + " idPersona: "+this.idPersona
                + " modificado: "+this.modificado.toString();
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAcNombre() {
        return acNombre;
    }

    public void setAcNombre(String acNombre) {
        this.acNombre = acNombre;
    }

    public String getAcEstatus() {
        return acEstatus;
    }

    public void setAcEstatus(String acEstatus) {
        this.acEstatus = acEstatus;
    }

    public String getAcTipo() {
        return acTipo;
    }

    public void setAcTipo(String acTipo) {
        this.acTipo = acTipo;
    }

    public String getAcUbicacion() {
        return acUbicacion;
    }

    public void setAcUbicacion(String acUbicacion) {
        this.acUbicacion = acUbicacion;
    }

    public String getAcEmail() {
        return acEmail;
    }

    public void setAcEmail(String acEmail) {
        this.acEmail = acEmail;
    }

    public String getModificadoPor() {
        return modificadoPor;
    }

    public void setModificadoPor(String modificadoPor) {
        this.modificadoPor = modificadoPor;
    }

    public Date getModificado() {
        return modificado;
    }

    public void setModificado(Date modificado) {
        this.modificado = modificado;
    }

    public String getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(String idPersona) {
        this.idPersona = idPersona;
    }

    public ArrayList<Perfil> getPerfiles() {
        return perfiles;
    }

    public void setPerfiles(ArrayList<Perfil> perfiles) {
        this.perfiles = perfiles;
    }
    
    
}
