<?xml version="1.0" encoding="euc-kr"?>
<%@ page import="com.gaon.ifou.v3.gowas" language="java" contentType="text/xml; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<%
	String qry = request.getParameter("qry");

	String rtn_json = jbset.get_sql_select(qry);

	out.println(rtn_json);
%>