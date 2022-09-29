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
import com.novo.util.SessionUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
public class AfiliacionAction extends ActionSupport implements BasicConfig {

    private static Logger log = Logger.getLogger(AfiliacionAction.class);
    private String message = "";
    private String tipoMessage = ""; //Error, manejo para el jsp

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
    private int cantidadTarjetas;

    public AfiliacionAction() {
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
        //campos = business.getCampos();
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
                tarjetasAct.clear();
                return "success";
            }

            //Validar que el archivo sea excel
            String ext = FilenameUtils.getExtension(filename);
            if (!ext.equals("xls") && !ext.equals("xlsx")) {
                message = "El archivo a cargar debe estar en formato Excel";
                tipoMessage = "error";
                tarjetasAct.clear();
                return "success";
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
                double dniDouble = 0;
                int i = 0;
                int k = 0;
                int size = sheet.getPhysicalNumberOfRows();
                String bin = "";
                String dni;
                String nombre;
                String apellido;
                String codcia;
                if (sheet.getRow(i) == null) {
                    message = "El archivo se encuentra vacio";
                    tipoMessage = "error";
                    tarjetasAct.clear();
                    return "success";
                }
                do {
                    Row row = sheet.getRow(i);
                    //System.out.println("CELDA:" + row.getCell(0).getCellType());
                    //System.out.println("holaaaaaaaaaaa" + row.getCell(0));

                    //La hoja excel debe contener mas de una tarjeta para el proceso masivo
                    if (size == 1) {
                        message = "Debe haber más de un registro para ejecutar afiliación masiva";
                        procesoOk = false;
                        tipoMessage = "error";
                        tarjetasAct.clear();
                        break;
                    }

                    if (row == null) {
                        break;
                    } else if ((row.getCell(0) == null || row.getCell(0).getCellType() == Cell.CELL_TYPE_BLANK) && row.getCell(1) != null) {
                        if (!row.getCell(1).getStringCellValue().isEmpty()) {
                            if (row.getCell(1).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                dniDouble = row.getCell(1).getNumericCellValue();
                                dni = String.valueOf(dniDouble);
                            } else {
                                dni = row.getCell(1).getStringCellValue();
                            }
                            message = "El archivo debe contener el número de tarjeta del DNI [" + dni + "]";
                            procesoOk = false;
                            tipoMessage = "error";
                            tarjetasAct.clear();
                            break;
                        } else {
                            break;
                        }
                    } else if (row.getCell(0).getCellType() == Cell.CELL_TYPE_BLANK && row.getCell(1).getCellType() == Cell.CELL_TYPE_BLANK) {
                        break;
                    }

                    //TARJETA
                    if (row.getCell(0).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        tarjetaDouble = row.getCell(0).getNumericCellValue();
                        tarjetaString = String.valueOf(tarjetaDouble);
                    } else {
                        tarjetaString = row.getCell(0).getStringCellValue();
                    }

                    //CEDULA
                    if (row.getCell(1) == null) {
                        message = "El archivo debe contener la identificación de la persona por cada tarjeta ha afiliar.";
                        procesoOk = false;
                        tipoMessage = "error";
                        tarjetasAct.clear();
                        break;
                    } else {
                        if (row.getCell(1).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                            dniDouble = row.getCell(1).getNumericCellValue();
                            dni = String.valueOf(BigDecimal.valueOf(dniDouble));
                        } else {
                            dni = row.getCell(1).getStringCellValue();
                        }
                        if (dni.contains(".")) {
                            dni = dni.split("\\.", 2)[0];
                        }
                        if (dni.contains(",")) {
                            dni = dni.split("\\,", 2)[0];
                        }

                    }
                    log.info("DNI: " + dni);

                    //NOMBRE
                    if (row.getCell(2) != null) {
                        nombre = row.getCell(2).getStringCellValue();
                        if (!nombre.equals("")) {
                            tar.setNombreCliente(nombre);
                        }
                    }

                    //APELLIDO
                    if (row.getCell(3) != null) {
                        apellido = row.getCell(3).getStringCellValue();
                        if (!apellido.equals("")) {
                            tar.setApellidoCliente(apellido);
                        }
                    }

                    //CODIGO EMPRESA
                    if (row.getCell(4) != null) {
                        codcia = row.getCell(4).getStringCellValue();
                        if (!codcia.equals("")) {
                            tar.setIdExtEmp(codcia);
                        }
                    }

                    //Valida que el archivo excel contenga tarjetas de un mismo bin
                    if (i == 0) {
                        if (!tarjetaString.equals("")) {
                            bin = tarjetaString.substring(0, 8);
                        }
                    } else {
                        if (!tarjetaString.equals("")) {
                            if (!bin.equals(tarjetaString.substring(0, 8))) {
                                message = "El archivo solo puede contener tarjetas de un mismo bin.";
                                procesoOk = false;
                                tipoMessage = "error";
                                tarjetasAct.clear();
                                break;
                            }
                        }
                    }

                    ajuste.setTarjeta(tarjetaString);
                    tar.setNroTarjeta(tarjetaString);
                    tar.setIdExtPer(dni);
                    if (!tarjetaString.equals("")) {
                        if (!tarjetaString.matches("\\d{16}")) {
                            message = "Formato incorrecto de la tarjeta";
                            tipoMessage = "error";
                            tarjetasAct.clear();
                            procesoOk = false;
                            break;
                        }
                    }
                    ajustes.add(ajuste);
                    tarjetasAct.add(tar);
                    ajuste = new Ajuste();
                    tar = new Tarjeta();
                    log.info("tarjeta [" + tarjetaString + "] ");
                    i++;
                } while (/*tarjeta!=0*/!tarjetaString.equals(""));
                if (i > 500) {
                    message = "El archivo excede el número de registros permitidos";
                    tarjetasAct.clear();
                    return SUCCESS;
                }
                //NO DEBE EXISTIR DOS O MAS REGISTROS CON LA MISMA IDENTIDAD
                if (!findDuplicates(tarjetasAct).isEmpty()) {
                    message = "El archivo no debe registrar dos o más tarjetas con la misma identidad";
                    tipoMessage = "error";
                    tarjetasAct.clear();
                    procesoOk = false;

                }

                if (procesoOk) {
                    message = "Carga de archivo exitosa. " + file.getName();
                    cantidadTarjetas = i;
                    String respuesta = business.checkTarjetas(ajustes);
                    if (respuesta.contains("errorT")) {
                        message = "No existe(n) la(s) siguiente(s) tarjeta(s): " + respuesta.substring(6, respuesta.length());
                        tipoMessage = "error";
                        tarjetasAct.clear();
                        return SUCCESS;
                    } else if (respuesta.contains("error")) {
                        message = "No se pudo realizar la afiliación";
                        tipoMessage = "error";
                        tarjetasAct.clear();
                        return SUCCESS;
                    }

                    ActionContext.getContext().getSession().put("tarjetasAct", tarjetasAct);
                }
            }
        } catch (Exception e) {
            log.error("error ", e);
            message = "No se pudo realizar la afiliación";
            tarjetasAct.clear();
            tipoMessage = "error";
        }
        return SUCCESS;
    }

    public static Set<String> findDuplicates(List<Tarjeta> listContainingDuplicates) {

        final Set<String> setToReturn = new HashSet<String>();
        final Set<String> set1 = new HashSet<String>();

        for (Tarjeta tarjetas : listContainingDuplicates) {
            if (!set1.add(tarjetas.getIdExtPer())) {
                setToReturn.add(tarjetas.getIdExtPer());
            }
        }
        return setToReturn;
    }

    public String afiliar() {

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
        String resp = business.makeAfiliacion(tarjetasAct, usuarioSesion.getIdUsuario());
        if (resp.contains("error2")) { //error al momento de insertar en tarjetahabiente ya existente
            message = "Registro del tarjetahabiente inválido, dni existente [" + resp.substring(6, resp.length()) + "]";
            tipoMessage = "error";
            selectedCampoValue = "";
            selectedCampo = "";
            tarjetasAct = new ArrayList<Tarjeta>();
            return SUCCESS;
        } else if (resp.contains("error3")) { //error al momento de actualizar en tarjetahabiente, ya existe
            message = "Actualización del tarjetahabiente inválido, dni repetido [" + resp.substring(6, resp.length()) + "]";
            tipoMessage = "error";
            selectedCampoValue = "";
            selectedCampo = "";
            tarjetasAct = new ArrayList<Tarjeta>();
            return SUCCESS;
        } else if (resp.contains("error4")) { //error al momento de validar el formato id empresa
            message = "El formato de la empresa del registro ha afiliar no es válida para la(s) tarjeta(s) [" + resp.substring(6, resp.length() - 1) + "]";
            tipoMessage = "error";
            selectedCampoValue = "";
            selectedCampo = "";
            tarjetasAct = new ArrayList<Tarjeta>();
            return SUCCESS;
        } else if (resp.contains("error5")) { //error al momento de validar existencia del id empresa
            message = "La(s) empresa(s) de los registros ha afiliar [" + resp.substring(6, resp.length()) + "] no existe(n)";
            tipoMessage = "error";
            selectedCampoValue = "";
            selectedCampo = "";
            tarjetasAct = new ArrayList<Tarjeta>();
            return SUCCESS;
        } else if (!resp.equals("0")) {
            message = "No se pudo cargar lote de afiliación en Base de Datos";
            tipoMessage = "error";
            selectedCampoValue = "";
            selectedCampo = "";
            tarjetasAct = new ArrayList<Tarjeta>();
            return SUCCESS;
        }
        selectedCampoValue = "";
        selectedCampo = "";
        tarjetasAct = new ArrayList<Tarjeta>();
        message = "El lote de afiliación ha sido cargado exitosamente.";
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

    public String getTipoMessage() {
        return tipoMessage;
    }

    public void setTipoMessage(String tipoMessage) {
        this.tipoMessage = tipoMessage;
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

    public int getCantidadTarjetas() {
        return cantidadTarjetas;
    }

    public void setCantidadTarjetas(int cantidadTarjetas) {
        this.cantidadTarjetas = cantidadTarjetas;
    }

}
