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
	
	String depcd = request.getParameter("depcd");
	String memid = request.getParameter("memid");
	String mempw = request.getParameter("mempw");
	String memnm = request.getParameter("memnm");
	String memlv = request.getParameter("memlv");
	String memtel1 = request.getParameter("memtel1");
	String memtel2 = request.getParameter("memtel2");
	String mememail = request.getParameter("mememail");
	
	int result = jbset.get_060505_item_userInfo_insert(tuser, depcd, memid, mempw, memnm, memlv, memtel1, memtel2, mememail);
%>
<script>
	if(<%=result%> == 1){
		alert("사용자 등록이 완료되었습니다.");
	} else if(<%=result%> == 2) {
		alert("이미 등록된 사용자 입니다.");
	} else {
		alert("오류가 발생했습니다.");
	}
	parent.parent.orgTab.tabs("a5").reloadURL();
	window.location.reload();
</script>