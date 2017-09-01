<%@taglib uri="/struts-tags" prefix="s" %>

<div class="nav" id="nav">
    <div class="navContent">


        <ul id="navBar">
            <jsp:include page="../include/LoadingJquery.jsp"/>

            <li><s:a action="portal.do" namespace="/inicio" onclick="openDialogLoading()">Inicio</s:a></li>

                <li><a href="">Reportes</a>
                    <ul>
                        <li><s:a action="ActividadDiaria" namespace="/reportes" onclick="openDialogLoading()">Resumen de Actividad Diaria</s:a></li>
                    </ul>
                </li>
                <li><a href="">Configuración</a>
                    <ul>
                        <li><s:a action="CambiosMoneda" namespace="/reportes" onclick="openDialogLoading()">Cambios de Moneda</s:a></li>
                    </ul>
                </li>
                <li><a href="">Operaciones</a>
                    <ul>
                        <li><a href="">Ajuste de Transacciones</a>
                            <ul>
                                <li><s:a action="AjusteTransacciones" namespace="/operaciones" onclick="openDialogLoading()">Ajuste de Transacciones</s:a></li>
                            <li><s:a action="ajusteTarjetaAjusteTransacciones" namespace="/operaciones" onclick="openDialogLoading()">Ajuste Masivo</s:a></li>
                            <li><s:a action="listarAjusteTransacciones" namespace="/operaciones" onclick="openDialogLoading()">Consultar Ajustes</s:a></li>
                            <li><s:a action="Traslados" namespace="/operaciones" onclick="openDialogLoading()">Traslados</s:a></li>
                            <li><s:a action="Sobregiros" namespace="/operaciones" onclick="openDialogLoading()">Sobregiros</s:a></li>
                            <!-- <li><s:a action="bloqueoDesbloqueo" namespace="/operaciones">Bloqueo/Desbloqueo</s:a></li> -->
                            <li><s:a action="bloqueo" namespace="/operaciones" onclick="openDialogLoading()">Bloqueo</s:a></li>
                            <li><s:a action="desbloqueo" namespace="/operaciones" onclick="openDialogLoading()">Desbloqueo</s:a></li>
                            <li><s:a action="renovacion" namespace="/operaciones" onclick="openDialogLoading()">Renovación</s:a></li>
                            <li><s:a action="consultarrenovacion" namespace="/operaciones" onclick="openDialogLoading()">Consultar Renovación</s:a></li>
                            <li><s:a action="ConsultaSaldo" namespace="/operaciones" onclick="openDialogLoading()">Consultar Saldo</s:a></li>


                            </ul>
                        </li>

                        <li><s:a action="Actualizaciones" namespace="/operaciones" onclick="openDialogLoading()">Actualizacion</s:a></li>

                        <li><s:a action="Afiliacion" namespace="/operaciones" onclick="openDialogLoading()">Afiliacion</s:a></li>

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
                        <li><s:a action="perfil" namespace="/inicio" onclick="openDialogLoading()">Ver Perfil</s:a></li>
                    <li><s:a action="Clave" namespace="/inicio" onclick="openDialogLoading()">Cambiar Clave</s:a></li>
                    <li><s:a action="LogoutAction" namespace="/inicio" onclick="openDialogLoading()">Salir</s:a></li>
                </ul>   
            </li>
        </ul>


    </div>
</div>