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
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
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
	<script>
		function regi_form(){
			var f = document.memreg;

			if($('#depcd option:selected').val()==""){
				alert('사업부를 선택하여 주십시오.');
				return false;
			}

			if($('#mem_id').val()==""){
				alert('아이디를 입력하여 주십시오.');
				return false;
			}

			if($('#mem_pw').val()==""){
				alert('비밀번호를 입력하여 주십시오.');
				return false;
			}

			//비밀번호 유효성 검사
			/*
			if(!post_check($('#mem_pw').val())){
				return false;
			}*/

			if($('#mem_nm').val()==""){
				alert('사용자명을 입력하여 주십시오.');
				return false;
			}
			f.submit();
		}

		function reset_form(){
			var f = document.memreg;
			f.reset();
		}

	function post_check(obj)
	{

		// 비밀번호(패스워드) 유효성 체크 (문자, 숫자, 특수문자의 조합으로 6~16자리)
		if(obj.length<6) {
			alert("비밀번호는 영문,숫자,특수문자(!@$%^&* 만 허용)를 사용하여 6~16자까지, 영문은 대소문자를 구분합니다.");
			return false;
		}

		if(!obj.match(/([a-zA-Z0-9].*[!,@,#,$,%,^,&,*,?,_,~])|([!,@,#,$,%,^,&,*,?,_,~].*[a-zA-Z0-9])/)) {
			alert("비밀번호는 영문,숫자,특수문자(!@$%^&* 만 허용)를 사용하여 6~16자까지, 영문은 대소문자를 구분합니다.");
			return false;
		}

		//if(ObjUserID.value.indexOf(ObjUserPassword) > -1) {
		//  alert("비밀번호에 아이디를 사용할 수 없습니다.");
		//  return false;
		//}

		var SamePass_0 = 0; //동일문자 카운트
		var SamePass_1 = 0; //연속성(+) 카운드
		var SamePass_2 = 0; //연속성(-) 카운드

		for(var i=0; i < obj.length; i++) {
			var chr_pass_0 = obj.charAt(i);
			var chr_pass_1 = obj.charAt(i+1);

			//동일문자 카운트
			if(chr_pass_0 == chr_pass_1) {
				SamePass_0 = SamePass_0 + 1
			}

			var chr_pass_2 = obj.charAt(i+2);

			//연속성(+) 카운드
			if(chr_pass_0.charCodeAt(0) - chr_pass_1.charCodeAt(0) == 1 && chr_pass_1.charCodeAt(0) - chr_pass_2.charCodeAt(0) == 1) {
				SamePass_1 = SamePass_1 + 1
			}

			//연속성(-) 카운드
			if(chr_pass_0.charCodeAt(0) - chr_pass_1.charCodeAt(0) == -1 && chr_pass_1.charCodeAt(0) - chr_pass_2.charCodeAt(0) == -1) {
				SamePass_2 = SamePass_2 + 1
			}
		}
	
		if(SamePass_0 > 1) {
			alert("동일문자를 3번 이상 사용할 수 없습니다.");
			return false;
		}

		if(SamePass_1 > 1 || SamePass_2 > 1 ) {
			alert("연속된 문자열(123 또는 321, abc, cba 등)을\n 3자 이상 사용 할 수 없습니다.");
			return false;
		}
		return true;
	}
	</script>
</head>
<body>
<FORM name="memreg" method="post" action="./proc/sub060505proc.jsp">
<table width='100%' class='tb00_none'>
	<tr height="42">
		<td width="120">■ 사업부선택</td>
		<td>
			<!--  -->
			<%=userdepo %>
		</td>
		<td width="120">■ 아이디</td>
		<td>
			<input type="text" name="memid" id="mem_id" value="" class="searchbox_large">
		</td>
		<td width="120">■ 비밀번호</td>
		<td>
			<input type="password" name="mempw" id="mem_pw" value="" class="searchbox_large">
		</td>
		<td width="120">■ 사용자명</td>
		<td>
			<input type="text" name="memnm" id="mem_nm" value="" class="searchbox_large">
		</td>
		<td align="right">
			<span class="button large icon" onclick="regi_form();"><span class="add"></span><a href="#">신규등록</a></span>
		</td>
	</tr>
	<tr height="1"><td colspan="9" bgcolor="#c0c0c0"></td></tr>
	<tr height="42">
		<td width="120">■ 그룹선택</td>
		<td>
			<select name="memlv" class="searchbox_drop">
				<option>그룹선택</option>
				<option value="S">사용자그룹</option>
				<option value="M">마스터그룹</option>
			</select>
		</td>
		<td width="120">■ 전화번호</td>
		<td>
			<input type="text" name="memtel1" value="" class="searchbox_large">
		</td>
		<td width="120">■ 핸드폰번호</td>
		<td>
			<input type="text" name="memtel2" value="" class="searchbox_large">
		</td>
		<td width="120">■ 이메일</td>
		<td>
			<input type="text" name="mememail" value="" class="searchbox_large">
		</td>
		<td align="right">
			<span class="button large icon" onclick="reset_form();"><span class="check"></span><a href="#">다시작성</a></span>
		</td>
	</tr>
</table>
</form>
<script>
	$("#depcd").append("<option value='M'>통합관리자</option>");
</script>
</body>
</html>