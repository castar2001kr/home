package game.repository.manager;

import java.util.Queue;
import java.util.Stack;

import game.repository.ActRouter.ActEntrance;
import game.repository.MsgRouter.MsgEntrance;
import game.repository.idgenerator.IdGenerator;
import game.repository.idgenerator.Info;
import game.repository.idgenerator.Node;
import game.repository.player.Player;
import member.dto.MemberDTO;

public class Room {
	
	private final int rid;
	private final IdGenerator<Player> idGen;
	private static final int SIZE = 6;
	
	private final String title;
	private int hostPid;
	
	ActEntrance actEnt;
	MsgEntrance msgEnt;
	
	public String getTitle() {
		return this.title;
	}
	
	public boolean cleared = false;
	
	
	public Room(String title, int rid){ // make new room
		
		idGen=new IdGenerator<Player>(SIZE);
		this.title=title;
		this.hostPid=0;
		this.rid=rid;
		actEnt=new ActEntrance(this);
		msgEnt=new MsgEntrance(actEnt);
	}
	
	synchronized public boolean enter(Player p) { 	
		
		int pid = idGen.getID(p.getName());
		
		if(p.getState()) { // 10.06
			return false;
		}
		
		if(pid<0||this.cleared) {
			return false;
		}
		
		p.setPid(pid);
		p.setState(true); // 10.06
		p.setRoom(rid);
		
		Info<Player> info=new Info<Player>();

		synchronized(info) {
			
			info.setInfo(p);
			
			idGen.set(pid,info);
			
			info.setState(true);
			
			return true;
		}
		
		
		
	}
	
	
	synchronized public boolean out(Player p) {
	
		idGen.erase(p.getPid());
		
		if(p.getPid()==this.getHostPid()) {
			
			this.stop();
			
			Queue<Node> q = this.idGen.getAll();
			
			while(!q.isEmpty()) {
				
				int temp = (int) q.poll().getId();
				
				if(this.idGen.get(temp)==null||this.idGen.get(temp).getState()==false) {
					
					
				}else {
					
					this.setHostPid(temp);
					return true;
					
				}
			}
			
			// clear room info
			RoomManager.getInstance().clearRoom(rid);
			this.cleared=true;
			return false;
		}
			
		return false;
	}
	
	public Queue<Node> getPlayers() {
		
		return this.idGen.getAll();
		
	}
	
	public Info<Player> getPlayer(int pid){
		
		return this.idGen.get(pid);
	}
	
	
	public int getHostPid() {
		
		return hostPid;
	}
	
	synchronized public void setHostPid(int pid) {
		
		this.hostPid=pid;
		
	}
	
	synchronized public void play() {
		
		RoomManager.getInstance().getRoom(rid).setState(false);
		this.idGen.lock();
		
	}
	
	synchronized public void stop() {
		
		RoomManager.getInstance().getRoom(rid).setState(true);
		this.idGen.unlock();
	}
	
	
	
	
	public void speak(String msg, int pid) { //send msg to router
		
		this.msgEnt.saveMsg(msg, pid);
		
	}
	
	public ActEntrance getAct() {
		
		return this.actEnt;
	}
	

}
