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
	//쿼리 주석처리 돼있음
	
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
		}
		/*2014 09 25 유병현추가*/
		$(window).resize(function(){
				var h = $(document).height();
			var w = $(document).width();
			
			$(".dhx_cell_layout, .dhx_cell_cont_layout").css("width","100%").css("min-width","1160px");
			$(".dhx_cell_cont_layout").css("border","0").css("width","100%").css("border-top","1px solid #c0c0c0").css("border-bottom","1px solid #c0c0c0");
			$(".cont_title").css("margin","0 2px").css("min-width","550px");
			
			var xset	= (x-150) + "px";
			document.getElementById("parentId").style.height = hset;
		});
		/*2014 09 25 유병현추가*/

		$(function(){
			$(document).on("keyup", "input:text[numberOnly]", function() {$(this).val( $(this).val().replace(/[^0-9]/gi,"") );});
			$(document).on("keyup", "input:text[datetimeOnly]", function() {$(this).val( $(this).val().replace(/[^0-9:\-]/gi,"") );});
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
	<form name='regular' method='post' action='".$url."'>
		<input type='hidden' name='extitle' id='extitle' value=''>
		<input type='hidden' name='search' value='Y'>
		<table class="tb" border="1" bordercolor="#e6e6e6" cellpadding="5" style="width: 100%; border-collapse: collapse;">
		<colgroup>
		<col class="cellC" style="width:100px;">
		<col class="cellL" style="width:200px">
		<col class="cellC" style="width:100px;">
		<col class="cellL" style="width:180px">
		<col class="cellC" style="width:100px;">
		<col class="cellL" style="width:130px">
		<col class="cellC" style="width:100px;">
		<col class="cellL">
		<col class="cellL" style="width:100px;">
		</colgroup>
			<tbody>
			<tr height="36">
				<td>
					<span class='schtitle'>년/월 선택</span>
				</td>
				<td>
					<select name="syear" id="syear" style="width:80px; height:22px;">
					<!-- 
						<option value="">::연도선택::</option>
						<?
						for($i=date("Y");$i>date("Y")-10;$i--){
						?>
						<option value="<?=$i?>" <?if($SearchYear==$i){echo "selected";}?>><?=$i?>년</option>
						<?}?>
					 -->
					</select>
					<select name="smon" id="smon" style="width:80px; height:22px;">
					<!--
						<option value="">::월선택::</option>
						<?
						for($i=1;$i<=12;$i++){
							$mon = STR_PAD($i,2,"0",STR_PAD_LEFT);
						?>
						<option value="<?=$i?>" <?if($SearchMon==$mon){echo "selected";}?>><?=$mon?>월</option>
						<?}?>
					 -->
					</select>
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
				<td colspan="3">
					<input type='text' name='mid' id='mid' class="searchbox_large">
					<span class='schtitle'>										* 안마의자, 쉴낙원 매출 제외</span>
				</td>
				<td rowspan="2" align="right"><img src='./images/btn/btn_search.gif' onclick='search_go();' style='cursor:pointer;'></td>
			</tr>
			</tbody>
		</table>
		</form>
	</div>
	<div style="position: relative; top: 0px; left: 0px; width: 100%; height:10px;"></div>
	<div class="control_div">
		<table class="tb00_none" width="100%">
			<tr>
				<td>■ Total Rows : <span id="grid_total_rows"></span>건</td>
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
    AccLayout = new dhtmlXLayoutObject("parentId", "1C");
	AccLayout.setSkin("dhx_web");	
	AccLayout.cells("a").hideHeader();

	accountGrid = AccLayout.cells("a").attachGrid();
	
	var fields   = "거래일자,사업부,카드사,가맹점번호,한도금액";
		fields	+= ",이전한도금액,담보금액,사용금액,한도초과금액,잔여금액,비고";
	
	var filters	 = "#select_filter,#select_filter,#select_filter,#select_filter,#text_filter";
		filters += ",#text_filter,#text_filter,#text_filter,#text_filter,#text_filter,#text_filter";
	
    var aligns	 = "left,left,left,left,right";
		aligns	+= ",right,right,right,right,right,right";
	
    var colTypes  = "ro,ro,ro,ro,ron";
        colTypes += ",ron,ron,ron,ron,ron,ron";
	
    var sorts	 = "str,str,str,str,int";
        sorts   += ",int,int,int,int,int,str";
    
    var colWidth   = "150,150,150,150,*";
        colWidth  += ",*,*,*,*,*,*";

	accountGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	accountGrid.setSkin("dhx_web");	
	accountGrid.setHeader(fields);
	accountGrid.setInitWidths(colWidth);
	accountGrid.setColTypes(colTypes);
	accountGrid.setColAlign(aligns);
	accountGrid.setColSorting(sorts);
	accountGrid.attachEvent("onRowDblClicked",doOnRowDblClicked);
	accountGrid.enableSmartRendering(true,50);
	accountGrid.setNumberFormat("0,000",4,".",",");
	accountGrid.setNumberFormat("0,000",5,".",",");
	accountGrid.setNumberFormat("0,000",6,".",",");
	accountGrid.setNumberFormat("0,000",7,".",",");
	accountGrid.setNumberFormat("0,000",8,".",",");
	accountGrid.setNumberFormat("0,000",9,".",",");
	accountGrid.init();
	
	accountGrid.attachEvent("onXLE", function() {
		AccLayout.items[0].progressOff();
		var count=accountGrid.getRowsNum();
		$('#grid_total_rows').html(count);
	}); 

	function doOnRowDblClicked(rowId, cellInd){
		var regex = /[^0-9]/gi;
		var rowinfo  = "&rowId="+rowId+"&appdd="+accountGrid.cells(accountGrid.getRowId(rowId-1), 0).getValue().replace(regex, "");
			rowinfo	+= "&mid="+accountGrid.cells(accountGrid.getRowId(rowId-1), 3).getValue();
			rowinfo += "&memo="+accountGrid.cells(accountGrid.getRowId(rowId-1), 10).getValue();
		var url = "./card_limit_mod.html?uauth=<?=$uauth?>"+rowinfo;
		detail_pop_view(url, 840, 450);
	}

	function acc_exceldn(){
		var exsrc	= "./excel/excel0202.php?"+glb_where();
		$('#subq').attr('src', exsrc);
	}

	function glb_where(){
		//var stime	= byId("stime").value;
		//var etime	= byId("etime").value;
		//var appno	= byId("appno").value;
		var syear	= $('#syear option:selected').val();
		var smon	= $('#smon option:selected').val();

		//var samt	= byId("samt").value;
		//var eamt	= byId("eamt").value;
		var mid	= byId("mid").value;
 		var depcd	= $('#depcd option:selected').val(); 
		
 		var w	 = "uauth=<%=tuser%>";
 		
		w	+= (syear!="") ? "&syear="+syear : "";
		w	+= (smon!="") ? "&smon="+smon : "";
		//w	+= (samt!="") ? "&samt="+samt : "";
		//w	+= (eamt!="") ? "&eamt="+eamt : "";
 		w	+= (depcd!="") ? "&depcd="+depcd : ""; 
		w	+= (mid!="") ? "&mid="+mid : "";
		
		return w;
	}

	function search_go(){

		if($('#syear').val()==""){
			dhtmlx.alert({
				type:"alert-warning",
				text:"검색하실 년도를 선택하여 주십시오."
			});
			return false;
		}

		if($('#smon').val()==""){
			dhtmlx.alert({
				type:"alert-warning",
				text:"검색하실 월을 선택하여 주십시오."
			});
			return false;
		}

		//AccLayout.items[0].progressOn();
		//accountGrid.clearAndLoad("./xmlparse/sub02_0215xml.php?"+glb_where());	
	}
</script>
<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>