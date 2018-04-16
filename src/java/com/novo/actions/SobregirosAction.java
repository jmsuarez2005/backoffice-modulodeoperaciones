/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.actions;

import com.novo.constants.BasicConfig;
import static com.novo.constants.BasicConfig.USUARIO_SESION;
import com.novo.dao.SobregirosDAO;
import com.novo.dao.temp.SobregirosDAOINF;
import com.novo.model.Ajuste;
import com.novo.model.CamposActualizacion;
import com.novo.model.TAjuste;
import com.novo.model.Tarjeta;
import com.novo.model.UsuarioSesion;
import com.novo.objects.util.Utils;
import com.novo.process.ReporteTransacciones;
import com.novo.util.SessionUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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
public class SobregirosAction extends ActionSupport implements BasicConfig {

    private static Logger log = Logger.getLogger(SobregirosAction.class);
    private String message = "";

    private String nroTarjeta;
    private List<Tarjeta> tarjetas;
    private List<Tarjeta> tarjetasAct;
    private String selectedAjuste = "";
    private String selectedCampoValue = "";
    private UsuarioSesion usuarioSesion;
    private List<TAjuste> tipoAjustes;
    private List<Ajuste> ajustex;

    private File file;
    private String contentType;
    private String filename;
    private String pais;
    private String tipoMessage = ""; //Error, manejo para el jsp
    private Properties prop;
    private String propMigra;

    public SobregirosAction() {
        tipoAjustes = new ArrayList<TAjuste>();
        tarjetas = new ArrayList<Tarjeta>();
        tarjetasAct = new ArrayList<Tarjeta>();
        usuarioSesion = (UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion");
        this.prop = Utils.getConfig("oracleRegEx.properties");
        this.propMigra = prop.getProperty("paisOracle");
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
                log.info("Sesion expira del usuario " + ((UsuarioSesion) ActionContext.getContext().getSession().get(USUARIO_SESION)).getIdUsuario());
                ActionContext.getContext().getSession().clear();
            } catch (Exception e) {
                log.info("Es posible que la sesión para este usuario ya haya sido cerrada previamente a la llamada del LogoutAction.");
            }

            return "out";
        }
        //Fin valida sesion

        ReporteTransacciones business = new ReporteTransacciones();
        tipoAjustes = business.getTipoAjustes();
        return SUCCESS;
    }

    public List<Ajuste> getAjustex() {
        return ajustex;
    }

    public void setAjustex(List<Ajuste> ajustex) {
        this.ajustex = ajustex;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
                log.info("Sesion expira del usuario " + ((UsuarioSesion) ActionContext.getContext().getSession().get(USUARIO_SESION)).getIdUsuario());
                ActionContext.getContext().getSession().clear();
            } catch (Exception e) {
                log.info("Es posible que la sesión para este usuario ya haya sido cerrada previamente a la llamada del LogoutAction.");
            }

            return "out";
        }
        //Fin valida sesion

        ReporteTransacciones business = new ReporteTransacciones();
        if (nroTarjeta != null && !nroTarjeta.equals("")) {
            log.info("tarjeta = " + nroTarjeta);
            tarjetas = business.getTarjetasUsuario(null, nroTarjeta);
        }
        tarjetasAct = tarjetas;
        ActionContext.getContext().getSession().put("tarjetasAct", tarjetasAct);
        //campos = business.getCampos();
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
                log.info("Sesion expira del usuario " + ((UsuarioSesion) ActionContext.getContext().getSession().get(USUARIO_SESION)).getIdUsuario());
                ActionContext.getContext().getSession().clear();
            } catch (Exception e) {
                log.info("Es posible que la sesión para este usuario ya haya sido cerrada previamente a la llamada del LogoutAction.");
            }

            return "out";
        }
        //Fin valida sesion

        ReporteTransacciones business = new ReporteTransacciones();

        UsuarioSesion usuario = (UsuarioSesion) ActionContext.getContext().getSession().get(USUARIO_SESION);
        pais = ((String) ActionContext.getContext().getSession().get("pais"));
        usuario.getIdUsuario();
        //campos = business.getCampos();
        Tarjeta tar = new Tarjeta();
        List<Ajuste> ajustes = new ArrayList<Ajuste>();
        Ajuste ajuste = new Ajuste();
        boolean procesoOk = true;
        SobregirosDAO sobregiros = null;
        SobregirosDAOINF sobregirosInf = null;
        if (propMigra.toUpperCase().contains(pais.toUpperCase())) {
            sobregiros = new SobregirosDAO(appName, dbOracle, pais);
        } else {
            sobregirosInf = new SobregirosDAOINF(appName, databases, pais);
        }
        try {

            if (this.file == null) {
                this.message = "Error al cargar el archivo";
                tipoMessage = "error";
                tipoAjustes = business.getTipoAjustes();
                return SUCCESS;
            }
            log.info(file.getAbsolutePath() + " " + file.getCanonicalPath());
            File file2;
            file2 = new File(file.getPath() + filename);
            file.renameTo(file2);
            InputStream buffer = new FileInputStream(file2.getAbsolutePath());
            Workbook workbook = WorkbookFactory.create(buffer);
            Sheet sheet = workbook.getSheetAt(0);
            String tarjetaString = "";
            String montoString = "";
            double tarjetaDouble = 0;
            double montoDouble = 0;
            int i = 0;
            do {
                Row row = sheet.getRow(i);

                if (row == null) {
                    break;
                } else if (row.getCell(0) == null) {
                    break;
                }

                if (row.getCell(0).getCellType() == 3) {
                    break;
                }
                if (row.getCell(0).getCellType() == 0) {
                    tarjetaDouble = row.getCell(0).getNumericCellValue();
                    tarjetaString = String.valueOf(tarjetaDouble);
                } else {
                    tarjetaString = row.getCell(0).getStringCellValue();
                }
                if (row.getCell(1).getCellType() == 0) {
                    montoDouble = row.getCell(1).getNumericCellValue();
                    montoString = String.valueOf(montoDouble);
                } else {
                    montoString = row.getCell(1).getStringCellValue();
                }

                ajuste.setTarjeta(tarjetaString);
                ajuste.setMonto(montoString);

                tar.setNroTarjeta(tarjetaString);
                if (!tarjetaString.matches("\\d{16}")) {
                    message = "Error con el formato de la tarjeta";
                    tipoMessage = "error";
                    procesoOk = false;
                    break;
                }
                ajustes.add(ajuste);
                tarjetasAct.add(tar);
                ajuste = new Ajuste();
                tar = new Tarjeta();
                log.info("tarjeta [" + tarjetaString + "] ");

                i++;
                log.info("por favor" + sheet.getRow(i));
            } while (!tarjetaString.equals("") && sheet.getRow(i) != null);

            if (i > 500) {
                message = "Numero de registros en el archivo excedido";
                tipoMessage = "error";
                tipoAjustes = business.getTipoAjustes();
                return SUCCESS;
            }
            if (procesoOk) {

                message = "Carga de Archivo Exitosa. " + file.getName();

                String respuesta = business.checkTarjetas(ajustes);
                if (respuesta.contains("errorT")) {
                    message = "Error, Tarjeta(s) Inexistente(s):" + respuesta.substring(6, respuesta.length());
                    tipoMessage = "error";
                    tipoAjustes = business.getTipoAjustes();
                    return SUCCESS;
                } else if (respuesta.contains("error")) {
                    message = "[!] Error de sistema";
                    tipoMessage = "error";
                    tipoAjustes = business.getTipoAjustes();
                    return SUCCESS;
                }

                if (propMigra.toUpperCase().contains(pais.toUpperCase())) {
                    sobregiros.setAjustes(ajustes);
                    sobregiros.ProcesarSobregirosDAO(usuario.getIdUsuario(), selectedAjuste);
                } else {
                    sobregirosInf.setAjustes(ajustes);
                    sobregirosInf.ProcesarSobregirosDAO(usuario.getIdUsuario(), selectedAjuste);

                }
                ajustex = ajustes;

                tipoAjustes = business.getTipoAjustes();
                ActionContext.getContext().getSession().put("tarjetasAct", tarjetasAct);
            }
        } catch (Exception e) {
            log.error("error ", e);
            tipoMessage = "error";
            message = "[!] Error de sistema";
        }
        return SUCCESS;
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

    public List<Tarjeta> getTarjetasAct() {
        return tarjetasAct;
    }

    public void setTarjetasAct(List<Tarjeta> tarjetasAct) {
        this.tarjetasAct = tarjetasAct;
    }

    public String getSelectedAjuste() {
        return selectedAjuste;
    }

    public void setSelectedAjuste(String selectedAjuste) {
        this.selectedAjuste = selectedAjuste;
    }

    public String getSelectedCampoValue() {
        return selectedCampoValue;
    }

    public void setSelectedCampoValue(String selectedCampoValue) {
        this.selectedCampoValue = selectedCampoValue;
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

    public List<TAjuste> getTipoAjustes() {
        return tipoAjustes;
    }

    public void setTipoAjustes(List<TAjuste> tipoAjustes) {
        this.tipoAjustes = tipoAjustes;
    }

    public String getTipoMessage() {
        return tipoMessage;
    }

    public void setTipoMessage(String tipoMessage) {
        this.tipoMessage = tipoMessage;
    }
}
