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
	
	String tuser =  utilm.setDefault(request.getParameter("uauth"));
	byte[] byte_tuser = decoder.decode(tuser);
	tuser = new String(byte_tuser, "UTF-8");
	
	String syear = utilm.setDefault(request.getParameter("syear"));
	String smon = utilm.setDefault(request.getParameter("smon"));
	String samt = utilm.setDefault(request.getParameter("samt"));
	String eamt = utilm.setDefault(request.getParameter("eamt"));
	String tid = utilm.setDefault(request.getParameter("tid"));
	String mid = utilm.setDefault(request.getParameter("mid"));
	String acqcd = utilm.setDefault(request.getParameter("acqcd"));
	String depcd = utilm.setDefault(request.getParameter("depcd"));
	
	String rtncd = jbset.get_excel_0202(tuser, syear, smon, samt, eamt, tid, mid, acqcd, depcd);

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
	response.setHeader("Content-Disposition", "inline; filename=excel0202_"+setDate.format(nowTime)+".xls");
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
		mso-style-name:"?????? \[0\]";
		mso-style-id:6;
		mso-background-source:auto;
		mso-rotate:0;
		mso-background-source:auto;
		mso-pattern:auto;
		mso-protection:locked visible;
		mso-style-name:??????;
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
		font-family:"?????? ??????", monospace;
		mso-font-charset:129;
		border:none;
		mso-protection:locked visible;
		mso-style-name:??????;
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
		font-family:"?????? ??????", monospace;
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
		<td class=extxt style='font-size:12pt; font-weight:bold; border-top:none;border-left:none' colspan="10">??? ??????????????????</td>
	</TR>
</table>
<TABLE><TR><TD HEIGHT="10"></TD></TR></TABLE>
<table  style='border-collapse:collapse;table-layout:fixed;width:270pt'>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class='extxt' style='border-top:none;border-left:none;border-right:none;' COLSPAN="10">??? ????????????</td>
	</TR>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class='extxt' colspan="4"></td>
		<td class='extxt'>????????????</td>
		<td class='extxt'>????????????</td>
		<td class='extxt'>????????????</td>
		<td class='extxt'>????????????</td>
		<td class='extxt'>????????????</td>
		<td class='extxt'>????????????</td>
	</tr>
	<%
		for (int i = 0; i<totalAry.size(); i++){
			JSONObject totalObject = (JSONObject) totalAry.get(i);
			JSONArray totalData = (JSONArray)totalObject.get("data");
	%>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class='extxt' colspan="4">??????</td>
		<td class='exnum'><%=totalData.get(4)%></td>
		<td class='exnum'><%=totalData.get(5)%></td>
		<td class='exnum'><%=totalData.get(6)%></td>
		<td class='exnum'><%=totalData.get(7)%></td>
		<td class='exnum'><%=totalData.get(8)%></td>
		<td class='exnum'><%=totalData.get(9)%></td>
	</tr>
	<% } %>
</table>

<TABLE><TR><TD HEIGHT="10"></TD></TR></TABLE>
<table  style='border-collapse:collapse;table-layout:fixed;width:270pt'>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class='extxt' style='border-top:none;border-left:none;border-right:none;' COLSPAN="10">??? ????????????</td>
	</TR>
	<tr style='mso-height-source:userset;height:15.95pt'>
		<td class='extxt'>????????????</td>
		<td class='extxt'>?????????</td>
		<td class='extxt'>?????????</td>
		<td class='extxt'>???????????????</td>
		<td class='extxt'>????????????</td>
		<td class='extxt'>????????????</td>
		<td class='extxt'>????????????</td>
		<td class='extxt'>????????????</td>
		<td class='extxt'>????????????</td>
		<td class='extxt'>????????????</td>
	</TR>
	<%
		for(int i = 0; i<itemAry.size(); i++){
			JSONObject itemObject = (JSONObject) itemAry.get(i);
			JSONArray itemData = (JSONArray)itemObject.get("data");
			
			String result = itemData.get(0).toString();
			if(result.equals("<font color='#8B4513'><strong>??????</strong></font>") || result.equals("<font color='#A0522D'><strong>??????</strong></font>")){
				
	%>
	<TR style='mso-height-source:userset;height:15.95pt'>
		<TD class='extxt' colspan="4"><%=itemData.get(0)%></TD>
		<TD class='exnum'><%=itemData.get(4)%></TD>
		<TD class='exnum'><%=itemData.get(5)%></TD>
		<TD class='exnum'><%=itemData.get(6)%></TD>
		<TD class='exnum'><%=itemData.get(7)%></TD>
		<TD class='exnum'><%=itemData.get(8)%></TD>
		<TD class='exnum'><%=itemData.get(9)%></TD>
	</TR>
	<% } else { %>
	<TR style='mso-height-source:userset;height:15.95pt'>
		<TD class='extxt'><%=itemData.get(0)%></TD>
		<TD class='extxt'><%=itemData.get(1)%></TD>
		<TD class='extxt'><%=itemData.get(2)%></TD>
		<TD class='extxt'><%=itemData.get(3)%></TD>
		<TD class='exnum'><%=itemData.get(4)%></TD>
		<TD class='exnum'><%=itemData.get(5)%></TD>
		<TD class='exnum'><%=itemData.get(6)%></TD>
		<TD class='exnum'><%=itemData.get(7)%></TD>
		<TD class='exnum'><%=itemData.get(8)%></TD>
		<TD class='exnum'><%=itemData.get(9)%></TD>
	</TR>
	<% 	} 
		}
	%>
</table>