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
	<link rel="stylesheet" type="text/css" href="./dhtmlx/dhtmlxTabbar/codebase/skins/dhtmlxtabbar_dhx_terrace.css"/>
	<link type="text/css" rel="stylesheet" href="./include/css/style.css"  media="all" >
	<script src="./dhtmlx/codebase/dhtmlx.js"></script>
	<script src="./include/js/jquery-1.8.1.min.js" type="text/javascript"></script>
	<script src="./include/js/common.js"></script>
    <script src="./include/js/dhtmlxgrid_export.js"></script>
	<script>
		var myCalendar;
		var AccLayout2, orgTab;
		function doOnLoad() {
			
			var h = $(window).height();
			var w = $(window).width();
			$("#parentIdMain").css("min-height","680px").css("min-width","860px");
			$("#parentIdMain").css("height","690px");
			$(".dhx_cell_cont_layout").css("width","100%");
			AccLayout2.setSizes();
		}

		$(window).resize(function(){
			var h = $(window).height();
			var w = $(window).width();
			
			$(".dhx_cell_layout, .dhx_cell_cont_layout").css("width","100%").css("min-width","1160px");
			$(".dhx_cell_cont_layout").css("border","0").css("width","100%").css("border-top","1px solid #c0c0c0").css("border-bottom","1px solid #c0c0c0");
			$(".cont_title").css("margin","0 2px").css("min-width","550px");
			$("#parentIdMain").css("min-height","680px").css("min-width","860px");
			$("#parentIdMain").css("height","690px");
			$(".dhx_cell_cont_layout").css("width","100%");
			AccLayout2.setSizes();

		});
		/*2014 09 25 유병현추가*/
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
		.totaldata{text-align:right;font-weight:none;height:24px;background-color:#f0f0f0;padding-top:8px;}
		.schtitle{font-size:9pt;}
		.dhx_cell_hdr{border:0px solid #ffffff;}
		.dhx_cell_cont_layout{border:0px solid #ffffff;}
		.subtotal_grid{background-color:#f0f0f0; font-weight:bold; color:#000000; font-style: italic;}

		parentId {overflow:auto;}
	</style>
</head>
<body onload="doOnLoad();">
<div class='sub_content' id="sub_content">
	<div class='sub_content_space'></div>
	<div id="parentIdMain" style="position: relative; top: 0px; left: 0px; width: 100%; height:690px;"></div>
</div>
<script type="text/javascript">
	AccLayout2 = new dhtmlXLayoutObject("parentIdMain", "1C");
	AccLayout2.setSkin("dhx_terrace");	
	AccLayout2.cells("a").hideHeader();
	orgTab = AccLayout2.cells("a").attachTabbar();
	orgTab.setSkin("dhx_terrace");
	orgTab.addTab("a1", "원장관리",null, null, true);
	orgTab.addTab("a2", "사업부관리");
	orgTab.addTab("a3", "가맹점번호");
	orgTab.addTab("a4", "단말기관리");
	orgTab.addTab("a5", "사용자관리");
	
	orgTab.tabs("a1").attachURL("sub06_05_01.jsp");
	orgTab.tabs("a2").attachURL("sub06_05_02.jsp");
	orgTab.tabs("a3").attachURL("sub06_05_03.jsp");
	orgTab.tabs("a4").attachURL("sub06_05_04.jsp");
	orgTab.tabs("a5").attachURL("sub06_05_05.jsp");

	/*
	function reloadset(rowId){
		orgTab.tabs("a1").attachURL("sub06_05_01.jsp");
		orgTab.tabs("a2").attachURL("sub06_05_02.jsp");
		orgTab.tabs("a3").attachURL("sub06_05_03.jsp");
		orgTab.tabs("a4").attachURL("sub06_05_04.jsp");
		orgTab.tabs("a5").attachURL("sub06_05_05.jsp");
	}
	*/
</script>
<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>