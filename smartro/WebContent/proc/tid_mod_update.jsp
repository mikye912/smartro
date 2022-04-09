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
	String depcd = utilm.setDefault(request.getParameter("depcd"));
	String tid[] = request.getParameterValues("tid");
	
	int result = jbset.get_060502_item_tid_list_update(orgcd, depcd, tid, tuser);
	
	//2021.03.11 단말기 등록 시 세션 재등록
	String tid_session = jbset.get_session_datareset(orgcd, "", "tid");
	session.removeAttribute("usertid");
	session.setAttribute("usertid", tid_session);
%>
<script>
	if(<%=result%> >= 0){
		alert("정보가 수정되었습니다.");
	} else {
		alert("오류가 발생했습니다.");
	}
	parent.ipopup_close();
</script>