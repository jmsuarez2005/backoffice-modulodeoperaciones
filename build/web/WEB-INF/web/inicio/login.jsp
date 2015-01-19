<%-- 
    Document   : StrutsTagsJSP
    Created on : 11/04/2014, 12:42:23 PM
    Author     : jorojas
--%>
<%@taglib uri="/struts-tags" prefix="s" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Log in</title>
        <%@include file="../include/head.jsp" %>
         
    </head>
    <body>            
            <%@include file="../include/header.jsp" %>
            
            <div class="container">
                <div class="content" id="content">

                    <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
                    <center>
                        <div class="shadowBox bgSilver" id="loginBox">
                            <center>
                                <h3>Inicio de Sesión</h3>
                                <s:form method="post" action="LoginAction" id="formulario">
                                <s:textfield name="user" label="Usuario" required="true" size="25" cssStyle="width:175px;" />
                                <s:password id="password" name="password"  label="Clave" required="true" size="25" cssStyle="width:175px;" />
                                <s:submit value="Ingresar" onclick="javascript:enviar_formularioHash();preshow()" onload="prehide()" />
                                </s:form>
                            </center>
                        </div>
                        <div style="width: 315px">        
                            <%@include file="../include/alertMessage.jsp" %>
                        </div>
                    </center>

                </div>
            </div>
            
            <%@include file="../include/footer.jsp" %>
    </body>
</html>

