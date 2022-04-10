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

			var hset	= (h-150) + "px";
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
			//init values
			var t = new Date();
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
		.totaldata{text-align:right;font-weight:none;height:24px;background-color:#f0f0f0;padding-top:8px;}
		.schtitle{font-size:9pt;}
		.dhx_cell_hdr{border:0px solid #ffffff;}
		.dhx_cell_cont_layout{border:0px solid #ffffff;}
		.depo_subtotal{background-color:#e7edf8;color:#3399cc;font-style:italic;font-weight:bold;}
	</style>
<style>
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
	<form name='exceldn' id='exceldn' action='./excel/exceldn_0301_ban.jsp'>
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
					<span class='schtitle'>입금일자</span>
				</td>
				<td>
					<input type='text' name='stime' id='stime' class="searchbox_nomal" onclick="setSens('etime', 'max');" onblur="datesam();">
					~
					<input type='text' name='etime' id='etime' class="searchbox_nomal" onclick="setSens('stime', 'min');" >
				</td>
				<td>
					<span class='schtitle'>카드사</span>
				</td>
				<td>
					<%=useracq%>
				</td>
				<td>
					<span class='schtitle'>사업부</span>
				</td>
				<td>
					<select id="depcd" name="depcd" style="width:165px; height:22px">
						<option value="">:: 사업부선택 ::</option>
						<option value="1">GS25</option>
						<option value="2">GS수퍼</option>
						<option value="3">랄라블라</option>
					</select>
				</td>
				<td>
					<span class='schtitle'>가맹점번호</span>
				</td>
				<td>
					<input type='text' name='mid' id='mid' class="searchbox_large">
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
	
	AccLayout = new dhtmlXLayoutObject("parentId", "2E");
	AccLayout.setSkin("dhx_web");	
	AccLayout.cells("a").hideHeader();
	AccLayout.cells("b").hideHeader();
	
	totalGrid = AccLayout.cells("a").attachGrid();
	var tfields  = "정상건수,반송건수,매출금액,수수료,입금액합계,정상건수,반송건수,매출금액,수수료,입금액합계,실통장금액,입금차액,통장차액";
	var taligns	 = "right,right,right,right,";
	    taligns  += "right,right,right,right,right,";
		taligns	+= "right,right,right,right";
	var tcoltypes  = "ron,ron,ron,ron,ron,";
	    tcoltypes += "ron,ron,ron,ron,ron,";
		tcoltypes += "ron,ron,ron";
	var tsorts	 = "int,int,";
	    tsorts   += "int,int,int,int,int,";
		tsorts	+= "int,int,int,int,int,int";
	var tcolwidth  = "*,*,*,*,*,";
	    tcolwidth += "*,*,*,*,*,";
		tcolwidth += "*,*,*";
	
	totalGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	totalGrid.setSkin("dhx_web");	
	totalGrid.setHeader(tfields);
	totalGrid.setInitWidths(tcolwidth);
	totalGrid.setColTypes(tcoltypes);
	totalGrid.setColAlign(taligns);
	totalGrid.setColSorting(tsorts);
	totalGrid.setNumberFormat("0,000",0,".",",");
	totalGrid.setNumberFormat("0,000",1,".",",");
	totalGrid.setNumberFormat("0,000",2,".",",");
	totalGrid.setNumberFormat("0,000",3,".",",");
	totalGrid.setNumberFormat("0,000",4,".",",");
	totalGrid.setNumberFormat("0,000",5,".",",");
	totalGrid.setNumberFormat("0,000",6,".",",");
	totalGrid.setNumberFormat("0,000",7,".",",");
	totalGrid.setNumberFormat("0,000",8,".",",");
	totalGrid.setNumberFormat("0,000",9,".",",");
	totalGrid.setNumberFormat("0,000",10,".",",");
	totalGrid.setNumberFormat("0,000",11,".",",");
	totalGrid.setNumberFormat("0,000",12,".",",");
	
	totalGrid.init();
	
	
	accountGrid = AccLayout.cells("b").attachGrid();
	var headd	 = "사업부명,가맹점번호,카드사,입금예정일,합계부,#cspan";
	    headd   += ",#cspan,#cspan,#cspan,거래부,#cspan";
	    headd   += ",#cspan,#cspan,#cspan,차액,#cspan";
		headd	+= ",#cspan";
	
	var fields   = "#rspan,#rspan,#rspan,#rspan,정상건수,반송건수";
	    fields  += ",매출금액,수수료,입금액합계①,정상건수,반송건수";
	    fields  += ",매출금액,수수료,입금액합계②,실통장금액③,입반차액(①-②)";
		fields	+= ",입금차액(②-③)";
	
	var filters  = "#select_filter,#select_filter,#select_filter,#select_filter,#text_filter,#text_filter";
	    filters += ",#text_filter,#text_filter,#text_filter,#text_filter,#text_filter";
		filters	+= ",#text_filter,#text_filter,#text_filter,#text_filter,#text_filter";
		filters += ",#text_filter";
	
	var aligns	 = "left,left,center,center,right,right";
	    aligns  += ",right,right,right,right,right";
		aligns	+= ",right,right,right,right,right";
		aligns	+= ",right";
	
	var colTypes  = "ro,ro,ro,ro,ron,ron";
	    colTypes += ",ron,ron,ron,ron,ron";
		colTypes += ",ron,ron,ron,ron,ron";
		colTypes += ",ron";
	
	var sorts	 = "str,str,str,str,int,str";
	    sorts   += ",int,int,int,int,int";
		sorts	+= ",int,int,int,int,int";
		sorts	+= ",int";
	
	var colWidth  = "140,120,100,120,100,80";
	    colWidth += ",120,100,120,100,100";
		colWidth += ",120,100,120,120,120";
		colWidth += ",120";
	
	
	accountGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	accountGrid.setSkin("dhx_web");	
	accountGrid.setHeader(headd);
	accountGrid.setInitWidths(colWidth);
	accountGrid.attachHeader(fields);
	accountGrid.setColTypes(colTypes);
	accountGrid.setColAlign(aligns);
	accountGrid.setColSorting(sorts);
	accountGrid.attachEvent("onRowDblClicked",doOnRowDblClicked);
	accountGrid.setNumberFormat("0,000",4,".",",");
	accountGrid.setNumberFormat("0,000",5,".",",");
	accountGrid.setNumberFormat("0,000",6,".",",");
	accountGrid.setNumberFormat("0,000",7,".",",");
	accountGrid.setNumberFormat("0,000",8,".",",");
	accountGrid.setNumberFormat("0,000",9,".",",");
	accountGrid.setNumberFormat("0,000",10,".",",");
	accountGrid.setNumberFormat("0,000",11,".",",");
	accountGrid.setNumberFormat("0,000",12,".",",");
	accountGrid.setNumberFormat("0,000",13,".",",");
	accountGrid.setNumberFormat("0,000",14,".",",");
	accountGrid.setNumberFormat("0,000",15,".",",");
	accountGrid.setNumberFormat("0,000",16,".",",");
	accountGrid.enableSmartRendering(true);
	accountGrid.enableColSpan(true);
	accountGrid.init();
	
	accountGrid.attachEvent("onXLE", function() {
		accountGrid.setRowTextStyle("total", "text-align:right;font-weight:bold;background-color:#f3fafe;color:#006699;border:1px solid #ffffff;");
		accountGrid.setCellTextStyle("total",0,"text-align:center;font-weight:bold;background-color:#f3fafe;color:#006699;border:1px solid #ffffff;");
		
		AccLayout.items[0].progressOff();
		AccLayout.items[1].progressOff();
		
		accountGrid.setColspan("total",0,4);
		var count=accountGrid.getRowsNum();
		$('#grid_total_rows').html(count);
	}); 


	function glb_where(){
		var stime	= byId("stime").value;
		var etime	= byId("etime").value;
		var mid	= byId("mid").value;
		var acqcd	= $('#acqcd option:selected').val();
		var depcd	= $('#depcd option:selected').val();
		var w	 = "uauth=<%=tuser%>";
 
		w	+= (stime!="") ? "&stime="+stime : "";
		w	+= (etime!="") ? "&etime="+etime : "";
		w	+= (acqcd!="") ? "&acqcd="+acqcd : "";
		w	+= (depcd!="") ? "&depcd="+depcd : "";
		w	+= (mid!="") ? "&mid="+mid : "";

		return w;
	}
	
	function doOnRowDblClicked(rowId, cellInd){
		var stime	= $('#stime').val();
		var etime	= $('#etime').val();
		var mid = accountGrid.cellById(rowId, 1).getValue();
		var seturl = "./sub03_08.jsp?uauth=<%=tuser%>";
		seturl += "&stime="+stime+"&etime="+etime+"&mid="+mid;
		seturl += "&detail_view=sub03_01_ban";

		parent.urlgoDirect(-1, "입금상세조회", seturl);
	}

	function acc_exceldn(){
		/*
		var exsrc = "./ifou_xml_was/excel0301_ban.jsp?" + glb_where();
		
		$.getJSON(exsrc, {
			tags : "mount rainier",
			tagmode : "any",
			format : "json"
		}, 
			function(data, status) {
				if(data["RST"]=="S000"){
					$("#totalarray").val(data["TOTALARRAY"]);
					$("#itemarray").val(data["ITEMARRAY"]);
					
					$("#exceldn").submit();
				}
			}
		);
		*/
		$("#exceldn").submit();
		
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
		totalGrid.clearAndLoad("./ifou_xml_was/total_0301_ban.jsp?"+glb_where(), afterload_prog01,"json");	
		accountGrid.clearAndLoad("./ifou_xml_was/item_0301_ban.jsp?"+glb_where(), afterload_prog02, "json");	
	}	
</script>
<!-- <form id = "exceldn" method = "post" name="exceldn" target="subq" action="./excel/exceldn_0301.jsp">
	<input type="text" name="totalarray" id="totalarray">
	<input type="text" name="itemarray" id="itemarray">
</form> -->
<iframe name="subq" id="subq" style="width:100px; height:100px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>