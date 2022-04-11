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
	Decoder decoder = Base64.getDecoder();

	request.setCharacterEncoding("UTF-8");

	String stime = utilm.setDefault(request.getParameter("stime")).replaceAll("-", "");
	String etime = utilm.setDefault(request.getParameter("etime")).replaceAll("-", "");
	String appno = utilm.setDefault(request.getParameter("appno"));
	String cardno = utilm.setDefault(request.getParameter("cardno"));
	
	String tuser = utilm.setDefault(request.getParameter("uauth"));
	byte[] byte_tuser = decoder.decode(tuser);
	tuser = new String(byte_tuser, "UTF-8");
	
	String rtncd = jbset.get_0205_excel(tuser, stime, etime, cardno, appno);

	JSONParser rtnParser = new JSONParser();
	JSONObject rtnJson = (JSONObject) rtnParser.parse(rtncd);
	
	String totalArray = rtnJson.get("TOTALARRAY").toString();
	String itemArray = rtnJson.get("ITEMARRAY").toString();
	
	//total parse
	//object - object - id(string data), data (array)
	JSONParser totalParse = new JSONParser();
	JSONObject totalObj = (JSONObject) totalParse.parse(totalArray);
	JSONArray totalAry = (JSONArray) totalObj.get("rows");

	//item parse
	JSONParser itemParse = new JSONParser();
	JSONObject itemObj = (JSONObject) itemParse.parse(itemArray);
	JSONArray itemAry = (JSONArray) itemObj.get("rows");
	
	Date nowTime = new Date();
	SimpleDateFormat setDate = new SimpleDateFormat("yyyy-MM-dd");
	response.setHeader("Content-Type", "application/vnd.ms-xls");
	response.setHeader("Content-Disposition", "inline; filename=excel0205_"+setDate.format(nowTime)+".xls");

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
		<td class=extxt style='font-size:12pt; font-weight:bold; border-top:none;border-left:none' colspan="7">□ 반송사유조회</td>
	</TR>
</table>
<TABLE><TR><TD HEIGHT="10"></TD></TR></TABLE>
<table  style='border-collapse:collapse;table-layout:fixed;width:270pt'>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none;border-right:none;' COLSPAN="17">□ 집계내역</td>
	</TR>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none'>순번</td>
		<td class=extxt style='border-top:none;border-left:none'>매장코드</td>
		<td class=extxt style='border-top:none;border-left:none'>매장명</td>
		<td class=extxt style='border-top:none;border-left:none'>반송건수</td>
		<td class=extxt style='border-top:none;border-left:none'>반송금액</td>
		<td class=extxt style='border-top:none;border-left:none'>승인취소매출접수</td>
		<td class=extxt style='border-top:none;border-left:none'>당일승인/취소</td>
		<td class=extxt style='border-top:none;border-left:none'>원매출 승인취소</td>
		<td class=extxt style='border-top:none;border-left:none'>원매출없음</td>
		<td class=extxt style='border-top:none;border-left:none'>비씨</td>
		<td class=extxt style='border-top:none;border-left:none'>농협</td>
		<td class=extxt style='border-top:none;border-left:none'>국민</td>
		<td class=extxt style='border-top:none;border-left:none'>삼성</td>
		<td class=extxt style='border-top:none;border-left:none'>하나</td>
		<td class=extxt style='border-top:none;border-left:none'>롯데</td>
		<td class=extxt style='border-top:none;border-left:none'>현대</td>
		<td class=extxt style='border-top:none;border-left:none'>신한</td>
	</TR>
	<%
	for(int i = 0; i<totalAry.size(); i++){
		JSONObject totalObject = (JSONObject) totalAry.get(i);
		JSONArray totalDataAry = (JSONArray) totalObject.get("data");
	%>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none'><%=totalDataAry.get(0)%></td>	
		<td class=extxt style='border-top:none;border-left:none'><%=totalDataAry.get(1)%></td>
		<td class=extxt style='border-top:none;border-left:none'><%=totalDataAry.get(2)%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalDataAry.get(3)%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalDataAry.get(4)%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalDataAry.get(5)%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalDataAry.get(6)%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalDataAry.get(7)%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalDataAry.get(8)%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalDataAry.get(9)%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalDataAry.get(10)%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalDataAry.get(11)%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalDataAry.get(12)%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalDataAry.get(13)%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalDataAry.get(14)%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalDataAry.get(15)%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalDataAry.get(16)%></td>
	</TR>
	<%}%>
</TABLE>
<TABLE>
<TR><TD></TD></TR>
</TABLE>
<table  style='border-collapse:collapse;table-layout:fixed;width:270pt'>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none;border-right:none;' COLSPAN="17">□ 입금상세내역</td>
	</TR>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none'>순번</td>
		<td class=extxt style='border-top:none;border-left:none'>거래구분</td>
		<td class=extxt style='border-top:none;border-left:none'>메징코드</td>
		<td class=extxt style='border-top:none;border-left:none'>매장명</td>
		<td class=extxt style='border-top:none;border-left:none'>반송코드</td>
		<td class=extxt style='border-top:none;border-left:none'>반송사유</td>
		<td class=extxt style='border-top:none;border-left:none'>단말기번호</td>
		<td class=extxt style='border-top:none;border-left:none'>가맹점번호</td>
		<td class=extxt style='border-top:none;border-left:none'>카드번호</td>
		<td class=extxt style='border-top:none;border-left:none'>카드사명</td>
		<td class=extxt style='border-top:none;border-left:none'>금액</td>
		<td class=extxt style='border-top:none;border-left:none'>할부기간</td>
		<td class=extxt style='border-top:none;border-left:none'>승인일자</td>
		<td class=extxt style='border-top:none;border-left:none'>승인번호</td>
		<td class=extxt style='border-top:none;border-left:none'>요청일자</td>
		<td class=extxt style='border-top:none;border-left:none'>확장1</td>
		<td class=extxt style='border-top:none;border-left:none'>확장2</td>
	</tr>
<%
	for(int i = 0; i < itemAry.size(); i++){
		JSONObject itemObject = (JSONObject) itemAry.get(i);
		JSONArray itemDataAry = (JSONArray) itemObject.get("data");
%>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(0)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(1)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(2)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(3)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(4)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(5)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(6)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(7)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(8)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(9)%></td>
		<td class='exnum' style='border-top:none;border-left:none'><%=itemDataAry.get(10)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(11)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(12)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(13)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(14)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(15)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(16)%></td>
	</tr>
<% } %>
</table>



