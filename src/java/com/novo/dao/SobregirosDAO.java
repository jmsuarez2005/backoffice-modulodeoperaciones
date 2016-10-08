/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.dao;

import com.novo.constants.AjustesTransacciones;
import com.novo.constants.BasicConfig;
import com.novo.database.Dbinterface;
import com.novo.database.NovoDAO;
import com.novo.model.Ajuste;
import com.novo.objects.util.Utils;
import com.novo.trans.TransactionHandler;
import com.novo.trans.TransactionProcess;
import com.opensymphony.xwork2.ActionContext;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author rgomez
 */
public class SobregirosDAO extends NovoDAO implements BasicConfig, AjustesTransacciones {

    private static Logger log = Logger.getLogger(SobregirosDAO.class);
    List<Ajuste> ajustes;

    public List<Ajuste> getAjustes() {
        return ajustes;
    }

    public SobregirosDAO(String dbproperty, String[] databases, String pais) {
        super(dbproperty, databases, pais);
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
                ajuste.setObservacion("La Tarjeta ya existe para hacer un sobregiro");
            } else {
                ajuste.setFecha("");
                ajuste.setStatus("7");
                ajuste.setObservacion("Anulado");
            }

    
            cont1++;

        }

        return cont1;
    }

    public String RegistrarSobregirosDAO(String Tarjeta, String Monto, String idUsuario, String selectedAjuste) {

        String sql2 = "Select NRO_CLIENTE FROM MAESTRO_PLASTICO_TEBCA";

        String sql1 = "SELECT (\n"
                + "SELECT ACVALUE AS IP FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustes_novotran_ip'\n"
                + ") AS IP,\n"
                + "--UNION\n"
                + "(\n"
                + "SELECT  ACVALUE AS PORT FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustes_novotran_port'\n"
                + ") AS PORT,\n"
                + "--UNION\n"
                + "(\n"
                + "SELECT ACVALUE AS TERMINAL FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustes_novotran_terminal'\n"
                + ") AS TERMINAL,\n"
                + "--UNION\n"
                + "(\n"
                + "SELECT ACVALUE AS TIMEOUT FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustes_novotran_timeout'\n"
                + ") AS TIMEOUT\n"
                + " FROM systables where tabid = 1";

        Dbinterface dbi = ds.get("informix");
        Dbinterface dbo = ds.get("oracle");

        dbi.dbreset();
        TransactionHandler handler = null;
        String Terminal = "";
        String nro_cliente = "";
        String tipo_ajuste = "";
        String exptarjeta = "";

        String ConsultaFechaExp = "select substr(fec_expira,3) || substr(fec_expira,0,2) as fec_expira from MAESTRO_CONSOLIDADO_TEBCA where nro_cuenta= '" + Tarjeta.substring(0, 14) + "00" + "'";

        if (dbo.executeQuery(sql1) == 0) {
            if (dbo.nextRecord()) {
                exptarjeta = dbo.getFieldString("fec_expira");
            }

            dbo.dbClose();
        } else {
            dbo.dbClose();
            return "error";
        }

        if (dbi.executeQuery(sql1) == 0) {
            if (dbi.nextRecord()) {
                handler = new TransactionHandler(dbi.getFieldString("IP"), Integer.parseInt(dbi.getFieldString("PORT")), Integer.parseInt(dbi.getFieldString("TIMEOUT")));
                Terminal = dbi.getFieldString("TERMINAL");
            }

            dbi.dbClose();
        } else {
            dbi.dbClose();
            return "error";
        }

        String sql5 = "select count(*) as respuesta from novo_sobregiros where tarjeta=" + Tarjeta + " and estatus=3";

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

            handler.execBloqueo("0", systrace, Tarjeta, Terminal, "SERVICIO DE ACTIVACION", "nro_cliente", "717", "PB", exptarjeta);

            if (handler.getRespCode().equals("00")) {

                String Nuevosobregiro = "insert into novo_sobregiros (NRO_TARJETA, MONTO_AJUSTE,USUARIO_INGRESO,TIPO_AJUSTE) VALUES ('" + Tarjeta + "'," + Monto + ",'" + idUsuario + "'," + selectedAjuste + ")";
                String ActualizaPlastico = "UPDATE MAESTRO_PLASTICO_TEBCA SET BLOQUE= 'PB' WHERE NRO_CUENTA=0000" + Tarjeta;
                String ActualizaConsolidado = "UPDATE MAESTRO_CONSOLIDADO_TEBCA SET BLOQUE= 'PB' WHERE NRO_CUENTA=0000" + Tarjeta.substring(0, 14) + "00";

                dbo.dbreset();

                if (dbo.executeQuery(Nuevosobregiro) == 0) {
                    if (dbo.executeQuery(ActualizaPlastico) == 0) {
                        if (dbo.executeQuery(ActualizaConsolidado) == 0) {
                            dbo.dbClose();
                            return "ok";
                        } else {
                            dbo.dbClose();
                            return "error";
                        }

                    } else {
                        dbo.dbClose();
                        return "error";
                    }

                } else {
                    dbo.dbClose();
                    return "error";
                }
            } else {
                log.info("La tarjeta " + Tarjeta + "no pudo ser bloqueada, código respuesta" + handler.getRespCode());

                String Sobregiro = "insert into novo_sobregiros (NRO_TARJETA, MONTO_AJUSTE,USUARIO_INGRESO,TIPO_AJUSTE,MSG) VALUES ('" + Tarjeta + "'," + Monto + ",'" + idUsuario + "'," + selectedAjuste + ", 'La tarjeta no pudo ser bloqueada" + handler.getRespCode() + "')";
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

    ;

    @Override
    public void closeConection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
