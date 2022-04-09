//거래내역 서브그리드
function DetailGridClose(){

	if(document.getElementById('detail_grid').style.display=="none"){
		$('detail_grid').attr("style", "height:500px;");
		tabbar.setSize();
	}

	$('#detail_grid').hide();
	$('#degrid').hide();
	
}

function sub2gridClose(){
	DetailGridClose();

	$('#a_tabbar').attr("style", "height:500px;");
	tabbar.setSize();
	$('#detail2_grid').hide();
	$('#sub2grid').hide();
	
}

function detailgrid(depcd, stocd, datekey, stime, etime, acqcd, cardno, appno, tid, mid, authgb, diffgb){


	var schadd	= "depcd="+depcd+"&stocd="+stocd+"&datekey="+datekey+"&stime="+stime+"&etime="+etime+"&acqcd="+acqcd+"&cardno="+cardno+"&appno="+appno+"&tid="+tid+"&mid="+mid+"&agb="+authgb+"&diffgb="+diffgb;	
	$('#a_tabbar').attr("style", "height:300px;");
	tabbar.setSize();
	$('#degrid').show();
	$('#detail_grid').show();
	$('#qry02').val(schadd);


//		document.getElementById('qry02').value=schadd;

	mygrids = new dhtmlXGridObject('detail_grid');
	mygrids.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	mygrids.setSkin("dhx_skyblue");

var sfields    = "순번,거래상태,거래구분,사업부코드,사업부명,";
	sfields	 += "매장코드,매장명,점포코드,단말기번호,가맹점번호,";
	sfields	 += "카드번호,카드사명,카드종류,금액,예정수수료,";
	sfields	 += "입금예정액,할부기간,승인일자,승인시간,승인번호,";
	sfields	 += "원승인일자,청구일자,응답일자,결과코드,매입결과,";
	sfields	 += "입금예정일,계약번호,영업사원"

var sfilters  = "&nbsp;,#select_filter,#select_filter,#select_filter,#select_filter,";
	sfilters	+= "#text_filter,#text_filter,#text_filter,#text_filter,#text_filter,";
	sfilters	+= "#text_filter,#select_filter,#select_filter,#text_filter,#text_filter,";
	sfilters	+= "#text_filter,#text_filter,#text_filter,#text_filter,#text_filter,";
	sfilters += "#text_filter,#text_filter,#text_filter,#select_filter,#text_filter,";
	sfilters	+= "#text_filter,#text_filter,#text_filter";

var saligns	 = "center,center,center,center,left,"
	saligns	+= "center,left,center,left,left,";
	saligns	+= "center,center,center,right,right,";
	saligns	+= "right,center,center,center,center,";
	saligns	+= "center,center,center,center,center,";
	saligns  += "center,center,center";

var scolTypes  = "ed,ed,ed,ed,ed,";
	scolTypes += "ed,ed,ed,ed,ed,";
	scolTypes += "ed,ed,ed,ed,ed,";
	scolTypes += "ed,ed,ed,ed,ed,";
	scolTypes += "ed,ed,ed,ed,ed,";
	scolTypes += "ed,ed,ed";

var ssorts	 = "int,str,str,str,str,";
	ssorts	+= "str,str,str,str,str,";
	ssorts	+= "str,str,str,str,int,";
	ssorts	+= "str,str,str,str,str,";
	ssorts	+= "str,str,str,str,str,";
	ssorts	+= "str,str,str";
	
var scolWidth	 = "40,60,60,70,80,";
	scolWidth	+= "80,140,80,80,80,";
	scolWidth	+= "110,80,80,100,100,";
	scolWidth	+= "100,60,80,80,80,";
	scolWidth	+= "80,60,80,80,80,";
	scolWidth	+= "80,80,80";

	mygrids.setHeader(sfields);
	mygrids.setInitWidths(scolWidth);
	mygrids.attachHeader(sfilters);
	mygrids.setColTypes(scolTypes);
	mygrids.setColAlign(saligns);
	mygrids.setColSorting(ssorts);
	mygrids.enableSmartRendering(true);
	mygrids.enableMultiselect(true);
	mygrids.init();

	mygrids.loadXML("./xmlparse/sub_detail.php?"+schadd);

	document.getElementById('schadd').value=schadd;

	var dp = new dataProcessor("./proc/subgrid_proc.php");
	dp.init(mygrids);

	dp.defineAction("error", function(tag) {
		alert(tag.firstChild.nodeValue);
		return true;
	});
}

function detailadd(){

}


//매장별 서브 그리드
function det_dep2sto(depcd, stocd, datekey, stime, etime, acqcd, cardno, appno, tid, mid, authgb, diffgb){

	var schadd	= "depcd="+depcd+"&stocd="+stocd+"&datekey="+datekey+"&stime="+stime+"&etime="+etime+"&acqcd="+acqcd+"&cardno="+cardno+"&appno="+appno+"&tid="+tid+"&mid="+mid+"&agb="+authgb+"&diffgb="+diffgb;	
	$('#a_tabbar').attr("style", "height:300px;");
	tabbar.setSize();
	$('#sub2grid').show();
	$('#detail2_grid').show();
	$('#qry03').val(schadd);
	
	mygrids = new dhtmlXGridObject('detail2_grid');
	mygrids.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	mygrids.setSkin("dhx_skyblue");

	var sfields    = "사업부명,매장명,매장코드,승인건수,승인금액,";
		sfields	  += "취소건수,취소금액,합계건수,합계금액";

	var sfilters  = "#select_filter,#select_filter,#text_filter,#text_filter,#text_filter,";
		sfilters	+= "#text_filter,#text_filter,#text_filter,#text_filter";

	var saligns	 = "left,left,center,right,right,"
		saligns	+= "right,right,right,right";

	var scolTypes  = "ed,ed,ed,ed,price,";
		scolTypes += "ed,price,ed,price";

	var ssorts	 = "str,str,str,str,str";
		ssorts	+= "str,str,str,str";
		
	var scolWidth	 = "200,200,100,*,*,";
		scolWidth	+= "*,*,*,*";

	mygrids.setHeader(sfields);
	mygrids.setInitWidths(scolWidth);
	mygrids.attachHeader(sfilters);
	mygrids.setColTypes(scolTypes);
	mygrids.setColAlign(saligns);
	mygrids.setColSorting(ssorts);
	mygrids.enableSmartRendering(true);
	mygrids.enableMultiselect(true);
	mygrids.init();

	mygrids.loadXML("./xmlparse/dep2storegrid.php?"+schadd);
}


//매입내역 서브 그리드
function depogrid(depcd, stocd, skey, stime, ekey, etime, acqcd, cardno, appno, tid, mid, authgb){

	var schadd	= "depcd="+depcd+"&stocd="+stocd+"&skey="+skey+"&stime="+stime+"&ekey="+ekey+"&etime="+etime+"&acqcd="+acqcd+"&cardno="+cardno+"&appno="+appno+"&tid="+tid+"&mid="+mid+"&agb="+authgb;	
	$('#a_tabbar').attr("style", "height:300px;");
	tabbar.setSize();
	$('#degrid').show();
	$('#detail_grid').show();

	mygrids = new dhtmlXGridObject('detail_grid');
	mygrids.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	mygrids.setSkin("light");

	var sfields   = "순번,거래상태,거래구분,매장코드,매장명,";
		sfields	 += "단말기번호,가맹점번호,카드번호,카드사명,카드종류,";
		sfields	 += "금액,예정수수료,입금예정액,할부기간,승인일자,";
		sfields	 += "승인시간,승인번호,원승인일자,매입요청일,입금예정일,";
		sfields	 += "매입결과,매입결과메시지";

	var sfilters  = "&nbsp;,#select_filter,#select_filter,#select_filter,#text_filter,";
		sfilters += "#text_filter,#text_filter,#text_filter,#text_filter,#text_filter,";
		sfilters += "#text_filter,#text_filter,#text_filter,#text_filter,#text_filter,";
		sfilters += "#text_filter,#text_filter,#text_filter,#text_filter,#text_filter,";
		sfilters += "#text_filter,#text_filter";

	var saligns	 = "center,center,center,center,left,"
		saligns	+= "center,left,left,center,center,";
		saligns	+= "right,right,right,center,center,";
		saligns	+= "center,center,center,center,center,";
		saligns	+= "center,center";

	var scolTypes  = "ed,ed,ed,ed,ed,";
		scolTypes += "ed,ed,ed,ed,ed,";
		scolTypes += "ed,ed,ed,ed,ed,";
		scolTypes += "ed,ed,ed,ed,ed,";
		scolTypes += "ed,ed";

	var ssorts	 = "int,str,str,str,str,";
		ssorts	+= "str,str,str,str,str,";
		ssorts	+= "str,str,str,str,int,";
		ssorts	+= "str,str,str,str,str,";
		ssorts	+= "str,str";
		
	var scolWidth	 = "60,60,80,80,200,";
		scolWidth	+= "110,150,80,80,100,";
		scolWidth	+= "100,100,100,80,100,";
		scolWidth	+= "100,100,100,100,100,";
		scolWidth	+= "100,160";

	mygrids.setHeader(sfields);
	mygrids.setInitWidths(scolWidth);
	mygrids.attachHeader(sfilters);
	mygrids.setColTypes(scolTypes);
	mygrids.setColAlign(saligns);
	mygrids.setColSorting(ssorts);
	mygrids.enableSmartRendering(true);
	mygrids.enableMultiselect(true);
	mygrids.init();

	mygrids.loadXML("./xmlparse/depo_detail.php?"+schadd);
}