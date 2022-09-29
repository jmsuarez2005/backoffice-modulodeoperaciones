/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.constants;

/**
 *
 * @author jorojas
 */
public interface RCConfig {
    /** Para respuestas básicas de RC **/
    int rcExitoso = 0;
    int rcUsuarioExitoso = 0;
    int rcError = -1;
    int rcUsuarioNoEncontrado = -1;
    int rcClaveIncorrecta = -2;
    int rcUsuarioInactivo = -3;
    int rcCambiarClave = -4;
    int rcExiste = -5;
    int rcFechaLogin = -6;
}
