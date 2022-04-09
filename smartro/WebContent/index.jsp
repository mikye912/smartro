<%@ page import="com.gaon.ifou.v3.gowas" language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<%

//String sdate = (String)session.getAttribute("sdate");

String sdate = request.getParameter("sdate");
out.print(jbset.get_icvan(sdate));
%>

