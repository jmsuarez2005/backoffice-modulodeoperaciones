<%-- 
    Document   : AccesoDenegado.jsp
    Created on : 11/04/2014, 12:42:23 PM
    Author     : jorojas
--%>
<%@taglib uri="/struts-tags" prefix="s" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@include file="../include/head.jsp" %>
        <body>
            <header>
                <%@include file="../include/header.jsp" %>
            </header>
        <div class="container">  
                <div class="content" id="content">
                    <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>

                    
                        <div class="shadowBox" id="shadowBox" style="margin:0 auto;">
                            <img src="../recursos/images/img-alerta.png" style="float:left;padding: 5px;">
                            <p>Usted no tiene acceso a este sitio, consulte con su supervisor.</p>
                            <br/>
                            <center><s:a action="portal" namespace="/inicio">Ir al Portal</s:a></center>
                        </div> <!-- s:a action="portal" namespace="/inicio" /s:a> -->
                        <!-- <a href="javascript:goBack();">Volver</a> -->
                        
                </div>
        </div>
        <footer>
                <%@include file="../include/footer.jsp" %>    
        </footer>
    </body>
</html>
