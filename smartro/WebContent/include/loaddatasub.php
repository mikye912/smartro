<?
include "./../include/oci_connect.php";
$oci->connect();

$gcd	= $_GET["tmp"];
$gcd2	= $_GET["tmp2"];

$CorpQry	= "SELECT COUNT(1) MCNT FROM TB_BAS_STORE WHERE DEP_CD='$gcd'";
$oci->parseExec($CorpQry);
$oci->fetchInto(&$CorpCnt);
$CorpMax	= $CorpCnt["MCNT"];

$CorpSel	= "SELECT STO_CD, STO_NM FROM TB_BAS_STORE WHERE DEP_CD='$gcd'"; 
$oci->parseExec($CorpSel);
for($Corpi=1;$Corpi<=$CorpMax;$Corpi++){
	$oci->fetchInto(&$CorpGet[$Corpi]);
}
?>


<script>
<!--
	parent.regsave.sto_sel.length = <?=$CorpMax+1?>;
	parent.regsave.sto_sel.options[0].text = '::사업장(매장)선택::';
	parent.regsave.sto_sel.options[0].value = '';
<?
for($i=1;$i<=$CorpMax;$i++){
?>
	parent.regsave.sto_sel.options[<?=$i?>].text = '<?=$CorpGet[$i][STO_NM]?>';
	parent.regsave.sto_sel.options[<?=$i?>].value = '<?=$CorpGet[$i]["STO_CD"]?>';
<?
	if($gcd==$CorpGet[$i]["STO_CD"]){
?>
	parent.regsave.sto_sel.options[<?=$i?>].selected=true;
<?
	}
}	
?>
-->
</script>
