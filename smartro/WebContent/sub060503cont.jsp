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

			if($('#depcd option:selected').val()==""){
				alert('사업부를 선택하여 주십시오.');
				return false;
			}

			if($('#mid').val()==""){
				alert('가맹점번호를 입력하여 주십시오.');
				return false;
			}

			if($('#purcd option:selected').val()==""){
				alert('카드사를 선택하여 주십시오.');
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
<FORM name="memreg" method="post" action="./proc/sub060503proc.jsp">
<table width='100%' class='tb00_none'>
	<tr height="10"><td colspan="9"></td></tr>
	<tr height="30">
		<td width="120">■ 사업부선택</td>
		<td>
			<%=userdepo%>
		</td>
		<td width="120">■ 카드사선택</td>
		<td>
			<select name="purcd" id="purcd" class="searchbox_drop">
				<option value="">카드사선택</option>
				<option value='VC0001' >국민카드</option>
				<option value='VC0003' >롯데카드</option>
				<option value='VC0004' >삼성카드</option>
				<option value='VC0006' >비씨카드</option>
				<option value='VC0030' >농협카드</option>
				<option value='VC0002' >현대카드</option>
				<option value='VC0005' >하나카드</option>
				<option value='VC0007' >신한카드</option>
			</select>
		</td>
		<td width="120">■ 가맹점번호</td>
		<td>
			<input type="text" name="mid" id="mid" value="" class="searchbox_large">
		</td>
		<td align="right" style="padding-right:4px;">
			<span class="button large icon" onclick="regi_form();"><span class="add"></span><a href="#">신규등록</a></span>
		</td>
	</tr>
	<tr height="1"><td colspan="7" bgcolor="#c0c0c0"></td></tr>
	<tr>
		<td width="120" height="42">■ 수수료 적용시작일</td>
		<td>
			<input type="text" name="merst" value="" class="searchbox_large">
		</td>
		<td width="120">■ 수수료 적용종료일</td>
		<td>
			<input type="text" name="meret" value="" class="searchbox_large">
		</td>
		<td width="120">■ VAN사선택</td>
		<td>
			<select name="van" id="van" class="searchbox_drop">
				<option value="">VAN사선택</option>
				<option value="03">코세스</option>
				<option value="04">다우</option>
			</select>
		</td>
		<td align="right" style="padding-right:4px;">
			<span class="button large icon" onclick="reset_form();"><span class="check"></span><a href="#">다시작성</a></span>
		</td>
	</tr>
	<tr height="1"><td colspan="7" bgcolor="#c0c0c0"></td></tr>
	<tr height="30">
		<td width="120">■ 일반수수료</td>
		<td>
			<input type="text" name="fee01" value="" class="searchbox_large">
		</td>
		<td width="120">■ 체크수수료</td>
		<td>
			<input type="text" name="fee02" value="" class="searchbox_large">
		</td>
		<td width="120">■ 해외수수료</td>
		<td>
			<input type="text" name="fee03" value="" class="searchbox_large">
		</td>
		<td width="120"></td>
		<td></td>
	</tr>
</table>
</form>
</body>
</html>