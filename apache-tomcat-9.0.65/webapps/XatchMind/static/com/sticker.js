//script src = "here";

function styleloader(){ // header onload = styleloader

    let sty = document.createElement("link");
    sty.href="/static/com/footbar.css"; //css
    sty.rel="stylesheet";
    
    
    let sty2 = document.createElement("link");
    sty2.href="/static/com/navbar.css"; //css
    sty2.rel="stylesheet";

    document.querySelector("head").appendChild(sty);
    document.querySelector("head").appendChild(sty2);

}



styleloader();