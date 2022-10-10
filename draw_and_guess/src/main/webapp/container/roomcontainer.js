
let re;

let rooms = [];



function refajax(){
	
    $.ajax("/game/roomlist")
    .done((data)=>{
    
    rooms=[];
    

    $(".roomlist").text("");
    
    re=data;
    console.log(data);
        JSON.parse(data).result.forEach((e) => {
            
            let box=document.createElement("div");
            box.className="room";

            let title = document.createElement("span");
            title.innerText = e.name;
            title.className = "tit"

            let rid = document.createElement("span");
            rid.className = "iid"
            rid.innerText = e.id;
            
            let btn = document.createElement("button");
            btn.innerText = "입장"

            $(box).append(title).append(rid).append(btn);

            $(".roomlist").append(box);
            rooms.push(btn);
        });


        rooms.forEach((e)=>{
            $(e).click(()=>{
               let num = $(e).parent().find(".iid").text()-0;
               let tit = $(e).parent().find(".tit").text();

               $.ajax("/game/"+num+"?title="+tit).done((data)=>{

                data=JSON.parse(data);

                if(data.result){
                    alert("방에 입장합니다.")

                    location.href = "/enter"

                }else{
                    alert("입장 불가. 로그아웃중이거나 게임진행 중임.")
                }

            })

            })
        })


    })

}


function makajax(){

    location.href="/game/newroom";

}


$.ajax("/container/roomcontainer.html")
.done(
    (e)=>{
        let refind = document.createElement("div");
         refind = $(refind).html(e).find(".containerofview");
		$(".con").html(refind);


        $(".refresh").click(()=>{refajax();});
        $(".make").click(()=>{makajax()});


    }

)