<%@ page import="com.gaon.ifou.v3.gowas" language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<jsp:useBean id="utilm" class="com.gaon.ifou.v3.trans_util_manager" scope="page" />
<%@ page import="java.util.Base64.Encoder" %>
<%@ page import="java.util.Base64.Decoder" %>
<%
	String tuser = utilm.setDefault(request.getParameter("uauth"));

	String syear = utilm.setDefault(request.getParameter("syear"));
	String smon = utilm.setDefault(request.getParameter("smon"));
	smon = String.format("%02d", Integer.parseInt(smon));
	
	String acqcd = utilm.setDefault(request.getParameter("acqcd"));
	String mid = utilm.setDefault(request.getParameter("mid"));
	String tid = utilm.setDefault(request.getParameter("tid"));
	String depcd = utilm.setDefault(request.getParameter("depcd"));

	String rtnstr = jbset.get_0210_cal_total(tuser, syear, smon, acqcd, mid, tid, depcd);
	out.println(rtnstr);
%>