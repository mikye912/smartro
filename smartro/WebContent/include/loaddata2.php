<?
include "./../include/oci_connect.php";
//include "./../include/workspace.php";

$SQLQ = new Oci_API;
$SQLQ->DBConnect();

$gcd	= $_GET["tmp"];
$gcd2	= $_GET["tmp2"];

$CorpQry	= "SELECT COUNT(1) MCNT FROM TB_BAS_STORE WHERE DEP_CD='$gcd'";
$SQLQ->SqlExec($CorpQry);
$CorpCnt = $SQLQ->fetchInto($CorpQry);
$CorpMax	= $CorpCnt[0][MCNT];

$CorpSel	= "SELECT STO_CD, STO_NM FROM TB_BAS_STORE WHERE DEP_CD='$gcd'"; 
$SQLQ->SqlExec($CorpSel);
$CorpGet = $SQLQ->fetchInto($CorpSel);
?>


<script>
<!--
	parent.regsave.sto_cd.length = <?=$CorpMax+1?>;
	parent.regsave.sto_cd.options[0].text = '::사업장(매장)선택::';
	parent.regsave.sto_cd.options[0].value = '';
<?
for($i=0;$i<$CorpMax;$i++){
?>
	parent.regsave.sto_cd.options[<?=$i+1?>].text = '<?=$CorpGet[$i][STO_NM]?>';
	parent.regsave.sto_cd.options[<?=$i+1?>].value = '<?=$CorpGet[$i]["STO_CD"]?>';
<?
	if($gcd==$CorpGet[$i]["STO_CD"]){
?>
	parent.regsave.sto_cd.options[<?=$i+1?>].selected=true;
<?
	}
}	
?>
-->
</script>
