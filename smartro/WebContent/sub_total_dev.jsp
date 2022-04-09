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

			var hset	= (h-250) + "px";

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

			
			byId("stime").value = "<%=sdate.format(nowTime)%>";
			byId("etime").value = "<%=edate.format(nowTime)%>";

			
			$("#parentId").css("min-height","600px").css("min-width","860px");
			$(".dhx_cell_cont_layout").css("width","100%");

		}

		$(window).resize(function(){
			var h = $(document).height();
			var w = $(document).width();
			
			$(".dhx_cell_layout, .dhx_cell_cont_layout").css("width","100%").css("min-width","1160px");
			$(".dhx_cell_cont_layout").css("border","0").css("width","100%").css("border-top","1px solid #c0c0c0").css("border-bottom","1px solid #c0c0c0");
			$(".cont_title").css("margin","0 2px").css("min-width","550px");
			
			var hset	= (h-250) + "px";
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
		<form name="regular" method="post" action="">
		<input type="hidden" name="extitle" id="extitle" value="">
		<input type="hidden" name="casher" id="casher" value="">
		<input type="hidden" name="search" value="Y">
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
				<td><span class="schtitle">매입사</span></td>
				<td>
					<select name='acqcd' id='acqcd' style='font-size:9pt;' class='searchbox_drop'>
						<option value=''>:: 전체 ::</option>
						<option value='1106,026,VC0006' >비씨카드</option>
						<option value='2211,018,VC0030' >농협카드</option>
						<option value='1101,016,VC0001' >국민카드</option>
						<option value='1104,031,VC0004' >삼성카드</option>
						<option value='1105,008,VC0005' >하나카드</option>
						<option value='1103,047,VC0003' >롯데카드</option>
						<option value='1102,027,VC0002' >현대카드</option>
						<option value='1107,029,VC0007' >신한카드</option>
					</select>
				</td>
				<td><span class="schtitle">수납자</span></td>
				<td>
					<input type="text" name="casher" id="casher" class="searchbox_large" value="">
				</td>
				<td><span class="schtitle">사업부</span></td>
				<td>
					<%=userdepo%>
				</td>
				<td align="right"><img src="./images/btn/btn_search.gif" onclick="search_go();" style="cursor:pointer;"></td>
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
					<span class='button large icon' onclick='trans_add();'><span class='print'></span><a href='#'>화면인쇄</a></span>
				</td>
			</tr>
		</table>
	</div>
	<div id="parentId" style="position: relative; top: 0px; left: 0px; width: 100%; height:300px;"></div>
	<input type="hidden" name="getmid" id="getmid" value="<?=$mid?>">
	<input type="hidden" name="tid" id="tid" value="<?=$tid?>">
	<input type="hidden" name="svrday" id="svrday">
	<input type="hidden" name="svrtime" id="svrtime">
	<input type="hidden" name="setpage" id="setpage">
	<input type="hidden" name="pagegroup" id="pagegroup">
</div>
<script type="text/javascript">
    AccLayout = new dhtmlXLayoutObject("parentId", "1C");
	AccLayout.setSkin("dhx_web");	
	AccLayout.cells("a").hideHeader();

	accountGrid = AccLayout.cells("a").attachGrid();

	var fields   = "순번,승인일,매입사,체크승인,신용승인,체크취소,신용취소,체크승인금액,신용승인금액,체크취소금액,신용취소금액,체크합계,신용합계";
	var aligns	 = "center,center,left,right,right,right,right,right,right,right,right,right,right";
	var colTypes  = "ro,ro,ro,ron,ron,ron,ron,ron,ron,ron,ron,ron,ron";
	var sorts	 = "int,str,str,str,str,str,str,str,str,str,str,str,str";
	var colWidth	 = "60,120,100,*,*,*,*,*,*,*,*,*,*";

	accountGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	accountGrid.setSkin("dhx_web");	
	accountGrid.setHeader(fields);
	accountGrid.setInitWidths(colWidth);
	accountGrid.setColTypes(colTypes);
	accountGrid.setColAlign(aligns);
	accountGrid.setColSorting(sorts);
	accountGrid.attachEvent("onRowDblClicked",doOnRowDblClicked);
	accountGrid.setNumberFormat("0,000",3,".",",");
	accountGrid.setNumberFormat("0,000",4,".",",");
	accountGrid.setNumberFormat("0,000",5,".",",");
	accountGrid.setNumberFormat("0,000",6,".",",");
	accountGrid.setNumberFormat("0,000",7,".",",");
	accountGrid.setNumberFormat("0,000",8,".",",");
	accountGrid.setNumberFormat("0,000",9,".",",");
	accountGrid.setNumberFormat("0,000",10,".",",");
	accountGrid.setNumberFormat("0,000",11,".",",");
	accountGrid.setNumberFormat("0,000",12,".",",");
	accountGrid.init();
	
	function doOnRowDblClicked(rowId, cellInd){
		var seturl = "./ifou_xml_was/detail_0204.jsp?uauth=<%=tuser%>&seqno=" + rowId;

		$.getJSON(seturl, 
			{ 
				tags: "mount rainier", 
				tagmode: "any", 
				format: "json"
			}, // 서버가 필요한 정보를 같이 보냄. 
			function(data, status) { 
				if(data["RST"]=="S000"){
					detail_pop_view("detail_view_van.jsp?seq="+rowId+"&udata="+data["UDATA"], 1000, 600);
				}
			} 
		);
	}

	accountGrid.attachEvent("onXLE", function() {
		AccLayout.items[0].progressOff();
		
		var count=accountGrid.getRowsNum();
		$('#grid_total_rows').html(count);
	}); 

	function leadingZeros(n, digits) {
	  var zero = '';
	  n = n.toString();
	  if (n.length < digits) {
		for (var i = 0; i < digits - n.length; i++){
		  zero += '0';
		 }
	  }
	  return zero + n;
	}

	function wherequery(){
		var stime	= $('#stime').val();
		var etime	= $('#etime').val();
		var casher	= $('#casher').val();
		var acqcd	= $('#acqcd  option:selected').val();
		var depcd	= $('#depcd  option:selected').val();

		
		var w	 = "uauth=<%=tuser%>&";

		w	+= (stime!="") ? "&stime="+stime : "";
		w	+= (etime!="") ? "&etime="+etime : "";
		w	+= (casher!="") ? "&casher="+casher : "";
		w	+= (acqcd!="") ? "&acqcd="+acqcd : "";
		w	+= (depcd!="") ? "&depcd="+depcd : "";
		
		return w;
	}

	
	
	function acc_exceldn(){
		var exsrc	= "./ifou_xml_was/excel0204.jsp?"+wherequery();
		//$('#subq').attr('src', exsrc);
		
		//2021.01.27 엑셀 다운로드 flow
		//excel0204.jsp -> RST:S000 exceldn submit -> exceldn_0204.jsp
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
					
					$('#exceldn').submit();
				}
			} 
		);
	}
	
	function exceldown(totalarray, itemarray){
		// POST 방식으로 서버에 HTTP Request를 보냄. 
		$.post("login_check.jsp", 
			{ totarray: totalarray, itemarray: itemarray}, // 서버가 필요한 정보를 같이 보냄. 
			function(data, status) { 
				window.location.replace("./sub_main.jsp");
			} 
		);
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
	
	function tstat_check(obj){
		if(obj==1){
			if($("#tstat01").prop("checked")){
				$("#tstat02").attr('checked', false);
				$("#tstat03").attr('checked', false);
				$("#tstat04").attr('checked', false);
			}
		}else{
			$("#tstat01").attr('checked', false);
		}
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
		accountGrid.clearAndLoad("./ifou_xml_was/total_0000.jsp?"+wherequery(), afterload_prog01, "json");
	}
	
	function afterload_prog01(){
		AccLayout.items[0].progressOff();
	}
	
</script>
<form id="exceldn" method="post" name="exceldn" target="subq" action="./excel/exceldn_0204.jsp">
	<input type="text" name="totalarray" id="totalarray">
	<input type="text" name="itemarray" id="itemarray">
</form>
<iframe name="subq" id="subq" style="width:100px; height:100px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>