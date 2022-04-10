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

		myCalendar = new dhtmlXCalendarObject(["stime","etime","sreqdd","ereqdd"]);
		myCalendar.attachEvent("onClick", function(d){
			byId("etime").value = myCalendar.getFormatedDate(null,d);
		});
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
	<form name='exceldn' id="exceldn" action='./excel/exceldn_0303.jsp'>
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
		<col class="cellL" style="width:150px">
		<col class="cellC" style="width:100px;">
		<col class="cellL">
		<col class="cellL" style="width:100px;">
		</colgroup>
			<tbody>
			<tr height="36">
				<td>
					<span class='schtitle'>거래일자</span>
				</td>
				<td>
					<input type='text' name='stime' id='stime' class="searchbox_nomal" onclick="setSens('etime', 'max');" onblur="datesam();">
					~
					<input type='text' name='etime' id='etime' class="searchbox_nomal" onclick="setSens('stime', 'min');" >
				</td>
				<td>
					<span class='schtitle'>청구일자</span>
				</td>
				<td>
					<input type='text' name='sreqdd' id='sreqdd' class="searchbox_nomal" onclick="setSens('ereqdd', 'max');" autocomplete="off">
					~
					<input type='text' name='ereqdd' id='ereqdd' class="searchbox_nomal" onclick="setSens('sreqdd', 'min');" autocomplete="off">
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
				<td rowspan="2" align="center"><img src='./images/btn/btn_search.gif' onclick='search_go();' style='cursor:pointer;'></td>
			</tr>
			<tr height="36">
				<td>
					<span class='schtitle'>단말기번호</span>
				</td>
				<td>
					<%=usertid%>
				</td>
				<td>
					<span class='schtitle'>가맹점번호</span>
				</td>
				<td>
					<input type='text' name='mid' id='mid' class="searchbox_large">
				</td>
				<td>
					<span class='schtitle'>승인번호</span>
				</td>
				<td colspan="3">
					<input type='text' name='appno' id='appno' class="searchbox_large">
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
	AccLayout = new dhtmlXLayoutObject("parentId", "2E");
	AccLayout.setSkin("dhx_web");	
	AccLayout.cells("a").hideHeader();
	AccLayout.cells("b").hideHeader();

	totalGrid = AccLayout.cells("a").attachGrid();
	var tfields  = "순번,단말기명,단말기번호,비씨,국민";
		tfields += ",하나,삼성,신한,현대,롯데";
		tfields += ",농협";

    var taligns	 = "center,left,left,right,right";
		taligns += ",right,right,right,right,right";
		taligns += ",right";

    var tcoltypes  = "ro,ro,ro,ron,ron";
        tcoltypes += ",ron,ron,ron,ron,ron";
		tcoltypes += ",ron";

    var tsorts	 = "str,str,str,int,int";
        tsorts   += ",int,int,int,int,int";
		tsorts   += ",int";

    var tcolwidth   = "60,150,*,*,*";
		tcolwidth	+= ",*,*,*,*,*";
		tcolwidth	+= ",*";

	totalGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	totalGrid.setSkin("dhx_web");	
	totalGrid.setHeader(tfields);
	totalGrid.setInitWidths(tcolwidth);
	totalGrid.setColTypes(tcoltypes);
	totalGrid.setColAlign(taligns);
	totalGrid.setColSorting(tsorts);
	totalGrid.setNumberFormat("0,000",3,".",",");
	totalGrid.setNumberFormat("0,000",4,".",",");
	totalGrid.setNumberFormat("0,000",5,".",",");
	totalGrid.setNumberFormat("0,000",6,".",",");
	totalGrid.setNumberFormat("0,000",7,".",",");
	totalGrid.setNumberFormat("0,000",8,".",",");
	totalGrid.setNumberFormat("0,000",9,".",",");
	totalGrid.setNumberFormat("0,000",10,".",",");
	totalGrid.enableColSpan(true);
	totalGrid.init();
	
	totalGrid.attachEvent("onXLE", function() {
		totalGrid.setColspan("total",0,3);
		totalGrid.setRowTextStyle("total", "text-align:right;font-weight:bold;background-color:#f3fafe;color:#006699;border:1px solid #ffffff;");
		totalGrid.setCellTextStyle("total",0,"text-align:center;font-weight:bold;background-color:#f3fafe;color:#006699;border:1px solid #ffffff;");
	}); 

	accountGrid = AccLayout.cells("b").attachGrid();

	var fields    = "사업부,단말기명,단말기번호,가맹점번호,카드사";
        fields   += ",거래일자,청구일자,입금일자,거래건수,거래금액";
        fields   += ",수수료,입금예정액";

	var filters   = "#select_filter,#select_filter,#select_filter,#select_filter,#select_filter";
        filters  += ",#select_filter,#select_filter,#select_filter,#text_filter,#text_filter";
		filters	 += ",#text_filter,#text_filter";

	var aligns	 = "left,left,left,left,center";
        aligns  += ",center,center,center,right,right";
		aligns	+= ",right,right";

	var colTypes  = "ro,ro,ro,ro,ro";
        colTypes += ",ro,ro,ro,ron,ron";
		colTypes += ",ron,ron";

	var sorts	 = "str,str,str,str,str";
        sorts   += ",str,str,str,int,int";
		sorts	+= ",int,int";

	var colWidth  = "140,120,120,120,100";
        colWidth += ",100,100,100,*,*";
		colWidth += ",*,*";

	accountGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	accountGrid.setSkin("dhx_web");	
	accountGrid.setHeader(fields);
	accountGrid.setInitWidths(colWidth);
	accountGrid.setColTypes(colTypes);
	accountGrid.setColAlign(aligns);
	accountGrid.setColSorting(sorts);
	accountGrid.attachEvent("onRowDblClicked",doOnRowDblClicked);
	accountGrid.setNumberFormat("0,000",8,".",",");
	accountGrid.setNumberFormat("0,000",9,".",",");
	accountGrid.setNumberFormat("0,000",10,".",",");
	accountGrid.setNumberFormat("0,000",11,".",",");
	accountGrid.enableColSpan(true);
	accountGrid.init();

	accountGrid.attachEvent("onXLE", function() {
		accountGrid.setRowTextStyle("total", "text-align:right;font-weight:bold;background-color:#f3fafe;color:#006699;border:1px solid #ffffff;");
		accountGrid.setCellTextStyle("total",0,"text-align:center;font-weight:bold;background-color:#f3fafe;color:#006699;border:1px solid #ffffff;");
		accountGrid.setColspan("total",0,4);
		
		AccLayout.items[0].progressOff();
		AccLayout.items[1].progressOff();
		var count=accountGrid.getRowsNum();
		$('#grid_total_rows').html(count);
	}); 
	


	function glb_where(){
		var stime	= byId("stime").value;
		var etime	= byId("etime").value;
		var sreqdd	= byId("sreqdd").value;
		var ereqdd	= byId("ereqdd").value;
		var mid		= byId("mid").value;
		var acqcd	= $('#acqcd option:selected').val();
		var depcd	= $('#depcd option:selected').val();
		var tid		= $('#tid option:selected').val();
		var appno	= byId("appno").value;
		
		var w	 = "uauth=<%=tuser%>";
		w	+= (stime!="") ? "&stime="+stime : "";
		w	+= (etime!="") ? "&etime="+etime : "";
		w	+= (sreqdd!="") ? "&sreqdd="+sreqdd : "";
		w	+= (ereqdd!="") ? "&ereqdd="+ereqdd : "";
		w	+= (acqcd!="") ? "&acqcd="+acqcd : "";
		w	+= (depcd!="") ? "&depcd="+depcd : "";
		w	+= (tid!="") ? "&tid="+tid : "";
		w	+= (mid!="") ? "&mid="+mid : "";
		w	+= (appno!="") ? "&appno="+appno : "";

		return w;
	}

	function doOnRowDblClicked(rowId){
		//단말기
		var tid		= accountGrid.cells(rowId, 2).getValue();
		//가맹점
		var mid		= accountGrid.cells(rowId, 3).getValue();
		//거래일자
		var appdd	= accountGrid.cells(rowId, 5).getValue();
		//청구일자
		var reqdd	= accountGrid.cells(rowId, 6).getValue();
		//입금일자
		var expdd	= accountGrid.cells(rowId, 7).getValue();
		//승인번호
		var appno	= byId("appno").value;

		var w	= "?uauth=<%=tuser%>&reqdd="+reqdd+"&appdd="+appdd+"&expdd="+expdd+"&mid="+mid+"&tid="+tid+"&appno="+appno;
		parent.urlgoDirect(-1, "거래일자상세내역", "sub03_03detail.jsp"+w);
	}

	function acc_exceldn(){
		$('#exceldn').submit();
	}
	
	function afterload_prog01(){
		AccLayout.items[0].progressOff();
	}
	
	function afterload_prog02(){
		AccLayout.items[1].progressOff();
	}

	function search_go(){

		var daydiff	= getDateDiff($('#etime').val(),$('#stime').val());
		if(daydiff > 31){
			dhtmlx.alert({
				type:"alert-warning",
				text:"검색기간이 초과되었습니다.<br>검색 기간은 30일까지 가능합니다."
			});
			return false;
		}

		AccLayout.items[0].progressOn();
		AccLayout.items[1].progressOn();
		totalGrid.clearAndLoad("./ifou_xml_was/total_0303.jsp?"+glb_where(), afterload_prog01, "json");	
		accountGrid.clearAndLoad("./ifou_xml_was/item_0303.jsp?"+glb_where(), afterload_prog02, "json");
	}
	
</script>
<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>