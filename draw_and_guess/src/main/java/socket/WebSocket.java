package socket;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import game.repository.ActRouter.ActEntrance;
import game.repository.MsgRouter.MsgEntrance;
import game.repository.idgenerator.Info;
import game.repository.manager.Room;
import game.repository.manager.RoomManager;
import game.repository.player.Player;
import member.dto.MemberDTO;

//세션을 통하여 Player설정, OnOpen에 따라 MsgSubmit이 Interval하게 작동하도록 처리
//메시지 핸들러는 Msg라우터를 통하여 처리되게 한다.
//OnCLose에 따라 MsgSubmit을 하는 타이머 태스크가 동작을 멈추도록 한다

@ServerEndpoint(value = "/game.io", configurator = Config.class)
public class WebSocket {

	private Player player;
	private Room room;
	private ActEntrance actEnt;
	private Timer timer;
	private HttpSession hs;
	private int pid;
	
	@OnOpen
	public void handleOpen(Session session, EndpointConfig config) throws IOException {
		
		
		
		this.hs= (HttpSession) config.getUserProperties().get("SESSION");
		System.out.println(hs);

		if(hs==null || hs.getAttribute("player")==null)
		{	
			System.out.println("player attribute is null!!!");
			session.close();
			return;
		}
		if(!(((Player)hs.getAttribute("player")).getState())) {
			session.close();
			System.out.println("closing session");
			return;
		}
		
		this.player=(Player) hs.getAttribute("player");
		this.player.setSession(session);
		
		System.out.println("소켓접속!!");
		
		int rid = player.getRoom();
		
		Info<Room> info= RoomManager.getInstance().getRoom(rid);
		this.room=info.getInfo();
		this.player=(Player) hs.getAttribute("player");
		this.pid=this.player.getPid();
		this.actEnt=room.getAct();
		this.pid=this.player.getPid();
		
		
		this.timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				player.emitMsg();
				
			}
		};

		timer.schedule(task, 50, 1000);
		
		
	}
	
	@OnMessage
	public void handleMsg(String msg) {
		
		room.speak(msg, this.pid);
		
	}
	
	@OnClose
	public void handleClose() {
		
		
		if(timer!=null)
		timer.cancel();
		if(room!=null)
		room.out(player);

		hs.removeAttribute("player");
		
		System.out.println("플레이어 나감.");
		
		
	}
	
	@OnError
	public void handleError(Throwable throwable) {	// 나가기 버튼을 따로 구현하면 close나 error를 바꿀 수 있다.
		
		throwable.printStackTrace();
		
		if(timer!=null)
		timer.cancel();
		if(room!=null)
		room.out(player);
		
		hs.removeAttribute("player");
	}
	
	
}
