﻿﻿﻿﻿﻿﻿﻿
$(document).ready(function(){
	
	currentMenu(); //메뉴 리셋
	giwanList();	//기관 리셋
	faq();			//faq메뉴 리셋
	ui();			//ui메뉴 리셋
	selectgiwanList();	//기관 리셋
	$("#user_sch").focus();	//기관검색 인풋에 포커스
	$("#veri1").focus();	//원본확인 첫번째 인풋에 포커스
	
	$(".mNav li").mouseover(function(){	//메인슬라이드메뉴 마우스오버시
		var idx = $(this).index();	//마우스오버된 객체의 인덱스
		motionMenu(idx);	//인덱스를 인자로 해당함수 실행
	});
	
	$(".btn_search").click(function(){	//클래스명이 btn_search인 버튼 클릭시 기관검색 함수 실행
		searchGiwan();
	});
	
	$("#user_sch").keydown(function(evnt){	//기관검색 인풋에 엔터이벤트 발생시 기관검색 함수 실행
		if(evnt.keyCode == 13){
			searchGiwan();
		}
	});
	
	$(".btn_search2").click(function(){	//클래스명이 btn_search인 버튼 클릭시 기관검색 함수 실행
		searchGiwan2();
	});
	
	$("#user_sch2").keydown(function(evnt){	//기관검색 인풋에 엔터이벤트 발생시 기관검색 함수 실행
		if(evnt.keyCode == 13){
			searchGiwan2();
		}
	});
	
	$(".cMenu>li").click(function(){	//faq메뉴 클릭시
		
		var idx = $(this).index();		//클릭한 객체의 인덱스를 알아온다
		
		var target = $(this);			//클릭한 객체를 타겟으로 지정
		var val = target.hasClass("cur"); 	//cur 클래스를 갖고있는지 판단
		
		if(val == false){	//갖고 있지 않을경우
			
			$(".info").hide();	//답변을 감춘다.
			$(".cur").removeClass("cur");	//현재 펼쳐져있는 답변을 찾아 cur클래스를 삭제한다.
			
			target.addClass("cur");	//타겟에 cur클래스 추가
			faq();	//faq함수 실행
		}
	});

	$(".uMenu>li").click(function(){	//faq와 동일
		
		var idx = $(this).index();
		
		var target = $(this);
		var val = target.hasClass("cur"); 
		
		if(val == false){
			
			$(".info").hide();
			$(".cur").removeClass("cur");
			
			target.addClass("cur");
			ui();
		}
	});
		
	$(".more").click(function(){	//클래스명이 more인 객체 클릭시
		
		$(this).next()	//클릭한 객체의 답변을 보여준다
			   .show();
			   
		$(".more").not(this)	//클래스명이 more인 객체를 제외하고 펼쳐져있던 답변을 감춘다.
			   	  .next()
			   	  .hide();
	});
	
	//인풋,textarea,select에 포커스,블러 시 보더색 변경
	$("input").focus(function(){
		
		$(this).css("border-color","#008ae4");
	});
	
	$("input").blur(function(){
		
		$(this).css("border-color","#d6d6d6");
		
	});
	
	$("textarea").focus(function(){
		
		$(this).css("border-color","#008ae4");
	});
	
	$("textarea").blur(function(){
		
		$(this).css("border-color","#d6d6d6");
		
	});

	$("select").focus(function(){
		
		$(this).css("border-color","#008ae4");
	});
	
	$("select").blur(function(){
		
		$(this).css("border-color","#d6d6d6");
		
	});
	
	$(".fSite").change(function(){	//패밀리사이트 변경시 새창으로 해당 url로 이동
		
		var val = $(this).val();

		if(val != ""){
			
			window.open(val,"_blank");
		}
	});
	
	$(".veri>input[type=text]").keyup(function(event){	//원본확인 인풋에 키보드 이벤트 발생시
		
		var idx = $(this).index();	//4개의 인풋태그중 몇번째인지 체크한다. 

		//if(idx != 4){//4번째가 아닐 경우
			
		//	var target = "textfield" + (idx + 1);	//target변수에 현재 인풋의 다음 인풋 
		//	checklength(target, 4, $(this).attr("name"));	//인풋값의 길이 체크
		
		//}else{
			
		//	executeCommand(event);
		//}
	});
});

function faq(){
	
	var idx = $(".cur").index();	//클래스명이 ".cur"인 dom객체를 찾아 인덱스번호를 담는다.
	
	$(".tbl03").each(function(){	//tbl03이 클래스인 테이블들에 반복수행
		
		var targetIdx = $(this).index() - 3;	//먼저나온 객체들의 차이로 인덱스 번호를 맞춰준다.
		
		if(idx == targetIdx){	//인덱스가 일치하면 보이고
			
			$(this).show();
		
		}else{//인덱스가 일치하지 않으면 감춘다
			
			$(this).hide();
		}
	});
}

function ui(){	//faq와 동일
	
	var idx = $(".cur").index();
	
	$(".ui").each(function(){
		
		var targetIdx = $(this).index() - 3;
		
		if(idx == targetIdx){
			
			$(this).show();
		
		}else{
			
			$(this).hide();
		}
	});
}

function motionMenu(idx){	
	
	$(".mNav li").each(function(){	//메인슬라이드메뉴의 각각 반복 수행
			
		var curIdx = $(this).index();	//현재 인덱스를 알아온다
		
		if(curIdx == idx){	//인자로 받은 인덱스와 비교해 같으면

			$(this).stop()								//css의 width값을 578로 10의 속도로 애니메이트
				   .animate({width:578},10,"")
				   .children()
				   .addClass("current");				//자식노드에 current클래스 추가
				   
		}else{
			
			$(this).stop()								//css의 width값을 149로 10의 속도로 애니메이트
				   .animate({width:149},10,"")
				   .children()
				   .removeClass("current");				//자식노드에 current클래스 삭제
		}
	});
	
	currentMenu();	//슬라이드 함수 실행
}

function currentMenu(){
	
	$(".mNav li a").each(function(){ //클래스명이 mNav의 li의 a에 대해서 반복수행
		
		if($(this).hasClass("current")){	//current클래스를 갖고 있으면
			
			$(this).stop()
			       .animate({left:-149},200,"");	//css의 left값을 -149까지 200의 속도로 애니메이트
			
		}else{	//current클래스를 갖고 있지 않으면
			
			$(this).stop()
			       .animate({left:0},200,"");//css의 left값을 0까지 200의 속도로 애니메이트
		}
	});
}

function giwanList(){
	
	$.get("/medcerti_portal/xml/hospital.xml",function(xml){	//기관 xml 파싱
		
		$(xml).find("SubItem")	//xml에서 노드명이 SubItem인 자식을 찾아 아래의 코드 반복
		      .each(function(){
		      	
		      		var targetPart = "";
		      		
		      		var init = $(this).children()	//5번째 자식을 init변수에 담는다.(이니셜)
		      						  .eq(10)
		      						  .text();
		      		
		      		switch(init){	//이니셜에 따라 타겟 지정
		      			
		      			case "ㄱ" :
		      			case "ㄴ" :
		      			case "ㄷ" :
		      				
		      				targetPart = ".list1";
		      				break;
		      				
		      			case "ㄹ" :
		      			case "ㅁ" :
		      			case "ㅂ" :
		      			
		      				targetPart = ".list2";
		      				break;
		      				
		      			case "ㅅ" :
		      			case "ㅇ" :
		      			case "ㅈ" :
		      			
		      				targetPart = ".list3";
		      				break;
		      				
		      			case "ㅊ" :
		      			case "ㅋ" :
		      			case "ㅌ" :
		      			
		      				targetPart = ".list4";
		      				break;
		      				
		      			case "ㅍ" :
		      			case "ㅎ" :
		      			
		      				targetPart = ".list5";
		      				break;
		      				
		      			default :
		      			
		      				targetPart = ".list6";
		      		}
		      		
		      		var dd = $("<dd>").appendTo(targetPart);	//dd태그생성후 이니셜로 찾은 타겟에 자식으로 추가
		      		
		      		var txt = $(this).children()	//0번째 자식의 택스트를 알아와 담는다.
			      					 .eq(0)
			      		   			 .text();
			      			
	      			var service = $(this).children()	//4번째 자식을 찾아와 service변수에 담는다 (sso 체크)
	      						         .eq(9)
	      						         .text();
	      						 
			      	var targetStr1 = $(this).children()	//첫번째 url
			      	                        .eq(3)
			      	                        .text();
			      	                        
			      	var targetStr2 = $(this).children()	//두번째 유알엘
			      	                        .eq(2)
			      	                        .text();
			      						 
			      	var targetUrl = "";
			      	
			      	if(service == "Y"){	//sso아닐 경우의 url
			      		
			      		targetUrl = targetStr1 + "&GIWANNO=" + targetStr2;
			      		
			      	}else{	//sso일경우
			      		
			      		targetUrl = targetStr1;
			      	}
			      	
			      	if(service == "X"){
			      		$("<text>").text(txt)
			      				   .appendTo(dd)
			      				   .css("color","#BDBDBD");
			      	}else {
			      		$("<a>").attr("href",targetUrl)	    //a태그 생성후 하이퍼링크 값에 targetUrl을 넣고 타겟을 새창으로 한다 targetTxt를
	      					.attr("target","_blank")	//txt를 택스트로 한뒤 처음 생성한 dd태그에 자식으로 추가
	      					.text(txt)
	      					.appendTo(dd);
			      	}
		      });
	});
}

function selectgiwanList(){
	
	$.get("/medcerti_portal/xml/hospital.xml",function(xml){	//기관 xml 파싱
		
		$(xml).find("SubItem")	//xml에서 노드명이 SubItem인 자식을 찾아 아래의 코드 반복
		      .each(function(){
		      	
		      		var txt = $(this).children()	//0번째 자식의 택스트를 알아와 담는다.
			      					 .eq(0)
			      		   			 .text();
			      			
			      	var giwanno = $(this).children()	//두번째 유알엘
			      	                        .eq(2)
			      	                        .text();
			      	
			      	giwanno	= giwanno + "^" + txt;
			      	
			      	var option = $('<option value="'+giwanno+'" >'+txt+'</option>').appendTo('#user_select');
			      	
		      		//$("<a>").attr("href","gogiwan("+targetStr2+")")	    //a태그 생성후 하이퍼링크 값에 targetUrl을 넣고 타겟을 새창으로 한다 targetTxt를
      				//	.attr("target","_blank")	//txt를 택스트로 한뒤 처음 생성한 dd태그에 자식으로 추가
      				//	.text(giwanno)
      				//	.appendTo(dd);
		      });
	});
}

function searchGiwan(){	//기관 검색 함수
	
	$(".result").empty();	//클래스명이 "result"인 dom객체의 자식을 모두 삭제한다.
		
	var searchTxt = $("#user_sch").val();	//아이디가 "#user_sch" 인풋의 value값을 searchTxt변수에 담는다.
	
	if(searchTxt.length < 2) {
		$("<li>").text("기관명은 두글자 이상 입력해 주세요.")
		 .addClass("widPlus")
  		 .appendTo(".result");
		return;
	}
	
	if(searchTxt != ""){	//searchTxt의 값이 공백이 아닐때
		
		$.get("/medcerti_portal/xml/hospital.xml",function(xml){	//기관 xml 파싱
			
			$(xml).find("SubItem")	//xml에서 노드명이 "SubItem"인 자식노드를 찾아 아래의 코드를 반복수행
			      .each(function(){
			      	
			      		var targetTxt = $(this).children()	//0번째 자식의 텍스트를 targetTxt변수에 담는다
			      			   				   .eq(0)
			      			   				   .text();
			      		
			      		if(targetTxt.indexOf(searchTxt) != -1){	//targetTxt의 값으로 searchTxt변수에서 일치하는 스트링값이 있을경우
			      			
			      			var li = $("<li>").appendTo(".result");	//<li> 생성 후 클래스명이 "result"인 dom객체에 자식으로 추가
			      			
			      			var service = $(this).children()	//4번째 자식노드의 값을 service 변수에 담는다. (sso 구분)
			      						         .eq(9)
			      						         .text();
			      						 
					      	var targetStr1 = $(this).children()	//3번째 자식노드의 값을 targetStr1에 담는다. (첫번째 url)
					      	                        .eq(3)
					      	                        .text();
					      	                        
					      	var targetStr2 = $(this).children() //2번째 자식노드의 값을 targetStr2에 담는다. (두번째url)
					      	                        .eq(2)
					      	                        .text();
					      						 
					      	var targetUrl = "";	
					      	
					      	if(service == "Y"){	//sso학교가 아닐 경우
					      		
					      		targetUrl = targetStr1 + "&GIWANNO=" + targetStr2; //두가지 url에 "&GIWANNO"를 합쳐 targetUrl에 담는다.
					      		
					      	}else{	//sso 학교일 경우
					      		
					      		targetUrl = targetStr1;	//첫번째 url만 담는다.
					      	}
					      	
					      	if(service == "X"){
					      		$("<text>").text(targetTxt)
					      				   .appendTo(li)
					      				   .css("color","#BDBDBD");
					      	}else {
						      	$("<a>").attr("href",targetUrl)	    //a태그 생성후 하이퍼링크 값에 targetUrl을 넣고 타겟을 새창으로 한다 targetTxt를
						      			.attr("target","_blank")	//targetTxt를 택스트로 한뒤 처음 생성한 li태그에 자식으로 추가
						      			.text(targetTxt)
						      			.appendTo(li);
					      	}
			      		}
			      });
			
			if($(".result").children().size() == 0){	//일치하는 값이 없을 경우
				
				$("<li>").text("검색어와 일치하는 기관이 없습니다.")
						 .addClass("widPlus")
				   		 .appendTo(".result");
			}
		});
	}else{	// 공백일 경우
		
		$("<li>").text("병원명을 입력하고 검색버튼을 눌러주세요")
		         .appendTo(".resultArea");
	}
}

function searchGiwan2(){	//기관 검색 함수
	
	$(".result2").empty();	//클래스명이 "result"인 dom객체의 자식을 모두 삭제한다.
		
	var searchTxt = $("#user_sch2").val();	//아이디가 "#user_sch" 인풋의 value값을 searchTxt변수에 담는다.
	
	if(searchTxt.length < 2) {
		$("<li>").text("기관명은 두글자 이상 입력해 주세요.")
		 .addClass("widPlus2")
  		 .appendTo(".result2");
		return;
	}
	
	if(searchTxt != ""){	//searchTxt의 값이 공백이 아닐때
		
		var onegiwanne	= "";
		var onegiwanno	= "";
		var i			= 1;
		
		$.get("/medcerti_portal/xml/hospital.xml",function(xml){	//기관 xml 파싱
			
			$(xml).find("SubItem")	//xml에서 노드명이 "SubItem"인 자식노드를 찾아 아래의 코드를 반복수행
			      .each(function(){
			      	
			      		var giwanne = $(this).children()	//0번째 자식의 텍스트를 targetTxt변수에 담는다
			      							 .eq(0)
			      							 .text();
			      		
			      		if(giwanne.indexOf(searchTxt) != -1){	//targetTxt의 값으로 searchTxt변수에서 일치하는 스트링값이 있을경우
			      			
			      			var li = $("<li>").appendTo(".result2");	//<li> 생성 후 클래스명이 "result"인 dom객체에 자식으로 추가
				      			
					      	var giwanno = $(this).children() //2번째 자식노드의 값을 targetStr2에 담는다. (두번째url)
					      						 .eq(2)
					      						 .text();
					      	
					      	if(giwanno == ""){
					      		$("<text>").text(giwanne)
					      				   .appendTo(li)
					      				   .css("color","#BDBDBD");
					      	}else {
					      		$("<a>").attr("href","javascript:giwanclick('"+giwanno+"','2','"+giwanne+"');")	    //a태그 생성후 하이퍼링크 값에 targetUrl을 넣고 타겟을 새창으로 한다 targetTxt를
				      					.text(giwanne)
				      					.appendTo(li);
					      	}
					      	
					      	i			= i+1;
					      	onegiwanne	= giwanne;
					      	onegiwanno	= giwanno;
			      		}
			      });
			
			if($(".result2").children().size() == 0){	//일치하는 값이 없을 경우
				
				$("<li>").text("검색어와 일치하는 기관이 없습니다.")
						 .addClass("widPlus2")
				   		 .appendTo(".result2");
			}
			
			if($(".result2").children().size() == 1){
				$(".rArea2 li").css("font-weight", "bold");
				giwanclick(onegiwanno, 2, onegiwanne);
			}
			
			var menu_ok; 
			$(".rArea2 li").click(function() { 
				var choice = $(this).index(); 
				$(".rArea2 li").each(function(idx) { 
					if(idx == choice) { 
						$(this).removeClass('no').addClass('ok'); 
						menu_ok = choice; 
					} else { 
						$(this).removeClass('ok').addClass('no'); 
					} 
				}); 
			});
			
		});
	}else{	// 공백일 경우
		
		$("<li>").text("병원명을 입력하고 검색버튼을 눌러주세요")
		         .appendTo(".resultArea");
	}
}

function giwanclick(giwanno, num, giwanne) {
	if(num == "1") {
		var x	= document.forms[0].selectgiwan.value.split("^");
		giwanne	= x[1];
		document.forms[0].GIWANNO.value	= x[0];
	}else {
		document.forms[0].GIWANNO.value	= giwanno;
	}
	
	if(document.forms[0].GIWANNO.value == "") {
	}else {
		document.forms[0].giwantxt.value	= giwanne;
		//alert("선택 되었습니다. 문서확인번호를 입력해주세요.");
		document.forms[0].textfield.focus();
	}
}

function pop(u,w,h){ //팝업창 생성
	
	var url = "https://www.medcerti.com/hp1.0/jsp/function/etc/" + u;
	var opt = "width=" + w + "," + "height=" + h + ",scrollbars = yes";
	
	window.open(url,"",opt);
}

function form_submit() {	//원본확인 폼 검사
	
	var msg = "";
	var x   = document.forms[0];
	
	if(x.GIWANNO.value == "") {
		alert("기관을 선택해 주세요.");
		return;
	}
	
	if(x.textfield.value=='' ||  x.textfield.value.length < 4){ //스트링값이 공백이거나 4글자 밑일경우
	
		x.textfield.focus();	//포커스 준 뒤 함수 종료
		return;
	}
	
	if(x.textfield2.value=='' ||  x.textfield2.value.length < 4 ){
	
		x.textfield2.focus();
		return;
	}
	
	if(x.textfield3.value=='' ||  x.textfield3.value.length < 4 ){
	
		x.textfield3.focus();
		return;
	}
	
	if(x.textfield4.value=='' ||  x.textfield4.value.length < 4 ){
	
		x.textfield4.focus();
		return;
	}
	
	x.DOC_NO.value = x.textfield.value + x.textfield2.value + x.textfield3.value + x.textfield4.value ; //원본확인 4가지 값을 합쳐 변수에 담는다.
	
	//문서번호 특수기호 숫자 체크
	if (!specialkeyPWD(x.DOC_NO.value)){	//합친 값을 스페셜키 검사 함수로 체크 있을경우 아래코드 실행
	
		x.textfield.focus();
		x.textfield.value  = "";
		x.textfield2.value = "";
		x.textfield3.value = "";
		x.textfield4.value = "";
		msg = "문서번호에 특수문자가 들어갈 수 없습니다.";
		
		//csscody.alert('<p style="font-family:dotum,tahoma;font-weight:bold;font-size:13px; margin-top:10px;">'+msg+'</p>');
	    alert(msg);
	    
	    return ; 
	}
	
	var temppwd1 = document.forms[0].DOC_NO.value;
	
	for (i = 0; i < temppwd1.length; i++){
	
	    if (!((temppwd1.charAt (i) >= "0" && temppwd1.charAt (i) <= "9") || (temppwd1.charAt (i) >= "A" && temppwd1.charAt (i) <="Z") ||
	        (temppwd1.charAt (i) >= "a" && temppwd1.charAt (i) <= "z") || temppwd1.charAt (i) == "-" )) {	//0~9,A~Z,a~z의 값이 아닐경우
	       
	        x.textfield.value  = "";
	        x.textfield2.value = "";
	        x.textfield3.value = "";
			x.textfield4.value = "";
	    		
    		x.textfield.focus();
            
            return ;
        }
    }
        
    document.forms[0].action = "https://www.medcerti.com/servlet/HPVERIFY?COMMAND=VERIFYACTIVEX&GIWANNO="+x.GIWANNO.value;
    //document.forms[0].action = "http://211.232.75.150:8080/servlet/HPVERIFY?COMMAND=VERIFYACTIVEX&GIWANNO="+x.GIWANNO.value;
	window.open('', 'verify', 'width=620,height=600, scrollbars=yes');	//새창에 받은값으로 원본확인 실행
	document.forms[0].submit();
	
}
	
function checklength(nextfield,chars,currfield) {

	var  x	= document.forms[0][currfield].value.length;	

	if (x == chars) {// 체크하는 인풋의 value값의 길이가 4일 경우 다음 인풋에 포커스

    	eval('document.forms[0].' + nextfield + '.focus();');
  	}

}

function specialkeyPWD(str){

	var SpecialChar = "`~!@#$%^&*()_+=|\{}[];':,./<>?";
	var chknum = 0;

	for(i=0; i<str.length; i++){ //str의 스트링 길이 만큼 반복수행

		for(j=0; j<SpecialChar.length; j++){ //담아둔 특수문자의 길이만큼 반복수행

			if(str.charAt(i) == SpecialChar.charAt(j)){  //str과 특수문자가 같은경우 chknum++

				chknum++;
			}
		}
	}
	
	if(chknum > 0){ //특수 문자가 있을경우
		
		return false;
	
	}else{	//특수문자가 없을경우
		
		return true;
	}
}

function executeCommand(event){
    
    if (event.keyCode == 13){ //엔터이벤트일경우
    
    	form_submit();//실행
   
    }else{
     
        return ;
    }
}
