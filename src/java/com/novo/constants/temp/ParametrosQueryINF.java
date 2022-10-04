/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.constants.temp;

/**
 *
 * @author jorojas
 */
public interface ParametrosQueryINF {
    String obtenerParametroQuery = "SELECT acname,acvalue,acprofile,acgroup FROM TEB_PARAMETERS WHERE acname='$ACNAME$'";
    
    String modificarParametroQuery = "UPDATE TEB_PARAMETERS SET acvalue='$ACVALUE$' WHERE acname='$ACNAME$'";
}
