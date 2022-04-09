<%@ page import="com.gaon.ifou.v3.gowas" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<jsp:useBean id="utilm" class="com.gaon.ifou.v3.trans_util_manager" scope="page" />
<%@ page import="java.util.*" %> 
<%@ page import="java.util.Base64.Encoder" %>
<%@ page import="java.util.Base64.Decoder" %>
<%
	request.setCharacterEncoding("UTF-8");

	String tuser = (String)session.getAttribute("uinfo");
	Decoder decoder = Base64.getDecoder();
	byte[] byte_tuser = decoder.decode(tuser);
	tuser = new String(byte_tuser, "UTF-8");
	
	String seqno = utilm.setDefault(request.getParameter("rid"));
	String dpflag = utilm.setDefault(request.getParameter("dpflag"));
	
	int result = jbset.get_deposit_checkup(tuser, seqno, dpflag);

%>
<script>
	parent.totgrid_refresh();
	alert("매입 보류 완료");
	self.close();
</script>
