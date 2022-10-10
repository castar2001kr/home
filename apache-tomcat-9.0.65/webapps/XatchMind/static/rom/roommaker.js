

  

function callroom(roomview){ //roomview를 받아서 방 목록 갱신

    const roomHttp = new XMLHttpRequest();

    roomHttp.onreadystatechange = function(){

        if(this.status == 200 && this.readyState==this.DONE){

            let rooms = JSON.parse(roomHttp.responseText);
            
            roomview.innerHTML="";

            rooms.array.forEach(room => {

                roomview.appendChild(createroom(room));

            });
            
        }

    }

    

}

function createroom(room){
    
    let container= document.createElement("div");
    
    let title = document.createElement("span");
    let personnel = document.createElement("span");
    let btn  = document.createElement("button");

    title.innerText=room.title;
    personnel.innerText = room.personnel;
    btn.innerText = "입장";

    makeEvent(room.id,btn);

    return container;

}

function makeEvent(id,btn){

    btn.addEventListener('click',

      ()=>{
        location.href="?"+id; //입장 버튼을 누르면 링크를 걸어서 옮겨줄 위치
      }

    )

}