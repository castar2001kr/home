package game.repository.ActRouter;

import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import game.repository.manager.Room;
import game.repository.player.Player;
import game.repository.process.Process0;
import game.repository.process.Process1;
import game.repository.process.Process2;

public class Router012 {

	private final ActEntrance ent;
	
	private final Room room;
	
	private Process0 pro0;
	private Process1 pro1;
	private Process2 pro2;
	
	private Timer timer;
	
	private boolean pstate = false;
	
	public Router012(ActEntrance ent) {
		
		this.ent=ent;
		this.room=ent.getRoom();
		
		this.pro1=new Process1(room);
		this.pro2=new Process2(room);
		
	}
	
	synchronized public void play(String ans) { // it can be occured by only host
		
		if(!pstate) {//play; 
			this.pro0=new Process0(this.room, ans); //make Process0 : collecting answers
			pstate=true;
			pro1.emit_1();
			
			timer = new Timer();
			TimerTask task = new TimerTask() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					pro1.emit_2();
				}
			};
			
			timer.schedule(task, 1000*60);
			
		}
	}
	
	
	synchronized public void answer(String msg, int pid) {
		
		if(pstate) {
			
			JSONParser parser = new JSONParser();
			JSONObject obj;
			try {
				obj = (JSONObject) parser.parse(msg);
				
			} catch (ParseException e) {
				
				return;
			}
			
			long action= (long)(obj.get("a"));
			
			if(action!=1) {
				return;
			}
			
					
			String ans = (String) obj.get("b");
			
			boolean corrected = pro0.emit_0(ans, pid); //player corrected the answer
			
			if(corrected) {
				
				pro1.emit_2(); //stop;
				this.pstate=false;
				
				if(timer!=null) {
					timer.cancel();
					timer=null;
				}
				
			}
			
		}
		
	}
	
	synchronized public boolean enter(Player p) {
		
		if(pro2.emit_0(p)) {
			return true;
		}
		return false;
	}
	
	synchronized public void out(Player p) {
		
		if(pro2.emit_1(p)) {
			
			
			pro1.emit_1(); //stop
			this.pstate=false;
			
			if(timer!=null) {
				timer.cancel();
				timer=null;
			}
			
		}
		
	}
	
	
	
}
