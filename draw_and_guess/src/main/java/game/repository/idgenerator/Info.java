package game.repository.idgenerator;

public class Info<E> { //you must synchronized this object when you do I.O task with this!

	private boolean state;
	private E info;
	
	public boolean getState() {
		
			return state;
	
	}
	
	public void setState(boolean bool) {

			this.state=bool;
	
	}
	
	public E getInfo() {
		
		return this.info;
	}
	
	public void setInfo(E info) {
		
		synchronized(this) {
			
			this.info=info;
		}
		
	}
	
	
}
