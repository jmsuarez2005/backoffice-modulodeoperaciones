<%-- 
    Document   : ajusteTransacciones.jsp
    Created on : 23/02/2015, 07:51:04 PM
    Author     : ggutierrez
--%>

<%@taglib uri="/struts-tags" prefix="s" %>
<%@taglib uri="/struts-dojo-tags" prefix="sx" %>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page buffer = "16kb" %>
<%@ taglib prefix="sj" uri="/struts-jquery-tags"%>  
<!DOCTYPE html>
<html>
    <title>Ajuste de Transacciones</title>
    
    <%@include file="../include/head_1.jsp" %>
    
        <body>
            <header>
                <%@include file="../include/header.jsp" %>
            </header>
            <jsp:include page="../include/LoadingJquery.jsp"/>
        <div class="container">
            <div class="content" id="content">
                <h1 style="margin: 0 0 0 0;">Ajuste de Transacciones</h1>
                <br/>
                <s:if test="%{tipoMessage.equals(\"error\")}">
                    <div class="alert alert-error">
                        <s:property value = "%{message}" /> 
                    </div><span class="glyphicon glyphicon-ok form-control-feedback" aria-hidden="true"></span>
                </s:if>
                <s:elseif  test="%{!message.equals(\"\")}">
                    <div class="alert alert-info">
                        <s:property value = "%{message}" />
                    </div><span class="glyphicon glyphicon-ok form-control-feedback" aria-hidden="true"></span>
                </s:elseif >

                <s:form theme="simple" namespace="/operaciones" action="AjusteTransacciones" >

                    <div class="panel" style="width: 770px;margin: 0 auto;">
                        <table class="table" style="width: 100%;">
                            <thead>
                                <tr class="info"><th colspan="4"  style="text-align: center;">Búsqueda de Usuario</th></tr>                                

                            </thead>
                            <tbody>
                                <tr>
                                    <td style="padding: 0px; margin: 0px; text-align: center" colspan="2"><table style="width: 100%;padding: 0px; margin: 0px;"><sx:datetimepicker cssClass="search-query" id="fechaIni" name="fechaIni" label="Fecha Inicio" displayFormat="dd-MMM-yyyy" value="%{today}"/></table>
                                    <td style="padding: 0px; margin: 0px; text-align: center" colspan="2"><table style="width: 100%;padding: 0px; margin: 0px;"><sx:datetimepicker cssClass="search-query" id="fechaFin" name="fechaFin" label="Fecha Fin" displayFormat="dd-MMM-yyyy" value="%{today}"/></table></td>
                                </tr>
                                <tr>
                                    <td  style="width:50%;text-align:center;" ><span class="text-1">Documento de Identidad</span></td>
                                    <td><s:textfield cssClass="search-query" name="documentoIdentidad" /></td>                               
                                    <td style="width:50%;text-align:center;"><span class="text-1">Tarjeta</span></td>
                                    <td><s:textfield cssClass="search-query" name="nroTarjeta" /></td>
                                </tr>
                                <tr>                                    
                                    <td style="width:50%;text-align:center;"><span class="text-1">Productos</span></td>
                                    <td  style=""><s:select id="listaProductos" name ="selectedProducto" list = "listaProductos" listKey = "prefix" listValue = "descripcion" cssClass="search-query" headerValue ="Seleccione Producto" headerKey=""/></td>

                                    <td style="width:50%;text-align:center;"><span class="text-1">Empresa</span></td>
                                    <!-- <td  style=""><s:select id="listaEmpresas" name ="selectedEmpresa" list = "listaEmpresas" listKey = "rif" listValue = "nombreCorto" cssClass="search-query" headerValue ="Seleccione Empresa" headerKey=""/></td> -->
                                    <td><s:textfield cssClass="search-query" name="selectedEmpresa" placeholder="Nombre" /></td>
                                </tr>
                                <tr>
                                    <s:hidden value="buscarProductosEmpresas" name ="opcion" />
                                    <td colspan="4" style="text-align:center;"><s:submit cssClass="btn btn-primary" value="Buscar" action="buscarUsuarioAjusteTransacciones" onclick="openDialogLoading()"/></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    <s:if test="%{getTarjetas().size()>0}">
                        <div class="panel" style="width: 900px;margin: 0 auto;">
                            <table class="table-bordered table-striped table-hover table-condensed" style="width: 100%;">

                                <thead>
                                    <tr><th colspan="5" style="text-align: center;">Tarjetas</th></tr>
                                    <!--
                                        <tr>
                                            <th>Tarjeta</th>
                                            <th>DNI</th>
                                            <th>Producto</th>
                                            <th>Nombre</th>
                                            <th>Empresa</th>
                                        </tr>
                                    -->

                                </thead>
                                <tbody>


                                    <display:table export="false" class="table-bordered table-striped table-hover table-condensed"  style="width: 100%;text-align:center;" 
                                                   id="tarjetasTable" name="tarjetas" pagesize="15" requestURI="" >

                                        <tr>
                                            <td>
                                                <display:column property="mascara" title="Tarjeta" style="width:15%;text-align:center;" 
                                                                href="getTransaccionesTarjetaAjusteTransacciones.do?tarjetaSelected=${tarjetasTable.nroTarjeta}" >
                                                    <a href="getTransaccionesTarjetaAjusteTransacciones.do?tarjetaSelected=${tarjetasTable.nroTarjeta}">${tarjetasTable.mascara}</a> 

                                                </display:column>
                                            </td>      
                                            <td><display:column property="idExtPer" title="DNI"  style="width:15%;text-align:center;" /></td>                                        
                                            <td><display:column property="cardProgram" title="Producto" style="width:15%;text-align:center;" /></td>                                            
                                            <td><display:column property="nombreCliente" title="Nombre" style="width:15%;text-align:center;" /></td>
                                            <td><display:column property="nombreEmpresa" title="Empresa" style="width:15%;text-align:center;" /></td>
                                        </tr>

                                        <display:setProperty name="export.excel" value="false"/>
                                        <display:setProperty name="export.pdf" value="false"/>
                                        <display:setProperty name="export.excel.filename" value="Resultados.xls"/>

                                        <display:setProperty name="paging.banner.placement" value="bottom"/>

                                    </display:table>  

                                    <s:submit action="generarExcelAjusteTransacciones" type="image"  cssStyle="float:center;" src="../recursos/icons/MSExcel.png"  title="Exportar a Excel"/> 



                                </tbody>


                                <!-- METODO SIN PAGINACION
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
                                -->



                            </table>


                        </div>




                    </s:if>            
                </s:form> 

            </div>       
        </div>
        <footer>
            <%@include file="../include/footer.jsp" %>    
        </footer> 

    </body>
</html>
