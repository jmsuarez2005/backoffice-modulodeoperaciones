/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 * Autor: Jonathan Rojas Lara
 */


/*
 * Agrega a la ventana un evento 'scroll'. Si el scroll está posicionado al principio de la página
 * despliega la navegación; para todo lo demás, esconde la navegación.
 */
$(window).bind('scroll', function() {
    var docElement = $(document)[0].documentElement;
    var winElement = $(window)[0];
         
    if($(window).scrollTop() === 0 || $(window).scrollTop() === $(document).height()- $(window).height()) {
        // do something
        displayNav();
    } 
    else{
        hideNav();
    }
    
    if ((docElement.scrollHeight - winElement.innerHeight) === winElement.pageYOffset) {
        hideNav();
    }
});

/*
 * Muestra el elemento de navegación.
 */
function displayNav(){
    document.getElementById('nav').setAttribute('style', 'display:block;');
//    $('#nav').show(1500);
}

/*
 * Oculta la navegación. Si está posicionado al inicio de la página, dejarla desplegada.
 */
function hideNav(){

    if ($(window).scrollTop() === 0 )
        displayNav();
    else
        document.getElementById('nav').setAttribute('style', 'display:none;');
//        $('#nav').hide(1500);
        
}