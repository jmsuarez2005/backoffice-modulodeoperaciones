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
        
        <%@include file="../include/head_1.jsp" %>
        
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
                //$("#listaUsuariosBusqueda").prepend("<option selected>Todos</option selected>");
                //$("#listaTipoAjustes").prepend("<option selected>Todos</option selected>");
                $("#listaUsuariosBusqueda2").prepend("<option selected>Todos</option selected>");
                //$("#listaFiltroAjustes").prepend("<option selected>Fecha</option selected>");

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
            <header>
                <%@include file="../include/header.jsp" %>
            </header>
            <jsp:include page="../include/LoadingJquery.jsp"/>    
            <div class="container">
            <div class="content" id="content">
                <h1 style="margin: 0 0 0 0;">Consultar Ajustes</h1>
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
                                <tr><th colspan="6" style="text-align:center;">Búsqueda</th></tr>
                            </thead>
                            <tbody>
                                <tr id="picker1" style=" padding: 0px; margin: 0px">
                                    <td colspan="2" style="padding: 0px; margin: 0px; text-align: center"><table class=""  style="width: 100%;padding: 0px; margin: 0px;"><tbody><sx:datetimepicker cssClass="search-query" id="fechaIni" name="fechaIni" label="Fecha Inicio" displayFormat="dd-MMM-yyyy" value="%{'today'}"/></tbody></table></td>                                                                
                                    <td colspan="2" style="padding: 0px; margin: 0px; text-align: center"><table  class=""  style="width: 100%;padding: 0px; margin: 0px;"><tbody><sx:datetimepicker cssClass="search-query" id="fechaFin" name="fechaFin" label="Fecha Fin" displayFormat="dd-MMM-yyyy" value="%{'today'}"/></tbody></table></td>
                                </tr>
                                <tr>
                                    <td  style="width: 18.8%;">Estatus</td>
                                    <td  style=""><s:select id="listaTipoAjustes" name ="selectedStatus" list = "status"/></td>
                                    <td  style="width: 16.4%;">Usuario</td>
                                    <td  style=""><s:select id="listaUsuariosBusqueda" name ="selectedUsuario" list = "listaUsuariosBusqueda"/></td>         

                                </tr>
                                <tr>                                 
                                    <td style="width: 18.8%;">Ordenar</td>
                                    <td style=""><s:select id="listaFiltroAjustes" name ="selectedFiltro" list = "filtro"/></td>     
                                    <td  style="width: 16.4%;">Listar</td>
                                    <td  style=""><s:select id="listarRegistrosPorPagina" name ="selectedPages" list = "registrosPorPagina"/></td>                                                   
                                </tr> 
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
                    <s:textfield cssStyle="display:none" id="selectedStatus" name="selectedStatus" />
                    
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
                                        <th>Observación</th>                    
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
                                    
                                    
                                    <!-- PAGINACION QUE SUPLANTA EL ITERATOR (COMENTADO EN LA LINEA DE ARRIBA) -->
                                    <display:table export="false" class="table-bordered table-striped table-hover table-condensed"  style="width: 100%;text-align:center;" 
                                                   id="ajustesTable" name="ajustes" pagesize="${selectedPages}" requestURI="" >

                                        <tr>                                    

                                            <display:column title="Tarjeta"  style="width:15%;text-align:center;" > 
                                                <s:property value="#attr.ajustesTable.mascara"/>
                                            </display:column> 

                                            <display:column title="Monto"  style="width:15%;text-align:center;" >  
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
                                                <input type="hidden" name="montoChecked" value="${ajustesTable.getMonto()}@@${ajustesTable.getStatus()}"/>          
                                            </display:column>

                                            <display:column title="Fecha"  style="width:15%;text-align:center;" >
                                                <s:property value="#attr.ajustesTable.fecha"/>
                                            </display:column>

                                            <display:column title="DNI"  style="width:15%;text-align:center;" > 
                                                <s:property value="#attr.ajustesTable.idExtPer"/>
                                            </display:column>    

                                            <display:column  title="Nombre"  style="width:15%;text-align:center;" > 
                                                <s:property value="#attr.ajustesTable.nomCliente"/>
                                            </display:column> 
                                            
                                            <display:column  title="Empresa"  style="width:15%;text-align:center;" > 
                                                <s:property value="#attr.ajustesTable.idExtEmp"/>
                                            </display:column> 

                                            <display:column property="usuario" title="Usuario"  style="width:15%;text-align:center;" > 
                                                <s:property value="#attr.ajustesTable.fecha"/>
                                            </display:column> 

                                            <display:column title="Tipo Ajuste"  style="width:15%;text-align:center;" >  
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

                                    <display:column  title="Estatus"  style="width:15%;text-align:center;" >  
                                        <s:property value="#attr.ajustesTable.descStatus"/>
                                    </display:column>   

                                    <display:column  title="Observación"  style="width:15%;text-align:center;" > 
                                        <s:property value="#attr.ajustesTable.observacion"/>
                                    </display:column>  

                                    <display:column title="<input type='checkbox' name='selectedAjusteAll' onclick='checkboxes(this)'/>">
                                        <s:if test="%{!#attr.ajustesTable.getStatus().equals(\"7\") && !#attr.ajustesTable.getStatus().equals(\"2\")}">
                                            <input type="checkbox" name="selectedAjuste" onclick="checkbox(this,${ajustesTable.getMonto()})" value=${ajustesTable.getIdDetalleAjuste()} />                     
                                        </s:if>
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
                                        <s:if test="%{!selectedStatus.equals(\"ANULADO\") && !selectedStatus.equals(\"PROCESADO\")}">
                                            <td style="text-align: center"><label>Cambiar Estatus: </label></td>
                                            <td style="text-align: center"><s:select id="listaTipoAjustes2" name ="selectedStatus" list = "status2" /></td>
                                        </s:if>
                                    </s:if>
                                    <s:if test="%{!selectedStatus.equals(\"ANULADO\") && !selectedStatus.equals(\"PROCESADO\")}">
                                        <td style="text-align: center"><s:submit cssClass="btn btn-primary" value= "Confirmar" action= "actualizarStatusAjustesAjusteTransacciones" onclick="return confirmBox(selectedStatus);"/></td>
                                    </s:if>
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
         <footer>
            <%@include file="../include/footer.jsp" %>    
        </footer> 
        <script>
            
            var c = 0;
            var x = 0;
            var z = 0;
            var l = 0;
            var montoTotal = 0;
            
            //Para confirmar antes de accionar, colocar en el elemento class="confirmation"
            $('.confirmation').on('click', function () {
                var data = '¿Desea ajustar la transacción?';

                return  confirm(data);
            });
            
            function checkbox(box,monto) {
                if (box.checked) {
                    c = c + 1;
                    montoTotal = montoTotal + monto;
                }else{
                    c = c - 1;
                    montoTotal = montoTotal - monto;
                }
                
                if(c > 0){
                    x = 1;
                }
                if(montoTotal < 0){
                    montoTotal = 0;
                }
                
                z = 0;            
                
            }
            function checkboxes(box) {
                    /*
                 *var elements = getElementById("AjusteTransacciones_checkMe");                                                
                 for( i = 0; i< elements.length;i++){                    
                 elements[i].checked = box.checked;
                 }*/
                if(x == 1){
                    c = 0;
                    montoTotal = 0;
                    x = 0;
                }
                var inputs = document.getElementsByTagName('input');
                var inputsMonto = document.getElementsByTagName('input');
                //var inputsStatus = document.getElementsByTagName('input');
                var statusNum, montoNum;
                for (var i = 0; i < inputs.length; i++) {
                    if (inputs[i].getAttribute('type') == 'checkbox') {
                        inputs[i].checked = box.checked;
                        if (box.checked) {
                            c = c + 1;
                        }else { 
                            c = c - 1;
                        }
                    }
                    /*
                    if (inputsStatus[i].getAttribute('type') == 'hidden') {
                        if (inputsStatus[i].getAttribute('name') == 'statusChecked') {
                            statusNum = Number(inputsStatus[i].getAttribute('value'));
                        }
                    }*/
                    if (inputsMonto[i].getAttribute('type') == 'hidden') {
                       
                        if (inputsMonto[i].getAttribute('name') == 'montoChecked') {
                            statusNum = inputsMonto[i].getAttribute('value');
                            montoNum = inputsMonto[i].getAttribute('value');
                            
                            statusNum = Number(statusNum.substring(statusNum.length - 1, statusNum.length));
                            montoNum = Number(montoNum.substring(0, montoNum.length - 3));
                            
                            if(statusNum != 2 && statusNum != 7 && l == 0){
                                //montoTotal = montoTotal + Number(inputsMonto[i].getAttribute('value'));
                                montoTotal = montoTotal + montoNum;
                            }
                            if(l == 1){
                                 montoTotal = 0;
                                 l = 2;
                            }
                        }                       
                    }                    
                }
                if(l == 2){
                    l = 0;
                }else{
                    l = 1;
                }
                //window.confirm(montoTotal);
                if(c > 0){
                    c = c - 1;
                    z = 1;
                }else if(c < 0){
                    c = 0;
                    montoTotal = 0;
                }
                
                
                
            }
            
            function confirmBox() {
                
                var sel = document.getElementById('listaTipoAjustes2');
                var inputs = document.getElementsByTagName('input');
                
                if(sel.options[sel.selectedIndex].text == "Autorizar Todos"){
                    
                    //CANTIDAD DE REGISTRO Y MONTO TOTAL
                    z = 0;
                    c = 0;
                    montoTotal = 0; 
                    <s:iterator value="ajustes" >
                        var monto = <s:property value="monto"/>;
                        var status = <s:property value="status"/>;
                        if(status != 7 && status != 2){
                            montoTotal = montoTotal + monto;
                            c = c + 1;
                        }
                    </s:iterator>
                        
                }
                var resp;
                
                if(c > 0){
                    if(montoTotal > 0){
                        montoTotal = parseFloat(Math.round(montoTotal * 100) / 100).toFixed(2);
                    }
                    if(z != 1){
                        resp = window.confirm("Total registros: " + c + "\n" +
                                        "    Total monto: " + montoTotal + "\n");
                    }else{
                        resp = window.confirm("Total registros: " + c + "\n" +
                                        "    Total monto: " + montoTotal + "\n");                        
                    }
                }else{
                    alert("Debe seleccionar un registro");
                    resp = false;
                }
                
                if (resp == true) {
                   return true;
                } else {
                   z = 0;
                   c = 0;
                   l = 0;
                   montoTotal = 0;
                   for (var i = 0; i < inputs.length; i++) {
                        if (inputs[i].getAttribute('type') == 'checkbox') {
                            $('input:checkbox[name=selectedAjuste]').attr('checked',false);
                            $('input:checkbox[name=selectedAjusteAll]').attr('checked',false);
                        }
                    }
                   return false;
                }
             }
        </script>
    </body>
</html>
