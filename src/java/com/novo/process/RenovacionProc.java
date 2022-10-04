/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.process;

import static com.novo.constants.BasicConfig.appName;
import static com.novo.constants.BasicConfig.dbOracle;
import com.novo.dao.RenovacionDAO;
import com.novo.model.Ajuste;
import com.novo.model.Renovacion;
import com.opensymphony.xwork2.ActionContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author rgomez
 */
public class RenovacionProc {

    String pais = (String) ActionContext.getContext().getSession().get("pais");
    private static Logger log = Logger.getLogger(RenovacionProc.class);

    public void addFile(String rutaOrigen, String rutaDestino, String host, String usuario) {
        try {

            String variable = "scp " + rutaOrigen + " " + usuario + "@" + host + ":" + rutaDestino;
            log.info("variable " + variable);
            Process p = Runtime.getRuntime().exec(variable);

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = null;
            while ((line = in.readLine()) != null) {
                log.info(line + "<br>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
    }

    public List<Renovacion> checkTarjetasARenovar(List<Ajuste> ajustes) {
        List<String> listaTarjetaAux = new ArrayList<String>();
        List<Renovacion> listaRenovacion = new ArrayList();

        for (Ajuste ajuste : ajustes) {
            listaTarjetaAux.add(ajuste.getTarjeta());
        }
        Set<String> set = new HashSet<String>();
        set.addAll(listaTarjetaAux);
        listaTarjetaAux.clear();
        listaTarjetaAux.addAll(set);
        Collections.sort(listaTarjetaAux);
        //VALIDAR QUE EXISTAN LAS TARJETAS
        RenovacionDAO dao = new RenovacionDAO(appName, dbOracle, this.pais);
        listaRenovacion = dao.checkTarjetasARenovarDAO(listaTarjetaAux);

        dao.closeConection();
        return listaRenovacion;
    }

    public String insertarRenovacion(List<Renovacion> listaRenovacion) {

        RenovacionDAO dao = new RenovacionDAO(appName, dbOracle, this.pais);
        String respuesta = dao.insertarNovoRenovacion(listaRenovacion);

        dao.closeConection();
        return respuesta;
    }

}
