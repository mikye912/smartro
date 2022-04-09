<%@ page import="com.gaon.ifou.v3.gowas" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<jsp:useBean id="utilm" class="com.gaon.ifou.v3.trans_util_manager" scope="page" />
<%
	request.setCharacterEncoding("UTF-8");

	String orgcd = utilm.setDefault(request.getParameter("orgcd"));
	String depcd = utilm.setDefault(request.getParameter("depcd"));
	
	String dep_nm = utilm.setDefault(request.getParameter("dep_nm"));
	String dep_adm_user = utilm.setDefault(request.getParameter("dep_adm_user"));
	String dep_tel1 = utilm.setDefault(request.getParameter("dep_tel1"));
	String dep_email = utilm.setDefault(request.getParameter("dep_email"));
	String dep_type = utilm.setDefault(request.getParameter("dep_type"));
	
	int result = jbset.get_060502_item_deposit_update(dep_nm, dep_adm_user, dep_tel1, dep_email, dep_type, orgcd, depcd);

	//2021.03.10 사업부 세션 재설정
	String dep_session = jbset.get_session_datareset(orgcd, "", "depcd");
	session.removeAttribute("userdepo");
	session.setAttribute("userdepo", dep_session);
%>
<script>
	if(<%=result%> >= 0){
		alert("정보가 수정되었습니다.");
	} else {
		alert("오류가 발생했습니다.");
	}
	parent.ipopup_close();
	parent.parent.orgTab.tabs("a2").reloadURL();
</script>