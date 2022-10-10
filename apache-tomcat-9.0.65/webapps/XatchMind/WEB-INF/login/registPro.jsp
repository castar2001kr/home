<%@ page import="Member.MemberService"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>

<% request.setCharacterEncoding("UTF-8"); %>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
</head>
<body>


	<jsp:useBean id="member" class="Member.MemberDTO" scope="page" />
	<jsp:setProperty property="id" name="member" />
	<jsp:setProperty property="pwd" name="member" />
	<jsp:setProperty property="email" name="member" />
	<jsp:setProperty property="name" name="member" />


	<%
	
		member.setLevel(1);
		MemberService service=MemberService.getInstance();
		
		boolean result = service.regist(member);
	
		if(result){
			
			%>

	회원 가입 성공

	<%
		}else{ %>

	회원 가입 실패...
	<%
		}
	%>




</body>
</html>