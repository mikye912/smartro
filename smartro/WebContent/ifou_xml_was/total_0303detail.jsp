<%@ page import="com.gaon.ifou.v3.gowas" language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
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
	
	String stime = utilm.setDefault(request.getParameter("stime")).replaceAll("-", "");
	String etime = utilm.setDefault(request.getParameter("etime")).replaceAll("-", "");
	String sreqdd = utilm.setDefault(request.getParameter("sreqdd")).replaceAll("-", "");
	String ereqdd = utilm.setDefault(request.getParameter("ereqdd")).replaceAll("-", "");
	String sexpdd = utilm.setDefault(request.getParameter("sexpdd")).replaceAll("-", "");
	String eexpdd = utilm.setDefault(request.getParameter("eexpdd")).replaceAll("-", "");
	
	String depcd = utilm.setDefault(request.getParameter("depcd"));
	String appno = utilm.setDefault(request.getParameter("appno"));
	String tid = utilm.setDefault(request.getParameter("tid"));
	String mid = utilm.setDefault(request.getParameter("mid"));
	
	//String rtncd = jbset.get_0303detail_item(tuser, stime, etime, sreqdd, ereqdd, sexpdd, eexpdd, depcd, appno, tid, mid);
	//out.println(rtncd);
%>