<%@ page import="com.gaon.ifou.v3.gowas" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<jsp:useBean id="utilm" class="com.gaon.ifou.v3.trans_util_manager" scope="page" />
<%@ page import="java.util.*" %> 
<%@ page import="java.util.Base64.Encoder" %>
<%@ page import="java.util.Base64.Decoder" %>
<%
	String tuser = (String)session.getAttribute("uinfo");

	Decoder decoder = Base64.getDecoder();
	byte[] byte_tuser = decoder.decode(tuser);
	tuser = new String(byte_tuser, "UTF-8");
	request.setCharacterEncoding("UTF-8");

	//tuser split
	String[] userexp = tuser.split(":");
	
	String dep_nm = utilm.setDefault(request.getParameter("dep_nm"));
	String dep_adm_user = utilm.setDefault(request.getParameter("dep_adm_user"));
	String dep_tel1 = utilm.setDefault(request.getParameter("dep_tel1"));
	String dep_email = utilm.setDefault(request.getParameter("dep_email"));
	String dep_type = utilm.setDefault(request.getParameter("dep_type"));
	
	int result = jbset.get_060502_deposit_insert(tuser, dep_nm, dep_adm_user, dep_tel1, dep_email, dep_type);	

	//2021.03.10 사업부 세션 재설정
	String dep_session = jbset.get_session_datareset(userexp[1], "", "depcd");
	session.removeAttribute("userdepo");
	session.setAttribute("userdepo", dep_session);
	
%>

<script>
	if(<%=result%> >= 0){
		alert("사업부가 등록되었습니다.");
	} else {
		alert("오류가 발생했습니다.");
	}
	parent.parent.orgTab.tabs("a2").reloadURL();
</script>