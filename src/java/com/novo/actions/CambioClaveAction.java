/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.actions;

import com.novo.constants.BasicConfig;
import static com.novo.constants.BasicConfig.USUARIO_SESION;
import com.novo.model.UsuarioSesion;
import com.novo.process.UsuarioProc;
import com.novo.util.SessionUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;

/**
 *
 * @author jorojas
 */
public class CambioClaveAction extends ActionSupport implements BasicConfig{
    private String message, successMessage, errorMessage;
    private String claveActual,newpw,newpwcf;
    private UsuarioSesion usuarioSesion;
    private static Logger log = Logger.getLogger(CambioClaveAction.class);
    
    public CambioClaveAction() {
        this.message = "";
        this.successMessage = "";
        this.errorMessage = "";
        usuarioSesion = (UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion");
    }
    
    @Override
    public String execute() throws Exception {
        
        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        if (usuarioSesion == null) {
            return "out";
        }
        String sessionDate = usuarioSesion.getSessionDate();
        if (!sessionUtil.validateSession(sessionDate, usuarioSesion)) {
            try {
                log.info("Sesion expira del usuario " + ((UsuarioSesion) ActionContext.getContext().getSession().get(USUARIO_SESION)).getIdUsuario());
                ActionContext.getContext().getSession().clear();
            } catch (Exception e) {
                log.info("Es posible que la sesión para este usuario ya haya sido cerrada previamente a la llamada del LogoutAction.");
            }

            return "out";
        }
        //Fin valida sesion
        
        return SUCCESS;
    }
    
    public String cambiar() throws Exception {
        
        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        if (usuarioSesion == null) {
            return "out";
        }
        String sessionDate = usuarioSesion.getSessionDate();
        if (!sessionUtil.validateSession(sessionDate, usuarioSesion)) {
            try {
                log.info("Sesion expira del usuario " + ((UsuarioSesion) ActionContext.getContext().getSession().get(USUARIO_SESION)).getIdUsuario());
                ActionContext.getContext().getSession().clear();
            } catch (Exception e) {
                log.info("Es posible que la sesión para este usuario ya haya sido cerrada previamente a la llamada del LogoutAction.");
            }

            return "out";
        }
        //Fin valida sesion
        
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

