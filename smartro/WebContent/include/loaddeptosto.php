<?
include "./../include/oci_connect.php";
include "./../include/commonclass.inc.php";

$SQLQ = new Oci_API;
$SQLQ->DBConnect();
$Common = new Common();

$gcd	= $_GET["tmp"];
$gcd2	= $_GET["tmp2"];


$CorpSel	= "SELECT STO_CD, STO_NM FROM TB_BAS_STORE WHERE DEP_CD='$gcd'"; 
$SQLQ->SqlExec($CorpSel);
$GetD	= $SQLQ->fetchInto($CorpSel);
?>


<script>
<!--
	parent.regsave.<?=$gcd2?>.length = <?=count($GetD)+1?>;
	parent.regsave.<?=$gcd2?>.options[0].text = '::사업장(매장)선택::';
	parent.regsave.<?=$gcd2?>.options[0].value = '';
<?
for($i=1;$i<=count($GetD);$i++){
?>
	parent.regsave.<?=$gcd2?>.options[<?=$i?>].text = '<?=$GetD[$i][STO_NM]?>';
	parent.regsave.<?=$gcd2?>.options[<?=$i?>].value = '<?=$GetD[$i]["STO_CD"]?>';
<?
	if($gcd==$GetD[$i]["STO_CD"]){
?>
	parent.regsave.<?=$gcd2?>.options[<?=$i?>].selected=true;
<?
	}
}	
?>
-->
</script>
