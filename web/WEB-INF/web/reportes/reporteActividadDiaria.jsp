<%-- 
    Document   : reporteActividadDiaria
    Created on : 11/04/2014, 12:42:23 PM
    Author     : jorojas
--%>
<%@taglib uri="/struts-tags" prefix="s" %>
<%@taglib uri="/struts-dojo-tags" prefix="sx" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>

        <title>Actividad Diaria</title>
        <%@include file="../include/head.jsp" %>
    </head>
    <body>
        
            <%@include file="../include/header.jsp" %>
            <div class="container">
                <div class="content" id="content">
                    <br/>
                    <h1>Resumen de Actividad Diaria</h1>
                    
                    <br/>
                    
                    <s:form theme="simple" namespace="/reportes" action="ActividadDiaria">
                        <div class="panel">
                            <table class="table" style="width:100%;">
                                <thead>
                                    <tr><th colspan="3">Propiedades del Resumen</th></tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td style="width: 30%" align="left"> <!-- Input -->

                                            Fecha: <sx:datetimepicker requiredLabel="true" name="fecha" displayFormat="yyyy-MM-dd" />&#160;&#160;&#160;&#160;&#160;
                                            <a href="javascript:showHide('#tablaComedorPe')"><input type="button" value="Comedores" /></a>
                                            <s:submit value="Generar" onclick="preshow();" onload="prehide();" />

                                        </td>
                                        <td  style="width: 40%" align="center"> <!-- Informacion Adicional del Reporte -->

                                            <span class="text-1">BsF/Dolar: </span>
                                            <s:property value="cambioBsDolar" /> &#160;
                                            <span class="text-1">Soles/Dolar: </span>
                                            <s:property value="cambioSolesDolar" /> &#160;
                                            <span class="text-1">Pesos/Dolar: </span>
                                            <s:property value="cambioPesosDolar" />

                                        </td>
                                        <td  style="width: 30%" align="right"> <!-- Botones lado derecho -->
                                            <span id="wait" style="float:left;display:none;"><img src="../recursos/images/ajax_loader.gif" class="icon" /></span>
                                            <s:submit action="enviarCorreoActividadDiaria" cssClass="buttonMail" cssStyle="float:right;" value="" title="Enviar a los Destinatarios" />
                                            <s:submit action="generarExcelActividadDiaria" cssClass="buttonExcel" cssStyle="float:right;" value="" title="Exportar a Excel" />   
                                        </td>
                                    </tr>
                                    <tr id="tablaComedorPe" style="display:none;">
                                        <td align="center">
                                            <span class="text-1">Comedores de Perú</span>
                                        </td>
                                        <td colspan="2">
                                            <span class="text-1">Ingresos: </span>
                                            <s:textfield name="ingresoComedorPe" />
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div> 
                        <br/>                
                    </s:form>
                    
                    <br/>
                    <s:fielderror />
                    
                    <br/>
                    <i style="color: #c90000"><s:property value="message" /></i>
                    <br/>
                    
                    <!-- Todos los Paises -->
                    <table class="table reporteTable" id="tablaReporte" style="width: 100%;">
                        <thead>
                            <tr><th colspan="2" style="background-color: #fff;border:0;">&nbsp;</th><th colspan="2">Emisiones</th><th colspan="5">Recargas</th></tr>
                            <tr><th rowspan="2">País</th><th rowspan="2">Producto</th><th rowspan="2">Día <s:date name="fecha" format="dd/MM/yyyy" /></th><th rowspan="2">Acumulada <s:date name="fecha" format="MMMM yyyy" /></th><th colspan="2">Día <s:date name="fecha" format="dd/MM/yyyy" /></th><th colspan="3">Acumulada <s:date name="fecha" format="MMMM yyyy" /></th></tr>
                            <tr><th>Moneda Local</th><th>US$</th><th>Moneda Local</th><th>US$</th><th>%Rep</th></tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>Venezuela</td> <!-- Pais -->
                                <td>
                                    <s:property value="reporteVe.natural.producto" /><br/>
                                    <s:property value="reporteVe.juridica.producto" />
                                </td> <!-- Producto -->
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reporteVe.natural.emisionesFecha)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reporteVe.juridica.emisionesFecha)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reporteVe.natural.emisionesAcum)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reporteVe.juridica.emisionesAcum)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.natural.recargasDiaLocal)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.juridica.recargasDiaLocal)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.natural.recargasDiaDolares)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.juridica.recargasDiaDolares)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.natural.recargasAcumLocal)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.juridica.recargasAcumLocal)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.natural.recargasAcumDolares)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.juridica.recargasAcumDolares)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.natural.porcRepGlobal)" />%<br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.juridica.porcRepGlobal)" />%
                                </td>
                            </tr>
                            <tr>
                                <td valign="middle">Perú</td> <!-- Pais -->
                                <td>
                                    <s:property value="reportePe.natural.producto" /><br/>
                                    <s:property value="reportePe.juridica.producto" /><br/>
                                    <s:property value="reportePe.juridicaUS.producto" /><br/>
                                    <s:property value="reportePe.comedores.producto" />
                                </td> <!-- Producto -->
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reportePe.natural.emisionesFecha)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reportePe.juridica.emisionesFecha)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reportePe.juridicaUS.emisionesFecha)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reportePe.comedores.emisionesFecha)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reportePe.natural.emisionesAcum)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reportePe.juridica.emisionesAcum)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reportePe.juridicaUS.emisionesAcum)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reportePe.comedores.emisionesAcum)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.natural.recargasDiaLocal)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.juridica.recargasDiaLocal)" /><br/>
                                    -<br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.comedores.recargasDiaLocal)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.natural.recargasDiaDolares)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.juridica.recargasDiaDolares)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.juridicaUS.recargasDiaDolares)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.comedores.recargasDiaDolares)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.natural.recargasAcumLocal)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.juridica.recargasAcumLocal)" /><br/>
                                    -<br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.comedores.recargasAcumLocal)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.natural.recargasAcumDolares)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.juridica.recargasAcumDolares)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.juridicaUS.recargasAcumDolares)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.comedores.recargasAcumDolares)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.natural.porcRepGlobal)" />%<br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.juridica.porcRepGlobal)" />%<br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.juridicaUS.porcRepGlobal)" />%<br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.comedores.porcRepGlobal)" />%
                                </td>
                            </tr>
                            <tr>
                                <td valign="middle">Colombia</td> <!-- Pais -->
                                <td>
                                    <s:property value="reporteCo.juridica.producto" />
                                </td> <!-- Producto -->
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reporteCo.juridica.emisionesFecha)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reporteCo.juridica.emisionesAcum)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteCo.juridica.recargasDiaLocal)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteCo.juridica.recargasDiaDolares)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteCo.juridica.recargasAcumLocal)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteCo.juridica.recargasAcumDolares)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteCo.juridica.porcRepGlobal)" />%
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2" style="text-align: left;">
                                    <b><s:property value="totales.producto" /><b>
                                </td> <!-- Producto: Totales -->
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearEntero(totales.emisionesFecha)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearEntero(totales.emisionesAcum)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearDecimal(totales.recargasDiaLocal)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearDecimal(totales.recargasDiaDolares)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearDecimal(totales.recargasAcumLocal)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearDecimal(totales.recargasAcumDolares)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearDecimal(totales.porcRepGlobal)" />%<b>
                                </td>
                            </tr>
                        </tbody>
                    </table>    
                    
                             
                    <br/><br/>
                    <h3>Venezuela</h3>
                    <br/>
                    <table class="table reporteTable" id="tablaReporteVe" style="width: 100%;">
                        <thead>
                            <tr><th colspan="2" style="background-color: #fff;border:0;">&nbsp;</th><th colspan="2">Emisiones</th><th colspan="5">Recargas</th></tr>
                            <tr><th rowspan="2">País</th><th rowspan="2">Producto</th><th rowspan="2">Día <s:date name="fecha" format="dd/MM/yyyy" /></th><th rowspan="2">Acumulada <s:date name="fecha" format="MMMM yyyy" /></th><th colspan="2">Día <s:date name="fecha" format="dd/MM/yyyy" /></th><th colspan="3">Acumulada <s:date name="fecha" format="MMMM yyyy" /></th></tr>
                            <tr><th>Moneda Local</th><th>US$</th><th>Moneda Local</th><th>US$</th><th>%Rep</th></tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>Venezuela</td> <!-- Pais -->
                                <td>
                                    <s:property value="reporteVe.natural.producto" /><br/>
                                    <s:property value="reporteVe.juridica.producto" />
                                </td> <!-- Producto -->
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reporteVe.natural.emisionesFecha)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reporteVe.juridica.emisionesFecha)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reporteVe.natural.emisionesAcum)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reporteVe.juridica.emisionesAcum)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.natural.recargasDiaLocal)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.juridica.recargasDiaLocal)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.natural.recargasDiaDolares)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.juridica.recargasDiaDolares)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.natural.recargasAcumLocal)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.juridica.recargasAcumLocal)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.natural.recargasAcumDolares)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.juridica.recargasAcumDolares)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.natural.porcRep)" />%<br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.juridica.porcRep)" />%
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2" style="text-align: left;">
                                    <b><s:property value="reporteVe.totales.producto" /><b>
                                </td> <!-- Producto: Totales -->
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearEntero(reporteVe.totales.emisionesFecha)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearEntero(reporteVe.totales.emisionesAcum)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.totales.recargasDiaLocal)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.totales.recargasDiaDolares)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.totales.recargasAcumLocal)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.totales.recargasAcumDolares)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteVe.totales.porcRep)" />%<b>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                                
                    <br/><br/>
                    <h3>Perú</h3>
                    <br/>
                    <table class="table reporteTable" id="tablaReporteVe" style="width: 100%;">
                        <thead>
                            <tr><th colspan="2" style="background-color: #fff;border:0;">&nbsp;</th><th colspan="2">Emisiones</th><th colspan="5">Recargas</th></tr>
                            <tr><th rowspan="2">País</th><th rowspan="2">Producto</th><th rowspan="2">Día <s:date name="fecha" format="dd/MM/yyyy" /></th><th rowspan="2">Acumulada <s:date name="fecha" format="MMMM yyyy" /></th><th colspan="2">Día <s:date name="fecha" format="dd/MM/yyyy" /></th><th colspan="3">Acumulada <s:date name="fecha" format="MMMM yyyy" /></th></tr>
                            <tr><th>Moneda Local</th><th>US$</th><th>Moneda Local</th><th>US$</th><th>%Rep</th></tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>Perú</td> <!-- Pais -->
                                <td>
                                    <s:property value="reportePe.natural.producto" /><br/>
                                    <s:property value="reportePe.juridica.producto" /><br/>
                                    <s:property value="reportePe.juridicaUS.producto" /><br/>
                                    <s:property value="reportePe.comedores.producto" />
                                </td> <!-- Producto -->
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reportePe.natural.emisionesFecha)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reportePe.juridica.emisionesFecha)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reportePe.juridicaUS.emisionesFecha)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reportePe.comedores.emisionesFecha)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reportePe.natural.emisionesAcum)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reportePe.juridica.emisionesAcum)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reportePe.juridicaUS.emisionesAcum)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reportePe.comedores.emisionesAcum)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.natural.recargasDiaLocal)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.juridica.recargasDiaLocal)" /><br/>
                                    -<br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.comedores.recargasDiaLocal)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.natural.recargasDiaDolares)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.juridica.recargasDiaDolares)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.juridicaUS.recargasDiaDolares)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.comedores.recargasDiaDolares)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.natural.recargasAcumLocal)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.juridica.recargasAcumLocal)" /><br/>
                                    -<br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.comedores.recargasAcumLocal)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.natural.recargasAcumDolares)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.juridica.recargasAcumDolares)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.juridicaUS.recargasAcumDolares)" /><br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.comedores.recargasAcumDolares)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.natural.porcRep)" />%<br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.juridica.porcRep)" />%<br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.juridicaUS.porcRep)" />%<br/>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.comedores.porcRep)" />%
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2" style="text-align: left;">
                                    <b><s:property value="reportePe.totales.producto" /><b>
                                </td> <!-- Producto: Totales -->
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearEntero(reportePe.totales.emisionesFecha)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearEntero(reportePe.totales.emisionesAcum)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.totales.recargasDiaLocal)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.totales.recargasDiaDolares)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.totales.recargasAcumLocal)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.totales.recargasAcumDolares)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearDecimal(reportePe.totales.porcRep)" />%<b>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    
                    <br/><br/>
                    <h3>Colombia</h3>
                    <br/>
                    <table class="table reporteTable" id="tablaReporteVe" style="width: 100%;">
                        <thead>
                            <tr><th colspan="2" style="background-color: #fff;border:0;">&nbsp;</th><th colspan="2">Emisiones</th><th colspan="5">Recargas</th></tr>
                            <tr><th rowspan="2">País</th><th rowspan="2">Producto</th><th rowspan="2">Día <s:date name="fecha" format="dd/MM/yyyy" /></th><th rowspan="2">Acumulada <s:date name="fecha" format="MMMM yyyy" /></th><th colspan="2">Día <s:date name="fecha" format="dd/MM/yyyy" /></th><th colspan="3">Acumulada <s:date name="fecha" format="MMMM yyyy" /></th></tr>
                            <tr><th>Moneda Local</th><th>US$</th><th>Moneda Local</th><th>US$</th><th>%Rep</th></tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>Colombia</td> <!-- Pais -->
                                <td>
                                    <s:property value="reporteCo.juridica.producto" />
                                </td> <!-- Producto -->
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reporteCo.juridica.emisionesFecha)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearEntero(reporteCo.juridica.emisionesAcum)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteCo.juridica.recargasDiaLocal)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteCo.juridica.recargasDiaDolares)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteCo.juridica.recargasAcumLocal)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteCo.juridica.recargasAcumDolares)" />
                                </td>
                                <td>
                                    <s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteCo.juridica.porcRep)" />%
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2" style="text-align: left;">
                                    <b><s:property value="reporteCo.totales.producto" /><b>
                                </td> <!-- Producto: Totales -->
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearEntero(reporteCo.totales.emisionesFecha)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearEntero(reporteCo.totales.emisionesAcum)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteCo.totales.recargasDiaLocal)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteCo.totales.recargasDiaDolares)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteCo.totales.recargasAcumLocal)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteCo.totales.recargasAcumDolares)" /><b>
                                </td>
                                <td>
                                    <b><s:property value="@com.novo.util.TextUtil@formatearDecimal(reporteCo.totales.porcRep)" />%<b>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <br/><br/><br/><br/>            
                                
                </div>
            </div>
            <%@include file="../include/footer.jsp" %>
        
    </body>
</html>