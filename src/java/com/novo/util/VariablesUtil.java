/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.util;

/**
 *
 * @author mpulido
 * @lastUpdate 30/10/2020
 */
public class VariablesUtil {
    
        public static  String codPais(String pais) {
            String codigo = "";
            switch (pais){
                case "pe":
                    codigo = "Pe";
                    break;
                case"peusd": 
                    codigo = "Usd";
                    break;
                case "ve": 
                    codigo = "Ve";
                    break;
                case "co": 
                    codigo = "Co";
                    break;
                 case "ec": 
                    codigo = "Ec-bp";
                    break;
                default:
                    codigo = "";
            }
            return codigo;
    }
}
