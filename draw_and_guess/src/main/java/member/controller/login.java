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
 * Servlet implementation class login
 */
@WebServlet("/login")
public class login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
		if(request.getSession().getAttribute("MemberDTO")!=null) {
			System.out.println("중복로그인시도 감지");
			return;
		}
		
		MemberDTO m = new MemberDTO();
		
		m.setId((String) request.getParameter("id"));
		m.setPwd((String) request.getParameter("pw"));
		
		boolean result = MemberService.getInstance().login(m);
		
		if(result) {
			 m = MemberService.getInstance().search(m);
		}
		
		JSONObject obj = new JSONObject();
		
		obj.put("result", result);
		obj.put("name", m.getName());
		obj.put("id", m.getId());
		
		response.getWriter().print(obj.toJSONString());
		
		m=MemberService.getInstance().search(m);
		
		request.getSession().setAttribute("MemberDTO", m);
		MemberDTO x = (MemberDTO) request.getSession().getAttribute("MemberDTO");
		
		if(x!=null)
		System.out.println(x.getId());
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		ServletContext context = this.getServletContext();
		RequestDispatcher dispatcher = context.getRequestDispatcher("/WEB-INF/member/login.html");
		dispatcher.forward(request, response);
	}

}
