/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.process;

import static com.novo.constants.BasicConfig.databases;
import com.novo.dao.RenovacionDAO;
import com.novo.model.UsuarioSesion;
import com.novo.model.ValoresRen;
import com.novo.objects.util.Utils;
import com.opensymphony.xwork2.ActionContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author rgomez
 */
public class RenovacionProc {

    String pais;
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

}
