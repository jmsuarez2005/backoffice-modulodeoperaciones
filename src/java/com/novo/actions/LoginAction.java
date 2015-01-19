/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.actions;
import com.novo.constants.BasicConfig;
import com.novo.constants.RCConfig;
import com.novo.process.UsuarioProc;
import com.novo.util.Utils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;

/**
 *
 * @author jorojas
 */
public class LoginAction extends ActionSupport implements BasicConfig,RCConfig{
    private static Logger log = Logger.getLogger(LoginAction.class);
    
    private String user;
    private String password;
    private String message, successMessage, errorMessage;
    
    public LoginAction() {
        log.info("Llamada al LoginAction");
    }
    
    
    @Override
    public String execute() throws Exception {
        String result;
        int rc;
        UsuarioProc business= new UsuarioProc();
        
        business.login(user, password,Utils.getConfig(BasicConfig.properties).getProperty("pais"));
        rc=business.getRc().getRc();
        
        if (rc == rcUsuarioExitoso){
            successMessage="Login Exitoso";
            ActionContext.getContext().getSession().put(USUARIO_SESION, business.getUsuarioSesion());
            result=SUCCESS;
        } else if (rc == rcCambiarClave){
            ActionContext.getContext().getSession().put(USUARIO_SESION, business.getUsuarioSesion());
            message = business.getRc().getMensaje();
            result="cambiarClave";
        }
        else{
            errorMessage=business.getRc().getMensaje();
            result=ERROR;
        }
        
        log.info("Mensaje: "+message);
        return result;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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