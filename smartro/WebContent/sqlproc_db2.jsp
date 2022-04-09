<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<!DOCTYPE html>
<html>
<head>
	<title>Init from script</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="./include/js/jquery-1.8.1.min.js" type="text/javascript"></script>
<script src="./include/js/common.js"></script>
<style>
	.print{
		width : 80%;
		height : 30px;
		border: none;
		white-space : normal;
		word-wrap:break-word;
		font-size: 15px;
		padding : 10px;
		cursor : pointer;
	}
	
	input[type="SUBMIT"]{
		width: 60px;
		height: 30px;
	}
</style>
</head>
<body>
<input type="hidden" class="print" id="query" value="" readonly="readonly"/>
<input type="hidden" class="print" id="count" value="" readonly="readonly"/>
<input type="hidden" class="print" id="error" value="" readonly="readonly"/>
<form method="post" target="subq" style="padding:10px;" >
	<textarea name="qry" id="qry" style="width:80%; height:150px;"></textarea>
	<input type="SUBMIT" value="RUN" onclick='search_go();'>
</form>
<script>
	function search_go(){
		var inqry = document.getElementById('query');
		var inmessage = document.getElementById('error');
		var incount = document.getElementById('count');
		var qry = document.getElementById('qry');

		var seturl = "./ifou_xml_was/sqlproc_db2.jsp?qry="+qry.value;
		
		$.getJSON(seturl,
			{
				tags: "mount rainier", 
				tagmode: "any", 
				format: "json"
			},
			function(data){
				if(data["rs"] != null){
					$('#count').attr('value',"변동된 로우 갯수 : "+data["count"]);
					incount.type="";
					inmessage.type="hidden";
				}else if(data["error"] != null){
					$('#error').attr('value', data["error"]);
					inmessage.type="";
					incount.type="hidden";
				}
				$('#query').attr('value',qry.value);
				inqry.type="";
				qry.value="";
			}
		)
	}
</script>
<iframe name="subq" style="width:0; height:0px; border:0px solid #3366cc;" frameborder=0></iframe>
</body>
</html>
	