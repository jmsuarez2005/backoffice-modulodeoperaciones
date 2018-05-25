/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.dao.temp;

import com.novo.constants.BasicConfig;
import com.novo.constants.temp.ParametrosQueryINF;
import com.novo.database.Dbinterface;
import com.novo.database.NovoDAO;
import com.novo.model.Parametro;
import org.apache.log4j.Logger;

/**
 *
 * @author jorojas
 */
public class ParametrosDAOINF extends NovoDAO implements BasicConfig,ParametrosQueryINF{
    private static Logger log = Logger.getLogger(ParametrosDAOINF.class);
    private String pais;
    
    public ParametrosDAOINF(String appName,String[] databases){
        super(appName,databases);
        this.pais = "";
        log.info("ParametrosDAO inicializado, sin configuración de país.");
    }
    
    public ParametrosDAOINF(String appName,String[] databases,String pais){
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
            Dbinterface dbi=ds.get(INFORMIX);
            dbi.dbreset();

            if (dbi.executeQuery(query) == 0){
                if (dbi.nextRecord()){
                    parametro = new Parametro();
                    parametro.setAcname(dbi.getFieldString("acname"));
                    parametro.setAcvalue(dbi.getFieldString("acvalue"));
                    parametro.setAcprofile(dbi.getFieldString("acprofile"));
                    parametro.setAcgroup(dbi.getFieldString("acgroup"));
                }
            }
            dbi.dbClose();
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
        
        log.info("Ejecutando ["+query+"]"+INFORMIX);
        
        try{
            Dbinterface dbi=ds.get(INFORMIX);
            dbi.dbreset();
            
                if (dbi.executeQuery(query) != 0) {
                    log.error("modificarParametro: Error modificando parametro "+parametro.getAcname()+" [" + query + "]");
                    modificado=false;
                } else {
                    log.info("modificarParametro: Modificación Exitosa "+parametro.getAcname()+" [" + query + "]");
                    modificado=true;
                }
            
            dbi.dbClose();
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
