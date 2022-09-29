/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author jorojas
 */
public class DateUtil {
    /*
     * rellenarCeros
     * Retorna un String con ceros a la izquierda hasta completar la longitud.
     */
    public static String rellenarCeros(String valor,int longitud){
        String resultado=valor;
        
        int x=longitud-valor.length(), i=0;
        
        while (i<x){
            resultado="0"+resultado;
            i++;
        }
        
        return resultado;
    }
    
    public static String format(String format,Date date){
        DateFormat df = new SimpleDateFormat(format);
        
        return df.format(date);
    }
    
    public static String formatYYYYMMDD(Date date,String format){
        String day=""+date.getDate();
        String month=""+(date.getMonth()+1);
        String year=""+(date.getYear()+1900);
        month=rellenarCeros(month, 2);
        day=rellenarCeros(day, 2);
        
        String dateString=format.replace("YYYY", year).replace("MM", month).replace("DD", day);
        
        return dateString;
    }
    
    public static Date getYesterday(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        // Create a calendar object with today date. Calendar is in java.util pakage.
        Calendar calendar = Calendar.getInstance();

        // Move calendar to yesterday
        calendar.add(Calendar.DATE, -1);

        // Get current date of calendar which point to the yesterday now
        Date yesterday = calendar.getTime();
        
        return yesterday;
    }
}
