<%@page import="java.io.Console"%>
<%@ page import="com.gaon.ifou.v3.gowas" language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<jsp:useBean id="utilm" class="com.gaon.ifou.v3.trans_util_manager" scope="page" />
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="org.json.simple.parser.JSONParser"%>
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
	String sadd_date = utilm.setDefault(request.getParameter("sadd_date")).replaceAll("-", "");
	String eadd_date = utilm.setDefault(request.getParameter("eadd_date")).replaceAll("-", "");
	String sadd_recp = utilm.setDefault(request.getParameter("sadd_recp")).replaceAll("-", "");
	String eadd_recp = utilm.setDefault(request.getParameter("eadd_recp")).replaceAll("-", "");
	String appno = utilm.setDefault(request.getParameter("appno"));
	String pid = utilm.setDefault(request.getParameter("pid"));
	String pcd = utilm.setDefault(request.getParameter("pcd"));
	String depcd = utilm.setDefault(request.getParameter("depcd"));
	
	String auth01 = utilm.setDefault(request.getParameter("auth01"));
	String auth02 = utilm.setDefault(request.getParameter("auth02"));
	String auth03 = utilm.setDefault(request.getParameter("auth03"));
	
	String card01 = utilm.setDefault(request.getParameter("card01"));
	String card02 = utilm.setDefault(request.getParameter("card02"));
	String card03 = utilm.setDefault(request.getParameter("card03"));
	String card04 = utilm.setDefault(request.getParameter("card04"));
	String card05 = utilm.setDefault(request.getParameter("card05"));
	
	String npage = utilm.setDefault(request.getParameter("npage"));
	String pmode = utilm.setDefault(request.getParameter("pmode"));
	
	JSONObject rtncd = jbset.get_0107_cvs_cnt(tuser, stime, etime, sadd_date, eadd_date, sadd_recp, eadd_recp, appno, pid, pcd, depcd, auth01, auth02, auth03, card01, card02, card03, card04, card05, npage, pmode);
	out.println(rtncd);
/* 	JSONParser rtnParser = new JSONParser();
	JSONObject rtnJson = (JSONObject)rtnParser.parse(rtncd);
	out.println(rtnJson);	 */
%>