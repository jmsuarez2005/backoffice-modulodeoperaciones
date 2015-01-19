/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 *
 * @author jorojas
 */
public class ReporteEmisionRecarga implements Serializable{
    private String pais;
    private String producto;
    private int emisionesFecha;
    private int emisionesAcum;
    private BigDecimal recargasDiaLocal;
    private BigDecimal recargasDiaDolares;
    private BigDecimal recargasAcumLocal;
    private BigDecimal recargasAcumDolares;
    private double porcRep;
    private double porcRepGlobal;

    public ReporteEmisionRecarga() {
        this.pais = "";
        this.producto = "";
        this.emisionesFecha = 0;
        this.emisionesAcum = 0;
        this.recargasDiaLocal = new BigDecimal("0.00").setScale(2,BigDecimal.ROUND_HALF_EVEN);
        this.recargasDiaDolares = new BigDecimal("0.00").setScale(2,BigDecimal.ROUND_HALF_EVEN);
        this.recargasAcumLocal = new BigDecimal("0.00").setScale(2,BigDecimal.ROUND_HALF_EVEN);
        this.recargasAcumDolares = new BigDecimal("0.00").setScale(2,BigDecimal.ROUND_HALF_EVEN);
        this.porcRep = 0.00;
        this.porcRepGlobal = 0.00;
    }

    public ReporteEmisionRecarga(String pais, String producto) {
        this.pais = pais;
        this.producto = producto;
        this.emisionesFecha = 0;
        this.emisionesAcum = 0;
        this.recargasDiaLocal = new BigDecimal("0.00").setScale(2,BigDecimal.ROUND_HALF_EVEN);
        this.recargasDiaDolares = new BigDecimal("0.00").setScale(2,BigDecimal.ROUND_HALF_EVEN);
        this.recargasAcumLocal = new BigDecimal("0.00").setScale(2,BigDecimal.ROUND_HALF_EVEN);
        this.recargasAcumDolares = new BigDecimal("0.00").setScale(2,BigDecimal.ROUND_HALF_EVEN);
        this.porcRep = 0.00;
        this.porcRepGlobal = 0.00;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getEmisionesFecha() {
        return emisionesFecha;
    }

    public void setEmisionesFecha(int emisionesFecha) {
        this.emisionesFecha = emisionesFecha;
    }

    public int getEmisionesAcum() {
        return emisionesAcum;
    }

    public void setEmisionesAcum(int emisionesAcum) {
        this.emisionesAcum = emisionesAcum;
    }

    public BigDecimal getRecargasDiaLocal() {
        return recargasDiaLocal;
    }

    public void setRecargasDiaLocal(BigDecimal recargasDiaLocal) {
        this.recargasDiaLocal = recargasDiaLocal;
    }

    public BigDecimal getRecargasDiaDolares() {
        return recargasDiaDolares;
    }

    public void setRecargasDiaDolares(BigDecimal recargasDiaDolares) {
        this.recargasDiaDolares = recargasDiaDolares;
    }

    public BigDecimal getRecargasAcumLocal() {
        return recargasAcumLocal;
    }

    public void setRecargasAcumLocal(BigDecimal recargasAcumLocal) {
        this.recargasAcumLocal = recargasAcumLocal;
    }

    public BigDecimal getRecargasAcumDolares() {
        return recargasAcumDolares;
    }

    public void setRecargasAcumDolares(BigDecimal recargasAcumDolares) {
        this.recargasAcumDolares = recargasAcumDolares;
    }

    public double getPorcRep() {
        return porcRep;
    }

    public void setPorcRep(double porcRep) {
        this.porcRep = porcRep;
    }

    public double getPorcRepGlobal() {
        return porcRepGlobal;
    }

    public void setPorcRepGlobal(double porcRepGlobal) {
        this.porcRepGlobal = porcRepGlobal;
    }
    
    
}
