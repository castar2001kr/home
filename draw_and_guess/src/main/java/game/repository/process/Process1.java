package game.repository.process;

import java.util.Queue;

import game.repository.idgenerator.Node;
import game.repository.manager.Room;

public class Process1 {

	
	private final Room room;
	
	public Process1(Room room) {
		
		this.room = room;
	}
	
	public void emit_0() { //wait no use
		
		String msg = "{h : 1, p : -1, a : 0, b : 0 }";
		sendAll(msg);
		
		
	}
	
	public void emit_1() { //play
		
		room.play();
		String msg = "{h : 1, p : -1, a : 1, b : 0 }";
		sendAll(msg);
		
	}
	
	
	public void emit_2() { //stop game msg. if the client's sent this msg without notify of winner, it means that the game has been interrupted stop.
		
		room.stop();
		String msg = "{h : 1, p : -1, a : 2, b : 0 }";
		sendAll(msg);
		
	}
	
	private void sendAll(String msg) {
		
		Queue<Node> q = room.getPlayers();
		
		while(!q.isEmpty()) {
			
			room.getPlayer(q.poll().getId()).getInfo().propMsg(msg);
			
		}
		
	}
	
	
}
