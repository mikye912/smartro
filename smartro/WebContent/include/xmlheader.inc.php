<?php
header("Content-type:text/xml; charset=UTF-8");
session_start();
set_time_limit(0);
include "./../include/oci_connect.php";
include "./../include/commonclass.inc.php";

$SQLQ = new Oci_API;
$SQLQ->DBConnect();
$Common = new Common();

#USERID|ORGCD|DEPCD|LOGINTIME
$user_dec_txt	= base64_decode($uauth);
$UserExpAuth	= explode("|", $user_dec_txt);

$tidcnt		= "select count(1) TIDCNT from tb_bas_tidmst where org_cd='$UserExpAuth[1]' and dep_cd='$UserExpAuth[2]'";
$SQLQ->SqlExec($tidcnt);
$TIDCNT	= $SQLQ->fetchInto($tidcnt);

//로그인 사용자에 따른 검색 조건
if($UserExpAuth[1]!=""){
	$userWhere[]	= "ORG_CD='".$UserExpAuth[1]."'";
}

if($UserExpAuth[2]!=""){
	$userWhere[]	= "DEP_CD='".$UserExpAuth[2]."'";
}

if(count($userWhere)>0){
	$authWhere[]	= " MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE ".implode("AND ",$userWhere).") ";
	$midWhere	= " MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE ".implode("AND ",$userWhere).") ";
}

if($TIDCNT[0][MCNT]>0){
	$authWhere[]	= " TID IN (SELECT TERM_ID FROM TB_BAS_TIDMST WHERE ".implode("AND ",$userWhere).") ";
	$tidWhere	= " TID IN (SELECT TERM_ID FROM TB_BAS_TIDMST WHERE ".implode("AND ",$userWhere).") ";
}

if(count($authWhere)>0){
	$DefWhere		= " WHERE ".implode(" AND ",$authWhere);
}
?>