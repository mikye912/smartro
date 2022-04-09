<%@ page language="java" contentType="application/vnd.ms-excel; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.net.*, java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="java.lang.String" %>
<%@ page import="java.security.*" %>
<%@ page import="java.util.Base64.Encoder" %>
<%@ page import="java.util.Base64.Decoder" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="org.json.simple.parser.JSONParser"%>
<%@ page import="java.text.SimpleDateFormat" %>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<jsp:useBean id="utilm" class="com.gaon.ifou.v3.trans_util_manager" scope="page" />

<%
	request.setCharacterEncoding("UTF-8");
	//base64 decoding
	Decoder decoder = Base64.getDecoder();

	String stime = utilm.setDefault(request.getParameter("stime")).replaceAll("-", "");
	String etime = utilm.setDefault(request.getParameter("etime")).replaceAll("-", "");
	String samt = utilm.setDefault(request.getParameter("samt"));
	String eamt = utilm.setDefault(request.getParameter("eamt"));
	String appno = utilm.setDefault(request.getParameter("appno"));
	String tradeidx = utilm.setDefault(request.getParameter("tradeidx"));
	
	String auth01 = utilm.setDefault(request.getParameter("auth01"));
	String auth02 = utilm.setDefault(request.getParameter("auth02"));
	String auth03 = utilm.setDefault(request.getParameter("auth03"));
	
	String mid = utilm.setDefault(request.getParameter("mid"));
	String tid = utilm.setDefault(request.getParameter("tid"));
	String acqcd = utilm.setDefault(request.getParameter("acqcd"));
	String tid2 = utilm.setDefault(request.getParameter("tid2"));
	
	String tuser =  utilm.setDefault(request.getParameter("uauth"));
	byte[] byte_tuser = decoder.decode(tuser);
	tuser = new String(byte_tuser, "UTF-8");

	String rtncd = jbset.get_excel_0106_cvs(tuser, stime, etime, samt, eamt, appno, tradeidx, auth01, auth02, auth03, mid, tid, acqcd, tid2);

	JSONParser rtnParser = new JSONParser();
	JSONObject rtnJson = (JSONObject)rtnParser.parse(rtncd);
	
	String totalArray = rtnJson.get("TOTALARRAY").toString();
	String itemArray = rtnJson.get("ITEMARRAY").toString();
	String fieldsTxt = rtnJson.get("FIELDS_TXT").toString();
	
	//total
	JSONParser totalParse = new JSONParser();
	JSONObject totalObj = (JSONObject) totalParse.parse(totalArray);
	JSONArray totalAry = (JSONArray)totalObj.get("rows");
	
	//detail
	JSONParser itemParse = new JSONParser();
	JSONObject itemObj = (JSONObject) itemParse.parse(itemArray);
	JSONArray itemAry = (JSONArray) itemObj.get("rows");
	
	//field
	String[] field_txt = fieldsTxt.split(",");
	
	//amount field 찾기
	int amtset = 0;
	for(int i = 0; i< field_txt.length; i++){
		if(field_txt[i].equals("금액")){
			amtset = i + 1;
		}
	}
	
	Date nowTime = new Date();
	SimpleDateFormat setDate = new SimpleDateFormat("yyyy-MM-dd");
	response.setHeader("Content-Type", "application/vnd.ms-xls");
	response.setHeader("Content-Disposition", "inline; filename=excel0106_cvs_"+setDate.format(nowTime)+".xls");
%>


<!DOCTYPE html>
<html>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge">
	<style>
	tr
		{mso-height-source:auto;
		mso-ruby-visibility:none;}
	col
		{mso-width-source:auto;
		mso-ruby-visibility:none;}
	br
		{mso-data-placement:same-cell;}
	ruby
		{ruby-align:left;}
	.style17
		{mso-number-format:"_-* \#\,\#\#0_-\;\\-* \#\,\#\#0_-\;_-* \0022-\0022_-\;_-\@_-";
		mso-style-name:"쉼표 \[0\]";
		mso-style-id:6;
		mso-background-source:auto;
		mso-rotate:0;
		mso-background-source:auto;
		mso-pattern:auto;
		mso-protection:locked visible;
		mso-style-name:표준;
		mso-style-id:0;
		}
	.style0
		{mso-number-format:General;
		text-align:general;
		vertical-align:middle;
		white-space:nowrap;
		mso-rotate:0;
		mso-background-source:auto;
		mso-pattern:auto;
		color:black;
		font-size:11.0pt;
		font-weight:400;
		font-style:normal;
		text-decoration:none;
		font-family:"맑은 고딕", monospace;
		mso-font-charset:129;
		border:none;
		mso-protection:locked visible;
		mso-style-name:표준;
		mso-style-id:0;}
	td
		{mso-style-parent:style0;
		padding-top:1px;
		padding-right:1px;
		padding-left:1px;
		mso-ignore:padding;
		color:black;
		font-size:11.0pt;
		font-weight:400;
		font-style:normal;
		text-decoration:none;
		font-family:"맑은 고딕", monospace;
		mso-font-charset:129;
		mso-number-format:General;
		text-align:general;
		vertical-align:middle;
		border:none;
		mso-background-source:auto;
		mso-pattern:auto;
		mso-protection:locked visible;
		white-space:nowrap;
		mso-rotate:0;}
	.exnum
		{mso-style-parent:style17;
		font-size:9.0pt;
		mso-number-format:"_-* \#\,\#\#0_-\;\\-* \#\,\#\#0_-\;_-* \0022-\0022_-\;_-\@_-";
		border:.5pt solid #538DD5;}
	.extxt
		{mso-style-parent:style0;
		font-size:9.0pt;
		mso-number-format:"\@";
		border:.5pt solid #538DD5;
		mso-pattern:black none;}
	</style>
</head>
<table  style='border-collapse:collapse;table-layout:fixed;width:270pt'>
	<tr style='mso-height-source:userset;height:20.95pt'>
		<td class=extxt style='font-size:12pt; font-weight:bold; border-top:none;border-left:none' colspan="7">□ 청구일자별조회</td>
	</TR>
</table>
<TABLE><TR><TD HEIGHT="10"></TD></TR></TABLE>
<table  style='border-collapse:collapse;table-layout:fixed;width:270pt'>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class='extxt' style='border-top:none;border-left:none;border-right:none;' COLSPAN="16">□ 집계자료</td>
	</TR>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none'>순번</td>
		<td class=extxt style='border-top:none;border-left:none'>사업부</td>
		<td class=extxt style='border-top:none;border-left:none'>승인건수</td>
		<td class=extxt style='border-top:none;border-left:none'>승인금액</td>
		<td class=extxt style='border-top:none;border-left:none'>취소건수</td>
		<td class=extxt style='border-top:none;border-left:none'>취소금액</td>
		<td class=extxt style='border-top:none;border-left:none'>총건수</td>
		<td class=extxt style='border-top:none;border-left:none'>합계(승인-취소)금액</td>
		<td class=extxt style='border-top:none;border-left:none'>비씨</td>
		<td class=extxt style='border-top:none;border-left:none'>농협</td>
		<td class=extxt style='border-top:none;border-left:none'>국민</td>
		<td class=extxt style='border-top:none;border-left:none'>삼성</td>
		<td class=extxt style='border-top:none;border-left:none'>하나</td>
		<td class=extxt style='border-top:none;border-left:none'>롯데</td>
		<td class=extxt style='border-top:none;border-left:none'>현대</td>
		<td class=extxt style='border-top:none;border-left:none'>신한</td>
	</tr>
	<%
			JSONObject totalObject = (JSONObject) totalAry.get(0);
			JSONArray totalData = (JSONArray)totalObject.get("data");
	%>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none' colspan="2" style="text-align:center;">합계</td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(2).toString()%></td> <%--승인건수--%>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(3).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(4).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(5).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(6).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(7).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(8).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(9).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(10).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(11).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(12).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(13).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(14).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(15).toString()%></td>
	</tr>
	
	<%
	totalObject = new JSONObject();
	totalData = new JSONArray();
	for(int i = 1; i<totalAry.size(); i++){
		totalObject = (JSONObject)totalAry.get(i);
		totalData = (JSONArray)totalObject.get("data");
	%>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none'><%=totalData.get(0).toString()%></td>
		<td class=extxt style='border-top:none;border-left:none'><%=totalData.get(1).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(2).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(3).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(4).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(5).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(6).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(7).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(8).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(9).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(10).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(11).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(12).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(13).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(14).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(15).toString()%></td>
	</tr>
	<% } %>
</table>

<TABLE><TR><TD HEIGHT="10"></TD></TR></TABLE>
<table  style='border-collapse:collapse;table-layout:fixed;width:270pt'>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none;border-right:none;' COLSPAN="<%=field_txt.length + 1%>">□ 상세자료</td>
	</TR>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none'>순번</td>
	<% 
		for(int i = 0; i < field_txt.length; i++){
	%>
		<td class=extxt style='border-top:none;border-left:none'><%=field_txt[i]%></td>
	<% } %>
	</TR>
	
	<%
	long total_amount = 0;
		for (int i = 0; i<itemAry.size(); i++){
			JSONObject itemObject = (JSONObject) itemAry.get(i);
			JSONArray itemData = (JSONArray) itemObject.get("data");
	%>
	<TR style='mso-height-source:userset;height:15.95pt'>
		<%
			
			for(int j = 0; j<field_txt.length+1; j++){
				if(j == amtset){
					//승인, 취소 구분
					String appgb = itemData.get(1).toString();
					if(appgb.equals("승인")){
						total_amount += Long.parseLong(itemData.get(j).toString());
					} else {
						total_amount -= Long.parseLong(itemData.get(j).toString());
					}	
		%>
		<td class=exnum><%=itemData.get(j).toString()%></td>
		<% } else { %>
		<td class=extxt><%=itemData.get(j).toString()%></td>
		<% 		} 
			}
		%>
	</TR>
	<%			
		}
	%>
	
	<TR style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt colspan="2" style="text-align:center;">합계</td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=exnum><%=total_amount%></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
	</TR>	
</table>