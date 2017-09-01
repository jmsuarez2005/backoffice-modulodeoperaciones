/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.process;

import com.novo.constants.BasicConfig;
import com.novo.dao.AjustesDAO;
import com.novo.dao.BloqueoDesbloqueoDAO;
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
import com.opensymphony.xwork2.ActionContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author ggutierrez
 */
public class ReporteTransacciones implements BasicConfig {

    private static Logger log = Logger.getLogger(ReporteTransacciones.class);

    private String pais = (String) ActionContext.getContext().getSession().get("pais");
    /*
     * obtiene las transacciones por usuario, ya sea por la tarjeta o documento de identidad
     */

    public List<Transaccion> getTransaccionesUsuario(String nroTarjeta) {
        AjustesDAO dao = new AjustesDAO(appName, databases, pais);
        List<Transaccion> transacciones;

        if (nroTarjeta != null) {
            transacciones = dao.getTransaccionesDAO(nroTarjeta);
            dao.closeConection();
            return transacciones;
        }
        return null;
    }

    public List<Producto> getProductos() {
        AjustesDAO dao = new AjustesDAO(appName, databases, pais);
        List<Producto> productos;
        productos = dao.getProductosDAO();
        dao.closeConection();
        return productos;
    }

    public List<Empresa> getEmpresas() {
        AjustesDAO dao = new AjustesDAO(appName, databases, pais);
        List<Empresa> empresas;
        empresas = dao.getEmpresasDAO();
        dao.closeConection();
        return empresas;
    }

    /*
     * obtiene las tarjetas pertenecientes al usuario, ya sea por la tarjeta o documento de identidad
     */
    public List<Tarjeta> getTarjetasUsuario(String documento, String nroTarjeta) {
        AjustesDAO dao = new AjustesDAO(appName, databases, pais);
        List<Tarjeta> tarjetas = null;
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
        return null;
    }

    public List<Tarjeta> getTarjetasUsuario(String documento, String nroTarjeta, String prefix, String rif) {
        AjustesDAO dao = new AjustesDAO(appName, databases, pais);
        List<Tarjeta> tarjetas = null;

        //if (!documento.equals("") || !nroTarjeta.equals("") || !prefix.equals("") || !rif.equals("")) {
        tarjetas = dao.getTarjetasFiltrosDAO(documento, nroTarjeta, prefix, rif);
        dao.closeConection();
        return tarjetas;
        // }
//        if (documento != null) {
//            tarjetas = dao.getTarjetasDAO(documento, true, prefix, rif);
//            dao.closeConection();
//            return tarjetas;
//
//        }
//        if (nroTarjeta != null) {
//            tarjetas = dao.getTarjetasDAO(nroTarjeta, false);
//            dao.closeConection();
//            return tarjetas;
//        }
//
//        if (prefix != null) {
//            tarjetas = dao.getTarjetasDAO(null, true, prefix, rif);
//            dao.closeConection();
//            return tarjetas;
//        }
//
//        if (rif != null) {
//            tarjetas = dao.getTarjetasDAO(null, true, prefix, rif);
//            dao.closeConection();
//            return tarjetas;
//        }

//        if((documento==null && nroTarjeta==null) || (documento.equals("") && nroTarjeta.equals(""))){
//            tarjetas = dao.getTarjetasDAO(null,true,prefix, rif);
//            dao.closeConection();
//            return tarjetas;
//        }
        //return null;
    }

    public List<Tarjeta> getTarjetasUsuarioTransacciones(String documento, String nroTarjeta, String prefix, String rif, String fechaIni, String fechaFin) {
        AjustesDAO dao = new AjustesDAO(appName, databases, pais);
        List<Tarjeta> tarjetas = null;

        if (!documento.equals("") || !nroTarjeta.equals("") || !prefix.equals("") || !rif.equals("")) {
            tarjetas = dao.getTarjetasFiltrosTransaccionesDAO(documento, nroTarjeta, prefix, rif, fechaIni, fechaFin);
            dao.closeConection();
            return tarjetas;
        }
        return null;
    }

    public List<TAjuste> getTipoAjustes() {
        AjustesDAO dao = new AjustesDAO(appName, databases, pais);
        List<TAjuste> tipoAjustes;
        tipoAjustes = dao.getTipoAjustesDAO();
        dao.closeConection();
        return tipoAjustes;
    }

    public List<TBloqueo> getTipoBloqueo() {
        BloqueoDesbloqueoDAO dao = new BloqueoDesbloqueoDAO("operaciones", databases, this.pais);

        List tipoBloqueo = dao.getTipoBloqueoDAO();

        return tipoBloqueo;
    }

    public String RegistrarAjuste(String tarjeta, String monto, String codigoAjuste, String referencia, String usuario, String obs, boolean tras) {
        AjustesDAO dao = new AjustesDAO(appName, databases, pais);
        if (referencia == null || referencia.equals("")) {
            referencia = "000000";
        }

        referencia = Utils.padString(referencia, true, "0", 6);

        String result = dao.RegistrarAjusteDAO(tarjeta, monto, codigoAjuste, referencia, usuario, obs, tras);
        dao.closeConection();
        return result;
    }

    public List<Ajuste> getAjustes(String status, String usuario, Date fechaIni, Date fechaFin) {
        AjustesDAO dao = new AjustesDAO(appName, databases, pais);
        List<Ajuste> ajustes = new ArrayList<Ajuste>();
        ajustes = dao.getAjustesDAO(status, usuario, fechaIni, fechaFin);
        dao.closeConection();
        return ajustes;
    }

    public List<String> getUsuarios() {
        AjustesDAO dao = new AjustesDAO(appName, databases, pais);
        List<String> usuarios;
        usuarios = dao.getUsuariosDao();
        dao.closeConection();
        return usuarios;
    }

    public String updateAjuste(String[] idAjustes, String status) {
        AjustesDAO dao = new AjustesDAO(appName, databases, pais);
        String result = "";
        for (String idAjuste : idAjustes) {
            idAjuste = idAjuste.replaceAll(" ", "");
        }
        result = dao.updateAjustesDAO(idAjustes, status);
        dao.closeConection();
        return result;
    }

    public String updateEdicion(String[] idAjustes, String monto, String tipoAjusteEditar, String AjusteDesc) {
        AjustesDAO dao = new AjustesDAO("operaciones", databases, this.pais);
        String result = "";
        for (String idAjuste : idAjustes) {
            idAjuste = idAjuste.replaceAll(" ", "");
        }
        result = dao.updateEditarAjustesDAO(idAjustes, monto, tipoAjusteEditar, AjusteDesc);

        dao.closeConection();
        return result;
    }

    public String doAjusteMasivo(List<Ajuste> ajustes, String idAjuste, String usuario, String obs) {
        AjustesDAO dao = new AjustesDAO(appName, databases, pais);
        if (obs == null) {
            obs = "";
        }
        String result = dao.doAjusteMasivoDAO(ajustes, idAjuste, usuario, obs);
        dao.closeConection();
        return result;
    }

    public String checkTarjetas(List<Ajuste> ajustes) {
        List<String> listaTarjetaAux = new ArrayList<String>();

        for (Ajuste ajuste : ajustes) {
            listaTarjetaAux.add(ajuste.getTarjeta());
        }
        Set<String> set = new HashSet<String>();
        set.addAll(listaTarjetaAux);
        listaTarjetaAux.clear();
        listaTarjetaAux.addAll(set);
        Collections.sort(listaTarjetaAux);
        //VALIDAR QUE EXISTAN LAS TARJETAS

        AjustesDAO dao = new AjustesDAO(appName, databases, pais);
        String result = dao.checkTarjetasDAO(listaTarjetaAux);
        dao.closeConection();
        return result;
    }

    public Tarjeta consultarSaldo(Tarjeta tarjeta) {
        TransactionProcess tp = new TransactionProcess(appName, databases, pais);
        String systrace = tp.getSystrace();
        Transaccion trans = new Transaccion();
        trans = tp.balance(tarjeta, systrace, pais, tarjeta.getFechaExpiracion());

        if (trans.getRc() == 0) {
            tarjeta.setSaldoDisponible(trans.getSaldo());

        } else {
            tarjeta.setSaldoDisponible("---");
        }
        tp.closeConection();
        return tarjeta;
    }

    public int traslado(Tarjeta tarjetaOrigen, Tarjeta tarjetaDestino, String usuario) {
        TransactionProcess tp = new TransactionProcess(appName, databases, pais);
        int rc = 0;
        String systrace = tp.getSystrace();
        Transaccion trans = new Transaccion();
        trans = tp.cargo(tarjetaOrigen, systrace, pais);
        RegistrarAjuste(tarjetaOrigen.getNroTarjeta(), tarjetaOrigen.getMonto(), "15", systrace, usuario, "", true);
        systrace = tp.getSystrace();
        trans = tp.abono(tarjetaDestino, systrace, pais);
        RegistrarAjuste(tarjetaDestino.getNroTarjeta(), tarjetaDestino.getMonto(), "14", systrace, usuario, "", true);
        return rc;
    }

    public List getCampos() {
        List<CamposActualizacion> campos;
        AjustesDAO dao = new AjustesDAO(appName, databases, pais);
        campos = dao.getCamposActualizacionDAO("1");
        dao.closeConection();
        return campos;
    }

    public List getHoldCodes() {
        List<HoldCode> codigos;
        AjustesDAO dao = new AjustesDAO(appName, databases, pais);
        codigos = dao.getHoldResponseCodes();
        dao.closeConection();
        return codigos;
    }

    public String makeUpdates(List<Tarjeta> tarjetas, List<CamposActualizacion> campos, String usuario) {
        //(campos)fields already matched together, the id with the value selected by the user.
        //(tarjetas)list of cards that are going to be updated with the selected fields.
        AjustesDAO dao = new AjustesDAO(appName, databases, pais);
        String respuesta = dao.makeUpdatesDAO(tarjetas, campos, usuario, pais);
        
        if (respuesta.contains("-3")) {
            dao.closeConection();
            return respuesta;
        }else if (!respuesta.equals("0")) {
            dao.closeConection();
            return "-1";
        }
        dao.closeConection();
        return "0";
    }

    public String makeAfiliacion(List<Tarjeta> tarjetas, String usuario) {
        //(campos)fields already matched together, the id with the value selected by the user.
        //(tarjetas)list of cards that are going to be updated with the selected fields.
        AjustesDAO dao = new AjustesDAO(appName, databases, pais);
        String resp = dao.makeAfiliacionDAO(tarjetas, usuario);
        if (resp.contains("error2")) {
            dao.closeConection();
            return resp;
        } else if (resp.contains("error3")) {
            dao.closeConection();
            return resp;
        } else if (resp.contains("error4")) {
            dao.closeConection();
            return resp;
        } else if (resp.contains("error5")) {
            dao.closeConection();
            return resp;
        } else if (!resp.equals("0")) {
            dao.closeConection();
            return "-1";
        }
        dao.closeConection();
        return "0";
    }
}
