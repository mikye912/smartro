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

	Date nowTime = new Date();
	SimpleDateFormat sf = new SimpleDateFormat("yyyy년 MM월 dd일 a hh:mm:ss");
	SimpleDateFormat setdate = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat sdate = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat edate = new SimpleDateFormat("yyyyMMdd");
	
	//2021.02.21 현금영수증 column list
	String[] pageColumn = jbset.get_page_column(tuser, "tr");
	String fields = pageColumn[0];
	String aligns = pageColumn[1];
	String colTypes = pageColumn[2];
	String sorts = pageColumn[3];
	String colWidth = pageColumn[4];
	int amtset = Integer.parseInt(pageColumn[5]);
	
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
	
	<link href="./jeegoopopup/skins/basic/style.css" rel="Stylesheet" type="text/css" />
	<link href="./jeegoopopup/skins/black/style.css" rel="Stylesheet" type="text/css" />
	<link href="./jeegoopopup/skins/blue/style.css" rel="Stylesheet" type="text/css" />
	<link href="./jeegoopopup/skins/clean/style.css" rel="Stylesheet" type="text/css" />
	<link href="./jeegoopopup/skins/gray/style.css" rel="Stylesheet" type="text/css" />
	<link href="./jeegoopopup/skins/round/style.css" rel="Stylesheet" type="text/css" />
	<script type="text/javascript" src="./jeegoopopup/jquery-1.10.2.min.js"></script>
	<script type="text/javascript" src="./jeegoopopup/jquery.jeegoopopup.1.0.0.js"></script>
	
	<script>
		var myCalendar;
		var AccLayout, accountGrid;
		function doOnLoad() {
			var h = $(window).height();
			var w = $(window).width();
			
			var hset	= (h-230) + "px";

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

			$("#parentId").css("min-height","600px").css("min-width","860px");
			$(".dhx_cell_cont_layout").css("width","100%");
		}

		$(window).resize(function(){
			var h = $(document).height();
			var w = $(document).width();
			
			$(".dhx_cell_layout, .dhx_cell_cont_layout").css("width","100%").css("min-width","1160px");
			$(".dhx_cell_cont_layout").css("border","0").css("width","100%").css("border-top","1px solid #c0c0c0").css("border-bottom","1px solid #c0c0c0");
			$(".cont_title").css("margin","0 2px").css("min-width","550px");
			
			var hset	= (h-230) + "px";
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

	function detail_trans_view(url){
		var options = {
			width: 700,
			height: 400,
			center: true,
			fixed: true,
			skinClass: 'jg_popup_clean',
			overlay: 50,
			overlayColor: '#000',
			fadeIn: 0,
			url:url
		};
		$.jeegoopopup.open(options);
	}

	function ipopup_close(){
		 $.jeegoopopup.close();
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
	<form name="exceldn" id="exceldn"  method="post" action="./excel/exceldn_0211.jsp">
		<input type='hidden' name='extitle' id='extitle' value=''>
		<input type="hidden" name="uauth" value="<%=tuser%>">
		<input type='hidden' name='search' value='Y'>
		<table class="tb" border="1" bordercolor="#e6e6e6" cellpadding="5" style="width: 100%; border-collapse: collapse;">
		<colgroup>
		<col class="cellC" style="width:100px;">
		<col class="cellL" style="width:180px">
		<col class="cellC" style="width:100px;">
		<col class="cellL" style="width:180px">
		<col class="cellC" style="width:100px;">
		<col class="cellL" style="width:210px">
		<col class="cellC" style="width:100px;">
		<col class="cellL">
		<col class="cellL" style="width:100px;">
		</colgroup>
			<tbody>
			<tr height="34">
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
					<input type='text' name='samt' id='samt' class="searchbox_nomal" value="">
					~
					<input type='text' name='eamt' id='eamt' class="searchbox_nomal" value="">
				</td>
				<td>
					<span class='schtitle'>승인번호</span>
				</td>
				<td>
					<input type='text' name='appno' id='appno' class="searchbox_large" value="">
				</td>
				<td>
					<span class='schtitle'>진료구분</span>
				</td>
				<td>
					<select name="medi_gb" id="medi_gb" class="searchbox_drop">
						<option value=""> :: 진료구분선택 :: </option>
						<option value="1">외래</option>
						<option value="2">응급</option>
						<option value="3">입원</option>
						<option value="4">종합검진</option>
						<option value="5">일반검진</option>
						<option value="6">장례식장</option>
					</select>
				</td>
				<td rowspan="3" align="right"><img src='./images/btn/btn_search.gif' onclick='search_go();' style='cursor:pointer;'></td>
			</tr>
			<tr height="34">
				<td>
					<span class='schtitle'>등록번호</span>
				</td>
				<td>
					<input type='text' name='pid' id='pid'  class="searchbox_large" value="">
				</td>
				<td>
					<span class='schtitle'>수납자</span>
				</td>
				<td>
					<input type='text' name='mediid' id='mediid'  class="searchbox_large" value="">
				</td>
				<td>
					<span class='schtitle'>진료과</span>
				</td>
				<td>
					<input type='text' name='medi_cd' id='medi_cd'  class="searchbox_large" value="">
				</td>
				<td></td>
				<td></td>
			</tr>
			<tr height="34">
				<td>
					<span class='schtitle'>인증번호</span>
				</td>
				<td>
					<input type='text' name='cardno' id='cardno' class="searchbox_large" value="">
				</td>
				<td>
					<span class='schtitle'>거래코드</span>
				</td>
				<td>
					<input type='text' name='tradeidx' id='tradeidx' class="searchbox_large" value="">
				</td>
				<td>
					<span class='schtitle'>승인구분</span>
				</td>
				<td>
					<input type="checkbox" name="auth01" id="auth01" checked onclick="auth_chk(1);" value="Y">전체거래
					<input type="checkbox" name="auth02" id="auth02" onclick="auth_chk(2);" value="Y">승인거래
					<input type="checkbox" name="auth03" id="auth03" onclick="auth_chk(3);" value="Y">취소거래
				</td>
				<td></td>
				<td></td>
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
		</table>
	</div>
	<div id="parentId" style="position: relative; top: 0px; left: 0px; width: 100%; height:220px;"></div>
	<input type="hidden" name="getmid" id="getmid" value="">
	<input type="hidden" name="tid" id="tid" value="">
	<input type="hidden" name="svrday" id="svrday">
	<input type="hidden" name="svrtime" id="svrtime">
</div>
<script type="text/javascript">
    AccLayout = new dhtmlXLayoutObject("parentId", "2E");
	AccLayout.setSkin("dhx_web");	
	AccLayout.cells("a").hideHeader();
	AccLayout.cells("b").hideHeader();

	totalGrid = AccLayout.cells("a").attachGrid();
	var tfields		= "순번,사업부,승인건수,승인금액,취소건수,취소금액,총건수,합계금액";
    var taligns		= "center,left,right,right,right,right,right,right";
    var tcoltypes	= "ro,ro,ro,ron,ro,ron,ro,ron";
    var tsorts		= "str,str,int,int,int,int,int,int";
    var tcolwidth   = "80,360,110,150,110,150,110,150";
	var filterVal = "", filterIdx="";

	totalGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	totalGrid.setSkin("dhx_web");	
	totalGrid.setHeader(tfields);
	totalGrid.setInitWidths(tcolwidth);
	totalGrid.setColTypes(tcoltypes);
	totalGrid.setColAlign(taligns);
	totalGrid.setColSorting(tsorts)
	totalGrid.setNumberFormat("0,000",3,".",",");
	totalGrid.setNumberFormat("0,000",5,".",",");
	totalGrid.setNumberFormat("0,000",7,".",",");
	totalGrid.init();

	totalGrid.attachEvent("onXLE", function() {
		totalGrid.setRowTextStyle("total", "text-align:right;font-weight:bold;background-color:#f3fafe;color:#006699;border:1px solid #ffffff;");
	}); 
	
	accountGrid = AccLayout.cells("b").attachGrid();

	accountGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	accountGrid.setSkin("dhx_web");
	accountGrid.setHeader("<%=fields%>");
	accountGrid.setInitWidths("<%=colWidth%>");
	accountGrid.setColTypes("<%=colTypes%>");
	accountGrid.setColAlign("<%=aligns%>");
	accountGrid.setColSorting("<%=sorts%>");
	accountGrid.attachEvent("onRowDblClicked",doOnRowDblClicked);
	accountGrid.setNumberFormat("0,000",<%=amtset%>,".",",");
	accountGrid.init();

	//2021.02.23 현금영수증 더블클릭 - 웹취소
	function doOnRowDblClicked(rowId, cellInd){
		var appno = accountGrid.cellById(rowId, 7).getValue();
		var seturl = "./ifou_xml_was/detail_0211.jsp?uauth=<%=tuser%>&seqno="+rowId+"&appno="+appno;
		
		$.getJSON(seturl, 
				{ 
					tags: "mount rainier", 
					tagmode: "any", 
					format: "json"
				}, // 서버가 필요한 정보를 같이 보냄. 
				function(data, status) { 
					if(data["RST"]=="S000"){
						var url = "./detail_view_subcash01.jsp?seq="+rowId+"&appno="+appno+"&udata="+data["UDATA"]+"&purl=V";
						detail_trans_view(url);
					}
				} 
			);
	}

	accountGrid.attachEvent("onXLE", function() {
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
		var samt	= $('#samt').val();
		var eamt	= $('#eamt').val();
		var appno	= $('#appno').val();
		//var tid	= $('#tid  option:selected').val();
		//등록번호
		var pid		= $('#pid').val();
		//수납자
		var mediid = $('#mediid').val();
		//진료과
		var medi_cd = $('#medi_cd').val();
		//진료구분
		var medi_gb = $('#medi_gb  option:selected').val();
		var cardno	= $('#cardno').val();
		var tradeidx	= $('#tradeidx').val();
		var auth01	= $('#auth01').val();
		var auth02	= $('#auth02').val();
		var auth03	= $('#auth03').val();

		var todaysvr = $('#svrday').val();
		var timeset = $('#svrtime').val();

		var w	 = "uauth=<%=tuser%>&";
		
		w	+= (todaysvr!="") ? "&svrday="+todaysvr : "";
		w	+= (timeset!="") ? "&svrtime="+timeset : "";

		w	+= (stime!="") ? "&stime="+stime : "";
		w	+= (etime!="") ? "&etime="+etime : "";
		w	+= (samt!="") ? "&samt="+samt : "";
		w	+= (eamt!="") ? "&eamt="+eamt : "";
		w	+= (appno!="") ? "&appno="+appno : "";
		//w	+= (tid!="") ? "&tid="+tid : "";
		w	+= (pid!="") ? "&pid="+pid : "";
		w	+= (mediid!="") ? "&mediid="+mediid : "";
		w	+= (medi_cd!="") ? "&medi_cd="+medi_cd : "";
		w	+= (medi_gb!="") ? "&medi_gb="+medi_gb : "";
		w	+= (cardno!="") ? "&cardno="+cardno : "";
		w	+= (tradeidx!="") ? "&tradeidx="+tradeidx : "";
		if($("#auth01").prop("checked")){w	+= (auth01!="") ? "&auth01="+auth01 : "";}
		if($("#auth02").prop("checked")){w	+= (auth02!="") ? "&auth02="+auth02 : "";}
		if($("#auth03").prop("checked")){w	+= (auth03!="") ? "&auth03="+auth03 : "";}

		return w;
	}

	function acc_exceldn(){
		//var exsrc	= "./excel/excel0211.php?"+wherequery();
		//$('#subq').attr('src', exsrc);
		$('#exceldn').submit();
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
		AccLayout.items[1].progressOn();
		totalGrid.clearAndLoad("./ifou_xml_was/total_0211.jsp?"+wherequery(), afterload_prog01, "json");
		accountGrid.clearAndLoad("./ifou_xml_was/item_0211.jsp?"+wherequery(), afterload_prog02, "xml");
	}
	
	function afterload_prog01(){
		AccLayout.items[0].progressOff();
	}
	
	function afterload_prog02(){
		AccLayout.items[1].progressOff();
	}
	
	function acc_exceldn(){
		$('#exceldn').submit();
	}
	
</script>

<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>