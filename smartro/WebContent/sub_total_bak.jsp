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
%>

<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" type="text/css" href="./dhtmlx/codebase/dhtmlx.css"/>
	<link rel="stylesheet" type="text/css" href="./dhtmlx/dhtmlxGrid/codebase/skins/dhtmlxgrid_dhx_web.css"/>
	<link type="text/css" rel="stylesheet" href="./include/css/style.css"  media="all" >
	<script src="./dhtmlx/codebase/dhtmlx.js"></script>
	<script src="./include/js/jquery-1.8.1.min.js" type="text/javascript"></script>
	<script src="./include/js/common.js"></script>
    <script src="./include/js/dhtmlxgrid_export.js"></script>
	<script type="text/javascript" src="./include/js/jquery.printArea.js" ></script>
	<script>
	var myCalendar;
	function doOnLoad() {
		myCalendar = new dhtmlXCalendarObject(["stime","etime"]);
		myCalendar.attachEvent("onClick", function(d){
			byId("etime").value = myCalendar.getFormatedDate(null,d);
		});
		myCalendar.setWeekStartDay(7);
		myCalendar.setDate("<%=setdate.format(nowTime)%>");
		myCalendar.hideTime();
		myCalendar.setSkin('dhx_skyblue');

		// init values
		var t = new Date();
		$("#stime").val("<%=setdate.format(nowTime)%>");
		$("#etime").val("<%=setdate.format(nowTime)%>");
		

		$("#printButton").click(function(){
			$("#printable").printArea();
		});
	}

	function setDate(obj){
		var tmp = obj.value;
		str = tmp.replace(/[^0-9]/g,'');
		if(str.length<8||str.length>8){
			alert("입력하신 날짜형식을 확인 하시기 바랍니다.");
		}else{
			var df	= str.substring(0,4)+"-"+str.substring(4,6)+"-"+str.substring(6,8);
			obj.value = df;
		}
	}

	var D = 0;
	function setSens(id, k) {
		myCalendar.setSensitiveRange(null,null);
	}

	function byId(id) {
		return document.getElementById(id);
	}

	function preview_print(){ 
		var OLECMDID = 7; 
		var PROMPT = 1; 
		var WebBrowser = '<OBJECT ID="WebBrowser1" WIDTH=0 HEIGHT=0 CLASSID="CLSID:8856F961-340A-11D0-A96B-00C04FD705A2"></OBJECT>';

		document.body.insertAdjacentHTML('beforeEnd', WebBrowser); 
		WebBrowser1.ExecWB( OLECMDID, PROMPT); 
	} 

	function winPrint(){ 
		var browser = navigator.userAgent.toLowerCase();
		if ( -1 != browser.indexOf('chrome') ){
			parent.window.print();
		}else if ( -1 != browser.indexOf('trident') ){
			preview_print();
		} 
	}

	</script>
<style>
	span.label {
		font-family: Tahoma;
		font-size: 12px;
	}
	.hdrcell{text-align:center;font-weight:bold;}
	.schtitle{font-size:9pt;}
	.dhx_cell_hdr{border:0px solid #ffffff;}
	.dhx_cell_cont_layout{border:0px solid #ffffff;}
	

	table.tb {border-collapse: collapse;}
	table.tb>tr>td,
	table.tb>tr>th,
	table.tb>tbody>tr>td,
	table.tb>tbody>tr>th {border:1px solid #cccccc; padding:5px;}

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
</head>
<body onload="doOnLoad();">
<br>
<div style="padding:10px;">
	<form name='regular' method='post'>
	<input type='hidden' name='search' value='Y'>
	<table class="tb" border="1" bordercolor="#e6e6e6" cellpadding="5" style="width: 100%; border-collapse: collapse;">
	<colgroup>
	<col class="cellC" style="width:100px;">
	<col class="cellL" style="width:180px">
	<col class="cellC" style="width:100px;">
	<col class="cellL" style="width:180px">
	<col class="cellC" style="width:100px;">
	<col class="cellL" style="width:150px">
	<col class="cellC" style="width:100px;">
	<col class="cellL">
	<col class="cellL" style="width:100px;">
	</colgroup>
		<tbody>
		<tr height="36">
			<td>
				<span class='schtitle'>검색기간</span>
			</td>
			<td>
				<input type='text' name='stime' id='stime' class="searchbox_nomal" >
				~
				<input type='text' name='etime' id='etime' class="searchbox_nomal" >
			</td>
			<td>
				<span class='schtitle'>매입사</span>
			</td>
			<td colspan="5">
			<!-- 2021.02.08 강원대병원 매입사 선택 코드 -->
				<select name='acqcd' id='acqcd' style='font-size:9pt;' class='searchbox_drop'>
					<option value=''>:: 전체 ::</option>
					<option value='02,VC0001' >국민카드</option>
					<option value='33,VC0003' >롯데카드</option>
					<option value='06,VC0004' >삼성카드</option>
					<option value='01,VC0006' >비씨카드</option>
					<option value='11,VC0030' >농협카드</option>
					<option value='08,VC0002' >현대카드</option>
					<option value='03,VC0005' >하나카드</option>
					<option value='07,VC0007' >신한카드</option>
				</select>
			</td>
			<td align="right"><input type="image" src="./images/btn/btn_search.gif" onclick="search_go();" style='cursor:pointer;'></td>
		</tr>
		</tbody>
	</table>
	</form>
	<table width="100%" class="tb00_none">
	<tr>
		<td height="15px"></td>
	</tr>
	</table>
	<table width="100%" class="tb00_none">
	<tr>
		<td>
		<b>■ 일별집계</b>
		</td>
		<td align="right">
			<span class='button large icon' onclick='javascript:excel_down();'><span class='excel'></span><a href='#'>엑셀다운로드</a></span>
			<span class='button large icon' onclick='javascript:winPrint();'><span class='print'></span><a href='#'>화면인쇄</a></span>		
		</td>
	</tr>
	<tr>
		<td colspan="2" height="5"></td>
	</tr>
	</table>
	<table width="100%" class="tb01_gray" id="tempSub">
	<colgroup>
		<col width="100"></col>
		<col width="120"></col>
		<col width="120"></col>
		<col width="200"></col>
		<col width="200"></col>
		<col width="200"></col>
		<col width="200"></col>
		<col width="200"></col>
	</colgroup>
	<tr>
		<th>순번</th>
		<th>승인일</th>
		<th>매입사</th>
		<th>승인</th>
		<th>취소</th>
		<th>승인금액</th>
		<th>취소금액</th>
		<th>합계</th>
	</tr>
	</table>
</div>
<script>
	function wherequery(){
		var stime	= byId("stime").value;
		var etime	= byId("etime").value;
		var acqcd	= $('#acqcd option:selected').val();
		var w	 = "uauth=<%=tuser%>";
	
		w	+= (stime!="") ? "&stime="+stime : "";
		w	+= (etime!="") ? "&etime="+etime : "";
		w	+= (acqcd!="") ? "&acqcd="+acqcd : "";
	
		return w;
	}
	
	function search_go(){
		var seturl = "./ifou_xml_was/total_main.jsp?" + wherequery();
		$.get(seturl, 
				{ 
					tags: "mount rainier", 
					tagmode: "any", 
					format: "json"
				}, // 서버가 필요한 정보를 같이 보냄. 
				function(data, status) { 
					$.each(data, function(key,val) { 
						//alert(data["UDATA"]);
						$("#tempSub").append(data["UDATA"]);
					});
				} 
		);
	}
</script>
<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>
