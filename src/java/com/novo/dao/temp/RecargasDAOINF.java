/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.dao.temp;

import com.novo.constants.BasicConfig;
import com.novo.constants.temp.RecargasQueryINF;
import com.novo.database.Dbinterface;
import com.novo.database.NovoDAO;
import com.novo.util.DateUtil;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author jorojas
 */
public class RecargasDAOINF extends NovoDAO implements BasicConfig,RecargasQueryINF{
    private static Logger log = Logger.getLogger(RecargasDAOINF.class);
    private String pais;
    
    public RecargasDAOINF(String appName,String[] databases){
        super(appName,databases);
        pais="";
        log.info("RecargasDAO inicializado, sin configuración de país.");
    }
    
    public RecargasDAOINF(String appName,String[] databases,String pais){
        super(appName,databases,pais);
        this.pais=pais;
        log.info("RecargasDAO inicializado, país: "+pais);
    }
    
    /*
     * MONTO RECARGAS PERSONA JURIDICA DIA(Informix)
     */
    public BigDecimal obtenerMontoRecargasPersonaJurDia(Date fecha){
        BigDecimal monto=new BigDecimal("0.00");
        monto=monto.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        
        String day=""+fecha.getDate();
        String month=""+(fecha.getMonth()+1);
        String year=""+(fecha.getYear()+1900);
        
        month=DateUtil.rellenarCeros(month, 2);
        day=DateUtil.rellenarCeros(day, 2);
        
        String query = "";
        
        if (this.pais.equals(co)){
            query = recargasPersonaJurDiaCoQuery;
        }else if(this.pais.equals(pe)){
            query = recargasPersonaJurDiaPeQuery;
        }else if(this.pais.equals(ve)){
            query = recargasPersonaJurDiaVeQuery;
        }else if(this.pais.equals(peusd)){
            query = recargasPersonaJurDiaPeQuery;
        }
        
        
        query = query.replace("$DAY", day);
        query = query.replace("$MONTH", month);
        query = query.replace("$YEAR", year);
        
        
        log.info("Ejecutando ["+query+"]");
        try{
            Dbinterface dbi=ds.get(informix);
            dbi.dbreset();
        
            if (dbi.executeQuery(query) == 0){
                if (dbi.nextRecord()){
                    log.info("EL MONTO ES: ["+dbi.getFieldString("MONTO")+"]");
                    if (dbi.getFieldString("MONTO") != null && !"".equals(dbi.getFieldString("MONTO"))) {
                        monto=monto.add(new BigDecimal(dbi.getFieldString("MONTO")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
                    } else {
                        monto=monto.add(new BigDecimal("0.00").setScale(2,BigDecimal.ROUND_HALF_EVEN));
                    }
                }
            }
            //dbi.dbClose();
        } catch (Exception e){
            log.error("Se capturó una excepción al intentar obtener monto recargas persona juridica dia "+this.pais);
            log.error("Causa: "+e);
            e.printStackTrace();
        }
        
        return monto;
    }
    
    /*
     * MONTO RECARGAS PERSONA JURIDICA DIA(Informix)
     */
    public BigDecimal obtenerMontoRecargasPersonaJurMes(Date fecha){
        BigDecimal monto=new BigDecimal("0.00");
        monto=monto.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        
        String day=""+fecha.getDate();
        String month=""+(fecha.getMonth()+1);
        String year=""+(fecha.getYear()+1900);
        
        day=DateUtil.rellenarCeros(day, 2);
        month=DateUtil.rellenarCeros(month, 2);
        
        String query = "";
        
        switch (this.pais) {
            case co:
                query = recargasPersonaJurMesCoQuery;
                break;
            case pe:
                query = recargasPersonaJurMesPeQuery;
                break;
            case ve:
                if(Integer.parseInt(year + month) > Integer.parseInt(ANIO_RECONVERSION_VZLA + MES_RECONVERSION_VZLA)){
                    query = recargasPersonaJurMesVeQuery;                       
                }else if(Integer.parseInt(year + month) == Integer.parseInt(ANIO_RECONVERSION_VZLA + MES_RECONVERSION_VZLA)){
                    query = recargasPersonaJurMesVeReconversionUnionQuery
                            .replace("$DAY", day);
                }else{
                    query = recargasPersonaJurMesVeReconversionQuery;                    
                }
                break;
            case peusd:
                query = recargasPersonaJurMesPeQuery;
                break;
            default:
                break;
        }
        
        
        query = query.replace("$FECHAINI", year+"-"+month+"-01 00:00");
        query = query.replace("$FECHAFIN", year+"-"+month+"-"+day+" 23:59");
        
        
        log.info("Ejecutando ["+query+"]");
        try{
            Dbinterface dbi=ds.get(informix);
            dbi.dbreset();
        
            if (dbi.executeQuery(query) == 0){
                if (dbi.nextRecord()){
                    log.info("EL MONTO ES: ["+dbi.getFieldString("MONTO")+"]");
                    if (dbi.getFieldString("MONTO") != null && !"".equals(dbi.getFieldString("MONTO"))) {
                        monto=monto.add(new BigDecimal(dbi.getFieldString("MONTO")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
                    } else {
                        monto=monto.add(new BigDecimal("0.00").setScale(2,BigDecimal.ROUND_HALF_EVEN));
                    }
                }
            }
            //dbi.dbClose();
        } catch (Exception e){
            log.error("Se capturó una excepción al intentar obtener monto recargas persona juridica mes "+this.pais);
            log.error("Causa: "+e.getMessage()+" localizado en: "+e.getLocalizedMessage());
            e.printStackTrace();
        }
        
        return monto;
    }
    
    
    @Override
    public void closeConection() {
        this.shutdownDatabases();
    }
    
}
