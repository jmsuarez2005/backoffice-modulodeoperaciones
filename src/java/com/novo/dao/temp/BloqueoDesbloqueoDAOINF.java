/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.dao.temp;

import com.novo.constants.AjustesTransacciones;
import com.novo.constants.BasicConfig;
import com.novo.database.Dbinterface;
import com.novo.database.NovoDAO;
import com.novo.model.Ajuste;
import com.novo.model.TBloqueo;
import com.novo.trans.TransactionHandler;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author rgomez
 */
public class BloqueoDesbloqueoDAOINF extends NovoDAO implements BasicConfig, AjustesTransacciones {

    private List<TBloqueo> bloqueo;
    private static Logger log = Logger.getLogger(BloqueoDesbloqueoDAOINF.class);
    List<Ajuste> ajustes;

    public List<Ajuste> getAjustes() {
        return this.ajustes;
    }

    public BloqueoDesbloqueoDAOINF(String dbproperty, String[] databases, String pais) {
        super(dbproperty, databases, pais);
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

    public String Nro_Organizacion() {
        String organizacion = "";

        if (this.pais.equals("pe")) {
            organizacion = "717";
        }

        if (this.pais.equals("peusd")) {
            organizacion = "716";
        }

        if (this.pais.equals("ve")) {
            organizacion = "719";
        }
        if (this.pais.equals("co")) {
            organizacion = "713";
        }

        return organizacion;
    }

    public String RegistrarBloqueoDesblqueoDAO(String Tarjeta, String idUsuario, String selectedBloqueo) {
        //String sql2 = "Select NRO_CLIENTE FROM MAESTRO_PLASTICO_TEBCA";
        String uf = "";
        String uf2 = "";
        String trn = "";
        String sql1 = "SELECT (\nSELECT ACVALUE AS IP FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustes_novotran_ip'\n) AS IP,\n--UNION\n(\nSELECT  ACVALUE AS PORT FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustes_novotran_port'\n) AS PORT,\n--UNION\n(\nSELECT ACVALUE AS TERMINAL FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustes_novotran_terminal'\n) AS TERMINAL,\n--UNION\n(\nSELECT ACVALUE AS TIMEOUT FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustes_novotran_timeout'\n) AS TIMEOUT\n FROM informix.systables where tabid = 1";

        Dbinterface dbi = (Dbinterface) this.ds.get("informix");
        dbi.dbreset();
        Dbinterface dbo = (Dbinterface) this.ds.get("oracle");
        dbo.dbreset();
        TransactionHandler handler = null;
        String terminal = "";
        //String nro_cliente = "";
        //String tipo_ajuste = "";
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

        if (dbi.executeQuery(sql1) == 0) {
            if (dbi.nextRecord()) {
                handler = new TransactionHandler(dbi.getFieldString("IP"), Integer.parseInt(dbi.getFieldString("PORT")), Integer.parseInt(dbi.getFieldString("TIMEOUT")));
                terminal = dbi.getFieldString("TERMINAL");
            }

            dbi.dbClose();
        } else {
            dbi.dbClose();
            return "error";
        }

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
            handler.execBloqueo("0", systrace, Tarjeta, terminal, trn, "", Nro_Organizacion(), selectedBloqueo, exptarjeta);

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
                            log.info("La tarjeta " + Tarjeta + "fue bloqueada o desbloqueada, código respuesta " + handler.getRespCode());
                            return "ok";
                        }
                        dbo.dbClose();
                        log.info("La tarjeta " + Tarjeta + "no pudo ser bloqueada o desbloqueada, código respuesta " + handler.getRespCode());
                        return handler.getRespCode();
                    }

                    dbo.dbClose();
                    log.info("La tarjeta " + Tarjeta + "no pudo ser bloqueada o desbloqueada, código respuesta " + handler.getRespCode());
                    return handler.getRespCode();
                }

                dbo.dbClose();
                log.info("La tarjeta " + Tarjeta + "no pudo ser bloqueada o desbloqueada, código respuesta " + handler.getRespCode());
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
    }

    public void closeConection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TBloqueo> getBloqueo() {
        return this.bloqueo;
    }

    public void setBloqueo(List<TBloqueo> bloqueo) {
        this.bloqueo = bloqueo;
    }

}
