<%-- 
    Document   : AccesoDenegado.jsp
    Created on : 11/04/2014, 12:42:23 PM
    Author     : jorojas
--%>
<%@taglib uri="/struts-tags" prefix="s" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>En Proceso</title>
        <%@include file="../include/head.jsp" %>
    </head>
    <body>
        <%@include file="../include/header.jsp" %>
        <div class="container">
                <div class="content" id="content">
                    <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
                    <center>
                        <div class="shadowBox" id="shadowBox">
                            <p>Por favor espere...</p>
                            <center><img src="../recursos/images/ajax_loader.gif"/></center>
                        </div>
                    </center>
                </div>
        </div>
        <%@include file="../include/footer.jsp" %>
    </body>
</html>
