/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.dao;

import com.novo.constants.BaseQuery;
import com.novo.constants.RCConfig;
import com.novo.database.Dbinterface;
import com.novo.database.NovoDAO;
import com.novo.model.Funcion;
import com.novo.model.Perfil;
import com.novo.model.ResponseRC;
import com.novo.model.UsuarioSesion;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author jorojas
 */
public class UsuarioDAO extends NovoDAO implements BaseQuery, RCConfig {

    private static Logger log = Logger.getLogger(UsuarioDAO.class);
    private ResponseRC rc;

    public UsuarioDAO(String appName, String[] databases) {
        super(appName, databases);
        log.info("UsuarioDAO inicializado, sin configuración de país.");
        rc = new ResponseRC();
    }

    public UsuarioDAO(String appName, String[] databases, String pais) {
        super(appName, databases, pais);
        log.info("UsuarioDAO inicializado, país: " + pais);
        rc = new ResponseRC();
    }

    public UsuarioSesion obtenerUsuarioSesion(String idUsuario) {
        UsuarioSesion usuario = null;

        String query = obtenerUsuarioQuery.replace("$IDUSUARIO", idUsuario);

        log.info("Ejecutando [" + query + "]" + UsuarioDAO.ORACLE);
        try {
            Dbinterface dbo = ds.get(UsuarioDAO.ORACLE);

            dbo.dbreset();

            if (dbo.executeQuery(query) == 0) {
                if (dbo.nextRecord()) {
                    usuario = new UsuarioSesion();
                    usuario.setIdUsuario(idUsuario);
                    usuario.setAcNombre(dbo.getFieldString("acnombre").trim());
                    usuario.setPassword(dbo.getFieldString("acclave").trim());
                    usuario.setAcEstatus(dbo.getFieldString("acestatus").trim());
                    usuario.setAcTipo(dbo.getFieldString("actipo").trim());
                    usuario.setAcUbicacion(dbo.getFieldString("acubicacion").trim());
                    usuario.setAcEmail(dbo.getFieldString("acemail").trim());
                    usuario.setIdPersona(dbo.getFieldString("idpersona").trim());
                    usuario.setModificadoPor(dbo.getFieldString("cidusuario").trim());
                    usuario.setModificado(new Date(dbo.getFieldString("timestamp").split(" ")[0].replace("-", "/")));

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    usuario.setSessionDate(dateFormat.format(date));

                    log.info("Se encontró el usuario " + usuario.getIdUsuario());
                }
            } else {
                log.error("Ocurrió un error al intentar buscar al usuario [" + idUsuario + "] Error[" + dbo.msgErr + "]");
                rc.setRc(rcError);
                rc.setMensaje("-> Error Interno [" + dbo.msgErr + "]");
            }
            dbo.dbClose();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return usuario;
    }

    public ArrayList<Perfil> obtenerPerfilesUsuario(String idUsuario) {
        ArrayList<Perfil> perfiles = new ArrayList();

        String query = obtenerPerfilesUsuarioQuery.replace("$IDUSUARIO", idUsuario);

        log.info("Ejecutando [" + query + "]" + UsuarioDAO.ORACLE);
        try {
            Dbinterface dbo = ds.get(UsuarioDAO.ORACLE);
            dbo.dbreset();

            if (dbo.executeQuery(query) == 0) {
                while (dbo.nextRecord()) {
                    Perfil aux = new Perfil();
                    aux.setIdPerfil(dbo.getFieldString("idperfil"));
                    aux.setDescripcion(dbo.getFieldString("acdesc"));
                    perfiles.add(aux);
                }
            }
            dbo.dbClose();

            log.info("--- FUNCIONES ---");
            int f = 0;
            while (perfiles.size() > f) {
                perfiles.get(f).setFunciones(this.obtenerFuncionesPerfil(perfiles.get(f).getIdPerfil()));
                f++;
                log.info("-----------");
            }

        } catch (Exception e) {
            log.info("obtenerPerfilesUsuario: Ocurrió un error al intentar buscar los perfiles de usuario.");
            log.info(e.getMessage() + " localizado en: " + e.getLocalizedMessage());
        }

        return perfiles;
    }

    public ArrayList<Funcion> obtenerFuncionesPerfil(String idPerfil) {
        ArrayList<Funcion> funciones = new ArrayList();

        String query = obtenerFuncionesPerfilQuery.replace("$IDPERFIL", idPerfil);

        log.info("Ejecutando [" + query + "]" + UsuarioDAO.ORACLE);
        try {
            Dbinterface dbo = ds.get(UsuarioDAO.ORACLE);
            dbo.dbreset();

            if (dbo.executeQuery(query) == 0) {
                while (dbo.nextRecord()) {
                    Funcion aux = new Funcion();
                    aux.setIdFuncion(dbo.getFieldString("idfuncion"));
                    aux.setDescripcion(dbo.getFieldString("acdescfun"));
                    aux.setIdModulo(dbo.getFieldString("idmodulo"));

                    funciones.add(aux);
                }
            }
            dbo.dbClose();
        } catch (Exception e) {
            log.info("obtenerFuncionesPerfil: Ocurrió un error al intentar buscar las funciones del perfil " + idPerfil);
            log.info(e.getMessage() + " localizado en: " + e.getLocalizedMessage());
        }
        log.info("Se encontraron " + funciones.size() + " funciones para el perfil " + idPerfil);

        return funciones;
    }

    public ArrayList<Funcion> obtenerFuncionesUsuario(String idUsuario) {
        ArrayList<Funcion> funciones = new ArrayList();

        String query = obtenerFuncionesUsuarioQuery.replace("$IDUSUARIO", idUsuario);

        log.info("Ejecutando [" + query + "]" + UsuarioDAO.ORACLE);
        try {
            Dbinterface dbo = ds.get(UsuarioDAO.ORACLE);
            dbo.dbreset();

            if (dbo.executeQuery(query) == 0) {
                while (dbo.nextRecord()) {
                    Funcion aux = new Funcion();
                    aux.setIdFuncion(dbo.getFieldString("idfuncion"));
                    aux.setDescripcion(dbo.getFieldString("acdescfun"));
                    aux.setIdModulo(dbo.getFieldString("idmodulo"));

                    funciones.add(aux);
                }
            }
            dbo.dbClose();
        } catch (Exception e) {
            log.info("obtenerFuncionesUsuario: Ocurrió un error al intentar buscar las funciones de un usuario.");
            log.info(e.getMessage() + " localizado en: " + e.getLocalizedMessage());
        }

        log.info("Se encontraron " + funciones.size() + " funciones para el usuario " + idUsuario);

        return funciones;
    }

    public boolean actualizarClaveUsuario(String idUsuario, String acClave) {
        log.info("actualizarClaveUsuario(" + idUsuario + ")");
        boolean modificado = false;
        String query = actualizarClaveUsuarioQuery;
        query = query.replace("$IDUSUARIO", idUsuario);
        query = query.replace("$ACCLAVE", acClave);

        log.info("Ejecutando [" + query + "]" + UsuarioDAO.ORACLE);
        try {
            Dbinterface dbo = ds.get(UsuarioDAO.ORACLE);
            dbo.dbreset();
            if (dbo.executeQuery(query) != 0) {
                log.error("actualizarClaveUsuario: Error modificando clave de " + idUsuario + " [" + query + "]");
            } else {
                log.info("actualizarClaveUsuario: Modificación Exitosa");
                modificado = true;
            }

            dbo.dbClose();
        } catch (Exception ex) {
            log.info("modificarConsulta: Se capturó una excepción al intentar ejecutar la consulta: [" + query + "]");
            log.info("Causa: " + ex.getMessage() + " localizado en: " + ex.getLocalizedMessage());
        }

        return modificado;
    }

    public boolean setLastLoginDate(String idUsuario) {
        log.info("setLastLoginDate(" + idUsuario + ")");
        boolean modificado = false;
        String query = actualizarFechaLoginUsuarioQuery;
        query = query.replace("$IDUSUARIO", idUsuario);

        log.info("Ejecutando [" + query + "]" + UsuarioDAO.ORACLE);
        try {
            Dbinterface dbo = ds.get(UsuarioDAO.ORACLE);
            dbo.dbreset();
            if (dbo.executeQuery(query) != 0) {
                log.error("actualizarFechaLoginUsuario: Error actualizando fecha login de " + idUsuario + " [" + query + "]");
            } else {
                log.info("actualizarFechaLoginUsuario: Modificación Exitosa");
                modificado = true;
            }

            dbo.dbClose();
        } catch (Exception ex) {
            log.info("modificarConsulta: Se capturó una excepción al intentar ejecutar la consulta: [" + query + "]");
            log.info("Causa: " + ex.getMessage() + " localizado en: " + ex.getLocalizedMessage());
        }

        return modificado;
    }

    @Override
    public void closeConection() {
        this.shutdownDatabases();
    }

    public ResponseRC getRc() {
        return rc;
    }

}
