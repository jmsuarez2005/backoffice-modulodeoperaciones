<%@taglib uri="/struts-tags" prefix="s" %>
<s:bean name="com.novo.util.Version" var="version" />
<div class="footerBG" data-module="footer">
    <div class="footerContent">
        <br/>
        <table style="width:100%;border-spacing: 0;border-collapse: collapse">
            <tbody>
                <tr>
                    <td style="width:50%; text-align:left; vertical-align:top;">
                        <img src="../recursos/images/logo-novopayment.png" alt="novopayment" style="float:left; margin-right:15px;" />
                        <s:property value="@com.novo.constants.BasicConfig@appNameExtended" />, Versión <s:property value="#version.version" /><br/>
                        Powered by Novopayment, Inc. Todos los derechos reservados.
                    </td>
                    <td style="width:50%; text-align:right; vertical-align:top;">
                        <s:if test="%{#session.usuarioSesion!=null}">
                            <span class="user">
                                <s:property value="#session.usuarioSesion.acNombre" />, 
                                <s:if test="%{#session.usuarioSesion.acTipo.equalsIgnoreCase('M')}">
                                    MASTER
                                </s:if>
                                <s:else>
                                    USUARIO
                                </s:else>
                            </span>
                        </s:if>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
                    
<script type="text/javascript" src="../recursos/js/commons.js" ></script>