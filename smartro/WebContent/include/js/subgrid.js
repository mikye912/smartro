//�ŷ����� ����׸���
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

var sfields    = "����,�ŷ�����,�ŷ�����,������ڵ�,����θ�,";
	sfields	 += "�����ڵ�,�����,�����ڵ�,�ܸ����ȣ,��������ȣ,";
	sfields	 += "ī���ȣ,ī����,ī������,�ݾ�,����������,";
	sfields	 += "�Աݿ�����,�ҺαⰣ,��������,���νð�,���ι�ȣ,";
	sfields	 += "����������,û������,��������,����ڵ�,���԰��,";
	sfields	 += "�Աݿ�����,����ȣ,�������"

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


//���庰 ���� �׸���
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

	var sfields    = "����θ�,�����,�����ڵ�,���ΰǼ�,���αݾ�,";
		sfields	  += "��ҰǼ�,��ұݾ�,�հ�Ǽ�,�հ�ݾ�";

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


//���Գ��� ���� �׸���
function depogrid(depcd, stocd, skey, stime, ekey, etime, acqcd, cardno, appno, tid, mid, authgb){

	var schadd	= "depcd="+depcd+"&stocd="+stocd+"&skey="+skey+"&stime="+stime+"&ekey="+ekey+"&etime="+etime+"&acqcd="+acqcd+"&cardno="+cardno+"&appno="+appno+"&tid="+tid+"&mid="+mid+"&agb="+authgb;	
	$('#a_tabbar').attr("style", "height:300px;");
	tabbar.setSize();
	$('#degrid').show();
	$('#detail_grid').show();

	mygrids = new dhtmlXGridObject('detail_grid');
	mygrids.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	mygrids.setSkin("light");

	var sfields   = "����,�ŷ�����,�ŷ�����,�����ڵ�,�����,";
		sfields	 += "�ܸ����ȣ,��������ȣ,ī���ȣ,ī����,ī������,";
		sfields	 += "�ݾ�,����������,�Աݿ�����,�ҺαⰣ,��������,";
		sfields	 += "���νð�,���ι�ȣ,����������,���Կ�û��,�Աݿ�����,";
		sfields	 += "���԰��,���԰���޽���";

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