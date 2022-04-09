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
	
	String stime = utilm.setDefault(request.getParameter("stime")).replace("-", "");
	String etime = utilm.setDefault(request.getParameter("etime")).replace("-", "");
	String samt = utilm.setDefault(request.getParameter("samt"));
	String eamt = utilm.setDefault(request.getParameter("eamt"));
	String tid = utilm.setDefault(request.getParameter("tid"));
	String depcd = utilm.setDefault(request.getParameter("depcd"));
	
	String rtn_json = jbset.get_0203_item(tuser, stime, etime, samt, eamt, tid, depcd);
	out.print(rtn_json);
%>