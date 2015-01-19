<%@taglib uri="/struts-tags" prefix="s" %>

<div class="headerBG" onmouseover="javascript:displayNav();" onmouseout="javascript:hideNav();">
    <div class="headerContent" id="headerContent">
        <table style="width:100%;border-spacing: 0;border-collapse: collapse;">
            <tr>
                
                <td style="text-align:left;width:115px;">
                    <img src="../recursos/images/logo-tebca.png" alt="tebca" />
                    <img src="../recursos/images/logo-servitebca.png" alt="servitebca" />                  
                </td>
                <td style="">
                    <b style="font-size:15px;"><s:property value="@com.novo.constants.BasicConfig@appNameExtended" /></b>
                </td>
                
            </tr>                
        </table>
    </div>
    <s:if test="%{#session.usuarioSesion!=null}">
          <%@include file="nav.jsp" %>
    </s:if>
</div>