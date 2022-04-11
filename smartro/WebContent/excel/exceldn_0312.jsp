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

	String stime = utilm.setDefault(request.getParameter("stime")).replaceAll("-", "");
	String etime = utilm.setDefault(request.getParameter("etime")).replaceAll("-", "");
	String reqstime = utilm.setDefault(request.getParameter("reqstime")).replaceAll("-", "");
	String reqetime = utilm.setDefault(request.getParameter("reqetime")).replaceAll("-", "");
	String tid = utilm.setDefault(request.getParameter("tid"));

	String tuser =  utilm.setDefault(request.getParameter("uauth"));
	byte[] byte_tuser = decoder.decode(tuser);
	tuser = new String(byte_tuser, "UTF-8");
	
	String rtncd = jbset.get_0312_excel(tuser, stime, etime, reqstime, reqetime, tid);

	JSONParser rtnParser = new JSONParser();
	JSONObject rtnJson = (JSONObject)rtnParser.parse(rtncd);
	
	String totalArray = rtnJson.get("TOTALARRAY").toString();
	
	//total
	JSONParser totalParse = new JSONParser();
	JSONObject totalObj = (JSONObject) totalParse.parse(totalArray);
	JSONArray totalAry = (JSONArray)totalObj.get("rows");
		
	response.setHeader("Content-Type", "application/vnd.ms-xls");
	response.setHeader("Content-Disposition", "inline; filename=excel0312_"+setDate.format(nowTime)+".xls");
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
		<td class=extxt style='font-size:12pt; font-weight:bold; border-top:none;border-left:none' colspan="5">□ 청구승인일자기준</td>
	</TR>
</table>
<TABLE><TR><TD HEIGHT="10"></TD></TR></TABLE>
<table  style='border-collapse:collapse;table-layout:fixed;width:270pt'>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none;border-right:none;' COLSPAN="34">□ 집계자료</td>
	</TR>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt>순번</td>
		<td class=extxt>승인일자</td>
		<td class=extxt>국민승인건수</td>
		<td class=extxt>국민승인금액</td>
		<td class=extxt>국민취소건수</td>
		<td class=extxt>국민취소금액</td>
		<td class=extxt>농협승인건수</td>
		<td class=extxt>농협승인금액</td>
		<td class=extxt>농협취소건수</td>
		<td class=extxt>농협취소금액</td>
		<td class=extxt>롯데승인건수</td>
		<td class=extxt>롯데승인금액</td>
		<td class=extxt>롯데취소건수</td>
		<td class=extxt>롯데취소금액</td>
		<td class=extxt>비씨승인건수</td>
		<td class=extxt>비씨승인금액</td>
		<td class=extxt>비씨취소건수</td>
		<td class=extxt>비씨취소금액</td>
		<td class=extxt>삼성승인건수</td>
		<td class=extxt>삼성승인금액</td>
		<td class=extxt>삼성취소건수</td>
		<td class=extxt>삼성취소금액</td>
		<td class=extxt>신한승인건수</td>
		<td class=extxt>신한승인금액</td>
		<td class=extxt>신한취소건수</td>
		<td class=extxt>신한취소금액</td>
		<td class=extxt>하나승인건수</td>
		<td class=extxt>하나승인금액</td>
		<td class=extxt>하나취소건수</td>
		<td class=extxt>하나취소금액</td>
		<td class=extxt>현대승인건수</td>
		<td class=extxt>현대승인금액</td>
		<td class=extxt>현대취소건수</td>
		<td class=extxt>현대취소금액</td>
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
			<td class='extxt' colspan="2" style="text-align:center;">합계</td>
			<td class=exnum><%=totalData.get(2).toString()%></td>
			<td class=exnum><%=totalData.get(3).toString()%></td>
			<td class=exnum><%=totalData.get(4).toString()%></td>
			<td class=exnum><%=totalData.get(5).toString()%></td>
			<td class=exnum><%=totalData.get(6).toString()%></td>
			<td class=exnum><%=totalData.get(7).toString()%></td>
			<td class=exnum><%=totalData.get(8).toString()%></td>
			<td class=exnum><%=totalData.get(9).toString()%></td>
			<td class=exnum><%=totalData.get(10).toString()%></td>
			<td class=exnum><%=totalData.get(11).toString()%></td>
			<td class=exnum><%=totalData.get(12).toString()%></td>
			<td class=exnum><%=totalData.get(13).toString()%></td>
			<td class=exnum><%=totalData.get(14).toString()%></td>
			<td class=exnum><%=totalData.get(15).toString()%></td>
			<td class=exnum><%=totalData.get(16).toString()%></td>
			<td class=exnum><%=totalData.get(17).toString()%></td>
			<td class=exnum><%=totalData.get(18).toString()%></td>
			<td class=exnum><%=totalData.get(19).toString()%></td>
			<td class=exnum><%=totalData.get(20).toString()%></td>
			<td class=exnum><%=totalData.get(21).toString()%></td>
			<td class=exnum><%=totalData.get(22).toString()%></td>
			<td class=exnum><%=totalData.get(23).toString()%></td>
			<td class=exnum><%=totalData.get(24).toString()%></td>
			<td class=exnum><%=totalData.get(25).toString()%></td>
			<td class=exnum><%=totalData.get(26).toString()%></td>
			<td class=exnum><%=totalData.get(27).toString()%></td>
			<td class=exnum><%=totalData.get(28).toString()%></td>
			<td class=exnum><%=totalData.get(29).toString()%></td>
			<td class=exnum><%=totalData.get(30).toString()%></td>
			<td class=exnum><%=totalData.get(31).toString()%></td>
			<td class=exnum><%=totalData.get(32).toString()%></td>
			<td class=exnum><%=totalData.get(33).toString()%></td>
			<td class=exnum><%=totalData.get(34).toString()%></td>
			<td class=exnum><%=totalData.get(35).toString()%></td>
		</tr>
	<% }else{%>
		<tr style='mso-height-source:userset;height:15.95pt'>
			<td class=extxt><%=totalData.get(0)%></td>
			<td class=extxt><%=totalData.get(1)%></td>
			<td class=exnum><%=totalData.get(2)%></td>
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
			<td class=exnum><%=totalData.get(13)%></td>
			<td class=exnum><%=totalData.get(14)%></td>
			<td class=exnum><%=totalData.get(15)%></td>
			<td class=exnum><%=totalData.get(16)%></td>
			<td class=exnum><%=totalData.get(17)%></td>
			<td class=exnum><%=totalData.get(18)%></td>
			<td class=exnum><%=totalData.get(19)%></td>
			<td class=exnum><%=totalData.get(20)%></td>
			<td class=exnum><%=totalData.get(21)%></td>
			<td class=exnum><%=totalData.get(22)%></td>
			<td class=exnum><%=totalData.get(23)%></td>
			<td class=exnum><%=totalData.get(24)%></td>
			<td class=exnum><%=totalData.get(25)%></td>
			<td class=exnum><%=totalData.get(26)%></td>
			<td class=exnum><%=totalData.get(27)%></td>
			<td class=exnum><%=totalData.get(28)%></td>
			<td class=exnum><%=totalData.get(29)%></td>
			<td class=exnum><%=totalData.get(30)%></td>
			<td class=exnum><%=totalData.get(31)%></td>
			<td class=exnum><%=totalData.get(32)%></td>
			<td class=exnum><%=totalData.get(33)%></td>
			<td class=exnum><%=totalData.get(34)%></td>
			<td class=exnum><%=totalData.get(35)%></td>
		</tr>
	<% }
	} %>
</table>

	