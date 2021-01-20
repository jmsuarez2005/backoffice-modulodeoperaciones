/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.dao;

import com.novo.constants.AjustesTransacciones;
import com.novo.constants.BasicConfig;
import com.novo.database.Dbinterface;
import com.novo.database.NovoDAO;
import com.novo.model.Ajuste;
import com.novo.model.TBloqueo;
import com.novo.objects.util.Utils;
import com.novo.trans.TransactionHandler;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import static com.novo.util. VariablesUtil.codPais;

/**
 *
 * @author rgomez
 */
public class BloqueoDesbloqueoDAO extends NovoDAO implements BasicConfig, AjustesTransacciones {

    private List<TBloqueo> bloqueo;
    private static Logger log = Logger.getLogger(BloqueoDesbloqueoDAO.class);
    Properties prop;
    List<Ajuste> ajustes;

    public List<Ajuste> getAjustes() {
        return this.ajustes;
    }

    public BloqueoDesbloqueoDAO(String dbproperty, String[] databases, String pais) {
        super(dbproperty, databases, pais);
        prop = Utils.getConfig("NovoConexionTrans_"+codPais(pais)+".properties");          
    }

    public void setAjustes(List<Ajuste> ajustes) {
        this.ajustes = ajustes;
    }

    public int ProcesarBloqueoDAO(String idUsuario, String selectedBloqueo) {
        int cont1 = 0;

        Calendar fecha = Calendar.getInstance();
        int año = fecha.get(1);
        int mes = fecha.get(2);
        int dia = fecha.get(5);
        String annoying = String.valueOf(año) + "-" + String.valueOf(mes + 1) + "-" + String.valueOf(dia);

        for (TBloqueo ajuste : this.bloqueo) {
            String respuesta = RegistrarBloqueoDesblqueoDAO(ajuste.getNro_tarjeta(), idUsuario, selectedBloqueo);
            if (respuesta.equals("ok")) {
                ajuste.setDescripcion("Procesado");
            } else if (respuesta.equals("existe")) {
                ajuste.setDescripcion("La tarjeta ya existe para hacer un bloqueo");
            } else {
                ajuste.setDescripcion("Anulado, Codigo de Respuesta: " + respuesta);
            }

            ajuste.setFec_reg(annoying);

            cont1++;
        }

        return cont1;
    }

    public List<TBloqueo> getTipoBloqueoDAO() {
        List tipoBloqueo = new ArrayList();
        TBloqueo tipobloqueo = new TBloqueo();
        String sql = "select * from NOVO_TIPO_BLOQUE order by TIPO_BLOQUE";
        Dbinterface dbo = (Dbinterface) this.ds.get("oracle");
        dbo.dbreset();
        log.info("Sql [" + sql + "]");
        if (dbo.executeQuery(sql) == 0) {
            while (dbo.nextRecord()) {
                tipobloqueo.setCodigo(dbo.getFieldString("CODIGO").trim());
                tipobloqueo.setStatus(dbo.getFieldString("STATUS").trim());
                tipobloqueo.setId_bloque(dbo.getFieldString("ID_BLOQUE").trim());
                tipobloqueo.setTipo_bloque(dbo.getFieldString("TIPO_BLOQUE").trim());
                tipoBloqueo.add(tipobloqueo);
                tipobloqueo = new TBloqueo();
            }
        }
        dbo.dbClose();
        return tipoBloqueo;
    }

//    public String Nro_Organizacion() {
//        String organizacion = "";
//        switch (pais){
//            case "pe":
//                organizacion = "717";
//                break;
//            case"peusd": 
//                organizacion = "716";
//                break;
//            case "ve": 
//                organizacion = "719";
//                break;
//            case "co": 
//                organizacion = "713";
//                break;
//            default:
//                organizacion = "";
//        }
//        return organizacion;
//    }

    public String RegistrarBloqueoDesblqueoDAO(String Tarjeta, String idUsuario, String selectedBloqueo) {
        String ip, port, timeout, terminal,nroOrganizacion;
        String uf = "";
        String uf2 = "";
        String trn = "";
        String res="";
//        String sql1 = "SELECT ("
//                + "\nSELECT ACVALUE AS IP FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustes_novotran_ip') AS IP,"
//                + "\n(SELECT  ACVALUE AS PORT FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustes_novotran_port') AS PORT,"
//                + "\n(SELECT ACVALUE AS TERMINAL FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustes_novotran_terminal') AS TERMINAL,"
//                + "\n(SELECT ACVALUE AS TIMEOUT FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustes_novotran_timeout') AS TIMEOUT"
//                + "\n FROM DUAL where ROWNUM = 1";
       try{
            ip = prop.getProperty("moduloAjustes_novotran_ip");
            port = prop.getProperty("moduloAjustes_novotran_port");
            timeout = prop.getProperty("moduloAjustes_novotran_timeout");
            terminal = prop.getProperty("moduloAjustes_novotran_terminal");
            nroOrganizacion = prop.getProperty("nro_organizacion");
            
            log.info(ip+","+port+","+timeout+","+terminal+","+nroOrganizacion);
       //        Dbinterface dbi = (Dbinterface) this.ds.get("informix");
        //        dbi.dbreset();
//        
//        Dbinterface dbo2 = (Dbinterface) this.ds.get("oracle");
//        dbo2.dbreset();
        Dbinterface dbo = (Dbinterface) this.ds.get("oracle");
        dbo.dbreset();
        TransactionHandler handler = null;
        //String terminal = "";
        String exptarjeta = "";

        String sql6 = "select substr(fec_expira,3) || substr(fec_expira,0,2) as fec_expira from MAESTRO_CONSOLIDADO_TEBCA where nro_cuenta = '0000" + Tarjeta.substring(0, 14) + "00" + "'";

        if (dbo.executeQuery(sql6) == 0) {
            if (dbo.nextRecord()) {
                exptarjeta = dbo.getFieldString("fec_expira");
            }

            dbo.dbClose();
        } else {
            dbo.dbClose();
            return "error";
        }

//        if (dbo2.executeQuery(sql1) == 0) {
//            if (dbo2.nextRecord()) {
                handler = new TransactionHandler(ip, Integer.parseInt(port), Integer.parseInt(timeout));
//                terminal = dbo2.getFieldString("TERMINAL");
////            }
//
//            dbo2.dbClose();
//        } else {
//            dbo2.dbClose();
//            return "error";
//        }

        boolean existe = false;

        if (!existe) {
            String systrace = "";
            String sql3 = "select tebcp_cpsystrace.nextval from dual ";
            dbo.dbreset();
            if ((dbo.executeQuery(sql3) == 0)
                    && (dbo.nextRecord())) {
                systrace = dbo.getFieldString("NEXTVAL");
            }

            if (selectedBloqueo.equals("00")) {
                trn = "SERVICIO DE ACTIVACION";
            } else {
                trn = "SERVICIO DE BLOQUEO";
            }

            //enviando operacion a novotrans
            handler.execBloqueo("0", systrace, Tarjeta, terminal, trn, "", nroOrganizacion, selectedBloqueo, exptarjeta);

            if (handler.getRespCode().equals("00")) {
                String sql = "insert into novo_bloqueo (NRO_TARJETA,USUARIO_INGRESO,TIPO_BLOQUE,DESCRIPCION,CANAL) VALUES ('" + Tarjeta + "','" + idUsuario + "','" + selectedBloqueo + "', '" + handler.getRespCode() + "', 'OPE')";
                if (selectedBloqueo.equals("00")) {
                    //actualizando los maestros en caso de activacion
                    uf = "UPDATE MAESTRO_PLASTICO_TEBCA SET BLOQUE=NULL WHERE NRO_CUENTA=0000" + Tarjeta;
                    uf2 = "UPDATE MAESTRO_CONSOLIDADO_TEBCA SET BLOQUE = NULL, FEC_CAMBIO_BLOQUE = SYSDATE WHERE NRO_CUENTA=0000" + Tarjeta.substring(0, 14) + "00";
                } else {
                    //actualizando los maestros en caso de bloqueo
                    uf = "UPDATE MAESTRO_PLASTICO_TEBCA SET BLOQUE='" + selectedBloqueo + "' WHERE NRO_CUENTA=0000" + Tarjeta;
                    uf2 = "UPDATE MAESTRO_CONSOLIDADO_TEBCA SET BLOQUE='" + selectedBloqueo + "', FEC_CAMBIO_BLOQUE = SYSDATE WHERE NRO_CUENTA=0000" + Tarjeta.substring(0, 14) + "00";
                }

                log.debug("sql #0 [" + sql + "]");
                log.debug("sql #1 [" + uf + "]");
                log.debug("sql #2 [" + uf2 + "]");

                dbo.dbreset();

                if (dbo.executeQuery(sql) == 0) {
                    if (dbo.executeQuery(uf) == 0) {
                        if (dbo.executeQuery(uf2) == 0) {
                            dbo.dbClose();
                            log.info("La tarjeta " + Tarjeta + " fue bloqueada o desbloqueada, código respuesta " + handler.getRespCode());
                            return "ok";
                        }
                        dbo.dbClose();
                        log.info("La tarjeta " + Tarjeta + " no pudo ser bloqueada o desbloqueada, código respuesta " + handler.getRespCode());
                        return handler.getRespCode();
                    }

                    dbo.dbClose();
                    log.info("La tarjeta " + Tarjeta + " no pudo ser bloqueada o desbloqueada, código respuesta " + handler.getRespCode());
                    return handler.getRespCode();
                }

                dbo.dbClose();
                log.info("La tarjeta " + Tarjeta + " no pudo ser bloqueada o desbloqueada, código respuesta " + handler.getRespCode());
                return handler.getRespCode();

            }else{
                dbo.dbClose();
                log.info("La tarjeta " + Tarjeta + " no pudo ser bloqueada o desbloqueada, código respuesta " + handler.getRespCode());
                
            }

            String sql4 = "insert into novo_bloqueo (NRO_TARJETA,USUARIO_INGRESO,TIPO_BLOQUE,DESCRIPCION,CANAL) VALUES ('" + Tarjeta + "','" + idUsuario + "','" + selectedBloqueo + "', 'La tarjeta no pudo ser bloqueada RC=" + handler.getRespCode() + "', 'OPE')";
            log.debug("sql #0 [" + sql4 + "]");
            if (dbo.executeQuery(sql4) == 0) {
                dbo.dbClose();
                return handler.getRespCode();
            }
            dbo.dbClose();
            return "error";
        }

        return "existe";
    }catch (Exception e){
            log.error(e.getCause());
            log.error(e.getMessage());
            return "error";
        }
    }

    public void closeConection() {
        this.shutdownDatabases();
    }

    public List<TBloqueo> getBloqueo() {
        return this.bloqueo;
    }

    public void setBloqueo(List<TBloqueo> bloqueo) {
        this.bloqueo = bloqueo;
    }

}
