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

	String orgcd = utilm.setDefault(request.getParameter("orgcd"));
	String tidcd = utilm.setDefault(request.getParameter("tidcd"));
	
	String depcd = utilm.setDefault(request.getParameter("depcd"));
	String tidnm = utilm.setDefault(request.getParameter("tidnm"));
	String tid = utilm.setDefault(request.getParameter("tid"));
	String vangb = utilm.setDefault(request.getParameter("vangb"));
	String term_type = utilm.setDefault(request.getParameter("term_type"));
	
	int result = jbset.get_060503_item_tid_update(orgcd, tidcd, depcd, tidnm, tid, vangb, term_type);
%>
<script>
	if(<%=result%> >= 0){
		alert("단말기 정보가 수정되었습니다.");
	} else {
		alert("오류가 발생했습니다.");
	}
	parent.parent.orgTab.tabs("a4").reloadURL();
</script>