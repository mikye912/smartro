<%@ page import="com.gaon.ifou.v3.gowas" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<jsp:useBean id="utilm" class="com.gaon.ifou.v3.trans_util_manager" scope="page" />
<%

	String orgcd = utilm.setDefault(request.getParameter("orgcd"));
	String depcd = utilm.setDefault(request.getParameter("depcd"));
	String memcd = utilm.setDefault(request.getParameter("memcd"));
	
	String mem_id = utilm.setDefault(request.getParameter("mem_id"));
	String mem_pw = utilm.setDefault(request.getParameter("mem_pw"));
	String mem_nm = utilm.setDefault(request.getParameter("mem_nm"));
	String memlv = utilm.setDefault(request.getParameter("memlv"));
	String mem_tel1 = utilm.setDefault(request.getParameter("mem_tel1"));
	String mem_tel2 = utilm.setDefault(request.getParameter("mem_tel2"));
	String mem_email = utilm.setDefault(request.getParameter("mem_email"));
	
	int result = jbset.get_060505_item_userInfo_update(orgcd, depcd, memcd, mem_id, mem_pw, mem_nm, memlv, mem_tel1, mem_tel2, mem_email);
%>
<script>
	if(<%=result%> >= 0){
		alert("정보가 수정되었습니다.");
	} else {
		alert("오류가 발생했습니다.");
	}
	parent.ipopup_close();
	parent.parent.orgTab.tabs("a5").reloadURL();
</script>