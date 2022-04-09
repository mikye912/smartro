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
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<jsp:useBean id="utilm" class="com.gaon.ifou.v3.trans_util_manager" scope="page" />
<%
	String tuser = utilm.setDefault(request.getParameter("uauth"));

	String seqno = utilm.setDefault(request.getParameter("idx"));
	String appno = utilm.setDefault(request.getParameter("appno"));
	
	String recvData = jbset.transcancel_mst(seqno, tuser, appno);

	//전문 체크용 테스트 문구
	String authcd = "9999";
	String message = "연결오류";
%>

<!DOCTYPE html>
<html>
<head>
	<title>Init from script</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge">
	<link rel="stylesheet" type="text/css" href="./../dhtmlx/codebase/dhtmlx.css"/>
	<link type="text/css" rel="stylesheet" href="./../include/css/style.css"  media="all" >
	<link rel="stylesheet" type="text/css" href="./../dhtmlx/dhtmlxTabbar/codebase/skins/dhtmlxtabbar_dhx_terrace.css"/>
	<script src="./../dhtmlx/codebase/dhtmlx.js"></script>
	<script src="./../include/js/jquery-1.8.1.min.js" type="text/javascript"></script>
	<script src="./../include/js/common.js"></script>
</head>
<style>
	#grid_space{
		float:left;
		width:100%;
		height:5px;
	}
	.schtitle{font-size:9pt;}
</style>
<style>
	.tbox{width:100%; height:95%; border:2px solid #0099cc;}
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
<body style="margin:0px; padding:0px;">
<table width="100%" height="100%" class="tb01_gray">
	<tr>
		<td width="20%" style="background-color:#f0f0f0; text-weight:bold;">응답코드</td>		
		<td><%=authcd%></td>
	</tr>
	<tr>
		<td width="20%" style="background-color:#f0f0f0; text-weight:bold;">응답메세지</td>		
		<td><%=message%></td>
	</tr>
</table>
</body>