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
	byte[] byte_tuser = decoder.decode(tuser);
	tuser = new String(byte_tuser, "UTF-8");
	
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
	
	//2021.03.04 강원대병원 - 시스템관리
	String[] org_data = jbset.get_060501_item(tuser);
%>

<!DOCTYPE html>
<html>
<head>
	<title>Init from script</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" type="text/css" href="./dhtmlx/codebase/dhtmlx.css"/>
	<link rel="stylesheet" type="text/css" href="./dhtmlx/dhtmlxGrid/codebase/skins/dhtmlxgrid_dhx_web.css"/>
	<link rel="stylesheet" type="text/css" href="./dhtmlx/dhtmlxTabbar/codebase/skins/dhtmlxtabbar_dhx_terrace.css"/>
	<link type="text/css" rel="stylesheet" href="./include/css/style.css"  media="all" >
	<script src="./dhtmlx/codebase/dhtmlx.js"></script>
	<script src="./include/js/jquery-1.8.1.min.js" type="text/javascript"></script>
	<script src="./include/js/common.js"></script>
    <script src="./include/js/dhtmlxgrid_export.js"></script>
	<script>
		var myCalendar;
		var AccLayout, orgTab;
		function doOnLoad() {
			
			var h = $(window).height();
			var w = $(window).width();

			/*2014 09 25 유병현추가*/
			$("#orgInfo").css("min-height","150px").css("min-width","860px");
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
			// init values
			var t = new Date();
			
		}
		
		function setSens(id, k) {
			myCalendar.setSensitiveRange(null,null);
		}

		function byId(id) {
			return document.getElementById(id);
		}
	</script>
	<style>
		input#stime, input#etime {
			font-size: 9pt;
			background-color: #fafafa;
			border: #c0c0c0 1px solid;
			width: 100px;
		}
		span.label {
			font-family: Tahoma;
			font-size: 12px;
		}
		.hdrcell{text-align:center;font-weight:bold;}
		.totaldata{text-align:right;font-weight:none;height:24px;background-color:#f0f0f0;padding-top:8px;}
		.schtitle{font-size:9pt;font-weight:bold;}
		.dhx_cell_hdr{border:0px solid #ffffff;}
		.dhx_cell_cont_layout{border:0px solid #ffffff;}
		.subtotal_grid{background-color:#f0f0f0; font-weight:bold; color:#000000; font-style: italic;}
	</style>
	<script>
		function regi_form(){
			if(confirm("정말 수정하시겠습니까?")){
				var f = document.orgreg;
				f.submit();
			}	
		}
	</script>
</head>
<body onload="doOnLoad();">
<div class='sub_content' id="sub_content">
	<div class='sub_content_space'></div>
	<div class='cont_title'>
		<table width='100%' class='tb00_none'>
			<tr>
				<td width="500"><span class='schtitle'>&nbsp;&nbsp;■ 사업자정보</span></td>
				<td align="right"><span class="button medium icon" onclick="regi_form();"><span class="add"></span><a href="#">정보수정</a></span></td>
			</tr>
		</table>
	</div>
<form name="orgreg" method="post" action="./proc/sub060501proc.jsp">
<input type="hidden" name="orgcd" value="">
	<div style="position: relative; top: 0px; left: 0px; width: 100%; height:10px;"></div>
	<div style="position: relative; top: 0px; left: 0px; width: 100%; height:160px;">

		<table width='100%' class='tb01_gray'>
			<colgroup>
				<col width="120"></col>
				<col width="*"></col>
				<col width="120"></col>
				<col width="*"></col>
			</colgroup>
			<tr>
				<td><span class='schtitle'>법인명</span></td>
				<td><input type="text" name="comnm" value="<%=org_data[0]%>"></td>
				<td><span class='schtitle'>사업자번호</span></td>
				<td><input type="text" name="comno" value="<%=org_data[1]%>"></td>
			</tr>
			<tr>
				<td><span class='schtitle'>법인번호</span></td>
				<td><input type="text" name="comexno" value="<%=org_data[2]%>"></td>
				<td><span class='schtitle'>대표자명</span></td>
				<td><input type="text" name="comceo" value="<%=org_data[3]%>"></td>
			</tr>
			<tr>
				<td><span class='schtitle'>업태</span></td>
				<td><input type="text" name="cometype" value="<%=org_data[4]%>"></td>
				<td><span class='schtitle'>종목</span></td>
				<td><input type="text" name="comservice" value="<%=org_data[5]%>"></td>
			</tr>
			<tr>
				<td><span class='schtitle'>대표전화</span></td>
				<td><input type="text" name="comtel" value="<%=org_data[6]%>"></td>
				<td><span class='schtitle'>주소</span></td>
				<td><input type="text" name="comaddr" value="<%=org_data[7]%>" style="width:400px;"></td>
			</tr>
		</table>

	</div>
	<div style="position: relative; top: 0px; left: 0px; width: 100%; height:20px;">
		<table width='100%' class='tb00_none'>
			<tr>
				<td width="500"><span class='schtitle'>&nbsp;&nbsp;■ 사용자 정보</span></td>
				<td align="right"></td>
			</tr>
		</table>
	</div>
	<div id="parentId" style="position: relative; top: 0px; left: 0px; width: 100%; height:250px;"></div>
	<div style="position: relative; top: 0px; left: 0px; width: 100%; height:10px;"></div>
	<div style="position: relative; top: 0px; left: 0px; width: 100%; height:20px;">
		<table width='100%' class='tb00_none'>
			<tr>
				<td width="500"><span class='schtitle'>&nbsp;&nbsp;■ 사이트 메모</span></td>
				<td></td>
			</tr>
		</table>
	</div>
	<div style="position: relative; top: 0px; left: 0px; width: 100%; height:100px;">
		<textarea name="orgmemo" style="width:100%; height:120px; border:1px solid #c0c0c0c0; font-size:9pt;"><%=org_data[8]%></textarea>
	</div>
</form>
</div>
<script type="text/javascript">
	 AccLayout = new dhtmlXLayoutObject("parentId", "1C");
	AccLayout.setSkin("dhx_web");	
	AccLayout.cells("a").hideHeader();

	accountGrid = AccLayout.cells("a").attachGrid();
	
	var fields   = "아이디,그룹,담당자명,소속,전화번호";
		fields	+= ",헨드폰번호,이메일";
	
	var filters	 = "#text_filter,#text_filter,#text_filter,#select_filter,#text_filter";
		filters += ",#text_filter,#text_filter";
	
    var aligns	 = "left,left,left,left,left";
		aligns	+= ",left,left";
	
    var colTypes  = "ro,ro,ro,ro,ro";
        colTypes += ",ro,ro";
	
    var sorts	 = "str,str,str,str,str";
        sorts   += ",str,str";
    
    var colWidth   = "150,150,150,150,*";
        colWidth  += ",*,*";

	accountGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	accountGrid.setSkin("dhx_web");	
	accountGrid.setHeader(fields);
	accountGrid.setInitWidths(colWidth);
	accountGrid.attachHeader(filters);
	accountGrid.setColTypes(colTypes);
	accountGrid.setColAlign(aligns);
	accountGrid.setColSorting(sorts);
	accountGrid.init();
	accountGrid.clearAndLoad("./ifou_xml_was/sub06_05userxml.jsp", "json");

</script>
<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>