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
	
	String tmp_acq = (String)session.getAttribute("useracq");
	byte[] useracq_buf = decoder.decode(tmp_acq);
	String useracq = new String(useracq_buf, "UTF-8");
	
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
	<script>
	var myCalendar;
	var AccLayout, accountGrid;
	var filterVal = "", filterIdx="";

	function doOnLoad() {
		var h = $(window).height();
		var w = $(window).width();

		var hset	= (h-130) + "px";
		
		document.getElementById("parentId").style.height = hset;
		AccLayout.setSizes();
		
		myCalendar = new dhtmlXCalendarObject(["stime","etime"]);
		myCalendar.attachEvent("onClick", function(d){
			byId("etime").value = myCalendar.getFormatedDate(null,d);
		});
		myCalendar.setWeekStartDay(7);
		myCalendar.setDate("<%=setdate.format(nowTime)%>");
		myCalendar.hideTime();
		myCalendar.setSkin('dhx_skyblue');

		byId("stime").value = "<%=setdate.format(nowTime)%>";
		byId("etime").value = "<%=setdate.format(nowTime)%>";

		$("#parentId").css("min-height","800px").css("min-width","860px");
		$(".dhx_cell_cont_layout").css("width","100%");
	}

	$(window).resize(function(){
		var h = $(document).height();
		var w = $(document).width();
		
		$(".dhx_cell_layout, .dhx_cell_cont_layout").css("width","100%").css("min-width","1160px");
		$(".dhx_cell_cont_layout").css("border","0").css("width","100%").css("border-top","1px solid #c0c0c0").css("border-bottom","1px solid #c0c0c0");
		$(".cont_title").css("margin","0 2px").css("min-width","550px");
		
		var hset	= (h-150) + "px";
		document.getElementById("parentId").style.height = hset;
	});
	
	function setSens(id, k) {
       myCalendar.setSensitiveRange(null,null);
	}

	function byId(id) {
		return document.getElementById(id);
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
<div class='sub_content'>
	<div class='sub_content_space'></div>
	<div class='cont_title'>
	<form name='exceldn' id="exceldn" action='./excel/exceldn_0205.jsp'>
		<input type='hidden' name='extitle' id='extitle' value=''>
		<input type='hidden' name='search' value='Y'>
		<input type="hidden" name="uauth" value="<%=tuser%>">
		<table class="tb" border="1" bordercolor="#e6e6e6" cellpadding="5" style="width: 100%; border-collapse: collapse;">
		<colgroup>
		<col class="cellC" style="width:100px;">
		<col class="cellL" style="width:180px">
		<col class="cellC" style="width:100px;">
		<col class="cellL" style="width:180px">
		<col class="cellC" style="width:100px;">
		<col class="cellL" style="width:180px">
		<col class="cellC" style="width:100px;">
		<col class="cellL">
		<col class="cellL" style="width:100px;">
		</colgroup>
			<tbody>
			<tr height="36">
				<td>
					<span class='schtitle'>승인일자</span>
				</td>
				<td>
					<input type='text' name='stime' id='stime' class="searchbox_nomal" onclick="setSens('etime', 'max');" onblur="datesam();">
					~
					<input type='text' name='etime' id='etime' class="searchbox_nomal" onclick="setSens('stime', 'min');" >
				</td>
				<td>
					<span class='schtitle'>카드번호</span>
				</td>
				<td>
					<input type='text' name='cardno' id='cardno' class="searchbox_large" value="">
				</td>
				<td>
					<span class='schtitle'>승인번호</span>
				</td>
				<td colspan='3'>
					<input type='text' name='appno' id='appno' class="searchbox_large" value="">
				</td>
				<td align="right"><img src='./images/btn/btn_search.gif' onclick='search_go();' style='cursor:pointer;'></td>
			</tr>
			</tbody>
		</table>
		</form>
	</div>
	<div style="position: relative; top: 0px; left: 0px; width: 100%; height:10px;"></div>
	<div class="control_div">
		<table class="tb00_none" width="100%">
			<tr>
				<td>Total Rows : <span id="grid_total_rows"></span>건</td>
				<td align="right">
				<span class='button large icon' onclick='acc_exceldn();'><span class='excel'></span><a href='#'>엑셀다운로드</a></span>
				</td>
			</tr>
			<tr>
				<td colspan="2" height="5"></td>
			</tr>
		</table>
	</div>
	<div id="parentId" style="position: relative; top: 0px; left: 0px; width: 100%; height:130px;"></div>
</div>
<script type="text/javascript">
	var filterVal = "", filterIdx="";
    AccLayout = new dhtmlXLayoutObject("parentId", "2E");
	AccLayout.setSkin("dhx_web");	
	AccLayout.cells("a").hideHeader();
	AccLayout.cells("b").hideHeader();

	totalGrid = AccLayout.cells("a").attachGrid();
	var tfields  = "순번,매장코드,매장명,반송건수,반송금액,승인취소매출접수";
		tfields += ",당일승인/취소,원매출 승인취소,원매출없음,비씨,농협";
		tfields += "국민,삼성,하나,롯데,현대,신한";
    var taligns	 = "center,left,left,right,right";
		taligns	+= ",right,right,right,right,right";
		taligns	+= ",right,right,right,right,right,right";
    var tcoltypes  = "ro,ro,ro,ron,ron";
        tcoltypes += ",ron,ron,ron,ron,ron";
        tcoltypes += ",ron,ron,ron,ron,ron,ron";
    var tsorts	 = "str,str,str,int,int";
        tsorts   += ",int,int,int,int,int";
        tsorts   += ",int,int,int,int,int,int";
    var tcolwidth   = "60,80,150,*,*";
		tcolwidth	+= ",*,*,*,*,*";
		tcolwidth	+= ",*,*,*,*,*,*";

	totalGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	totalGrid.setSkin("dhx_web");	
	totalGrid.setHeader(tfields);
	totalGrid.setInitWidths(tcolwidth);
	totalGrid.setColTypes(tcoltypes);
	totalGrid.setColAlign(taligns);
	totalGrid.setColSorting(tsorts);
	totalGrid.init();

	accountGrid = AccLayout.cells("b").attachGrid();

var fields   = "순번,거래구분,매장코드,매장명,반송코드";
	fields	+= ",반송사유,단말기번호,가맹점번호,카드번호,카드사명";
	fields	+= ",금액,할부기간,승인일자,승인번호,요청일자,확장1,확장2";
	 
var filters  = "&nbsp;,#select_filter,#select_filter,#select_filter,#select_filter";
	filters	+= ",#text_filter,#select_filter,#text_filter,#text_filter,#select_filter";
	filters	+= ",#text_filter,#text_filter,#text_filter,#text_filter,#text_filter,#text_filter,#text_filter";
	
var aligns	 = "center,center,center,center,left";
	aligns	+= ",center,center,center,center,center";
	aligns	+= ",right,right,center,center,center,center,center";

var colTypes  = "ed,ed,ed,ed,ed";
	colTypes += ",ed,ed,ed,ed,ed";
	colTypes += ",ed,ed,ed,ed,ed,ed,ed";
	
var sorts	 = "int,str,str,str,str";
	sorts	+= ",str,str,str,str,str";
	sorts	+= ",int,str,str,str,str,str,str";
	
var colWidth	 = "60,80,80,200,80";
	colWidth	+= ",150,100,100,160,100";
	colWidth	+= ",100,100,100,100,100,100,100";
	
	
	accountGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	accountGrid.setSkin("dhx_web");	
	//accountGrid.setHeader(headd);
	accountGrid.setInitWidths(colWidth);
	accountGrid.setHeader(fields);
	accountGrid.attachHeader(filters);
	accountGrid.setColTypes(colTypes);
	accountGrid.setColAlign(aligns);
	accountGrid.setColSorting(sorts);
	accountGrid.setNumberFormat("0,000",10,".",",");
	//accountGrid.attachEvent("onRowDblClicked",doOnRowDblClicked);
	accountGrid.init();

	accountGrid.attachEvent("onXLE", function() {
		AccLayout.items[0].progressOff();
		AccLayout.items[1].progressOff();
		var count=accountGrid.getRowsNum();
		$('#grid_total_rows').html(count);
	}); 

	function doOnRowDblClicked(rowId, cellInd){
		detail_view(rowId);
	}
	
	function wherequery(){
		var stime	= $('#stime').val();
		var etime	= $('#etime').val();
		var cardno	= $('#cardno').val();
		var appno	= $('#appno').val();
		
		var w	 = "uauth=<%=tuser%>";

		w	+= (stime!="") ? "&stime="+stime : "";
		w	+= (etime!="") ? "&etime="+etime : "";
		w	+= (cardno!="") ? "&cardno="+cardno : "";
		w	+= (appno!="") ? "&appno="+appno : "";

		return w;
	}

	function acc_exceldn(){
		$("#exceldn").submit();
	}
	
	function afterload_prog01(){
		AccLayout.items[0].progressOff();
	}
	
	function afterload_prog02(){
		AccLayout.items[1].progressOff();
	}

	function search_go(){
		AccLayout.items[0].progressOn();
		AccLayout.items[1].progressOn();

		totalGrid.clearAndLoad("./ifou_xml_was/total_0205.jsp?" + wherequery(), afterload_prog01, "json");
		accountGrid.clearAndLoad("./ifou_xml_was/item_0205.jsp?" + wherequery(), afterload_prog02, "json");
	}

	
</script>
<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>