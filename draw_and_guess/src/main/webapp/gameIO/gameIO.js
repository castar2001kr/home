let interface:
let url = location.href;
url=url.split('/');
url="ws:"+"//"+url[2]+"/game.io";
console.log(url)

const Hostpid = [false];

const Mypid = [false]; //ajax요청으로 받아오기.

let players = [];

let profiles = [];




function emitter_setting(sock){ //채팅창, 정답창, 플레이버튼 등등 세팅.
    playbtn_setting(sock);
    chatbtn_setting(sock);
    ansbtn_setting(sock);
}

function playbtn_setting(sock){

    $(".playbtn").click(()=>{

      let an =  $(".anns").text(); $(".anns").text("");
    
      let obj = {
        h:1,a:0,b:an,pid:Mypid[0],
      }

      sock.send(JSON.stringify(obj));

    });

}

function chatbtn_setting(sock){

    $("#chatemit").click(()=>{
        let ch = $("#chat").text(); $("#chat").text("");

        let obj = {
            h:5,a:0,b:ch,pid:Mypid[0],
          }

        sock.send(JSON.stringify(obj));

    })
}

function ansbtn_setting(sock){

    $("#ansemit").click(()=>{
        let an = $("#ans").text(); $("#ans").text("");

        let obj = {
            h:0,a:1,b:an,pid:Mypid[0],
          }

        sock.send(JSON.stringify(obj));

    })

}


// <textarea class="anns"></textarea>
// <button class="playbtn"> 시작 버튼</button>


function host_menu_visible(){
    $(".playbtn").css('visibility','visible');
    $(".anns").css('visibility','visible');
}

function host_menu_unvisible(){
    $(".playbtn").css('visibility','hidden');
    $(".anns").css('visibility','hidden');
}

async function refresh(){

    await getMypids();
    await getpids();
    await gethost();
    if(Hostpid[0]==Mypid[0]){
        host_menu_visible();

        interface.dfh.reset();

    }else{
        interface.dfh.reset();
    }

}


function getMypids(){ //내 pid 받아오기

    return new Promise(function(resolve,reject){
        
        $.ajax("/jquery/MyPid").done(
            (e)=>{
                e=JSON.parse(e);
                console.log(e);
                Mypid[0]=e.msg;
                resolve(true);
    
            }
        )
    })
}

function getpids(){ //플레이어들 pid, name 불러오기

   

    return new Promise(function(resolve,reject){
        
        $.ajax("/jquery/Pids").done(
            (e)=>{
                e=JSON.parse(e);
                console.log(e);
                players=e.msg;
                resolve(true);
                
            }
        )
    })

}

function gethost(){
    

    return new Promise(function(resolve,reject){
        
        $.ajax("/jquery/host").done(
            (e)=>{
                e=JSON.parse(e);
                console.log(e);
                Hostpid[0]=e.msg;
                resolve(true);
            }
        )
    })
    
}



function resetprofiles(){ //플레이어 사진 등록

    profiles.forEach((e)=>{$(e).css('visibility','hidden');})

    players.forEach((e)=>{
        console.log(e)
        $(profiles[e.pid]).find(".name").text(e.name);
        $(profiles[e.pid]).find(".avatar").html("<img src='/gameIO/profile_ex.jpg' alt='아바타사진'>");
        $(profiles[e.pid]).css('visibility','visible');
    })


}






class drawer_for_client{
 
    constructor(canvas){

        this.ctx=canvas.getContext("2d");//t
        this.bool = false;


        this.working= false;
        this.buffer = [];

        this.funcs = [this.dot, this.line_start, this. line_to, this.line_end];
    }

    listen(e){ // e.data.b = array 형태여야하며, 각 원소는 {x: , y: , delay: } 형태

        let arr = e.data.b;

        while(arr[0]!=null){
            this.buffer.push(arr.shift());
        }

        if(!this.working){

            this.order();
        }
    }


    dot(x,y){
        this.ctx.fillRect(x,y,2,2);
    }


    line_start(x,y){
        
        if(!this.bool){

            this.dot(x,y);
            this.ctx.beginPath();
            this.ctx.moveTo(x,y);
            this.ctx.lineTo(x,y);
        }

    }

    line_to(x,y){
        
        if(this.bool){
            this.ctx.lineTo(x,y);
            this.ctx.stroke();
        }
    }

    line_end(x,y){

        if(this.bool){
            this.bool=!this.bool;
        }
    }

    order(){
        if(!this.working){
            this.do();
        }

    }

    do(){ // 재귀적 호출
        
        let that = this;

        if(this.buffer[0]!=null){

            let e=buffer.shift();
            
            setTimeout(()=>{

                that.funcs[e.type](e.x,e.y);
                that.do();

            },e.delay);



        }else{
            this.working=false;
        }

    }

}



class drawer_for_host{

    constructor(canvas, sock){

        this.ctx = canvas.getContext("2d");
        this.canvas = canvas;
        this.sock = sock;

        this.buffer = [];

        this.BeforeTime = 0;
        this.started = false;

        this.bool = false;

        let that = this;

        this.set();

    }

    getT(){
        return (new Date()).getMilliseconds();
    }

    timeStamp(type,x,y){

        let delay=0;
        let now = this.getT();

        if(this.started){

            delay=Math.min(30,Math.abs(now-this.BeforeTime))
        }
        
        this.BeforeTime = now;
        
        return {
            type : type,
            x : x,
            y : y,
            delay : delay,
        }
    }

    dot(e){
        this.ctx.fillRect(e.offsetX,e.offsetY,2,2);
    }

    down(e){

        if(!this.bool){

            this.dot(e);
            this.ctx.beginPath();
            this.ctx.moveTo(e.offsetX,e.offsetY);
            this.ctx.lineTo(e.offsetX,e.offsetY);
            this.buffer.push(this.timeStamp(1,e.offsetX,e.offsetY))
        
            this.bool = !this.bool
        }
    }

    move(e){

        if(this.bool){

            this.ctx.beginPath();
            this.ctx.moveTo(e.offsetX,e.offsetY);
            this.ctx.lineTo(e.offsetX,e.offsetY)
            this.buffer.push(this.timeStamp(1,e.offsetX,e.offsetY))
            if(this.buffer.length>30){
                this.flush();
            }
        }

    }

    up(e){
        
        if(this.bool){
            this.ctx.beginPath();
            this.ctx.moveTo(e.offsetX,e.offsetY);
            this.ctx.lineTo(e.offsetX,e.offsetY);
            this.buffer.push(this.timeStamp(1,e.offsetX,e.offsetY));
            this.flush();
        }

    }

    flush(){

        this.sock.send(
            JSON.stringify(
                {   h : 3,

                    pid : Mypid[0],

                    a: 0,

                    b : this.buffer,

                }
  
            )  
        );

        this.buffer =[];
    }

    idown(e){}
    imove(e){}
    iup(e){}

    set(){ //make event

        let that = this;

        this.canvas.addEventListener('mousedown',(e)=>{
            ()=>that.idown(e);
        });

        this.canvas.addEventListener('mousemove',(e)=>{
            that.imove(e);
        });

        this.canvas.addEventListener('mouseup',(e)=>{
            that.iup(e);
        });
    }


    reset(pp){ //clear event

        
        this.canvas.getContext("2d").clearRect(0,0,1000,1000);
        
        if(!pp){
            return;
        }
        
        if(Hostpid[0]==Mypid[0]){
            this.idown=this.down;
            this.iup=this.up;
            this.imove=this.move;
        }

    }
}





class Router{

    constructor(canvas){

        this.canvas = canvas;

        this.mode =Mypid[0]==Hostpid[0];
        this.stop =true;

        this.dfc = new drawer_for_client(this.canvas);
            
        this.dispatcher=[];
        this.dispatcher.push([(d)=>this.onans(d)]);  //0
        this.dispatcher.push([]);  //1
        this.dispatcher.push([]);
        this.dispatcher.push([(d)=>this.ondraw(d)]);//3
        this.dispatcher.push([]);
        this.dispatcher.push([(d)=>this.onchat(d)])//5

        this.dispatcher[1].push([]);
        this.dispatcher[1].push((d)=>this.onplay(d));
        this.dispatcher[1].push((d)=>this.onstop(d));
        this.dispatcher[1].push((d)=>this.onenter(d));
        this.dispatcher[1].push(null);
        this.dispatcher[1].push((d)=>this.onout(d));
        this.dispatcher[1].push(null);
        this.dispatcher[1].push((d)=>this.onhostchanged(d));
    }

    onans(d){
        if(d.a==0){
            //정답 맞춘 사람 메시지 칸에 표시
            
           let n = players[d.pid].name;
           $(".chatlog").append("<p>"+n+"님이 정답을 맞췄습니다."+"</p>");
            

        }else if(d.a==1){
            //정답칸에 메시지 표시
            profiles[d.pid].find(".anslog")
            .append("<p>"+players[d.pid].name +" : "+d.b+"</p>"); 
        }
    };      
    
    onplay(d){this.stop=false; interface.dfh.reset(true)};    

    onstop(d){this.mode=false; this.stop=true;
        
        this.canvas.width=this.canvas.width; //setter를 이용한 초기화
        
    };

    onenter(d){
        refresh();
    };    // 들어온 사람 ajax로 조회해서 표시.

    onout(d){

        refresh();
    };      // 나간 사람 표시.

    onchat(d){

        $(".chatlog").append("<p>"+players[d.pid].name +" : "+d.b+"</p>");
    };     // 채팅창에 채팅 표시

    ondraw(d){ //그림 표시

        
        if(this.mode||this.stop){return;}
        this.dfc.listen(d.data.b); 


    };     

    onhostchanged(d){ //호스트 바뀐 소식 알려주기.

        Hostpid[0]=d.pid;

        if(d.pid==Mypid[0]){
            this.mode=true;
        }else{
            this.mode=false;
        }
        refresh();

    }
}

class SocketInterface{

    constructor(canvas){

        this.canvas= canvas;

        this.eventMake();

        this.onopen;
        this.onmessage;
        this.onclose;

        console.log(this.onopen);

        this.sock=new WebSocket(url);

        this.sock.onopen =this.onopen;

        this.sock.onmessage= this.onmessage;

        this.sock.onclose = ()=>{
            alert("서버와 접속 끊김...")
        }

        
        this.dfh = new drawer_for_host(this.canvas, this.sock);
        
        this.router=new Router(this.canvas);

        
        

        this.mode=false; // client mode : false, host mode : true

    }


    eventMake(){

        this.onmessage=(e)=>{
            try{
                if(e==null){
                    return;
                }

                e = JSON.parse(e);

                let header = e.data.h;
                let action = e.data.a? e.data.a : 0;
    
                this.router.dispatcher[header][action](e.data); //h,a변수에 따라서 router에 적절하게 보내줌.

            }catch(msg){
                console.log(msg)
                console.log(e);
            }

           

        }


        this.onclose=()=>{


        }

        this.onopen=()=>{

            alert("게임시작");
            emitter_setting(this.sock);
        }
        
    }


    getSock(){
        return this.sock;
    }

}

function make_msg_event(sock){

    $("#chatemit").click(
        ()=>{
            
            let txt = $("#chat").text();
            $("#chat").text("");
            let jsonmsg={
    
                h:0, p:Mypid[0], a:0, b:txt,
            }

            sock.send(JSON.stringify(jsonmsg));
    
        })

}

function make_ans_event(sock){

    $("#ansemit").click(

        ()=>{
            let txt = $("#ans").text();
            $("#ans").text("");
            let jsonmsg={
                h:3,p:Mypid[0], a:1, b:txt,
            }

            sock.send(JSON.stringify(jsonmsg));
        }

    )

}


document.querySelectorAll(".profile").forEach((e)=>{profiles.push(e)});

refresh();

let CANVAS = document.querySelector(".canvas1");
console.log(CANVAS)
interface = new SocketInterface(CANVAS);
