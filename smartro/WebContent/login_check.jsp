<%@ page language ="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	request.setCharacterEncoding("UTF-8");
	String uinfo = request.getParameter("uinfo");
	String umenu = request.getParameter("umenu");
	String utid = request.getParameter("utid");
	String udepo = request.getParameter("udepo");
	String uacq = request.getParameter("uacq");
	
	session.setAttribute("uinfo", uinfo);
	session.setAttribute("usermenu", umenu);
	session.setAttribute("usertid", utid);
	session.setAttribute("userdepo", udepo);
	session.setAttribute("useracq", uacq);

%>