<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<jsp:useBean id="utilm" class="com.gaon.ifou.v3.trans_util_manager" scope="page" />
<%@ page import="java.util.*" %> 
<%@ page import="java.util.Base64.Encoder" %>
<%@ page import="java.util.Base64.Decoder" %>
<%
	String tuser =  utilm.setDefault(request.getParameter("uauth"));

	Decoder decoder = Base64.getDecoder();
	byte[] byte_tuser = decoder.decode(tuser);
	tuser = new String(byte_tuser, "UTF-8");
	
	String date = utilm.setDefault(request.getParameter("date"));
	String endddate = utilm.setDefault(request.getParameter("endddate"));

	//String rtn_json = jbset.get_0215_total(tuser, date, endddate);
	//out.print(rtn_json);
%>
