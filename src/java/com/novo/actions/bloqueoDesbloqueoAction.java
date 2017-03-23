/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.actions;

import com.novo.constants.BasicConfig;
import com.novo.dao.BloqueoDesbloqueoDAO;
import com.novo.model.Ajuste;
import com.novo.model.TAjuste;
import com.novo.model.TBloqueo;
import com.novo.model.Tarjeta;
import com.novo.model.UsuarioSesion;
import com.novo.process.ReporteTransacciones;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private UsuarioSesion usuarioSesion;
    private List<TAjuste> tipoAjustes;
    private List<Ajuste> ajustex;
    private List<TBloqueo> tipoBloqueo;
    String pais;
    private File file;
    private String contentType;
    private String filename;

    public bloqueoDesbloqueoAction() {
        this.tipoAjustes = new ArrayList();
        this.tarjetas = new ArrayList();
        this.tarjetasAct = new ArrayList();
    }

    public String execute() {
        ReporteTransacciones business = new ReporteTransacciones();
        this.tipoAjustes = business.getTipoAjustes();
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
        ReporteTransacciones business = new ReporteTransacciones();

        UsuarioSesion usuario = (UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion");
        this.pais = ((String) ActionContext.getContext().getSession().get("pais"));
        usuario.getIdUsuario();

        Tarjeta tar = new Tarjeta();
        List bloquex = new ArrayList();
        List ajustes = new ArrayList();
        TBloqueo ajuste = new TBloqueo();
        Ajuste ajusteB = new Ajuste();
        boolean procesoOk = true;
        BloqueoDesbloqueoDAO bloqueo = new BloqueoDesbloqueoDAO("operaciones", databases, this.pais);
        try {
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
                    procesoOk = false;
                    break;
                }
                bloquex.add(ajuste);
                ajustes.add(ajusteB);
                this.tarjetasAct.add(tar);
                ajuste = new TBloqueo();
                tar = new Tarjeta();
                log.info("tarjeta [" + tarjetaString + "] ");

                i++;
                //log.info("por favor" + sheet.getRow(i));
            } while ((!tarjetaString.equals("")) && (sheet.getRow(i) != null));

            if (i > 500) {
                this.message = "Numero de registros en el archivo excedido";
                return "success";
            }
            if (procesoOk) {
                this.message = ("Carga de Archivo Exitosa. " + this.file.getName());
                if (business.checkTarjetas(ajustes).compareToIgnoreCase("error") == 0) {
                    this.message = "Error, Tarjeta Inexistente.";
                    return "success";
                }

                bloqueo.setBloqueo(bloquex);
                bloqueo.ProcesarBloqueoDAO(usuario.getIdUsuario(), this.selectedBloqueo);
                this.tipoBloqueo = bloqueo.getBloqueo();

                ActionContext.getContext().getSession().put("tarjetasAct", this.tarjetasAct);
            }
        } catch (Exception e) {
            log.error("error ", e);
            this.message = "error";
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

}
