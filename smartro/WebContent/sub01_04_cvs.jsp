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
	
	String gridfoot = "";
	
	//2022.02.04 청구일자별조회 accountGrid 컬럼구성
	//fields, aligns, colTypes, sorts, colWidth, amtset
	String[] pageColumn = jbset.get_page_column(tuser, "van");
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
<!-- 청구일자별조회 -->
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

		var hset	= (h-230) + "px";

		document.getElementById("parentId").style.height = hset;
		AccLayout.setSizes();

		myCalendar = new dhtmlXCalendarObject(["stime","etime","sappdd","eappdd"]);
		myCalendar.attachEvent("onClick", function(d){
			byId("etime").value = myCalendar.getFormatedDate(null,d);
		});
		myCalendar.setWeekStartDay(7);
		myCalendar.setDate("<%=setdate.format(nowTime)%>");
		myCalendar.hideTime();
		myCalendar.setSkin('dhx_skyblue');

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
	span.label {font-family: Tahoma;font-size: 12px;}
	table.tb {border-collapse: collapse;}
	table.tb>tr>td,
	table.tb>tr>th,
	table.tb>tbody>tr>td,
	table.tb>tbody>tr>th {border:1px solid #cccccc; padding:5px;}

	.hdrcell{text-align:center;font-weight:bold;}
	.totaldata{text-align:right;font-weight:none;height:24px;background-color:#f0f0f0;padding-top:8px;}
	.schtitle{font-size:9pt;}
	.dhx_cell_hdr{border:0px solid #ffffff;}
	.dhx_cell_cont_layout{border:0px solid #ffffff;}
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
	<form name="exceldn" id="exceldn"  method="post" action='./excel/exceldn_0104_cvs.jsp'>
		<input type='hidden' name='extitle' id='extitle' value=''>
		<input type='hidden' name='search' value='Y'>
		<input type="hidden" name="uauth" value="<%=tuser%>">
		<table class="tb" border="1" bordercolor="#e6e6e6" cellpadding="5" style="width: 100%; border-collapse: collapse;">
		<colgroup>
		<col class="cellC" style="width:100px;">
		<col class="cellL" style="width:200px">
		<col class="cellC" style="width:100px;">
		<col class="cellL" style="width:200px">
		<col class="cellC" style="width:100px;">
		<col class="cellL" style="width:210px">
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
					<input type='text' name='stime' id='stime' class="searchbox_nomal" onclick="setSens('etime', 'max');" onblur="datesam();">
					~
					<input type='text' name='etime' id='etime' class="searchbox_nomal" onclick="setSens('stime', 'min');" >
				</td>
				<td><span class="schtitle">승인금액</span></td>
				<td>
					<input type="text" name="samt" id="samt" class="searchbox_nomal" value="">
					~
					<input type="text" name="eamt" id="eamt" class="searchbox_nomal" value="">
				</td>
				<td>
					<span class='schtitle'>승인번호</span>
				</td>
				<td>
					<input type='text' name='appno' id='appno' class="searchbox_large">
				</td>
				<td></td>
				<td></td>
				<td rowspan="3" align="center"><img src='./images/btn/btn_search.gif' onclick='search_go()' style='cursor:pointer;'></td>
			</tr>
			<tr height="36">
				<td>
					<span class="schtitle">사업부</span>
				</td>
				<td>
					<select id="tid2" name="tid2" style="width:165px; height:22px">
						<option value="">:: 사업부선택 ::</option>
						<option value="7635015001_39242631_39257046_7635053001">GS25</option>
                        <option value="7017728001_39242632_39257048_7635054001">GS수퍼</option>
                        <option value="7642904001_39242633_39257047_7635055001">랄라블라</option>
					</select>
				</td>
				<td>
					<span class='schtitle'>단말기번호</span>
				</td>
				<td>
					<%=usertid%>
				</td>
				<td>
					<span class='schtitle'>카드사선택</span>
				</td>
				<td>
					<%=useracq%>
				</td>
				<td></td>
				<td></td>
			</tr>
			<tr height="36">
			
				<td>
					<span class='schtitle'>거래코드</span>
				</td>
				<td>
					<input type='text' name='tradeidx' id='tradeidx' class="searchbox_large" value="">
				</td>
				<td>
					<span class='schtitle'>가맹점번호</span>
				</td>
				<td>
					<input type='text' name='mid' id='mid' class="searchbox_large" value="">
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
	<div style="width:100%; height:10px;"></div>
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
		<div id="parentId" style="position: relative; top: 0px; left: 0px; width: 100%; height:300px;"></div>
<%-- 	<input type="hidden" name="getmid" id="getmid" value="<%=mid%>">
	<input type="hidden" name="tid" id="tid" value="<%=tid %>"> --%>
	<input type="hidden" name="svrday" id="svrday">
	<input type="hidden" name="svrtime" id="svrtime">
	<input type="hidden" name="setpage" id="setpage">
	<input type="hidden" name="pagegroup" id="pagegroup">
</div>
</div>
<script type="text/javascript">
	AccLayout = new dhtmlXLayoutObject("parentId", "2E");
	AccLayout.setSkin("dhx_web");	
	AccLayout.cells("a").hideHeader();
	AccLayout.cells("b").hideHeader();

	totalGrid = AccLayout.cells("a").attachGrid();
	var tfields		= "순번,사업부,승인건수,승인금액,취소건수,취소금액,총건수,합계(승인-취소)금액,비씨,농협,국민,삼성,하나,롯데,현대,신한";
    var taligns		= "center,left,right,right,right,right,right,right,right,right,right,right,right,right,right,right";
    var tcoltypes	= "ro,ro,ro,ro,ro,ro,ro,ro,ro,ro,ro,ro,ro,ro,ro,ro";
    var tsorts		= "str,str,int,int,int,int,int,int,int,int,int,int,int,int,int,int";
    var tcolwidth   = "60,260,*,*,*,*,*,220,*,*,*,*,*,*,*,*";
	var filterVal 	= "", filterIdx="";

	totalGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	totalGrid.setSkin("dhx_web");	
	totalGrid.setHeader(tfields);
	totalGrid.setInitWidths(tcolwidth);
	totalGrid.setColTypes(tcoltypes);
	totalGrid.setColAlign(taligns);
	totalGrid.setColSorting(tsorts);
	totalGrid.init();

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
	accountGrid.attachFooter("<div id='nr_navi'>0</div>,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan",["text-align:left;"]);

	accountGrid.attachEvent("onXLE", function() {
		AccLayout.items[0].progressOff();
		AccLayout.items[1].progressOff();
		
		var svrday		= accountGrid.cells(accountGrid.getRowId(0), 7).getValue();
		var svrtime		= accountGrid.cells(accountGrid.getRowId(0), 8).getValue();

		$("#svrday").val(svrday);
		$("#svrtime").val(svrtime);
		
		var count=accountGrid.getRowsNum();
		
		$('#grid_total_rows').html(count);
	});
	
	function doOnRowDblClicked(rowId, cellInd){
		var url = "./detail_view_van.html?uauth=<%=tuser%>&seq="+rowId;

		detail_pop_view(url, 700, 450);
	}

	function glb_where(){
		var stime	= byId("stime").value;
		var etime	= byId("etime").value;
		var samt	= $('#samt').val();
		var eamt	= $('#eamt').val();
		var appno	= $('#appno').val();
		//var pid		= $('#pid').val();
		var tradeidx = $('#tradeidx').val();
		var tid2 	= $('#tid2 option:selected').val();
		
		//var tstat01	= $('#tstat01').val();
		//var tstat02	= $('#tstat02').val();
		//var tstat03	= $('#tstat03').val();
		//var tstat04	= $('#tstat04').val();
		
		var auth01	= $('#auth01').val();
		var auth02	= $('#auth02').val();
		var auth03	= $('#auth03').val();

		var mid	= $('#mid').val();
		var tid	= $('#tid  option:selected').val();
		var acqcd	= $('#acqcd  option:selected').val();

		var todaysvr = $('#svrday').val();
		var timeset = $('#svrtime').val();
		var getpage = $("#setpage").val();
		
		var w	 = "uauth=<%=tuser%>";
		w	+= (stime!="") ? "&stime="+stime : "";
		w	+= (etime!="") ? "&etime="+etime : "";
		w	+= "&page="+getpage;
		
		w	+= (todaysvr!="") ? "&svrday="+todaysvr : "";
		w	+= (timeset!="") ? "&svrtime="+timeset : "";

		w	+= (samt!="") ? "&samt="+samt : "";
		w	+= (eamt!="") ? "&eamt="+eamt : "";
		w	+= (appno!="") ? "&appno="+appno : "";
		//w	+= (pid!="") ? "&pid="+pid : "";
		w	+= (tradeidx!="") ? "&tradeidx="+tradeidx : "";
		//if($("#tstat01").prop("checked")){w	+= (tstat01!="") ? "&tstat01="+tstat01 : "";}
		//if($("#tstat02").prop("checked")){w	+= (tstat02!="") ? "&tstat02="+tstat02 : "";}
		//if($("#tstat03").prop("checked")){w	+= (tstat03!="") ? "&tstat03="+tstat03 : "";}
		//if($("#tstat04").prop("checked")){w	+= (tstat04!="") ? "&tstat04="+tstat04 : "";}
		if($("#auth01").prop("checked")){w	+= (auth01!="") ? "&auth01="+auth01 : "";}
		if($("#auth02").prop("checked")){w	+= (auth02!="") ? "&auth02="+auth02 : "";}
		if($("#auth03").prop("checked")){w	+= (auth03!="") ? "&auth03="+auth03 : "";}
		//w	+= (cardno!="") ? "&cardno="+cardno : "";
		w	+= (mid!="") ? "&mid="+mid : "";
		w	+= (tid!="") ? "&tid="+tid : "";
		w	+= (acqcd!="") ? "&acqcd="+acqcd : "";
		w	+= (tid2!="") ? "&tid2="+tid2 : "";

		return w;
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

	function afterload_prog01(){
		AccLayout.items[0].progressOff();
	}
	
	function afterload_prog02(){
		AccLayout.items[1].progressOff();
	}

	function acc_exceldn(){
		$('#exceldn').submit();
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
		
		var st = srvTime();
		var today = new Date(st);
		var tmon   = leadingZeros((today.getMonth()+1), 2);
		var tday	= leadingZeros(today.getDate(), 2);
		var thour	= leadingZeros(today.getHours(), 2);
		var tminu	= leadingZeros(today.getMinutes(), 2);
		var tsecon	= leadingZeros(today.getSeconds(), 2);
		var todaysvr	= today.getFullYear()+'-'+tmon+'-'+tday;
		var timeset = thour+':'+tminu+':'+tsecon;

		if($("#rendchk").prop("checked")){
			accountGrid.enableSmartRendering(true,200);
		}else{
			accountGrid.enableSmartRendering(false);
		}

		$("#setpage").val("");
		$("#pagegroup").val("");
		
		 $.ajax({
			url: "./ifou_xml_was/sub0104page.jsp?"+glb_where(),
			type:"GET",
			dataType: 'JSON',
            success: function(data){
            	const obj = Object.keys(data);
            	if(obj<2){
            		page(data.apn_page);
            	}else{
            		set_navi(data.apn_page, data.npage);
            	}

            }
		}); 
	}
	/*
	function set_page(obj){
		$("#setpage").val(obj);

		var setn = $("#pagegroup").val();
		var exsrc	= "./ifou_xml_was/sub0104page.jsp?"+glb_where()+"&npage="+setn+"&pmode=";

		$('#subq').attr('src', exsrc);
	}
	*/
	function afterload_prog01(){
		AccLayout.items[0].progressOff();
	}
	
	function afterload_prog02(){
		AccLayout.items[1].progressOff();
	}
	
	function page(obj){
		$('#nr_navi').html(obj);
		$("#nr_navi").css("min-height","25px").css("font-size","11pt").css("padding-top", "5px");
		AccLayout.items[0].progressOn();
		AccLayout.items[1].progressOn();
		totalGrid.clearAndLoad("./ifou_xml_was/total_0104_cvs.jsp?"+glb_where(), afterload_prog01, "json");	
		accountGrid.clearAndLoad("./ifou_xml_was/item_0104_cvs.jsp?"+glb_where(), afterload_prog02, "json");
	}

	function set_navi(obj, np){
		$("#pagegroup").val(np);
		$('#nr_navi').html(obj);
		$("#nr_navi").css("min-height","25px").css("font-size","11pt").css("padding-top", "5px");
	}

	function move_navi(obj){
		var setn = $("#pagegroup").val();
		var exsrc	= "./ifou_xml_was/sub0104page.jsp?"+glb_where()+"&npage="+setn+"&pmode="+obj;
		$('#subq').attr('src', exsrc);
	}
	
</script>
<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>