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
<%
	Decoder decoder = Base64.getDecoder();
	
	String tuser = (String)session.getAttribute("uinfo");
	
	String tmp_tid = (String)session.getAttribute("usertid");
	byte[] usertid_buf = decoder.decode(tmp_tid);
	String usertid = new String(usertid_buf, "UTF-8");
	
	String tmp_depo = (String)session.getAttribute("userdepo");
	byte[] userdepo_buf = decoder.decode(tmp_depo);
	String userdepo = new String(userdepo_buf, "UTF-8");

	Date nowTime = new Date();
	SimpleDateFormat sf = new SimpleDateFormat("yyyy년 MM월 dd일 a hh:mm:ss");
	SimpleDateFormat setdate = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat sdate = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat edate = new SimpleDateFormat("yyyyMMdd");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link type="text/css" rel="stylesheet" href="./include/css/style.css"  media="all" >
	<script src="./include/js/jquery-1.8.1.min.js" type="text/javascript"></script>
	<script src="./include/js/common.js"></script>
	<link type="text/css" rel="stylesheet" href="./include/css/jquery-ui-1.10.3.jdh.css" />
	<script src="./include/js/jquery-1.9.1.js"></script>
	<script src="./include/js/jquery-ui-1.10.3.custom.js"></script>
<head>
<title>:::Welcome Administrator:::</title>
<script>
	function formchk(){
		var f = document.regdata;

		if(f.STAT_DIFF.value==""){
			alert("대사상태를 선택하여 주십시오.");
			$("#STAT_DIFF").css("border","1px solid red");
			f.STAT_DIFF.FOCUS();
			return false;
		}

		if(f.STAT_STP.value==""){
			alert("거래상태를 선택하여 주십시오.");
			$("#STAT_STP").css("border","1px solid red");
			f.STAT_STP.FOCUS();
			return false;
		}

		if(f.APPGB.value==""){
			alert("거래구분을 선택하여 주십시오.");
			$("#APPGB").css("border","1px solid red");
			f.APPGB.FOCUS();
			return false;
		}

		if(f.TID.value==""){
			alert("단말기번호를 입력하여 주십시오.");
			$("#TID").css("border","1px solid red");
			f.TID.FOCUS();
			return false;
		}

		if(f.MID.value==""){
			alert("가맹점번호를 입력하여 주십시오.");
			$("#MID").css("border","1px solid red");
			f.MID.FOCUS();
			return false;
		}

		if(f.CARDNO.value==""){
			alert("카드번호를 입력하여 주십시오.");
			$("#CARDNO").css("border","1px solid red");
			f.CARDNO.FOCUS();
			return false;
		}

		if(f.HALBU.value==""){
			alert("할부기간을 입력하여 주십시오.");
			$("#HALBU").css("border","1px solid red");
			f.HALBU.FOCUS();
			return false;
		}

		if(f.AMT_UNIT.value==""){
			alert("판매금액을 입력하여 주십시오.");
			$("#AMT_UNIT").css("border","1px solid red");
			f.AMT_UNIT.FOCUS();
			return false;
		}

		if(f.ACQ_CD.value==""){
			alert("매입사를 선택하여 주십시오.");
			$("#ACQ_CD").css("border","1px solid red");
			f.ACQ_CD.FOCUS();
			return false;
		}

		if(f.ENTRYMD.value==""){
			alert("입력방식을 선택하여 주십시오.");
			$("#ENTRYMD").css("border","1px solid red");
			f.ENTRYMD.FOCUS();
			return false;
		}

		if(f.AMOUNT.value==""){
			alert("합계금액을 입력하여 주십시오.");
			$("#AMOUNT").css("border","1px solid red");
			f.AMOUNT.FOCUS();
			return false;
		}

		if(f.APPNO.value==""){
			alert("승인번호를 입력하여 주십시오.");
			$("#APPNO").css("border","1px solid red");
			f.APPNO.FOCUS();
			return false;
		}

		if(f.APPDD.value==""){
			alert("승인일자를 입력하여 주십시오.");
			$("#APPDD").css("border","1px solid red");
			f.APPDD.FOCUS();
			return false;
		}

		if(f.APPGB.value=="C"){
			if(f.OAPPNO.value==""){
				alert("원승인번호를 입력하여 주십시오.");
				$("#OAPPNO").css("border","1px solid red");
				f.OAPPNO.FOCUS();
				return false;
			}

			if(f.OAPPDD.value==""){
				alert("원승인일자를 입력하여 주십시오.");
				$("#OAPPDD").css("border","1px solid red");
				f.OAPPDD.FOCUS();
				return false;
			}
		}

		f.submit();
	}

	function rtnrst(obj, appdd, appno){
		if(obj=='Y'){
			if(confirm('정상등록되었습니다. 등록된 정보를 확인하시겠습니까?')){
				parent.xml_reload(appdd, appno);
				parent.window.close();
			}else{
				parent.window.close();
			}
		}else{
			alert('전화승인등록중 오류가 발생하였습니다. 다시시도하여 주십시오.');
		}
	}
</script>
<style>
body{margin:0; padding:0;}
#wrap{width:800px;height: auto;margin:0 auto;padding:5px;}
#contents{width:100%;height:473px;margin:0 auto;border:3px #3366ff solid;padding:5px;}
.content_title{display:relative;text-align:left;font-size:10pt;font-weight:bold;color:#0066cc;}
.content_info{display:relative;margin-top:10px;text-align:left;font-size:9pt;color:#555555;}
.content_space{display:relative;width:100%;height:5px;}
.register_form{display:relative;margin:0 auto;width:790px;height:326px;border:1px #c0c0c0 solid;background-color:#f0f0f0;font-size:9pt;padding:3px;}
.register_form ul {position:relative;margin:0;padding:0;vertical-align:middle;width:100%;}
.register_form ul li {float:left;list-style:none;}
.tit{width:120px;text-align:left;padding-top:3px;font-weight:bold;}
.cont{width:260px;text-align:left;}
.contline{margin-top:3px; margin-bottom:3px;width:100%;height:1px;background-color:#c0c0c0;}
.inbox_sel{width:220px; height:20px;vertical-align:middle;font-size:9pt;}
.inbox_txt{width:220px; height:14px;vertical-align:middle;font-size:9pt;}
.control_area{display:relative;margin-top:20px;width:100%;height:30px;vertical-align:middle;text-align:center;}
</style>
</head>
<body>
<div id="wrap">
	<div id="contents">
		<div class="content_title">> 전화승인등록</div>
		<div class="content_info">전화등록은 백업단말기를 이용하여 승인하신 경우 조회 시스템에만 등록하는 메뉴 입니다.<br>자료만 등록되는 메뉴로 실제 승인/취소와 무관하게 등록됩니다.</div>
		<div class="content_space"></div>
		<form name="regdata" method="post" action="proc/trans_add_card.php" target="subproc">
		<div class="register_form">
			<ul>
				<li class="tit">■ VAN구분(필수)</li>
				<li class="cont">
					<SELECT NAME="VANGB" id="VANGB" class="inbox_sel">
						<OPTION VALUE="">벤사구분</OPTION>
						<OPTION VALUE="02">KICC</OPTION>
						<OPTION VALUE="03">KTFC</OPTION>
						<OPTION VALUE="04">KIS</OPTION>
						<OPTION VALUE="06">NICE</OPTION>
					</SELECT>
				</li>
				<li class="tit">■ 거래일련번호</li>
				<li class="cont">
					<input type="text" name="TRANIDX" id="TRANIDX" value="" class="inbox_txt">
				</li>
			</ul>
			<ul>
				<li class="contline"></li>
			</ul>
			<ul>
				<li class="tit">■ 대사상태(필수)</li>
				<li class="cont">
					<SELECT NAME="STAT_DIFF" id="STAT_DIFF" class="inbox_sel">
						<OPTION VALUE="">대사선택</OPTION>
						<OPTION VALUE="P">중계거래</OPTION>
						<OPTION VALUE="V" selected>실시간거래</OPTION>
						<OPTION VALUE="E">대사완료</OPTION>
					</SELECT>
				</li>
				<li class="tit">■ 거래상태(필수)</li>
				<li class="cont">
					<SELECT NAME="STAT_STP" id="STAT_STP" class="inbox_sel">
						<OPTION VALUE="">::거래상태::</OPTION>
						<OPTION VALUE="0" selected>거래(매입대상)</OPTION>
						<OPTION VALUE="1">매입요청</OPTION>
						<OPTION VALUE="2">매입완료</OPTION>
						<OPTION VALUE="3">정산완료</OPTION>
						<OPTION VALUE="4">거래취소</OPTION>
					</SELECT>
				</li>
			</ul>
			<ul>
				<li class="contline"></li>
			</ul>
			<ul>
				<li class="tit">■ 거래구분(필수)</li>
				<li class="cont">
					<SELECT NAME="APPGB" id="APPGB" class="inbox_sel">
						<OPTION VALUE="">::거래구분::</OPTION>
						<OPTION VALUE="A">승인</OPTION>
						<OPTION VALUE="C">취소</OPTION>
					</SELECT>
				</li>
				<li class="tit">■ 카드종류(필수)</li>
				<li class="cont">
					<SELECT NAME="CHECK_CARD" id="CHECK_CARD" class="inbox_sel">
						<OPTION VALUE="N">일반카드</OPTION>
						<OPTION VALUE="Y">체크카드</OPTION>
					</SELECT>
				</li>
			</ul>
			<ul>
				<li class="contline"></li>
			</ul>
			<ul>
				<li class="tit">■ 입력구분</li>
				<li class="cont">
					<SELECT NAME="ENTRYMD" id="ENTRYMD" class="inbox_sel">
						<OPTION VALUE="">::keyin구분::</OPTION>
						<OPTION VALUE="S">SWIPE</OPTION>
						<OPTION VALUE="K">KEYIN</OPTION>
					</SELECT>
				</li>
				<li class="tit">■ 카드사선택(필수)</li>
				<li class="cont">
					<SELECT NAME="ACQ_CD" id="ACQ_CD" class="inbox_sel">
						<option value=''>:: 전체 ::</option>
						<option value='1106,026,VC0006' >비씨카드</option>
						<option value='2211,018,VC0030' >농협카드</option>
						<option value='1101,016,VC0001' >국민카드</option>
						<option value='1104,031,VC0004' >삼성카드</option>
						<option value='1105,008,VC0005' >하나카드</option>
						<option value='1103,047,VC0003' >롯데카드</option>
						<option value='1102,027,VC0002' >현대카드</option>
						<option value='1107,029,VC0007' >신한카드</option>
					</SELECT>
				</li>
			</ul>
			<ul>
				<li class="contline"></li>
			</ul>
			<ul>
				<li class="tit">■ 단말기번호(필수)</li>
				<li class="cont">
					<input type="text" name="TID" id="TID" value="" class="inbox_txt">
				</li>
				<li class="tit">■ 가맹점번호(필수)</li>
				<li class="cont">
					<input type="text" name="MID" id="MID" value="" class="inbox_txt">
				</li>
			</ul>
			<ul>
				<li class="contline"></li>
			</ul>
			<ul>
				<li class="tit">■ 카드번호(필수)</li>
				<li class="cont">
					<input type="text" name="CARDNO" id="CARDNO" value="" class="inbox_txt">
				</li>
				<li class="tit">■ 할부기간(필수)</li>
				<li class="cont">
					<input type="text" name="HALBU" id="HALBU" value="" class="inbox_txt">
				</li>
			</ul>
			<ul>
				<li class="contline"></li>
			</ul>
			<ul>
				<li class="tit">■ 판매금액(필수)</li>
				<li class="cont">
					<input type="text" name="AMT_UNIT" id="AMT_UNIT" value="" class="inbox_txt">
				</li>
				<li class="tit">■ 봉사료</li>
				<li class="cont">
					<input type="text" name="AMT_TIP" id="AMT_TIP" value="" class="inbox_txt">
				</li>
			</ul>
			<ul>
				<li class="contline"></li>
			</ul>
			<ul>
				<li class="tit">■ 세금</li>
				<li class="cont">
					<input type="text" name="AMT_TAX" id="AMT_TAX" value="" class="inbox_txt">
				</li>
				<li class="tit">■ 합계금액(필수)</li>
				<li class="cont">
					<input type="text" name="AMOUNT" id="AMOUNT" value="" class="inbox_txt">
				</li>
			</ul>
			<ul>
				<li class="contline"></li>
			</ul>
			<ul>
				<li class="tit">■ 승인번호(필수)</li>
				<li class="cont">
					<input type="text" name="APPNO" id="APPNO" value="" class="inbox_txt">
				</li>
				<li class="tit">■ 원승인번호(필수)</li>
				<li class="cont">
					<input type="text" name="OAPPNO" id="OAPPNO" value="" class="inbox_txt">
				</li>
			</ul>
			<ul>
				<li class="contline"></li>
			</ul>
			<ul>
				<li class="tit">■ 승인일자(필수)</li>
				<li class="cont">
					<input type="text" name="APPDD" id="APPDD" value="" class="inbox_txt">
				</li>
				<li class="tit">■ 승인시간</li>
				<li class="cont">
					<input type="text" name="APPTM" id="APPTM" value="" class="inbox_txt">
				</li>
			</ul>
			<ul>
				<li class="contline"></li>
			</ul>
			<ul>
				<li class="tit">■ 원승인일자</li>
				<li class="cont">
					<input type="text" name="OAPPDD" id="OAPPDD" value="" class="inbox_txt">
				</li>
				<li class="tit">■ 원승인시간</li>
				<li class="cont">
					<input type="text" name="OAPPTM" id="OAPPTM" value="" class="inbox_txt">
				</li>
			</ul>
			<ul>
				<li class="contline"></li>
			</ul>
			<ul>
				<li class="tit">■ 등록번호</li>
				<li class="cont">
					<input type="text" name="ADD_CID" id="ADD_CID" value="" class="inbox_txt">
				</li>
				<li class="tit">■ 진료구분</li>
				<li class="cont">
					<input type="text" name="ADD_GB" id="ADD_GB" value="" class="inbox_txt">
				</li>
			</ul>
			<ul>
				<li class="contline"></li>
			</ul>
			<ul>
				<li class="tit">■ 진료과</li>
				<li class="cont">
					<input type="text" name="ADD_CD" id="ADD_CD" value="" class="inbox_txt">
				</li>
				<li class="tit">■ 수납자</li>
				<li class="cont">
					<input type="text" name="ADD_CASHER" id="ADD_CASHER" value="" class="inbox_txt">
				</li>
			</ul>
			<ul>
				<li class="contline"></li>
			</ul>
		</div>
		<div class="control_area">
			<span class="button large icon" onclick="formchk();"><span class="add"></span><a href="#">승인등록</a></span>
			<span class="button large icon" onclick="document.regdata.reset();"><span class="refresh"></span><a href="#">다시작성</a></span>
			<span class="button large icon" onclick="parent.window.close();"><span class="delete"></span><a href="#">창닫기</a></span>
		</div>
		</form>
	</div>
</div>
<iframe name="subproc" style="width:0px; height:0px; border:0;" scrolling="no"></iframe>