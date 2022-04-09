<%@ page import="com.gaon.ifou.v3.gowas" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<jsp:useBean id="utilm" class="com.gaon.ifou.v3.trans_util_manager" scope="page" />
<%
	String orgcd = utilm.setDefault(request.getParameter("orgcd"));
	String mercd = utilm.setDefault(request.getParameter("mercd"));
	
	String depcd = utilm.setDefault(request.getParameter("depcd"));
	String mid = utilm.setDefault(request.getParameter("mid"));
	String purcd = utilm.setDefault(request.getParameter("purcd"));
	String merst = utilm.setDefault(request.getParameter("merst"));
	String meret = utilm.setDefault(request.getParameter("meret"));
	
	String fee01 = utilm.setDefault(request.getParameter("fee01"));
	String fee02 = utilm.setDefault(request.getParameter("fee02"));
	String fee03 = utilm.setDefault(request.getParameter("fee03"));
	
	int result = jbset.get_060503_item_merData_update(orgcd, mercd, depcd, mid, purcd, merst, meret, fee01, fee02, fee03);
%>
<script>
	if(<%=result%> >= 0){
		alert("정보가 수정되었습니다.");
	} else {
		alert("오류가 발생했습니다.");
	}
	parent.ipopup_close();
	parent.parent.orgTab.tabs("a3").reloadURL();
</script>