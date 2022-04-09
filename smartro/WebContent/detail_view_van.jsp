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
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="org.json.simple.parser.JSONParser"%>
<%

	String udata =  request.getParameter("udata");

	Decoder decoder = Base64.getDecoder();

	byte[] udata_decode = decoder.decode(udata);
	String udata_json = new String(udata_decode, "UTF-8");
	
	JSONParser jsonParse = new JSONParser();
	JSONObject jsonObj = (JSONObject) jsonParse.parse(udata_json);
	JSONArray itemArray = (JSONArray) jsonObj.get("ITEMS");
	JSONObject itemObj = (JSONObject) itemArray.get(0);

	//2021.02.24 웹취소
	String seqno = itemObj.get("SEQNO").toString();
	String appno = itemObj.get("APPNO").toString();
	
	String tuser = (String)session.getAttribute("uinfo");
	byte[] byte_tuser = decoder.decode(tuser);
	tuser = new String(byte_tuser, "UTF-8");
	
%>
<!DOCTYPE html>
<html>
<head>
	<title>Init from script</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge">
	<link rel="stylesheet" type="text/css" href="./dhtmlx/codebase/dhtmlx.css"/>
	<link type="text/css" rel="stylesheet" href="./include/css/style.css"  media="all" >
	<link rel="stylesheet" type="text/css" href="./dhtmlx/dhtmlxTabbar/codebase/skins/dhtmlxtabbar_dhx_terrace.css"/>
	<script src="./dhtmlx/codebase/dhtmlx.js"></script>
	<script src="./include/js/jquery-1.8.1.min.js" type="text/javascript"></script>
	<script src="./include/js/common.js"></script>
	<script>
		var myTabbar;
		function doOnLoad() {
			
			var h = $(document).height();
			var w = $(document).width();
			/*2014 09 25 유병현추가*/
			$("#my_tabbar").css("min-height","400px").css("min-width","600px");
			/*2014 09 25 유병현추가*/

			var hset	= "400px";
			document.getElementById("my_tabbar").style.height = hset;

			myTabbar = new dhtmlXTabBar({
				parent: "my_tabbar",
				close_button: true,
				skin: 'dhx_terrace',
				height: hset
			});
			
			myTabbar.addTab("a1", "거래내역상세", null, null, true);
			myTabbar.addTab("a2", "전체거래내역", null, null, false);
			myTabbar.tabs("a1").attachURL('detail_view_sub01.jsp?purl=V&udata=<%=udata%>');
			myTabbar.tabs("a2").attachURL('detail_view_sub02.jsp?purl=V&udata=<%=udata%>');

		}
		$(window).resize(function(){
			var h = $(document).height();
			var w = $(document).width();
			/*2014 09 25 유병현추가*/
			$("#my_tabbar, .dhxtabbar_tabs, .dhx_cell_tabbar, .dhx_cell_tabbar > div  ").css("width","100%").css("min-width","600px");
			$(".dhxtabbar_tabs").css("border","0").css("border-top","1px solid #c0c0c0").css("border-bottom","1px solid #c0c0c0");
			$(".sub_menu_bar").css("min-width","600px");
			/*2014 09 25 유병현추가*/
			var hset	= "400px";
			document.getElementById("my_tabbar").style.height = hset;
		});
		function add(pos,tit,url) {
			var id = new Date().getTime();
			if (pos == -1){
				myTabbar.addTab(id, tit, null, pos);
				myTabbar.tabs(id).attachURL(url);
				myTabbar.tabs(id).setActive();
			}else{
				myTabbar.addTab(id, tit, "*", pos);
				myTabbar.setTabActive(id);
			}
		}

		$(document).ready(function() {
			$("#cnbtn").click(function(){

			if(confirm("취소 하시겠습니까?")) {
				document.f.submit();
			} else {
				return false;
			}


			});

			$("#prbtn").click(function(){
				document.prf.submit();
			});
		});

		function data_move(obj){
			myTabbar.tabs("a1").attachURL('detail_view_sub01.jsp?purl=V&uauth=<?=$uauth?>&SEQ='+obj);
			myTabbar.tabs("a1").setActive();
		}
		
	</script>
	<style>
		#grid_space{
			float:left;
			width:100%;
			height:5px;
		}

		.schtitle{font-size:9pt;}
	</style>

	<style>
		.tbox{width:100%; height:100%; border:2px solid #0099cc;}
		#pop_top_bg{width:685px; background-image:url('/images/popup/pop_box_mt.gif')}
		#pop_bot_bg{width:685px; background-image:url('/images/popup/pop_box_mb.gif')}
		#pop_mid_left{width:8px; background-image:url('/images/popup/pop_box_ml.gif')}
		#pop_mid_right{width:7px; background-image:url('/images/popup/pop_box_mr.gif')}
		#pop_title{margin:0; padding:0; width:685px; height:47px;}
		#space05{width:100%; height:5px;}
		#space10{width:100%; height:10px;}
		#mtitle{padding:5px; color:#336699; font-weight:bold;}
		#ctitle{padding:5px; padding-left:10px; color:#8c6dc0}
		#fblue{color:#336699;}
		#tdline02{height:2px; background-color:#aeb0ff}
		#tdline01{height:1px; background-color:#e4e6f8}
		#tdline_end{height:1px; background-color:#aeb0ff}
		.fcolor_red{color:#ff9999;}
		#cnbtn{cursor:pointer;color:#ff6600;}
		body { overflow:hidden } 
	</style>

</head>
<body onload="doOnLoad();">
<!-- web -> was -> ap -->
<form name="f" method="POST" action="./proc/transcancel_mst.jsp">
	<input type="hidden" name="idx" value="<%=seqno%>">
	<input type="hidden" name="appno" value="<%=appno%>">
	<input type="hidden" name="uauth" value="<%=tuser%>">
	<input type="hidden" name="purl" value="V">
</form>
<form name="prf" method="POST" action="detail_view_com.html">
	<input type="hidden" name="idx" value="">
	<input type="hidden" name="uauth" value="">
	<input type="hidden" name="purl" value="V">
</form>
<form name="tlf" method="POST" action="detail_view_tel.html">
	<input type="hidden" name="idx" value="">
	<input type="hidden" name="uauth" value="">
	<input type="hidden" name="purl" value="V">
</form>
<div id="pop_title"><img src="./images/popup/pop_title_01.gif"></div>
<div id="space10"></div>
<div id="my_tabbar" style="width:100%; margin-top:10px; z-index:10;"></div>
<iframe id="subq" style="width:0px; height:0px;"></iframe>
</body>
</html>
