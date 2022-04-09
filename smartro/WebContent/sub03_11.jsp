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
	
	//2022.02.21 당일 날짜에서 -7일로 설정하기
	Calendar cal = Calendar.getInstance();
	cal.setTime(nowTime);
	cal.add(Calendar.DATE, -7);
	String setTime = setdate.format(cal.getTime());
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
	a.cbtn {display:inline-block; height:25px; padding:0 14px 0; border:1px solid #304a8a; background-color:#3f5a9d; font-size:13px; color:#fff; line-height:25px;}	
	a.cbtn:hover {border: 1px solid #091940; background-color:#1f326a; color:#fff;}
	a.sbtn {display:inline-block; height:25px; padding:0 14px 0; border:1px solid #304a8a; background-color:#3f5a9d; font-size:13px; color:#fff; line-height:25px;}	
	a.sbtn:hover {border: 1px solid #091940; background-color:#1f326a; color:#fff;}
</style>

<script>
	var myCalendar, reqCalendar, myTabbar;
	var AccLayout, accountGrid, tabbar2, mygrid2, mygrid3;

	// 로딩
	function doOnLoad() {
			var h = $(window).height();
			var w = $(window).width();

			var hset	= (h-200) + "px";
	
			document.getElementById("parentId").style.height = hset;

			AccLayout.setSizes();

			$("#parentId").css("min-height","600px").css("min-width","860px");
			$(".dhx_cell_cont_layout").css("width","100%");
			
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

			reqCalendar = new dhtmlXCalendarObject(["reqstime","reqetime"]);
			reqCalendar.attachEvent("onClick", function(d){
				byId("reqetime").value = reqCalendar.getFormatedDate(null,d);
			});

			reqCalendar.setWeekStartDay(7);
			reqCalendar.setDate("<%=setdate.format(nowTime)%>");
			reqCalendar.hideTime();
			reqCalendar.setSkin('dhx_skyblue');		
			
			byId("reqstime").value = "<%=setTime%>";
			byId("reqetime").value = "<%=setdate.format(nowTime)%>";
		}

		/*2014 09 25 유병현추가*/
		$(window).resize(function(){
			var h = $(document).height();
			var w = $(document).width();
			
			$(".dhx_cell_layout, .dhx_cell_cont_layout").css("width","100%").css("min-width","1160px");
			$(".dhx_cell_cont_layout").css("border","0").css("width","100%").css("border-top","1px solid #c0c0c0").css("border-bottom","1px solid #c0c0c0");
			$(".cont_title").css("margin","0 2px").css("min-width","550px");
			
			var hset	= (h-190) + "px";
			document.getElementById("parentId").style.height = hset;
		});

	function grid_refresh(){
		accountGrid.clearAndLoad("./ifou_xml_was/total_0311.jsp?"+wherequery(), afterload_prog01, "json");
	}
</script>
<script type="text/javascript">
	var D = 0;
	function setSens(id, k) {
		if ( k == "min" ){
			myCalendar.setSensitiveRange(byId(id).value, null);
		}else{
			if(D == 0){
				myCalendar.setSensitiveRange(null,null);
			}else{
				myCalendar.setSensitiveRange(null, byId(id).value);
			}
			D = D+1;
		}
	}

	function setReqSens(id, k) {
		if ( k == "min" ){
			reqCalendar.setSensitiveRange(byId(id).value, null);
		}else{
			if(D == 0){
				reqCalendar.setSensitiveRange(null,null);
			}else{
				reqCalendar.setSensitiveRange(null, byId(id).value);
			}
			D = D+1;
		}
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
		<form name="exceldn" id="exceldn"  method="post" action='./excel/exceldn_0311.jsp'>
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
					<span class='schtitle'>청구일자</span>
				</td>
				<td>
					<input type='text' name='reqstime' id='reqstime'  class="searchbox_nomal" onclick="setReqSens('reqetime', 'max');" onblur="datesam();">
					~
					<input type='text' name='reqetime' id='reqetime'  class="searchbox_nomal" onclick="setReqSens('reqstime', 'min');" >
				</td>
				<td>
					<span class='schtitle'>승인일자</span>
				</td>
				<td>
					<input type='text' name='stime' id='stime'  class="searchbox_nomal" onclick="setSens('etime', 'max');" onblur="datesam();">
					~
					<input type='text' name='etime' id='etime'  class="searchbox_nomal" onclick="setSens('stime', 'min');" >
				</td>								
				<%-- <td>
					<span class='schtitle'>사업부</span>
				</td>
				<td>
					<%=userdepo %>
				</td> --%>
				<td>
					<span class='schtitle'></span>
				</td>
				<td></td>
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
				<td>
					Total Rows : <span id="grid_total_rows"></span>건													
					<span id="tmp"></span>
				</td>
				
				<td align="right">					
					<span id="conttxt" class='button large icon' onclick="acc_exceldn();"><span class='excel'></span><a href='#'>엑셀다운로드</a></span>
				</td>
				
			</tr>
		</table>
	</div>
	<div style="position: relative; top: 0px; left: 0px; width: 100%; height:5px;"></div>

	<div id="parentId" style="position: relative; top: 0px; left: 0px; width: 100%; height:250px;"></div>
</div>
<script type="text/javascript">

	AccLayout = new dhtmlXLayoutObject("parentId", "1C");
	AccLayout.setSkin("dhx_web");
	AccLayout.cells("a").hideHeader();

	accountGrid = AccLayout.cells("a").attachGrid();

	var fields   = "순번,청구일자,사업자번호,매출일(FROM),매출일(TO),";
		fields	+= "GS25합계건수,GS25합계금액,수퍼합계건수,수퍼합계금액,랄라블라합계건수,";
		fields  += "랄라블라합계금액,합계건수,합계금액";
	

	var aligns	 = "center,center,center,center,center";
		aligns	+= ",right,right,right,right,right";
		aligns	+= ",right,right,right";


	var colTypes  = "ro,ro,ro,ro,ro";
		colTypes += ",ron,ron,ron,ron,ron";
		colTypes += ",ron,ron,ron";

	var sorts	 = "int,str,str,str,str";
		sorts	+= ",int,int,int,int,int";
		sorts	+= ",int,int,int";

	var colWidth	 = "60,100,100,100,100";
		colWidth	+= ",120,120,120,120,120";
		colWidth	+= ",120,120,120";
	
	accountGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	accountGrid.setSkin("dhx_web");
	accountGrid.setHeader(fields);
	accountGrid.setInitWidths(colWidth);
	accountGrid.setColTypes(colTypes);
	accountGrid.setColAlign(aligns);
	accountGrid.setColSorting(sorts);
	accountGrid.attachEvent("onRowDblClicked",doOnRowDblClicked);
	accountGrid.enableSmartRendering(true,100);
	accountGrid.setNumberFormat("0,000",5,".",",");
	accountGrid.setNumberFormat("0,000",6,".",",");
	accountGrid.setNumberFormat("0,000",7,".",",");
	accountGrid.setNumberFormat("0,000",8,".",",");
	accountGrid.setNumberFormat("0,000",9,".",",");
	accountGrid.setNumberFormat("0,000",10,".",",");
    accountGrid.setNumberFormat("0,000",11,".",",");
    accountGrid.enableColSpan(true);
	accountGrid.init();
	
	accountGrid.attachEvent("onXLE", function() {
		AccLayout.items[0].progressOff();		
		var count=accountGrid.getRowsNum();
		$('#grid_total_rows').html(count);
		
		accountGrid.setColspan("total",0,3);
		
		accountGrid.setRowTextStyle("total", "text-align:right;font-weight:bold;background-color:#f3fafe;color:#006699;border:1px solid #ffffff;");
		accountGrid.setCellTextStyle("total",0,"text-align:center;font-weight:bold;background-color:#f3fafe;color:#006699;border:1px solid #ffffff;");
	});

	
	function doOnRowDblClicked(rowId, cellInd, reqdate){
		var w	= "?uauth=<%=tuser%>&trtp=P&deposeq="+rowId+"&reqdate="+rowId.substr(0,8);

		parent.urlgoDirect(-1, "청구이력상세조회", "sub03_10detail.jsp"+w);
	}
	
	function search_go(){

		var daydiff	= getDateDiff($('#reqetime').val(),$('#reqstime').val());
		
		if(daydiff > 31){
			dhtmlx.alert({
				type:"alert-warning",
				text:"검색기간이 초과되었습니다.<br>검색 기간은 31일까지 가능합니다."
			});
			return false;
		}
		
		AccLayout.items[0].progressOn();
		accountGrid.clearAndLoad("./ifou_xml_was/total_0311.jsp?"+wherequery(), afterload_prog01, "json");
	}
	
	function afterload_prog01(){
		AccLayout.items[0].progressOff();
	}

	// 조건 파라메터 셋.
	function wherequery(){
		var stime	= $('#stime').val();
		var etime	= $('#etime').val();		
		var reqstime	= $('#reqstime').val();
		var reqetime	= $('#reqetime').val();
		
		var w	 = "uauth=<%=tuser%>";

		w	+= (stime!="") ? "&stime="+stime : "";
		w	+= (etime!="") ? "&etime="+etime : "";
		w	+= (reqstime!="") ? "&reqstime="+reqstime : "";
		w	+= (reqetime!="") ? "&reqetime="+reqetime : "";
		
		return w;
	}
	
	// 엑셀다운.
	function acc_exceldn(){		
		
		$('#exceldn').submit();
	}
	
</script>
<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>
