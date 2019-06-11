<%-- 
    Document   : actualizaciones
    Created on : 05/08/2015, 03:05:38 PM
    Author     : ggutierrez
--%>

<%@taglib uri="/struts-tags" prefix="s" %>
<%@taglib uri="/struts-dojo-tags" prefix="sx" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

        <title>Actualizaciones</title>
        <%@include file="../include/head_1.jsp" %>
    
        <body>
            <header>
                <%@include file="../include/header.jsp" %>
            </header>
            <jsp:include page="../include/LoadingJquery.jsp"/>
            <div class="container">
            <div class="content" id="content">
                <h1 style="margin: 0 0 0 0;">Actualizaciones</h1>
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
                </s:elseif >
                <s:form theme="simple" namespace="/operaciones" action="Actualizaciones">
                    <div class="panel" style="width: 900px;margin: 0 auto;">
                        <table class="table" style="width: 100%;">
                            <thead>
                                <tr><th colspan="4" style="text-align:center;">Búsqueda de Usuario</th></tr>                                        

                            </thead>
                            <tbody>
                                <tr>
                                    <td  style="width:50%;text-align:center;" ><span class="text-1">Número de Tarjeta</span></td>
                                    <td><s:textfield cssClass="search-query" name="nroTarjeta" /></td>       
                                </tr>
                                <tr>
                                    <td colspan="4" align="center"  style="text-align:center;"><s:submit cssClass="btn btn-primary" value="Buscar" action="buscarUsuarioActualizaciones" onclick="openDialogLoading()"/></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </s:form>
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
                                    <s:form theme="simple" namespace="/operaciones" action="Actualizaciones">
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
                    <s:form action="Actualizaciones" method="post" enctype="multipart/form-data" theme = "simple">
                        <table class="table" style="width: 100%;">
                            <thead>
                                <tr><th colspan="3" style="text-align:center;">Actualización Masiva</th></tr>
                            </thead>
                            <tbody>                                
                                <tr>
                                    <td><label>Cargar Archivo :</label></td>
                                    <td><s:file cssClass="btn btn-info" name="upload" label="File"/></td>  

                                </tr>
                                <tr>                                    
                                    <td colspan="3" style="text-align:center"  ><s:submit cssClass="btn btn-primary" value="Cargar Archivo" position="center" action= "UploadActualizaciones" onclick="openDialogLoading()"/></td>                                   
                                </tr>

                            </tbody>      
                        </table>
                        <s:if test="%{getTarjetasAct().size()>0}">        
                            <table class="table" style="width: 100%;">
                                <thead>
                                    <tr><th colspan="3" style="text-align:center;">Campos</th></tr>
                                    <td><button  id ="boton1" >Añadir Campos</button></td>
                                </thead>
                                <tbody>
                                    <tr id="listaCampos0" ><td ><s:select  name="selectedCampo" headerValue="-Seleccionar Campo-" headerKey="" list = "campos" listKey = "idCampo"  listValue="campo" /></td><td colspan="2"><s:textfield  name="selectedCampoValue" value = ""/></td></tr>
                                    <tr id="listaCampos1" style="display:none;"><td ><s:select  name="selectedCampo" headerValue="-Seleccionar Campo-" headerKey="" list = "campos" listKey = "idCampo"  listValue="campo" /></td><td colspan="2"><s:textfield  name="selectedCampoValue" /></td></tr>
                                    <tr id="listaCampos2" style="display:none;"><td ><s:select  name="selectedCampo" headerValue="-Seleccionar Campo-" headerKey="" list = "campos" listKey = "idCampo"  listValue="campo" /></td><td colspan="2"><s:textfield  name="selectedCampoValue" /></td></tr>
                                    <tr id="listaCampos3" style="display:none;"><td ><s:select  name="selectedCampo" headerValue="-Seleccionar Campo-" headerKey="" list = "campos" listKey = "idCampo"  listValue="campo" /></td><td colspan="2"><s:textfield  name="selectedCampoValue" /></td></tr>
                                    <tr id="listaCampos4" style="display:none;"><td ><s:select  name="selectedCampo" headerValue="-Seleccionar Campo-" headerKey="" list = "campos" listKey = "idCampo"  listValue="campo" /></td><td colspan="2"><s:textfield  name="selectedCampoValue" /></td></tr>
                                    <tr id="listaCampos5" style="display:none;"><td ><s:select  name="selectedCampo" headerValue="-Seleccionar Campo-" headerKey="" list = "campos" listKey = "idCampo"  listValue="campo" /></td><td colspan="2"><s:textfield  name="selectedCampoValue" /></td></tr>
                                    <tr id="listaCampos6" style="display:none;"><td ><s:select  name="selectedCampo" headerValue="-Seleccionar Campo-" headerKey="" list = "campos" listKey = "idCampo"  listValue="campo" /></td><td colspan="2"><s:textfield  name="selectedCampoValue" /></td></tr>
                                    <tr id="listaCampos7" style="display:none;"><td ><s:select  name="selectedCampo" headerValue="-Seleccionar Campo-" headerKey="" list = "campos" listKey = "idCampo"  listValue="campo" /></td><td colspan="2"><s:textfield  name="selectedCampoValue" /></td></tr>                               
                                    <tr id="listaCampos8" style="display:none;"><td ><s:select  name="selectedCampo" headerValue="-Seleccionar Campo-" headerKey="" list = "campos" listKey = "idCampo"  listValue="campo" /></td><td colspan="2"><s:textfield  name="selectedCampoValue" /></td></tr>
                                    <tr id="listaCampos9" style="display:none;"><td ><s:select  name="selectedCampo" headerValue="-Seleccionar Campo-" headerKey="" list = "campos" listKey = "idCampo"  listValue="campo" /></td><td colspan="2"><s:textfield  name="selectedCampoValue" /></td></tr>
                                    <tr id="listaCampos10" style="display:none;"><td ><s:select  name="selectedCampo" headerValue="-Seleccionar Campo-" headerKey="" list = "campos" listKey = "idCampo"  listValue="campo" /></td><td colspan="2"><s:textfield  name="selectedCampoValue" /></td></tr>
                                    <tr id="listaCampos11" style="display:none;"><td ><s:select  name="selectedCampo" headerValue="-Seleccionar Campo-" headerKey="" list = "campos" listKey = "idCampo"  listValue="campo" /></td><td colspan="2"><s:textfield  name="selectedCampoValue" /></td></tr>
                                    <tr id="listaCampos12" style="display:none;"><td ><s:select  name="selectedCampo" headerValue="-Seleccionar Campo-" headerKey="" list = "campos" listKey = "idCampo"  listValue="campo" /></td><td colspan="2"><s:textfield  name="selectedCampoValue" /></td></tr>                               
                                    <tr><td colspan="3" style="text-align:center"  ><s:submit cssClass="btn btn-primary" value="Actualizar" position="center" action="actualizarActualizaciones"/></td></tr>
                                </tbody>
                            </table>
                        </s:if> 
                    </s:form>            
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
