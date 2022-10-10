package member.service;

import member.dao.MemberDAO;
import member.dto.MemberDTO;

public class MemberService {
	
	
	private static MemberService memberservice;
	
	private MemberDAO dao;
	
	private MemberService() {
		
		dao=MemberDAO.getInstance();
		
	}
	
	public static MemberService getInstance() {
		
		if(memberservice==null)
		memberservice=new MemberService();
		
		return memberservice;
		
	}
	
	
	public boolean regist(MemberDTO member) {
		
		return dao.save(member);
	}
	
	public boolean login(MemberDTO member) {
		
		try {
			
			MemberDTO ans = dao.find(member.getId());
			
			return ans.getId().equals(member.getId()) && ans.getPwd().equals(member.getPwd());
		}catch(Exception e) {
			return false;
		}
		
	}
	
	public MemberDTO search(MemberDTO member) {
		
		MemberDTO ans = dao.find(member.getId());
		
		return ans;
		
	}

}