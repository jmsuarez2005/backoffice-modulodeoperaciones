/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.actions;

import com.novo.constants.BasicConfig;
import static com.novo.constants.BasicConfig.USUARIO_SESION;
import com.novo.model.ReporteEmisionRecarga;
import com.novo.model.UsuarioSesion;
import com.novo.objects.util.Utils;
import com.novo.process.ReporteActividadDiariaProc;
import com.novo.util.DateUtil;
import com.novo.util.SessionUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;

/**
 *
 * @author jorojas
 */
public class ReporteActividadDiariaAction extends ActionSupport implements BasicConfig, SessionAware {

    private Map<String, Object> session;
    private static Logger log = Logger.getLogger(ReporteActividadDiariaAction.class);
    private String message; //Para imprimir alertas, errores, etc
    private Date fecha; //fecha del reporte
    private Map<String, ReporteEmisionRecarga> reporteCo;
    private Map<String, ReporteEmisionRecarga> reportePe;
    private Map<String, ReporteEmisionRecarga> reporteVe;
    private ReporteEmisionRecarga totales;
    private String cambioBsDolar, cambioSolesDolar, cambioPesosDolar;
    private String ingresoComedorPe;

    private InputStream inputStream;
    private String reportFile;
    private String tipoMessage = ""; //Error, manejo para el jsp
    private Properties prop;
    private String propMigra;
    private String pais;

    public ReporteActividadDiariaAction() {
        this.totales = new ReporteEmisionRecarga();
        this.ingresoComedorPe = "0.00";
        this.fecha = com.novo.util.DateUtil.getYesterday();
        this.prop = Utils.getConfig("oracleRegEx.properties");
        this.propMigra = prop.getProperty("paisOracle");
        this.pais = ((String) ActionContext.getContext().getSession().get("pais"));
    }

    @Override
    public String execute() throws Exception {

        //Valido sesion
        UsuarioSesion usuario = (UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion");
        if (usuario == null) {
            return "out";
        }
        SessionUtil sessionUtil = new SessionUtil();
        String sessionDate = usuario.getSessionDate();
        if (!sessionUtil.validateSession(sessionDate, usuario)) {
            try {
                log.info("Sesión expirada del usuario " + ((UsuarioSesion) ActionContext.getContext().getSession().get(USUARIO_SESION)).getIdUsuario());
                ActionContext.getContext().getSession().clear();
            } catch (Exception e) {
                log.info("Es posible que la sesión para este usuario ya haya sido cerrada previamente a la llamada del LogoutAction.");
            }

            return "out";
        }
        //Fin valida sesion
        ReporteActividadDiariaProc business = new ReporteActividadDiariaProc(fecha, (UsuarioSesion) this.session.get("usuarioSesion"));

        this.reporteCo = business.obtenerEmisionRecargaColombia();
        this.reportePe = business.obtenerEmisionRecargaPeru(this.ingresoComedorPe);
        this.reporteVe = business.obtenerEmisionRecargaVenezuela();
        this.totales = business.obtenerTotales();
        this.cambioBsDolar = business.getCambioBsDolar().toString();
        this.cambioSolesDolar = business.getCambioSolesDolar().toString();
        this.cambioPesosDolar = business.getCambioPesosDolar().toString();

        return SUCCESS;

    }

    public String generarExcel() throws Exception {

        //Valido sesion
        UsuarioSesion usuario = (UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion");
        if (usuario == null) {
            return "out";
        }
        SessionUtil sessionUtil = new SessionUtil();
        String sessionDate = usuario.getSessionDate();
        if (!sessionUtil.validateSession(sessionDate, usuario)) {
            try {
                log.info("Sesión expirada  del usuario " + ((UsuarioSesion) ActionContext.getContext().getSession().get(USUARIO_SESION)).getIdUsuario());
                ActionContext.getContext().getSession().clear();
            } catch (Exception e) {
                log.info("Es posible que la sesión para este usuario ya haya sido cerrada previamente a la llamada del LogoutAction.");
            }

            return "out";
        }
        //Fin valida sesion

        this.message = "Llamada al método de generar excel. " + this.fecha.toString();
        this.execute();

        ReporteActividadDiariaProc business = new ReporteActividadDiariaProc(fecha, (UsuarioSesion) this.session.get("usuarioSesion"));

        business.setReporteCo(this.reporteCo);
        business.setReportePe(this.reportePe);
        business.setReporteVe(this.reporteVe);
        business.setTotales(this.totales);

        this.reportFile = "reporte_actividad_diaria_" + DateUtil.format("YYYYMMdd", fecha) + ".xls";
        try {
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            business.crearWorkBookExcel().write(boas);
            setInputStream(new ByteArrayInputStream(boas.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return EXCEL;
    }

    public String enviarCorreo() throws Exception {

        this.execute();

        ReporteActividadDiariaProc business = new ReporteActividadDiariaProc(fecha, (UsuarioSesion) this.session.get("usuarioSesion"));
        business.setReporteCo(this.reporteCo);
        business.setReportePe(this.reportePe);
        business.setReporteVe(this.reporteVe);
        business.setTotales(this.totales);

        try {
            business.enviarCorreo();
        } catch (Exception ex) {
            this.message = "No se pudo enviar correo: " + ex.toString();
            tipoMessage = "error";
        }

        return SUCCESS;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getFecha() {
        return fecha;
    }

    @RequiredFieldValidator(message = "Debes ingresar una fecha.")
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Map<String, ReporteEmisionRecarga> getReporteCo() {
        return reporteCo;
    }

    public void setReporteCo(Map<String, ReporteEmisionRecarga> reporteCo) {
        this.reporteCo = reporteCo;
    }

    public Map<String, ReporteEmisionRecarga> getReportePe() {
        return reportePe;
    }

    public void setReportePe(Map<String, ReporteEmisionRecarga> reportePe) {
        this.reportePe = reportePe;
    }

    public Map<String, ReporteEmisionRecarga> getReporteVe() {
        return reporteVe;
    }

    public void setReporteVe(Map<String, ReporteEmisionRecarga> reporteVe) {
        this.reporteVe = reporteVe;
    }

    public ReporteEmisionRecarga getTotales() {
        return totales;
    }

    public void setTotales(ReporteEmisionRecarga totales) {
        this.totales = totales;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getReportFile() {
        return reportFile;
    }

    public void setReportFile(String reportFile) {
        this.reportFile = reportFile;
    }

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }

    public String getCambioBsDolar() {
        return cambioBsDolar;
    }

    public void setCambioBsDolar(String cambioBsDolar) {
        this.cambioBsDolar = cambioBsDolar;
    }

    public String getCambioSolesDolar() {
        return cambioSolesDolar;
    }

    public void setCambioSolesDolar(String cambioSolesDolar) {
        this.cambioSolesDolar = cambioSolesDolar;
    }

    public String getCambioPesosDolar() {
        return cambioPesosDolar;
    }

    public void setCambioPesosDolar(String cambioPesosDolar) {
        this.cambioPesosDolar = cambioPesosDolar;
    }

    public String getIngresoComedorPe() {
        return ingresoComedorPe;
    }

    public void setIngresoComedorPe(String ingresoComedorPe) {
        this.ingresoComedorPe = ingresoComedorPe;
    }

    public String getTipoMessage() {
        return tipoMessage;
    }

    public void setTipoMessage(String tipoMessage) {
        this.tipoMessage = tipoMessage;
    }

}
