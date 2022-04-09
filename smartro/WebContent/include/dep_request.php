<?
session_start();
include "./include/oci_connect.php";
include "./include/commonclass.inc.php";
include "./include/deposit.php";
$SQLQ = new Oci_API;
$SQLQ->DBConnect();
$Common				= new Common;
$DPSIT	= new DepoSit;

$start_time = array_sum(explode(' ', microtime()));
$UPDATE	= date("Ymd");
$UPTIME	= date("His");
$Dbug		= "OFF";
$TMODE		= "N";

include "./include/procrun.inc.php";
$MSSQLQ = new MSSQL_API;
$MSSQLQ->DBConnect();
$result = mssql_query("SET ANSI_NULLS ON");
$result = mssql_query("SET ANSI_WARNINGS ON");

if($idx==""){
	echo "<script> alert('매입할 자료가 없습니다.');</script>";
}else{
	$expdata	= explode(",",$idx);
	for($e=0;$e<count($expdata);$e++){
		if(!@array_search($expdata[$e],$texx)){
			$texx[]	= $expdata[$e];
			$addw[]	= "SALE_SEQ='".$expdata[$e]."'";
		}
	}
}

//$DepoSitChk	= $DPSIT->DepoDepChk($texx);
//$dkey		= $DPSIT->DepoKeyChk($DepoSitChk);

for($p=1;$p<=3;$p++){
	$DEPCD	= "HS00000".$p;

	$CNTQ	= "SELECT ";
	$CNTQ	.= "    COUNT(1) MCNT ";
	$CNTQ	.= "FROM ";
	$CNTQ	.= "    TB_SALES_MST A01 ";
	$CNTQ	.= "WHERE DEP_CD='".$DEPCD."' AND (".implode(" OR ",$addw).")";

	$SQLQ->SqlExec($CNTQ);
	$GETCNT	= $SQLQ->fetchInto($CNTQ);

	$as01	= 0;
	$as02	= 0;
	$as03	= 0;
	$acnt	= 0;
	$aamt	= 0;
	$ccnt	= 0;
	$camt	= 0;
	$UPSEQ	= "";

	if($GETCNT[0][MCNT]>0){
		/*매입자료 폴더 생성[D:\DEPTRN\사업부[INT, KI, AS]\년\월\UINXTIME_아이디]*/
		$CheckUpFolder	= $DPSIT->DepoUpFolder($DEPCD,$MEMBER_LOGIN_SESSION);
		/*매입자료 파일명 생성*/
		$DpFileNm		= $DPSIT->DepoMkFileNm($DEPCD, $UPDATE);
		/*매입자료 해더 만들기*/
		$DEPOHEAD		= $DPSIT->DepoMkHead($DEPCD,$DpFileNm);

		$dfile = fopen($CheckUpFolder."/".$DpFileNm, "w");

		//$dfile = fopen($CheckUpFolder."/".$DpFileNm, "w");
		fputs ($dfile, $DEPOHEAD);

		$SQL_QUERY	= "SELECT ";
		$SQL_QUERY	.= "    ROWNUM, ";
		$SQL_QUERY	.= "    A01.SALE_SEQ, ";
		$SQL_QUERY	.= "    A01.STAT_DIFF, ";
		$SQL_QUERY	.= "    A01.AUTH_DIV, ";
		$SQL_QUERY	.= "    A01.INST_DIV, ";
		$SQL_QUERY	.= "    A01.CARD_NO, ";
		$SQL_QUERY	.= "    A01.CARD_VALID, ";
		$SQL_QUERY	.= "    A01.CARD_TYPE, ";
		$SQL_QUERY	.= "    A01.APP_NO, ";
		$SQL_QUERY	.= "    A01.APP_DD, ";
		$SQL_QUERY	.= "    A01.APP_TM, ";
		$SQL_QUERY	.= "    A01.OAPP_DD, ";
		$SQL_QUERY	.= "    A01.OAPP_TM, ";
		$SQL_QUERY	.= "    A01.OAPP_NO, ";
		$SQL_QUERY	.= "    A01.CARD_INST, ";
		$SQL_QUERY	.= "    A01.AMOUNT, ";
		$SQL_QUERY	.= "    A01.AMT_TIP, ";
		$SQL_QUERY	.= "    A01.TID, ";
		$SQL_QUERY	.= "    A01.MID, ";
		$SQL_QUERY	.= "    A01.ACQ_CD ";
		$SQL_QUERY	.= "FROM ";
		$SQL_QUERY	.= "    TB_SALES_MST A01 ";
		$SQL_QUERY	.= "WHERE DEP_CD='".$DEPCD."' AND (".implode(" OR ",$addw).")";

		$SQLQ->SqlExec($SQL_QUERY);
		$GetD	= $SQLQ->fetchInto($SQL_QUERY);
		for($i=0;$i<count($GetD);$i++){
			$tBody			= $DPSIT->DepoBodyMake2($DEPCD,$GetD[$i],$i);
			//$tBody	= $GetD[$i][CARD_NO].$GetD[$i][APPROVAL_NO].$GetD[$i][APPROVAL_DD]."\r\n";
			fputs ($dfile, $tBody);

			$as01	= $as01+$GetD[$i][AMOUNT];
			$as02	= $as02+$GetD[$i][AMT_TIP];
			$as03	= $as03+$GetD[$i][AMOUNT];
			if(trim($GetD[$i][AUTH_DIV])=="A"){
				$acnt	= $acnt+1;
				$aamt	= $aamt+$GetD[$i][AMOUNT];
			}elseif(trim($GetD[$i][AUTH_DIV])=="C"||trim($GetD[$i][AUTH_DIV])=="M"){
				$ccnt	= $ccnt+1;
				$camt	= $camt+$GetD[$i][AMOUNT];
			}


			$UPSEQ[]	= "SALE_SEQ='".$GetD[$i][SALE_SEQ]."'";

			if($GetD[$i][AUTH_DIV]=="A"){
				$spAuth	= "02";
			}else if ($GetD[$i][AUTH_DIV]=="C"){
				$spAuth	= "12";
			}

			$dp[0]		 = trim($GetD[$i][APP_DD]);//승인일(취소시 원승인일)
			$dp[1]		 = trim($GetD[$i][CARD_NO]);//카드번호
			$dp[2]		 = trim($GetD[$i][APP_NO]);//승인번호
			$dp[3]		 = trim($spAuth);//승인취소여부
			$dp[4]		 = trim($GetD[$i][AMOUNT]);//거래금액
			$dp[5]		 = trim(date("Ymd"));//요청일
			$dp[6]		 = $MEMBER_LOGIN_SESSION;//작업자
				
			$result2	 = $MSSQLQ->DepoReqInsert($dp);

		}

		if($DEPCD=="HS000001"){
			$as03	= $aamt-$camt;
		}

		/*매입자료 total*/
		$tTotal			= $DPSIT->DepoTotalMake($DEPCD,count($GetD), $as01, $as02, $as03);
		fputs ($dfile, $tTotal);
		fclose($dfile);

		for($as=0;$as<count($GetD);$as=$as+1000){
			$upslice	= array_slice($UPSEQ,$as,1000); 
			
			$SQL_QUERY   = "BEGIN ";
			$SQL_QUERY	.= "UPDATE TB_SALES_MST SET STAT_STP='1', DEPO_DD='".$UPDATE."', DEPO_TM='".$UPTIME."' WHERE ".implode(" OR ",$upslice)."; ";
			$SQL_QUERY	.= "COMMIT; ";
			$SQL_QUERY	.= "END; ";
			$SQLQ->SqlExec($SQL_QUERY);
		}


		if($TMODE!="Y"){
			$fp = fsockopen("127.0.0.1","9011");
			if (!$fp) {
				echo "ERROR: $errno - $errstr<br />\n";
			} else {
				fwrite($fp, $CheckUpFolder."/".$DpFileNm);
				fclose($fp);
			}
		}	
		
	}//자료가 있다면
}



$end_time = array_sum(explode(' ', microtime()));
echo "TIME : ". ( $end_time - $start_time );


echo "<script src='./include/js/jquery-1.8.1.min.js' type='text/javascript'></script>";
echo "<script>parent.reloadxml();</script>";
echo "<script>$('#depoproc_box', parent.document).hide();</script>";
echo "<script>alert('매입청구가 완료되었습니다.');</script>";
?>