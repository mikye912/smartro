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
	String tid = request.getParameter("tid");
	String term_nm = request.getParameter("term_nm");
	String term_type = request.getParameter("term_type");
	String vangb = request.getParameter("vangb");
	
	int result = jbset.get_060504_item_insert(tuser, depcd, tid, term_nm, term_type, vangb);
%>
<script>
	if(<%=result%> == 1){
		alert("단말기번호가 등록되었습니다.");
	} else if(<%=result%> == 2) {
		alert("이미 등록된 단말기 번호입니다.");
	} else {
		alert("오류가 발생했습니다.");
	}
	parent.parent.orgTab.tabs("a4").reloadURL();
	window.location.reload();
</script>