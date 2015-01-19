<%@taglib uri="/struts-tags" prefix="s" %>

<div class="nav" id="nav">
    <div class="navContent">

        <ul id="navBar">
            <li><s:a action="portal.do" namespace="/inicio">Inicio</s:a></li>

            <li><a href="">Reportes</a>
                <ul>
                    <li><s:a action="ActividadDiaria" namespace="/reportes">Resumen de Actividad Diaria</s:a></li>
                </ul>
            </li>
            <li><a href="">Configuración</a>
                <ul>
                    <li><s:a action="CambiosMoneda" namespace="/reportes">Cambios de Moneda</s:a></li>
                </ul>
            </li>
<!--            
            <li><a href="">Elemento 2</a>
             <ul>
               <li><a href="">Elemento 2.1</a></li>
               <li><a href="">Elemento 2.2</a></li>
               <li><a href="">Elemento 2.3</a></li>
               <li><a href="">Elemento 2.4</a>
                <ul>
                 <li><a href="">Elemento 2.4.1</a></li>
                 <li><a href="">Elemento 2.4.2</a></li>
                 <li><a href="">Elemento 2.4.3</a></li>
                </ul>
               </li>
               <li><a href="">Elemento 2.5</a></li>
             </ul>
            </li>

            <li><s:a action="pruebaAction.do" namespace="/prueba">Módulo de Prueba</s:a></li>

            <li><a href="">Módulo</a>
                <ul>
                   <li><a href="">Función 1</a></li>
                   <li><a href="">Función 2</a></li>
                   <li><a href="">Función 3</a></li>
                   <li><a href="">Cat. de Función</a>
                       <ul>
                           <li><a href="">Función 4</a></li>
                           <li><a href="">Función 5</a></li>
                       </ul>
                   </li>
                </ul>
            </li>
-->
            <li style="float:right"><s:a action="perfil" namespace="/inicio"><s:property value="#session.usuarioSesion.idUsuario" /></s:a>
              <ul>
                  <li><s:a action="perfil" namespace="/inicio">Ver Perfil</s:a></li>
              <li><s:a action="Clave" namespace="/inicio">Cambiar Clave</s:a></li>
                  <li><s:a action="LogoutAction" namespace="/inicio">Salir</s:a></li>
              </ul>   
            </li>
      </ul>


    </div>
</div>