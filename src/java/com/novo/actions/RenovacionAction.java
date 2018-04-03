/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.actions;

import static com.novo.actions.AfiliacionAction.findDuplicates;
import com.novo.constants.BasicConfig;
import static com.novo.constants.BasicConfig.USUARIO_SESION;
import com.novo.dao.RenovacionDAO;
import com.novo.model.Ajuste;
import com.novo.model.Empresa;
import com.novo.model.Producto;
import com.novo.model.Renovacion;
import com.novo.model.Tarjeta;
import com.novo.model.UsuarioSesion;
import com.novo.model.ValoresRen;
import com.novo.objects.util.Utils;
import com.novo.process.RenovacionProc;
import com.novo.process.ReporteTransacciones;
import com.novo.util.SessionUtil;
import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private List<Tarjeta> tarjetas;
    private String documentoIdentidad;
    private String fechaIni;
    private String fechaFin;
    private String tipoMessage = ""; //Error, manejo para el jsp

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
                log.info("Sesion expira del usuario " + ((UsuarioSesion) ActionContext.getContext().getSession().get(USUARIO_SESION)).getIdUsuario());
                ActionContext.getContext().getSession().clear();
            } catch (Exception e) {
                log.info("Es posible que la sesión para este usuario ya haya sido cerrada previamente a la llamada del LogoutAction.");
            }

            return "out";
        }
        //Fin valida sesion

        if (this.file == null) {
            this.message = "Error al cargar el archivo";
            tipoMessage = "error";
            return SUCCESS;
        }

        RenovacionDAO ren = new RenovacionDAO("operaciones", dbOracle, this.pais);

        ValoresRen renoAction = new ValoresRen();

        renoAction = ren.ValoresCarga();

        String rutaOrigen = renoAction.getRutaOrigen();
        String rutaDestino = renoAction.getRutaDestino();
        String nombreRenovacion = renoAction.getNombreRenovacion();
        String host = renoAction.getHost();
        String usuarioR = renoAction.getUsuario();

        File file2 = new File(rutaOrigen + "/" + nombreRenovacion);
        this.file.renameTo(file2);
        this.file.createNewFile();
        this.message = "Carga de archivo exitosa";
        log.info("iniciando conexion");
        log.info(this.file.getAbsolutePath() + nombreRenovacion);
        double tarjetaDouble = 0;
        boolean procesoOk = true;

        List<Ajuste> ajustes = new ArrayList<Ajuste>();
        Ajuste ajuste = new Ajuste();
        RenovacionProc business = new RenovacionProc();

        try {
            InputStream buffer = new FileInputStream(file2.getAbsolutePath());
            Workbook workbook = WorkbookFactory.create(buffer);
            Sheet sheet = workbook.getSheetAt(0);
            String tarjetaString = "";
            int i = 0;

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
                        this.message = "Error con el formato de la tarjeta";
                        tipoMessage = "error";
                        procesoOk = false;
                        break;
                    }

                    ajuste.setTarjeta(tarjetaString);
                    ajustes.add(ajuste);
                    ajuste = new Ajuste();
                    log.info("tarjeta [" + tarjetaString + "] ");
                }

                i++;
            } while (!tarjetaString.equals(""));

            if (procesoOk) {
                //VERIFICAR SI LAS TARJETAS SON APTAS PARA RENOVACION
                List<Renovacion> listaRenovacion = business.checkTarjetasARenovar(ajustes);
                String respuesta = listaRenovacion.get(listaRenovacion.size() - 1).getRespuesta();

                if (respuesta.contains("errorT")) {
                    message = "Error, Tarjeta(s) no son validos para renovar: [" + respuesta.substring(6, respuesta.length()) + "]";
                    tipoMessage = "error";
                    return SUCCESS;
                } else if (respuesta.contains("error")) {
                    message = "[!] Error de sistema";
                    tipoMessage = "error";
                    return SUCCESS;
                } else if (respuesta.contains("ok")) {
                    //SE PROCEDE A INSERTAR EN NOVO_RENOVACION
                    respuesta = business.insertarRenovacion(listaRenovacion);
                    if (respuesta.equals("ok")) {
                        business.addFile(rutaOrigen + "/" + nombreRenovacion, rutaDestino, host, usuarioR);
                        ren.InsertarRenovacion(nombreRenovacion, rutaDestino, "", usuario.getIdUsuario(), "", "", "", "", null);
                    } else {
                        message = "[!] Error al registrar la instruccion para renovacion";
                        tipoMessage = "error";
                        return SUCCESS;
                    }
                }

            }

        } catch (Exception e) {
            log.error("error ", e);
            this.message = "[!] Error de sistema";
            tipoMessage = "error";
            return "success";
        }

        return "success";
    }

    public String consultar() {
        //Valido sesion
        UsuarioSesion usuario = (UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion");
        SessionUtil sessionUtil = new SessionUtil();
        if (usuario == null) {
            return "out";
        }
        String sessionDate = usuario.getSessionDate();
        if (!sessionUtil.validateSession(sessionDate, usuario)) {
            try {
                log.info("Sesion expira del usuario " + ((UsuarioSesion) ActionContext.getContext().getSession().get(USUARIO_SESION)).getIdUsuario());
                ActionContext.getContext().getSession().clear();
            } catch (Exception e) {
                log.info("Es posible que la sesión para este usuario ya haya sido cerrada previamente a la llamada del LogoutAction.");
            }

            return "out";
        }
        //Fin valida sesion

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
                log.info("Sesion expira del usuario " + ((UsuarioSesion) ActionContext.getContext().getSession().get(USUARIO_SESION)).getIdUsuario());
                ActionContext.getContext().getSession().clear();
            } catch (Exception e) {
                log.info("Es posible que la sesión para este usuario ya haya sido cerrada previamente a la llamada del LogoutAction.");
            }

            return "out";
        }
        //Fin valida sesion

        RenovacionDAO ren = new RenovacionDAO("operaciones", dbOracle, this.pais);
        this.renovar = ren.QueryRenovacion(this.selectedEmpresa, this.selectedProducto, this.documentoIdentidad, this.fechaIni, this.fechaFin);
        listar();

        return "consultar";
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
                log.info("Sesion expira del usuario " + ((UsuarioSesion) ActionContext.getContext().getSession().get(USUARIO_SESION)).getIdUsuario());
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

}
