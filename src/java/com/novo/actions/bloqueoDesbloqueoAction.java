/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.actions;

import com.novo.constants.BasicConfig;
import static com.novo.constants.BasicConfig.USUARIO_SESION;
import com.novo.dao.BloqueoDesbloqueoDAO;
import com.novo.dao.temp.BloqueoDesbloqueoDAOINF;
import com.novo.model.Ajuste;
import com.novo.model.TAjuste;
import com.novo.model.TBloqueo;
import com.novo.model.Tarjeta;
import com.novo.model.UsuarioSesion;
import com.novo.objects.util.Utils;
import com.novo.process.ReporteTransacciones;
import com.novo.util.SessionUtil;
import com.novo.util.TextUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author rgomez
 */
public class bloqueoDesbloqueoAction extends ActionSupport implements BasicConfig {

    private static Logger log = Logger.getLogger(bloqueoDesbloqueoAction.class);
    private String message = "";
    private String nroTarjeta;
    private List<Tarjeta> tarjetas;
    private List<Tarjeta> tarjetasAct;
    private String selectedAjuste = "";
    private String selectedCampoValue = "";
    private String selectedBloqueo;
    private List<TAjuste> tipoAjustes;
    private List<Ajuste> ajustex;
    private List<TBloqueo> tipoBloqueo;
    String pais;
    private File file;
    private String contentType;
    private String filename;
    private String tipoMessage = ""; //Error, manejo para el jsp
    private Properties prop;
    private String propMigra;
    private TextUtil txt = new TextUtil();

    public bloqueoDesbloqueoAction() {
        //this.tipoAjustes = new ArrayList();
        this.tarjetas = new ArrayList();
        this.tarjetasAct = new ArrayList();
        this.prop = Utils.getConfig("oracleRegEx.properties");
        this.propMigra = prop.getProperty("paisOracle");
    }

    public String execute() {

        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        UsuarioSesion usuarioSesion = (UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion");
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
        //this.tipoAjustes = business.getTipoAjustes();
        this.tipoBloqueo = business.getTipoBloqueo();
        return "success";
    }

    public List<Ajuste> getAjustex() {
        return this.ajustex;
    }

    public void setAjustex(List<Ajuste> ajustex) {
        this.ajustex = ajustex;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String buscarUsuario() {

        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        UsuarioSesion usuarioSesion = (UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion");
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
        if ((this.nroTarjeta != null) && (!this.nroTarjeta.equals(""))) {
            log.info("tarjeta = " + this.nroTarjeta);
            this.tarjetas = business.getTarjetasUsuario(null, this.nroTarjeta);
        }
        this.tarjetasAct = this.tarjetas;
        ActionContext.getContext().getSession().put("tarjetasAct", this.tarjetasAct);

        return "success";
    }

    public String Upload() {

        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        UsuarioSesion usuarioSesion = (UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion");
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

        UsuarioSesion usuario = (UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion");
        this.pais = ((String) ActionContext.getContext().getSession().get("pais"));
        usuario.getIdUsuario();

        Tarjeta tar = new Tarjeta();
        List bloquex = new ArrayList();
        List<Ajuste> ajustes = new ArrayList<Ajuste>();
        TBloqueo ajuste = new TBloqueo();
        Ajuste ajusteB = new Ajuste();
        boolean procesoOk = true;
        BloqueoDesbloqueoDAO bloqueo = null;
        BloqueoDesbloqueoDAOINF bloqueoInf = null;
        if (txt.paisMigra(propMigra, this.pais)) {   
            bloqueo = new BloqueoDesbloqueoDAO("operaciones", dbOracle, this.pais);
        } else {
            bloqueoInf = new BloqueoDesbloqueoDAOINF("operaciones", databases, this.pais);
        }
        try {
            if (this.file == null) {
                this.message = "Error al cargar el archivo";
                tipoMessage = "error";
                this.tipoBloqueo = business.getTipoBloqueo();
                return "success";
            }
            log.info(this.file.getAbsolutePath() + " " + this.file.getCanonicalPath());

            File file2 = new File(this.file.getPath() + this.filename);
            this.file.renameTo(file2);
            InputStream buffer = new FileInputStream(file2.getAbsolutePath());
            Workbook workbook = WorkbookFactory.create(buffer);
            workbook.write(new FileOutputStream(this.file));
            Sheet sheet = workbook.getSheetAt(0);
            String tarjetaString = "";
            String montoString = "";
            double tarjetaDouble = 0.0D;
            double montoDouble = 0.0D;
            int i = 0;
            do {
                Row row = sheet.getRow(i);

                if (row.getCell(0).getCellType() == 3) {
                    break;
                }
                if (row.getCell(0).getCellType() == 0) {
                    tarjetaDouble = row.getCell(0).getNumericCellValue();
                    tarjetaString = String.valueOf(tarjetaDouble);
                } else {
                    tarjetaString = row.getCell(0).getStringCellValue();
                }

                ajuste.setNro_tarjeta(tarjetaString);
                ajusteB.setTarjeta(tarjetaString);

                tar.setNroTarjeta(tarjetaString);
                if (!tarjetaString.matches("\\d{16}")) {
                    this.message = "Error con el formato de la tarjeta";
                    tipoMessage = "error";
                    procesoOk = false;
                    break;
                }
                bloquex.add(ajuste);
                ajustes.add(ajusteB);
                this.tarjetasAct.add(tar);
                ajusteB = new Ajuste();
                ajuste = new TBloqueo();
                tar = new Tarjeta();
                log.info("tarjeta [" + tarjetaString + "] ");

                i++;
                //log.info("por favor" + sheet.getRow(i));
            } while ((!tarjetaString.equals("")) && (sheet.getRow(i) != null));

            if (i > 500) {
                this.message = "Numero de registros en el archivo excedido";
                tipoMessage = "error";
                return "success";
            }
            if (procesoOk) {
                this.message = ("Carga de Archivo Exitosa. " + this.file.getName());
                String respuesta = business.checkTarjetas(ajustes);
                if (respuesta.contains("errorT")) {
                    message = "Error, Tarjeta(s) Inexistente(s):" + respuesta.substring(6, respuesta.length());
                    tipoMessage = "error";
                    this.tipoBloqueo = business.getTipoBloqueo();
                    return "success";
                } else if (respuesta.contains("error")) {
                    message = "[!] Error de sistema";
                    tipoMessage = "error";
                    this.tipoBloqueo = business.getTipoBloqueo();
                    return "success";
                }

                if (txt.paisMigra(propMigra, this.pais)) {   
                    bloqueo.setBloqueo(bloquex);
                    bloqueo.ProcesarBloqueoDAO(usuario.getIdUsuario(), this.selectedBloqueo);
                    this.tipoBloqueo = bloqueo.getBloqueo();
                } else {
                    bloqueoInf.setBloqueo(bloquex);
                    bloqueoInf.ProcesarBloqueoDAO(usuario.getIdUsuario(), this.selectedBloqueo);
                    this.tipoBloqueo = bloqueoInf.getBloqueo();
                }
                this.tipoBloqueo = business.getTipoBloqueo();

                ActionContext.getContext().getSession().put("tarjetasAct", this.tarjetasAct);
            }
        } catch (Exception e) {
            log.error("error ", e);
            tipoMessage = "error";
            this.message = "[!] Error de sistema";
        }
        return "success";
    }

    public String getNroTarjeta() {
        return this.nroTarjeta;
    }

    public void setNroTarjeta(String nroTarjeta) {
        this.nroTarjeta = nroTarjeta;
    }

    public List<Tarjeta> getTarjetas() {
        return this.tarjetas;
    }

    public void setTarjetas(List<Tarjeta> tarjetas) {
        this.tarjetas = tarjetas;
    }

    public List<Tarjeta> getTarjetasAct() {
        return this.tarjetasAct;
    }

    public void setTarjetasAct(List<Tarjeta> tarjetasAct) {
        this.tarjetasAct = tarjetasAct;
    }

    public String getSelectedAjuste() {
        return this.selectedAjuste;
    }

    public void setSelectedAjuste(String selectedAjuste) {
        this.selectedAjuste = selectedAjuste;
    }

    public String getSelectedCampoValue() {
        return this.selectedCampoValue;
    }

    public void setSelectedCampoValue(String selectedCampoValue) {
        this.selectedCampoValue = selectedCampoValue;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<TAjuste> getTipoAjustes() {
        return this.tipoAjustes;
    }

    public void setTipoAjustes(List<TAjuste> tipoAjustes) {
        this.tipoAjustes = tipoAjustes;
    }

    public String getSelectedBloqueo() {
        return this.selectedBloqueo;
    }

    public void setSelectedBloqueo(String selectedBloqueo) {
        this.selectedBloqueo = selectedBloqueo;
    }

    public List<TBloqueo> getTipoBloqueo() {
        return this.tipoBloqueo;
    }

    public void setTipoBloqueo(List<TBloqueo> tipoBloqueo) {
        this.tipoBloqueo = tipoBloqueo;
    }

    public String getTipoMessage() {
        return tipoMessage;
    }

    public void setTipoMessage(String tipoMessage) {
        this.tipoMessage = tipoMessage;
    }
}
