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
	
	//2021.03.18 매출달력 신용 -> 더블클릭 시 넘어올 때
	String cal_stime = request.getParameter("stime") == null ? "" : request.getParameter("stime");
	String cal_etime = request.getParameter("etime") == null ? "" : request.getParameter("etime");
	String trtp = request.getParameter("trtp") == null ? "" : request.getParameter("trtp");
	
	String cal_acqcd = request.getParameter("acqcd") == null ? "" : request.getParameter("acqcd");
	String cal_depcd = request.getParameter("depcd") == null ? "" : request.getParameter("depcd");
	String cal_tid = request.getParameter("tid") == null ? "" : request.getParameter("tid");
	String cal_mid = request.getParameter("mid") == null ? "" : request.getParameter("mid");
	
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

			var hset	= (h-200) + "px";

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

			
			byId("stime").value = "<%=setdate.format(nowTime)%>";
			byId("etime").value = "<%=setdate.format(nowTime)%>";

			
			$("#parentId").css("min-height","600px").css("min-width","860px");
			$(".dhx_cell_cont_layout").css("width","100%");
			
			var trtp = "<%=trtp%>";
			if(trtp != ""){
				byId("stime").value = "<%=cal_stime%>";
				byId("etime").value = "<%=cal_etime%>";
				
				if("<%=cal_acqcd%>" != ""){
					$("#acqcd").val("<%=cal_acqcd%>").attr("selected", true);
				}
				
				if("<%=cal_depcd%>" != ""){
					$("#depcd").val("<%=cal_depcd%>").attr("selected", true);
				}
				
				if("<%=cal_tid%>" != ""){
					$("#tid").val("<%=cal_tid%>").attr("selected", true);
				}
				
				if("<%=cal_mid%>" != ""){
					byId("mid").value = "<%=cal_mid%>";
				}
				search_go();
			}
		}

		$(window).resize(function(){
			var h = $(document).height();
			var w = $(document).width();
			
			$(".dhx_cell_layout, .dhx_cell_cont_layout").css("width","100%").css("min-width","1160px");
			$(".dhx_cell_cont_layout").css("border","0").css("width","100%").css("border-top","1px solid #c0c0c0").css("border-bottom","1px solid #c0c0c0");
			$(".cont_title").css("margin","0 2px").css("min-width","550px");
			
			var hset	= (h-200) + "px";
			document.getElementById("parentId").style.height = hset;
		});

		var D = 0;
		function setSens(id, k) {
			myCalendar.setSensitiveRange(null,null);
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
<body onload="doOnLoad();"  style="padding:5px;">
<div class='sub_content'>
	<div class='sub_content_space'></div>
	<div class='cont_title'>
		<form name="exceldn" id="exceldn" method="post" action="./excel/exceldn_0201.jsp">
		<input type="hidden" name="extitle" id="extitle" value="">
		<input type="hidden" name="casher" id="casher" value="">
		<input type="hidden" name="search" value="Y">
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
				<td><span class="schtitle">승인일자</span></td>
				<td>
					<input type="text" name="stime" id="stime" class="searchbox_nomal" onblur="datesam();">
					~
					<input type="text" name="etime" id="etime" class="searchbox_nomal" >
				</td>
				<td><span class="schtitle">승인금액</span></td>
				<td>
					<input type="text" name="samt" id="samt" class="searchbox_nomal" value="">
					~
					<input type="text" name="eamt" id="eamt" class="searchbox_nomal" value="">
				</td>
				<td><span class="schtitle">승인번호</span></td>
				<td>
					<input type="text" name="appno" id="appno" class="searchbox_large" value="">
				</td>
				<td><span class="schtitle">카드사선택</span></td>
				<td>
					<%=useracq %>
				</td>
				<td rowspan="2" align="right"><img src="./images/btn/btn_search.gif" onclick="search_go();" style="cursor:pointer;"></td>
			</tr>
			<tr height="36">
				<td><span class="schtitle">단말기번호</span></td>
				<td>
					<%=usertid%>
				</td>
				<td><span class="schtitle">사업부</span></td>
				<td>
					<%=userdepo%>
				</td>
				<td><span class="schtitle">가맹점번호</span></td>
				<td colspan="3">
					<input type="text" name="mid" id="mid"  class="searchbox_large" value="">
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
					<span class='button large icon' onclick='acc_exceldn();'><span class='excel'></span><a href='#'>엑셀다운로드</a></span>
				</td>
			</tr>
		</table>
	</div>
	<div id="parentId" style="position: relative; top: 0px; left: 0px; width: 100%; height:140px;"></div>
	<input type="hidden" name="getmid" id="getmid" value="<?=$mid?>">
	<input type="hidden" name="tid" id="tid" value="<?=$tid?>">
	<input type="hidden" name="svrday" id="svrday">
	<input type="hidden" name="svrtime" id="svrtime">
	<input type="hidden" name="setpage" id="setpage">
	<input type="hidden" name="pagegroup" id="pagegroup">
</div>
<script type="text/javascript">
    AccLayout = new dhtmlXLayoutObject("parentId", "2E");
	AccLayout.setSkin("dhx_web");	
	AccLayout.cells("a").hideHeader();
	AccLayout.cells("b").hideHeader();

	totalGrid = AccLayout.cells("a").attachGrid();
	var tfields  = ",#cspan,#cspan,#cspan,승인건수,승인금액,취소건수,취소금액,합계건수,합계금액";
    var taligns	 = "left, left, left, left, right, right";
		taligns	+= ",right, right, right, right";
    var tcoltypes  = "ro,ro,ro,ro,ron,ron,ron";
        tcoltypes += ",ron,ron,ron,ron";
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
	totalGrid.setNumberFormat("0,000",4,".",",");
	totalGrid.setNumberFormat("0,000",5,".",",");
	totalGrid.setNumberFormat("0,000",6,".",",");
	totalGrid.setNumberFormat("0,000",7,".",",");
	totalGrid.setNumberFormat("0,000",8,".",",");
	totalGrid.setNumberFormat("0,000",9,".",",");
	
	totalGrid.init();

	accountGrid = AccLayout.cells("b").attachGrid();

	var fields   = "사업부,단말기명,단말기번호,카드사,가맹점번호";
		fields	+= ",승인건수,승인금액,취소건수,취소금액,합계건수";
		fields	+= ",합계금액";
	
	var filters	 = "#select_filter,#select_filter,#select_filter,#select_filter,#text_filter";
		filters += ",#text_filter,#text_filter,#text_filter,#text_filter,#text_filter";
		filters += ",#text_filter";
	
    var aligns	 = "left,left,left,left,left";
		aligns	+= ",right,right,right,right,right";
		aligns	+= ",right";
	
    var colTypes  = "ro,ro,ro,ro,ro";
        colTypes += ",ron,ron,ron,ron,ron";
		colTypes += ",ron";
	
    var sorts	 = "str,str,str,str,str";
        sorts   += ",int,int,int,int,int";
		sorts	+= ",int";
    
    var colWidth   = "150,150,150,150,150";
        colWidth  += ",190,190,190,190,190";
		colWidth  += ",190";

	accountGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	accountGrid.setSkin("dhx_web");	
	accountGrid.setHeader(fields);
	accountGrid.setInitWidths(colWidth);
	accountGrid.setColTypes(colTypes);
	accountGrid.setColAlign(aligns);
	accountGrid.setColSorting(sorts);
	accountGrid.attachEvent("onRowDblClicked",doOnRowDblClicked);
	accountGrid.setNumberFormat("0,000",5,".",",");
	accountGrid.setNumberFormat("0,000",6,".",",");
	accountGrid.setNumberFormat("0,000",7,".",",");
	accountGrid.setNumberFormat("0,000",8,".",",");
	accountGrid.setNumberFormat("0,000",9,".",",");
	accountGrid.setNumberFormat("0,000",10,".",",");
	accountGrid.enableColSpan(true);
	accountGrid.init();

	function doOnRowDblClicked(rowId, cellInd){
		//2021.02.23 tid까지 넘겨야 함
		var mid = accountGrid.cellById(rowId, 4).getValue();
		var tid = accountGrid.cellById(rowId, 2).getValue();
		var stime	= $('#stime').val();
		var etime	= $('#etime').val();
		var samt	= $('#samt').val();
		var eamt	= $('#eamt').val();
		var detail_view = "sub02_01.jsp";
		var seturl = "detail_view="+detail_view+"&mid="+mid+"&stime="+stime+"&etime="+etime+"&samt="+samt+"&eamt="+eamt+"&tid="+tid;

		parent.urlgoDirect(-1, "카드사상세(V)", "sub02_04.jsp?" + seturl);
	}

	accountGrid.attachEvent("onXLE", function() {
		var count=accountGrid.getRowsNum();
		
		//accountGrid.setColspan("total",0,5);
		
		$('#grid_total_rows').html(count);
	}); 

	function wherequery(){
		var stime	= $('#stime').val();
		var etime	= $('#etime').val();
		var acqcd	= $('#acqcd  option:selected').val();
		var depcd	= $('#depcd  option:selected').val();
		var samt = $('#samt').val();
		var eamt = $('#eamt').val();
		var appno = $('#appno').val();
		var tid = $("#tid option:selected").val();
		var mid = $('#mid').val();
		
		var w	 = "uauth=<%=tuser%>";

		w	+= (stime!="") ? "&stime="+stime : "";
		w	+= (etime!="") ? "&etime="+etime : "";
		w	+= (acqcd!="") ? "&acqcd="+acqcd : "";
		w	+= (depcd!="") ? "&depcd="+depcd : "";
		w	+= (samt!="") ? "&samt="+samt : "";
		w	+= (eamt!="") ? "&eamt="+eamt : "";
		w	+= (appno!="") ? "&appno="+appno : "";
		w	+= (tid!="") ? "&tid="+tid : "";
		w	+= (mid!="") ? "&mid="+mid : "";
		
		return w;
	}

	function acc_exceldn(){
		/*
		var exsrc	= "./ifou_xml_was/excel0201.jsp?"+wherequery();
		$.getJSON(exsrc, 
			{ 
				tags: "mount rainier", 
				tagmode: "any", 
				format: "json"
			}, 
			function(data, status) { 
				if(data["RST"]=="S000"){
					$('#totalarray').val(data["TOTALARRAY"]);
					$('#itemarray').val(data["ITEMARRAY"]);
					$("#excelStime").val($("#stime").val());
					$("#excelEtime").val($("#etime").val());
					$('#exceldn').submit();
				}
			} 
		);
		*/
		$('#exceldn').submit();
	}
	
	function trans_add(){
		var seqno = accountGrid.getSelectedId();
		var seturl = "./ifou_xml_was/detail_0204.jsp?uauth=<%=tuser%>&seqno=" + seqno;
		
		$.getJSON(seturl, 
			{ 
				tags: "mount rainier", 
				tagmode: "any", 
				format: "json"
			}, // 서버가 필요한 정보를 같이 보냄. 
			function(data, status) { 
				if(data["RST"]=="S000"){
					alert(data["UDATA"]);
					//postuserinfo(data["UDATA"], data["USERMENU"], data["SELETID"], data["SELEDEPO"]);
					//detail_pop_view(url, 1000, 600);
				}
			} 
		);
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
				text:"검색기간이 초과되었습니다.<br>검색 기간은 31일까지 가능합니다."
			});
			return false;
		}
		
		if($("#rendchk").prop("checked")){
			accountGrid.enableSmartRendering(true,200);
		}else{
			accountGrid.enableSmartRendering(false);
		}

		AccLayout.items[0].progressOn();
		AccLayout.items[1].progressOn();
		totalGrid.clearAndLoad("./ifou_xml_was/total_0201.jsp?"+wherequery(), afterload_prog01, "json");
		accountGrid.clearAndLoad("./ifou_xml_was/item_0201.jsp?"+wherequery(), afterload_prog02, "json");
	}
	
</script>
<!-- <form id="exceldn" method="post" name="exceldn" target="subq" action="./excel/exceldn_0201.jsp">
	<input type="hidden" name="totalarray" id="totalarray">
	<input type="hidden" name="itemarray" id="itemarray">
	excel stime, etime 설정해서 표기함
	<input type="hidden" name="excelStime" id="excelStime">
	<input type="hidden" name="excelEtime" id="excelEtime">
</form> -->
<iframe name="subq" id="subq" style="width:100px; height:100px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>