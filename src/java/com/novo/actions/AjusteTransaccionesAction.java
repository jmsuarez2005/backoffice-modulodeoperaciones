package com.novo.actions;

import com.novo.constants.BasicConfig;
import static com.novo.constants.BasicConfig.USUARIO_SESION;
import com.novo.model.Ajuste;
import com.novo.model.Empresa;
import com.novo.model.Producto;
import com.novo.model.TAjuste;
import com.novo.model.Tarjeta;
import com.novo.model.Transaccion;
import com.novo.model.UsuarioSesion;
import com.novo.process.AjusteTransaccionesProc;
import com.novo.process.ReporteTransacciones;
import com.novo.util.DateUtil;
import com.novo.util.SessionUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class AjusteTransaccionesAction extends ActionSupport
        implements BasicConfig {

    private static Logger log = Logger.getLogger(AjusteTransaccionesAction.class);
    private String message = "";
    private File file;
    private String contentType;
    private String filename;
    private String observacion;
    private String nroTarjeta;
    private String tarjetaSelected;
    private String systrace;
    private String documentoIdentidad;
    private String montoAjuste;
    private String opcion = "-1";
    private List<Transaccion> transacciones;
    private List<Tarjeta> tarjetas;
    private List<TAjuste> tipoAjustes;
    private List<Ajuste> ajustes;
    private UsuarioSesion usuarioSesion;
    private String selectedAjuste;
    private List<String> status;
    private List<String> status2;
    private List<String> filtro;
    private List<Producto> listaProductos;
    private List<Empresa> listaEmpresas;
    private String selectedStatus;
    private String selectedStatus2;
    private String selectedUsuario2;
    private String selectedFiltro;
    private String selectedEmpresa = "";
    private String selectedProducto = "";
    private List<String> listaUsuariosBusqueda;
    private String selectedUsuario;
    private String idDetalleAjuste;
    private String checkMe;
    private Date fechaIni;
    private Date fechaFin;
    private Date fechaIni2;
    private Date fechaFin2;
    private boolean editar = false;
    private String filaEditar;
    private String montoEditar;
    private String tipoAjusteEditar;
    private String selectedtipoAjuste;
    private String AjusteDesc;
    private String msg = "";
    private String tipoMessage = ""; //Error, manejo para el jsp
    String pais;

    private Map<String, Object> session;
    private Date fecha; //fecha del reporte
    private InputStream inputStream;

    private String reportFile;

    public AjusteTransaccionesAction() {
        this.listaUsuariosBusqueda = new ArrayList();
        this.transacciones = new ArrayList();
        this.tipoAjustes = new ArrayList();
        this.ajustes = new ArrayList();
        this.usuarioSesion = ((UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion"));
        this.status = new ArrayList();
        this.status2 = new ArrayList();
        this.filtro = new ArrayList();
        this.listaProductos = new ArrayList();
        this.listaEmpresas = new ArrayList();
        this.status.add("TODOS");
        this.status.add("PENDIENTE");
        this.status.add("EN PROCESO");
        this.status.add("AUTORIZADO");
        this.status.add("ANULADO");
        this.status.add("PROCESADO");
        this.status2.add("AUTORIZADO");
        this.status2.add("AUTORIZAR TODOS");
        this.status2.add("ANULADO");
        this.filtro.add("Tarjeta");
        this.filtro.add("Monto");
        this.filtro.add("DNI");
        this.filtro.add("Nombre");
        this.filtro.add("Usuario");
        this.filtro.add("Tipo Ajuste");
        this.filtro.add("Estatus");
        this.filtro.add("Observacion");
    }

    public String execute() {
        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        this.usuarioSesion = ((UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion"));
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
        this.tipoAjustes = business.getTipoAjustes();
        if (this.opcion.equals("-1")) {
            this.listaProductos = business.getProductos();
            //this.listaEmpresas = business.getEmpresas();
        }
        String result = "";
        System.out.println("opcion" + this.opcion);

        if (this.opcion.equals("registrarAjuste")) {
            log.info("registrarajuste tarjeta [" + this.nroTarjeta + "] referencia [" + this.systrace + "] montoajuste [" + this.montoAjuste + "] codigoajuste[" + this.selectedAjuste + "]");
            result = business.RegistrarAjuste(this.nroTarjeta, this.montoAjuste, this.selectedAjuste, this.systrace, this.usuarioSesion.getIdUsuario(), this.observacion, false);
            if (result.equals("ok")) {
                this.message = "Registro de Ajuste realizado satisfactoriamente.";
            } else {
                this.message = "Su Ajuste no pudo ser registrado.";
            }
            if (this.systrace == null) {
                return "ajustetarjeta";
            }

            return "success";
        }
        if (this.opcion.equals("buscarAjustes")) {
            log.info("status selected [" + this.selectedStatus + "] ---- usuario selected [" + this.selectedUsuario + "]  --- fecha inicio [" + this.fechaIni + "] ---- fecha final[" + this.fechaFin + "] ");

            this.listaUsuariosBusqueda = business.getUsuarios();
            this.ajustes = business.getAjustes(getStatusfromSelected(this.selectedStatus), this.selectedUsuario, this.fechaIni, this.fechaFin);
            return "listar";
        }
        if (this.opcion.equals("actualizarAjustes")) {
            this.listaUsuariosBusqueda = business.getUsuarios();
            String[] idAjustes;
            try {
                idAjustes = this.selectedAjuste.split(",");
            } catch (Exception e) {
                this.message = "El Ajuste no se pudo actualizar en este momento.";
                return "listar";
            }
            String idstatus = getStatusfromSelected(this.selectedStatus);
            result = business.updateAjuste(idAjustes, idstatus);

            if (result.equals("ok")) {
                this.message = " Ajuste actualizado satisfactoriamente.";
            } else {
                this.message = "El Ajuste no se pudo actualizar en este momento.";
            }
            return "listar";
        }

        if (this.opcion.equals("buscarTipoAjustes")) {

            this.listaUsuariosBusqueda = business.getUsuarios();
            this.selectedStatus2 = this.selectedStatus2.substring(7, this.selectedStatus2.length());
            this.ajustes = business.getAjustes(getStatusfromSelected(this.selectedStatus2), this.selectedUsuario2, this.fechaIni2, this.fechaFin2);
            log.info("seledUsuario:" + this.selectedUsuario);
            buscarTipoAjustes();
            return "listar";
        }

        return "success";
    }

    public String actualizarAjustes() {
        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        this.usuarioSesion = ((UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion"));
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

        if (this.opcion.equals("HEditar")) {
            log.info("************** Edicion de ajuste ***************");
            actualizarEdicion();
        } else {
            String result = "";
            ReporteTransacciones business = new ReporteTransacciones();

            this.listaUsuariosBusqueda = business.getUsuarios();
            String[] idAjustes;
            try {
                if (this.selectedAjuste == null) {
                    this.message = "Error, seleccione un status a cambiar.";
                    tipoMessage = "error";
                    return "listar";
                }

                idAjustes = this.selectedAjuste.split(",");
            } catch (Exception e) {
                return "listar";
            }
            String idstatus = getStatusfromSelected(this.selectedStatus);
            result = business.updateAjuste(idAjustes, idstatus);

            if (result.equals("ok")) {
                this.message = " Ajuste actualizado satisfactoriamente.";
            } else {
                this.message = "El Ajuste no se pudo actualizar en este momento.";
            }

        }

        return "listar";
    }

    public String actualizarStatusAjustes() {
        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        this.usuarioSesion = ((UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion"));
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

        String[] status = this.selectedStatus.split(", ");
        log.info("status selected [" + status[0] + "] [" + status[1] + "]---- usuario selected [" + this.selectedUsuario + "]  --- fecha inicio [" + this.fechaIni + "] ---- fecha final[" + this.fechaFin + "] ");

        if (this.opcion.equals("HEditar")) {
            actualizarEdicion();
            System.out.println("hola entre");
        } else {
            String result = "";
            ReporteTransacciones business = new ReporteTransacciones();

            this.listaUsuariosBusqueda = business.getUsuarios();
            String[] idAjustes;
            String idstatus;
            try {
                if (this.selectedAjuste == null && !status[1].equals("AUTORIZAR TODOS")) {
                    this.message = "Error al cambiar el status.";
                    tipoMessage = "error";
                    return "listar";
                }
                if (status[1].equals("AUTORIZAR TODOS")) {
                    this.ajustes = business.getAjustes(getStatusfromSelected(status[0]), this.selectedUsuario, this.fechaIni, this.fechaFin);
                    log.info("LISTAAA " + this.ajustes.size());
                    this.selectedAjuste = "";
                    for (int i = 0; i < this.ajustes.size(); i++) {
                        this.selectedAjuste = this.selectedAjuste + this.ajustes.get(i).getIdDetalleAjuste() + ",";
                    }
                    this.selectedAjuste = this.selectedAjuste.substring(0, this.selectedAjuste.length() - 1);
                }

                idstatus = getStatusfromSelected(status[1]);
                log.info("AJUSTES [" + this.selectedAjuste + "]");
                idAjustes = this.selectedAjuste.split(",");

            } catch (Exception e) {
                return "listar";
            }
            result = business.updateAjuste(idAjustes, idstatus);

            switch (result) {
                case "ok":
                    this.message = "Ajuste actualizado satisfactoriamente.";
                    break;
                case "vacio":
                    this.message = "Tipo de ajuste [Autorizado/Anulado/Procesado] no puede ser [Autorizado].";
                    tipoMessage = "error";
                    return "listar";
                default:
                    this.message = "El Ajuste no se pudo actualizar en este momento.";
                    break;
            }

        }
        this.ajustes.clear();
        return "listar";
    }

    public String actualizarEdicion() {
        String result = "";
        ReporteTransacciones business = new ReporteTransacciones();

        if (this.montoEditar == null || this.montoEditar.equals("")) {
            this.message = "Monto invalido, intente nuevamente.";
            tipoMessage = "error";
            this.listaUsuariosBusqueda = business.getUsuarios();
            return "listar";
        }

        String[] idAjustes;
        try {
            idAjustes = this.filaEditar.split(",");
        } catch (Exception e) {
            this.message = "El Ajuste no se pudo actualizar en este momento.";
            return "listar";
        }

        String monto = this.montoEditar;

        monto = monto.replace(",", ".");

        if (!monto.matches("\\d{1,8}.\\d{1,2}")) {
            tipoMessage = "error";
            return "error";
        }

        this.tipoAjustes = business.getTipoAjustes();
        result = business.updateEdicion(idAjustes, monto, this.selectedtipoAjuste, this.AjusteDesc);

        if (result.equals("ok")) {
            this.message = " Ajuste actualizado satisfactoriamente.";
        } else {
            this.message = "El Ajuste no se pudo actualizar en este momento.";
        }

        this.listaUsuariosBusqueda = business.getUsuarios();
        return "listar";
    }

    public String buscarTipoAjustes() {
        ReporteTransacciones business = new ReporteTransacciones();
        this.tipoAjustes = business.getTipoAjustes();
        this.editar = true;
        this.fechaIni = getFechaIni2();
        this.fechaFin = getFechaFin2();
        this.opcion = "buscarTipoAjustes";

        return "listar";
    }

    public String buscarAjustes() {
        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        this.usuarioSesion = ((UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion"));
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

        log.info("status selected [" + this.selectedStatus + "] ---- usuario selected [" + this.selectedUsuario + "]  --- fecha inicio [" + this.fechaIni + "] ---- fecha final[" + this.fechaFin + "] ---- filtro[" + this.selectedFiltro + "] ");

        ReporteTransacciones business = new ReporteTransacciones();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");

//        this.fechaIni = new GregorianCalendar(2016, 01, 01).getTime();
//        this.fechaFin = new GregorianCalendar(2017, 05, 05).getTime();
        if ((this.fechaIni == null && this.fechaFin == null)
                || (this.fechaIni == null && this.fechaFin != null)
                || (this.fechaIni != null && this.fechaFin == null)) {
            this.message = "Error, ingresar un rango de fecha.";
            tipoMessage = "error";
            this.listaUsuariosBusqueda = business.getUsuarios();
            return "listar";
        } else if (Integer.parseInt(df.format(this.fechaIni)) > Integer.parseInt(df.format(this.fechaFin))) {
            this.message = "Error, fecha inicio no debe ser mayor a fecha fin.";
            tipoMessage = "error";
            this.listaUsuariosBusqueda = business.getUsuarios();
            return "listar";
        } else if (Integer.parseInt(df.format(this.fechaFin)) < Integer.parseInt(df.format(this.fechaIni))) {
            this.message = "Error, fecha fin no debe ser menor a fecha inicio.";
            tipoMessage = "error";
            this.listaUsuariosBusqueda = business.getUsuarios();
            return "listar";
        }

        this.listaUsuariosBusqueda = business.getUsuarios();
        this.ajustes = business.getAjustes(getStatusfromSelected(this.selectedStatus), this.selectedUsuario, this.fechaIni, this.fechaFin, this.selectedFiltro);
        return "listar";
    }

    public String ajusteTarjeta() {
        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        this.usuarioSesion = ((UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion"));
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
        this.tipoAjustes = business.getTipoAjustes();
        return "ajustetarjeta";
    }

    public String getNroTarjeta() {
        return this.nroTarjeta;
    }

    public void setNroTarjeta(String nroTarjeta) {
        this.nroTarjeta = nroTarjeta;
    }

    public String getDocumentoIdentidad() {
        return this.documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public String buscarUsuario() {

        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        this.usuarioSesion = ((UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion"));
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
        String usuario = "prueba busqueda usuario 123";

        log.info("------------------------------");
        log.info("DNI:" + documentoIdentidad);
        log.info("Tarjeta:" + nroTarjeta);
        log.info("Producto:" + selectedProducto);
        log.info("Empresa:" + selectedEmpresa);
        log.info("------------------------------");

        //VALIDO FILTRO FECHA
        DateFormat df = new SimpleDateFormat("yyyyMMdd");

        if (this.opcion.equals("buscarProductosEmpresas")) {
            if ((this.fechaIni == null && this.fechaFin == null)
                    || (this.fechaIni == null && this.fechaFin != null)
                    || (this.fechaIni != null && this.fechaFin == null)) {
                this.message = "Error, ingresar un rango de fecha.";
                tipoMessage = "error";
                this.listaProductos = business.getProductos();
                return "success";
            } else if (Integer.parseInt(df.format(this.fechaIni)) > Integer.parseInt(df.format(this.fechaFin))) {
                this.message = "Error, fecha inicio no debe ser mayor a fecha fin.";
                tipoMessage = "error";
                this.listaProductos = business.getProductos();
                return "success";
            } else if (Integer.parseInt(df.format(this.fechaFin)) < Integer.parseInt(df.format(this.fechaIni))) {
                this.message = "Error, fecha fin no debe ser menor a fecha inicio.";
                tipoMessage = "error";
                this.listaProductos = business.getProductos();
                return "success";
            }
        }

        //VALIDA DATOS/FILTROS A BUSCAR
        if (this.opcion.equals("buscarProductosEmpresas")) {
            if (this.nroTarjeta.equals("") && this.documentoIdentidad.equals("")
                    && this.selectedEmpresa.equals("")) {
                this.message = "Error, ingresar al menos un filtro mas.";
                tipoMessage = "error";
                this.listaProductos = business.getProductos();
                return "success";
            } else if (this.selectedProducto.equals("") && this.documentoIdentidad.equals("")
                    && this.nroTarjeta.equals("")) {
                this.message = "Error, ingresar al menos un filtro mas.";
                tipoMessage = "error";
                this.listaProductos = business.getProductos();
                return "success";
            }
        }

        if ((this.nroTarjeta != null) && (!this.nroTarjeta.equals("")) && !this.opcion.equals("buscarProductosEmpresas")) {
            log.info("tarjeta = " + this.nroTarjeta);
            this.tarjetas = business.getTarjetasUsuario(null, this.nroTarjeta);
        }

        if ((this.documentoIdentidad != null) && (!this.documentoIdentidad.equals("")) && !this.opcion.equals("buscarProductosEmpresas")) {
            //this.tarjetas = business.getTarjetasUsuario(this.documentoIdentidad, null, this.selectedProducto.trim(), this.selectedEmpresa.trim());
            this.tarjetas = business.getTarjetasUsuario(this.documentoIdentidad, null);

        }

        if (this.opcion.equals("buscarProductosEmpresas")) {
            DateFormat df2 = new SimpleDateFormat("ddMMyyyy");
            this.tarjetas = business.getTarjetasUsuarioTransacciones(this.documentoIdentidad, this.nroTarjeta, this.selectedProducto, this.selectedEmpresa, df2.format(this.fechaIni), df2.format(this.fechaFin));
        }

        if ((this.opcion != null) && (this.opcion.equals("buscarUsuario2"))) {
            this.tipoAjustes = business.getTipoAjustes();

            if ((this.tarjetas == null) || (this.tarjetas.size() == 0)) {
                this.message = "No se encontraron registros de tarjetas para este usuario";
            }
            return "ajustetarjeta";
        }

        this.listaProductos = business.getProductos();
        //this.listaEmpresas = business.getEmpresas();
        log.info(usuario);

//    if ((this.tarjetas == null) || (this.tarjetas.isEmpty())) {
//      this.message = "No se encontraron registros de tarjetas para este usuario";
//    }
        return "success";
    }

    public String getTransaccionesTarjeta() {
        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        this.usuarioSesion = ((UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion"));
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
        this.transacciones = business.getTransaccionesUsuario(this.tarjetaSelected);
        this.tipoAjustes = business.getTipoAjustes();
        if ((this.transacciones == null) || (this.transacciones.size() == 0)) {
            this.message = "No se encontraron registros";
        }
        return "transacciones";
    }

    public String registrarAjuste() {

        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        this.usuarioSesion = ((UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion"));
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

        String result = "";
        ReporteTransacciones business = new ReporteTransacciones();
        log.info("registrarajuste tarjeta [" + this.nroTarjeta + "] referencia [" + this.systrace + "] montoajuste [" + this.montoAjuste + "] codigoajuste[" + this.selectedAjuste + "]");
        result = business.RegistrarAjuste(this.nroTarjeta, this.montoAjuste, this.selectedAjuste, this.systrace, this.usuarioSesion.getIdUsuario(), this.observacion, false);
        if (result.equals("ok")) {
            this.message = "Registro de Ajuste realizado satisfactoriamente.";
        } else {
            this.message = "Su Ajuste no pudo ser registrado.";
        }
        if (this.systrace == null) {
            return "ajustetarjeta";
        }

        return "success";
    }

    public String listar() {

        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        this.usuarioSesion = ((UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion"));
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
        this.listaUsuariosBusqueda = business.getUsuarios();

        this.listaProductos = business.getProductos();
        //this.listaEmpresas = business.getEmpresas();
        return "listar";
    }

    public List<Transaccion> getTransacciones() {
        return this.transacciones;
    }

    public void setTransacciones(List<Transaccion> transacciones) {
        this.transacciones = transacciones;
    }

    public List<Tarjeta> getTarjetas() {
        return this.tarjetas;
    }

    public void setTarjetas(List<Tarjeta> tarjetas) {
        this.tarjetas = tarjetas;
    }

    public String getTarjetaSelected() {
        return this.tarjetaSelected;
    }

    public void setTarjetaSelected(String tarjetaSelected) {
        this.tarjetaSelected = tarjetaSelected;
    }

    public String getMontoAjuste() {
        return this.montoAjuste;
    }

    public void setMontoAjuste(String montoAjuste) {
        this.montoAjuste = montoAjuste;
    }

    public List<TAjuste> getTipoAjustes() {
        return this.tipoAjustes;
    }

    public void setTipoAjustes(List<TAjuste> tipoAjustes) {
        this.tipoAjustes = tipoAjustes;
    }

    public String getSelectedAjuste() {
        return this.selectedAjuste;
    }

    public void setSelectedAjuste(String selectedAjuste) {
        this.selectedAjuste = selectedAjuste;
    }

    public String getSystrace() {
        return this.systrace;
    }

    public void setSystrace(String systrace) {
        this.systrace = systrace;
    }

    public String getOpcion() {
        return this.opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public List<Ajuste> getAjustes() {
        return this.ajustes;
    }

    public void setAjustes(List<Ajuste> ajustes) {
        this.ajustes = ajustes;
    }

    public List<String> getStatus() {
        return this.status;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }

    public String getSelectedStatus() {
        return this.selectedStatus;
    }

    public void setSelectedStatus(String selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public UsuarioSesion getUsuarioSesion() {
        return this.usuarioSesion;
    }

    public void setUsuarioSesion(UsuarioSesion usuarioSesion) {
        this.usuarioSesion = usuarioSesion;
    }

    public List<String> getListaUsuariosBusqueda() {
        return this.listaUsuariosBusqueda;
    }

    public void setListaUsuariosBusqueda(List<String> listaUsuariosBusqueda) {
        this.listaUsuariosBusqueda = listaUsuariosBusqueda;
    }

    public String getSelectedUsuario() {
        return this.selectedUsuario;
    }

    public void setSelectedUsuario(String selectedUsuario) {
        this.selectedUsuario = selectedUsuario;
    }

    public Date getFechaIni() {
        return this.fechaIni;
    }

    public void setFechaIni(Date fechaIni) {
        this.fechaIni = fechaIni;
    }

    public Date getFechaFin() {
        return this.fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getStatusfromSelected(String status) {
        if (status.equals("PROCESADO")) {
            return "2";
        }
        if (status.equals("PENDIENTE")) {
            return "3";
        }
        if (status.equals("AUTORIZADO")) {
            return "0";
        }
        if (status.equals("AUTORIZAR TODOS")) {
            return "0";
        }
        if (status.equals("ANULADO")) {
            return "7";
        }
        if (status.equals("EN PROCESO")) {
            return "1";
        }
        if (status.equals("TODOS")) {
            return "TODOS";
        }
        return null;
    }

    public String getIdDetalleAjuste() {
        return this.idDetalleAjuste;
    }

    public void setIdDetalleAjuste(String idDetalleAjuste) {
        this.idDetalleAjuste = idDetalleAjuste;
    }

    public String getCheckMe() {
        return this.checkMe;
    }

    public void setCheckMe(String checkMe) {
        this.checkMe = checkMe;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String Upload() {

        //Valido sesion
        SessionUtil sessionUtil = new SessionUtil();
        this.usuarioSesion = ((UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion"));
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
        this.tipoAjustes = business.getTipoAjustes();
        List ajustes = new ArrayList();
        Ajuste ajuste = new Ajuste();
        boolean procesoOk = true;
        try {

            if (this.file == null) {
                this.message = "Error al cargar el archivo";
                tipoMessage = "error";
                return "ajustetarjeta";
            }

            log.info(this.file.getAbsolutePath() + " " + this.file.getCanonicalPath());

            File file2 = new File(this.file.getPath() + this.filename);
            this.file.renameTo(file2);
            InputStream buffer = new FileInputStream(file2.getAbsolutePath());
            Workbook workbook = WorkbookFactory.create(buffer);
            Sheet sheet = workbook.getSheetAt(0);
            String tarjetaString = "";
            String montoString = "";
            double montoDouble = 0.0D;
            double tarjetaDouble = 0.0D;
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

                ajuste.setTarjeta(tarjetaString.trim());
                ajuste.setMonto(montoString.trim());
                log.info("tarjeta [" + ajuste.getTarjeta() + "] Monto[" + ajuste.getMonto() + "] ");

                if (!ajuste.getTarjeta().trim().matches("^[0-9]{16}$")) {
                    this.message = "Error en formato. Algunas tarjetas no son de 16 dígitos.";
                    tipoMessage = "error";
                    procesoOk = false;
                    break;
                }

                if (!ajuste.getMonto().matches("\\d{1,8}.\\d{1,2}")) {
                    this.message = "Error en formato. Algunos montos presentan errores en su formato. Ejemplo de formato correcto [100.00]";
                    tipoMessage = "error";
                    procesoOk = false;
                    break;
                }

                ajustes.add(ajuste);
                ajuste = new Ajuste();
                i++;
            } while ((!tarjetaString.equals("")) && (sheet.getRow(i) != null));
            if (i > 500) {
                this.message = "Numero de registros en el archivo excedido";
                tipoMessage = "error";
                return "ajustetarjeta";
            }
            if (procesoOk) {
                this.message = "exitoso";
                String respuesta = business.checkTarjetas(ajustes);
                if (respuesta.contains("errorT")) {
                    message = "Error, Tarjeta(s) Inexistente(s):" + respuesta.substring(6, respuesta.length());
                    tipoMessage = "error";
                    return "ajustetarjeta";
                } else if (respuesta.contains("error")) {
                    message = "[!] Error de sistema";
                    tipoMessage = "error";
                    return "ajustetarjeta";
                }

                if (business.doAjusteMasivo(ajustes, this.selectedAjuste, this.usuarioSesion.getIdUsuario(), this.observacion).compareToIgnoreCase("error") == 0) {
                    this.message = "Error registrando los ajustes";
                    tipoMessage = "error";
                    return "ajustetarjeta";
                }
            }
        } catch (Exception e) {
            log.error("error ", e);
            tipoMessage = "error";
            this.message = "[!] Error de sistema";
        }
        log.info("Message: " + this.message);
        return "ajustetarjeta";
    }

    public String generarExcel() throws Exception {

        this.message = "Llamada al método de Generar Excel. ";

        this.buscarUsuario();

        this.fecha = com.novo.util.DateUtil.getYesterday();
        AjusteTransaccionesProc business = new AjusteTransaccionesProc(fecha, "AJUSTE_TRANSACCIONES");

        this.reportFile = "ajuste_transacciones_" + DateUtil.format("YYYYMMdd", fecha) + ".xls";
        try {
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            business.crearWorkBookExcel(this.tarjetas).write(boas);
            setInputStream(new ByteArrayInputStream(boas.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return EXCEL;
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

    public List<String> getStatus2() {
        return this.status2;
    }

    public void setStatus2(List<String> status2) {
        this.status2 = status2;
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

    public String getObservacion() {
        return this.observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public boolean isEditar() {
        return this.editar;
    }

    public void setEditar(boolean editar) {
        this.editar = editar;
    }

    public String getSelectedStatus2() {
        return this.selectedStatus2;
    }

    public void setSelectedStatus2(String selectedStatus2) {
        this.selectedStatus2 = selectedStatus2;
    }

    public String getFilaEditar() {
        return this.filaEditar;
    }

    public void setFilaEditar(String filaEditar) {
        this.filaEditar = filaEditar;
    }

    public String getMontoEditar() {
        return this.montoEditar;
    }

    public void setMontoEditar(String montoEditar) {
        this.montoEditar = montoEditar;
    }

    public String getSelectedtipoAjuste() {
        return this.selectedtipoAjuste;
    }

    public void setSelectedtipoAjuste(String selectedtipoAjuste) {
        this.selectedtipoAjuste = selectedtipoAjuste;
    }

    public Date getFechaIni2() {
        return this.fechaIni2;
    }

    public void setFechaIni2(Date fechaIni2) {
        this.fechaIni2 = fechaIni2;
    }

    public Date getFechaFin2() {
        return this.fechaFin2;
    }

    public void setFechaFin2(Date fechaFin2) {
        this.fechaFin2 = fechaFin2;
    }

    public String getTipoMessage() {
        return tipoMessage;
    }

    public void setTipoMessage(String tipoMessage) {
        this.tipoMessage = tipoMessage;
    }

    public String getSelectedUsuario2() {
        return selectedUsuario2;
    }

    public void setSelectedUsuario2(String selectedUsuario2) {
        this.selectedUsuario2 = selectedUsuario2;
    }

    public String getTipoAjusteEditar() {
        return tipoAjusteEditar;
    }

    public void setTipoAjusteEditar(String tipoAjusteEditar) {
        this.tipoAjusteEditar = tipoAjusteEditar;
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

    public List<String> getFiltro() {
        return filtro;
    }

    public void setFiltro(List<String> filtro) {
        this.filtro = filtro;
    }

    public String getSelectedFiltro() {
        return selectedFiltro;
    }

    public void setSelectedFiltro(String selectedFiltro) {
        this.selectedFiltro = selectedFiltro;
    }

}
