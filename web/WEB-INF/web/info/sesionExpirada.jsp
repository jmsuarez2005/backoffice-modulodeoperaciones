<%-- 
    Document   : SessionExpirada.jsp
    Created on : 07/07/2017, 12:42:23 PM
    Author     : etabban
--%>
<%@taglib uri="/struts-tags" prefix="s" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Sesion Expirada</title>
        <%@include file="../include/head.jsp" %>
    </head>
    <body>
        <%@include file="../include/header.jsp" %>
        <div class="container">  
                <div class="content" id="content">
                    <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>

                    
                        <div class="shadowBox" id="shadowBox" style="margin:0 auto;">
                            <img src="../recursos/images/img-alerta.png" style="float:left;padding: 5px;">
                            <p>La sesion ha expirado, por favor ingresar nuevamente</p>
                            <br/>
                            <center><s:a action="portal" namespace="/inicio">Ir al inicio</s:a></center>
                        </div> <!-- s:a action="portal" namespace="/inicio" /s:a> -->
                        <!-- <a href="javascript:goBack();">Volver</a> -->
                        
                </div>
        </div>
        <%@include file="../include/footer.jsp" %>
    </body>
</html>
