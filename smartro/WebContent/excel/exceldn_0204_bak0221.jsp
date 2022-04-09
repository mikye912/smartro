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

<%

	request.setCharacterEncoding("UTF-8");
	String totalArray = request.getParameter("totalarray");
	String itemArray = request.getParameter("itemarray");
	String fieldsTxt = request.getParameter("fieldstxt");
		
	Date nowTime = new Date();
	SimpleDateFormat setdate = new SimpleDateFormat("yyyy-MM-dd");	
	response.setHeader("Content-Type", "application/vnd.ms-xls");
	response.setHeader("Content-Disposition", "inline; filename=excel0204_"+setdate.format(nowTime)+".xls");
	
	//base64 decoding
	Decoder decoder = Base64.getDecoder();
	
	byte[] total_byte = decoder.decode(totalArray);
	byte[] item_byte = decoder.decode(itemArray);
	byte[] field_byte = decoder.decode(fieldsTxt);
	
	//ITEMS - [id, data]
	String total_json = new String(total_byte, "UTF-8");
	String item_json = new String(item_byte, "UTF-8");
	
	//total
	JSONParser totalParse = new JSONParser();
	JSONObject totalObj = (JSONObject) totalParse.parse(total_json);
	JSONArray totalAry = (JSONArray)totalObj.get("rows");
	
	//detail
	JSONParser itemParse = new JSONParser();
	JSONObject itemObj = (JSONObject) itemParse.parse(item_json);
	JSONArray itemAry = (JSONArray) itemObj.get("rows");
	
	//field
	String field_json = new String(field_byte, "UTF-8");
	String[] field_txt = field_json.split(",");
	
	//amount field 찾기
	int amtset = 0;
	for(int i = 0; i< field_txt.length; i++){
		if(field_txt[i].equals("금액")){
			amtset = i + 1;
		}
	}
	
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
<body>
<table  style='border-collapse:collapse;table-layout:fixed;width:270pt'>
	<tr style='mso-height-source:userset;height:20.95pt'>
		<td class=extxt style='font-size:12pt; font-weight:bold; border-top:none;border-left:none' colspan="10">□ 상세내역조회</td>
	</TR>
</table>
<TABLE><TR><TD HEIGHT="10"></TD></TR></TABLE>
<table  style='border-collapse:collapse;table-layout:fixed;width:270pt'>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none;border-right:none;' COLSPAN="16">□ 집계자료</td>
	</TR>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none'>순번</td>
		<td class=extxt style='border-top:none;border-left:none'>사업부</td>
		<td class=extxt style='border-top:none;border-left:none'>승인건수</td>
		<td class=extxt style='border-top:none;border-left:none'>승인금액</td>
		<td class=extxt style='border-top:none;border-left:none'>취소건수</td>
		<td class=extxt style='border-top:none;border-left:none'>취소금액</td>
		<td class=extxt style='border-top:none;border-left:none'>총건수</td>
		<td class=extxt style='border-top:none;border-left:none'>합계금액</td>
		<td class=extxt style='border-top:none;border-left:none'>비씨</td>
		<td class=extxt style='border-top:none;border-left:none'>국민</td>
		<td class=extxt style='border-top:none;border-left:none'>하나</td>
		<td class=extxt style='border-top:none;border-left:none'>삼성</td>
		<td class=extxt style='border-top:none;border-left:none'>신한</td>
		<td class=extxt style='border-top:none;border-left:none'>현대</td>
		<td class=extxt style='border-top:none;border-left:none'>롯데</td>
		<td class=extxt style='border-top:none;border-left:none'>농협</td>
	</tr>
	<%
		JSONObject totalObject = (JSONObject)totalAry.get(0);
		JSONArray totalData = (JSONArray)totalObject.get("data");
	%>

	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none' colspan="2">합계</td>
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
<%
	}	
%>
</table>

<TABLE>
<TR><TD></TD></TR>
</TABLE>

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
		for (int i = 0; i<itemAry.size(); i++){
			JSONObject itemObject = (JSONObject) itemAry.get(i);
			JSONArray itemData = (JSONArray) itemObject.get("data");
	%>
	<TR style='mso-height-source:userset;height:15.95pt'>
		<%
			for(int j = 0; j<field_txt.length+1; j++){
				if(j == amtset){
		%>
		<td class=exnum>
		<% } else { %>
		<td class=extxt>
		<%
			} 
		%>
			<%=itemData.get(j)%></td>
		<%
			}
		%>
	</TR>
	<%			
		}
	%>
	
	<TR style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt></td>
		<td class=extxt>합계</td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=exnum></td>
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
</body>	
</table>