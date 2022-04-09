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
		function doOnLoad() {
			
			var h = $(window).height();
			var w = $(window).width();

			var hset	= (h-150) + "px";
			
			document.getElementById("parentId").style.height = hset;

			AccLayout.setSizes();

			/*2014 09 25 유병현추가*/
			$("#parentId").css("min-height","600px").css("min-width","860px");
			$(".dhx_cell_cont_layout").css("width","100%");
			/*2014 09 25 유병현추가*/
			
			myCalendar = new dhtmlXCalendarObject(["stime","etime"]);
			myCalendar.attachEvent("onClick", function(d){
				byId("etime").value = myCalendar.getFormatedDate(null,d);
			});
			myCalendar.setWeekStartDay(7);
			myCalendar.setDate("<?=date('Y-m-d');?>");
			myCalendar.hideTime();
			myCalendar.setSkin('dhx_skyblue');
			// init values
			var t = new Date();
			byId("stime").value = "<?=$stime?>";
			byId("etime").value = "<?=$etime?>";

			
		}
		/*2014 09 25 유병현추가*/
		$(window).resize(function(){
			var h = $(document).height();
			var w = $(document).width();
			
			$(".dhx_cell_layout, .dhx_cell_cont_layout").css("width","100%").css("min-width","1160px");
			$(".dhx_cell_cont_layout").css("border","0").css("width","100%").css("border-top","1px solid #c0c0c0").css("border-bottom","1px solid #c0c0c0");
			$(".cont_title").css("margin","0 2px").css("min-width","550px");
			
			var hset	= (h-150) + "px";
			document.getElementById("parentId").style.height = hset;
		});
		/*2014 09 25 유병현추가*/
        var D = 0;
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
	<table width='100%' class='tb00_none'>
	<form name='regular' method='post' action='".$url."'>
	<input type='hidden' name='extitle' id='extitle' value=''>
	<input type='hidden' name='search' value='Y'>
		<tr>
			<td width='4'><img src='./images/sch_left.gif'></td>
			<td background='./images/sch_mid.gif' width='150'><span class='schtitle'>&nbsp;&nbsp; ■ 대사조회</span></td>
			<!--td background='./images/sch_mid.gif' width='285'><?=$Obj->depsel($depcd, $dep_sel, $sto_sel)?></td-->
			<td background='./images/sch_mid.gif'>
				<span class='schtitle'>승인일자</span>
				<input type='text' value='' name='stime' id='stime' style='width:70px; font-size:9pt;' onclick="setSens('stime', 'min');" readonly="true"> 
				~
				<input type='text' value='' name='etime' id='etime' style='width:70px; font-size:9pt;' onclick="setSens('etime', 'max');" readonly="true"> 
				&nbsp;&nbsp;
				<span class='schtitle'>카드번호</span>
				<input type='text' name='card_no' id='card_no' value='<?=$card_no?>' style='width:150px; font-size:9pt;'>
				&nbsp;&nbsp;<span class='schtitle'>승인번호</span>
				<input type='text' name='app_no' id='app_no' value='<?=$app_no?>' style='width:60px; font-size:9pt;'>
			</td>
			<td width='60' background='./images/sch_mid.gif'><img src='./images/sch_btn.png' onclick='search_go();' style='cursor:pointer;'></td>
			<td width='4'><img src='./images/sch_right.gif'></td>
		</tr>
	</form>
	</table>
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
	var tfields  = ",승인건수,승인금액,취소건수,취소금액,합계건수,합계금액,승인건수,승인금액,취소건수,취소금액,합계건수,합계금액";
    var taligns	 = "left, right, right";
		taligns	+= ",right, right, right, right, right, right";
		taligns	+= ",right, right, right, right";
    var tcoltypes  = "ro,ro,ro,ro,ro,ro,ro";
        tcoltypes += ",ro,ro,ro,ro,ro,ro,ro";
    var tsorts	 = "str, int,int,int,int,int";
        tsorts   += ",int,int,int,int,int,int,int";
    var tcolwidth   = "150,*,*,*,*,*";
		tcolwidth	+= ",*,*,*,*";

	totalGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	totalGrid.setSkin("dhx_web");	
	totalGrid.setHeader(tfields);
	totalGrid.setInitWidths(tcolwidth);
	totalGrid.setColTypes(tcoltypes);
	totalGrid.setColAlign(taligns);
	totalGrid.setColSorting(tsorts);
	totalGrid.init();

	accountGrid = AccLayout.cells("b").attachGrid();
	
	var headd	 = "순번,포스자료,#cspan,#cspan,#cspan";
    headd   += ",#cspan,#cspan,#cspan,#cspan,#cspan";
    headd   += ",#cspan,#cspan,#cspan,서버자료,#cspan,#cspan";
	headd	+= ",#cspan,#cspan,#cspan";

var fields   = "#rspan,점포명,점포코드,송장번호,접수일자,";
	fields	+= "집하일자,결제구분,승인일자,";
	fields	+= "승인번호,승인구분,카드번호,금액,승인구분,";
	fields	+= "승인일자,승인번호,카드번호,금액";
	 
var filters  = "&nbsp;,#select_filter,#select_filter,#select_filter,#select_filter";
	filters	+= ",#select_filter,#select_filter,#select_filter";
	filters	+= ",#text_filter,#select_filter,#text_filter,#text_filter,#select_filter";
	filters	+= ",#select_filter,#text_filter,#text_filter,#text_filter";
	
var aligns	 = "center,left,left,left,left";
	aligns	+= ",center,center,center";
	aligns	+= ",left,center,left,right,left";
	aligns	+= ",center,center,center,right";

var colTypes  = "ro,ro,ro,ro,ro";
	colTypes += ",ro,ro,ro";
	colTypes += ",ro,ro,ro,ron,ro";
	colTypes += ",ro,ro,ro,ron";
	
var sorts	 = "int,str,str,str,str";
	sorts	+= ",str,str,str";
	sorts	+= ",str,str,str,int,str";
	sorts	+= ",str,str,str,int";
	
var colWidth	 = "60,160,100,100,100";
	colWidth	+= ",80,80,100";
	colWidth	+= ",80,80,120,100,80";
	colWidth	+= ",80,80,120,100";
	
	
	accountGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	accountGrid.setSkin("dhx_web");	
	accountGrid.setHeader(headd);
	accountGrid.setInitWidths(colWidth);
	accountGrid.attachHeader(fields);
	accountGrid.attachHeader(filters);
	accountGrid.setColTypes(colTypes);
	accountGrid.setColAlign(aligns);
	accountGrid.setColSorting(sorts);
	accountGrid.setNumberFormat("0,000",11,".",",");
	accountGrid.setNumberFormat("0,000",16,".",",");
	//accountGrid.attachEvent("onRowDblClicked",doOnRowDblClicked);
	accountGrid.init();

	accountGrid.attachEvent("onXLE", function() {
		AccLayout.items[0].progressOff();
		AccLayout.items[1].progressOff();
		var count=accountGrid.getRowsNum();
		$('#grid_total_rows').html(count);
	}); 
	accountGrid.attachEvent("onXLE", function() {
		AccLayout.items[0].progressOff();
	}); 

	function doOnRowDblClicked(rowId, cellInd){
		detail_view(rowId);
	}

	function acc_exceldn(){
		var stime	= $('#stime').val();
		var etime	= $('#etime').val();
		var w	= "stime="+stime+"&etime="+etime;

		var exsrc	= "./excel/excel0109.php?"+w;
		$('#subq').attr('src', exsrc);
	}
	
	

	function search_go(){

		var stime	= $('#stime').val();
		var etime	= $('#etime').val();
		var cardno	= $('#card_no').val();
		var appno	= $('#app_no').val();

		var w	= "stime="+stime+"&etime="+etime+"&cardno="+cardno+"&appno="+appno;

		AccLayout.items[0].progressOn();
		AccLayout.items[1].progressOn();
		totalGrid.clearAndLoad("./xmlparse/sub01_09totxml.php?"+w);
		accountGrid.clearAndLoad("./xmlparse/sub01_09xml.php?"+w);	
		
		
	}
</script>
<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>