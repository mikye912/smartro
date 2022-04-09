<%@ page import="com.gaon.ifou.v3.gowas" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<jsp:useBean id="utilm" class="com.gaon.ifou.v3.trans_util_manager" scope="page" />
<%
	String orgcd=utilm.setDefault(request.getParameter("orgcd"));
	String mid=utilm.setDefault(request.getParameter("mid"));
	
	int result = jbset.get_060503_item_merData_delete(orgcd, mid);
%>
<script>
	if(<%=result%> >= 0 ){
		alert("삭제되었습니다.");
	} else {
		alert("오류가 발생했습니다.");
	}
	parent.ipopup_close();
	parent.parent.orgTab.tabs("a3").reloadURL();
</script>