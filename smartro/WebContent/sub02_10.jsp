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
	SimpleDateFormat nowYear = new SimpleDateFormat("yyyy");
	SimpleDateFormat nowMonth = new SimpleDateFormat("MM");
	int year = Integer.parseInt(nowYear.format(nowTime));
	int month = Integer.parseInt(nowMonth.format(nowTime));
	
	//2021.03.15 sub02_10_cal -> sub02_10
	String searchYear = request.getParameter("searchYear") == null ? "" : request.getParameter("searchYear");
	String searchMon = request.getParameter("searchMon") == null ? "" : request.getParameter("searchMon");
	//이거 0 붙어서 오는 바람에 선택이 안돼 ㅋㅋㅋㅋㅋㅋㅋㅋㅋ ㅠㅠㅠ
	searchMon = searchMon.replace("0", "");
	
	String trtp = request.getParameter("trtp") == null ? "" : request.getParameter("trtp");	
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
			
			//2021.03.18 searchYear, searchMon 있으면 설정
			var trtp = "<%=trtp%>";
			if(trtp != ""){
				byId("syear").value = "<%=searchYear%>";
				byId("smon").value = "<%=searchMon%>";
				
				search_go();
			}
				
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
	<form name='regular' method='post' action=''>
		<input type='hidden' name='tuser' id='tuser' value="<%=tuser%>">
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
						<option value="">::연도선택::</option>
						<% for(int i = (year-3); i < year+1; i++) { %>
						<option value="<%=i%>" <%=i == year ? "selected" : ""%>><%=i%>년</option>
						<% } %>
					</select>
					<select name="smon" id="smon" style="width:80px; height:22px;">
						<option value="">::월선택::</option>
						<% for(int j = 1;j <= 12; j++){ %>
						<option value="<%=j%>" <%=j == month ? "selected" : ""%>><%=j%>월</option>
						<% } %>
					</select>
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
					<span class='schtitle'>가맹점번호</span>
				</td>
				<td colspan="3">
					<input type='text' name='mid' id='mid' class="searchbox_large">
				</td>
				<td rowspan="2" align="right"><img src='./images/btn/btn_search.gif' onclick='search_go();' style='cursor:pointer;'></td>
			</tr>
			<tr height="36">
				<td>
					<span class='schtitle'>단말기번호</span>
				</td>
				<td>
					<%=usertid%>
				</td>
				<td>
					<span class='schtitle'>사업부</span>
				</td>
				<td>
					<%=userdepo%>
				</td>
				<td>
					<!-- <span class='schtitle'>합계금액</span> -->
				</td>
				<td colspan="3">
					<!-- <input type='text' name='samt' id='samt' class="searchbox_nomal">
					~
					<input type='text' name='eamt' id='eamt' class="searchbox_nomal"> -->
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
	var tfields  = ",#cspan,승인건수,승인금액,취소건수,취소금액,합계건수,합계금액";
    var taligns	 = "left, right, right";
		taligns	+= ",right, right, right, right, right";
    var tcoltypes  = "ro,ro";
        tcoltypes += ",ron,ron,ron,ron,ron,ron";
    var tsorts	 = "str,str,int";
        tsorts   += ",int,int,int,int,int";
    var tcolwidth   = "600,*,*";
		tcolwidth	+= ",*,*,*,*,*";

	totalGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	totalGrid.setSkin("dhx_web");	
	totalGrid.setHeader(tfields);
	totalGrid.setInitWidths(tcolwidth);
	totalGrid.setColTypes(tcoltypes);
	totalGrid.setColAlign(taligns);
	totalGrid.setColSorting(tsorts);
	totalGrid.setNumberFormat("0,000",2,".",",");
	totalGrid.setNumberFormat("0,000",3,".",",");
	totalGrid.setNumberFormat("0,000",4,".",",");
	totalGrid.setNumberFormat("0,000",5,".",",");
	totalGrid.setNumberFormat("0,000",6,".",",");
	totalGrid.setNumberFormat("0,000",7,".",",");
	totalGrid.init();
	
	totalGrid.attachEvent("onXLE", function() {
		totalGrid.setRowTextStyle("total", "text-align:right;font-weight:bold;background-color:#f3fafe;color:#006699;border:1px solid #ffffff;");
	}); 
	
	AccLayout.attachEvent("onContentLoaded", function(b){
		AccLayout.items[0].progressOff();
		AccLayout.items[1].progressOff();
	});

	function doOnRowDblClicked(rowId, cellInd){
		var stime	= accountGrid.cells(rowId, 0).getValue();
		var etime	= accountGrid.cells(rowId, 0).getValue();
		var mid		= accountGrid.cells(rowId, 3).getValue();
		var w	= "?uauth=<%=tuser%>&search=Y&trtp=P&stime="+stime+"&etime="+etime+"&mid="+mid+"&urlcd=0202";
		//parent.urlgoDirect(-1, "일자별상세(V)", "sub02_01.jsp"+w);
	}

	function glb_where(){
		var syear	= $('#syear option:selected').val();
		var smon	= $('#smon option:selected').val();

		var mid	= $('#mid').val();
		var acqcd	= $('#acqcd option:selected').val();
		var depcd	= $('#depcd option:selected').val();
		var tid	= $('#tid option:selected').val();
		
		var w	 = "uauth=<%=tuser%>";
		w	+= (syear!="") ? "&syear="+syear : "";
		w	+= (smon!="") ? "&smon="+smon : "";
		w	+= (acqcd!="") ? "&acqcd="+acqcd : "";
		w	+= (mid!="") ? "&mid="+mid : "";
		w	+= (tid!="") ? "&tid="+tid : "";
		w	+= (depcd!="") ? "&depcd="+depcd : "";
		
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

		AccLayout.items[0].progressOn();
		AccLayout.items[1].progressOn();
		totalGrid.clearAndLoad("./ifou_xml_was/total_0210.jsp?"+glb_where(), "json");
		AccLayout.cells("b").attachURL("./sub02_10_cal.jsp?"+glb_where());
	}
		
</script>
<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>