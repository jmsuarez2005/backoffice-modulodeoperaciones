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
import com.novo.model.Parametro;
import com.novo.model.Renovacion;
import com.novo.model.ValoresRen;
import com.novo.process.RenovacionProc;
import com.novo.trans.TransactionHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author rgomez
 */
public class RenovacionDAO extends NovoDAO implements BasicConfig, AjustesTransacciones {

    private static Logger log = Logger.getLogger(RenovacionDAO.class);
    List<Ajuste> ajustes;

    public List<Ajuste> getAjustes() {
        return this.ajustes;
    }

    public RenovacionDAO(String dbproperty, String[] databases, String pais) {
        super(dbproperty, databases, pais);
    }

    public void setAjustes(List<Ajuste> ajustes) {
        this.ajustes = ajustes;
    }

    public ValoresRen ValoresCarga() {

         String host = "";
        String RutaOrigen = "";
        String RutaDestino = "";
        String Usuario = "";
        String nombreRenovacion = "";
        
        String sql1 = "SELECT (\n"
                + "SELECT ACVALUE AS rutaOrigen FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustesRen_rutaOrigen'\n"
                + ") AS rutaOrigen,\n"
                
                + "(\n"
                + "SELECT  ACVALUE AS rutaDestino FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustesRen_rutaDestino'\n"
                + ") AS rutaDestino,\n"
            
                + "(\n"
                + "SELECT ACVALUE AS host FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustesRen_host'\n"
                + ") AS host,\n"
               
                + "(\n"
                + "SELECT ACVALUE AS Usuario FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustesRen_usuario'\n"
                + ") AS Usuario,\n"
               
                + "(\n"
                + "SELECT ACVALUE AS NombreRenovacion FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustesRen_nombreRenovacion'\n"
                + ") AS NombreRenovacion\n"
                + " FROM systables where tabid = 1";

       
        
        ValoresRen reno = new ValoresRen();

        Dbinterface dbi = (Dbinterface) ds.get("informix");
        dbi.dbreset();
        
       

        if (dbi.executeQuery(sql1)==0) {
            if (dbi.nextRecord()) {
                RutaOrigen = dbi.getFieldString("rutaOrigen");
                RutaDestino = dbi.getFieldString("rutaDestino");
                host = dbi.getFieldString("Host");
                Usuario = dbi.getFieldString("Usuario");
                nombreRenovacion = dbi.getFieldString("NombreRenovacion");
            }

            dbi.dbClose();
        } else {

            dbi.dbClose();

        }

        reno.setHost(host);
        reno.setNombreRenovacion(nombreRenovacion);
        reno.setRutaDestino(RutaDestino);
        reno.setRutaOrigen(RutaOrigen);
        reno.setUsuario(Usuario);

        return reno;
    }

    public String InsertarRenovacion(String nombre, String path, String cant_registros, String usuario, String aplicacion, String tipo_lote, String status, String tipo_archivo, Date fecha) {

        String sql = "insert into NOVO_CARGA_ARCHIVOS (NOMBRE_ARCHIVO, PATH, CANT_REGISTROS, USUARIO_CARGA, APLICACION, TIPO_LOTE, STATUS, TIPO_ARCHIVO) VALUES ('" + nombre + "','" + path + "',NULL,'" + usuario + "','" + aplicacion + "',NULL,3,'" + tipo_archivo + "')";

        Dbinterface dbo = (Dbinterface) this.ds.get("oracle");
        dbo.dbreset();
        log.info("sql [" + sql + "]");
        if (dbo.executeQuery(sql) == 0) {
            dbo.dbClose();
            return "ok";
        }
        dbo.dbClose();
        return "error";

    }

    public List<Renovacion> QueryRenovacion(String empresa, String producto, String documentoIdentidad, String fechaIni, String fechaFin) {
        String filtro = " and ";

        boolean first = false;
        List listarenovar = new ArrayList();
        Renovacion renovar = new Renovacion();

        if (!producto.equals("")) {
            filtro = filtro + "b.prefix='" + producto + "'";
            first = true;
        }
        if (!empresa.equals("")) {
            if (first) {
                filtro = filtro + " and a.cirif_cliente=LPAD('" + empresa.trim() + "',20,0)";
            } else {
                first = true;
                filtro = filtro + " a.cirif_cliente=LPAD('" + empresa.trim() + "',20,0)";
            }
        }

        if (!documentoIdentidad.equals("")) {
            if (first) {
                filtro = filtro + " and c.id_ext_per='" + documentoIdentidad + "'";
            } else {
                first = true;
                filtro = filtro + " c.id_ext_per='" + documentoIdentidad + "'";
            }
        }

        if ((fechaIni != null) && (!fechaIni.equals(""))) {
            if (first) {
                filtro = filtro + " and d.fecha_reg>=TO_DATE('" + fechaIni.substring(0, 10) + "','YYYY-MM-DD')";
            } else {
                first = true;
                filtro = filtro + "d.fecha_reg>=TO_DATE('" + fechaIni.substring(0, 10) + "','YYYY-MM-DD')";
            }

        }

        if ((fechaFin != null) && (!fechaFin.equals(""))) {
            if (first) {
                filtro = filtro + " and TRUNC(d.fecha_reg)<=TO_DATE('" + fechaFin.substring(0, 10) + "','YYYY-MM-DD')";
            } else {
                first = true;
                filtro = filtro + " TRUNC(d.fecha_reg)<=TO_DATE('" + fechaFin.substring(0, 10) + "','YYYY-MM-DD')";
            }

        }

        String sql = "select c.id_ext_per, c.nro_cuenta, b.nombre, a.nom_cliente, d.fecha_reg  from MAESTRO_CLIENTES_TEBCA a, CONFIG_PRODUCTOS b, MAESTRO_PLASTICO_TEBCA c, NOVO_RENOVACION d where LTRIM(c.nro_cuenta,0) between b.NUMCUENTAI and b.NUMCUENTAF and LPAD(c.id_ext_emp,20,0)=a.cirif_cliente and LPAD(d.tarjeta,20,0)=c.nro_cuenta " + filtro;

        Dbinterface dbo = (Dbinterface) this.ds.get("oracle");
        dbo.dbreset();
        log.info("sql [" + sql + "]");
        if (dbo.executeQuery(sql) == 0) {
            while (dbo.nextRecord()) {
                renovar.setId_ext_per(dbo.getFieldString("id_ext_per"));
                renovar.setNom_cliente(dbo.getFieldString("nom_cliente"));
                renovar.setNombre(dbo.getFieldString("nombre"));
                renovar.setNro_cuenta(dbo.getFieldString("nro_cuenta").substring(4));
                renovar.setFec_reg(dbo.getFieldString("fecha_reg"));

                listarenovar.add(renovar);
                renovar = new Renovacion();
            }
            dbo.dbClose();
            return listarenovar;
        }
        dbo.dbClose();
        return null;
    }

    public void closeConection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
