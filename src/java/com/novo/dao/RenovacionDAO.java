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
import com.novo.model.Renovacion;
import com.novo.model.Tarjeta;
import com.novo.model.ValoresRen;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author rgomez
 */
public class RenovacionDAO extends NovoDAO implements BasicConfig, AjustesTransacciones {

    private static Logger log = Logger.getLogger(RenovacionDAO.class);
    private List<Renovacion> renovar;
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
                + "SELECT ACVALUE AS rutaOrigen FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustesRen_rutaOrigen') AS rutaOrigen,\n"
                + "(SELECT  ACVALUE AS rutaDestino FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustesRen_rutaDestino') AS rutaDestino,\n"
                + "(SELECT ACVALUE AS host FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustesRen_host') AS host,\n"
                + "(SELECT ACVALUE AS Usuario FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustesRen_usuario') AS Usuario,\n"
                + "(SELECT ACVALUE AS NombreRenovacion FROM TEB_PARAMETERS WHERE ACNAME = 'moduloAjustesRen_nombreRenovacion') AS NombreRenovacion\n"
                + " FROM DUAL where ROWNUM = 1";

        ValoresRen reno = new ValoresRen();

        //Dbinterface dbi = (Dbinterface) ds.get("informix");
        Dbinterface dbo = (Dbinterface) ds.get("oracle");
        dbo.dbreset();
        log.info("SQL [" + sql1 + "]");

        if (dbo.executeQuery(sql1) == 0) {
            if (dbo.nextRecord()) {
                RutaOrigen = dbo.getFieldString("rutaOrigen");
                RutaDestino = dbo.getFieldString("rutaDestino");
                host = dbo.getFieldString("Host");
                Usuario = dbo.getFieldString("Usuario");
                nombreRenovacion = dbo.getFieldString("NombreRenovacion");
            }

            dbo.dbClose();
        } else {

            dbo.dbClose();

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
            return "Ok";
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

        String sql = "select c.id_ext_per, c.nro_cuenta, b.nombre, a.nom_cliente, d.fecha_reg from MAESTRO_CLIENTES_TEBCA a, CONFIG_PRODUCTOS b, MAESTRO_PLASTICO_TEBCA c, NOVO_RENOVACION d where LTRIM(c.nro_cuenta,0) between b.NUMCUENTAI and b.NUMCUENTAF and c.id_ext_emp=a.cirif_cliente and LPAD(d.tarjeta,20,0)=c.nro_cuenta " + filtro;

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

    public List<Renovacion> checkTarjetasARenovarDAO(List<String> tarjetas) {
        Dbinterface dbo = ds.get("oracle");
        //Dbinterface dbi = ds.get("informix");
        Dbinterface dbo2 = ds.get("oracle");
        Renovacion renovacion = new Renovacion();
        List<Renovacion> listarenovar = new ArrayList();
        List<Renovacion> listarenovar1 = new ArrayList();
        String filtro = "(";
        String filtroUpdate = "(";
        String sql = "";
        Date fecha = new Date();
        SimpleDateFormat dia = new SimpleDateFormat("dd/MM/yyyy");
        //String rifFiltro = "";
        String nroTarjeta = "";
        List<String> nroTarjetasList = new ArrayList<String>();
        List<String> nroTarjetasList1 = new ArrayList<String>();
        String tarjetasInexis = "";
        int cantidad = 0;
        for (String tarjeta : tarjetas) {
            filtro = filtro + "'0000" + tarjeta + "',";
            filtroUpdate = filtroUpdate + "'" + tarjeta + "',";
        }
        log.info("Pais:" + pais);
//        if(!pais.equalsIgnoreCase("VE")){
//            rifFiltro = "'000000000'||";
//        }
        
        filtro = filtro.substring(0, filtro.length() - 1) + ")";
        filtroUpdate = filtroUpdate.substring(0, filtroUpdate.length() - 1) + ")";
        sql = "SELECT MCT.NRO_CUENTA, MPT.NRO_CUENTA AS NRO_TARJETA, M.COD_CLIENTE, CP.PREFIX \n"
                + "FROM MAESTRO_PLASTICO_TEBCA MPT \n"
                + "INNER JOIN MAESTRO_CONSOLIDADO_TEBCA MCT ON (SUBSTR(MPT.NRO_CUENTA,1,18) = SUBSTR(MCT.NRO_CUENTA,1,18)) \n"
                + "INNER JOIN CONFIG_PRODUCTOS CP ON MPT.SUBBIN  between substr(CP.NUMCUENTAI,0,8) and substr(CP.NUMCUENTAF,0,8) \n"
                + "INNER JOIN MAESTRO_CLIENTES_TEBCA M ON LTRIM(M.CIRIF_CLIENTE,'0') = LTRIM(MCT.ID_EXT_EMP,'0')  \n"
                + "WHERE MPT.NRO_CUENTA IN  " + filtro + " \n"
                + "AND MPT.CON_ESTATUS in (1,2,4) AND (SUBSTR(MCT.FEC_EXPIRA,3,4)||SUBSTR(MCT.FEC_EXPIRA,1,2)) <= to_char(sysdate,'YYMM') "
//                + "AND TO_NUMBER(SUBSTR(MCT.FEC_EXPIRA,3,2)||SUBSTR(MCT.FEC_EXPIRA,1,2)) >= TO_NUMBER(TO_CHAR(SYSDATE,'YYMM'))"
                + "AND MPT.FEC_BAJA IS NULL";

        log.info("sql [" + sql + "]");
        dbo.dbreset();
        if (dbo.executeQuery(sql) == 0) {
            while (dbo.nextRecord()) {
                nroTarjeta = dbo.getFieldString("NRO_TARJETA");
                nroTarjetasList.add(nroTarjeta.substring(4, nroTarjeta.length()));
                cantidad++;
                
                renovacion = new Renovacion();
                renovacion.setNro_cuenta(dbo.getFieldString("NRO_CUENTA"));
                renovacion.setNro_tarjeta(nroTarjeta);
                renovacion.setCod_cliente(dbo.getFieldString("COD_CLIENTE"));
                renovacion.setPrefix(dbo.getFieldString("PREFIX"));
                renovacion.setFec_reg(dia.format(fecha));
                renovacion.setRespuesta("Ok");
                listarenovar.add(renovacion);
                
            }
        } else {
            dbo.dbClose();
            log.info("No se pudo realizar la renovación");
            renovacion = new Renovacion();
            renovacion.setRespuesta("error");
            listarenovar1.clear();
            listarenovar1.add(renovacion);
            return listarenovar1;
        }

        //VALIDA TARJETAS EN RENOVACION
        sql = "SELECT TARJETA FROM NOVO_RENOVACION where TARJETA in " + filtro + " AND PREFIX NOT IN ('0') AND ESTATUS IN (0,3)";
        int c = 0;
        log.info("sql [" + sql + "]");
        dbo.dbreset();
        if (dbo.executeQuery(sql) == 0) {
            while (dbo.nextRecord()) {
                tarjetasInexis = tarjetasInexis + dbo.getFieldString("TARJETA").substring(4, dbo.getFieldString("TARJETA").length()) + ",";
                renovacion = new Renovacion();
                renovacion.setFec_reg(dia.format(fecha));
                renovacion.setNro_tarjeta(dbo.getFieldString("TARJETA").substring(4, dbo.getFieldString("TARJETA").length()));
                renovacion.setRespuesta("Tarjeta ya en Renovación");
                listarenovar1.add(renovacion);
                c++;
            }
        }
        if(c > 0){            
            tarjetasInexis = tarjetasInexis.substring(0, tarjetasInexis.length() - 1);
            log.info("TARJETAS NO VÁLIDAS:" + tarjetasInexis);
//            renovacion = new Renovacion();
//            renovacion.setNro_tarjeta(nroTarjeta);
//            renovacion.setRespuesta("errorT" + tarjetasInexis);
//            listarenovar.clear();
//            listarenovar.add(renovacion);
            dbo.dbClose();
            return listarenovar1;
        }else{
            log.info("TARJETAS VÁLIDAS PARA EL PROCESO DE RENOVACIÓN");
            
        }

        if (cantidad == tarjetas.size()) {
            dbo.dbClose();

            //VERIFICAR EN ORACLE SI EXISTE REPOSICIONES PENDIENTES
            //SI SE ENCUENTRAN REGISTROS DE LAS TARJETAS SELECCIONADAS, ESTAS SE ACTUALIZAN EL ESTATUS A 7 
            //SIEMPRE Y CUANDO ESTEN EN ESTATUS 1
            sql = "UPDATE teb_reposiciones SET idestatus = 7 "
                    + "WHERE idestatus = 1 AND nocuenta IN " + filtroUpdate;
            log.info("SQL:" + sql);
            dbo2.dbreset();
            if (dbo2.executeQuery(sql) == 0) {
                log.info("Oracle - Total de reposiciones anuladas = " + dbo2.rowsCount);
            } else {
                dbo2.dbClose();
                renovacion = new Renovacion();
                renovacion.setRespuesta("error");
                listarenovar1.clear();
                listarenovar1.add(renovacion);
                return listarenovar1;
            }

            dbo2.dbClose();
            //renovacion.setRespuesta("Ok");
            //listarenovar.add(renovacion);
            return listarenovar;
        } else {
            //TARJETAS INEXISTENTES Y/O NO VALIDAS PARA RENOVACION
            //COMPARA LAS DOS LISTAS Y EXTRAE DEL EXCEL AQUELLAS TARJETAS QUE NO HAN SIDO ENCONTRADAS O VALIDADAS EN LA CONSULTA DB
            Collection listOne = nroTarjetasList;
            Collection listTwo = tarjetas;

            Collection<String> similar = new HashSet<String>(listOne);
            Collection<String> different = new HashSet<String>();
            different.addAll(listOne);
            different.addAll(listTwo);

            similar.retainAll(listTwo);
            different.removeAll(similar);
            tarjetasInexis = different.toString().substring(1, different.toString().length() - 1);
            log.info("TARJETAS NO VÁLIDAS:" + tarjetasInexis);

        }
        dbo.dbClose();

        if (tarjetasInexis.equals("")) {
            renovacion = new Renovacion();
            renovacion.setRespuesta("error");
            listarenovar1.clear();
            listarenovar1.add(renovacion);
            return listarenovar1;
        } else {
            //renovacion = new Renovacion();
            //renovacion.setRespuesta("Tarjeta ya en Renovación");
            //listarenovar1.clear();
            //listarenovar1.add(renovacion);
            return listarenovar1;
        }
    }

    public String insertarNovoRenovacion(List<Renovacion> listaRenovacion) {

        Dbinterface dbo = (Dbinterface) this.ds.get("oracle");
        String sql = "INSERT ALL\n ";

        for (Renovacion ajuste : listaRenovacion) {
            if (ajuste.getNro_tarjeta() != null && !ajuste.getNro_tarjeta().equals("")) {
                sql = sql + "INTO NOVO_RENOVACION (TARJETA, CUENTA, ESTATUS, COD_CLIENTE, PREFIX) VALUES "
                        + "('" + ajuste.getNro_tarjeta() + "','" + ajuste.getNro_cuenta() + "','0','" + ajuste.getCod_cliente() + "','" + ajuste.getPrefix() + "') \n";
            }
        }
        sql = sql + " SELECT * FROM dual";
        dbo.dbreset();
        log.info("sql [" + sql + "]");
        if (dbo.executeQuery(sql) == 0) {
            log.info("Oracle - Total de renovaciones insertadas = " + dbo.rowsCount);
            dbo.dbClose();
            return "Ok";
        }
        dbo.dbClose();
        return "error";

    }

    @Override
    public void closeConection() {
        this.shutdownDatabases();
    }

    public List<Renovacion> getRenovar() {
        return renovar;
    }

    public void setRenovar(List<Renovacion> renovar) {
        this.renovar = renovar;
    }
}
