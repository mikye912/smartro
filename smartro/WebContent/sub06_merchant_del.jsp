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
	
	String mercd = request.getParameter("mercd");
	String orgcd = request.getParameter("orgcd");
	String mid = request.getParameter("mid");
	
	//누른 mid에 대한 정보 불러와야 함.
	String[] midData = jbset.get_060503_item_merData(orgcd, mercd, mid);
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
			if(confirm('선택하신 가맹점정보를 삭제하시겠습니까?')==true){
				f.submit();
			}
		}
	</script>
</head>
<body>
<div style="width:100%; height:320px; overflow:auto;">
<table style="width:100%; height:40px;">
<tr>
	<td style="font-size:16pt; font-weight:bold; color:#6fa5fd;">가맹점번호 삭제</td>
</tr>
<tr>
	<td style="height:1px; background-color:#6fa5fd;"></td>
</tr>
<tr>
	<td style="height:10px;"></td>
</tr>
</table>
<form name="f" method="post" action="./proc/sub06_merchant_del_delete.jsp">
<input type="hidden" name="orgcd" value="<%=midData[9]%>">
<input type="hidden" name="mid" value="<%=midData[1]%>">
	<table class="tb" border="1" bordercolor="#e6e6e6" cellpadding="5" style="width: 100%; border-collapse: collapse;">
		<colgroup>
		<col style="width:130px; background-color:#f0f0f0;">
		<col>
		</colgroup>
		<tr>
			<td>사업부명</td>
			<td><%=midData[8]%></td>
		</tr>
		<tr>
			<td>가맹점번호</td>
			<td><%=midData[1]%></td>
		</tr>
		<tr>
			<td>수수료적용시작일</td>
			<td><%=midData[3]%></td>
		</tr>
		<tr>
			<td>수수료적용종료일</td>
			<td><%=midData[4]%></td>
		</tr>
		<tr>
			<td>일반수수료</td>
			<td><%=midData[5]%></td>
		</tr>
		<tr>
			<td>체크수수료</td>
			<td><%=midData[6]%></td>
		</tr>
		<tr>
			<td>해외수수료</td>
			<td><%=midData[7]%></td>
		</tr>
		<tr>
			<td colspan="2">위의 가맹점번호를 삭제하시겠습니까?</td>
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
</body>
<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</html>