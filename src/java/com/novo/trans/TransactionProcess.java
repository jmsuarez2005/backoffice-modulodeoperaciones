/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.trans;

import com.novo.constants.TransactionProcessConstants;
import com.novo.database.Dbinterface;
import com.novo.database.NovoDAO;
import com.novo.model.Tarjeta;
import com.novo.model.Transaccion;
import com.novo.objects.util.Utils;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author ggutierrez
 */
public class TransactionProcess extends NovoDAO implements TransactionProcessConstants{
    protected Properties prop;
     private static Logger log = Logger.getLogger(TransactionProcess.class);
    public TransactionProcess(String dbproperty, String[] databases, String pais) {
        super(dbproperty, databases, pais);
        prop = Utils.getConfig("operaciones.properties");
    }
    
    public Transaccion cargo(Tarjeta tarjeta, String systrace, String pais){
        Transaccion trans = new Transaccion();
        String ip, port, timeout, terminal,nroOrganizacion;
        ip = prop.getProperty("novotrans_ip_"+pais);
        port = prop.getProperty("novotrans_port");
        timeout = prop.getProperty("novotrans_timeout");
        terminal = prop.getProperty("terminal");
        nroOrganizacion = prop.getProperty("nro_organizacion");        
        TransactionHandler th = new TransactionHandler(ip, Integer.parseInt(port), Integer.parseInt(timeout));
        th.setNroOrganizacion(nroOrganizacion);
        String fechExpira = tarjeta.getFechaExpiracion().substring(2,4) +tarjeta.getFechaExpiracion().substring(0,2);
        trans.setRc(th.execCobro(ID_CARGO, tarjeta.getNroTarjeta(), tarjeta.getMonto(), systrace, nroOrganizacion, terminal, "CARGO POR TRASLADO", fechExpira, true, null));
        trans.setTransRespCode(th.getRespCode());
        trans.setMsg("");
        
        return trans;
    }
    
    public Transaccion abono(Tarjeta tarjeta, String systrace, String pais){
        Transaccion trans = new Transaccion();
        String ip, port, timeout, terminal,nroOrganizacion;
        ip = prop.getProperty("novotrans_ip_"+pais);
        port = prop.getProperty("novotrans_port");
        timeout = prop.getProperty("novotrans_timeout");
        terminal = prop.getProperty("terminal");
        nroOrganizacion = prop.getProperty("nro_organizacion");        
        TransactionHandler th = new TransactionHandler(ip, Integer.parseInt(port), Integer.parseInt(timeout));
        th.setNroOrganizacion(nroOrganizacion);
        String fechExpira = tarjeta.getFechaExpiracion().substring(2,4) +tarjeta.getFechaExpiracion().substring(0,2);
        trans.setRc(th.execRecarga(ID_ABONO, tarjeta.getNroTarjeta(), tarjeta.getMonto(), systrace, nroOrganizacion, terminal, "ABONO POR TRASLADO", fechExpira, true, null));
        trans.setTransRespCode(th.getRespCode());
        trans.setMsg("");
        return trans;
    }
    
    public Transaccion balance(Tarjeta tarjeta, String systrace, String pais, String exptarjeta) {
        Transaccion trans = new Transaccion();
        String ip, port, timeout, terminal,nroOrganizacion;
        ip = prop.getProperty("novotrans_ip_"+pais);
        port = prop.getProperty("novotrans_port");
        timeout = prop.getProperty("novotrans_timeout");
        terminal = prop.getProperty("terminal");
        nroOrganizacion = prop.getProperty("nro_organizacion");
        log.info(ip+","+port+","+timeout+","+terminal+","+nroOrganizacion);
        TransactionHandler th = new TransactionHandler(ip, Integer.parseInt(port), Integer.parseInt(timeout));
        th.setNroOrganizacion(nroOrganizacion);
        trans.setRc(th.execSaldo(ID_BALANCE, systrace, tarjeta.getNroTarjeta(), terminal, "CONSULTA POR TRASLADO", "0", "0",exptarjeta));
        trans.setTransRespCode(th.getRespCode());
        trans.setMsg("");
        trans.setSaldo(Utils.formatMontoGenerico(Double.parseDouble(th.getSaldoDisponible()) / 100, prop.getProperty("pais")));
        return trans;
    }
    
    public String getSystrace(){
        Dbinterface dbo = ds.get("oracle");
        String systrace= "";
        String sql = "select tebcp_cpsystrace.nextval from dual ";
        if(dbo.executeQuery(sql)==0){
            if(dbo.nextRecord()){
                systrace = dbo.getFieldString("NEXTVAL");
            }
        }
        return systrace;
    }
    
    
    
    @Override
    public void closeConection() {
         this.shutdownDatabases();
    }
    
    
}
