<%-- 
    Document   : ajusteTransacciones.jsp
    Created on : 23/02/2015, 07:51:04 PM
    Author     : ggutierrez
--%>

<%@taglib uri="/struts-tags" prefix="s" %>
<%@taglib uri="/struts-dojo-tags" prefix="sx" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Ajuste de Transacciones</title>
        <%@include file="../include/head.jsp" %>
        <link rel="stylesheet" type="text/css" href="../recursos/css/bootstrap.css"/>
    </head>
    <body>        
        <%@include file="../include/header.jsp" %>
        <div class="container">
            <div class="content" id="content">
                <br/>
                <h1>Ajuste de Transacciones</h1>
                <br/>
                <s:if test="%{!message.equals(\"\")}">
                    <div class="alert alert-info">
                        <s:property value = "%{message}" />
                    </div><span class="glyphicon glyphicon-ok form-control-feedback" aria-hidden="true"></span>
                </s:if>
                <s:form theme="simple" namespace="/operaciones" action="AjusteTransacciones" >
                    
                    <div class="panel" style="width: 770px;margin: 0 auto;">
                        <table class="table" style="width: 100%;">
                            <thead>
                                <tr class="info"><th colspan="4"  style="text-align: center;">Busqueda de Usuario</th></tr>                                
                                
                            </thead>
                            <tbody>
                                <tr>
                                    <td  style="width:50%;text-align:center;" ><span class="text-1">Documento de Identidad</span></td>
                                    <td><s:textfield cssClass="search-query" name="documentoIdentidad" /></td>                               
                                    <td style="width:50%;text-align:center;"><span class="text-1">Tarjeta</span></td>
                                    <td><s:textfield cssClass="search-query" name="nroTarjeta" /></td>
                                </tr>
                                <tr>                                    
                                    <td style="width:50%;text-align:center;"><span class="text-1">Productos</span></td>
                                    <td  style=""><s:select id="listaProductos" name ="selectedProducto" list = "listaProductos" listKey = "prefix" listValue = "descripcion" cssClass="search-query" headerValue ="Seleccione Producto" headerKey=""/></td>
                                    
                                    <td style="width:50%;text-align:center;"><span class="text-1">Empresas</span></td>
                                    <td  style=""><s:select id="listaEmpresas" name ="selectedEmpresa" list = "listaEmpresas" listKey = "rif" listValue = "nombreCorto" cssClass="search-query" headerValue ="Seleccione Empresa" headerKey=""/></td>
                                </tr>
                                <tr>
                                        <td colspan="4" style="text-align:center;"><s:submit cssClass="btn btn-primary" value="Buscar" action="buscarUsuarioAjusteTransacciones" /></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    <s:if test="%{getTarjetas().size()>0}">
                        <div class="panel" style="width: 900px;margin: 0 auto;">
                            <table class="table table-bordered table-striped table-hover table-condensed" style="width: 100%;">
                                <thead>
                                    <tr><th colspan="5" style="text-align: center;">Tarjetas</th></tr>
                                    <tr>
                                        <th>Tarjeta</th>
                                        <th>DNI</th>
                                        <th>Producto</th>
                                        <th>Nombre</th>
                                        <th>Empresa</th>
                                    </tr>
                                </thead>
                                <tbody>                                      
                                    <s:iterator value="tarjetas" var="myObj">
                                        <tr>
                                            <td style="width:15%;text-align:center;" colspan="1">
                                                <s:a action="getTransaccionesTarjetaAjusteTransacciones" includeParams="get">
                                                    <s:property value="mascara"/>
                                                    <s:param name="tarjetaSelected"><s:property value ="nroTarjeta"/></s:param>
                                                </s:a>
                                            </td>    
                                            <td style="width:15%;text-align:center;" colspan="1"><s:property value="idExtPer"/></td>                                            
                                            <td style="width:15%;text-align:center;" colspan="1"><s:property value="cardProgram"/></td>                                            
                                            <td style="width:15%;text-align:center;" colspan="1"><s:property value="nombreCliente"/></td>
                                            <td style="width:40%;text-align:center;" colspan="1"><s:property value="nombreEmpresa"/></td>    
                                        </tr>
                                    </s:iterator>
                                </tbody>
                            </table>
                        </div>
                    </s:if>            
                </s:form> 
                
            </div>       
        </div>
        <%@include file="../include/footer.jsp" %>
        
    </body>
</html>
