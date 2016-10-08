<%-- 
    Document   : modificarTransaccion
    Created on : 27/02/2015, 02:06:38 PM
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
                    </div>
                    </s:if>
                <s:form theme="simple" namespace="/operaciones" action="AjusteTransacciones">                   
                    <s:if test="%{getTransacciones().size()>0}">
                        <div class="panel" style="width: 1000px;margin: 0 auto;">
                            <table class="table table-bordered table-striped table-hover table-condensed" style="width: 170%;">
                                <thead>
                                    <tr><th colspan="7" style="text-align:center;">Transacciones</th></tr>
                                    <tr>
                                        <th>Tarjeta</th>
                                        <th>Referencia</th>
                                        <th>Monto</th>
                                        <th>Fecha Transacción</th>
                                        <th>MontoAjuste</th>
                                        <th>Descripcion</th>
                                        <th>tipo ajustes</th>
                                        <th>Confirmar Ajuste</th>
                                    </tr>
                                </thead>
                                <tbody>                                      
                                    <s:iterator value="transacciones" >
                                        <s:form namespace="/operaciones" action="AjusteTransacciones">
                                            <tr> 
                                                <s:hidden value="%{nroTarjeta}" name="nroTarjeta" />
                                                <s:hidden value="%{systrace}" name="systrace" />
                                                <s:hidden value="registrarAjuste" name="opcion" />
                                                <td style="width:16%;text-align:center;" colspan="1"><s:property value="mascara"/></td>                                            
                                                <td style="width:12%;text-align:center;" colspan="1"><s:property value="systrace"/></td> 
                                                <td style="width:10%;text-align:center;" colspan="1"><s:property value="monto"/></td>
                                                <td style="width:30%;text-align:center;" colspan="1"><s:property value="fechaTransaccion"/></td>
                                                <td style="width:5%;text-align:center;" ><s:textfield cssClass="input-mini search-query" name="montoAjuste" size="10" id="monto"/></td>
                                                <td style="width:18%;text-align:center;" colspan="1"><s:property value="descripcion"/></td>                                            
                                                <td style="width:16%;text-align:center;" colspan="1"><s:select  id="listaTipoAjustes" name="selectedAjuste" headerValue="Tipo de Ajuste" list = "tipoAjustes" listKey = "idCodigoAjuste"  listValue="descripcion" /></td>  
                                                <td style="width:5%;text-align:center;" colspan="1">                                                                                                        
                                                <s:submit value="Registrar Ajuste" cssClass="btn btn-primary" action="registrarAjusteTransacciones" /> 
                                                </td>
                                            </tr>
                                        </s:form>
                                    </s:iterator>
                                </tbody>
                            </table>
                        </div>
                    </s:if>            
                </s:form> 
                
            </div>       
        </div>
        <%@include file="../include/footer.jsp" %>
        <script>
            //Para confirmar antes de accionar, colocar en el elemento class="confirmation"
            function  validarMonto(){
                var str=document.getElementById("monto").value
                var patt = new RegExp("[^0-9]");
			var res = patt.test(str);
                if(res){
                   alert("mal monto"); 
                }else{
                    confirm();
                }        
            }
        </script>
    </body>
</html>
