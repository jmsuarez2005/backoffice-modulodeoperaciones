<%-- 
    Document   : AjusteSaldo
    Created on : 03/08/2015, 10:22:00 AM
    Author     : ggutierrez
--%>
<%@taglib uri="/struts-tags" prefix="s" %>
<%@taglib uri="/struts-dojo-tags" prefix="sx" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
   <head>
        <title>Sobregiros</title>
        <%@include file="../include/head_1.jsp" %>
    
        <body>
            <header>
                <%@include file="../include/header.jsp" %>
            </header>
            <jsp:include page="../include/LoadingJquery.jsp"/>
            <div class="container">
            <div class="content" id="content">
                <h1 style="margin: 0 0 0 0;">Sobregiros</h1>
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
                    <s:form action="Sobregiros" method="post" enctype="multipart/form-data" theme = "simple">
                        <table class="table" style="width: 100%;">
                            <thead>
                                <tr><th colspan="3" style="text-align:center;">Procesar Sobregiros Masivos</th></tr>
                            </thead>
                            <tbody>                                
                                <tr>
                                    <td><label>Cargar Archivo :</label></td>
                                    <td><s:file cssClass="btn btn-info" name="file" label="File"/></td>  
                                    <td><select name="selectedAjuste"><s:iterator value="tipoAjustes" var="myObj1">
                                                    <s:if test="#myObj1.idCodigoAjuste==16">
                                                        <option value="<s:property value ="idCodigoAjuste"/>"> <s:property value ="descripcion"/></option>
                                                        </s:if>
                                                        <s:elseif test="#myObj1.idCodigoAjuste==6">
                                                            <option value="<s:property value ="idCodigoAjuste"/>"> <s:property value ="descripcion"/></option>
                                                           </s:elseif>
                                                           <s:elseif test="#myObj1.idCodigoAjuste==2">
                                                            <option value="<s:property value ="idCodigoAjuste"/>"> <s:property value ="descripcion"/></option></s:elseif></s:iterator></select>
                                       </td>
                                </tr>
                                
                                <tr>                                    
                                    <td colspan="3" style="text-align:center"  ><s:submit cssClass="btn btn-primary" value="Cargar Archivo" position="center" action= "UploadSobregiros" onclick="openDialogLoading()"/></td>                                   
                                    
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
                                    <th>Monto</th>
                                    <th>Estatus</th>
                                    <th>Mensaje</th>
                                </tr>
                            </thead>
                            <tbody>                                      
                                <s:iterator value="ajustex" var="myObj">
                                  
                                        <tr>
                                            <td style="width:25%;text-align:center;" colspan="1">                                                
                                                <s:property value ="fecha"/>
                                            </td>    
                                            <td style="width:25%;text-align:center;" colspan="1"><s:property value="tarjeta"/></td>                                            
                                            <td style="width:25%;text-align:center;" colspan="1"><s:property value="monto"/></td>                                            
                                            <td style="width:25%;text-align:center;" colspan="1"><s:property value="status"/></td>
                                            <td style="width:25%;text-align:center;" colspan="1"><s:property value="observacion"/></td>
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
        <script>
            var contador = 1;    
            document.getElementById("boton1").addEventListener("click", function(event){
                event.preventDefault()
                despliegue();
            });
            function despliegue(){                
                document.getElementById("listaCampos"+contador++).style.display="table-row";
                
            }                        
        </script>
    </body>


</html>
