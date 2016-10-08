package com.novo.actions;

import com.novo.constants.BasicConfig;
import com.novo.model.Ajuste;
import com.novo.model.Empresa;
import com.novo.model.Producto;
import com.novo.model.TAjuste;
import com.novo.model.Tarjeta;
import com.novo.model.Transaccion;
import com.novo.model.UsuarioSesion;
import com.novo.process.ReporteTransacciones;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class AjusteTransaccionesAction extends ActionSupport
  implements BasicConfig
{
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
  private List<Producto> listaProductos;
  private List<Empresa> listaEmpresas;
  private String selectedStatus;
  private String selectedStatus2;
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
  String pais;

  public AjusteTransaccionesAction()
  {
    this.listaUsuariosBusqueda = new ArrayList();
    this.transacciones = new ArrayList();
    this.tipoAjustes = new ArrayList();
    this.ajustes = new ArrayList();
    this.usuarioSesion = ((UsuarioSesion)ActionContext.getContext().getSession().get("usuarioSesion"));
    this.status = new ArrayList();
    this.status2 = new ArrayList();
    this.listaProductos = new ArrayList();
    this.listaEmpresas = new ArrayList();

    this.status.add("PENDIENTE");
    this.status.add("EN PROCESO");
    this.status.add("AUTORIZADO");
    this.status.add("ANULADO");
    this.status.add("PROCESADO");
    this.status2.add("AUTORIZADO");
    this.status2.add("ANULADO");
  }

  public String execute()
  {
    ReporteTransacciones business = new ReporteTransacciones();
    this.tipoAjustes = business.getTipoAjustes();
    if (this.opcion.equals("-1")) {
      this.listaProductos = business.getProductos();
      this.listaEmpresas = business.getEmpresas();
    }
    String result = "";
    System.out.println("opcion" + this.opcion);

    if (this.opcion.equals("registrarAjuste")) {
      log.info("registrarajuste tarjeta [" + this.nroTarjeta + "] referencia [" + this.systrace + "] montoajuste [" + this.montoAjuste + "] codigoajuste[" + this.selectedAjuste + "]");
      result = business.RegistrarAjuste(this.nroTarjeta, this.montoAjuste, this.selectedAjuste, this.systrace, this.usuarioSesion.getIdUsuario(), this.observacion, false);
      if (result.equals("ok"))
        this.message = "Registro de Ajuste realizado satisfactoriamente.";
      else {
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
      try { idAjustes = this.selectedAjuste.split(",");
      } catch (Exception e) {
        this.message = "El Ajuste no se pudo actualizar en este momento.";
        return "listar";
      }
      String idstatus = getStatusfromSelected(this.selectedStatus);
      result = business.updateAjuste(idAjustes, idstatus);

      if (result.equals("ok")) {
        this.message = " Ajuste actualizado satisfactoriamente.";
      }
      else {
        this.message = "El Ajuste no se pudo actualizar en este momento.";
      }
      return "listar";
    }

    if (this.opcion.equals("buscarTipoAjustes"))
    {
      this.listaUsuariosBusqueda = business.getUsuarios();
      this.ajustes = business.getAjustes(getStatusfromSelected(this.selectedStatus2), this.selectedUsuario, this.fechaIni2, this.fechaFin2);
      System.out.println("seledUsuario" + this.selectedUsuario);
      buscarTipoAjustes();
      return "listar";
    }

    return "success";
  }

  public String actualizarAjustes()
  {
    if (this.opcion.equals("HEditar")) {
      actualizarEdicion();
      System.out.println("hola entre"); } else {
      String result = "";
      ReporteTransacciones business = new ReporteTransacciones();

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

      if (result.equals("ok"))
        this.message = " Ajuste actualizado satisfactoriamente.";
      else {
        this.message = "El Ajuste no se pudo actualizar en este momento.";
      }
     
    }

    return "listar";
  }

  public String actualizarEdicion()
  {
    String result = "";
    ReporteTransacciones business = new ReporteTransacciones();
    String[] idAjustes;
    try {
      idAjustes = this.selectedAjuste.split(",");
    } catch (Exception e) {
      this.message = "El Ajuste no se pudo actualizar en este momento.";
      return "listar";
    }

    String monto = this.montoEditar;

    monto = monto.replace(",", ".");

    if (!monto.matches("\\d{1,8}.\\d{1,2}"))
    {
      return "error";
    }

    this.tipoAjustes = business.getTipoAjustes();
    result = business.updateEdicion(idAjustes, monto, this.selectedtipoAjuste, this.AjusteDesc);

    if (result.equals("ok"))
      this.message = " Ajuste actualizado satisfactoriamente.";
    else {
      this.message = "El Ajuste no se pudo actualizar en este momento.";
    }
    return "listar";
  }

  public String buscarTipoAjustes()
  {
    ReporteTransacciones business = new ReporteTransacciones();
    this.tipoAjustes = business.getTipoAjustes();
    this.editar = true;
    this.opcion = "buscarTipoAjustes";

    return "listar";
  }

  public String buscarAjustes() {
    log.info("status selected [" + this.selectedStatus + "] ---- usuario selected [" + this.selectedUsuario + "]  --- fecha inicio [" + this.fechaIni + "] ---- fecha final[" + this.fechaFin + "] ");

    ReporteTransacciones business = new ReporteTransacciones();
    this.listaUsuariosBusqueda = business.getUsuarios();
    this.ajustes = business.getAjustes(getStatusfromSelected(this.selectedStatus), this.selectedUsuario, this.fechaIni, this.fechaFin);
    return "listar";
  }

  public String ajusteTarjeta()
  {
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

  public String buscarUsuario()
  {
    ReporteTransacciones business = new ReporteTransacciones();
    String usuario = "prueba busqueda usuario 123";
    
     if ((this.nroTarjeta != null) && (!this.nroTarjeta.equals(""))) {
      log.info("tarjeta = " + this.nroTarjeta);
      this.tarjetas = business.getTarjetasUsuario(null, this.nroTarjeta);
    }
    
    if ((this.documentoIdentidad != null) && (!this.documentoIdentidad.equals(""))) {
      log.info("documento identidad = " + this.documentoIdentidad);
      //this.tarjetas = business.getTarjetasUsuario(this.documentoIdentidad, null, this.selectedProducto.trim(), this.selectedEmpresa.trim());
      this.tarjetas = business.getTarjetasUsuario(this.documentoIdentidad, null);
     
    }
    
    if((this.selectedProducto != null) && (!this.selectedProducto.equals("")))
    {
        log.info("Producto"+ selectedProducto);
        this.tarjetas = business.getTarjetasUsuario(this.documentoIdentidad, this.nroTarjeta, this.selectedProducto, null);
    }
    
    if ((this.opcion != null) && (this.opcion.equals("buscarUsuario2"))) 
    {
      this.tipoAjustes = business.getTipoAjustes();
      
      if ((this.tarjetas == null) || (this.tarjetas.size() == 0)) {
        this.message = "No se encontraron registros de tarjetas para este usuario";
      }
      return "ajustetarjeta";
    }
    
    

    this.listaProductos = business.getProductos();
    this.listaEmpresas = business.getEmpresas();
    log.info(usuario);
    
//    if ((this.tarjetas == null) || (this.tarjetas.isEmpty())) {
//      this.message = "No se encontraron registros de tarjetas para este usuario";
//    }
    return "success";
  }
  

  public String getTransaccionesTarjeta()
  {
    ReporteTransacciones business = new ReporteTransacciones();
    this.transacciones = business.getTransaccionesUsuario(this.tarjetaSelected);
    this.tipoAjustes = business.getTipoAjustes();
    if ((this.transacciones == null) || (this.transacciones.size() == 0)) {
      this.message = "No se encontraron registros";
    }
    return "transacciones";
  }

  public String registrarAjuste() {
    String result = "";
    ReporteTransacciones business = new ReporteTransacciones();
    log.info("registrarajuste tarjeta [" + this.nroTarjeta + "] referencia [" + this.systrace + "] montoajuste [" + this.montoAjuste + "] codigoajuste[" + this.selectedAjuste + "]");
    result = business.RegistrarAjuste(this.nroTarjeta, this.montoAjuste, this.selectedAjuste, this.systrace, this.usuarioSesion.getIdUsuario(), this.observacion, false);
    if (result.equals("ok"))
      this.message = "Registro de Ajuste realizado satisfactoriamente.";
    else {
      this.message = "Su Ajuste no pudo ser registrado.";
    }
    if (this.systrace == null) {
      return "ajustetarjeta";
    }

    return "success";
  }

  public String listar() {
    ReporteTransacciones business = new ReporteTransacciones();
    this.listaUsuariosBusqueda = business.getUsuarios();

    this.listaProductos = business.getProductos();
    this.listaEmpresas = business.getEmpresas();
    return "listar";
  }

  public List<Transaccion> getTransacciones()
  {
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
    if (status.equals("ANULADO")) {
      return "7";
    }
    if (status.equals("EN PROCESO")) {
      return "1";
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

  public String Upload()
  {
    ReporteTransacciones business = new ReporteTransacciones();
    this.tipoAjustes = business.getTipoAjustes();
    List ajustes = new ArrayList();
    Ajuste ajuste = new Ajuste();
    boolean procesoOk = true;
    try {
      log.info(this.file.getAbsolutePath() + " " + this.file.getCanonicalPath());

      File file2 = new File(this.file.getPath() + this.filename);
      this.file.renameTo(file2);
      InputStream buffer = new FileInputStream(file2.getAbsolutePath());
      Workbook workbook = WorkbookFactory.create(buffer);
      Sheet sheet = workbook.getSheetAt(0);
      String tarjetaString = ""; String montoString = "";
      double montoDouble = 0.0D; double tarjetaDouble = 0.0D;
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
          procesoOk = false;
          break;
        }

        if (!ajuste.getMonto().matches("\\d{1,8}.\\d{1,2}")) {
          this.message = "Error en formato. Algunos montos presentan errores en su formato. Ejemplo de formato correcto [100.00]";
          procesoOk = false;
          break;
        }

        ajustes.add(ajuste);
        ajuste = new Ajuste();
        i++;
      }while ((!tarjetaString.equals("")) && (sheet.getRow(i) != null));
      if (i > 500) {
        this.message = "numero de registros en el archivo excedido";
        return "ajustetarjeta";
      }
      if (procesoOk) {
        this.message = "exitoso";
        if (business.checkTarjetas(ajustes).compareToIgnoreCase("error") == 0) {
          this.message = "Error, Tarjeta Inexistente.";
          return "ajustetarjeta";
        }
        if (business.doAjusteMasivo(ajustes, this.selectedAjuste, this.usuarioSesion.getIdUsuario(), this.observacion).compareToIgnoreCase("error") == 0) {
          this.message = "Error registrando los ajustes";
          return "ajustetarjeta";
        }
      }
    } catch (Exception e) {
      log.error("error ", e);
      this.message = "error";
    }
    log.info("Message: " + this.message);
    return "ajustetarjeta";
  }

  public void setUpload(File file)
  {
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

  public String getSelectedEmpresa()
  {
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
}