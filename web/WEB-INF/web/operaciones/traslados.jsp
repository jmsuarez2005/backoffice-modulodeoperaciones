<%-- 
    Document   : traslados
    Created on : 14/07/2015, 09:18:42 AM
    Author     : ggutierrez
--%>


<%@taglib uri="/struts-tags" prefix="s" %>
<%@taglib uri="/struts-dojo-tags" prefix="sx" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
        <title>Traslado de Saldos</title>
        
        <%@include file="../include/head_1.jsp" %>
    
        <body>
            <header>
                <%@include file="../include/header.jsp" %>
            </header>
            <jsp:include page="../include/LoadingJquery.jsp"/>
            <div class="container">
            <div class="content" id="content">
                <h1 style="margin: 0 0 0 0;">Traslado de Saldos</h1>
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

                <s:form theme="simple" namespace="/operaciones" action="Traslados">       
                    <div class="panel" style="width: 1000px;margin: 0 auto;">
                        <table class="table" style="width: 100%; padding: 0px; margin: 0px; ">
                            <thead>
                                <tr><th colspan="4" style="text-align:center;">Búsqueda</th></tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td  style="width:50%;text-align:center;" ><span class="text-1">Documento de Identidad</span></td>
                                    <td><s:textfield cssClass="search-query" name="documentoIdentidad" /></td>   
                                    <td style="width:50%;text-align:center;"><span class="text-1">Productos</span></td>
                                    <td  style=""><s:select id="listaProductos" name ="selectedProducto" list = "listaProductos" listKey = "prefix" listValue = "descripcion" cssClass="search-query" headerValue ="Seleccione Producto" headerKey=""/></td>
                                </tr>
                                <tr>
                                    <td colspan="4" style="text-align:center;"><s:submit cssClass="btn btn-primary" value="Buscar" action="buscarUsuarioTraslados" onclick="openDialogLoading()"/></td>
                                </tr>
                            </tbody>   
                        </table>
                    </div>
                </s:form>

                <s:if test="%{getTarjetas().size()>0}">
                    <s:form theme="simple" namespace="/operaciones" action="Traslados">
                        <div class="panel" style="width: 1000px;margin: 0 auto;">
                            <table class="table" style="width: 100%; padding: 0px; margin: 0px; ">
                                <thead>
                                    <tr><th colspan="9" style="text-align: center;">Tarjetas</th></tr>
                                    <tr>
                                        <th>Tarjeta</th>
                                        <th>DNI</th>
                                        <th>Producto</th>
                                        <th>Nombre</th>
                                        <th colspan="3">Empresa</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <s:iterator value="tarjetas" var="myObj">
                                        <s:form theme="simple" namespace="/operaciones" action="Traslados">
                                        <tr>


                                            <td style="width:15%;text-align:center;" colspan="1">                                               
                                                <s:property value="mascara"/>                                                                                                  
                                            </td>    
                                            <td style="width:15%;text-align:center;" colspan="1"><s:property value="idExtPer"/></td>                                            
                                            <td style="width:15%;text-align:center;" colspan="1"><s:property value="cardProgram"/></td>                                            
                                            <td style="width:20%;text-align:center;" colspan="1"><s:property value="nombreCliente"/></td>
                                            <td style="width:30%;text-align:center;" colspan="1"><s:property value="nombreEmpresa"/></td>                                        
                                            <s:hidden value="%{documentoIdentidad}" name="documentoIdentidad" />
                                            <s:hidden value="%{selectedProducto}" name="selectedProducto" />
                                            <s:hidden value="%{nroTarjeta}" name="selectedTarjeta" />
                                            <s:if test="%{getTarjetaOrigen()!=null}">                                                
                                                 <s:hidden value="%{getTarjetaOrigen().getNroTarjeta()}" name="selectedTarjetaOrigen" />
                                            </s:if>
                                            <s:if test="%{getTarjetaDestino()!=null}">                                                
                                                 <s:hidden value="%{getTarjetaDestino().getNroTarjeta()}" name="selectedTarjetaDestino" />
                                            </s:if>
                                            <s:if test = "%{tarjetas.size() < 2}">
                                            <td><s:submit title="Necesita mas de una tarjeta para realizar el traslado" disabled="true" value="Origen" id="submit-%{nroTarjeta}0" action="fillTarjetaOrigenTraslados"/></td>                                                                                                                         
                                            <td><s:submit title="Necesita mas de una tarjeta para realizar el traslado" disabled="true" value="Destino" id="submit-%{nroTarjeta}1" action="fillTarjetaDestinoTraslados"/></td>
                                            
                                            </s:if>
                                            <s:else>
                                                <td><s:submit  value="Origen" id="submit-%{nroTarjeta}0" action="fillTarjetaOrigenTraslados"/></td>                                                                                                                         
                                            <td><s:submit value="Destino" id="submit-%{nroTarjeta}1" action="fillTarjetaDestinoTraslados"/></td> 
                                                </s:else>
                                        </tr>
                                </s:form>
                                    </s:iterator>
                                </tbody>
                            </table>
                        </div>
                        <div class="panel" style="width: 1000px;margin: 0 auto;">
                            <table class="table" style="width: 100%; padding: 0px; margin: 0px; ">
                                <thead>
                                    <tr><th colspan="8" style="text-align: center;">Traslado</th></tr>
                                </thead>
                                <tbody>                                
                                    <tr>

                                        <s:if test="%{getTarjetaOrigen()!=null}" >
                                            <td>Origen: </td>
                                            <td>
                                                <s:property value="%{getTarjetaOrigen().getMascara()}"/>   
                                            </td>
                                            <td>Saldo: </td>
                                            <td><s:property value="%{getTarjetaOrigen().getSaldoDisponible()}"/></td>
                                        </s:if>

                                        <s:if test="%{getTarjetaDestino()!=null}" >
                                            <td>Destino: </td>
                                            <td>
                                                <s:property value="%{getTarjetaDestino().getMascara()}"/>   
                                            </td>
                                            
                                            <td>Saldo: </td>
                                            <td><s:property value="%{getTarjetaDestino().getSaldoDisponible()}"/></td>
                                        </s:if>
                                    </tr>                                
                                    <tr>
                                        <td colspan="8" style="text-align: right;">
                                            <s:submit value="Borrar" id = "submit" action="ereaseTarjetaTrasTraslados"/>
                                            <s:if test = "%{!getTarjetaOrigen().getSaldoDisponible().equals(\"---\") && !getTarjetaDestino().getSaldoDisponible().equals(\"---\")}">
                                                <s:submit value="Trasladar"  id = "submit" action="procesarTraslados" />
                                            </s:if>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>                    
                    </s:form>
                </s:if>
            </div>            
        </div>
        <footer>
            <%@include file="../include/footer.jsp" %>    
        </footer> 
    </body>
</html>
