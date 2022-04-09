<script>
	mygrid = new dhtmlXGridObject('gridbox');
	mygrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	mygrid.setSkin("dhx_skyblue");

	var fields   = "����,����θ�,�����,�����ڵ�,�����ڵ�,";
		fields	+= "�ܸ����ȣ,�ܸ��ⱸ��,����,�Ե�,�Ｚ,";
		fields	+= "��ȯ,BC,����,����,����";

	var filters  = "&nbsp;,#select_filter,#text_filter,#text_filter,#select_filter,";
		filters	+= "#text_filter,#select_filter,#text_filter,#text_filter,#text_filter,";
		filters	+= "#text_filter,#text_filter,#text_filter,#text_filter,#text_filter";

	var aligns	 = "center,left,left,center,center,";
		aligns	+= "center,center,center,center,center,";
		aligns	+= "center,center,center,center,center";

	var colTypes  = "ro,ed,ed,ed,ed,";
		colTypes += "ed,ed,ed,ed,ed,";
		colTypes += "ed,ed,ed,ed,ed";

	var sorts	 = "int,str,int,str,int,";
		sorts	+= "int,str,str,str,str,";
		sorts	+= "str,str,str,str,str";

	var colWidth	 = "60,120,120,*,*,";
		colWidth	+= "*,*,*,*,*,";
		colWidth	+= "*,*,*,*,*";
	
mygrid.setHeader(fields);
mygrid.setInitWidths(colWidth);
mygrid.attachHeader(filters);
mygrid.setColTypes(colTypes);
mygrid.setColAlign(aligns);
mygrid.setColSorting(sorts);
mygrid.enableSmartRendering(true);
mygrid.enableMultiselect(true);
mygrid.init();

mygrid.loadXML("./xmlparse/sub01_01xml.php?<?=$AddWhere?>");

mygrid.attachEvent("onXLS", function() {
    $('#text_box').show();
});
mygrid.attachEvent("onXLE", function() {
    $('#text_box').hide();

	var count=mygrid.getRowsNum();
	$('#mygrid_ttcnt').html(count);
});
</script>