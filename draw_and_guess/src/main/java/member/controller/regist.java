package member.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import member.dto.MemberDTO;
import member.service.MemberService;

/**
 * Servlet implementation class regist
 */
@WebServlet("/regist")
public class regist extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public regist() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		MemberDTO m = new MemberDTO();
		
		m.setId((String) request.getParameter("id"));
		m.setPwd((String) request.getParameter("pw"));
		m.setName((String) request.getParameter("name"));
		m.setEmail((String)request.getParameter("email"));
		m.setLv(0);
		
		boolean result = MemberService.getInstance().regist(m);
		
		JSONObject obj = new JSONObject();
		
		obj.put("result", result);
		response.getWriter().print(obj.toJSONString());
		
		System.out.println("regist trial result : "+result);
		
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		ServletContext context = this.getServletContext();
		RequestDispatcher dispatcher = context.getRequestDispatcher("/WEB-INF/member/regist.html");
		dispatcher.forward(request, response);
	}

}
