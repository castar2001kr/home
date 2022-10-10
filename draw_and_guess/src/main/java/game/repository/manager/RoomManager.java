package game.repository.manager;

import java.util.Queue;

import game.repository.idgenerator.IdGenerator;
import game.repository.idgenerator.Info;
import game.repository.idgenerator.Node;
import game.repository.player.Player;

public class RoomManager {

	private static final RoomManager manager=new RoomManager();

	private final IdGenerator<Room> idGen;
	private static final int SIZE = 100;
	
	
	
	
	private RoomManager() {
		
		this.idGen = new IdGenerator<Room>(SIZE);
		
	}
	
	public static RoomManager getInstance() {
		
		return manager;
	}
	
	public int makeRoom(Player p, String title) {
		
		if(p.getState()) {
			System.out.println(p.getState());
			return -1;
		}
		
		int rid = this.idGen.getID(title);
		
		if(rid<0) {
			return -1;
		}
		
		System.out.println("red : "+rid);

		Info<Room> info = new Info<Room>();
		
		synchronized(info){
			
			Room r = new Room(title,rid);
			info.setInfo(r);
			r.enter(p);
			this.idGen.set(rid,info);
			info.setState(true);
			return rid; 
			
		}
		
	}
	
	public void clearRoom(int rid) {
		
		idGen.erase(rid);
		
	}
	
	public Queue<Node> getRooms() {
		
		return this.idGen.getAll();
		
	}
	
	public Info<Room> getRoom(int rid){
		
		
		return this.idGen.get(rid);
		
	}
	
	
	
	
	
}
