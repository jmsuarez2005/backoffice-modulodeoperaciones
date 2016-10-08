/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.actions;

import com.novo.constants.BasicConfig;
import com.novo.dao.RenovacionDAO;
import com.novo.model.Empresa;
import com.novo.model.Producto;
import com.novo.model.Renovacion;
import com.novo.model.Tarjeta;
import com.novo.model.UsuarioSesion;
import com.novo.model.ValoresRen;
import com.novo.objects.util.Utils;
import com.novo.process.RenovacionProc;
import com.novo.process.ReporteTransacciones;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

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

    public String carga() throws IOException {
        UsuarioSesion usuario = (UsuarioSesion) ActionContext.getContext().getSession().get("usuarioSesion");
        this.pais = ((String) ActionContext.getContext().getSession().get("pais"));
        
        
        RenovacionDAO ren = new RenovacionDAO("operaciones", databases, this.pais);
        
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
        RenovacionProc conectar = new RenovacionProc();
        log.info(this.file.getAbsolutePath() + nombreRenovacion);

        conectar.addFile(rutaOrigen + "/" + nombreRenovacion, rutaDestino, host, usuarioR);
        ren.InsertarRenovacion(nombreRenovacion, rutaDestino, "", usuario.getIdUsuario(), "", "", "", "", null);

        return "success";
    }

    public String consultar() {
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

        RenovacionDAO ren = new RenovacionDAO("operaciones", databases, this.pais);
        this.renovar = ren.QueryRenovacion(this.selectedEmpresa, this.selectedProducto, this.documentoIdentidad, this.fechaIni, this.fechaFin);
        listar();

        return "consultar";
    }

    public String execute() {
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

}
