/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
function prehide(){ 
if (document.getElementById){ 
document.getElementById('preload').style.visibility='hidden';
document.getElementById('preload').style.display='none';
} 
} 
function preshow(){ 
if (document.getElementById){ 
document.getElementById('preload').style.visibility='visible'
document.getElementById('preload').style.display='block';
} 
} 

