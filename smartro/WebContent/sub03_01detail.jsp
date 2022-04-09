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
	
	//2021.01.25 sub03_01 -> doOnClick function
	String etime = request.getParameter("etime");
	String stime = request.getParameter("stime");
	String mid = request.getParameter("mid");
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
			myCalendar.setWeekStartDay(7);
			myCalendar.setDate("<%=setdate.format(nowTime)%>");
			myCalendar.hideTime();
			myCalendar.setSkin('dhx_skyblue');

			byId("stime").value = "<%=stime%>";
			byId("etime").value = "<%=etime%>";

			search_go();
		}

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
</style>
</head>
<body onload="doOnLoad();">
<div class='sub_content'>
	<div class='sub_content_space'></div>

	<div class='cont_title'>
		<form name='exceldn' id="exceldn" action='./excel/exceldn_0301_detail.jsp'>
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
					<%=useracq%>
				</td>
				<td>
					<span class='schtitle'>사업부</span>
				</td>
				<td>
					<%=userdepo%>
				</td>
				<td>
					<span class='schtitle'>단말기번호</span>
				</td>
				<td>
					<%=usertid%>
				</td>
				<td rowspan="2" align="center"><img src='./images/btn/btn_search.gif' onclick='search_go();' style='cursor:pointer;'></td>
			</tr>
			<tr height="36">
				<td>
					<span class='schtitle'>가맹점번호</span>
				</td>
				<td>
					<input type='text' name='mid' id='mid' class="searchbox_large" value="<%=mid%>">
				</td>
				<td>
					<span class='schtitle'>승인번호</span>
				</td>
				<td>
					<input type='text' name='appno' id='appno' class="searchbox_large" value="">
				</td>
				<td>
					<span class='schtitle'>반송조회</span>
				</td>
				<td  colspan="3">
					<input type="checkbox" name="auth01" id="auth01" onclick="auth_chk(1);" value="Y">반송전체
					<input type="checkbox" name="auth02" id="auth02" onclick="auth_chk(2);" value="Y">매출반송
					<br>
					<input type="checkbox" name="auth03" id="auth03" onclick="auth_chk(3);" value="Y">취소반송
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
				<td align="right" valign="absmiddle">
					<span class='button large icon'><input type="checkbox" name="rendchk" id="rendchk" checked><a href='#'>랜더링사용</a></span>
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
	var tfields  = "사업부,가맹점번호,카드사,정상건수,반송건수";
		tfields	+= ",매출금액,수수료,입금액";
    var taligns	 = "left,left,left,right,right";
        taligns  += ",right,right,right";
    var tcoltypes  = "ro,ro,ro,ron,ron";
        tcoltypes += ",ron,ron,ron";
    var tsorts	 = "str,str,str,int,int";
        tsorts   += ",int,int,int";
    var tcolwidth  = "120,120,120,*,*";
        tcolwidth += ",*,*,*";

	totalGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	totalGrid.setSkin("dhx_web");	
	totalGrid.setHeader(tfields);
	totalGrid.setInitWidths(tcolwidth);
	totalGrid.setColTypes(tcoltypes);
	totalGrid.setColAlign(taligns);
	totalGrid.setColSorting(tsorts);
	totalGrid.setNumberFormat("0,000",4,".",",");
	totalGrid.setNumberFormat("0,000",5,".",",");
	totalGrid.setNumberFormat("0,000",6,".",",");
	totalGrid.setNumberFormat("0,000",7,".",",");
	totalGrid.setNumberFormat("0,000",8,".",",");
	totalGrid.init();
	
	accountGrid = AccLayout.cells("b").attachGrid();
	
	var fields    = "순번,사업부명,단말기명,단말기번호,카드사";
		fields	 += ",가맹점번호,매입구분,승인구분,카드번호,금액";
		fields	 += ",할부기간,승인번호,승인일자,원승인일자";
		fields	 += ",예정수수료,입금예정액,청구일자,응답일자,매입결과";
		fields	 += ",입금예정일,영수증번호,입반내역"; 
	
	var filters  = "&nbsp;,#select_filter,#select_filter,#select_filter,#select_filter";
		filters	+= ",#select_filter,#select_filter,#select_filter,#text_filter,#text_filter";
		filters	+= ",#text_filter,#text_filter,#text_filter,#text_filter";
		filters	+= ",#text_filter,#text_filter,#select_filter,#select_filter,#select_filter";
		filters	+= ",#select_filter,#text_filter";
	
	var aligns	 = "center,left,left,left,center"
		aligns	+= ",left,center,center,center,right";
	    aligns	+= ",center,center,center,center";
		aligns	+= ",right,right,center,center,center";
		aligns	+= ",center,center,left";
	
	var colTypes  = "ro,ro,ro,ro,ro";
		colTypes += ",ro,ro,ro,ro,ron";
	    colTypes += ",ro,ro,ro,ro";
		colTypes += ",ron,ron,ro,ro,ro";
		colTypes += ",ro,ro,ro";
	
	var sorts	 = "int,str,str,str,str";
		sorts	+= ",str,str,str,str,int";
	    sorts   += ",str,str,str,int";
		sorts	+= ",int,int,str,str,str";
		sorts	+= ",str,str,str";
		
	var colWidth	 = "60,160,120,100,90";
		colWidth	+= ",100,80,80,130,100";
	    colWidth    += ",70,80,80,80";
		colWidth	+= ",80,100,100,100,100";
		colWidth	+= ",100,130,140";
	
	accountGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	accountGrid.setSkin("dhx_web");	
	accountGrid.setHeader(fields);
	accountGrid.setInitWidths(colWidth);
	accountGrid.setColTypes(colTypes);
	accountGrid.setColAlign(aligns);
	accountGrid.setColSorting(sorts);
	accountGrid.setNumberFormat("0,000",9,".",",");
	accountGrid.setNumberFormat("0,000",15,".",",");
	accountGrid.setNumberFormat("0,000",16,".",",");
//	accountGrid.enableSmartRendering(true);
	accountGrid.init();

	accountGrid.attachEvent("onFilterStart", function(indexes,values){
		var va	= values.toString();
		var c = va.split(',');

		filterIdx = indexes;
		filterVal = values;

		console.log(indexes);
		console.log(values);
		accountGrid.filterBy(indexes,values);

		var count=accountGrid.getRowsNum();
		$('#grid_total_rows').html(count);
	});
	
	accountGrid.attachEvent("onXLE", function() {
		AccLayout.items[0].progressOff();
		AccLayout.items[1].progressOff();
		var count=accountGrid.getRowsNum();
		$('#grid_total_rows').html(count);
	}); 

	function wherequery(){
		var stime	= $('#stime').val();
		var etime	= $('#etime').val();
		var acqcd	= $('#acqcd option:selected').val();
		var depcd	= $('#depcd option:selected').val();
		var tid		= $('#tid option:selected').val();
		var mid		= $('#mid').val();
		var appno	= $('#appno').val();
		var auth01	= $('#auth01').val();
		var auth02	= $('#auth02').val();
		var auth03	= $('#auth03').val();
		
		var w	 = "uauth=<%=tuser%>";

		w	+= (stime!="") ? "&stime="+stime : "";
		w	+= (etime!="") ? "&etime="+etime : "";
		w	+= (acqcd!="") ? "&acqcd="+acqcd : "";
		w	+= (depcd!="") ? "&depcd="+depcd : "";
		w	+= (tid!="") ? "&tid="+tid : "";
		w	+= (mid!="") ? "&mid="+mid : "";
		w	+= (appno!="") ? "&appno="+appno : "";
		if($("#auth01").prop("checked")){w	+= (auth01!="") ? "&auth01="+auth01 : "";}
		if($("#auth02").prop("checked")){w	+= (auth02!="") ? "&auth02="+auth02 : "";}
		if($("#auth03").prop("checked")){w	+= (auth03!="") ? "&auth03="+auth03 : "";}

		return w;
	}

	function acc_exceldn(){
		/* var exsrc	= "./excel/excel0301detail.php?uauth=<?=$uauth?>&"+wherequery();
		//$('#subq').attr('src', exsrc); */
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

		if($("#rendchk").prop("checked")){
			accountGrid.enableSmartRendering(true);
		}else{
			accountGrid.enableSmartRendering(false);
		}

		totalGrid.clearAndLoad("./ifou_xml_was/total_0301_detail.jsp?" + wherequery(), afterload_prog01, "json");
		accountGrid.clearAndLoad("./ifou_xml_was/item_0301_detail.jsp?" + wherequery(), afterload_prog02, "json");
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