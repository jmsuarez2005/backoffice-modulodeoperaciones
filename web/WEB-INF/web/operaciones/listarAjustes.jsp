<%-- 
    Document   : listarAjustes
    Created on : 06/03/2015, 04:08:44 PM
    Author     : ggutierrez
--%>



<%@taglib uri="/struts-tags" prefix="s" %>
<%@taglib uri="/struts-dojo-tags" prefix="sx" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ page buffer = "16kb" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Ajuste de Transacciones</title>
        <%@include file="../include/head.jsp" %>
        <link rel="stylesheet" type="text/css" href="../recursos/css/bootstrap.css"/>

        <script>
            function Editar(fila) {
                //window.alert(fila);
                $("#option").val("buscarTipoAjustes");

                var e = document.getElementById("listaTipoAjustes");
                var status = e.options[e.selectedIndex].value;
                $("#statusSelected").val(status);

                var u = document.getElementById("listaUsuariosBusqueda");
                var usuario = u.options[u.selectedIndex].value; 
                $("#usuarioSelected").val(usuario);

                $("#idFilaEditar").val(fila);

                $("#fechaIni2").val($("input[name=fechaIni]").val());
                $("#fechaFin2").val($("input[name=fechaFin]").val());
                document.example.submit();

            }

            /*$("#listaUsuariosBusqueda").change(function () {
             $("#selectedUsuarios").val($("#listaUsuariosBusqueda").val());                
             });*/


            $(document).ready(function () {
                $("#listaTipoAjustes2").prepend("<option selected>Seleccione...</option selected>");
                //$("#listaUsuariosBusqueda").prepend("<option selected>TODOS</option selected>");
                //$("#listaTipoAjustes").prepend("<option selected>TODOS</option selected>");
                $("#listaUsuariosBusqueda2").prepend("<option selected>TODOS</option selected>");

                $("#listaTipoAjustes2").on('change', function () {
                    var indice = document.getElementById("listaTipoAjustes2").value;

                    /*
                    if (indice === "AUTORIZADO") {
                        alert("Recuerde que no podrá autorizar mas de 500 registros al mismo tiempo");
                        return false;

                    }
                    */
                });

            });


            //$("#listaTipoAjustes2").before("<option></option>")

        </script>

    </head>
    <body>        
        <%@include file="../include/header.jsp" %>   
        <jsp:include page="../include/LoadingJquery.jsp"/>     
        <div class="container">
            <div class="content" id="content">
                <br/>
                <h1>Consultar Ajustes</h1>
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
                <s:form theme="simple" namespace="/operaciones" action="AjusteTransacciones">       
                    <div class="panel" style="width: 1000px;margin: 0 auto;">
                        <table class="table" style="width: 100%; padding: 0px; margin: 0px; ">
                            <thead>
                                <tr><th colspan="4" style="text-align:center;">Busqueda</th></tr>
                            </thead>
                            <tbody>
                                <tr id="picker1" style=" padding: 0px; margin: 0px">
                                    <td style="padding: 0px; margin: 0px; text-align: center" colspan="2"><table class=""  style="width: 100%;padding: 0px; margin: 0px;"><tbody><sx:datetimepicker cssClass="search-query" id="fechaIni" name="fechaIni" label="Fecha Inicio" displayFormat="dd-MMM-yyyy" value="%{'today'}"/></tbody></table></td>                                                                
                                    <td style="padding: 0px; margin: 0px; text-align: center" colspan="2"><table class=""  style="width: 100%;padding: 0px; margin: 0px;"><tbody><sx:datetimepicker cssClass="search-query" id="fechaFin" name="fechaFin" label="Fecha Fin" displayFormat="dd-MMM-yyyy" value="%{'today'}"/></tbody></table></td>
                                </tr>
                                <tr>
                                    <td  style="width: 18.8%;">Status</td>
                                    <td  style=""><s:select id="listaTipoAjustes" name ="selectedStatus" list = "status"/></td>
                                    <td  style="width: 16.4%;">Usuario</td>
                                    <td  style=""><s:select id="listaUsuariosBusqueda" name ="selectedUsuario" list = "listaUsuariosBusqueda"/></td>

                                </tr>
                                <tr>                                                                  
                                    <td colspan="4" style="text-align:center;"><s:submit cssClass="btn btn-primary" value= "Buscar" action="buscarAjustesAjusteTransacciones" onclick="openDialogLoading()"/></td>
                                </tr>  
                            </tbody>                            
                        </table>


                    </div>      
                </s:form>            
                <s:form name="example" theme="simple" namespace="/operaciones" action="AjusteTransacciones"> 
                    <s:select cssStyle="display:none"  id="listaTipoAjustes" name ="selectedStatus2" list = "status"/>
                    <!-- <s:select cssStyle="display:none"  id="listaUsuariosBusqueda2" name ="selectedUsuario" list = "listaUsuariosBusqueda"/>-->
                    <s:textfield cssStyle="display:none" id="selectedUsuarios"  name="selectedUsuario"  displayFormat="dd-MMM-yyyy" />
                    <s:textfield cssStyle="display:none" id="fechaIni2"  name="fechaIni2"  displayFormat="dd-MMM-yyyy" value="%{today}"/>
                    <s:textfield cssStyle="display:none" id="fechaFin2" name="fechaFin2"  displayFormat="dd-MMM-yyyy" value="%{today}"/>
                    <s:textfield cssStyle="display:none" id="fechaIni"  name="fechaIni"  displayFormat="dd-MMM-yyyy" />
                    <s:textfield cssStyle="display:none" id="fechaFin" name="fechaFin"  displayFormat="dd-MMM-yyyy" />
                    <s:textfield cssStyle="display:none" id="selectedStatus" name="selectedStatus"  />

                    <s:if test="%{getAjustes().size()>0}">
                        <div class="panel" style="width: 1000px;margin: 0 auto;">
                            <table class="table" style="width: 100%;">
                                <thead>

                                    <!--
                                    <tr><th colspan="12">Ajustes</th></tr>
                                    <tr>

                                        <th>Tarjeta</th>
                                        <th>Monto</th>
                                        <th>Fecha</th>
                                        <th>DNI</th>
                                        <th>Nombre</th>
                                        <th>Usuario</th>
                                        <th>Tipo Ajuste</th>
                                        <th>Estatus</th>  
                                        <th>Observacion</th>                    
                                        <th colspan="1"><input type="checkbox" onclick="checkboxes(this)"/></th>
                                    <s:if test="%{getStatusfromSelected(selectedStatus).equals(\"3\")}">
                                    <th>Editar</th>
                                    </s:if>
                            </tr>
                                    -->
                                </thead>


                                <tbody>       


                                    <!--
                                    <s:iterator value="ajustes" >                                       
                                        <tr>                                    
                                            <td style="width:16%;text-align:center;"><s:property value="mascara"/></td>
                                            <td style="width:10%;text-align:center;">

                                        <s:if test="%{!isEditar()}">
                                            <s:property value="monto"/>
                                        </s:if><s:else><s:if test="%{filaEditar.equals(getIdDetalleAjuste())}">
                                                <s:textfield value="%{monto}" cssStyle= "width:80px" name="montoEditar" id="idMontoEditar"/>
                                            </s:if><s:else><s:property value="monto"/></s:else>                                                                          
                                        </s:else>

                                    </td>
                                    <td style="width:10%;text-align:center;"><s:property value="fecha"/></td>
                                    <td style="width:10%;text-align:center;"><s:property value="idExtPer"/></td>                                            
                                    <td style="width:10%;text-align:center;"><s:property value="nomCliente"/></td>
                                    <td style="width:16%;text-align:center;"><s:property value="usuario"/></td>
                                    <td style="width:16%;text-align:center;">
                                        <s:if test="%{!isEditar()}">
                                            <s:property value="descripcion"/>
                                        </s:if>
                                        <s:else>
                                            <s:if test="%{filaEditar.equals(getIdDetalleAjuste())}">
                                                <select name="selectedtipoAjuste"  id="tipoAjustesdic"><s:iterator value="tipoAjustes" var="myObj1">

                                                        <option value="<s:property value ="idCodigoAjuste"/>"> <s:property value ="descripcion"/></option>
                                                </s:iterator></select>
                                            </s:if>
                                            <s:else>
                                                <s:property value="descripcion"/>
                                            </s:else>                                                                          
                                        </s:else></td>
                                <td style="width:16%;text-align:center;"><s:property value="descStatus"/></td>                                            
                                <td style="width:16%;text-align:center;"><s:property value="observacion"/></td>

                                <td style="width:5%;text-align:center;"><s:checkbox name="selectedAjuste" fieldValue="%{getIdDetalleAjuste()}" value="%{getIdDetalleAjuste()}" /></td>                                             
                                        <s:if test="%{getStatusfromSelected(selectedStatus).equals(\"3\")}">
                                            <td style="width:5%;text-align:center;"><img width="25px" src="../recursos/images/editarOperaciones.png" alt="editar" onclick="Editar(<s:property value="getIdDetalleAjuste()"/>);"/></td></s:if>
                                        </tr>                                     
                                    </s:iterator>   
                                    -->




                                    <%
                                        String abcdStr = (String) request.getAttribute("nroPaginas");
                                        int abcdInt = Integer.valueOf("15").intValue();
                                    %> 

                                    <!-- PAGINACION QUE SUPLANTA EL ITERATOR (COMENTADO EN LA LINEA DE ARRIBA) -->
                                    <display:table export="false" class="table-bordered table-striped table-hover table-condensed"  style="width: 100%;text-align:center;" 
                                    id="ajustesTable" name="ajustes" pagesize="15" requestURI="" >

                                        <tr>                                    

                                            <display:column title="Tarjeta"  style="width:15%;text-align:center;"> 
                                                <s:property value="#attr.ajustesTable.mascara"/>
                                            </display:column> 

                                            <display:column title="Monto"  style="width:15%;text-align:center;">  
                                                <s:if test="%{!isEditar()}">
                                                    <s:property value="#attr.ajustesTable.monto"/>
                                                </s:if>
                                                <s:else>
                                                    <s:if test="%{filaEditar.equals(#attr.ajustesTable.getIdDetalleAjuste())}">
                                                        <s:textfield value="%{#attr.ajustesTable.monto}" cssStyle= "width:80px" name="montoEditar" id="idMontoEditar"/>
                                                    </s:if>
                                                    <s:else>
                                                        <s:property value="#attr.ajustesTable.monto"/> 
                                                    </s:else>                                                                          
                                                </s:else>
                                            </display:column>

                                            </td>
                                            <display:column title="Fecha"  style="width:15%;text-align:center;">
                                                <s:property value="#attr.ajustesTable.fecha"/>
                                            </display:column>

                                            <display:column title="DNI"  style="width:15%;text-align:center;"> 
                                                <s:property value="#attr.ajustesTable.idExtPer"/>
                                            </display:column>    

                                            <display:column  title="Nombre"  style="width:15%;text-align:center;"> 
                                                <s:property value="#attr.ajustesTable.nomCliente"/>
                                            </display:column> 

                                            <display:column property="usuario" title="Usuario"  style="width:15%;text-align:center;"> 
                                                <s:property value="#attr.ajustesTable.fecha"/>
                                            </display:column> 

                                            <display:column title="Tipo Ajuste"  style="width:15%;text-align:center;">  
                                                <s:if test="%{!isEditar()}">
                                                    <s:property value="#attr.ajustesTable.descripcion"/> 
                                                </s:if>
                                                <s:else>
                                                    <s:if test="%{filaEditar.equals(#attr.ajustesTable.getIdDetalleAjuste())}">
                                                <select name="selectedtipoAjuste"  id="tipoAjustesdic">
                                                    <s:iterator value="tipoAjustes" var="myObj1">
                                                        <option value="<s:property value ="idCodigoAjuste"/>"> <s:property value ="descripcion"/></option>
                                                    </s:iterator>
                                                </select>
                                            </s:if>
                                            <s:else>                                                        
                                                <s:property value="#attr.ajustesTable.descripcion"/>
                                            </s:else>                                                                          
                                        </s:else></td>
                                    </display:column>

                                    <display:column  title="Estatus"  style="width:15%;text-align:center;">  
                                        <s:property value="#attr.ajustesTable.descStatus"/>
                                    </display:column>   

                                    <display:column  title="Observacion"  style="width:15%;text-align:center;"> 
                                        <s:property value="#attr.ajustesTable.observacion"/>
                                    </display:column>  

                                    <display:column title="<input type='checkbox' onclick='checkboxes(this)' />">
                                        <input type="checkbox" name="selectedAjuste" value=${ajustesTable.getIdDetalleAjuste()} />
                                    </display:column>

                                    <s:if test="%{getStatusfromSelected(selectedStatus).equals(\"3\")}">
                                        <display:column title="Editar"> 
                                            <img width="25px" src="../recursos/images/editarOperaciones.png" alt="editar" onclick="Editar(<s:property value="#attr.ajustesTable.getIdDetalleAjuste()"/>);"/>
                                        </display:column>
                                    </s:if>

                                    </tr> 

                                    <display:setProperty name="paging.banner.placement" value="bottom"/>

                                </display:table>
                                   

                                </tbody>                               
                            </table>
                            <table>
                                <tr>
                                    <s:if test="%{isEditar()}">
                                        <s:hidden value="HEditar" name="opcion" id="option" />
                                    </s:if>
                                    <s:else>
                                        <s:hidden value="actualizarAjustes" name="opcion" id="option" />
                                        <s:hidden name="selectedStatus2" id="statusSelected" />
                                        <s:hidden name="selectedUsuario2" id="usuarioSelected" />
                                    </s:else>
                                    <s:hidden  name="filaEditar" id="idFilaEditar" />
                                    <s:if test="%{!isEditar()}">
                                        <td style="text-align: center"><label>Cambiar Status: </label></td>
                                        <td style="text-align: center"><s:select id="listaTipoAjustes2" name ="selectedStatus" list = "status2" /></td>
                                    </s:if>
                                    <td style="text-align: center"><s:submit cssClass="btn btn-primary" value= "Confirmar" action= "actualizarStatusAjustesAjusteTransacciones"/></td>
                                    <!--
                                    <td style="width:50%;text-align:right;" ><span class="text-1">Pagina</span></td>
                                    <td>  <s:textfield style="width:50%;" cssClass="search-query" name="nroPaginas" value="22" id="nroPaginas" disabled="true" /></td>  
                                    <td style="text-align:right;"><s:submit cssClass="btn btn-primary" value= "Aplicar" action="buscarAjustesAjusteTransacciones" onclick="openDialogLoading()"/></td>
                                    -->
                                </tr>   
                            </table>
                        </div>
                    </s:if>            
                </s:form>
                <s:form theme="simple" namespace="/operaciones" action="AjusteTransacciones" name="formularioEditar">

                </s:form>
            </div>       
        </div>
        <%@include file="../include/footer.jsp" %>
        <script>
            //Para confirmar antes de accionar, colocar en el elemento class="confirmation"
            $('.confirmation').on('click', function () {
                var data = 'Desea ajustar la transaccion?';

                return  confirm(data);
            });
            function checkboxes(box) {
                /*
                 *var elements = getElementById("AjusteTransacciones_checkMe");                                                
                 for( i = 0; i< elements.length;i++){                    
                 elements[i].checked = box.checked;
                 }*/

                var inputs = document.getElementsByTagName('input');
                for (var i = 0; i < inputs.length; i++) {
                    if (inputs[i].getAttribute('type') == 'checkbox') {
                        inputs[i].checked = box.checked;
                    }
                }
            }

        </script>
    </body>
</html>
