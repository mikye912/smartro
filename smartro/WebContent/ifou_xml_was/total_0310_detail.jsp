<%@ page import="com.gaon.ifou.v3.gowas" language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<jsp:useBean id="utilm" class="com.gaon.ifou.v3.trans_util_manager" scope="page" />
<%@ page import="java.util.*" %> 
<%@ page import="java.util.Base64.Encoder" %>
<%@ page import="java.util.Base64.Decoder" %>
<%
	String tuser = utilm.setDefault(request.getParameter("uauth"));
	
	Decoder decoder = Base64.getDecoder();
	byte[] byte_tuser = decoder.decode(tuser);
	tuser = new String(byte_tuser, "UTF-8");
	
	String reqstime = utilm.setDefault(request.getParameter("reqstime")).replaceAll("-", "");
	String reqetime = utilm.setDefault(request.getParameter("reqetime")).replaceAll("-", "");
	String samt = utilm.setDefault(request.getParameter("samt"));
	String eamt = utilm.setDefault(request.getParameter("eamt"));
	String appno = utilm.setDefault(request.getParameter("appno"));
	String pid = utilm.setDefault(request.getParameter("pid"));
	String tradeidx = utilm.setDefault(request.getParameter("tradeidx"));
	String deposeq = utilm.setDefault(request.getParameter("deposeq"));
	
	String tstat01 = utilm.setDefault(request.getParameter("tstat01"));
	String tstat02 = utilm.setDefault(request.getParameter("tstat02"));
	String tstat03 = utilm.setDefault(request.getParameter("tstat03"));
	String tstat04 = utilm.setDefault(request.getParameter("tstat04"));
	
	String auth01 = utilm.setDefault(request.getParameter("auth01"));
	String auth02 = utilm.setDefault(request.getParameter("auth02"));
	String auth03 = utilm.setDefault(request.getParameter("auth03"));
	
	String mid = utilm.setDefault(request.getParameter("mid"));
	String tid = utilm.setDefault(request.getParameter("tid"));
	String acqcd = utilm.setDefault(request.getParameter("acqcd"));
	String depcd = utilm.setDefault(request.getParameter("depcd"));
	
	String rtn_json = jbset.get_0310_detail_total(tuser, reqstime, reqetime, samt, eamt, appno, pid, tradeidx, acqcd, tid, deposeq, depcd, auth01, auth02, auth03, tstat01, tstat02, tstat03, tstat04, mid);
	out.print(rtn_json);
%>