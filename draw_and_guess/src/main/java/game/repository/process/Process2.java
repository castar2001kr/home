package game.repository.process;

import java.util.Queue;

import game.repository.idgenerator.Node;
import game.repository.manager.Room;
import game.repository.player.Player;

public class Process2 { //process enter, out, changeHost msg
	
	private final Room room;
	
	public Process2(Room r) {
		
		this.room=r;
	}
	

	public boolean emit_0(Player p) { //enter
		
		boolean check = room.enter(p);
		
		if(!check) {
			return false;
		}
		
		int in = p.getPid();
		
		String msg = "{h : 1, p : "+in+", a : 3, b : 0 }"; //JSON msg
		
		Queue<Node> q=room.getPlayers();
		
		while(!q.isEmpty()) {
			
			int temp = q.poll().getId();
			
			room.getPlayer(temp).getInfo().propMsg(msg);
			
		}
		
		return true;
		
	}
	
	
	public boolean emit_1(Player p) { //out
		
		boolean hostChanged=room.out(p);
		
		int out = p.getPid();
		
		String msg = "{h : 1, p :"+ out +", a : 5, b : 0 }"; //JSON msg
		
		Queue<Node> q=room.getPlayers();
		
		while(!q.isEmpty()) {
			
			room.getPlayer(q.poll().getId()).getInfo().propMsg(msg);
		}
		
		if(hostChanged) {
			emit_2();
			return true;
		}
		
		return false;
	}
	
	
	private void emit_2() { //change host event's occured by host has been out from room
		
		String msg="{h:1, b : 0, a:2 ,pid : "+room.getHostPid()+"}"; //JSON msg. ajax로 조회해야 할 것들 : 자신의 PID, 전체 게임 참가자 리스트 <-> 들어온 사람 정보.
		
		Queue<Node> q = room.getPlayers();
		
		while(!q.isEmpty()) {
			
			room.getPlayer(q.poll().getId()).getInfo().propMsg(msg);
		}
		
	}
	
	
	

}
