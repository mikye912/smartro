<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.*, java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="java.lang.String" %>
<%@ page import="java.security.*" %>
<%@ page import="java.util.Base64.Encoder" %>
<%@ page import="java.util.Base64.Decoder" %>
<%@ page import="java.util.Date" %>
<%@page import="org.json.simple.JSONObject"%>
<%@page import="org.json.simple.JSONArray"%>
<%@page import="org.json.simple.parser.JSONParser"%>
<%
	Decoder decoder = Base64.getDecoder();
	String udata =  request.getParameter("udata");
	
	String tuser = (String)session.getAttribute("uinfo");
	byte[] byte_tuser = decoder.decode(tuser);
	tuser = new String(byte_tuser, "UTF-8");
	
	String[] userexp = tuser.split(":");
	
	String purl = request.getParameter("purl");
	
	byte[] udata_decode = decoder.decode(udata);
	String udata_json = new String(udata_decode, "UTF-8");
	
	JSONParser jsonParse = new JSONParser();
	JSONObject jsonObj = (JSONObject) jsonParse.parse(udata_json);
	JSONArray itemArray = (JSONArray) jsonObj.get("ITEMS");
	JSONObject itemObj = (JSONObject) itemArray.get(0); 
	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>승인거래 상세정보</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf8">
	<link type="text/css" rel="stylesheet" href="./include/css/style.css">
	<script type="text/javascript" src="./include/js/jquery-1.9.1.js" ></script>
	<script type="text/javascript" src="./include/js/jquery.printArea.js" ></script>
	<script type="text/javascript">
	$(document).ready(function() {
		$("#cnbtn").click(function(){
			if(confirm("취소 하시겠습니까?")) {
				parent.document.f.submit();
			} else {
				return false;
			}
		});

		$("#prbtn").click(function(){
			parent.document.prf.submit();
		});
	});


	</script>
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
<body>
<table width="100%" class="tb00_none">
	<colgroup>
		<col width="100"></col>
		<col width="5"></col>
		<col width="240"></col>
		<col width="100"></col>
		<col width="5"></col>
		<col width="240"></col>
	</colgroup>
	<tr>
		<td colspan="5" id="mtitle">1.기초정보</td>
		<td align="right" style="padding-right:5px;"><!--a href="javascript:cancel_tran('<?=$idx?>','C');"-->
		<% if(userexp[7].equals("M"))
			{
		%>
			<% if(purl.equals("V"))
				{
			%>
		<span id='prbtn'>내역인쇄</span>&nbsp;&nbsp;
			<% } %>
		<span id='cnbtn'>거래취소</span>
		<% } %>
		</td>
	</tr>
	<tr>
		<td colspan="6" id="tdline02"></td>
	</tr>
	<tr>
		<td id="ctitle">거래일련번호</td>
		<td>:</td>
		<td><%=itemObj.get("TRANIDX")%></td>
		<td id="ctitle">환자(고객)번호</td>
		<td>:</td>
		<td><%=itemObj.get("ADD_CID")%></td>
	</tr>
	<!--tr><td colspan="6" id="tdline01"></td></tr>
	<tr>
		<td id="ctitle">고객번호</td>
		<td>:</td>
		<td><?=$GetD[0][MEDI_NO]?></td>
		<td id="ctitle">진료과</td>
		<td>:</td>
		<td><?=$GetD[0][MEDI_CD]?></td>
	</tr-->
	<tr><td colspan="6" id="tdline01"></td></tr>
	<tr>
		<td id="ctitle">거래요청일자</td>
		<td>:</td>
		<td><%=itemObj.get("APPDD")%></td>
		<td id="ctitle">거래요청시간</td>
		<td>:</td>
		<td><%=itemObj.get("APPTM")%></td>
	</tr>
	<tr><td colspan="6" id="tdline_end"></td></tr>
</table>
<div id="space10"></div>
<table width="100%" class="tb00_none">
	<colgroup>
		<col width="100"></col>
		<col width="5"></col>
		<col width="240"></col>
		<col width="100"></col>
		<col width="5"></col>
		<col width="240"></col>
	</colgroup>
	<tr>
		<td colspan="6" id="mtitle">2.거래정보</td>
	</tr>
	<tr>
		<td colspan="6" id="tdline02"></td>
	</tr>
	<tr>
		<td id="ctitle">거래종류</td>
		<td>:</td>
		<td><%=itemObj.get("APPGB")%></td>
		<td id="ctitle">인증방식</td>
		<td>:</td>
		<td></td>
	</tr>
	<tr><td colspan="6" id="tdline01"></td></tr>
	<tr>
		<td id="ctitle">카드번호</td>
		<td>:</td>
		<td><%=itemObj.get("CARDNO")%></td>
		<td id="ctitle">할부기간</td>
		<td>:</td>
		<td><%=itemObj.get("HALBU")%></td>
	</tr>
	<tr><td colspan="6" id="tdline01"></td></tr>
	<tr>
		<td id="ctitle">발급사</td>
		<td>:</td>
		<td><%=itemObj.get("ISS_NM")%></td>
		<td id="ctitle">매입사</td>
		<td>:</td>
		<td><%=itemObj.get("PUR_NM")%></td>
	</tr>
	<tr><td colspan="6" id="tdline01"></td></tr>		
	<% 
	String appgb = itemObj.get("APPGB").toString();
	if(appgb.equals("취소")){ %>
	<tr>
		<td id="ctitle">취소일자</td>
		<td>:</td>
		<td><%=itemObj.get("OAPPDD")%></td>
		<td id="ctitle">취소응답</td>
		<td>:</td>
		<% 
		String authcd = itemObj.get("AUTHCD").toString();
		if(authcd.equals("0000")) {%>
		<td>정상취소</td>
		<% } else { %>
		<td>취소오류(<span style="color:#ff9933;">오류코드:<%=itemObj.get("AUTHMSG")%></span>)</td>
		<% } %>
	</tr>
	<tr><td colspan="6" id="tdline01"></td></tr>
	<% } %>
	<tr>
		<td id="ctitle">거래요청금액</td>
		<td>:</td>
		<td><%=itemObj.get("AMOUNT")%>원</td>
		<td id="ctitle">승인번호</td>
		<td>:</td>
		<td><%=itemObj.get("APPNO")%></td>
	</tr>
	<tr><td colspan="6" id="tdline01"></td></tr>
	<tr>
		<td id="ctitle">응답메시지</td>
		<td>:</td>
		<td colspan="4"><%=itemObj.get("AUTHMSG")%></td>
	</tr>
	<tr><td colspan="6" id="tdline_end"></td></tr>
</table>
<div id="space10"></div>
<table width="100%" class="tb00_none">
	<tr>
		<td align="center"><img src="./images/btn/btn_close.gif" onclick="parent.parent.ipopup_close();" style="cursor:pointer;"></td>
	</tr>
</table>
<iframe id="subq" style="width:0px; height:0px;"></iframe>
</body>
</html>
