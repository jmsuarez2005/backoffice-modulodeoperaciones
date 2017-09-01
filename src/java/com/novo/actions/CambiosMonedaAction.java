/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.actions;

import com.novo.constants.BasicConfig;
import static com.novo.constants.BasicConfig.USUARIO_SESION;
import com.novo.model.UsuarioSesion;
import com.novo.process.ReporteActividadDiariaProc;
import com.novo.util.SessionUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;

/**
 *
 * @author jorojas
 */
public class CambiosMonedaAction extends ActionSupport implements BasicConfig {

    private static Logger log = Logger.getLogger(CambiosMonedaAction.class);
    private String message;
    private String cambioDolaresVE, cambioDolaresPE, cambioDolaresCO;
    private String tipoMessage = ""; //Error, manejo para el jsp
    private UsuarioSesion usuarioSesion;

    public CambiosMonedaAction() {
        this.cambioDolaresVE = "0.00";
        this.cambioDolaresPE = "0.00";
        this.cambioDolaresCO = "0.00";
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

        ReporteActividadDiariaProc business = new ReporteActividadDiariaProc();
        this.cambioDolaresVE = business.getCambioBsDolar().toString();
        this.cambioDolaresPE = business.getCambioSolesDolar().toString();
        this.cambioDolaresCO = business.getCambioPesosDolar().toString();
        return SUCCESS;
    }

    public String update() throws Exception {

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

        ReporteActividadDiariaProc business = new ReporteActividadDiariaProc();

        if (business.modificarCambioMoneda(ve, this.cambioDolaresVE)
                && business.modificarCambioMoneda(pe, this.cambioDolaresPE)
                && business.modificarCambioMoneda(co, this.cambioDolaresCO)) {
            this.message = "Cambios de Moneda actualizados.";
        } else {
            this.message = "Ocurrió un error al intentar actualizar.";
            tipoMessage = "error";
        }
        this.execute();

        return SUCCESS;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCambioDolaresVE() {
        return cambioDolaresVE;
    }

    public void setCambioDolaresVE(String cambioDolaresVE) {
        this.cambioDolaresVE = cambioDolaresVE;
    }

    public String getCambioDolaresPE() {
        return cambioDolaresPE;
    }

    public void setCambioDolaresPE(String cambioDolaresPE) {
        this.cambioDolaresPE = cambioDolaresPE;
    }

    public String getCambioDolaresCO() {
        return cambioDolaresCO;
    }

    public void setCambioDolaresCO(String cambioDolaresCO) {
        this.cambioDolaresCO = cambioDolaresCO;
    }

    public String getTipoMessage() {
        return tipoMessage;
    }

    public void setTipoMessage(String tipoMessage) {
        this.tipoMessage = tipoMessage;
    }

}
