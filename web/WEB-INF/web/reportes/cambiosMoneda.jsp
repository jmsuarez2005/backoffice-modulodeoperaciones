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

        <title>Cambios de Moneda</title>
        <%@include file="../include/head.jsp" %>
    </head>
    <body>

        <%@include file="../include/header.jsp" %>
        <div class="container">
            <div class="content" id="content">
                <br/>
                <h1>Cambios de Moneda</h1>

                <br/>

                <s:form theme="simple" namespace="/reportes" action="CambiosMoneda">
                    <div class="panel" style="width: 770px;margin: 0 auto;">
                        <table class="table" style="width: 100%;">
                            <thead>
                                <tr><th colspan="2">Propiedades</th></tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td style="width:50%;text-align:right;"><span class="text-1">BsS / $</span></td>
                                    <td><s:textfield name="cambioDolaresVE" id="cambioVE" /></td>
                                </tr>
                                <tr>
                                    <td style="width:50%;text-align:right;"><span class="text-1">Soles / $</span></td>
                                    <td><s:textfield name="cambioDolaresPE" id="cambioPE"/></td>
                                </tr>
                                <tr>
                                    <td style="width:50%;text-align:right;"><span class="text-1">Pesos / $</span></td>
                                    <td><s:textfield name="cambioDolaresCO" id="cambioCO"/></td>
                                </tr>
                                <tr>
                                    <td colspan="2" align="center"><s:submit value="Guardar Cambios" action="updateCambiosMoneda" onclick="return confirmBox();"/></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </s:form>

                <br/>
                <s:fielderror />

                <div style="width: 770px;margin: 0 auto;">        
                    <%@include file="../include/alertMessage.jsp" %>
                </div>


                <br/><br/><br/><br/>            

            </div>
        </div>
        <%@include file="../include/footer.jsp" %>


        <script>
            function confirmBox() {
                var ve = document.getElementById("cambioVE").value;
                var pe = document.getElementById("cambioPE").value;
                var co = document.getElementById("cambioCO").value;
                if (ve > 0 && pe > 0 && co > 0) {
                    return true;
                } else {
                    alert("Los montos deben ser mayor a 0");
                    return false;
                }
            }

        </script>

    </body>
</html>