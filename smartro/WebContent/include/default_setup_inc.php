<?PHP
header("Content-type:text/html; charset=UTF-8");
include "./include/oci_connect.php";
include "./include/workspace.php";
include "./include/gridextra.php";
require_once ('./include/KISA_SEED_CBC.php');

$SQLQ			= new Oci_API;
$SQLQ->DBConnect();
$GridExt		= new GridExtra;


#USERID|ORGCD|DEPCD|LOGINTIME
$user_dec_txt	= base64_decode($uauth);
$UserExpAuth	= explode("|", $user_dec_txt);

$Obj		= new WorkSpace;
$mGetD		= $Obj->member_get($UserExpAuth[0]);
$MenuArr	= $Obj->general_menu($mGetD[0]['AUTH_SEQ'], $UserExpAuth[1]);
$MenuGet	= $Obj->menu_setup($MenuArr, $uauth);

if($search=="Y"){
	$keywords    = base64_encode($keywords);
	$AddWhere	 = "search={$search}&stime={$stime}&etime={$etime}&cardno={$card_no}&approvalno={$approval_no}&dep_sel={$dep_sel}&sto_sel={$sto_sel}&contract_no={$contract_no}&salesman_no={$salesman_no}";
	$AddWhere	.= "&mer_no={$mer_no}&datekey={$datekey}&".$DEF_URL;
}else{
	$stime		= date("Y-m-d",mktime(0,0,0,date("m"),date("d"),date("Y")));
	$etime		= date("Y-m-d",mktime(0,0,0,date("m"),date("d"),date("Y")));
	$AddWhere	= "stime={$stime}&etime={$etime}&search=Y&".$DEF_URL;
}

$default_org_qry = "select VDETAIL, PDETAIL, DEPO_PORT, CAN_IP, CAN_PORT, USER_LOGO FROM TB_BAS_ORG WHERE ORG_CD='$UserExpAuth[1]'";
$SQLQ->SqlExec($default_org_qry);
$DfOrgD	= $SQLQ->fetchInto($default_org_qry);
?>