/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.interceptors;

import com.novo.model.Funcion;
import com.novo.model.Perfil;
import com.novo.model.UsuarioSesion;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;
/**
 *
 * @author jorojas
 */
public class FuncionAccesoInterceptor implements Interceptor {
    private static Logger log = Logger.getLogger(FuncionAccesoInterceptor.class);
    private String idPerfil;
    private String idFuncion;
    private String idModulo;
    
    public FuncionAccesoInterceptor() {
    }
    
    public FuncionAccesoInterceptor(String idPerfil, String idModulo, String idFuncion) {
        this.idPerfil = idPerfil;
        this.idModulo = idModulo;
        this.idFuncion = idFuncion;
    }
    
    @Override
    public void destroy() {
        log.info(FuncionAccesoInterceptor.class.getName()+" destroy() es llamado...");
    }
    
    @Override
    public void init() {
        log.info(FuncionAccesoInterceptor.class.getName()+" init() es llamado...");
    }
    
    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        log.info("Interceptando petición...");
        Map session = actionInvocation.getInvocationContext().getSession();
        UsuarioSesion usuarioSesion = (UsuarioSesion)session.get("usuarioSesion");
        
        if (usuarioSesion == null) {
            return actionInvocation.invoke();
        }
        
        log.info("FuncionAccesoInterceptor: Validando si ["+usuarioSesion.getIdUsuario()+"] "
                + "tiene acceso a Perfil["+this.idPerfil+"] Módulo["+this.idModulo+"] y Función["+this.idFuncion+"]");
        
        //Validar siempre y cuando el usuario no sea Master. Si es Master, se le otorga acceso
        if (!usuarioSesion.getAcTipo().equals("M")){
            if(!this.existePerfil(usuarioSesion)){
                log.info("FuncionAccesoInterceptor: Acceso Denegado. El perfil ["+this.idPerfil+"] no está asociado al usuario");
//                session.clear();
                return "accesodenegado";
            }
            if(!this.existeModuloFuncion(usuarioSesion)){
                log.info("FuncionAccesoInterceptor: Acceso Denegado. El usuario no tiene acceso a Módulo["+this.idModulo+"] y Función["+this.idFuncion+"]");
                return "accesodenegado";
            }
        }
        String result = actionInvocation.invoke();
        log.info("FuncionAccesoInterceptor: Success. "+ result);
        return result;
    }
    
    private boolean existeModuloFuncion(UsuarioSesion usuarioSesion){
        boolean resp=false;
        
        Iterator iteratorPerfil = usuarioSesion.getPerfiles().iterator();
        
        while (iteratorPerfil.hasNext()){
            Perfil auxPerfil = (Perfil)iteratorPerfil.next();
            
            Iterator iteratorFuncion = auxPerfil.getFunciones().iterator();
            
            
                while (iteratorFuncion.hasNext()){
                    Funcion auxFuncion = (Funcion)iteratorFuncion.next();
                    if (this.idFuncion!=null){
                        if (auxFuncion.getIdModulo().trim().equals(this.idModulo) && auxFuncion.getIdFuncion().trim().equals(this.idFuncion)){
                            resp=true;
                            break;
                        }
                    }
                    else{
                        //Solo se validará el módulo
                        if (auxFuncion.getIdModulo().trim().equals(this.idModulo)||this.idModulo==null){
                            resp=true;
                            break;
                        }
                    }
                }
        }
        
        return resp;
    }
    
    private boolean existePerfil(UsuarioSesion usuarioSesion){
        boolean resp=false;
        Iterator iteratorPerfil = usuarioSesion.getPerfiles().iterator();        
        while (iteratorPerfil.hasNext()){
            Perfil auxPerfil = (Perfil)iteratorPerfil.next();            
            if (this.idPerfil!=null){
                if(auxPerfil.getIdPerfil().trim().equals(this.idPerfil)){
                    resp=true;
                    log.info("El perfil existe. ["+auxPerfil.getIdPerfil()+"]");
                    break;
                }
            }
            else{
                resp=true;
                break;
            }
        }
        
        
        return resp;
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

    public String getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(String idPerfil) {
        this.idPerfil = idPerfil;
    } 
    
}
