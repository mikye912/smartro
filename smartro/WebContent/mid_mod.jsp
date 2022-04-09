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
	
	//2021.03.05 - mid 등록
	String depcd = request.getParameter("depcd");
	String orgcd = request.getParameter("orgcd");
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
	
		$.ajax({
			url:"./ifou_xml_was/mid_list.jsp?orgcd=<%=orgcd%>&depcd=<%=depcd%>",
			type:"GET",
			dataType: 'text',
	        success: function(data){
	    		$("#midList").append(data);
	        }
		});
	
		function close_go(){
			parent.ipopup_close();
		}

		function form_go(){
			var f = document.f;
			if(confirm('선택하신 가맹점번호를 사업부에 등록하시겠습니까?')==true){
				f.submit();
			}
		}
		
	</script>
</head>
<body>
<div style="width:100%; height:330px; overflow:auto;">
<table style="width:100%; height:40px;">
<tr>
	<td style="font-size:16pt; font-weight:bold; color:#6fa5fd;">사업부 가맹점번호관리</td>
</tr>
<tr>
	<td style="height:1px; background-color:#6fa5fd;"></td>
</tr>
<tr>
	<td style="height:10px;"></td>
</tr>
</table>
<form name="f" method="post" action="./proc/mid_mod_update.jsp">
<input type="hidden" name="orgcd" value="<%=orgcd%>">
<input type="hidden" name="depcd" value="<%=depcd%>">
	<table class="tb" id="midList" border="1" bordercolor="#e6e6e6" cellpadding="5" style="width: 100%; border-collapse: collapse;">
		<colgroup>
		<col style="width:60px;">
		<col style="width:180px">
		<col>
		</colgroup>
		<tr style="background-color:#f0f0f0;">
			<th>선택</th>
			<th>카드사</th>
			<th>가맹점번호</th>
		</tr>
		<!-- mid list append -->
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
