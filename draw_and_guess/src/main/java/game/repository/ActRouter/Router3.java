package game.repository.ActRouter;

import game.repository.manager.Room;
import game.repository.player.Player;
import game.repository.process.Process0;
import game.repository.process.Process1;
import game.repository.process.Process2;
import game.repository.process.Process3;

public class Router3 {

	private final ActEntrance ent;
	
	private final Room room;
	
	private Process3 pro3;
	
	
	public Router3(ActEntrance ent) {
		
		this.ent=ent;
		this.room=ent.getRoom();
		this.pro3=new Process3(room);
		
	}
	
	public void chat(String msg) { // fomated JSON msg. you have to check pid at that time msg entered msg router with JSON_parser
		
		pro3.emit_0(msg);
		
	}
	
	
}
