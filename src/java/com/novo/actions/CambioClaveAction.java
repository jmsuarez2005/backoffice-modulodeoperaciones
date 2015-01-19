/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.actions;

import com.novo.constants.BasicConfig;
import com.novo.model.UsuarioSesion;
import com.novo.process.UsuarioProc;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 *
 * @author jorojas
 */
public class CambioClaveAction extends ActionSupport implements BasicConfig{
    private String message, successMessage, errorMessage;
    private String claveActual,newpw,newpwcf;
    
    public CambioClaveAction() {
        this.message = "";
        this.successMessage = "";
        this.errorMessage = "";
    }
    
    @Override
    public String execute() throws Exception {
        return SUCCESS;
    }
    
    public String cambiar() throws Exception {
        
        UsuarioSesion usuarioSesion = (UsuarioSesion)ActionContext.getContext().getSession().get(USUARIO_SESION);
        UsuarioProc business = new UsuarioProc();
        
        if (business.cambiarClave(usuarioSesion, this.claveActual, this.newpw)){
            this.successMessage = "Clave actualizada de forma exitosa.";
            ActionContext.getContext().getSession().put(USUARIO_SESION, usuarioSesion);
            return "cambioExitoso";
        }else{
            this.errorMessage = business.getRc().getMensaje();
        }
        
        return SUCCESS;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getClaveActual() {
        return claveActual;
    }

    public void setClaveActual(String claveActual) {
        this.claveActual = claveActual;
    }

    public String getNewpw() {
        return newpw;
    }

    public void setNewpw(String newpw) {
        this.newpw = newpw;
    }

    public String getNewpwcf() {
        return newpwcf;
    }

    public void setNewpwcf(String newpwcf) {
        this.newpwcf = newpwcf;
    }
    
    
}

