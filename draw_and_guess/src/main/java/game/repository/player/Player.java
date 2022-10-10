package game.repository.player;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.json.simple.JSONArray;

public class Player {
	
	private Session session;
	
	private int rid;
	
	private int pid;
	
	private String name;
	
	private String id;
	
	private int lv;
	
	private int score;
	
	private Queue<String> msgQ = new LinkedList<String>();
	
	private boolean state = false;
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLv() {
		return lv;
	}

	public void setLv(int lv) {
		this.lv = lv;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	
	public int getPid() {
		return this.pid;
	}
	
	public void setPid(int pid) {
		this.pid=pid;
	}
	
	public boolean propMsg(String msg) {
		
		synchronized(msgQ) {
			
				msgQ.add(msg);
				return true;
			
		}
		
	}
	
	public void setState(boolean s) {
		
		this.state=s;
	}
	
	public boolean getState(){
		return this.state;
	}
	
	@SuppressWarnings("unchecked")
	public boolean emitMsg() {
		
		synchronized (this) {
			
			try {
				
				JSONArray arr= new JSONArray();

				while(!msgQ.isEmpty()) {
					
					arr.add((msgQ.poll()));
					
				}
				
				this.session.getBasicRemote().sendText(arr.toJSONString());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return false;
			}
			return true;
		}
		
	}
	
	
	public void plusScore(int s) {
		this.score+=s;
	}

	public void setRoom(int rid) {
		// TODO Auto-generated method stub
		this.rid=rid;
	}
	
	public int getRoom() {
		return this.rid;
	}

	public void setSession(Session hs) {
		// TODO Auto-generated method stub
		this.session=hs;
	}
	
}
