/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.model;

/**
 *
 * @author ggutierrez
 */
public class CamposActualizacion implements Comparable<Object> {
    
    private String registro;
    private String idCampo;
    private String campo;
    private String posInicial;
    private String longitud;
    private String tipo;
    private String valor ;

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

    public String getIdCampo() {
        return idCampo;
    }

    public void setIdCampo(String idCampo) {
        this.idCampo = idCampo;
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

    public String getPosInicial() {
        return posInicial;
    }

    public void setPosInicial(String posInicial) {
        this.posInicial = posInicial;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public int compareTo(Object o) {
        CamposActualizacion objeto =(CamposActualizacion) o;
        return Integer.parseInt(idCampo) - Integer.parseInt(objeto.getIdCampo());
    }
    
    
}
