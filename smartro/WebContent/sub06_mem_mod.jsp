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
	
	String depcd = request.getParameter("depcd");
	String orgcd = request.getParameter("orgcd");
	String memcd = request.getParameter("memcd");
	
	String userData[] = jbset.get_060505_item_userInfo(memcd);
%>
<!DOCTYPE html>
<html>
<head>
	<title>IFOU 행복정산 서비스</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" type="text/css" href="./dhtmlx/codebase/dhtmlx.css"/>
	<link rel="stylesheet" type="text/css" href="./dhtmlx/dhtmlxGrid/codebase/skins/dhtmlxgrid_dhx_web.css"/>
	<link rel="stylesheet" type="text/css" href="./dhtmlx/dhtmlxTabbar/codebase/skins/dhtmlxtabbar_dhx_terrace.css"/>
	<link type="text/css" rel="stylesheet" href="./include/css/style.css"  media="all" >
	<script src="./dhtmlx/codebase/dhtmlx.js"></script>
	<script src="./include/js/jquery-1.8.1.min.js" type="text/javascript"></script>
	<script src="./include/js/common.js"></script>
	<script src="./include/js/dhtmlxgrid_export.js"></script>
	<style>
	input#stime, input#etime {font-size: 9pt;background-color: #fafafa;border: #c0c0c0 1px solid;width: 100px;}
	span.label {font-family: Tahoma;font-size: 12px;}
	table.tb {border-collapse: collapse;}
	table.tb>tr>td,
	table.tb>tr>th,
	table.tb>tbody>tr>td,
	table.tb>tbody>tr>th {border:1px solid #cccccc; padding:5px;}

	.hdrcell{text-align:center;font-weight:bold;}
	.totaldata{text-align:right;font-weight:none;height:24px;background-color:#f0f0f0;padding-top:8px;}
	.schtitle{font-size:9pt;}
	.dhx_cell_hdr{border:0px solid #ffffff;}
	.dhx_cell_cont_layout{border:0px solid #ffffff;}
	.cellC {
		color:#333333;
		background:#f6f6f6;
		font:12px tahoma;
		width:120px;
		text-align:left;
		padding-left:10px;
		padding-top:7px;
		font-weight:bold;
		letter-spacing:-1;
	}
	.cellL {padding-right:6px;padding-left:10px;}
	.cellR {text-align:right;padding-left:10px;}
	</style>
	<script>
		function close_go(){
			parent.ipopup_close();
		}

		function form_go(){
			var f = document.f;

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

			/*
			if(!post_check($('#mem_pw').val())){
				return false;
			}
			*/

			if($('#mem_nm').val()==""){
				alert('사용자명을 입력하여 주십시오.');
				return false;
			}

			if(confirm('선택하신 사용자정보를 수정하시겠습니까?')==true){
				f.submit();
			}
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
<div style="width:100%; height:320px; overflow:auto;">
<table style="width:100%; height:40px;">
<tr>
	<td style="font-size:16pt; font-weight:bold; color:#6fa5fd;">사용자정보 수정</td>
</tr>
<tr>
	<td style="height:1px; background-color:#6fa5fd;"></td>
</tr>
<tr>
	<td style="height:10px;"></td>
</tr>
</table>
<form name="f" method="post" action="./proc/sub06_mem_mod_update.jsp">
<input type="hidden" name="orgcd" value="<%=orgcd%>">
<input type="hidden" name="memcd" value="<%=memcd%>">
	<table class="tb" border="1" bordercolor="#e6e6e6" cellpadding="5" style="width: 100%; border-collapse: collapse;">
		<colgroup>
		<col style="width:130px; background-color:#f0f0f0;">
		<col>
		</colgroup>
		<tr>
			<td>사업부선택</td>
			<td><%=userdepo %></td>
		</tr>
		<tr>
			<td>아이디</td>
			<td><input type="text" name="mem_id" id="mem_id" value="<%=userData[1]%>"></td>
		</tr>
		<tr>
			<td>비밀번호</td>
			<td><input type="password" name="mem_pw" id="mem_pw" value=""></td>
		</tr>
		<tr>
			<td>사용자명</td>
			<td><input type="text" name="mem_nm" id="mem_nm" value="<%=userData[3]%>" style="width:200px;"></td>
		</tr>
		<tr>
			<td>그룹선택</td>
			<td>
			<select name="memlv" id="memlv">
				<option>그룹선택</option>
				<option value="S">사용자그룹</option>
				<option value="M">마스터그룹</option>
			</select>
			</td>
		</tr>
		<tr>
			<td>전화번호</td>
			<td><input type="text" name="mem_tel1" value="<%=userData[5]%>"></td>
		</tr>
		<tr>
			<td>핸드폰번호</td>
			<td><input type="text" name="mem_tel2" value="<%=userData[6]%>"></td>
		</tr>
		<tr>
			<td>이메일</td>
			<td><input type="text" name="mem_email" value="<%=userData[7]%>"></td>
		</tr>
	</table>
</form>
</div>
<table width="100%" height="50px">
	<tr>
		<td align="center" style="background-color:#f0f0f0; padding-top:5px;">
			<img src="./images/pop_btn/btn_ok.png" onclick="javascript:form_go();">
			<img src="./images/pop_btn/btn_close.png" onclick="javascript:close_go();">
		</td>
	</tr>
</table>
<script>
	$("#memlv").val("<%=userData[4]%>").attr("selected", true);
	$("#depcd").append("<option value='M'>통합관리자</option>");
</script>
</body>
<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</html>