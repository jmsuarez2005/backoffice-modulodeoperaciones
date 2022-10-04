<%-- 
    Document   : ajusteTarjeta
    Created on : 17/03/2015, 02:48:40 PM
    Author     : ggutierrez
--%>

<%@taglib uri="/struts-tags" prefix="s" %>
<%@taglib uri="/struts-dojo-tags" prefix="sx" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page buffer = "16kb" %>
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
                <h1 style="margin: 0 0 0 0;">Ajustes Masivos</h1>
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
                <s:form theme="simple" namespace="/operaciones" action="AjusteTransacciones">
                    
                  
                    <div class="panel" style="width: 900px;margin: 0 auto;">
                        <table class="table" style="width: 1076px;">
                            <thead>
                                <tr><th colspan="4" style="text-align:center;">Búsqueda de Usuario</th></tr>                                       
                     
                            </thead>
                            <tbody>
                                <tr>
                                    <td  style="width:50%;text-align:center;" ><span class="text-1">Documento de Identidad</span></td>
                                    <td><s:textfield cssClass="search-query" name="documentoIdentidad" /></td>                               
                                    <td style="width:50%;text-align:center;"><span class="text-1">Tarjeta</span></td>
                                    <td><s:textfield cssClass="search-query" name="nroTarjeta" /></td>
                                    <s:hidden value="buscarUsuario2" name ="opcion" />
                                </tr>
                                <tr>
                                        <td colspan="4" align="center"  style="text-align:center;"><s:submit cssClass="btn btn-primary" value="Buscar" action="buscarUsuarioAjusteTransacciones" onclick="openDialogLoading()" /></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                 </s:form>
                  <s:if test="%{getTarjetas().size()>0}">
                        <div class="panel" style="width: 900px;margin: 0 auto;">
                            <table class="table" style="width: 1076px;">
                                <thead>
                                    <tr><th colspan="9" style="text-align:center;">Tarjetas</th></tr>
                                    <tr>
                                        <th>Tarjeta</th>
                                        <th>DNI</th>
                                        <th>Producto</th>
                                        <th>Nombre</th>
                                        <th>Monto Ajuste</th>
                                        <!--<th>Fecha Ajuste</th> -->
                                        <th>Tipo Ajuste</th>
                                        <th>Observación</th>
                                        <th>Realizar Ajuste</th>
                                    </tr>
                                </thead>
                                <tbody> 
                                    
                                    <s:iterator value="tarjetas" var="myObj">
                                        <s:form theme="simple" namespace="/operaciones" action="AjusteTransacciones">
                                        <tr><s:hidden value="%{nroTarjeta}" name="nroTarjeta" />
                                            <s:hidden value="registrarAjuste" name="opcion" />
                                            <td style="width:25%;text-align:center;" colspan="1"><s:property value ="mascara"/></td>    
                                            <td style="width:25%;text-align:center;" colspan="1"><s:property value="idExtPer"/></td>                                            
                                            <td style="width:25%;text-align:center;" colspan="1"><s:property value="cardProgram"/></td>                                            
                                            <td style="width:25%;text-align:center;" colspan="1"><s:property value="nombreCliente"/></td>                                            
                                            <td><s:textfield cssClass="input-mini search-query" name="montoAjuste" id="monto"/></td>
                                            <!-- <td style="padding-right: 25px;" colspan="1"><sx:datetimepicker name="fechaIni" label="Format (dd-MMM-yyyy)" displayFormat="dd-MMM-yyyy" value="%{'today'}"/></td> -->
                                            <td><s:select id="listaTipoAjustes" name="selectedAjuste" headerValue="Tipo de Ajuste" list = "tipoAjustes" listKey = "idCodigoAjuste"  listValue="descripcion" /></td>
                                            <td><s:textfield cssClass="search-query" name="observacion" id="observacion"/></td>
                                            <td><s:submit cssClass="btn btn-primary" value="Realizar Ajuste" action="registrarAjusteAjusteTransacciones" onclick="openDialogLoading()"/></td>                                        
                                        </tr>
                                        </s:form>                                            
                                    </s:iterator>
                                </tbody>
                            </table>
                        </div>
                    </s:if>  
               
                <div class="panel" style="width: 900px;margin: 0 auto;"> 
                    <s:form action="UploadAjusteTransacciones" method="post" enctype="multipart/form-data" theme = "simple">
                        <table class="table" style="width: 1076px;">
                            <thead>
                                <tr><th colspan="7" style="text-align:center;">Ajuste Masivo</th></tr>
                            </thead>
                            <tbody>                                
                                <tr>
                                    <td><label>Cargar Archivo :</label></td>
                                    <td><s:file cssClass="btn btn-info" name="upload" label="File"/></td>
                                    <td><label>Tipo de Ajuste :</label></td>
                                    <td> <s:select id="listaTipoAjustes" name="selectedAjuste" headerValue="Tipo de Ajuste" list = "tipoAjustes" listKey = "idCodigoAjuste"  listValue="descripcion" label="Tipo de Ajuste"/></td>                                    
                                </tr>                                                                                                                              
                                <tr>
                                     <td><label>Observación :</label></td>
                                     <td><s:textfield cssClass="search-query" name="observacion" id="observacion"/></td>
                                     <td colspan="2" style="text-align:center"  ><s:submit cssClass="btn btn-primary" value="Enviar Archivo" position="center" onclick="openDialogLoading()"/></td>
                                </tr>
                            </tbody>      
                        </table>                       
                    </s:form>            
                </div>     
            </div>       
        </div>
          <footer>
            <%@include file="../include/footer.jsp" %>    
        </footer> 
        <script>
           
            function  validarMonto(){
                var str=document.getElementById("monto").value
                var patt = new RegExp("[^0-9]");
			var res = patt.test(str);
                if(res){
                   alert("Monto Invalido"); 
                   
                }
                
            }
            
            
            
        </script>
    </body>
</html>

