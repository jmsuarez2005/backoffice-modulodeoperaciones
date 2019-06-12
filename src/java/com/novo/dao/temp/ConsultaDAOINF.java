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
import com.novo.model.Tarjeta;
import com.novo.objects.util.Utils;
import com.novo.trans.TransactionHandler;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author rgomez
 */
public class ConsultaDAOINF extends NovoDAO implements BasicConfig, AjustesTransacciones {

    private static Logger log = Logger.getLogger(BloqueoDesbloqueoDAOINF.class);

    public ConsultaDAOINF(String dbproperty, String[] databases, String pais) {
        super(dbproperty, databases, pais);
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

    public Tarjeta RegistrarConsultaDAO(Tarjeta Tarjeta) {
        String sql1 = "SELECT (\nSELECT ACVALUE AS IP FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustes_novotran_ip'\n) AS IP,\n--UNION\n(\nSELECT  ACVALUE AS PORT FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustes_novotran_port'\n) AS PORT,\n--UNION\n(\nSELECT ACVALUE AS TERMINAL FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustes_novotran_terminal'\n) AS TERMINAL,\n--UNION\n(\nSELECT ACVALUE AS TIMEOUT FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustes_novotran_timeout'\n) AS TIMEOUT\n FROM informix.systables where tabid = 1";

        Dbinterface dbi = (Dbinterface) this.ds.get("informix");
        Dbinterface dbo = (Dbinterface) this.ds.get("oracle");
        dbi.dbreset();
        TransactionHandler handler = null;
        String terminal = "";
        String nro_cliente = "";
        String tipo_ajuste = "";
        String exptarjeta = "";
       

        String sql6 = "select substr(fec_expira,3) || substr(fec_expira,0,2) as fec_expira from MAESTRO_CONSOLIDADO_TEBCA where nro_cuenta = '0000" + Tarjeta.getNroTarjeta().substring(0, 14) + "00" + "'";

        if (dbo.executeQuery(sql6) == 0) {
            if (dbo.nextRecord()) {
                exptarjeta = dbo.getFieldString("fec_expira");
            }

            dbo.dbClose();
        } else {
            dbo.dbClose();
            Tarjeta.setSaldoDisponible("La tarjeta no posee fecha de expiración");
            return Tarjeta;
        }
        

        if (dbi.executeQuery(sql1) == 0) {
            if (dbi.nextRecord()) {
                handler = new TransactionHandler(dbi.getFieldString("IP"), Integer.parseInt(dbi.getFieldString("PORT")), Integer.parseInt(dbi.getFieldString("TIMEOUT")));
                terminal = dbi.getFieldString("TERMINAL");
            }

            dbi.dbClose();
        } else {
            dbi.dbClose();
            Tarjeta.setSaldoDisponible("No se pudo consultar el saldo. No se pudo consultar la Base de Datos");
            return Tarjeta;
        }

       // Dbinterface dbo = (Dbinterface) this.ds.get("oracle");
        boolean existe = false;

        String systrace = "";
        String sql3 = "select tebcp_cpsystrace.nextval from dual ";
        if (dbo.executeQuery(sql3) == 0) {
            if (dbo.nextRecord()) {
                systrace = dbo.getFieldString("NEXTVAL");
            }
        } else {
            Tarjeta.setSaldoDisponible("No se pudo consultar el saldo. No se pudo consultar la Base de Datos");
            return Tarjeta;
        }

        handler.execSaldo("1080", systrace, Tarjeta.getNroTarjeta(), terminal, nro_cliente, Nro_Organizacion(), "0",exptarjeta);

        if (handler.getRespCode().equals("00")) {
            Tarjeta.setSaldoDisponible(Utils.formatMontoGenerico(Double.parseDouble(handler.getSaldoDisponible()) / 100.0D, "Pe"));
        } else {
            Tarjeta.setSaldoDisponible("No se pudo consultar saldo rc=" + handler.getRespCode());
        }

        return Tarjeta;
    }

    public void closeConection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
