/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.actions;

import com.novo.constants.BasicConfig;
import com.novo.model.UsuarioSesion;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;

/**
 *
 * @author jorojas
 */
public class LogoutAction extends ActionSupport implements BasicConfig{
    private static Logger log = Logger.getLogger(LogoutAction.class);
    private String message, successMessage, errorMessage;
    
    public LogoutAction() {
    }
    
    public String execute() throws Exception {
        log.info("Llamada al LogoutAction");
        
        try{
            log.info("Cerrando sesión de "+((UsuarioSesion)ActionContext.getContext().getSession().get(USUARIO_SESION)).getIdUsuario());
            ActionContext.getContext().getSession().clear();
        }catch(Exception e){  
            log.info("Es posible que la sesión para este usuario ya haya sido cerrada previamente a la llamada del LogoutAction."); 
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
    
    
}
