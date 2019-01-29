<%-- 
    Document   : Perfil
    Created on : 11/04/2014, 12:42:23 PM
    Author     : jorojas
--%>
<%@taglib uri="/struts-tags" prefix="s" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
        <title>Perfil</title>
        <%@include file="../include/head.jsp" %>
        <body>
            <header>
                <%@include file="../include/header.jsp" %>
            </header>
            <jsp:include page="../include/LoadingJquery.jsp"/>
            <div class="container">
                <div class="content" id="content">
                    <br/><br/><br/>
                            
                    <div class="info-box" style="width: 80%;">
                        <h2>Perfil de <s:property value="#session.usuarioSesion.acNombre" /></h2>
                        <div class="first">
                            <table style="width:100%;">
                                <tr>
                                    <td><p><span class="text-1">Usuario: </span><s:property value="#session.usuarioSesion.idUsuario" /></p></td>
                                    <td><p><span class="text-1">Tipo: </span>
                                        <s:if test="%{#session.usuarioSesion.acTipo.equalsIgnoreCase('M')}">
                                            Master
                                        </s:if>
                                        <s:else>
                                            Usuario
                                        </s:else>
                                        </p>
                                    </td>
                                    <td><p><span class="text-1">Estatus: </span>
                                        <s:if test="%{#session.usuarioSesion.acEstatus.equalsIgnoreCase('A')}">
                                            Activo
                                        </s:if>
                                        <s:else>
                                            Inactivo
                                        </s:else>
                                        </p>
                                    </td>
                                </tr>
                                <tr>
                                    <td><p><p><span class="text-1">Nombre: </span><s:property value="#session.usuarioSesion.acNombre" /></p></td>
                                    <td><p><span class="text-1">Doc. de Identidad: </span><s:property value="#session.usuarioSesion.idPersona" /></p></td>
                                    <td><p><span class="text-1">Correo: </span><s:property value="#session.usuarioSesion.acEmail" /></p></td>
                                </tr>
                                <tr>
                                    <td><p><span class="text-1">Área/Ubicación: </span><s:property value="#session.usuarioSesion.acUbicacion" /></p></td>
                                    <td><p><span class="text-1">Modificado: </span><s:property value="#session.usuarioSesion.modificado" /></p></td>
                                    <td><p><span class="text-1">Modificado por: </span><s:property value="#session.usuarioSesion.modificadoPor" /></p></td>
                                </tr>
                            </table>
                        </div>
                    </div>

                    <br/>
                    <h2>Perfiles Asociados</h2>
                    <ol>
                    <s:iterator value="#session.usuarioSesion.perfiles" var="perfil">
                        <li><b>Perfil: </b><s:property value="idPerfil"/> | <s:property value="descripcion"/>

                            <table class="table" style="width:60%;margin-left: 15px;margin-top:5px;">
                                <thead><th>Módulo</th><th>Función</th><th>Descripción</th></thead>
                                <s:iterator value="#perfil.funciones" var="funcion">
                                    <tr><td><s:property value="idModulo"/></td><td><s:property value="idFuncion"/></td><td><s:property value="descripcion"/></td></tr>
                                </s:iterator>
                                <s:if test="%{#perfil.funciones.size()==0}">
                                    <tr><td colspan="3" style="text-align: center">No hay funciones asociadas a este perfil</td></tr>
                                </s:if>
                            </table>
                          <br/>
                      </li>
                    </s:iterator>
                    </ol>

                </div>
            </div>
        <footer>
            <%@include file="../include/footer.jsp" %>    
        </footer>
    </body>
</html>
