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
	Decoder decoder = Base64.getDecoder();
	
	Date nowTime = new Date();
	SimpleDateFormat setDate = new SimpleDateFormat("yyyy-MM-dd");

	String reqstime = utilm.setDefault(request.getParameter("reqstime")).replaceAll("-", "");
	String reqetime = utilm.setDefault(request.getParameter("reqetime")).replaceAll("-", "");
	String stime = utilm.setDefault(request.getParameter("stime")).replaceAll("-", "");
	String etime = utilm.setDefault(request.getParameter("etime")).replaceAll("-", "");

	String tuser =  utilm.setDefault(request.getParameter("uauth"));
	byte[] byte_tuser = decoder.decode(tuser);
	tuser = new String(byte_tuser, "UTF-8");
	
	String rtncd = jbset.get_0311_excel(tuser, reqstime, reqetime, stime, etime);

	JSONParser rtnParser = new JSONParser();
	JSONObject rtnJson = (JSONObject)rtnParser.parse(rtncd);
	
	String totalArray = rtnJson.get("TOTALARRAY").toString();
	
	//total
	JSONParser totalParse = new JSONParser();
	JSONObject totalObj = (JSONObject) totalParse.parse(totalArray);
	JSONArray totalAry = (JSONArray)totalObj.get("rows");
		
	response.setHeader("Content-Type", "application/vnd.ms-xls");
	response.setHeader("Content-Disposition", "inline; filename=excel0310_"+setDate.format(nowTime)+".xls");
%>

<!DOCTYPE HTML>
<html>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
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
		<td class=extxt style='font-size:12pt; font-weight:bold; border-top:none;border-left:none' colspan="5">□ 청구이력조회</td>
	</TR>
</table>
<TABLE><TR><TD HEIGHT="10"></TD></TR></TABLE>
<table  style='border-collapse:collapse;table-layout:fixed;width:270pt'>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none;border-right:none;' COLSPAN="12">□ 집계자료</td>
	</TR>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt>순번</td>
		<td class=extxt>청구일자</td>
		<td class=extxt>사업자번호</td>
		<td class=extxt>매출일(FROM)</td>
		<td class=extxt>매출일(TO)</td>
		<td class=extxt>GS25합계건수</td>
		<td class=extxt>GS25합계금액</td>
		<td class=extxt>수퍼합계건수</td>
		<td class=extxt>수퍼합계금액</td>
		<td class=extxt>랄라블라합계건수</td>
		<td class=extxt>랄라블라합계금액</td>
		<td class=extxt>합계건수</td>
		<td class=extxt>합계금액</td>		
	</tr>
	<%
	for(int i = 0; i<totalAry.size(); i++){
		JSONObject totalObject = (JSONObject) totalAry.get(i);
		JSONArray totalData = (JSONArray) totalObject.get("data");
		if(totalData.get(0).equals("합계")){
	%>
		<tr style='mso-height-source:userset;height:15.95pt'>
			<td class='extxt' colspan="5" style="text-align:center;">합계</td>
			<td class=exnum><%=totalData.get(5).toString()%></td>
			<td class=exnum><%=totalData.get(6).toString()%></td>
			<td class=exnum><%=totalData.get(7).toString()%></td>
			<td class=exnum><%=totalData.get(8).toString()%></td>
			<td class=exnum><%=totalData.get(9).toString()%></td>
			<td class=exnum><%=totalData.get(10).toString()%></td>
			<td class=exnum><%=totalData.get(11).toString()%></td>
			<td class=exnum><%=totalData.get(12).toString()%></td>
		</tr>
	<% }else{%>
		<tr style='mso-height-source:userset;height:15.95pt'>
			<td class=extxt><%=totalData.get(0)%></td>
			<td class=extxt><%=totalData.get(1)%></td>
			<td class=extxt><%=totalData.get(2)%></td>
			<td class=exnum><%=totalData.get(3)%></td>
			<td class=exnum><%=totalData.get(4)%></td>
			<td class=exnum><%=totalData.get(5)%></td>
			<td class=exnum><%=totalData.get(6)%></td>
			<td class=exnum><%=totalData.get(7)%></td>
			<td class=exnum><%=totalData.get(8)%></td>
			<td class=exnum><%=totalData.get(9)%></td>
			<td class=exnum><%=totalData.get(10)%></td>
			<td class=exnum><%=totalData.get(11)%></td>
			<td class=exnum><%=totalData.get(12)%></td>
		</tr>
	<% }
	} %>
</table>

	