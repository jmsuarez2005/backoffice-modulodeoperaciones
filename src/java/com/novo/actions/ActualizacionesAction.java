/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.actions;

import com.novo.constants.BasicConfig;
import com.novo.model.Ajuste;
import com.novo.model.CamposActualizacion;
import com.novo.model.Tarjeta;
import com.novo.model.UsuarioSesion;
import com.novo.process.ReporteTransacciones;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
        ReporteTransacciones business = new ReporteTransacciones();
        campos = business.getCampos();
        return SUCCESS;
    }
    
    public String buscarUsuario() {
        ReporteTransacciones business = new ReporteTransacciones();
        if (nroTarjeta != null && !nroTarjeta.equals("")) {
            log.info("tarjeta = " + nroTarjeta);
            tarjetas = business.getTarjetasUsuario(null, nroTarjeta);
        }
        tarjetasAct = tarjetas;
        ActionContext.getContext().getSession().put("tarjetasAct", tarjetasAct);
        campos = business.getCampos();
        return SUCCESS;
    }
    
    public String Upload() {
        
        ReporteTransacciones business = new ReporteTransacciones();
        campos = business.getCampos();
        Tarjeta tar = new Tarjeta();
        List<Ajuste> ajustes = new ArrayList<Ajuste>();
        Ajuste ajuste = new Ajuste();
        boolean procesoOk = true;
        try {
            log.info(file.getAbsolutePath() + " " + file.getCanonicalPath());
            File file2;
            file2 = new File(file.getPath() + filename);
            file.renameTo(file2);
            InputStream buffer = new FileInputStream(file2.getAbsolutePath());
            Workbook workbook = WorkbookFactory.create(buffer);
            Sheet sheet = workbook.getSheetAt(0);
            String tarjetaString = "";
            double tarjetaDouble = 0;
            int i = 0;
            do {
                Row row = sheet.getRow(i);
                //System.out.println("CELDA:" + row.getCell(0).getCellType());
                //System.out.println("holaaaaaaaaaaa" + row.getCell(0));
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
                ajuste.setTarjeta(tarjetaString);
                tar.setNroTarjeta(tarjetaString);
                if (!tarjetaString.matches("\\d{16}")) {
                    message = "Error con el formato de la tarjeta";
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
                message = "Numero de registros en el archivo excedido";
                return SUCCESS;
            }
            if (procesoOk) {
                message = "Carga de Archivo Exitosa. " + file.getName();
                if (business.checkTarjetas(ajustes).compareToIgnoreCase("error") == 0) {
                    message = "Error, Tarjeta Inexistente.";
                    return SUCCESS;
                }
                ActionContext.getContext().getSession().put("tarjetasAct", tarjetasAct);
            }
        } catch (Exception e) {
            log.error("error ", e);
            message = "error";
        }
        return SUCCESS;
    }
    
    public String actualizar() {
        ReporteTransacciones business = new ReporteTransacciones();
        tarjetasAct = (List<Tarjeta>) ActionContext.getContext().getSession().get("tarjetasAct");
        campos = business.getCampos();
        List<CamposActualizacion> campos2;
        log.info("campos seleccionados [" + selectedCampo + "]");
        log.info("Valor de los campos [" + selectedCampoValue + "]");
        //call method that matches idfields with the values selected by the user.
        campos2 = matchFields(selectedCampo, selectedCampoValue);
        Collections.sort(campos2);
        if (campos2.size() == 0) {
            message = "debe seleccionar al menos un campo";
            selectedCampoValue = "";
            selectedCampo = "";
            tarjetasAct = new ArrayList<Tarjeta>();
            return SUCCESS;
        }
        if (business.makeUpdates(tarjetasAct, campos2, usuarioSesion.getIdUsuario()) != 0) {
            message = "Error Cargando lote de Actualizacion en Base de datos";
            selectedCampoValue = "";
            selectedCampo = "";
            tarjetasAct = new ArrayList<Tarjeta>();
            return SUCCESS;
        }
        selectedCampoValue = "";
        selectedCampo = "";
        tarjetasAct = new ArrayList<Tarjeta>();
        message = "El lote de Actualizacion ha sido cargado exitosamente.";
        //call business method that register the updates for the car.
        return SUCCESS;
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
            if (!vecCampo[i].trim().equals("")) {
                campos.setIdCampo(vecCampo[i].trim());
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
    
}
