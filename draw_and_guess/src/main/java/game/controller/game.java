package game.controller;

import java.io.IOException;
import java.util.Queue;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import game.repository.idgenerator.Info;
import game.repository.idgenerator.Node;
import game.repository.manager.Room;
import game.repository.manager.RoomManager;
import game.repository.player.Player;
import member.dto.MemberDTO;

/**
 * Servlet implementation class game
 */
@WebServlet(urlPatterns= {"/game/*"})
public class game extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public game() {
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
		
		if(paths[1].equals("newroom")) {
			
			
			ServletContext context = this.getServletContext();
			RequestDispatcher dispatcher = context.getRequestDispatcher("/container/newroom.html");
			dispatcher.forward(request, response);
			return;
			
		}
		
		if(paths[1].equals("roomlist")) { //roomlist 요청
			
			Queue<Node> q = RoomManager.getInstance().getRooms();
			
			JSONArray jarray = new JSONArray();
			JSONObject result = new JSONObject();
			
			while(!q.isEmpty()) {
				
				JSONObject obj = new JSONObject();
				obj.put("id", q.peek().getId());
				obj.put("name", q.peek().getName());
				
				
				jarray.add(obj);
				q.poll();
				
			}
			
			result.put("result", jarray);
			
			response.getWriter().print(result.toJSONString());
		}else {
					// game/rid , roomname=?  // room 입장, 입장여부 확인
			try{
				
				String rn=(String) request.getParameter("title");
				
				MemberDTO dto=(MemberDTO) request.getSession().getAttribute("MemberDTO");
				
				int rid =Integer.parseInt(paths[1]);
				
				
				
				if(request.getSession().getAttribute("player")!=null) {
					
					if(((Player)(request.getSession().getAttribute("player"))).getState()) {
						
						JSONObject obj = new JSONObject();
						obj.put("result", false);
						obj.put("msg", "you already entered room!!");
						response.getWriter().print(obj.toJSONString());
						return;
						
					}
				}else {
					System.out.println("no session att");
					System.out.println("rn : "+rn);
					
					Player player  = new Player();
					player.setId(dto.getId());
					player.setLv(dto.getLv());
					player.setName(dto.getName());
					request.getSession().setAttribute("player", player);
					
				}
					
				
				Info<Room> rinfo = RoomManager.getInstance().getRoom(rid);
				boolean checkval = false;
				
				if(rinfo.getInfo().getTitle().equals(rn)) {
					
					checkval = rinfo.getInfo().enter((Player)request.getSession().getAttribute("player"));
					System.out.println("enter trial result : "+checkval);
						
				}
				
				JSONObject obj = new JSONObject();
				obj.put("result", checkval);
					
				response.getWriter().print(obj.toJSONString());
								
				
				
				
			}catch(Exception e) {
				
				response.getWriter().print("잘못된 요청...");
			}
			
			
		}
		
		
		
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String pathInfo=request.getPathInfo();
		String[] paths = pathInfo.split("/");
		
		
		if(paths[1].equals("roomlist")) {				//roomlist에 올리기. 방 생성.
			
			
			
			
			MemberDTO dto=(MemberDTO) request.getSession().getAttribute("MemberDTO");
			
			if(dto==null) {
				
				JSONObject obj = new JSONObject();
				obj.put("result", false);
				obj.put("msg", "you must login before make a room!!");
				response.getWriter().print(obj.toJSONString());
				return;
	
			}
			
			
			
			if(request.getSession().getAttribute("player")!=null) {
				
				if(((Player)(request.getSession().getAttribute("player"))).getState()) {
					
					JSONObject obj = new JSONObject();
					obj.put("result", false);
					obj.put("msg", "you already entered room!!");
					response.getWriter().print(obj.toJSONString());
					return;
					
				}
				
			}else {
				
				Player player  = new Player();
				player.setId(dto.getId());
				player.setLv(dto.getLv());
				player.setName(dto.getName());
				request.getSession().setAttribute("player", player);
				System.out.println("new player has been made");
				
			}

			try {
				
				String title = (String) request.getParameter("title"); // post title=?
				
				int rid = RoomManager.getInstance().makeRoom((Player)request.getSession().getAttribute("player"), title);
				
				
				JSONObject jobj=new JSONObject();
				
				jobj.put("result", true);
				
				
				response.getWriter().print(jobj.toJSONString());
				
				System.out.println(jobj.toJSONString());
				
				
				
				
			}catch (Exception e) {
				
				JSONObject jobj=new JSONObject();
				
				jobj.put("result", false);
				jobj.put("msg", "error");
				
				e.printStackTrace();
				
				
				response.getWriter().print(jobj.toJSONString());
				
				// TODO: handle exception
			}
		}
		
		
		
	}

}
