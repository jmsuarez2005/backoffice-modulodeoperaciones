<%@taglib uri="/struts-tags" prefix="s" %>
<s:bean name="com.novo.util.Version" var="version" />
<s:bean name="com.novo.util.CountriesUtil" var="countryUtil" />

<%@ page import='com.novo.util.*' %>
             
<script type="text/javascript" src="../recursos/js/commons.js" ></script>

           <div class="columns1">
               <span class="foot_left">
                   <!-- © 2019 NovoPayment Inc. All rights reserved. -->
                 <!-- < %@ page import="com.novo.util.FechaUtil" %> -->
                 <!-- < % com.novo.util.FechaUtil f = new com.novo.util.FechaUtil(); %> -->
                 <% Version f = new Version(); %>
                   © <%=f.getAnio()%> NovoPayment Inc. All rights reserved
               </span>
           </div>
           <div class="columns2">
                <span class="foot_left">
                   M&oacute;dulo de operaciones. Versión <s:property value="#version.version" />
                </span>
            </div>
           <div class="columns3">
              <a target="_blank" href="http://novopayment.com/">
                  <img alt="Powered-By" src="../recursos/images/logo-novopayment-powered.png" height="28px">
              </a>
          </div>
                
   <script type="text/javascript">
   	
	 footer = function(){ 
     /*el alto que tiene el navegador*/
     $alto_navegador= $(window).height();
     /*el alto que tiene el contenido de la pagina*/
     $alto_documento= $(document).height(); 
     /*  aqui condicionamos si el alto del contenido 
      *  es mayor que
      *  el alto del navegador*/
     if ($alto_documento>$alto_navegador)
     {
         /* si es mayor es que tiene un contenido mas 
          * largo que el alto del navegador y entonces lo dejamos a relativo*/
         $("footer").css({"position":"relative"})
         console.log("relative");
     }
     else
     {
         /* si el alto del contenido es menor que el alto del navegador es que
          * tenemos espacio vacio y le mandamos abajo*/
         $("footer").css({"position":"fixed"})
         console.log("fixed");
     } 
 }
footer();
</script>         
