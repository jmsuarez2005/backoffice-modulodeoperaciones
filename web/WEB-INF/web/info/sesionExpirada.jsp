<%-- 
    Document   : SessionExpirada.jsp
    Created on : 07/07/2017, 12:42:23 PM
    Author     : etabban
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
                        <div class="info-box" style="width:30%;margin: 5em 0 0 43em;">
                        <center>
                           <h2>Atención</h2> 
                        </center>
                            <div class="first">
                                <table style="width:100%;">
                                    <tr>
                                        <td style="width: 50%;">
                                            <p>
                                                <img src="../recursos/images/img-alerta.png" style="float:left;padding: 5px;">
                                                <center>
                                                    <span class="text-1">Disculpe, su sesión ha caducado, ingrese nuevamente.</span>
                                                </center>
                                                <br/>
                                                <center>
                                                    <s:a action="login" namespace="/inicio">Ingresar</s:a>
                                                </center>
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
        <footer>
                <%@include file="../include/footer.jsp" %>    
        </footer>
    </body>
</html>
