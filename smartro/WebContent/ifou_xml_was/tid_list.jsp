<%@ page import="com.gaon.ifou.v3.gowas" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<jsp:useBean id="utilm" class="com.gaon.ifou.v3.trans_util_manager" scope="page" />
<%
	String depcd = utilm.setDefault(request.getParameter("depcd"));
	String orgcd = utilm.setDefault(request.getParameter("orgcd"));
		
	//2021.03.05 강원대병원 v3 - 단말기관리 tab
	String tid_list = jbset.get_060502_item_tid_list(orgcd, depcd);
	out.println(tid_list);	
%>