<%-- 
    Document   : StrutsTagsJSP
    Created on : 11/04/2014, 12:42:23 PM
    Author     : jorojas
--%>
<%@taglib uri="/struts-tags" prefix="s" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<s:bean name="com.novo.util.CountriesUtil" var="country" />
<!DOCTYPE html>

<%@ page import='com.novo.util.*' %>
<html>
    
     <%@include file="../include/head_login.jsp" %>
    
     <body>
        <header>
            <%@include file="../include/header_login.jsp" %>
        </header>
        <div class="cont">
            <div class="form">
                <center>
                    <s:form method="post" action="LoginAction" cssClass="frm" id="formulario">
                    <s:select  name="Pais" headerKey="" headerValue="Seleccione un País" list="#country.countries" listKey="key" listValue="value" required="true" placeholder="Seleccione un País" cssClass="icon-pais"/> 
                    <s:textfield name="user" required="true" placeholder="Usuario" cssClass="icon-user" />
                    <s:password id="password" name="password" required="true" placeholder="Contraseña" cssClass="icon-pass" />
                    <s:submit value="Ingresar" onclick="javascript:enviar_formularioHash();preshow()" onload="prehide()" cssClass="button" />   
                    <!--  < s:textfield name="user" label="Usuario" required="true" size="25" cssStyle="width:175px;" />
                        < s:password id="password" name="password"  label="Clave" required="true" size="25" cssStyle="width:175px;" />
                        < s:select  name="country" headerKey="" headerValue="Seleccione un País" list="#country.countries" listKey="key" listValue="value" required="true" placeholder="Seleccione un País"/>    -->                         
                    <%@include file="../include/alertMessage.jsp" %>
                </s:form>
                </center>
            </div>
        </div>
        <footer>
            <%@include file="../include/footer.jsp" %>    
        </footer> 
    </body>
</html>

