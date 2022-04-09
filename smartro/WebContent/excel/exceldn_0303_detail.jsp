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

	String stime = utilm.setDefault(request.getParameter("stime")).replaceAll("-", "");
	String etime = utilm.setDefault(request.getParameter("etime")).replaceAll("-", "");
	String sreqdd = utilm.setDefault(request.getParameter("sreqdd")).replaceAll("-", "");
	String ereqdd = utilm.setDefault(request.getParameter("ereqdd")).replaceAll("-", "");
	String sexpdd = utilm.setDefault(request.getParameter("sexpdd")).replaceAll("-", "");
	String eexpdd = utilm.setDefault(request.getParameter("eexpdd")).replaceAll("-", "");
	
	String depcd = utilm.setDefault(request.getParameter("depcd"));
	String tid = utilm.setDefault(request.getParameter("tid"));
	String mid = utilm.setDefault(request.getParameter("mid"));
	String appno = utilm.setDefault(request.getParameter("appno"));
	String acqcd = utilm.setDefault(request.getParameter("acqcd"));
	
	String tuser =  utilm.setDefault(request.getParameter("uauth"));
	byte[] byte_tuser = decoder.decode(tuser);
	tuser = new String(byte_tuser, "UTF-8");

	String rtn_json = jbset.get_0303detail_excel(tuser, stime, etime, sreqdd, ereqdd, sexpdd, eexpdd, appno, tid, mid, acqcd, depcd);
	
	JSONParser rtnParser = new JSONParser();
	JSONObject rtnJson = (JSONObject) rtnParser.parse(rtn_json);
	
	String totalArray = rtnJson.get("TOTALARRAY").toString();
	String itemArray = rtnJson.get("ITEMARRAY").toString();
	
	//total parse
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
	response.setHeader("Content-Disposition", "inline; filename=excel0303_detail_"+setDate.format(nowTime)+".xls");
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
		<td class=extxt style='font-size:12pt; font-weight:bold; border-top:none;border-left:none' colspan="7">□ 거래일자상세내역</td>
	</TR>
</table>
<TABLE><TR><TD HEIGHT="10"></TD></TR></TABLE>
<table  style='border-collapse:collapse;table-layout:fixed;width:270pt'>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none;border-right:none;' COLSPAN="11">□ 집계내역</td>
	</TR>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none'>순번</td>
		<td class=extxt style='border-top:none;border-left:none'>단말기명</td>
		<td class=extxt style='border-top:none;border-left:none'>단말기번호</td>
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
		for (int i = 0; i<totalAry.size(); i++){
			JSONObject totalObject = (JSONObject) totalAry.get(i);
			JSONArray totalData = (JSONArray) totalObject.get("data");
			if(totalData.get(0).equals("합계")){
	%>
			<tr style='mso-height-source:userset;height:15.95pt'>
				<td class=extxt style='border-top:none;border-left:none;text-align:center;' colspan="3"><%=totalData.get(0)%></td>	
				<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(3)%></td>
				<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(4)%></td>
				<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(5)%></td>
				<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(6)%></td>
				<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(7)%></td>
				<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(8)%></td>
				<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(9)%></td>
				<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(10)%></td>
			</TR>
	<%			
			}else{
				%>
				<tr style='mso-height-source:userset;height:15.95pt'>
					<td class=extxt style='border-top:none;border-left:none'><%=totalData.get(0)%></td>	
					<td class=extxt style='border-top:none;border-left:none'><%=totalData.get(1)%></td>
					<td class=extxt style='border-top:none;border-left:none'><%=totalData.get(2)%></td>
					<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(3)%></td>
					<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(4)%></td>
					<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(5)%></td>
					<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(6)%></td>
					<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(7)%></td>
					<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(8)%></td>
					<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(9)%></td>
					<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(10)%></td>
				</TR>
	<% } 
	}%>
</table>
<TABLE>
<TR><TD></TD></TR>
</TABLE>
<table  style='border-collapse:collapse;table-layout:fixed;width:270pt'>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none;border-right:none;' COLSPAN="22">□ 상세내역</td>
	</TR>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none'>순번</td>
		<td class=extxt style='border-top:none;border-left:none'>사업부</td>
		<td class=extxt style='border-top:none;border-left:none'>단말기명</td>
		<td class=extxt style='border-top:none;border-left:none'>단말기번호</td>
		<td class=extxt style='border-top:none;border-left:none'>카드사</td>
		<td class=extxt style='border-top:none;border-left:none'>가맹점번호</td>
		<td class=extxt style='border-top:none;border-left:none'>매입구분</td>
		<td class=extxt style='border-top:none;border-left:none'>승인구분</td>
		<td class=extxt style='border-top:none;border-left:none'>카드번호</td>
		<td class=extxt style='border-top:none;border-left:none'>금액</td>
		<td class=extxt style='border-top:none;border-left:none'>할부기간</td>
		<td class=extxt style='border-top:none;border-left:none'>승인번호</td>
		<td class=extxt style='border-top:none;border-left:none'>승인일자</td>
		<td class=extxt style='border-top:none;border-left:none'>승인시간</td>
		<td class=extxt style='border-top:none;border-left:none'>원승인일자</td>
		<td class=extxt style='border-top:none;border-left:none'>예정수수료</td>
		<td class=extxt style='border-top:none;border-left:none'>입금예정액</td>
		<td class=extxt style='border-top:none;border-left:none'>청구일자</td>
		<td class=extxt style='border-top:none;border-left:none'>응답일자</td>
		<td class=extxt style='border-top:none;border-left:none'>매입결과</td>
		<td class=extxt style='border-top:none;border-left:none'>입금예정일</td>
		<td class=extxt style='border-top:none;border-left:none'>입반내역</td>
	</tr>
	<%
		//합계
		long total_amount = 0, total_fee = 0, total_exp = 0;
	
		for(int i = 0; i < itemAry.size(); i++){
		JSONObject itemObject = (JSONObject) itemAry.get(i);
		JSONArray itemDataAry = (JSONArray) itemObject.get("data");
		
		if(!itemDataAry.get(0).toString().equals("<font color='#A0522D'><strong>합계</strong></font>")){	
			total_amount += Long.parseLong(itemDataAry.get(9).toString());
			total_fee += Integer.parseInt(itemDataAry.get(15).toString());
			total_exp += Long.parseLong(itemDataAry.get(16).toString());
			
			String compareTid = "";
			if(i < (itemAry.size()-1)){
				JSONObject comObject = (JSONObject) itemAry.get(i+1);
				JSONArray comData =  (JSONArray) comObject.get("data");
				compareTid = comData.get(2).toString();
			}
		
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
		
		<td class='exnum' style='border-top:none;border-left:none'><%=itemDataAry.get(9)%></td>
		
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(10)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(11)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(12)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(13)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(14)%></td>
		<td class='exnum' style='border-top:none;border-left:none'><%=itemDataAry.get(15)%></td>
		<td class='exnum' style='border-top:none;border-left:none'><%=itemDataAry.get(16)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(17)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(18)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(19)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(20)%></td>
		<td class='extxt' style='border-top:none;border-left:none'><%=itemDataAry.get(21)%></td>
	</tr>
	<% }
	}%>
	
	<TR style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt colspan="4" style="text-align:center;">합계</td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=exnum><%=total_amount%></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=exnum><%=total_fee%></td>
		<td class=exnum><%=total_exp%></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
		<td class=extxt></td>
	</TR>	
</table>
