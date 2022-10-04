<%-- 
    Document   : consultaRen
    Created on : 01/08/2016, 04:17:48 PM
    Author     : rgomez
--%>
<%@taglib uri="/struts-tags" prefix="s" %>
<%@taglib uri="/struts-dojo-tags" prefix="sx" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Ajuste de Transacciones</title>
        <%@include file="../include/head_1.jsp" %>
    
        <body>
            <header>
                <%@include file="../include/header.jsp" %>
            </header>
            <jsp:include page="../include/LoadingJquery.jsp"/>
            <div class="container">
            <div class="content" id="content">
                <h1 style="margin: 0 0 0 0;">Consultar Lotes de Renovación</h1>
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
                <s:form theme="simple" namespace="/operaciones" action="buscarrenovacion">       
                    <div class="panel" style="width: 1000px;margin: 0 auto;">
                        <table class="table" style="width: 100%; padding: 0px; margin: 0px; ">
                            <thead>
                                <tr><th colspan="6" style="text-align:center;">Búsqueda</th></tr>
                            </thead>
                            <tbody>
                                <tr id="picker1" style=" padding: 0px; margin: 0px">
                                    <td style="padding: 0px; margin: 0px; text-align: center" colspan="3"><table class=""  style="width: 100%;padding: 0px; margin: 0px;"><tbody><sx:datetimepicker cssClass="search-query" name="fechaIni" label="Fecha Inicio" displayFormat="dd-MMM-yyyy" value="%{today}"/></tbody></table></td>                                                                
                                    <td style="padding: 0px; margin: 0px; text-align: center" colspan="3"><table class=""  style="width: 100%;padding: 0px; margin: 0px;"><tbody><sx:datetimepicker cssClass="search-query" name="fechaFin" label="Fecha Fin" displayFormat="dd-MMM-yyyy" value="%{today}"/></tbody></table></td>
                                </tr>
                                <tr>
                                    <td  style="width: 18.8%;">Empresa</td>
                                    <td  style=""><s:select id="listaEmpresas" name ="selectedEmpresa" list = "listaEmpresas" listKey = "rif" listValue = "nombreCorto" cssClass="search-query" headerValue ="Seleccione Empresa" headerKey=""/></td>
                                    <td  style="width: 16.4%;">Producto</td>
                                    <td  style=""><s:select id="listaProductos" name ="selectedProducto" list = "listaProductos" listKey = "prefix" listValue = "descripcion" cssClass="search-query" headerValue ="Seleccione Producto" headerKey=""/></td>
                                    <td  style="width:16.4%;text-align:center;" ><span class="text-1">Documento de Identidad</span></td>
                                    <td><s:textfield cssClass="search-query" name="documentoIdentidad" /></td>

                                </tr>
                                <tr>                                                                  
                                    <td colspan="6" style="text-align:center;"><s:submit cssClass="btn btn-primary" value= "Buscar" action="buscarrenovacion" onclick="openDialogLoading()"/></td> 
                                </tr>  
                            </tbody>                            
                        </table>
                        <br>
                        <table class="table" style="width: 100%;">
                            <thead>
                                <tr><th colspan="10" style="text-align:center;">Renovación</th></tr>
                                <tr>
                                    <th style="text-align:center;">DNI</th>
                                    <th style="text-align:center;">Nro de Cuenta</th>  
                                    <th style="text-align:center;">Nombre</th>
                                    <th style="text-align:center;">Cliente</th>

                                </tr>
                            </thead>
                            <tbody>                                      
                                <s:iterator value="renovar" var="myObj">

                                    <tr>
                                        <td style="width:25%;text-align:center;" colspan="1"><s:property value ="id_ext_per"/></td>    
                                        <td style="width:25%;text-align:center;" colspan="1"><s:property value="nro_cuenta"/></td>                                            
                                        <td style="width:25%;text-align:center;" colspan="1"><s:property value="nombre"/></td>
                                        <td style="width:25%;text-align:center;" colspan="1"><s:property value="nom_cliente"/></td>
                                    </tr>

                                </s:iterator>
                            </tbody>
                        </table>
                    </div>
                    <div class="panel" style="width: 900px;margin: 0 auto;">
                    </div>
                </div>
            </div>
        </s:form>
        <footer>
            <%@include file="../include/footer.jsp" %>    
        </footer>
    </body>
</html>
