package game.repository.ActRouter;

import game.repository.manager.Room;
import game.repository.process.Process3;

public class Router5 {

	private final ActEntrance ent;
	private final Room room;
	
	private Process3 pro3;
	
	public Router5(ActEntrance ent) {
		
		this.ent=ent;
		this.room=ent.getRoom();
		pro3=new Process3(room);
	}
	
	public void draw(String msg, int pid) { //formated JSON msg
		
		if(room.getHostPid()==pid) {
			
			pro3.emit_0(msg);
			
		}
		
	}
	
	
}
