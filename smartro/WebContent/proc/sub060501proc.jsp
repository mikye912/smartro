<%@ page import="com.gaon.ifou.v3.gowas" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<jsp:useBean id="utilm" class="com.gaon.ifou.v3.trans_util_manager" scope="page" />
<%@ page import="java.util.*" %> 
<%@ page import="java.util.Base64.Encoder" %>
<%@ page import="java.util.Base64.Decoder" %>
<%
	String tuser = (String)session.getAttribute("uinfo");

	Decoder decoder = Base64.getDecoder();
	byte[] byte_tuser = decoder.decode(tuser);
	tuser = new String(byte_tuser, "UTF-8");
	
	request.setCharacterEncoding("UTF-8");

	//법인명
	String comnm = utilm.setDefault(request.getParameter("comnm"));
	//사업자번호
	String comno = utilm.setDefault(request.getParameter("comno"));
	//법인번호
	String comexno = utilm.setDefault(request.getParameter("comexno"));
	//대표자명
	String comceo = utilm.setDefault(request.getParameter("comceo"));
	//업태
	String cometype = utilm.setDefault(request.getParameter("cometype"));
	//종목
	String comservice = utilm.setDefault(request.getParameter("comservice"));
	//대표전화
	String comtel = utilm.setDefault(request.getParameter("comtel"));
	//주소
	String comaddr = utilm.setDefault(request.getParameter("comaddr"));
	//사이트메모
	String orgmemo = utilm.setDefault(request.getParameter("orgmemo"));
	
	int result = jbset.get_060501_item_upadte(tuser, comnm, comno, comexno, comceo, cometype, comservice, comtel, comaddr, orgmemo);
%>
<script>
	if(<%=result%> >= 0){
		alert("정보가 수정되었습니다.");
	} else {
		alert("오류가 발생했습니다.");
	}
	parent.orgTab.tabs("a1").reloadURL();
</script>