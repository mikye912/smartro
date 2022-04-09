<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.*, java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="java.lang.String" %>
<%@ page import="java.security.*" %>
<%@ page import="java.util.Base64.Encoder" %>
<%@ page import="java.util.Base64.Decoder" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<%
	Decoder decoder = Base64.getDecoder();
	
	String tuser = (String)session.getAttribute("uinfo");
	
	String tmp_tid = (String)session.getAttribute("usertid");
	byte[] usertid_buf = decoder.decode(tmp_tid);
	String usertid = new String(usertid_buf, "UTF-8");
	
	String tmp_depo = (String)session.getAttribute("userdepo");
	byte[] userdepo_buf = decoder.decode(tmp_depo);
	String userdepo = new String(userdepo_buf, "UTF-8");

	Date nowTime = new Date();
	SimpleDateFormat sf = new SimpleDateFormat("yyyy년 MM월 dd일 a hh:mm:ss");
	SimpleDateFormat setdate = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat sdate = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat edate = new SimpleDateFormat("yyyyMMdd");
%>

<!DOCTYPE html>
<html>
<head>
	<title>Init from script</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" type="text/css" href="./dhtmlx/codebase/dhtmlx.css"/>
	<link rel="stylesheet" type="text/css" href="./dhtmlx/dhtmlxGrid/codebase/skins/dhtmlxgrid_dhx_web.css"/>
	<link type="text/css" rel="stylesheet" href="./include/css/style.css"  media="all" >
	<script src="./dhtmlx/codebase/dhtmlx.js"></script>
	<script src="./include/js/jquery-1.8.1.min.js" type="text/javascript"></script>
	<script src="./include/js/common.js"></script>
    <script src="./include/js/dhtmlxgrid_export.js"></script>

	<link type="text/css" rel="stylesheet" href="./include/css/jquery-ui-1.10.3.jdh.css" />
	<script src="./include/js/jquery-1.9.1.js"></script>
	<script src="./include/js/jquery-ui-1.10.3.custom.js"></script>
	<script>
		function regi_form(){
			var f = document.memreg;
			
			if($('#dep_nm').val()==""){
				alert("사업부명을 입력하여 주십시오.");
				return false;
			}

			if($('#dep_adm_user').val()==""){
				alert("사업부 담당자를 입력하여 주십시오.");
				return false;
			}

			f.submit();
		}

		function reset_form(){
			var f = document.memreg;
			f.reset();
		}
	</script>
</head>
<body>
<FORM name="memreg" method="post" action="./proc/sub060502proc.jsp">
<table width='100%' class='tb00_none'>
	<tr height="42">
		<td width="120">■ 사업부명</td>
		<td>
			<input type="text" name="dep_nm" id="dep_nm" value=""  class="searchbox_large">
		</td>
		<td width="120">■ 사업부담당자</td>
		<td>
			<input type="text" name="dep_adm_user" id="dep_adm_user" value=""  class="searchbox_large">
		</td>
		<td width="120">■ 전화번호</td>
		<td>
			<input type="text" name="dep_tel1" value=""  class="searchbox_large">
		</td>
		<td width="120"></td>
		<td>
		</td>
		<td width="120"></td>
		<td>
		</td>
		<td align="right" style="padding-right:4px;">
			<span class="button large icon" onclick="regi_form();"><span class="add"></span><a href="#">신규등록</a></span>
		</td>
	</tr>
	<tr height="1"><td colspan="11" bgcolor="#c0c0c0"></td></tr>
	<tr height="42">
		<td width="120">■ 이메일</td>
		<td>
			<input type="text" name="dep_email" id="dep_email" value="" class="searchbox_large">
		</td>
		<td width="120">■ 매입방식</td>
		<td>
			<select name="dep_type" class="searchbox_drop">
				<option value="">::매입방식::</option>
				<option value="DDC">DDC</option>
				<option value="EDI">EDI</option>
				<option value="HDDC">HDDC</option>
			</select>
		</td>
		<td width="120"></td>
		<td>
		</td>
		<td width="120"></td>
		<td>
		</td>
		<td width="120"></td>
		<td>
		</td>
		<td align="right" style="padding-right:4px;">
			<span class="button large icon" onclick="reset_form();"><span class="check"></span><a href="#">다시작성</a></span>
		</td>
	</tr>
</table>
</form>
</body>
</html>
