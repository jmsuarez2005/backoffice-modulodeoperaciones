/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.util;

import com.google.gson.Gson;
import com.novo.constants.BasicConfig;
import java.util.Properties;

/**
 *
 * @author lbelen
 * 
 * La clase CountriesUtil se encarga de obtener del archivo novoreport.properties una cadena tipo JSON la cual contiene
 * la lista de paises que usa la base de datos para una sesión en específico.
 * 
 * Variable jsonCoutry captura la cadena del archivo novoreport.properties
 * Variable listCountry mapea la cadena que recibe de la variable jsonCountry
 * 
 */


public  class CountriesUtil {
    private String jsonCountry;
    private Properties listCountry;
    
    /**
     * Constructor que inicializa las variables con la cadena que recibe de la clase novoreport.properties
     */
    public CountriesUtil(){
         //Recibe Lista de Paises
        Gson gson = new Gson();
        jsonCountry = Utils.getConfig(BasicConfig.properties).getProperty("paises");
        listCountry = gson.fromJson(jsonCountry, Properties.class);      
    }
     
    /**
     * 
     * @return lisCountry a la vista login.jsp para mostrar la lista de paises
     */
    public Properties getCountries(){
        return listCountry;
    }
    
}
