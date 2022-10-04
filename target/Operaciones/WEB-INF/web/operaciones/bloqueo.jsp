<%-- 
    Document   : bloqueoDesbloqueo
    Created on : 01/08/2016, 04:11:37 PM
    Author     : rgomez
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s" %>
<%@taglib uri="/struts-dojo-tags" prefix="sx" %>
<!DOCTYPE html>
<html>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
     <title>Bloqueo</title>
     <%@include file="../include/head_1.jsp" %>
    
        <body>
            <header>
                <%@include file="../include/header.jsp" %>
            </header>
            <jsp:include page="../include/LoadingJquery.jsp"/>
            <div class="container">
            <div class="content" id="content">
                <h1 style="margin: 0 0 0 0;">Bloqueo</h1>
                <br/>
                <s:if test="%{tipoMessage.equals(\"error\")}">
                    <div class="alert alert-error">
                        <s:property value = "%{message}" /> 
                    </div>
                </s:if>
                <s:elseif  test="%{!message.equals(\"\")}">
                    <div class="alert alert-info">
                        <s:property value = "%{message}" />
                    </div>
                </s:elseif>

                <s:if test="%{getTarjetas().size()>0}">
                    <div class="panel" style="width: 900px;margin: 0 auto;">
                        <table class="table" style="width: 100%;">
                            <thead>
                                <tr><th colspan="7" style="text-align:center;">Tarjetas</th></tr>
                                <tr>
                                    <th>Tarjeta</th>
                                    <th>DNI</th>
                                    <th>Producto</th>
                                    <th>Nombre</th>
                                </tr>
                            </thead>
                            <tbody>                                      
                                <s:iterator value="tarjetas" var="myObj">
                                    <s:form theme="simple" namespace="/operaciones" action="Sobregiros">
                                        <tr>
                                            <td style="width:25%;text-align:center;" colspan="1">                                                
                                                <s:property value ="mascara"/>
                                            </td>    
                                            <td style="width:25%;text-align:center;" colspan="1"><s:property value="idExtPer"/></td>                                            
                                            <td style="width:25%;text-align:center;" colspan="1"><s:property value="cardProgram"/></td>                                            
                                            <td style="width:25%;text-align:center;" colspan="1"><s:property value="nombreCliente"/></td>                                                                                       
                                        </tr>
                                    </s:form>
                                </s:iterator>
                            </tbody>
                        </table>
                    </div>
                </s:if>
                <div class="panel" style="width: 900px;margin: 0 auto;"> 
                    <s:form action="bloqueo" method="post" enctype="multipart/form-data" theme = "simple">
                        <table class="table" style="width: 100%;">
                            <thead>
                                <tr><th colspan="3" style="text-align:center;">Procesar Bloqueo Masivo</th></tr>
                            </thead>
                            <tbody>                                
                                <tr>
                                    <td><label>Cargar Archivo :</label></td>
                                    <td><s:file cssClass="btn btn-info" name="upload" label="File"/></td>  
                                    <td><select name="selectedBloqueo">
                                            <s:iterator value="tipoBloqueo" var="myObj1">

                                                <option value="<s:property value ="codigo"/>"> <s:property value ="tipo_bloque"/></option>


                                            </s:iterator></select>
                                    </td>
                                </tr>

                                <tr>                                    
                                    <td colspan="3" style="text-align:center"  ><s:submit cssClass="btn btn-primary" value="Cargar Archivo" position="center" action= "Uploadbloqueo" onclick="openDialogLoading()"/></td>                                   

                                </tr>

                            </tbody>      
                        </table>                        
                    </s:form>
                    <div class="panel" style="width: 900px;margin: 0 auto;">
                        <table class="table" style="width: 100%;">
                            <thead>
                                <tr><th colspan="7" style="text-align:center;">Tarjetas</th></tr>
                                <tr>
                                    <th>Fecha</th>
                                    <th>Tarjeta</th>  
                                    <th>Mensaje</th>
                                </tr>
                            </thead>
                            <tbody>                                      
                                <s:iterator value="listbloquex" var="myObj">

                                    <tr>
                                        <td style="width:25%;text-align:center;" colspan="1"><s:property value="fec_reg"/></td>    
                                        <td style="width:25%;text-align:center;" colspan="1"><s:property value="nro_tarjeta"/></td>                                            
                                        <td style="width:25%;text-align:center;" colspan="1"><s:property value="descripcion"/></td>
                                    </tr>

                                </s:iterator>
                            </tbody>
                        </table>
                    </div>
                </div>   
            </div>           
        </div>  
         <footer>
            <%@include file="../include/footer.jsp" %>    
        </footer>
        <!--
        <script>
            var contador = 1;
            document.getElementById("boton1").addEventListener("click", function (event) {
                event.preventDefault();
                despliegue();
            });
            function despliegue() {
                document.getElementById("listaCampos" + contador++).style.display = "table-row";

            }
        </script>
        -->
    </body>
</html>
