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
	
	String depcd = utilm.setDefault(request.getParameter("depcd"));
	String purcd = utilm.setDefault(request.getParameter("purcd"));
	String mid = utilm.setDefault(request.getParameter("mid"));	
	String merst = utilm.setDefault(request.getParameter("merst"));
	String meret = utilm.setDefault(request.getParameter("meret"));
	String van = utilm.setDefault(request.getParameter("van"));
	String fee01 = utilm.setDefault(request.getParameter("fee01"));
	String fee02 = utilm.setDefault(request.getParameter("fee02"));
	String fee03 = utilm.setDefault(request.getParameter("fee03"));
	
	int result = jbset.get_060503_item_merData_insert(tuser, depcd, purcd, mid, merst, meret, van, fee01, fee02, fee03);
%>
<script>
	if(<%=result%> == 1){
		alert("가맹점번호가 등록되었습니다.");
	} else if(<%=result%> == 2) {
		alert("이미 등록된 가맹점 번호입니다.");
	} else {
		alert("오류가 발생했습니다.");
	}
	parent.parent.orgTab.tabs("a3").reloadURL();
	//window.location.reload();
</script>