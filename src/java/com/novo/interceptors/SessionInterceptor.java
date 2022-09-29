/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.interceptors;

import com.novo.model.UsuarioSesion;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author etabban
 */
public class SessionInterceptor implements Interceptor {

    private static Logger log = Logger.getLogger(SessionInterceptor.class);

    public SessionInterceptor() {
    }

    public void destroy() {
        System.out.println("SessionInterceptor destroy() es llamado...");
    }

    public void init() {
        System.out.println("SessionInterceptor init() es llamado...");
    }

    public String intercept(ActionInvocation actionInvocation) throws Exception {
        Map session = actionInvocation.getInvocationContext().getSession();
        UsuarioSesion usuarioSesion = (UsuarioSesion) session.get("usuarioSesion");
        

        if (session.isEmpty()) {
            return "session";
        } // session is empty/expired
        else if (session.containsKey("usuarioSesion") != true) {
            return "session";
        }
        return actionInvocation.invoke();
    }
}
