
const Mypid = 0; //ajax요청으로 받아오기.

class drawer_for_client{
 
    constructor(canvas){

        this.ctx=canvas.getContext();//t
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



class drawer_for_host{

    constructor(canvas, sock){

        this.ctx = canvas.getContext();
        this.canvas = canvas;
        this.scok = sock;

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

        sock.send(
            JSON.stringify(
                {   h : 3,

                    pid : Mypid,

                    a: 0,

                    b : this.buffer,

                }
  
            )  
        );

        this.buffer =[];
    }

    set(){ //make event

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


    reset(){ //clear event

        this.canvas.getContext().clearRect(0,0,1000,1000);

        this.canvas.addEventListener('mousedown',()=>{
           
        });

        this.canvas.addEventListener('mousemove',()=>{
           
        });

        this.canvas.addEventListener('mouseup',()=>{
           
        });
    }
}





class Router{

    constructor(canvas){

        this.canvas = canvas;

        this.mode =false;
        this.stop =true;

        this.dfc = new drawer_for_client(this.canvas);
            
        this.dispatcher=[];
        this.dispatcher.shift([(d)=>this.onans(d)]);  //0
        this.dispatcher.shift([]);  //1
        this.dispatcher.shift(null);
        this.dispatcher.shift([(d)=>this.ondraw(d)]);//3
        this.dispatcher.shift(null);
        this.dispatcher.shift([(d)=>this.onchat(d)])//5

        this.dispatcher[1].shift(null);
        this.dispatcher[1].shift((d)=>this.onplay(d));
        this.dispatcher[1].shift((d)=>this.onstop(d));
        this.dispatcher[1].shift((d)=>this.onenter(d));
        this.dispatcher[1].shift(null);
        this.dispatcher[1].shift((d)=>this.onout(d));
        this.dispatcher[1].shift(null);
        this.dispatcher[1].shift((d)=>this.onhostchanged(d));
    }

    onans(d){
        if(d.pid!=-1){
            
            //정답칸에 메시지 표시
        }else{
            //정답 맞춘 사람 강조효과
        }
    };      
    
    onplay(d){this.stop=false;};    

    onstop(d){this.mode=false; this.stop=true;};

    onenter(d){};    // 들어온 사람 ajax로 조회해서 표시.

    onout(d){};      // 나간 사람 표시.

    onchat(d){};     // 채팅창에 채팅 표시

    ondraw(d){ //그림 표시

        
        if(this.mode||this.stop){return;}
        this.dfc.listen(d.data.b); 


    };     

    onhostchanged(d){ //호스트 바뀐 소식 알려주기.

        if(d.data.pid==Mypid){
            this.mode=true;
        }else{
            this.mode=false;
        }

    }
}

class SocketInterface{

    constructor(canvas){

        this.canvas= canvas;

        this.sock=new WebSocket();
        this.sock.onmessage=this.onmessage;
        this.sock.onopen=this.onopen;
        this.sock.onclose=this.onclose;

        
        
        this.dfh = new drawer_for_host(this.canvas, this.sock)
        
        this.router=new Router(this.canvas);

        this.mode=false; // client mode : false, host mode : true

    }


    eventMake(){

        this.onmessage=(e)=>{

            let header = e.data.h;
            let action = e.data.a? e.data.a : 0;

            this.router.dispatcher[header][action](e.data);

        }


        this.onclose=()=>{


        }

        this.onopen=()=>{


        }
        
    }


    getSock(){
        return this.sock;
    }

}