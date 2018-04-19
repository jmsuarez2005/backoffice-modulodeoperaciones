/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.process.temp;

import com.novo.constants.BasicConfig;
import com.novo.constants.RCConfig;
import com.novo.dao.temp.UsuarioDAOINF;
import com.novo.model.Funcion;
import com.novo.model.Perfil;
import com.novo.model.ResponseRC;
import com.novo.model.UsuarioSesion;
import com.novo.util.Utils;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author jorojas
 */
public class UsuarioProcINF implements BasicConfig, RCConfig {

    private static Logger log = Logger.getLogger(UsuarioProcINF.class);
    private ResponseRC rc;
    private UsuarioSesion usuarioSesion;

    public UsuarioProcINF() {
        rc = new ResponseRC();
        rc.setRc(-1);
        usuarioSesion = null;
    }

    public int login(String user, String password, String pais) {
        UsuarioDAOINF usuarioDAO = new UsuarioDAOINF(appName, databases, pais);
        UsuarioSesion usuario;
        usuario = usuarioDAO.obtenerUsuarioSesion(user.toUpperCase());

        if (usuario == null) {
            rc.setRc(rcUsuarioNoEncontrado);
            rc.setMensaje("Usuario No Encontrado");

            if (usuarioDAO.getRc().getRc() == rcError) {
                rc.setMensaje(rc.getMensaje() + " " + usuarioDAO.getRc().getMensaje());
            }
        } else {
            //Para soportar el reset de clave
            if (usuario.getPassword().length() == 0) {
                log.info("Se detectó un reset de clave [" + usuario.getIdUsuario() + "]");
                usuario.setPassword(usuario.getIdPersona().trim());
            }
            //

            if (usuario.getPassword().length() < 32) {
                rc.setRc(rcCambiarClave);
                rc.setMensaje("Por seguridad, debe actualizar su clave");

                try {
                    usuario.setPassword(com.novo.objects.util.MD5Hash.hash(usuario.getPassword()));
                } catch (NoSuchAlgorithmException ex) {
                    rc.setRc(rcError);
                    rc.setMensaje("Ocurrió un problema en el procesamiento del usuario");
                    java.util.logging.Logger.getLogger(UsuarioProcINF.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            if (usuario.getPassword().toUpperCase().equals(password.toUpperCase()) != true) {
                rc.setRc(rcClaveIncorrecta);
                rc.setMensaje("Clave Incorrecta");
            } else if (usuario.getAcEstatus().equals("A") != true) {
                rc.setRc(rcUsuarioInactivo);
                rc.setMensaje("Usuario Inactivo");
            } else {
                if (rc.getRc() != rcCambiarClave) {
                    rc.setRc(rcUsuarioExitoso);
                }
                usuarioSesion = usuario;
                log.info("Usuario Encontrado: " + this.usuarioSesion.print());
                
                //Actualizo fecha de ultima sesion
                boolean loginDate = usuarioDAO.setLastLoginDate(user.toUpperCase());
                if (!loginDate) {
                    rc.setRc(rcFechaLogin);
                    rc.setMensaje("Error al actualizar fecha login del usuario");
                }

                this.obtenerPerfiles(usuarioDAO);
            }

        }

        usuarioDAO.closeConection();
        return rc.getRc();
    }

    /*
     * obtenerPerfiles
     * D: Método para obtener los perfiles de un usuario, se agrega un perfil adicional por defecto para aquellas
     * funciones del usuario que no estén relacionadas con un perfil (funciones que le fueron agregadas particularmente)
     */
    private void obtenerPerfiles(UsuarioDAOINF usuarioDAO) {

        //Si es Usuario Master, no se obtienen sus perfiles por tener Full Access
        if (this.usuarioSesion.getAcTipo().equals("M") != true) {

            ArrayList<Perfil> perfiles = usuarioDAO.obtenerPerfilesUsuario(this.usuarioSesion.getIdUsuario());

            Perfil perfilDefault = new Perfil();
            perfilDefault.setIdPerfil("DEFAULT");
            perfilDefault.setDescripcion("FUNCIONES ASOCIADAS DIRECTAMENTE AL USUARIO");
            perfilDefault.setFunciones(usuarioDAO.obtenerFuncionesUsuario(this.usuarioSesion.getIdUsuario()));

            usuarioSesion.getPerfiles().add(perfilDefault);
            usuarioSesion.getPerfiles().addAll(perfiles);

        } else {
            Perfil perfilMaster = new Perfil();
            perfilMaster.setIdPerfil("MASTER");
            perfilMaster.setDescripcion("PERFIL DE ADMINISTRADOR");

            Funcion funcion = new Funcion();
            funcion.setIdModulo("MASTER");
            funcion.setIdFuncion("MASTER");
            funcion.setDescripcion("Acceso Ilimitado");
            perfilMaster.getFunciones().add(funcion);

            usuarioSesion.getPerfiles().add(perfilMaster);
        }
    }

    public boolean cambiarClave(UsuarioSesion usuario, String claveActual, String claveNueva) {
        this.usuarioSesion = usuario;
        String pais = Utils.getConfig(BasicConfig.properties).getProperty("pais");

        if (usuario.getPassword().toUpperCase().equals(claveActual.toUpperCase())) {
            //Proceder a Actualizar la clave
            UsuarioDAOINF usuarioDAO = new UsuarioDAOINF(appName, databases, pais);

            usuarioDAO.actualizarClaveUsuario(this.usuarioSesion.getIdUsuario(), claveNueva);
            this.usuarioSesion.setPassword(claveNueva.toUpperCase());
            usuarioDAO.closeConection();
            return true;
        } else {
            rc.setRc(rcClaveIncorrecta);
            rc.setMensaje("La clave actual introducida es incorrecta.");
            log.info(rc.getMensaje());
            return false;
        }
    }

    public ResponseRC getRc() {
        return rc;
    }

    public UsuarioSesion getUsuarioSesion() {
        return usuarioSesion;
    }

}
