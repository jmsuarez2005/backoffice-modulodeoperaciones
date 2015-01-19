<%-- 
    Document   : Portal
    Created on : 11/04/2014, 12:42:23 PM
    Author     : jorojas
--%>
<%@taglib uri="/struts-tags" prefix="s" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>

        <title>Cambio de Clave</title>
        <%@include file="../include/head.jsp" %>
    </head>
    <body>
        
            <%@include file="../include/header.jsp" %>
            <div class="container">
                <div class="content" id="content">
                    <br/><br/><br/><br/><br/><br/><br/><br/>
                    
                    <div class="info-box" style="margin:0 auto;width: 770px;">
                        <h2>Cambio de Clave</h2>
                        <div class="first">
                            <s:form theme="simple" action="Clave">
                                <s:property value="#session.usuarioSesion.acNombre" />, <s:property value="#session.usuarioSesion.acUbicacion" /><br/><br/>
                                

                                <div style="float:left;width:50%;">
                                    <b class="text-1">Clave Actual: </b>
                                    <br/><s:password name="claveActual" size="45" /><br/><br/><br/>
                                    <b class="text-1">Clave Nueva: </b>
                                    <br/><s:password name="newpw" size="45" /><br/><br/><br/>
                                    <b class="text-1">Confirmación de clave nueva: </b>
                                    <br/><s:password name="newpwcf" size="45" /><br/><br/><br/>
                                    <center><s:submit value="Cambiar Clave" action="cambiarClave" onclick="return validarCambioClave(this.form,event)" /> <s:reset value="Reset" /></center>
                                </div>
                                <div style="float:left;width:50%;">
                                    <div style="padding-left: 30px;border-left: 1px #dddddd solid;">
                                        <b class="text-1">Condiciones para crear la nueva clave:</b>
                                        <ol>
                                            <li id="tip1">Debe tener mínimo 8 caracteres y máximo 15 caracteres <img class="icon" id="tip1_icon" src="../recursos/icons/low_rating.png" style="height:15px;width:auto;" /></li>
                                            <li id="tip2">Debe contener al menos un caracter especial (ej: ¡@?+-.,#) <img class="icon" id="tip2_icon" src="../recursos/icons/low_rating.png" style="height:15px;width:auto;" /></li>
                                            <li id="tip3">No debe contener más de 3 caracteres iguales consecutivos <img class="icon" id="tip3_icon" src="../recursos/icons/low_rating.png" style="height:15px;width:auto;" /></li>
                                            <li id="tip4">Debe tener de 1 a 3 números <img class="icon" id="tip4_icon" src="../recursos/icons/low_rating.png" style="height:15px;width:auto;" /></li>
                                            <li id="tip5">Debe tener al menos una letra mayúscula <img class="icon" id="tip5_icon" src="../recursos/icons/low_rating.png" style="height:15px;width:auto;" /></li>
                                            <li id="tip6">Debe tener al menos una letra <img class="icon" id="tip6_icon" src="../recursos/icons/low_rating.png" style="height:15px;width:auto;" /></li>
                                        </ol>
                                    </div>
                                </div>
                                    
                                <div style="clear:both">
                                    <!-- pie de la caja informativa -->
                                    <br/>
                                    <s:fielderror />
                                    <br/>
                                </div>
                                
                            </s:form>
                        </div>
                    </div>
                    
                    <br/>
                    
                    <div style="width: 770px;margin: 0 auto;">        
                        <%@include file="../include/alertMessage.jsp" %>
                    </div>
                                
                </div>
            </div>
            <%@include file="../include/footer.jsp" %>
    </body>
</html>