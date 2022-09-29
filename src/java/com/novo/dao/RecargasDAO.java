/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.dao;

import com.novo.constants.BasicConfig;
import static com.novo.constants.BasicConfig.ANIO_RECONVERSION_VZLA;
import static com.novo.constants.BasicConfig.MES_RECONVERSION_VZLA;
import com.novo.constants.RecargasQuery;
import static com.novo.constants.temp.RecargasQueryINF.recargasPersonaJurMesVeQuery;
import static com.novo.constants.temp.RecargasQueryINF.recargasPersonaJurMesVeReconversionQuery;
import static com.novo.constants.temp.RecargasQueryINF.recargasPersonaJurMesVeReconversionUnionQuery;
import com.novo.database.Dbinterface;
import com.novo.database.NovoDAO;
import com.novo.objects.util.Utils;
import com.novo.util.DateUtil;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author jorojas
 */
public class RecargasDAO extends NovoDAO implements BasicConfig,RecargasQuery{
    private static Logger log = Logger.getLogger(RecargasDAO.class);
    private String pais;
    
    public RecargasDAO(String appName,String[] databases){
        super(appName,databases);
        pais="";
        log.info("RecargasDAO inicializado, sin configuración de país.");
    }
    
    public RecargasDAO(String appName,String[] databases,String pais){
        super(appName,databases,pais);
        this.pais=pais;
        log.info("RecargasDAO inicializado, país: "+pais);
    }
    
    /*
     * MONTO RECARGAS PERSONA NATURAL ACUMULADA(Oracle)
     */
    public BigDecimal obtenerMontoRecargasPersonaNatMes(Date fecha,String cod_transaccion){
        //Códigos 20 - 27
        BigDecimal monto=new BigDecimal("0.00");
        monto=monto.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        
        String day=""+fecha.getDate();
        String month=""+(fecha.getMonth()+1);
        String year=""+(fecha.getYear()+1900);
        
        month=DateUtil.rellenarCeros(month, 2);
        day=DateUtil.rellenarCeros(day, 2);
        
        String query="";
        
        switch (this.pais) {
            case co:
                query = "";
                break;
            case pe:
                {
                    Properties properties = Utils.getConfig("operaciones.properties");
                    query = recargasPersonaNatPeQuery
                            .replace("$BINES$", properties.getProperty("binesRecargaPersonaNatPe"));
                    break;
                }
            case ve:
                {
                    Properties properties = Utils.getConfig("operaciones.properties");
                    if(Integer.parseInt(year + month) > Integer.parseInt(ANIO_RECONVERSION_VZLA + MES_RECONVERSION_VZLA)){
                        query = recargasPersonaNatMesVeQuery;
                    }else if(Integer.parseInt(year + month) == Integer.parseInt(ANIO_RECONVERSION_VZLA + MES_RECONVERSION_VZLA)){
                        query = recargasPersonaNatMesVeReconversionUnionQuery
                                .replace("$DAY",  day);
                    }else{
                        query = recargasPersonaNatMesVeReconversionQuery;
                    }
                    
                    query = query.replace("$BINES$",  properties.getProperty("binesRecargaPersonaNatVe"));
                    
                    break;                    
                }
            case peusd:
                break;
            default:
                break;
        }
        
        query = query.replace("$FECHAINI", "01-"+month+"-"+year+" 00:00:00");
        query = query.replace("$FECHAFIN", day+"-"+month+"-"+year+" 23-59-59");
        query = query.replace("$FORMATO", "dd-MM-yyyy HH24:MI:SS");
        query = query.replace("$CODTRANSACCION", cod_transaccion);
        
        
        log.info("Ejecutando ["+query+"]");
        try{
            Dbinterface dbo=ds.get(oracle);
            dbo.dbreset();
        
            if (dbo.executeQuery(query) == 0){
                if (dbo.nextRecord()){
                    log.info("EL MONTO ES: ["+dbo.getFieldString("MONTO")+"]");
                    if (dbo.getFieldString("MONTO") != null && !"".equals(dbo.getFieldString("MONTO"))) {
                        monto=monto.add(new BigDecimal(dbo.getFieldString("MONTO")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
                    } else {
                        monto=monto.add(new BigDecimal("0.00").setScale(2,BigDecimal.ROUND_HALF_EVEN));
                    }
                }
            }
        } catch (Exception e){
            log.error("Se capturó una excepción al intentar obtener monto recargas persona natural del mes "+this.pais);
            log.error("Causa: "+e);
            e.printStackTrace();
        }
        
        return monto;
    }
    
    /*
     * MONTO RECARGAS PERSONA NATURAL DIA(Oracle)
     */
    public BigDecimal obtenerMontoRecargasPersonaNatDia(Date fecha,String cod_transaccion){
        BigDecimal monto=new BigDecimal("0.00");
        monto=monto.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        
        String day=""+fecha.getDate();
        String month=""+(fecha.getMonth()+1);
        String year=""+(fecha.getYear()+1900);
        
        month=DateUtil.rellenarCeros(month, 2);
        day=DateUtil.rellenarCeros(day, 2);
        
        String query="";
        
        if (this.pais.equals(co)){
            query = "";
        }else if(this.pais.equals(pe)){
            
            Properties properties = Utils.getConfig("operaciones.properties");
            query = recargasPersonaNatPeQuery
                    .replace("$BINES$", properties.getProperty("binesRecargaPersonaNatPe"));
            
        }else if(this.pais.equals(ve)){
            
            Properties properties = Utils.getConfig("operaciones.properties");
            query = recargasPersonaNatDiaVeQuery
                    .replace("$BINES$", properties.getProperty("binesRecargaPersonaNatVe"))
                    .replace("$YEAR", year)
                    .replace("$MONTH", month)
                    .replace("$DAY", day);
            
        }else if(this.pais.equals(peusd)){
            
        }
        
        query = query.replace("$FECHAINI", day+"-"+month+"-"+year+" 00:00:00");
        query = query.replace("$FECHAFIN", day+"-"+month+"-"+year+" 23-59-59");
        query = query.replace("$FORMATO", "dd-MM-yyyy HH24:MI:SS");
        query = query.replace("$CODTRANSACCION", cod_transaccion);
        
        
        log.info("Ejecutando ["+query+"]");
        try{
            Dbinterface dbo=ds.get(oracle);
            dbo.dbreset();
        
            if (dbo.executeQuery(query) == 0){
                if (dbo.nextRecord()){
                    log.info("EL MONTO ES: ["+dbo.getFieldString("MONTO")+"]");
                    if (dbo.getFieldString("MONTO") != null && !"".equals(dbo.getFieldString("MONTO"))) {
                        monto=monto.add(new BigDecimal(dbo.getFieldString("MONTO")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
                    } else {
                        monto=monto.add(new BigDecimal("0.00").setScale(2,BigDecimal.ROUND_HALF_EVEN));
                    }
                }
            }
        } catch (Exception e){
            log.error("Se capturó una excepción al intentar obtener monto recargas persona natural del dia "+this.pais);
            log.error("Causa: "+e);
            e.printStackTrace();
        }
        
        return monto;
    }
    
    /*
     * MONTO RECARGAS PERSONA JURIDICA DIA(oracle)
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
            Dbinterface dbo=ds.get(oracle);
            dbo.dbreset();
        
            if (dbo.executeQuery(query) == 0){
                if (dbo.nextRecord()){
                    log.info("EL MONTO ES: ["+dbo.getFieldString("MONTO")+"]");
                    if (dbo.getFieldString("MONTO") != null && !"".equals(dbo.getFieldString("MONTO"))) {
                        monto=monto.add(new BigDecimal(dbo.getFieldString("MONTO")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
                    } else {
                        monto=monto.add(new BigDecimal("0.00").setScale(2,BigDecimal.ROUND_HALF_EVEN));
                    }
                }
            }
        } catch (Exception e){
            log.error("Se capturó una excepción al intentar obtener monto recargas persona juridica del dia "+this.pais);
            log.error("Causa: "+e);
            e.printStackTrace();
        }
        
        return monto;
    }
    
    /*
     * MONTO RECARGAS PERSONA JURIDICA DIA(Oracle)
     */
    public BigDecimal obtenerMontoRecargasPersonaJurMes(Date fecha){
        BigDecimal monto=new BigDecimal("0.00");
        monto=monto.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        
        String day=""+fecha.getDate();
        String month=""+(fecha.getMonth()+1);
        String year=""+(fecha.getYear()+1900);
        
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
                            .replace("$DAY",  day);
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
            Dbinterface dbo=ds.get(oracle);
            dbo.dbreset();
        
            if (dbo.executeQuery(query) == 0){
                if (dbo.nextRecord()){
                    log.info("EL MONTO ES: ["+dbo.getFieldString("MONTO")+"]");
                    if (dbo.getFieldString("MONTO") != null && !"".equals(dbo.getFieldString("MONTO"))) {
                        monto=monto.add(new BigDecimal(dbo.getFieldString("MONTO")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
                    } else {
                        monto=monto.add(new BigDecimal("0.00").setScale(2,BigDecimal.ROUND_HALF_EVEN));
                    }
                }
            }
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
    
     /*
     * MONTO RECARGAS ABONOS MAESTRO DIA(Oracle)
     */
    public BigDecimal obtenerMontoRecargasAbonosMaestroDia(Date fecha,String cod_transaccion){
        BigDecimal monto = new BigDecimal("0.00");
        monto = monto.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        
        String day = "" + fecha.getDate();
        String month = "" + (fecha.getMonth()+1);
        String year = "" + (fecha.getYear()+1900);
        
        month = DateUtil.rellenarCeros(month, 2);
        day = DateUtil.rellenarCeros(day, 2);
        
        String query = "";
                
        if (this.pais.equals(co)){
            
            Properties properties = Utils.getConfig("operaciones.properties");
            query = recargasAbonosMaestroDiaCoQuery
                    .replace("$BINES$", properties.getProperty("binesRecargaMaestroCo"));
            
//        }else if(this.pais.equals(pe)){
//            //query = recargasPersonaNatPeQuery;
//        }else if(this.pais.equals(ve)){
//            //query = recargasPersonaNatVeQuery;
//        }else if(this.pais.equals(peusd)){
            
        }
        query = query.replace("$FECHA", day + "/" + month + "/" + year);
        query = query.replace("$CODTRANSACCION", cod_transaccion);
        
        
        log.info("Ejecutando [" + query + "]");
        try{
            Dbinterface dbo = ds.get(oracle);
            dbo.dbreset();
        
            if (dbo.executeQuery(query) == 0){
                if (dbo.nextRecord()){
                    log.info("EL MONTO ES: [" + dbo.getFieldString("MONTO") + "]");
                    if (dbo.getFieldString("MONTO") != null && !"".equals(dbo.getFieldString("MONTO"))) {
                        monto = monto.add(new BigDecimal(dbo.getFieldString("MONTO")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
                    } else {
                        monto = monto.add(new BigDecimal("0.00").setScale(2,BigDecimal.ROUND_HALF_EVEN));
                    }
                }
            }
        } catch (Exception e){
            log.error("Se capturó una excepción al intentar obtener monto recargas persona natural del dia " + this.pais);
            log.error("Causa: "+e);
            e.printStackTrace();
        }
        
        return monto;
    }
    
     
    /*
     * MONTO RECARGAS ABONOS MAESTRO ACUMULADA(Oracle)
     */
    public BigDecimal obtenerMontoRecargasAbonosMaestroMes(Date fecha,String cod_transaccion){
        //Códigos 20 - 27
        BigDecimal monto = new BigDecimal("0.00");
        monto = monto.setScale(2,BigDecimal.ROUND_HALF_EVEN);
        
        String day = "" + fecha.getDate();
        String month = "" + (fecha.getMonth() + 1);
        String year = "" + (fecha.getYear() + 1900);
        
        month = DateUtil.rellenarCeros(month, 2);
        day = DateUtil.rellenarCeros(day, 2);
        
        String query = "";
        
        if (this.pais.equals(co)){
            
            Properties properties = Utils.getConfig("operaciones.properties");
            query = recargasAbonosMaestroMesCoQuery
                    .replace("$BINES$", properties.getProperty("binesRecargaMaestroCo"));
            
        }else if(this.pais.equals(pe)){
            //query = recargasPersonaNatPeQuery;
        }else if(this.pais.equals(ve)){
            //query = recargasPersonaNatVeQuery; 
        }else if(this.pais.equals(peusd)){
            
        }
        
        query = query.replace("$FECHAINI", "01-" + month + "-" + year + " 00:00:00");
        query = query.replace("$FECHAFIN", day + "-" + month + "-" + year + " 23:59:59");
        query = query.replace("$FORMATO", "dd-MM-yyyy HH24:MI:SS");
        query = query.replace("$CODTRANSACCION", cod_transaccion);
        
        
        log.info("Ejecutando [" + query + "]");
        try{
            Dbinterface dbo = ds.get(oracle);
            dbo.dbreset();
        
            if (dbo.executeQuery(query) == 0){
                if (dbo.nextRecord()){
                    log.info("EL MONTO ES: [" + dbo.getFieldString("MONTO") + "]");
                    if (dbo.getFieldString("MONTO") != null && !"".equals(dbo.getFieldString("MONTO"))) {
                        monto = monto.add(new BigDecimal(dbo.getFieldString("MONTO")).setScale(2,BigDecimal.ROUND_HALF_EVEN));
                    } else {
                        monto = monto.add(new BigDecimal("0.00").setScale(2,BigDecimal.ROUND_HALF_EVEN));
                    }
                }
            }
        } catch (Exception e){
            log.error("Se capturó una excepción al intentar obtener monto recargas persona natural del mes " + this.pais);
            log.error("Causa: "+e);
            e.printStackTrace();
        }
        
        return monto;
    }
    
}
