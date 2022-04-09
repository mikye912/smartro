<%@page language ="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.*, java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="java.lang.String" %>
<%@ page import="java.security.*" %>
<%@ page import="java.util.Base64.Encoder" %>
<%@ page import="java.util.Base64.Decoder" %>

<%
	Decoder decoder = Base64.getDecoder();

	String tuser = (String)session.getAttribute("uinfo");
	try{
		if(tuser==null || tuser.equals("")){                            // id가 Null 이거나 없을 경우
%>
<jsp:forward page="index.html"/>
<%
			response.sendRedirect("index.html");    // 로그인 페이지로 리다이렉트 한다.
			return;
		}
	}catch(Exception e){
%>
<jsp:forward page="index.html"/>
<%	
		response.sendRedirect("index.html");    // 로그인 페이지로 리다이렉트 한다.
		return;
	}

	String tmenu = (String)session.getAttribute("usermenu");
	if(tmenu==null || tmenu.equals("")){
%>
<jsp:forward page="index.html"/>
<%	
	response.sendRedirect("index.html");    // 로그인 페이지로 리다이렉트 한다.
	return;
}

	byte[] str_usermenu = decoder.decode(tmenu);
	String StrOUT = new String(str_usermenu, "UTF-8");
	
	
	byte[] byte_tuser = decoder.decode(tuser);
	tuser = new String(byte_tuser, "UTF-8");
	
	String[] expd = tuser.split(":");
	
	String setTitle = "<span style='color:#999933; font-weight:bold;'>" + expd[0] + "</span>님 반갑습니다.";

%>
<!DOCTYPE html>
<html>
<head>
	<title>IFOU 행복정산 서비스</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge">
	<link rel="stylesheet" type="text/css" href="./dhtmlx/codebase/dhtmlx.css"/>
	<link type="text/css" rel="stylesheet" href="./include/css/style.css"  media="all" >
	<link rel="stylesheet" type="text/css" href="./dhtmlx/dhtmlxTabbar/codebase/skins/dhtmlxtabbar_dhx_terrace.css"/>
	<script src="./dhtmlx/codebase/dhtmlx.js"></script>
	<script src="./include/js/jquery-1.8.1.min.js" type="text/javascript"></script>
	<script src="./include/js/jquery.base64.js" type="text/javascript"></script>
	<script src="./include/js/common.js"></script>
	<script>
		var myTabbar;
		function doOnLoad() {
			
			var h = $(window).height();
			var w = $(window).width();
			/*2014 09 25 유병현추가*/
			$("#my_tabbar").css("min-height","750px").css("min-width","860px");
			/*2014 09 25 유병현추가*/

			var hset	= (h-160) + "px";
			document.getElementById("my_tabbar").style.height = hset;

			myTabbar = new dhtmlXTabBar({
				parent: "my_tabbar",
				close_button: true,
				skin: 'dhx_terrace',
				height: hset
			});
			
			myTabbar.addTab("a1", "종합정보", null, null, true);
			myTabbar.tabs("a1").attachURL('sub_total.jsp');
		}

		$(window).resize(function(){
			var h = $(window).height();
			var w = $(window).width();
			/*2014 09 25 유병현추가*/
			$("#my_tabbar, .dhxtabbar_tabs, .dhx_cell_tabbar, .dhx_cell_tabbar > div  ").css("width","100%").css("min-width","1024px");
			$(".dhxtabbar_tabs").css("border","0").css("border-top","1px solid #c0c0c0").css("border-bottom","1px solid #c0c0c0");
			$(".sub_menu_bar").css("min-width","860px");
			/*2014 09 25 유병현추가*/
			var hset	= (h-160) + "px";

			document.getElementById("my_tabbar").style.height = hset;

			myTabbar.setSizes();
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

		function urlgo(obj1, obj2, obj3){
			//var url		= "?seltp="+seltp+"&stime="+stime+"&etime="+etime+"&card_no="+cardno+"&app_no="+app_no;
			var gourl = obj3;
			add(obj1, obj2, gourl);
		}

		function urlgoDirect(obj1, obj2, obj3){
			add(obj1, obj2, obj3);
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
</head>
<body onload="doOnLoad();">

<div id='sub_head'>
	<div class="sub_top">
		<div class="sub_logo">
			<a href="sub_main.jsp">
				<img src='./images/logo.png'>
			</a>
		</div>
		<div class="sub_welcome"><div style="padding-top:25px;"><%=setTitle%></div></div>
	</div>
	<div class="sub_menu_bar">
		<table width="100%" class="tb00_none">
			<tr>
				<td width="*">
				<ul class="topnav" id="topnav">
					<%=StrOUT%>
				</ul>
				</td>
				<td width="200" class="menu_bar_right" align="right"><a href="sub_main.jsp"><span style="color:#ffffff;">메인페이지</span></a>&nbsp;&nbsp;&nbsp;<a href="logout.jsp"><span style="color:#ffffff;">로그아웃</span></a>&nbsp;&nbsp;</td>
			</tr>
		</table>
	</div>
</div>
<div id="my_tabbar" style="width:100%; margin-top:10px; z-index:10;"></div>
<script>
var seturl = "./ifou_xml_was/menu_get.jsp?orgcd=<%=expd[1]%>&mseq=<%=expd[8]%>";
$.get(seturl, 
	{ 
		tags: "mount rainier", 
		tagmode: "any", 
		format: "json"
	}, // 서버가 필요한 정보를 같이 보냄. 
	function(data, status) { 
		$.each(data, function(key,val) { 
			alert(data["UDATA"]);
			//$("#topnav").html(data["UDATA"]);
		});
	} 
);
</script>