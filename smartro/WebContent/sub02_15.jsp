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
//사용안함
	Decoder decoder = Base64.getDecoder();
	
	String tuser = (String)session.getAttribute("uinfo");
	
	String tmp_tid = (String)session.getAttribute("usertid");
	byte[] usertid_buf = decoder.decode(tmp_tid);
	String usertid = new String(usertid_buf, "UTF-8");
	
	String tmp_depo = (String)session.getAttribute("userdepo");
	byte[] userdepo_buf = decoder.decode(tmp_depo);
	String userdepo = new String(userdepo_buf, "UTF-8");

	Date nowTime = new Date();
	SimpleDateFormat nowYear = new SimpleDateFormat("yyyy");
	SimpleDateFormat nowMonth = new SimpleDateFormat("MM");
	int year = Integer.parseInt(nowYear.format(nowTime));
	int month = Integer.parseInt(nowMonth.format(nowTime));
	
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
<style type="text/css">
	.layer {display:none; position:fixed; _position:absolute; top:0; left:0; width:100%; height:100%; z-index:100;}
	.layer .bg {position:absolute; top:0; left:0; width:100%; height:100%; background:#000; opacity:.5; filter:alpha(opacity=90);}
	.layer .pop-layer {display:block;}

	.pop-layer {display:none; position: absolute; top: 50%; left: 50%; width: 570px; height:auto;  background-color:#fff; border: 5px solid #3571B5; z-index: 10;}	
	.pop-layer .pop-container {padding: 20px 25px;}
	.pop-layer p.ctxt {color: #666; line-height: 25px;}
	.pop-layer .btn-r {width: 100%; margin:10px 0 20px; padding-top: 10px; border-top: 1px solid #DDD; text-align:right;}

	a.cbtn {display:inline-block; height:25px; padding:0 14px 0; border:1px solid #304a8a; background-color:#3f5a9d; font-size:13px; color:#fff; line-height:25px;}	
	a.cbtn:hover {border: 1px solid #091940; background-color:#1f326a; color:#fff;}
	a.sbtn {display:inline-block; height:25px; padding:0 14px 0; border:1px solid #304a8a; background-color:#3f5a9d; font-size:13px; color:#fff; line-height:25px;}	
	a.sbtn:hover {border: 1px solid #091940; background-color:#1f326a; color:#fff;}
</style>

<script>
var myCalendar;
var AccLayout, accountGrid;
function doOnLoad() {
	
	var h = $(window).height();
	var w = $(window).width();

	var hset	= (h-123) + "px";


	document.getElementById("parentId").style.height = hset;

	AccLayout.setSizes();

	/*2014 09 25 유병현추가*/
	$("#parentId").css("min-height","600px").css("min-width","860px");
	$(".dhx_cell_cont_layout").css("width","100%");
	/*2014 09 25 유병현추가*/
	var sYear=2017;
	var eYear=2030;
	var sMonth=1;
	var eMonth=12;
	var strYear="";
	var strMonth="";
	var today = new Date();
	var mm = today.getMonth()+1; //January is 0!
	var yyyy = today.getFullYear();
	var cnt  = 0;
	
		
	for(var i = sYear; i<=eYear; i++ )
	{		
		strYear +="<option value="+i+" >"+i+"</option>";
	}
	for(var i = sMonth; i<=eMonth; i++ )
	{
		strMonth +="<option value="+i+" >"+i+"</option>";
	}
	
	
	document.getElementById("year").innerHTML = strYear;
	document.getElementById("month").innerHTML = strMonth;
	
	document.getElementById("endyear").innerHTML = strYear;
	document.getElementById("endmonth").innerHTML = strMonth;
	if(mm<10) {
	    mm='0'+mm
	} 
	
	document.getElementById("month").selectedIndex = mm-1;
	document.getElementById("year").selectedIndex = yyyy-2017;
	
	document.getElementById("endmonth").selectedIndex = mm-1;
	document.getElementById("endyear").selectedIndex = yyyy-2017;
	
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
	input#stime, input#etime {
		font-size: 9pt;
		background-color: #fafafa;
		border: #c0c0c0 1px solid;
		width: 100px;
	}
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
<body onload="doOnLoad();">
<div class='sub_content'>
	<div class='sub_content_space'></div>
	<div class='cont_title'>
		<form name='regular' method='post' action='".$url."'>
			<input type='hidden' name='extitle' id='extitle' value=''>
			<input type='hidden' name='search' value='Y'>
			<table class="tb" border="1" bordercolor="#e6e6e6" cellpadding="5" style="width: 100%; border-collapse: collapse;">
			<colgroup>
			<col class="cellC" style="width:180px;">
			<col class="cellL" style="width:180px">
			<col class="cellC" style="width:180px;">
			<col class="cellL" style="width:100px">
			<col class="cellC" style="width:100px;">
			<col class="cellL" style="width:100px">
			<col class="cellC" style="width:100px;">
			<col class="cellL">
			<col class="cellL" style="width:100px;">
			</colgroup>
			<tbody>
			<tr height="36">
				
				<td>
					<span class='schtitle'> 검색 일자 </span>
				</td>
				<td colspan="5"> 
					<select style = "width:70px;" id="year" name="year"></select>
					<select style = "width:50px;" id="month" name="month"></select> ~
					<select style = "width:70px;" id="endyear" name="endyear"></select>
					<select style = "width:50px;" id="endmonth" name="endmonth"></select>
				</td>				
				<td colspan="3" align="right"><img src='./images/btn/btn_search.gif'  onclick='search_go();' style='cursor:pointer;'></td>
			</tr>
			</tbody>
		</table>
		</form>
	</div>
	<div style="position: relative; top: 0px; left: 0px; width: 100%; height:10px;"></div>
	<div class="control_div">
		<table class="tb00_none" width="100%">
			<tr>
				<td colspan="2"></td>
			</tr>
			<tr>
								
				<td align="right">					
					<span id="conttxt" class='button large icon' onclick="acc_exceldn();"><span class='excel'></span><a href='#'>엑셀다운로드</a></span>
				</td>
				
			</tr>
		</table>
	</div>
	<div style="position: relative; top: 0px; left: 0px; width: 100%; height:5px;"></div>

	<div id="parentId" style="position: relative; top: 0px; left: 0px; width: 100%; height:500px;"></div>
</div>
<script type="text/javascript">
	
AccLayout = new dhtmlXLayoutObject("parentId", "1C");
AccLayout.setSkin("dhx_web");	
AccLayout.cells("a").hideHeader();

accountGrid = AccLayout.cells("a").attachGrid();
var headd	 = "거래년월,매입사,GS25,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,";
	headd   += "수퍼,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,";
	headd   += "랄라블라,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan";
				

var fields   = "#rspan,#rspan,신용,#cspan,체크,#cspan,합계,#cspan";
	fields  += ",신용비율,체크비율,";
	fields  += "신용,#cspan,체크,#cspan,합계,#cspan,신용비율,체크비율,";
	fields  += "신용,#cspan,체크,#cspan,합계,#cspan,신용비율,체크비율,";

var fields1   = "#rspan,#rspan,건수,금액,건수,금액,건수,금액";
	fields1  += ",#rspan,#rspan,";
	fields1  += "건수,금액,건수,금액,건수,금액,#rspan,#rspan,";
	fields1  += "건수,금액,건수,금액,건수,금액,#rspan,#rspan";
	
var aligns	 = "center,center,center,center";
	aligns  += ",center,center,center,center";
	aligns  += ",center,center,center,center";
	aligns  += ",center,center,center,center";
	aligns  += ",center,center,center,center";
	aligns  += ",center,center,center,center";
	aligns  += ",center,center";

var colTypes  = "ro,ro,ron,ron,ron,ron,ron,ron";
	colTypes += ",ron,ron,ron,ron";
	colTypes += ",ron,ron,ron,ron";
	colTypes += ",ron,ron,ron,ron";
	colTypes += ",ron,ron,ron,ron";
	colTypes += ",ron,ron,ron,ron";
	colTypes += ",ron,ron";

var sorts	 = "str,str,int,int,int,int,int,int";
	sorts   += ",int,int,int,int";
	sorts   += ",int,int,int,int";
	sorts   += ",int,int,int,int";
	sorts   += ",int,int,int,int";
	sorts   += ",int,int,int,int";
	sorts   += ",int,int";

var colWidth  = "70,70,75,105,75,105,75,105,75,75";
	colWidth += ",75,105,75,105,70,105,75,75";
	colWidth += ",75,105,75,105,70,105,75,75";

	accountGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	accountGrid.setSkin("dhx_web");	
	accountGrid.setHeader(headd);
	accountGrid.setInitWidths(colWidth);
	accountGrid.attachHeader(fields);
	accountGrid.attachHeader(fields1);
	accountGrid.setColTypes(colTypes);
	accountGrid.setColAlign(aligns);
	accountGrid.setColSorting(sorts);
	accountGrid.setNumberFormat("0,000",2,".",",");
	accountGrid.setNumberFormat("0,000",3,".",",");
	accountGrid.setNumberFormat("0,000",4,".",",");
	accountGrid.setNumberFormat("0,000",5,".",",");
	accountGrid.setNumberFormat("0,000",6,".",",");
	accountGrid.setNumberFormat("0,000",7,".",",");
	accountGrid.setNumberFormat("0,000",10,".",",");
	accountGrid.setNumberFormat("0,000",11,".",",");
	accountGrid.setNumberFormat("0,000",12,".",",");
	accountGrid.setNumberFormat("0,000",13,".",",");
	accountGrid.setNumberFormat("0,000",14,".",",");
	accountGrid.setNumberFormat("0,000",15,".",",");
	accountGrid.setNumberFormat("0,000",18,".",",");
	accountGrid.setNumberFormat("0,000",19,".",",");
	accountGrid.setNumberFormat("0,000",20,".",",");
	accountGrid.setNumberFormat("0,000",21,".",",");
	accountGrid.setNumberFormat("0,000",22,".",",");
	accountGrid.setNumberFormat("0,000",23,".",",");
	accountGrid.init();
	
	function glb_where(){
		
		var year	= byId("year").value;
		var month	= byId("month").value;
		var endyear	= byId("endyear").value;
		var endmonth	= byId("endmonth").value;	
		var day = 1;
		var endday = 31;
		
		
		if(day<10){
			day='0'+day;
		}
		
		if(month<10) {
			month='0'+month;
		} 
		
		if (endmonth < 10) {
			endmonth ='0'+endmonth;
		}
		
		var w	 = "uauth=<?=$uauth?>";
		var date 	= year+month+day;
		var enddate = endyear+endmonth+endday;
		
		w	+= (date!="") ? "&date="+date : "";
		w   += (enddate!="") ? "&enddate="+enddate : "";
		
		return w;
	}

	function acc_exceldn(){
		var exsrc	= "./ifou_xml_was/excel02_15.jsp?"+glb_where();
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
	
	accountGrid.attachEvent("onXLE", function() {
		AccLayout.items[0].progressOff();
		AccLayout.items[1].progressOff();

	}); 

	function search_go(){
		AccLayout.items[0].progressOn();
		accountGrid.clearAndLoad("./ifou_xml_was/total_0215.jsp?"+glb_where(), "json");	
	}
</script>
<form id="exceldn" method="post" name="exceldn" target="subq" action="./excel/exceldn_0215.jsp" enctype="application/x-www-form-urlencoded">
	<input type="hidden" name="totalarray" id="totalarray">
</form>
</body>
</html>