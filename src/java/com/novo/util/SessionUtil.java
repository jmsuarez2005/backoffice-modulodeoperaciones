/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.util;

import static com.novo.constants.BasicConfig.USUARIO_SESION;
import com.novo.interceptors.SessionInterceptor;
import com.novo.model.UsuarioSesion;
import com.opensymphony.xwork2.ActionContext;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author etabban
 */
public class SessionUtil {

    private static Logger log = Logger.getLogger(SessionUtil.class);

    public SessionUtil() {
    }

    public boolean validateSession(String fechaSession, UsuarioSesion usuarioSesion) {
        boolean res = true;

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        log.info("Validando sesión [" + fechaSession + "]");
        log.info("Actual [" + dateFormat.format(date) + "]");

        long diffMinutos = getMinutos(fechaSession, dateFormat.format(date));

        log.info("Diferencia en minutos [" + diffMinutos + "]");
        
        // *****************************************************
        //Tiempo en minutos que puede durar una sesion activa
        // *****************************************************
        if (diffMinutos >= 10) { 
            res = false;
            return res;
        }else{
            usuarioSesion.setSessionDate(dateFormat.format(date));
        }

        log.info("Usuario " + ((UsuarioSesion) ActionContext.getContext().getSession().get(USUARIO_SESION)).getIdUsuario());

        return res;
    }

    public long getMinutos(String fechaSession, String fechaActual) {
        Date dinicio = null, dfinal = null;
        long milis1, milis2, diff;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        try {
            // PARSEO STRING A DATE
            dinicio = sdf.parse(fechaSession);
            dfinal = sdf.parse(fechaActual);

        } catch (ParseException e) {

            System.out.println("Se ha producido un error en el parseo de fecha sesión");
        }

        //INSTANCIA DEL CALENDARIO GREGORIANO
        Calendar cinicio = Calendar.getInstance();
        Calendar cfinal = Calendar.getInstance();

        //ESTABLECEMOS LA FECHA DEL CALENDARIO CON EL DATE GENERADO ANTERIORMENTE
        cinicio.setTime(dinicio);
        cfinal.setTime(dfinal);

        milis1 = cinicio.getTimeInMillis();

        milis2 = cfinal.getTimeInMillis();

        diff = milis2 - milis1;

        // calcular la diferencia en minutos
        long diffMinutos = Math.abs(diff / (60 * 1000));

        return diffMinutos;

    }

}
