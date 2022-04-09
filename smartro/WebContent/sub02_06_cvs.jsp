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
	
	
	//2021.02.17 상세내역조회 accountGrid 컬럼구성
	//fields, aligns, colTypes, sorts, colWidth, amtset
	String[] pageColumn = jbset.get_page_column(tuser, "van");
	String fields = pageColumn[0];
	String aligns = pageColumn[1];
	String colTypes = pageColumn[2];
	String sorts = pageColumn[3];
	String colWidth = pageColumn[4];
	int amtset = Integer.parseInt(pageColumn[5]);
	
	//2021.02.23 월일자별조회 -> 일자별 상세 : 승인일자, mid
	//2021.02.03 카드사별조회 -> 카드사상세로 넘어오는 경우
	//2021.02.23 매장별거래조회 -> 매장별상세
	//null check 필수
	String detail_view = request.getParameter("detail_view") == null ? "" : request.getParameter("detail_view");
	String detail_mid = request.getParameter("mid") == null ? "" : request.getParameter("mid");
	String detail_stime = request.getParameter("stime") == null ? "" : request.getParameter("stime");
	String detail_etime = request.getParameter("etime") == null ? "" : request.getParameter("etime");
	String detail_samt = request.getParameter("samt") == null ? "" : request.getParameter("samt");
	String detail_eamt = request.getParameter("eamt") == null ? "" : request.getParameter("eamt");
	String detail_tid = request.getParameter("tid") == null ? "" : request.getParameter("tid");
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

			//detail_view가 빈값이 아니면 어딘가에서 넘어와요
			var detail_view = "<%=detail_view%>";
			if(detail_view != ""){
				var detail_mid = "<%=detail_mid%>";
				var detail_tid = "<%=detail_tid%>";
				byId("stime").value = "<%=detail_stime%>";
				byId("etime").value = "<%=detail_etime%>";
				byId("samt").value = "<%=detail_samt%>";
				byId("eamt").value = "<%=detail_eamt%>";
				
				//mid가 넘어올수도 있고, tid가 넘어올 수도 있는데 넘어오는 거에 따라 setting
				if(detail_mid != ""){
					byId("mid").value = detail_mid;
				}
				if(detail_tid != ""){
					//tid chack로 변경해야 함.
					$("#tid").val("<%=detail_tid%>").attr("selected", true);
				}
				
				search_go();
				
			} else {
				byId("stime").value = "<%=setdate.format(nowTime)%>";
				byId("etime").value = "<%=setdate.format(nowTime)%>";
			}
			
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
				width: 900,
				height: 480,
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
	<!-- 2021.02.21 form submit ::
	기존 방식대로 한번 거쳐서 전달할라고 보니.... 일정 길이가 넘어가면 null발생함. ㅜㅜ... -->
	<form name="exceldn" id="exceldn"  method="post" action="./excel/exceldn_0204.jsp">
		<input type="hidden" name="extitle" id="extitle" value="">
		<input type="hidden" name="casher" id="casher" value="">
		<input type="hidden" name="search" value="Y">
		<input type="hidden" name="uauth" value="<%=tuser%>">
		<!-- 2021.02.23 월일자별조회 -> 더블클릭 시 mid setting -->
		<input type="hidden" name="mid" id="mid">
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
				<td rowspan="3" align="right"><img src="./images/btn/btn_search.gif" onclick="search_go();" style="cursor:pointer;"/></td>
			</tr>
			<tr height="36">
				<td>
					<span class='schtitle'>단말기번호</span>
				</td>
				<td>
					<%=usertid %>
				</td>
				<td>
					<span class='schtitle'>사업부</span>
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
					<span class='schtitle'>가맹점번호</span>
				</td>
				<td>
					<input type='text' name='mid' id='mid'  class="searchbox_large" value="">
				</td>
				<td>
					<span class='schtitle'>취소상태</span>
				</td>
				<td>
					<input type="checkbox" name="can01" id="can01" checked onclick="can_chk(1);" value="Y">전체거래
					<input type="checkbox" name="can02" id="can02" onclick="can_chk(2);" value="Y">취소요청
					<input type="checkbox" name="can03" id="can03" onclick="can_chk(3);" value="Y">WEB취소거래
				</td>
			</tr>
			<tr height="36">
				<%-- <td>
					<span class='schtitle'>카드번호</span>
				</td>
				<td>
					<input type='text' name='cardno' id='cardno' class="searchbox_large" value="<?=$cardno?>">
				</td> --%>
				<td>
					<span class='schtitle'>결제구분 </span>
				</td>
				<td>
					<select id="cardtp" name="cardtp" style="width:165px; height:22px">
						<option value="">:: 전체조회 ::</option>
						<option value="01">카카오머니</option>
						<option value="02">카카오페이카드</option>
						<option value="03">제로페이</option>
						<option value="04">페이코</option>
					</select>
				</td>
				<!-- <td>
					<span class='schtitle'>거래상태</span>
				</td>
				<td>
					<input type="checkbox" name="tstat01" id="tstat01" checked onclick="tstat_check(1);" value="Y">전체거래
					<input type="checkbox" name="tstat02" id="tstat02" onclick="tstat_check(2);" value="Y">정상거래<br>
					<input type="checkbox" name="tstat03" id="tstat03" onclick="tstat_check(3);" value="Y">당일취소
					<input type="checkbox" name="tstat04" id="tstat04" onclick="tstat_check(4);" value="Y">전일취소
				</td> -->
				<td>
					<span class='schtitle'>승인구분</span>
				</td>
				<td>
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
				<td align="right" valign="absmiddle">
					<span class='button large icon'><input type="checkbox" name="rendchk" id="rendchk" checked><a href='#'>랜더링사용</a></span>
					<span class='button large icon' onclick='acc_exceldn();'><span class='excel'></span><a href='#'>엑셀다운로드</a></span>
				</td>
			</tr>
		</table>
	</div>
	<div id="parentId" style="position: relative; top: 0px; left: 0px; width: 100%; height:300px;"></div>
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
	var tfields		= "순번,사업부,승인건수,승인금액,취소건수,취소금액,총건수,합계(승인-취소)금액,비씨,농협,국민,삼성,하나,롯데,현대,신한";
    var taligns		= "center,left,right,right,right,right,right,right,right,right,right,right,right,right,right,right";
    var tcoltypes	= "ro,ro,ro,ro,ro,ro,ro,ro,ro,ro,ro,ro,ro,ro,ro,ro";
    var tsorts		= "str,str,int,int,int,int,int,int,int,int,int,int,int,int,int,int";
    var tcolwidth   = "60,260,*,*,*,*,*,*,150,*,*,*,*,*,*,*";
	var filterVal = "", filterIdx="";

	totalGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	totalGrid.setSkin("dhx_web");	
	totalGrid.setHeader(tfields);
	totalGrid.setInitWidths(tcolwidth);
	totalGrid.setColTypes(tcoltypes);
	totalGrid.setColAlign(taligns);
	totalGrid.setColSorting(tsorts);
	totalGrid.init();
	
	totalGrid.attachEvent("onXLE", function() {
		totalGrid.setRowTextStyle("total", "text-align:right;font-weight:bold;background-color:#f3fafe;color:#006699;border:1px solid #ffffff;");
		totalGrid.setCellTextStyle("total",0,"text-align:center;font-weight:bold;background-color:#f3fafe;color:#006699;border:1px solid #ffffff;");
		totalGrid.setColspan("total",0,2);
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
	accountGrid.attachFooter("<div id='nr_navi'>0</div>,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan,#cspan",["text-align:left;"]);
	
	//2021.02.22 승인번호도 넘겨줄 것.
	function doOnRowDblClicked(rowId, cellInd){
		var appno = accountGrid.cellById(rowId, 5).getValue();
		var seturl = "./ifou_xml_was/detail_0204.jsp?uauth=<%=tuser%>&seqno="+rowId+"&appno="+appno;

		$.getJSON(seturl, 
			{ 
				tags: "mount rainier", 
				tagmode: "any", 
				format: "json"
			}, // 서버가 필요한 정보를 같이 보냄. 
			function(data, status) { 
				if(data["RST"]=="S000"){
					var url = "./detail_view_van.jsp?seq="+rowId+"&appno="+appno+"&udata="+data["UDATA"];
					//detail_pop_view("detail_view_van.jsp?seq="+rowId+"&udata="+data["UDATA"], 1000, 600);
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
		var cardtp	= $('#cardtp').val();
		var auth01	= $('#auth01').val();
		var auth02	= $('#auth02').val();
		var auth03	= $('#auth03').val();
		var can01	= $('#can01').val();
		var can02	= $('#can02').val();
		var can03	= $('#can03').val();
		var mid	= $('#mid').val();
		var tid	= $('#tid  option:selected').val();
		var acqcd	= $('#acqcd  option:selected').val();
		var tid2 = $('#tid2 option:selected').val();

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
		if($("#auth01").prop("checked")){w	+= (auth01!="") ? "&auth01="+auth01 : "";}
		if($("#auth02").prop("checked")){w	+= (auth02!="") ? "&auth02="+auth02 : "";}
		if($("#auth03").prop("checked")){w	+= (auth03!="") ? "&auth03="+auth03 : "";}
		if($("#can01").prop("checked")){w	+= (can01!="") ? "&can01="+can01 : "";}
		if($("#can02").prop("checked")){w	+= (can02!="") ? "&can02="+can02 : "";}
		if($("#can03").prop("checked")){w	+= (can03!="") ? "&can03="+can03 : "";}
		//w	+= (cardno!="") ? "&cardno="+cardno : "";
		w	+= (mid!="") ? "&mid="+mid : "";
		w	+= (tid!="") ? "&tid="+tid : "";
		w	+= (acqcd!="") ? "&acqcd="+acqcd : "";
		w	+= (tid2!="") ? "&tid2="+tid2 : "";
		return w;
	}
	
	function exceldown(totalarray, itemarray){
		// POST 방식으로 서버에 HTTP Request를 보냄. 
		$.post("login_check.jsp", 
			{ totarray: totalarray, itemarray: itemarray }, // 서버가 필요한 정보를 같이 보냄. 
			function(data, status) { 
				window.location.replace("./sub_main.jsp");
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
	function can_chk(obj){
		if(obj==1){
			if($("#can01").prop("checked")){
				$("#can02").attr('checked', false);
				$("#can03").attr('checked', false);
			}
		}else{
			$("#can01").attr('checked', false);
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

		var st = srvTime();
		var today = new Date(st);
		var tmon   = leadingZeros((today.getMonth()+1), 2);
		var tday	= leadingZeros(today.getDate(), 2);
		var thour	= leadingZeros(today.getHours(), 2);
		var tminu	= leadingZeros(today.getMinutes(), 2);
		var tsecon	= leadingZeros(today.getSeconds(), 2);
		var todaysvr	= today.getFullYear()+'-'+tmon+'-'+tday;
		var timeset = thour+':'+tminu+':'+tsecon;

		$("#svrday").val(todaysvr);
		$("#svrtime").val(timeset);

		if($("#rendchk").prop("checked")){
			accountGrid.enableSmartRendering(true,200);
		}else{
			accountGrid.enableSmartRendering(false);
		}
		
		$.ajax({
			url: "./ifou_xml_was/sub0206page.jsp?"+wherequery(),
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
	
	function afterload_prog01(){
		AccLayout.items[0].progressOff();
	}
	
	function afterload_prog02(){
		AccLayout.items[1].progressOff();
	}
	
	function acc_exceldn(){
		$('#exceldn').submit();
	}
	
	function set_page(obj){
		$("#setpage").val(obj);

		var setn = $("#pagegroup").val();
		var exsrc	= "./ifou_xml_was/sub0206page.php?"+wherequery()+"&npage="+setn+"&pmode=";

		$('#subq').attr('src', exsrc);
	}

 	function page(obj){
		$('#nr_navi').html(obj);
		$("#nr_navi").css("min-height","40px").css("font-size","11pt").css("padding-top", "5px");
		AccLayout.items[0].progressOn();
		AccLayout.items[1].progressOn();
		
		totalGrid.clearAndLoad("./ifou_xml_was/total_0204_cvs.jsp?"+wherequery(), afterload_prog01, "json");	
		accountGrid.clearAndLoad("./ifou_xml_was/item_0204_cvs.jsp?"+wherequery(), afterload_prog02, "xml");
	}

	function set_navi(obj, np){
		$("#pagegroup").val(np);
		$('#nr_navi').html(obj);
		$("#nr_navi").css("min-height","40px").css("font-size","11pt").css("padding-top", "5px");
	}

	function move_navi(obj){
		var setn = $("#pagegroup").val();
		var exsrc	= "./ifou_xml_was/sub0206page.jsp?"+wherequery()+"&npage="+setn+"&pmode="+obj;
		$('#subq').attr('src', exsrc);
	}
	
</script>
<iframe name="subq" id="subq" style="width:100px; height:100px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>