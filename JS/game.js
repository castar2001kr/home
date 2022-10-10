
class SocketInterface{

    constructor(){

        this.sock=new WebSocket();
        this.sock.onmessage=this.onmessage;
        this.sock.onopen=this.onopen;
        this.sock.onclose=this.onclose;

    }

    onmessage(){}

    onopen(){}

    onclose(){}

    set(obj){

        if(obj.listen!=null){

            this.onmessage=(e)=>{

                if(e.data.header.type==1){ //0 : client->host //1 : wait //2 : sw-host //3 : sw-client// 5 : ready-state// 7 : host message // 8 : 정답
    
                    wait(); // must be implemented
                    return;
                }

                if(e.data.header.type==7){

                    hostmessage(e);
                    return;
                }

                if(e.data.header.type==8){

                    answer_post(e);
                    return;
                }

                obj.listen(e);
            }
        }

        this.onclose=()=>{


        }

        this.onopen=()=>{


        }
        
    }

    reset(){

        this.onmessage=function(e){

            if(e.data.header.type==1){
    
                wait(); // must be implemented
                return;
            }

        };
        this.onclose=function(){};
    }

    getSock(){
        return this.sock;
    }

}

class drawer_for_client{
 
    constructor(canvas){

        this.ctx=canvas.getContext();//t
        this.bool = false;


        this.working= false;
        this.buffer = [];

        this.funcs = [this.dot, this.line_start, this. line_to, this.line_end];
    }

    listen(e){ // e.data.body = array 형태여야하며, 각 원소는 {x: , y: , delay: } 형태

        let arr = e.data.body;

        while(arr[0]!=null){
            this.buffer.push(arr.shift());
        }

        this.order();
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



        }

    }

}

const INTERFACE = new SocketInterface();

const WEBSOCK = INTERFACE.getSock();

const CONTAINER = document.querySelector('container');

let drawer = null;

function switch_to_client(){

    INTERFACE.reset();

    CONTAINER.innerHTML="";

    let canv=document.createElement('canvas');
    canv.className='canvas';

    CONTAINER.appendChild(canv);
    drawer = new drawer_for_client(canv);

    INTERFACE.set(drawer);

    WEBSOCK.send(

        {header : {type : 5}}
    );

}



class drawer_for_host{

    constructor(canvas){
        this.ctx = canvas.getContext();
        this.canvas = canvas;
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

    dot(x,y){
        this.ctx.fillRect(x,y,2,2);
    }

    down(e){

        if(!this.bool){

            this.dot(x,y);
            this.ctx.beginPath();
            this.ctx.moveTo(x,y);
            this.ctx.lineTo(x,y);
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

        WEBSOCK.send(
            JSON.stringify(
                {   header : {type : 0},

                    body : this.buffer,

                }
  
            )  
        );

        this.buffer =[];
    }

    set(){

        let that = this;

        this.canvas.addEventListener('mousedown',()=>{
            that.down();
        });

        this.canvas.addEventListener('mousemove',()=>{
            that.move();
        });

        this.canvas.addEventListener('mouseup',()=>{
            that.up();
        });
    }
}


function switch_to_host(){

    INTERFACE.reset();

    CONTAINER.innerHTML="";

    let canv=document.createElement('canvas');
    canv.className='canvas';

    CONTAINER.appendChild(canv);
    drawer = new drawer_for_host(canv);

    INTERFACE.set(drawer);

    WEBSOCK.send(

        {header : {type : 5}}
    );

}

function wait(){

    WEBSOCK.onmessage=(e)=>{

        if(e.data.header.type==0){
        
            switch_to_client();
            return;
        }

        if(e.data.type==1){
            switch_to_host();
            return;
        }

        if(e.data.type==2){
            switch_to_client();
            return;
        }

    }

    WEBSOCK.send(

        {header : {type : 5}}
    );

}

