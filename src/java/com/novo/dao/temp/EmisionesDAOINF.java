/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.dao.temp;

import com.novo.constants.BasicConfig;
import com.novo.constants.temp.EmisionesQueryINF;
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
public class EmisionesDAOINF extends NovoDAO implements BasicConfig,EmisionesQueryINF{
    private static Logger log = Logger.getLogger(EmisionesDAOINF.class);
    private String pais;
    
    public EmisionesDAOINF(String appName,String[] databases) {
        super(appName,databases);
        this.pais="";
        log.info("EmisionesDAO inicializado, sin configuración de país.");
    }
    
    public EmisionesDAOINF(String appName,String[] databases,String pais) {
        super(appName,databases,pais);
        this.pais=pais;
        log.info("EmisionesDAO inicializado, país: "+this.pais);
    }
    
    /**
     * OBTENER CANTIDAD TARJETAS EMITIDAS PERSONA JURIDICA EN EL DIA  (Informix)
     */
    public int obtenerTarjEmitidasPersonaJurDia(Date fecha) {
        log.info("OBTENER CANTIDAD TARJETAS EMITIDAS PERSONA JURIDICA EN EL DIA  (Informix)");
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
            Dbinterface dbi=ds.get(informix);
            dbi.dbreset();
        
            if (dbi.executeQuery(query) == 0) {
                if (dbi.nextRecord()) {
                    if (dbi.getFieldString("cant_plast") != null && !"".equals(dbi.getFieldString("cant_plast"))) {
                        cantidad = Integer.valueOf(dbi.getFieldString("cant_plast"));
                    } else {
                        cantidad = 0;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Se capturó una excepción al intentar obtener tarjetas emitidas persona jurídica del dia "+this.pais);
            log.error("Causa: "+e);
            e.printStackTrace();
        }
        
        log.info("obtenerTarjEmitidasPersonaJurDia [cantidad: "+cantidad+"]"+this.pais);
        return cantidad;
    }
    
    /**
     * OBTENER CANTIDAD TARJETAS EMITIDAS PERSONA JURIDICA ACUMULADAS  (Informix)
     */
    public int obtenerTarjEmitidasPersonaJurMes(Date fecha) {
        log.info("OBTENER CANTIDAD TARJETAS EMITIDAS PERSONA JURIDICA ACUMULADAS (Informix)");
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
            Dbinterface dbi=ds.get(informix);
            dbi.dbreset();
        
            if (dbi.executeQuery(query) == 0) {
                if (dbi.nextRecord()) {
                    if (dbi.getFieldString("cant_plast") != null && !"".equals(dbi.getFieldString("cant_plast"))) {
                        cantidad = Integer.valueOf(dbi.getFieldString("cant_plast"));
                    } else {
                        cantidad = 0;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Se capturó una excepción al intentar obtener tarjetas emitidas persona jurídica del mes "+this.pais);
            log.error("Causa: "+e);
            e.printStackTrace();
        }
        
        log.info("obtenerTarjEmitidasPersonaJurMes [cantidad: "+cantidad+"]"+this.pais);
        
        return cantidad;
    }
    
    @Override
    public void closeConection() {
        this.shutdownDatabases();
    }
    
}
