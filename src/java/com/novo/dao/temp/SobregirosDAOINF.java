/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.dao.temp;

import com.novo.constants.AjustesTransacciones;
import com.novo.constants.BasicConfig;
import com.novo.database.Dbinterface;
import com.novo.database.NovoDAO;
import com.novo.model.Ajuste;
import com.novo.objects.util.Utils;
import com.novo.trans.TransactionHandler;
import static com.novo.util.VariablesUtil.codPais;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author rgomez
 */
public class SobregirosDAOINF extends NovoDAO implements BasicConfig, AjustesTransacciones {

    private static Logger log = Logger.getLogger(SobregirosDAOINF.class);
    Properties prop;
    List<Ajuste> ajustes;

    public List<Ajuste> getAjustes() {
        return ajustes;
    }

    public SobregirosDAOINF(String dbproperty, String[] databases, String pais) {
        super(dbproperty, databases, pais);
        prop = Utils.getConfig("NovoConexionTrans_"+codPais(pais)+".properties");               
    }

    public void setAjustes(List<Ajuste> ajustes) {
        this.ajustes = ajustes;
    }

    public int ProcesarSobregirosDAO(String idUsuario, String selectedAjuste) {

        int cont1 = 0;
        String annoying;
        Calendar fecha = Calendar.getInstance();
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        annoying = String.valueOf(año) + "-" + String.valueOf(mes + 1) + "-" + String.valueOf(dia);
        String respuesta;

        for (Ajuste ajuste : ajustes) {

            respuesta = RegistrarSobregirosDAO(ajuste.getTarjeta(), ajuste.getMonto(), idUsuario, selectedAjuste);
            if (respuesta.equals("ok")) {

                ajuste.setFecha(annoying);
                ajuste.setStatus("3");
                ajuste.setObservacion("Procesado");
            } else if (respuesta.equals("existe")) {
                ajuste.setFecha("");
                ajuste.setStatus("7");
                ajuste.setObservacion("La tarjeta ya existe para hacer un sobregiro");
            } else {
                ajuste.setFecha("");
                ajuste.setStatus("7");
                ajuste.setObservacion("Anulado");
            }

    
            cont1++;

        }

        return cont1;
    }
    
//     public String Nro_Organizacion() {
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
        
    public String RegistrarSobregirosDAO(String Tarjeta, String Monto, String idUsuario, String selectedAjuste) {

        String sql2 = "Select NRO_CLIENTE FROM MAESTRO_PLASTICO_TEBCA";
        String ip, port, timeout, terminal,nroOrganizacion;
//        String sql1 = "SELECT (\n"
//                + "SELECT ACVALUE AS IP FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustes_novotran_ip'\n"
//                + ") AS IP,\n"
//                + "--UNION\n"
//                + "(\n"
//                + "SELECT  ACVALUE AS PORT FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustes_novotran_port'\n"
//                + ") AS PORT,\n"
//                + "--UNION\n"
//                + "(\n"
//                + "SELECT ACVALUE AS TERMINAL FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustes_novotran_terminal'\n"
//                + ") AS TERMINAL,\n"
//                + "--UNION\n"
//                + "(\n"
//                + "SELECT ACVALUE AS TIMEOUT FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustes_novotran_timeout'\n"
//                + ") AS TIMEOUT\n"
//                + " FROM informix.systables where tabid = 1";
            ip = prop.getProperty("moduloAjustes_novotran_ip");
            port = prop.getProperty("moduloAjustes_novotran_port");
            timeout = prop.getProperty("moduloAjustes_novotran_timeout");
            terminal = prop.getProperty("moduloAjustes_novotran_terminal");
            nroOrganizacion = prop.getProperty("nro_organizacion");
            
            log.info(ip+","+port+","+timeout+","+terminal+","+nroOrganizacion);

        //Dbinterface dbi = ds.get("informix");
        Dbinterface dbo = ds.get("oracle");

        //dbi.dbreset();
        TransactionHandler handler = null;
//        String Terminal = "";
        String nro_cliente = "";
        String tipo_ajuste = "";
        String exptarjeta = "";

        String ConsultaFechaExp = "select substr(fec_expira,3) || substr(fec_expira,0,2) as fec_expira from MAESTRO_CONSOLIDADO_TEBCA where nro_cuenta= '0000" + Tarjeta.substring(0, 14) + "00" + "'";

        if (dbo.executeQuery(ConsultaFechaExp) == 0) {
            if (dbo.nextRecord()) {
                exptarjeta = dbo.getFieldString("fec_expira");
            }

            dbo.dbClose();
        } else {
            dbo.dbClose();
            return "error";
        }

        //if (dbi.executeQuery(sql1) == 0) {
            handler = new TransactionHandler(ip, Integer.parseInt(port), Integer.parseInt(timeout));
        //} else {
           // return "error";
        //}

        String sql5 = "select count(*) as respuesta from novo_sobregiros where nro_tarjeta=" + Tarjeta + " and status=3";

        boolean existe = false;

        if ((dbo.executeQuery(sql5) == 0) && (dbo.nextRecord())) {
            if (Integer.parseInt(dbo.getFieldString("respuesta")) > 0) {
                existe = true;
            }
        }

        if (!existe) {

            String systrace = "";
            String SecuenciaTrama = "select tebcp_cpsystrace.nextval from dual ";
            if (dbo.executeQuery(SecuenciaTrama) == 0) {
                if (dbo.nextRecord()) {
                    systrace = dbo.getFieldString("NEXTVAL");
                }
            }

            handler.execBloqueo("0", systrace, Tarjeta, terminal, "SERVICIO DE BLOQUEO", "nro_cliente", nroOrganizacion, "PB", exptarjeta);

            if (handler.getRespCode().equals("00")) {

                String Nuevosobregiro = "insert into novo_sobregiros (ID, NRO_TARJETA, MONTO_AJUSTE,USUARIO_INGRESO,TIPO_AJUSTE) VALUES (NOVO_SOBREGIROS_SEQ.nextval, '" + Tarjeta + "'," + Monto + ",'" + idUsuario + "'," + selectedAjuste + ")";
                String ActualizaPlastico = "UPDATE MAESTRO_PLASTICO_TEBCA SET BLOQUE= 'PB' WHERE NRO_CUENTA=0000" + Tarjeta;
                String ActualizaConsolidado = "UPDATE MAESTRO_CONSOLIDADO_TEBCA SET BLOQUE= 'PB' WHERE NRO_CUENTA=0000" + Tarjeta.substring(0, 14) + "00";

                dbo.dbreset();

                if (dbo.executeQuery(Nuevosobregiro) == 0) {
                    if (dbo.executeQuery(ActualizaPlastico) == 0) {
                        if (dbo.executeQuery(ActualizaConsolidado) == 0) {
                            dbo.dbClose();
                            log.info("La tarjeta " + Tarjeta + " fue bloqueada, código respuesta " + handler.getRespCode());
                            return "ok";
                        } else {
                            dbo.dbClose();
                            log.info("La tarjeta " + Tarjeta + " no pudo ser bloqueada, código respuesta " + handler.getRespCode());
                            return "error";
                        }

                    } else {
                        dbo.dbClose();
                        log.info("La tarjeta " + Tarjeta + " no pudo ser bloqueada, código respuesta " + handler.getRespCode());
                        return "error";
                    }

                } else {
                    dbo.dbClose();
                    log.info("La tarjeta " + Tarjeta + " no pudo ser bloqueada, código respuesta " + handler.getRespCode());
                    return "error";
                }
            } else {
                log.info("La tarjeta " + Tarjeta + " no pudo ser bloqueada, código respuesta " + handler.getRespCode());

                String Sobregiro = "insert into novo_sobregiros (ID, NRO_TARJETA, MONTO_AJUSTE,USUARIO_INGRESO,TIPO_AJUSTE,MSG) VALUES (NOVO_SOBREGIROS_SEQ.nextval, '" + Tarjeta + "'," + Monto + ",'" + idUsuario + "'," + selectedAjuste + ", 'La tarjeta no pudo ser bloqueada RC=" + handler.getRespCode() + "')";
                if (dbo.executeQuery(Sobregiro) == 0) {
                    dbo.dbClose();
                    return "ok";
                } else {
                    dbo.dbClose();
                    return "error";
                }

            }
        } else {
            return ("existe");

        }

    }

    @Override
    public void closeConection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
