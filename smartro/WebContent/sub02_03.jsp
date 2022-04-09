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
	
	//2021.01.28 하루 전으로 sdate, edate 설정
	Calendar cal = Calendar.getInstance();
	cal.setTime(nowTime);
	cal.add(Calendar.DATE, -1);
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
	<script>
		var myCalendar;
		var AccLayout, accountGrid;
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

			byId("stime").value = "<%=setTime%>";
			byId("etime").value = "<%=setTime%>";

			/*2014 09 25 유병현추가*/
			$("#parentId").css("min-height","800px").css("min-width","860px");
			$(".dhx_cell_cont_layout").css("width","100%");
			/*2014 09 25 유병현추가*/

		}

		function date_amonth(){
			var selDate = $("#etime").val().split("-");
			var newDate	= new Date();
			newDate.setFullYear(selDate[0], selDate[1]-2, selDate[2]);
			
			var y = newDate.getFullYear();
            var m = newDate.getMonth() + 1;
            var d = newDate.getDate();

            if(m < 10) { 
                m = "0" + m;
            }

            if(d < 10) {
                d = "0" + d;
            }

            var resultDate = y + "-" + m + "-" + d;

			$("#stime").val(resultDate);
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
	<form name='exceldn' id='exceldn' method='post' action='./excel/exceldn_0203.jsp'>
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
	var filterVal = "", filterIdx="";
    AccLayout = new dhtmlXLayoutObject("parentId", "2E");
	AccLayout.setSkin("dhx_web");	
	AccLayout.cells("a").hideHeader();
	AccLayout.cells("b").hideHeader();

	totalGrid = AccLayout.cells("a").attachGrid();
	var tfields  = ",#cspan,승인건수,승인금액,취소건수,취소금액,합계건수,합계금액";
    var taligns	 = "left, right, right";
		taligns	+= ",right, right, right, right, right";
    var tcoltypes  = "ro,ro,ron,ron";
        tcoltypes += ",ron,ron,ron,ron";
    var tsorts	 = "str,str,int,int";
        tsorts   += ",int,int,int,int";
    var tcolwidth   = "150,150,*,*";
		tcolwidth	+= ",*,*,*,*";


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

	accountGrid = AccLayout.cells("b").attachGrid();
	
	var fields   = "사업부,단말기명,단말기번호,승인건수,승인금액";
		fields	+= ",취소건수,취소금액,합계건수,합계금액";
	
	var filters	 = "#select_filter,#select_filter,#select_filter,#text_filter,#text_filter";
		filters += ",#text_filter,#text_filter,#text_filter,#text_filter";
	
    var aligns	 = "left,left,left,right,right";
		aligns	+= ",right,right,right,right";
	
    var colTypes  = "ro,ro,ro,ron,ron";
        colTypes += ",ron,ron,ron,ron";
	
    var sorts	 = "str,str,str,int,int";
        sorts   += ",int,int,int,int";
    
    var colWidth   = "150,150,150,*,*";
        colWidth  += ",*,*,*,*";

	accountGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	accountGrid.setSkin("dhx_web");	
	accountGrid.setHeader(fields);
	accountGrid.setInitWidths(colWidth);
	accountGrid.setColTypes(colTypes);
	accountGrid.setColAlign(aligns);
	accountGrid.setColSorting(sorts);
	accountGrid.enableSmartRendering(true,50);
	accountGrid.attachEvent("onRowDblClicked",doOnRowDblClicked);
	accountGrid.setNumberFormat("0,000",3,".",",");
	accountGrid.setNumberFormat("0,000",4,".",",");
	accountGrid.setNumberFormat("0,000",5,".",",");
	accountGrid.setNumberFormat("0,000",6,".",",");
	accountGrid.setNumberFormat("0,000",7,".",",");
	accountGrid.setNumberFormat("0,000",8,".",",");
	accountGrid.enableColSpan(true);
	
	accountGrid.init();

		
	function doOnRowDblClicked(rowId, cellInd){
		//단말기번호, 가맹점번호 넘겨주기
		var tid = accountGrid.cellById(rowId, 2).getValue();
		var stime	= $('#stime').val();
		var etime	= $('#etime').val();
		var samt	= $('#samt').val();
		var eamt	= $('#eamt').val();
		
		var detail_view = "sub02_03.jsp";
		var seturl = "detail_view="+detail_view+"&tid="+tid+"&stime="+stime+"&etime="+etime+"&samt="+samt+"&eamt="+eamt;

		parent.urlgoDirect(-1, "매장별상세(V)", "sub02_04.jsp?" + seturl);
	}

	accountGrid.attachEvent("onXLE", function() {
		AccLayout.items[0].progressOff();
		AccLayout.items[1].progressOff();
		accountGrid.setColspan("total",0,3);
		
		accountGrid.setRowTextStyle("total", "text-align:right;font-weight:bold;background-color:#f3fafe;color:#006699;border:1px solid #ffffff;");
		accountGrid.setCellTextStyle("total",0,"text-align:center;font-weight:bold;background-color:#f3fafe;color:#006699;border:1px solid #ffffff;");
		var count=accountGrid.getRowsNum();
		$('#grid_total_rows').html(count);
	}); 

	function glb_where(){
		var stime	= byId("stime").value;
		var etime	= byId("etime").value;
		//var appno	= byId("appno").value;

		var samt	= byId("samt").value;
		var eamt	= byId("eamt").value;
		var tid	= $('#tid option:selected').val();
 		var depcd	= $('#depcd option:selected').val(); 
		
		var w	 = "uauth=<%=tuser%>&urlcd=0203";
		w	+= (stime!="") ? "&stime="+stime : "";
		w	+= (etime!="") ? "&etime="+etime : "";
		w	+= (samt!="") ? "&samt="+samt : "";
		w	+= (eamt!="") ? "&eamt="+eamt : "";
		w	+= (tid!="") ? "&tid="+tid : "";
		w	+= (depcd!="") ? "&depcd="+depcd : "";


		return w;
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
		totalGrid.clearAndLoad("./ifou_xml_was/total_0203.jsp?"+glb_where(), afterload_prog01, "json");
		accountGrid.clearAndLoad("./ifou_xml_was/item_0203.jsp?"+glb_where(), afterload_prog02, "json");	
	}
	
	function acc_exceldn(){
		/* var exsrc	= "./ifou_xml_was/excel0203.jsp?"+glb_where();

		$.getJSON(exsrc, 
			{ 
				tags: "mount rainier", 
				tagmode: "any", 
				format: "json"
			}, // 서버가 필요한 정보를 같이 보냄. 
			function(data, status) { 
				if(data["RST"]=="S000"){
					$('#totalarray').val(data["TOTALARRAY"]);
					$('#itemarray').val(data["ITEMARRAY"]);
					
					
				}
			} 
		); */
		$('#exceldn').submit();
	}
	
</script>
<!-- <form id="exceldn" method="post" name="exceldn" target="subq" action="./excel/exceldn_0203.jsp">
	<input type="hidden" name="totalarray" id="totalarray">
	<input type="hidden" name="itemarray" id="itemarray">
</form> -->
<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>