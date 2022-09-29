/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.interceptors;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import java.util.Map;

/**
 *
 * @author jorojas
 */
public class SessionCheckInterceptor implements Interceptor {
    
    public SessionCheckInterceptor() {
    }
    
    public void destroy() {
        System.out.println("SessionCheckInterceptor destroy() es llamado..");
    }
    
    public void init() {
        System.out.println("SessionCheckInterceptor init() es llamado..");
    }
    
    public String intercept(ActionInvocation actionInvocation) throws Exception {
      Map<String,Object> session = actionInvocation.getInvocationContext().getSession();
      if(session.isEmpty()) {
            return "session";
        } // session is empty/expired
      else if (session.containsKey("usuarioSesion")!=true){
          return "session";
      }
      return actionInvocation.invoke();
    }
}
