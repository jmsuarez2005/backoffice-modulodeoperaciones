<%-- 
    Document   : actualizaciones
    Created on : 05/08/2015, 03:05:38 PM
    Author     : ggutierrez
--%>

<%@taglib uri="/struts-tags" prefix="s" %>
<%@taglib uri="/struts-dojo-tags" prefix="sx" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
        <title>Afiliacion</title>
       <%@include file="../include/head_1.jsp" %>
    
        <body>
            <header>
                <%@include file="../include/header.jsp" %>
            </header>
            <jsp:include page="../include/LoadingJquery.jsp"/>
            <div class="container">
            <div class="content" id="content">
                <h1 style="margin: 0 0 0 0;">Afiliacion</h1>
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

                <!--
                                <svg height="200" width="200">
                                <circle id="_cir_P_x" cx="100" cy="100" r="60" stroke="#004C70" stroke-width="20" fill="#E0CC97"></circle>
                                <circle id="_cir_P_y" cx="100" cy="100" r="60" stroke="#E0A025" stroke-width="20"  stroke-dasharray="0,1000" fill="none"></circle>
                                <text x="50%" y="50%" text-anchor="middle" stroke="none" stroke-width="1px" dy=".3em" id="_cir_Per">0%</text>
                                </svg>
                
                                <button onclick="__showProgress(30)" class="btn btn-default">Show 30%</button>
                -->

                <div class="panel" style="width: 900px;margin: 0 auto;"> 
                    <s:form action="Afiliacion" method="post" enctype="multipart/form-data" theme = "simple">
                        <table class="table" style="width: 100%;">
                            <thead>
                                <tr><th colspan="3" style="text-align:center;">Afiliacion Masiva</th></tr>
                            </thead>
                            <tbody>                                
                                <tr>
                                    <td><label>Cargar Archivo :</label></td>
                                    <td><s:file cssClass="btn btn-info" name="upload" label="File"/></td>  
                                </tr>
                                <tr>                                    
                                    <td colspan="3" style="text-align:center"  ><s:submit cssClass="btn btn-primary" value="Cargar archivo" position="center" action= "UploadAfiliacion" onclick="openDialogLoading()"/></td>                                   

                                </tr>

                            </tbody>      
                        </table> 


                        <!-- Prueba progressbar           
                       <div id="myProgress">
                           <div id="myBar" onload=""></div>
                       </div>
                        -->
                        <s:if test="%{getTarjetasAct().size()>0}">
                            <table class="table" style="width: 20%;text-align:center;" align="center">
                                <thead>
                                    <tr>
                                        <th style="text-align:center;">Numero de registros</th>
                                    </tr>
                                </thead>
                                <tbody>      
                                    <tr>  
                                        <td style="text-align:center;" colspan="1"><s:property value="cantidadTarjetas"/></td>                                                                                       
                                    </tr>

                                </tbody>
                            </table>

                            <table style="width: 25%;text-align:center;" align="center">
                                <td>
                                    <s:submit cssClass="btn btn-primary" value="Afiliar" action= "afiliarAfiliacion" align="center" onclick="openDialogLoading()"/>
                                </td> 
                            </table>

                        </s:if>



                    </s:form>  


                </div>  

            </div>           
        </div>  
        <footer>
            <%@include file="../include/footer.jsp" %>    
        </footer>
        <script>
           
           

            var contador = 1;
            document.getElementById("boton1").addEventListener("click", function (event) {
                event.preventDefault()
                despliegue();
            });
            function despliegue() {
                document.getElementById("listaCampos" + contador++).style.display = "table-row";

            }

            function move() {
                var elem = document.getElementById("myBar");
                var width = 1;
                var id = setInterval(frame, 10);
                function frame() {
                    if (width >= 100) {
                        clearInterval(id);
                    } else {
                        width++;
                        elem.style.width = width + '%';
                    }
                }
            }

            function gowait() {
                document.getElementById("main").style.visibility = "hidden";
                document.getElementById("wait").style.visibility = "visible";
                window.setTimeout('showProgress()', 500);
            }
            function showProgress() {
                var wg = document.getElementById("progressbar");
                wg.src = wg.src;
            }



        </script>
        <style>
            #myProgress {
                width: 100%;
                background-color: #ddd;
            }

            #myBar {
                width: 1%;
                height: 30px;
                background-color: #4CAF50;
            }
        </style>
    </body>

</html>
