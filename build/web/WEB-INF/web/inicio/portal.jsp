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

        <title>Inicio</title>
        <%@include file="../include/head.jsp" %>
    </head>
    <body>
        
            <%@include file="../include/header.jsp" %>
            <div class="container">
                <div class="content" id="content">
                    <br/><br/><br/><br/><br/>
                    
                    <div class="info-box" style="width:50%;margin:0 auto;">
                        <h2>Portal</h2>
                        <div class="first">
                            <span class="text-1">Usuario: </span>
                                <s:property value="#session.usuarioSesion.acNombre" />, <s:property value="#session.usuarioSesion.acUbicacion" />
                        </div>
                    </div>
                </div>
            </div>
            <%@include file="../include/footer.jsp" %>
        
    </body>
</html>
