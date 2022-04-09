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
	var hspace	= 250;

	function doOnLoad() {
		var h = $(window).height();
		var w = $(window).width();

		myCalendar = new dhtmlXCalendarObject(["stime","etime","sappdd","eappdd"]);
		myCalendar.setWeekStartDay(7);
		myCalendar.setDate("<%=setdate.format(nowTime)%>");
		myCalendar.hideTime();
		myCalendar.setSkin('dhx_skyblue');

		$("#stime").val("<%=setdate.format(nowTime)%>");
		$("#etime").val("<%=setdate.format(nowTime)%>");
		$("#parentId").css("min-height","600px").css("min-width","860px");
		$(".dhx_cell_cont_layout").css("width","100%");
		$('#parentId').css('height', h-hspace);
		AccLayout.setSizes();
	}

	$(window).resize(function(){
		var h = $(document).height();
		var w = $(document).width();
		
		$(".dhx_cell_layout, .dhx_cell_cont_layout").css("width","100%").css("min-width","1160px");
		$(".dhx_cell_cont_layout").css("border","0").css("width","100%").css("border-top","1px solid #c0c0c0").css("border-bottom","1px solid #c0c0c0");
		$(".cont_title").css("margin","0 2px").css("min-width","550px");
		
		$('#parentId').css('height', h-hspace);
		AccLayout.setSizes();
	});

	function setSens(id, k) {
	   myCalendar.setSensitiveRange(null,null);
	}

	function byId(id) {
		return document.getElementById(id);
	}
</script>
<style>
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
</head>
<body onload="doOnLoad();">
<div class='sub_content'>
	<div class='sub_content_space'></div>
	<div class='cont_title'>
	<form name='regular' method='post' action="./proc/excelup.jsp"  enctype="multipart/form-data" target="subq">
		<input type='hidden' name='extitle' id='extitle' value=''>
		<input type='hidden' name='search' value='Y'>
		<input type='hidden' name="uauth" value="<%=tuser%>">
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
					<span class='schtitle'>입금일자</span>
				</td>
				<td>
					<input type='text' name='stime' id='stime' class="searchbox_nomal" onclick="setSens('etime', 'max');" onblur="datesam();">
					~
					<input type='text' name='etime' id='etime' class="searchbox_nomal" onclick="setSens('stime', 'min');" >
				</td>
				<td>
					<span class='schtitle'>카드사선택</span>
				</td>
				<td>
					<!-- <select name='acqcd' id='acqcd' style='font-size:9pt;' class='searchbox_drop'>
						<option value=''>:: 전체 ::</option>
						<option value='02,VC0001' >국민카드</option>
						<option value='33,VC0003' >롯데카드</option>
						<option value='06,VC0004' >삼성카드</option>
						<option value='01,VC0006' >비씨카드</option>
						<option value='11,VC0030' >농협카드</option>
						<option value='08,VC0002' >현대카드</option>
						<option value='03,VC0005' >하나카드</option>
						<option value='07,VC0007' >신한카드</option>
					</select> -->
					<%=useracq %>
				</td>
				<td>
					<span class='schtitle'>사업부</span>
				</td>
				<td>
					<%=userdepo%>
				</td>
				<td>
					<span class='schtitle'>가맹점번호</span>
				</td>
				<td>
					<input type='text' name='mid' id='mid' class="searchbox_large">
				</td>
				<td rowspan="2" align="center"><img src='./images/btn/btn_search.gif' onclick='search_go();' style='cursor:pointer;'></td>
			</tr>
			<tr height="36">
				<td>
					<span class='schtitle'>비고내역</span>
				</td>
				<td colspan="3">
					<input type='text' name='accetc' id='accetc' style='width:350px; font-size:9pt;'>
				</td>
				<td>
					<span class='schtitle'>입금내역등록</span>
				</td>
				<td colspan="3">
					<input type='file' name='file' id='file' style='width:300px; font-size:9pt;'><button style="color:#2686a3" style="cursor:pointer;">입금내역업로드</button>
				</td>
			</tr>
			</tbody>
		</table>
		</form>
	</div>
	<div style="width:100%; height:10px;"></div>
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
	<div id="parentId" style="position: relative; top: 0px; left: 0px; width: 100%; height:200px;"></div>
</div>
<script type="text/javascript">
	AccLayout = new dhtmlXLayoutObject("parentId", "1C");
	AccLayout.setSkin("dhx_web");	
	AccLayout.cells("a").hideHeader();

	accountGrid = AccLayout.cells("a").attachGrid();

	var fields    = "사업부,가맹점번호,카드사,입금일자,입금액,비고내역";
	var aligns	 = "left,left,left,left,right,left";
	var colTypes  = "ro,ro,ro,ro,ron,ro";
	var sorts	 = "str,str,str,str,int,str";
	var colWidth  = "140,120,120,120,120,*";

	accountGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	accountGrid.setSkin("dhx_web");	
	accountGrid.setHeader(fields);
	accountGrid.setInitWidths(colWidth);
	accountGrid.setColTypes(colTypes);
	accountGrid.setColAlign(aligns);
	accountGrid.setColSorting(sorts);
	accountGrid.setNumberFormat("0,000",4,".",",");
	accountGrid.init();

	accountGrid.attachEvent("onXLE", function() {
		AccLayout.items[0].progressOff();
		var count=accountGrid.getRowsNum();
		$('#grid_total_rows').html(count);
	}); 

	function glb_where(){
		var stime	= byId("stime").value;
		var etime	= byId("etime").value;
		var mid		= byId("mid").value;
		var acqcd	= $('#acqcd option:selected').val();
		var depcd	= $('#depcd option:selected').val();
		var accetc	= byId("accetc").value;
		
		var w	 = "uauth=<%=tuser%>";
		w	+= (stime!="") ? "&stime="+stime : "";
		w	+= (etime!="") ? "&etime="+etime : "";
		w	+= (acqcd!="") ? "&acqcd="+acqcd : "";
		w	+= (depcd!="") ? "&depcd="+depcd : "";
		w	+= (mid!="") ? "&mid="+mid : "";
		w	+= (accetc!="") ? "&accetc="+accetc : "";

		return w;
	}

	function acc_exceldn(){
		var exsrc	= "./ifou_xml_was/excel0304.jsp?"+glb_where();
		
		$.getJSON(exsrc, 
			{ 
				tags: "mount rainier", 
				tagmode: "any", 
				format: "json"
			}, // 서버가 필요한 정보를 같이 보냄. 
			function(data, status) { 
				if(data["RST"]=="S000"){
					$('#totalarray').val(data["TOTALARRAY"]);
					$('#exceldn').submit();
				}
			} 
		);
	}
	
	function afterload_prog01(){
		AccLayout.items[0].progressOff();
	}
	
	function search_go(){

		var daydiff	= getDateDiff($('#etime').val(),$('#stime').val());
		if(daydiff > 366){
			dhtmlx.alert({
				type:"alert-warning",
				text:"검색기간이 초과되었습니다.<br>검색 기간은 365일까지 가능합니다."
			});
			return false;
		}

		AccLayout.items[0].progressOn();
		accountGrid.clearAndLoad("./ifou_xml_was/total_0304.jsp?"+glb_where(), afterload_prog01, "json");	
	}
	

</script>
<form id="exceldn" method="post" name="exceldn" target="subq" action="./excel/exceldn_0304.jsp">
	 <input type="hidden" name="totalarray" id="totalarray">
</form>
<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>