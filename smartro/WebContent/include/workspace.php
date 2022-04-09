<? 
class WorkSpace extends Oci_API { 
	
	function member_get($mid){  

		$sql = "SELECT USER_ID, USER_PW, USER_NM, AUTH_SEQ, ORG_CD FROM TB_BAS_USER WHERE USER_ID='".$mid."'";

		parent::DBConnect(); 
		parent::SqlExec($sql);
		$mGet = parent::fetchInto($sql);
		parent::DBClose();

		return $mGet;

	}

	////  메뉴 셋업 함수
	function general_menu($obj, $orgcd){

		$sql	 = "SELECT ";
		$sql	.= "	A.PROGRAM_SEQ MENU_SEQ, ";
		$sql	.= "	B.PROGRAM_NAME MENU_NAME, ";
		$sql	.= "	B.DEPTH MENU_DEPTH, ";
		$sql	.= "	B.PARENT_SEQ PARENT_SEQ, ";
		$sql	.= "	A.ENABLE_READ AUTH_R, ";
		$sql	.= "	A.ENABLE_CREATE AUTH_C, ";
		$sql	.= "	A.ENABLE_UPDATE AUTH_U, ";
		$sql	.= "	A.ENABLE_DELETE AUTH_D, ";
		$sql	.= "	B.SRC_LOCATION MURL ";
		$sql	.= "FROM  ";
		$sql	.= "    TB_SYS_MENU A ";
		$sql	.= "LEFT OUTER JOIN ";
		$sql	.= "    (SELECT PROGRAM_SEQ, PROGRAM_NAME, PARENT_SEQ, DEPTH, SRC_LOCATION, SORT FROM TB_SYS_PROGRAM) B ";
		$sql	.= "ON (A.PROGRAM_SEQ=B.PROGRAM_SEQ) ";
		$sql	.= "WHERE A.AUTH_SEQ='".$obj."' AND ORGCD='".$orgcd."'";
		$sql	.= "ORDER BY B.SORT ASC";

		parent::DBConnect();
		parent::SqlExec($sql);
		$mGet	= parent::fetchInto($sql);

		for($i=0;$i<count($mGet);$i++){
			$rtn['MENU_SEQ'][$i]		= $mGet[$i]['MENU_SEQ'];
			$rtn['MENU_NAME'][$i]		= $mGet[$i]['MENU_NAME'];
			$rtn['MENU_DEPTH'][$i]		= $mGet[$i]['MENU_DEPTH'];
			$rtn['PARENT_SEQ'][$i]		= $mGet[$i]['PARENT_SEQ'];
			$rtn['AUTH_R'][$i]			= $mGet[$i]['AUTH_R'];
			$rtn['AUTH_C'][$i]			= $mGet[$i]['AUTH_C'];
			$rtn['AUTH_U'][$i]			= $mGet[$i]['AUTH_U'];
			$rtn['AUTH_D'][$i]			= $mGet[$i]['AUTH_D'];
			$rtn['MURL'][$i]			= $mGet[$i]['MURL'];
		}
		parent::DBClose();

		return $rtn;

	}

	function menu_setup($arr, $uauth){
		$key		= $this->arr_search($arr, "MENU_DEPTH", "0");
		$MenuSet	= "";
		for($i=0;$i<count($key);$i++){
			$MenuSet	.= "<li><a href='#'>".$arr['MENU_NAME'][$key[$i]]."</a><ul class='subnav'>";
									
			$key2	= $this->arr_search($arr, "PARENT_SEQ", $arr['MENU_SEQ'][$key[$i]]);
			for($o=0;$o<count($key2);$o++){
				if($arr['AUTH_R'][$key2[$o]]=="Y"){
					$MenuSet	.= "<li><a href='#' onclick=\"urlgo(-1, '".$arr['MENU_NAME'][$key2[$o]]."', '".$arr['MURL'][$key2[$o]]."')\">".$arr['MENU_NAME'][$key2[$o]]."</a></li>";
				}else{
					$MenuSet	.= "<li><a>".$arr['MENU_NAME'][$key2[$o]]."</a></li>";
				}
			}
			$MenuSet	.= "</ul></li>";
		}

		return $MenuSet;
	}

	function arr_search($array, $key, $value){ 
		$arr_cnt	= count($array[$key]);
		$j	= 0;
		for($i=0;$i<$arr_cnt;$i++){
			if($array[$key][$i]==$value){
				$rtn[$j]	= $i;
				$j++;
			}
		}
		return $rtn;
	}

	function menu_auth($arr, $key, $value){ 
		$arr_cnt	= count($arr[$key]);
		for($i=0;$i<$arr_cnt;$i++){
			if($arr[$key][$i]==$value){
				$rtn	= $arr['AUTH_R'][$i];
			}
		}

		if($rtn!="Y"){
			echo "<script>alert('접근하실 수 없는 메뉴 입니다.');history.go(-1);</script>";
		}

		return $rtn;
	}

	function menu_auth_all($arr, $key, $value){ 
		$arr_cnt	= count($arr[$key]);
		for($i=0;$i<$arr_cnt;$i++){
			if($arr[$key][$i]==$value){
				$rtn['R']	= $arr['AUTH_R'][$i];
				$rtn['C']	= $arr['AUTH_C'][$i];
				$rtn['U']	= $arr['AUTH_U'][$i];
				$rtn['D']	= $arr['AUTH_D'][$i];
			}
		}

		return $rtn;
	}

	function select_org($obj){
		$qry = "SELECT * FROM TB_BAS_ORG";
		parent::Dbconnect();
		parent::SqlExec($qry);
		$GetD	= parent::fetchInto($qry);
		parent::DBClose();

		$outStr	 = "<select name='selectorg' id='selectorg' onChange='siteChange(this)' style='font-size:9pt;'>";
		$outStr	.= "<option value=''>:: 법인선택 ::</option>";
		for($i=0;$i<count($GetD);$i++){
			$outStr	.= "<option value='".$GetD[$i]['ORG_CD']."' ".$this->selectValCheck($GetD[$i]['ORG_CD'], $obj).">".$GetD[$i]['ORG_NM']."</option>";
		}
		$outStr	.= "</select>";

		return $outStr;
	}

	function selectValCheck($val, $obj){
		if($obj==$val){
			$rtn = "selected";
		}else{
			$rtn = "";		
		}

		return $rtn;
	}
	
	// 사업부, 매장선택 공통
	// $obj1 : 검색어
	function depsel($obj1, $obj2, $obj3){

		if($obj1!=""){
			$DEF_WHERE	= "WHERE DEP_CD='".$obj1."'";
		}else{
			if($obj2!=""){
				$DEF_WHERE	= "WHERE DEP_CD='".$obj2."'";
			}else{
				$DEF_WHERE = "";
			}
		}

		$qry = "SELECT * FROM TB_BAS_DEPART $DEF_WHERE";
		parent::Dbconnect();
		parent::SqlExec($qry);
		$GetD	= parent::fetchInto($qry);
		parent::DBClose();

		$outStr	 = "<select name='dep_sel' id='dep_sel' onChange='loadData(this)' style='font-size:9pt;'>";
		$outStr	.= "<option value=''>:: 사업부선택 ::</option>";
		for($i=0;$i<count($GetD);$i++){
			if($GetD[$i]['DEP_CD']==$obj2){
				$opt	 = "selected";
			}else{
				$opt	 = "";
			}
			$outStr	.= "<option value='".$GetD[$i]['DEP_CD']."' ".$opt.">".$GetD[$i]['DEP_NM']."</option>";
		
		}
		$outStr	.= "</select>";

		if($obj2!=""){
			parent::Dbconnect();
			$qry = "SELECT * FROM TB_BAS_STORE $DEF_WHERE";
			parent::SqlExec($qry);
			$sGetD	= parent::fetchInto($qry);
			parent::DBClose();
		}

		$outStr	.= "&nbsp;<select name='sto_sel' id='sto_sel' style='font-size:9pt;'>";
		$outStr	.= "<option value=''>::매장선택::</option>";
		
		$sGetD		= isset($sGetD) ? $sGetD : NULL;
		
		for($i2=0;$i2<count($sGetD);$i2++){
			if($sGetD[$i2]['STO_CD']==$obj3){
				$opt2	 = "selected";
			}else{
				$opt2	 = "";
			}
			$outStr	.= "<option value='".$sGetD[$i2]['STO_CD']."' ".$opt2.">".$sGetD[$i2]['STO_NM']."</option>";
		
		}
		$outStr	.= "</select>";

		return $outStr;
	}

	// 사업부, 매장선택 공통
	// $obj1 : 검색어
	function depsel_sys($obj1, $obj2, $obj3){

		if($obj1!=""){
			$DEF_WHERE	= "WHERE DEP_CD='".$obj1."'";
		}else{
			if($obj2!=""){
				$DEF_WHERE	= "WHERE DEP_CD='".$obj2."'";
			}else{
				$DEF_WHERE = "";
			}
		}

		$qry = "SELECT * FROM TB_BAS_DEPART $DEF_WHERE";
		parent::Dbconnect();
		parent::SqlExec($qry);
		$GetD	= parent::fetchInto($qry);
		parent::DBClose();

		$outStr	 = "<select name='dep_cd' id='dep_cd' onChange='loadData2(this)' style='font-size:9pt;'>";
		$outStr	.= "<option value=''>:: 사업부선택 ::</option>";
		for($i=0;$i<count($GetD);$i++){
			if($GetD[$i]['DEP_CD']==$obj2){
				$opt	 = "selected";
			}else{
				$opt	 = "";
			}
			$outStr	.= "<option value='".$GetD[$i]['DEP_CD']."' ".$opt.">".$GetD[$i]['DEP_NM']."</option>";
		
		}
		$outStr	.= "</select>";

		if($obj2!=""){
			parent::Dbconnect();
			$qry = "SELECT * FROM TB_BAS_STORE $DEF_WHERE";
			parent::SqlExec($qry);
			$sGetD	= parent::fetchInto($qry);
			parent::DBClose();
		}

		$outStr	.= "&nbsp;<select name='sto_cd' id='sto_cd' style='font-size:9pt;'>";
		$outStr	.= "<option value=''>::매장선택::</option>";
		for($i2=0;$i2<count($sGetD);$i2++){
			if($sGetD[$i2]['STO_CD']==$obj3){
				$opt2	 = "selected";
			}else{
				$opt2	 = "";
			}
			$outStr	.= "<option value='".$sGetD[$i2]['STO_CD']."' ".$opt2.">".$sGetD[$i2]['STO_NM']."</option>";
		
		}
		$outStr	.= "</select>";

		return $outStr;
	}

	//사업부 선택 
	function trans_depsel($auth, $obj){

		if($auth[1]!=""){$AUTH_WH[] = "ORG_CD='".$auth[1]."'";}
		if($auth[2]!=""){$AUTH_WH[] = "DEP_CD='".$auth[2]."'";}

		if(count($AUTH_WH)>0){
			$USER_AUTH	= " WHERE ".implode(" AND ", $AUTH_WH);
		}


		$qry = "SELECT * FROM TB_BAS_DEPART $USER_AUTH";
		parent::Dbconnect();
		parent::SqlExec($qry);
		$GetD	= parent::fetchInto($qry);
		parent::DBClose();

		$outStr	 = "<select name='depcd' id='depcd' style='font-size:9pt;' class='searchbox_drop'>";
		$outStr	.= "<option value=''>:: 사업부선택 ::</option>";
		for($i=0;$i<count($GetD);$i++){
			if($GetD[$i][DEP_CD]==$obj){
				$opt	 = "selected";//선택한내용을유지
			}else{
				$opt	 = "";
			}
			$outStr	.= "<option value='".$GetD[$i][DEP_CD]."' ".$opt.">".$GetD[$i][DEP_NM]."</option>";
		
		}
		$outStr	.= "</select>";

		return $outStr;
	}
	
	function trans_tid2(){
	    
	    $outStr	 = "<select name='tid2' id='tid2' style='font-size:9pt;' class='searchbox_drop'>";
	    $outStr	.= "<option value=''>:: 사업부선택 ::</option>";

	        $outStr	.= "<option value='7635015001_39242631'>GS25</option>";
	        $outStr	.= "<option value='7017728001_39242632'>GS수퍼</option>";
	        $outStr	.= "<option value='7642904001_39242633'>랄라블라</option>";

	    $outStr	.= "</select>";
	    
	    return $outStr;
	}

	 /**
	  * 매입사 선택 함수
	  * 작성일 2014.05.02
	  * 작성자 : 장도현
	  * @param obj1			: 매입사 코드(PIXXXXX)
	  */
	function acqsel($obj1){
		$obj1	= trim($obj1);
		$qry = "SELECT PUR_CD, PUR_NM FROM TB_BAS_PURINFO WHERE PUR_USE='Y' ORDER BY PUR_SORT ASC";
		parent::Dbconnect();
		parent::SqlExec($qry);
		$GetD	= parent::fetchInto($qry);
		parent::DBClose();

		$outStr	 = "<select name='acq_sel'  style='font-size:9pt;' class='searchbox_drop'>";
		$outStr	.= "<option value=''>:: 매입사선택 ::</option>";
		for($i=0;$i<count($GetD);$i++){
			if($GetD[$i][PUR_CD]==$obj1){
				$opt	 = "selected";//선택한내용을유지
			}else{
				$opt	 = "";
			}
			$outStr	.= "<option value='".$GetD[$i][PUR_CD]."' ".$opt.">".$GetD[$i][PUR_NM]."</option>";
		
		}
		$outStr	.= "</select>";

		return $outStr;
	}

	function select_opt_tid($obj){

		if($obj[13]!=""){
			$AUTH_WH[] = "ORG_CD='".$obj[13]."'";
			$MST_WH = " AND ORG_CD='".$obj[13]."'";
		}
		if($obj[14]!=""){$AUTH_WH[] = "DEP_CD='".$obj[14]."'";}

		
		if(count($AUTH_WH)>0){
			$USER_AUTH	= " WHERE ".implode(" AND ", $AUTH_WH);
		}

		$qry = "SELECT TERM_ID, TERM_NM FROM TB_BAS_TIDMST WHERE TERM_ID IN (SELECT TID FROM TB_BAS_TIDMAP $USER_AUTH) $MST_WH ORDER BY TERM_SORT ASC";

		parent::Dbconnect();
		parent::SqlExec($qry);
		$GetD	= parent::fetchInto($qry);
		parent::DBClose();

		$outStr	 = "<select name='tid' id='tid' style='font-size:9pt;' class='searchbox_drop'>";
		$outStr	.= "<option value=''>:: 단말기선택 ::</option>";
		for($i=0;$i<count($GetD);$i++){
			if($GetD[$i][TERM_ID]==$obj[15]){
				$opt	 = "selected";//선택한내용을유지
			}else{
				$opt	 = "";
			}
			$outStr	.= "<option value='".$GetD[$i][TERM_ID]."' ".$opt.">".$GetD[$i][TERM_NM]."(".$GetD[$i][TERM_ID].")</option>";
		
		}
		$outStr	.= "</select>";

		return $outStr;
	}


	/**
	* 가맹점번호를 이용한 매입사 선택
	**/
	function select_acqTomid($obj){

		$qry = "
		SELECT 
			MER_CD
			, PUR_NM
		FROM
			TB_BAS_MERINFO T1
		LEFT OUTER JOIN(
			SELECT PUR_CD, PUR_NM, PUR_SORT FROM TB_BAS_PURINFO
		)T2 ON(T1.PUR_CD=T2.PUR_CD)
		ORDER BY PUR_SORT
		";
		parent::Dbconnect();
		parent::SqlExec($qry);
		$GetD	= parent::fetchInto($qry);
		parent::DBClose();

		$outStr	 = "&nbsp;&nbsp;<select name='acq_sel' id='acq_sel'  style='font-size:9pt;' class='searchbox_drop'>";
		$outStr	.= "<option value=''>:: 카드사선택 ::</option>";
		for($i=0;$i<count($GetD);$i++){
			if($GetD[$i][MER_CD]==$obj){

				$opt	 = "selected";//선택한내용을유지
			}else{
				$opt	 = "";
			}

			$outStr	.= "<option value='".$GetD[$i][MER_CD]."' ".$opt.">".$GetD[$i][PUR_NM]."</option>";
		}
		$outStr	.= "</select>";

		return $outStr;
	}


	/**
	* 가맹점번호를 이용한 매입사 선택
	**/
	// 카드사 조회시 다우데이터 안나오는것 수정 20170203
	function select_acqcd($obj){

		$qry = "SELECT PUR_OCD, PUR_KOCES, PUR_NM, PUR_CD FROM TB_BAS_PURINFO WHERE PUR_USE='Y' ORDER BY PUR_SORT ASC";
		$acqor = ",";
		parent::Dbconnect();
		parent::SqlExec($qry);
		$GetD	= parent::fetchInto($qry);
		parent::DBClose();

		$outStr	 = "<select name='acqcd' id='acqcd' style='font-size:9pt;' class='searchbox_drop'>";
		$outStr	.= "<option value=''>:: 전체 ::</option>";
		for($i=0;$i<count($GetD);$i++){
			$outStr	.= "<option value='".$GetD[$i][PUR_KOCES]."".$acqor."".$GetD[$i][PUR_OCD].$acqor."".$GetD[$i][PUR_CD]."' ".$this->AcqChk($GetD[$i][PUR_KOCES], $obj).">".$GetD[$i][PUR_NM]."</option>";
		}
		$outStr	.= "</select>";

		return $outStr;
	}
	
	function SearchTpl($url, $stitle, $depcd, $dep_sel, $sto_sel, $stime, $etime, $card_no, $approval_no,$acqcd=''){
		$rtnHTML	 = "<div class='cont_title'>";
		$rtnHTML	.= "	<table width='100%' class='tb00_none'>";
		$rtnHTML	.= "	<form name='regular' method='post' action='".$url."'>";
		$rtnHTML	.= "	<input type='hidden' name='extitle' id='extitle' value=''>";
		$rtnHTML	.= "	<input type='hidden' name='search' value='Y'>";
        $rtnHTML	.= "	<input type='hidden' name='acqcd'  id='acqcd' value='".$acqcd."'>";
		$rtnHTML	.= "		<tr>";
		$rtnHTML	.= "			<td width='4'><img src='./images/sch_left.gif'></td>";
		$rtnHTML	.= "			<td background='./images/sch_mid.gif' width='150'><span class='schtitle'>&nbsp;&nbsp; ".$stitle."</span></td>";
		$rtnHTML	.= "			<td background='./images/sch_mid.gif'>";
		$rtnHTML	.= "				<span class='schtitle'>승인일자</span>";
		$rtnHTML	.= "				<input type='text' value='".$stime."' name='stime' id='stime' style='width:70px; font-size:9pt;' onclick=\"setSens('etime', 'max');\" onblur=\"datesam();\"> ";
		$rtnHTML	.= "				~";
		$rtnHTML	.= "				<input type='text' value='".$etime."' name='etime' id='etime' style='width:70px; font-size:9pt;' onclick=\"setSens('stime', 'min');\" > ";
		$rtnHTML	.= "				&nbsp;&nbsp;<span class='schtitle'>카드번호</span>";
		$rtnHTML	.= "				<input type='text' name='card_no' id='card_no' value='".$card_no."' style='width:150px; font-size:9pt;'>";
		$rtnHTML	.= "				&nbsp;&nbsp;<span class='schtitle'>승인번호</span>";
		$rtnHTML	.= "				<input type='text' name='approval_no' id='approval_no' value='".$approval_no."' style='width:60px; font-size:9pt;'>";
		$rtnHTML	.= $this->select_opt_tid('');
		$rtnHTML	.= "				&nbsp;&nbsp;<span class='schtitle'>가맹점번호</span>";
		$rtnHTML	.= "				<input type='text' name='mid' id='mid' value='".$mid."' style='width:100px; font-size:9pt;'>";
		$rtnHTML	.= "			</td>";
		$rtnHTML	.= "			<td width='60' background='./images/sch_mid.gif'><img src='./images/sch_btn.png' onclick='search_go();' style='cursor:pointer;'></td>";
		$rtnHTML	.= "			<td width='4'><img src='./images/sch_right.gif'></td>";
		$rtnHTML	.= "		</tr>";
		$rtnHTML	.= "	</form>";
		$rtnHTML	.= "	</table>";
		$rtnHTML	.= "</div>";
	
		return $rtnHTML;
	}

	function SearchTpl1($url, $stitle, $depcd, $dep_sel, $sto_sel, $stime, $etime, $card_no, $approval_no){
		$rtnHTML	 = "<div class='cont_title'>";
		$rtnHTML	.= "	<table width='100%' class='tb00_none'>";
		$rtnHTML	.= "	<form name='regular' method='post' action='".$url."'>";
		$rtnHTML	.= "	<input type='hidden' name='extitle' id='extitle' value=''>";
		$rtnHTML	.= "	<input type='hidden' name='search' value='Y'>";
		$rtnHTML	.= "		<tr>";
		$rtnHTML	.= "			<td width='4'><img src='./images/sch_left.gif'></td>";
		$rtnHTML	.= "			<td background='./images/sch_mid.gif' width='150'><span class='schtitle'>&nbsp;&nbsp; ".$stitle."</span></td>";
	//	$rtnHTML	.= "			<td background='./images/sch_mid.gif' width='285'>".$this->depsel($depcd, $dep_sel, $sto_sel)."</td>";
		$rtnHTML	.= "			<td background='./images/sch_mid.gif'>";
		$rtnHTML	.= "				<span class='schtitle'>입금일자</span>";
		$rtnHTML	.= "				<input type='text' value='".$stime."' name='stime' id='stime' style='width:70px; font-size:9pt;' onclick=\"setSens('etime', 'max');\" > ";
		//$rtnHTML	.= "				<img src='/images/admin/btn/calendar.gif' alt='' onclick=\"setSens('stime', 'max');\" align='absmiddle'>";
		$rtnHTML	.= "				~";
		$rtnHTML	.= "				<input type='text' value='".$etime."' name='etime' id='etime' style='width:70px; font-size:9pt;' onclick=\"setSens('stime', 'min');\" > ";
		//$rtnHTML	.= "				<img src='/images/admin/btn/calendar.gif' alt='' onclick=\"setSens('etime', 'min');\" align='absmiddle'>";
		$rtnHTML	.= "				&nbsp;&nbsp;<span class='schtitle'>카드번호</span>";
		$rtnHTML	.= "				<input type='text' name='card_no' id='card_no' value='".$card_no."' style='width:150px; font-size:9pt;'>";
		$rtnHTML	.= "				&nbsp;&nbsp;<span class='schtitle'>승인번호</span>";
		$rtnHTML	.= "				<input type='text' name='approval_no' id='approval_no' value='".$approval_no."' style='width:60px; font-size:9pt;'>";
		$rtnHTML	.= "			</td>";
		$rtnHTML	.= "			<td width='60' background='./images/sch_mid.gif'><img src='./images/sch_btn.png' onclick='search_go();' style='cursor:pointer;'></td>";
		$rtnHTML	.= "			<td width='4'><img src='./images/sch_right.gif'></td>";
		$rtnHTML	.= "		</tr>";
		$rtnHTML	.= "	</form>";
		$rtnHTML	.= "	</table>";
		$rtnHTML	.= "</div>";
	
		return $rtnHTML;
	}

    function SearchTpl1_1($url, $stitle, $depcd, $dep_sel, $sto_sel, $stime, $etime, $card_no, $approval_no){
		$rtnHTML	 = "<div class='cont_title'>";
		$rtnHTML	.= "	<table width='100%' class='tb00_none'>";
		$rtnHTML	.= "	<form name='regular' method='post' action='".$url."'>";
		$rtnHTML	.= "	<input type='hidden' name='extitle' id='extitle' value=''>";
		$rtnHTML	.= "	<input type='hidden' name='search' value='Y'>";
		$rtnHTML	.= "		<tr>";
		$rtnHTML	.= "			<td width='4'><img src='./images/sch_left.gif'></td>";
		$rtnHTML	.= "			<td background='./images/sch_mid.gif' width='150'><span class='schtitle'>&nbsp;&nbsp; ".$stitle."</span></td>";
	//	$rtnHTML	.= "			<td background='./images/sch_mid.gif' width='285'>".$this->depsel($depcd, $dep_sel, $sto_sel)."</td>";
		$rtnHTML	.= "			<td background='./images/sch_mid.gif'>";
		$rtnHTML	.= "				<span class='schtitle'>일자</span>";
		$rtnHTML	.= "				<input type='text' value='".$stime."' name='stime' id='stime' style='width:70px; font-size:9pt;' onclick=\"setSens('etime', 'max');\" > ";
		//$rtnHTML	.= "				<img src='/images/admin/btn/calendar.gif' alt='' onclick=\"setSens('stime', 'max');\" align='absmiddle'>";
		$rtnHTML	.= "				~";
		$rtnHTML	.= "				<input type='text' value='".$etime."' name='etime' id='etime' style='width:70px; font-size:9pt;' onclick=\"setSens('stime', 'min');\" > ";
		//$rtnHTML	.= "				<img src='/images/admin/btn/calendar.gif' alt='' onclick=\"setSens('etime', 'min');\" align='absmiddle'>";
	//	$rtnHTML	.= "				&nbsp;&nbsp;<span class='schtitle'>카드번호</span>";
	//	$rtnHTML	.= "				<input type='text' name='card_no' id='card_no' value='".$card_no."' style='width:150px; font-size:9pt;'>";
	//	$rtnHTML	.= "				&nbsp;&nbsp;<span class='schtitle'>승인번호</span>";
	//	$rtnHTML	.= "				<input type='text' name='approval_no' id='approval_no' value='".$approval_no."' style='width:60px; font-size:9pt;'>";
		$rtnHTML	.= "			</td>";
		$rtnHTML	.= "			<td width='60' background='./images/sch_mid.gif'><img src='./images/sch_btn.png' onclick='search_go();' style='cursor:pointer;'></td>";
		$rtnHTML	.= "			<td width='4'><img src='./images/sch_right.gif'></td>";
		$rtnHTML	.= "		</tr>";
		$rtnHTML	.= "	</form>";
		$rtnHTML	.= "	</table>";
		$rtnHTML	.= "</div>";
	
		return $rtnHTML;
	}
	
	function SearchTpl2($url, $stitle, $depcd, $dep_sel, $sto_sel, $stime, $etime, $card_no, $approval_no){
		$rtnHTML	 = "<div class='cont_title'>";
		$rtnHTML	.= "	<table width='100%' class='tb00_none'>";
		$rtnHTML	.= "	<form name='regular' method='post' action='".$url."'>";
		$rtnHTML	.= "	<input type='hidden' name='extitle' id='extitle' value=''>";
		$rtnHTML	.= "	<input type='hidden' name='search' value='Y'>";
		$rtnHTML	.= "		<tr>";
		$rtnHTML	.= "			<td width='4'><img src='./images/sch_left.gif'></td>";
		$rtnHTML	.= "			<td background='./images/sch_mid.gif' width='150'><span class='schtitle'>&nbsp;&nbsp; ".$stitle."</span></td>";
	//	$rtnHTML	.= "			<td background='./images/sch_mid.gif' width='285'>".$this->depsel($depcd, $dep_sel, $sto_sel)."</td>";
		$rtnHTML	.= "			<td background='./images/sch_mid.gif'>";
		$rtnHTML	.= "				<span class='schtitle'>청구일자</span>";
		$rtnHTML	.= "				<input type='text' value='".$stime."' name='stime' id='stime' style='width:70px; font-size:9pt;' onclick=\"setSens('etime', 'max');\" > ";
		//$rtnHTML	.= "				<img src='/images/admin/btn/calendar.gif' alt='' onclick=\"setSens('stime', 'max');\" align='absmiddle'>";
		$rtnHTML	.= "				~";
		$rtnHTML	.= "				<input type='text' value='".$etime."' name='etime' id='etime' style='width:70px; font-size:9pt;' onclick=\"setSens('stime', 'min');\" > ";
		//$rtnHTML	.= "				<img src='/images/admin/btn/calendar.gif' alt='' onclick=\"setSens('etime', 'min');\" align='absmiddle'>";
		$rtnHTML	.= "				&nbsp;&nbsp;<span class='schtitle'>카드번호</span>";
		$rtnHTML	.= "				<input type='text' name='card_no' id='card_no' value='".$card_no."' style='width:60px; font-size:9pt;'>";
		$rtnHTML	.= "				&nbsp;&nbsp;<span class='schtitle'>승인번호</span>";
		$rtnHTML	.= "				<input type='text' name='approval_no' id='approval_no' value='".$approval_no."' style='width:60px; font-size:9pt;'>";
		$rtnHTML	.= "			</td>";
		$rtnHTML	.= "			<td width='60' background='./images/sch_mid.gif'><img src='./images/sch_btn.png' onclick='search_go();' style='cursor:pointer;'></td>";
		$rtnHTML	.= "			<td width='4'><img src='./images/sch_right.gif'></td>";
		$rtnHTML	.= "		</tr>";
		$rtnHTML	.= "	</form>";
		$rtnHTML	.= "	</table>";
		$rtnHTML	.= "</div>";
	
		return $rtnHTML;
	}

	function SearchDepoTpl($url, $stitle, $depcd, $dep_sel, $sto_sel, $stime, $etime, $card_no, $approval_no, $datekey){
		
		if($datekey=="DEPO_DD"||$datekey==""){
			$depodd	= "selected";
		}elseif($datekey=="DEPO_EX_DD"){
			$exdd	= "selected";
		}elseif($datekey=="APP_DD"){
			$appdd	= "selected";
		}

		$rtnHTML	 = "<div class='cont_title'>";
		$rtnHTML	.= "	<table width='100%' class='tb00_none'>";
		$rtnHTML	.= "	<form name='regular' method='post' action='".$url."'>";
		$rtnHTML	.= "	<input type='hidden' name='extitle' id='extitle' value=''>";
		$rtnHTML	.= "	<input type='hidden' name='search' value='Y'>";
		$rtnHTML	.= "		<tr>";
		$rtnHTML	.= "			<td width='4'><img src='./images/sch_left.gif'></td>";
		$rtnHTML	.= "			<td background='./images/sch_mid.gif' width='140'><span id='schtitle'>&nbsp;&nbsp; ".$stitle."</span></td>";
		$rtnHTML	.= "			<td background='./images/sch_mid.gif' width='285'>".$this->depsel($depcd, $dep_sel, $sto_sel)."</td>";
		$rtnHTML	.= "			<td background='./images/sch_mid.gif'>";
		$rtnHTML	.= "				<select name='datekey' style='font-size:9pt;'>";
		$rtnHTML	.= "					<option value='DEPO_DD' ".$depodd.">청구일자</option>";
		$rtnHTML	.= "					<option value='DEPO_EX_DD' ".$exdd.">입금일자</option>";
		$rtnHTML	.= "					<option value='APP_DD' ".$appdd.">승인일자</option>";
		$rtnHTML	.= "				</select>";
		$rtnHTML	.= "				<input type='text' value='".$stime."' name='stime' id='stime' style='width:60px; font-size:9pt;' onblur='datesam();' onchange='datesam();'> ";
		$rtnHTML	.= "				<img src='/images/admin/btn/calendar.gif' alt='' onclick=\"displayCalendar(document.forms[0].stime,'yyyymmdd',this)\" align='absmiddle'>";
		$rtnHTML	.= "				~";
		$rtnHTML	.= "				<input type='text' value='".$etime."' name='etime' id='etime' style='width:60px; font-size:9pt;'> ";
		$rtnHTML	.= "				<img src='/images/admin/btn/calendar.gif' alt='' onclick=\"displayCalendar(document.forms[0].etime,'yyyymmdd',this)\" align='absmiddle'>";
		$rtnHTML	.= "				&nbsp;<span id='schtitle'>카드번호</span>";
		$rtnHTML	.= "				<input type='text' name='card_no' value='".$card_no."' style='width:60px; font-size:9pt;'>";
		$rtnHTML	.= "				&nbsp;<span id='schtitle'>승인번호</span>";
		$rtnHTML	.= "				<input type='text' name='approval_no' value='".$approval_no."' style='width:60px; font-size:9pt;'>";
		$rtnHTML	.= "			</td>";
		$rtnHTML	.= "			<td width='60' background='./images/sch_mid.gif'><img src='./images/sch_btn.png' onclick='search_check();' style='cursor:pointer;'></td>";
		$rtnHTML	.= "			<td width='4'><img src='./images/sch_right.gif'></td>";
		$rtnHTML	.= "		</tr>";
		$rtnHTML	.= "	</form>";
		$rtnHTML	.= "	</table>";
		$rtnHTML	.= "</div>";
	
		return $rtnHTML;
	}

	/**
	* PARAM 
	* $arg[0]	: url
	* $arg[1]	: title
	* $arg[2]	: depcd
	* $arg[3]	: dep_sel
	* arg[4]	: sto_sel
	* arg[5]	: stime
	* arg[6]	: etime
	* arg[7]	: card_no
	* arg[8]	: approval_no
	* arg[9]	: acq_cd
	* arg[10]	: amount start
	* arg[11]	: amount end
	**/

	function AcqChk($obj, $obj2){
		if($obj==$obj2){
			$rtn = "selected";
		}else{
			$rtn = "";
		}

		return $rtn;
	}
	function SearchTpl_total($arg){
		$rtnHTML	 = "<div class='cont_title'>";
		$rtnHTML	.= "	<table width='100%' class='tb00_none'>";
		$rtnHTML	.= "	<form name='regular' method='post' action='".$arg[0]."'>";
		$rtnHTML	.= "	<input type='hidden' name='extitle' id='extitle' value=''>";
		$rtnHTML	.= "	<input type='hidden' name='search' value='Y'>";
		$rtnHTML	.= "		<tr>";
		$rtnHTML	.= "			<td width='4'><img src='./images/sch_left.gif'></td>";
		$rtnHTML	.= "			<td background='./images/sch_mid.gif' width='150'><span class='schtitle'>&nbsp;&nbsp; ".$arg[1]."</span></td>";
		$rtnHTML	.= "			<td background='./images/sch_mid.gif'>";
		$rtnHTML	.= "				<span class='schtitle'>승인일자</span>";
		$rtnHTML	.= "				<input type='text' value='".$arg[5]."' name='stime' id='stime' style='width:70px; font-size:9pt;' onclick=\"setSens('etime', 'max');\"  onblur=\"datesam();\"> ";
		$rtnHTML	.= "				~";
		$rtnHTML	.= "				<input type='text' value='".$arg[6]."' name='etime' id='etime' style='width:70px; font-size:9pt;' onclick=\"setSens('stime', 'min');\" > ";
		$rtnHTML	.= $this->select_opt_tid($arg)."
		
								카드사선택
								<select name='acqcd' id='acqcd'>
								<option value=''>전체</option>
									<option value='1101' ".$this->AcqChk('1101', $arg[9]).">국민카드</option>
									<option value='1102' ".$this->AcqChk('1102', $arg[9]).">현대카드</option>
									<option value='1103' ".$this->AcqChk('1103', $arg[9]).">롯데카드</option>
									<option value='1104' ".$this->AcqChk('1104', $arg[9]).">삼성카드</option>
									<option value='1105' ".$this->AcqChk('1105', $arg[9]).">하나카드</option>
									<option value='1106' ".$this->AcqChk('1106', $arg[9]).">비씨카드</option>
									<option value='1107' ".$this->AcqChk('1107', $arg[9]).">신한카드</option>
									<option value='2211' ".$this->AcqChk('2211', $arg[9]).">농협카드</option>
								</select>
								거래금액
								<input type='text' name='amounts' id='amounts' value='".$arg[10]."' style='width:60px; font-size:9pt;'>~
								<input type='text' name='amounte' id='amounte' value='".$arg[11]."' style='width:60px; font-size:9pt;'>

		";
		$rtnHTML	.= "				&nbsp;&nbsp;<span class='schtitle'>카드번호</span>";
		$rtnHTML	.= "				<input type='text' name='card_no' id='card_no' value='".$arg[7]."' style='width:110px; font-size:9pt;'>";
		$rtnHTML	.= "				&nbsp;&nbsp;<span class='schtitle'>승인번호</span>";
		$rtnHTML	.= "				<input type='text' name='approval_no' id='approval_no' value='".$arg[8]."' style='width:60px; font-size:9pt;'>";
		$rtnHTML	.= "			</td>";
		$rtnHTML	.= "			<td width='60' background='./images/sch_mid.gif'><img src='./images/sch_btn.png' onclick='search_go();' style='cursor:pointer;'></td>";
		$rtnHTML	.= "			<td width='4'><img src='./images/sch_right.gif'></td>";
		$rtnHTML	.= "		</tr>";
		$rtnHTML	.= "	</form>";
		$rtnHTML	.= "	</table>";
		$rtnHTML	.= "</div>";
	
		return $rtnHTML;
	}

	function ControlAreaSet($gridid, $reg, $close, $obj,$search_title='',$url_query = '')
    {
	
        $GridID		 = $gridid."_ttcnt"; 
		$rtnHTML	 = "<div style=\"width:100%; height:20px;\"></div>";
		$rtnHTML	.= "<div id=\"control_div\">";
		$rtnHTML	.= "<table class=\"tb00_none\" width=\"100%\">";
		$rtnHTML	.= "	<tr height=\"20\">";
		
        if ( $search_title ){
        $rtnHTML	.= "	<td width=\"200\" align='left'><font  color='red'><b>■ ".$search_title. "</b></font> 검색&nbsp;</td>";
        }
       
        $rtnHTML	.= "		<td></td>";
        
        


		if($reg=="Y"){
			$rtnHTML	.= "	<td width=\"150\"><span id=\"conttxt\" style=\"javascript:void(0)\" onclick=\"jstest(mygrids.getSelectedId());\"><img src=\"/images/regi.png\" align=\"absmiddle\"> 거래등록</span>&nbsp;</td>";
		}
		if($close=="Y"){
			$rtnHTML	.= "	<td width=\"90\"><span id=\"conttxt\" style=\"javascript:void(0)\" onclick=\"DetailGridClose();\"><img src=\"/images/close.png\" align=\"absmiddle\"> 그리드닫기</span></td>";
		}

		$rtnHTML	.= "		<td width=\"100\" align=\"right\">";
		
		$rtnHTML	.= "			<span id=\"conttxt\" style=\"javascript:void(0)\" onclick=\"accountGrid.toExcel('./proc/generate.php?obj=".$obj."');\"><img src=\"/images/excel_icon.png\" align=\"absmiddle\"> ExcelDown</span>";

	//	$rtnHTML	.= "			<span id=\"conttxt\" style=\"javascript:void(0)\" onclick=\"exceldn('".$obj."','".$url_query."');\"><img src=\"/images/excel_icon.png\" align=\"absmiddle\"> ExcelDown</span>";

		$rtnHTML	.= "		</td>";
		$rtnHTML	.= "	</tr>";
		$rtnHTML	.= "</table>";
		$rtnHTML	.= "</div>";

		return $rtnHTML;
	}

	function NomalSearch($url, $stitle, $depcd, $dep_sel, $sto_sel, $stime, $etime, $card_no, $approval_no){
		$rtnHTML	 = "<div class='cont_title'>";
		$rtnHTML	.= "	<table width='100%' class='tb00_none'>";
		$rtnHTML	.= "	<form name='regular' method='post' action='".$url."'>";
		$rtnHTML	.= "	<input type='hidden' name='extitle' id='extitle' value=''>";
		$rtnHTML	.= "	<input type='hidden' name='search' value='Y'>";
		$rtnHTML	.= "		<tr>";
		$rtnHTML	.= "			<td width='4'><img src='./images/sch_left.gif'></td>";
		$rtnHTML	.= "			<td background='./images/sch_mid.gif' width='160'><span id='schtitle'>&nbsp;&nbsp; ".$stitle."</span></td>";
		$rtnHTML	.= "			<td background='./images/sch_mid.gif' width='280'></td>";
		$rtnHTML	.= "			<td background='./images/sch_mid.gif' >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		$rtnHTML	.= "			</td>";
		$rtnHTML	.= "			<td width='60' background='./images/sch_mid.gif'><img src='./images/sch_btn.png' onclick='search_check();' style='cursor:pointer;'></td>";
		$rtnHTML	.= "			<td width='4'><img src='./images/sch_right.gif'></td>";
		$rtnHTML	.= "		</tr>";
		$rtnHTML	.= "	</form>";
		$rtnHTML	.= "	</table>";
		$rtnHTML	.= "</div>";
	
		return $rtnHTML;
	}
} 
?>