/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.actions;

import com.novo.constants.BasicConfig;
import static com.novo.constants.BasicConfig.USUARIO_SESION;
import com.novo.dao.RenovacionDAO;
import com.novo.dao.temp.RenovacionDAOINF;
import com.novo.model.Ajuste;
import com.novo.model.Empresa;
import com.novo.model.Producto;
import com.novo.model.Renovacion;
import com.novo.model.UsuarioSesion;
import com.novo.model.ValoresRen;
import com.novo.objects.util.Utils;
import com.novo.process.RenovacionProc;
import com.novo.process.ReporteTransacciones;
import com.novo.process.temp.RenovacionProcINF;
import com.novo.util.SessionUtil;
import com.novo.util.TextUtil;
import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FilenameUtils;
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
public class RenovacionAction extends ActionSupport implements BasicConfig {

    File file;
    private static Logger log = Logger.getLogger(RenovacionAction.class);
    String filename;
    private String message = "";
    private List<Producto> listaProductos;
    private List<Empresa> listaEmpresas;
    private List<Renovacion> renovar;
    private String selectedEmpresa = "";
    private String selectedProducto = "";
    private String pais;
    private String usuario;
    private String aplicacion;
    private String tipo_archivo;
    private List<String> listaUsuariosBusqueda;
    private Object nroTarjeta;
    private List<Renovacion> listReno;
    private String documentoIdentidad;
    private String fechaIni;
    private String fechaFin;
    private String tipoMessage = ""; //Error, manejo para el jsp
    private Properties prop;
    private String propMigra;
    private TextUtil txt = new TextUtil();
    private String contentType;
    private Workbook workbook;
    
    public RenovacionAction() {
        this.prop = Utils.getConfig("oracleRegEx.properties");
        this.propMigra = prop.getProperty("paisOracle");
        
    }

    public String carga() throws IOException {
        UsuarioSesion usuario = (UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion");
        this.pais = ((String) ActionContext.getContext().getSession().get("pais"));

        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        if (usuario == null) {
            return "out";
        }
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

        if (this.file == null) {
            this.message = "No se pudo cargar el archivo";
            tipoMessage = "error";
            return SUCCESS;
        }
        RenovacionDAO ren = null;
        RenovacionDAOINF renInf = null;
        RenovacionProc business = null;
        RenovacionProcINF businessInf = null;

        ValoresRen renoAction = new ValoresRen();

        if (txt.paisMigra(propMigra, this.pais)) {    
            ren = new RenovacionDAO("operaciones", dbOracle, this.pais);
            renoAction = ren.ValoresCarga();
            business = new RenovacionProc();
        } else {
            renInf = new RenovacionDAOINF("operaciones", databases, this.pais);
            renoAction = renInf.ValoresCarga();
            businessInf = new RenovacionProcINF();
        }
        
        //Validar que el archivo sea excel
        String ext = FilenameUtils.getExtension(filename);
        if (!ext.equals("xls") && !ext.equals("xlsx")) {
            message = "El archivo a cargar debe estar en formato Excel";
            tipoMessage = "error";
            return SUCCESS;
        }else{
        
            String rutaOrigen = renoAction.getRutaOrigen();
            String rutaDestino = renoAction.getRutaDestino();
            String nombreRenovacion = renoAction.getNombreRenovacion();
            String host = renoAction.getHost();
            String usuarioR = renoAction.getUsuario();

            File file2 = new File(rutaOrigen + "/" + nombreRenovacion);
            this.file.renameTo(file2);
            this.file.createNewFile();
            this.message = "Carga de archivo exitosa";
            log.info("Iniciando conexión");
            log.info(this.file.getAbsolutePath() + nombreRenovacion);
            log.info(rutaOrigen + "/" + nombreRenovacion);
            
            double tarjetaDouble = 0;
            boolean procesoOk = true;

            List<Ajuste> ajustes = new ArrayList<Ajuste>();
            Ajuste ajuste = new Ajuste();
        try {
//          InputStream buffer = new File(file2.getAbsolutePath());

                workbook = WorkbookFactory.create(new File(file2.getAbsolutePath()));
            Sheet sheet = workbook.getSheetAt(0);
            String tarjetaString = "";
            int i = 0;
            if (sheet.getRow(i) == null) {
                this.message = "El archivo se encuentra vacio";
                tipoMessage = "error";
                return SUCCESS;
            }

            do {
                Row row = sheet.getRow(i);

                if (row == null) {
                    break;
                }

                //TARJETA
                if (row.getCell(0).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    tarjetaDouble = row.getCell(0).getNumericCellValue();
                    tarjetaString = String.valueOf(tarjetaDouble);
                } else {
                    tarjetaString = row.getCell(0).getStringCellValue();
                }

                if (!tarjetaString.equals("")) {
                    if (!tarjetaString.matches("\\d{16}")) {
                        this.message = "Formato de la tarjeta inválido";
                        tipoMessage = "error";
                        procesoOk = false;
                        break;
                    }

                    ajuste.setTarjeta(tarjetaString);
                    ajustes.add(ajuste);
                    ajuste = new Ajuste();
                    log.info("Tarjeta [" + tarjetaString + "] ");
                }

                i++;
            } while (!tarjetaString.equals(""));

            if (procesoOk) {
                //VERIFICAR SI LAS TARJETAS SON APTAS PARA RENOVACION
                List<Renovacion> listaRenovacion;
                if (txt.paisMigra(propMigra, this.pais)) {    
                    listaRenovacion = business.checkTarjetasARenovar(ajustes);
                    ren.setRenovar(listaRenovacion);
                    listReno=ren.getRenovar();
                } else {
                    listaRenovacion = businessInf.checkTarjetasARenovar(ajustes);
                    renInf.setRenovar(listaRenovacion);
                    listReno=renInf.getRenovar();

                }
                String respuesta = listaRenovacion.get(listaRenovacion.size() - 1).getRespuesta();

                if (respuesta.contains("Tarjeta ya en Renovación")) {
                    message = "Tarjeta(s) inválida(s) para renovar:";
                    tipoMessage = "error";
                    return SUCCESS;
                } else if (respuesta.contains("error")) {
                    message = "No se pudo realizar la renovación";
                    tipoMessage = "error";
                    return SUCCESS;
                } else if (respuesta.contains("Tarjeta no apta")) {
                    message = "Esta(s) tarjeta(s) no se puede(n) ser renovada(s) porque no cumplen las condiciones";
                    tipoMessage = "error";
                    return SUCCESS;    
                } else if (respuesta.contains("Ok")) {
                    //SE PROCEDE A INSERTAR EN NOVO_RENOVACION
                    if (txt.paisMigra(propMigra, this.pais)) {    
                        respuesta = business.insertarRenovacion(listaRenovacion);
                    } else {
                        respuesta = businessInf.insertarRenovacion(listaRenovacion);
                        
                    }
                    if (respuesta.equals("Ok")) {

                        if (txt.paisMigra(propMigra, this.pais)) {    
                            business.addFile(rutaOrigen + "/" + nombreRenovacion, rutaDestino, host, usuarioR);
                        } else {
                            businessInf.addFile(rutaOrigen + "/" + nombreRenovacion, rutaDestino, host, usuarioR);

                        }
                        if (txt.paisMigra(propMigra, this.pais)) {    
                            ren.InsertarRenovacion(nombreRenovacion, rutaDestino, "", usuario.getIdUsuario(), "", "", "", "", null);
                        } else {
                            renInf.InsertarRenovacion(nombreRenovacion, rutaDestino, "", usuario.getIdUsuario(), "", "", "", "", null);
                        }
                    } else {
                        message = "No se pudo registrar la instrucción para renovación";
                        tipoMessage = "error";
                        return SUCCESS;
                    }
                }

            }
        } catch (Exception e) {
            log.error("error ", e);
            this.message = "No se pudo realizar la renovación";
            tipoMessage = "error";
            return SUCCESS;
        }
      }

        return SUCCESS;
    }

    public String consultar() {
        
        this.pais = ((String) ActionContext.getContext().getSession().get("pais"));
        //Valido sesion
        UsuarioSesion usuario = (UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion");
        SessionUtil sessionUtil = new SessionUtil();
        if (usuario == null) {
            return "out";
        }
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
        RenovacionDAO ren = null;
        RenovacionDAOINF renInf = null;
        if (txt.paisMigra(propMigra, this.pais)) {    
            ren = new RenovacionDAO("operaciones", dbOracle, this.pais);
            this.renovar = ren.QueryRenovacion(this.selectedEmpresa, this.selectedProducto, this.documentoIdentidad, this.fechaIni, this.fechaFin);
        } else {
            renInf = new RenovacionDAOINF("operaciones", databases, this.pais);
            this.renovar = renInf.QueryRenovacion(this.selectedEmpresa, this.selectedProducto, this.documentoIdentidad, this.fechaIni, this.fechaFin);
        }

        this.listaProductos = new ArrayList();
        this.listaEmpresas = new ArrayList();

        listar();

        return "consultar";
    }

    public String listar() {
        ReporteTransacciones business = new ReporteTransacciones();
        this.listaUsuariosBusqueda = business.getUsuarios();

        this.listaProductos = business.getProductos();
        this.listaEmpresas = business.getEmpresas();
        return "listar";
    }

    public String buscar() {
        this.pais = ((String) ActionContext.getContext().getSession().get("pais"));

        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        UsuarioSesion usuario = (UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion");
        if (usuario == null) {
            return "out";
        }
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
        RenovacionDAO ren = null;
        RenovacionDAOINF renInf = null;
        if (txt.paisMigra(propMigra, this.pais)) {    
            ren = new RenovacionDAO("operaciones", dbOracle, this.pais);
            this.renovar = ren.QueryRenovacion(this.selectedEmpresa, this.selectedProducto, this.documentoIdentidad, this.fechaIni, this.fechaFin);
        } else {
            renInf = new RenovacionDAOINF("operaciones", databases, this.pais);
            this.renovar = renInf.QueryRenovacion(this.selectedEmpresa, this.selectedProducto, this.documentoIdentidad, this.fechaIni, this.fechaFin);
        }
        listar();

        return "buscar";
    }

    public String execute() {

        SessionUtil sessionUtil = new SessionUtil();
        UsuarioSesion usuario = (UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion");
        if (usuario == null) {
            return "out";
        }
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

        return "success";
    }

    public File getFile() {
        return this.file;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<Producto> getListaProductos() {
        return this.listaProductos;
    }

    public void setListaProductos(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public List<Empresa> getListaEmpresas() {
        return this.listaEmpresas;
    }

    public void setListaEmpresas(List<Empresa> listaEmpresas) {
        this.listaEmpresas = listaEmpresas;
    }

    public String getSelectedEmpresa() {
        return this.selectedEmpresa;
    }

    public void setSelectedEmpresa(String selectedEmpresa) {
        this.selectedEmpresa = selectedEmpresa;
    }

    public String getSelectedProducto() {
        return this.selectedProducto;
    }

    public void setSelectedProducto(String selectedProducto) {
        this.selectedProducto = selectedProducto;
    }

    public List<String> getListaUsuariosBusqueda() {
        return this.listaUsuariosBusqueda;
    }

    public void setListaUsuariosBusqueda(List<String> listaUsuariosBusqueda) {
        this.listaUsuariosBusqueda = listaUsuariosBusqueda;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Renovacion> getRenovar() {
        return this.renovar;
    }

    public void setRenovar(List<Renovacion> renovar) {
        this.renovar = renovar;
    }

    public String getDocumentoIdentidad() {
        return this.documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public String getFechaIni() {
        return this.fechaIni;
    }

    public void setFechaIni(String fechaIni) {
        this.fechaIni = fechaIni;
    }

    public String getFechaFin() {
        return this.fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getTipoMessage() {
        return tipoMessage;
    }

    public void setTipoMessage(String tipoMessage) {
        this.tipoMessage = tipoMessage;
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

    public List<Renovacion> getListReno() {
        return listReno;
    }

    public void setListReno(List<Renovacion> listReno) {
        this.listReno = listReno;
    }
    
}
