package game.repository.idgenerator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;


public class IdGenerator<E> {
	
	private final int size;
	
	private final Info<E>[] infoSet;
	
	private final Queue<Integer> bucket;
	
	private final Node firstNode;
	
	private Node lastNode;
	
	private final Map<Integer,Node> nodeMap;
	
	private boolean lock_regist = false;
	
	
	@SuppressWarnings("unchecked")
	public IdGenerator(int size){
	
			this.size=size;

			infoSet=(Info<E>[]) new Info[size];
			
			for(int i=0; i<size; i++) {
				infoSet[i]=new Info<E>();
			}
			
			this.firstNode=new Node();
			
			this.lastNode=firstNode;
			
			this.nodeMap = new HashMap<Integer,Node>();
			
			bucket=new LinkedList<Integer>();
			
			for(int i=0; i<this.size; i++) {
				
				
				bucket.add(i);
			}
		
		
		
	}
	
	synchronized public void lock() {
		
		this.lock_regist=true;
		
	}
	
	synchronized public void unlock() {
		
		this.lock_regist=false;
		
	}
	
	
	public int getID() {
		
		if(this.lock_regist) {
			return -1;
		}
		
		Object tmp;
		
		synchronized(bucket) {
			
			tmp = bucket.poll();
			
		}
			if(tmp==null) {
				return -1;
			}
			
			int temp=(int)tmp;
			
			Node n = new Node();
			
			n.setId(temp);
			
			synchronized(nodeMap) {
				
				n.setBefore(lastNode);
				
				lastNode.setAfter(n);
				
				nodeMap.put(temp, n); //must be synchronized 
				
				lastNode=n;
				
			}
			
			return temp;
	
	}
	
	public int getID(String name) {
			
			if(this.lock_regist) {
				return -1;
			}
			
			Object tmp;
			
			synchronized(bucket) {
				
				tmp = bucket.poll();
				
			}
				if(tmp==null) {
					return -1;
				}
				
				int temp=(int)tmp;
				
				Node n = new Node();
				
				n.setId(temp);
				
				n.setName(name);
				
				synchronized(nodeMap) { 
					
					n.setBefore(lastNode);
					
					lastNode.setAfter(n);
					
					nodeMap.put(temp, n); //must be synchronized 
					
					lastNode=n;
					
				}
				
				return temp;
		
		}
		
	
	public boolean erase(int id) {
		
		synchronized(infoSet[id]) {
			
			synchronized(nodeMap) {
				
				if(nodeMap.containsKey(id)) {
					
					Node n = nodeMap.get(id);
					
					if(n.equals(lastNode)) {
						
						lastNode=n.before;	
					}
					
					n.erase();
					
					infoSet[id].setState(false);
					
					return true;
					
				}
			}
			
			
			
			return false;
		}
		
	}
	
	public Queue<Node> getAll() {
			
			Queue<Node> result=new LinkedList<>();
			
			if(lastNode.equals(firstNode)) {
				
				if(lastNode==null) {
					
					return result;
				}
			}
			
			Node cur = this.firstNode.after;
			
			while(cur!=null) {
				
				result.add(cur);
				cur=cur.after;
			}
			
			
			return result;
		}
	
	
	public boolean set(int id, Info<E> info) {
		
		synchronized(infoSet[id]) {
	
				infoSet[id]=info;
				
				return true;
	
		}
		
		
	}
	
	public Info<E> get(int id) {
		
		synchronized(infoSet[id]) {
			
			if(infoSet[id].getState()) {
				
				return infoSet[id];
			}
			
			return null;
		}
		
	}

		
		
	
	
	
	

}
