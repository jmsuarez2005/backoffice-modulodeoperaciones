/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.process;

import com.novo.constants.BasicConfig;
import com.novo.dao.EmisionesDAO;
import com.novo.dao.ParametrosDAO;
import com.novo.dao.RecargasDAO;
import com.novo.model.Parametro;
import com.novo.model.ReporteEmisionRecarga;
import com.novo.model.UsuarioSesion;
import com.novo.util.DateUtil;
import com.novo.util.ExcelUtil;
import com.novo.util.SendMail;
import com.novo.util.TextUtil;
import com.novo.util.Utils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;

/**
 *
 * @author jorojas
 */
public class ReporteActividadDiariaProc implements BasicConfig {
    private static Logger log = Logger.getLogger(ReporteActividadDiariaProc.class);
    
    private Date fecha;
    private Map<String,ReporteEmisionRecarga> reporteCo;
    private Map<String,ReporteEmisionRecarga> reportePe;
    private Map<String,ReporteEmisionRecarga> reporteVe;
    private ReporteEmisionRecarga totales;
    private UsuarioSesion usuarioSesion;
    private BigDecimal cambioBsDolar,cambioSolesDolar,cambioPesosDolar;
    
    public ReporteActividadDiariaProc(Date date,UsuarioSesion user) {
        this.fecha = date;
        this.reporteCo = new HashMap();
        this.reportePe = new HashMap();
        this.reporteVe = new HashMap();
        this.totales = new ReporteEmisionRecarga();
        this.usuarioSesion = user;
        this.cambioBsDolar = new BigDecimal(ReporteActividadDiariaProc.obtenerCambioMoneda(ve)).setScale(2,BigDecimal.ROUND_HALF_EVEN);  
        this.cambioSolesDolar = new BigDecimal(ReporteActividadDiariaProc.obtenerCambioMoneda(pe)).setScale(2,BigDecimal.ROUND_HALF_EVEN);
        this.cambioPesosDolar = new BigDecimal(ReporteActividadDiariaProc.obtenerCambioMoneda(co)).setScale(2,BigDecimal.ROUND_HALF_EVEN);
    }
    
    public ReporteActividadDiariaProc(){
        this.cambioBsDolar = new BigDecimal(ReporteActividadDiariaProc.obtenerCambioMoneda(ve)).setScale(2,BigDecimal.ROUND_HALF_EVEN);  
        this.cambioSolesDolar = new BigDecimal(ReporteActividadDiariaProc.obtenerCambioMoneda(pe)).setScale(2,BigDecimal.ROUND_HALF_EVEN);
        this.cambioPesosDolar = new BigDecimal(ReporteActividadDiariaProc.obtenerCambioMoneda(co)).setScale(2,BigDecimal.ROUND_HALF_EVEN);
    }
    
    public Map obtenerEmisionRecargaVenezuela(){ 
        EmisionesDAO emisionesNatDAO,emisionesJurDAO;
        RecargasDAO recargasNatDAO,recargasJurDAO;
        
        ReporteEmisionRecarga natural = new ReporteEmisionRecarga();
        ReporteEmisionRecarga juridica = new ReporteEmisionRecarga();
        ReporteEmisionRecarga totalesVE = new ReporteEmisionRecarga();    
        
        //Consultar Persona Natural en BD
        natural.setProducto("Persona Natural");
        //emisionesNatDAO = new EmisionesDAO(appName,databases,ve);
        emisionesNatDAO = new EmisionesDAO(appName,dbOracle,ve);
        natural.setEmisionesFecha(emisionesNatDAO.obtenerTarjEmitidasPersonaNatDia(fecha));
        natural.setEmisionesAcum(emisionesNatDAO.obtenerTarjEmitidasPersonaNatMes(fecha));
        emisionesNatDAO.closeConection(); //Cierre de conexion
        
        //recargasNatDAO = new RecargasDAO(appName,databases,ve);
        recargasNatDAO = new RecargasDAO(appName,dbOracle,ve);
        natural.setRecargasDiaLocal(recargasNatDAO.obtenerMontoRecargasPersonaNatDia(fecha,"20").subtract(recargasNatDAO.obtenerMontoRecargasPersonaNatDia(fecha,"27")));
        natural.setRecargasAcumLocal(recargasNatDAO.obtenerMontoRecargasPersonaNatMes(fecha,"20").subtract(recargasNatDAO.obtenerMontoRecargasPersonaNatMes(fecha,"27")));
        natural.setRecargasDiaDolares(natural.getRecargasDiaLocal().divide(cambioBsDolar,2,RoundingMode.HALF_UP));
        natural.setRecargasAcumDolares(natural.getRecargasAcumLocal().divide(cambioBsDolar,2,RoundingMode.HALF_UP));
        recargasNatDAO.closeConection(); //Cierre de conexion
        
        
        //Consultar Persona Juridica en BD
        juridica.setProducto("Persona Jurídica");
//        emisionesJurDAO = new EmisionesDAO(appName,databases,ve); 
        emisionesJurDAO = new EmisionesDAO(appName,dbOracle,ve);  
        juridica.setEmisionesFecha(emisionesJurDAO.obtenerTarjEmitidasPersonaJurDia(fecha));
        juridica.setEmisionesAcum(emisionesJurDAO.obtenerTarjEmitidasPersonaJurMes(fecha));
        emisionesJurDAO.closeConection(); //Cierre de conexion
        
//        recargasJurDAO = new RecargasDAO(appName,databases,ve);
        recargasJurDAO = new RecargasDAO(appName,dbOracle,ve);
        juridica.setRecargasDiaLocal(recargasJurDAO.obtenerMontoRecargasPersonaJurDia(fecha));
        juridica.setRecargasAcumLocal(recargasJurDAO.obtenerMontoRecargasPersonaJurMes(fecha));
        juridica.setRecargasDiaDolares(juridica.getRecargasDiaLocal().divide(cambioBsDolar,2,RoundingMode.HALF_UP));
        juridica.setRecargasAcumDolares(juridica.getRecargasAcumLocal().divide(cambioBsDolar,2,RoundingMode.HALF_UP));
        recargasJurDAO.closeConection(); //Cierre de conexion
        
        
        //Calcular Totales
        totalesVE.setProducto("Totales");
        totalesVE.setEmisionesFecha(natural.getEmisionesFecha()+juridica.getEmisionesFecha());
        totalesVE.setEmisionesAcum(natural.getEmisionesAcum()+juridica.getEmisionesAcum());
        totalesVE.setRecargasDiaLocal(natural.getRecargasDiaLocal().add(juridica.getRecargasDiaLocal()));
        totalesVE.setRecargasAcumLocal(natural.getRecargasAcumLocal().add(juridica.getRecargasAcumLocal()));
        totalesVE.setRecargasDiaDolares(natural.getRecargasDiaDolares().add(juridica.getRecargasDiaDolares()));
        totalesVE.setRecargasAcumDolares(natural.getRecargasAcumDolares().add(juridica.getRecargasAcumDolares()));
        
        
        //Calcular %Rep
        totalesVE.setPorcRep(100.00);
        if(totalesVE.getRecargasAcumDolares().doubleValue()!=0){
            natural.setPorcRep(Double.valueOf(natural.getRecargasAcumDolares().divide(totalesVE.getRecargasAcumDolares(),2,RoundingMode.HALF_UP).toPlainString())*100);
            juridica.setPorcRep(Double.valueOf(juridica.getRecargasAcumDolares().divide(totalesVE.getRecargasAcumDolares(),2,RoundingMode.HALF_UP).toPlainString())*100);
        }else{
            natural.setPorcRep(0.00);
            juridica.setPorcRep(0.00);
        }
        totalesVE.setPorcRep(natural.getPorcRep()+juridica.getPorcRep());
        
        this.reporteVe.put("natural", natural);
        this.reporteVe.put("juridica", juridica);
        this.reporteVe.put("totales", totalesVE);
        
        return this.reporteVe;     
    }
    
    public Map obtenerEmisionRecargaColombia(){
        EmisionesDAO emisionesJurDAO;
        RecargasDAO recargasJurDAO;
        RecargasDAO recargasMaestroDAO;
        
        ReporteEmisionRecarga juridica = new ReporteEmisionRecarga();
        ReporteEmisionRecarga maestro = new ReporteEmisionRecarga();
        ReporteEmisionRecarga totalesCO = new ReporteEmisionRecarga();
        
        //Consultar Persona Juridica en BD
        juridica.setProducto("Persona Jurídica");
//        emisionesJurDAO = new EmisionesDAO(appName,databases,co);
        emisionesJurDAO = new EmisionesDAO(appName,dbOracle,co);
        juridica.setEmisionesFecha(emisionesJurDAO.obtenerTarjEmitidasPersonaJurDia(fecha));
        juridica.setEmisionesAcum(emisionesJurDAO.obtenerTarjEmitidasPersonaJurMes(fecha));
        emisionesJurDAO.closeConection(); //Cierre de conexion
        
//        recargasJurDAO = new RecargasDAO(appName,databases,co);
        recargasJurDAO = new RecargasDAO(appName,dbOracle,co);
        juridica.setRecargasDiaLocal(recargasJurDAO.obtenerMontoRecargasPersonaJurDia(fecha));
        juridica.setRecargasAcumLocal(recargasJurDAO.obtenerMontoRecargasPersonaJurMes(fecha));
        juridica.setRecargasDiaDolares(juridica.getRecargasDiaLocal().divide(cambioPesosDolar,2,RoundingMode.HALF_UP));
        System.out.println("dividir;" + juridica.getRecargasAcumLocal() + " " + cambioPesosDolar);
        juridica.setRecargasAcumDolares(juridica.getRecargasAcumLocal().divide(cambioPesosDolar,2,RoundingMode.HALF_UP));
        recargasJurDAO.closeConection(); //Cierre de conexion
        
        //Consultar Abonos Maestro en BD
        maestro.setProducto("Abonos Maestro");
//        recargasMaestroDAO = new RecargasDAO(appName, databases, co);
        recargasMaestroDAO = new RecargasDAO(appName, dbOracle, co);
        maestro.setRecargasDiaLocal(recargasMaestroDAO.obtenerMontoRecargasAbonosMaestroDia(fecha,"20"));
        maestro.setRecargasAcumLocal(recargasMaestroDAO.obtenerMontoRecargasAbonosMaestroMes(fecha,"20"));
        maestro.setRecargasDiaDolares(maestro.getRecargasDiaLocal().divide(cambioPesosDolar, 2, RoundingMode.HALF_UP));
        maestro.setRecargasAcumDolares(maestro.getRecargasAcumLocal().divide(cambioPesosDolar, 2, RoundingMode.HALF_UP));
        recargasMaestroDAO.closeConection(); //Cierre de conexion
        
        //Calcular Totales
        totalesCO.setProducto("Totales");
        totalesCO.setEmisionesFecha(juridica.getEmisionesFecha());
        totalesCO.setEmisionesAcum(juridica.getEmisionesAcum());
        totalesCO.setRecargasDiaLocal(juridica.getRecargasDiaLocal().add(maestro.getRecargasDiaLocal()));
        totalesCO.setRecargasAcumLocal(juridica.getRecargasAcumLocal().add(maestro.getRecargasAcumLocal()));
        totalesCO.setRecargasDiaDolares(juridica.getRecargasDiaDolares().add(maestro.getRecargasDiaDolares()));
        totalesCO.setRecargasAcumDolares(juridica.getRecargasAcumDolares().add(maestro.getRecargasAcumDolares()));   
        
        totalesCO.setPorcRep(100.00);
        if(totalesCO.getRecargasAcumDolares().doubleValue()!=0){
            juridica.setPorcRep(Double.valueOf(juridica.getRecargasAcumDolares().divide(totalesCO.getRecargasAcumDolares(),2,RoundingMode.HALF_UP).toPlainString())*100);
            maestro.setPorcRep(Double.valueOf(maestro.getRecargasAcumDolares().divide(totalesCO.getRecargasAcumDolares(),2,RoundingMode.HALF_UP).toPlainString())*100);
        }else{
            juridica.setPorcRep(0.00);
            maestro.setPorcRep(0.00);
        }
        
        //Calcular %Rep
        totalesCO.setPorcRep(juridica.getPorcRep() + maestro.getPorcRep());
        
        
        this.reporteCo.put("juridica", juridica);
        this.reporteCo.put("maestro", maestro);
        this.reporteCo.put("totales", totalesCO);
        
        return this.reporteCo;
    }
    
    public Map obtenerEmisionRecargaPeru(String montoComedores){
        EmisionesDAO emisionesNatDAO,emisionesJurDAO,emisionesUSDAO;
        RecargasDAO recargasNatDAO,recargasJurDAO,recargasUSDAO;
        
        ReporteEmisionRecarga natural = new ReporteEmisionRecarga();
        ReporteEmisionRecarga juridica = new ReporteEmisionRecarga();
        ReporteEmisionRecarga juridicaUS = new ReporteEmisionRecarga();
        ReporteEmisionRecarga comedores = this.obtenerComedoresPe(montoComedores);
        
        ReporteEmisionRecarga totalesPE = new ReporteEmisionRecarga();
        
        //Consultar Persona Natural en BD
        natural.setProducto("Persona Natural");
//        emisionesNatDAO = new EmisionesDAO(appName,databases,pe);
        emisionesNatDAO = new EmisionesDAO(appName,dbOracle,pe);
        natural.setEmisionesFecha(emisionesNatDAO.obtenerTarjEmitidasPersonaNatDia(fecha));
        natural.setEmisionesAcum(emisionesNatDAO.obtenerTarjEmitidasPersonaNatMes(fecha));
        emisionesNatDAO.closeConection(); //Cierre de conexion
        
//        recargasNatDAO = new RecargasDAO(appName,databases,pe);
        recargasNatDAO = new RecargasDAO(appName,dbOracle,pe);
        natural.setRecargasDiaLocal(recargasNatDAO.obtenerMontoRecargasPersonaNatDia(fecha,"20").subtract(recargasNatDAO.obtenerMontoRecargasPersonaNatDia(fecha,"27")));
        natural.setRecargasAcumLocal(recargasNatDAO.obtenerMontoRecargasPersonaNatMes(fecha,"20").subtract(recargasNatDAO.obtenerMontoRecargasPersonaNatMes(fecha,"27")));
        
        System.out.println("dividir;" + natural.getRecargasDiaLocal() + " " + cambioSolesDolar);
        natural.setRecargasDiaDolares(natural.getRecargasDiaLocal().divide(cambioSolesDolar,2,RoundingMode.HALF_UP));
        natural.setRecargasAcumDolares(natural.getRecargasAcumLocal().divide(cambioSolesDolar,2,RoundingMode.HALF_UP));
        recargasNatDAO.closeConection(); //Cierre de conexion
        
        
        //Consultar Persona Juridica en BD
        juridica.setProducto("Persona Jurídica");
//        emisionesJurDAO = new EmisionesDAO(appName,databases,pe);
        emisionesJurDAO = new EmisionesDAO(appName,dbOracle,pe);
        juridica.setEmisionesFecha(emisionesJurDAO.obtenerTarjEmitidasPersonaJurDia(fecha));
        juridica.setEmisionesAcum(emisionesJurDAO.obtenerTarjEmitidasPersonaJurMes(fecha));
        emisionesJurDAO.closeConection(); //Cierre de conexion
        
//        recargasJurDAO = new RecargasDAO(appName,databases,pe);
        recargasJurDAO = new RecargasDAO(appName,dbOracle,pe);
        juridica.setRecargasDiaLocal(recargasJurDAO.obtenerMontoRecargasPersonaJurDia(fecha));
        juridica.setRecargasAcumLocal(recargasJurDAO.obtenerMontoRecargasPersonaJurMes(fecha));
        juridica.setRecargasDiaDolares(juridica.getRecargasDiaLocal().divide(cambioSolesDolar,2,RoundingMode.HALF_UP));
        juridica.setRecargasAcumDolares(juridica.getRecargasAcumLocal().divide(cambioSolesDolar,2,RoundingMode.HALF_UP));
        recargasJurDAO.closeConection(); //Cierre de conexion
        
        
        //Consultar Persona Juridica US$ en BD
        juridicaUS.setProducto("Persona Jurídica US$");
//        emisionesUSDAO = new EmisionesDAO(appName,databases,peusd);
        emisionesUSDAO = new EmisionesDAO(appName,dbOracle,peusd);
        juridicaUS.setEmisionesFecha(emisionesUSDAO.obtenerTarjEmitidasPersonaJurDia(fecha));
        juridicaUS.setEmisionesAcum(emisionesUSDAO.obtenerTarjEmitidasPersonaJurMes(fecha));
        emisionesUSDAO.closeConection(); //Cierre de conexion
        
//        recargasUSDAO = new RecargasDAO(appName,databases,peusd);
        recargasUSDAO = new RecargasDAO(appName,dbOracle,peusd);
        juridicaUS.setRecargasDiaLocal(recargasUSDAO.obtenerMontoRecargasPersonaJurDia(fecha));
        juridicaUS.setRecargasAcumLocal(recargasUSDAO.obtenerMontoRecargasPersonaJurMes(fecha));
        juridicaUS.setRecargasDiaDolares(juridicaUS.getRecargasDiaLocal());
        juridicaUS.setRecargasAcumDolares(juridicaUS.getRecargasAcumLocal());
        recargasUSDAO.closeConection(); //Cierre de conexion
        
        //Calcular Totales
        totalesPE.setProducto("Totales");
        totalesPE.setEmisionesFecha(natural.getEmisionesFecha()+juridica.getEmisionesFecha()+juridicaUS.getEmisionesFecha()+comedores.getEmisionesFecha());
        totalesPE.setEmisionesAcum(natural.getEmisionesAcum()+juridica.getEmisionesAcum()+juridicaUS.getEmisionesAcum()+comedores.getEmisionesAcum());
        //totalesPE.setRecargasDiaLocal(natural.getRecargasDiaLocal().add(juridica.getRecargasDiaLocal()).add(juridicaUS.getRecargasDiaLocal()).add(comedores.getRecargasDiaLocal()));
        //totalesPE.setRecargasAcumLocal(natural.getRecargasAcumLocal().add(juridica.getRecargasAcumLocal()).add(juridicaUS.getRecargasAcumLocal()).add(comedores.getRecargasAcumLocal()));
        totalesPE.setRecargasDiaDolares(natural.getRecargasDiaDolares().add(juridica.getRecargasDiaDolares()).add(juridicaUS.getRecargasDiaDolares()).add(comedores.getRecargasDiaDolares()));
        totalesPE.setRecargasAcumDolares(natural.getRecargasAcumDolares().add(juridica.getRecargasAcumDolares()).add(juridicaUS.getRecargasAcumDolares()).add(comedores.getRecargasAcumDolares()));
        
        
        //Calcular %Rep
        totalesPE.setPorcRep(100.00);
        if(totalesPE.getRecargasAcumDolares().doubleValue()!=0){
            natural.setPorcRep(Double.valueOf(natural.getRecargasAcumDolares().divide(totalesPE.getRecargasAcumDolares(),2,RoundingMode.HALF_UP).toPlainString())*100);
            juridica.setPorcRep(Double.valueOf(juridica.getRecargasAcumDolares().divide(totalesPE.getRecargasAcumDolares(),2,RoundingMode.HALF_UP).toPlainString())*100);
            juridicaUS.setPorcRep(Double.valueOf(juridicaUS.getRecargasAcumDolares().divide(totalesPE.getRecargasAcumDolares(),2,RoundingMode.HALF_UP).toPlainString())*100);
            comedores.setPorcRep(Double.valueOf(comedores.getRecargasAcumDolares().divide(totalesPE.getRecargasAcumDolares(),2,RoundingMode.HALF_UP).toPlainString())*100);
        }else{
            natural.setPorcRep(0.00);
            juridica.setPorcRep(0.00);
            juridicaUS.setPorcRep(0.00);
            comedores.setPorcRep(0.00);
        }
        totalesPE.setPorcRep(natural.getPorcRep()+juridica.getPorcRep()+juridicaUS.getPorcRep()+comedores.getPorcRep());
        
        this.reportePe.put("natural", natural);
        this.reportePe.put("juridica", juridica);
        this.reportePe.put("juridicaUS", juridicaUS);
        this.reportePe.put("comedores", comedores);
        this.reportePe.put("totales", totalesPE);
        
        return this.reportePe;
    }
    
    public ReporteEmisionRecarga obtenerComedoresPe(String montoComedores){
        log.info("obtenerComedoresPe()");
        ReporteEmisionRecarga reporte = new ReporteEmisionRecarga();
        reporte.setProducto("Comedores");
        
        if(!montoComedores.equals("0.00")){
            try{
                reporte = new ReporteEmisionRecarga();
                reporte.setProducto("Comedores");
                reporte.setRecargasDiaLocal(new BigDecimal(montoComedores).setScale(2,BigDecimal.ROUND_HALF_EVEN));
                reporte.setRecargasAcumLocal(new BigDecimal(montoComedores).setScale(2,BigDecimal.ROUND_HALF_EVEN));
                reporte.setRecargasDiaDolares(reporte.getRecargasDiaLocal().divide(cambioSolesDolar,2,RoundingMode.HALF_UP));
                reporte.setRecargasAcumDolares(reporte.getRecargasDiaLocal().divide(cambioSolesDolar,2,RoundingMode.HALF_UP));
            }
            catch(Exception e){
                log.error("El monto ingresado ["+montoComedores+"] es inválido.");
                log.error(e.getMessage());
            }
        }
        
        return reporte;
    }
    
    public ReporteEmisionRecarga obtenerTotales(){
        this.totales.setPais("Global");
        this.totales.setProducto("Totales");
        Map<String,Map<String,ReporteEmisionRecarga> > paises = new HashMap();
        paises.put("Co", this.reporteCo);
        paises.put("Pe", this.reportePe);
        paises.put("Ve", this.reporteVe);
        
        
        for (Map.Entry<String,Map<String,ReporteEmisionRecarga> > pais: paises.entrySet()){
            
            for (Map.Entry<String,ReporteEmisionRecarga> entry: pais.getValue().entrySet()){
                if (entry.getValue().getProducto().equals("Totales")!=true){
                    this.totales.setEmisionesFecha(totales.getEmisionesFecha()+entry.getValue().getEmisionesFecha());
                    this.totales.setEmisionesAcum(totales.getEmisionesAcum()+entry.getValue().getEmisionesAcum());
                    //this.totales.setRecargasDiaLocal(totales.getRecargasDiaLocal().add(entry.getValue().getRecargasDiaLocal()));
                    this.totales.setRecargasDiaDolares(totales.getRecargasDiaDolares().add(entry.getValue().getRecargasDiaDolares()));
                    //this.totales.setRecargasAcumLocal(totales.getRecargasAcumLocal().add(entry.getValue().getRecargasAcumLocal()));
                    this.totales.setRecargasAcumDolares(totales.getRecargasAcumDolares().add(entry.getValue().getRecargasAcumDolares()));
                }
            }
            
        }
        
         //%Rep Globales  
            for (Map.Entry<String,Map<String,ReporteEmisionRecarga> > pais: paises.entrySet()){
                for (Map.Entry<String,ReporteEmisionRecarga> entry: pais.getValue().entrySet()){
                    if(totales.getRecargasAcumDolares().doubleValue()!=0){
                        entry.getValue().setPorcRepGlobal(Double.valueOf(entry.getValue().getRecargasAcumDolares().divide(this.totales.getRecargasAcumDolares(),5,RoundingMode.HALF_UP).toPlainString())*100);
                    }else{
                        entry.getValue().setPorcRepGlobal(0.00);
                    }
                    
                    if (entry.getValue().getProducto().equals("Totales")!=true){
                        this.totales.setPorcRep(this.totales.getPorcRep()+entry.getValue().getPorcRepGlobal());
                        this.totales.setPorcRepGlobal(this.totales.getPorcRepGlobal()+entry.getValue().getPorcRepGlobal());
                        log.info("Agregando "+pais.getKey()+" "+entry.getValue().getProducto()+" Valor["+entry.getValue().getPorcRepGlobal()+"]");
                    }
                }
            }
        
        
        return this.totales;
    }
    
    public HSSFWorkbook crearWorkBookExcel() {
        HSSFWorkbook myWorkBook = new HSSFWorkbook();
        HSSFSheet mySheet = myWorkBook.createSheet();
        mySheet.setGridsPrinted(false);
        String[] nombreProductosPe = {"natural","juridica","juridicaUS","comedores"};
        String[] nombreProductosCo = {"juridica","maestro"};
        String[] nombreProductosVe = {"natural","juridica"};
        
        try{
            ExcelUtil.crearTitulo(myWorkBook, mySheet, "Reporte de Actividad Diaria, "+DateUtil.formatYYYYMMDD(fecha, "DD/MM/YYYY"),0);
            
            this.crearFilaCambiosMonedaExcel(myWorkBook, mySheet, 3, totales);
        
            this.crearEncabezado(myWorkBook, mySheet, 6);
            this.llenarExcelPais(myWorkBook,mySheet,9,"Venezuela",this.reporteVe,nombreProductosVe,true);
            this.llenarExcelPais(myWorkBook,mySheet,11,"Perú",this.reportePe,nombreProductosPe,true);
            this.llenarExcelPais(myWorkBook,mySheet,15,"Colombia",this.reporteCo,nombreProductosCo,true);       
            this.crearFilaTotalesExcel(myWorkBook, mySheet, 17, this.totales);
            
            this.crearEncabezado(myWorkBook, mySheet, 21);
            this.llenarExcelPais(myWorkBook,mySheet,24,"Venezuela",this.reporteVe,nombreProductosVe,false);
            this.crearFilaTotalesExcel(myWorkBook, mySheet, 26, this.reporteVe.get("totales"));
                  
            this.crearEncabezado(myWorkBook, mySheet, 30);
            this.llenarExcelPais(myWorkBook,mySheet,33,"Perú",this.reportePe,nombreProductosPe,false);
            this.crearFilaTotalesExcel(myWorkBook, mySheet, 37, this.reportePe.get("totales"));
            
            this.crearEncabezado(myWorkBook, mySheet, 41);
            this.llenarExcelPais(myWorkBook,mySheet,44,"Colombia",this.reporteCo,nombreProductosCo,false);
            this.crearFilaTotalesExcel(myWorkBook, mySheet, 46, this.reporteCo.get("totales"));              
            
        } catch(Exception ex){
            log.error(ex);
            ex.printStackTrace();
        }
        
        return myWorkBook;
    }
    
    private void crearEncabezado(HSSFWorkbook myWorkBook,HSSFSheet mySheet,int rowIndex){
        int index = rowIndex;
        
        HSSFRow row1 = mySheet.createRow(index);   //Fila 1
               
        ExcelUtil.crearCeldaEncabezado(row1, 3, myWorkBook, mySheet, "Emisiones");
        ExcelUtil.setEstiloEncabezado(myWorkBook, mySheet, row1.createCell(4));
        mySheet.addMergedRegion(new CellRangeAddress(index,index,3,4));
        
        ExcelUtil.crearCeldaEncabezado(row1, 5, myWorkBook, mySheet, "Recargas");
        mySheet.addMergedRegion(new CellRangeAddress(index,index,5,9));       
        
        for (int i=6;i<=9;i++) {
            ExcelUtil.setEstiloEncabezado(myWorkBook, mySheet, row1.createCell(i));
        }
        
        index=index+1; //Apuntando a la siguiente fila       
        HSSFRow row2 = mySheet.createRow(index); //Fila 2
        
        ExcelUtil.crearCeldaEncabezado(row2, 1, myWorkBook, mySheet, "País");
        mySheet.addMergedRegion(new CellRangeAddress(index,index+1,1,1));

        ExcelUtil.crearCeldaEncabezado(row2, 2, myWorkBook, mySheet, "Producto");
        mySheet.addMergedRegion(new CellRangeAddress(index,index+1,2,2));
        
        ExcelUtil.crearCeldaEncabezado(row2, 3, myWorkBook, mySheet, "Día "+DateUtil.format("d/MM/YYYY", fecha));
        mySheet.addMergedRegion(new CellRangeAddress(index,index+1,3,3));
        
        ExcelUtil.crearCeldaEncabezado(row2, 4, myWorkBook, mySheet, "Acumulada "+DateUtil.format("MM/YYYY", fecha));
        mySheet.addMergedRegion(new CellRangeAddress(index,index+1,4,4));
        
        ExcelUtil.crearCeldaEncabezado(row2, 5, myWorkBook, mySheet, "Día "+DateUtil.format("dd/MM/YYYY", fecha));
        mySheet.addMergedRegion(new CellRangeAddress(index,index,5,6));
        
        ExcelUtil.crearCeldaEncabezado(row2, 7, myWorkBook, mySheet, "Acumulada "+DateUtil.format("MM/YYYY", fecha));
        mySheet.addMergedRegion(new CellRangeAddress(index,index,7,9));
        ExcelUtil.setEstiloEncabezado(myWorkBook, mySheet, row2.createCell(9));
        
        index=index+1;//Apuntando a la siguiente fila
        HSSFRow row3 = mySheet.createRow(index); //Fila 3
        
        ExcelUtil.crearCeldaEncabezado(row3, 5, myWorkBook, mySheet, "Moneda Local");
        ExcelUtil.crearCeldaEncabezado(row3, 6, myWorkBook, mySheet, "US$");
        ExcelUtil.crearCeldaEncabezado(row3, 7, myWorkBook, mySheet, "Moneda Local");
        ExcelUtil.crearCeldaEncabezado(row3, 8, myWorkBook, mySheet, "US$");
        ExcelUtil.crearCeldaEncabezado(row3, 9, myWorkBook, mySheet, "%Rep");
        
        for (int i=1;i<=4;i++) {
            ExcelUtil.setEstiloEncabezado(myWorkBook, mySheet, row3.createCell(i));
        }
        
        for (int i=0;i<12;i++){
            mySheet.setColumnWidth(i, 4500);
        }
    }
    
    private void llenarExcelPais(HSSFWorkbook myWorkBook,HSSFSheet mySheet,int rowIndex,String pais,Map<String,ReporteEmisionRecarga> productos,String[] nombreProductos,boolean global){
            
        int index = rowIndex;
        
        List<ReporteEmisionRecarga> listaPais = new ArrayList();
        for(int i=0;i<nombreProductos.length;i++) {
            listaPais.add(productos.get(nombreProductos[i]));
        }
        
        Iterator iteratorPeru = listaPais.iterator();
        
        while (iteratorPeru.hasNext()){
            ReporteEmisionRecarga reporte = (ReporteEmisionRecarga)iteratorPeru.next();
            
            HSSFRow row = mySheet.createRow(index);
            ExcelUtil.crearCeldaBold(row, 1, myWorkBook, mySheet, pais);
            ExcelUtil.crearCeldaBold(row, 2, myWorkBook, mySheet, reporte.getProducto());
            ExcelUtil.crearCelda(row, 3, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getEmisionesFecha())+"");
            ExcelUtil.crearCelda(row, 4, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getEmisionesAcum())+"");
            ExcelUtil.crearCelda(row, 5, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getRecargasDiaLocal())+"");
            ExcelUtil.crearCelda(row, 6, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getRecargasDiaDolares())+"");
            ExcelUtil.crearCelda(row, 7, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getRecargasAcumLocal())+"");
            ExcelUtil.crearCelda(row, 8, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getRecargasAcumDolares())+"");
            if (global){
                ExcelUtil.crearCelda(row, 9, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getPorcRepGlobal())+" %");
            }else{
                ExcelUtil.crearCelda(row, 9, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getPorcRep())+" %");
            }
            
            index++;
        }
        mySheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex+listaPais.size()-1,1,1));
        
        
    }
    
    public void crearFilaTotalesExcel(HSSFWorkbook myWorkBook,HSSFSheet mySheet,int rowIndex,ReporteEmisionRecarga reporte){
        HSSFRow rowTotales = mySheet.createRow(rowIndex);
        ExcelUtil.crearCeldaBold(rowTotales, 1, myWorkBook, mySheet, "Totales");
        ExcelUtil.crearCeldaBold(rowTotales, 2, myWorkBook, mySheet, reporte.getProducto());
        ExcelUtil.crearCeldaBold(rowTotales, 3, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getEmisionesFecha())+"");
        ExcelUtil.crearCeldaBold(rowTotales, 4, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getEmisionesAcum())+"");
        ExcelUtil.crearCeldaBold(rowTotales, 5, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getRecargasDiaLocal())+"");
        ExcelUtil.crearCeldaBold(rowTotales, 6, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getRecargasDiaDolares())+"");
        ExcelUtil.crearCeldaBold(rowTotales, 7, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getRecargasAcumLocal())+"");
        ExcelUtil.crearCeldaBold(rowTotales, 8, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getRecargasAcumDolares())+"");
        ExcelUtil.crearCeldaBold(rowTotales, 9, myWorkBook, mySheet, TextUtil.formatearDecimal(reporte.getPorcRep())+" %");
        mySheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,1,2));  
    }
    
    public void crearFilaCambiosMonedaExcel(HSSFWorkbook myWorkBook,HSSFSheet mySheet,int rowIndex,ReporteEmisionRecarga reporte){
        HSSFRow rowMonedas = mySheet.createRow(rowIndex);
        ExcelUtil.crearCeldaEncabezado(rowMonedas, 1, myWorkBook, mySheet, "Cambios de Moneda");
        ExcelUtil.crearCeldaEncabezado(rowMonedas, 2, myWorkBook, mySheet, "");
        mySheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,1,2));
        
        ExcelUtil.crearCeldaBold(rowMonedas, 3, myWorkBook, mySheet, "BsF/$: ");
        ExcelUtil.crearCelda(rowMonedas, 4, myWorkBook, mySheet, TextUtil.formatearDecimal(this.cambioBsDolar));
        ExcelUtil.crearCeldaBold(rowMonedas, 5, myWorkBook, mySheet, "Soles/$: ");
        ExcelUtil.crearCelda(rowMonedas, 6, myWorkBook, mySheet, TextUtil.formatearDecimal(this.cambioSolesDolar));
        ExcelUtil.crearCeldaBold(rowMonedas, 7, myWorkBook, mySheet, "Pesos/$: ");
        ExcelUtil.crearCelda(rowMonedas, 8, myWorkBook, mySheet, TextUtil.formatearDecimal(this.cambioPesosDolar));
    }
    
    public void enviarCorreo(){
        String rutaImg = Utils.getConfig(appName+".properties").getProperty("rutaImg");
        String smtpServ = Utils.getConfig(appName+".properties").getProperty("smtpConfName").trim();
        String sender = Utils.getConfig(appName+".properties").getProperty("smtpConfSender").trim();
        String mailUser = Utils.getConfig(appName+".properties").getProperty("mailUser").trim();
        String mailPasswd = Utils.getConfig(appName+".properties").getProperty("mailPasswd").trim();
        String asuntoMail = Utils.getConfig(appName+".properties").getProperty("asuntoMailActividadDiaria").trim();
        
        int numAdrGLOBAL = Integer.valueOf(Utils.getConfig(appName+".properties").getProperty("smtpNumberAdresses"+GLOBAL).trim()).intValue();
        int numAdrCO = Integer.valueOf(Utils.getConfig(appName+".properties").getProperty("smtpNumberAdresses"+CO).trim()).intValue();
        int numAdrPE = Integer.valueOf(Utils.getConfig(appName+".properties").getProperty("smtpNumberAdresses"+PE).trim()).intValue();
        int numAdrVE = Integer.valueOf(Utils.getConfig(appName+".properties").getProperty("smtpNumberAdresses"+VE).trim()).intValue();
        
        String mensaje;
        
        mensaje=this.generarReporteHtmlPais(this.reporteCo,CO);
        SendMail.sendEmailHtml(smtpServ,this.obtenerRemitentesCorreo(numAdrCO, CO),asuntoMail+" "+ReporteActividadDiariaProc.COLOMBIA+" al "+(new SimpleDateFormat("dd/MM/yyyy")).format(this.fecha),mensaje,sender,mailUser,mailPasswd,rutaImg);
        mensaje=this.generarReporteHtmlPais(this.reportePe,PE);
        SendMail.sendEmailHtml(smtpServ,this.obtenerRemitentesCorreo(numAdrPE, PE),asuntoMail+" "+ReporteActividadDiariaProc.PERU+" al "+(new SimpleDateFormat("dd/MM/yyyy")).format(this.fecha),mensaje,sender,mailUser,mailPasswd,rutaImg);
        mensaje=this.generarReporteHtmlPais(this.reporteVe,VE);
        SendMail.sendEmailHtml(smtpServ,this.obtenerRemitentesCorreo(numAdrVE, VE),asuntoMail+" "+ReporteActividadDiariaProc.VENEZUELA+" al "+(new SimpleDateFormat("dd/MM/yyyy")).format(this.fecha),mensaje,sender,mailUser,mailPasswd,rutaImg);
        mensaje=this.generarReporteHtmlGlobal();
        SendMail.sendEmailHtml(smtpServ,this.obtenerRemitentesCorreo(numAdrGLOBAL, GLOBAL),asuntoMail+" al "+(new SimpleDateFormat("dd/MM/yyyy")).format(this.fecha),mensaje,sender,mailUser,mailPasswd,rutaImg);
    }
    
    public String generarReporteHtmlPais(Map<String,ReporteEmisionRecarga> reportePais,String pais){
        List<ReporteEmisionRecarga> listaPais = new ArrayList();
        if(reportePais.get("natural")!=null) {
            listaPais.add(reportePais.get("natural"));
        }
        if(reportePais.get("juridica")!=null) {
            listaPais.add(reportePais.get("juridica"));
        }
        if(reportePais.get("juridicaUS")!=null) {
            listaPais.add(reportePais.get("juridicaUS"));
        }
        if(reportePais.get("comedores")!=null) {
            listaPais.add(reportePais.get("comedores"));
        }

        Iterator iteratorPais = listaPais.iterator();
        
        String htmlText = "";
        String width = "900";
        String styleTH = "style='background:#4F81BD; text-align:center; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:12px; font-weight:bold; color: #fff'";
        String styleTD = "style='background:#FFF; text-align:center; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:12px; color: #131313'";
        String style10px = "style='text-align:right; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:10px'";
        
        htmlText=htmlText
                +"<body>"
                + "<table width='"+width+"'><tr><td><center><h2>Resumen de Actividad Diaria al "+(new SimpleDateFormat("dd/MM/yyyy")).format(this.fecha)+"</h2></center></td></tr></table>"
                + "<br/>"
                
                + "<table width='"+width+"' border='1'>"
                + "<thead>"
                + "<tr><th rowspan='3' "+styleTH+">Producto</th><th colspan='2' "+styleTH+">Emisiones</th><th colspan='5' "+styleTH+">Recargas</th></tr>"
                + "<tr><th rowspan='2' "+styleTH+">Día "+(new SimpleDateFormat("dd/MM/yyyy")).format(this.fecha)+"</th><th rowspan='2' "+styleTH+">Acumulada "+(new SimpleDateFormat("MM/yyyy")).format(this.fecha)+"</th><th colspan='2' "+styleTH+">Día "+(new SimpleDateFormat("dd/MM/yyyy")).format(this.fecha)+"</th><th colspan='3' "+styleTH+">Acumulada "+(new SimpleDateFormat("MM/yyyy")).format(this.fecha)+"</th></tr>"
                + "<tr><th "+styleTH+">Moneda Local</th><th "+styleTH+">US$</th><th "+styleTH+">Moneda Local</th><th "+styleTH+">US$</th><th "+styleTH+">%Rep</th></tr>"
                + "</thead>"
                
                + "<tbody>";
                
                while (iteratorPais.hasNext()){
                    ReporteEmisionRecarga reporte = (ReporteEmisionRecarga)iteratorPais.next();
                    htmlText=htmlText+"<tr><td "+styleTD+">"+reporte.getProducto()+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(reporte.getEmisionesFecha())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(reporte.getEmisionesAcum())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(reporte.getRecargasDiaLocal())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(reporte.getRecargasDiaDolares())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(reporte.getRecargasAcumLocal())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(reporte.getRecargasAcumDolares())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(reporte.getPorcRep())+"%</td></tr>";
                }
        htmlText=htmlText
                + "<tr>"
                + "<td "+styleTD+"><b>Totales: </b></td>"
                + "<td "+styleTD+"><b>"+TextUtil.formatearDecimal(reportePais.get("totales").getEmisionesFecha())+"</b></td>"
                + "<td "+styleTD+"><b>"+TextUtil.formatearDecimal(reportePais.get("totales").getEmisionesAcum())+"</b></td>"
                + "<td "+styleTD+"><b>"+TextUtil.formatearDecimal(reportePais.get("totales").getRecargasDiaLocal())+"</b></td>"
                + "<td "+styleTD+"><b>"+TextUtil.formatearDecimal(reportePais.get("totales").getRecargasDiaDolares())+"</b></td>"
                + "<td "+styleTD+"><b>"+TextUtil.formatearDecimal(reportePais.get("totales").getRecargasAcumLocal())+"</b></td>"
                + "<td "+styleTD+"><b>"+TextUtil.formatearDecimal(reportePais.get("totales").getRecargasAcumDolares())+"</b></td>"
                + "<td "+styleTD+"><b>"+TextUtil.formatearDecimal(reportePais.get("totales").getPorcRep())+"%</b></td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                
                + "<br/><br/>"
                + this.generarLeyendaMonedaHtml(width,pais)
                
                + "<br/><br/><br/>"
                + this.generarLeyendaUsuarioHtml(width, style10px)
                + "<br/><br/><br/>"
                
                + "<table width='"+width+"'><tr><td>"
                + "<p "+style10px+">ADVERTENCIA LEGAL: La información contenida y transmitida por medio de este mensaje y/o su(s) anexo(s) es confidencial y destinada exclusivamente a ser utilizada por parte del destinatario indicado en el encabezado. Si Usted no es dicho destinatario, por medio de la presente, se le notifica que está prohibido el uso, divulgación, modificación o copia de este mensaje y/o su(s)  anexo(s). En caso de que Usted reciba este mensaje por error, notifíquenos de inmediato utilizando el enlace Responder, destruya o elimine el original así como, cualquier impresión del mismo y mantenga la confidencialidad sobre su contenido. Aunque creemos que este mensaje y/o su(s) anexo(s) no tienen virus ni otros defectos, es responsabilidad del destinatario asegurarse que efectivamente ello sea así; por consiguiente, no aceptamos responsabilidad alguna por los daños y perjuicios que pudieran surgir, de cualquier forma, por el uso de este mensaje y/o su(s) anexo(s).</p>"
                + "<p "+style10px+">DISCLAIMER: The information contained and transmitted by this e-mail and/or any attachment(s) is confidential and is intended for the sole use of the named recipient(s). If you are not the named recipient you are hereby notified that the use, dissemination, modification or copy of this e-mail and/or any attachment(s) is prohibited. If you have received this e-mail by mistake, please notify us immediately, delete the original and any printouts and keep the contents thereof confidential. Although this e-mail and/ore any attachments are believed to be free from virus or other defects, it is the responsibility of the recipient to ensure that it does not contain any virus or other defects and we accept no responsibility for any damage arising in any way from its use.</p>"
                + "</td></tr></table>"
        
                +"</body>";
        
        
        
        return htmlText;
    }
    
    public String generarReporteHtmlGlobal(){
        String htmlText="";
        String width = "900";
        String styleTH = "style='background:#4F81BD; text-align:center; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:12px; font-weight:bold; color: #fff'";
        String styleTD = "style='background:#FFF; text-align:center; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:12px; color: #131313'";
        String style10px = "style='text-align:right; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:10px'";
        
        htmlText=htmlText
                +"<body>"
                + "<table width='"+width+"'><tr><td><center><h2>Resumen de Actividad Diaria al "+(new SimpleDateFormat("dd/MM/yyyy")).format(this.fecha)+"</h2></center></td></tr></table>"
                + "<br/>"
                
                + "<table width='"+width+"' border='1'>"
                + "<thead>"
                + "<tr><th colspan='2' "+styleTH+">&nbsp;</th><th colspan='2' "+styleTH+">Emisiones</th><th colspan='5' "+styleTH+">Recargas</th></tr>"
                + "<tr><th rowspan='2' "+styleTH+">País</th><th rowspan='2' "+styleTH+">Producto</th><th rowspan='2' "+styleTH+">Día "+(new SimpleDateFormat("dd/MM/yyyy")).format(this.fecha)+"</th><th rowspan='2' "+styleTH+">Acumulada "+(new SimpleDateFormat("MM/yyyy")).format(this.fecha)+"</th><th colspan='2' "+styleTH+">Día "+(new SimpleDateFormat("dd/MM/yyyy")).format(this.fecha)+"</th><th colspan='3' "+styleTH+">Acumulada "+(new SimpleDateFormat("MM/yyyy")).format(this.fecha)+"</th></tr>"
                + "<tr><th "+styleTH+">Moneda Local</th><th "+styleTH+">US$</th><th "+styleTH+">Moneda Local</th><th "+styleTH+">US$</th><th "+styleTH+">%Rep</th></tr>"
                + "</thead>"
                
                + "<tbody>";
        
        htmlText=htmlText       
                + "<tr><td rowspan='2'"+styleTD+">"+VENEZUELA+"</td><td "+styleTD+">"+this.reporteVe.get("natural").getProducto()+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteVe.get("natural").getEmisionesFecha())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteVe.get("natural").getEmisionesAcum())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteVe.get("natural").getRecargasDiaLocal())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteVe.get("natural").getRecargasDiaDolares())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteVe.get("natural").getRecargasAcumLocal())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteVe.get("natural").getRecargasAcumDolares())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteVe.get("natural").getPorcRepGlobal())+"%</td></tr>"
                + "<tr><td "+styleTD+">"+this.reporteVe.get("juridica").getProducto()+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteVe.get("juridica").getEmisionesFecha())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteVe.get("juridica").getEmisionesAcum())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteVe.get("juridica").getRecargasDiaLocal())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteVe.get("juridica").getRecargasDiaDolares())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteVe.get("juridica").getRecargasAcumLocal())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteVe.get("juridica").getRecargasAcumDolares())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteVe.get("juridica").getPorcRepGlobal())+"%</td></tr>"
                
                + "<tr><td rowspan='3'"+styleTD+">"+PERU+"</td><td "+styleTD+">"+this.reportePe.get("natural").getProducto()+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("natural").getEmisionesFecha())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("natural").getEmisionesAcum())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("natural").getRecargasDiaLocal())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("natural").getRecargasDiaDolares())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("natural").getRecargasAcumLocal())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("natural").getRecargasAcumDolares())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("natural").getPorcRepGlobal())+"%</td></tr>"
                + "<tr><td "+styleTD+">"+this.reportePe.get("juridica").getProducto()+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("juridica").getEmisionesFecha())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("juridica").getEmisionesAcum())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("juridica").getRecargasDiaLocal())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("juridica").getRecargasDiaDolares())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("juridica").getRecargasAcumLocal())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("juridica").getRecargasAcumDolares())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("juridica").getPorcRepGlobal())+"%</td></tr>"
                + "<tr><td "+styleTD+">"+this.reportePe.get("juridicaUS").getProducto()+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("juridicaUS").getEmisionesFecha())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("juridicaUS").getEmisionesAcum())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("juridicaUS").getRecargasDiaLocal())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("juridicaUS").getRecargasDiaDolares())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("juridicaUS").getRecargasAcumLocal())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("juridicaUS").getRecargasAcumDolares())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("juridicaUS").getPorcRepGlobal())+"%</td></tr>"
                + "<tr><td "+styleTD+">"+this.reportePe.get("comedores").getProducto()+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("comedores").getEmisionesFecha())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("comedores").getEmisionesAcum())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("comedores").getRecargasDiaLocal())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("comedores").getRecargasDiaDolares())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("comedores").getRecargasAcumLocal())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("comedores").getRecargasAcumDolares())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reportePe.get("comedores").getPorcRepGlobal())+"%</td></tr>"
                
                + "<tr><td "+styleTD+">"+COLOMBIA+"</td>"
                + "<td "+styleTD+">"+this.reporteCo.get("juridica").getProducto()+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteCo.get("juridica").getEmisionesFecha())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteCo.get("juridica").getEmisionesAcum())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteCo.get("juridica").getRecargasDiaLocal())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteCo.get("juridica").getRecargasDiaDolares())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteCo.get("juridica").getRecargasAcumLocal())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteCo.get("juridica").getRecargasAcumDolares())+"</td><td "+styleTD+">"+TextUtil.formatearDecimal(this.reporteCo.get("juridica").getPorcRepGlobal())+"%</td></tr>"
                + "";
        
        htmlText=htmlText
                + "<tr>"
                + "<td colspan='2' "+styleTD+"><b>Totales: </b></td>"
                + "<td "+styleTD+"><b>"+TextUtil.formatearDecimal(this.totales.getEmisionesFecha())+"</b></td>"
                + "<td "+styleTD+"><b>"+TextUtil.formatearDecimal(this.totales.getEmisionesAcum())+"</b></td>"
                + "<td "+styleTD+"><b>"+TextUtil.formatearDecimal(this.totales.getRecargasDiaLocal())+"</b></td>"
                + "<td "+styleTD+"><b>"+TextUtil.formatearDecimal(this.totales.getRecargasDiaDolares())+"</b></td>"
                + "<td "+styleTD+"><b>"+TextUtil.formatearDecimal(this.totales.getRecargasAcumLocal())+"</b></td>"
                + "<td "+styleTD+"><b>"+TextUtil.formatearDecimal(this.totales.getRecargasAcumDolares())+"</b></td>"
                + "<td "+styleTD+"><b>"+TextUtil.formatearDecimal(this.totales.getPorcRepGlobal())+"%</b></td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                
                + "<br/><br/>"
                + this.generarLeyendaMonedaHtml(width,"all")
                
                + "<br/><br/><br/>"
                + this.generarLeyendaUsuarioHtml(width, style10px)
                + "<br/><br/><br/>"
                
                + "<table width='"+width+"'><tr><td>"
                + "<p "+style10px+">ADVERTENCIA LEGAL: La información contenida y transmitida por medio de este mensaje y/o su(s) anexo(s) es confidencial y destinada exclusivamente a ser utilizada por parte del destinatario indicado en el encabezado. Si Usted no es dicho destinatario, por medio de la presente, se le notifica que está prohibido el uso, divulgación, modificación o copia de este mensaje y/o su(s)  anexo(s). En caso de que Usted reciba este mensaje por error, notifíquenos de inmediato utilizando el enlace Responder, destruya o elimine el original así como, cualquier impresión del mismo y mantenga la confidencialidad sobre su contenido. Aunque creemos que este mensaje y/o su(s) anexo(s) no tienen virus ni otros defectos, es responsabilidad del destinatario asegurarse que efectivamente ello sea así; por consiguiente, no aceptamos responsabilidad alguna por los daños y perjuicios que pudieran surgir, de cualquier forma, por el uso de este mensaje y/o su(s) anexo(s).</p>"
                + "<p "+style10px+">DISCLAIMER: The information contained and transmitted by this e-mail and/or any attachment(s) is confidential and is intended for the sole use of the named recipient(s). If you are not the named recipient you are hereby notified that the use, dissemination, modification or copy of this e-mail and/or any attachment(s) is prohibited. If you have received this e-mail by mistake, please notify us immediately, delete the original and any printouts and keep the contents thereof confidential. Although this e-mail and/ore any attachments are believed to be free from virus or other defects, it is the responsibility of the recipient to ensure that it does not contain any virus or other defects and we accept no responsibility for any damage arising in any way from its use.</p>"
                + "</td></tr></table>"
        
                +"</body>";
        
        return htmlText;
    }
    
    private String[] obtenerRemitentesCorreo(int numAdr,String sufijo){
        String recipients[] = new String[numAdr];
        
        log.info("Recipientes"+sufijo+": "+numAdr);
        for(int i = 0; i < numAdr; i++) {
            recipients[i] = Utils.getConfig(appName+".properties").getProperty("smtpRecvAddress"+sufijo+(i + 1)).trim();
            log.info("Recipiente "+(i+1)+": "+recipients[i]);
        }
        
        return recipients;
    }
    
    private String generarLeyendaMonedaHtml(String width,String pais){
        
        String textHtml="";
        textHtml = textHtml + "<table width='"+width+"'><tbody><tr>";
        if (pais.equals(VE)||pais.equals("all")) {
            textHtml = textHtml + "<td><center><b>BsF/$: </b>"+TextUtil.formatearDecimal(this.cambioBsDolar)+"</center></td>";
        }
        if (pais.equals(PE)||pais.equals("all")) {
            textHtml = textHtml + "<td><center><b>Soles/$: </b>"+TextUtil.formatearDecimal(this.cambioSolesDolar)+"</center></td>" ;
        }
        if (pais.equals(CO)||pais.equals("all")) {
            textHtml = textHtml + "<td><center><b>Pesos/$: </b>"+TextUtil.formatearDecimal(this.cambioPesosDolar)+"</center></td>";
        }
        textHtml = textHtml + "</tr></tbody></table>";
        
        return textHtml;
    }
    
    private String generarLeyendaUsuarioHtml(String width,String style){
        String textHtml=""
                + "<table width='"+width+"'><tbody><tr>"
                + "<td>"
                + "<p "+style+">Generado por: <br/>"
                + this.usuarioSesion.getAcNombre()+", "+this.usuarioSesion.getAcUbicacion()+"<br/>"
                + this.usuarioSesion.getAcEmail()
                + "</p>"
                + "</td>"
                + "</tr></tbody></table>";
        
        return textHtml;
    }
    
    public static String obtenerCambioMoneda(String pais){
        //ParametrosDAO parametrosDAO = new ParametrosDAO(appName,dbInformix,pais);
        ParametrosDAO parametrosDAO = new ParametrosDAO(appName,dbOracle,pais);
        Parametro parametro = parametrosDAO.obtenerParametro("CambioDolares"+pais.toUpperCase());
        
        if (parametro!=null){
            return parametro.getAcvalue();
        }
        else{
            return "0.00";
        }
    }
    
    public boolean modificarCambioMoneda(String pais,String valor){
        //ParametrosDAO parametrosDAO = new ParametrosDAO(appName,dbInformix,pais);
        ParametrosDAO parametrosDAO = new ParametrosDAO(appName,dbOracle,pais);
        return parametrosDAO.modificarParametro(new Parametro("CambioDolares"+pais.toUpperCase(),valor,"default","00"));
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Map<String, ReporteEmisionRecarga> getReporteCo() {
        return reporteCo;
    }

    public void setReporteCo(Map<String, ReporteEmisionRecarga> reporteCo) {
        this.reporteCo = reporteCo;
    }

    public Map<String, ReporteEmisionRecarga> getReportePe() {
        return reportePe;
    }

    public void setReportePe(Map<String, ReporteEmisionRecarga> reportePe) {
        this.reportePe = reportePe;
    }

    public Map<String, ReporteEmisionRecarga> getReporteVe() {
        return reporteVe;
    }

    public void setReporteVe(Map<String, ReporteEmisionRecarga> reporteVe) {
        this.reporteVe = reporteVe;
    }

    public ReporteEmisionRecarga getTotales() {
        return totales;
    }

    public void setTotales(ReporteEmisionRecarga totales) {
        this.totales = totales;
    }

    public BigDecimal getCambioBsDolar() {
        return cambioBsDolar;
    }

    public void setCambioBsDolar(BigDecimal cambioBsDolar) {
        this.cambioBsDolar = cambioBsDolar;
    }

    public BigDecimal getCambioSolesDolar() {
        return cambioSolesDolar;
    }

    public void setCambioSolesDolar(BigDecimal cambioSolesDolar) {
        this.cambioSolesDolar = cambioSolesDolar;
    }

    public BigDecimal getCambioPesosDolar() {
        return cambioPesosDolar;
    }

    public void setCambioPesosDolar(BigDecimal cambioPesosDolar) {
        this.cambioPesosDolar = cambioPesosDolar;
    }
    
    
    
}
