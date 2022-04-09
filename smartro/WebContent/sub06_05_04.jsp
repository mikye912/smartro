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

	<link href="jeegoopopup/skins/basic/style.css" rel="Stylesheet" type="text/css" />
	<link href="jeegoopopup/skins/black/style.css" rel="Stylesheet" type="text/css" />
	<link href="jeegoopopup/skins/blue/style.css" rel="Stylesheet" type="text/css" />
	<link href="jeegoopopup/skins/clean/style.css" rel="Stylesheet" type="text/css" />
	<link href="jeegoopopup/skins/gray/style.css" rel="Stylesheet" type="text/css" />
	<link href="jeegoopopup/skins/round/style.css" rel="Stylesheet" type="text/css" />
	<script type="text/javascript" src="jeegoopopup/jquery-1.10.2.min.js"></script>
	<script type="text/javascript" src="jeegoopopup/jquery.jeegoopopup.1.0.0.js"></script>

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

			fixSize("b");
			
		}
		/*2014 09 25 유병현추가*/
		$(window).resize(function(){
			var h = $(document).height();
			var w = $(document).width();
			
			$(".dhx_cell_layout, .dhx_cell_cont_layout").css("width","100%").css("min-width","1160px");
			$(".dhx_cell_cont_layout").css("border","0").css("width","100%").css("border-top","1px solid #c0c0c0").css("border-bottom","1px solid #c0c0c0");
			$(".cont_title").css("margin","0 2px").css("min-width","550px");
			
			var hset	= (h-190) + "px";
			document.getElementById("parentId").style.height = hset;
		});
		/*2014 09 25 유병현추가*/
        var D = 0;
		function setSens(id, k) {
			myCalendar.setSensitiveRange(null,null);
		}

		function byId(id) {
			return document.getElementById(id);
		}

	function tid_mod(org, dep, tid){
		var popUrl = "./sub06_tid_mod.jsp?orgcd="+org+"&depcd="+dep+"&tid="+tid;
		var options = {
			width: 500,
			height: 400,
			center: true,
			fixed: true,
			skinClass: 'jg_popup_clean',
			overlay: 50,
			overlayColor: '#000',
			fadeIn: 0,
			url:popUrl
		};

		$.jeegoopopup.open(options);
	}
	function tid_del(org, dep, tid){
		var popUrl = "./sub06_tid_del.jsp?orgcd="+org+"&depcd="+dep+"&tid="+tid;
		var options = {
			width: 500,
			height: 400,
			center: true,
			fixed: true,
			skinClass: 'jg_popup_clean',
			overlay: 50,
			overlayColor: '#000',
			fadeIn: 0,
			url:popUrl
		};

		$.jeegoopopup.open(options);
	}

	function ipopup_close(){
		 $.jeegoopopup.close();
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
		.schtitle{font-size:9pt;}
		.dhx_cell_hdr{border:0px solid #ffffff;}
		.dhx_cell_cont_layout{border:0px solid #ffffff;}
		.subtotal_grid{background-color:#f0f0f0; font-weight:bold; color:#000000; font-style: italic;}
	</style>
</head>
<body onload="doOnLoad();">
<div class='sub_content' id="sub_content">
	<div class='sub_content_space'></div>
	<div class='cont_title'>
		<table width='100%' class='tb00_none'>
			<tr>
				<td width="500"><span class='schtitle'>■ 단말기정보</span></td>
				<td align="right"><span id="conttxt" style="javascript:void(0)" onclick="reloadset();"><img src="./images/refresh.png" align="absmiddle"> REFRASH</span></td>
			</tr>
		</table>
	</div>
	<div style="position: relative; top: 0px; left: 0px; width: 100%; height:10px;"></div>
	<div id="parentId" style="position: relative; top: 0px; left: 0px; width: 100%; height:610px;"></div>
</div>
<script type="text/javascript">
	AccLayout = new dhtmlXLayoutObject("parentId", "3U");
	AccLayout.setSkin("dhx_web");	
	AccLayout.cells("a").setText("단말기번호 원장");
	AccLayout.cells("b").setText("단말기번호 등록현황");
	AccLayout.cells("c").hideHeader();
	AccLayout.cells("c").setHeight(100);
	AccLayout.cells("b").setWidth(800);

	tidGrid = AccLayout.cells("a").attachGrid();
	
	var fields   = "사업부,단말기명,단말기번호,VAN사,단말기구분,수정,삭제,등록일";
    var aligns	 = "left,left,left,left,left,center,center,left";
    var colTypes  = "ro,ro,ro,ro,ro,ro,ro,ro";
	var colWidth   = "250,250,150,150,150,80,80,*";
    var sorts	 = "str,str,str,str,str,str,str,str";

	tidGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	tidGrid.setSkin("dhx_web");	
	tidGrid.setHeader(fields);
	tidGrid.setInitWidths(colWidth);
	tidGrid.setColTypes(colTypes);
	tidGrid.setColAlign(aligns);
	tidGrid.setColSorting(sorts);
	tidGrid.init();

	var fields2		= "사업부명,단말기명,단말기번호,VAN사,등록자,등록일";
    var aligns2		= "left,left,left,left,center,center";
    var colTypes2	= "ro,ro,ro,ro,ro,ro";
    var sorts2		= "str,str,str,str,str,str";
    var colWidth2   = "150,150,150,150,150,150";
	tidlistGrid = AccLayout.cells("b").attachGrid();
	tidlistGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	tidlistGrid.setSkin("dhx_web");	
	tidlistGrid.setHeader(fields2);
	tidlistGrid.setInitWidths(colWidth2);
	tidlistGrid.setColTypes(colTypes2);
	tidlistGrid.setColAlign(aligns2);
	tidlistGrid.setColSorting(sorts2);
	tidlistGrid.init();
	
	tidGrid.clearAndLoad("./ifou_xml_was/sub060504tidlist.jsp", "json");
	tidlistGrid.clearAndLoad("./ifou_xml_was/sub060504tidmap.jsp", "json");
	AccLayout.cells("c").attachURL("./sub060504cont.jsp");

	function reloadset(){
		tidGrid.clearAndLoad("./ifou_xml_was/sub060504tidlist.jsp", "json");
		tidlistGrid.clearAndLoad("./ifou_xml_was/sub060504tidmap.jsp", "json");
		AccLayout.cells("c").attachURL("./sub060504cont.jsp");
	}

	function fixSize(id) {
		AccLayout.cells("b").fixSize(false, true);
	}

</script>
<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>