/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.model;

/**
 *
 * @author rgomez
 */
public class TBloqueo {
    
  private String tipo_bloque;
  private String id_bloque;
  private String Status;
  private String codigo;
  private String nro_tarjeta;
  private String descripcion;
  private String fec_reg;

  public String getTipo_bloque()
  {
    return this.tipo_bloque;
  }

  public void setTipo_bloque(String tipo_bloque) {
    this.tipo_bloque = tipo_bloque;
  }

  public String getId_bloque() {
    return this.id_bloque;
  }

  public void setId_bloque(String id_bloque) {
    this.id_bloque = id_bloque;
  }

  public String getStatus() {
    return this.Status;
  }

  public void setStatus(String Status) {
    this.Status = Status;
  }

  public String getCodigo() {
    return this.codigo;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getNro_tarjeta() {
    return this.nro_tarjeta;
  }

  public void setNro_tarjeta(String nro_tarjeta) {
    this.nro_tarjeta = nro_tarjeta;
  }

  public String getDescripcion() {
    return this.descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getFec_reg() {
    return this.fec_reg;
  }

  public void setFec_reg(String fec_reg) {
    this.fec_reg = fec_reg;
  }
    
}
