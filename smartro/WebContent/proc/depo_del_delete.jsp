<%@ page import="com.gaon.ifou.v3.gowas" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<jsp:useBean id="utilm" class="com.gaon.ifou.v3.trans_util_manager" scope="page" />
<%
	String orgcd = utilm.setDefault(request.getParameter("orgcd"));
	String depcd = utilm.setDefault(request.getParameter("depcd"));
	
	int result = jbset.get_060502_item_deposit_delete(orgcd, depcd);
	
	//2021.03.10 사업부 세션 재설정
	String dep_session = jbset.get_session_datareset(orgcd, "", "depcd");
	session.removeAttribute("userdepo");
	session.setAttribute("userdepo", dep_session);
	
	//2021.03.11 단말기 등록 시 세션 재등록
	String tid_session = jbset.get_session_datareset(orgcd, "", "tid");
	session.removeAttribute("usertid");
	session.setAttribute("usertid", tid_session);
%>
<script>
	if(<%=result%> >= 0){
		alert("사업부가 삭제되었습니다.");
	} else {
		alert("오류가 발생했습니다.");
	}
	parent.parent.orgTab.tabs("a2").reloadURL();
</script>