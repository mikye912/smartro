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
	String sadd_date = utilm.setDefault(request.getParameter("sadd_date")).replaceAll("-", "");
	String eadd_date = utilm.setDefault(request.getParameter("eadd_date")).replaceAll("-", "");
	String sadd_recp = utilm.setDefault(request.getParameter("sadd_recp")).replaceAll("-", "");
	String eadd_recp = utilm.setDefault(request.getParameter("eadd_recp")).replaceAll("-", "");
	String appno = utilm.setDefault(request.getParameter("appno"));
	String pid = utilm.setDefault(request.getParameter("pid"));
	String pcd = utilm.setDefault(request.getParameter("pcd"));
	String depcd = utilm.setDefault(request.getParameter("depcd"));
	
	String auth01 = utilm.setDefault(request.getParameter("auth01"));
	String auth02 = utilm.setDefault(request.getParameter("auth02"));
	String auth03 = utilm.setDefault(request.getParameter("auth03"));
	
	String card01 = utilm.setDefault(request.getParameter("card01"));
	String card02 = utilm.setDefault(request.getParameter("card02"));
	String card03 = utilm.setDefault(request.getParameter("card03"));
	String card04 = utilm.setDefault(request.getParameter("card04"));
	String card05 = utilm.setDefault(request.getParameter("card05"));
	
	String paging = utilm.setDefault(request.getParameter("page"));
	
	String tuser =  utilm.setDefault(request.getParameter("uauth"));
	byte[] byte_tuser = decoder.decode(tuser);
	tuser = new String(byte_tuser, "UTF-8");

	String rtncd = jbset.get_excel_0107_cvs(tuser, stime, etime, sadd_date, eadd_date, sadd_recp, eadd_recp, appno, pid, pcd, depcd, auth01, auth02, auth03, card01, card02, card03, card04, card05, paging);

	JSONParser rtnParser = new JSONParser();
	JSONObject rtnJson = (JSONObject)rtnParser.parse(rtncd);
	
	String totalArray = rtnJson.get("TOTALARRAY").toString();
	String itemArray = rtnJson.get("ITEMARRAY").toString();
	
	//total
	JSONParser totalParse = new JSONParser();
	JSONObject totalObj = (JSONObject) totalParse.parse(totalArray);
	JSONArray totalAry = (JSONArray)totalObj.get("rows");
	
	//detail
	JSONParser itemParse = new JSONParser();
	JSONObject itemObj = (JSONObject) itemParse.parse(itemArray);
	JSONArray itemAry = (JSONArray) itemObj.get("rows");
	
	
	Date nowTime = new Date();
	SimpleDateFormat setDate = new SimpleDateFormat("yyyy-MM-dd");
	response.setHeader("Content-Type", "application/vnd.ms-xls");
	response.setHeader("Content-Disposition", "inline; filename=excel0107_cvs_"+setDate.format(nowTime)+".xls");
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
		<td class=extxt style='font-size:12pt; font-weight:bold; border-top:none;border-left:none' colspan="6">□ 현금영수증조회</td>
	</TR>
</table>
<TABLE><TR><TD HEIGHT="10"></TD></TR></TABLE>
<table  style='border-collapse:collapse;table-layout:fixed;width:270pt'>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class='extxt' style='border-top:none;border-left:none;border-right:none;' COLSPAN="6">□ 집계자료</td>
	</TR>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none'>승인건수</td>
		<td class=extxt style='border-top:none;border-left:none'>승인금액</td>
		<td class=extxt style='border-top:none;border-left:none'>취소건수</td>
		<td class=extxt style='border-top:none;border-left:none'>취소금액</td>
		<td class=extxt style='border-top:none;border-left:none'>합계건수</td>
		<td class=extxt style='border-top:none;border-left:none'>합계금액</td>
	</tr>
	<%
			JSONObject totalObject = (JSONObject) totalAry.get(0);
			JSONArray totalData = (JSONArray)totalObject.get("data");
	%>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none' colspan="2" style="text-align:center;">합계</td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(0).toString()%></td> <%--승인건수--%>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(1).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(2).toString()%></td>
		<td class=exnum style='border-top:none;border-left:none'><%=totalData.get(3).toString()%></td>
	</tr>
</table>

<TABLE><TR><TD HEIGHT="10"></TD></TR></TABLE>
<table  style='border-collapse:collapse;table-layout:fixed;width:270pt'>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none;border-right:none;' COLSPAN="17">□ 상세자료</td>
	</TR>

	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class=extxt style='border-top:none;border-left:none'>순번</td>
		<td class=extxt style='border-top:none;border-left:none'>점포명</td>
		<td class=extxt style='border-top:none;border-left:none'>점포코드</td>
		<td class=extxt style='border-top:none;border-left:none'>송장번호</td>
		<td class=extxt style='border-top:none;border-left:none'>접수일자</td>
		<td class=extxt style='border-top:none;border-left:none'>집하일자</td>
		<td class=extxt style='border-top:none;border-left:none'>결제구분</td>
		<td class=extxt style='border-top:none;border-left:none'>승인일자</td>
		<td class=extxt style='border-top:none;border-left:none'>승인시간</td>
		<td class=extxt style='border-top:none;border-left:none'>원승인일자</td>
		<td class=extxt style='border-top:none;border-left:none'>승인번호</td>
		<td class=extxt style='border-top:none;border-left:none'>승인구분</td>
		<td class=extxt style='border-top:none;border-left:none'>신분확인번호</td>
		<td class=extxt style='border-top:none;border-left:none'>금액</td>
		<td class=extxt style='border-top:none;border-left:none'>거래구분</td>
		<td class=extxt style='border-top:none;border-left:none'>취소사유</td>
		<td class=extxt style='border-top:none;border-left:none'>거래고유번호</td>
		
	</TR>
<%
		for(int i = 0; i<itemAry.size(); i++){
			JSONObject itemObject = (JSONObject) itemAry.get(i);
			JSONArray itemData = (JSONArray) itemObject.get("data");
			
			if(i != itemAry.size()-1){
	%>
				<tr style='mso-height-source:userset;height:15.95pt'>
					<td class=extxt><%=itemData.get(0)%></td>
					<td class=extxt><%=itemData.get(1)%></td>
					<td class=extxt><%=itemData.get(2)%></td>
					<td class=extxt><%=itemData.get(3)%></td>
					<td class=exnum><%=itemData.get(4)%></td>
					<td class=exnum><%=itemData.get(5)%></td>
					<td class=exnum><%=itemData.get(6)%></td>
					<td class=exnum><%=itemData.get(7)%></td>
					<td class=exnum><%=itemData.get(8)%></td>
					<td class=exnum><%=itemData.get(9)%></td>
					<td class=exnum><%=itemData.get(10)%></td>
					<td class=exnum><%=itemData.get(11)%></td>
					<td class=exnum><%=itemData.get(12)%></td>
					<td class=exnum><%=itemData.get(13)%></td>
					<td class=exnum><%=itemData.get(14)%></td>
					<td class=exnum><%=itemData.get(15)%></td>
					<td class=exnum><%=itemData.get(16)%></td>
				</tr>
	<%
			} else {
				//맨 마지막 인덱스 : 합계
	%>
				<tr style='mso-height-source:userset;height:15.95pt'>
					<td class='extxt' colspan="3" style="text-align:center;">합계</td>
					<td class='exnum'><%=itemData.get(3)%></td>	
					<td class='exnum'><%=itemData.get(4)%></td>	
					<td class='exnum'><%=itemData.get(5)%></td>	
					<td class='exnum'><%=itemData.get(6)%></td>	
					<td class='exnum'><%=itemData.get(7)%></td>	
					<td class='exnum'><%=itemData.get(8)%></td>	
					<td class='exnum'><%=itemData.get(9)%></td>	
					<td class='exnum'><%=itemData.get(10)%></td>	
					<td class='exnum'><%=itemData.get(11)%></td>	
					<td class='exnum'><%=itemData.get(12)%></td>	
					<td class='exnum'><%=itemData.get(13)%></td>	
					<td class='exnum'><%=itemData.get(14)%></td>	
					<td class='exnum'><%=itemData.get(15)%></td>	
					<td class='exnum'><%=itemData.get(16)%></td>	
				</TR>
	<% 
			}
			
		} 
	%>
</table>