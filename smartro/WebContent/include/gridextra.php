<?
class GridExtra extends Oci_API { 

	function SttQry($p, $obj){
		$spt		= self::SplAddWhere($obj);
		if($spt[search]=="Y"){
			$qry	= self::makeQrySum($p, $spt);
			parent::DBConnect();
			parent::SqlExec($qry);
			$mGet = parent::fetchInto($qry);
			parent::DBClose();
		}

		return $mGet;
	}

	function makeQrySum($p, $obj){
		

		if($p == "P"){
			$DEF_WHERE	= " WHERE (T01.STAT_DIFF='E' OR T01.STAT_DIFF='P') ";
		}elseif($p=="V"){
			$DEF_WHERE	= " WHERE (T01.STAT_DIFF='E' OR T01.STAT_DIFF='V') ";
		}elseif($p=="DR"){
			$DEF_WHERE	= " WHERE (T01.DEPO_DD IS NOT NULL) ";
		}
		
		if($obj[stime]!=""&&$obj[datekey]==""){
			$W[]	= "T01.APP_DD>='".TRIM($obj[stime])."'";
		}elseif($obj[stime]!=""&&$obj[datekey]!=""){
			$W[]	= "T01.".trim($obj[datekey]).">='".TRIM($obj[stime])."'";
		}

		if($obj[etime]!=""&&$obj[datekey]==""){
			$W[]	= "T01.APP_DD<='".TRIM($obj[etime])."'";
		}elseif($obj[etime]!=""&&$obj[datekey]!=""){
			$W[]	= "T01.".trim($obj[datekey])."<='".TRIM($obj[etime])."'";
		}

		if($obj[cardno]!=""){
			$W[]	= "T01.CARD_NO LIKE '%".TRIM($obj[cardno])."%'";
		}

		if($obj[approvalno]!=""){
			$W[]	= "T01.APP_NO LIKE '%".TRIM($obj[approvalno])."%'";
		}

		if($obj[dep_sel]!=""){
			$W[]	= "T01.DEP_CD='".TRIM($obj[dep_sel])."'";
		}

		if($obj[sto_sel]!=""){
			$W[]	= "T01.STO_CD='".TRIM($obj[sto_sel])."'";
		}

		if(count($W)>0){
			$where	= " AND ".implode(" AND ",$W);
		}

		if($p=="P"||$p=="V"){
			$QRY	 = " SELECT ";
			$QRY	.= "     SUM(MCNT01) C1, SUM(MSUM01) S1, SUM(MCNT02) C2, SUM(MSUM02) S2, SUM(MCNT03) C3, SUM(MSUM03) S3 ";
			$QRY	.= " FROM( ";
			$QRY	.= "     SELECT  ";
			$QRY	.= "         COUNT(1) AS MCNT01, SUM(T01.AMOUNT) AS MSUM01, 0 AS MCNT02, 0 AS MSUM02, 0 AS MCNT03, 0 AS MSUM03 ";
			$QRY	.= "     FROM ";
			$QRY	.= "         TB_SALES_MST T01 ";
			$QRY	.= "     ".$DEF_WHERE.$where." AND AUTH_DIV='A'";
			$QRY	.= "     UNION ALL ";
			$QRY	.= "     SELECT  ";
			$QRY	.= "         0 AS MCNT01, 0 AS MSUM01, COUNT(1) AS MCNT02, SUM(AMOUNT) AS MSUM02, 0 AS MCNT03, 0 AS MSUM03 ";
			$QRY	.= "     FROM ";
			$QRY	.= "         TB_SALES_MST T01 ";
			$QRY	.= "     ".$DEF_WHERE.$where." AND AUTH_DIV='C' AND APP_DD!=OAPP_DD ";
			$QRY	.= "     UNION ALL ";
			$QRY	.= "     SELECT  ";
			$QRY	.= "         0 AS MCNT01, 0 AS MSUM01, 0 AS MCNT02, 0 AS MSUM02, COUNT(1) AS MCNT03, SUM(AMOUNT) AS MSUM03 ";
			$QRY	.= "     FROM ";
			$QRY	.= "        TB_SALES_MST T01 ";
			$QRY	.= "     ".$DEF_WHERE.$where." AND AUTH_DIV='C' AND (APP_DD=OAPP_DD OR OAPP_DD IS NULL)";
			$QRY	.= " ) ";
		}else if($p=="DR"){
			$QRY	 = " SELECT ";
			$QRY	.= " 	SUM(MC01) MC01, SUM(MS01) MS01, SUM(MC02) MC02, SUM(MS02) MS02, SUM(MC03) MC03, SUM(MS03) MS03, SUM(MC04) MC04, SUM(MS04) MS04 ";
			$QRY	.= " FROM(     ";
			$QRY	.= " 	SELECT ";
			$QRY	.= " 		COUNT(1) MC01, SUM(AMOUNT) MS01, 0 AS MC02, 0 AS MS02, 0 AS MC03, 0 AS MS03, 0 AS MC04, 0 AS MS04 ";
			$QRY	.= " 	FROM ";
			$QRY	.= " 		TB_SALES_MST T01 ";
			$QRY	.= " 	WHERE ";
			$QRY	.= " 		SALE_RSCCD='60'".$where;
			$QRY	.= " 	UNION ALL  ";
			$QRY	.= " 	SELECT  ";
			$QRY	.= " 		0 AS MC01, 0 AS MS01, COUNT(1) MC02, SUM(AMOUNT) MS02, 0 AS MC03, 0 AS MS03, 0 AS MC04, 0 AS MS04 ";
			$QRY	.= " 	FROM ";
			$QRY	.= " 		TB_SALES_MST T01 ";
			$QRY	.= " 	WHERE ";
			$QRY	.= " 		SALE_RSCCD='67'".$where;
			$QRY	.= " 	UNION ALL  ";
			$QRY	.= " 	SELECT  ";
			$QRY	.= " 		0 AS MC01, 0 AS MS01, 0 AS MC02, 0 AS MS02, COUNT(1) AS MC03, SUM(AMOUNT) AS MS03, 0 AS MC04, 0 AS MS04 ";
			$QRY	.= " 	FROM ";
			$QRY	.= " 		TB_SALES_MST T01 ";
			$QRY	.= " 	WHERE ";
			$QRY	.= " 		SALE_RSCCD='61'".$where;
			$QRY	.= " 	UNION ALL  ";
			$QRY	.= " 	SELECT  ";
			$QRY	.= " 		0 AS MC01, 0 AS MS01, 0 AS MC02, 0 AS MS02, 0 AS MC03, 0 AS MS03, COUNT(1) AS MC04, SUM(AMOUNT) AS MS04 ";
			$QRY	.= " 	FROM ";
			$QRY	.= " 		TB_SALES_MST T01 ";
			$QRY	.= " 	WHERE ";
			$QRY	.= " 		SALE_RSCCD='64'".$where;
			$QRY	.= " ) ";

		}
		return $QRY;
	}

	function SplAddWhere($obj){
		if(strlen($obj)>0){
			$exp	= explode("&",$obj);
			if(count($exp)>0){
				for($i=0;$i<count($exp);$i++){
					$ex2	= explode("=",$exp[$i]);
					$spt[$ex2[0]]	= $ex2[1];
				}
			}
		}

		return $spt;
	}
	
	function SttTpl($p, $obj){
		$GetD		 = self::SttQry($p, $obj);

		if($p=="P"||$p=="V"){
			$TITLE01	= "정상승인";
			$TITLE02	= "청구후 취소";
			$TITLE03	= "당일취소";
			$TITLE04	= "합계";
			$VALUE01	= number_format($GetD[0][C1]);
			$VALUE02	= number_format($GetD[0][S1]);
			$VALUE03	= number_format($GetD[0][C2]);
			$VALUE04	= number_format($GetD[0][S2]);
			$VALUE05	= number_format($GetD[0][C3]);
			$VALUE06	= number_format($GetD[0][S3]);
			$VALUE07	= number_format($GetD[0][C1]+$GetD[0][C2]+$GetD[0][C3]);
			$VALUE08	= number_format($GetD[0][S1]-($GetD[0][S2]+$GetD[0][S3]));
			$STITLE01	= "<span title='정상승인+청구후취소+당일취소'>";
			$STITLE02	= "<span title='정상승인-(청구후취소+당일취소)'>";
		}else if($p=="DR"){
			$TITLE01	= "매출정상";
			$TITLE02	= "매출취소";
			$TITLE03	= "매출반송";
			$TITLE04	= "취소반송";
			$TITLE05	= "매출합계";
			$TITLE06	= "반송합계";
			$VALUE01	= number_format($GetD[0][MC01]);
			$VALUE02	= number_format($GetD[0][MS01]);
			$VALUE03	= number_format($GetD[0][MC02]);
			$VALUE04	= number_format($GetD[0][MS02]);
			$VALUE05	= number_format($GetD[0][MC03]);
			$VALUE06	= number_format($GetD[0][MS03]);
			$VALUE07	= number_format($GetD[0][MC04]);
			$VALUE08	= number_format($GetD[0][MS04]);
		}

		$rtnHtml	 = "<table class='tb00_none' width='100%'>";
		$rtnHtml	.= "<tr>";
		$rtnHtml	.= "	<td>";
		$rtnHtml	.= "		<table class='tb00_none' width='100%'>";
		$rtnHtml	.= "			<tr>";
		$rtnHtml	.= "				<td width='9'><img src='/images/admin/searchbox/searchbox_lefttop.jpg'></td>";
		$rtnHtml	.= "				<td style='background-image:url(/images/admin/searchbox/searchbox_topbg.jpg);'></td>";
		$rtnHtml	.= "				<td width='9'><img src='/images/admin/searchbox/searchbox_righttop.jpg'></td>";
		$rtnHtml	.= "			</tr>";
		$rtnHtml	.= "			<tr>";
		$rtnHtml	.= "				<td style='background-image:url(/images/admin/searchbox/searchbox_leftbg.jpg);'></td>";
		$rtnHtml	.= "				<td>";
		$rtnHtml	.= "					<table width='100%' class='tbp10_none'>";
		$rtnHtml	.= "						<colgroup>";
		$rtnHtml	.= "							<col></col>";
		$rtnHtml	.= "							<col></col>";
		$rtnHtml	.= "							<col></col>";
		$rtnHtml	.= "						</colgroup>";
		$rtnHtml	.= "						<tr>";
		$rtnHtml	.= "							<td height='28' class='mtitle ac bc01'>구분</td>";
		$rtnHtml	.= "							<td height='28' class='mtitle ac'>거래건수</td>";
		$rtnHtml	.= "							<td height='28' class='mtitle ac bc01'>거래금액</td>";
		$rtnHtml	.= "						</tr>";
		$rtnHtml	.= "						<tr>";
		$rtnHtml	.= "							<td colspan='4' class='tdline01'></td>";
		$rtnHtml	.= "						</tr>";
		$rtnHtml	.= "						<tr>";
		$rtnHtml	.= "							<td height='28' class='mtitle al bc01 plr10'>".$TITLE01."</td>";
		$rtnHtml	.= "							<td class='ar'>".$VALUE01." 건</td>";
		$rtnHtml	.= "							<td class='ar bc01'>".$VALUE02." 원</td>";
		$rtnHtml	.= "						</tr>";
		$rtnHtml	.= "						<tr>";
		$rtnHtml	.= "							<td colspan='4' class='tdline01'></td>";
		$rtnHtml	.= "						</tr>";
		$rtnHtml	.= "						<tr>";
		$rtnHtml	.= "							<td height='28' class='mtitle al bc01 plr10'>".$TITLE02."</td>";
		$rtnHtml	.= "							<td class='ar'>".$VALUE03." 건</td>";
		$rtnHtml	.= "							<td class='ar bc01'>".$VALUE04." 원</td>";
		$rtnHtml	.= "						</tr>";
if($p=="DR"){
		$rtnHtml	.= "						<tr>";
		$rtnHtml	.= "							<td colspan='4' class='tdline01'></td>";
		$rtnHtml	.= "						</tr>";

		$rtnHtml	.= "						<tr>";
		$rtnHtml	.= "							<td height='28' class='mtitle al bc01 plr10'>".$TITLE05."</td>";
		$rtnHtml	.= "							<td class='ar'>".number_format($GetD[0][MC01]+$GetD[0][MC02])." 건</td>";
		$rtnHtml	.= "							<td class='ar bc01'>".number_format($GetD[0][MS01]+$GetD[0][MS02])." 원</td>";
		$rtnHtml	.= "						</tr>";
}
		$rtnHtml	.= "					</table>";
		$rtnHtml	.= "				</td>";
		$rtnHtml	.= "				<td style='background-image:url(/images/admin/searchbox/searchbox_rightbg.jpg);'></td>";
		$rtnHtml	.= "			</tr>";
		$rtnHtml	.= "			<tr>";
		$rtnHtml	.= "				<td><img src='/images/admin/searchbox/searchbox_leftbot.jpg'></td>";
		$rtnHtml	.= "				<td style='background-image:url(/images/admin/searchbox/searchbox_botbg.jpg);'></td>";
		$rtnHtml	.= "				<td><img src='/images/admin/searchbox/searchbox_rightbot.jpg'></td>";
		$rtnHtml	.= "			</tr>";
		$rtnHtml	.= "		</table>";
		$rtnHtml	.= "	</td>";
		$rtnHtml	.= "	<td width='10px'></td>";
		$rtnHtml	.= "	<td>";
		$rtnHtml	.= "		<table class='tb00_none' width='100%'>";
		$rtnHtml	.= "			<tr>";
		$rtnHtml	.= "				<td width='9'><img src='/images/admin/searchbox/searchbox_lefttop.jpg'></td>";
		$rtnHtml	.= "				<td style='background-image:url(/images/admin/searchbox/searchbox_topbg.jpg);'></td>";
		$rtnHtml	.= "				<td width='9'><img src='/images/admin/searchbox/searchbox_righttop.jpg'></td>";
		$rtnHtml	.= "			</tr>";
		$rtnHtml	.= "			<tr>";
		$rtnHtml	.= "				<td style='background-image:url(/images/admin/searchbox/searchbox_leftbg.jpg);'></td>";
		$rtnHtml	.= "				<td>";
		$rtnHtml	.= "					<table width='100%' class='tbp10_none'>";
		$rtnHtml	.= "						<colgroup>";
		$rtnHtml	.= "							<col></col>";
		$rtnHtml	.= "							<col></col>";
		$rtnHtml	.= "							<col></col>";
		$rtnHtml	.= "						</colgroup>";
		$rtnHtml	.= "						<tr>";
		$rtnHtml	.= "							<td height='28' class='mtitle ac bc01'>구분</td>";
		$rtnHtml	.= "							<td height='28' class='mtitle ac'>거래 건수</td>";
		$rtnHtml	.= "							<td height='28' class='mtitle ac bc01'>거래 금액</td>";
		$rtnHtml	.= "						</tr>";
		$rtnHtml	.= "						<tr>";
		$rtnHtml	.= "							<td colspan='4' class='tdline01'></td>";
		$rtnHtml	.= "						</tr>";
		$rtnHtml	.= "						<tr>";
		$rtnHtml	.= "							<td height='28' class='mtitle al bc01 plr10'>".$TITLE03."</td>";
		$rtnHtml	.= "							<td class='ar'>".$VALUE05." 건</td>";
		$rtnHtml	.= "							<td class='ar bc01'>".$VALUE06." 원</td>";
		$rtnHtml	.= "						</tr>";
		$rtnHtml	.= "						<tr>";
		$rtnHtml	.= "							<td colspan='4' class='tdline01'></td>";
		$rtnHtml	.= "						</tr>";
		$rtnHtml	.= "						<tr>";
		$rtnHtml	.= "							<td height='28' class='mtitle al bc01 plr10'>".$TITLE04."</td>";
		$rtnHtml	.= "							<td class='ar'>".$VALUE07." 건</span></td>";
		$rtnHtml	.= "							<td class='ar bc01'>".$VALUE08." 원</span></td>";
		$rtnHtml	.= "						</tr>";
if($p=="DR"){
		$rtnHtml	.= "						<tr>";
		$rtnHtml	.= "							<td colspan='4' class='tdline01'></td>";
		$rtnHtml	.= "						</tr>";

		$rtnHtml	.= "						<tr>";
		$rtnHtml	.= "							<td height='28' class='mtitle al bc01 plr10'>".$TITLE06."</td>";
		$rtnHtml	.= "							<td class='ar'>".number_format($GetD[0][MC03]+$GetD[0][MC04])." 건</span></td>";
		$rtnHtml	.= "							<td class='ar bc01'>".number_format($GetD[0][MS03]+$GetD[0][MS04])." 원</span></td>";
		$rtnHtml	.= "						</tr>";
}
		$rtnHtml	.= "					</table>";
		$rtnHtml	.= "				</td>";
		$rtnHtml	.= "				<td style='background-image:url(/images/admin/searchbox/searchbox_rightbg.jpg);'></td>";
		$rtnHtml	.= "			</tr>";
		$rtnHtml	.= "			<tr>";
		$rtnHtml	.= "				<td><img src='/images/admin/searchbox/searchbox_leftbot.jpg'></td>";
		$rtnHtml	.= "				<td style='background-image:url(/images/admin/searchbox/searchbox_botbg.jpg);'></td>";
		$rtnHtml	.= "				<td><img src='/images/admin/searchbox/searchbox_rightbot.jpg'></td>";
		$rtnHtml	.= "			</tr>";
		$rtnHtml	.= "		</table>";
		$rtnHtml	.= "	</td>";
		$rtnHtml	.= "</tr>";
		$rtnHtml	.= "</table>";

		return $rtnHtml;

	}
}
?>