/* 
 * Commons.js
 * Métodos básicos en Javascript
 */
function goBack() {
    //window.history.back()
    window.history.go(-1);
}

function preshow() {
    $('#wait').css('display', 'block');
}

function prehide() {
    $('#wait').css('display', 'none');
}

/*
 * Muestra/Oculta un elemento html
 */
function showHide(idElemento){      
    if( $(idElemento).is(":visible") ){
        $(idElemento).hide(1000);
    }else{
        $(idElemento).show(1000);
    }
}


function solicitarParametros(idTexto,event) {
    
    var query = document.getElementById(idTexto).value;

    queryFinal = '';
    begin = false;
    end = false;
    pos = 0;
    parametro = '';
    valor = '';
    cancelar = false;

    for(x = 0; x<query.length;x++) {           
        if(begin) {
            if(query.charAt(x)=='$') {
                begin = false;
                valor += prompt("Parametro: "+parametro, "");
                if(valor == 'null')
                {
                   cancelar = true;
                   break;
                }
                queryFinal += valor;
                pos++;
                parametro = '';
                valor = '';
            }else{
                parametro += query.charAt(x);
            }
        }
        else{
            if(query.charAt(x)=='$') {
                begin = true;
            }
            else{
                queryFinal += query.charAt(x);
                //alert('position: '+x+',position: '+query.charAt(x)+',consulta: '+queryFinal);
            }
        }
    } 
    if(!cancelar) {
        //alert('consulta: '+queryFinal);
        document.getElementById('form_queryExecute').value = queryFinal
    } else {
        ie8SafePreventEvent(event);
        prehide();
    }
    
                
                
}


function validarCambioClave(form,event) {
    
    var newPassword = new String(form.newpw.value);
    
    colorearValidos(form);
    
    if (form.newpw.value == form.newpwcf.value) {
               
        //validate the length
        if ( newPassword.length < 8  || newPassword.length > 15) {             
            alert("La clave nueva debe tener mínimo 8 caracteres y máximo 15 caracteres");
            //event.preventDefault();
            //ie8SafePreventEvent(event);
            return false;
        } else {            
            //validate letter
            if ( newPassword.match(/[a-z]/) ) {
                //validate capital letter
                if ( newPassword.match(/[A-Z]/) ) {
                    //validate number                                 
                    //if (!newPassword.match(/((\w|[!@#$%])*\d(\w|[!@#$%])*\d(\w|[!@#$%])*\d(\w|[!@#\$%])*\d(\w|[!@#$%])*(\d)*)/) && newPassword.match(/\d{1}/) ) {
                    if (newPassword.split(/[0-9]/).length - 1 >= 1 && newPassword.split(/[0-9]/).length - 1 <= 3){
                    //validate consecutivo
                        if (!newPassword.match(/(.)\1{2,}/) && (newPassword.length > 0) ) {
                            //validate especial
                            if ( newPassword.match(/([!@\*\-\?¡¿+\/.,_#])/ )) {

                                getMD5('Clave_claveActual');
                                getMD5('Clave_newpw');
                                getMD5('Clave_newpwcf');
                                
                                //form.submit();
                                return true;
                            } else {
                                alert("La clave nueva debe contener al menos un caracter especial(ej:!@?+-.,#)");
                                //event.preventDefault();
                                //ie8SafePreventEvent(event);
                                return false;
                            }                           
                        } else {
                            alert("La clave nueva no debe contener más de 3 caracteres iguales consecutivos");
                            //event.preventDefault();
                            //ie8SafePreventEvent(event);
                            return false;
                        }                       
                    } else {
                        alert("La clave nueva debe tener de 1 a 3 números");
                        //event.preventDefault();
                        //ie8SafePreventEvent(event);
                        return false;
                    }   
                } else {
                    alert("La clave nueva debe tener al menos una letra mayúscula");
                    //event.preventDefault();
                    //ie8SafePreventEvent(event);
                    return false;
                }              
            } else {
                alert("La clave nueva debe tener al menos una letra");
                //event.preventDefault();
                //ie8SafePreventEvent(event);
                return false;
            }       
        }      
    } else {
        alert("La clave nueva no coincide con la confirmación");
        //event.preventDefault();
        //ie8SafePreventEvent(event);
        return false;
    }
}


function ie8SafePreventEvent(e) {
    e.preventDefault ? e.preventDefault() : e.returnValue = false;  
}

function colorearValidos(form) {
    var newPassword = new String(form.newpw.value);
    
    if( newPassword.match(/[a-z]/) ) {
        $('#tip6').css('color', 'green');
        $('#tip6_icon').attr('src', '../recursos/icons/ok.png');
    }else{
        $('#tip6').css('color', 'red');
        $('#tip6_icon').attr('src', '../recursos/icons/danger.png');
    }
    
    if( newPassword.match(/[A-Z]/) ) {
        $('#tip5').css('color', 'green');
        $('#tip5_icon').attr('src', '../recursos/icons/ok.png');
    }else{
        $('#tip5').css('color', 'red');
        $('#tip5_icon').attr('src', '../recursos/icons/danger.png');
    }
    
    //if(!newPassword.match(/((\w|[!@#$%])*\d(\w|[!@#$%])*\d(\w|[!@#$%])*\d(\w|[!@#\$%])*\d(\w|[!@#$%])*(\d)*)/) && newPassword.match(/\d{1}/) ) {
    if(newPassword.split(/[0-9]/).length - 1 >= 1 && newPassword.split(/[0-9]/).length - 1 <= 3){
        $('#tip4').css('color', 'green');
        $('#tip4_icon').attr('src', '../recursos/icons/ok.png');
    }else{
        $('#tip4').css('color', 'red');
        $('#tip4_icon').attr('src', '../recursos/icons/danger.png');
    }
    
    if(!newPassword.match(/(.)\1{2,}/) && (newPassword.length > 0)) {
        $('#tip3').css('color', 'green');
        $('#tip3_icon').attr('src', '../recursos/icons/ok.png');
    }else{
        $('#tip3').css('color', 'red');
        $('#tip3_icon').attr('src', '../recursos/icons/danger.png');
    }
    
    if( newPassword.match(/([!@\*\-\?¡¿+\/.,_#])/ )) {
        $('#tip2').css('color', 'green');
        $('#tip2_icon').attr('src', '../recursos/icons/ok.png');
    }else{
        $('#tip2').css('color', 'red');
        $('#tip2_icon').attr('src', '../recursos/icons/danger.png');
    }
    
    if( newPassword.length < 8  || newPassword.length > 15) {
        $('#tip1').css('color', 'red');
        $('#tip1_icon').attr('src', '../recursos/icons/danger.png');
    }else{
        $('#tip1').css('color', 'green');
        $('#tip1_icon').attr('src', '../recursos/icons/ok.png');
    }
}

function confirmBox(msj) {
    var answer;
    answer = window.confirm(msj);
    if (answer == true) {
       return true;
    } else {
       return false;
    }
}