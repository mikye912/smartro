<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>Init from script</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" type="text/css" href="./../dhtmlx/codebase/dhtmlx.css"/>
	<link rel="stylesheet" type="text/css" href="./../dhtmlx/dhtmlxGrid/codebase/skins/dhtmlxgrid_dhx_web.css"/>
	<link type="text/css" rel="stylesheet" href="./../include/css/style.css"  media="all" >
	<script src="./../dhtmlx/codebase/dhtmlx.js"></script>
	<script src="./../include/js/jquery-1.8.1.min.js" type="text/javascript"></script>
	<script src="./../include/js/common.js"></script>
	<script src="./../include/js/dhtmlxgrid_export.js"></script>
	<script src='./../include/js/jquery.base64.js'></script>
	<script>
		var AccLayout, accountGrid;
		function doOnLoad() {
			
			var h = $(window).height();
			var w = $(window).width();

			var hset	= (h-230) + "px";

			document.getElementById("parentId").style.height = hset;
			AccLayout.setSizes();
			
						
			$("#parentId").css("min-height","600px").css("min-width","860px");
			$(".dhx_cell_cont_layout").css("width","100%");

		}

		$(window).resize(function(){
			var h = $(document).height();
			var w = $(document).width();
			
			$(".dhx_cell_layout, .dhx_cell_cont_layout").css("width","100%").css("min-width","1160px");
			$(".dhx_cell_cont_layout").css("border","0").css("width","100%").css("border-top","1px solid #c0c0c0").css("border-bottom","1px solid #c0c0c0");
			$(".cont_title").css("margin","0 2px").css("min-width","550px");
			
			var hset	= (h-230) + "px";
			document.getElementById("parentId").style.height = hset;
		});

		var D = 0;
		function setSens(id, k) {
			myCalendar.setSensitiveRange(null,null);
		}

		function byId(id) {
			return document.getElementById(id);
		}

/* 	function srvTime(){
		var xmlHttp;

		if (window.XMLHttpRequest) {//분기하지 않으면 IE에서만 작동된다.
			xmlHttp = new XMLHttpRequest(); // IE 7.0 이상, 크롬, 파이어폭스 등
			xmlHttp.open('HEAD',window.location.href.toString(),false);
			xmlHttp.setRequestHeader("Content-Type", "text/html");
			xmlHttp.send('');
			return xmlHttp.getResponseHeader("Date");
		}else if (window.ActiveXObject) {
			xmlHttp = new ActiveXObject('Msxml2.XMLHTTP');
			xmlHttp.open('HEAD',window.location.href.toString(),false);
			xmlHttp.setRequestHeader("Content-Type", "text/html");
			xmlHttp.send('');
			return xmlHttp.getResponseHeader("Date");
		}
	} */

</script>
<style>
	span.label {
		font-family: Tahoma;
		font-size: 12px;
	}
	.hdrcell{text-align:center;font-weight:bold;}
	.schtitle{font-size:9pt;}
	.dhx_cell_hdr{border:0px solid #ffffff;}
	.dhx_cell_cont_layout{border:0px solid #ffffff;}
	

	table.tb {border-collapse: collapse;}
	table.tb>tr>td,
	table.tb>tr>th,
	table.tb>tbody>tr>td,
	table.tb>tbody>tr>th {border:1px solid #cccccc; padding:5px;}

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
</head>
<body onload="doOnLoad();"  style="padding:5px;">
<div class='sub_content'>
	<div class='sub_content_space'></div>
	<div class='cont_title'>
	<form>
		<table class="tb" border="1" bordercolor="#e6e6e6" cellpadding="5" style="width: 100%; border-collapse: collapse;">
			<colgroup>
			<col class="cellC" style="width:100px;">
			<col class="cellL" style="*">
			<col class="cellC" style="width:100px;">
			</colgroup>
			<tbody>
			<tr height="36">				
				<td>
					<span class='schtitle'>입력</span>
				</td>
				<td>
					<textarea name="qry" id="qry" style="width:100%; height:150px;"></textarea>
				</td>
				<td><img src="./images/btn/btn_search.gif" onclick='search_go();' style='cursor:pointer;'></td>
			</tr>
			</tbody>
		</table>
	</form>
	</div>

	<div id="parentId" style="position: relative; top: 0px; left: 0px; width: 100%; height:300px;"></div>
</div>
<script type="text/javascript">
	AccLayout = new dhtmlXLayoutObject("parentId", "1C");
	AccLayout.setSkin("dhx_web");	
	AccLayout.cells("a").hideHeader();	
	
	accountGrid = AccLayout.cells("a").attachGrid();
	accountGrid.setImagePath("./dhtmlx/dhtmlxGrid/codebase/imgs/");
	accountGrid.setSkin("dhx_web");	
	
	accountGrid.enableColumnAutoSize(true);
	accountGrid.enableSmartRendering(true);

	function search_go(){
		var qry	= $('#qry').val();
		AccLayout.items[0].progressOn();
		accountGrid.clearAndLoad("./ifou_xml_was/sql_db2.jsp?qry="+$.base64.btoa(qry), afterload_prog01(), "xml");
	}
	
	function afterload_prog01(){
		AccLayout.items[0].progressOff();
	}
</script>
<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>
	