/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.model;

/**
 *
 * @author ggutierrez
 */
public class Transaccion {

    private String nroTarjeta;
    private String mascara;
    private String systrace;
    private String monto;
    private String montoComision;
    private String descripcion;
    private String fechaTransaccion;    
    
    private String transRespCode;
    private String saldo;    
    private int rc;
    private String msg;
    public Transaccion() {
    }

    public String getMascara() {
        return mascara;
    }

    public void setMascara(String mascara) {
        this.mascara = mascara;
    }
    
    
    public String getNroTarjeta() {
        return nroTarjeta;
    }

    public void setNroTarjeta(String nroTarjeta) {
        this.nroTarjeta = nroTarjeta;
    }

    public String getSystrace() {
        return systrace;
    }

    public void setSystrace(String systrace) {
        this.systrace = systrace;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monton) {
        this.monto = monton;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(String fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public String getMontoComision() {
        return montoComision;
    }

    public void setMontoComision(String montoComision) {
        this.montoComision = montoComision;
    }

    public String getTransRespCode() {
        return transRespCode;
    }

    public void setTransRespCode(String transRespCode) {
        this.transRespCode = transRespCode;
    }

    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }

    public int getRc() {
        return rc;
    }

    public void setRc(int rc) {
        this.rc = rc;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
        
    
}
