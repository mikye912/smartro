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
<%@ page import="java.text.DecimalFormat" %>

<%

	request.setCharacterEncoding("UTF-8");
	String itemArray = request.getParameter("itemarray");
	
	//base64 decoding
	Decoder decoder = Base64.getDecoder();
	
	byte[] item_byte = decoder.decode(itemArray);
	String item_json = new String(item_byte, "UTF-8");
	
	JSONParser itemParse = new JSONParser();
	JSONObject itemObj = (JSONObject) itemParse.parse(item_json);
	JSONArray itemAry = (JSONArray) itemObj.get("rows");
	
	Date nowTime = new Date();
	SimpleDateFormat setDate = new SimpleDateFormat("yyyy-MM-dd");
	response.setHeader("Content-Type", "application/vnd.ms-xls");
	response.setHeader("Content-Disposition", "inline; filename=excel0000_"+setDate.format(nowTime)+".xls");

%>
<!DOCTYPE html>
<html>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge">
	<style>
		*{margin:0;	padding:0;}
		HTML, BODY {WIDTH:100%; HEIGHT:100%; FONT-SIZE: 12px;}
		img {border:0px;}
		.tb01_gray {margin:0px; border-collapse:collapse; border:0px; padding:0px;}
		.tb01_gray th {padding:0px;border:1px solid #cccccc; text-align:center; color:#4d4d4d; background-color:#f0f0f0; height:26px;}
		.tb01_gray td {padding:5px;border:1px solid #c0c0c0;}
		
		.tb_box {margin: 0px;border-collapse:collapse;border:0px;padding:0px;}
		.tb_box th {padding:0px;border:0px #cccccc solid;text-align:center;color:#4d4d4d; }
		.tb_box td {padding:15px;border:3px solid #c0c0c0;}
		
		.tbnone {margin: 0px;border-collapse:collapse;border:0px;padding:0px;}
		.tbnone th {padding:0px;border:0px #cccccc solid;text-align:center;color:#4d4d4d; }
		.tbnone td {padding:0px;border:0px solid #f0f0f0;}
	</style>
</head>
<body>
<table width="100%" class="tb00_none">
	<tr>
		<td>
		<b>■ 일별집계</b>
		</td>
	</tr>
</table>
<table width="100%" class="tb01_gray">
	<tr>
		<th>순번</th>
		<th>승인일</th>
		<th>매입사</th>
		<th>승인</th>
		<th>취소</th>
		<th>승인금액</th>
		<th>취소금액</th>
		<th>합계</th>
	</tr>
	<%
	if(itemAry.size() > 0){
		for (int i = 0; i<itemAry.size(); i++){
			JSONObject itemObject = (JSONObject) itemAry.get(i);
			JSONArray dataAry = (JSONArray) itemObject.get("data");

			DecimalFormat formatter = new DecimalFormat("###,###");
			
			if(dataAry.get(0).toString().equals("소계") || dataAry.get(0).toString().equals("합계")){
				//합계부분 Total(stime ~ etime)
				int acnt = Integer.parseInt(dataAry.get(3).toString());
				int ccnt = Integer.parseInt(dataAry.get(5).toString());
				int tcnt = acnt + ccnt;
				long aamt = Long.parseLong(dataAry.get(4).toString());
				long camt = Long.parseLong(dataAry.get(6).toString());
				long tamt = aamt - camt;
			%>
				<tr>
				<% if(dataAry.get(0).toString().equals("합계")){ %>
					<td colspan='3' align='center' bgcolor='#f0f0f0'>Total<br>(<%=dataAry.get(1)%> ~ <%=dataAry.get(2)%>)</td>
				<% } else { %>
					<td colspan='3' align='center' bgcolor='#f0f0f0'>소계<br>(<%=dataAry.get(1)%>)</td>
				<% } %>
					<td colspan='2' align='right' bgcolor='#f0f0f0'>정산 <%=formatter.format(tcnt)%>건<br>(<%=formatter.format(acnt)%> + <%=formatter.format(ccnt)%>)</td>
					<td colspan='3' align='right' bgcolor='#f0f0f0'>정산 <%=formatter.format(tamt)%>원<br>(<%=formatter.format(aamt)%> - <%=formatter.format(camt)%>)</td>
				</tr>
		<% } else { %>
		<tr>
			<td align="center"><%=dataAry.get(0).toString()%></td>
			<td align="center"><%=dataAry.get(1).toString()%></td>
			<td align="center"><%=dataAry.get(2).toString()%></td>
			<td align="right"><%=formatter.format(Integer.parseInt(dataAry.get(3).toString()))%></td>
			<td align="right"><%=formatter.format(Integer.parseInt(dataAry.get(5).toString()))%></td>
			<td align="right"><%=formatter.format(Long.parseLong(dataAry.get(4).toString()))%></td>
			<td align="right"><%=formatter.format(Long.parseLong(dataAry.get(6).toString()))%></td>
			<td align="right"><%=formatter.format(Long.parseLong(dataAry.get(7).toString()))%></td>
		</tr>
	<%
			}
		}
	} else { 
	%>
		<tr>
			<td colspan="8" align="center">검색된 자료가 없습니다.</td>
		</tr>	
<%  }  %>
</table>
</body>