/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novo.constants;

/**
 *
 * @author jorojas
 */
public interface BaseQuery {
    //[obtener,insertar,modificar,eliminar] + Modelo(s) + [Descrip] + Query

    String obtenerUsuarioQuery = "SELECT * FROM teb_adm_usuario WHERE idusuario='$IDUSUARIO'";

    String obtenerPerfilesUsuarioQuery = "SELECT up.idperfil,p.acdesc from teb_adm_up up INNER JOIN teb_adm_perfil p ON up.idperfil=p.idperfil WHERE up.idusuario='$IDUSUARIO' GROUP BY up.idperfil,p.acdesc ORDER BY up.idperfil ASC";

    String obtenerFuncionesPerfilQuery = "SELECT pmf.idperfil,pmf.idmodulo,pmf.idfuncion,mf.acdescfun FROM teb_adm_pmf pmf INNER JOIN teb_adm_mod_fun mf ON pmf.idfuncion=mf.idfuncion AND pmf.idmodulo=mf.idmodulo WHERE pmf.idperfil='$IDPERFIL' GROUP BY pmf.idperfil,pmf.idmodulo,pmf.idfuncion,mf.acdescfun ORDER BY pmf.idmodulo ASC,pmf.idfuncion ASC";

    String obtenerFuncionesUsuarioQuery = "SELECT umf.idmodulo,umf.idfuncion,mf.acdescfun FROM teb_adm_umf umf INNER JOIN teb_adm_mod_fun mf ON umf.idfuncion=mf.idfuncion AND umf.idmodulo=mf.idmodulo WHERE umf.idusuario='$IDUSUARIO' GROUP BY umf.idmodulo,umf.idfuncion,mf.acdescfun ORDER BY umf.idmodulo ASC,umf.idfuncion ASC";

    String actualizarClaveUsuarioQuery = "UPDATE teb_adm_usuario SET acclave='$ACCLAVE' WHERE idusuario='$IDUSUARIO'";

    String actualizarFechaLoginUsuarioQuery = "UPDATE teb_adm_usuario SET dtfecha_ult_acceso = CURRENT  , app_ultimo_acceso = 'MOD_OPERACIONES' where idusuario = '$IDUSUARIO'";
    
}
