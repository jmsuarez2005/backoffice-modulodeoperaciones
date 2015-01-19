/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.model;

/**
 * Clase Modelo utilizada para el transporte de respuestas entre objetos de la capa control y la capa modelo.
 * Comúnmente utilizada para respuestas exitosas, no exitosas y manejo de excepciones.
 * 
 * Forma de utilizarse:
 * Colocarla en la clase que envía respuestas de tipo RC como una variable global rc con su respectivo método getRC(). 
 * Si un determinado método es exitoso, colocar rc=rcExitoso=0, respuesta='Exitosa', mensaje='' y msjExcepcion=''.
 * En caso contrario, especificar rc, respuesta, mensaje, y msjExcepcion en caso de aplicarse una Excepcion.
 * 
 * El objeto que recibirá este rc:
 * llamar al método, obtener el objeto rc. Si el rc obtenido es != rcExitoso=0, aplicar su interpretación.
 * 
 * @author jorojas
 */
public class ResponseRC {
    private int rc;
    private String respuesta; //En caso de necesitarse. Tratar de no utilizarla.
    private String mensaje;
    private String msjExcepcion; //Cuando se arroja una excepcion, escribir el msj en esta variable

    public ResponseRC() {
        this.rc = 0; //0: Exitoso
        this.respuesta = "";
        this.mensaje = "";
        this.msjExcepcion = "";
    }

    public ResponseRC(int rc, String respuesta, String mensaje, String msjExcepcion) {
        this.rc = rc;
        this.respuesta = respuesta;
        this.mensaje = mensaje;
        this.msjExcepcion = msjExcepcion;
    }

    public int getRc() {
        return rc;
    }

    public void setRc(int rc) {
        this.rc = rc;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMsjExcepcion() {
        return msjExcepcion;
    }

    public void setMsjExcepcion(String msjExcepcion) {
        this.msjExcepcion = msjExcepcion;
    }
    
    
    
}
