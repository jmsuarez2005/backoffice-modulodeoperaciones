/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.actions;

import com.novo.constants.BasicConfig;
import com.novo.process.ReporteActividadDiariaProc;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;

/**
 *
 * @author jorojas
 */
public class CambiosMonedaAction extends ActionSupport implements BasicConfig {
    private static Logger log = Logger.getLogger(CambiosMonedaAction.class);
    private String message;
    private String cambioDolaresVE,cambioDolaresPE,cambioDolaresCO;
    
    public CambiosMonedaAction() {
        this.cambioDolaresVE = "0.00";
        this.cambioDolaresPE = "0.00";
        this.cambioDolaresCO = "0.00";
    }
    
    @Override
    public String execute() throws Exception {
        ReporteActividadDiariaProc business = new ReporteActividadDiariaProc();
        this.cambioDolaresVE=business.getCambioBsDolar().toString();
        this.cambioDolaresPE=business.getCambioSolesDolar().toString();
        this.cambioDolaresCO=business.getCambioPesosDolar().toString();
        return SUCCESS;
    }
    
    public String update() throws Exception {
        ReporteActividadDiariaProc business = new ReporteActividadDiariaProc();
        
        if (business.modificarCambioMoneda(ve, this.cambioDolaresVE)
          &&business.modificarCambioMoneda(pe, this.cambioDolaresPE)
          &&business.modificarCambioMoneda(co, this.cambioDolaresCO))
        {
            this.message = "Cambios de Moneda actualizados.";
        }
        else{
            this.message = "Ocurrió un error al intentar actualizar.";
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
    
    
}
