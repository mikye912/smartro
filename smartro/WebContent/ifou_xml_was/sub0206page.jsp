<%@page import="java.io.Console"%>
 <%@ page import="java.net.*, java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="com.gaon.ifou.v3.gowas" language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<jsp:useBean id="utilm" class="com.gaon.ifou.v3.trans_util_manager" scope="page" />
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="java.util.Base64.Encoder" %>
<%@ page import="java.util.Base64.Decoder" %>
<%
	String tuser =  utilm.setDefault(request.getParameter("uauth"));

	Decoder decoder = Base64.getDecoder();
	byte[] byte_tuser = decoder.decode(tuser);
	tuser = new String(byte_tuser, "UTF-8");

	String stime = utilm.setDefault(request.getParameter("stime")).replaceAll("-", "");
	String etime = utilm.setDefault(request.getParameter("etime")).replaceAll("-", "");
	String samt = utilm.setDefault(request.getParameter("samt"));
	String eamt = utilm.setDefault(request.getParameter("eamt"));
	String appno = utilm.setDefault(request.getParameter("appno"));
	String cardtp = utilm.setDefault(request.getParameter("cardtp"));
	
	String auth01 = utilm.setDefault(request.getParameter("auth01"));
	String auth02 = utilm.setDefault(request.getParameter("auth02"));
	String auth03 = utilm.setDefault(request.getParameter("auth03"));
	
	String can01 = utilm.setDefault(request.getParameter("can01"));
	String can02 = utilm.setDefault(request.getParameter("can02"));
	String can03 = utilm.setDefault(request.getParameter("can03"));
	
	String mid = utilm.setDefault(request.getParameter("mid"));
	String tid = utilm.setDefault(request.getParameter("tid"));
	String acqcd = utilm.setDefault(request.getParameter("acqcd"));
	String tid2 = utilm.setDefault(request.getParameter("tid2"));
	
	String npage = utilm.setDefault(request.getParameter("npage"));
	String pmode = utilm.setDefault(request.getParameter("pmode"));
	
	JSONObject rtncd = jbset.get_0204_cvs_cnt(tuser, stime, etime, samt, eamt, appno, cardtp, auth01, auth02, auth03, can01, can02, can03, mid, tid, acqcd, tid2, npage, pmode);
	out.println(rtncd);
	//out.println(rtncd.size());
	
	//out.println(rtncd.get("apn_page"));
%>
