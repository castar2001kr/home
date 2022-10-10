



function getCookie(name){

    let val  = document.cookie.match('(^\;)?'+ name + '=([^;]*)(;|$)');
    return val? val[2] : null;

}

function deleteCookie(name){
	document.cookie = name+"=; expires=Thu, 01 Jan 1980 00:00:01 GMT;";
}


let name1 = getCookie("name");
let id = getCookie("id");


$(document).ready(
function(){

if(id){


    $.ajax("/index/navbar.html")
    .done(function(e){
         let refind = document.createElement("div");
         refind = $(refind).html(e).find('#loged');
         
        
       	
         $("body").prepend(refind);
         $("#id").text(id);
         $("#name").text(name1);
       	
       	$("#out").click(()=>{
       	
       		$.ajax("/logout").done(function(e){
       		
       			deleteCookie("name");
       			deleteCookie("id");
       			location.href="/";

                
       			
       		}
       	);
      	  
    });
})



}else{


	$.ajax("/logout");

    $.ajax("/index/navbar.html")
    .done(function(e){
       
       	 let refind = document.createElement("div");
         refind = $(refind).html(e).find('#logout');
         $("body").prepend(refind);
       	 
       	
    });
    
}


}
)

