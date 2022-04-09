<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.*, java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="java.lang.String" %>
<%@ page import="java.security.*" %>
<%@ page import="java.util.Base64.Encoder" %>
<%@ page import="java.util.Base64.Decoder" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="org.json.simple.parser.JSONParser"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<%
	//매출달력
	Decoder decoder = Base64.getDecoder();
	
	String tuser = (String)session.getAttribute("uinfo");
	
	String tmp_tid = (String)session.getAttribute("usertid");
	byte[] usertid_buf = decoder.decode(tmp_tid);
	String usertid = new String(usertid_buf, "UTF-8");
	
	String tmp_depo = (String)session.getAttribute("userdepo");
	byte[] userdepo_buf = decoder.decode(tmp_depo);
	String userdepo = new String(userdepo_buf, "UTF-8");

	Date nowTime = new Date();
	
	String now_year =  new SimpleDateFormat("yyyy").format(nowTime);
	String now_month = new SimpleDateFormat("MM").format(nowTime);
	String now_date = new SimpleDateFormat("dd").format(nowTime);
	
	//2021.03.15 매출달력 detail
	//sub02_10.jsp에서 넘어온 년, 월 data
	//년, 월, 카드사선택, 가맹점번호, 단말기번호, 사업부
	String syear = request.getParameter("syear");
	String smon = request.getParameter("smon");
	String sday = "";
	
	if(syear != "" && smon != ""){
		sday = now_date;
	} else {
		syear = now_year;
		smon = now_month;
	}
	
	smon = String.format("%02d", Integer.parseInt(smon));
	
	//이전 년, 월
	//다음 년, 월
	Calendar cal = Calendar.getInstance();
	
	//이건 어디다 쓰는거지 ㅇㅂㅇ
	//2021.03.15 최초 설정일 때만 이 값이고, 만약에 < >버튼을 눌러서 온 경우라면
	//이 값이 아니라 parameter 값 기준으로 설정해야함.
	cal.set(Integer.parseInt(syear), Integer.parseInt(smon)-1, 1);
	cal.add(Calendar.MONTH, -1);
	//2021.03.19
	//parameter 값 기준으로 지난달, 월 & 다음달, 월 계산
	String pre_y = new SimpleDateFormat("yyyy").format(cal.getTime());
	String pre_m = new SimpleDateFormat("MM").format(cal.getTime());
	
	cal.add(Calendar.MONTH, +2);
	
	String nex_y = new SimpleDateFormat("yyyy").format(cal.getTime());
	String nex_m = new SimpleDateFormat("MM").format(cal.getTime());

	cal.set(Integer.parseInt(syear), Integer.parseInt(smon)-1, 1);
	
	//검색 달의 최대 일수를 구함 (smon -1)
	int last_month_day = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	//1일의 요일을 구함 (1~7)
	int blank = cal.get(Calendar.DAY_OF_WEEK);
	
	//sub02_10.jsp 에서 넘어온 parameter data
	String acqcd = request.getParameter("acqcd") == null ? "" : request.getParameter("acqcd");
	String mid = request.getParameter("mid") == null ? "" : request.getParameter("mid");
	String tid = request.getParameter("tid") == null ? "" : request.getParameter("tid");
	String depcd = request.getParameter("depcd") == null ? "" : request.getParameter("depcd");
	
	//카드
	String result_card = jbset.get_0210_cal_item_card(tuser, syear, smon, acqcd, mid, tid, depcd);

	JSONParser cardParser = new JSONParser();
	JSONObject cardObj = (JSONObject)cardParser.parse(result_card);
	JSONArray cardAry = (JSONArray) cardObj.get("rows");

	//현금
	String result_cash = jbset.get_0210_cal_item_cash(tuser, syear, smon, acqcd, mid, tid, depcd);

	JSONParser cashParser = new JSONParser();
	JSONObject cashObj = (JSONObject)cashParser.parse(result_cash);
	JSONArray cashAry = (JSONArray) cashObj.get("rows");

%>


<!DOCTYPE html>
<html>
<head>
	<title>Init from script</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" type="text/css" href="./dhtmlx/codebase/dhtmlx.css"/>
	<link rel="stylesheet" type="text/css" href="./dhtmlx/dhtmlxGrid/codebase/skins/dhtmlxgrid_dhx_web.css"/>
	<link type="text/css" rel="stylesheet" href="./include/css/style.css"  media="all" >
	<script src="./dhtmlx/codebase/dhtmlx.js"></script>
	<script src="./include/js/jquery-1.8.1.min.js" type="text/javascript"></script>
	<script src="./include/js/common.js"></script>
	<script src="./include/js/dhtmlxgrid_export.js"></script>

	<link type="text/css" rel="stylesheet" href="./include/css/jquery-ui-1.10.3.jdh.css" />
	<script src="./include/js/jquery-1.9.1.js"></script>
	<script src="./include/js/jquery-ui-1.10.3.custom.js"></script>

	<style>
		table.tb {border-collapse: collapse;}
		table.tb>tr>td,
		table.tb>tr>th,
		table.tb>tbody>tr>td,
		table.tb>tbody>tr>th {border:1px solid #cccccc; padding:5px;}

		.tb05_none {margin: 0px;border-collapse:collapse;border:1px;padding:0px;}
		.tb05_none th {padding:0px;border:1px #cccccc solid;text-align:center ;color:#4d4d4d; }
		.tb05_none td {padding:0px;border:1px #cccccc solid; font-size:9pt;}
		
		.txt_sat_bold {color:#3399ff;font-weight:bold;}
		.txt_sat {color:#3399ff;}
		.txt_sun_bold {color:#ff3300;font-weight:bold;}
		.txt_sun {color:#ff3300;}
		.txt_non {color:#333333;}
		.txt_non_bold {color:#333333;}
	</style>
	
	<script>
		function doOnLoad() {
			var h = $(window).height();
			var w = $(window).width();
			var hset	= (h-130) + "px";
			var tdw	= Math.floor(w/7) + "px";
			$("#caltb").css("height",hset);
			$("#caltd").css("width",tdw);
		}

		function cal_prev(){
			var syear = "<%=pre_y%>";
			var smon = "<%=pre_m%>";
			var url  = "sub02_10.jsp?uauth=<%=tuser%>&searchYear="+syear+"&searchMon="+smon+"&trtp=P";
			parent.document.location.href=url;
		}

		function cal_next(){
			var syear = "<%=nex_y%>";
			var smon = "<%=nex_m%>";
			var url  = "sub02_10.jsp?uauth=<%=tuser%>&searchYear="+syear+"&searchMon="+smon+"&trtp=P";
			parent.document.location.href=url;
		}
		
		//2020.12.10 신용부분 눌렀을 때 카드사별 조회 tab으로 이동
		//넘길 값 : 날짜(stime=etime)
		function cal_card_page(id){
			var year = "<%=syear%>";
			var month = "<%=smon%>";
			var day = $(id).prev().prev().find("span").text();
			var time = year+"-"+lpad(month, 2, "0")+"-"+lpad(day, 2, "0");
			var w = "?uauth=<%=tuser%>&stime="+time+"&etime="+time+"&trtp=P";
			
			//2021.03.19 검색조건이 추가적으로 있었을 때	
			var acqcd = parent.document.getElementById("acqcd").value;
			var depcd = parent.document.getElementById("depcd").value;
			var tid = parent.document.getElementById("tid").value;
			var mid = parent.document.getElementById("mid").value;
			
			w	+= (acqcd!="") ? "&acqcd="+acqcd : "";
			w	+= (depcd!="") ? "&depcd="+depcd : "";
			w	+= (tid!="") ? "&tid="+tid : "";
			w	+= (mid!="") ? "&mid="+mid : "";
			
			parent.parent.urlgoDirect(-1, "카드사별조회(V)", "sub02_01.jsp"+w);
		}

		//2020.12.11 1일 ~ 9일까지 앞에 0 붙여서 처리하는 메서드
		function lpad(str, padLen, padStr){
			if(padStr.length > padLen){
				return str;
			}

			while(str.length < padLen) {
				str = padStr + str;
			}
			str = str.length >= padLen ? str.substring(0, padLen) : str;
			return str;
		}

	</script>
</head>
<body onload="doOnLoad();">
<table id="caltb" width="100%">
	<tr>
		<td width="100%" height="100%" style="padding:5px;" valign="top">
			<table width="100%">
				<tr>
					<td width="150"><span class="button large" onclick="javascript:cal_prev();"><a href="#"><</a></span><span class="button large" onclick="javascript:cal_next();"><a href="#">></a></span></td>
					<td width="*"><h2><%=syear%>년 <%=smon%>월<h2></td>
					<td width="150"></td>
				</tr>
			</table>
			<table width="100%">
				<tr><td height="10"></td></tr>
			</table>

			<table width="100%"  class="tb05_none" style="table-layout:fixed;">
				<tr height="32" align="center">
					<th bgcolor="#3399ff">일</th>
					<th bgcolor="#3399ff">월</th>
					<th bgcolor="#3399ff">화</th>
					<th bgcolor="#3399ff">수</th>
					<th bgcolor="#3399ff">목</th>
					<th bgcolor="#3399ff">금</th>
					<th bgcolor="#3399ff">토</th>
				</tr>
			<%
			//전체 라인 수
			int line = (int) Math.ceil((double)(blank + last_month_day) / 7);

			int card_cnt = 0;
			int cash_cnt = 0;
			long card_amt = 0;
			long cash_amt = 0;
			
			//JSONObject 위치 체크용
			int cash_point = 0;
			int card_point = 0;
			
			//원단위 표시
			DecimalFormat formatter = new DecimalFormat("###,###");
			
			for (int i = 0; i <line; i++) { 
			%>
				<tr>
			<% 
				//일 ~ 토 출력
				for (int j = 1; j <= 7; j++) { 
					int date = (i * 7 - blank + j)+1; //날짜 정하기
					if (date <= 0 || date > last_month_day){
						date = 0; //마이너스나 날짜가 넘어가는것을 빈칸 처리해준다 

					}
					
					String txt_cls = "txt_non";
					
					if(date > 0){
						//매주 토요일, 일요일마다 표기 (파랑, 빨강)
						cal.set(Integer.parseInt(syear), Integer.parseInt(smon)-1, date);
						int dayCheck = cal.get(Calendar.DAY_OF_WEEK);
						
						if(dayCheck == 7){
							txt_cls	= "txt_sat_bold";
						} else if (dayCheck == 1) {
							txt_cls	= "txt_sun_bold";
						}
						
						//1. 거래건이 아예 0건인 경우는 표출을 해주지 않아야 함.
						//2. 합계 금액은 0이더라도 거래건이 있을 경우 표출이 되어야 함.
						//즉, 표기 기준은 [거래건수 기준]
						
						String compareDay = syear + smon + String.format("%02d", date);
						
						//신용 거래건 표기
						if(cardAry.size() > 0){
							JSONObject cardObject = (JSONObject) cardAry.get(card_point);
							JSONArray card_date = (JSONArray) cardObject.get("data");
							
							if(compareDay.equals(card_date.get(0).toString())){
								card_amt = Long.parseLong(card_date.get(1).toString());
								card_cnt = Integer.parseInt(card_date.get(2).toString());
								if(card_point < (cardAry.size()-1)){
									card_point++;
								}
							} else {
								card_amt = 0;
								card_cnt = 0;
							}
						}
						
						//현금 거래건 표기
						if(cashAry.size() > 0){
							JSONObject cashObject = (JSONObject) cashAry.get(cash_point);
							JSONArray cash_date = (JSONArray) cashObject.get("data");
							
							if(compareDay.equals(cash_date.get(0).toString())){
								cash_amt = Long.parseLong(cash_date.get(1).toString());
								cash_cnt = Integer.parseInt(cash_date.get(2).toString());
								if(cash_point < (cashAry.size()-1)){
									cash_point++;
								}
							} else {
								cash_amt = 0;
								cash_cnt = 0;
							}
						}
					}
			%>
					<td class="caltd" height="70" valign="top" align="right" style="text-ailgn:right; padding:5px;">
						<div class="cal_date" style="position:relative; width:100%; height:16px; float:left;">
						<span class="<%=txt_cls%>"><%=date > 0 && date <= last_month_day ? date : "" %></span></div>
						<div class="cal_date" style="position:relative; width:100%; height:6px; float:left;"></div>
						<!-- 신용 클릭시 카드사별 조회 tab 오픈 -->
						<%	if(date > 0) {
								if(card_cnt != 0){
						%>
						<div class="cal_date" onClick="cal_card_page(this)" style="position:relative; width:100%; height:16px; float:left;">
							<a><span class="<%=txt_cls%>">신용 : <%=formatter.format(card_amt)%>(<%=card_cnt%>)</span></a>
						</div>
						<%
								}
						
								if(cash_cnt != 0){
						%>
						<div class="cal_date" style="position:relative; width:100%; height:16px; float:left;">
							<span class="<%=txt_cls%>">현금 : <%=formatter.format(cash_amt)%>(<%=cash_cnt%>)</span>
						</div>
						
						<%
								}  
						%>
					</td>
			<%
						}
					}
				}
			%>
				</tr>
			</table>
		</td>
	</tr>
</table>