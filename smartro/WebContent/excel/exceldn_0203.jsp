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
	//2021.02.16 강원대병원v3 - 매장별거래조회 excel download
	//소계부분 나와야함!
	request.setCharacterEncoding("UTF-8");
	Decoder decoder = Base64.getDecoder();
	
	String stime = utilm.setDefault(request.getParameter("stime")).replace("-", "");
	String etime = utilm.setDefault(request.getParameter("etime")).replace("-", "");
	String samt = utilm.setDefault(request.getParameter("samt"));
	String eamt = utilm.setDefault(request.getParameter("eamt"));
	String tid = utilm.setDefault(request.getParameter("tid"));
	String depcd = utilm.setDefault(request.getParameter("depcd"));
	
	String tuser =  utilm.setDefault(request.getParameter("uauth"));
	byte[] byte_tuser = decoder.decode(tuser);
	tuser = new String(byte_tuser, "UTF-8");
	
	String rtn_json = jbset.get_excel_0203(tuser, stime, etime, samt, eamt, tid, depcd);
	
	JSONParser rtnParser = new JSONParser();
	JSONObject rtnJson = (JSONObject) rtnParser.parse(rtn_json);
	
	String totalArray = rtnJson.get("TOTALARRAY").toString();
	String itemArray = rtnJson.get("ITEMARRAY").toString();
	
	JSONParser totalParser = new JSONParser();
	JSONObject totalObj = (JSONObject) totalParser.parse(totalArray);
	JSONArray totalAry = (JSONArray) totalObj.get("rows");
	
	JSONParser itemParse = new JSONParser();
	JSONObject itemObj = (JSONObject) itemParse.parse(itemArray);
	JSONArray itemAry = (JSONArray) itemObj.get("rows");
	
	Date nowTime = new Date();
	SimpleDateFormat setDate = new SimpleDateFormat("yyyy-MM-dd");
	response.setHeader("Content-Type", "application/vnd.ms-xls");
	response.setHeader("Content-Disposition", "inline; filename=excel0203_"+setDate.format(nowTime)+".xls");
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
		<td class=extxt style='font-size:12pt; font-weight:bold; border-top:none;border-left:none' colspan="9">□ 매장별거래조회</td>
	</TR>
</table>
<TABLE><TR><TD HEIGHT="10"></TD></TR></TABLE>
<table  style='border-collapse:collapse;table-layout:fixed;width:270pt'>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none;border-right:none;' COLSPAN="9">□ 집계자료</td>
	</TR>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none' colspan="3"></td>
		<td class=extxt style='border-top:none;border-left:none'>승인건수</td>
		<td class=extxt style='border-top:none;border-left:none'>승인금액</td>
		<td class=extxt style='border-top:none;border-left:none'>취소건수</td>
		<td class=extxt style='border-top:none;border-left:none'>취소금액</td>
		<td class=extxt style='border-top:none;border-left:none'>합계건수</td>
		<td class=extxt style='border-top:none;border-left:none'>합계금액</td>
	</tr>
	<%
		for (int i = 0; i<totalAry.size(); i++){
			JSONObject totalObject = (JSONObject) totalAry.get(i);
			JSONArray totalData = (JSONArray)totalObject.get("data");
	%>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none' colspan="3">합계</td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(2)%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(3)%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(4)%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(5)%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(6)%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(7)%></td>
	</tr>
	<% } %>
</table>

<TABLE>
<TR><TD></TD></TR>
</TABLE>


<table  style='border-collapse:collapse;table-layout:fixed;width:270pt'>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none;border-right:none;' COLSPAN="9">□ 상세자료</td>
	</TR>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none'>사업부</td>
		<td class=extxt style='border-top:none;border-left:none'>단말기명</td>
		<td class=extxt style='border-top:none;border-left:none'>단말기번호</td>
		<td class=extxt style='border-top:none;border-left:none'>승인건수</td>
		<td class=extxt style='border-top:none;border-left:none'>승인금액</td>
		<td class=extxt style='border-top:none;border-left:none'>취소건수</td>
		<td class=extxt style='border-top:none;border-left:none'>취소금액</td>
		<td class=extxt style='border-top:none;border-left:none'>합계건수</td>
		<td class=extxt style='border-top:none;border-left:none'>합계금액</td>
	</TR>
	
	<%
		//합계
		long total_aamt = 0, total_camt = 0;
		int total_acnt = 0, total_ccnt = 0;
		
		//소계
		long day_aamt = 0, day_camt = 0;
		int day_acnt = 0, day_ccnt = 0;
		
		for(int i = 0; i<itemAry.size(); i++){
			JSONObject itemObject = (JSONObject) itemAry.get(i);
			JSONArray itemData = (JSONArray) itemObject.get("data");
			
			if(!itemData.get(0).toString().equals("<font color='#A0522D'><strong>합계</strong></font>")){
			
				day_acnt += Integer.parseInt(itemData.get(3).toString());
				day_aamt += Long.parseLong(itemData.get(4).toString());
				day_ccnt += Integer.parseInt(itemData.get(5).toString());
				day_camt += Long.parseLong(itemData.get(6).toString());
				
				total_acnt += Integer.parseInt(itemData.get(3).toString());
				total_aamt += Long.parseLong(itemData.get(4).toString());
				total_ccnt += Integer.parseInt(itemData.get(5).toString());
				total_camt += Long.parseLong(itemData.get(6).toString());
				
				String compareTid = "";
				if(i < (itemAry.size()-1)){
					JSONObject comObject = (JSONObject) itemAry.get(i+1);
					JSONArray comData =  (JSONArray) comObject.get("data");
					compareTid = comData.get(2).toString();
				}
			if(itemData.get(0).toString().equals("합계")){
				%>
			
				<TR style="mso-height-source:userset;height:15.95pt">
				<TD class=extxt style="border-top:none;border-left:none;text-align:center;" colspan='3'><%=itemData.get(0)%></TD>
				<TD class=exnum style="border-top:none;border-left:none"><%=itemData.get(3)%></TD>
				<TD class=exnum style="border-top:none;border-left:none"><%=itemData.get(4)%></TD>
				<TD class=exnum style="border-top:none;border-left:none"><%=itemData.get(5)%></TD>
				<TD class=exnum style="border-top:none;border-left:none"><%=itemData.get(6)%></TD>
				<TD class=exnum style="border-top:none;border-left:none"><%=itemData.get(7)%></TD>
				<TD class=exnum style="border-top:none;border-left:none"><%=itemData.get(8)%></TD>
			</TR>	
		<%
			}else if(!itemData.get(0).toString().equals("합계")){
		%>	
	<TR style='mso-height-source:userset;height:15.95pt'>
		<TD class=extxt style='border-top:none;border-left:none'><%=itemData.get(0)%></TD>
		<TD class=extxt style='border-top:none;border-left:none'><%=itemData.get(1)%></TD>
		<TD class=extxt style='border-top:none;border-left:none'><%=itemData.get(2)%></TD>
		<TD class=exnum style='border-top:none;border-left:none'><%=itemData.get(3)%></TD>
		<TD class=exnum style='border-top:none;border-left:none'><%=itemData.get(4)%></TD>
		<TD class=exnum style='border-top:none;border-left:none'><%=itemData.get(5)%></TD>
		<TD class=exnum style='border-top:none;border-left:none'><%=itemData.get(6)%></TD>
		<TD class=exnum style='border-top:none;border-left:none'><%=itemData.get(7)%></TD>
		<TD class=exnum style='border-top:none;border-left:none'><%=itemData.get(8)%></TD>
	</TR>
	<%}%>

	<% 
		if(!compareTid.equals(itemData.get(2).toString()) || i == (itemAry.size()-2)){	
	%>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<TD class=extxt style='border-top:none;border-left:none;' COLSPAN="3">소계</TD>
		<TD class=exnum style='border-top:none;border-left:none'><%=day_acnt%></TD>
		<TD class=exnum style='border-top:none;border-left:none'><%=day_aamt%></TD>
		<TD class=exnum style='border-top:none;border-left:none'><%=day_ccnt%></TD>
		<TD class=exnum style='border-top:none;border-left:none'><%=day_camt%></TD>
		<TD class=exnum style='border-top:none;border-left:none'><%=day_acnt + day_ccnt%></TD>
		<TD class=exnum style='border-top:none;border-left:none'><%=day_aamt - day_camt%></TD>
	</TR>
	<% 
			day_acnt = 0;
			day_aamt = 0;
			day_ccnt = 0;
			day_camt = 0;
			
			}
		}
	}
	%>
</table>
