<? 
class QueryZip extends Oci_API { 

	function userauth_tidmap($uauth, $depcd){
		#USERID|ORGCD|DEPCD|LOGINTIME
		$user_dec_txt	= base64_decode($uauth);
		$UserExpAuth	= explode("|", $user_dec_txt);
		if($depcd!=""){$AUTH_WH[] = "DEP_CD='".$depcd."'";}
		if($UserExpAuth[1]!=""){$AUTH_WH[] = "ORG_CD='".$UserExpAuth[1]."'";}
		if($UserExpAuth[2]!=""){$AUTH_WH[] = "DEP_CD='".$UserExpAuth[2]."'";}
		
		if(count($AUTH_WH)>0){
			$USER_AUTH	= " WHERE ".implode(" AND ", $AUTH_WH);
		}else{
			$USER_AUTH	= "";
		}

		return $USER_AUTH;
	}

	function select_tidmap($uauth, $depcd){
		$qry	= "SELECT TID FROM TB_BAS_TIDMAP ".$this->userauth_tidmap($uauth, $depcd);
		return $qry;
	}

	function site0101_item($SET_WHERE, $UserExpAuth){  

		$QUERY = ("
		SELECT 
			DEP_NM, TID, TERM_NM, PUR_NM, T1.MER_CD, ACNT, CCNT, AAMT, CAMT
		FROM(
			SELECT
				TID, MER_CD, SUM(ACNT) ACNT, SUM(CCNT) CCNT, SUM(AAMT) AAMT, SUM(CAMT) CAMT
			FROM(
				SELECT
					TID,
					MER_CD, 
					CASE WHEN APP_TP='11' THEN COUNT(1) ELSE 0 END ACNT,
					CASE WHEN APP_TP='11' THEN SUM(AMOUNT) ELSE 0 END AAMT,
					CASE WHEN APP_TP='21' THEN COUNT(1) ELSE 0 END CCNT,
					CASE WHEN APP_TP='21' THEN SUM(AMOUNT) ELSE 0 END CAMT
				FROM 
					".$UserExpAuth[4]."
					".$SET_WHERE."
				GROUP BY TID, MER_CD, APP_TP
			)
			GROUP BY TID, MER_CD
		)T1
		LEFT OUTER JOIN(
			SELECT DISTINCT MER_NO, PUR_CD FROM TB_BAS_MERINFO WHERE ORG_CD='".$UserExpAuth[1]."'
		)TM ON(T1.MER_CD=TM.MER_NO)
		LEFT OUTER JOIN( 
			SELECT PUR_NM, PUR_CD, PUR_SORT FROM TB_BAS_PURINFO 
		)T2 ON(TM.PUR_CD=T2.PUR_CD)
		LEFT OUTER JOIN( 
			SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD='".$UserExpAuth[1]."'
		)T3 ON(T1.TID=T3.TERM_ID)
		LEFT OUTER JOIN( 
			SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART $USER_AUTH
		)T4 ON(T3.DEP_CD=T4.DEP_CD)
		ORDER BY TID, T2.PUR_SORT, T1.MER_CD
		");

		parent::DBConnect(); 
		parent::SqlExec($QUERY);
		$mGet = parent::fetchInto($QUERY);
		parent::DBClose();

		return $mGet;

	}

	function site0101_total($SET_WHERE, $UserExpAuth){
		$QUERY = ("
		SELECT 
			ACNT, CCNT, AAMT, CAMT
		FROM(
			SELECT
				SUM(ACNT) ACNT, SUM(CCNT) CCNT, SUM(AAMT) AAMT, SUM(CAMT) CAMT
			FROM(
				SELECT
					CASE WHEN APP_TP='11' THEN COUNT(1) ELSE 0 END ACNT,
					CASE WHEN APP_TP='11' THEN SUM(AMOUNT) ELSE 0 END AAMT,
					CASE WHEN APP_TP='21' THEN COUNT(1) ELSE 0 END CCNT,
					CASE WHEN APP_TP='21' THEN SUM(AMOUNT) ELSE 0 END CAMT
				FROM 
					".$UserExpAuth[4]."
					$SET_WHERE
				GROUP BY APP_TP
			)
		)
		");

		parent::DBConnect(); 
		parent::SqlExec($QUERY);
		$mGet = parent::fetchInto($QUERY);
		parent::DBClose();

		return $mGet;
	}
}
?>