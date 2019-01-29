<%@taglib uri="/struts-tags" prefix="s" %>
    <div class="cont-title">
        <img alt="logo" class="logo" src="../recursos/images/emblem-operation.png">
            <span class="title5"><s:property value="@com.novo.constants.BasicConfig@appNameExtended" /></span>
        
             <div class="headerBG">
                <s:if test="%{#session.usuarioSesion!=null}">
                    <%@include file="nav.jsp" %>
                </s:if>
            </div>
    </div>                 
                
                
                
                