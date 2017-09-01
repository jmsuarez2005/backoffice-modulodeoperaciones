<%-- 
    Document   : LoadingJquery
    Created on : 30-jun-2017, 10:29:48
    Author     : etabban
--%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sj" uri="/struts-jquery-tags"%>  

<html>
    <head>

        <sj:head jqueryui="true"   jquerytheme="redmond"/>

    </head>

    <body>

        <sj:dialog id="indicator"
                   height="70"
                   width="170"
                   title="Cargando"
                   autoOpen="false"
                   modal="true"
                   resizable="false"
                   cssStyle="ui-dialog-title hidden;"
                   closeOnEscape="false"
                   showEffect="fadeIn" 
                   hideEffect="fadeOut">

            <!--
        <td style="vertical-align: middle;"><img  src="../recursos/images/jquery_loader.gif" /></td>

        <td style="vertical-align: middle">&nbsp; Cargando ...</td>
            -->
        <td style="vertical-align: middle;">

            <img align="middle" style="text-align: center;" src="../recursos/images/jquery_loader.gif" /> &nbsp; Cargando ...
        </td>

    </sj:dialog> 

    <script>

        function openDialogLoading() {
            $(".ui-dialog-titlebar").hide();
            $("#indicator").dialog('open');
        }

        function closeDialogLoading() {
            $('#indicator').dialog('close');
        }

    </script>
</body>
</html>