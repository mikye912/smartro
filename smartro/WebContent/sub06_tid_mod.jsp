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
	
	String depcd = request.getParameter("depcd");
	String orgcd = request.getParameter("orgcd");
	String tid = request.getParameter("tid");
	
	//2021.03.09
	String[] tidData = jbset.get_060503_item_tidData(depcd, orgcd, tid);
%>

<!DOCTYPE html>
<html>
<head>
	<title>IFOU 행복정산 서비스</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" type="text/css" href="./dhtmlx/codebase/dhtmlx.css"/>
	<link rel="stylesheet" type="text/css" href="./dhtmlx/dhtmlxGrid/codebase/skins/dhtmlxgrid_dhx_web.css"/>
	<link rel="stylesheet" type="text/css" href="./dhtmlx/dhtmlxTabbar/codebase/skins/dhtmlxtabbar_dhx_terrace.css"/>
	<link type="text/css" rel="stylesheet" href="./include/css/style.css"  media="all" >
	<script src="./dhtmlx/codebase/dhtmlx.js"></script>
	<script src="./include/js/jquery-1.8.1.min.js" type="text/javascript"></script>
	<script src="./include/js/common.js"></script>
	<script src="./include/js/dhtmlxgrid_export.js"></script>
	<style>
	input#stime, input#etime {font-size: 9pt;background-color: #fafafa;border: #c0c0c0 1px solid;width: 100px;}
	span.label {font-family: Tahoma;font-size: 12px;}
	table.tb {border-collapse: collapse;}
	table.tb>tr>td,
	table.tb>tr>th,
	table.tb>tbody>tr>td,
	table.tb>tbody>tr>th {border:1px solid #cccccc; padding:5px;}

	.hdrcell{text-align:center;font-weight:bold;}
	.totaldata{text-align:right;font-weight:none;height:24px;background-color:#f0f0f0;padding-top:8px;}
	.schtitle{font-size:9pt;}
	.dhx_cell_hdr{border:0px solid #ffffff;}
	.dhx_cell_cont_layout{border:0px solid #ffffff;}
	.cellC {
		color:#333333;
		background:#f6f6f6;
		font:12px tahoma;
		width:120px;
		text-align:left;
		padding-left:10px;
		padding-top:7px;
		font-weight:bold;
		letter-spacing:-1;
	}
	.cellL {padding-right:6px;padding-left:10px;}
	.cellR {text-align:right;padding-left:10px;}
	</style>
	<script>
	
		function close_go(){
			parent.ipopup_close();
		}

		function form_go(){
			var f = document.f;

			if($('#depcd option:selected').val()==""){
				alert('사업부를 선택하여 주십시오.');
				return false;
			}

			if($('#tidnm').val()==""){
				alert('단말기명을 입력하여 주십시오.');
				return false;
			}

			if($('#tid').val()==""){
				alert('단말기번호를 입력하여 주십시오.');
				return false;
			}

			if(confirm('선택하신 단말기정보를 수정하시겠습니까?')==true){
				f.submit();
			}
		}
		
	</script>
</head>
<body>
<div style="width:100%; height:320px; overflow:auto;">
<table style="width:100%; height:40px;">
<tr>
	<td style="font-size:16pt; font-weight:bold; color:#6fa5fd;">단말기번호 수정</td>
</tr>
<tr>
	<td style="height:1px; background-color:#6fa5fd;"></td>
</tr>
<tr>
	<td style="height:10px;"></td>
</tr>
</table>
<form name="f" method="post" action="./proc/sub06_tid_mod_update.jsp">
<input type="hidden" name="orgcd" id="orgcd" value="<%=orgcd%>">
<input type="hidden" name="tidcd" id="tidcd" value="<%=tid%>">
	<table class="tb" border="1" bordercolor="#e6e6e6" cellpadding="5" style="width: 100%; border-collapse: collapse;">
		<colgroup>
		<col style="width:130px; background-color:#f0f0f0;">
		<col>
		</colgroup>
		<tr>
			<td>사업부선택</td>
			<td><%=userdepo%></td>
		</tr>
		<tr>
			<td>단말기명</td>
			<td><input type="text" name="tidnm" id="tidnm" value="<%=tidData[2]%>" style="width:150px;"></td>
		</tr>
		<tr>
			<td>단말기번호</td>
			<td><input type="text" name="tid" id="tid" value="<%=tidData[3]%>" style="width:150px;"></td>
		</tr>
		<tr>
			<td>VAN사구분</td>
			<td>
			<select name="vangb">
				<option value="">::VAN사구분::</option>
				<option value="03">코세스</option>
				<option value="04">다우</option>
			</select>
			</td>
		</tr>
		<tr>
			<td>단말기구분</td>
			<td>
			<select name="term_type">
				<option value="">::단말기구분::</option>
				<option value="0">MSR단말기</option>
				<option value="1">IC단말기</option>
			</select>
			</td>
		</tr>
	</table>
</form>
</div>
<table width="100%" height="50px">
	<tr>
		<td align="center" style="background-color:#f0f0f0; padding-top:5px;">
			<img src="./images/pop_btn/btn_ok.png" onclick="javascript:form_go();">
			<img src="./images/pop_btn/btn_close.png" onclick="javascript:close_go();">
		</td>
	</tr>
</table>
<script>
	$("#depcd").val("<%=tidData[0]%>").attr("selected", true);
	$("#vangb").val("<%=tidData[4]%>").attr("selected", true);
	$("#term_type").val("<%=tidData[5]%>").attr("selected", true);
</script>
</body>
<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</html>