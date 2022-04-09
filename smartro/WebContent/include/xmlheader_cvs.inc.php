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


?>