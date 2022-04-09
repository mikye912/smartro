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
			myCalendar.setDate("<%=setdate.format(nowTime)%>");
			myCalendar.hideTime();
			myCalendar.setSkin('dhx_skyblue');

			myCalendar1 = new dhtmlXCalendarObject(["sadd_date","eadd_date"]);
			myCalendar1.attachEvent("onClick", function(d){
				byId("eadd_date").value = myCalendar1.getFormatedDate(null,d);
			});
			myCalendar1.setWeekStartDay(7);
			myCalendar1.setDate("<%=setdate.format(nowTime)%>");
			myCalendar1.hideTime();
			myCalendar1.setSkin('dhx_skyblue');
			myCalendar2 = new dhtmlXCalendarObject(["sadd_recp","eadd_recp"]);
			myCalendar2.attachEvent("onClick", function(d){
				byId("eadd_recp").value = myCalendar2.getFormatedDate(null,d);
			});
			myCalendar2.setWeekStartDay(7);
			myCalendar2.setDate("<%=setdate.format(nowTime)%>");
			myCalendar2.hideTime();
			myCalendar2.setSkin('dhx_skyblue');

			// init values
			var t = new Date();
			byId("stime").value = "<%=setdate.format(nowTime)%>";
			byId("etime").value = "<%=setdate.format(nowTime)%>";
			byId("sadd_date").value = "<%=setdate.format(nowTime)%>";
			byId("eadd_date").value = "<%=setdate.format(nowTime)%>";
			byId("sadd_recp").value = "<%=setdate.format(nowTime)%>";
			byId("eadd_recp").value = "<%=setdate.format(nowTime)%>";
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
		
		function setSens1(id, k) {
			myCalendar1.setSensitiveRange(null,null);
		}
		
		function setSens2(id, k) {
			myCalendar2.setSensitiveRange(null,null);
		}
		
		function byId(id) {
			return document.getElementById(id);
		}
		
		function srvTime(){
			var xmlHttp;

			if (window.XMLHttpRequest) {//분기하지 않으면 IE에서만 작동된다.
				xmlHttp = new XMLHttpRequest(); // IE 7.0 이상, 크롬, 파이어폭스 등
				xmlHttp.open('HEAD',window.location.href.toString(),false);
				xmlHttp.setRequestHeader("Content-Type", "text/html");
				xmlHttp.send('');
				return xmlHttp.getResponseHeader("Date");
			}else if (window.ActiveXObject) {
				xmlHttp = new ActiveXObject('Msxml2.XMLHTTP');
				xmlHttp.open('HEAD',window.location.href.toString(),false);
				xmlHttp.setRequestHeader("Content-Type", "text/html");
				xmlHttp.send('');
				return xmlHttp.getResponseHeader("Date");
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
<div class='sub_content'>
	<div class='sub_content_space'></div>
	<div class='cont_title'>
	<form name="exceldn" id="exceldn"  method="post" action="./excel/exceldn_0107_cvs.jsp">
		<input type='hidden' name='extitle' id='extitle' value=''>
		<input type='hidden' name='search' value='Y'>
		<input type="hidden" name="setpage" id="setpage">
		<input type="hidden" name="pagegroup" id="pagegroup">
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
					<span class='schtitle'>접수일자</span>
				</td>
				<td>
					<input type='text' name='sadd_date' id='sadd_date' class="searchbox_nomal" onclick="setSens1('eadd_date', 'max');" onblur="datesam();">
						~
					<input type='text' name='eadd_date' id='eadd_date' class="searchbox_nomal" onclick="setSens1('sadd_date', 'min');" >
				</td>
				<td>
					<span class='schtitle'>집하일자</span>
				</td>
				<td>
					<input type='text' name='sadd_recp' id='sadd_recp' class="searchbox_nomal" onclick="setSens2('eadd_recp', 'max');" onblur="datesam();">
						~
					<input type='text' name='eadd_recp' id='eadd_recp' class="searchbox_nomal" onclick="setSens2('sadd_recp', 'min');" >
				</td>
				<td rowspan="2" align="center"><img src='./images/btn/btn_search.gif' onclick='search_go();' style='cursor:pointer;'></td>
			</tr>
			<tr height="36">
				<td>
					<span class='schtitle'>송장번호</span>
				</td>
				<td>
					<input type="text" name="pid" id="pid" class="searchbox_large">
				</td>
				<td>
					<span class='schtitle'>점포코드</span>
				</td>
				<td>
					<input type="text" name="pcd" id="pcd" class="searchbox_large">
				</td>
				<td>
					<span class='schtitle'>승인번호</span>
				</td>
				<td>
					<input type="text" name="appno" id="appno" class="searchbox_large">
				</td>
				<td>
					<span class='schtitle'>승인/결제 구분</span>
				</td>
				<td>
					<input type="checkbox" name="auth01" id="auth01" checked onclick="auth_chk(1);" value="Y">전체거래
					<input type="checkbox" name="auth02" id="auth02" onclick="auth_chk(2);" value="Y">승인거래
					<input type="checkbox" name="auth03" id="auth03" onclick="auth_chk(3);" value="Y">취소거래 | &nbsp;
					<input type="checkbox" name="card01" id="card01" checked onclick="card_chk(1);" value="Y">전체거래
					<input type="checkbox" name="card02" id="card02" onclick="card_chk(2);" value="Y">선불
					<input type="checkbox" name="card03" id="card03" onclick="card_chk(3);" value="Y">착불
					<input type="checkbox" name="card04" id="card04" onclick="card_chk(4);" value="Y">카드
					<input type="checkbox" name="card05" id="card05" onclick="card_chk(5);" value="Y">신용
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
	var filterVal = "", filterIdx="";
    AccLayout = new dhtmlXLayoutObject("parentId", "2E");
	AccLayout.setSkin("dhx_web");	
	AccLayout.cells("a").hideHeader();
	AccLayout.cells("b").hideHeader();

	totalGrid = AccLayout.cells("a").attachGrid();
	var tfields  = ",#cspan,#cspan,#cspan,승인건수,승인금액,취소건수,취소금액,합계건수,합계금액";
    var taligns	 = "left, left, left, left, right, right";
		taligns	+= ",right, right, right, right";
    var tcoltypes  = "ro,ro,ro,ro,ro,ro,ro";
        tcoltypes += ",ro,ro,ro,ro";
    var tsorts	 = "str, str,str,str,int,int";
        tsorts   += ",int,int,int,int";
    var tcolwidth   = "150,150,150,150,*,*";
		tcolwidth	+= ",*,*,*,*";

	totalGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	totalGrid.setSkin("dhx_web");	
	totalGrid.setHeader(tfields);
	totalGrid.setInitWidths(tcolwidth);
	totalGrid.setColTypes(tcoltypes);
	totalGrid.setColAlign(taligns);
	totalGrid.setColSorting(tsorts);
	totalGrid.enableColSpan(true);
	totalGrid.init();

	accountGrid = AccLayout.cells("b").attachGrid();

	var fields   = "순번,점포명,점포코드,송장번호,접수일자,집하일자,결제구분,승인일자";//8
		fields	+= ",승인시간,원승인일자,승인번호,승인구분,신분확인번호";//5
		fields	+= ",금액,거래구분,취소사유";//3
		fields	+= ",거래고유번호";//1

	var filters  = "&nbsp;,#text_filter,#text_filter,#text_filter,#text_filter,#text_filter,#text_filter,#select_filter";
		filters	+= ",#text_filter,#select_filter,#text_filter,#select_filter,#text_filter";
		filters	+= ",#text_filter,#select_filter,#select_filter";
		filters	+= ",#text_filter";

	var aligns	 = "center,left,left,left,left,left,left,center";
		aligns	+= ",center,center,left,center,left";
		aligns	+= ",right,left,left";
		aligns	+= ",left";

	var colTypes  = "ro,ro,ro,ro,ro,ro,ro,ro";
		colTypes += ",ro,ro,ro,ro,ro";
		colTypes += ",ron,ro,ro";
		colTypes += ",ro";

	var sorts	 = "int,str,str,str,str,str,str,str";
		sorts	+= ",str,str,str,str,str";
		sorts	+= ",int,str,str";
		sorts	+= ",str";

	var colWidth	 = "60,150,100,150,80,80,100,100";
		colWidth	+= ",100,100,80,100,120";
		colWidth	+= ",100,100,120";
		colWidth	+= ",120";
		
	accountGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	accountGrid.setSkin("dhx_web");	
	accountGrid.setHeader(fields);
	accountGrid.setInitWidths(colWidth);
	accountGrid.setColTypes(colTypes);
	accountGrid.setColAlign(aligns);
	accountGrid.setColSorting(sorts);
//	accountGrid.attachEvent("onRowDblClicked",doOnRowDblClicked);
	accountGrid.enableSmartRendering(true,50);
	accountGrid.enableColSpan(true);
	accountGrid.init();
	
	var footer  = "<div id='nr_navi'>0</div>,#cspan,#cspan,#cspan,#cspan";
		footer += ",#cspan,#cspan,#cspan,#cspan,#cspan";
		footer += ",#cspan,#cspan,#cspan,#cspan,#cspan";
		footer += ",#cspan,#cspan";

	accountGrid.attachFooter(footer,["text-align:left;"]);


	/* function doOnRowDblClicked(rowId, cellInd){
		detail_view(rowId);
	} */

	accountGrid.attachEvent("onXLE", function() {
		accountGrid.setRowTextStyle("total", "text-align:right;font-weight:bold;background-color:#f3fafe;color:#006699;border:1px solid #ffffff;");
		accountGrid.setCellTextStyle("total",0,"text-align:center;font-weight:bold;background-color:#f3fafe;color:#006699;border:1px solid #ffffff;");
		accountGrid.setColspan("total",0,3);
		
		AccLayout.items[0].progressOff();
		AccLayout.items[1].progressOff();
		
		var count=accountGrid.getRowsNum();
		$('#grid_total_rows').html(count);
	}); 

	function glb_where(){
		var stime		= byId("stime").value;
		var etime		= byId("etime").value;
		var sadd_date	= byId("sadd_date").value;
		var eadd_date   = byId("eadd_date").value;
		var sadd_recp   = byId("sadd_recp").value;
		var eadd_recp	= byId("eadd_recp").value;
		var appno		= byId("appno").value;
		var pid			= byId("pid").value;
		var pcd			= byId("pcd").value;
		var depcd		= $('#depcd option:selected').val();
		var auth01		= $('#auth01').val();
		var auth02		= $('#auth02').val();
		var auth03		= $('#auth03').val();
		var card01      = $('#card01').val();
        var card02      = $('#card02').val();
        var card03      = $('#card03').val();
        var card04      = $('#card04').val();
		var card05      = $('#card05').val();
		var getpage 	= $("#setpage").val();
		
		var w	 = "uauth=<%=tuser%>";
		
		w	+= "&page="+getpage;
		w	+= (stime!="") ? "&stime="+stime : "";
		w	+= (etime!="") ? "&etime="+etime : "";
		w	+= (sadd_date!="") ? "&sadd_date="+sadd_date : "";
		w   += (eadd_date!="") ? "&eadd_date="+eadd_date : "";
		w	+= (sadd_recp!="") ? "&sadd_recp="+sadd_recp : "";
		w   += (eadd_recp!="") ? "&eadd_recp="+eadd_recp : "";
		w	+= (appno!="") ? "&appno="+appno : "";
		w	+= (pid!="") ? "&pid="+pid : "";
		w	+= (pcd!="") ? "&pcd="+pcd : "";
		w	+= (depcd!="") ? "&depcd="+depcd : "";
		if($("#auth01").prop("checked")){w	+= (auth01!="") ? "&auth01="+auth01 : "";}
		if($("#auth02").prop("checked")){w	+= (auth02!="") ? "&auth02="+auth02 : "";}
		if($("#auth03").prop("checked")){w	+= (auth03!="") ? "&auth03="+auth03 : "";}
		if($("#card01").prop("checked")){w  += (card01!="") ? "&card01="+card01 : "";}
		if($("#card02").prop("checked")){w  += (card02!="") ? "&card02="+card02 : "";}
		if($("#card03").prop("checked")){w  += (card03!="") ? "&card03="+card03 : "";}
		if($("#card04").prop("checked")){w  += (card04!="") ? "&card04="+card04 : "";}
		if($("#card05").prop("checked")){w  += (card05!="") ? "&card05="+card05 : "";}

		return w;
	}

	function acc_exceldn(){
		$('#exceldn').submit();
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
		
		var st = srvTime();
		var today = new Date(st);
		var tmon   = leadingZeros((today.getMonth()+1), 2);
		var tday	= leadingZeros(today.getDate(), 2);
		var thour	= leadingZeros(today.getHours(), 2);
		var tminu	= leadingZeros(today.getMinutes(), 2);
		var tsecon	= leadingZeros(today.getSeconds(), 2);
		var todaysvr	= today.getFullYear()+'-'+tmon+'-'+tday;
		var timeset = thour+':'+tminu+':'+tsecon;
		
		$("#setpage").val("");
		$("#pagegroup").val("");

		$.ajax({
			url: "./ifou_xml_was/sub0107page.jsp?"+glb_where(),
			type:"GET",
			dataType: 'JSON',
            success: function(data){
            	const obj = Object.keys(data).length;
            	
            	if(obj<2){
            		page(data.apn_page);
            	}else{
            		set_navi(data.apn_page, data.npage);
            	}
            }
		}); 
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
	

	function card_chk(obj){
		if(obj==1){
			if($("#card01").prop("checked")){
				$("#card02").attr('checked', false);
				$("#card03").attr('checked', false);
			}
		}else{
			$("#card01").attr('checked', false);
		}
	}

	function set_page(obj){
		$("#setpage").val(obj);
	
		var setn = $("#pagegroup").val();
		var exsrc       = "./ifou_xml_was/sub0107page.jsp?"+glb_where()+"&npage="+setn+"&pmode=";
		
		$('#subq').attr('src', exsrc);
	}
	
	function afterload_prog01(){
		AccLayout.items[0].progressOff();
	}
	
	function afterload_prog02(){
		AccLayout.items[1].progressOff();
	}

	function page(obj){
		$('#nr_navi').html(obj);
		$("#nr_navi").css("min-height","40px").css("font-size","11pt").css("padding-top", "5px");
		AccLayout.items[0].progressOn();
		AccLayout.items[1].progressOn();
		totalGrid.clearAndLoad("./ifou_xml_was/total_0107_cvs.jsp?"+glb_where(), afterload_prog01, "json");
		accountGrid.clearAndLoad("./ifou_xml_was/item_0107_cvs.jsp?"+glb_where(), afterload_prog02, "json");	
	}

	function set_navi(obj, np){
		$("#pagegroup").val(np);
		$('#nr_navi').html(obj);
		$("#nr_navi").css("min-height","40px").css("font-size","11pt").css("padding-top", "5px");
	}

	function move_navi(obj){
		var setn = $("#pagegroup").val();
		var exsrc	= "./ifou_xml_was/sub0107page.jsp?"+glb_where()+"&npage="+setn+"&pmode="+obj;
		$('#subq').attr('src', exsrc);
	}
</script>
<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>