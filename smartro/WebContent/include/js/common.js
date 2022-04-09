$(document).ready(function(){
	$("ul.subnav").parent().append("<span></span>")
	$("ul.topnav li a").click(function() {
		$(this).parent().find("ul.subnav").slideDown('fast').show();
		$(this).parent().hover(function() {
		}, function(){	
			$(this).parent().find("ul.subnav").slideUp('slow');
		});

		}).hover(function() { 
			$(this).addClass("subhover");
		}, function(){
			$(this).removeClass("subhover");
	});
});

function search_open(){//div 열기
	document.getElementById('searchbox_on_layer').style.display='';
	document.getElementById('searchbox_off_layer').style.display='none';
	document.getElementById('searchbox_close').style.display='';
	document.getElementById('searchbox_open').style.display='none'; 
}

function search_close(){//div 닫기
	document.getElementById('searchbox_on_layer').style.display='none';
	document.getElementById('searchbox_off_layer').style.display='';
	document.getElementById('searchbox_close').style.display='none'; 
	document.getElementById('searchbox_open').style.display='';
}

function loadData(z){
	document.regular.sto_sel.options[0].selected=true;
	var tmp = z.options[z.selectedIndex].value;
	subq.location.href = "./include/loaddata.php?tmp="+tmp; 
}

function loadData2(z){
	document.regular.sto_sel.options[0].selected=true;
	var tmp = z.options[z.selectedIndex].value;
	subq.location.href = "./include/loaddata2.php?tmp="+tmp; 
}

function search_check(){
	var f = document.regular;
	f.submit();
}

function exceldn(obj1,url_query){	


	
	if ( obj1 ==  "0102_01" || obj1 ==  "0202_01" )
	{
		var syear	= $('#syear option:selected').val();
		var smon	= $('#smon option:selected').val();
		var cardno	= $('#card_no').val();
		var appno	= $('#approval_no').val();

		var w	= "syear="+syear+"&smon="+smon+"&cardno="+cardno+"&appno="+appno;
	}
	else if ( obj1 == "0105_01" )
	{
		var stime	= $('#stime').val();
		var cardno	= $('#card_no').val();
		var appno	= $('#approval_no').val();

		var w	= "stime="+stime+"&cardno="+cardno+"&appno="+appno;
	}
	else
	{
		var stime	= $('#stime').val();
		var etime	= $('#etime').val();

		var cardno	= $('#card_no').val();
		var appno	= $('#approval_no').val();

		var w	= "stime="+stime+"&etime="+etime+"&cardno="+cardno+"&appno="+appno;
	}

	if ( url_query != "" )
	{
		var w	= url_query;
	
	}
	
	var exsrc	= "./proc/excel"+obj1+".php?"+w;

	$('#subq').attr('src', exsrc);
}

function exceldownload(obj1, obj2){
	var exsrc	= "./excel/excel"+obj1+".php?"+obj2;
	alert(obj2);
	$('#subq').attr('src', exsrc);
}

function data_insert_on(){ 
	var obj = document.getElementById("data_insert_box"); 
	obj.style.width = document.body.scrollWidth + 'px'; 
	obj.style.height = document.body.scrollHeight + 'px'; 
	obj.style.visibility = "visible"; 
}

function date_insert_off(){
	var obj = document.getElementById("data_insert_box"); 
	obj.style.visibility = "hidden"; 
}

function datesam(){
	var f = document.regular;
	var t = f.stime.value;
	f.etime.value=t;
}

function commify(n) {
  var reg = /(^[+-]?\d+)(\d{3})/;   // 정규식
  n += '';                          // 숫자를 문자열로 변환

  while (reg.test(n))
    n = n.replace(reg, '$1' + ',' + '$2');

  return n;
}

function getDateDiff(date1,date2){
	var arrDate1 = date1.split("-");
	var getDate1 = new Date(parseInt(arrDate1[0]),parseInt(arrDate1[1])-1,parseInt(arrDate1[2]));
	var arrDate2 = date2.split("-");
	var getDate2 = new Date(parseInt(arrDate2[0]),parseInt(arrDate2[1])-1,parseInt(arrDate2[2]));
	
	var getDiffTime = getDate1.getTime() - getDate2.getTime();
	
	return Math.floor(getDiffTime / (1000 * 60 * 60 * 24));
}

function leadingZeros(n, digits) {
    var zero = '';
    n = n.toString();
    if (digits > n.length) {
        for (var i = 0; digits - n.length > i; i++) {
            zero += '0';
        }
    }
    return zero + n;
}

function set_comma(n) {
	var reg = /(^[+-]?\d+)(\d{3})/;
	n += '';
	while (reg.test(n))
	 n = n.replace(reg, '$1' + ',' + '$2');

	return n;
}
function GetNumString(s) {
	var rtn = parseFloat(s.replace(/,/gi, ""));
	if (isNaN(rtn)) {
		return 0;
	}
	else {
		return rtn;
	}
}
function sumColumn(ind){
	var out = 0;
	for(var i=0;i<accountGrid.getRowsNum();i++){
		out+= parseFloat(GetNumString(accountGrid.cells2(i,ind).getValue()))
	}
	return out;
}
function sumIncome(indPrice,indQuant){
	var out = 0;
	for(var i=0;i<accountGrid.getRowsNum();i++){
		out+= parseFloat(accountGrid.cells2(i,indPrice).getValue())*parseFloat(accountGrid.cells2(i,indQuant).getValue())
	}
	return out;
}

function detail_pop_view(url, w, h){
	var lVal = (screen.aWidth - w) / 2;
	var tVal = (screen.availHeight - h) / 2;

	var options = {
		height: h, // sets the height in pixels of the window.
		width: w, // sets the width in pixels of the window.
		toolbar: 0, // determines whether a toolbar (includes the forward and back buttons) is displayed {1 (YES) or 0 (NO)}.
		scrollbars: 0, // determines whether scrollbars appear on the window {1 (YES) or 0 (NO)}.
		status: 0, // whether a status line appears at the bottom of the window {1 (YES) or 0 (NO)}.
		resizable: 0, // whether the window can be resized {1 (YES) or 0 (NO)}. Can also be overloaded using resizable.
		left: lVal, // left position when the window appears.
		top: tVal, // top position when the window appears.
		center: 1, // should we center the window? {1 (YES) or 0 (NO)}. overrides top and left
		createnew: 0, // should we create a new window for each occurance {1 (YES) or 0 (NO)}.
		location: 0, // determines whether the address bar is displayed {1 (YES) or 0 (NO)}.
		menubar: 0 // determines whether the menu bar is displayed {1 (YES) or 0 (NO)}.
	};

	var parameters = "location=" + options.location +
					 ",menubar=" + options.menubar +
					 ",height=" + options.height +
					 ",width=" + options.width +
					 ",toolbar=" + options.toolbar +
					 ",scrollbars=" + options.scrollbars +
					 ",status=" + options.status +
					 ",resizable=" + options.resizable +
					 ",left=" + options.left +
					 ",screenX=" + options.left +
					 ",top=" + options.top +
					 ",screenY=" + options.top;
	nWindow = window.open(url,"_blank",parameters);
	nWindow.opener = self;
}