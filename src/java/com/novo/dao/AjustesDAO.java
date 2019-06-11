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
import com.novo.model.CamposActualizacion;
import com.novo.model.Empresa;
import com.novo.model.HoldCode;
import com.novo.model.Producto;
import com.novo.model.TAjuste;
import com.novo.model.Tarjeta;
import com.novo.model.Transaccion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import org.apache.log4j.Logger;

/**
 *
 * @author ggutierrez
 */
public class AjustesDAO extends NovoDAO implements BasicConfig, AjustesTransacciones {

    private static Logger log = Logger.getLogger(AjustesDAO.class);

    public AjustesDAO(String dbproperty, String[] databases, String pais) {
        super(dbproperty, databases, pais);
    }

    public List<Tarjeta> getTarjetasDAO(String dni, boolean banderaFiltro, String prefix, String rif) {
        Tarjeta tarjeta = new Tarjeta();
        List<Tarjeta> tarjetas = new ArrayList<Tarjeta>();
        String sql = "";
        Dbinterface dbo = ds.get("oracle");
        Dbinterface dbo2 = ds.get("oracle");

        if (!banderaFiltro) { //cambio [!]
            sql = "select mpt.*,mct.FEC_EXPIRA from  maestro_plastico_tebca mpt, maestro_consolidado_tebca mct where mpt.nro_cuenta = '0000" + dni + "' and  substr(mpt.NRO_CUENTA,0,18) = substr(mct.NRO_CUENTA,0,18) and mct.CON_ESTATUS in ('1','2') ";
            log.info("query get tarjetas dao " + sql);
        } else {
            sql = "select cp.*,mpt.*,mct.FEC_EXPIRA from CONFIG_PRODUCTOS cp , maestro_plastico_tebca mpt, maestro_consolidado_tebca mct where mpt.SUBBIN  between substr(cp.NUMCUENTAI,0,8) and substr(cp.NUMCUENTAF,0,8) ";

            if (dni != null && !dni.equals("")) {
                sql = sql + "and mpt.id_ext_per = '" + dni + "'";
            }

            sql = sql + " and substr(mpt.NRO_CUENTA,0,18) = substr(mct.NRO_CUENTA,0,18) and mct.CON_ESTATUS in ('1','2') ";

            log.info("query get tarjetas dao" + sql);

            if (!prefix.equals("") && prefix != null) {
                sql += " and cp.prefix = '" + prefix + "'";
            }

            if (rif != null) {
                if (!rif.equals("")) {
                    sql += " and mpt.id_ext_emp = '" + rif.trim() + "'";
                }
            }
        }
        dbo.dbreset();
        log.info("sql [" + sql + "]");
        if (dbo.executeQuery(sql) == 0) {
            while (dbo.nextRecord()) {
                tarjeta.setNroTarjeta(dbo.getFieldString("nro_cuenta").substring(4));
                tarjeta.setIdExtPer(dbo.getFieldString("id_ext_per"));
                tarjeta.setNombreCliente(dbo.getFieldString("nom_cliente"));
                tarjeta.setMascara(tarjeta.getNroTarjeta().substring(0, 6) + "******" + tarjeta.getNroTarjeta().substring(12));
                tarjeta.setCardProgram(dbo.getFieldString("DESCRIPCION"));
                tarjeta.setIdExtEmp(dbo.getFieldString("id_ext_emp"));
                tarjeta.setFechaExpiracion(dbo.getFieldString("FEC_EXPIRA"));
                tarjetas.add(tarjeta);
                tarjeta = new Tarjeta();
            }
        }
        dbo2.dbreset();
        for (Tarjeta tarjetaAux : tarjetas) {
            dbo2.dbreset();
            //sql = "select acnombre from teb_productos where SUBSTR(acnumcuentai,1,8) ='" + tarjetaAux.getNroTarjeta().substring(0,8) + "'";
            ////sql = "select acnomciacorto from empresas where acrif = '" + tarjetaAux.getIdExtEmp() + "'";
            sql = "select NOMBRE_CORTO AS acnomciacorto from MAESTRO_CLIENTES_TEBCA where LTRIM(CIRIF_CLIENTE,'0') = '" + tarjetaAux.getIdExtEmp() + "'";
            log.info("sql [" + sql + "]");
            if (dbo2.executeQuery(sql) == 0) {
                if (dbo2.nextRecord()) {
                    tarjetaAux.setNombreEmpresa(dbo2.getFieldString("acnomciacorto"));
                }
            }
        }
        dbo.dbClose();
        dbo2.dbClose();
        return tarjetas;
    }

    public List<Tarjeta> getTarjetasFiltrosDAO(String dni, String nroTarjeta, String prefix, String nombreEmpresa) {
        Tarjeta tarjeta = new Tarjeta();
        List<Tarjeta> tarjetas = new ArrayList<Tarjeta>();
        String sql = "";
        Dbinterface dbo = ds.get("oracle");
        Dbinterface dbo2 = ds.get("oracle");
        //Dbinterface dbi = ds.get("informix");
        String rif = "";

        if (!nombreEmpresa.equals("")) {
            dbo2.dbreset();
            //sql = "select acnombre from teb_productos where SUBSTR(acnumcuentai,1,8) ='" + tarjetaAux.getNroTarjeta().substring(0,8) + "'";
            ////sql = "select acrif from empresas where acnomciacorto like '%" + nombreEmpresa.toUpperCase().trim() + "%'";
            sql = "select LTRIM(CIRIF_CLIENTE,'0') AS acrif from MAESTRO_CLIENTES_TEBCA where NOMBRE_CORTO like '%" + nombreEmpresa.toUpperCase().trim() + "%'";
            log.info("sql [" + sql + "]");
            if (dbo2.executeQuery(sql) == 0) {
                while (dbo2.nextRecord()) {
                    rif = rif + dbo2.getFieldString("acrif").trim() + "','";
                }
            }
            if (!rif.equals("")) {
                rif = "'" + rif.substring(0, rif.length() - 2);
            }
        }

        sql = "select cp.*,mpt.*,mct.FEC_EXPIRA from CONFIG_PRODUCTOS cp , maestro_plastico_tebca mpt, maestro_consolidado_tebca mct where mpt.SUBBIN  between substr(cp.NUMCUENTAI,0,8) and substr(cp.NUMCUENTAF,0,8) ";

        if (dni != null && !dni.equals("")) {
            sql = sql + "and mpt.id_ext_per = '" + dni + "'";
        }

        sql = sql + " and substr(mpt.NRO_CUENTA,0,18) = substr(mct.NRO_CUENTA,0,18) and mct.CON_ESTATUS in ('1','2') ";

        if (!prefix.equals("") && prefix != null) {
            sql += " and cp.prefix = '" + prefix + "'";
        }

        if (rif != null) {
            if (!rif.equals("")) {
                sql += " and mpt.id_ext_emp in (" + rif.trim() + ")";
            }
        }

        if (nroTarjeta != null) {
            if (!nroTarjeta.equals("")) {
                sql += " and mpt.nro_cuenta = '0000" + nroTarjeta.trim() + "'";
            }
        }

        dbo.dbreset();
        log.info("sql [" + sql + "]");
        if (dbo.executeQuery(sql) == 0) {
            while (dbo.nextRecord()) {
                tarjeta.setNroTarjeta(dbo.getFieldString("nro_cuenta").substring(4));
                tarjeta.setIdExtPer(dbo.getFieldString("id_ext_per"));
                tarjeta.setNombreCliente(dbo.getFieldString("nom_cliente"));
                tarjeta.setMascara(tarjeta.getNroTarjeta().substring(0, 6) + "******" + tarjeta.getNroTarjeta().substring(12));
                tarjeta.setCardProgram(dbo.getFieldString("DESCRIPCION"));
                tarjeta.setIdExtEmp(dbo.getFieldString("id_ext_emp"));
                tarjeta.setFechaExpiracion(dbo.getFieldString("FEC_EXPIRA"));
                tarjetas.add(tarjeta);
                tarjeta = new Tarjeta();
            }
        }
        dbo2.dbreset();
        for (Tarjeta tarjetaAux : tarjetas) {
            dbo2.dbreset();
            //sql = "select acnombre from teb_productos where SUBSTR(acnumcuentai,1,8) ='" + tarjetaAux.getNroTarjeta().substring(0,8) + "'";
            /////sql = "select acnomciacorto from empresas where acrif = '" + tarjetaAux.getIdExtEmp() + "'";
            sql = "select NOMBRE_CORTO AS acnomciacorto from MAESTRO_CLIENTES_TEBCA where LTRIM(CIRIF_CLIENTE,'0') = '" + tarjetaAux.getIdExtEmp() + "'";
            log.info("sql [" + sql + "]");
            if (dbo2.executeQuery(sql) == 0) {
                if (dbo2.nextRecord()) {
                    tarjetaAux.setNombreEmpresa(dbo2.getFieldString("acnomciacorto"));
                }
            }
        }
        dbo.dbClose();
        dbo2.dbClose();
        return tarjetas;
    }

    public List<Tarjeta> getTarjetasFiltrosTransaccionesDAO(String dni, String nroTarjeta, String prefix, String nombreEmpresa, String fechaIni, String fechaFin) {
        Tarjeta tarjeta = new Tarjeta();
        List<Tarjeta> tarjetas = new ArrayList<Tarjeta>();
        String sql = "";
        Dbinterface dbo = ds.get("oracle");
        Dbinterface dbo2 = ds.get("oracle");
        //Dbinterface dbi = ds.get("informix");
        String rif = "";

        if (!nombreEmpresa.equals("")) {
            dbo2.dbreset();
            //sql = "select acnombre from teb_productos where SUBSTR(acnumcuentai,1,8) ='" + tarjetaAux.getNroTarjeta().substring(0,8) + "'";
            //////sql = "select acrif from empresas where acnomciacorto like '%" + nombreEmpresa.toUpperCase().trim() + "%'";
            sql = "select LTRIM(CIRIF_CLIENTE,'0') AS acrif from MAESTRO_CLIENTES_TEBCA where NOMBRE_CORTO like '%" + nombreEmpresa.toUpperCase().trim() + "%'";
            log.info("sql [" + sql + "]");
            if (dbo2.executeQuery(sql) == 0) {
                while (dbo2.nextRecord()) {
                    rif = rif + dbo2.getFieldString("acrif").trim() + "','";
                }
            }
            if (!rif.equals("")) {
                rif = "'" + rif.substring(0, rif.length() - 2);
            }
        }

        sql = "select cp.*,mpt.*,mct.FEC_EXPIRA from CONFIG_PRODUCTOS cp , maestro_plastico_tebca mpt, maestro_consolidado_tebca mct where mpt.SUBBIN  between substr(cp.NUMCUENTAI,0,8) and substr(cp.NUMCUENTAF,0,8) "
                + "and trunc(mct.FEC_ULT_ACTIVIDAD) between to_date('" + fechaIni + "','DDMMYYYY') and to_date('" + fechaFin + "','DDMMYYYY') ";

        if (dni != null && !dni.equals("")) {
            sql = sql + "and mpt.id_ext_per = '" + dni + "'";
        }

        sql = sql + " and substr(mpt.NRO_CUENTA,0,18) = substr(mct.NRO_CUENTA,0,18) and mct.CON_ESTATUS in ('1','2') ";

        if (!prefix.equals("") && prefix != null) {
            sql += " and cp.prefix = '" + prefix + "'";
        }

        if (rif != null) {
            if (!rif.equals("")) {
                sql += " and mpt.id_ext_emp in (" + rif.trim() + ")";
            }
        }

        if (nroTarjeta != null) {
            if (!nroTarjeta.equals("")) {
                sql += " and mpt.nro_cuenta = '0000" + nroTarjeta.trim() + "'";
            }
        }

        dbo.dbreset();
        log.info("sql [" + sql + "]");
        if (dbo.executeQuery(sql) == 0) {
            while (dbo.nextRecord()) {
                tarjeta.setNroTarjeta(dbo.getFieldString("nro_cuenta").substring(4));
                tarjeta.setIdExtPer(dbo.getFieldString("id_ext_per"));
                tarjeta.setNombreCliente(dbo.getFieldString("nom_cliente"));
                tarjeta.setMascara(tarjeta.getNroTarjeta().substring(0, 6) + "******" + tarjeta.getNroTarjeta().substring(12));
                tarjeta.setCardProgram(dbo.getFieldString("DESCRIPCION"));
                tarjeta.setIdExtEmp(dbo.getFieldString("id_ext_emp"));
                tarjeta.setFechaExpiracion(dbo.getFieldString("FEC_EXPIRA"));
                tarjetas.add(tarjeta);
                tarjeta = new Tarjeta();
            }
        }
        dbo2.dbreset();
        for (Tarjeta tarjetaAux : tarjetas) {
            dbo2.dbreset();
            //sql = "select acnombre from teb_productos where SUBSTR(acnumcuentai,1,8) ='" + tarjetaAux.getNroTarjeta().substring(0,8) + "'";
            //////sql = "select acnomciacorto from empresas where acrif = '" + tarjetaAux.getIdExtEmp() + "'";
            sql = "select NOMBRE_CORTO AS acnomciacorto from MAESTRO_CLIENTES_TEBCA where LTRIM(CIRIF_CLIENTE,'0') = '" + tarjetaAux.getIdExtEmp() + "'";
            log.info("sql [" + sql + "]");
            if (dbo2.executeQuery(sql) == 0) {
                if (dbo2.nextRecord()) {
                    tarjetaAux.setNombreEmpresa(dbo2.getFieldString("acnomciacorto"));
                }
            }
        }
        dbo.dbClose();
        dbo2.dbClose();
        return tarjetas;
    }

    public List<Tarjeta> getTarjetasDAO(String filtro, boolean banderaFiltro) {
        Tarjeta tarjeta = new Tarjeta();
        List<Tarjeta> tarjetas = new ArrayList<Tarjeta>();
        String sql = "";
        Dbinterface dbo = ds.get("oracle");
        //Dbinterface dbi = ds.get("informix");
        if (banderaFiltro == false) {
            sql = "select * from MAESTRO_PLASTICO_TEBCA where nro_cuenta = '0000" + filtro + "'";
        } else {
            sql = "select * from MAESTRO_PLASTICO_TEBCA where id_ext_per = '" + filtro + "'";
        }
        dbo.dbreset();
        log.info("sql [" + sql + "]");
        if (dbo.executeQuery(sql) == 0) {
            while (dbo.nextRecord()) {
                tarjeta.setNroTarjeta(dbo.getFieldString("nro_cuenta").substring(4));
                tarjeta.setIdExtPer(dbo.getFieldString("id_ext_per"));
                tarjeta.setIdExtEmp(dbo.getFieldString("id_ext_emp"));
                tarjeta.setNombreCliente(dbo.getFieldString("nom_cliente"));
                tarjeta.setMascara(tarjeta.getNroTarjeta().substring(0, 6) + "******" + tarjeta.getNroTarjeta().substring(12));
                tarjetas.add(tarjeta);
                tarjeta = new Tarjeta();
            }
        }
        //dbi.dbreset();

        for (Tarjeta tarjetaAux : tarjetas) {
            dbo.dbreset();
            sql = "select nombre from config_productos where SUBSTR(numcuentai,1,8) ='" + tarjetaAux.getNroTarjeta().substring(0, 8) + "'";
            log.info("sql [" + sql + "]");
            if (dbo.executeQuery(sql) == 0) {
                if (dbo.nextRecord()) {
                    tarjetaAux.setCardProgram(dbo.getFieldString("nombre"));
                }
                String sql1 = "select NOMBRE_CORTO from MAESTRO_CLIENTES_TEBCA where CIRIF_CLIENTE like '%" + tarjetaAux.getIdExtEmp() + "'";
                log.info("sql [" + sql1 + "]");
                if (dbo.executeQuery(sql1) == 0) {
                    if (dbo.nextRecord()) {
                        tarjetaAux.setNombreEmpresa(dbo.getFieldString("NOMBRE_CORTO"));
                    }
                }

            }

        }
        dbo.dbClose();
        //dbi.dbClose();
        return tarjetas;
    }

    /**
     * obtiene la lista de productos disponibles.
     */
    public List<Producto> getProductosDAO() {
        String sql = "";
        Dbinterface dbo = ds.get("oracle");
        List<Producto> productos = new ArrayList<Producto>();
        Producto producto = new Producto();
        sql = "select nombre,prefix,descripcion from config_productos order by descripcion asc";
        log.info("sql [" + sql + "]");
        dbo.dbreset();
        if (dbo.executeQuery(sql) == 0) {
            while (dbo.nextRecord()) {
                producto.setNombre(dbo.getFieldString("nombre"));
                producto.setPrefix(dbo.getFieldString("prefix"));
                producto.setDescripcion(dbo.getFieldString("descripcion"));
                productos.add(producto);
                producto = new Producto();
            }
        }
        dbo.dbClose();
        return productos;
    }

    public List<Empresa> getEmpresasDAO() {
        //Dbinterface dbi = ds.get("informix");
        Dbinterface dbo = ds.get("oracle");
        //String sql = "select accodcia, acnomcia, acrif,acnomciacorto  from empresas where cstatus = 'A' order by acnomciacorto asc";
        String sql = "select COD_CLIENTE AS accodcia, NOM_CLIENTE AS acnomcia, LTRIM(CIRIF_CLIENTE,'0') AS acrif,NOMBRE_CORTO AS acnomciacorto  from MAESTRO_CLIENTES_TEBCA where cstatus = 'A' order by NOMBRE_CORTO asc";
        List<Empresa> empresas = new ArrayList<Empresa>();
        Empresa empresa = new Empresa();
        log.info("sql [" + sql + "]");
        if (dbo.executeQuery(sql) == 0) {
            while (dbo.nextRecord()) {
                empresa.setAccodcia(dbo.getFieldString("accodcia"));
                empresa.setAcnomcia(dbo.getFieldString("acnomcia"));
                empresa.setRif(dbo.getFieldString("acrif"));
                empresa.setNombreCorto(dbo.getFieldString("acnomciacorto"));
                empresas.add(empresa);
                empresa = new Empresa();
            }
        }
        return empresas;
    }

    /**
     * obtiene las transacciones de detalle_transacciones_tebca por la tarjeta
     * enviada por parametros
     */
    public List<Transaccion> getTransaccionesDAO(String tarjeta) {
        Transaccion transaccion = new Transaccion();
        List<Transaccion> transacciones = new ArrayList<Transaccion>();
        String sql = "";
        Dbinterface dbo = ds.get("oracle");
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator('.');
        double aux = 0;
        NumberFormat doublef = new DecimalFormat("#0.00", otherSymbols);
        if (!tarjeta.equals("")) {
            sql = "select * from detalle_transacciones_tebca where nro_tarjeta = '0000" + tarjeta + "' order by FEC_TRANSACCION desc";
            dbo.dbreset();
            log.info("sql [" + sql + "]");
            if (dbo.executeQuery(sql) == 0) {
                while (dbo.nextRecord()) {
                    transaccion.setDescripcion(dbo.getFieldString("NOMBRE_CIUDAD_EDO"));
                    aux = Double.parseDouble(dbo.getFieldString("MON_TRANSACCION"));
                    aux = aux / 100;
                    transaccion.setMonto(dbo.getFieldString("SIGNO") + doublef.format(aux));
                    transaccion.setNroTarjeta(tarjeta);
                    transaccion.setMascara(tarjeta.substring(0, 6) + "******" + tarjeta.substring(12));
                    transaccion.setSystrace(dbo.getFieldString("REF_INTERNA"));
                    transaccion.setFechaTransaccion(dbo.getFieldString("FEC_TRANSACCION"));
                    transacciones.add(transaccion);
                    transaccion = new Transaccion();
                }
            } else {
                log.error("error ejecutando query getTransacciones()");
            }
        }
        dbo.dbClose();
        return transacciones;
    }

    public List<TAjuste> getTipoAjustesDAO() {
        List<TAjuste> tipoAjustes = new ArrayList<TAjuste>();
        TAjuste tipoAjuste = new TAjuste();
        String sql = "select * from NOVO_CODIGO_AJUSTES order by DESCRIPCION";
        Dbinterface dbo = ds.get("oracle");
        dbo.dbreset();
        log.info("Sql [" + sql + "]");
        if (dbo.executeQuery(sql) == 0) {
            while (dbo.nextRecord()) {
                tipoAjuste.setCodigo(dbo.getFieldString("COD_AJUSTE").trim());
                tipoAjuste.setDescripcion(dbo.getFieldString("DESCRIPCION").trim());
                tipoAjuste.setIdCodigoAjuste(dbo.getFieldString("ID_AJUSTE").trim());
                tipoAjustes.add(tipoAjuste);
                tipoAjuste = new TAjuste();
            }
        }
        dbo.dbClose();
        return tipoAjustes;
    }

    public String RegistrarAjusteDAO(String tarjeta, String monto, String codigoAjuste, String referencia, String usuario, String observacion, boolean traslado) {
        monto = monto.replaceAll("-", "");
        monto = monto.replace(",", ".");
        String[] montos = monto.split(".");
        log.info(monto);
        log.info(Integer.valueOf(montos.length));

        if (!monto.contains(".")) {
            monto = monto + ".00";
        }

        log.info(monto);
        if (!monto.matches("\\d{1,15}.\\d{1,2}")) {
            log.info("Formato de monto inválido");
            return "error";
        }

        String status = "";

        String dtproceso = "";

        if ((traslado) && ((codigoAjuste.equals("15")) || (codigoAjuste.equals("14")))) {
            status = "2";
            dtproceso = "NULL";
        } else {
            status = "3";
            dtproceso = "NULL";
        }

        String a = referencia.substring(referencia.length() - 6, referencia.length());
        
        String sql = "insert into novo_detalle_ajustes (ID_REGISTRO,FECHA,TARJETA,MONTO,STATUS,USUARIO,ID_CODIGO_AJUSTE,DTREGISTRO,AUTORIZACION,descripcion,cod_operacion,cod_moneda,cat_comercio,dtproceso, observacion) values (SEQ_ID_DETALLE_AJUSTE.NEXTVAL,sysdate,'" + tarjeta + "'," + monto + ",'" + status + "','" + usuario + "','" + codigoAjuste + "',SYSDATE,'" + a + "',(select descripcion from novo_codigo_ajustes where id_ajuste ='" + codigoAjuste + "'),(select COD_OPERACION from novo_codigo_ajustes where id_ajuste ='" + codigoAjuste + "'),'604','0000'," + dtproceso + ", '" + observacion + "')";
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

    public List<Ajuste> getAjustesDAO(String status, String usuario, Date fechaIni, Date fechaFin) {
        List<Ajuste> ajustes = new ArrayList<Ajuste>();
        Ajuste ajuste = new Ajuste();
        String aux = "";
        double daux = 0;

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator('.');
        NumberFormat doublef = new DecimalFormat("#0.00", otherSymbols);
        String sql = "select nda.*,NCA.DESCRIPCION AS DESCRIPCION2,NCA.ID_AJUSTE,mpt.NOM_CLIENTE,mpt.ID_EXT_PER  from novo_detalle_ajustes nda join novo_codigo_ajustes nca on (nda.ID_CODIGO_AJUSTE =nca.ID_AJUSTE) join maestro_plastico_tebca mpt on (nda.tarjeta=0000+mpt.NRO_CUENTA) where ";

        if (fechaIni != null && fechaFin != null) {
            DateFormat df = new SimpleDateFormat("ddMMyyyy");
            aux = " trunc(nda.fecha) between to_date('" + df.format(fechaIni) + "','DDMMYYYY') and to_date('" + df.format(fechaFin) + "','DDMMYYYY') ";
            sql = sql + aux;
        }

        if (!status.equals("Todos")) {
            sql = sql + " and status = '" + status + "'";
        }

        if (!usuario.equals("Todos")) {
            sql = sql + " and usuario = '" + usuario + "'";
        }

        sql = sql + " order by nda.ID_REGISTRO, nda.FECHA";

        Dbinterface dbo = ds.get("oracle");
        dbo.dbreset();
        log.info("sql [" + sql + "]");
        if (dbo.executeQuery(sql) == 0) {
            while (dbo.nextRecord()) {
                ajuste.setTarjeta(dbo.getFieldString("TARJETA"));
                daux = Double.parseDouble(dbo.getFieldString("MONTO"));
                ajuste.setMonto(doublef.format(daux));
                ajuste.setFecha(dbo.getFieldString("FECHA"));
                ajuste.setIdCodigoAjuste(dbo.getFieldString("ID_CODIGO_AJUSTE"));
                ajuste.setStatus(dbo.getFieldString("STATUS"));
                ajuste.setIdDetalleAjuste(dbo.getFieldString("ID_REGISTRO"));
                ajuste.setMascara(ajuste.getTarjeta().substring(0, 6) + "******" + ajuste.getTarjeta().substring(12));
                ajuste.setNomCliente(dbo.getFieldString("NOM_CLIENTE"));
                ajuste.setIdExtPer(dbo.getFieldString("ID_EXT_PER"));
                if (ajuste.getStatus().equals("2")) {
                    ajuste.setDescStatus("Procesado");
                }
                if (ajuste.getStatus().equals("3")) {
                    ajuste.setDescStatus("Pendiente");
                }
                if (ajuste.getStatus().equals("0")) {
                    ajuste.setDescStatus("Autorizado");
                }
                if (ajuste.getStatus().equals("7")) {
                    ajuste.setDescStatus("Anulado");
                }
                if (ajuste.getStatus().equals("1")) {
                    ajuste.setDescStatus("En Proceso");
                }
                ajuste.setUsuario(dbo.getFieldString("USUARIO"));
                ajuste.setDescripcion(dbo.getFieldString("DESCRIPCION2"));
                ajuste.setObservacion(dbo.getFieldString("OBSERVACION"));
                if (ajuste.getObservacion().equals("")) {
                    ajuste.setObservacion("N/A");
                }
                ajustes.add(ajuste);
                ajuste = new Ajuste();
            }
        }
        dbo.dbClose();
        return ajustes;
    }

    public List<Ajuste> getAjustesDAO(String status, String usuario, Date fechaIni, Date fechaFin, String filtro) {
        List<Ajuste> ajustes = new ArrayList<Ajuste>();
        Ajuste ajuste = new Ajuste();
        String aux = "";
        double daux = 0;

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator('.');
        NumberFormat doublef = new DecimalFormat("#0.00", otherSymbols);
        String sql = "select nda.TARJETA,nda.MONTO,nda.STATUS,nda.FECHA,nda.ID_CODIGO_AJUSTE,nda.ID_REGISTRO,nda.USUARIO,nda.OBSERVACION,NCA.DESCRIPCION AS DESCRIPCION2,NCA.ID_AJUSTE,mpt.NOM_CLIENTE,mpt.ID_EXT_PER, mct.ID_EXT_EMP "
                + "from novo_detalle_ajustes nda "
                + "inner join novo_codigo_ajustes nca on (nda.ID_CODIGO_AJUSTE =nca.ID_AJUSTE) "
                + "inner join maestro_plastico_tebca mpt on (nda.tarjeta=0000+mpt.NRO_CUENTA) "
                + "inner join MAESTRO_CONSOLIDADO_TEBCA mct on SUBSTR(mct.NRO_CUENTA,1,18) = SUBSTR(mpt.NRO_CUENTA,1,18) "
                + "where ";

        if (fechaIni != null && fechaFin != null) {
            DateFormat df = new SimpleDateFormat("ddMMyyyy");
            aux = " trunc(nda.fecha) between to_date('" + df.format(fechaIni) + "','DDMMYYYY') and to_date('" + df.format(fechaFin) + "','DDMMYYYY') ";
            sql = sql + aux;
        }

        if (!status.equals("Todos")) {
            sql = sql + " and status = '" + status + "'";
        }

        if (!usuario.equals("Todos")) {
            sql = sql + " and usuario = '" + usuario + "'";
        }

        sql = sql + " order by ";

        if (filtro.equals("Fecha")) {
            sql = sql + "nda.ID_REGISTRO, nda.FECHA";
        } else if (filtro.equals("Tarjeta")) {
            sql = sql + "nda.TARJETA";
        } else if (filtro.equals("Monto")) {
            sql = sql + "nda.MONTO";
        } else if (filtro.equals("DNI")) {
            sql = sql + "mpt.ID_EXT_PER";
        } else if (filtro.equals("Nombre")) {
            sql = sql + "mpt.NOM_CLIENTE";
        }else if (filtro.equals("Empresa")){
            sql = sql + "mct.ID_EXT_EMP";
        }else if (filtro.equals("Usuario")){
            sql = sql + "nda.USUARIO";
        } else if (filtro.equals("Tipo Ajuste")) {
            sql = sql + "NCA.DESCRIPCION";
        } else if (filtro.equals("Estatus")) {
            sql = sql + "nda.STATUS";
        } else if (filtro.equals("Observacion")) {
            sql = sql + "nda.OBSERVACION";
        }

        Dbinterface dbo = ds.get("oracle");
        dbo.dbreset();
        log.info("sql [" + sql + "]");
        if (dbo.executeQuery(sql) == 0) {
            while (dbo.nextRecord()) {
                ajuste.setTarjeta(dbo.getFieldString("TARJETA"));
                daux = Double.parseDouble(dbo.getFieldString("MONTO"));
                ajuste.setMonto(doublef.format(daux));
                ajuste.setFecha(dbo.getFieldString("FECHA"));
                ajuste.setIdCodigoAjuste(dbo.getFieldString("ID_CODIGO_AJUSTE"));
                ajuste.setStatus(dbo.getFieldString("STATUS"));
                ajuste.setIdDetalleAjuste(dbo.getFieldString("ID_REGISTRO"));
                ajuste.setMascara(ajuste.getTarjeta().substring(0, 6) + "******" + ajuste.getTarjeta().substring(12));
                ajuste.setNomCliente(dbo.getFieldString("NOM_CLIENTE"));
                ajuste.setIdExtPer(dbo.getFieldString("ID_EXT_PER"));
                ajuste.setIdExtEmp(dbo.getFieldString("ID_EXT_EMP"));
                if (ajuste.getStatus().equals("2")) {
                    ajuste.setDescStatus("Procesado");
                }
                if (ajuste.getStatus().equals("3")) {
                    ajuste.setDescStatus("Pendiente");
                }
                if (ajuste.getStatus().equals("0")) {
                    ajuste.setDescStatus("Autorizado");
                }
                if (ajuste.getStatus().equals("7")) {
                    ajuste.setDescStatus("Anulado");
                }
                if (ajuste.getStatus().equals("1")) {
                    ajuste.setDescStatus("En Proceso");
                }
                ajuste.setUsuario(dbo.getFieldString("USUARIO"));
                ajuste.setDescripcion(dbo.getFieldString("DESCRIPCION2"));
                ajuste.setObservacion(dbo.getFieldString("OBSERVACION"));
                if (ajuste.getObservacion().equals("")) {
                    ajuste.setObservacion("N/A");
                }
                ajustes.add(ajuste);
                ajuste = new Ajuste();
            }
        }
        dbo.dbClose();
        return ajustes;
    }

    public List<String> getUsuariosDao() {
        //Dbinterface dbi = ds.get("informix");
        Dbinterface dbo = ds.get("oracle");

        List<String> usuarios = new ArrayList<String>();

        // String sql = "select idusuario from teb_adm_usuario where acestatus = 'A' order by idusuario";
        String sql = "select idusuario from teb_adm_usuario where acestatus = 'A' order by idusuario";
        dbo.dbreset();
        log.info("sql [" + sql + "]");
        if (dbo.executeQuery(sql) == 0) {
            usuarios.add("Todos");
            while (dbo.nextRecord()) {
                usuarios.add(dbo.getFieldString("idusuario").trim());
            }
        }
        dbo.dbClose();
        return usuarios;
    }

    public String updateAjustesDAO(String[] idAjustes, String status) {
        Dbinterface dbo = ds.get("oracle");
        String filtro = "(";
        String filtroStatus = "";
        for (String idAjuste : idAjustes) {
            filtro += "'" + idAjuste + "',";
        }
        filtro = filtro.substring(0, filtro.length() - 1) + ")";

        if (status.equals("7")) { //ajuste autorizado(0) puede ser anulado(7)
            filtroStatus = "2,7";
        } else {
            filtroStatus = "2,7,0";
        }

        String sql = "update novo_detalle_ajustes set status = '" + status + "' where id_REGISTRO in " + filtro + " and status not in (" + filtroStatus + ")";
        dbo.dbreset();
        log.info("sql [" + sql + "]");
        if (dbo.executeQuery(sql) == 0) {
            dbo.dbClose();
            log.info("Oracle - total de registros afectados = " + dbo.rowsCount);
            if (dbo.rowsCount > 0) {
                return "ok";
            } else {
                return "vacio";
            }
        } else {
            dbo.dbClose();
            log.error("error actualizando el status de los ajustes");
            return "error";
        }

    }

    //Metodo agregado del actual en produccion updateCampos
    public String updateCampos(String tarjeta, String nombre, String apellido, String dni, String pais) {
        Dbinterface dbo = (Dbinterface) this.ds.get("oracle");
        //Dbinterface dbi = (Dbinterface) this.ds.get("informix");

        String id = "";
        String ConsultaCliente = "select LTRIM (NRO_CLIENTE,0) AS NRO_CLIENTE FROM MAESTRO_PLASTICO_TEBCA WHERE NRO_CUENTA='0000" + tarjeta + "'";
        log.info(ConsultaCliente);

        dbo.dbreset();

        if (dbo.executeQuery(ConsultaCliente) == 0) {
            while (dbo.nextRecord()) {
                id = dbo.getFieldString("NRO_CLIENTE").trim();
            }
        }

        String ActualizarSQL = "update TARJETAHABIENTE set status = 0, ";

        if (!nombre.equals("")) {
            ActualizarSQL = ActualizarSQL + "NOMBRES = '" + nombre + "', ";
        }

        if (!apellido.equals("")) {
            ActualizarSQL = ActualizarSQL + "APELLIDOS = '" + apellido + "', ";
        }

        if (!dni.equals("") && pais.equalsIgnoreCase("ve")) {
            ActualizarSQL = ActualizarSQL + "ID_EXT_PER = '" + dni + "', ID_INTERNO = '" + dni + "', ";
        } else if (!dni.equals("")) {
            ActualizarSQL = ActualizarSQL + "ID_EXT_PER = '" + dni + "', ";
        }

        ActualizarSQL = ActualizarSQL.substring(0, ActualizarSQL.length() - 2) + " where ID_INTERNO= '" + id + "'";

        log.info("SQL [" + ActualizarSQL + "]");

        if (dbo.executeQuery(ActualizarSQL) == 0) {
            dbo.dbClose();
            log.info("Oracle - total de registros afectados =" + dbo.rowsCount);
        } else {
            dbo.dbClose();
            log.error("Oracle - Error actualizando los campos");
            return "error";
        }

//        if (dbi.executeQuery(ActualizarSQL) == 0) {
//            dbi.dbClose();
//            log.info("Informix - total de registros afectados =" + dbi.rowsCount);
//        } else {
//            dbi.dbClose();
//            log.error("Informix - Error actualizando los campos");
//            return "error";
//        }
        return "success";
    }

    //Metodo agregado del actual en produccion updateCampos
    public String updateAfiliacion(String tarjeta, String nombre, String apellido, String dni, String pais) {
        Dbinterface dbo = (Dbinterface) this.ds.get("oracle");
        //Dbinterface dbi = (Dbinterface) this.ds.get("informix");

        String id = "";
        boolean exist = false;
        String tarjetaDni = "";
        String ConsultaCliente = "select LTRIM (NRO_CLIENTE,0) AS NRO_CLIENTE FROM MAESTRO_PLASTICO_TEBCA WHERE NRO_CUENTA='0000" + tarjeta + "'";

        log.info(ConsultaCliente);

        dbo.dbreset();

        if (dbo.executeQuery(ConsultaCliente) == 0) {
            while (dbo.nextRecord()) {
                id = dbo.getFieldString("NRO_CLIENTE").trim();
            }
        }

        //BUSCO SI YA EXISTE ALGUN DNI EN ACTUALIZAR EN TARJETAHABIENTE
        //CUANDO EL ID_INTERNO SEA DIFERENTE A LA CEDULA (VENEZUELA)
        if (pais.equalsIgnoreCase("ve") && !id.equals(dni)) {
            String comparoDNI = "select TH.ID_EXT_PER FROM TARJETAHABIENTE TH WHERE TH.ID_EXT_PER = '" + dni + "'";
            log.info(comparoDNI);
            dbo.dbreset();

            if (dbo.executeQuery(comparoDNI) == 0) {
                if (dbo.nextRecord()) {
                    log.debug("YA EXISTE UN DNI [" + dni + "] A ACTUALIZAR REGISTRADA EN TARJETAHABIENTE");
//                    dbo.dbClose();
//                    return "error3" + dni;
                    // si ya existe el proceso nocturno se encarga de borrar/actualizar en esta tabla
                    exist = true;
                }
            }
        }

        if (!exist) {

            String ActualizarSQL = "update TARJETAHABIENTE set status = 0, ";

            if (!nombre.equals("")) {
                ActualizarSQL = ActualizarSQL + "NOMBRES = '" + nombre + "', ";
            }

            if (!apellido.equals("")) {
                ActualizarSQL = ActualizarSQL + "APELLIDOS = '" + apellido + "', ";
            }

            if (!dni.equals("") && pais.equalsIgnoreCase("ve")) {
                ActualizarSQL = ActualizarSQL + "ID_EXT_PER = '" + dni + "', ID_INTERNO = '" + dni + "', ";
            } else if (!dni.equals("")) {
                ActualizarSQL = ActualizarSQL + "ID_EXT_PER = '" + dni + "', ";
            }

            ActualizarSQL = ActualizarSQL.substring(0, ActualizarSQL.length() - 2) + " where ID_INTERNO = '" + id + "'";

            log.info("SQL [" + ActualizarSQL + "]");

            if (dbo.executeQuery(ActualizarSQL) == 0) {
                dbo.dbClose();
                log.info("Oracle - total de registros afectados =" + dbo.rowsCount);
                if (dbo.rowsCount == 0 && pais.equalsIgnoreCase("ve")) { // 0 = no se encontro registro, se procede a insertar un nuevo registro con los datos del tarjetahabiente

                    String ConsultaTH = "select ID_EXT_PER FROM TARJETAHABIENTE WHERE ID_EXT_PER = '" + dni + "'";
                    log.info(ConsultaTH);
                    dbo.dbreset();

                    if (dbo.executeQuery(ConsultaTH) == 0) {
                        if (dbo.nextRecord()) {
                            log.error("YA EXISTE UN TARJETAHABIENTE CON LA CEDULA [" + dni + "]");
                            dbo.dbClose();
                            return "error2" + dni;
                        }
                    }

                    String InsertarSQL = "insert into TARJETAHABIENTE values (";

                    if (!dni.equals("") && pais.equalsIgnoreCase("ve")) {
                        InsertarSQL = InsertarSQL + "'" + dni + "','" + dni + "', 0,";
                    }

                    if (!nombre.equals("")) {
                        InsertarSQL = InsertarSQL + "'" + nombre + "',";
                    } else {
                        InsertarSQL = InsertarSQL + "null,";
                    }

                    if (!apellido.equals("")) {
                        InsertarSQL = InsertarSQL + "'" + apellido + "',";
                    } else {
                        InsertarSQL = InsertarSQL + "null,";
                    }

                    InsertarSQL = InsertarSQL + "null,null,null,null,null,null,null,null,null,null,sysdate,null,null)";
                    log.info("SQL [" + InsertarSQL + "]");

                    dbo.dbreset();
                    if (dbo.executeQuery(InsertarSQL) == 0) {
                        dbo.dbClose();
                        log.info("Oracle - total de registros insertados =" + dbo.rowsCount);
                    } else {
                        dbo.dbClose();
                        log.error("Oracle - Error insertando nuevo registro");
                        return "error";
                    }
                }
            } else {
                dbo.dbClose();
                log.error("Oracle - Error actualizando los campos");
                return "error";
            }

//            if (dbi.executeQuery(ActualizarSQL) == 0) {
//                dbi.dbClose();
//                log.info("Informix - total de registros afectados =" + dbi.rowsCount);
//            if (dbi.rowsCount == 0 && pais.equalsIgnoreCase("ve")) { // 0 = no se encontro registro, se procede a insertar un nuevo registro con los datos del tarjetahabiente
            if (dbo.rowsCount == 0 && pais.equalsIgnoreCase("ve")) { // 0 = no se encontro registro, se procede a insertar un nuevo registro con los datos del tarjetahabiente

                String ConsultaTH = "select ID_EXT_PER FROM TARJETAHABIENTE WHERE ID_EXT_PER = '" + dni + "'";
                log.info(ConsultaTH);
                dbo.dbreset();

                if (dbo.executeQuery(ConsultaTH) == 0) {
                    if (dbo.nextRecord()) {
                        log.error("YA EXISTE UN TARJETAHABIENTE CON LA CEDULA [" + dni + "]");
                        dbo.dbClose();
                        return "error";
                    }
                }

                String InsertarSQL = "insert into TARJETAHABIENTE values (";

                if (!dni.equals("") && pais.equalsIgnoreCase("ve")) {
                    InsertarSQL = InsertarSQL + "'" + dni + "','" + dni + "', 0,";
                }

                if (!nombre.equals("")) {
                    InsertarSQL = InsertarSQL + "'" + nombre + "',";
                } else {
                    InsertarSQL = InsertarSQL + "null,";
                }

                if (!apellido.equals("")) {
                    InsertarSQL = InsertarSQL + "'" + apellido + "',";
                } else {
                    InsertarSQL = InsertarSQL + "null,";
                }

//                InsertarSQL = InsertarSQL + "null,null,null,null,null,null,null,null,null,null,current,null)";
                InsertarSQL = InsertarSQL + "null,null,null,null,null,null,null,null,null,null,sysdate,null)";
                log.info("ORACLE - SQL [" + InsertarSQL + "]");

                dbo.dbreset();
                if (dbo.executeQuery(InsertarSQL) == 0) {
                    dbo.dbClose();
                    log.info("Oracle - total de registros insertados =" + dbo.rowsCount);
                } else {
                    dbo.dbClose();
                    log.error("Oracle - Error insertando nuevo registro");
                    return "error";
                }
            }
//            } else {
//                dbi.dbClose();
//                log.error("Informix - Error actualizando los campos");
//                return "error";
//            }

        }

        return "success";
    }
    //Metodo agregado del actual en produccion updateEditarAjustesDAO

    public String updateEditarAjustesDAO(String[] idAjustes, String monto, String tipoAJusteEditar, String AjusteDesc) {
        Dbinterface dbo = (Dbinterface) this.ds.get("oracle");
        String filtro = "(";

        monto = monto.replace(",", ".");
        String[] montos = monto.split(".");

        if (montos.length == 0) {
            if (!monto.contains(".")) {
                monto = monto + ".00";
            }
        }

        for (String idAjuste : idAjustes) {
            filtro = filtro + "'" + idAjuste + "'";
        }
        //filtro = filtro.substring(0, filtro.length() - 1) + ")";
        String ActualizarMonto = "update novo_detalle_ajustes set monto = " + monto + " , ID_CODIGO_AJUSTE='" + tipoAJusteEditar + "' , "
                + "DESCRIPCION = (select descripcion from NOVO_CODIGO_AJUSTES where ID_AJUSTE = '" + tipoAJusteEditar + "') where id_REGISTRO in " + filtro + ")";
        dbo.dbreset();
        log.info("ActualizarMonto [" + ActualizarMonto + "]");
        if (dbo.executeQuery(ActualizarMonto) == 0) {
            dbo.dbClose();
            log.info("total de registros afectados=" + dbo.rowsCount);
            return "ok";
        }
        dbo.dbClose();
        log.info("error actualizando el status de los ajustes");
        return "error";
    }

    public String doAjusteMasivoDAO(List<Ajuste> ajustes, String idAjuste, String usuario, String obs) {
        Dbinterface dbo = ds.get("oracle");
        String filtro, sqlSecuencia = "select SEQ_ID_DETALLE_AJUSTE.NEXTVAL secuencia from dual";
        String secuencia = "";
        filtro = "";
        dbo.dbreset();
        for (Ajuste ajuste : ajustes) {
            if ((dbo.executeQuery(sqlSecuencia) == 0)
                    && (dbo.nextRecord())) {
                secuencia = dbo.getFieldString("secuencia");
            }

            String monto = ajuste.getMonto();
            monto = monto.replace(",", ".");

            if (!monto.contains(".")) {
                monto = monto + ".00";
            }

            ajuste.setMonto(monto);

            filtro = filtro + " into novo_detalle_ajustes (ID_REGISTRO,FECHA,TARJETA,MONTO,STATUS,USUARIO,ID_CODIGO_AJUSTE,DTREGISTRO,AUTORIZACION,descripcion,cod_operacion,cod_moneda,cat_comercio,observacion) values ('" + secuencia + "',sysdate,'" + ajuste.getTarjeta() + "',TO_NUMBER('" + ajuste.getMonto() + "','9999999999990D99'),'3','" + usuario + "','" + idAjuste + "',SYSDATE,'000000',(select descripcion from novo_codigo_ajustes where id_ajuste ='" + idAjuste + "'),(select COD_OPERACION from novo_codigo_ajustes where id_ajuste ='" + idAjuste + "'),'604','0000','" + obs + "')";
        }
        filtro = "insert all " + filtro + " select * from dual";
        log.info("sql [" + filtro + "]");
        dbo.dbreset();
        if (dbo.executeQuery(filtro) == 0) {
            dbo.dbClose();
            log.info("Query ejecutado con exito");
            return "ok";
        } else {
            dbo.dbClose();
            log.error("Error ejecutando query");
            return "error";
        }
    }

    public String checkTarjetasDAO(List<String> tarjetas) {
        Dbinterface dbo = (Dbinterface) this.ds.get("oracle");
        String filtro = "(";
        String sql = "";
        String nroTarjeta = "";
        List<String> nroTarjetasList = new ArrayList<String>();
        String tarjetasInexis = "";
        int cantidad = 0;
        for (String tarjeta : tarjetas) {
            filtro = filtro + "'0000" + tarjeta + "',";
        }
        filtro = filtro.substring(0, filtro.length() - 1) + ")";
        sql = "select NRO_CUENTA from MAESTRO_PLASTICO_TEBCA where nro_cuenta in " + filtro;
        log.info("sql [" + sql + "]");
        dbo.dbreset();
        if (dbo.executeQuery(sql) == 0) {
            while (dbo.nextRecord()) {
                nroTarjeta = dbo.getFieldString("NRO_CUENTA");
                nroTarjetasList.add(nroTarjeta.substring(4, nroTarjeta.length()));
                cantidad++;
            }
        } else {
            dbo.dbClose();
            return "error";
        }
        if (cantidad == tarjetas.size()) {
            dbo.dbClose();
            return "ok";
        } else {
            //TARJETAS INEXISTENTES
            //COMPARA LAS DOS LISTAS Y EXTRAE DEL EXCEL AQUELLAS TARJETAS QUE NO HAN SIDO ENCONTRADAS EN LA CONSULTA DB
            Collection listOne = nroTarjetasList;
            Collection listTwo = tarjetas;

            Collection<String> similar = new HashSet<String>(listOne);
            Collection<String> different = new HashSet<String>();
            different.addAll(listOne);
            different.addAll(listTwo);

            similar.retainAll(listTwo);
            different.removeAll(similar);
            tarjetasInexis = different.toString().substring(1, different.toString().length() - 1);
            log.info("TARJETAS INEXISTENTES:" + tarjetasInexis);

        }
        dbo.dbClose();

        if (tarjetasInexis.equals("")) {
            return "error";
        } else {
            return "errorT" + tarjetasInexis;
        }
    }

    public List<CamposActualizacion> getCamposActualizacionDAO(String opcion) {
        List<CamposActualizacion> listaCampos = new ArrayList<CamposActualizacion>();
        CamposActualizacion campo = new CamposActualizacion();
        String sql = "SELECT IDCAMPO, CAMPO,  LONGITUD, TIPO FROM NOVO_ACT_CAMPOS_POSTBRIDGE ORDER BY IDCAMPO";
        if (opcion.equals("1")) {
            sql = "SELECT IDCAMPO, CAMPO,  LONGITUD, TIPO FROM NOVO_ACT_CAMPOS_POSTBRIDGE where status = '0' ORDER BY IDCAMPO";
        }
        Dbinterface dbo = ds.get("oracle");
        dbo.dbreset();
        log.info("sql [" + sql + "]");
        if (dbo.executeQuery(sql) == 0) {
            while (dbo.nextRecord()) {
                campo.setIdCampo(dbo.getFieldString("IDCAMPO").trim());
                campo.setCampo(dbo.getFieldString("CAMPO").trim());
                campo.setLongitud(dbo.getFieldString("LONGITUD").trim());
                campo.setTipo(dbo.getFieldString("TIPO").trim());
                listaCampos.add(campo);
                campo = new CamposActualizacion();
            }
        }

        return listaCampos;
    }

    public List<HoldCode> getHoldResponseCodes() {
        List<HoldCode> codigos = new ArrayList<HoldCode>();
        HoldCode code = new HoldCode();
        Dbinterface dbo = ds.get("oracle");
        String sql = "select * from novo_tipo_bloque where status = 'A' order by id_bloque";
        log.info("sql [" + sql + "]");
        dbo.dbreset();
        if (dbo.executeQuery(sql) == 0) {
            while (dbo.nextRecord()) {
                code.setHoldCode(dbo.getFieldString("CODIGO").trim());
                code.setId(dbo.getFieldString("ID_BLOQUE").trim());
                code.setTipo(dbo.getFieldString("TIPO_BLOQUE").trim());
                codigos.add(code);
                code = new HoldCode();
            }
        }
        return codigos;
    }

    public String numLote() {
        //Dbinterface dbi = ds.get("informix");
        Dbinterface dbo = ds.get("oracle");
        dbo.dbreset();
        int cont1 = 0;
        String fechanumlote;
        String respuesta = "";
        SimpleDateFormat fecha = new SimpleDateFormat("yyMMdd");
        fechanumlote = fecha.format(new Date());

        //String sql = "select max(acnumlote) + 1 as LOTE from teb_lote where accodcia = '1' and to_char(dtfechorcarga,'%y%m%d') =" + fechanumlote + "";
        String sql = "select max(acnumlote) + 1 as LOTE from teb_lote where accodcia = '1' and to_char(dtfechorcarga,'YYYYMMDD') =" + fechanumlote + "";

        if (dbo.executeQuery(sql) == 0) {
            if (dbo.nextRecord()) {
                respuesta = dbo.getFieldString("LOTE");
                if (respuesta.equals("")) {
                    respuesta = fechanumlote + "00";
                }
            }

        } else {
            log.info("No se pudo consultar el número de lote");
        }

        return respuesta;
    }

    public String makeUpdatesDAO(List<Tarjeta> tarjetas, List<CamposActualizacion> campos, String usuario, String pais) throws SQLException {
        //Dbinterface dbi = ds.get("informix");
        Dbinterface dbo = ds.get("oracle");
        dbo.dbreset();
        String idLote = "";
        Boolean updateName = Boolean.valueOf(false);
        Boolean updateApellido = Boolean.valueOf(false);
        boolean flag = false;
        String nombre = "";
        String apellido = "";
        String dni = "";
        log.info("User : " + usuario);
        String acnumlote = numLote();

        if (acnumlote.equals("")) {
            return "-1";
        }

        //Valido si la cedula en actualizar exista en otro registro diferente
        List<CamposActualizacion> camposAux = getCamposActualizacionDAO("0");
        List<CamposActualizacion> camposAux2 = new ArrayList<CamposActualizacion>();
        for (CamposActualizacion campo : campos) {
            for (CamposActualizacion campoAux : camposAux) {
                camposAux2.add(campoAux);
                if (campo.getIdCampo().equals(campoAux.getIdCampo())) {

                    if (campo.getIdCampo().equals("3")) {
                        dni = campo.getValor();
                        String sql = "SELECT NOMBRES, APELLIDOS FROM TARJETAHABIENTE where ID_EXT_PER = '" + dni + "'";
                        log.info("sql [" + sql + "]: makeUpdatesDAO() se valida la cedula de la persona");
                        if (dbo.executeQuery(sql) == 0) {
                            while (dbo.nextRecord()) {
                                //Cedula ya existe registrado para otro usuario
                                //nombre = dbo.getFieldString("NOMBRES");
                                //apellido = dbo.getFieldString("APELLIDOS");
                                log.info("CEDULA ENCONTRADA [" + dni + "]: " + nombre + " " + apellido);
                                flag = true;
                                break;
                            }
//                            if (flag) {
                            //RESPUESTA CON EL NOMBRE Y APELLIDO DEL TARJETA HABIENTE QUE CONTIENE LA CEDULA EN EL CAMPO DE ACTUALIZAR
                            //return "-3" + dni + ":" + nombre + " " + apellido;
//                            }
                        } else {
                            return "-1";
                        }
                        dbo.dbreset();
                    }

                    break;
                }
            }
            camposAux.removeAll(camposAux2);
        }

        String sql = "INSERT INTO teb_lote(ACIDLOTE, accodgrupo,accodcia, ctipolote, nmonto, ncantregs, dtfechorvalor, accodusuarioc, dtfechorcarga, accodusuarioa, dtfechorauto, accodusuarioa2, dtfechorauto2, dtfechorproceso, nrechazos, nid_trans, actipoauto, accanal, cestatus, obs, actipoproducto, acxmlext, achist, acnocuenta, idordens, cfacturacion, montocomision, montoiva, montorecarga, acnumlote)"
                //+ "VALUES( '0101010101', '1','A', 0.00, " + tarjetas.size() + ", NULL, '" + usuario + "', current, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '3', 'Actualizacion', (select acprefix from teb_productos where '" + tarjetas.get(0).getNroTarjeta() + "' between acnumcuentai and acnumcuentaf and acnumcuentai not like '00000000%'), NULL, '0', NULL, NULL, NULL, 0.00, 0.00, 0.00, " + acnumlote + ")";
                + "VALUES(SEQ_ACIDLOTE_TEB_LOTE.NEXTVAL, '0101010101', '1','A', 0.00, " + tarjetas.size() + ", NULL, '" + usuario + "', sysdate, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '3', 'Actualizacion', (select PREFIX from CONFIG_PRODUCTOS where '" + tarjetas.get(0).getNroTarjeta() + "' between numcuentai and numcuentaf and numcuentai not like '00000000%'), NULL, '0', NULL, NULL, NULL, 0.00, 0.00, 0.00, " + acnumlote + ")";
        log.info("sql [" + sql + "]: makeUpdatesDAO() se inserta el lote");
        if (dbo.executeQuery(sql) == 0) {
            sql = "select acidlote from teb_lote where accodusuarioc = '" + usuario + "' and ctipolote = 'A' and cestatus ='3' order by acidlote desc";
            log.info("sql [" + sql + "]:se obtiene el id del lote");
            dbo.dbreset();
            if (dbo.executeQuery(sql) == 0) {
                ResultSet rs = dbo.getQueryResults();
                while (rs.next()) {
                    idLote = rs.getString("acidlote");
                    log.info("idlote: " + idLote);
                }
            }
        } else {
            return "-1";
        }
        dbo.dbreset();
        dni = "";
        camposAux = getCamposActualizacionDAO("0");
        camposAux2 = new ArrayList<CamposActualizacion>();
        for (Tarjeta tarjeta : tarjetas) {
            //sql = "INSERT INTO tebca_lote_ad( numlote, notarjeta, dttimestamp,  idlote, idpersona,  nombre1, direccion, telefono1, fechanac, apellido1, codarea1, nombre2, apellido2, nombretienda)"+17
            sql = "INSERT INTO tebca_lote_ad( numlote, notarjeta, dttimestamp,  idlote,customstate,holdresponsecode,idpersona,nombre1,apellido1,nombre2,apellido2,nombretienda,subsidiary,companyid,cod_misc1,cod_misc2,cod_misc3,intcompany)"
                    + "VALUES( '" + idLote + "', '" + tarjeta.getNroTarjeta().trim() + "',  sysdate,'" + idLote + "' , CAMPOVALOR, CAMPOVALOR, CAMPOVALOR, CAMPOVALOR, CAMPOVALOR, CAMPOVALOR, CAMPOVALOR, CAMPOVALOR, CAMPOVALOR, CAMPOVALOR, CAMPOVALOR, CAMPOVALOR, CAMPOVALOR, CAMPOVALOR)";
            for (CamposActualizacion campo : campos) {
                for (CamposActualizacion campoAux : camposAux) {
                    camposAux2.add(campoAux);
                    if (campo.getIdCampo().equals(campoAux.getIdCampo())) {
                        sql = sql.replaceFirst("CAMPOVALOR", "'" + campo.getValor() + "'");
                        //break;

                        //if agregado del modulo actual en produccion
                        if (campo.getIdCampo().equals("4")) {
                            updateName = Boolean.valueOf(true);
                            nombre = campo.getValor();
                        }

                        if (campo.getIdCampo().equals("3")) {
                            dni = campo.getValor();
                        }

                        if (!campo.getIdCampo().equals("5")) {
                            break;
                        }
                        updateApellido = Boolean.valueOf(true);
                        apellido = campo.getValor();
                        break;
                    }

                    if (Integer.parseInt(campo.getIdCampo()) >= Integer.parseInt(campoAux.getIdCampo())) {
                        sql = sql.replaceFirst("CAMPOVALOR", "NULL");
                    }
                }
                camposAux.removeAll(camposAux2);
            }
            sql = sql.replaceAll("CAMPOVALOR", "NULL");
            camposAux = getCamposActualizacionDAO("0");
            camposAux2 = new ArrayList<CamposActualizacion>();
            log.info("sql [" + sql + "]");
            dbo.dbreset();
            if (dbo.executeQuery(sql) != 0) {
                return "-1";
            }

            if (flag) {
                dni = "";
            }

            if (!nombre.equals("") || !apellido.equals("") || !dni.equals("")) {
                if (updateCampos(tarjeta.getNroTarjeta(), nombre, apellido, dni, pais).equals("error")) {
                    return "-1";
                }
            }

        }
        return "0";
    }

    public String makeAfiliacionDAO(List<Tarjeta> tarjetas, String usuario) {
        //Dbinterface dbi = ds.get("informix");
        Dbinterface dbo = ds.get("oracle");
        dbo.dbreset();
        String idLote = "";
        Boolean updateName = Boolean.valueOf(false);
        Boolean updateApellido = Boolean.valueOf(false);
        String nombre = "";
        String apellido = "";
        String idCia = "";
        log.info("User : " + usuario);
        String acnumlote = numLote();

        if (acnumlote.equals("")) {
            return "-1";
        }

        ///////////////////////////////////////////////////////////////////
        //SE VALIDA EL FORMATO DEL ID O RIF DE LA EMPRESA POR CADA REGISTRO
        //BUSCO LA EXPRESION REGULAR
        String regx = "";
        String sql = "select acvalue from teb_parameters where acname like 'idextempfmtregex%'";
        log.info("sql [" + sql + "]");
        if (dbo.executeQuery(sql) == 0) {
            if (dbo.nextRecord()) {
                regx = dbo.getFieldString("acvalue");
            } else {
                return "-1";
            }
        } else {
            return "-1";
        }
        String tarjetasCia = checkFormatCia(tarjetas, regx);
        if (!tarjetasCia.equals("")) {
            return "error4" + tarjetasCia;
        }
        ////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////
        //SE VALIDA SI LA EMPRESA EXISTE
        String empresasInexis = "";
        List<String> empresas1 = new ArrayList<String>();
        List<String> empresas2 = new ArrayList<String>();
        String empresas = "";
        for (Tarjeta tarjeta : tarjetas) {
            if (tarjeta.getIdExtEmp() != null) {
                if (!tarjeta.getIdExtEmp().trim().equals("")) {
                    empresas = empresas + "'" + tarjeta.getIdExtEmp() + "',";
                    empresas1.add(tarjeta.getIdExtEmp());
                }
            }
        }
        if (!empresas.equals("")) {
            //sql = "select acrif from empresas where acrif in (" + empresas.substring(0, empresas.length() - 1) + ")";
            sql = "select LTRIM(CIRIF_CLIENTE,'0') AS acrif from MAESTRO_CLIENTES_TEBCA where LTRIM(CIRIF_CLIENTE,'0') in (" + empresas.substring(0, empresas.length() - 1) + ")";
            log.info("sql [" + sql + "]");
            dbo.dbreset();
            if (dbo.executeQuery(sql) == 0) {
                while (dbo.nextRecord()) {
                    empresas2.add(dbo.getFieldString("acrif").trim());
                }
            } else {
                return "-1";
            }

            Collection listOne = empresas2;
            Collection listTwo = empresas1;

            Collection<String> similar = new HashSet<String>(listOne);
            Collection<String> different = new HashSet<String>();
            different.addAll(listOne);
            different.addAll(listTwo);

            similar.retainAll(listTwo);
            different.removeAll(similar);
            empresasInexis = different.toString().substring(1, different.toString().length() - 1);
            if (!empresasInexis.equals("")) {
                return "error5" + empresasInexis;
            }
        }
        ////////////////////////////////////////////////////////////////////

        sql = "INSERT INTO teb_lote( ACIDLOTE,accodgrupo,accodcia, ctipolote, nmonto, ncantregs, dtfechorvalor, accodusuarioc, dtfechorcarga, accodusuarioa, dtfechorauto, accodusuarioa2, dtfechorauto2, dtfechorproceso, nrechazos, nid_trans, actipoauto, accanal, cestatus, obs, actipoproducto, acxmlext, achist, acnocuenta, idordens, cfacturacion, montocomision, montoiva, montorecarga, acnumlote)"
//                + "VALUES( '0101010101', '1','A', 0.00, " + tarjetas.size() + ", NULL, '" + usuario + "', current, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '3', 'Afiliacion', (select acprefix from teb_productos where '" + tarjetas.get(0).getNroTarjeta() + "' between acnumcuentai and acnumcuentaf and acnumcuentai not like '00000000%'), NULL, '0', NULL, NULL, NULL, 0.00, 0.00, 0.00, " + acnumlote + ")";
                + "VALUES(SEQ_ACIDLOTE_TEB_LOTE.NEXTVAL, '0101010101', '1','A', 0.00, " + tarjetas.size() + ", NULL, '" + usuario + "', sysdate, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '3', 'Afiliacion', (select PREFIX from CONFIG_PRODUCTOS where '" + tarjetas.get(0).getNroTarjeta() + "' between numcuentai and numcuentaf and numcuentai not like '00000000%'), NULL, '0', NULL, NULL, NULL, 0.00, 0.00, 0.00, " + acnumlote + ")";
        log.info("sql [" + sql + "]: makeAfiliacionDAO() se inserta el lote");
        dbo.dbreset();
        if (dbo.executeQuery(sql) == 0) {
            sql = "select acidlote from teb_lote where accodusuarioc = '" + usuario + "' and ctipolote = 'A' and cestatus ='3' order by acidlote desc";
            log.info("sql [" + sql + "]:se obtiene el id del lote");
            if (dbo.executeQuery(sql) == 0) {
                if (dbo.nextRecord()) {
                    idLote = dbo.getFieldString("acidlote");
                } else {
                    return "-1";
                }
            }
        } else {
            return "-1";
        }
        dbo.dbreset();

        log.info("----------------------");
        log.info("GENERA DETALLES LOTES");
        log.info("----------------------");
        for (Tarjeta tarjeta : tarjetas) {
            //sql = "INSERT INTO tebca_lote_ad( numlote, notarjeta, dttimestamp,  idlote, idpersona,  nombre1, direccion, telefono1, fechanac, apellido1, codarea1, nombre2, apellido2, nombretienda)"+17
            sql = "INSERT INTO tebca_lote_ad( numlote, notarjeta, dttimestamp,  idlote,customstate,holdresponsecode,idpersona,nombre1,apellido1,nombre2,apellido2,nombretienda,subsidiary,companyid,cod_misc1,cod_misc2,cod_misc3,intcompany)"
                    + "VALUES( '" + idLote + "', '" + tarjeta.getNroTarjeta().trim() + "',  sysdate,'" + idLote + "' , CAMPOVALOR, CAMPOVALOR,"
                    + " '" + tarjeta.getIdExtPer() + "' , ";

            nombre = "";
            if (tarjeta.getNombreCliente() != null) {
                nombre = tarjeta.getNombreCliente();
                sql = sql + "'" + nombre + "' , ";
            } else {
                sql = sql + "NULL , ";
            }

            apellido = "";
            if (tarjeta.getApellidoCliente() != null) {
                apellido = tarjeta.getApellidoCliente();
                sql = sql + "'" + apellido + "' , ";

            } else {
                sql = sql + "NULL , ";
            }

            sql = sql + "CAMPOVALOR, CAMPOVALOR, CAMPOVALOR, CAMPOVALOR, ";

            idCia = "";
            if (tarjeta.getIdExtEmp() != null && !tarjeta.getIdExtEmp().equals("")) {
                idCia = tarjeta.getIdExtEmp();
                sql = sql + "'" + idCia + "' , ";

            } else {
                sql = sql + "NULL , ";
            }

            sql = sql + "CAMPOVALOR, CAMPOVALOR, CAMPOVALOR, CAMPOVALOR)";

            sql = sql.replaceAll("CAMPOVALOR", "NULL");
            log.info("sql [" + sql + "]");
            if (dbo.executeQuery(sql) != 0) {
                return "-1";
            }

            if (!nombre.equals("") || !apellido.equals("") || !tarjeta.getIdExtPer().equals("")) {
                String resp = updateAfiliacion(tarjeta.getNroTarjeta(), nombre, apellido, tarjeta.getIdExtPer(), pais);
                if (resp.equals("error")) {
                    return "-1";
                } else if (resp.contains("error2")) {
                    return resp;
                } else if (resp.contains("error3")) {
                    return resp;
                }
            }
        }
        return "0";
    }

    public String checkFormatCia(List<Tarjeta> tarjetas, String regx) {
        String resp = "";

        for (Tarjeta tarjeta : tarjetas) {
            if (tarjeta.getIdExtEmp() != null) {
                if (!tarjeta.getIdExtEmp().trim().matches(regx) && !tarjeta.getIdExtEmp().trim().equals("")) {
                    //EL FORMATO NO ES VALIDO
                    resp = resp + tarjeta.getNroTarjeta() + ",";
                }
            }
        }

        return resp;
    }

    @Override
    public void closeConection() {
        this.shutdownDatabases();
    }

}
