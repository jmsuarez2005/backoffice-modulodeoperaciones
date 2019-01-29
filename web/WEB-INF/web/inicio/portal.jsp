<%-- 
    Document   : Portal
    Created on : 11/04/2014, 12:42:23 PM
    Author     : jorojas
--%>
<%@taglib uri="/struts-tags" prefix="s" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<s:bean name="com.novo.util.CountriesUtil" var="countryUtil" />
<!DOCTYPE html>
<html>
    <%@include file="../include/head.jsp" %>
        <body>
            <header>
                <%@include file="../include/header.jsp" %>
            </header>
            <div class="container">
                <div class="content" id="content">
                    <div class="info-box" style="width:50%;margin:0 auto;">
                        <h2>Portal</h2>
                            <div class="first">
                                <span class="text-1">Usuario: </span>
                                    <s:property value="#session.usuarioSesion.acNombre" />, <s:property value="#session.usuarioSesion.acUbicacion" />
                            </div>
                    </div>
                </div>
            </div>
        <footer>
            <%@include file="../include/footer.jsp" %>    
        </footer>   
    </body>
</html>
