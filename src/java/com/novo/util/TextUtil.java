/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author jorojas
 */
public class TextUtil {
    
    public static String formatearDecimal(Object decimal){
        Locale locale  = new Locale("es", "VE");
        String pattern = "###,##0.##";
        DecimalFormat decimalFormat = (DecimalFormat)NumberFormat.getNumberInstance(locale);
        decimalFormat.applyPattern(pattern);
        
        return decimalFormat.format(decimal);
    }
    
    public static String formatearEntero(int entero){
        Locale locale  = new Locale("es", "VE");
        String pattern = "#,###";
        DecimalFormat decimalFormat = (DecimalFormat)NumberFormat.getNumberInstance(locale);
        decimalFormat.applyPattern(pattern);
        
        return decimalFormat.format(entero);
    }
    
    public static String truncateDouble(double decimal){
        String truncated=decimal+"";
        String[] partes = truncated.split(".");
        if (partes[1].length()>1)
        {
            truncated = partes[0]+"."+partes[1].charAt(0)+partes[1].charAt(1);
        }
        else{
            truncated = partes[0]+"."+partes[1].charAt(0);
        }
        
        return truncated;
    }
}
