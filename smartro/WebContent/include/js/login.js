function CheckForm(){
	var f = document.loginform;

	if(f.userid.value == ""){
		alert('아이디를 입력하여 주십시오.');
		f.userid.focus();
		return false;
	}

	if(f.userpw.value==""){
		alert('비밀번호를 입력하여 주십시오.');
		f.userpw.focus();
		return false;
	}

	if(f.midchk.value=="N"){
		alert('아이디를 입력하여 주십시오.');
		
		return false;
	}

	if(f.pwchk.value=="N"){
		alert('비밀번호를 입력하여 주십시오.');
		
		return false;
	}

	f.submit();
	
}
function def_focus(){
	document.loginform.userid.focus();
}

function gidchange(){
	var f=document.loginform;
	if(f.gidchk.value=="N"){
		f.gid.value="";
		f.gidchk.value="Y";
	}
}


function midchange(){
	var f=document.loginform;
	if(f.midchk.value=="N"){
		f.userid.value="";
		f.midchk.value="Y";
	}
}

function midcheck(){
	var f=document.loginform;
	if(f.userid.value==""){
		f.userid.value="아이디";
		f.midchk.value="N";
	}
}

function pwchange(){
	var f=document.loginform;
	if(f.pwchk.value=="N"){
		f.userpw.value="";
		f.userpw.type="password";
		f.pwchk.value="Y";
	}
}

function pwcheck(){
	var f=document.loginform;
	if(f.userpw.value==""){
		f.userpw.type="text";
		f.userpw.value="비밀번호";
		f.pwchk.value="N";
	}
}