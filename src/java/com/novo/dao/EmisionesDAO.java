/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.dao;

import com.novo.constants.BasicConfig;
import com.novo.constants.EmisionesQuery;
import com.novo.database.Dbinterface;
import com.novo.database.NovoDAO;
import com.novo.objects.util.Utils;
import com.novo.util.DateUtil;
import java.util.Date;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author jorojas
 */
public class EmisionesDAO extends NovoDAO implements BasicConfig,EmisionesQuery{
    private static Logger log = Logger.getLogger(EmisionesDAO.class);
    private String pais;
    
    public EmisionesDAO(String appName,String[] databases) {
        super(appName,databases);
        this.pais="";
        log.info("EmisionesDAO inicializado, sin configuración de país.");
    }
    
    public EmisionesDAO(String appName,String[] databases,String pais) {
        super(appName,databases,pais);
        this.pais=pais;
        log.info("EmisionesDAO inicializado, país: "+this.pais);
    }
    
    /**
     * OBTENER CANTIDAD TARJETAS EMITIDAS PERSONA JURIDICA EN EL DIA  (Oracle)
     */
    public int obtenerTarjEmitidasPersonaJurDia(Date fecha) {
        log.info("OBTENER CANTIDAD TARJETAS EMITIDAS PERSONA JURIDICA EN EL DIA (ORACLE)");
        int cantidad=0;
        String day=""+fecha.getDate();
        String month=""+(fecha.getMonth()+1);
        String year=""+(fecha.getYear()+1900);  
        
        month=DateUtil.rellenarCeros(month, 2);
        day=DateUtil.rellenarCeros(day, 2);  
        
        String query = "";
        
        if (this.pais.equals(co)) {
            
            Properties properties = Utils.getConfig("operaciones.properties");
            query = obtenerTarjEmitidasPersonaJurDiaCoQuery
                    .replace("$ACRIF$", properties.getProperty("codCiaEmisionCo"));
            
        }else if(this.pais.equals(pe)) {
            query = obtenerTarjEmitidasPersonaJurDiaPeQuery;
        }else if(this.pais.equals(ve)) {
            query = obtenerTarjEmitidasPersonaJurDiaVeQuery;
        }else if(this.pais.equals(peusd)) {
            query = obtenerTarjEmitidasPersonaJurDiaPeQuery;
        }
        
        
        query=query.replace("$DAY", ""+day);
        query=query.replace("$MONTH", ""+month);
        query=query.replace("$YEAR", ""+year);
        
        log.info("Ejecutando ["+query+"]");
        try{
            Dbinterface dbo=ds.get(oracle);
            dbo.dbreset();
        
            if (dbo.executeQuery(query) == 0) {
                if (dbo.nextRecord()) {
                    if (dbo.getFieldString("cant_plast") != null && !"".equals(dbo.getFieldString("cant_plast"))) {
                        cantidad = Integer.valueOf(dbo.getFieldString("cant_plast"));
                    } else {
                        cantidad = 0;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Se capturó una excepción al intentar obtener tarjetas emitidas persona jurídica en el dia "+this.pais);
            log.error("Causa: "+e);
            e.printStackTrace();
        }
        
        log.info("obtenerTarjEmitidasPersonaJurDia [cantidad: "+cantidad+"]"+this.pais);
        return cantidad;
    }
    
    /**
     * OBTENER CANTIDAD TARJETAS EMITIDAS PERSONA JURIDICA ACUMULADAS  (Oracle)
     */
    public int obtenerTarjEmitidasPersonaJurMes(Date fecha) {
        log.info("OBTENER CANTIDAD TARJETAS EMITIDAS PERSONA JURIDICA ACUMULADAS (Oracle)");
        int cantidad=0;
        String day=""+fecha.getDate();
        String month=""+(fecha.getMonth()+1);
        String year=""+(fecha.getYear()+1900);
        
        
        month=DateUtil.rellenarCeros(month, 2);
        
        String query = "";
        
        if (this.pais.equals(co)) {            
            Properties properties = Utils.getConfig("operaciones.properties");
            query = obtenerTarjEmitidasPersonaJurMesCoQuery
                    .replace("$ACRIF$", properties.getProperty("codCiaEmisionCo"));
        }else if(this.pais.equals(pe)) {
            query = obtenerTarjEmitidasPersonaJurMesPeQuery;
        }else if(this.pais.equals(ve)) {
            query = obtenerTarjEmitidasPersonaJurMesVeQuery;
        }else if(this.pais.equals(peusd)) {
            query = obtenerTarjEmitidasPersonaJurMesPeQuery;
        }
        
        query = query.replace("$FECHAINI", year+"-"+month+"-01 00:00");
        query = query.replace("$FECHAFIN", year+"-"+month+"-"+day+" 23:59");
        
        log.info("Ejecutando ["+query+"]");
        try{
            Dbinterface dbo=ds.get(oracle);
            dbo.dbreset();
        
            if (dbo.executeQuery(query) == 0) {
                if (dbo.nextRecord()) {
                    if (dbo.getFieldString("cant_plast") != null && !"".equals(dbo.getFieldString("cant_plast"))) {
                        cantidad = Integer.valueOf(dbo.getFieldString("cant_plast"));
                    } else {
                        cantidad = 0;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Se capturó una excepción al intentar obtener tarjetas emitidas persona jurídica en el mes "+this.pais);
            log.error("Causa: "+e);
            e.printStackTrace();
        }
        
        log.info("obtenerTarjEmitidasPersonaJurMes [cantidad: "+cantidad+"]"+this.pais);
        
        return cantidad;
    }
    
    /**
     * OBTENER CANTIDAD TARJETAS EMITIDAS PERSONA NATURAL ACUMULADAS  (Oracle)
     */
    public int obtenerTarjEmitidasPersonaNatMes(Date fecha) {
        log.info("OBTENER CANTIDAD TARJETAS EMITIDAS PERSONA NATURAL ACUMULADAS (Oracle)");
        int cantidad=0;
        String day=""+fecha.getDate();
        String month=""+(fecha.getMonth()+1);
        String year=""+(fecha.getYear()+1900);
        
        day=DateUtil.rellenarCeros(day, 2);
        month=DateUtil.rellenarCeros(month, 2);
        
        String query = "";
        
        if (this.pais.equals(co)) {
            query = "";
        }else if(this.pais.equals(pe)) {
            query = obtenerTarjEmitidasPersonaNatMesPeQuery;       
        }else if(this.pais.equals(ve)) {
            query = obtenerTarjEmitidasPersonaNatMesVeQuery;
        }else if(this.pais.equals(peusd)) {
            
        }
        
            query = query.replace("$FECHAINI", ""+year+"-"+month+"-01 00:00:00");
            query = query.replace("$FECHAFIN", ""+year+"-"+month+"-"+day+" 23-59-59");
            query = query.replace("$FORMATO", "yyyy-MM-dd HH24:MI:SS");
        
        log.info("Ejecutando ["+query+"]");
        try{
            Dbinterface dbo=ds.get(oracle);
            dbo.dbreset();
        
            if (dbo.executeQuery(query) == 0) {
                if (dbo.nextRecord()) {
                    if (dbo.getFieldString("cant_plast") != null && !"".equals(dbo.getFieldString("cant_plast"))) {
                        cantidad = Integer.valueOf(dbo.getFieldString("cant_plast"));
                    } else {
                        cantidad = 0;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Se capturó una excepción al intentar obtener tarjetas emitidas persona natural en el mes "+this.pais);
            log.error("Causa: "+e);
            e.printStackTrace();
        }
        
        return cantidad;
    }
    
    /**
     * OBTENER CANTIDAD TARJETAS EMITIDAS PERSONA NATURAL AL DIA (Oracle)
     */
    public int obtenerTarjEmitidasPersonaNatDia(Date fecha) {
        log.info("OBTENER CANTIDAD TARJETAS EMITIDAS PERSONA NATURAL AL DIA (Oracle)");
        int cantidad=0;
        String day=""+fecha.getDate();
        String month=""+(fecha.getMonth()+1);
        String year=""+(fecha.getYear()+1900);
        
        day=DateUtil.rellenarCeros(day, 2);
        month=DateUtil.rellenarCeros(month, 2);
        
        String query = "";
        
        if (this.pais.equals(co)) {
            query = "";
        }else if(this.pais.equals(pe)) {
            query = obtenerTarjEmitidasPersonaNatDiaPeQuery;
        }else if(this.pais.equals(ve)) {
            query = obtenerTarjEmitidasPersonaNatDiaVeQuery;
        }else if(this.pais.equals(peusd)) {

        }
        
        query=query.replace("$FECHA", ""+year+"-"+month+"-"+day);
        query=query.replace("$FORMATO", "YYYY-MM-DD");
        
        log.info("Ejecutando ["+query+"]");
        try{
            Dbinterface dbo=ds.get(oracle);
            dbo.dbreset();
        
            if (dbo.executeQuery(query) == 0) {
                if (dbo.nextRecord()) {
                    if (dbo.getFieldString("cant_plast") != null && !"".equals(dbo.getFieldString("cant_plast"))) {
                        cantidad = Integer.valueOf(dbo.getFieldString("cant_plast"));
                    } else {
                        cantidad = 0;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Se capturó una excepción al intentar obtener tarjetas emitidas persona natural en el dia "+this.pais);
            log.error("Causa: "+e);
            e.printStackTrace();
        }
        
        log.info("obtenerTarjEmitidasPersonaNatDiaVe [cantidad: "+cantidad+"]");
        return cantidad;
    }
    
    @Override
    public void closeConection() {
        this.shutdownDatabases();
    }
    
}
