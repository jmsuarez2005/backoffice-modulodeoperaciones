/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.actions;

import com.novo.constants.BasicConfig;
import com.novo.model.Producto;
import com.novo.model.Tarjeta;
import com.novo.model.UsuarioSesion;
import com.novo.process.ReporteTransacciones;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author ggutierrez
 */
public class TrasladosAction extends ActionSupport implements BasicConfig {
    
    private static Logger log = Logger.getLogger(TrasladosAction.class);
    private String message = "";
    
    private String documentoIdentidad;
    private String selectedProducto="";
    
    private List<Producto> listaProductos;
    private List<Tarjeta> tarjetas;
    private Tarjeta tarjetaOrigen;
    private Tarjeta tarjetaDestino;
    private String selectedTarjetaOrigen="";
    private String selectedTarjetaDestino="";
    private String selectedTarjeta;
    private UsuarioSesion usuarioSesion;
    String pais;
    
    public TrasladosAction() {
        tarjetas= new ArrayList<Tarjeta>();
        listaProductos = new ArrayList<Producto>();
        usuarioSesion = (UsuarioSesion)ActionContext.getContext().getSession().get("usuarioSesion");
        pais = ((String)ActionContext.getContext().getSession().get("pais"));
    }
    
    
    
    @Override
    public String execute(){
        ReporteTransacciones business = new ReporteTransacciones();
        listaProductos = business.getProductos();
        return SUCCESS;
    }
    
    public String buscarUsuario() {
        ReporteTransacciones business = new ReporteTransacciones();
        listaProductos = business.getProductos();
        if (selectedProducto != null && !selectedProducto.equals("")) {
            tarjetas = business.getTarjetasUsuario(documentoIdentidad, null, selectedProducto.trim(), "");
        }else{
            message="Debe seleccionar un producto";
        }
        return SUCCESS;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public List<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public String getSelectedProducto() {
        return selectedProducto;
    }

    public void setSelectedProducto(String selectedProducto) {
        this.selectedProducto = selectedProducto;
    }

    public List<Tarjeta> getTarjetas() {
        return tarjetas;
    }

    public void setTarjetas(List<Tarjeta> tarjetas) {
        this.tarjetas = tarjetas;
    }    

    public Tarjeta getTarjetaOrigen() {
        return tarjetaOrigen;
    }

    public void setTarjetaOrigen(Tarjeta tarjetaOrigen) {
        this.tarjetaOrigen = tarjetaOrigen;
    }

    public Tarjeta getTarjetaDestino() {
        return tarjetaDestino;
    }

    public void setTarjetaDestino(Tarjeta tarjetaDestino) {
        this.tarjetaDestino = tarjetaDestino;
    }
    public String ereaseTarjetaTras(){
        ReporteTransacciones business = new ReporteTransacciones();
        log.info("borrarTarjetas() ");
        listaProductos = business.getProductos();
        tarjetas = business.getTarjetasUsuario(documentoIdentidad, null, selectedProducto.trim(), "");  
        tarjetaDestino = null;
        tarjetaOrigen = null;
        return SUCCESS;
    }
    
    public String fillTarjetaOrigen(){
        log.info("fillTarjetaOrigen() ["+selectedTarjeta+"]");
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setMascara("prueba");
        ReporteTransacciones business = new ReporteTransacciones();
        tarjetas = business.getTarjetasUsuario(documentoIdentidad, null, selectedProducto.trim(), "");  
        
        listaProductos = business.getProductos();
        for(Tarjeta tarjetaAux: tarjetas){
            if(tarjetaAux.getNroTarjeta().equals(selectedTarjeta)){
                tarjetaOrigen=tarjetaAux;
            }
            if(selectedTarjetaDestino.equals(tarjetaAux.getNroTarjeta())){
                tarjetaDestino=tarjetaAux;
            }
        }
        if (tarjetaDestino != null) {
            tarjetaDestino = business.consultarSaldo(tarjetaDestino);            
        }
        if (tarjetaOrigen != null) {
            tarjetaOrigen = business.consultarSaldo(tarjetaOrigen);
        }
        return SUCCESS;
    }
    public String fillTarjetaDestino(){
        log.info("fillTarjetaDestino() ["+selectedTarjeta+"]");
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setMascara("prueba");
        ReporteTransacciones business = new ReporteTransacciones();
        tarjetas = business.getTarjetasUsuario(documentoIdentidad, null, selectedProducto.trim(), "");  
        
        listaProductos = business.getProductos();
        for(Tarjeta tarjetaAux: tarjetas){
            if(tarjetaAux.getNroTarjeta().equals(selectedTarjeta)){
                tarjetaDestino=tarjetaAux;
            }
            if(selectedTarjetaOrigen.equals(tarjetaAux.getNroTarjeta())){
                tarjetaOrigen=tarjetaAux;
            }
        }
        if (tarjetaDestino != null) {
            tarjetaDestino = business.consultarSaldo(tarjetaDestino);
        }
        if (tarjetaOrigen != null) {
            tarjetaOrigen = business.consultarSaldo(tarjetaOrigen);
        }
        return SUCCESS;
    }
    
    /**
     * Realiza un Traslado
     * @return 
     */
    public String procesar(){
        ReporteTransacciones business = new ReporteTransacciones();
        tarjetas = business.getTarjetasUsuario(documentoIdentidad, null, selectedProducto.trim(), "");  
        listaProductos = business.getProductos();
        for(Tarjeta tarjetaAux: tarjetas){
            if(selectedTarjetaDestino.equals(tarjetaAux.getNroTarjeta())){
                tarjetaDestino=tarjetaAux;
            }
            if(selectedTarjetaOrigen.equals(tarjetaAux.getNroTarjeta())){
                tarjetaOrigen=tarjetaAux;
            }
        }
        if (tarjetaDestino != null) {
            tarjetaDestino = business.consultarSaldo(tarjetaDestino);
        }
        if (tarjetaOrigen != null) {
            tarjetaOrigen = business.consultarSaldo(tarjetaOrigen);
        }
        tarjetaDestino.setMonto(tarjetaOrigen.getSaldoDisponible());
        tarjetaOrigen.setMonto(tarjetaOrigen.getSaldoDisponible());
        if(business.traslado(tarjetaOrigen, tarjetaDestino, usuarioSesion.getIdUsuario())==0){
            message= "Traslado realizado con exito";
        }else{
            message = "Traslado no realizado";
        }
        tarjetaDestino=null;
        tarjetaOrigen=null;
        return SUCCESS;
    }
    public String getSelectedTarjeta() {
        return selectedTarjeta;
    }

    public void setSelectedTarjeta(String selectedTarjeta) {
        this.selectedTarjeta = selectedTarjeta;
    }

    public String getSelectedTarjetaOrigen() {
        return selectedTarjetaOrigen;
    }

    public void setSelectedTarjetaOrigen(String selectedTarjetaOrigen) {
        this.selectedTarjetaOrigen = selectedTarjetaOrigen;
    }

    public String getSelectedTarjetaDestino() {
        return selectedTarjetaDestino;
    }

    public void setSelectedTarjetaDestino(String selectedTarjetaDestino) {
        this.selectedTarjetaDestino = selectedTarjetaDestino;
    }
    
}
