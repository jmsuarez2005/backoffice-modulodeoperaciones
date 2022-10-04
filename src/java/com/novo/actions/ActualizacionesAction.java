/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.actions;

import com.novo.constants.BasicConfig;
import static com.novo.constants.BasicConfig.USUARIO_SESION;
import com.novo.model.Ajuste;
import com.novo.model.CamposActualizacion;
import com.novo.model.Tarjeta;
import com.novo.model.UsuarioSesion;
import com.novo.process.ReporteTransacciones;
import com.novo.util.SessionUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author ggutierrez
 */
public class ActualizacionesAction extends ActionSupport implements BasicConfig {

    private static Logger log = Logger.getLogger(ActualizacionesAction.class);
    private String message = "";

    private String nroTarjeta;
    private List<Tarjeta> tarjetas;
    private List<Tarjeta> tarjetasAct;
    private List<CamposActualizacion> campos;
    private String selectedCampo = "";
    private String selectedCampoValue = "";
    private UsuarioSesion usuarioSesion;
    private String tipoMessage = ""; //Error, manejo para el jsp

    private File file;
    private String contentType;
    private String filename;

    public ActualizacionesAction() {
        tarjetas = new ArrayList<Tarjeta>();
        tarjetasAct = new ArrayList<Tarjeta>();
        campos = new ArrayList<CamposActualizacion>();
        usuarioSesion = (UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion");
    }

    @Override
    public String execute() {
        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        if (usuarioSesion == null) {
            return "out";
        }
        String sessionDate = usuarioSesion.getSessionDate();
        if (!sessionUtil.validateSession(sessionDate, usuarioSesion)) {
            try {
                log.info("Sesión expirada del usuario " + ((UsuarioSesion) ActionContext.getContext().getSession().get(USUARIO_SESION)).getIdUsuario());
                ActionContext.getContext().getSession().clear();
            } catch (Exception e) {
                log.info("Es posible que la sesión para este usuario ya haya sido cerrada previamente a la llamada del LogoutAction.");
            }

            return "out";
        }
        //Fin valida sesion

        ReporteTransacciones business = new ReporteTransacciones();
        campos = business.getCampos();
        return SUCCESS;
    }

    public String buscarUsuario() {

        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        if (usuarioSesion == null) {
            return "out";
        }
        String sessionDate = usuarioSesion.getSessionDate();
        if (!sessionUtil.validateSession(sessionDate, usuarioSesion)) {
            try {
                log.info("Sesión expirada del usuario " + ((UsuarioSesion) ActionContext.getContext().getSession().get(USUARIO_SESION)).getIdUsuario());
                ActionContext.getContext().getSession().clear();
            } catch (Exception e) {
                log.info("Es posible que la sesión para este usuario ya haya sido cerrada previamente a la llamada del LogoutAction.");
            }

            return "out";
        }
        //Fin valida sesion
        if (nroTarjeta == null || nroTarjeta.equals("")) { 
            message = "Debe introducir un número de tarjeta válido";
            tipoMessage = "error";
            return SUCCESS;
        } else{
            ReporteTransacciones business = new ReporteTransacciones();
            if (nroTarjeta != null && !nroTarjeta.equals("")) {
                log.info("Tarjeta consultada: " + nroTarjeta);
                tarjetas = business.getTarjetasUsuario(null, nroTarjeta);
                if (tarjetas.isEmpty()){
                    this.message = "No se pudo obtener la tarjeta solicitada. Intente nuevamente";
                    tipoMessage = "error";
                    return SUCCESS;
                }
            }
        tarjetasAct = tarjetas;
        ActionContext.getContext().getSession().put("tarjetasAct", tarjetasAct);
        campos = business.getCampos();
        return SUCCESS;
        }
    }

    public String Upload() {

        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        if (usuarioSesion == null) {
            return "out";
        }
        String sessionDate = usuarioSesion.getSessionDate();
        if (!sessionUtil.validateSession(sessionDate, usuarioSesion)) {
            try {
                log.info("Sesión expirada del usuario " + ((UsuarioSesion) ActionContext.getContext().getSession().get(USUARIO_SESION)).getIdUsuario());
                ActionContext.getContext().getSession().clear();
            } catch (Exception e) {
                log.info("Es posible que la sesión para este usuario ya haya sido cerrada previamente a la llamada del LogoutAction.");
            }

            return "out";
        }
        //Fin valida sesion

        ReporteTransacciones business = new ReporteTransacciones();
        campos = business.getCampos();
        Tarjeta tar = new Tarjeta();
        List<Ajuste> ajustes = new ArrayList<Ajuste>();
        Ajuste ajuste = new Ajuste();
        boolean procesoOk = true;
        try {

            if (this.file == null) {
                this.message = "No se pudo cargar el archivo";
                tipoMessage = "error";
                return SUCCESS;
            }

            //Validar que el archivo sea excel
            String ext = FilenameUtils.getExtension(filename);
            if (!ext.equals("xls") && !ext.equals("xlsx")) {
                message = "El archivo a cargar debe estar en formato Excel";
                tipoMessage = "error";
                return SUCCESS;
            } else {

                log.info(file.getAbsolutePath());
                File file2;
                file2 = new File(file.getPath() + filename);
                file.renameTo(file2);
                InputStream buffer = new FileInputStream(file2.getAbsolutePath());
                Workbook workbook = WorkbookFactory.create(buffer);
                Sheet sheet = workbook.getSheetAt(0);
                String tarjetaString = "";
                double tarjetaDouble = 0;
                int i = 0;
                if (sheet.getRow(i) == null) {
                    this.message = "El archivo se encuentra vacio";
                    tipoMessage = "error";
                    return SUCCESS;
                }
                int size = sheet.getPhysicalNumberOfRows();
                String bin = "";

                do {
                    Row row = sheet.getRow(i);

                    //La hoja excel debe contener mas de una tarjeta para el proceso masivo
                    if (size == 1) {
                        message = "Debe existir más de un registro para ejecutar actualización masiva";
                        tipoMessage = "error";
                        tarjetasAct.clear();
                        procesoOk = false;
                        break;
                    }

                    if (row == null) {
                        break;
                    } else if (row.getCell(0) == null) {
                        break;
                    } else if (row.getCell(0).getCellType() == Cell.CELL_TYPE_BLANK) {
                        break;
                    }

                    if (row.getCell(0).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        tarjetaDouble = row.getCell(0).getNumericCellValue();
                        tarjetaString = String.valueOf(tarjetaDouble);
                    } else {
                        tarjetaString = row.getCell(0).getStringCellValue();
                    }

                    //Valida que el archivo excel contenga tarjetas de un mismo bin
                    if (i == 0) {
                        bin = tarjetaString.substring(0, 8);
                    } else {
                        if (!bin.equals(tarjetaString.substring(0, 8))) {
                            message = "El archivo solo puede contener tarjetas de un mismo bin.";
                            tipoMessage = "error";
                            procesoOk = false;
                            tarjetasAct.clear();
                            break;
                        }
                    }

                    ajuste.setTarjeta(tarjetaString);
                    tar.setNroTarjeta(tarjetaString);
                    if (!tarjetaString.matches("\\d{16}")) {
                        message = "Formato incorrecto de la tarjeta";
                        tipoMessage = "error";
                        tarjetasAct.clear();
                        procesoOk = false;
                        break;
                    }
                    ajustes.add(ajuste);
                    tarjetasAct.add(tar);
                    ajuste = new Ajuste();
                    tar = new Tarjeta();
                    log.info("tarjeta [" + tarjetaString + "] ");
                    i++;
                } while (/*tarjeta!=0*/!tarjetaString.equals(""));
                if (i > 500) {
                    message = "Número de registros en el archivo excedido";
                    tarjetasAct.clear();
                    return SUCCESS;
                }
                if (procesoOk) {
                    message = "Carga de archivo exitosa. " + file.getName();
                    String respuesta = business.checkTarjetas(ajustes);
                    if (respuesta.contains("errorT")) {
                        //message = "Error, tarjeta inexistente";
                        message = "No existe(n) la(s) siguiente(s) tarjeta(s):" + respuesta.substring(6, respuesta.length());
                        tipoMessage = "error";
                        tarjetasAct.clear();
                        return SUCCESS;
                    } else if (respuesta.contains("error")) {
                        message = "No se pudo realizar la actualización";
                        tipoMessage = "error";
                        tarjetasAct.clear();
                        return SUCCESS;
                    }

                    ActionContext.getContext().getSession().put("tarjetasAct", tarjetasAct);
                }
            }
        } catch (Exception e) {
            log.error("error ", e);
            tipoMessage = "error";
            message = "No se pudo realizar la actualización";
        }
        return SUCCESS;
    }

    public String actualizar() throws SQLException {

        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        if (usuarioSesion == null) {
            return "out";
        }
        String sessionDate = usuarioSesion.getSessionDate();
        if (!sessionUtil.validateSession(sessionDate, usuarioSesion)) {
            try {
                log.info("Sesión expirada del usuario " + ((UsuarioSesion) ActionContext.getContext().getSession().get(USUARIO_SESION)).getIdUsuario());
                ActionContext.getContext().getSession().clear();
            } catch (Exception e) {
                log.info("Es posible que la sesión para este usuario ya haya sido cerrada previamente a la llamada del LogoutAction.");
            }

            return "out";
        }
        //Fin valida sesion

        ReporteTransacciones business = new ReporteTransacciones();
        tarjetasAct = (List<Tarjeta>) ActionContext.getContext().getSession().get("tarjetasAct");
        campos = business.getCampos();
        List<CamposActualizacion> campos2;
        log.info("Campos seleccionados [" + selectedCampo + "]");
        log.info("Valor de los campos [" + selectedCampoValue + "]");
        //call method that matches idfields with the values selected by the user.
        campos2 = matchFields(selectedCampo, selectedCampoValue);

        if (campos2.get(0).getValor().equals("errorIdPersona")) {
            message = "El valor de [id persona] ha actualizar es inválido";
            tipoMessage = "error";
            selectedCampoValue = "";
            selectedCampo = "";
            tarjetasAct = new ArrayList<Tarjeta>();
            return SUCCESS;
        }

        Collections.sort(campos2);
        if (campos2.size() == 0) {
            message = "El campo/valor no debe estar vacio";
            tipoMessage = "error";
            selectedCampoValue = "";
            selectedCampo = "";
            tarjetasAct = new ArrayList<Tarjeta>();
            return SUCCESS;
        }

        String respuesta = business.makeUpdates(tarjetasAct, campos2, usuarioSesion.getIdUsuario());
        if (respuesta.contains("-3")) {
            message = "La cedula [" + respuesta.substring(2, respuesta.lastIndexOf(":")) + "] ha actualizar ya existe en otro registro a nombre de "
                    + "[" + respuesta.substring(respuesta.lastIndexOf(":") + 1, respuesta.length()) + "]";
            tipoMessage = "error";
            selectedCampoValue = "";
            selectedCampo = "";
            tarjetasAct = new ArrayList<Tarjeta>();
            return SUCCESS;

        } else if (!respuesta.equals("0")) {
            message = "No se pudo cargar el lote de actualización en Base de Datos";
            tipoMessage = "error";
            selectedCampoValue = "";
            selectedCampo = "";
            tarjetasAct = new ArrayList<Tarjeta>();
            return SUCCESS;
        }
        selectedCampoValue = "";
        selectedCampo = "";
        tarjetasAct = new ArrayList<Tarjeta>();
        message = "El lote de actualización ha sido cargado exitosamente.";
        //call business method that register the updates for the car.
        return SUCCESS;
    }

    public boolean isNumeric(String cadena) {
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public void setUpload(File file) {
        this.file = file;
    }

    public void setUploadContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setUploadFileName(String filename) {
        this.filename = filename;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNroTarjeta() {
        return nroTarjeta;
    }

    public void setNroTarjeta(String nroTarjeta) {
        this.nroTarjeta = nroTarjeta;
    }

    public List<Tarjeta> getTarjetas() {
        return tarjetas;
    }

    public void setTarjetas(List<Tarjeta> tarjetas) {
        this.tarjetas = tarjetas;
    }

    public List<CamposActualizacion> getCampos() {
        return campos;
    }

    public void setCampos(List<CamposActualizacion> campos) {
        this.campos = campos;
    }

    public String getSelectedCampo() {
        return selectedCampo;
    }

    public void setSelectedCampo(String selectedCampo) {
        this.selectedCampo = selectedCampo;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSelectedCampoValue() {
        return selectedCampoValue;
    }

    public void setSelectedCampoValue(String selectedCampoValue) {
        this.selectedCampoValue = selectedCampoValue;
    }

    public List<CamposActualizacion> matchFields(String campo, String campoValue) {
        String[] vecCampo, vecCampoValue;
        List<CamposActualizacion> matchedFields = new ArrayList<CamposActualizacion>();
        CamposActualizacion campos = new CamposActualizacion();
        vecCampo = campo.split(",");
        vecCampoValue = campoValue.split(",");
        for (int i = 0; i < vecCampo.length; i++) {
            if (vecCampo[i].trim().equals("3")) { // 3 - identificacion persona
                if (!isNumeric(vecCampoValue[i].trim())) {
                    matchedFields.clear();
                    campos.setValor("errorIdPersona");
                    matchedFields.add(campos);
                    log.error("El id persona [" + vecCampoValue[i].trim() + "] debe ser númerico");
                    return matchedFields;
                }
            }
            if (!vecCampo[i].trim().equals("")) {
                campos.setIdCampo(vecCampo[i].trim());
                if (vecCampoValue[i].trim().equals("")) {
                    matchedFields.clear();
                    return matchedFields;
                }
                campos.setValor(vecCampoValue[i].trim());
                matchedFields.add(campos);
                campos = new CamposActualizacion();
            }
        }
        return matchedFields;
    }

    public List<Tarjeta> getTarjetasAct() {
        return tarjetasAct;
    }

    public void setTarjetasAct(List<Tarjeta> tarjetasAct) {
        this.tarjetasAct = tarjetasAct;
    }

    public String getTipoMessage() {
        return tipoMessage;
    }

    public void setTipoMessage(String tipoMessage) {
        this.tipoMessage = tipoMessage;
    }

}
