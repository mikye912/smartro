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
			// init values
			var t = new Date();
			byId("stime").value = "<%=setdate.format(nowTime)%>";
			byId("etime").value = "<%=setdate.format(nowTime)%>";

			
		/*2014 09 25 유병현추가*/
			$("#parentId").css("min-height","600px").css("min-width","860px");
			$(".dhx_cell_cont_layout").css("width","100%");
			/*2014 09 25 유병현추가*/
		}
	
		function setSens(id, k) {
			myCalendar.setSensitiveRange(null,null);
		}
		
		function byId(id) {
			return document.getElementById(id);
		}

		function detail_view(obj){
			var lVal = (screen.aWidth - 700) / 2;
			var tVal = (screen.availHeight - 450) / 2;

			var options = {
				height: 450, // sets the height in pixels of the window.
				width: 700, // sets the width in pixels of the window.
				toolbar: 0, // determines whether a toolbar (includes the forward and back buttons) is displayed {1 (YES) or 0 (NO)}.
				scrollbars: 0, // determines whether scrollbars appear on the window {1 (YES) or 0 (NO)}.
				status: 0, // whether a status line appears at the bottom of the window {1 (YES) or 0 (NO)}.
				resizable: 0, // whether the window can be resized {1 (YES) or 0 (NO)}. Can also be overloaded using resizable.
				left: lVal, // left position when the window appears.
				top: tVal, // top position when the window appears.
				center: 1, // should we center the window? {1 (YES) or 0 (NO)}. overrides top and left
				createnew: 0, // should we create a new window for each occurance {1 (YES) or 0 (NO)}.
				location: 0, // determines whether the address bar is displayed {1 (YES) or 0 (NO)}.
				menubar: 0 // determines whether the menu bar is displayed {1 (YES) or 0 (NO)}.
			};

			var parameters = "location=" + options.location +
							 ",menubar=" + options.menubar +
							 ",height=" + options.height +
							 ",width=" + options.width +
							 ",toolbar=" + options.toolbar +
							 ",scrollbars=" + options.scrollbars +
							 ",status=" + options.status +
							 ",resizable=" + options.resizable +
							 ",left=" + options.left +
							 ",screenX=" + options.left +
							 ",top=" + options.top +
							 ",screenY=" + options.top;

			nWindow = window.open("./detail_cash_view.html?seq="+obj,"_blank",parameters);
			nWindow.opener = self;
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
	</style>
</head>
<body onload="doOnLoad();">
<div class='sub_content'>
	<div class='sub_content_space'></div>
	<div class='cont_title'>
	<form name="exceldn" id="exceldn"  method="post" action="./excel/exceldn_0107.jsp">
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
					<span class='schtitle'>승인금액</span>
				</td>
				<td>
					<input type='text' name='samt' id='samt' class="searchbox_nomal">
					~
					<input type='text' name='eamt' id='eamt' class="searchbox_nomal">
				</td>
				<td>
					<span class='schtitle'>사업부</span>
				</td>
				<td  colspan="3">
					<%=userdepo%>
				</td>
				
				<td rowspan="2" align="center"><img src='./images/btn/btn_search.gif' onclick='search_go();' style='cursor:pointer;'></td>
			</tr>
			<tr height="36">
				<!-- <td>
					<span class='schtitle'>신분확인번호</span>
				</td>
				<td>
					<input type="text" name="cardno" id="cardno" class="searchbox_large">
				</td> -->
				<td>
					<span class='schtitle'>발급유형</span>
				</td>
				<td>
					<select name="cardtp" id="cardtp" class="searchbox_drop">	
						<option value="">::발급유형선택::</option>
						<option value="0">소득공제</option>
						<option value="1">지출증빙</option>
						<option value="2">자진발급</option>
					</select>
				</td>
				<td>
					<span class='schtitle'>승인번호</span>
				</td>
				<td>
					<input type="text" name="appno" id="appno" class="searchbox_large">
				</td>
				<td>
					<span class='schtitle'>승인구분</span>
				</td>
				<td  colspan="3">
					<input type="checkbox" name="auth01" id="auth01" checked onclick="auth_chk(1);" value="Y">전체거래
					<input type="checkbox" name="auth02" id="auth02" onclick="auth_chk(2);" value="Y">승인거래
					<input type="checkbox" name="auth03" id="auth03" onclick="auth_chk(3);" value="Y">취소거래
				</td>

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
	<div id="parentId" style="position: relative; top: 0px; left: 0px; width: 100%; height:300px;"></div>
</div>
<script type="text/javascript">
	AccLayout = new dhtmlXLayoutObject("parentId", "1C");
	AccLayout.setSkin("dhx_web");	
	AccLayout.cells("a").hideHeader();

	accountGrid = AccLayout.cells("a").attachGrid();

	var fields   = "순번,사업부,단말기명,단말기번호,승인일자";
		fields	+= ",승인시간,원승인일자,승인번호,승인구분,신분확인번호";
		fields	+= ",금액,거래구분,회선구분,취소사유,응답코드";
		fields	+= ",거래고유번호,운송장번호,응답메시지";

	var filters  = "&nbsp;,#select_filter,#select_filter,#select_filter,#select_filter";
		filters	+= ",#text_filter,#select_filter,#text_filter,#select_filter,#text_filter";
		filters	+= ",#text_filter,#select_filter,#select_filter,#select_filter,#select_filter";
		filters	+= ",#text_filter,#text_filter,#text_filter";

	var aligns	 = "center,left,left,left,center";
		aligns	+= ",center,center,left,center,left";
		aligns	+= ",right,left,center,left,center";
		aligns	+= ",left,left,left";


	var colTypes  = "ro,ro,ro,ro,ro";
		colTypes += ",ro,ro,ro,ro,ro";
		colTypes += ",ron,ro,ro,ro,ro";
		colTypes += ",ro,ro,ro";

	var sorts	 = "int,str,str,str,str";
		sorts	+= ",str,str,str,str,str";
		sorts	+= ",int,str,str,str,str";
		sorts	+= ",str,str,str";

	var colWidth	 = "60,150,100,100,100";
		colWidth	+= ",100,100,80,100,120";
		colWidth	+= ",100,120,100,120,100";
		colWidth	+= ",120,100,200";

	accountGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	accountGrid.setSkin("dhx_web");
	accountGrid.setHeader(fields);
	accountGrid.setInitWidths(colWidth);
	accountGrid.setColTypes(colTypes);
	accountGrid.setColAlign(aligns);
	accountGrid.setColSorting(sorts);
	accountGrid.attachEvent("onRowDblClicked",doOnRowDblClicked);
	accountGrid.enableColSpan(true);
	accountGrid.init();

	function doOnRowDblClicked(rowId, cellInd){
		detail_view(rowId);
	}

	accountGrid.attachEvent("onXLE", function() {
		AccLayout.items[0].progressOff();
		var count=accountGrid.getRowsNum();
		$('#grid_total_rows').html(count);
		
		accountGrid.setRowTextStyle("total", "text-align:right;font-weight:bold;background-color:#f3fafe;color:#006699;border:1px solid #ffffff;");
		accountGrid.setCellTextStyle("total",0,"text-align:center;font-weight:bold;background-color:#f3fafe;color:#006699;border:1px solid #ffffff;");
		accountGrid.setColspan("total",0,2);
	}); 

	function glb_where(){
		var stime	= byId("stime").value;
		var etime	= byId("etime").value;
		var samt	= byId("samt").value;
		var eamt	= byId("eamt").value;
		var appno	= byId("appno").value;
		//var cardno	= byId("cardno").value;
		var cardtp	= $('#cardtp option:selected').val();
		//var depcd	= $('#depcd option:selected').val();
		var auth01	= $('#auth01').val();
		var auth02	= $('#auth02').val();
		var auth03	= $('#auth03').val();
		
		var w	 = "uauth=<%=tuser%>";
		
		w	+= (stime!="") ? "&stime="+stime : "";
		w	+= (etime!="") ? "&etime="+etime : "";
		w	+= (samt!="") ? "&samt="+samt : "";
		w	+= (eamt!="") ? "&eamt="+eamt : "";
		w	+= (appno!="") ? "&appno="+appno : "";
		//w	+= (cardno!="") ? "&cardno="+cardno : "";
		w	+= (cardtp!="") ? "&cardtp="+cardtp : "";
		//w	+= (depcd!="") ? "&depcd="+depcd : "";
		if($("#auth01").prop("checked")){w	+= (auth01!="") ? "&auth01="+auth01 : "";}
		if($("#auth02").prop("checked")){w	+= (auth02!="") ? "&auth02="+auth02 : "";}
		if($("#auth03").prop("checked")){w	+= (auth03!="") ? "&auth03="+auth03 : "";}

		return w;
	}

	function acc_exceldn(){
		$('#exceldn').submit();
	}
	
	function afterload_prog01(){
		AccLayout.items[0].progressOff();
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
		accountGrid.clearAndLoad("./ifou_xml_was/total_0107.jsp?"+glb_where(), afterload_prog01, "json");	
	}	

	function auth_chk(obj){
		if(obj==1){
			if($("#auth01").prop("checked")){
				$("#auth02").attr('checked', false);
				$("#auth03").attr('checked', false);
			}
		}else{
			$("#auth01").attr('checked', false);
		}
	}
</script>
<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>