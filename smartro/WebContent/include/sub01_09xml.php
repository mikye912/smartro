<?PHP
//include "./../include/xmlheader_cvs.inc.php";
require_once ('./../include/KISA_SEED_CBC.php');


if($stime!=""){		


$WH[]	=	"T1.CAPPDD>='".preg_replace("/[ #\&\+\-%@=\/\\\:;,\.'\"\^`~\_|\!\?\$#<>()\[\]\{\}]/i", "", $stime)."'";		}

if($etime!=""){
		$WH[]	=	"T1.CAPPDD<='".preg_replace("/[ #\&\+\-%@=\/\\\:;,\.'\"\^`~\_|\!\?\$#<>()\[\]\{\}]/i", "", $etime)."'";
	}


/**********************************************************************
* 입력받은 카드번호를 암호화하여 조회
***********************************************************************/
if($cardno!=""){
	$cardarray	= str_split($cardno);
	for($t=0;$t<count($cardarray);$t++){
		$cardhex[$t]	= StrToHex($cardarray[$t]);
	}
	$cardenc = encrypt($g_bszIV, $g_bszUser_key, implode($cardhex, ","));
	$cardenc = strtolower(str_replace(",","",$cardenc));
	$WH[]	= "T1.CARDNO='".$cardenc."'";
}

if(count($WH)>0){
	$ADDWHERE	= " AND ".implode(" and ",$WH);
}

#USERID|ORGCD|DEPCD|LOGINTIME
$user_dec_txt	= base64_decode($uauth);
$UserExpAuth	= explode("|", $user_dec_txt);
if($depcd!=""){$AUTH_WH[] = "DEP_CD='".$depcd."'";}
if($UserExpAuth[1]!=""){$AUTH_WH[] = "org_cd='".$UserExpAuth[1]."'";}
if($UserExpAuth[2]!=""){$AUTH_WH[] = "dep_cd='".$UserExpAuth[2]."'";}

if(count($AUTH_WH)>0){
	$USER_AUTH	= " where ".implode(" AND ", $AUTH_WH);
}

$AUTHQRY	= "SELECT AUTH_QRY01 FROM TB_BAS_USER WHERE ORG_CD='".$UserExpAuth[1]."' AND USER_ID='".$UserExpAuth[0]."'";
$SQLQ->SqlExec($AUTHQRY);
$AuthGetD	= $SQLQ->FetchInto($AUTHQRY);

if(trim($AuthGetD[0][AUTH_QRY01])!=""){
	$AuthQry	= " AND ".$AuthGetD[0][AUTH_QRY01];
}

$SET_WHERE = "WHERE $ADDWHERE";

$qry = ("
SELECT
	CAPPGB,
	CAPPDD,
	COAPPDD,
	CCARDNO,
	CAPPNO,
	CAMOUNT,
	CPID,
	CGCD,
	CGNM,
	CINCOMDD,
	COUTDD,
	CAPPTYPE,
	DELYN,
	REGDT

FROM
	CVS_ADD_INFO T1
$SET_WHERE
order by COAPPDD desc
");


$SQLQ->SqlExec($qry);
$GetD	= $SQLQ->FetchInto($qry);

echo "<?xml version='1.0' encoding='utf-8'?>";
echo '<rows id="0">';
for($i=0;$i<count($GetD);$i++){
	
	if($GetD[$i]['CAPPGB']=="A"){
		$styleb	= "grid_bg_trans_auth";
		$authtxt = "신용승인";
	}elseif($GetD[$i]['CAPPGB']=="C"){
		$styleb	= "grid_bg_trans_canc";
		$authtxt = "신용취소";
	}else{
		$styleb	= "grid_bg_trans_none";
	}

	

	if($GetD[$i][CAPPTYPE]=="1"){
		$cardtype="선불";
	}elseif($GetD[$i][CAPPTYPE]=="2"){
		$cardtype="착불";
	}elseif($GetD[$i][CAPPTYPE]=="3"){
		$cardtype="카드";
	}elseif($GetD[$i][CAPPTYPE]=="4"){
		$cardtype="신용";
	}else{
		$cardtype="";
	}

	
	$card_len = strlen(trim($GetD[$i]['CCARDNO']));
	$card_txt = "";
	if($card_len>0){
		for($e=0;$e<($card_len/2);$e++){
			$s=$e*2;
			$card_ary[$e] = substr($GetD[$i]['CCARDNO'], $s, 2);
		}


		$dec2 = decrypt($g_bszIV, $g_bszUser_key, implode($card_ary, ","));
		$decExp	= explode(",", $dec2);
		for($f=0;$f<count($decExp);$f++){
			if($f>7&&$f<12){
				$card_txt .= "*";
			}else{
				$card_txt .= hexTostr($decExp[$f]);
			}
		}
		
	}else{
		$dec2 = "";
	}

?>

	<row id='<?=($i+1)?>'>
		<cell class='<?=$styleb?>'><?=($i+1)?></cell>
		<cell class='<?=$styleb?>'><?=$GetD[$i]['CGCD']?></cell>
		<cell class='<?=$styleb?>'><?=$GetD[$i]['CGNM']?></cell>
		<cell class='<?=$styleb?>'><?=$GetD[$i]['CPID']?></cell>
		<cell class='<?=$styleb?>'><?=$GetD[$i]['CINCOMDD']?></cell>
		<cell class='<?=$styleb?>'><?=$GetD[$i]['COUTDD']?></cell>		
		<cell class='<?=$styleb?>'><?=$cardtype?></cell>
		<cell class='<?=$styleb?>'><?=$Common->subdate($GetD[$i]['CAPPDD'],"-")?></cell>
		<cell class='<?=$styleb?>'><?=$Common->subdate($GetD[$i]['COAPPDD'],"-")?></cell>
		<cell class='<?=$styleb?>'><?=$GetD[$i]['CAPPNO']?></cell>		
		<cell class='<?=$styleb?>'><?=$authtxt?></cell>		
		//<cell class='<?=$styleb?>'><?=$card_txt?></cell>
		<cell class='<?=$styleb?>'><?=$card_txt?></cell>
		<cell class='<?=$styleb?>'><?=$GetD[$i]['CAMOUNT']?></cell>
	</row>
<?
}
echo '</rows>';
?>