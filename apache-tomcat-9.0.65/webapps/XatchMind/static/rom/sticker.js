function styleloader_view(){ // header onload = styleloader

    let sty = document.createElement("link");
    sty.href="/static/rom/roomview.css"; //css
    sty.rel="stylesheet";
    

    document.querySelector("head").appendChild(sty);

}



styleloader_view();