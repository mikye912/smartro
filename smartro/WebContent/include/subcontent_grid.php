<script>
	function jstest(obj){
		var w = window.document.body.offsetWidth;
		var obj2 = document.getElementById("detail_grid"); 
			obj2.style.width = (w-520) + 'px';
			tabbar.setSize();
			
			$('#add_grid').attr("style", "width:490px;");
			$('#add_grid').attr("style", "height:300px;");	
			
		var addwhere	= document.getElementById("schadd").value;
			$('#addctrn').attr("src", "./proc/adddata.html?selcd="+obj+"&"+addwhere);
			$('#add_grid').show();

	}

	function addDataClose(){
		$('#add_grid').attr("style", "width:0px;");
		$('#add_grid').hide();

		var w = window.document.body.offsetWidth;
		var obj2 = document.getElementById("detail_grid"); 
			obj2.style.width = (w-20) + 'px';
			tabbar.setSize();
	}

	function detail_grid_reload(obj){
		var schadd = document.getElementById("schadd").value; 
		//alert(schadd);
		mygrids.updateFromXML("./xmlparse/sub_detail.php?"+schadd);
		mygrid.refresh();
		mygrid2.refresh();
		mygrid3.refresh();
		mygrid4.refresh();
		mygrid5.refresh();
		mygrid6.refresh();
		mygrid7.refresh();
		mygrid8.refresh();
	}
</script>
<script src="./include/js/subgrid.js" type="text/javascript"></script>
<div id="sub2grid" style="width:100%; margin-bottom:30px; height:300px; z-index:40; display:none;">
	<?=$Obj->ControlAreaSet('detail2_grid', 'N','N','detail02');?>
	<div id="detail2_grid" style="width:100%; height:300px; z-index:40; display:none;"></div>
</div>

<div style="height:20px; width:100%;"></div>
<div id="degrid" style="width:100%; margin-bottom:20px; display:none;">
	<?=$Obj->ControlAreaSet('detail_grid', '<?=$AuthChk[C]?>','Y','detail01');?>
	<div id="detail_grid" style="width:100%; height:300px; z-index:40; display:none; float:left;"></div>
	<div id="add_grid" style="width:0px; height:300px; z-index:40; overflow-x:hidden;">
	<iframe id="addctrn" style="width:490px; height:300px; border:1px solid #A4BED4; padding:0px;"></iframe>
</div>