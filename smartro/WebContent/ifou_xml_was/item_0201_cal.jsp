<%@ page import="com.gaon.ifou.v3.gowas" language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<jsp:useBean id="utilm" class="com.gaon.ifou.v3.trans_util_manager" scope="page" />
<%
	String tuser = utilm.setDefault(request.getParameter("uauth"));
	String year = utilm.setDefault(request.getParameter("SearchYear"));
	String month = utilm.setDefault(request.getParameter("SearchMon"));
	
	/*
		String rtncd = jbset.get_excel_0204(tuser, stime, etime, tid, appno, samt, eamt, mid, pid, tridx, depcd, casher, cardno, auth01, auth02, auth03);
		out.println(rtncd);	
	*/
	
%>