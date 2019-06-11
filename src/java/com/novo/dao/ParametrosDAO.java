/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.dao;

import com.novo.constants.BasicConfig;
import com.novo.constants.ParametrosQuery;
import com.novo.database.Dbinterface;
import com.novo.database.NovoDAO;
import com.novo.model.Parametro;
import com.novo.util.TextUtil;
import com.novo.util.Utils;
import org.apache.log4j.Logger;

/**
 *
 * @author jorojas
 */
public class ParametrosDAO extends NovoDAO implements BasicConfig,ParametrosQuery{
    private static Logger log = Logger.getLogger(ParametrosDAO.class);
    private String pais;
    private TextUtil txt = new TextUtil();
    
    public ParametrosDAO(String appName,String[] databases){
        super(appName,databases);
        this.pais = "";
        log.info("ParametrosDAO inicializado, sin configuración de país.");
    }
    
    public ParametrosDAO(String appName,String[] databases,String pais){
        super(appName,databases,pais);
        this.pais = pais;
        log.info("ParametrosDAO inicializado, país: "+this.pais);
    }
    
    public Parametro obtenerParametro(String param){
        log.info("obtenerParametro()");
        Parametro parametro = null;
        
        String query=obtenerParametroQuery.replace("$ACNAME$", param);
        
        log.info("Ejecutando ["+query+"]");
        try{
            String propMigra = Utils.getConfig("oracleRegEx.properties").getProperty("paisOracle");
            Dbinterface dbo;
             if (txt.paisMigra(propMigra, pais)) {                
                dbo=ds.get(ORACLE);
             }else{
                dbo=ds.get(INFORMIX);
             }
             
            dbo.dbreset();

            if (dbo.executeQuery(query) == 0){
                if (dbo.nextRecord()){
                    parametro = new Parametro();
                    parametro.setAcname(dbo.getFieldString("acname"));
                    parametro.setAcvalue(dbo.getFieldString("acvalue"));
                    parametro.setAcprofile(dbo.getFieldString("acprofile"));
                    parametro.setAcgroup(dbo.getFieldString("acgroup"));
                }
            }
            dbo.dbClose();
        } catch (Exception e){
            log.info("Se capturó una excepción al intentar buscar el parámetro "+param);
            log.info("Causa: "+e.getMessage()+" localizado en: "+e.getLocalizedMessage());
        }
        
        return parametro;
    }
    
    public boolean modificarParametro(Parametro parametro){
        log.info("modificarParametro()");
        boolean modificado=false;

        String query=modificarParametroQuery;
        query=query.replace("$ACNAME$", parametro.getAcname());
        query=query.replace("$ACVALUE$", parametro.getAcvalue());
        
        
        try{
            String propMigra = Utils.getConfig("oracleRegEx.properties").getProperty("paisOracle");
            Dbinterface dbo;
             if (txt.paisMigra(propMigra, pais)) {                       
                dbo=ds.get(ORACLE);
                log.info("Ejecutando ["+query+"]"+ORACLE);
             }else{
                dbo=ds.get(INFORMIX);
                log.info("Ejecutando ["+query+"]"+INFORMIX);
             }
            dbo.dbreset();
            
                if (dbo.executeQuery(query) != 0) {
                    log.error("modificarParametro: Error modificando parámetro "+parametro.getAcname()+" [" + query + "]");
                    modificado=false;
                } else {
                    log.info("modificarParametro: Modificación Exitosa "+parametro.getAcname()+" [" + query + "]");
                    modificado=true;
                }
            
            dbo.dbClose();
        }catch(Exception ex){
            log.info("modificarParametro: Se capturó una excepción al intentar ejecutar la consulta: ["+query+"]");
            log.info("Causa: "+ex.getMessage()+" localizado en: "+ex.getLocalizedMessage());
        }
        
        return modificado;
    }
    
    @Override
    public void closeConection() {
        this.shutdownDatabases();
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }
    
}
