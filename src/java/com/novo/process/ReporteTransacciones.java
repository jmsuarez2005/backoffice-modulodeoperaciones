/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.process;

import com.novo.constants.BasicConfig;
import com.novo.dao.AjustesDAO;
import com.novo.dao.BloqueoDesbloqueoDAO;
import com.novo.dao.temp.AjustesDAOINF;
import com.novo.model.Ajuste;
import com.novo.model.CamposActualizacion;
import com.novo.model.Empresa;
import com.novo.model.HoldCode;
import com.novo.model.Producto;
import com.novo.model.TAjuste;
import com.novo.model.TBloqueo;
import com.novo.model.Tarjeta;
import com.novo.model.Transaccion;
import com.novo.objects.util.Utils;
import com.novo.trans.TransactionProcess;
import com.novo.util.TextUtil;
import com.opensymphony.xwork2.ActionContext;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author ggutierrez
 */
public class ReporteTransacciones implements BasicConfig {

    private static Logger log = Logger.getLogger(ReporteTransacciones.class);

    private String pais = (String) ActionContext.getContext().getSession().get("pais");
    private Properties prop;
    private String propMigra;
    private TextUtil txt = new TextUtil();

    /*
     * obtiene las transacciones por usuario, ya sea por la tarjeta o documento de identidad
     */
    public ReporteTransacciones() {
        this.prop = Utils.getConfig("oracleRegEx.properties");
        this.propMigra = prop.getProperty("paisOracle");
    }

    public List<Transaccion> getTransaccionesUsuario(String nroTarjeta) {
        AjustesDAO dao = new AjustesDAO(appName, dbOracle, pais);
        List<Transaccion> transacciones;

        if (nroTarjeta != null) {
            transacciones = dao.getTransaccionesDAO(nroTarjeta);
            dao.closeConection();
            return transacciones;
        }
        return null;
    }

    public List<Producto> getProductos() {
        AjustesDAO dao = new AjustesDAO(appName, dbOracle, pais);
        List<Producto> productos;
        productos = dao.getProductosDAO();
        dao.closeConection();
        return productos;
    }

    public List<Empresa> getEmpresas() {
        List<Empresa> empresas;
        if (txt.paisMigra(propMigra, this.pais)) {   
            AjustesDAO dao = new AjustesDAO(appName, dbOracle, pais);
            empresas = dao.getEmpresasDAO();
            dao.closeConection();
        } else {
            AjustesDAOINF dao = new AjustesDAOINF(appName, databases, pais);
            empresas = dao.getEmpresasDAO();
            dao.closeConection();

        }
        return empresas;
    }

    /*
     * obtiene las tarjetas pertenecientes al usuario, ya sea por la tarjeta o documento de identidad
     */
    public List<Tarjeta> getTarjetasUsuario(String documento, String nroTarjeta) {
        if (txt.paisMigra(propMigra, this.pais)) {   
            AjustesDAO dao = new AjustesDAO(appName, dbOracle, pais);
            List<Tarjeta> tarjetas;
            
            if ((documento != null) && (nroTarjeta != null)){
                    tarjetas = dao.getTarjetasDAOF(documento, nroTarjeta);
                    dao.closeConection();
                    return tarjetas;
            }else{
                if (documento != null) {
                    tarjetas = dao.getTarjetasDAO(documento, true);
                    dao.closeConection();
                    return tarjetas;
                }
                if (nroTarjeta != null) {
                    tarjetas = dao.getTarjetasDAO(nroTarjeta, false);
                    dao.closeConection();
                    return tarjetas;
                }
            }

        } else {
            AjustesDAOINF dao = new AjustesDAOINF(appName, databases, pais);
            List<Tarjeta> tarjetas;
            if ((documento != null) && (nroTarjeta != null)){
                tarjetas = dao.getTarjetasDAOF(documento, nroTarjeta);
                dao.closeConection();
                return tarjetas;
            }else{
                if (documento != null) {
                    tarjetas = dao.getTarjetasDAO(documento, true);
                    dao.closeConection();
                    return tarjetas;
                }
                if (nroTarjeta != null) {
                    tarjetas = dao.getTarjetasDAO(nroTarjeta, false);
                    dao.closeConection();
                    return tarjetas;
                }
            }
        }
        return null;
    }

    public List<Tarjeta> getTarjetasUsuario(String documento, String nroTarjeta, String prefix, String rif) {
        List<Tarjeta> tarjetas;
        if (txt.paisMigra(propMigra, this.pais)) {   
            AjustesDAO dao = new AjustesDAO(appName, dbOracle, pais);
            tarjetas = dao.getTarjetasFiltrosDAO(documento, nroTarjeta, prefix, rif);
            dao.closeConection();

        } else {
            AjustesDAOINF dao = new AjustesDAOINF(appName, databases, pais);
            tarjetas = dao.getTarjetasFiltrosDAO(documento, nroTarjeta, prefix, rif);
            dao.closeConection();

        }
        return tarjetas;
    }

    public List<Tarjeta> getTarjetasUsuarioTransacciones(String documento, String nroTarjeta, String prefix, String rif, String fechaIni, String fechaFin) {
        if (txt.paisMigra(propMigra, this.pais)) {   
            AjustesDAO dao = new AjustesDAO(appName, dbOracle, pais);
            if (!documento.equals("") || !nroTarjeta.equals("") || !prefix.equals("") || !rif.equals("")) {
                List<Tarjeta> tarjetas = dao.getTarjetasFiltrosTransaccionesDAO(documento, nroTarjeta, prefix, rif, fechaIni, fechaFin);
                dao.closeConection();
                return tarjetas;
            }

        } else {
            AjustesDAOINF dao = new AjustesDAOINF(appName, databases, pais);
            if (!documento.equals("") || !nroTarjeta.equals("") || !prefix.equals("") || !rif.equals("")) {
                List<Tarjeta> tarjetas = dao.getTarjetasFiltrosTransaccionesDAO(documento, nroTarjeta, prefix, rif, fechaIni, fechaFin);
                dao.closeConection();
                return tarjetas;
            }
        }
        return null;
    }

    public List<TAjuste> getTipoAjustes() {
        AjustesDAO dao = new AjustesDAO(appName, dbOracle, pais);
        List<TAjuste> tipoAjustes;
        tipoAjustes = dao.getTipoAjustesDAO();
        dao.closeConection();
        return tipoAjustes;
    }

    public List<TBloqueo> getTipoBloqueo() {
        BloqueoDesbloqueoDAO dao = new BloqueoDesbloqueoDAO("operaciones", dbOracle, this.pais);
        List<TBloqueo> tipoBloqueo = dao.getTipoBloqueoDAO();
        return tipoBloqueo;
    }

    public String RegistrarAjuste(String tarjeta, String monto, String codigoAjuste, String referencia, String usuario, String obs, boolean tras) {
        AjustesDAO dao = new AjustesDAO(appName, dbOracle, pais);
        if (referencia == null || referencia.equals("")) {
            referencia = "000000";
        }

        referencia = Utils.padString(referencia, true, "0", 6);

        String result = dao.RegistrarAjusteDAO(tarjeta, monto, codigoAjuste, referencia, usuario, obs, tras);
        dao.closeConection();
        return result;
    }

    public List<Ajuste> getAjustes(String status, String usuario, Date fechaIni, Date fechaFin) {
        AjustesDAO dao = new AjustesDAO(appName, dbOracle, pais);
        List<Ajuste> ajustes = new ArrayList<Ajuste>();
        ajustes = dao.getAjustesDAO(status, usuario, fechaIni, fechaFin);
        dao.closeConection();
        return ajustes;
    }

    public List<Ajuste> getAjustes(String status, String usuario, Date fechaIni, Date fechaFin, String filtro) {
        AjustesDAO dao = new AjustesDAO(appName, dbOracle, pais);
        List<Ajuste> ajustes = new ArrayList<Ajuste>();
        ajustes = dao.getAjustesDAO(status, usuario, fechaIni, fechaFin, filtro);
        dao.closeConection();
        return ajustes;
    }

    public List<String> getUsuarios() {
        List<String> usuarios;
        if (txt.paisMigra(propMigra, this.pais)) {   
            AjustesDAO dao = new AjustesDAO(appName, dbOracle, pais);
            usuarios = dao.getUsuariosDao();
            dao.closeConection();
        } else {
            AjustesDAOINF dao = new AjustesDAOINF(appName, databases, pais);
            usuarios = dao.getUsuariosDao();
            dao.closeConection();
        }
        return usuarios;
    }

    public String updateAjuste(String[] idAjustes, String status) {
        AjustesDAO dao = new AjustesDAO(appName, dbOracle, pais);
        for (String idAjuste : idAjustes) {
            idAjuste = idAjuste.replaceAll(" ", "");
        }
        String result = dao.updateAjustesDAO(idAjustes, status);
        dao.closeConection();
        return result;
    }

    public String updateEdicion(String[] idAjustes, String monto, String tipoAjusteEditar, String AjusteDesc) {
        AjustesDAO dao = new AjustesDAO("operaciones", dbOracle, this.pais);
        for (String idAjuste : idAjustes) {
            idAjuste = idAjuste.replaceAll(" ", "");
        }
        String result = dao.updateEditarAjustesDAO(idAjustes, monto, tipoAjusteEditar, AjusteDesc);

        dao.closeConection();
        return result;
    }

    public String doAjusteMasivo(List<Ajuste> ajustes, String idAjuste, String usuario, String obs) {
        AjustesDAO dao = new AjustesDAO(appName, dbOracle, pais);
        if (obs == null) {
            obs = "";
        }
        String result = dao.doAjusteMasivoDAO(ajustes, idAjuste, usuario, obs);
        dao.closeConection();
        return result;
    }

    public String checkTarjetas(List<Ajuste> ajustes) {
        List<String> listaTarjetaAux = new ArrayList<>();

        for (Ajuste ajuste : ajustes) {
            listaTarjetaAux.add(ajuste.getTarjeta());
        }
        Set<String> set = new HashSet<>();
        set.addAll(listaTarjetaAux);
        listaTarjetaAux.clear();
        listaTarjetaAux.addAll(set);
        Collections.sort(listaTarjetaAux);
        //VALIDAR QUE EXISTAN LAS TARJETAS

        AjustesDAO dao = new AjustesDAO(appName, dbOracle, pais);
        String result = dao.checkTarjetasDAO(listaTarjetaAux);
        dao.closeConection();
        return result;
    }

    public Tarjeta consultarSaldo(Tarjeta tarjeta) {
        TransactionProcess tp = new TransactionProcess(appName, dbOracle, pais);
        String systrace = tp.getSystrace();
        Transaccion trans = tp.balance(tarjeta, systrace, pais, tarjeta.getFechaExpiracion());

        if (trans.getRc() == 0) {
            tarjeta.setSaldoDisponible(trans.getSaldo());

        } else {
            tarjeta.setSaldoDisponible("---");
        }
        tp.closeConection();
        return tarjeta;
    }

    public int traslado(Tarjeta tarjetaOrigen, Tarjeta tarjetaDestino, String usuario) {
        TransactionProcess tp = new TransactionProcess(appName, dbOracle, pais);
        int rc = 0;
        String systrace = tp.getSystrace();
        RegistrarAjuste(tarjetaOrigen.getNroTarjeta(), tarjetaOrigen.getMonto(), "15", systrace, usuario, "", true);
        systrace = tp.getSystrace();
        RegistrarAjuste(tarjetaDestino.getNroTarjeta(), tarjetaDestino.getMonto(), "14", systrace, usuario, "", true);
        return rc;
    }

    public List<CamposActualizacion> getCampos() {
        List<CamposActualizacion> campos;
        AjustesDAO dao = new AjustesDAO(appName, dbOracle, pais);
        campos = dao.getCamposActualizacionDAO("1");
        dao.closeConection();
        return campos;
    }

    public List<HoldCode> getHoldCodes() {
        List<HoldCode> codigos;
        AjustesDAO dao = new AjustesDAO(appName, dbOracle, pais);
        codigos = dao.getHoldResponseCodes();
        dao.closeConection();
        return codigos;
    }

    public String makeUpdates(List<Tarjeta> tarjetas, List<CamposActualizacion> campos, String usuario) throws SQLException {
        //(campos)fields already matched together, the id with the value selected by the user.
        //(tarjetas)list of cards that are going to be updated with the selected fields.
        if (txt.paisMigra(propMigra, this.pais)) {   
            AjustesDAO dao = new AjustesDAO(appName, dbOracle, pais);
            String respuesta = dao.makeUpdatesDAO(tarjetas, campos, usuario, pais);

            if (respuesta.contains("-3")) {
                dao.closeConection();
                return respuesta;
            } else if (!respuesta.equals("0")) {
                dao.closeConection();
                return "-1";
            }
            dao.closeConection();
        } else {
            AjustesDAOINF dao = new AjustesDAOINF(appName, databases, pais);
            String respuesta = dao.makeUpdatesDAO(tarjetas, campos, usuario, pais);

            if (respuesta.contains("-3")) {
                dao.closeConection();
                return respuesta;
            } else if (!respuesta.equals("0")) {
                dao.closeConection();
                return "-1";
            }
            dao.closeConection();

        }
        return "0";
    }

    public String makeAfiliacion(List<Tarjeta> tarjetas, String usuario) {
        //(campos)fields already matched together, the id with the value selected by the user.
        //(tarjetas)list of cards that are going to be updated with the selected fields.
        if (txt.paisMigra(propMigra, this.pais)) {   
            AjustesDAO dao = new AjustesDAO(appName, dbOracle, pais);
            String resp = dao.makeAfiliacionDAO(tarjetas, usuario);
            if (resp.contains("error2")) {
                dao.closeConection();
                return resp;
            }  
            if (resp.contains("error3")) {
                dao.closeConection();
                return resp;
            }  
            if (resp.contains("error4")) {
                dao.closeConection();
                return resp;
            }  
            if (resp.contains("error5")) {
                dao.closeConection();
                return resp;
            }  
            if (!resp.equals("0")) {
                dao.closeConection();
                return "-1";
            }
            dao.closeConection();
        } else {
            AjustesDAOINF dao = new AjustesDAOINF(appName, databases, pais);
            String resp = dao.makeAfiliacionDAO(tarjetas, usuario);
            if (resp.contains("error2")) {
                dao.closeConection();
                return resp;
            } 
            if (resp.contains("error3")) {
                dao.closeConection();
                return resp;
            } 
            if (resp.contains("error4")) {
                dao.closeConection();
                return resp;
            } 
            if (resp.contains("error5")) {
                dao.closeConection();
                return resp;
            } 
            if (!resp.equals("0")) {
                dao.closeConection();
                return "-1";
            }
            dao.closeConection();
        }
        return "0";
    }
}
