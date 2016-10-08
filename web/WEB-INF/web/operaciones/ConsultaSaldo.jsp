<%-- 
    Document   : ConsultaSaldo
    Created on : 01/08/2016, 04:22:53 PM
    Author     : rgomez
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Consulta de Saldo</title>
        <%@include file="../include/head.jsp" %>
        <link rel="stylesheet" type="text/css" href="../recursos/css/bootstrap.css"/>
    </head>
    <body>
        <%@include file="../include/header.jsp" %>
        <div class="container">
            <div class="content" id="content">
                <br/>
                <h1>Consulta Saldo</h1>
                <br/>
                <s:if test="%{!message.equals(\"\")}">
                    <div class="alert alert-info">
                        <s:property value = "%{message}" />
                    </div>
                </s:if>
                
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
                                    <s:form theme="simple" namespace="/operaciones" action="ConsultaSaldo">
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
                    <s:form action="ConsultaSaldo" method="post" enctype="multipart/form-data" theme = "simple">
                        <table class="table" style="width: 100%;">
                            <thead>
                                <tr><th colspan="3" style="text-align:center;">Consulta de saldo masiva</th></tr>
                            </thead>
                            <tbody>                                
                                <tr>
                                    <td><label>Cargar Archivo :</label></td>
                                    <td><s:file cssClass="btn btn-info" name="file" label="File"/></td>  
                                    
                                </tr>
                                
                                <tr>                                    
                                    <td colspan="3" style="text-align:center"  ><s:submit cssClass="btn btn-primary" value="Cargar Archivo" position="center" action= "UploadConsultaSaldo"/></td>                                   
                                    
                                </tr>

                            </tbody>      
                        </table>                        
                    </s:form>
                     <div class="panel" style="width: 900px;margin: 0 auto;">
                        <table class="table" style="width: 100%;">
                            <thead>
                                <tr><th colspan="2" style="text-align:center;">Listado de Tarjetas</th></tr>
                                <tr>
                                    
                                    <th style="width:448px;text-align:center;">Tarjeta</th>
                                    <th style="width:448px;text-align:center;">Saldo Disponible</th>
                                    
                                </tr>
                            </thead>
                            <tbody>                                      
                                <s:iterator value="tarjetasRes" var="myObj">
                                  
                                        <tr>
                                                
                                            <td style="width:50%;text-align:center;" ><s:property value="nroTarjeta"/></td>                                            
                                            <td style="width:50%;text-align:center;" ><s:property value="saldoDisponible"/></td>                                            
                                           
                                        </tr>
                                    
                                </s:iterator>
                            </tbody>
                        </table>
                    </div>
                    <a href="reportesConsultaSaldo.do" download>Descargar Archivo</a>
                    
                </div>   
            </div>           
        </div>  
        <%@include file="../include/footer.jsp" %>
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
