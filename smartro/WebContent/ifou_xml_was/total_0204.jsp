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
	
	String stime = utilm.setDefault(request.getParameter("stime")).replaceAll("-", "");
	String etime = utilm.setDefault(request.getParameter("etime")).replaceAll("-", "");
	String samt = utilm.setDefault(request.getParameter("samt"));
	String eamt = utilm.setDefault(request.getParameter("eamt"));
	String appno = utilm.setDefault(request.getParameter("appno"));
	String acqcd = utilm.setDefault(request.getParameter("acqcd"));
	String pid = utilm.setDefault(request.getParameter("pid"));
	String mediid = utilm.setDefault(request.getParameter("mediid"));
	String medi_cd = utilm.setDefault(request.getParameter("medi_cd"));
	String cardno = utilm.setDefault(request.getParameter("cardno"));
	String tid = utilm.setDefault(request.getParameter("tid"));
	String tradeidx = utilm.setDefault(request.getParameter("tradeidx"));
	String depcd = utilm.setDefault(request.getParameter("depcd"));
	
	String auth01 = utilm.setDefault(request.getParameter("auth01"));
	String auth02 = utilm.setDefault(request.getParameter("auth02"));
	String auth03 = utilm.setDefault(request.getParameter("auth03"));
	
	//String authchk = utilm.setDefault(request.getParameter("authchk"));
	String mid = utilm.setDefault(request.getParameter("mid"));
	
	String rtn_json = jbset.get_0204_total(tuser, stime, etime, samt, eamt, appno, acqcd, pid, mediid, medi_cd, cardno, tid, tradeidx, depcd, auth01, auth02, auth03, mid);
	out.print(rtn_json);
%>