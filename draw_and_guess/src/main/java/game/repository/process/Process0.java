package game.repository.process;

import java.util.Queue;

import game.repository.idgenerator.Node;
import game.repository.manager.Room;

public class Process0 {

	private final Room room;
	
	private final String ans;
	
	private final int SCORE= 30;
	
	private int winnerPid=-1;
	
	public Process0(Room room, String ans) {
		
		this.room=room;
		this.ans=ans;
	}
	
	public boolean emit_0(String input, int pid) { //collect ans; and notify winner
		
		if(this.ans.equals(input)) {
			
			synchronized(this){
				
				if(this.winnerPid<0)
				
				this.winnerPid=pid;
				
				String msg = "{ h : 0, p : " + this.winnerPid + " , a : 0 , b : 0 }"; //0 means corrected answer
				
				sendAll(msg);
				
				room.getPlayer(pid).getInfo().plusScore(SCORE);
				
				return true;
			}
			
		}
		
		return false;
	}
	
	
	
	private void sendAll(String msg) {
		
		Queue<Node> q = room.getPlayers();
		
		while(!q.isEmpty()) {
			
			room.getPlayer(q.poll().getId()).getInfo().propMsg(msg);
			
		}
		
	}
	
	
}
