<?
class DepoSit extends Oci_API { 
	
	function DepoDepChk($obj){  
		parent::DBConnect(); 
		
		for($i=0;$i<count($obj);$i++){
			$iWhere	= "WHERE SALE_SEQ='$obj[$i]'";
			$SQL_QUERY	= "SELECT ";
			$SQL_QUERY	.= "    SALE_SEQ SALESEQ, DEP_CD DEPCD ";
			$SQL_QUERY	.= "FROM ";
			$SQL_QUERY	.= "    TB_SALES_MST  ";
			$SQL_QUERY	.= $iWhere;

			parent::SqlExec($SQL_QUERY);
			$GetD = parent::fetchInto($SQL_QUERY);

			$DEPDATA[$GetD[0][DEPCD]][]	= $GetD[0][SALESEQ];
		}
		
		parent::DBClose();
		return $DEPDATA;

	}

	

	function DepoTotalMake($obj, $obj2, $obj3, $obj4, $obj5){
		if($obj=="HS000001"){//인테리어(HANSSEM)
			$rtn	= self::KocesEDITotal($obj2, $obj5);
		}else if($obj=="HS000002"||$obj=="HS000003"){//부엌(HB)//AS(HA)
			$rtn	= self::KocesHDCTotal($obj2, $obj3, $obj4, $obj5);
		}

		return $rtn;
	}

	function KocesHDCTotal($obj, $obj2, $obj3, $obj4){
		$rtn	= "E1   ";
		$rtn	.= STR_PAD($obj,6,"0",STR_PAD_LEFT);
		$rtn	.= STR_PAD($obj,7,"0",STR_PAD_LEFT);
		$rtn	.= STR_PAD($obj2,12,"0",STR_PAD_LEFT);
		$rtn	.= STR_PAD($obj3,12,"0",STR_PAD_LEFT);
		$rtn	.= STR_PAD($obj4,12,"0",STR_PAD_LEFT);
		$rtn	.= STR_PAD("0",7,"0",STR_PAD_LEFT);
		$rtn	.= STR_PAD("0",12,"0",STR_PAD_LEFT);
		$rtn	.= STR_PAD("0",12,"0",STR_PAD_LEFT);
		$rtn	.= STR_PAD("0",12,"0",STR_PAD_LEFT);
		$rtn	.= STR_PAD("0",7,"0",STR_PAD_LEFT);
		$rtn	.= STR_PAD("0",12,"0",STR_PAD_LEFT);
		$rtn	.= STR_PAD("0",12,"0",STR_PAD_LEFT);
		$rtn	.= STR_PAD("0",7,"0",STR_PAD_LEFT);
		$rtn	.= STR_PAD("",15," ",STR_PAD_RIGHT);

		return $rtn;
	}

	function DepoBodyMake($obj, $obj2, $idx){
		if($obj=="HS000001"){//인테리어(HANSSEM)
			$rtn	= self::KocesEDIBody($obj2, $idx);
		}else if($obj=="HS000002"||$obj=="HS000003"){//부엌(HB)//AS(HA)
			$rtn	= self::KocesHDCBody($obj2, $idx);
		}

		return $rtn;
	}

	function KocesHDCBody($obj,$idx){
		$trbody	 = self::depPayGb($obj[0][AUTH_DIV]);
		$trbody .= STR_PAD($obj[0][CARD_NO],16," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD($obj[0][CARD_VALID],4," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD(substr($obj[0][APP_DD],2,6),6," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD(substr($obj[0][OAPP_DD],2,6),6," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD($obj[0][APP_NO],10," ",STR_PAD_RIGHT);
		$trbody .= "410";
		$trbody .= STR_PAD($obj[0][AMOUNT],9,"0",STR_PAD_LEFT);
		$trbody .= STR_PAD($obj[0][AMT_TIP],9,"0",STR_PAD_LEFT);
		$trbody .= STR_PAD($obj[0][AMT_TOT],9,"0",STR_PAD_LEFT);
		$trbody .= STR_PAD($obj[0][CARD_INST],2,"0",STR_PAD_LEFT);
		$trbody .= STR_PAD("",6," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD($obj[0][APP_TM],6," ",STR_PAD_LEFT);
		$trbody .= STR_PAD($idx,4,"0",STR_PAD_LEFT);
		$trbody .= "00";
		$trbody .= "1";
		$trbody .= "K";
		$trbody .= STR_PAD($obj[0][TID],10," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD("1338122865",10," ",STR_PAD_RIGHT);//사업자번호(부엌)
		$trbody .= STR_PAD($obj[0][MID],14," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD($obj[0][ACQ_CD],4," ",STR_PAD_RIGHT);
		if($obj[0][ACQ_CD]=="PI0016"){
			$trbody .= "E";
			$trbody .= "E";
		}else{
			$trbody .= "D";
			$trbody .= "D";
		}
		$trbody .= STR_PAD("",14," ",STR_PAD_RIGHT);
		$trbody	.= "";

		return $trbody;
	}

	function KocesEDIBody($obj,$idx){

		$trbody	 = "D";
		$trbody .= STR_PAD($idx+1,6,"0",STR_PAD_LEFT);
		$trbody	.= self::depPayGbEDI($obj[0][AUTH_DIV]);
		$trbody	.= "1";
		$trbody	.= "E";
		$trbody	.= "0";
		$trbody	.= "S";
		$trbody .= STR_PAD(trim($obj[0][TID]),10," ",STR_PAD_RIGHT);
		$trbody .= self::AcqCodeEDI($obj[0][ACQ_CD]);
		$trbody .= STR_PAD(trim($obj[0][MID]),15," ",STR_PAD_RIGHT);
		$trbody .= self::AcqCodeEDI($obj[0][ACQ_CD]);
		$trbody .= STR_PAD(trim($obj[0][CARD_NO]),16," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD(trim($obj[0][CARD_VALID]),4," ",STR_PAD_RIGHT);
		$trbody	.= "410";
		$trbody .= STR_PAD(trim(CEIL($obj[0][AMOUNT]/1.1)),12,"0",STR_PAD_LEFT);
		$trbody .= STR_PAD(trim($obj[0][AMT_TIP]),12,"0",STR_PAD_LEFT);
		$trbody .= STR_PAD(trim($obj[0][AMOUNT]-CEIL($obj[0][AMOUNT]/1.1)),12,"0",STR_PAD_LEFT);
		$trbody .= STR_PAD(trim($obj[0][CARD_INST]),2,"0",STR_PAD_LEFT);
		$trbody .= STR_PAD(trim($obj[0][APP_NO]),8," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD(trim(substr($obj[0][OAPP_DD],2,6)),6," ",STR_PAD_LEFT);
		$trbody .= STR_PAD(trim(substr($obj[0][APP_DD],2,6)),6," ",STR_PAD_LEFT);
		$trbody	.= "0000";
		$trbody	.= "0000";
		$trbody .= STR_PAD("",20," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD("",6," ",STR_PAD_RIGHT);
		$trbody	.= "000000000";
		$trbody .= STR_PAD("",6," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD("13381228650000",28," ",STR_PAD_RIGHT);

		return $trbody;
	}

	function KocesEDITotal($obj,$obj2){
		$trbody	 = "T";
		$trbody .= STR_PAD("",6,"0",STR_PAD_RIGHT);
		$trbody .= STR_PAD("",13,"0",STR_PAD_RIGHT);
		$trbody .= STR_PAD("",6,"0",STR_PAD_RIGHT);
		$trbody .= STR_PAD("",13,"0",STR_PAD_RIGHT);
		$trbody .= STR_PAD("",6,"0",STR_PAD_RIGHT);
		$trbody .= STR_PAD("",13,"0",STR_PAD_RIGHT);
		$trbody .= STR_PAD($obj,6,"0",STR_PAD_LEFT);
		$trbody .= STR_PAD($obj2,13,"0",STR_PAD_LEFT);
		$trbody .= STR_PAD("",6,"0",STR_PAD_RIGHT);
		$trbody .= STR_PAD("",13,"0",STR_PAD_RIGHT);
		$trbody .= STR_PAD("",6,"0",STR_PAD_RIGHT);
		$trbody .= STR_PAD("",13,"0",STR_PAD_RIGHT);
		$trbody .= STR_PAD("",6,"0",STR_PAD_RIGHT);
		$trbody .= STR_PAD("",13,"0",STR_PAD_RIGHT);
		$trbody .= STR_PAD("",6,"0",STR_PAD_RIGHT);
		$trbody .= STR_PAD("",13,"0",STR_PAD_RIGHT);
		$trbody .= STR_PAD("",13,"0",STR_PAD_RIGHT);
		$trbody .= STR_PAD("",34," ",STR_PAD_RIGHT);

		return $trbody;
	}



	function DepoSqlGet($obj){
		parent::DBConnect(); 

		$iWhere	= "WHERE A01.SALE_SEQ='".trim($obj)."'";
		$SQL_QUERY	= "SELECT ";
		$SQL_QUERY	.= "    ROWNUM, ";
		$SQL_QUERY	.= "    A01.PUR_DD, ";
		$SQL_QUERY	.= "    A01.PUR_TM, ";
		$SQL_QUERY	.= "    A01.PUR_RT_DD, ";
		$SQL_QUERY	.= "    A01.PUR_RT_TM, ";
		$SQL_QUERY	.= "    A01.PUR_RT_CD, ";
		$SQL_QUERY	.= "    A01.SALE_SEQ, ";
		$SQL_QUERY	.= "    A01.DIFF, ";
		$SQL_QUERY	.= "    A01.CONFIRM_GB, ";
		$SQL_QUERY	.= "    A01.PAY_GB, ";
		$SQL_QUERY	.= "    A01.KEYIN, ";
		$SQL_QUERY	.= "    A01.SALE_IDX, ";
		$SQL_QUERY	.= "    A01.CARD_NO, ";
		$SQL_QUERY	.= "    A01.CARD_VALID, ";
		$SQL_QUERY	.= "    A01.CARD_TYPE, ";
		$SQL_QUERY	.= "    A01.APPROVAL_NO, ";
		$SQL_QUERY	.= "    A01.APPROVAL_DD, ";
		$SQL_QUERY	.= "    A01.APPROVAL_TM, ";
		$SQL_QUERY	.= "    A01.OAPPROVAL_DD, ";
		$SQL_QUERY	.= "    A01.OAPPROVAL_TM, ";
		$SQL_QUERY	.= "    A01.OAPPROVAL_NO, ";
		$SQL_QUERY	.= "    A01.CARD_INSTALLMENT, ";
		$SQL_QUERY	.= "    A01.AMT_TYPE, ";
		$SQL_QUERY	.= "    A01.AMT_SALE, ";
		$SQL_QUERY	.= "    A01.AMT_SUPP, ";
		$SQL_QUERY	.= "    A01.AMT_TAX, ";
		$SQL_QUERY	.= "    A01.AMT_TIP, ";
		$SQL_QUERY	.= "    A01.AMT_TOT, ";
		$SQL_QUERY	.= "    A01.TID, ";
		$SQL_QUERY	.= "    A01.MERCHANT_NO, ";
		$SQL_QUERY	.= "    A01.ACQ_CODE, ";
		$SQL_QUERY	.= "    A01.COMM, ";
		$SQL_QUERY	.= "    A01.INCOME_DD, ";
		$SQL_QUERY	.= "    A01.PUR_TYPE, ";
		$SQL_QUERY	.= "    A01.PAY_STEP, ";
		$SQL_QUERY	.= "    A01.CONTRACT_NO, ";
		$SQL_QUERY	.= "    A01.SALESMAN_NO, ";
		$SQL_QUERY	.= "    A02.TERM_TYPE, ";
		$SQL_QUERY	.= "    A03.STO_NM, ";
		$SQL_QUERY	.= "    A03.STO_EXT_CD, ";
		$SQL_QUERY	.= "    A05.PUR_NM, ";
		$SQL_QUERY	.= "    A04.DEP_NM, ";
		$SQL_QUERY	.= "    A04.DEP_CD, ";
		$SQL_QUERY	.= "    A06.DP_INCOME_DD, ";
		$SQL_QUERY	.= "    A06.DP_COMM, ";
		$SQL_QUERY	.= "    A07.FEE_01, ";
		$SQL_QUERY	.= "    A07.FEE_02, ";
		$SQL_QUERY	.= "    A07.FEE_03 ";
		$SQL_QUERY	.= "FROM ";
		$SQL_QUERY	.= "    TB_MNG_SALES A01 ";
		$SQL_QUERY	.= "    LEFT OUTER JOIN  ";
		$SQL_QUERY	.= "       (SELECT TERM_ID, DEP_CD, STO_CD, TERM_TYPE FROM TB_BAS_TIDMST ) A02  ";
		$SQL_QUERY	.= "    ON (A01.TID=A02.TERM_ID)  ";
		$SQL_QUERY	.= "    LEFT OUTER JOIN  ";
		$SQL_QUERY	.= "       (SELECT STO_CD, STO_EXT_CD, STO_NM FROM TB_BAS_STORE) A03 ";
		$SQL_QUERY	.= "    ON (A02.STO_CD=A03.STO_CD) ";
		$SQL_QUERY	.= "    LEFT OUTER JOIN  ";
		$SQL_QUERY	.= "       (SELECT DEP_CD, DEP_NM FROM TB_BAS_DEPART) A04 ";
		$SQL_QUERY	.= "    ON (A02.DEP_CD=A04.DEP_CD) ";
		$SQL_QUERY	.= "    LEFT OUTER JOIN  ";
		$SQL_QUERY	.= "       (SELECT PUR_KOCES, PUR_OCD, PUR_NM FROM TB_BAS_PURINFO) A05  ";
		$SQL_QUERY	.= "    ON (A05.PUR_KOCES=A01.ACQ_CODE) ";
		$SQL_QUERY	.= "    LEFT OUTER JOIN  ";
		$SQL_QUERY	.= "        (SELECT DP_CARD_NO, DP_AUTH_GB, DP_APPROVAL_NO, DP_TRADE_DD, DP_INCOME_DD, DP_COMM FROM TB_MNG_DEPDATA) A06  ";
		$SQL_QUERY	.= "    ON(A06.DP_CARD_NO=A01.CARD_NO AND A06.DP_APPROVAL_NO=A01.APPROVAL_NO AND A06.DP_TRADE_DD=A01.APPROVAL_DD AND A06.DP_AUTH_GB=A01.PAY_GB) ";
		$SQL_QUERY	.= "    LEFT OUTER JOIN  ";
		$SQL_QUERY	.= "		(SELECT FEE_01, FEE_02, FEE_03, MER_CD, TID_CD FROM TB_BAS_CARDFEE WHERE STR_DD<=TO_CHAR(SYSDATE, 'YYYYMMDD') AND END_DD>TO_CHAR(SYSDATE, 'YYYYMMDD')) A07  ";
		$SQL_QUERY	.= "    ON (A07.MER_CD=A01.MERCHANT_NO AND A07.TID_CD=A01.TID) ";
		$SQL_QUERY	.= $iWhere;

		parent::SqlExec($SQL_QUERY);
		$GetD = parent::fetchInto($SQL_QUERY);
		parent::DBClose();
		return $GetD;
	}

	function DepoMkHead($obj, $obj2){
		if($obj=="HS000001"){
			$rtn	= "H".date("Ymd")."KOCES     HANSSEM             1148511180EDI                                                                                                                                                    ";
		}else if($obj=="HS000002"){
			$rtn	= "S100000000000000000000000000DDC1338122865HANSSEM0022148631917KOCES".DATE("ymd")."          ".STR_PAD($obj2,68," ",STR_PAD_RIGHT);
		}else if($obj=="HS000003"){
			$rtn	= "S100000000000000000000000000DDC1338122865HANSSEM0022148631917KOCES".DATE("ymd")."          ".STR_PAD($obj2,68," ",STR_PAD_RIGHT);
		}
		return $rtn;
	}
	
	/*Array Key값에 해당하는 매입자료의 SALE_SEQ값 리턴*/
	function DepoSeqGet($obj, $obj2){
		for($i=0;$i<count($obj2[$obj]);$i++){
			$rtn[]	=  $obj2[$obj][$i];
		}
		return $rtn;
	}

	function DepoMkFileNm($obj, $obj2){
		
		//$mdate	= date("Ymd");

		if($obj=="HS000001"){//인테리어(HANSSEM)
			$rtn	= "HANSSEM.".$obj2;
		}else if($obj=="HS000002"){//부엌(HB)
			$rtn	= "HB".$obj2.".DAT";
		}else if($obj=="HS000003"){//AS(HA)
			$rtn	= "HA".$obj2.".DAT";
		}
		
		return $rtn;
	}

	function DepoKeyChk($obj){
		$akey	= array_keys($obj);
		for($i=0;$i<count($akey);$i++){
			$rtn[$i]	= $akey[$i];
		}
		return $rtn;
	}
	
	/*$obj:사업부코드, $obj2:회원코드*/
	function DepoUpFolder($obj, $obj2){
		$ntime	= mktime();
		$y	= date("Y");
		$m	= date("m");
		$d	= date("d");

		$def_f	= "D:/DEPTRN";
		$upfold	= $ntime."_".$obj2;

		if($obj=="HS000001"){//인테리어
			$mdf	= "INT";
		}else if($obj=="HS000002"){//부엌
			$mdf	= "KI";
		}else if($obj=="HS000003"){//AS
			$mdf	= "AS";
		}

		$f[0]	= $mdf;
		$f[1]	= $y;
		$f[2]	= $m;
		$f[3]	= $upfold;
		
		$rtn		= $def_f; 
		for($i=0;$i<count($f);$i++){
			$rtn	.= "/".$f[$i];
			if(!is_dir($rtn)){
				mkdir($rtn,0777);
				chmod($rtn,0777);
			}
		}
		
		return $rtn;
	}

	function DKC($obj){
		parent::DBConnect();
		parent::SqlExec($SQL_QUERY);
		$GetD = parent::fetchInto($SQL_QUERY);

		parent::DBClose();
		return $rtn;
	}

	function depPayGb($obj){
		
		if($obj=="A"){
			$rtn = "D1";
		}else if($obj=="C"||$obj=="M"){
			$rtn = "D2";
		}else{
			$rtn = "";	
		}

		return $rtn;
	}

	function depPayGbEDI($obj){
		
		if($obj=="A"){
			$rtn = "CA";
		}else if($obj=="C"||$obj=="M"){
			$rtn = "CC";
		}else{
			$rtn = "  ";	
		}

		return $rtn;
	}

	function AcqCodeEDI($obj){
		if($obj=="HS000001"){
			$rtn	= "01";
		}else if($obj=="HS000003"){
			$rtn	= "02";
		}else if($obj=="HS000007"){
			$rtn	= "03";
		}else if($obj=="HS000004"){
			$rtn	= "04";
		}else if($obj=="HS000002"){
			$rtn	= "06";
		}else if($obj=="HS000006"){
			$rtn	= "07";
		}else if($obj=="HS000019"){
			$rtn	= "09";
		}else if($obj=="HS000020"){
			$rtn	= "10";
		}else if($obj=="HS000014"){
			$rtn	= "13";
		}else if($obj=="HS000012"){
			$rtn	= "14";
		}else if($obj=="HS000008"){
			$rtn	= "16";
		}else if($obj=="HS000009"){
			$rtn	= "17";
		}else if($obj=="HS000018"){
			$rtn	= "20";
		}else if($obj=="HS000016"){
			$rtn	= "21";
		}else if($obj=="HS000017"){
			$rtn	= "22";
		}else if($obj=="HS000005"){
			$rtn	= "27";
		}

		return $rtn;
	}

	function AcqCodeHDDC($obj){
		if($obj=="HS000001"){
			$rtn	= "1101";
		}else if($obj=="HS000003"){
			$rtn	= "1104";
		}else if($obj=="HS000007"){
			$rtn	= "1107";
		}else if($obj=="HS000004"){
			$rtn	= "1105";
		}else if($obj=="HS000002"){
			$rtn	= "1103";
		}else if($obj=="HS000006"){
			$rtn	= "1102";
		}else if($obj=="HS000019"){
			$rtn	= "1180";
		}else if($obj=="HS000020"){
			$rtn	= "1181";
		}else if($obj=="HS000014"){
			$rtn	= "2253";
		}else if($obj=="HS000012"){
			$rtn	= "2207";
		}else if($obj=="HS000008"){
			$rtn	= "2211";
		}else if($obj=="HS000009"){
			$rtn	= "2281";
		}else if($obj=="HS000018"){
			$rtn	= "2237";
		}else if($obj=="HS000016"){
			$rtn	= "2234";
		}else if($obj=="HS000017"){
			$rtn	= "2235";
		}else if($obj=="HS000005"){
			$rtn	= "1106";
		}

		return $rtn;
	}

	function DepoBodyMake2($obj, $obj2, $idx){
		if($obj=="HS000001"){//인테리어(HANSSEM)
			$rtn	= self::KocesEDIBody2($obj2, $idx);
		}else if($obj=="HS000002"||$obj=="HS000003"){//부엌(HB)//AS(HA)
			$rtn	= self::KocesHDCBody2($obj2, $idx);
		}

		return $rtn;
	}

	function KocesHDCBody2($obj,$idx){
		$trbody	 = self::depPayGb($obj[AUTH_DIV]);
		$trbody .= STR_PAD($obj[CARD_NO],16," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD($obj[CARD_VALID],4," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD(substr($obj[APP_DD],2,6),6," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD(substr($obj[OAPP_DD],2,6),6," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD($obj[APP_NO],10," ",STR_PAD_RIGHT);
		$trbody .= "410";
		$trbody .= STR_PAD($obj[AMOUNT],9,"0",STR_PAD_LEFT);
		$trbody .= STR_PAD($obj[AMT_TIP],9,"0",STR_PAD_LEFT);
		$trbody .= STR_PAD($obj[AMOUNT],9,"0",STR_PAD_LEFT);
		$trbody .= STR_PAD($obj[CARD_INST],2,"0",STR_PAD_LEFT);
		$trbody .= STR_PAD("",6," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD($obj[APP_TM],6," ",STR_PAD_LEFT);
		$trbody .= STR_PAD($idx,4,"0",STR_PAD_LEFT);
		$trbody .= "00";
		$trbody .= "1";
		$trbody .= "K";
		$trbody .= STR_PAD($obj[TID],10," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD("1338122865",10," ",STR_PAD_RIGHT);//사업자번호(부엌)
		$tmp_mid	= STR_PAD(TRIM($obj[MID]),14," ",STR_PAD_RIGHT);
		if(strlen($tmp_mid)>14){
			$trbody .= substr($tmp_mid,0,14);
		}else{
			$trbody	.= $tmp_mid;
		}
		//$trbody .= STR_PAD($obj[MID],14," ",STR_PAD_RIGHT);
		
		$acqcd	  = self::AcqCodeHDDC($obj[ACQ_CD]);

		$trbody .= STR_PAD($acqcd,4," ",STR_PAD_RIGHT);
		if($obj[ACQ_CD]=="HS000005"){
			$trbody .= "E";
			$trbody .= "E";
		}else{
			$trbody .= "D";
			$trbody .= "D";
		}
		$trbody .= STR_PAD("",14," ",STR_PAD_RIGHT);
		$trbody	.= "";

		return $trbody;
	}

	function KocesEDIBody2($obj,$idx){

		$trbody	 = "D";
		$trbody .= STR_PAD($idx+1,6,"0",STR_PAD_LEFT);
		$trbody	.= self::depPayGbEDI($obj[AUTH_DIV]);
		$trbody	.= "1";
		$trbody	.= "E";
		$trbody	.= "0";
		$trbody	.= "S";
		$trbody .= STR_PAD(trim($obj[TID]),10," ",STR_PAD_RIGHT);
		$trbody .= self::AcqCodeEDI($obj[ACQ_CD]);
		$trbody .= STR_PAD(trim($obj[MID]),15," ",STR_PAD_RIGHT);
		$trbody .= self::AcqCodeEDI($obj[ACQ_CD]);
		$trbody .= STR_PAD(trim($obj[CARD_NO]),16," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD(trim($obj[CARD_VALID]),4," ",STR_PAD_RIGHT);
		$trbody	.= "410";
		$trbody .= STR_PAD(trim(CEIL($obj[AMOUNT]/1.1)),12,"0",STR_PAD_LEFT);
		$trbody .= STR_PAD(trim($obj[AMT_TIP]),12,"0",STR_PAD_LEFT);
		$trbody .= STR_PAD(trim($obj[AMOUNT]-CEIL($obj[AMOUNT]/1.1)),12,"0",STR_PAD_LEFT);
		$trbody .= STR_PAD(trim($obj[CARD_INST]),2,"0",STR_PAD_LEFT);
		$trbody .= STR_PAD(trim($obj[APP_NO]),8," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD(trim(substr($obj[OAPP_DD],2,6)),6," ",STR_PAD_LEFT);
		$trbody .= STR_PAD(trim(substr($obj[APP_DD],2,6)),6," ",STR_PAD_LEFT);
		$trbody	.= "0000";
		$trbody	.= "0000";
		$trbody .= STR_PAD("",20," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD("",6," ",STR_PAD_RIGHT);
		$trbody	.= "000000000";
		$trbody .= STR_PAD("",6," ",STR_PAD_RIGHT);
		$trbody .= STR_PAD("13381228650000",28," ",STR_PAD_RIGHT);

		return $trbody;
	}
}
?>