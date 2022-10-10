package game.repository.ActRouter;

import game.repository.manager.Room;
import game.repository.player.Player;

public class ActEntrance {
	
	
	private final Room room;
	
	private final Router012 r012;
	private final Router3 r3;
	private final Router5 r5;
	
	public ActEntrance(Room r) {
		
		this.room=r;
		this.r012=new Router012(this);
		this.r3=new Router3(this);
		this.r5=new Router5(this);
	}
	
	public Room getRoom() {
		
		return this.room;
	}
	
	public boolean enter(Player p) {
		
		return r012.enter(p);
	}
	
	public void out(Player p) {
		
		r012.out(p);
	}
	
	public void answer(String msg, int pid) {
		
		r012.answer(msg, pid);
		
	}
	
	public void play(Room room, String ans) {
		
		r012.play(ans);
	}
	
	
	public void chat(String msg) {
		
		r3.chat(msg);
	}
	
	public void draw(String msg, int pid) {
		
		r5.draw(msg, pid);
		
	}

}
