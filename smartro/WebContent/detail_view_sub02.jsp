<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.*, java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="java.lang.String" %>
<%@ page import="java.security.*" %>
<%@ page import="java.util.Base64.Encoder" %>
<%@ page import="java.util.Base64.Decoder" %>
<%@ page import="java.util.Date" %>
<%@page import="org.json.simple.JSONObject"%>
<%@page import="org.json.simple.JSONArray"%>
<%@page import="org.json.simple.parser.JSONParser"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<%
	Decoder decoder = Base64.getDecoder();
	String udata =  request.getParameter("udata");
	
	byte[] udata_decode = decoder.decode(udata);
	String udata_json = new String(udata_decode, "UTF-8");
	
	String tuser = (String)session.getAttribute("uinfo");
	
	JSONParser jsonParse = new JSONParser();
	JSONObject jsonObj = (JSONObject) jsonParse.parse(udata_json);
	JSONArray itemArray = (JSONArray) jsonObj.get("ITEMS");
	JSONObject itemObj = (JSONObject) itemArray.get(0); 
	
	//fields, aligns, colTypes, sorts, colWidth, amtset
	String[] pageColumn = jbset.get_page_column(tuser, "van");
	String fields = pageColumn[0];
	String aligns = pageColumn[1];
	String colTypes = pageColumn[2];
	String sorts = pageColumn[3];
	String colWidth = pageColumn[4];
	int amtset = Integer.parseInt(pageColumn[5]);
	
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
	<script>
		var myCalendar;
		var AccLayout, accountGrid;
		function doOnLoad() {
			
			var h = $(window).height();
			var w = $(window).width();

			var hset	= (h-130) + "px";

			document.getElementById("parentId").style.height = hset;
			AccLayout.setSizes();
			
			
		/*2014 09 25 유병현추가*/
			$("#parentId").css("min-height","800px").css("min-width","860px");
			$(".dhx_cell_cont_layout").css("width","100%");
			/*2014 09 25 유병현추가*/

		}
			/*2014 09 25 유병현추가*/
		$(window).resize(function(){
				var h = $(document).height();
			var w = $(document).width();
			
			$(".dhx_cell_layout, .dhx_cell_cont_layout").css("width","100%").css("min-width","1160px");
			$(".dhx_cell_cont_layout").css("border","0").css("width","100%").css("border-top","1px solid #c0c0c0").css("border-bottom","1px solid #c0c0c0");
			$(".cont_title").css("margin","0 2px").css("min-width","550px");
			
			var hset	= (h-190) + "px";
			document.getElementById("parentId").style.height = hset;
		});
		 var D = 0;
		function setSens(id, k) {
			myCalendar.setSensitiveRange(null,null);
		}
		function byId(id) {
			return document.getElementById(id);
		}

		function detail_view(obj){
			var lVal = (screen.aWidth - 700) / 2;
			var tVal = (screen.availHeight - 450) / 2;

			var options = {
				height: 450, // sets the height in pixels of the window.
				width: 700, // sets the width in pixels of the window.
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

			nWindow = window.open("./detail_view_van.jsp?udata=<%=udata%>","_blank",parameters);
			nWindow.opener = self;
		}
	</script>
	<style>
		input#stime, input#etime {
			font-size: 9pt;
			background-color: #fafafa;
			border: #c0c0c0 1px solid;
			width: 100px;
		}
		span.label {
			font-family: Tahoma;
			font-size: 12px;
		}
		.hdrcell{text-align:center;font-weight:bold;}
		.totaldata{text-align:right;font-weight:none;height:24px;background-color:#f0f0f0;padding-top:8px;}
		.schtitle{font-size:9pt;}

		.pop_sub_content{min-height:100%;  margin-top:-107px; padding:3px;}
	</style>
</head>
<body onload="doOnLoad();">
<div class='pop_sub_content'>
	<div class='sub_content_space'></div>
	<div class="control_div">
		<table class="tb00_none" width="100%">
			<tr>
				<td colspan="2">Total Rows : <span id="grid_total_rows"></span>건</td>
			</tr>
		</table>
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
	accountGrid.setHeader("<%=fields%>");
	accountGrid.setInitWidths("<%=colWidth%>");
	accountGrid.setColTypes("<%=colTypes%>");
	accountGrid.setColAlign("<%=aligns%>");
	accountGrid.setColSorting("<%=sorts%>");
	accountGrid.setNumberFormat("0,000",<%=amtset%>,".",",");
	accountGrid.init();

	AccLayout.items[0].progressOn();
	var w	= "uauth=<%=tuser%>&cardno=<%=itemObj.get("MEDI_GOODS")%>";
	accountGrid.clearAndLoad("./ifou_xml_was/item_0204.jsp?"+w, "json");	
	
	accountGrid.attachEvent("onXLE", function() {
		AccLayout.items[0].progressOff();
		var count=accountGrid.getRowsNum();
		$('#grid_total_rows').html(count);
	}); 

</script>
<iframe name="subq" id="subq" style="width:0px; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>