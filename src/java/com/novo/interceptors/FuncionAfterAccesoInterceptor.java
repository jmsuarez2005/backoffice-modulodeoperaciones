/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.interceptors;

import com.novo.model.Funcion;
import com.novo.model.Perfil;
import com.novo.model.UsuarioSesion;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author rgomez
 */
public class FuncionAfterAccesoInterceptor implements Interceptor {

    private static Logger log = Logger.getLogger(FuncionAccesoInterceptor.class);
    private String idPerfil;
    private String idFuncion;
    private String idModulo;

    public FuncionAfterAccesoInterceptor() {
    }

    public FuncionAfterAccesoInterceptor(String idPerfil, String idModulo, String idFuncion) {
        this.idPerfil = idPerfil;
        this.idModulo = idModulo;
        this.idFuncion = idFuncion;
    }

    @Override
    public void destroy() {
        log.info(FuncionAccesoInterceptor.class.getName() + " destroy() es llamado...");
    }

    @Override
    public void init() {
        log.info(FuncionAccesoInterceptor.class.getName() + " init() es llamado...");
    }

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        log.info("Interceptando petición...");
        //actionInvocation.getStack().setParameter("idPerfil", idPerfil);
        actionInvocation.getInvocationContext().getParameters().put("idPerfil", idPerfil);
        actionInvocation.getInvocationContext().getParameters().put("idModulo", idModulo);
        actionInvocation.getInvocationContext().getParameters().put("idFuncion", idFuncion);

        log.info("idPerfil" + idPerfil);
        actionInvocation.addPreResultListener(new PreResultListener() {
            @Override
            public void beforeResult(ActionInvocation invocation, String resultCode) {
                log.info("Interceptando respuesta y validando funciones.");

                String idPerfil = (String) invocation.getInvocationContext().getParameters().get("idPerfil");
                String idModulo = (String) invocation.getInvocationContext().getParameters().get("idModulo");
                String idFuncion = (String) invocation.getInvocationContext().getParameters().get("idFuncion");

                log.info("idPerfil" + idPerfil);
                Map session = invocation.getInvocationContext().getSession();
                UsuarioSesion usuarioSesion = (UsuarioSesion) session.get("usuarioSesion");

                if (usuarioSesion != null) {

                    //session.clear();
                    log.info("FuncionAccesoInterceptor: Validando si [" + usuarioSesion.getIdUsuario() + "] "
                            + "tiene acceso a Perfil[" + idPerfil + "] Módulo[" + idModulo + "] y Función[" + idFuncion + "]");

                    if (!usuarioSesion.getAcTipo().equals("M")) {
                        if (idPerfil != null && !existePerfil(usuarioSesion, idPerfil)) {
                            log.info("FuncionAccesoInterceptor: Acceso Denegado. El perfil [" + idPerfil + "] no está asociado al usuario");
                            session.clear();
                            invocation.setResultCode("accesodenegado");
                        }
                        if ((idModulo!=null || idFuncion!=null) && !existeModuloFuncion(usuarioSesion, idModulo, idFuncion)) {
                            log.info("FuncionAccesoInterceptor: Acceso Denegado. El usuario no tiene acceso a módulo[" + idModulo + "] y Función[" + idFuncion + "]");
                            session.clear();
                            invocation.setResultCode("accesodenegado");
                        }
                    }
                }

            }
        });

        String result = actionInvocation.invoke();
        log.info("FuncionAccesoInterceptor: Success. " + result);
        return result;
    }

    public static Logger getLog() {
        return log;
    }

    public static void setLog(Logger log) {
        FuncionAfterAccesoInterceptor.log = log;
    }

    public String getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(String idPerfil) {
        this.idPerfil = idPerfil;
    }

    public String getIdFuncion() {
        return idFuncion;
    }

    public void setIdFuncion(String idFuncion) {
        this.idFuncion = idFuncion;
    }

    public String getIdModulo() {
        return idModulo;
    }

    public void setIdModulo(String idModulo) {
        this.idModulo = idModulo;
    }

    private static boolean existeModuloFuncion(UsuarioSesion usuarioSesion, String idFuncion, String idModulo) {
        boolean resp = false;

        Iterator iteratorPerfil = usuarioSesion.getPerfiles().iterator();

        while (iteratorPerfil.hasNext()) {
            Perfil auxPerfil = (Perfil) iteratorPerfil.next();

            Iterator iteratorFuncion = auxPerfil.getFunciones().iterator();

            while (iteratorFuncion.hasNext()) {
                Funcion auxFuncion = (Funcion) iteratorFuncion.next();
                if (idFuncion != null) {
                    if (auxFuncion.getIdModulo().trim().equals(idModulo) && auxFuncion.getIdFuncion().trim().equals(idFuncion)) {
                        resp = true;
                        break;
                    }
                } else //Solo se validará el módulo
                if (auxFuncion.getIdModulo().trim().equals(idModulo) || idModulo == null) {
                    resp = true;
                    break;
                }
            }
        }

        return resp;
    }

    private static boolean existePerfil(UsuarioSesion usuarioSesion, String idPerfil) {
        boolean resp = false;
        Iterator iteratorPerfil = usuarioSesion.getPerfiles().iterator();
        while (iteratorPerfil.hasNext()) {
            Perfil auxPerfil = (Perfil) iteratorPerfil.next();
            if (idPerfil != null) {
                if (auxPerfil.getIdPerfil().trim().equals(idPerfil)) {
                    resp = true;
                    log.info("El perfil existe. [" + auxPerfil.getIdPerfil() + "]");
                    break;
                }
            } else {
                resp = true;
                break;
            }
        }

        return resp;
    }

}
