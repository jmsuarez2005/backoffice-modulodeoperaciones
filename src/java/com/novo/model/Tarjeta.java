/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.model;

/**
 *
 * @author ggutierrez
 */
public class Tarjeta {

    private String nroTarjeta;
    private String idExtPer;
    private String cardProgram;
    private String nombreCliente;
    private String apellidoCliente;
    private String mascara;
    private String rif;
    private String idExtEmp;
    private String nombreEmpresa;
    private String saldoDisponible;
    private String fechaExpiracion;
    private String monto;

    public Tarjeta() {
    }

    public String getNroTarjeta() {
        return nroTarjeta;
    }

    public void setNroTarjeta(String nroTarjeta) {
        this.nroTarjeta = nroTarjeta;
    }

    public String getIdExtPer() {
        return idExtPer;
    }

    public void setIdExtPer(String idExtPer) {
        this.idExtPer = idExtPer;
    }

    public String getCardProgram() {
        return cardProgram;
    }

    public void setCardProgram(String cardProgram) {
        this.cardProgram = cardProgram;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getMascara() {
        return mascara;
    }

    public void setMascara(String mascara) {
        this.mascara = mascara;
    }

    public String getRif() {
        return rif;
    }

    public void setRif(String rif) {
        this.rif = rif;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getIdExtEmp() {
        return idExtEmp;
    }

    public void setIdExtEmp(String idExtEmp) {
        this.idExtEmp = idExtEmp;
    }

    public String getSaldoDisponible() {
        return saldoDisponible;
    }

    public void setSaldoDisponible(String saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
    }

    public String getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(String fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getApellidoCliente() {
        return apellidoCliente;
    }

    public void setApellidoCliente(String apellidoCliente) {
        this.apellidoCliente = apellidoCliente;
    }

}
