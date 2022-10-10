package game.controller;

import java.io.IOException;
import java.util.Queue;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import game.repository.idgenerator.Node;
import game.repository.manager.RoomManager;
import game.repository.player.Player;

/**
 * Servlet implementation class jquery
 */
@WebServlet("/jquery/*")
public class jquery extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public jquery() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String pathInfo = request.getPathInfo();
		String[] paths=pathInfo.split("/");
		
		if(paths[1].equals("Pids")) {
			
			try{
				
				Player p = (Player) request.getSession().getAttribute("player");
				int rid = p.getRoom();
				Queue<Node> nodes = RoomManager.getInstance().getRoom(rid).getInfo().getPlayers();
				
				JSONArray arr = new JSONArray();
				
				while(!nodes.isEmpty()) {
					
					Node n = nodes.poll();
					
					JSONObject obj =new JSONObject();
					obj.put("pid", n.getId());
					obj.put("name", n.getName());
					arr.add(obj);
					
				}
				
				JSONObject o = new JSONObject();
				o.put("result", true);
				o.put("msg", arr);
				response.getWriter().print(o.toJSONString());
				
			}catch (Exception e) {
				// TODO: handle exception
				JSONObject o = new JSONObject();
				o.put("result", false);
				response.getWriter().print(o.toJSONString());
				System.out.println("접속하지 않은 플레이어의 요청");
			}
			
			
		}else if(paths[1].equals("MyPid")) {
			
			try {
				
				Player p = (Player) request.getSession().getAttribute("player");
				int pid = p.getPid();
				
				JSONObject o = new JSONObject();
				o.put("result", true);
				o.put("msg", pid);
				response.getWriter().print(o.toJSONString());
				
				
				
			}catch(Exception e) {
			
				// TODO: handle exception
				JSONObject o = new JSONObject();
				o.put("result", false);
				response.getWriter().print(o.toJSONString());
			}
			
			
			
			
		}else if(paths[1].equals("host")) {
			
			try {
				Player p = (Player) request.getSession().getAttribute("player");
				int pid = RoomManager.getInstance().getRoom(p.getRoom()).getInfo().getHostPid();
				JSONObject o = new JSONObject();
				o.put("result", true);
				o.put("msg", pid);
				response.getWriter().print(o.toJSONString());
				
				
			}catch(Exception e) {
				
				// TODO: handle exception
				JSONObject o = new JSONObject();
				o.put("result", false);
				response.getWriter().print(o.toJSONString());
				
			}
			
		}
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */

}
