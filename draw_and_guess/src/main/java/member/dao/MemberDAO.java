package member.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import member.dto.MemberDTO;

public class MemberDAO {

	static private MemberDAO SingleTonDAO;
	
	
	private Connection con;
	
	private MemberDAO() {
		
		String url="jdbc:oracle:thin:@localhost:1521/system";
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			System.out.println("connecting...");
			
			con =DriverManager.getConnection(url,"system","7735");
			
			con.setAutoCommit(false);
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	public static MemberDAO getInstance() {
		
		if(SingleTonDAO==null) {
			
			SingleTonDAO=new MemberDAO();
		}
		
		return SingleTonDAO;
	}
	
	public static boolean save(MemberDTO member) {
		
		MemberDAO dao=getInstance();
		
		PreparedStatement stmt =null;
		
		String sql="INSERT INTO MEMBER (id,password,name,lv,email) VALUES(?,?,?,?,?)";
		
		try {
			
			
			if(find(member.getId())!=null) {
				return false;
			}
			
			stmt = dao.con.prepareStatement(sql);
			stmt.setString(1, member.getId());
			stmt.setString(2, member.getPwd());
			stmt.setString(3, member.getName());
			stmt.setInt(4, member.getLv());
			stmt.setString(5, member.getEmail());
			
			
			
			if(stmt.executeUpdate()!=0) {
				
				dao.con.commit();
				
				return true;
				
			}else {
				return false;
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			if(stmt!=null) {
				
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
		
		return false;
		
	}
	
	public static MemberDTO find(String id) {
		
		ResultSet rs= null;
		
		MemberDAO dao=getInstance();
		
		PreparedStatement stmt =null;
		
		String sql="SELECT * FROM MEMBER WHERE id=?";
		
		try {
			
			stmt = dao.con.prepareStatement(sql);
			stmt.setString(1, id);
			
			
			rs= stmt.executeQuery();
			
			if(rs.next()) {
				
				MemberDTO temp = new MemberDTO();
				
				temp.setId(rs.getString("id"));
				temp.setPwd(rs.getString("password"));
				temp.setName(rs.getString("name"));
				
				return temp;
				
			}else {
				
				return null;
			}
			
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			if(stmt!=null) {
				
				try {
					
					rs.close();
					stmt.close();
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
		
		return null;
		
	
	}
	
	
	
}