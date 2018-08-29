/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.constants;

import com.novo.util.Utils;

/**
 *
 * @author jorojas
 */
public interface BasicConfig {
    String appName="operaciones";
    String appNameExtended="Módulo de Operaciones";
    String properties=appName+".properties";
    String version = Utils.getConfig(properties).getProperty("version","No Definida");
    String server = Utils.getConfig(properties).getProperty("server","2"); //1= Tomcat, 2=WebLogic
    String[] databases = {"informix","oracle"};
    String[] dbOracle = {"oracle"};
    String[] dbInformix = {"informix"};
    String oracle="oracle";
    String informix="informix";
    String co="co";
    String pe="pe";
    String peusd="peusd";
    String ve="ve";
    String global="global";
    String CO="CO";
    String PE="PE";
    String PEUSD="PEUSD";
    String VE="VE";
    String GLOBAL="GLOBAL";
    String COLOMBIA="COLOMBIA";
    String PERU="PERU";
    String VENEZUELA="VENEZUELA";
    
    /** Para opciones de Actions **/
    String SESSION="session";
    String VIEW="view";
    String CREATE="create";
    String UPDATE="update";
    String DELETE="delete";
    String EXCEL="excel";
    String PDF="pdf";
    String TXT="txt";
    
    /** Constantes, otras **/
    String USUARIO_SESION="usuarioSesion"; //Llave en la cual es almacenada el usuario de sesion

    //Valida fecha reconversion monetaria bolivar soberano (VENEZUELA)  
    String DIA_RECONVERSION_VZLA = "19"; 
    String MES_RECONVERSION_VZLA = "08"; 
    String ANIO_RECONVERSION_VZLA = "2018"; 
    
}
