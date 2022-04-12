package com.gaon.ifou.v3;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class db2_depo_manager {
	public int get_json_0301item_cnt(String tuser, String stime, String etime, String mid, String acqcd, String depcd) {
		Connection con2 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;

		StringBuffer qrybuf = new StringBuffer();
		StringBuffer pqrybuf = new StringBuffer();
		int icnt = 0;
		int smtsidx = 3;
		try {

			String[] userexp = tuser.split(":");
			String setdc = "";

			pqrybuf.append("select ");
			pqrybuf.append("    count(1) MCNT ");
			pqrybuf.append("from( ");
			pqrybuf.append("    select ");
			pqrybuf.append("        mid, exp_dd, sum(tot_cnt) t_cnt, sum(tot_ban) t_ban, sum(tot_netamt) t_amt ");
			pqrybuf.append("        , sum(tot_inpamt) t_fee, sum(tot_expamt) t_exp, sum(i_cnt) i_cnt, sum(i_ban) i_ban ");
			pqrybuf.append("        , sum(i_amt) i_amt, sum(i_fee) i_fee, sum(i_exp) i_exp ");
			pqrybuf.append("    from( ");
			pqrybuf.append("        select ");
			pqrybuf.append("            mid, exp_dd, dep_seq,sum(tot_cnt) tot_cnt ,sum(ban_cnt) tot_ban,(sum(exp_amt)+sum(inp_amt)) tot_netamt ");
			pqrybuf.append("            ,sum(inp_amt) tot_inpamt, sum(exp_amt) tot_expamt ");
			pqrybuf.append("        from ");
			pqrybuf.append("            tb_mng_deptot ");
			pqrybuf.append("        where ");
			pqrybuf.append("            mid in (select mid from tb_bas_midmap where org_cd=?) ");
			pqrybuf.append("            and exp_dd>=? ");
			pqrybuf.append("            and exp_dd<=? ");

			int mid_idx = 0;
			if(null!=mid&&""!=mid) {
				smtsidx++;
				mid_idx = smtsidx;
				pqrybuf.append("            and mid = ? ");
			}

			pqrybuf.append("        group by mid, exp_dd, dep_seq ");
			pqrybuf.append("        order by exp_dd desc ");
			pqrybuf.append("    )t1 ");
			pqrybuf.append("    left outer join( ");
			pqrybuf.append("        select ");
			pqrybuf.append("            dep_seq ");
			pqrybuf.append("            , (sum(item_cnt60)+sum(item_cnt67)) i_cnt ");
			pqrybuf.append("            , sum(item_cntban) i_ban ");
			pqrybuf.append("            , (sum(item_amt60)-sum(item_amt67)) i_amt ");
			pqrybuf.append("            , (sum(item_fee60)-sum(item_fee67)) i_fee ");
			pqrybuf.append("            , (sum(item_amt60)-sum(item_amt67))-(sum(item_fee60)-sum(item_fee67)) i_exp ");
			pqrybuf.append("        from( ");
			pqrybuf.append("            select ");
			pqrybuf.append("                dep_seq ");
			pqrybuf.append("                ,case when rtn_cd='60' then count(1) else 0 end item_cnt60 ");
			pqrybuf.append("                ,case when rtn_cd='67' then count(1) else 0 end item_cnt67 ");
			pqrybuf.append("                ,case when rtn_cd not in ('60', '67') then count(1) else 0 end item_cntban ");
			pqrybuf.append("                ,case when rtn_cd='60' then sum(sale_amt) else 0 end item_amt60 ");
			pqrybuf.append("                ,case when rtn_cd='67' then sum(sale_amt) else 0 end item_amt67 ");
			pqrybuf.append("                ,case when rtn_cd='60' then sum(fee) else 0 end item_fee60 ");
			pqrybuf.append("                ,case when rtn_cd='67' then sum(fee) else 0 end item_fee67 ");
			pqrybuf.append("            from ");
			pqrybuf.append("                tb_mng_depdata ");
			pqrybuf.append("            where ");
			pqrybuf.append("                mid in (select mid from tb_bas_midmap where org_cd=?) ");
			pqrybuf.append("                and exp_dd>=? ");
			pqrybuf.append("                and exp_dd<=? ");
			pqrybuf.append("            group by dep_seq, rtn_cd ");
			pqrybuf.append("        ) ");
			pqrybuf.append("        group by dep_seq ");
			pqrybuf.append("    )t2 on(t1.dep_seq=t2.dep_seq) ");
			pqrybuf.append("    group by mid, exp_dd ");
			pqrybuf.append(")t1 ");
			pqrybuf.append("left outer join( ");
			pqrybuf.append("    select ");
			pqrybuf.append("        exp_dd ");
			pqrybuf.append("        , mid ");
			pqrybuf.append("        , case when sum(exp_amt) is null then 0 end bank_amt ");
			pqrybuf.append("    from ");
			pqrybuf.append("        tb_mng_bankdata ");
			pqrybuf.append("    group by exp_dd, mid ");
			pqrybuf.append(")t2 on(t1.mid=t2.mid and t1.exp_dd=t2.exp_dd) ");
			pqrybuf.append("    left outer join( ");
			pqrybuf.append("        select org_cd, dep_cd, mer_no, pur_cd from tb_bas_merinfo where org_cd=? ");
			pqrybuf.append("    )t3 on(t1.mid=t3.mer_no) ");
			pqrybuf.append("    left outer join( ");
			pqrybuf.append("        select org_cd, org_nm from tb_bas_org ");
			pqrybuf.append("    )t4 on(t3.org_cd=t4.org_cd) ");
			pqrybuf.append("    left outer join( ");
			pqrybuf.append("        select dep_cd, dep_nm from tb_bas_depart where org_cd=? ");
			pqrybuf.append("    )t5 on(t3.dep_cd=t5.dep_cd) ");
			pqrybuf.append("    left outer join( ");
			pqrybuf.append("        select pur_cd, pur_nm, pur_koces, pur_sort from tb_bas_purinfo ");
			pqrybuf.append("    )t6 on(t3.pur_cd=t6.pur_cd) ");
			pqrybuf.append("order by dep_nm asc, pur_sort asc, pur_nm asc ");

			con2 = getOraConnect();
			stmt2 = con2.prepareStatement(pqrybuf.toString());
			stmt2.setString(1, userexp[1]);
			stmt2.setString(2, stime);
			stmt2.setString(3, etime);

			if(null!=mid&&""!=mid) {stmt2.setString(mid_idx, mid);}

			smtsidx++;
			stmt2.setString(smtsidx, userexp[1]);
			smtsidx++;
			stmt2.setString(smtsidx, stime);
			smtsidx++;
			stmt2.setString(smtsidx, etime);
			smtsidx++;
			stmt2.setString(smtsidx, userexp[1]);
			smtsidx++;
			stmt2.setString(smtsidx, userexp[1]);


			rs2 = stmt2.executeQuery();

			rs2.next();

			icnt = rs2.getInt("MCNT");
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con2,stmt2,rs2);
		}
		return icnt;
	}

	// 입금조회 집계데이터
	public String get_json_0301total(String tuser, String stime, String etime, String acqcd, String depcd, String mid) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		StringBuffer qrybuf = new StringBuffer();
		StringBuffer wherebuf = new StringBuffer();

		//2021.03.02 수정 :: DEPTOT-ACQ_CD가 없어요...
		StringBuffer acqcdBuf = new StringBuffer();

		JSONObject sqlobj = new JSONObject();
		JSONArray objAry = new JSONArray();

		try {
			//tuser, stime, etime, acqcd, depcd, mid
			//tuser split
			String[] userexp = tuser.split(":");
			//acqcd split
			String[] acqcdexp = acqcd.split(",");
			//검색항목에 따른 where 조건절 setting 관련 변수
			ArrayList<String> setting = new ArrayList<>();

			wherebuf.append(" WHERE MID IN (SELECT MID FROM TB_BAS_MIDMAP WHERE ORG_CD = ?  AND DEP_CD = ?) ");
			setting.add(userexp[1]);
			setting.add(userexp[2]);

			
			if(!stime.equals("") && stime != null) {
				wherebuf.append(" AND EXP_DD >= ? ");
				setting.add(stime);
			}

			if(!etime.equals("") && etime != null) {
				wherebuf.append(" AND EXP_DD <= ? ");
				setting.add(etime);
			}
			
			if(!mid.equals("") && mid != null) {
				wherebuf.append(" AND MID = ? ");
				setting.add(mid);
			}

			//1. TID LIST중 LOGIN SESSION에 DEP_CD 설정되어 있을 때
			//1. 검색중 사업부 선택이 있을 때
			if(!depcd.equals("") && depcd != null) {
				if(depcd=="1") {
					wherebuf.append(" AND MID IN ('768017318','00052904921','00951457027','128890479','57296808','151098345','9052663887','151558364','721176212','00098153744','154944840','00903164052','0118721620','179102374','178597603','9956970402' ) ");
				}else if(depcd=="2") {
					wherebuf.append(" AND MID IN ('704855398','00084542316','00986653087','165138860','0104783451','860295101','9969229911','140239694','721219360','00098234952','155068491','00903276708','0118796648','179216357','178600027','9957975427' ) ");
				}else if(depcd=="3") {
					wherebuf.append(" AND MID IN ('707528764','00087259990','00989439518','167802984','0107608507','860386610','9967457077','143275451','721225822','00098235865','155072408','00903280940','0118799154','179216254','178600545','9957971095' )");
				}
			}
			
			if(!acqcd.equals("") && acqcd != null) {
				wherebuf.append(" AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD=? AND ORG_CD=? ) ");
				setting.add(acqcd);
				setting.add(userexp[1]);
			}

			qrybuf.append("SELECT ");
			qrybuf.append("    SUM(T_CNT) TCNT ");
			qrybuf.append("    , SUM(T_BAN) BCNT ");
			qrybuf.append("    , SUM(T_AMT) TAMT ");
			qrybuf.append("    , SUM(T_FEE) INAMT ");
			qrybuf.append("    , SUM(T_EXP) EXAMT ");
			qrybuf.append("    , SUM(I_CNT) ITEMCNT ");
			qrybuf.append("    , SUM(I_BAN) ITEMBAN ");
			qrybuf.append("    , SUM(I_AMT) ITEMAMT ");
			qrybuf.append("    , SUM(I_FEE) ITEMFEE ");
			qrybuf.append("    , SUM(I_EXP) ITEMICOM ");
			qrybuf.append("    , SUM(BANK_AMT) BANKAMT ");
			qrybuf.append("    , SUM(DIFF_ICOM) DIFFICOM ");
			qrybuf.append("    , SUM(DIFF_BANK) DIFFBANK ");
			qrybuf.append("FROM(     ");
			qrybuf.append("    SELECT ");
			qrybuf.append("		T_CNT, T_BAN, T_AMT, T_FEE, T_EXP ");
			qrybuf.append("		, I_CNT, I_BAN, I_AMT, I_FEE, I_EXP ");
			qrybuf.append("		, BANK_AMT, (T_EXP-I_EXP) DIFF_ICOM, (I_EXP-BANK_AMT) DIFF_BANK ");
			qrybuf.append("	FROM(     ");
			qrybuf.append("		SELECT ");
			qrybuf.append("			MID, EXP_DD, SUM(TOT_CNT) T_CNT, SUM(TOT_BAN) T_BAN, SUM(TOT_NETAMT) T_AMT ");
			qrybuf.append("			, SUM(TOT_INPAMT) T_FEE, SUM(TOT_EXPAMT) T_EXP, SUM(I_CNT) I_CNT, SUM(I_BAN) I_BAN ");
			qrybuf.append("			, SUM(I_AMT) I_AMT, SUM(I_FEE) I_FEE, SUM(I_EXP) I_EXP ");
			qrybuf.append("		FROM(     ");
			qrybuf.append("			SELECT ");
			qrybuf.append("				MID, EXP_DD, DEP_SEQ,SUM(TOT_CNT) TOT_CNT ,SUM(BAN_CNT) TOT_BAN,(SUM(EXP_AMT)+SUM(INP_AMT)) TOT_NETAMT ");
			qrybuf.append("				,SUM(INP_AMT) TOT_INPAMT, SUM(EXP_AMT) TOT_EXPAMT ");
			qrybuf.append("			FROM ");
			qrybuf.append("				TB_MNG_DEPTOT  ");
			qrybuf.append(wherebuf.toString());
			qrybuf.append("			GROUP BY MID, EXP_DD, DEP_SEQ   ");
			qrybuf.append("			ORDER BY EXP_DD DESC     ");
			qrybuf.append("		)T1 ");
			qrybuf.append("		LEFT OUTER JOIN( ");
			qrybuf.append("			SELECT ");
			qrybuf.append("				DEP_SEQ ");
			qrybuf.append("				, (SUM(ITEM_CNT60)+SUM(ITEM_CNT67)) I_CNT ");
			qrybuf.append("				, SUM(ITEM_CNTBAN) I_BAN ");
			qrybuf.append("				, (SUM(ITEM_AMT60)-SUM(ITEM_AMT67)) I_AMT ");
			qrybuf.append("				, (SUM(ITEM_FEE60)-SUM(ITEM_FEE67)) I_FEE ");
			qrybuf.append("				, (SUM(ITEM_AMT60)-SUM(ITEM_AMT67))-(SUM(ITEM_FEE60)-SUM(ITEM_FEE67)) I_EXP ");
			qrybuf.append("			FROM(     ");
			qrybuf.append("				SELECT  ");
			qrybuf.append("					DEP_SEQ   ");
			qrybuf.append("					,CASE WHEN RTN_CD='60' THEN COUNT(1) ELSE 0 END ITEM_CNT60 ");
			qrybuf.append("					,CASE WHEN RTN_CD='67' THEN COUNT(1) ELSE 0 END ITEM_CNT67 ");
			qrybuf.append("					,CASE WHEN RTN_CD NOT IN ('60', '67') THEN COUNT(1) ELSE 0 END ITEM_CNTBAN ");
			qrybuf.append("					,CASE WHEN RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END ITEM_AMT60 ");
			qrybuf.append("					,CASE WHEN RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END ITEM_AMT67 ");
			qrybuf.append("					,CASE WHEN RTN_CD='60' THEN SUM(FEE) ELSE 0 END ITEM_FEE60 ");
			qrybuf.append("					,CASE WHEN RTN_CD='67' THEN SUM(FEE) ELSE 0 END ITEM_FEE67 ");
			qrybuf.append("				FROM  ");
			qrybuf.append(userexp[6]);
			qrybuf.append(wherebuf.toString());

			//2021.02.02 wherebuf 두번들어감...ㅠㅠㅠㅠ
			ArrayList<String> preSet = new ArrayList<>();
			int tempNum = 0;
			for(int j = 0; j < 2; j++) {
				tempNum = setting.size() * j;
				for(int i = 0; i<setting.size(); i++) {
					preSet.add(setting.get(i));
				}
			}

			qrybuf.append("				GROUP BY DEP_SEQ, RTN_CD ");
			qrybuf.append("			) ");
			qrybuf.append("			GROUP BY DEP_SEQ ");
			qrybuf.append("		)T2 ON(T1.DEP_SEQ=T2.DEP_SEQ) ");
			qrybuf.append("		GROUP BY MID, EXP_DD ");
			qrybuf.append("	)T1 ");
			qrybuf.append("	LEFT OUTER JOIN( ");
			qrybuf.append("		  SELECT ");
			qrybuf.append("        EXP_DD ");
			qrybuf.append("        , MID ");
			qrybuf.append("        , CASE WHEN SUM(EXP_AMT) IS NULL THEN 0 ELSE SUM(EXP_AMT) END BANK_AMT ");
			qrybuf.append("    FROM  ");
			qrybuf.append("        TB_MNG_BANKDATA WHERE NOT REGEXP_LIKE( exp_amt,'[A-Za-z]|[가-힛]|') AND exp_amt != '' ");
			qrybuf.append("		GROUP BY EXP_DD, MID ");
			qrybuf.append("	)T2 ON(T1.MID=T2.MID AND T1.EXP_DD=T2.EXP_DD) ");
			qrybuf.append("	LEFT OUTER JOIN( ");
			qrybuf.append("		SELECT ORG_CD, DEP_CD, MER_NO, PUR_CD FROM TB_BAS_MERINFO WHERE ORG_CD=? ");
			preSet.add(userexp[1]);
			qrybuf.append("	)T3 ON(T1.MID=T3.MER_NO) ");
			qrybuf.append("	LEFT OUTER JOIN( ");
			qrybuf.append("		SELECT ORG_CD, ORG_NM FROM TB_BAS_ORG ");
			qrybuf.append("	)T4 ON(T3.ORG_CD=T4.ORG_CD) ");
			qrybuf.append("	LEFT OUTER JOIN( ");
			qrybuf.append("		SELECT DEP_CD, DEP_NM FROM TB_BAS_DEPART WHERE ORG_CD=? ");
			preSet.add(userexp[1]);
			qrybuf.append("	)T5 ON(T3.DEP_CD=T5.DEP_CD) ");
			qrybuf.append("	LEFT OUTER JOIN( ");
			qrybuf.append("		SELECT PUR_CD, PUR_NM, PUR_KOCES FROM TB_BAS_PURINFO ");
			qrybuf.append("	)T6 ON(T3.PUR_CD=T6.PUR_CD) ");
			qrybuf.append(") ");

			//디버깅용
			utilm.debug_sql(qrybuf, preSet);

			con = getOraConnect();
			stmt = con.prepareStatement(qrybuf.toString());
			
			for(int k = 0; k < preSet.size(); k++) {
				/*
				String test = preSet.get(k);
				if(test.equals("20211209") ) {
					test = "\'20211209\'";
				}
				System.out.println(test);
				*/
				stmt.setString((k+1), preSet.get(k));
			}
			
			/*
			stmt.setString(1, "OR0003");
			stmt.setString(2, "MD1544676184");
			stmt.setString(3, "20211209");
			stmt.setString(4, "20211209");
			stmt.setString(5, "OR0003");
			stmt.setString(6, "MD1544676184");
			stmt.setString(7, "20211209");
			stmt.setString(8, "20211209");
			stmt.setString(9, "OR0003");
			stmt.setString(10, "OR0003");
			*/
			rs = stmt.executeQuery();

			//TCNT, BCNT, TAMT, INAMT, EXAMT, ITEMCNT, ITEMBAN, ITEMAMT, ITEMFEE, ITEMICOM
			//정상건수, 반송건수, 매출금액, 수수료, 입금액합계, 정상건수, 반송건수, 매출금액, 수수료, 입금액합계
			//BANKAMT 실통장금액, DIFFICOM 입금차액, DIFFBANK 통장차액
			while(rs.next()) {
				JSONObject tempObj = new JSONObject();
				JSONArray tempAry = new JSONArray();

				tempAry.add(utilm.checkNumberData(rs.getString("TCNT")));
				tempAry.add(utilm.checkNumberData(rs.getString("BCNT")));
				tempAry.add(utilm.checkNumberData(rs.getString("TAMT")));
				tempAry.add(utilm.checkNumberData(rs.getString("INAMT")));
				tempAry.add(utilm.checkNumberData(rs.getString("EXAMT")));
				tempAry.add(utilm.checkNumberData(rs.getString("ITEMCNT")));
				tempAry.add(utilm.checkNumberData(rs.getString("ITEMBAN")));
				tempAry.add(utilm.checkNumberData(rs.getString("ITEMAMT")));
				tempAry.add(utilm.checkNumberData(rs.getString("ITEMFEE")));
				tempAry.add(utilm.checkNumberData(rs.getString("ITEMICOM")));				
				tempAry.add(utilm.checkNumberData(rs.getString("BANKAMT")));
				tempAry.add(utilm.checkNumberData(rs.getString("DIFFICOM")));
				tempAry.add(utilm.checkNumberData(rs.getString("DIFFBANK")));

				tempObj.put("id", "total");
				tempObj.put("data", tempAry);

				objAry.add(tempObj);
			}

			sqlobj.put("rows", objAry);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}

		return sqlobj.toJSONString();

	}


	//2021.02.02 입금조회 - item
	public String get_json_0301item(String tuser, String stime, String etime, String acqcd, String depcd, String mid) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		StringBuffer qrybuf = new StringBuffer();
		StringBuffer wherebuf = new StringBuffer();

		//2021.03.02 수정 :: DEPTOT-ACQ_CD가 없어요...
		StringBuffer acqcdBuf = new StringBuffer();

		JSONObject sqlobj = new JSONObject();
		JSONArray objAry = new JSONArray();

		try {
			//tuser split
			String[] userexp = tuser.split(":");
			//acqcd split
			String[] acqcdexp = acqcd.split(",");
			//검색항목에 따른 where 조건절 setting 관련 변수
			ArrayList<String> setting = new ArrayList<>();

			wherebuf.append(" WHERE MID IN (SELECT MID FROM TB_BAS_MIDMAP WHERE ORG_CD = ? AND DEP_CD =? ) ");
			setting.add(userexp[1]);
			setting.add(userexp[2]);
			
			if(!stime.equals("") && stime != null) {
				wherebuf.append(" AND EXP_DD >= ? ");
				setting.add(stime);
			}

			if(!etime.equals("") && etime != null) {
				wherebuf.append(" AND EXP_DD <= ? ");
				setting.add(etime);
			}
			
			if(!mid.equals("") && mid != null) {
				wherebuf.append(" AND MID = ? ");
				setting.add(mid);
			}

			//1. TID LIST중 LOGIN SESSION에 DEP_CD 설정되어 있을 때
			//1. 검색중 사업부 선택이 있을 때
			if(!depcd.equals("") && depcd != null) {
				if(depcd=="1") {
					wherebuf.append(" AND MID IN ('768017318','00052904921','00951457027','128890479','57296808','151098345','9052663887','151558364','721176212','00098153744','154944840','00903164052','0118721620','179102374','178597603','9956970402' ) ");
				}else if(depcd=="2") {
					wherebuf.append(" AND MID IN ('704855398','00084542316','00986653087','165138860','0104783451','860295101','9969229911','140239694','721219360','00098234952','155068491','00903276708','0118796648','179216357','178600027','9957975427' ) ");
				}else if(depcd=="3") {
					wherebuf.append(" AND MID IN ('707528764','00087259990','00989439518','167802984','0107608507','860386610','9967457077','143275451','721225822','00098235865','155072408','00903280940','0118799154','179216254','178600545','9957971095' )");
				}
			}
			
			if(!acqcd.equals("") && acqcd != null) {
				wherebuf.append(" AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD=? AND ORG_CD=? ) ");
				setting.add(acqcd);
				setting.add(userexp[1]);
			}

			qrybuf.append("SELECT ");
			qrybuf.append("	DEP_NM ");
			qrybuf.append("	, T3.DEP_CD DEP_CD ");
			qrybuf.append("	, PUR_NM ");
			qrybuf.append("	, T1.MID MID ");
			qrybuf.append("	, T1.EXP_DD EXP_DD ");
			qrybuf.append("	, T_CNT ");
			qrybuf.append("	, T_BAN ");
			qrybuf.append("	, T_AMT ");
			qrybuf.append("	, T_FEE ");
			qrybuf.append("	, T_EXP ");
			qrybuf.append("	, I_CNT ");
			qrybuf.append("	, I_BAN ");
			qrybuf.append("	, I_AMT ");
			qrybuf.append("	, I_FEE ");
			qrybuf.append("	, I_EXP ");
			qrybuf.append("	, BANK_AMT ");
			qrybuf.append("FROM(     ");
			qrybuf.append("    SELECT ");
			qrybuf.append("         MID ");
			qrybuf.append("         , EXP_DD ");
			qrybuf.append("         , SUM(TOT_CNT) T_CNT ");
			qrybuf.append("         , SUM(TOT_BAN) T_BAN ");
			qrybuf.append("         , SUM(TOT_NETAMT) T_AMT ");
			qrybuf.append("         , SUM(TOT_INPAMT) T_FEE ");
			qrybuf.append("         , SUM(TOT_EXPAMT) T_EXP ");
			qrybuf.append("         , SUM(I_CNT) I_CNT ");
			qrybuf.append("         , SUM(I_BAN) I_BAN ");
			qrybuf.append("         , SUM(I_AMT) I_AMT ");
			qrybuf.append("         , SUM(I_FEE) I_FEE ");
			qrybuf.append("         , SUM(I_EXP) I_EXP ");
			qrybuf.append("    FROM(     ");
			qrybuf.append("        SELECT ");
			qrybuf.append("            MID ");
			qrybuf.append("            , EXP_DD ");
			qrybuf.append("            , DEP_SEQ ");
			qrybuf.append("            , SUM(TOT_CNT) TOT_CNT ");
			qrybuf.append("            , SUM(BAN_CNT) TOT_BAN ");
			qrybuf.append("            , (SUM(EXP_AMT)+SUM(INP_AMT)) TOT_NETAMT ");
			qrybuf.append("            , SUM(INP_AMT) TOT_INPAMT ");
			qrybuf.append("            , SUM(EXP_AMT) TOT_EXPAMT ");
			qrybuf.append("        FROM ");
			qrybuf.append("            TB_MNG_DEPTOT  ");
			qrybuf.append(wherebuf.toString());
			qrybuf.append("        GROUP BY DEP_SEQ, MID, EXP_DD ");
			qrybuf.append("        ORDER BY EXP_DD DESC     ");
			qrybuf.append("    )T1 ");
			qrybuf.append("    LEFT OUTER JOIN( ");
			qrybuf.append("        SELECT ");
			qrybuf.append("            DEP_SEQ ");
			qrybuf.append("            , (SUM(ITEM_CNT60)+SUM(ITEM_CNT67)) I_CNT ");
			qrybuf.append("            , SUM(ITEM_CNTBAN) I_BAN ");
			qrybuf.append("            , (SUM(ITEM_AMT60)-SUM(ITEM_AMT67)) I_AMT ");
			qrybuf.append("            , (SUM(ITEM_FEE60)-SUM(ITEM_FEE67)) I_FEE ");
			qrybuf.append("            , (SUM(ITEM_AMT60)-SUM(ITEM_AMT67))-(SUM(ITEM_FEE60)-SUM(ITEM_FEE67)) I_EXP ");
			qrybuf.append("        FROM(     ");
			qrybuf.append("            SELECT  ");
			qrybuf.append("                DEP_SEQ   ");
			qrybuf.append("                ,CASE WHEN RTN_CD='60' THEN COUNT(1) ELSE 0 END ITEM_CNT60 ");
			qrybuf.append("                ,CASE WHEN RTN_CD='67' THEN COUNT(1) ELSE 0 END ITEM_CNT67 ");
			qrybuf.append("                ,CASE WHEN RTN_CD NOT IN ('60', '67') THEN COUNT(1) ELSE 0 END ITEM_CNTBAN ");
			qrybuf.append("                ,CASE WHEN RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END ITEM_AMT60 ");
			qrybuf.append("                ,CASE WHEN RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END ITEM_AMT67 ");
			qrybuf.append("                ,CASE WHEN RTN_CD='60' THEN SUM(FEE) ELSE 0 END ITEM_FEE60 ");
			qrybuf.append("                ,CASE WHEN RTN_CD='67' THEN SUM(FEE) ELSE 0 END ITEM_FEE67 ");
			qrybuf.append("            FROM  ");
			qrybuf.append(userexp[6]);
			qrybuf.append(wherebuf.toString());

			//2021.02.02 wherebuf 두번들어감...ㅠㅠㅠㅠ
			ArrayList<String> preSet = new ArrayList<>();
			int tempNum = 0;
			for(int j = 0; j < 2; j++) {
				tempNum = setting.size() * j;
				for(int i = 0; i<setting.size(); i++) {
					preSet.add(setting.get(i));
				}
			}

			qrybuf.append("            GROUP BY DEP_SEQ, RTN_CD ");
			qrybuf.append("        ) ");
			qrybuf.append("        GROUP BY DEP_SEQ ");
			qrybuf.append("    )T2 ON(T1.DEP_SEQ=T2.DEP_SEQ) ");
			qrybuf.append("    GROUP BY  MID, EXP_DD ");
			qrybuf.append(")T1 ");
			qrybuf.append("LEFT OUTER JOIN( ");
			qrybuf.append("    SELECT ");
			qrybuf.append("        EXP_DD ");
			qrybuf.append("        , MID ");
			qrybuf.append("        , CASE WHEN SUM(EXP_AMT) IS NULL THEN 0 ELSE SUM(EXP_AMT) END BANK_AMT ");
			qrybuf.append("    FROM  ");
			qrybuf.append("        TB_MNG_BANKDATA WHERE NOT REGEXP_LIKE( exp_amt,'[A-Za-z]|[가-힛]|') AND exp_amt != '' ");
			qrybuf.append("    GROUP BY EXP_DD, MID ");
			qrybuf.append(")T2 ON(T1.MID=T2.MID AND T1.EXP_DD=T2.EXP_DD) ");
			qrybuf.append("LEFT OUTER JOIN( ");
			qrybuf.append("    SELECT ORG_CD, DEP_CD, MER_NO, PUR_CD FROM TB_BAS_MERINFO WHERE ORG_CD=? ");
			preSet.add(userexp[1]);
			qrybuf.append(")T3 ON(T1.MID=T3.MER_NO) ");
			qrybuf.append("LEFT OUTER JOIN( ");
			qrybuf.append("    SELECT ORG_CD, ORG_NM FROM TB_BAS_ORG ");
			qrybuf.append(")T4 ON(T3.ORG_CD=T4.ORG_CD ");
			qrybuf.append(") LEFT OUTER JOIN( ");
			qrybuf.append("    SELECT DEP_CD, DEP_NM FROM TB_BAS_DEPART WHERE ORG_CD=? ");
			preSet.add(userexp[1]);
			qrybuf.append(")T5 ON(T3.DEP_CD=T5.DEP_CD) ");
			qrybuf.append("LEFT OUTER JOIN( ");
			qrybuf.append("SELECT PUR_CD, PUR_NM, PUR_SORT, PUR_KOCES FROM TB_BAS_PURINFO ");
			qrybuf.append(")T6 ON(T3.PUR_CD=T6.PUR_CD) ");
			qrybuf.append("ORDER BY DEP_NM ASC, PUR_NM ASC  ");

			//디버깅용
			utilm.debug_sql(qrybuf, preSet);

			con = getOraConnect();
			stmt = con.prepareStatement(qrybuf.toString());
			for(int k = 0; k < preSet.size(); k++) {
				stmt.setString((k+1), preSet.get(k));
			}
			rs = stmt.executeQuery();

			int rows = 1;

			//2021,02,24 total 합계부분 계산 후 css 추가
			int total_i_cnt = 0, total_i_ban = 0, total_t_cnt = 0, total_t_ban = 0;
			long total_i_amt = 0, total_i_fee = 0, total_i_exp = 0, total_t_amt = 0, total_t_fee = 0, total_t_exp = 0, total_bank_amt = 0;
			while(rs.next()) {
				//DEP_NM, T3.DEP_CD DEP_CD, PUR_NM, T1.MID MID, T1.EXP_DD EXP_DD, T_CNT, T_BAN, T_AMT, T_FEE, T_EXP, I_CNT, I_BAN, I_AMT, I_FEE, I_EXP, BANK_AMT 
				JSONObject tempObj = new JSONObject();
				JSONArray tempAry = new JSONArray();

				int t_cnt = Integer.parseInt(utilm.checkNumberData(rs.getString("T_CNT")));
				int t_ban = Integer.parseInt(utilm.checkNumberData(rs.getString("T_BAN")));
				long t_amt  = Long.parseLong(utilm.checkNumberData(rs.getString("T_AMT")));
				long t_fee  = Long.parseLong(utilm.checkNumberData(rs.getString("T_FEE")));
				long t_exp  = Long.parseLong(utilm.checkNumberData(rs.getString("T_EXP")));

				int i_cnt = Integer.parseInt(utilm.checkNumberData(rs.getString("I_CNT")));
				int i_ban = Integer.parseInt(utilm.checkNumberData(rs.getString("I_BAN")));
				long i_amt  = Long.parseLong(utilm.checkNumberData(rs.getString("I_AMT")));
				long i_fee  = Long.parseLong(utilm.checkNumberData(rs.getDouble("I_FEE")));
				long i_exp  = Long.parseLong(utilm.checkNumberData(rs.getDouble("I_EXP")));

				long bank_amt = Long.parseLong(utilm.checkNumberData(rs.getString("BANK_AMT")));

				total_i_cnt += i_cnt;
				total_i_ban += i_ban;
				total_i_amt += i_amt;
				total_i_fee += i_fee;
				total_i_exp += i_exp;

				total_t_cnt += t_cnt;
				total_t_ban += t_ban;
				total_t_amt += t_amt;
				total_t_fee += t_fee;
				total_t_exp += t_exp;

				total_bank_amt += bank_amt;

				tempAry.add(utilm.setDefault(rs.getString("DEP_NM")));
				tempAry.add(utilm.setDefault(rs.getString("MID")));
				tempAry.add(utilm.setDefault(rs.getString("PUR_NM")));
				tempAry.add(utilm.str_to_dateformat_deposit(rs.getString("EXP_DD")));
				tempAry.add(t_cnt);
				tempAry.add(t_ban);
				tempAry.add(t_amt);
				tempAry.add(t_fee);
				tempAry.add(t_exp);
				tempAry.add(i_cnt);
				tempAry.add(i_ban);
				tempAry.add(i_amt);
				tempAry.add(i_fee);
				tempAry.add(i_exp);
				tempAry.add(bank_amt);

				tempAry.add(t_exp - i_exp);
				tempAry.add(i_exp - bank_amt);

				tempObj.put("id", rows);
				tempObj.put("data", tempAry);

				objAry.add(tempObj);

				rows++;
			}

			//합계부분
			JSONObject tempObj = new JSONObject();
			JSONArray tempAry = new JSONArray();

			tempAry.add("합계");
			tempAry.add("");
			tempAry.add("");
			tempAry.add("");
			tempAry.add(total_t_cnt);
			tempAry.add(total_t_ban);
			tempAry.add(total_t_amt);
			tempAry.add(total_t_fee);
			tempAry.add(total_t_exp);
			tempAry.add(total_i_cnt);
			tempAry.add(total_i_ban);
			tempAry.add(total_i_amt);
			tempAry.add(total_i_fee);
			tempAry.add(total_i_exp);
			tempAry.add(total_bank_amt);

			tempAry.add(total_t_exp - total_i_exp);
			tempAry.add(total_i_exp - total_bank_amt);

			tempObj.put("id", "total");
			tempObj.put("data", tempAry);

			objAry.add(tempObj);

			sqlobj.put("rows", objAry);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}

		return sqlobj.toJSONString();
	}

	//2021.02.25 강원대병원v3 - 입금상세조회 - total
	public String get_json_0301detail_total(String tuser, String stime, String etime, String acqcd, String depcd, String mid, String tid, String appno, String auth01, String auth02, String auth03) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		JSONObject sqlobj = new JSONObject();
		JSONArray sqlAry = new JSONArray();

		StringBuffer pqrybuf = new StringBuffer();
		StringBuffer qrybuf = new StringBuffer();

		//int smtsidx = 4;
		try {

			//tuser split
			String[] userexp = tuser.split(":");
			//acqcd split
			String[] acqcdexp = acqcd.split(",");

			//검색항목에 따른 where 조건절 setting 관련 변수
			ArrayList<String> setting = new ArrayList<>();

			String setdc = "";

			pqrybuf.append("SELECT ");
			pqrybuf.append("    MID, DEP_NM, PUR_NM, PUR_SORT ");
			pqrybuf.append("    , (SUM(ITEM_CNT60)+SUM(ITEM_CNT67)) I_CNT ");
			pqrybuf.append("    , SUM(ITEM_CNTBAN) I_BAN ");
			pqrybuf.append("    , (SUM(ITEM_AMT60)-SUM(ITEM_AMT67)) I_AMT ");
			pqrybuf.append("    , (SUM(ITEM_FEE60)-SUM(ITEM_FEE67)) I_FEE ");
			pqrybuf.append("    , (SUM(ITEM_AMT60)-SUM(ITEM_AMT67))-(SUM(ITEM_FEE60)-SUM(ITEM_FEE67)) I_EXP ");
			pqrybuf.append("FROM( ");
			pqrybuf.append("    SELECT ");
			pqrybuf.append("        MID, DEP_NM, PUR_NM, PUR_SORT ");
			pqrybuf.append("        ,CASE WHEN RTN_CD='60' THEN COUNT(1) ELSE 0 END ITEM_CNT60 ");
			pqrybuf.append("        ,CASE WHEN RTN_CD='67' THEN COUNT(1) ELSE 0 END ITEM_CNT67 ");
			pqrybuf.append("        ,CASE WHEN RTN_CD NOT IN ('60', '67') THEN COUNT(1) ELSE 0 END ITEM_CNTBAN ");
			pqrybuf.append("        ,CASE WHEN RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END ITEM_AMT60 ");
			pqrybuf.append("        ,CASE WHEN RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END ITEM_AMT67 ");
			pqrybuf.append("        ,CASE WHEN RTN_CD='60' THEN SUM(FEE) ELSE 0 END ITEM_FEE60 ");
			pqrybuf.append("        ,CASE WHEN RTN_CD='67' THEN SUM(FEE) ELSE 0 END ITEM_FEE67 ");
			pqrybuf.append("    FROM( ");
			pqrybuf.append("        SELECT ");
			pqrybuf.append("            T1.DEP_CD, CARD_NO, EXP_DD, MID, REQ_DD, TID, RTN_CD, ");
			pqrybuf.append("            REG_DD, HALBU, SALE_AMT, RSC_CD, RS_MSG, ");
			pqrybuf.append("            FEE, DEP_NM, PUR_NM, PUR_SORT ");
			pqrybuf.append("        FROM ");
			pqrybuf.append("            " + userexp[6] + " T1 ");
			pqrybuf.append("        LEFT OUTER JOIN( ");

			pqrybuf.append("            SELECT ORG_CD, DEP_CD, STO_CD, MER_NO, PUR_CD FROM TB_BAS_MERINFO WHERE ORG_CD=? ");
			setting.add(userexp[1]);

			pqrybuf.append("        )T2 ON(T1.MID=T2.MER_NO) ");
			pqrybuf.append("        LEFT OUTER JOIN( ");

			pqrybuf.append("            SELECT TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD=? ");
			setting.add(userexp[1]);

			pqrybuf.append("        )T6 ON(T1.TID=T6.TERM_ID) ");
			pqrybuf.append("        LEFT OUTER JOIN( ");

			pqrybuf.append("            SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART  WHERE ORG_CD=? ");
			setting.add(userexp[1]);

			pqrybuf.append("        )T3 ON(T2.DEP_CD=T3.DEP_CD) ");
			pqrybuf.append("        LEFT OUTER JOIN( ");
			pqrybuf.append("            SELECT PUR_CD, PUR_NM, PUR_SORT,PUR_OCD,PUR_SMART FROM TB_BAS_PURINFO ");
			pqrybuf.append("        )T6 ON(T2.PUR_CD=T6.PUR_CD OR T2.PUR_CD = T6.PUR_SMART) ");
			pqrybuf.append("        WHERE MID IN (SELECT MID FROM TB_BAS_MIDMAP WHERE ORG_CD= ? ");
			setting.add(userexp[1]);
			pqrybuf.append(") ");

			//int stime_idx = 0;
			if(null!=stime&&""!=stime) {
				//smtsidx++;
				//stime_idx = smtsidx;
				pqrybuf.append("            AND EXP_DD>=? ");
				setting.add(stime);
			}

			//int etime_idx = 0;
			if(null!=etime&&""!=etime) {
				//smtsidx++;
				//etime_idx = smtsidx;
				pqrybuf.append("            AND EXP_DD<=? ");
				setting.add(etime);
			}

			//int mid_idx = 0;
			if(null!=mid&&""!=mid) {
				//smtsidx++;
				//mid_idx = smtsidx;
				pqrybuf.append("            AND MID=? ");
				setting.add(mid);
			}

			//int appno_idx = 0;
			if(null!=appno&&""!=appno) {
				//smtsidx++;
				//appno_idx = smtsidx;
				pqrybuf.append("            AND APP_NO=? ");
				setting.add(appno);
			}

			//int tid_idx = 0;
			if(null!=tid&&""!=tid) {
				//smtsidx++;
				//tid_idx = smtsidx;
				pqrybuf.append("            AND TID=? ");
				setting.add(tid);
			}
			
			//1. TID LIST중 LOGIN SESSION에 DEP_CD 설정되어 있을 때
			//1. 검색중 사업부 선택이 있을 때
			if(!depcd.equals("") && depcd != null) {
				if(depcd=="1") {
					pqrybuf.append(" AND MID IN ('768017318','00052904921','00951457027','128890479','57296808','151098345','9052663887','151558364','721176212','00098153744','154944840','00903164052','0118721620','179102374','178597603','9956970402' ) ");
				}else if(depcd=="2") {
					pqrybuf.append(" AND MID IN ('704855398','00084542316','00986653087','165138860','0104783451','860295101','9969229911','140239694','721219360','00098234952','155068491','00903276708','0118796648','179216357','178600027','9957975427' ) ");
				}else if(depcd=="3") {
					pqrybuf.append(" AND MID IN ('707528764','00087259990','00989439518','167802984','0107608507','860386610','9967457077','143275451','721225822','00098235865','155072408','00903280940','0118799154','179216254','178600545','9957971095' )");
				}
			}

			if(!acqcd.equals("") && acqcd != null) {
				pqrybuf.append(" AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD=? AND ORG_CD=? ) ");
				setting.add(acqcd);
				setting.add(userexp[1]);
			}
			
			if(null!=auth01&&""!=auth01) {
				pqrybuf.append("            and rtn_cd in ('61', '64')");
			}

			if(null!=auth02&&""!=auth02) {
				pqrybuf.append("            and rtn_cd = '61'");
			}

			if(null!=auth03&&""!=auth03) {
				pqrybuf.append("            and rtn_cd = '64'");
			}

			pqrybuf.append("    ) ");
			pqrybuf.append("    group by mid, dep_nm, pur_nm, pur_sort, rtn_cd ");
			pqrybuf.append(") ");
			pqrybuf.append("group by mid, dep_nm, pur_nm, pur_sort ");
			pqrybuf.append("order by pur_sort asc");

			//디버깅용
			utilm.debug_sql(pqrybuf, setting);

			con = getOraConnect();
			stmt = con.prepareStatement(pqrybuf.toString());

			for(int k = 0; k<setting.size(); k++) {
				stmt.setString((k+1), setting.get(k));
			}

			rs = stmt.executeQuery();

			int total_cnt = 0, total_ban = 0;
			long total_amt = 0, total_fee = 0, total_exp = 0;

			int icnt = 1;
			while(rs.next()) {
				JSONObject tempObj = new JSONObject();
				JSONArray tempAry = new JSONArray();

				int i_cnt = Integer.parseInt(utilm.checkNumberData(rs.getString("I_CNT")));
				int i_ban = Integer.parseInt(utilm.checkNumberData(rs.getString("I_BAN")));

				long i_amt = Long.parseLong(utilm.checkNumberData(rs.getString("I_AMT")));
				long i_fee = Long.parseLong(utilm.checkNumberData(rs.getDouble("I_FEE")));
				long i_exp = Long.parseLong(utilm.checkNumberData(rs.getDouble("I_EXP")));

				total_cnt += i_cnt;
				total_ban += i_ban;
				total_amt += i_amt;
				total_fee += i_fee;
				total_exp += i_exp;

				tempAry.add(utilm.setDefault(rs.getString("DEP_NM")));
				tempAry.add(utilm.setDefault(rs.getString("MID")));
				tempAry.add(utilm.setDefault(rs.getString("PUR_NM")));
				tempAry.add(i_cnt);
				tempAry.add(i_ban);
				tempAry.add(i_amt);
				tempAry.add(i_fee);
				tempAry.add(i_exp);

				tempObj.put("id", icnt);
				tempObj.put("data", tempAry);

				sqlAry.add(tempObj);
				icnt++;
			}

			//합계부분
			JSONObject tempObj = new JSONObject();
			JSONArray tempAry = new JSONArray();

			tempAry.add("합계");
			tempAry.add("");
			tempAry.add("");
			tempAry.add(total_cnt);
			tempAry.add(total_ban);
			tempAry.add(total_amt);
			tempAry.add(total_fee);
			tempAry.add(total_exp);

			tempObj.put("id", "total");
			tempObj.put("data", tempAry);
			sqlAry.add(0, tempObj);

			sqlobj.put("rows", sqlAry);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}
		return sqlobj.toJSONString();
	}

	//2021.02.25 강원대병원v3 - 입금상세내역 item
	public String get_json_0301detail_item(String tuser, String stime, String etime, String acqcd, String depcd, String mid, String tid, String appno, String auth01, String auth02, String auth03) {
		Connection con2 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		
		JSONObject sqlobj = new JSONObject();
		JSONArray sqlAry = new JSONArray();

		StringBuffer pqrybuf = new StringBuffer();
		StringBuffer qrybuf = new StringBuffer();

		try {

			//tuser split
			String[] userexp = tuser.split(":");

			//acqcd split
			String[] acqcdexp = acqcd.split(",");

			//검색항목에 따른 where 조건절 setting 관련 변수
			ArrayList<String> setting = new ArrayList<>();

			pqrybuf.append("SELECT ");
			pqrybuf.append("    CONCAT(T1.DEP_SEQ, T1.DEP_CD) SEQNO,  ");
			pqrybuf.append("    T1.DEP_CD, CARD_NO, EXP_DD, MID, REQ_DD, TID, RTN_CD, T1.APP_DD, ");
			pqrybuf.append("    REG_DD, HALBU, SALE_AMT, RSC_CD, RS_MSG, T1.APP_NO, T2.DEP_CD DPCD, T2.STO_CD, ");
			pqrybuf.append("    FEE, DEP_NM, STO_NM, PUR_NM, TERM_NM, EXT_FIELD ");
			pqrybuf.append("FROM ");
			pqrybuf.append("    " + userexp[6] + " T1 ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT ORG_CD, DEP_CD, STO_CD, MER_NO, PUR_CD FROM TB_BAS_MERINFO WHERE ORG_CD= ? ");
			setting.add(userexp[1]);
			pqrybuf.append(")T2 ON(T1.MID=T2.MER_NO) ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD= ? ");
			setting.add(userexp[1]);
			pqrybuf.append(")T6 ON(T1.TID=T6.TERM_ID)  ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD= ? ");
			setting.add(userexp[1]);
			pqrybuf.append(")T3 ON(T2.DEP_CD=T3.DEP_CD) ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT STO_NM, STO_CD, DEP_CD, ORG_CD FROM TB_BAS_STORE ");
			pqrybuf.append(")T4 ON(T2.STO_CD=T4.STO_CD AND T2.DEP_CD=T4.DEP_CD AND T2.ORG_CD=T4.ORG_CD) ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT PUR_CD, PUR_NM FROM TB_BAS_PURINFO ");
			pqrybuf.append(")T6 ON(T2.PUR_CD=T6.PUR_CD) ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("	SELECT APPDD, TRANIDX, EXT_FIELD FROM GLOB_MNG_ICVAN ");
			pqrybuf.append(")T7 ON(T1.APP_DD=T7.APPDD AND T1.TRANIDX=T7.TRANIDX) ");
			pqrybuf.append("WHERE MID IN (SELECT MID FROM TB_BAS_MIDMAP  where org_cd= ? ");
			setting.add(userexp[1]);
			
			if(!depcd.equals("") && depcd != null) {
				pqrybuf.append(" AND DEP_CD = ?");
				//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
				if(userexp[2] != null && !userexp[2].equals("")) {
					setting.add(userexp[2]);
				} else {
					setting.add(depcd);
				}
				//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
			} else if(userexp[2] != null && !userexp[2].equals("")) {
				pqrybuf.append(" AND DEP_CD = ?");
				setting.add(userexp[2]);
			}
			pqrybuf.append(") ");

			//int stime_idx = 0;
			if(!stime.equals("") && stime != null) {
				//smtsidx++;
				//stime_idx = smtsidx;
				pqrybuf.append("	AND EXP_DD>=? ");
				setting.add(stime);
			}

			//int etime_idx = 0;
			if(!etime.equals("") && etime != null) {
				//smtsidx++;
				//etime_idx = smtsidx;
				pqrybuf.append("            AND EXP_DD<=? ");
				setting.add(etime);
			}

			//int mid_idx = 0;
			if(!mid.equals("") && mid != null) {
				//smtsidx++;
				//mid_idx = smtsidx;
				pqrybuf.append("            AND MID=? ");
				setting.add(mid);
			}

			//int appno_idx = 0;
			if(!appno.equals("") && appno != null) {
				//smtsidx++;
				//appno_idx = smtsidx;
				pqrybuf.append("            AND APP_NO=? ");
				setting.add(appno);
			}

			//int tid_idx = 0;
			if(!appno.equals("") && appno != null) {
				//smtsidx++;
				//tid_idx = smtsidx;
				pqrybuf.append("            AND TID=? ");
				setting.add(appno);
			}
			
			//1. TID LIST중 LOGIN SESSION에 DEP_CD 설정되어 있을 때
			//1. 검색중 사업부 선택이 있을 때
			if(!depcd.equals("") && depcd != null) {
				if(depcd=="1") {
					pqrybuf.append(" AND MID IN ('768017318','00052904921','00951457027','128890479','57296808','151098345','9052663887','151558364','721176212','00098153744','154944840','00903164052','0118721620','179102374','178597603','9956970402' ) ");
				}else if(depcd=="2") {
					pqrybuf.append(" AND MID IN ('704855398','00084542316','00986653087','165138860','0104783451','860295101','9969229911','140239694','721219360','00098234952','155068491','00903276708','0118796648','179216357','178600027','9957975427' ) ");
				}else if(depcd=="3") {
					pqrybuf.append(" AND MID IN ('707528764','00087259990','00989439518','167802984','0107608507','860386610','9967457077','143275451','721225822','00098235865','155072408','00903280940','0118799154','179216254','178600545','9957971095' )");
				}
			}

			if(!acqcd.equals("") && acqcd != null) {
				pqrybuf.append(" AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD=? AND ORG_CD=? ) ");
				setting.add(acqcd);
				setting.add(userexp[1]);
			}

			if(!auth01.equals("") && auth01 != null) {
				pqrybuf.append("            AND RTN_CD IN ('61', '64')");
			}

			if(!auth02.equals("") && auth02 != null) {
				pqrybuf.append("            AND RTN_CD = '61'");
			}

			if(!auth03.equals("") && auth03 != null) {
				pqrybuf.append("            aND RTN_CD = '64'");
			}


			//디버깅용
			utilm.debug_sql(pqrybuf, setting);

			con2 = getOraConnect();
			stmt2 = con2.prepareStatement(pqrybuf.toString());

			for(int k = 0; k<setting.size(); k++) {
				stmt2.setString((k+1), setting.get(k));
			}

			rs2 = stmt2.executeQuery();
			//rs.setFetchSize(500);

			int icnt = 1;
			while(rs2.next()) {

				JSONObject tempObj = new JSONObject();
				JSONArray tempArr = new JSONArray();

				String depotype = "";
				if(rs2.getString("RTN_CD").equals("60")||rs2.getString("RTN_CD").equals("67")){
					depotype	= "정상매출";
				}else if(rs2.getString("RTN_CD").equals("61")||rs2.getString("RTN_CD").equals("64")){
					depotype	= "매출반송";
				}

				String authtxt = "";
				if(rs2.getString("RTN_CD").equals("60")||rs2.getString("RTN_CD").equals("61")){
					authtxt	= "승인";
				}else{
					authtxt	= "취소";
				}

				String newCardNo = utilm.cardno_masking(trans_seed_manager.seed_dec_card(rs2.getString("CARD_NO").trim()));
				int expamt = Integer.parseInt(rs2.getString("SALE_AMT")) - Integer.parseInt(utilm.checkNumberData(rs2.getDouble("FEE"))); 

				tempArr.add(icnt);
				tempArr.add(utilm.setDefault(rs2.getString("DEP_NM")));
				tempArr.add(utilm.setDefault(rs2.getString("TERM_NM")));
				tempArr.add(utilm.setDefault(rs2.getString("TID")));
				tempArr.add(utilm.setDefault(rs2.getString("PUR_NM")));
				tempArr.add(utilm.setDefault(rs2.getString("MID")));
				tempArr.add(depotype);
				tempArr.add(authtxt);
				tempArr.add(newCardNo);
				tempArr.add(utilm.checkNumberData(rs2.getString("SALE_AMT")));
				tempArr.add(utilm.setDefault(rs2.getString("HALBU")));
				tempArr.add(utilm.setDefault(rs2.getString("APP_NO")));
				tempArr.add(utilm.str_to_dateformat_deposit(rs2.getString("APP_DD")));
				tempArr.add(""); // 승인시간
				tempArr.add(""); // 원승인일자
				tempArr.add(utilm.checkNumberData(rs2.getDouble("FEE")));
				tempArr.add(expamt);
				tempArr.add(utilm.str_to_dateformat_deposit(utilm.setDefault(rs2.getString("REQ_DD"))));
				tempArr.add(utilm.str_to_dateformat_deposit(utilm.setDefault(rs2.getString("REG_DD"))));
				tempArr.add(rs2.getString("RTN_CD"));
				tempArr.add(utilm.str_to_dateformat_deposit(utilm.setDefault(rs2.getString("EXP_DD"))));
				tempArr.add(utilm.setDefault(rs2.getString("EXT_FIELD")));
				tempArr.add(utilm.setDefault(rs2.getString("RS_MSG")));

				tempObj.put("id", rs2.getString("SEQNO"));
				tempObj.put("data", tempArr);

				sqlAry.add(tempObj);
				icnt++;

			} 

			sqlobj.put("rows", sqlAry);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con2,stmt2,rs2);
		}

		return sqlobj.toJSONString();

	}
	
	public String get_json_0302total(String tuser, String stime, String etime, String sappdd, String eappdd, String acqcd, String depcd, String tid, String mid, String appno) {
		Connection con2 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		
		JSONObject jrtnobj = new JSONObject();

		JSONArray arr = new JSONArray();
		StringBuffer pqrybuf = new StringBuffer();
		StringBuffer qrybuf = new StringBuffer();
		
		ArrayList<String> setting = new ArrayList<>();

		try {

			String[] userexp = tuser.split(":");
			String setdc = "";

			pqrybuf.append("select ");
			pqrybuf.append("    t2.term_nm ");
			pqrybuf.append("    ,tid ");
			pqrybuf.append("    ,bc ");
			pqrybuf.append("    ,nh ");
			pqrybuf.append("    ,kb ");
			pqrybuf.append("    ,ss ");
			pqrybuf.append("    ,hn ");
			pqrybuf.append("    ,lo ");
			pqrybuf.append("    ,hd ");
			pqrybuf.append("    ,si ");
			pqrybuf.append("from( ");
			pqrybuf.append("    select ");
			pqrybuf.append("        tid ");
			pqrybuf.append("        ,sum(bca)-sum(bcc) bc ");
			pqrybuf.append("        ,sum(nha)-sum(nhc) nh ");
			pqrybuf.append("        ,sum(kba)-sum(kbc) kb ");
			pqrybuf.append("        ,sum(ssa)-sum(ssc) ss ");
			pqrybuf.append("        ,sum(hna)-sum(hnc) hn ");
			pqrybuf.append("        ,sum(loa)-sum(loc) lo ");
			pqrybuf.append("        ,sum(hda)-sum(hdc) hd ");
			pqrybuf.append("        ,sum(sia)-sum(sic) si ");
			pqrybuf.append("    from( ");
			pqrybuf.append("        select ");
			pqrybuf.append("            tid ");
			pqrybuf.append("			,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0400') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END BCA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0171') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END NHA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0170') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END KBA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1300') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END SSA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0505') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END HNA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1100') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END LOA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1200') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END HDA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0300') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END SIA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0400') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END BCC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0171') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END NHC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0170') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END KBC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1300') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END SSC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0505') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END HNC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1100') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END LOC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1200') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END HDC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0300') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END SIC ");
			pqrybuf.append("        from ");
			pqrybuf.append("            " + userexp[6]);
			pqrybuf.append("        where ");
			pqrybuf.append("            mid in (select mid from tb_bas_midmap where org_cd=?) ");
			setting.add(userexp[1]);

			if(!stime.equals("") && stime != null) {
				pqrybuf.append("            and req_dd>=? ");
				setting.add(stime);
			}

			if(!etime.equals("") && etime != null) {
				pqrybuf.append("            and req_dd<=? ");
				setting.add(etime);
			}

			if(!sappdd.equals("") && sappdd != null) {
				pqrybuf.append("            and app_dd>=? ");
				setting.add(sappdd);
			}

			if(!eappdd.equals("") && eappdd != null) {
				pqrybuf.append("            and app_dd<=? ");
				setting.add(eappdd);
			}

			if(!tid.equals("") && tid != null) {
				pqrybuf.append("            and tid=? ");
				setting.add(tid);
			}

			if(!mid.equals("") && mid != null) {
				pqrybuf.append("            and mid=? ");
				setting.add(mid);
			}

			if(!appno.equals("") && appno != null) {
				pqrybuf.append("            and app_no=? ");
				setting.add(appno);
			}

			if(!acqcd.equals("") && acqcd != null) {
				pqrybuf.append(" AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD=? AND ORG_CD=? ) ");
				setting.add(acqcd);
				setting.add(userexp[1]);
			}

			pqrybuf.append("        group by ");
			pqrybuf.append("            tid, mid, rtn_cd ");
			pqrybuf.append("    ) group by tid ");
			pqrybuf.append(") t1 ");
			pqrybuf.append("left outer join( ");
			pqrybuf.append("    select term_nm, term_id from tb_bas_tidmst ");
			pqrybuf.append(")t2 on(t1.tid=t2.term_id) ");
			
			utilm.debug_sql(pqrybuf, setting);

			con2 = getOraConnect();
			stmt2 = con2.prepareStatement(pqrybuf.toString());
			for(int k = 0; k<setting.size(); k++) {
				stmt2.setString((k+1), setting.get(k));
			}

			rs2 = stmt2.executeQuery();

			int icnt = 1;
			
			long aamt = 0, camt = 0, totcsum = 0, totasum = 0, bctot = 0, nhtot=0, kbtot = 0, sstot = 0, hntot = 0, lotot = 0, hdtot = 0, sitot = 0;
			int acnt = 0, ccnt = 0;			
			while(rs2.next()) {
				JSONObject obj1 = new JSONObject();
				JSONArray arr2 = new JSONArray();
				
				bctot	+=  Integer.parseInt(rs2.getString("BC"));
				kbtot	+=  Integer.parseInt(rs2.getString("KB"));
				hntot	+=  Integer.parseInt(rs2.getString("HN"));
				sstot	+=  Integer.parseInt(rs2.getString("SS"));
				sitot	+=  Integer.parseInt(rs2.getString("SI"));
				hdtot	+=  Integer.parseInt(rs2.getString("HD"));
				lotot	+=  Integer.parseInt(rs2.getString("LO"));
				nhtot	+=  Integer.parseInt(rs2.getString("NH"));

				arr2.add(icnt);
				arr2.add(rs2.getString("TERM_NM"));
				arr2.add(rs2.getString("TID"));
				arr2.add(rs2.getString("BC"));
				arr2.add(rs2.getString("KB"));
				arr2.add(rs2.getString("HN"));
				arr2.add(rs2.getString("SS"));
				arr2.add(rs2.getString("SI"));
				arr2.add(rs2.getString("HD"));
				arr2.add(rs2.getString("LO"));
				arr2.add(rs2.getString("NH"));

				obj1.put("id", Integer.toString(icnt));
				obj1.put("data", arr2);

				arr.add(obj1);
				icnt++;
			}
			
			JSONObject obj1 = new JSONObject();
			JSONArray arr2 = new JSONArray();
			
			arr2.add("합계");
			arr2.add("");
			arr2.add("");
			arr2.add(bctot);
			arr2.add(kbtot);
			arr2.add(hntot);
			arr2.add(sstot);
			arr2.add(sitot);
			arr2.add(hdtot);
			arr2.add(lotot);
			arr2.add(nhtot);
			
			obj1.put("id", "total");
			obj1.put("data", arr2);
			
			arr.add(0, obj1);

			jrtnobj.put("rows", arr);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con2,stmt2,rs2);
		}
		return jrtnobj.toJSONString();
	}

	public String get_json_0302item(String tuser, String stime, String etime, String sappdd, String eappdd, String acqcd, String depcd, String tid, String mid, String appno) {
		Connection con2 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;

		JSONObject jrtnobj = new JSONObject();

		JSONArray arr = new JSONArray();
		StringBuffer pqrybuf = new StringBuffer();
		StringBuffer qrybuf = new StringBuffer();
		
		ArrayList<String> setting = new ArrayList<>();

		try {

			String[] userexp = tuser.split(":");
			String setdc = "";

			pqrybuf.append("select ");
			pqrybuf.append("    t5.dep_nm ");
			pqrybuf.append("    , t2.term_nm ");
			pqrybuf.append("    , t6.pur_nm ");
			pqrybuf.append("    , app_dd ");
			pqrybuf.append("    , req_dd ");
			pqrybuf.append("    , exp_dd ");
			pqrybuf.append("    , tid ");
			pqrybuf.append("    , mid ");
			pqrybuf.append("    , item_cnt ");
			pqrybuf.append("    , item_amt ");
			pqrybuf.append("    , item_fee ");
			pqrybuf.append("    , item_exp ");
			pqrybuf.append("from( ");
			pqrybuf.append("    select ");
			pqrybuf.append("        app_dd ");
			pqrybuf.append("        ,req_dd ");
			pqrybuf.append("        ,exp_dd ");
			pqrybuf.append("        ,tid ");
			pqrybuf.append("        ,mid ");
			pqrybuf.append("        , sum(item_cnt60)+sum(item_cnt67) item_cnt ");
			pqrybuf.append("        , sum(item_amt60)-sum(item_amt67) item_amt ");
			pqrybuf.append("        , sum(item_fee60)-sum(item_fee67) item_fee ");
			pqrybuf.append("        ,(sum(item_amt60)-sum(item_amt67))-(sum(item_fee60)-sum(item_fee67)) item_exp ");
			pqrybuf.append("    from( ");
			pqrybuf.append("        select ");
			pqrybuf.append("            app_dd ");
			pqrybuf.append("            ,req_dd ");
			pqrybuf.append("            ,exp_dd ");
			pqrybuf.append("            ,tid ");
			pqrybuf.append("            ,mid ");  
			pqrybuf.append("            ,rtn_cd ");
			pqrybuf.append("            ,case when rtn_cd='60' then count(1) else 0 end item_cnt60 ");
			pqrybuf.append("            ,case when rtn_cd='67' then count(1) else 0 end item_cnt67 ");
			pqrybuf.append("            ,case when rtn_cd not in ('60', '67') then count(1) else 0 end item_cntban ");
			pqrybuf.append("            ,case when rtn_cd='60' then sum(sale_amt) else 0 end item_amt60 ");
			pqrybuf.append("            ,case when rtn_cd='67' then sum(sale_amt) else 0 end item_amt67 ");
			pqrybuf.append("            ,case when rtn_cd='60' then sum(fee) else 0 end item_fee60 ");
			pqrybuf.append("            ,case when rtn_cd='67' then sum(fee) else 0 end item_fee67 ");
			pqrybuf.append("        from ");
			pqrybuf.append("            " + userexp[6]);
			pqrybuf.append("        where ");
			pqrybuf.append("            mid in (select mid from tb_bas_midmap where org_cd=?) ");
			setting.add(userexp[1]);

			if(!stime.equals("") && stime != null) {
				pqrybuf.append("            and req_dd>=? ");
				setting.add(stime);
			}

			if(!etime.equals("") && etime != null) {
				pqrybuf.append("            and req_dd<=? ");
				setting.add(etime);
			}

			if(!sappdd.equals("") && sappdd != null) {
				pqrybuf.append("            and app_dd>=? ");
				setting.add(sappdd);
			}

			if(!eappdd.equals("") && eappdd != null) {
				pqrybuf.append("            and app_dd<=? ");
				setting.add(eappdd);
			}

			if(!tid.equals("") && tid != null) {
				pqrybuf.append("            and tid=? ");
				setting.add(tid);
			}

			if(!mid.equals("") && mid != null) {
				pqrybuf.append("            and mid=? ");
				setting.add(mid);
			}

			if(!appno.equals("") && appno != null) {
				pqrybuf.append("            and app_no=? ");
				setting.add(appno);
			}

			if(!acqcd.equals("") && acqcd != null) {
				pqrybuf.append(" AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD=? AND ORG_CD=? ) ");
				setting.add(acqcd);
				setting.add(userexp[1]);
			}

			pqrybuf.append("        group by req_dd, app_dd,  exp_dd, mid, tid,  rtn_cd ");
			pqrybuf.append("    ) ");
			pqrybuf.append("    group by req_dd, app_dd, exp_dd, tid, mid ");
			pqrybuf.append(")t1 ");
			pqrybuf.append("left outer join( ");
			pqrybuf.append("    select term_id, term_nm from tb_bas_tidmst where org_cd=? ");
			setting.add(userexp[1]);
			pqrybuf.append(")t2 on(t1.tid=t2.term_id) ");
			pqrybuf.append("left outer join( ");
			pqrybuf.append("    select org_cd, dep_cd, mer_no, pur_cd from tb_bas_merinfo where org_cd=? ");
			setting.add(userexp[1]);
			pqrybuf.append(")t3 on(t1.mid=t3.mer_no) ");
			pqrybuf.append("left outer join( ");
			pqrybuf.append("    select org_cd, org_nm from tb_bas_org ");
			pqrybuf.append(")t4 on(t3.org_cd=t4.org_cd) ");
			pqrybuf.append("left outer join( ");
			pqrybuf.append("    select dep_cd, dep_nm from tb_bas_depart where org_cd=? ");
			setting.add(userexp[1]);
			pqrybuf.append(")t5 on(t3.dep_cd=t5.dep_cd) ");
			pqrybuf.append("left outer join( ");
			pqrybuf.append("    select pur_cd, pur_nm, pur_sort, pur_koces from tb_bas_purinfo ");
			pqrybuf.append(")t6 on(t3.pur_cd=t6.pur_cd) ");
			pqrybuf.append("left outer join( ");
			pqrybuf.append("   select org_cd, user_pur_cd, user_pursort from tb_bas_userpurinfo where org_cd=? ");
			setting.add(userexp[1]);
			pqrybuf.append(")s3 on(t6.pur_cd=s3.user_pur_cd) ");
			pqrybuf.append("where item_cnt>0 ");
			pqrybuf.append("order by t3.dep_cd asc, t1.req_dd asc, user_pursort asc, t1.app_dd asc, t1.exp_dd asc ");
			
			utilm.debug_sql(pqrybuf, setting);

			con2 = getOraConnect();
			stmt2 = con2.prepareStatement(pqrybuf.toString());
			for(int k = 0; k<setting.size(); k++) {
				stmt2.setString((k+1), setting.get(k));
			}

			rs2 = stmt2.executeQuery();

			int icnt = 1;
			
			int count = 0;
			long amount = 0, fee = 0, expamt = 0;
			while(rs2.next()) {
				JSONObject obj1 = new JSONObject();
				JSONArray arr2 = new JSONArray();
				
				int item_cnt = Integer.parseInt(utilm.checkNumberData(rs2.getString("ITEM_CNT")));
				long item_amt = Long.parseLong(utilm.checkNumberData(rs2.getString("ITEM_AMT")));
				long item_fee = Long.parseLong(utilm.checkNumberData(rs2.getDouble("ITEM_FEE")));
				long item_exp = Long.parseLong(utilm.checkNumberData(rs2.getDouble("ITEM_EXP")));

				count += item_cnt;
				amount += item_amt;
				fee += item_fee;
				expamt += item_exp;

				arr2.add(rs2.getString("DEP_NM"));
				arr2.add(rs2.getString("TERM_NM"));
				arr2.add(rs2.getString("TID"));
				arr2.add(rs2.getString("MID"));
				arr2.add(rs2.getString("PUR_NM"));
				arr2.add(utilm.str_to_dateformat_deposit(rs2.getString("REQ_DD")));
				arr2.add(utilm.str_to_dateformat_deposit(rs2.getString("APP_DD")));
				arr2.add(utilm.str_to_dateformat_deposit(rs2.getString("EXP_DD")));
				arr2.add(rs2.getString("ITEM_CNT"));
				arr2.add(rs2.getString("ITEM_AMT"));
				arr2.add(rs2.getDouble("ITEM_FEE"));
				arr2.add(rs2.getDouble("ITEM_EXP"));

				obj1.put("id", Integer.toString(icnt));
				obj1.put("data", arr2);

				arr.add(obj1);
				icnt++;
			} 
			
			//합계부분
			JSONObject tempObj = new JSONObject();
			JSONArray tempArr = new JSONArray();

			tempArr.add("합계");
			tempArr.add("");
			tempArr.add("");
			tempArr.add("");
			tempArr.add("");
			tempArr.add("");
			tempArr.add("");
			tempArr.add("");
			tempArr.add(count);
			tempArr.add(amount);
			tempArr.add(fee);
			tempArr.add(expamt);

			tempObj.put("id", "total");
			tempObj.put("data", tempArr);

			arr.add(tempObj);

			jrtnobj.put("rows", arr);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con2,stmt2,rs2);
		}
		return jrtnobj.toJSONString();
	}
	
	public String get_json_0302detail_total(String tuser, String stime, String etime, String sappdd, String eappdd, String sexpdd, String eexpdd, String appno, String tid, String mid, String acqcd, String depcd) {
		Connection con2 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		
		JSONObject jrtnobj = new JSONObject();

		JSONArray arr = new JSONArray();
		StringBuffer pqrybuf = new StringBuffer();
		StringBuffer qrybuf = new StringBuffer();
		
		ArrayList<String> setting = new ArrayList<>();

		try {

			String[] userexp = tuser.split(":");
			String setdc = "";

			pqrybuf.append("SELECT ");
			pqrybuf.append("	T2.TERM_NM ");
			pqrybuf.append("	,TID ");
			pqrybuf.append("	,BC ");
			pqrybuf.append("	,NH ");
			pqrybuf.append("	,KB ");
			pqrybuf.append("	,SS ");
			pqrybuf.append("	,HN ");
			pqrybuf.append("	,LO ");
			pqrybuf.append("	,HD ");
			pqrybuf.append("	,SI ");
			pqrybuf.append("FROM( ");
			pqrybuf.append("	SELECT ");
			pqrybuf.append("		TID ");
			pqrybuf.append("		,SUM(BCA)-SUM(BCC) BC ");
			pqrybuf.append("		,SUM(NHA)-SUM(NHC) NH ");
			pqrybuf.append("		,SUM(KBA)-SUM(KBC) KB ");
			pqrybuf.append("		,SUM(SSA)-SUM(SSC) SS ");
			pqrybuf.append("		,SUM(HNA)-SUM(HNC) HN ");
			pqrybuf.append("		,SUM(LOA)-SUM(LOC) LO ");
			pqrybuf.append("		,SUM(HDA)-SUM(HDC) HD ");
			pqrybuf.append("		,SUM(SIA)-SUM(SIC) SI ");
			pqrybuf.append("	FROM( ");
			pqrybuf.append("		SELECT  ");
			pqrybuf.append("			TID ");
			pqrybuf.append("			,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0400') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END BCA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0171') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END NHA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0170') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END KBA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1300') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END SSA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0505') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END HNA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1100') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END LOA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1200') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END HDA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0300') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END SIA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0400') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END BCC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0171') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END NHC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0170') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END KBC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1300') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END SSC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0505') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END HNC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1100') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END LOC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1200') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END HDC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0300') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END SIC ");
			pqrybuf.append("		FROM ");
			pqrybuf.append(userexp[6]);
			pqrybuf.append("        WHERE MID IN (SELECT MID FROM TB_BAS_MIDMAP WHERE ORG_CD=?) ");
			setting.add(userexp[1]);

			if(!stime.equals("") && stime != null) {
				pqrybuf.append("            AND REQ_DD>=? ");
				setting.add(stime);
			}

			if(!etime.equals("") && etime != null) {
				pqrybuf.append("            AND REQ_DD<=? ");
				setting.add(etime);
			}

			if(!sappdd.equals("") && sappdd != null) {
				pqrybuf.append("            AND APP_DD>=? ");
				setting.add(sappdd);
			}

			if(!eappdd.equals("") && eappdd != null) {
				pqrybuf.append("            AND APP_DD<=? ");
				setting.add(eappdd);
			}

			if(!sexpdd.equals("") && sexpdd != null) {
				pqrybuf.append("            AND EXP_DD>=? ");
				setting.add(sexpdd);
			}

			if(!eexpdd.equals("") && eexpdd != null) {
				pqrybuf.append("            AND EXP_DD<=? ");
				setting.add(eexpdd);
			}

			if(!tid.equals("") && tid != null) {
				pqrybuf.append("            AND TID=? ");
				setting.add(tid);
			}

			if(!mid.equals("") && mid != null) {
				pqrybuf.append("            AND MID=? ");
				setting.add(mid);
			}

			if(!appno.equals("") && appno != null) {
				pqrybuf.append("            AND APP_NO=? ");
				setting.add(appno);
			}

			if(!acqcd.equals("") && acqcd != null) {
				pqrybuf.append(" AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD=? AND ORG_CD=? ) ");
				setting.add(acqcd);
				setting.add(userexp[1]);
			}

			pqrybuf.append("		GROUP BY  ");
			pqrybuf.append("			TID, ACQ_CD, RTN_CD, MID ");
			pqrybuf.append("	) GROUP BY TID ");
			pqrybuf.append(") T1 ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT TERM_NM, TERM_ID FROM TB_BAS_TIDMST ");
			pqrybuf.append(")T2 ON(T1.TID=T2.TERM_ID) ");
			
			utilm.debug_sql(pqrybuf, setting);

			con2 = getOraConnect();
			stmt2 = con2.prepareStatement(pqrybuf.toString());
			for(int k = 0; k<setting.size(); k++) {
				stmt2.setString((k+1), setting.get(k));
			}

			rs2 = stmt2.executeQuery();

			int icnt = 1;
			long aamt = 0, camt = 0, totcsum = 0, totasum = 0, bctot = 0, nhtot=0, kbtot = 0, sstot = 0, hntot = 0, lotot = 0, hdtot = 0, sitot = 0;
			while(rs2.next()) {
				JSONObject obj1 = new JSONObject();
				JSONArray arr2 = new JSONArray();
				
				bctot	+=  Integer.parseInt(rs2.getString("BC"));
				kbtot	+=  Integer.parseInt(rs2.getString("KB"));
				hntot	+=  Integer.parseInt(rs2.getString("HN"));
				sstot	+=  Integer.parseInt(rs2.getString("SS"));
				sitot	+=  Integer.parseInt(rs2.getString("SI"));
				hdtot	+=  Integer.parseInt(rs2.getString("HD"));
				lotot	+=  Integer.parseInt(rs2.getString("LO"));
				nhtot	+=  Integer.parseInt(rs2.getString("NH"));	

				arr2.add(icnt);
				arr2.add(rs2.getString("TERM_NM"));
				arr2.add(rs2.getString("TID"));
				arr2.add(rs2.getString("BC"));
				arr2.add(rs2.getString("KB"));
				arr2.add(rs2.getString("HN"));
				arr2.add(rs2.getString("SS"));
				arr2.add(rs2.getString("SI"));
				arr2.add(rs2.getString("HD"));
				arr2.add(rs2.getString("LO"));
				arr2.add(rs2.getString("NH"));

				obj1.put("id", Integer.toString(icnt));
				obj1.put("data", arr2);

				arr.add(obj1);
				icnt++;
			}
			JSONObject obj1 = new JSONObject();
			JSONArray arr2 = new JSONArray();
			
			arr2.add("합계");
			arr2.add("");
			arr2.add("");
			arr2.add(bctot);
			arr2.add(kbtot);
			arr2.add(hntot);
			arr2.add(sstot);
			arr2.add(sitot);
			arr2.add(hdtot);
			arr2.add(lotot);
			arr2.add(nhtot);

			obj1.put("id", "total");
			obj1.put("data", arr2);
			
			arr.add(0, obj1);

			jrtnobj.put("rows", arr);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con2,stmt2,rs2);
		}
		return jrtnobj.toJSONString();
	}

	public String get_json_0302detail_item(String tuser, String stime, String etime, String sappdd, String eappdd, String sexpdd, String eexpdd, String appno, String tid, String mid, String acqcd, String depcd) {
		Connection con2 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		
		JSONObject jrtnobj = new JSONObject();

		JSONArray arr = new JSONArray();
		StringBuffer pqrybuf = new StringBuffer();
		StringBuffer qrybuf = new StringBuffer();
		
		ArrayList<String> setting = new ArrayList<>();

		try {

			String[] userexp = tuser.split(":");
			String setdc = "";

			pqrybuf.append("SELECT ");
			pqrybuf.append("    CONCAT(T1.DEP_SEQ, T1.DEP_CD) SEQNO ");
			pqrybuf.append("	,T3.DEP_NM ");
			pqrybuf.append("	,T5.TERM_NM ");
			pqrybuf.append("	,TID ");
			pqrybuf.append("	,T6.PUR_NM ");
			pqrybuf.append("	,MID ");
			pqrybuf.append("	,CASE WHEN RTN_CD IN ('60', '67') THEN '정상매출' ELSE '매출반송' END RTN_TXT ");
			pqrybuf.append("	,CASE WHEN RTN_CD IN ('60', '61') THEN '승인' WHEN RTN_CD IN ('64','67') THEN '취소' END AUTHTXT ");
			pqrybuf.append("	,CARD_NO ");
			pqrybuf.append("	,SALE_AMT AMOUNT ");
			pqrybuf.append("	,HALBU ");
			pqrybuf.append("	,APP_NO ");
			pqrybuf.append("	,APP_DD ");
			pqrybuf.append("	,OAPP_DD ");
			pqrybuf.append("	,FEE ");
			pqrybuf.append("	,SALE_AMT-FEE EXP_AMT ");
			pqrybuf.append("	,REQ_DD ");
			pqrybuf.append("	,REG_DD ");
			pqrybuf.append("	,RTN_CD ");
			pqrybuf.append("	,EXP_DD ");
			pqrybuf.append("	,RS_MSG ");
			pqrybuf.append("FROM ");
			pqrybuf.append("    TB_MNG_DEPDATA T1 ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT ORG_CD, DEP_CD, STO_CD, MER_NO, PUR_CD FROM TB_BAS_MERINFO WHERE ORG_CD=? ");
			setting.add(userexp[1]);
			pqrybuf.append(")T2 ON(T1.MID=T2.MER_NO) ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD=? ");
			setting.add(userexp[1]);
			pqrybuf.append(")T3 ON(T2.DEP_CD=T3.DEP_CD) ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT STO_NM, STO_CD, DEP_CD, ORG_CD FROM TB_BAS_STORE ");
			pqrybuf.append(")T4 ON(T2.STO_CD=T4.STO_CD AND T2.DEP_CD=T4.DEP_CD AND T2.ORG_CD=T4.ORG_CD) ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD=? ");
			setting.add(userexp[1]);
			pqrybuf.append(")T5 ON(T1.TID=T5.TERM_ID) ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT PUR_CD, PUR_NM FROM TB_BAS_PURINFO ");
			pqrybuf.append(")T6 ON(T2.PUR_CD=T6.PUR_CD) ");
			pqrybuf.append("WHERE ");
			pqrybuf.append("	MID IN (SELECT MID FROM TB_BAS_MIDMAP WHERE ORG_CD=?) ");
			setting.add(userexp[1]);

			if(!stime.equals("") && stime != null) {
				pqrybuf.append("            AND REQ_DD>=? ");
				setting.add(stime);
			}

			if(!etime.equals("") && etime != null) {
				pqrybuf.append("            AND REQ_DD<=? ");
				setting.add(etime);
			}

			if(!sappdd.equals("") && sappdd != null) {
				pqrybuf.append("            AND APP_DD>=? ");
				setting.add(sappdd);
			}

			if(!eappdd.equals("") && eappdd != null) {
				pqrybuf.append("            AND APP_DD<=? ");
				setting.add(eappdd);
			}

			if(!sexpdd.equals("") && sexpdd != null) {
				pqrybuf.append("            AND EXP_DD>=? ");
				setting.add(sexpdd);
			}

			if(!eexpdd.equals("") && eexpdd != null) {
				pqrybuf.append("            AND EXP_DD<=? ");
				setting.add(eexpdd);
			}

			if(!tid.equals("") && tid != null) {
				pqrybuf.append("            AND TID=? ");
				setting.add(tid);
			}

			if(!mid.equals("") && mid != null) {
				pqrybuf.append("            AND MID=? ");
				setting.add(mid);
			}

			if(!appno.equals("") && appno != null) {
				pqrybuf.append("            AND APP_NO=? ");
				setting.add(appno);
			}

			if(!acqcd.equals("") && acqcd != null) {
				pqrybuf.append(" AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD=? AND ORG_CD=? ) ");
				setting.add(acqcd);
				setting.add(userexp[1]);
			}

			utilm.debug_sql(pqrybuf, setting);

			con2 = getOraConnect();
			stmt2 = con2.prepareStatement(pqrybuf.toString());
			for(int k = 0; k<setting.size(); k++) {
				stmt2.setString((k+1), setting.get(k));
			}

			rs2 = stmt2.executeQuery();

			int icnt = 1;
			while(rs2.next()) {
				JSONObject obj1 = new JSONObject();
				JSONArray arr2 = new JSONArray();

				arr2.add(icnt);
				arr2.add(rs2.getString("DEP_NM"));
				arr2.add(rs2.getString("TERM_NM"));
				arr2.add(rs2.getString("TID"));
				arr2.add(rs2.getString("PUR_NM"));
				arr2.add(rs2.getString("MID"));
				arr2.add(rs2.getString("RTN_TXT"));
				arr2.add(rs2.getString("AUTHTXT"));
				String cardNo = utilm.cardno_masking(trans_seed_manager.seed_dec_card(rs2.getString("CARD_NO").trim()));
				arr2.add(cardNo);
				arr2.add(rs2.getString("AMOUNT"));
				arr2.add(rs2.getString("HALBU"));
				arr2.add(rs2.getString("APP_NO"));
				arr2.add(utilm.str_to_dateformat_deposit(rs2.getString("APP_DD")));
				arr2.add("");
				arr2.add(utilm.setDefault(rs2.getString("OAPP_DD")));
				arr2.add(utilm.checkNumberData(rs2.getDouble("FEE")));
				arr2.add(utilm.checkNumberData(rs2.getDouble("EXP_AMT")));
				arr2.add(utilm.str_to_dateformat_deposit(rs2.getString("REQ_DD")));
				arr2.add(utilm.str_to_dateformat_deposit(rs2.getString("REG_DD")));
				arr2.add(rs2.getString("RTN_CD"));
				arr2.add(utilm.str_to_dateformat_deposit(rs2.getString("EXP_DD")));
				arr2.add(utilm.setDefault(rs2.getString("RS_MSG")));
			
				obj1.put("id", Integer.toString(icnt));
				obj1.put("data", arr2);

				arr.add(obj1);
				icnt++;
			} 

			jrtnobj.put("rows", arr);

		}catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con2,stmt2,rs2);
		}
		return jrtnobj.toJSONString();
	}

	//2021.02.26 강원대병원v3 - 거래일자별조회 total
	public String get_json_0303total(String tuser, String stime, String etime, String sreqdd, String ereqdd, String acqcd, String depcd, String tid, String mid, String appno) {
		Connection con2 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		
		JSONObject sqlobj = new JSONObject();
		JSONArray sqlAry = new JSONArray();

		StringBuffer pqrybuf = new StringBuffer();
		StringBuffer qrybuf = new StringBuffer();

		int smtsidx = 1;

		try {

			//tuser split
			String[] userexp = tuser.split(":");

			//acqcd split
			String[] acqcdexp = acqcd.split(",");

			//검색항목에 따른 where 조건절 setting 관련 변수
			ArrayList<String> setting = new ArrayList<>();

			String setdc = "";

			pqrybuf.append("SELECT ");
			pqrybuf.append("	T2.TERM_NM ");
			pqrybuf.append("	,TID ");
			pqrybuf.append("	,BC ");
			pqrybuf.append("	,NH ");
			pqrybuf.append("	,KB ");
			pqrybuf.append("	,SS ");
			pqrybuf.append("	,HN ");
			pqrybuf.append("	,LO ");
			pqrybuf.append("	,HD ");
			pqrybuf.append("	,SI ");
			pqrybuf.append("FROM( ");
			pqrybuf.append("	SELECT ");
			pqrybuf.append("		TID ");
			pqrybuf.append("		,SUM(BCA)-SUM(BCC) BC ");
			pqrybuf.append("		,SUM(NHA)-SUM(NHC) NH ");
			pqrybuf.append("		,SUM(KBA)-SUM(KBC) KB ");
			pqrybuf.append("		,SUM(SSA)-SUM(SSC) SS ");
			pqrybuf.append("		,SUM(HNA)-SUM(HNC) HN ");
			pqrybuf.append("		,SUM(LOA)-SUM(LOC) LO ");
			pqrybuf.append("		,SUM(HDA)-SUM(HDC) HD ");
			pqrybuf.append("		,SUM(SIA)-SUM(SIC) SI ");
			pqrybuf.append("	FROM( ");
			pqrybuf.append("		SELECT  ");
			pqrybuf.append("			TID ");
			pqrybuf.append("           ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0400') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END BCA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0171') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END NHA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0170') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END KBA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1300') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END SSA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0505') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END HNA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1100') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END LOA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1200') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END HDA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0300') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END SIA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0400') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END BCC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0171') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END NHC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0170') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END KBC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1300') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END SSC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0505') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END HNC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1100') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END LOC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1200') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END HDC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0300') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END SIC ");
			pqrybuf.append("		FROM ");
			pqrybuf.append("			 "+ userexp[6]);
			pqrybuf.append("			WHERE MID IN (SELECT MID FROM TB_BAS_MIDMAP  where org_cd=? ");
			setting.add(userexp[1]);

			//2021.03.11 depcd 값 설정
			if(!depcd.equals("") && depcd != null) {
				pqrybuf.append(" AND DEP_CD = ?");
				//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
				if(userexp[2] != null && !userexp[2].equals("")) {
					setting.add(userexp[2]);
				} else {
					setting.add(depcd);
				}
				//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
			} else if(userexp[2] != null && !userexp[2].equals("")) {
				pqrybuf.append(" AND DEP_CD = ?");
				setting.add(userexp[2]);
			}
			pqrybuf.append(") ");

			//int stime_idx = 0;
			if(!stime.equals("") && stime != null) {
				//smtsidx++;
				//stime_idx = smtsidx;
				pqrybuf.append("            and app_dd>=? ");
				setting.add(stime);
			}

			//int etime_idx = 0;
			if(!etime.equals("") && etime != null) {
				//smtsidx++;
				//etime_idx = smtsidx;
				pqrybuf.append("            and app_dd<=? ");
				setting.add(etime);
			}

			//int sreqdd_idx = 0;
			if(!sreqdd.equals("") && sreqdd != null) {
				//smtsidx++;
				//sreqdd_idx = smtsidx;
				pqrybuf.append("            and req_dd>=? ");
				setting.add(sreqdd);
			}

			//int ereqdd_idx = 0;
			if(!ereqdd.equals("") && ereqdd != null) {
				//smtsidx++;
				//ereqdd_idx = smtsidx;
				pqrybuf.append("            and req_dd<=? ");
				setting.add(ereqdd);
			}

			//int tid_idx = 0;
			if(!tid.equals("") && tid != null) {
				//smtsidx++;
				//tid_idx = smtsidx;
				pqrybuf.append("            and tid=? ");
				setting.add(tid);
			}

			//int mid_idx = 0;
			if(!mid.equals("") && mid != null) {
				//smtsidx++;
				//mid_idx = smtsidx;
				pqrybuf.append("            and mid=? ");
				setting.add(mid);
			}

			//int appno_idx = 0;
			if(!appno.equals("") && appno != null) {
				//smtsidx++;
				//appno_idx = smtsidx;
				pqrybuf.append("            and app_no=? ");
				setting.add(appno);
			}
			
			if(!acqcd.equals("") && acqcd != null) {
				pqrybuf.append(" AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD=? AND ORG_CD=? ) ");
				setting.add(acqcd);
				setting.add(userexp[1]);
			}
			/*
			if(!acqcd.equals("") && acqcd != null) {			
				pqrybuf.append(" AND ACQ_CD IN (");

				//setString 해야하는 parameter 개수만큼 물음표로 채워야 함.
				String[] paramTemp = new String[acqcdexp.length];

				for(int i = 0; i<acqcdexp.length; i++) {
					paramTemp[i] = "?";
					setting.add(acqcdexp[i]);
				}
				pqrybuf.append(utilm.implode(", ", paramTemp)+")");
			}
			*/


			pqrybuf.append("		GROUP BY  ");
			pqrybuf.append("			TID, MID, RTN_CD ");
			pqrybuf.append("	) GROUP BY TID ");
			pqrybuf.append(") T1 ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT TERM_NM, TERM_ID FROM TB_BAS_TIDMST ");
			pqrybuf.append(")T2 ON(T1.TID=T2.TERM_ID) ");

			//디버깅용
			utilm.debug_sql(pqrybuf, setting);

			con2 = getOraConnect();
			stmt2 = con2.prepareStatement(pqrybuf.toString());


			for(int k = 0; k<setting.size(); k++) {
				stmt2.setString((k+1), setting.get(k));
			}

			rs2 = stmt2.executeQuery();

			int icnt = 1;

			//합계부분 계산
			long tbc = 0, tkb = 0, thn = 0, tss = 0, tsi = 0, thd = 0, tlo = 0, tnh = 0;

			while(rs2.next()) {
				JSONObject tempObj = new JSONObject();
				JSONArray tempAry = new JSONArray();

				long bc = Long.parseLong(utilm.checkNumberData(rs2.getString("BC")));
				long kb = Long.parseLong(utilm.checkNumberData(rs2.getString("KB")));
				long hn = Long.parseLong(utilm.checkNumberData(rs2.getString("HN")));
				long ss = Long.parseLong(utilm.checkNumberData(rs2.getString("SS")));
				long si = Long.parseLong(utilm.checkNumberData(rs2.getString("SI")));
				long hd = Long.parseLong(utilm.checkNumberData(rs2.getString("HD")));
				long lo = Long.parseLong(utilm.checkNumberData(rs2.getString("LO")));
				long nh = Long.parseLong(utilm.checkNumberData(rs2.getString("NH")));

				tbc += bc;
				tkb += kb;
				thn += hn;
				tss += ss;
				tsi += si;
				thd += hd;
				tlo += lo;
				tnh += nh;

				tempAry.add(icnt);
				tempAry.add(utilm.setDefault(rs2.getString("TERM_NM")));
				tempAry.add(utilm.setDefault(rs2.getString("TID")));
				tempAry.add(bc);
				tempAry.add(kb);
				tempAry.add(hn);
				tempAry.add(ss);
				tempAry.add(si);
				tempAry.add(hd);
				tempAry.add(lo);
				tempAry.add(nh);

				tempObj.put("id", Integer.toString(icnt));
				tempObj.put("data", tempAry);

				sqlAry.add(tempObj);
				icnt++;
			} 

			//합계 계산
			JSONObject tempObj = new JSONObject();
			JSONArray tempAry = new JSONArray();

			tempAry.add("합계");
			tempAry.add("");
			tempAry.add("");
			tempAry.add(tbc);
			tempAry.add(tkb);
			tempAry.add(thn);
			tempAry.add(tss);
			tempAry.add(tsi);
			tempAry.add(thd);
			tempAry.add(tlo);
			tempAry.add(tnh);

			tempObj.put("id", "total");
			tempObj.put("data", tempAry);

			sqlAry.add(0, tempObj);

			sqlobj.put("rows", sqlAry);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con2,stmt2,rs2);
		}

		return sqlobj.toJSONString();
	}

	//2021.02.26 강원대병원v3 - 거래일자별조회item
	public String get_json_0303item(String tuser, String stime, String etime, String sreqdd, String ereqdd, String acqcd, String depcd, String tid, String mid, String appno) {
		Connection con2 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		
		JSONObject sqlobj = new JSONObject();
		JSONArray sqlAry = new JSONArray();

		StringBuffer pqrybuf = new StringBuffer();
		StringBuffer qrybuf = new StringBuffer();

		int smtsidx = 1;

		try {

			//tuser split
			String[] userexp = tuser.split(":");

			//acqcd split
			String[] acqcdexp = acqcd.split(",");

			//검색항목에 따른 where 조건절 setting 관련 변수
			ArrayList<String> setting = new ArrayList<>();

			String setdc = "";

			pqrybuf.append("select ");
			pqrybuf.append("    t5.dep_nm ");
			pqrybuf.append("    , t2.term_nm ");
			pqrybuf.append("    , t6.pur_nm ");
			pqrybuf.append("    , app_dd ");
			pqrybuf.append("    , req_dd ");
			pqrybuf.append("    , exp_dd ");
			pqrybuf.append("    , tid ");
			pqrybuf.append("    , mid ");
			pqrybuf.append("    , item_cnt ");
			pqrybuf.append("    , item_amt ");
			pqrybuf.append("    , item_fee ");
			pqrybuf.append("    , item_exp ");
			pqrybuf.append("from( ");
			pqrybuf.append("    select ");
			pqrybuf.append("        app_dd ");
			pqrybuf.append("        ,req_dd ");
			pqrybuf.append("        ,exp_dd ");
			pqrybuf.append("        ,tid ");
			pqrybuf.append("        ,mid ");
			pqrybuf.append("        , sum(item_cnt60)+sum(item_cnt67) item_cnt ");
			pqrybuf.append("        , sum(item_amt60)-sum(item_amt67) item_amt ");
			pqrybuf.append("        , sum(item_fee60)-sum(item_fee67) item_fee ");
			pqrybuf.append("        ,(sum(item_amt60)-sum(item_amt67))-(sum(item_fee60)-sum(item_fee67)) item_exp ");
			pqrybuf.append("    from( ");
			pqrybuf.append("        select ");
			pqrybuf.append("            app_dd ");
			pqrybuf.append("            ,req_dd ");
			pqrybuf.append("            ,exp_dd ");
			pqrybuf.append("            ,tid ");
			pqrybuf.append("            ,mid ");  
			pqrybuf.append("            ,rtn_cd ");
			pqrybuf.append("            ,case when rtn_cd='60' then count(1) else 0 end item_cnt60 ");
			pqrybuf.append("            ,case when rtn_cd='67' then count(1) else 0 end item_cnt67 ");
			pqrybuf.append("            ,case when rtn_cd not in ('60', '67') then count(1) else 0 end item_cntban ");
			pqrybuf.append("            ,case when rtn_cd='60' then sum(sale_amt) else 0 end item_amt60 ");
			pqrybuf.append("            ,case when rtn_cd='67' then sum(sale_amt) else 0 end item_amt67 ");
			pqrybuf.append("            ,case when rtn_cd='60' then sum(fee) else 0 end item_fee60 ");
			pqrybuf.append("            ,case when rtn_cd='67' then sum(fee) else 0 end item_fee67 ");
			pqrybuf.append("        from ");
			pqrybuf.append("            " + userexp[6]);
			pqrybuf.append("        where ");

			pqrybuf.append("            mid in (select mid from tb_bas_midmap where org_cd=? ");
			setting.add(userexp[1]);

			//2021.03.11 depcd 값 설정
			if(!depcd.equals("") && depcd != null) {
				pqrybuf.append(" AND DEP_CD = ?");
				//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
				if(userexp[2] != null && !userexp[2].equals("")) {
					setting.add(userexp[2]);
				} else {
					setting.add(depcd);
				}
				//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
			} else if(userexp[2] != null && !userexp[2].equals("")) {
				pqrybuf.append(" AND DEP_CD = ?");
				setting.add(userexp[2]);
			}
			pqrybuf.append(") ");


			//int stime_idx = 0;
			if(!stime.equals("") && stime != null) {
				//smtsidx++;
				//stime_idx = smtsidx;
				pqrybuf.append("            and app_dd>=? ");
				setting.add(stime);
			}

			//int etime_idx = 0;
			if(!etime.equals("") && etime != null) {
				//smtsidx++;
				//etime_idx = smtsidx;
				pqrybuf.append("            and app_dd<=? ");
				setting.add(etime);
			}

			//int sreqdd_idx = 0;
			if(!sreqdd.equals("") && sreqdd != null) {
				//smtsidx++;
				//sreqdd_idx = smtsidx;
				pqrybuf.append("            and req_dd>=? ");
				setting.add(sreqdd);
			}

			//int ereqdd_idx = 0;
			if(!ereqdd.equals("") && ereqdd != null) {
				//smtsidx++;
				//ereqdd_idx = smtsidx;
				pqrybuf.append("            and req_dd<=? ");
				setting.add(ereqdd);
			}

			//int tid_idx = 0;
			if(!tid.equals("") && tid != null) {
				//smtsidx++;
				//tid_idx = smtsidx;
				pqrybuf.append("            and tid=? ");
				setting.add(tid);
			}

			//int mid_idx = 0;
			if(!mid.equals("") && mid != null) {
				//smtsidx++;
				//mid_idx = smtsidx;
				pqrybuf.append("            and mid=? ");
				setting.add(mid);
			}

			//int appno_idx = 0;
			if(!appno.equals("") && appno != null) {
				//smtsidx++;
				//appno_idx = smtsidx;
				pqrybuf.append("            and app_no=? ");
				setting.add(appno);
			}
			
			if(!acqcd.equals("") && acqcd != null) {
				pqrybuf.append(" AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD=? AND ORG_CD=? ) ");
				setting.add(acqcd);
				setting.add(userexp[1]);
			}
			/*
			if(!acqcd.equals("") && acqcd != null) {			
				pqrybuf.append(" AND ACQ_CD IN (");

				//setString 해야하는 parameter 개수만큼 물음표로 채워야 함.
				String[] paramTemp = new String[acqcdexp.length];

				for(int i = 0; i<acqcdexp.length; i++) {
					paramTemp[i] = "?";
					setting.add(acqcdexp[i]);
				}
				pqrybuf.append(utilm.implode(", ", paramTemp)+")");
			}
			*/
			pqrybuf.append("        group by req_dd, app_dd,  exp_dd, mid, tid,  rtn_cd ");
			pqrybuf.append("    ) ");
			pqrybuf.append("    group by req_dd, app_dd, exp_dd, tid, mid ");
			pqrybuf.append(")t1 ");
			pqrybuf.append("left outer join( ");
			pqrybuf.append("    select term_id, term_nm from tb_bas_tidmst where org_cd=? ");
			setting.add(userexp[1]);

			pqrybuf.append(")t2 on(t1.tid=t2.term_id) ");
			pqrybuf.append("left outer join( ");
			pqrybuf.append("    select org_cd, dep_cd, mer_no, pur_cd from tb_bas_merinfo where org_cd=? ");
			setting.add(userexp[1]);

			pqrybuf.append(")t3 on(t1.mid=t3.mer_no) ");
			pqrybuf.append("left outer join( ");
			pqrybuf.append("    select org_cd, org_nm from tb_bas_org ");
			pqrybuf.append(")t4 on(t3.org_cd=t4.org_cd) ");
			pqrybuf.append("left outer join( ");
			pqrybuf.append("    select dep_cd, dep_nm from tb_bas_depart where org_cd=? ");
			setting.add(userexp[1]);

			pqrybuf.append(")t5 on(t3.dep_cd=t5.dep_cd) ");
			pqrybuf.append("left outer join( ");
			pqrybuf.append("    select pur_cd, pur_nm, pur_sort, pur_koces from tb_bas_purinfo ");
			pqrybuf.append(")t6 on(t3.pur_cd=t6.pur_cd) ");
			pqrybuf.append("left outer join( ");
			pqrybuf.append("   select org_cd, user_pur_cd, user_pursort from tb_bas_userpurinfo where org_cd=? ");
			setting.add(userexp[1]);

			pqrybuf.append(")s3 on(t6.pur_cd=s3.user_pur_cd) ");
			pqrybuf.append("where item_cnt>0 ");
			pqrybuf.append("order by t3.dep_cd asc, t1.req_dd asc, user_pursort asc, t1.app_dd asc, t1.exp_dd asc ");			

			//디버깅용
			utilm.debug_sql(pqrybuf, setting);

			con2 = getOraConnect();
			stmt2 = con2.prepareStatement(pqrybuf.toString());
			for(int k = 0; k<setting.size(); k++) {
				stmt2.setString((k+1), setting.get(k));
			}			
			rs2 = stmt2.executeQuery();

			int icnt = 1;

			int count = 0;
			long amount = 0, fee = 0, expamt = 0;

			while(rs2.next()) {
				JSONObject tempObj = new JSONObject();
				JSONArray tempArr = new JSONArray();

				int item_cnt = Integer.parseInt(utilm.checkNumberData(rs2.getString("ITEM_CNT")));
				long item_amt = Long.parseLong(utilm.checkNumberData(rs2.getString("ITEM_AMT")));
				long item_fee = Long.parseLong(utilm.checkNumberData(rs2.getDouble("ITEM_FEE")));
				long item_exp = Long.parseLong(utilm.checkNumberData(rs2.getDouble("ITEM_EXP")));

				count += item_cnt;
				amount += item_amt;
				fee += item_fee;
				expamt += item_exp;

				tempArr.add(rs2.getString("DEP_NM"));
				tempArr.add(rs2.getString("TERM_NM"));
				tempArr.add(rs2.getString("TID"));
				tempArr.add(rs2.getString("MID"));
				tempArr.add(rs2.getString("PUR_NM"));
				tempArr.add(utilm.str_to_dateformat_deposit(rs2.getString("APP_DD")));
				tempArr.add(utilm.str_to_dateformat_deposit(rs2.getString("REQ_DD")));
				tempArr.add(utilm.str_to_dateformat_deposit(rs2.getString("EXP_DD")));
				tempArr.add(item_cnt);
				tempArr.add(item_amt);
				tempArr.add(item_fee);
				tempArr.add(item_exp);

				tempObj.put("id", Integer.toString(icnt));
				tempObj.put("data", tempArr);

				sqlAry.add(tempObj);
				icnt++;
			} 

			//합계부분
			JSONObject tempObj = new JSONObject();
			JSONArray tempArr = new JSONArray();

			tempArr.add("합계");
			tempArr.add("");
			tempArr.add("");
			tempArr.add("");
			tempArr.add("");
			tempArr.add("");
			tempArr.add("");
			tempArr.add("");
			tempArr.add(count);
			tempArr.add(amount);
			tempArr.add(fee);
			tempArr.add(expamt);

			tempObj.put("id", "total");
			tempObj.put("data", tempArr);

			sqlAry.add(tempObj);

			sqlobj.put("rows", sqlAry);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con2,stmt2,rs2);
		}

		return sqlobj.toJSONString();

	}

	//2021.02.26 강원대병원 v3 - 거래일자상세내역 total
	public String get_json_0303detail_total(String tuser, String stime, String etime, String sreqdd, String ereqdd, String sexpdd, String eexpdd, String appno, String tid, String mid, String acqcd, String depcd) {
		Connection con2 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		
		JSONObject jrtnobj = new JSONObject();
		JSONArray arr = new JSONArray();

		StringBuffer pqrybuf = new StringBuffer();
		StringBuffer qrybuf = new StringBuffer();

		int smtsidx = 1;

		try {

			//tuser split
			String[] userexp = tuser.split(":");
			//acqcd split
			String[] acqcdexp = acqcd.split(",");
			//검색항목에 따른 where 조건절 setting 관련 변수
			ArrayList<String> setting = new ArrayList<>();

			String setdc = "";

			pqrybuf.append("SELECT ");
			pqrybuf.append("	T2.TERM_NM ");
			pqrybuf.append("	,TID ");
			pqrybuf.append("	,BC ");
			pqrybuf.append("	,NH ");
			pqrybuf.append("	,KB ");
			pqrybuf.append("	,SS ");
			pqrybuf.append("	,HN ");
			pqrybuf.append("	,LO ");
			pqrybuf.append("	,HD ");
			pqrybuf.append("	,SI ");
			pqrybuf.append("FROM( ");
			pqrybuf.append("	SELECT ");
			pqrybuf.append("		TID ");
			pqrybuf.append("		,SUM(BCA)-SUM(BCC) BC ");
			pqrybuf.append("		,SUM(NHA)-SUM(NHC) NH ");
			pqrybuf.append("		,SUM(KBA)-SUM(KBC) KB ");
			pqrybuf.append("		,SUM(SSA)-SUM(SSC) SS ");
			pqrybuf.append("		,SUM(HNA)-SUM(HNC) HN ");
			pqrybuf.append("		,SUM(LOA)-SUM(LOC) LO ");
			pqrybuf.append("		,SUM(HDA)-SUM(HDC) HD ");
			pqrybuf.append("		,SUM(SIA)-SUM(SIC) SI ");
			pqrybuf.append("	FROM( ");
			pqrybuf.append("		SELECT  ");
			pqrybuf.append("			TID ");
			pqrybuf.append("           ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0400') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END BCA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0171') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END NHA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0170') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END KBA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1300') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END SSA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0505') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END HNA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1100') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END LOA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1200') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END HDA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0300') AND RTN_CD='60' THEN SUM(SALE_AMT) ELSE 0 END SIA ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0400') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END BCC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0171') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END NHC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0170') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END KBC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1300') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END SSC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0505') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END HNC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1100') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END LOC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1200') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END HDC ");
			pqrybuf.append("            ,CASE WHEN MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0300') AND RTN_CD='67' THEN SUM(SALE_AMT) ELSE 0 END SIC ");
			pqrybuf.append("		FROM ");
			pqrybuf.append("			 "+ userexp[6]);
			pqrybuf.append("			WHERE MID IN (SELECT MID FROM TB_BAS_MIDMAP  where org_cd=? ");
			setting.add(userexp[1]);
			
			//2021.03.11 depcd 값 설정
			if(!depcd.equals("") && depcd != null) {
				pqrybuf.append(" AND DEP_CD = ?");
				//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
				if(userexp[2] != null && !userexp[2].equals("")) {
					setting.add(userexp[2]);
				} else {
					setting.add(depcd);
				}
				//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
			} else if(userexp[2] != null && !userexp[2].equals("")) {
				pqrybuf.append(" AND DEP_CD = ?");
				setting.add(userexp[2]);
			}
			pqrybuf.append(") ");

			//int stime_idx = 0;
			if(!stime.equals("") && stime != null) {
				//smtsidx++;
				//stime_idx = smtsidx;
				pqrybuf.append("            and app_dd>=? ");
				setting.add(stime);
			}

			//int etime_idx = 0;
			if(!etime.equals("") && etime != null) {
				//smtsidx++;
				//etime_idx = smtsidx;
				pqrybuf.append("            and app_dd<=? ");
				setting.add(etime);
			}

			//int sreqdd_idx = 0;
			if(!sreqdd.equals("") && sreqdd != null) {
				//smtsidx++;
				//sreqdd_idx = smtsidx;
				pqrybuf.append("            and req_dd>=? ");
				setting.add(sreqdd);
			}

			//int ereqdd_idx = 0;
			if(!ereqdd.equals("") && ereqdd != null) {
				//smtsidx++;
				//ereqdd_idx = smtsidx;
				pqrybuf.append("            and req_dd<=? ");
				setting.add(ereqdd);
			}

			//int sexpdd_idx = 0;
			if(!sexpdd.equals("") && sexpdd != null) {
				//smtsidx++;
				//sexpdd_idx = smtsidx;
				pqrybuf.append("            and exp_dd>=? ");
				setting.add(sexpdd);
			}

			//int eexpdd_idx = 0;
			if(!eexpdd.equals("") && eexpdd != null) {
				//smtsidx++;
				//eexpdd_idx = smtsidx;
				pqrybuf.append("            and exp_dd<=? ");
				setting.add(eexpdd);
			}

			//int tid_idx = 0;
			if(!tid.equals("") && tid != null) {
				//smtsidx++;
				//tid_idx = smtsidx;
				pqrybuf.append("            and tid=? ");
				setting.add(tid);
			}

			//int mid_idx = 0;
			if(!mid.equals("") && mid != null) {
				//smtsidx++;
				//mid_idx = smtsidx;
				pqrybuf.append("            and mid=? ");
				setting.add(mid);
			}

			//int appno_idx = 0;
			if(!appno.equals("") && appno != null) {
				//smtsidx++;
				//appno_idx = smtsidx;
				pqrybuf.append("            and app_no=? ");
				setting.add(appno);
			}
			
			if(!acqcd.equals("") && acqcd != null) {
				pqrybuf.append(" AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD=? AND ORG_CD=? ) ");
				setting.add(acqcd);
				setting.add(userexp[1]);
			}
			/*
			if(!acqcd.equals("") && acqcd != null) {			
				pqrybuf.append(" AND ACQ_CD IN (");

				//setString 해야하는 parameter 개수만큼 물음표로 채워야 함.
				String[] paramTemp = new String[acqcdexp.length];

				for(int i = 0; i<acqcdexp.length; i++) {
					paramTemp[i] = "?";
					setting.add(acqcdexp[i]);
				}
				pqrybuf.append(utilm.implode(", ", paramTemp)+")");
			}
			*/

			pqrybuf.append("		GROUP BY  ");
			pqrybuf.append("			TID, MID, RTN_CD ");
			pqrybuf.append("	) GROUP BY TID ");
			pqrybuf.append(") T1 ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT TERM_NM, TERM_ID FROM TB_BAS_TIDMST ");
			pqrybuf.append(")T2 ON(T1.TID=T2.TERM_ID) ");

			//디버깅용
			utilm.debug_sql(pqrybuf, setting);

			con2 = getOraConnect();
			stmt2 = con2.prepareStatement(pqrybuf.toString());
			for(int k = 0; k<setting.size(); k++) {
				stmt2.setString((k+1), setting.get(k));
			}

			rs2 = stmt2.executeQuery();

			int icnt = 1;
			//합계부분 계산
			long tbc = 0, tkb = 0, thn = 0, tss = 0, tsi = 0, thd = 0, tlo = 0, tnh = 0;
			while(rs2.next()) {
				JSONObject obj1 = new JSONObject();
				JSONArray arr2 = new JSONArray();
				
				long bc = Long.parseLong(utilm.checkNumberData(rs2.getString("BC")));
				long kb = Long.parseLong(utilm.checkNumberData(rs2.getString("KB")));
				long hn = Long.parseLong(utilm.checkNumberData(rs2.getString("HN")));
				long ss = Long.parseLong(utilm.checkNumberData(rs2.getString("SS")));
				long si = Long.parseLong(utilm.checkNumberData(rs2.getString("SI")));
				long hd = Long.parseLong(utilm.checkNumberData(rs2.getString("HD")));
				long lo = Long.parseLong(utilm.checkNumberData(rs2.getString("LO")));
				long nh = Long.parseLong(utilm.checkNumberData(rs2.getString("NH")));

				tbc += bc;
				tkb += kb;
				thn += hn;
				tss += ss;
				tsi += si;
				thd += hd;
				tlo += lo;
				tnh += nh;

				arr2.add(icnt);
				arr2.add(rs2.getString("TERM_NM"));
				arr2.add(rs2.getString("TID"));
				arr2.add(rs2.getString("BC"));
				arr2.add(rs2.getString("KB"));
				arr2.add(rs2.getString("HN"));
				arr2.add(rs2.getString("SS"));
				arr2.add(rs2.getString("SI"));
				arr2.add(rs2.getString("HD"));
				arr2.add(rs2.getString("LO"));
				arr2.add(rs2.getString("NH"));

				obj1.put("id", Integer.toString(icnt));
				obj1.put("data", arr2);

				arr.add(obj1);
				icnt++;
			}
			
			//합계 계산
			JSONObject obj1 = new JSONObject();
			JSONArray arr2 = new JSONArray();

			arr2.add("합계");
			arr2.add("");
			arr2.add("");
			arr2.add(tbc);
			arr2.add(tkb);
			arr2.add(thn);
			arr2.add(tss);
			arr2.add(tsi);
			arr2.add(thd);
			arr2.add(tlo);
			arr2.add(tnh);

			obj1.put("id", "total");
			obj1.put("data", arr2);
			
			arr.add(0, obj1);

			jrtnobj.put("rows", arr);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con2,stmt2,rs2);
		}
		return jrtnobj.toJSONString();
	}
	
	//2021.02.26 강원대병원 v3 거래일자상세내역 item
		public String get_json_0303detail_item(String tuser, String stime, String etime, String sreqdd, String ereqdd, String sexpdd, String eexpdd, String appno, String tid, String mid, String acqcd, String depcd) {
			Connection con2 = null;
			PreparedStatement stmt2 = null;
			ResultSet rs2 = null;
			
			JSONObject jrtnobj = new JSONObject();

			JSONArray arr = new JSONArray();
			StringBuffer pqrybuf = new StringBuffer();
			StringBuffer qrybuf = new StringBuffer();

			int smtsidx = 4;

			try {

				//tuser split
				String[] userexp = tuser.split(":");
				//acqcd split
				String[] acqcdexp = acqcd.split(",");
				//검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();

				String setdc = "";

				pqrybuf.append("SELECT ");
				pqrybuf.append("    CONCAT(T1.DEP_SEQ, T1.DEP_CD) SEQNO ");
				pqrybuf.append("	,T3.DEP_NM ");
				pqrybuf.append("	,T5.TERM_NM ");
				pqrybuf.append("	,TID ");
				pqrybuf.append("	,T6.PUR_NM ");
				pqrybuf.append("	,MID ");
				pqrybuf.append("	,CASE WHEN RTN_CD IN ('60', '67') THEN '정상매출' ELSE '매출반송' END RTN_TXT ");
				pqrybuf.append("	,CASE WHEN RTN_CD IN ('60', '61') THEN '승인' WHEN RTN_CD IN ('64','67') THEN '취소' END AUTHTXT ");
				pqrybuf.append("	,CARD_NO ");
				pqrybuf.append("	,SALE_AMT AMOUNT ");
				pqrybuf.append("	,HALBU ");
				pqrybuf.append("	,APP_NO ");
				pqrybuf.append("	,APP_DD ");
				pqrybuf.append("	,OAPP_DD ");
				pqrybuf.append("	,FEE ");
				pqrybuf.append("	,SALE_AMT-FEE EXP_AMT ");
				pqrybuf.append("	,REQ_DD ");
				pqrybuf.append("	,REG_DD ");
				pqrybuf.append("	,RTN_CD ");
				pqrybuf.append("	,EXP_DD ");
				pqrybuf.append("	,RS_MSG ");
				pqrybuf.append("FROM ");
				pqrybuf.append("    TB_MNG_DEPDATA T1 ");
				pqrybuf.append("LEFT OUTER JOIN( ");
				pqrybuf.append("    SELECT ORG_CD, DEP_CD, STO_CD, MER_NO, PUR_CD FROM TB_BAS_MERINFO WHERE ORG_CD=? ");
				setting.add(userexp[1]);
				pqrybuf.append(")T2 ON(T1.MID=T2.MER_NO) ");
				pqrybuf.append("LEFT OUTER JOIN( ");
				pqrybuf.append("    SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD=? ");
				setting.add(userexp[1]);
				pqrybuf.append(")T3 ON(T2.DEP_CD=T3.DEP_CD) ");
				pqrybuf.append("LEFT OUTER JOIN( ");
				pqrybuf.append("    SELECT STO_NM, STO_CD, DEP_CD, ORG_CD FROM TB_BAS_STORE ");
				pqrybuf.append(")T4 ON(T2.STO_CD=T4.STO_CD AND T2.DEP_CD=T4.DEP_CD AND T2.ORG_CD=T4.ORG_CD) ");
				pqrybuf.append("LEFT OUTER JOIN( ");
				pqrybuf.append("    SELECT TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD=? ");
				setting.add(userexp[1]);
				pqrybuf.append(")T5 ON(T1.TID=T5.TERM_ID)  ");
				pqrybuf.append("LEFT OUTER JOIN( ");
				pqrybuf.append("    SELECT PUR_CD, PUR_NM FROM TB_BAS_PURINFO ");
				pqrybuf.append(")T6 ON(T2.PUR_CD=T6.PUR_CD) ");
				pqrybuf.append("WHERE MID IN (SELECT MID FROM TB_BAS_MIDMAP  where org_cd=?  ");
				setting.add(userexp[1]);
				
				//2021.03.11 depcd 값 설정
				if(!depcd.equals("") && depcd != null) {
					pqrybuf.append(" AND DEP_CD = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					pqrybuf.append(" AND DEP_CD = ?");
					setting.add(userexp[2]);
				}
				pqrybuf.append(") ");
				
				//int stime_idx = 0;
				if(null!=stime&&""!=stime) {
					//smtsidx++;
					//stime_idx = smtsidx;
					pqrybuf.append("	and app_dd>=? ");
					setting.add(stime);
				}

				//int etime_idx = 0;
				if(null!=etime&&""!=etime) {
					//smtsidx++;
					//etime_idx = smtsidx;
					pqrybuf.append("	and app_dd<=? ");
					setting.add(etime);
				}

				//int sreqdd_idx = 0;
				if(null!=sreqdd&&""!=sreqdd) {
					//smtsidx++;
					//sreqdd_idx = smtsidx;
					pqrybuf.append("	and req_dd>=? ");
					setting.add(sreqdd);
				}

				//int ereqdd_idx = 0;
				if(null!=ereqdd&&""!=ereqdd) {
					//smtsidx++;
					//ereqdd_idx = smtsidx;
					pqrybuf.append("	and req_dd<=? ");
					setting.add(ereqdd);
				}

				//int sexpdd_idx = 0;
				if(null!=sexpdd&&""!=sexpdd) {
					//smtsidx++;
					//sexpdd_idx = smtsidx;
					pqrybuf.append("	and exp_dd>=? ");
					setting.add(sexpdd);
				}

				//int eexpdd_idx = 0;
				if(null!=eexpdd&&""!=eexpdd) {
					//smtsidx++;
					//eexpdd_idx = smtsidx;
					pqrybuf.append("	and exp_dd<=? ");
					setting.add(eexpdd);
				}

				//int tid_idx = 0;
				if(null!=tid&&""!=tid) {
					//smtsidx++;
					//tid_idx = smtsidx;
					pqrybuf.append("	and tid=? ");
					setting.add(tid);
				}

				//int mid_idx = 0;
				if(null!=mid&&""!=mid) {
					//smtsidx++;
					//mid_idx = smtsidx;
					pqrybuf.append("	and mid=? ");
					setting.add(mid);
				}

				//int appno_idx = 0;
				if(null!=appno&&""!=appno) {
					//smtsidx++;
					//appno_idx = smtsidx;
					pqrybuf.append("	and app_no=? ");
					setting.add(appno);
				}
				
				if(!acqcd.equals("") && acqcd != null) {
					pqrybuf.append(" AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD=? AND ORG_CD=? ) ");
					setting.add(acqcd);
					setting.add(userexp[1]);
				}
				/*
				if(!acqcd.equals("") && acqcd != null) {			
					pqrybuf.append(" AND ACQ_CD IN (");

					//setString 해야하는 parameter 개수만큼 물음표로 채워야 함.
					String[] paramTemp = new String[acqcdexp.length];

					for(int i = 0; i<acqcdexp.length; i++) {
						paramTemp[i] = "?";
						setting.add(acqcdexp[i]);
					}
					pqrybuf.append(utilm.implode(", ", paramTemp)+")");
				}
				*/
				//디버깅용
				utilm.debug_sql(pqrybuf, setting);

				con2 = getOraConnect();
				stmt2 = con2.prepareStatement(pqrybuf.toString());

				for(int k = 0; k<setting.size(); k++) {
					stmt2.setString((k+1), setting.get(k));
				}

				rs2 = stmt2.executeQuery();

				int icnt = 1;
				int count = 0;
				long amount = 0, fee = 0, expamt = 0;

				while(rs2.next()) {
					JSONObject obj1 = new JSONObject();
					JSONArray arr2 = new JSONArray();

					arr2.add(icnt);
					arr2.add(rs2.getString("DEP_NM"));
					arr2.add(rs2.getString("TERM_NM"));
					arr2.add(rs2.getString("TID"));
					arr2.add(rs2.getString("PUR_NM"));
					arr2.add(rs2.getString("MID"));
					arr2.add(utilm.setDefault(rs2.getString("RTN_TXT")));
					arr2.add(utilm.setDefault(rs2.getString("AUTHTXT")));
					String cardNo = utilm.cardno_masking(trans_seed_manager.seed_dec_card(rs2.getString("CARD_NO").trim()));
					//arr2.add(utilm.cardno_masking(rs2.getString("CARD_NO")));
					arr2.add(cardNo);
					arr2.add(rs2.getString("AMOUNT"));
					arr2.add(rs2.getString("HALBU"));
					arr2.add(rs2.getString("APP_NO"));
					arr2.add(utilm.str_to_dateformat_deposit(rs2.getString("APP_DD")));
					arr2.add("");
					arr2.add(utilm.setDefault(rs2.getString("OAPP_DD")));
					arr2.add(utilm.checkNumberData(rs2.getDouble("FEE")));
					arr2.add(utilm.checkNumberData(rs2.getDouble("EXP_AMT")));
					arr2.add(utilm.str_to_dateformat_deposit(rs2.getString("REQ_DD")));
					arr2.add(utilm.str_to_dateformat_deposit(rs2.getString("REG_DD")));
					arr2.add(rs2.getString("RTN_CD"));
					arr2.add(utilm.str_to_dateformat_deposit(rs2.getString("EXP_DD")));
					arr2.add(utilm.setDefault(rs2.getString("RS_MSG")));

					obj1.put("id", Integer.toString(icnt));
					obj1.put("data", arr2);

					arr.add(obj1);
					icnt++;
				} 

				jrtnobj.put("rows", arr);
				

			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con2,stmt2,rs2);
			}
			return jrtnobj.toJSONString();
		}
	
		public String get_json_0304total(String tuser, String stime, String etime, String mid, String acqcd, String depcd, String accetc) {
			Connection con2 = null;
			PreparedStatement stmt2 = null;
			ResultSet rs2 = null;
			
			JSONObject jrtnobj = new JSONObject();

			JSONArray arr = new JSONArray();
			StringBuffer pqrybuf = new StringBuffer();
			StringBuffer qrybuf = new StringBuffer();

			try {

				//tuser split
				String[] userexp = tuser.split(":");
				//acqcd split
				String[] acqcdexp = acqcd.split(",");
				//검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();

				pqrybuf.append("SELECT ");
				pqrybuf.append("	ORG_NM");
				pqrybuf.append("	, DEP_NM");
				pqrybuf.append("	, ACC_TXT");
				pqrybuf.append("	, ACQ_CD");
				pqrybuf.append("	, MID");
				pqrybuf.append("	, EXP_DD");
				pqrybuf.append("	, EXP_AMT");
				pqrybuf.append("	, PUR_NM");
				pqrybuf.append(" FROM");
				pqrybuf.append("	TB_MNG_BANKDATA T1");
				pqrybuf.append(" LEFT OUTER JOIN(");
				pqrybuf.append("    SELECT ORG_CD, ORG_NM FROM TB_BAS_ORG");
				pqrybuf.append(" )T2 ON(T1.ORG_CD=T2.ORG_CD)");
				pqrybuf.append(" LEFT OUTER JOIN(");
				pqrybuf.append("    SELECT DEP_CD, DEP_NM FROM TB_BAS_DEPART WHERE ORG_CD=?");
				setting.add(userexp[1]);
				pqrybuf.append(" )T3 ON(T1.DEP_CD=T3.DEP_CD)");
				pqrybuf.append(" LEFT OUTER JOIN(");
				pqrybuf.append("    SELECT PUR_CD, PUR_NM, PUR_SORT, PUR_KOCES FROM TB_BAS_PURINFO");
				pqrybuf.append(" )T4 ON(T1.ACQ_CD=T4.PUR_CD)");
				pqrybuf.append(" WHERE MID IN (SELECT MID FROM TB_BAS_MIDMAP ");
				pqrybuf.append(" WHERE ORG_CD=? )");
				setting.add(userexp[1]);
				
				if(null!=stime&&""!=stime) {
					pqrybuf.append("	AND EXP_DD>=? ");
					setting.add(stime);
				}

				if(null!=etime&&""!=etime) {
					pqrybuf.append("	AND EXP_DD<=? ");
					setting.add(etime);
				}

				if(null!=mid&&""!=mid) {
					pqrybuf.append("	AND mid=? ");
					setting.add(mid);
				}

				if(!acqcd.equals("") && acqcd != null) {			
					pqrybuf.append(" AND ACQ_CD IN (");

					String[] paramTemp = new String[acqcdexp.length];

					for(int i = 0; i<acqcdexp.length; i++) {
						paramTemp[i] = "?";
						setting.add(acqcdexp[i]);
					}
					pqrybuf.append(utilm.implode(", ", paramTemp)+")");
				}
				
				if(null!=depcd&&""!=depcd) {
					pqrybuf.append("	and depcd=? ");
					setting.add(depcd);
				}
				
				if(null!=accetc&&""!=accetc) {
					pqrybuf.append("	and ACC_TXT=? ");
					setting.add(accetc);
				}
				
				pqrybuf.append(" ORDER BY PUR_SORT ASC");

				//디버깅용
				utilm.debug_sql(pqrybuf, setting);

				con2 = getOraConnect();
				stmt2 = con2.prepareStatement(pqrybuf.toString());

				for(int k = 0; k<setting.size(); k++) {
					stmt2.setString((k+1), setting.get(k));
				}

				rs2 = stmt2.executeQuery();

				while(rs2.next()) {
					JSONObject obj1 = new JSONObject();
					JSONArray arr2 = new JSONArray();

					arr2.add(rs2.getString("DEP_NM"));
					arr2.add(rs2.getString("MID"));
					arr2.add(rs2.getString("PUR_NM"));
					arr2.add(utilm.str_to_dateformat_deposit(rs2.getString("EXP_DD")));
					arr2.add(rs2.getString("EXP_AMT"));
					arr2.add(rs2.getString("ACC_TXT"));
					
					obj1.put("id", "item");
					obj1.put("data", arr2);

					arr.add(obj1);
				} 

				jrtnobj.put("rows", arr);

			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con2,stmt2,rs2);
			}
			return jrtnobj.toJSONString();
		}

	public String get_json_0309total(String tuser, String stime, String etime, String samt, String eamt, String appno, String pid, String tradeidx,
			String mid, String tid, String acqcd, String depcd, String auth01, String auth02, String auth03, String depreq1, String depreq2, String depreq3) {
		Connection con2 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		
		JSONObject jrtnobj = new JSONObject();

		JSONArray arr = new JSONArray();
		StringBuffer pqrybuf = new StringBuffer();
		StringBuffer qrybuf = new StringBuffer();

		int smtsidx = 2;

		try {

			String[] userexp = tuser.split(":");
			String setdc = "";

			pqrybuf.append("SELECT  ");
			pqrybuf.append("	PUR_NM, TT1.MID, ACNT, CCNT, AAMT, CAMT, RACNT, RAAMT, RCCNT, RCAMT, (ACNT+CCNT) TCNT, (AAMT-CAMT) TAMT ");
			pqrybuf.append("FROM ( ");
			pqrybuf.append("   SELECT ");
			pqrybuf.append("       MID, NVL(SUM(ACNT),0) ACNT, NVL(SUM(CCNT),0) CCNT, NVL(SUM(AAMT),0) AAMT, NVL(SUM(CAMT),0) CAMT, NVL(SUM(RACNT),0) RACNT, NVL(SUM(RCCNT),0) RCCNT, NVL(SUM(RAAMT),0) RAAMT, NVL(SUM(RCAMT),0) RCAMT ");
			pqrybuf.append("   FROM( ");
			pqrybuf.append("       SELECT ");
			pqrybuf.append("           MID, ");
			pqrybuf.append("           CASE WHEN APPGB='A' AND TRAN_STAT IN ('TR00', 'DP99') THEN COUNT(1) ELSE 0 END ACNT, ");
			pqrybuf.append("           CASE WHEN APPGB='A' AND TRAN_STAT IN ('TR00', 'DP99') THEN SUM(AMOUNT) ELSE 0 END AAMT, ");
			pqrybuf.append("           CASE WHEN APPGB='C' AND TRAN_STAT IN ('TR00', 'DP99') THEN COUNT(1) ELSE 0 END CCNT, ");
			pqrybuf.append("           CASE WHEN APPGB='C' AND TRAN_STAT IN ('TR00', 'DP99') THEN SUM(AMOUNT) ELSE 0 END CAMT, ");
			pqrybuf.append("           CASE WHEN APPGB='A' AND TRAN_STAT IN ('RV01') THEN COUNT(1) ELSE 0 END RACNT, ");
			pqrybuf.append("           CASE WHEN APPGB='A' AND TRAN_STAT IN ('RV01') THEN SUM(AMOUNT) ELSE 0 END RAAMT, ");
			pqrybuf.append("           CASE WHEN APPGB='C' AND TRAN_STAT IN ('RV01') THEN COUNT(1) ELSE 0 END RCCNT, ");
			pqrybuf.append("           CASE WHEN APPGB='C' AND TRAN_STAT IN ('RV01') THEN SUM(AMOUNT) ELSE 0 END RCAMT ");
			pqrybuf.append("       FROM( ");
			pqrybuf.append("			SELECT ");
			pqrybuf.append("				SEQNO, TID, MID, APPDD, APPTM, OAPPDD, APPNO, APPGB, AMOUNT, TRAN_STAT ");
			pqrybuf.append("			FROM( ");
			pqrybuf.append("				SELECT ");
			pqrybuf.append("					SEQNO, TID, MID, TSTAT, ");
			pqrybuf.append("					CASE ");
			pqrybuf.append("						WHEN APPGB='A' AND TSTAT IS NULL  THEN '승인거래' ");
			pqrybuf.append("						WHEN APPGB='A' AND TSTAT=APPDD THEN '당일취소' ");
			pqrybuf.append("						WHEN APPGB='C' AND APPDD=OAPPDD THEN '당일취소' ");
			pqrybuf.append("						WHEN APPGB='C' AND APPDD<>OAPPDD THEN '전일취소' ");
			pqrybuf.append("						WHEN APPGB='A' AND APPDD<>TSTAT AND TSTAT IS NOT NULL THEN '전일취소' ");
			pqrybuf.append("					END TSTAT_TXT, ");
			pqrybuf.append("					APPDD, APPTM, TSTAT CANDATE, OAPPDD, APPNO, APPGB, ");
			pqrybuf.append("					CASE  ");
			pqrybuf.append("						WHEN APPGB='A' THEN '신용승인' ");
			pqrybuf.append("						WHEN APPGB='C' THEN '신용취소' ");
			pqrybuf.append("					END APPGB_TXT, ");
			pqrybuf.append("					CARDNO,	AMOUNT,	HALBU, ");
			pqrybuf.append("					TRANIDX, TRAN_STAT ");
			pqrybuf.append("				FROM( ");
			pqrybuf.append("					SELECT ");
			pqrybuf.append("						SEQNO, BIZNO, TID, MID, VANGB, MDATE, SVCGB, T1.TRANIDX, T1.APPGB, ENTRYMD, ");
			pqrybuf.append("						T1.APPDD, APPTM, T1.APPNO, T1.CARDNO, HALBU, CURRENCY, T1.AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX, ");
			pqrybuf.append("						ISS_CD, ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, ");
			pqrybuf.append("						OAPPNO, OAPPDD, OAPPTM, OAPP_AMT, ");
			pqrybuf.append("						CASE ");
			pqrybuf.append("							WHEN APPGB='C' THEN '' ");
			pqrybuf.append("							WHEN APPGB='A' THEN (SELECT C1.APPDD FROM "+userexp[5]+" C1 WHERE C1.APPGB='C' AND T1.APPDD=C1.OAPPDD AND T1.APPNO=C1.APPNO AND T1.AMOUNT=C1.AMOUNT AND T1.CARDNO=C1.CARDNO) ");
			pqrybuf.append("						END TSTAT, (SELECT RTN_CD FROM TB_MNG_DEPDATA WHERE TRANIDX = T1.TRANIDX AND APP_DD=T1.APPDD) DEP_RTN_CD ");
			pqrybuf.append("						,NVL(TRAN_STAT, 'TR00') TRAN_STAT ");
			pqrybuf.append("					FROM ");
			pqrybuf.append(userexp[5] );
			pqrybuf.append(" T1 WHERE SVCGB IN ('CC', 'CE') AND AUTHCD='0000' AND MID IN ( ");
			pqrybuf.append("	SELECT MID FROM TB_BAS_MIDMAP MT1 ");
			pqrybuf.append("	LEFT OUTER JOIN( ");
			pqrybuf.append("		SELECT MER_NO, MTYPE FROM TB_BAS_MERINFO WHERE ORG_CD=? AND MTYPE='EDI' ");
			pqrybuf.append("	)MT2 ON(MT1.MID=MT2.MER_NO) ");
			pqrybuf.append("	WHERE MT1.ORG_CD=? AND MT2.MTYPE='EDI' ");
			pqrybuf.append(")  ");

			int stime_idx = 0;
			if(null!=stime&&""!=stime) {
				smtsidx++;
				stime_idx = smtsidx;
				pqrybuf.append("            AND T1.APPDD>=? ");
			}

			int etime_idx = 0;
			if(null!=etime&&""!=etime) {
				smtsidx++;
				etime_idx = smtsidx;
				pqrybuf.append("            AND T1.APPDD<=? ");
			}

			int samt_idx = 0;
			if(null!=samt&&""!=samt) {
				smtsidx++;
				samt_idx = smtsidx;
				pqrybuf.append("            AND T1.AMOUNT>=? ");
			}

			int eamt_idx = 0;
			if(null!=eamt&&""!=eamt) {
				smtsidx++;
				eamt_idx = smtsidx;
				pqrybuf.append("            AND T1.AMOUNT<=? ");
			}

			int appno_idx = 0;
			if(null!=appno&&""!=appno) {
				smtsidx++;
				appno_idx = smtsidx;
				pqrybuf.append("            AND T1.APP_NO=? ");
			}

			int pid_idx = 0;
			if(null!=pid&&""!=pid) {
				smtsidx++;
				pid_idx = smtsidx;
				pqrybuf.append("            AND T1.EXT_FIELD=? ");
			}

			int tran_idx = 0;
			if(null!=tradeidx&&""!=tradeidx) {
				smtsidx++;
				tran_idx = smtsidx;
				pqrybuf.append("            AND T1.TRANIDX=? ");
			}
			
			int mid_idx = 0;
			if(null!=mid&&""!=mid) {
				smtsidx++;
				mid_idx = smtsidx;
				pqrybuf.append("            AND T1.MID=? ");
			}
			
			int tid_idx = 0;
			if(null!=tid&&""!=tid) {
				smtsidx++;
				tid_idx = smtsidx;
				pqrybuf.append("            AND T1.TID=? ");
			}


			if(null!=acqcd&&""!=acqcd) {
				String[] acqexp = acqcd.split(",");
				String acqwh = "('" + utilm.implode("', '", acqexp) + "')";
				pqrybuf.append("            And acq_cd in " + acqwh);
			}
			
			if(depreq1.equals("Y")&& !depreq1.equals("")||depreq2.equals("Y")&& !depreq2.equals("")||depreq3.equals("Y")&& !depreq3.equals("")){
				String[] imp_dep = new String[3];
				
				if(depreq1.equals("Y")&&depreq2.equals("Y")&&depreq3.equals("Y")) {
					imp_dep[0] = "'TR00'";
					imp_dep[1] = "'RV01'";
					imp_dep[2] = "'DP99'";
				}else if(depreq1.equals("Y")&& !depreq1.equals("")){
					imp_dep[0] = "'TR00'";
				}else if(depreq2.equals("Y")&& !depreq2.equals("")){
					imp_dep[1] = "'RV01'";
				}else if(depreq3.equals("Y") && !depreq3.equals("")) {
					imp_dep[2] = "'DP99'";
				}
				pqrybuf.append(" AND NVL(TRAN_STAT,'TR00') IN (" + utilm.implode(",", imp_dep) + ") ");
			}
			
			pqrybuf.append("					ORDER BY APPDD DESC, APPTM DESC ");
			pqrybuf.append("				) ");
			pqrybuf.append("			) ");
		
			if(!auth01.equals("Y") && !auth01.equals("")||auth02.equals("Y")||auth03.equals("Y")) {
				String[] imp_auth = new String[2];
				if(null != auth02 && "" != auth02 && auth02.equals("Y")) {
					imp_auth[0] = "'신용승인'"; 
				}

				if(null != auth03 && "" != auth03 && auth03.equals("Y")) {
					imp_auth[1] = "'신용취소'"; 
				}
				pqrybuf.append(" WHERE APPGB_TXT IN (" + utilm.implode(",", imp_auth) + ") ");
			}
			
			pqrybuf.append("		) GROUP BY MID, APPGB, TRAN_STAT ");
			pqrybuf.append("	) GROUP BY MID  ");
			pqrybuf.append(") TT1 ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT ORG_CD, DEP_CD, MER_NO, PUR_CD FROM TB_BAS_MERINFO WHERE ORG_CD=? ");
			pqrybuf.append(")T3 ON(TT1.MID=T3.MER_NO) ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT PUR_CD, PUR_NM, PUR_SORT, PUR_KOCES FROM TB_BAS_PURINFO ");
			pqrybuf.append(")T6 ON(T3.PUR_CD=T6.PUR_CD) ");
			pqrybuf.append("ORDER BY PUR_SORT ASC  ");

			con2 = getOraConnect();
			stmt2 = con2.prepareStatement(pqrybuf.toString());
			
			stmt2.setString(1, userexp[1]);
			stmt2.setString(2, userexp[1]);
			if(null!=stime&&""!=stime) {stmt2.setString(stime_idx, stime);}
			if(null!=etime&&""!=etime) {stmt2.setString(etime_idx, etime);}
			if(null!=samt&&""!=samt) {stmt2.setString(samt_idx, samt);}
			if(null!=eamt&&""!=eamt) {stmt2.setString(eamt_idx, eamt);}
			if(null!=appno&&""!=appno) {stmt2.setString(appno_idx, appno);}
			if(null!=pid&&""!=pid) {stmt2.setString(pid_idx, pid);}
			if(null!=tradeidx&&""!=tradeidx) {stmt2.setString(tran_idx, tradeidx);}
			if(null!=mid&&""!=mid) {stmt2.setString(mid_idx, mid);}
			if(null!=tid&&""!=tid) {stmt2.setString(tid_idx, tid);}
			smtsidx++;
			stmt2.setString(smtsidx, userexp[1]);

			rs2 = stmt2.executeQuery();

			int icnt = 1;
			long AAMTSUM = 0, CAMTSUM = 0, RAAMTSUM = 0, RCAMTSUM = 0, TOTASUM = 0;
			int ACNTSUM = 0, CCNTSUM = 0, RACNTSUM = 0, RCCNTSUM = 0, TOTCSUM = 0;
			while(rs2.next()) {
				JSONObject obj1 = new JSONObject();
				JSONArray arr2 = new JSONArray();
				
				ACNTSUM += Integer.parseInt(rs2.getString("ACNT"));
				AAMTSUM += Integer.parseInt(rs2.getString("AAMT"));
				CCNTSUM += Integer.parseInt(rs2.getString("CCNT"));
				CAMTSUM += Integer.parseInt(rs2.getString("CAMT"));
				RACNTSUM += Integer.parseInt(rs2.getString("RACNT"));
				RAAMTSUM += Integer.parseInt(rs2.getString("RAAMT"));
				RCCNTSUM += Integer.parseInt(rs2.getString("RCCNT"));
				RCAMTSUM += Integer.parseInt(rs2.getString("RCAMT"));
				TOTCSUM += Integer.parseInt(rs2.getString("ACNT"))-Integer.parseInt(rs2.getString("CCNT"));
				TOTASUM += Integer.parseInt(rs2.getString("AAMT"))-Integer.parseInt(rs2.getString("CAMT"));
			
				arr2.add(icnt);
				arr2.add(rs2.getString("PUR_NM"));
				arr2.add(rs2.getString("ACNT"));
				arr2.add(rs2.getString("AAMT"));
				arr2.add(rs2.getString("CCNT"));
				arr2.add(rs2.getString("CAMT"));
				arr2.add(rs2.getString("RACNT"));
				arr2.add(rs2.getString("RAAMT"));
				arr2.add(rs2.getString("RCCNT"));
				arr2.add(rs2.getString("RCAMT"));
				arr2.add(rs2.getString("TCNT"));
				arr2.add(rs2.getString("TAMT"));

				obj1.put("id", Integer.toString(icnt));
				obj1.put("data", arr2);

				arr.add(obj1);
				icnt++;
			} 
			
			JSONObject obj1 = new JSONObject();
			JSONArray arr2 = new JSONArray();

			arr2.add("합계");
			arr2.add("");
			arr2.add(ACNTSUM);
			arr2.add(AAMTSUM);
			arr2.add(CCNTSUM);
			arr2.add(CAMTSUM);
			arr2.add(RACNTSUM);
			arr2.add(RAAMTSUM);
			arr2.add(RCCNTSUM);
			arr2.add(RCAMTSUM);
			arr2.add(TOTCSUM);
			arr2.add(TOTASUM);
			
			obj1.put("id", "total");
			obj1.put("data", arr2);

			arr.add(0, obj1);

			jrtnobj.put("rows", arr);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con2,stmt2,rs2);
		}
		return jrtnobj.toJSONString();
	}

	public String get_json_0309item(String tuser, String stime, String etime, String samt, String eamt, String appno, String pid, String tradeidx,
			String mid, String tid, String acqcd, String depcd, String auth01, String auth02, String auth03, String depreq1, String depreq2, String depreq3) {
		Connection con2 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		
		JSONObject jrtnobj = new JSONObject();

		JSONArray arr = new JSONArray();
		StringBuffer pqrybuf = new StringBuffer();
		//StringBuffer qrybuf = new StringBuffer();

		int smtsidx = 13;

		try {

			String[] userexp = tuser.split(":");
			String setdc = "";

			pqrybuf.append("SELECT ");
			pqrybuf.append("	SEQNO, DEP_NM, TERM_NM, TID, MID, PUR_NM,  ");
			pqrybuf.append("	 APPDD, APPTM,  OAPPDD, APPNO, APPGB, ");
			pqrybuf.append("	APPGB_TXT, CARDNO,	AMOUNT,	HALBU, CARDTP_TXT, SIGNCHK_TXT, ");
			pqrybuf.append("	REQ_DD,	AUTHCD,	REG_DD,	RTN_CD, RTN_TXT,  ");
			pqrybuf.append("	EXP_DD,	EXT_FIELD, ADD_CID,	TRANIDX, AUTHMSG, DPFLAG, DEPOREQDD, DEP_RTN_CD, ");
			pqrybuf.append("	TRAN_STAT ");
			pqrybuf.append("FROM( ");
			pqrybuf.append("	SELECT ");
			pqrybuf.append("		SEQNO, DEP_NM, TERM_NM, TID, MID, PUR_NM,  ");
			pqrybuf.append("		APPDD, APPTM, OAPPDD, APPNO, APPGB, ");
			pqrybuf.append("		CASE  ");
			pqrybuf.append("			WHEN APPGB='A' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0015') ");
			pqrybuf.append("			WHEN APPGB='C' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0016') ");
			pqrybuf.append("		END APPGB_TXT, ");
			pqrybuf.append("		CARDNO,	AMOUNT,	HALBU, ");
			pqrybuf.append("CASE 			 ");
			pqrybuf.append("			WHEN CHECK_CARD='Y' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0019') ");
			pqrybuf.append("			ELSE (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0018') END CARDTP_TXT, ");
			pqrybuf.append("		CASE			 ");
			pqrybuf.append("			WHEN SIGNCHK='1' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0021') ");
			pqrybuf.append("			ELSE (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0022') END SIGNCHK_TXT, ");
			pqrybuf.append("		REQ_DD,	AUTHCD,	REG_DD,	RTN_CD, ");
			pqrybuf.append("		CASE ");
			pqrybuf.append("			WHEN RTN_CD IS NULL THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0024') ");
			pqrybuf.append("			WHEN RTN_CD IN('60', '67') THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0025') ");
			pqrybuf.append("			WHEN RTN_CD IN('61', '64') THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0026')  ");
			pqrybuf.append("		END RTN_TXT, ");
			pqrybuf.append("		EXP_DD,	EXT_FIELD, ADD_CID,	TRANIDX, AUTHMSG, DPFLAG, DEPOREQDD, REQDEPTH, DEP_RTN_CD ");
			pqrybuf.append("		,TRAN_STAT ");
			pqrybuf.append("	FROM(SELECT ");
			pqrybuf.append("		SEQNO, BIZNO, TID, MID, VANGB, MDATE, SVCGB, T1.TRANIDX, T1.APPGB, ENTRYMD, ");
			pqrybuf.append("			T1.APPDD, APPTM, T1.APPNO, T1.CARDNO, HALBU, CURRENCY, T1.AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX, ");
			pqrybuf.append("			ISS_CD, ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG, CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB, ");
			pqrybuf.append("			SIGNCHK, DDCGB, EXT_FIELD, OAPPNO, OAPPDD, OAPPTM, OAPP_AMT, ADD_GB, ADD_CID, ADD_CD, ");
			pqrybuf.append("			ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, PUR_NM, DEP_NM, EXP_DD, REQ_DD, REG_DD, RSC_CD, RTN_CD, TERM_NM, ");
			pqrybuf.append("			DPFLAG, DEPOREQDD, REQDEPTH,  ");
			pqrybuf.append("			(SELECT RTN_CD FROM "+ userexp[6] +" WHERE TRANIDX = T1.TRANIDX AND APP_DD=T1.APPDD) DEP_RTN_CD ");
			pqrybuf.append("			,NVL(TRAN_STAT, 'TR00') TRAN_STAT ");
			pqrybuf.append("		FROM ");
			pqrybuf.append(userexp[5]);
			pqrybuf.append(" T1 ");
			pqrybuf.append("		LEFT OUTER JOIN( ");
			pqrybuf.append("			SELECT EXP_DD, REQ_DD, REG_DD, APP_DD, TRANIDX, RSC_CD, RTN_CD FROM "+ userexp[6]);
			pqrybuf.append("		)T2 ON(T1.APPDD=T2.APP_DD AND T1.TRANIDX=T2.TRANIDX) ");
			pqrybuf.append("		LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD=?)T3 ON(T1.TID=T3.TERM_ID) ");
			pqrybuf.append("		LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD=?)T4 ON(T3.DEP_CD=T4.DEP_CD) ");
			pqrybuf.append("		LEFT OUTER JOIN( SELECT PUR_NM, PUR_OCD, PUR_KOCES FROM TB_BAS_PURINFO)T5 ON (T1.ACQ_CD=T5.PUR_OCD OR T1.ACQ_CD=T5.PUR_KOCES) ");
			pqrybuf.append("WHERE SVCGB IN ('CC', 'CE') AND AUTHCD='0000' AND MID IN ( ");
			pqrybuf.append("SELECT MID FROM TB_BAS_MIDMAP MT1  ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("	SELECT MER_NO, MTYPE FROM TB_BAS_MERINFO WHERE ORG_CD=? AND MTYPE='EDI' ");
			pqrybuf.append(")MT2 ON(MT1.MID=MT2.MER_NO) ");
			pqrybuf.append("WHERE MT1.ORG_CD=? AND MT2.MTYPE='EDI' ");
			pqrybuf.append(") ");
			
			int stime_idx = 0;
			if(null!=stime&&""!=stime) {
				smtsidx++;
				stime_idx = smtsidx;
				pqrybuf.append("            AND T1.APPDD>=? ");
			}

			int etime_idx = 0;
			if(null!=etime&&""!=etime) {
				smtsidx++;
				etime_idx = smtsidx;
				pqrybuf.append("            AND T1.APPDD<=? ");
			}

			int samt_idx = 0;
			if(null!=samt&&""!=samt) {
				smtsidx++;
				samt_idx = smtsidx;
				pqrybuf.append("            AND T1.AMOUNT>=? ");
			}

			int eamt_idx = 0;
			if(null!=eamt&&""!=eamt) {
				smtsidx++;
				eamt_idx = smtsidx;
				pqrybuf.append("            AND T1.AMOUNT<=? ");
			}

			int appno_idx = 0;
			if(null!=appno&&""!=appno) {
				smtsidx++;
				appno_idx = smtsidx;
				pqrybuf.append("            AND T1.APP_NO=? ");
			}

			int pid_idx = 0;
			if(null!=pid&&""!=pid) {
				smtsidx++;
				pid_idx = smtsidx;
				pqrybuf.append("            AND T1.EXT_FIELD=? ");
			}

			int tran_idx = 0;
			if(null!=tradeidx&&""!=tradeidx) {
				smtsidx++;
				tran_idx = smtsidx;
				pqrybuf.append("            AND T1.TRANIDX=? ");
			}

			int mid_idx = 0;
			if(null!=mid&&""!=mid) {
				smtsidx++;
				mid_idx = smtsidx;
				pqrybuf.append("            AND T1.MID=? ");
			}

			int tid_idx = 0;
			if(null!=tid&&""!=tid) {
				smtsidx++;
				tid_idx = smtsidx;
				pqrybuf.append("            AND T1.TID=? ");
			}


			if(null!=acqcd&&""!=acqcd) {
				String[] acqexp = acqcd.split(",");
				String acqwh = "('" + utilm.implode("', '", acqexp) + "')";
				pqrybuf.append("            And acq_cd in " + acqwh);
			}

			if(depreq1.equals("Y")&& !depreq1.equals("")||depreq2.equals("Y")&& !depreq2.equals("")||depreq3.equals("Y")&& !depreq3.equals("")){
				String[] imp_dep = new String[3];
				
				if(depreq1.equals("Y")&&depreq2.equals("Y")&&depreq3.equals("Y")) {
					imp_dep[0] = "'TR00'";
					imp_dep[1] = "'RV01'";
					imp_dep[2] = "'DP99'";
				}else if(depreq1.equals("Y")&& !depreq1.equals("")){
					imp_dep[0] = "'TR00'";
				}else if(depreq2.equals("Y")&& !depreq2.equals("")){
					imp_dep[1] = "'RV01'";
				}else if(depreq3.equals("Y") && !depreq3.equals("")) {
					imp_dep[2] = "'DP99'";
				}
				pqrybuf.append(" AND NVL(TRAN_STAT,'TR00') IN (" + utilm.implode(",", imp_dep) + ") ");
			}

			pqrybuf.append("		ORDER BY APPDD DESC, APPTM DESC ");
			pqrybuf.append("	) ");
			pqrybuf.append(") ");
			
			if(!auth01.equals("Y") && !auth01.equals("")||auth02.equals("Y")||auth03.equals("Y")) {
				String[] imp_auth = new String[2];
				if(null != auth02 && "" != auth02 && auth02.equals("Y")) {
					imp_auth[0] = "'신용승인'"; 
				}

				if(null != auth03 && "" != auth03 && auth03.equals("Y")) {
					imp_auth[1] = "'신용취소'"; 
				}
				pqrybuf.append(" WHERE APPGB_TXT IN (" + utilm.implode(",", imp_auth) + ") ");
			}
			
			con2 = getOraConnect();
			stmt2 = con2.prepareStatement(pqrybuf.toString());
			
			
			stmt2.setString(1, userexp[1]);
			stmt2.setString(2, userexp[1]);
			stmt2.setString(3, userexp[1]);
			stmt2.setString(4, userexp[1]);
			stmt2.setString(5, userexp[1]);
			stmt2.setString(6, userexp[1]);
			stmt2.setString(7, userexp[1]);
			stmt2.setString(8, userexp[1]);
			stmt2.setString(9, userexp[1]);
			stmt2.setString(10, userexp[1]);
			stmt2.setString(11, userexp[1]);
			stmt2.setString(12, userexp[1]);
			stmt2.setString(13, userexp[1]);
			if(null!=stime&&""!=stime) {stmt2.setString(stime_idx, stime);}
			if(null!=etime&&""!=etime) {stmt2.setString(etime_idx, etime);}
			if(null!=samt&&""!=samt) {stmt2.setString(samt_idx, samt);}
			if(null!=eamt&&""!=eamt) {stmt2.setString(eamt_idx, eamt);}
			if(null!=appno&&""!=appno) {stmt2.setString(appno_idx, appno);}
			if(null!=pid&&""!=pid) {stmt2.setString(pid_idx, pid);}
			if(null!=tradeidx&&""!=tradeidx) {stmt2.setString(tran_idx, tradeidx);}
			if(null!=mid&&""!=mid) {stmt2.setString(mid_idx, mid);}
			if(null!=tid&&""!=tid) {stmt2.setString(tid_idx, tid);}

			rs2 = stmt2.executeQuery();

			int icnt = 1;
			while(rs2.next()) {
				JSONObject obj1 = new JSONObject();
				JSONArray arr2 = new JSONArray();

				String cardno_dec = utilm.cardno_masking(trans_seed_manager.seed_dec_card(rs2.getString("CARDNO").trim()));
				
				arr2.add(icnt);
				arr2.add(utilm.deposit_hold_check(rs2.getString("DPFLAG"))); //0:체크안함, 1:체크함
				arr2.add(rs2.getString("DEP_NM"));
				arr2.add(rs2.getString("TERM_NM"));
				arr2.add(rs2.getString("TID"));
				arr2.add(rs2.getString("MID"));
				arr2.add(rs2.getString("PUR_NM"));
				arr2.add("");
				arr2.add(utilm.str_to_dateformat(rs2.getString("APPDD")));
				arr2.add(utilm.str_to_timeformat(rs2.getString("APPTM")));
				arr2.add("");
				arr2.add(utilm.str_to_dateformat(rs2.getString("OAPPDD")));
				arr2.add(rs2.getString("APPNO"));
				arr2.add(utilm.set_appgb_to_kor(rs2.getString("APPGB")));
				arr2.add(cardno_dec);
				arr2.add(rs2.getString("AMOUNT"));
				arr2.add(rs2.getString("HALBU"));
				arr2.add(utilm.strcardtype(rs2.getString("CARDTP_TXT")));
				arr2.add(utilm.strsigngb(rs2.getString("SIGNCHK_TXT")));
				arr2.add(utilm.str_to_dateformat(rs2.getString("REQ_DD"))); //매입요청일자
				arr2.add(rs2.getString("AUTHCD")); //매출코드
				arr2.add(utilm.str_to_dateformat(rs2.getString("REG_DD"))); //매입응답일
				arr2.add(utilm.deposit_result_str(rs2.getString("RTN_TXT"))); //매입결과
				arr2.add(utilm.str_to_dateformat(rs2.getString("EXP_DD"))); //입금예정일
				arr2.add(rs2.getString("ADD_CID")); //등록번호
				arr2.add(rs2.getString("TRANIDX")); //거래일련번호
				arr2.add(rs2.getString("AUTHMSG")); //카드사응답내용
				arr2.add(utilm.deposit_state_check(rs2.getString("TRAN_STAT"))); //청구/매입상태

				obj1.put("id", rs2.getString("SEQNO"));
				obj1.put("data", arr2);

				arr.add(obj1);
				icnt++;
			} 

			jrtnobj.put("rows", arr);
			setOraClose(con,stmt,rs);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con2,stmt2,rs2);
		}
		return jrtnobj.toJSONString();
	}
	
	public String get_json_0310total(String tuser, String reqstime, String reqetime, String stime, String etime) {
		Connection con2 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		
		JSONObject jrtnobj = new JSONObject();

		JSONArray arr = new JSONArray();
		StringBuffer pqrybuf = new StringBuffer();
		StringBuffer qrybuf = new StringBuffer();

		try {

			String[] userexp = tuser.split(":");
			//검색항목에 따른 where 조건절 setting 관련 변수
			ArrayList<String> setting = new ArrayList<>();

			pqrybuf.append("SELECT  ");
			pqrybuf.append("	DEPOSEQ, ORGCD, USERID, BIZNO, SDATE, EDATE,      ");
			pqrybuf.append("	ACNT, AAMT, CCNT, CAMT, (ACNT+CCNT) NORCNT,  ");
			pqrybuf.append("	(AAMT-CAMT) NORAMT, HACNT, HAAMT, HCCNT, HCAMT, ");
			pqrybuf.append("	(HACNT+HCCNT) HALBUCNT, (HAAMT-HCAMT) HALBUAMT, ");
			pqrybuf.append("	(ACNT+CCNT+HACNT+HCCNT) TOTCNT, ");
			pqrybuf.append("	((AAMT+HAAMT)-(CAMT+HCAMT)) TOTAMT, ");
			pqrybuf.append("	to_char(regdate, 'yyyyMMdd') REQDATE ");
			pqrybuf.append("FROM ");
			pqrybuf.append("	TB_HIS_DPREQ_TOT ");

			if(null!=reqstime&&""!=reqstime) {
				pqrybuf.append("           WHERE to_char(regdate, 'yyyyMMdd')>=? ");
				setting.add(reqstime);
			}

			if(null!=reqetime&&""!=reqetime) {
				pqrybuf.append("            AND to_char(regdate, 'yyyyMMdd')<=? ");
				setting.add(reqetime);
			}
			
			if(null!=stime&&""!=stime) {
				pqrybuf.append("            AND SDATE>=? ");
				setting.add(stime);
			}
			
			if(null!=etime&&""!=etime) {
				pqrybuf.append("            AND EDATE<=? ");
				setting.add(etime);
			}
			
			pqrybuf.append("ORDER BY REQDATE DESC ");
			
			//디버깅용
			utilm.debug_sql(pqrybuf, setting);

			con2 = getOraConnect();
			stmt2 = con2.prepareStatement(pqrybuf.toString());
			for(int k = 0; k<setting.size(); k++) {
				stmt2.setString((k+1), setting.get(k));
			}
			rs2 = stmt2.executeQuery();

			int icnt = 1;
			int TO_ACNT=0, TO_CCNT=0, TO_NORCNT=0, TO_HACNT=0, TO_HCCNT=0, TO_HALBUCNT=0, TO_TOTCNT=0;
			long TO_AAMT=0, TO_CAMT=0, TO_NORAMT=0, TO_HAAMT=0, TO_HCAMT=0, TO_HALBUAMT=0, TO_TOTAMT=0;
			while(rs2.next()) {
				JSONObject obj1 = new JSONObject();
				JSONArray arr2 = new JSONArray();

				TO_ACNT		+= Integer.parseInt(rs2.getString("ACNT"));
				TO_AAMT		+= Integer.parseInt(rs2.getString("AAMT"));
				TO_CCNT		+= Integer.parseInt(rs2.getString("CCNT"));
				TO_CAMT		+= Integer.parseInt(rs2.getString("CAMT"));
				TO_NORCNT	+= Integer.parseInt(rs2.getString("NORCNT"));
				TO_NORAMT	+= Integer.parseInt(rs2.getString("NORAMT"));
				TO_HACNT	+= Integer.parseInt(rs2.getString("HACNT"));
				TO_HAAMT	+= Integer.parseInt(rs2.getString("HAAMT"));
				TO_HCCNT	+= Integer.parseInt(rs2.getString("HCCNT"));
				TO_HCAMT	+= Integer.parseInt(rs2.getString("HCAMT"));
				TO_HALBUCNT	+= Integer.parseInt(rs2.getString("HALBUCNT"));
				TO_HALBUAMT	+= Integer.parseInt(rs2.getString("HALBUAMT"));
				TO_TOTCNT	+= Integer.parseInt(rs2.getString("TOTCNT"));
				TO_TOTAMT	+= Integer.parseInt(rs2.getString("TOTAMT"));
				
				arr2.add(icnt);
				arr2.add(utilm.str_to_dateformat_deposit(utilm.setDefault(rs2.getString("REQDATE"))));
				arr2.add(utilm.setDefault(rs2.getString("BIZNO")));
				arr2.add(utilm.str_to_dateformat_deposit(utilm.setDefault(rs2.getString("EDATE"))));
				arr2.add(utilm.str_to_dateformat_deposit(utilm.setDefault(rs2.getString("SDATE"))));
				arr2.add(utilm.setDefault(rs2.getString("ACNT")));
				arr2.add(utilm.setDefault(rs2.getString("AAMT")));
				arr2.add(utilm.setDefault(rs2.getString("CCNT")));
				arr2.add(utilm.setDefault(rs2.getString("CAMT")));
				arr2.add(utilm.setDefault(rs2.getString("NORCNT")));
				arr2.add(utilm.setDefault(rs2.getString("NORAMT")));
				arr2.add(utilm.setDefault(rs2.getString("HACNT")));
				arr2.add(utilm.setDefault(rs2.getString("HAAMT")));
				arr2.add(utilm.setDefault(rs2.getString("HCCNT")));
				arr2.add(utilm.setDefault(rs2.getString("HCAMT")));
				arr2.add(utilm.setDefault(rs2.getString("HALBUCNT")));
				arr2.add(utilm.setDefault(rs2.getString("HALBUAMT")));
				arr2.add(utilm.setDefault(rs2.getString("TOTCNT")));
				arr2.add(utilm.setDefault(rs2.getString("TOTAMT")));
				arr2.add(utilm.setDefault(rs2.getString("USERID")));
				
				obj1.put("id", rs2.getString("DEPOSEQ"));
				obj1.put("data", arr2);

				arr.add(obj1);
				icnt++;
			}
			
			//합계 계산
			JSONObject obj1 = new JSONObject();
			JSONArray arr2 = new JSONArray();

			arr2.add("합계");
			arr2.add("");
			arr2.add("");
			arr2.add("");
			arr2.add("");
			arr2.add(TO_ACNT);
			arr2.add(TO_AAMT);
			arr2.add(TO_CCNT);
			arr2.add(TO_CAMT);
			arr2.add(TO_NORCNT);
			arr2.add(TO_NORAMT);
			arr2.add(TO_HACNT);
			arr2.add(TO_HAAMT);
			arr2.add(TO_HCCNT);
			arr2.add(TO_HCAMT);
			arr2.add(TO_HALBUCNT);
			arr2.add(TO_HALBUAMT);
			arr2.add(TO_TOTCNT);
			arr2.add(TO_TOTAMT);
			arr2.add("");

			obj1.put("id", "total");
			obj1.put("data", arr2);

			arr.add(obj1);

			jrtnobj.put("rows", arr);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con2,stmt2,rs2);
		}
		return jrtnobj.toJSONString();
	}
	
	public String get_json_0310detail_total(String tuser, String reqstime, String reqetime, String samt, String eamt, String appno, String pid
			, String tradeidx, String acqcd, String tid, String deposeq,  String depcd, String auth01, String auth02, String auth03, String tstat01, String tstat02, String tstat03, 
			String tstat04, String mid) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		StringBuffer pqrybuf = new StringBuffer();
		StringBuffer wherebuf = new StringBuffer();
		StringBuffer exwherebuf = new StringBuffer();

		JSONObject sqlobj = new JSONObject();
		JSONArray objAry = new JSONArray();

		try {
			//tuser, stime, etime, acqcd, depcd, mid
			//tuser split
			String[] userexp = tuser.split(":");
			//acqcd split
			String[] acqcdexp = acqcd.split(",");
			//검색항목에 따른 where 조건절 setting 관련 변수
			ArrayList<String> setting = new ArrayList<>();

			wherebuf.append(" WHERE SVCGB IN ('CC', 'CE')  AND AUTHCD='0000' AND TID IN (SELECT TID FROM TB_BAS_TIDMAP WHERE ORG_CD=?) ");
			setting.add(userexp[1]);
			
			if(!reqstime.equals("") && reqstime != null) {
				wherebuf.append(" AND T1.DEPOREQDD >= ? ");
				setting.add(reqstime);
			}

			if(!reqetime.equals("") && reqetime != null) {
				wherebuf.append(" AND T1.DEPOREQDD <= ? ");
				setting.add(reqetime);
			}
			
			if(!samt.equals("") && samt != null) {
				wherebuf.append(" AND T1.AMOUNT >= ? ");
				setting.add(samt);
			}
			
			if(!eamt.equals("") && eamt != null) {
				wherebuf.append(" AND T1.AMOUNT <= ? ");
				setting.add(samt);
			}
			
			if(!appno.equals("") && appno != null) {
				wherebuf.append(" AND T1.APPNO <= ? ");
				setting.add(appno);
			}
			
			if(!pid.equals("") && pid != null) {
				wherebuf.append(" AND T1.ADD_CID <= ? ");
				setting.add(pid);
			}
			
			if(!tradeidx.equals("") && tradeidx != null) {
				wherebuf.append(" AND T1.TRADEIDX <= ? ");
				setting.add(tradeidx);
			}

			//원래 쿼리는 카드사도 안나옴...
			if(!acqcd.equals("") && acqcd != null) {
				wherebuf.append(" AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD=? AND ORG_CD=? ) ");
				setting.add(acqcd);
				setting.add(userexp[1]);
			}
			
			if(!tid.equals("") && tid != null) {
				wherebuf.append(" AND T1.TID = ? ");
				setting.add(tid);
			}
			
			if(!mid.equals("") && mid != null) {
				wherebuf.append(" AND T1.MID = ? ");
				setting.add(mid);
			}
			
			if(!deposeq.equals("") && deposeq != null) {
				wherebuf.append(" AND T1.DEPOSEQ = ? ");
				setting.add(deposeq);
			}
			
			if(!depcd.equals("") && depcd != null) {
				if(depcd=="1") {
					wherebuf.append(" AND MID IN ('768017318','00052904921','00951457027','128890479','57296808','151098345','9052663887','151558364','721176212','00098153744','154944840','00903164052','0118721620','179102374','178597603','9956970402' ) ");
				}else if(depcd=="2") {
					wherebuf.append(" AND MID IN ('704855398','00084542316','00986653087','165138860','0104783451','860295101','9969229911','140239694','721219360','00098234952','155068491','00903276708','0118796648','179216357','178600027','9957975427' ) ");
				}else if(depcd=="3") {
					wherebuf.append(" AND MID IN ('707528764','00087259990','00989439518','167802984','0107608507','860386610','9967457077','143275451','721225822','00098235865','155072408','00903280940','0118799154','179216254','178600545','9957971095' )");
				}
			}
			
			//auth01 전체, auth02 승인, auth03 취소
			if(!auth01.equals("Y")){
				if(auth02.equals("Y")){exwherebuf.append("  APPGB_TXT = '신용승인' AND ");}
				else if(auth03.equals("Y")){exwherebuf.append("  APPGB_TXT = '신용취소' AND ");}
				else if(auth02.equals("Y") && auth03.equals("Y")) {exwherebuf.append("  APPGB_TXT IN ('신용승인', '신용취소') AND ");}
			}
			
			if(!tstat01.equals("Y")){
				if(tstat02.equals("Y")){exwherebuf.append("  TSTAT_TXT = '정상거래'");}
				else if(tstat03.equals("Y")){exwherebuf.append("  TSTAT_TXT = '당일취소'");}
				else if(tstat04.equals("Y")){exwherebuf.append("  TSTAT_TXT = '전일취소'");}
				else if(tstat02.equals("Y") && tstat03.equals("Y") && tstat04.equals("Y")) {exwherebuf.append("  TSTAT_TXT IN ('정상거래', '당일취소', '전일취소')");}
			}
			
			pqrybuf.append("SELECT ");
			pqrybuf.append("     DEP_NM ");
			pqrybuf.append("	,TERM_ID ");
			pqrybuf.append("	,TERM_NM ");
			pqrybuf.append("    ,ACNT ");
			pqrybuf.append("    ,CCNT ");
			pqrybuf.append("    ,AAMT ");
			pqrybuf.append("    ,CAMT ");
			pqrybuf.append("    ,TOTCNT ");
			pqrybuf.append("    ,TOTAMT ");
			pqrybuf.append("    ,BC ");
			pqrybuf.append("    ,NH ");
			pqrybuf.append("    ,KB ");
			pqrybuf.append("    ,SS ");
			pqrybuf.append("    ,HN ");
			pqrybuf.append("    ,LO ");
			pqrybuf.append("    ,HD ");
			pqrybuf.append("    ,SI ");
			pqrybuf.append("FROM(     ");
			pqrybuf.append("    SELECT ");
			pqrybuf.append("         TID ");
			pqrybuf.append("        ,SUM(ACNT) ACNT ");
			pqrybuf.append("        ,SUM(CCNT) CCNT ");
			pqrybuf.append("        ,SUM(AAMT) AAMT ");
			pqrybuf.append("        ,SUM(CAMT) CAMT ");
			pqrybuf.append("        ,SUM(ACNT)+SUM(CCNT) TOTCNT ");
			pqrybuf.append("        ,SUM(AAMT)-SUM(CAMT) TOTAMT ");
			pqrybuf.append("        ,SUM(ABC  )-SUM(CBC  ) BC ");
			pqrybuf.append("        ,SUM(ANH  )-SUM(CNH  ) NH ");
			pqrybuf.append("        ,SUM(AKB  )-SUM(CKB  ) KB ");
			pqrybuf.append("        ,SUM(ASS  )-SUM(CSS  ) SS ");
			pqrybuf.append("        ,SUM(AHN  )-SUM(CHN  ) HN ");
			pqrybuf.append("        ,SUM(ALO  )-SUM(CLO  ) LO ");
			pqrybuf.append("        ,SUM(AHD  )-SUM(CHD  ) HD ");
			pqrybuf.append("        ,SUM(ASI  )-SUM(CSI  ) SI ");
			pqrybuf.append("    FROM(     ");
			pqrybuf.append("        SELECT ");
			pqrybuf.append("            TID ");
			pqrybuf.append("            ,CASE WHEN APPGB='A' THEN COUNT(1) ELSE 0 END ACNT ");
			pqrybuf.append("            ,CASE WHEN APPGB='C' THEN COUNT(1) ELSE 0 END CCNT ");
			pqrybuf.append("            ,CASE WHEN APPGB='A' THEN SUM(AMOUNT) ELSE 0 END AAMT ");
			pqrybuf.append("            ,CASE WHEN APPGB='C' THEN SUM(AMOUNT) ELSE 0 END CAMT ");
			pqrybuf.append("            ,CASE WHEN APPGB='A' AND MID IN ('704855398', '707528764', '768017318') THEN SUM(AMOUNT) ELSE 0 END ABC ");
			pqrybuf.append("            ,CASE WHEN APPGB='A' AND MID IN ('143275451', '140239694') THEN SUM(AMOUNT) ELSE 0 END ANH ");
			pqrybuf.append("            ,CASE WHEN APPGB='A' AND MID IN ('00052904921', '00087259990', '00084542316') THEN SUM(AMOUNT) ELSE 0 END AKB ");
			pqrybuf.append("            ,CASE WHEN APPGB='A' AND MID IN ('167802984', '128890479', '165138860') THEN SUM(AMOUNT) ELSE 0 END ASS ");
			pqrybuf.append("            ,CASE WHEN APPGB='A' AND MID IN ('00989439518', '00951457027', '00986653087') THEN SUM(AMOUNT) ELSE 0 END AHN ");
			pqrybuf.append("            ,CASE WHEN APPGB='A' AND MID IN ('9969229911', '9052663887', '9967457077') THEN SUM(AMOUNT) ELSE 0 END ALO ");
			pqrybuf.append("            ,CASE WHEN APPGB='A' AND MID IN ('860295101', '860386610', '151098345') THEN SUM(AMOUNT) ELSE 0 END AHD ");
			pqrybuf.append("            ,CASE WHEN APPGB='A' AND MID IN ('57296808', '0107608507', '0104783451') THEN SUM(AMOUNT) ELSE 0 END ASI ");
			pqrybuf.append("            ,CASE WHEN APPGB='C' AND MID IN ('704855398', '707528764', '768017318') THEN SUM(AMOUNT) ELSE 0 END CBC ");
			pqrybuf.append("            ,CASE WHEN APPGB='C' AND MID IN ('143275451', '140239694') THEN SUM(AMOUNT) ELSE 0 END CNH ");
			pqrybuf.append("            ,CASE WHEN APPGB='C' AND MID IN ('00052904921', '00087259990', '00084542316') THEN SUM(AMOUNT) ELSE 0 END CKB ");
			pqrybuf.append("            ,CASE WHEN APPGB='C' AND MID IN ('167802984', '128890479', '165138860') THEN SUM(AMOUNT) ELSE 0 END CSS ");
			pqrybuf.append("            ,CASE WHEN APPGB='C' AND MID IN('00989439518', '00951457027', '00986653087') THEN SUM(AMOUNT) ELSE 0 END CHN ");
			pqrybuf.append("            ,CASE WHEN APPGB='C' AND MID IN ('9969229911', '9052663887', '9967457077') THEN SUM(AMOUNT) ELSE 0 END CLO ");
			pqrybuf.append("            ,CASE WHEN APPGB='C' AND MID IN ('860295101', '860386610', '151098345') THEN SUM(AMOUNT) ELSE 0 END CHD ");
			pqrybuf.append("            ,CASE WHEN APPGB='C' AND MID IN ('57296808', '0107608507', '0104783451') THEN SUM(AMOUNT) ELSE 0 END CSI ");
			pqrybuf.append("        FROM ( ");
			pqrybuf.append("            SELECT ");
			pqrybuf.append("				SEQNO, DEP_NM, TERM_NM, TID, MID, PUR_NM, TSTAT, ACQ_CD,  ");
			pqrybuf.append("				TSTAT_TXT, APPDD, APPTM, TSTAT CANDATE, OAPPDD, APPNO, APPGB, ");
			pqrybuf.append("				APPGB_TXT, CARDNO,	AMOUNT,	HALBU, CARDTP_TXT, SIGNCHK_TXT, ");
			pqrybuf.append("				REQ_DD,	AUTHCD,	REG_DD,	RTN_CD, RTN_TXT,  ");
			pqrybuf.append("				EXP_DD,	EXT_FIELD,	TRANIDX, AUTHMSG ");
			pqrybuf.append("			FROM( ");
			pqrybuf.append("				SELECT ");
			pqrybuf.append("					SEQNO, DEP_NM, TERM_NM, TID, MID, PUR_NM, TSTAT, ");
			pqrybuf.append("					CASE ");
			pqrybuf.append("						WHEN APPGB='A' AND TSTAT IS NULL THEN '정상거래' ");
			pqrybuf.append("						WHEN APPGB='A' AND TSTAT=APPDD THEN '당일취소' ");
			pqrybuf.append("						WHEN APPGB='C' AND APPDD=OAPPDD THEN '당일취소' ");
			pqrybuf.append("						WHEN APPGB='C' AND APPDD<>OAPPDD THEN '전일취소' ");
			pqrybuf.append("						WHEN APPGB='A' AND APPDD<>TSTAT AND TSTAT IS NOT NULL THEN '전일취소' ");
			pqrybuf.append("					END TSTAT_TXT, ");
			pqrybuf.append("					APPDD, APPTM, TSTAT CANDATE, OAPPDD, APPNO, APPGB, ACQ_CD, ");
			pqrybuf.append("					CASE  ");
			pqrybuf.append("						WHEN APPGB='A' THEN '신용승인' ");
			pqrybuf.append("						WHEN APPGB='C' THEN '신용취소' ");
			pqrybuf.append("					END APPGB_TXT, ");
			pqrybuf.append("					CARDNO,	AMOUNT,	HALBU, ");
			pqrybuf.append("					CASE WHEN CHECK_CARD='Y' THEN '체크카드' ELSE '신용카드' END CARDTP_TXT, ");
			pqrybuf.append("					CASE WHEN SIGNCHK='1' THEN '전자서명' ELSE '무서명' END SIGNCHK_TXT, ");
			pqrybuf.append("					REQ_DD,	AUTHCD,	REG_DD,	RTN_CD, ");
			pqrybuf.append("					CASE ");
			pqrybuf.append("						WHEN RTN_CD IS NULL THEN '결과없음' ");
			pqrybuf.append("						WHEN RTN_CD IN('60', '67') THEN '정상매입' ");
			pqrybuf.append("						WHEN RTN_CD IN('61', '64') THEN '매입반송' ");
			pqrybuf.append("					END RTN_TXT, ");
			pqrybuf.append("					EXP_DD,	EXT_FIELD,	TRANIDX, AUTHMSG ");
			pqrybuf.append("				FROM( ");
			pqrybuf.append("					SELECT ");
			pqrybuf.append("						SEQNO, BIZNO, TID, MID, VANGB, MDATE, SVCGB, T1.TRANIDX, T1.APPGB, ENTRYMD, ");
			pqrybuf.append("						T1.APPDD, APPTM, T1.APPNO, T1.CARDNO, HALBU, CURRENCY, T1.AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX, ");
			pqrybuf.append("						ISS_CD, ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG, CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB, ");
			pqrybuf.append("						SIGNCHK, DDCGB, EXT_FIELD, OAPPNO, OAPPDD, OAPPTM, OAPP_AMT, ADD_GB, ADD_CID, ADD_CD, ");
			pqrybuf.append("						ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, PUR_NM, DEP_NM, EXP_DD, REQ_DD, REG_DD, RSC_CD, RTN_CD, TERM_NM, ");
			pqrybuf.append("						CASE ");
			pqrybuf.append("							WHEN APPGB='C' THEN '' ");
			pqrybuf.append("							WHEN APPGB='A' THEN (SELECT C1.APPDD FROM $UserExpAuth[5] C1 WHERE C1.APPGB='C' AND T1.APPDD=C1.OAPPDD AND T1.APPNO=C1.APPNO AND T1.AMOUNT=C1.AMOUNT AND T1.CARDNO=C1.CARDNO) ");
			pqrybuf.append("						END TSTAT ");
			pqrybuf.append("					FROM ");
			pqrybuf.append("						glob_mng_icvan_cvs T1 ");
			pqrybuf.append("					LEFT OUTER JOIN( ");
			pqrybuf.append("						SELECT EXP_DD, REQ_DD, REG_DD, APP_DD, TRANIDX, RSC_CD, RTN_CD FROM TB_MNG_DEPDATA ");
			pqrybuf.append("					)T2 ON(T1.APPDD=T2.APP_DD AND T1.TRANIDX=T2.TRANIDX) ");
			pqrybuf.append("					LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST $ORG_WH)T3 ON(T1.TID=T3.TERM_ID) ");
			pqrybuf.append("					LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART $ORG_WH)T4 ON(T3.DEP_CD=T4.DEP_CD) ");
			pqrybuf.append("					LEFT OUTER JOIN( SELECT PUR_NM, PUR_OCD, PUR_KOCES FROM TB_BAS_PURINFO)T5 ON (T1.ACQ_CD=T5.PUR_OCD OR T1.ACQ_CD=T5.PUR_KOCES ) ");
			pqrybuf.append("					LEFT OUTER JOIN( SELECT PUR_CD, MER_NO, ORG_CD  FROM TB_BAS_MERINFO)T6 ON (T1.MID = T6.MER_NO AND ORG_CD='$UserExpAuth[1]') ");
			setting.add(userexp[1]);
			if(userexp[2] != null && !userexp[2].equals("")) {
				pqrybuf.append(" and dep_cd = ?");
				setting.add(userexp[2]);
			}
			pqrybuf.append(					wherebuf.toString());
			pqrybuf.append("					order by appdd desc, apptm desc ");
			pqrybuf.append("				) ");
			pqrybuf.append("			) ");
			if(exwherebuf != null) {
				pqrybuf.append("WHERE ");
			}
			pqrybuf.append(			exwherebuf.toString());
			pqrybuf.append("		) ");
			pqrybuf.append("		GROUP BY TID, APPGB, MID ");
			pqrybuf.append("	) ");
			pqrybuf.append("	GROUP BY TID ");
			pqrybuf.append("	)T2 ");
			pqrybuf.append("LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD=?)T3 ON(T2.TID=T3.TERM_ID) ");
			setting.add(userexp[1]);
			pqrybuf.append("LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD=?)T4 ON(T3.DEP_CD=T4.DEP_CD) ");
			setting.add(userexp[1]);
			
			//디버깅용
			utilm.debug_sql(pqrybuf, setting);

			con = getOraConnect();
			stmt = con.prepareStatement(pqrybuf.toString());
			for (int k = 0; k < setting.size(); k++) {
				stmt.setString((k + 1), setting.get(k));
			}

			rs = stmt.executeQuery();
			
			int icnt = 1;
			int ACNTSUM=0, CCNTSUM=0, TOTCSUM=0;
			long AAMTSUM=0, CAMTSUM=0, TOTASUM=0, BCTOT=0, NHTOT=0, KBTOT=0, SSTOT=0, HNTOT=0, LOTOT=0, HDTOT=0, SITOT=0;
			while(rs.next()) { 
			  JSONObject tempObj = new JSONObject(); 
			  JSONArray tempAry = new JSONArray();
			  
			  String dep = rs.getString("DEP_NM")+"("+rs.getString("TERM_NM")+":"+rs.getString("TERM_ID")+")";
			  
			  	ACNTSUM		+= Integer.parseInt(rs.getString("ACNT"));
				AAMTSUM		+= Integer.parseInt(rs.getString("AAMT"));
				CCNTSUM		+= Integer.parseInt(rs.getString("CCNT"));
				CAMTSUM		+= Integer.parseInt(rs.getString("CAMT"));
				TOTCSUM		+= Integer.parseInt(rs.getString("TOTCNT"));
				TOTASUM		+= Integer.parseInt(rs.getString("TOTAMT"));
				BCTOT		+= Integer.parseInt(rs.getString("BC"));
				NHTOT		+= Integer.parseInt(rs.getString("NH"));
				KBTOT		+= Integer.parseInt(rs.getString("KB"));
				SSTOT		+= Integer.parseInt(rs.getString("SS"));
				HNTOT		+= Integer.parseInt(rs.getString("HN"));
				LOTOT		+= Integer.parseInt(rs.getString("LO"));
				HDTOT		+= Integer.parseInt(rs.getString("HD"));
				SITOT		+= Integer.parseInt(rs.getString("SI"));
				
				tempAry.add(icnt);
				tempAry.add(dep);
				tempAry.add(utilm.setDefault(rs.getString("ACNT")));
				tempAry.add(utilm.setDefault(rs.getString("AAMT")));
				tempAry.add(utilm.setDefault(rs.getString("CCNT")));
				tempAry.add(utilm.setDefault(rs.getString("CAMT")));
				tempAry.add(utilm.setDefault(rs.getString("TOTCNT")));
				tempAry.add(utilm.setDefault(rs.getString("TOTAMT")));
				tempAry.add(utilm.setDefault(rs.getString("BC")));
				tempAry.add(utilm.setDefault(rs.getString("NH")));
				tempAry.add(utilm.setDefault(rs.getString("KB")));
				tempAry.add(utilm.setDefault(rs.getString("SS")));
				tempAry.add(utilm.setDefault(rs.getString("HN")));
				tempAry.add(utilm.setDefault(rs.getString("LO")));
				tempAry.add(utilm.setDefault(rs.getString("HD")));
				tempAry.add(utilm.setDefault(rs.getString("SI")));
				
				tempObj.put("id", icnt);
				tempObj.put("data", tempAry);

				objAry.add(tempObj);
				icnt++;
			  }

				//합계 계산
				JSONObject tempObj = new JSONObject();
				JSONArray tempAry = new JSONArray();
	
				tempAry.add("합계");
				tempAry.add("");
				tempAry.add(ACNTSUM);
				tempAry.add(AAMTSUM);
				tempAry.add(CCNTSUM);
				tempAry.add(CAMTSUM);
				tempAry.add(TOTCSUM);
				tempAry.add(TOTASUM);
				tempAry.add(BCTOT);
				tempAry.add(NHTOT);
				tempAry.add(KBTOT);
				tempAry.add(SSTOT);
				tempAry.add(HNTOT);
				tempAry.add(LOTOT);
				tempAry.add(HDTOT);
				tempAry.add(SITOT);
	
				tempObj.put("id", "total");
				tempObj.put("data", tempAry);
	
				objAry.add(tempObj);
	
				tempObj.put("rows", tempAry);
			  
			 sqlobj.put("rows", objAry);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}

		return sqlobj.toJSONString();
	}
	
	public String get_json_0310detail_item(String tuser, String reqstime, String reqetime, String samt, String eamt, String appno, String pid
			, String tradeidx, String acqcd, String tid, String deposeq,  String depcd, String auth01, String auth02, String auth03, String tstat01, String tstat02, String tstat03, 
			String tstat04, String mid) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		StringBuffer qrybuf = new StringBuffer();
		StringBuffer wherebuf = new StringBuffer();
		StringBuffer exwherebuf = new StringBuffer();

		JSONObject sqlobj = new JSONObject();
		JSONArray objAry = new JSONArray();
		
		ArrayList<String> pos_field = get_column_field(tuser, "van", "field");

		try {
			//tuser, stime, etime, acqcd, depcd, mid
			//tuser split
			String[] userexp = tuser.split(":");
			//acqcd split
			String[] acqcdexp = acqcd.split(",");
			//검색항목에 따른 where 조건절 setting 관련 변수
			ArrayList<String> setting = new ArrayList<>();

			wherebuf.append(" WHERE SVCGB IN ('CC', 'CE')  AND AUTHCD='0000' AND TID IN (SELECT TID FROM TB_BAS_TIDMAP WHERE ORG_CD=?) ");
			setting.add(userexp[1]);
			
			if(!reqstime.equals("") && reqstime != null) {
				wherebuf.append(" AND T1.DEPOREQDD >= ? ");
				setting.add(reqstime);
			}

			if(!reqetime.equals("") && reqetime != null) {
				wherebuf.append(" AND T1.DEPOREQDD <= ? ");
				setting.add(reqetime);
			}
			
			if(!samt.equals("") && samt != null) {
				wherebuf.append(" AND T1.AMOUNT >= ? ");
				setting.add(samt);
			}
			
			if(!eamt.equals("") && eamt != null) {
				wherebuf.append(" AND T1.AMOUNT <= ? ");
				setting.add(samt);
			}
			
			if(!appno.equals("") && appno != null) {
				wherebuf.append(" AND T1.APPNO <= ? ");
				setting.add(appno);
			}
			
			if(!pid.equals("") && pid != null) {
				wherebuf.append(" AND T1.ADD_CID <= ? ");
				setting.add(pid);
			}
			
			if(!tradeidx.equals("") && tradeidx != null) {
				wherebuf.append(" AND T1.TRADEIDX <= ? ");
				setting.add(tradeidx);
			}

			//원래 쿼리는 카드사도 안나옴...
			if(!acqcd.equals("") && acqcd != null) {
				wherebuf.append(" AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD=? AND ORG_CD=? ) ");
				setting.add(acqcd);
				setting.add(userexp[1]);
			}
			
			if(!tid.equals("") && tid != null) {
				wherebuf.append(" AND T1.TID = ? ");
				setting.add(tid);
			}
			
			if(!mid.equals("") && mid != null) {
				wherebuf.append(" AND T1.MID = ? ");
				setting.add(mid);
			}
			
			if(!deposeq.equals("") && deposeq != null) {
				wherebuf.append(" AND T1.DEPOSEQ = ? ");
				setting.add(deposeq);
			}
			
			if(!depcd.equals("") && depcd != null) {
				if(depcd=="1") {
					wherebuf.append(" AND MID IN ('768017318','00052904921','00951457027','128890479','57296808','151098345','9052663887','151558364','721176212','00098153744','154944840','00903164052','0118721620','179102374','178597603','9956970402' ) ");
				}else if(depcd=="2") {
					wherebuf.append(" AND MID IN ('704855398','00084542316','00986653087','165138860','0104783451','860295101','9969229911','140239694','721219360','00098234952','155068491','00903276708','0118796648','179216357','178600027','9957975427' ) ");
				}else if(depcd=="3") {
					wherebuf.append(" AND MID IN ('707528764','00087259990','00989439518','167802984','0107608507','860386610','9967457077','143275451','721225822','00098235865','155072408','00903280940','0118799154','179216254','178600545','9957971095' )");
				}
			}
			
			//auth01 전체, auth02 승인, auth03 취소
			if(!auth01.equals("Y")){
				if(auth02.equals("Y")){exwherebuf.append("  APPGB_TXT = '신용승인' AND ");}
				else if(auth03.equals("Y")){exwherebuf.append("  APPGB_TXT = '신용취소' AND ");}
				else if(auth02.equals("Y") && auth03.equals("Y")) {exwherebuf.append("  APPGB_TXT IN ('신용승인', '신용취소') AND ");}
			}
			
			if(!tstat01.equals("Y")){
				if(tstat02.equals("Y")){exwherebuf.append("  TSTAT_TXT = '정상거래'");}
				else if(tstat03.equals("Y")){exwherebuf.append("  TSTAT_TXT = '당일취소'");}
				else if(tstat04.equals("Y")){exwherebuf.append("  TSTAT_TXT = '전일취소'");}
				else if(tstat02.equals("Y") && tstat03.equals("Y") && tstat04.equals("Y")) {exwherebuf.append("  TSTAT_TXT IN ('정상거래', '당일취소', '전일취소')");}
			}
			
			qrybuf.append("SELECT ");
			qrybuf.append("	RNUM, ");
			qrybuf.append("	SEQNO,  ");
			qrybuf.append("	APPGB, ");
			qrybuf.append("	DEP_NM		TR_DEPNM,  ");
			qrybuf.append("	TERM_NM		TR_TIDNM,  ");
			qrybuf.append("	TID		TR_TID,  ");
			qrybuf.append("	MID		TR_MID, ");
			qrybuf.append("	APPDD		TR_APPDD, ");
			qrybuf.append("	APPTM		TR_APPTM, ");
			qrybuf.append("	OAPPDD		TR_OAPPDD, ");
			qrybuf.append("	APPNO		TR_APPNO,  ");
			qrybuf.append("	APPGB_TXT	TR_AUTHTXT,  ");
			qrybuf.append("	CARDNO		TR_CARDNO,	 ");
			qrybuf.append("	AMOUNT		TR_AMT,	 ");
			qrybuf.append("	HALBU		TR_HALBU,  ");
			qrybuf.append("	CARDTP_TXT	TR_CARDTP,  ");
			qrybuf.append("	TLINEGBTXT	TR_LINE, ");
			qrybuf.append("	SIGNCHK_TXT	TR_SIGN, ");
			qrybuf.append("	AUTHCD		TR_RST_CD, ");
			qrybuf.append("	DEPO_DD		DP_REQ_DD, ");
			qrybuf.append("	REQ_DD		DP_RES_DD,	 ");
			qrybuf.append("	REG_DD		DP_REG_DD, ");
			qrybuf.append("	RTN_TXT		DP_RST_TXT, ");
			qrybuf.append("	EXP_DD		DP_EXP_DD, ");
			qrybuf.append("	ADD_CID		ADD_PID, ");
			qrybuf.append("	ADD_GB		ADD_PGB, ");
			qrybuf.append("	ADD_CASHER	ADD_CID, ");
			qrybuf.append("	ADD_CD		ADD_CD, ");
			qrybuf.append("	ADD_RECP	ADD_RECP, ");
			qrybuf.append("	TRANIDX		TR_SEQNO, ");
			qrybuf.append("	AUTHMSG		TR_RST_MSG ");
			qrybuf.append("FROM( ");
			qrybuf.append("	SELECT ROWNUM AS RNUM, ");
			qrybuf.append("		SEQNO, DEP_NM, TERM_NM, TID, MID,   ");
			qrybuf.append("		APPDD, APPTM, OAPPDD, APPNO, APPGB, ");
			qrybuf.append("		CASE  ");
			qrybuf.append("			WHEN APPGB='A' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0015') ");
			setting.add(userexp[1]);
			qrybuf.append("			WHEN APPGB='C' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0016') ");
			setting.add(userexp[1]);
			qrybuf.append("		END APPGB_TXT, ");
			qrybuf.append("		CARDNO,	AMOUNT,	HALBU, ");
			qrybuf.append("		CASE  ");
			qrybuf.append("			WHEN CHECK_CARD='Y' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0019')  ");
			setting.add(userexp[1]);
			qrybuf.append("			ELSE (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0018') END CARDTP_TXT, ");
			setting.add(userexp[1]);
			qrybuf.append("		CASE ");
			qrybuf.append("			WHEN SIGNCHK='Y' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0021')  ");
			setting.add(userexp[1]);
			qrybuf.append("			ELSE (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0022') END SIGNCHK_TXT, ");
			setting.add(userexp[1]);
			qrybuf.append("		REQ_DD,	AUTHCD,	REG_DD,	RTN_CD, ");
			qrybuf.append("		CASE ");
			qrybuf.append("			WHEN RTN_CD IS NULL THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0024')  ");
			setting.add(userexp[1]);
			qrybuf.append("			WHEN RTN_CD IN('60', '67') THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0025') ");
			setting.add(userexp[1]);
			qrybuf.append("			WHEN RTN_CD IN('61', '64') THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0026')  ");
			setting.add(userexp[1]);
			qrybuf.append("		END RTN_TXT, ");
			qrybuf.append("		EXP_DD,	EXT_FIELD, TRANIDX, AUTHMSG ");
			qrybuf.append("		,CASE WHEN TLINEGB='9000' THEN '모바일' ELSE 'OFFLINE' END TLINEGBTXT ");
			qrybuf.append("		,CASE  ");
			qrybuf.append("			WHEN ADD_GB='1' OR ADD_GB='O' THEN '외래'  ");
			qrybuf.append("			WHEN ADD_GB='2' OR ADD_GB='E' THEN '응급'  ");
			qrybuf.append("			WHEN ADD_GB='3' OR ADD_GB='I' THEN '입원'  ");
			qrybuf.append("			WHEN ADD_GB='4' THEN '종합검진'  ");
			qrybuf.append("			WHEN ADD_GB='5' THEN '일반검진'  ");
			qrybuf.append("			WHEN ADD_GB='6' THEN '장례식장'  ");
			qrybuf.append("			ELSE '' ");
			qrybuf.append("		END ADD_GB ");
			qrybuf.append("		, ADD_CID, ADD_CD, ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, DEPO_DD ");
			qrybuf.append("	FROM( ");
			qrybuf.append("		SELECT ");
			qrybuf.append("			SEQNO, BIZNO, TID, MID, VANGB, MDATE, SVCGB, T1.TRANIDX, T1.APPGB, ENTRYMD, ");
			qrybuf.append("			T1.APPDD, APPTM, T1.APPNO, T1.CARDNO, HALBU, CURRENCY, T1.AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX, ");
			qrybuf.append("			ISS_CD, ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG, CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB, ");
			qrybuf.append("			SIGNCHK, DDCGB, EXT_FIELD, OAPPNO, OAPPDD, OAPPTM, OAPP_AMT, ADD_GB, ADD_CID, ADD_CD, ");
			qrybuf.append("			ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, DEP_NM, EXP_DD, REQ_DD, REG_DD, RSC_CD, RTN_CD, TERM_NM, ");
			qrybuf.append("			DEPOREQDD DEPO_DD ");
			qrybuf.append("		FROM ");
			qrybuf.append("			GLOB_MNG_ICVAN_CVS T1 ");
			qrybuf.append("		LEFT OUTER JOIN( ");
			qrybuf.append("			SELECT EXP_DD, REQ_DD, REG_DD, APP_DD, APP_NO, SALE_AMT, TRANIDX, RSC_CD, RTN_CD, CARD_NO FROM "+userexp[6]);
			qrybuf.append("		)T2 ON( ");
			qrybuf.append("			T1.APPDD=T2.APP_DD  ");
			qrybuf.append("			AND T1.APPNO=T2.APP_NO  ");
			qrybuf.append("			AND T1.AMOUNT=T2.SALE_AMT ");
			qrybuf.append("			AND T1.CARDNO=T2.CARD_NO ");
			qrybuf.append("			AND T1.APPGB=CASE ");
			qrybuf.append("				WHEN T2.RTN_CD='60' THEN 'A' ");
			qrybuf.append("				WHEN T2.RTN_CD='67' THEN 'C' ");
			qrybuf.append("			END ");
			qrybuf.append("		) ");
			qrybuf.append("		LEFT OUTER JOIN(  ");
			qrybuf.append("			SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD=? ");
			setting.add(userexp[1]);
			qrybuf.append("		)T3 ON(T1.TID=T3.TERM_ID) ");
			qrybuf.append("		LEFT OUTER JOIN(  ");
			qrybuf.append("			SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD=? ");
			setting.add(userexp[1]);
			qrybuf.append("		)T4 ON(T3.DEP_CD=T4.DEP_CD) ");
			qrybuf.append(" ");
			qrybuf.append("		LEFT OUTER JOIN( SELECT PUR_CD, MER_NO, ORG_CD  FROM TB_BAS_MERINFO)T6 ON (T1.MID = T6.MER_NO AND ORG_CD=?) ");
			setting.add(userexp[1]);
			qrybuf.append("		LEFT OUTER JOIN( SELECT PUR_NM, PUR_OCD, PUR_KOCES, PUR_CD FROM TB_BAS_PURINFO)T5 ON (T1.ACQ_CD=T5.PUR_OCD OR T1.ACQ_CD=T5.PUR_KOCES OR T6.PUR_CD =T5.PUR_CD) ");
			qrybuf.append(		wherebuf.toString());
			qrybuf.append("		order by appdd desc, apptm desc ");
			qrybuf.append("	) ");
			qrybuf.append(") ");
			if(exwherebuf != null) {
				qrybuf.append("WHERE ");
			}
			qrybuf.append(		exwherebuf.toString());
			
			//디버깅용
			utilm.debug_sql(qrybuf, setting);

			con = getOraConnect();
			stmt = con.prepareStatement(qrybuf.toString());
			for (int k = 0; k < setting.size(); k++) {
				stmt.setString((k + 1), setting.get(k));
			}

			rs = stmt.executeQuery();
			
			int icnt = 1;

			while(rs.next()) { 
				JSONObject obj1 = new JSONObject();
				JSONArray arr2 = new JSONArray();
				String cardno_dec = utilm.cardno_masking(trans_seed_manager.seed_dec_card(rs.getString("TR_CARDNO").trim()));

				arr2.add(icnt);
				for(int i = 0; i<pos_field.size(); i++) {
					//cardno change plz
					//카드번호가 있을 때만 decode -> 9 ~ 12번째 별표시
					if(pos_field.get(i).equals("TR_CARDNO")) {
						String newCardNo = utilm.cardno_masking(trans_seed_manager.seed_dec_card(rs.getString(pos_field.get(i)).trim()));
						arr2.add(newCardNo);
					} else if(pos_field.get(i).equals("TR_APPDD") || pos_field.get(i).equals("TR_OAPPDD") || pos_field.get(i).equals("DP_EXP_DD") || pos_field.get(i).equals("DP_REG_DD") || pos_field.get(i).equals("DP_REQ_DD")
							|| pos_field.get(i).equals("TR_CANDD")|| pos_field.get(i).equals("DP_RES_DD")) {
						//일자 필드일 때 YYYY/MM/DD 형태로 변경해서 출력
						//str_to_dateformat
						String tempDate = utilm.setDefault(rs.getString(pos_field.get(i)));
						String newDate = "";
						if(tempDate != null && !tempDate.equals("")) {
							newDate = utilm.str_to_dateformat(tempDate);
						}
						arr2.add(newDate);
						//
					} else if (pos_field.get(i).equals("TR_APPTM")) {
						String tempDate = utilm.setDefault(rs.getString(pos_field.get(i)));
						String newDate = "";
						if(tempDate != null && !tempDate.equals("")) {
							newDate = utilm.str_to_timeformat(tempDate);
						}
						arr2.add(newDate);
					} else {
						//null check plz
						arr2.add(utilm.setDefault(rs.getString(pos_field.get(i))));
					}
				}

				obj1.put("id", Integer.toString(icnt));
				obj1.put("data", arr2);

				objAry.add(obj1);
				
			}
			sqlobj.put("rows", objAry);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}

		return sqlobj.toJSONString();
	}
	
	public String get_json_0311total(String tuser, String reqstime, String reqetime, String stime, String etime) {
		Connection con2 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		
		JSONObject jrtnobj = new JSONObject();

		JSONArray arr = new JSONArray();
		StringBuffer pqrybuf = new StringBuffer();
		StringBuffer qrybuf = new StringBuffer();

		try {

			String[] userexp = tuser.split(":");
			//검색항목에 따른 where 조건절 setting 관련 변수
			ArrayList<String> setting = new ArrayList<>();

			pqrybuf.append(" SELECT ");
			pqrybuf.append(" 	REQDATE ,T1.DEPOSEQ, BIZNO, SDATE, EDATE, GSAMT -GSCMT AS GSAMT, GSCNT ");
			pqrybuf.append(" 	, SUAMT - SUCMT AS SUAMT, SUCNT, LAAMT - LACMT AS LAAMT, LACNT , (GSAMT-GSCMT+SUAMT-SUCMT+LAAMT-LACMT) TOTAMT ");
			pqrybuf.append(" 	, (GSCNT+SUCNT+LACNT) TOTCNT ");
			pqrybuf.append(" FROM ");
			pqrybuf.append(" 	(SELECT ");
			pqrybuf.append(" 		SUM(GSAMT) GSAMT, SUM(GSCMT) GSCMT, SUM(GSCNT) GSCNT, SUM(SUAMT) SUAMT, SUM(SUCMT) SUCMT, SUM(SUCNT) SUCNT ");
			pqrybuf.append(" 		,SUM(LAAMT) LAAMT, SUM(LACMT) LACMT, SUM(LACNT) LACNT, DEPOSEQ ,SUBSTR(DEPOSEQ,1,8) REQDATE ");
			pqrybuf.append(" 	FROM ");
			pqrybuf.append(" 		(SELECT ");
			pqrybuf.append(" 			DEPOSEQ ");
			pqrybuf.append(" 			, CASE WHEN APPGB ='A' AND TID IN ('7635015001','7635053001') THEN SUM(AMOUNT) ELSE 0 END AS GSAMT ");
			pqrybuf.append(" 			, CASE WHEN APPGB ='C' AND TID IN ('7635015001','7635053001') THEN SUM(AMOUNT) ELSE 0 END AS GSCMT ");
			pqrybuf.append(" 			, CASE WHEN TID IN ('7635015001','7635053001') THEN COUNT(1) ELSE 0 END AS GSCNT ");
			pqrybuf.append(" 			, CASE WHEN APPGB ='A' AND TID IN ('7017728001','7635054001') THEN SUM(AMOUNT) ELSE 0 END AS SUAMT ");
			pqrybuf.append(" 			, CASE WHEN APPGB ='C' AND TID IN ('7017728001','7635054001') THEN SUM(AMOUNT) ELSE 0 END AS SUCMT ");
			pqrybuf.append(" 			, CASE WHEN TID IN ('7017728001','7635054001') THEN COUNT(1) ELSE 0 END AS SUCNT ");
			pqrybuf.append(" 			, CASE WHEN APPGB ='A' AND TID IN ('7642904001','7635055001') THEN SUM(AMOUNT) ELSE 0 END AS LAAMT ");
			pqrybuf.append(" 			, CASE WHEN APPGB ='C' AND TID IN ('7642904001','7635055001') THEN SUM(AMOUNT) ELSE 0 END AS LACMT ");
			pqrybuf.append(" 			, CASE WHEN TID IN ('7642904001','7635055001') THEN COUNT(1) ELSE 0 END AS LACNT       ");
			pqrybuf.append(" 		FROM GLOB_MNG_ICVAN_CVS ");
			pqrybuf.append(" 		GROUP BY APPGB, DEPOSEQ, TID ");
			pqrybuf.append(" 		)GROUP BY DEPOSEQ ");
			pqrybuf.append(" 	)T1  ");
			pqrybuf.append(" LEFT OUTER JOIN (SELECT BIZNO, SDATE , EDATE, DEPOSEQ,ORGCD FROM TB_HIS_DPREQ_TOT )T2 ON (T1.DEPOSEQ = T2.DEPOSEQ) ");
			pqrybuf.append(" WHERE ORGCD = ?");
			setting.add(userexp[1]);

			if(null!=reqstime&&""!=reqstime) {
				pqrybuf.append("           AND REQDATE>= ? ");
				setting.add(reqstime);
			}

			if(null!=reqetime&&""!=reqetime) {
				pqrybuf.append("            AND REQDATE<=? ");
				setting.add(reqetime);
			}
			
			if(null!=stime&&""!=stime) {
				pqrybuf.append("            AND SDATE>=? ");
				setting.add(stime);
			}
			
			if(null!=etime&&""!=etime) {
				pqrybuf.append("            AND EDATE<=? ");
				setting.add(etime);
			}
			
			pqrybuf.append(" ORDER BY REQDATE DESC ");
			
			//디버깅용
			utilm.debug_sql(pqrybuf, setting);

			con2 = getOraConnect();
			stmt2 = con2.prepareStatement(pqrybuf.toString());
			for(int k = 0; k<setting.size(); k++) {
				stmt2.setString((k+1), setting.get(k));
			}
			rs2 = stmt2.executeQuery();


			int icnt = 1;
			int GSCNT=0, SUCNT=0, LACNT=0, TOTCNT=0;
			long GSAMT=0, SUAMT=0, LAAMT=0, TOTAMT=0;
			
			while(rs2.next()) {
				JSONObject obj1 = new JSONObject();
				JSONArray arr2 = new JSONArray();

				GSCNT	+= Integer.parseInt(rs2.getString("GSCNT"));
				GSAMT	+= Integer.parseInt(rs2.getString("GSAMT"));
				SUCNT	+= Integer.parseInt(rs2.getString("SUCNT"));
				SUAMT	+= Integer.parseInt(rs2.getString("SUAMT"));
				LACNT	+= Integer.parseInt(rs2.getString("LACNT"));
				LAAMT	+= Integer.parseInt(rs2.getString("LAAMT"));
				TOTCNT	+= Integer.parseInt(rs2.getString("TOTCNT"));
				TOTAMT	+= Integer.parseInt(rs2.getString("TOTAMT"));
				
				arr2.add(icnt);
				arr2.add(utilm.str_to_dateformat_deposit(utilm.setDefault(rs2.getString("REQDATE"))));
				arr2.add(utilm.setDefault(rs2.getString("BIZNO")));
				arr2.add(utilm.str_to_dateformat_deposit(utilm.setDefault(rs2.getString("EDATE"))));
				arr2.add(utilm.str_to_dateformat_deposit(utilm.setDefault(rs2.getString("SDATE"))));
				arr2.add(utilm.setDefault(rs2.getString("GSCNT")));
				arr2.add(utilm.setDefault(rs2.getString("GSAMT")));
				arr2.add(utilm.setDefault(rs2.getString("SUCNT")));
				arr2.add(utilm.setDefault(rs2.getString("SUAMT")));
				arr2.add(utilm.setDefault(rs2.getString("LACNT")));
				arr2.add(utilm.setDefault(rs2.getString("LAAMT")));
				arr2.add(utilm.setDefault(rs2.getString("TOTCNT")));
				arr2.add(utilm.setDefault(rs2.getString("TOTAMT")));
				
				obj1.put("id", Integer.toString(icnt));
				obj1.put("data", arr2);

				arr.add(obj1);
				icnt++;
			}
			
			//합계 계산
			JSONObject obj1 = new JSONObject();
			JSONArray arr2 = new JSONArray();

			arr2.add("합계");
			arr2.add("");
			arr2.add("");
			arr2.add("");
			arr2.add("");
			arr2.add(GSCNT);
			arr2.add(GSAMT);
			arr2.add(SUCNT);
			arr2.add(SUAMT);
			arr2.add(LACNT);
			arr2.add(LAAMT);
			arr2.add(TOTCNT);
			arr2.add(TOTAMT);

			obj1.put("id", "total");
			obj1.put("data", arr2);

			arr.add(obj1);

			jrtnobj.put("rows", arr);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con2,stmt2,rs2);
		}
		return jrtnobj.toJSONString();
	}

	public String get_json_0312total(String tuser, String stime, String etime, String reqstime, String reqetime, String tid) {
		Connection con2 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs2 = null;
		
		JSONObject jrtnobj = new JSONObject();

		JSONArray arr = new JSONArray();
		StringBuffer pqrybuf = new StringBuffer();
		StringBuffer qrybuf = new StringBuffer();

		try {

			String[] userexp = tuser.split(":");
			//검색항목에 따른 where 조건절 setting 관련 변수
			ArrayList<String> setting = new ArrayList<>();

			pqrybuf.append("SELECT ");
			pqrybuf.append("	APPDD , SUM(KBAA) AS KBAA , SUM(KBAC) AS  KBAC ");
			pqrybuf.append("	, SUM(NHAA) AS NHAA, SUM(NHAC) AS  NHAC ");
			pqrybuf.append("	, SUM(LDAA) AS LDAA, SUM(LDAC) AS  LDAC ");
			pqrybuf.append("	, SUM(HDAA) AS HDAA , SUM(HDAC) AS  HDAC ");
			pqrybuf.append("	, SUM(SHAA) AS SHAA , SUM(SHAC) AS  SHAC ");
			pqrybuf.append("	, SUM(SSAA) AS SSAA , SUM(SSAC) AS  SSAC ");
			pqrybuf.append("	, SUM(HNAA) AS HNAA , SUM(HNAC) AS  HNAC ");
			pqrybuf.append("	, SUM(BCAA) AS BCAA , SUM(BCAC) AS  BCAC ");
			pqrybuf.append("	, SUM(KBCA) AS KBCA , SUM(KBCC) AS  KBCC ");
			pqrybuf.append("	, SUM(NHCA) AS NHCA, SUM(NHCC) AS  NHCC ");
			pqrybuf.append("	, SUM(LDCA) AS LDCA, SUM(LDCC) AS  LDCC ");
			pqrybuf.append("	, SUM(HDCA) AS HDCA , SUM(HDCC) AS  HDCC ");
			pqrybuf.append("	, SUM(SHCA) AS SHCA , SUM(SHCC) AS  SHCC ");
			pqrybuf.append("	, SUM(SSCA) AS SSCA , SUM(SSCC) AS  SSCC ");
			pqrybuf.append("	, SUM(HNCA) AS HNCA , SUM(HNCC) AS  HNCC ");
			pqrybuf.append("	, SUM(BCCA) AS BCCA , SUM(BCCC) AS  BCCC ");
			pqrybuf.append("FROM ( ");
			pqrybuf.append("	SELECT ");
			pqrybuf.append("		APPDD, NVL(SUM(DECODE(PUR_CD , 0170, AATM )),0) KBAA ");
			pqrybuf.append("		, NVL(SUM(DECODE(PUR_CD , 0170, ACNT )),0) KBAC ");
			pqrybuf.append("		, NVL(SUM(DECODE(PUR_CD , 0171, AATM )),0) NHAA, NVL(SUM(DECODE(PUR_CD , 0171, ACNT )),0) NHAC ");
			pqrybuf.append("		, NVL(SUM(DECODE(PUR_CD , 1100, AATM )),0) LDAA, NVL(SUM(DECODE(PUR_CD , 1100, ACNT )),0) LDAC ");
			pqrybuf.append("		, NVL(SUM(DECODE(PUR_CD , 1200, AATM )),0) HDAA, NVL(SUM(DECODE(PUR_CD , 1200, ACNT )),0) HDAC ");
			pqrybuf.append("		, NVL(SUM(DECODE(PUR_CD , 0300, AATM )),0) SHAA, NVL(SUM(DECODE(PUR_CD , 0300, ACNT )),0) SHAC ");
			pqrybuf.append("		, NVL(SUM(DECODE(PUR_CD , 1300, AATM )),0) SSAA, NVL(SUM(DECODE(PUR_CD , 1300, ACNT )),0) SSAC ");
			pqrybuf.append("		, NVL(SUM(DECODE(PUR_CD , 0505, AATM )),0) HNAA, NVL(SUM(DECODE(PUR_CD , 0505, ACNT )),0) HNAC ");
			pqrybuf.append("		, NVL(SUM(DECODE(PUR_CD , 0400, AATM )),0) BCAA, NVL(SUM(DECODE(PUR_CD , 0400, ACNT )),0) BCAC ");
			pqrybuf.append("		, NVL(SUM(DECODE(PUR_CD , 0170, CATM )),0) KBCA, NVL(SUM(DECODE(PUR_CD , 0170, CCNT )),0) KBCC ");
			pqrybuf.append("		, NVL(SUM(DECODE(PUR_CD , 0171, CATM )),0) NHCA, NVL(SUM(DECODE(PUR_CD , 0171, CCNT )),0) NHCC ");
			pqrybuf.append("		, NVL(SUM(DECODE(PUR_CD , 1100, CATM )),0) LDCA, NVL(SUM(DECODE(PUR_CD , 1100, CCNT )),0) LDCC ");
			pqrybuf.append("		, NVL(SUM(DECODE(PUR_CD , 1200, CATM )),0) HDCA, NVL(SUM(DECODE(PUR_CD , 1200, CCNT )),0) HDCC ");
			pqrybuf.append("		, NVL(SUM(DECODE(PUR_CD , 0300, CATM )),0) SHCA, NVL(SUM(DECODE(PUR_CD , 0300, CCNT )),0) SHCC ");
			pqrybuf.append("		, NVL(SUM(DECODE(PUR_CD , 1300, CATM )),0) SSCA, NVL(SUM(DECODE(PUR_CD , 1300, CCNT )),0) SSCC ");
			pqrybuf.append("		, NVL(SUM(DECODE(PUR_CD , 0505, CATM )),0) HNCA, NVL(SUM(DECODE(PUR_CD , 0505, CCNT )),0) HNCC ");
			pqrybuf.append("		, NVL(SUM(DECODE(PUR_CD , 0400, CATM )),0) BCCA, NVL(SUM(DECODE(PUR_CD , 0400, CCNT )),0) BCCC ");
			pqrybuf.append("	FROM ( ");
			pqrybuf.append("		SELECT ");
			pqrybuf.append("			APPDD ,  CASE WHEN APPGB ='A' THEN COUNT(1) ELSE 0 END AS ACNT ");
			pqrybuf.append("			, CASE WHEN APPGB ='A' THEN SUM(AMOUNT) ELSE 0 END  AATM ");
			pqrybuf.append("			,  CASE WHEN APPGB ='C' THEN COUNT(1) ELSE 0 END AS CCNT , CASE WHEN APPGB ='C' THEN SUM(AMOUNT) ELSE 0 END  CATM ");
			pqrybuf.append("			, PUR_CD ");
			pqrybuf.append("		FROM ( ");
			pqrybuf.append("			SELECT * FROM GLOB_MNG_ICVAN_CVS ");

			if(null!=stime&&""!=stime) {
				pqrybuf.append("            WHERE APPDD>=? ");
				setting.add(stime);
			}
			
			if(null!=etime&&""!=etime) {
				pqrybuf.append("            AND APPDD<=? ");
				setting.add(etime);
			}
			
			if(null!=reqstime&&""!=reqstime) {
				pqrybuf.append("           AND REQDATE>= ? ");
				setting.add(reqstime);
			}

			if(null!=reqetime&&""!=reqetime) {
				pqrybuf.append("            AND REQDATE<=? ");
				setting.add(reqetime);
			}

			if(null!=tid&&""!=tid){
		        String[] tidexp = tid.split(",");
		        pqrybuf.append("TID IN (?, ?)");
		        setting.add(tidexp[0]);
		        setting.add(tidexp[1]);
			}
			
			pqrybuf.append("		)T1 ");
			pqrybuf.append("		LEFT OUTER JOIN (SELECT PUR_CD , MER_NO FROM TB_BAS_MERINFO)T2 ON(T1.MID = T2.MER_NO) ");
			pqrybuf.append("		GROUP BY APPDD , APPGB  ,PUR_CD ");
			pqrybuf.append("	     )GROUP BY APPDD ,PUR_CD ");
			pqrybuf.append("	)GROUP BY APPDD ORDER BY APPDD DESC ");
			
			//디버깅용
			utilm.debug_sql(pqrybuf, setting);

			con2 = getOraConnect();
			stmt2 = con2.prepareStatement(pqrybuf.toString());
			for(int k = 0; k<setting.size(); k++) {
				stmt2.setString((k+1), setting.get(k));
			}
			rs2 = stmt2.executeQuery();


			int icnt = 1;
			long TOTKBAA = 0, TOTNHAA = 0, TOTBCAA = 0, TOTSSAA = 0, TOTSHAA = 0, TOTLDAA = 0, TOTHNAA = 0, TOTHDAA = 0;
			int TOTKBAC = 0, TOTNHAC = 0, TOTBCAC = 0, TOTSSAC = 0, TOTSHAC = 0, TOTLDAC = 0, TOTHNAC = 0, TOTHDAC = 0;
			long TOTKBCA = 0, TOTNHCA = 0, TOTBCCA = 0, TOTSSCA = 0, TOTSHCA = 0, TOTLDCA = 0, TOTHNCA = 0, TOTHDCA = 0;
			int TOTKBCC = 0, TOTNHCC = 0, TOTBCCC = 0, TOTSSCC = 0, TOTSHCC = 0, TOTLDCC = 0, TOTHNCC = 0, TOTHDCC = 0;
			long AMT=0, CNT=0, TOTAMT=0, TOTCNT=0;
			while(rs2.next()) {
				JSONObject obj1 = new JSONObject();
				JSONArray arr2 = new JSONArray();

				TOTKBAA		+= Integer.parseInt(rs2.getString("KBAA"));
				TOTKBAC		+= Integer.parseInt(rs2.getString("KBAC"));
				TOTNHAA		+= Integer.parseInt(rs2.getString("NHAA"));
				TOTNHAC		+= Integer.parseInt(rs2.getString("NHAC"));
				TOTBCAA		+= Integer.parseInt(rs2.getString("BCAA"));
				TOTBCAC		+= Integer.parseInt(rs2.getString("BCAC"));
				TOTSSAA		+= Integer.parseInt(rs2.getString("SSAA"));
				TOTSSAC		+= Integer.parseInt(rs2.getString("SSAC"));
				TOTSHAA		+= Integer.parseInt(rs2.getString("SHAA"));
				TOTSHAC		+= Integer.parseInt(rs2.getString("SHAC"));
				TOTLDAA		+= Integer.parseInt(rs2.getString("LDAA"));
				TOTLDAC		+= Integer.parseInt(rs2.getString("LDAC"));
				TOTHNAA		+= Integer.parseInt(rs2.getString("HNAA"));
				TOTHNAC		+= Integer.parseInt(rs2.getString("HNAC"));
				TOTHDAA		+= Integer.parseInt(rs2.getString("HDAA"));
				TOTHDAC		+= Integer.parseInt(rs2.getString("HDAC"));
				TOTKBCA		+= Integer.parseInt(rs2.getString("KBCA"));
				TOTKBCC		+= Integer.parseInt(rs2.getString("KBCC"));
				TOTNHCA		+= Integer.parseInt(rs2.getString("NHCA"));
				TOTNHCC		+= Integer.parseInt(rs2.getString("NHCC"));
				TOTBCCA		+= Integer.parseInt(rs2.getString("BCCA"));
				TOTBCCC		+= Integer.parseInt(rs2.getString("BCCC"));
				TOTSSCA		+= Integer.parseInt(rs2.getString("SSCA"));
				TOTSSCC		+= Integer.parseInt(rs2.getString("SSCC"));
				TOTSHCA		+= Integer.parseInt(rs2.getString("SHCA"));
				TOTSHCC		+= Integer.parseInt(rs2.getString("SHCC"));
				TOTLDCA		+= Integer.parseInt(rs2.getString("LDCA"));
				TOTLDCC		+= Integer.parseInt(rs2.getString("LDCC"));
				TOTHNCA		+= Integer.parseInt(rs2.getString("HNCA"));
				TOTHNCC		+= Integer.parseInt(rs2.getString("HNCC"));
				TOTHDCA		+= Integer.parseInt(rs2.getString("HDCA"));
				TOTHDCC		+= Integer.parseInt(rs2.getString("HDCC"));
				
				AMT = Integer.parseInt(rs2.getString("KBAA"))+Integer.parseInt(rs2.getString("NHAA"))+Integer.parseInt(rs2.getString("BCAA"));
				AMT += +Integer.parseInt(rs2.getString("SSAA"))+Integer.parseInt(rs2.getString("SHAA"))+Integer.parseInt(rs2.getString("LDAA"));
				AMT += +Integer.parseInt(rs2.getString("HNAA"))+Integer.parseInt(rs2.getString("HDAA"));
				AMT += -Integer.parseInt(rs2.getString("KBAC"))-Integer.parseInt(rs2.getString("NHAC"))-Integer.parseInt(rs2.getString("BCAC"));
				AMT += -Integer.parseInt(rs2.getString("SSAC"))-Integer.parseInt(rs2.getString("SHAC"))-Integer.parseInt(rs2.getString("LDAC"));
				AMT += -Integer.parseInt(rs2.getString("HNAC"))-Integer.parseInt(rs2.getString("HDAC"));

				CNT = Integer.parseInt(rs2.getString("KBCA"))+Integer.parseInt(rs2.getString("NHCA"))+Integer.parseInt(rs2.getString("BCCA"));
				CNT += +Integer.parseInt(rs2.getString("SSCA"))+Integer.parseInt(rs2.getString("SHCA"))+Integer.parseInt(rs2.getString("LDCA"));
				CNT += +Integer.parseInt(rs2.getString("HNCA"))+Integer.parseInt(rs2.getString("HDCA"));
				CNT += +Integer.parseInt(rs2.getString("KBCC"))+Integer.parseInt(rs2.getString("NHCC"))+Integer.parseInt(rs2.getString("BCCC"));
				CNT += +Integer.parseInt(rs2.getString("SSCC"))+Integer.parseInt(rs2.getString("SHCC"))+Integer.parseInt(rs2.getString("LDCC"));
				CNT += +Integer.parseInt(rs2.getString("HNCC"))+Integer.parseInt(rs2.getString("HDCC"));
				
				TOTAMT += AMT;
				TOTCNT += CNT;
				
				arr2.add(icnt);
				arr2.add(utilm.str_to_dateformat_deposit(utilm.setDefault(rs2.getString("APPDD"))));
				arr2.add(utilm.setDefault(rs2.getString("KBAC")));
				arr2.add(utilm.setDefault(rs2.getString("KBAA")));
				arr2.add(utilm.setDefault(rs2.getString("KBCC")));
				arr2.add(utilm.setDefault(rs2.getString("KBCA")));
				arr2.add(utilm.setDefault(rs2.getString("NHAC")));
				arr2.add(utilm.setDefault(rs2.getString("NHAA")));
				arr2.add(utilm.setDefault(rs2.getString("NHCC")));
				arr2.add(utilm.setDefault(rs2.getString("NHCA")));
				arr2.add(utilm.setDefault(rs2.getString("LDAC")));
				arr2.add(utilm.setDefault(rs2.getString("LDAA")));
				arr2.add(utilm.setDefault(rs2.getString("LDCC")));
				arr2.add(utilm.setDefault(rs2.getString("LDCA")));
				arr2.add(utilm.setDefault(rs2.getString("BCAC")));
				arr2.add(utilm.setDefault(rs2.getString("BCAA")));
				arr2.add(utilm.setDefault(rs2.getString("BCCC")));
				arr2.add(utilm.setDefault(rs2.getString("BCCA")));
				arr2.add(utilm.setDefault(rs2.getString("SSAC")));
				arr2.add(utilm.setDefault(rs2.getString("SSAA")));
				arr2.add(utilm.setDefault(rs2.getString("SSCC")));
				arr2.add(utilm.setDefault(rs2.getString("SSCA")));
				arr2.add(utilm.setDefault(rs2.getString("SHAC")));
				arr2.add(utilm.setDefault(rs2.getString("SHAA")));
				arr2.add(utilm.setDefault(rs2.getString("SHCC")));
				arr2.add(utilm.setDefault(rs2.getString("SHCA")));
				arr2.add(utilm.setDefault(rs2.getString("HNAC")));
				arr2.add(utilm.setDefault(rs2.getString("HNAA")));
				arr2.add(utilm.setDefault(rs2.getString("HNCC")));
				arr2.add(utilm.setDefault(rs2.getString("HNCA")));
				arr2.add(utilm.setDefault(rs2.getString("HDAC")));
				arr2.add(utilm.setDefault(rs2.getString("HDAA")));
				arr2.add(utilm.setDefault(rs2.getString("HDCC")));
				arr2.add(utilm.setDefault(rs2.getString("HDCA")));
				arr2.add(CNT);
				arr2.add(AMT);
				
				obj1.put("id", Integer.toString(icnt));
				obj1.put("data", arr2);

				arr.add(obj1);
				icnt++;
			}
			
			//합계 계산
			JSONObject obj1 = new JSONObject();
			JSONArray arr2 = new JSONArray();

			arr2.add("합계");
			arr2.add("");
			arr2.add(TOTKBAC);
			arr2.add(TOTKBAA);
			arr2.add(TOTKBCC);
			arr2.add(TOTKBCA);
			arr2.add(TOTNHAC);
			arr2.add(TOTNHAA);
			arr2.add(TOTNHCC);
			arr2.add(TOTNHCA);
			arr2.add(TOTLDAC);
			arr2.add(TOTLDAA);
			arr2.add(TOTLDCC);
			arr2.add(TOTLDCA);
			arr2.add(TOTBCAC);
			arr2.add(TOTBCAA);
			arr2.add(TOTBCCC);
			arr2.add(TOTBCCA);
			arr2.add(TOTSSAC);
			arr2.add(TOTSSAA);
			arr2.add(TOTSSCC);
			arr2.add(TOTSSCA);
			arr2.add(TOTSHAC);
			arr2.add(TOTSHAA);
			arr2.add(TOTSHCC);
			arr2.add(TOTSHCA);
			arr2.add(TOTHNAC);
			arr2.add(TOTHNAA);
			arr2.add(TOTHNCC);
			arr2.add(TOTHNCA);
			arr2.add(TOTHDAC);
			arr2.add(TOTHDAA);
			arr2.add(TOTHDCC);
			arr2.add(TOTHDCA);
			arr2.add(TOTCNT);
			arr2.add(TOTAMT);

			obj1.put("id", "total");
			obj1.put("data", arr2);

			arr.add(obj1);

			jrtnobj.put("rows", arr);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con2,stmt2,rs2);
		}
		return jrtnobj.toJSONString();
	}
	
	//2021.03.22 계좌입금원장 :: bankinfo 정보 가져오기
		public ArrayList<String[]> get_excelup_bankInfoData(String tuser) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			StringBuffer qrybuf = new StringBuffer();
			ArrayList<String[]> bankInfo = new ArrayList<>();
			
			try {
				//tuser split
				String[] userexp = tuser.split(":");
					
				qrybuf.append("SELECT ACC_TXT, MID, ACQCD FROM TB_BAS_BANKINFO WHERE ORG_CD = ?  AND DEP_CD = ? ");
				
				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				stmt.setString(1, userexp[1]);
				stmt.setString(2, userexp[2]);
				
				rs = stmt.executeQuery();
				
				while(rs.next()) {
					String[] temp = new String[3];
					//acc_txt, mid, acqcd 순으로
					temp[0] = utilm.setDefault(rs.getString("ACC_TXT"));
					temp[1] = utilm.setDefault(rs.getString("MID"));
					temp[2] = utilm.setDefault(rs.getString("ACQCD"));
					
					bankInfo.add(temp);
				}
				
			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}
			return bankInfo;
		}
		
		//2021.03.22 excelUp -> TB_MNG_BANKDATA DATA INSERT
		public void excelup_insertBankData(String tuser, String[] data){
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			StringBuffer qrybuf = new StringBuffer();
			
			try {
				//tuser split
				String[] userexp = tuser.split(":");
				
				qrybuf.append("INSERT INTO TB_MNG_BANKDATA ");
				qrybuf.append("(ORG_CD, DEP_CD, EXP_DD, EXP_AMT, ACC_TXT, ACQ_CD, MID)");
				qrybuf.append(" VALUES ( ?, ?, ?, ?, ?, ?, ?) ");
			
				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				
				stmt.setString(1, userexp[1]);
				stmt.setString(2, userexp[2]);
				stmt.setString(3, data[0]);
				stmt.setString(4, data[1]);
				stmt.setString(5, data[2]);
				stmt.setString(6, data[3]);
				stmt.setString(7, data[4]);
				
				stmt.executeUpdate();
				
			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}
			
		}
		
		//cvs 반송입금조회
				public String get_json_0301total_ban(String tuser, String stime, String etime, String acqcd, String depcd,
						String mid) {
					Connection con = null;
					PreparedStatement stmt = null;
					ResultSet rs = null;
					
					StringBuffer qrybuf = new StringBuffer();
					StringBuffer wherebuf = new StringBuffer();

					JSONObject sqlobj = new JSONObject();
					JSONArray objAry = new JSONArray();

					try {
						//tuser, stime, etime, acqcd, depcd, mid
						//tuser split
						String[] userexp = tuser.split(":");
						//acqcd split
						String[] acqcdexp = acqcd.split(",");
						//검색항목에 따른 where 조건절 setting 관련 변수
						ArrayList<String> setting = new ArrayList<>();

						wherebuf.append(" WHERE MID IN (SELECT MID FROM TB_BAS_MIDMAP WHERE ORG_CD = ? ) ");
						setting.add(userexp[1]);
						
						if(!stime.equals("") && stime != null) {
							wherebuf.append(" AND EXP_DD >= ? ");
							setting.add(stime);
						}

						if(!etime.equals("") && etime != null) {
							wherebuf.append(" AND EXP_DD <= ? ");
							setting.add(etime);
						}
						
						if(!mid.equals("") && mid != null) {
							wherebuf.append(" AND MID = ? ");
							setting.add(mid);
						}
						
						if(!depcd.equals("") && depcd != null) {
							if(depcd=="1") {
								wherebuf.append(" AND MID IN ('768017318','00052904921','00951457027','128890479','57296808','151098345','9052663887','151558364','721176212','00098153744','154944840','00903164052','0118721620','179102374','178597603','9956970402' ) ");
							}else if(depcd=="2") {
								wherebuf.append(" AND MID IN ('704855398','00084542316','00986653087','165138860','0104783451','860295101','9969229911','140239694','721219360','00098234952','155068491','00903276708','0118796648','179216357','178600027','9957975427' ) ");
							}else if(depcd=="3") {
								wherebuf.append(" AND MID IN ('707528764','00087259990','00989439518','167802984','0107608507','860386610','9967457077','143275451','721225822','00098235865','155072408','00903280940','0118799154','179216254','178600545','9957971095' )");
							}
						}
						
						if(!acqcd.equals("") && acqcd != null) {
							wherebuf.append(" AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD=? AND ORG_CD=? ) ");
							setting.add(acqcd);
							setting.add(userexp[1]);
						}

						qrybuf.append(" SELECT ");
						qrybuf.append(" 	SUM(T_CNT) TCNT ");
						qrybuf.append(" 	, SUM(T_BAN) BCNT ");
						qrybuf.append(" 	, SUM(T_AMT) TAMT ");
						qrybuf.append(" 	, SUM(T_FEE) INAMT ");
						qrybuf.append(" 	, SUM(T_EXP) EXAMT ");
						qrybuf.append(" 	, SUM(I_CNT) ITEMCNT ");
						qrybuf.append(" 	, SUM(I_BAN) ITEMBAN ");
						qrybuf.append(" 	, SUM(I_AMT) ITEMAMT ");
						qrybuf.append(" 	, SUM(I_FEE) ITEMFEE ");
						qrybuf.append(" 	, SUM(I_EXP) ITEMICOM ");
						qrybuf.append(" 	, SUM(BANK_AMT) BANKAMT ");
						qrybuf.append(" 	, SUM(DIFF_ICOM) DIFFICOM ");
						qrybuf.append(" 	, SUM(DIFF_BANK) DIFFBANK ");
						qrybuf.append(" FROM( ");
						qrybuf.append(" 	SELECT ");
						qrybuf.append(" 		T_CNT, T_BAN, T_AMT, T_FEE, T_EXP ");
						qrybuf.append(" 		, I_CNT, I_BAN, I_AMT, I_FEE, I_EXP ");
						qrybuf.append(" 		, BANK_AMT, (T_EXP-I_EXP) DIFF_ICOM, (I_EXP-BANK_AMT) DIFF_BANK ");
						qrybuf.append(" 	FROM( ");
						qrybuf.append(" 		SELECT ");
						qrybuf.append(" 			MID, EXP_DD, SUM(TOT_CNT) T_CNT, SUM(TOT_BAN) T_BAN, SUM(TOT_NETAMT) T_AMT ");
						qrybuf.append(" 			, SUM(TOT_INPAMT) T_FEE, SUM(TOT_EXPAMT) T_EXP, SUM(I_CNT) I_CNT, SUM(I_BAN) I_BAN ");
						qrybuf.append(" 			, SUM(I_AMT) I_AMT, SUM(I_FEE) I_FEE, SUM(I_EXP) I_EXP ");
						qrybuf.append(" 		FROM( ");
						qrybuf.append(" 			SELECT ");
						qrybuf.append(" 				MID, EXP_DD, DEP_SEQ,SUM(TOT_CNT) TOT_CNT ,SUM(BAN_CNT) TOT_BAN,(SUM(EXP_AMT)+SUM(INP_AMT)) TOT_NETAMT ");
						qrybuf.append(" 				,SUM(INP_AMT) TOT_INPAMT, SUM(EXP_AMT) TOT_EXPAMT ");
						qrybuf.append(" 			FROM ");
						qrybuf.append(" 				TB_MNG_DEPTOT  ");
						qrybuf.append(wherebuf.toString());
						qrybuf.append(" 			GROUP BY MID, EXP_DD, DEP_SEQ ");
						qrybuf.append(" 			ORDER BY EXP_DD DESC ");
						qrybuf.append(" 		)T1 ");
						qrybuf.append(" 	LEFT OUTER JOIN( ");
						qrybuf.append(" 		SELECT ");
						qrybuf.append(" 			DEP_SEQ ");
						qrybuf.append(" 			, (SUM(ITEM_CNT60)+SUM(ITEM_CNT67)) I_CNT ");
						qrybuf.append(" 			, SUM(ITEM_CNTBAN) I_BAN ");
						qrybuf.append(" 			, (SUM(ITEM_AMT60)-SUM(ITEM_AMT67)) I_AMT ");
						qrybuf.append(" 			, (SUM(ITEM_FEE60)-SUM(ITEM_FEE67)) I_FEE ");
						qrybuf.append(" 			, (SUM(ITEM_AMT60)-SUM(ITEM_AMT67))-(SUM(ITEM_FEE60)-SUM(ITEM_FEE67)) I_EXP ");
						qrybuf.append(" 		FROM( ");
						qrybuf.append(" 			SELECT  ");
						qrybuf.append(" 				DEP_SEQ   ");
						qrybuf.append(" 				,CASE WHEN RTN_CD='60' THEN COUNT(1) ELSE 0 END ITEM_CNT60 ");
						qrybuf.append(" 				,CASE WHEN RTN_CD='67' THEN COUNT(1) ELSE 0 END ITEM_CNT67 ");
						qrybuf.append(" 				,CASE WHEN RTN_CD NOT IN ('60', '67') THEN COUNT(1) ELSE 0 END ITEM_CNTBAN ");
						qrybuf.append(" 				,CASE WHEN RTN_CD='61' THEN SUM(SALE_AMT) ELSE 0 END ITEM_AMT60 ");
						qrybuf.append(" 				,CASE WHEN RTN_CD='64' THEN SUM(SALE_AMT) ELSE 0 END ITEM_AMT67 ");
						qrybuf.append(" 				,CASE WHEN RTN_CD='61' THEN SUM(FEE) ELSE 0 END ITEM_FEE60 ");
						qrybuf.append(" 				,CASE WHEN RTN_CD='64' THEN SUM(FEE) ELSE 0 END ITEM_FEE67 ");
						qrybuf.append(" 			FROM TB_MNG_DEPDATA ");
						qrybuf.append(wherebuf.toString());
						qrybuf.append(" 			GROUP BY DEP_SEQ, RTN_CD ");
						qrybuf.append(" 		   ) ");
						qrybuf.append(" 		GROUP BY DEP_SEQ ");
						qrybuf.append(" 	)T2 ON(T1.DEP_SEQ=T2.DEP_SEQ) ");
						qrybuf.append(" 	GROUP BY MID, EXP_DD ");
						qrybuf.append(" )T1 ");
						
						//조건절 두번 들어가는 부분
						ArrayList<String> preSet = new ArrayList<>();
						int tempNum = 0;
						for(int j = 0; j < 2; j++) {
							tempNum = setting.size() * j;
							for(int i = 0; i<setting.size(); i++) {
								preSet.add(setting.get(i));
							}
						}
						
						qrybuf.append(" LEFT OUTER JOIN( SELECT EXP_DD, MID, CASE WHEN SUM(EXP_AMT) IS NULL THEN 0 ELSE SUM(EXP_AMT) END BANK_AMT FROM TB_MNG_BANKDATA  ");
						qrybuf.append(" WHERE MID IS NOT NULL AND NOT REGEXP_LIKE( exp_amt,'[A-Za-z]|[가-힛]|') AND exp_amt != '' ");
						qrybuf.append(" GROUP BY EXP_DD, MID)T2 ON(T1.MID=T2.MID AND T1.EXP_DD=T2.EXP_DD)  ");
						qrybuf.append(" LEFT OUTER JOIN( SELECT ORG_CD, DEP_CD, MER_NO, PUR_CD FROM TB_BAS_MERINFO WHERE ORG_CD=?)T3 ON(T1.MID=T3.MER_NO) ");
						preSet.add(userexp[1]);
						qrybuf.append(" LEFT OUTER JOIN( SELECT ORG_CD, ORG_NM FROM TB_BAS_ORG)T4 ON(T3.ORG_CD=T4.ORG_CD) ");
						qrybuf.append(" LEFT OUTER JOIN( SELECT DEP_CD, DEP_NM FROM TB_BAS_DEPART WHERE ORG_CD=?)T5 ON(T3.DEP_CD=T5.DEP_CD) ");
						preSet.add(userexp[1]);
						qrybuf.append(" LEFT OUTER JOIN( SELECT PUR_CD, PUR_NM, PUR_KOCES FROM TB_BAS_PURINFO)T6 ON(T3.PUR_CD=T6.PUR_CD) ");
						qrybuf.append(" ) ");

						//디버깅용
						utilm.debug_sql(qrybuf, preSet);

						con = getOraConnect();
						stmt = con.prepareStatement(qrybuf.toString());
						for (int k = 0; k < preSet.size(); k++) {
							stmt.setString((k + 1), preSet.get(k));
						}
						
						rs = stmt.executeQuery();

						//TCNT, BCNT, TAMT, INAMT, EXAMT, ITEMCNT, ITEMBAN, ITEMAMT, ITEMFEE, ITEMICOM
						//정상건수, 반송건수, 매출금액, 수수료, 입금액합계, 정상건수, 반송건수, 매출금액, 수수료, 입금액합계
						//BANKAMT 실통장금액, DIFFICOM 입금차액, DIFFBANK 통장차액
						while(rs.next()) {
							JSONObject tempObj = new JSONObject();
							JSONArray tempAry = new JSONArray();

							tempAry.add(utilm.checkNumberData(rs.getString("TCNT")));
							tempAry.add(utilm.checkNumberData(rs.getString("BCNT")));
							tempAry.add(utilm.checkNumberData(rs.getString("TAMT")));
							tempAry.add(utilm.checkNumberData(rs.getString("INAMT")));
							tempAry.add(utilm.checkNumberData(rs.getString("EXAMT")));
							tempAry.add(utilm.checkNumberData(rs.getString("ITEMCNT")));
							tempAry.add(utilm.checkNumberData(rs.getString("ITEMBAN")));
							tempAry.add(utilm.checkNumberData(rs.getString("ITEMAMT")));
							tempAry.add(utilm.checkNumberData(rs.getString("ITEMFEE")));
							tempAry.add(utilm.checkNumberData(rs.getString("ITEMICOM")));				
							tempAry.add(utilm.checkNumberData(rs.getString("BANKAMT")));
							tempAry.add(utilm.checkNumberData(rs.getString("DIFFICOM")));
							tempAry.add(utilm.checkNumberData(rs.getString("DIFFBANK")));

							tempObj.put("id", "total");
							tempObj.put("data", tempAry);

							objAry.add(tempObj);
						}

						sqlobj.put("rows", objAry);

					} catch(Exception e){
						e.printStackTrace();
					} finally {
						setOraClose(con,stmt,rs);
					}

					return sqlobj.toJSONString();
				}
				
				public String get_json_0301item_ban(String tuser, String stime, String etime, String acqcd, String depcd,
						String mid) {
					Connection con = null;
					PreparedStatement stmt = null;
					ResultSet rs = null;
					
					StringBuffer qrybuf = new StringBuffer();
					StringBuffer wherebuf = new StringBuffer();

					//2021.03.02 수정 :: DEPTOT-ACQ_CD가 없어요...
					StringBuffer acqcdBuf = new StringBuffer();

					JSONObject sqlobj = new JSONObject();
					JSONArray objAry = new JSONArray();

					try {
						//tuser split
						String[] userexp = tuser.split(":");
						//acqcd split
						String[] acqcdexp = acqcd.split(",");
						//검색항목에 따른 where 조건절 setting 관련 변수
						ArrayList<String> setting = new ArrayList<>();

						wherebuf.append(" WHERE MID IN (SELECT MID FROM TB_BAS_MIDMAP WHERE ORG_CD = ? ) ");
						setting.add(userexp[1]);
						
						if(!stime.equals("") && stime != null) {
							wherebuf.append(" AND EXP_DD >= ? ");
							setting.add(stime);
						}

						if(!etime.equals("") && etime != null) {
							wherebuf.append(" AND EXP_DD <= ? ");
							setting.add(etime);
						}
						
						if(!mid.equals("") && mid != null) {
							wherebuf.append(" AND MID = ? ");
							setting.add(mid);
						}

						//1. TID LIST중 LOGIN SESSION에 DEP_CD 설정되어 있을 때
						//1. 검색중 사업부 선택이 있을 때
						if(!depcd.equals("") && depcd != null) {
							if(depcd=="1") {
								wherebuf.append(" AND MID IN ('768017318','00052904921','00951457027','128890479','57296808','151098345','9052663887','151558364','721176212','00098153744','154944840','00903164052','0118721620','179102374','178597603','9956970402' ) ");
							}else if(depcd=="2") {
								wherebuf.append(" AND MID IN ('704855398','00084542316','00986653087','165138860','0104783451','860295101','9969229911','140239694','721219360','00098234952','155068491','00903276708','0118796648','179216357','178600027','9957975427' ) ");
							}else if(depcd=="3") {
								wherebuf.append(" AND MID IN ('707528764','00087259990','00989439518','167802984','0107608507','860386610','9967457077','143275451','721225822','00098235865','155072408','00903280940','0118799154','179216254','178600545','9957971095' )");
							}
						}
						
						if(!acqcd.equals("") && acqcd != null) {
							wherebuf.append(" AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD=? AND ORG_CD=? ) ");
							setting.add(acqcd);
							setting.add(userexp[1]);
						}

						qrybuf.append(" SELECT ");
						qrybuf.append("     T1.DEP_SEQ DPSEQ, DEP_NM, T3.DEP_CD DEP_CD, PUR_NM, T1.MID MID, T1.EXP_DD EXP_DD ");
						qrybuf.append("     , T_CNT ");
						qrybuf.append("     , T_BAN ");
						qrybuf.append("     , T_AMT ");
						qrybuf.append("     , T_FEE ");
						qrybuf.append("     , T_EXP ");
						qrybuf.append("     , I_CNT ");
						qrybuf.append("     , I_BAN ");
						qrybuf.append("     , I_AMT ");
						qrybuf.append("     , I_FEE ");
						qrybuf.append("     , I_EXP ");
						qrybuf.append("     , BANK_AMT ");
						qrybuf.append(" FROM(     ");
						qrybuf.append("     SELECT ");
						qrybuf.append("         T1.DEP_SEQ, MID, EXP_DD, SUM(TOT_CNT) T_CNT, SUM(TOT_BAN) T_BAN, SUM(TOT_NETAMT) T_AMT ");
						qrybuf.append("         , SUM(TOT_INPAMT) T_FEE, SUM(TOT_EXPAMT) T_EXP, SUM(I_CNT) I_CNT, SUM(I_BAN) I_BAN ");
						qrybuf.append("         , SUM(I_AMT) I_AMT, SUM(I_FEE) I_FEE, SUM(I_EXP) I_EXP ");
						qrybuf.append("     FROM(     ");
						qrybuf.append("         SELECT ");
						qrybuf.append("             MID, EXP_DD, DEP_SEQ, SUM(TOT_CNT) TOT_CNT ,SUM(BAN_CNT) TOT_BAN,(SUM(EXP_AMT)+SUM(INP_AMT)) TOT_NETAMT ");
						qrybuf.append("             ,SUM(INP_AMT) TOT_INPAMT, SUM(EXP_AMT) TOT_EXPAMT ");
						qrybuf.append("         FROM ");
						qrybuf.append("             TB_MNG_DEPTOT  ");
						qrybuf.append(wherebuf.toString());
						qrybuf.append("         GROUP BY DEP_SEQ, MID, EXP_DD ");
						qrybuf.append("         ORDER BY EXP_DD DESC     ");
						qrybuf.append("     )T1 ");
						qrybuf.append("     LEFT OUTER JOIN( ");
						qrybuf.append("         SELECT ");
						qrybuf.append("             DEP_SEQ ");
						qrybuf.append("             , (SUM(ITEM_CNT60)+SUM(ITEM_CNT67)) I_CNT ");
						qrybuf.append("             , SUM(ITEM_CNTBAN) I_BAN ");
						qrybuf.append("             , (SUM(ITEM_AMT60)-SUM(ITEM_AMT67)) I_AMT ");
						qrybuf.append("             , (SUM(ITEM_FEE60)-SUM(ITEM_FEE67)) I_FEE ");
						qrybuf.append("             , (SUM(ITEM_AMT60)-SUM(ITEM_AMT67))-(SUM(ITEM_FEE60)-SUM(ITEM_FEE67)) I_EXP ");
						qrybuf.append("         FROM(     ");
						qrybuf.append("             SELECT  ");
						qrybuf.append("                 DEP_SEQ   ");
						qrybuf.append("                 ,CASE WHEN RTN_CD='60' THEN COUNT(1) ELSE 0 END ITEM_CNT60 ");
						qrybuf.append("                 ,CASE WHEN RTN_CD='67' THEN COUNT(1) ELSE 0 END ITEM_CNT67 ");
						qrybuf.append("                 ,CASE WHEN RTN_CD NOT IN ('60', '67') THEN COUNT(1) ELSE 0 END ITEM_CNTBAN ");
						qrybuf.append("                 ,CASE WHEN RTN_CD='61' THEN SUM(SALE_AMT) ELSE 0 END ITEM_AMT60 ");
						qrybuf.append("                 ,CASE WHEN RTN_CD='64' THEN SUM(SALE_AMT) ELSE 0 END ITEM_AMT67 ");
						qrybuf.append("                 ,CASE WHEN RTN_CD='61' THEN SUM(FEE) ELSE 0 END ITEM_FEE60 ");
						qrybuf.append("                 ,CASE WHEN RTN_CD='64' THEN SUM(FEE) ELSE 0 END ITEM_FEE67 ");
						qrybuf.append("             FROM  ");
						qrybuf.append("                 TB_MNG_DEPDATA     ");
						qrybuf.append(wherebuf.toString());
						qrybuf.append("             GROUP BY DEP_SEQ, RTN_CD ");
						qrybuf.append("         ) ");
						qrybuf.append("         GROUP BY DEP_SEQ ");
						qrybuf.append("     )T2 ON(T1.DEP_SEQ=T2.DEP_SEQ) ");
						qrybuf.append("     GROUP BY T1.DEP_SEQ, MID, EXP_DD ");
						qrybuf.append(" )T1 ");
						qrybuf.append(" LEFT OUTER JOIN( ");
						qrybuf.append("     SELECT ");
						qrybuf.append("         EXP_DD ");
						qrybuf.append("         , MID ");
						qrybuf.append("         , CASE WHEN SUM(EXP_AMT) IS NULL THEN 0 ELSE SUM(EXP_AMT) END BANK_AMT ");
						qrybuf.append("     FROM  ");
						qrybuf.append("         TB_MNG_BANKDATA ");
						qrybuf.append(" 		WHERE MID IS NOT NULL AND NOT REGEXP_LIKE( exp_amt,'[A-Za-z]|[가-힛]|') AND exp_amt != ''");
						qrybuf.append("     GROUP BY EXP_DD, MID ");
						qrybuf.append(" )T2 ON(T1.MID=T2.MID AND T1.EXP_DD=T2.EXP_DD) ");
						
						//조건절 두번 들어가는 부분
						ArrayList<String> preSet = new ArrayList<>();
						int tempNum = 0;
						for(int j = 0; j < 2; j++) {
							tempNum = setting.size() * j;
							for(int i = 0; i<setting.size(); i++) {
								preSet.add(setting.get(i));
							}
						}
						
						qrybuf.append(" LEFT OUTER JOIN(SELECT ORG_CD, DEP_CD, MER_NO, PUR_CD FROM TB_BAS_MERINFO WHERE ORG_CD=?)T3 ON(T1.MID=T3.MER_NO) ");
						preSet.add(userexp[1]);
						qrybuf.append(" LEFT OUTER JOIN(SELECT ORG_CD, ORG_NM FROM TB_BAS_ORG)T4 ON(T3.ORG_CD=T4.ORG_CD) ");
						qrybuf.append(" LEFT OUTER JOIN(SELECT DEP_CD, DEP_NM FROM TB_BAS_DEPART WHERE ORG_CD=?)T5 ON(T3.DEP_CD=T5.DEP_CD) ");
						preSet.add(userexp[1]);
						qrybuf.append(" LEFT OUTER JOIN(SELECT PUR_CD, PUR_NM, PUR_SORT, PUR_KOCES FROM TB_BAS_PURINFO)T6 ON(T3.PUR_CD=T6.PUR_CD) ");
						qrybuf.append(" LEFT OUTER JOIN(SELECT ORG_CD, USER_PUR_CD, USER_PURSORT FROM TB_BAS_USERPURINFO WHERE ORG_CD=?)S3 ON(T6.PUR_CD=S3.USER_PUR_CD) ");
						preSet.add(userexp[1]);
						qrybuf.append(" WHERE (I_AMT is not null OR I_AMT not in ('0')) ");
						qrybuf.append(" ORDER BY DEP_NM ASC, USER_PURSORT ASC, PUR_NM ASC   ");

						//디버깅용
						utilm.debug_sql(qrybuf, preSet);

						con = getOraConnect();
						stmt = con.prepareStatement(qrybuf.toString());
						for(int k = 0; k < preSet.size(); k++) {
							stmt.setString((k+1), preSet.get(k));
						}
						rs = stmt.executeQuery();

						int rows = 1;

						//2021,02,24 total 합계부분 계산 후 css 추가
						int total_i_cnt = 0, total_i_ban = 0, total_t_cnt = 0, total_t_ban = 0;
						long total_i_amt = 0, total_i_fee = 0, total_i_exp = 0, total_t_amt = 0, total_t_fee = 0, total_t_exp = 0, total_bank_amt = 0;
						while(rs.next()) {
							//DEP_NM, T3.DEP_CD DEP_CD, PUR_NM, T1.MID MID, T1.EXP_DD EXP_DD, T_CNT, T_BAN, T_AMT, T_FEE, T_EXP, I_CNT, I_BAN, I_AMT, I_FEE, I_EXP, BANK_AMT 
							JSONObject tempObj = new JSONObject();
							JSONArray tempAry = new JSONArray();

							int t_cnt = Integer.parseInt(utilm.checkNumberData(rs.getString("T_CNT")));
							int t_ban = Integer.parseInt(utilm.checkNumberData(rs.getString("T_BAN")));
							long t_amt  = Long.parseLong(utilm.checkNumberData(rs.getString("T_AMT")));
							long t_fee  = Long.parseLong(utilm.checkNumberData(rs.getString("T_FEE")));
							long t_exp  = Long.parseLong(utilm.checkNumberData(rs.getString("T_EXP")));

							int i_cnt = Integer.parseInt(utilm.checkNumberData(rs.getString("I_CNT")));
							int i_ban = Integer.parseInt(utilm.checkNumberData(rs.getString("I_BAN")));
							long i_amt  = Long.parseLong(utilm.checkNumberData(rs.getString("I_AMT")));
							long i_fee  = Long.parseLong(utilm.checkNumberData(rs.getDouble("I_FEE")));
							long i_exp  = Long.parseLong(utilm.checkNumberData(rs.getDouble("I_EXP")));

							long bank_amt = Long.parseLong(utilm.checkNumberData(rs.getString("BANK_AMT")));

							total_i_cnt += i_cnt;
							total_i_ban += i_ban;
							total_i_amt += i_amt;
							total_i_fee += i_fee;
							total_i_exp += i_exp;

							total_t_cnt += t_cnt;
							total_t_ban += t_ban;
							total_t_amt += t_amt;
							total_t_fee += t_fee;
							total_t_exp += t_exp;

							total_bank_amt += bank_amt;

							tempAry.add(utilm.setDefault(rs.getString("DEP_NM")));
							tempAry.add(utilm.setDefault(rs.getString("MID")));
							tempAry.add(utilm.setDefault(rs.getString("PUR_NM")));
							tempAry.add(utilm.str_to_dateformat_deposit(rs.getString("EXP_DD")));
							tempAry.add(t_cnt);
							tempAry.add(t_ban);
							tempAry.add(t_amt);
							tempAry.add(t_fee);
							tempAry.add(t_exp);
							tempAry.add(i_cnt);
							tempAry.add(i_ban);
							tempAry.add(i_amt);
							tempAry.add(i_fee);
							tempAry.add(i_exp);
							tempAry.add(bank_amt);

							tempAry.add(t_exp - i_exp);
							tempAry.add(i_exp - bank_amt);

							tempObj.put("id", rows);
							tempObj.put("data", tempAry);

							objAry.add(tempObj);

							rows++;
						}

						//합계부분
						JSONObject tempObj = new JSONObject();
						JSONArray tempAry = new JSONArray();

						tempAry.add("합계");
						tempAry.add("");
						tempAry.add("");
						tempAry.add("");
						tempAry.add(total_t_cnt);
						tempAry.add(total_t_ban);
						tempAry.add(total_t_amt);
						tempAry.add(total_t_fee);
						tempAry.add(total_t_exp);
						tempAry.add(total_i_cnt);
						tempAry.add(total_i_ban);
						tempAry.add(total_i_amt);
						tempAry.add(total_i_fee);
						tempAry.add(total_i_exp);
						tempAry.add(total_bank_amt);

						tempAry.add(total_t_exp - total_i_exp);
						tempAry.add(total_i_exp - total_bank_amt);

						tempObj.put("id", "total");
						tempObj.put("data", tempAry);

						objAry.add(tempObj);

						sqlobj.put("rows", objAry);

					} catch(Exception e){
						e.printStackTrace();
					} finally {
						setOraClose(con,stmt,rs);
					}
					return sqlobj.toJSONString();
				}

				public int get_deposit_checkup(String tuser, String seqno, String dpflag) {
					Connection con = null;
					PreparedStatement stmt = null;
					ResultSet rs = null;
					
					int result = 0;
					StringBuffer qrybuf = new StringBuffer();
					ArrayList<String> setting = new ArrayList<>();
					
					try {
						//tuser split
						String[] userexp = tuser.split(":");
						
						if(dpflag.equals("true")) {
							qrybuf.append("SELECT COUNT(1) MCNT FROM "+userexp[5]+" WHERE SEQNO=?");
							con = getOraConnect();
							con.setAutoCommit(false);
							stmt = con.prepareStatement(qrybuf.toString());
							stmt.setString(1, seqno);

							rs = stmt.executeQuery();
							int seqCh = 0;
							while(rs.next()) {
								seqCh = Integer.parseInt(rs.getString("MCNT"));
							}
							
							if(seqCh>0) {
								qrybuf = new StringBuffer();
								qrybuf.append(" update "+userexp[5]+" set tran_stat='RV01' where seqno=? ");

								stmt = con.prepareStatement(qrybuf.toString());
								stmt.setString(1, seqno);
								
								result = stmt.executeUpdate();
							}else {
							
								qrybuf = new StringBuffer();
								qrybuf.append("INSERT INTO TB_HIS_TRANS VALUES (?, ?, ?, "+userexp[5]+", 'TRAN_STAT', 'TR00', 'RV01', SYSDATE) ");
			
								stmt = con.prepareStatement(qrybuf.toString());
								stmt.setString(1, seqno);
								stmt.setString(2, userexp[1]);
								stmt.setString(3, userexp[0]);	
								
								result = stmt.executeUpdate();
							}
						}else{
							qrybuf = new StringBuffer();
							qrybuf.append("SELECT COUNT(1) MCNT FROM "+userexp[5]+" WHERE SEQNO=?");
							
							con = getOraConnect();
							stmt = con.prepareStatement(qrybuf.toString());
							stmt.setString(1, seqno);

							rs = stmt.executeQuery();
							int seqCh = 0;
							
							while(rs.next()) {
								seqCh = Integer.parseInt(rs.getString("MCNT"));
							}
							
							if(seqCh>0) {
								// 매입 보류인 항목은 S로 업데이트 한다.
								qrybuf = new StringBuffer();
								qrybuf.append(" update "+userexp[5]+" set tran_stat='' where seqno=? ");

								stmt = con.prepareStatement(qrybuf.toString());
								stmt.setString(1, seqno);
								
								result = stmt.executeUpdate();
							}else {

								qrybuf = new StringBuffer();
								qrybuf.append(" INSERT INTO TB_HIS_TRANS VALUES (?, ?, ?, "+userexp[5]+", 'TRAN_STAT', 'RV01', 'TR00', SYSDATE) ");
			
								stmt = con.prepareStatement(qrybuf.toString());
								stmt.setString(1, seqno);
								stmt.setString(2, userexp[1]);
								stmt.setString(3, userexp[0]);	
								
								result = stmt.executeUpdate();
							}
						}
						con.commit();

					} catch(Exception e){
						e.printStackTrace();
						rollBack(con);
						result = -1;
					} finally {
						setOraClose(con,stmt,rs);
					}
					return result;
				}
				
				public int get_deposit_request(String tuser, String stime, String etime, String samt, String eamt, String appno, String pid, String tradeidx,
						String mid, String tid, String acqcd, String depcd, String auth01, String auth02, String auth03, String depreq1, String depreq2, String depreq3) {
					Connection con2 = null;
					PreparedStatement stmt2 = null;
					ResultSet rs2 = null;
					
					StringBuffer pqrybuf = new StringBuffer();

					int smtsidx = 2;
					int result = 0;

					try {

						String[] userexp = tuser.split(":");
						String setdc = "";
						ArrayList<String> setting = new ArrayList<>();

						pqrybuf.append(" UPDATE "+userexp[5]+" T1 SET TRAN_STAT='RQ00' WHERE SVCGB IN ('CC', 'CE') AND AUTHCD='0000' AND MID IN ( ");
						pqrybuf.append("	SELECT MID FROM TB_BAS_MIDMAP MT1  ");
						pqrybuf.append("	LEFT OUTER JOIN( ");
						pqrybuf.append("		SELECT MER_NO, MTYPE FROM TB_BAS_MERINFO WHERE ORG_CD=? AND MTYPE='EDI' ");
						pqrybuf.append("	)MT2 ON(MT1.MID=MT2.MER_NO) ");
						pqrybuf.append("	WHERE MT1.ORG_CD=? AND MT2.MTYPE='EDI' ");
						pqrybuf.append(") ");
					
						int stime_idx = 0;
						if(null!=stime&&""!=stime) {
							smtsidx++;
							stime_idx = smtsidx;
							pqrybuf.append("            AND T1.APPDD>=? ");
						}

						int etime_idx = 0;
						if(null!=etime&&""!=etime) {
							smtsidx++;
							etime_idx = smtsidx;
							pqrybuf.append("            AND T1.APPDD<=? ");
						}

						int samt_idx = 0;
						if(null!=samt&&""!=samt) {
							smtsidx++;
							samt_idx = smtsidx;
							pqrybuf.append("            AND T1.AMOUNT>=? ");
						}

						int eamt_idx = 0;
						if(null!=eamt&&""!=eamt) {
							smtsidx++;
							eamt_idx = smtsidx;
							pqrybuf.append("            AND T1.AMOUNT<=? ");
						}

						int appno_idx = 0;
						if(null!=appno&&""!=appno) {
							smtsidx++;
							appno_idx = smtsidx;
							pqrybuf.append("            AND T1.APP_NO=? ");
						}

						int pid_idx = 0;
						if(null!=pid&&""!=pid) {
							smtsidx++;
							pid_idx = smtsidx;
							pqrybuf.append("            AND T1.EXT_FIELD=? ");
						}

						int tran_idx = 0;
						if(null!=tradeidx&&""!=tradeidx) {
							smtsidx++;
							tran_idx = smtsidx;
							pqrybuf.append("            AND T1.TRANIDX=? ");
						}
						
						int mid_idx = 0;
						if(null!=mid&&""!=mid) {
							smtsidx++;
							mid_idx = smtsidx;
							pqrybuf.append("            AND T1.MID=? ");
						}
						
						int tid_idx = 0;
						if(null!=tid&&""!=tid) {
							smtsidx++;
							tid_idx = smtsidx;
							pqrybuf.append("            AND T1.TID=? ");
						}


						if(null!=acqcd&&""!=acqcd) {
							String[] acqexp = acqcd.split(",");
							String acqwh = "('" + utilm.implode("', '", acqexp) + "')";
							pqrybuf.append("            AND T1.ACQ_CD IN " + acqwh);
						}
						
						if(depreq1.equals("Y")&& !depreq1.equals("")||depreq2.equals("Y")&& !depreq2.equals("")||depreq3.equals("Y")&& !depreq3.equals("")){
							String[] imp_dep = new String[2];
							
							if(depreq1.equals("Y")&&depreq3.equals("Y")) {
								imp_dep[0] = "'TR00'";
								imp_dep[1] = "'DP99'";
							}else if(depreq1.equals("Y")&& !depreq1.equals("")){
								imp_dep[0] = "'TR00'";
							}else if(depreq3.equals("Y") && !depreq3.equals("")) {
								imp_dep[1] = "'DP99'";
							}
							pqrybuf.append(" AND NVL(TRAN_STAT,'TR00') IN (" + utilm.implode(",", imp_dep) + ") ");
						}
						
						if(!auth01.equals("Y") && !auth01.equals("")||auth02.equals("Y")||auth03.equals("Y")) {
							String[] imp_auth = new String[2];
							if(null != auth02 && "" != auth02 && auth02.equals("Y")) {
								imp_auth[0] = "'A'"; 
							}

							if(null != auth03 && "" != auth03 && auth03.equals("Y")) {
								imp_auth[1] = "'C'"; 
							}
							pqrybuf.append(" AND T1.APPGB IN (" + utilm.implode(",", imp_auth) + ") ");
						}
					
						con2 = getOraConnect();
						con2.setAutoCommit(false);
						stmt2 = con2.prepareStatement(pqrybuf.toString());
						
						stmt2.setString(1, userexp[1]);
						stmt2.setString(2, userexp[1]);
						if(null!=stime&&""!=stime) {stmt2.setString(stime_idx, stime);}
						if(null!=etime&&""!=etime) {stmt2.setString(etime_idx, etime);}
						if(null!=samt&&""!=samt) {stmt2.setString(samt_idx, samt);}
						if(null!=eamt&&""!=eamt) {stmt2.setString(eamt_idx, eamt);}
						if(null!=appno&&""!=appno) {stmt2.setString(appno_idx, appno);}
						if(null!=pid&&""!=pid) {stmt2.setString(pid_idx, pid);}
						if(null!=tradeidx&&""!=tradeidx) {stmt2.setString(tran_idx, tradeidx);}
						if(null!=mid&&""!=mid) {stmt2.setString(mid_idx, mid);}
						if(null!=tid&&""!=tid) {stmt2.setString(tid_idx, tid);}

						int update = stmt2.executeUpdate();

						if (update == 1) {
							stmt2 = null;
							pqrybuf = new StringBuffer();
							
							pqrybuf.append(" INSERT INTO TB_HIS_DPREQ_REQ VALUES (?,'0',TO_CHAR(SYSDATE, 'YYYYMMDD'),sysdate,'') ");

							stmt2 = con2.prepareStatement(pqrybuf.toString());
							stmt2.setString(1, userexp[1]);
							result = stmt2.executeUpdate();

						}
						con2.commit();
					} catch(Exception e){
						e.printStackTrace();
						result = -1;
					} finally {
						setOraClose(con2,stmt2,rs2);
					}
					
					return result;
				}
		//수정해야함
				
				public String get_json_0107total(String tuser, String stime, String etime, String samt, String eamt, String appno
						, String cardtp, String auth01, String auth02, String auth03) {
					Connection con = null;
					PreparedStatement stmt = null;
					ResultSet rs = null;
					
					JSONObject sqlobj = new JSONObject();
					JSONArray sqlAry = new JSONArray();

					StringBuffer wherebuf = new StringBuffer();
					StringBuffer exwherebuf = new StringBuffer();
					StringBuffer qrybuf = new StringBuffer();

					try {
						// tuser, syear, smon, samt, eamt, depcd
						// tuser split
						String[] userexp = tuser.split(":");
						// 검색항목에 따른 where 조건절 setting 관련 변수
						ArrayList<String> setting = new ArrayList<>();
						
						if (!stime.equals("") && stime != null && !etime.equals("") && etime != null) {
							wherebuf.append(" AND T1.APPDD>=? AND T1.APPDD<=? ");
							setting.add(stime);
							setting.add(etime);
						}
						
						if (!samt.equals("") && samt != null && !eamt.equals("") && eamt != null) {
							wherebuf.append(" AND T1.AMOUNT>=? AND T1.AMOUNT<=? ");
							setting.add(stime);
							setting.add(etime);
						}

						if(!appno.equals("") && appno != null) {
							wherebuf.append(" AND T1.APPNO=? ");
							setting.add(appno);
						}
						
						if(!cardtp.equals("") && cardtp != null) {
							if(cardtp=="0") {
								exwherebuf.append("WHERE DDCTXT = '소득공제' ");
							}else if(cardtp=="1") {
								exwherebuf.append("WHERE DDCTXT = '지출증빙' ");
							}else if(cardtp=="2") {
								exwherebuf.append("WHERE DDCTXT = '자진발급' ");
							}
						}
						
						if(!auth01.equals("Y")){
							if(auth02.equals("Y")){wherebuf.append(" AND APPGB = 'A'");}
							else if(auth03.equals("Y")){wherebuf.append(" AND APPGB = 'C'");}
							else if(auth02.equals("Y") && auth03.equals("Y")) {wherebuf.append(" AND APPGB IN ('A', 'C')");}
						}				
						
						qrybuf.append("SELECT ");
						qrybuf.append("	SEQNO ");
						qrybuf.append("	, BIZNO ");
						qrybuf.append("	, TID ");
						qrybuf.append("	, MID ");
						qrybuf.append("	, VANGB ");
						qrybuf.append("	, MDATE ");
						qrybuf.append("	, SVCGB ");
						qrybuf.append("	, TRANIDX ");
						qrybuf.append("	, APPGB ");
						qrybuf.append("	, AUTHTXT ");
						qrybuf.append("	, ENTRYMD ");
						qrybuf.append("	, APPDD ");
						qrybuf.append("	, APPTM ");
						qrybuf.append("	, APPNO ");
						qrybuf.append("	, CARDNO ");
						qrybuf.append("	, HALBU ");
						qrybuf.append("	, CURRENCY ");
						qrybuf.append("	, AMOUNT ");
						qrybuf.append("	, AMT_UNIT ");
						qrybuf.append("	, AMT_TIP ");
						qrybuf.append("	, AMT_TAX ");
						qrybuf.append("	, ISS_CD ");
						qrybuf.append("	, ISS_NM ");
						qrybuf.append("	, ACQ_CD ");
						qrybuf.append("	, ACQ_NM ");
						qrybuf.append("	, AUTHCD ");
						qrybuf.append("	, AUTHMSG ");
						qrybuf.append("	, CARD_CODE ");
						qrybuf.append("	, CHECK_CARD ");
						qrybuf.append("	, OVSEA_CARD ");
						qrybuf.append("	, OVERTXT ");
						qrybuf.append("	, TLINEGB ");
						qrybuf.append("	, TLINEGBTXT ");
						qrybuf.append("	, SIGNCHK ");
						qrybuf.append("	, DDCGB ");
						qrybuf.append("	, DDCTXT ");
						qrybuf.append("	, EXT_FIELD ");
						qrybuf.append("	, OAPPNO ");
						qrybuf.append("	, OAPPDD ");
						qrybuf.append("	, OAPPTM ");
						qrybuf.append("	, OAPP_AMT ");
						qrybuf.append("	, ADD_GB ");
						qrybuf.append("	, ADD_CID ");
						qrybuf.append("	, ADD_CD ");
						qrybuf.append("	, ADD_RECP ");
						qrybuf.append("	, ADD_CNT ");
						qrybuf.append("	, ADD_CASHER ");
						qrybuf.append("	, ADD_DATE ");
						qrybuf.append("	, SECTION_NO ");
						qrybuf.append("	, PUR_NM ");
						qrybuf.append("	, DEP_NM ");
						qrybuf.append("	, TERM_NM ");
						qrybuf.append("FROM( ");
						qrybuf.append("	SELECT ");
						qrybuf.append("		SEQNO ");
						qrybuf.append("		, BIZNO ");
						qrybuf.append("		, TID ");
						qrybuf.append("		, MID ");
						qrybuf.append("		, VANGB ");
						qrybuf.append("		, MDATE ");
						qrybuf.append("		, SVCGB ");
						qrybuf.append("		, TRANIDX ");
						qrybuf.append("		, APPGB ");
						qrybuf.append("		, CASE ");
						qrybuf.append("			WHEN APPGB='A' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND DEP_CD=? AND SCD_CD='SCD0002') ");
						setting.add(userexp[1]);
						setting.add(userexp[2]);
						qrybuf.append("			WHEN APPGB='C' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND DEP_CD=? AND SCD_CD='SCD0003') ");
						setting.add(userexp[1]);
						setting.add(userexp[2]);
						qrybuf.append("		END AUTHTXT ");
						qrybuf.append("		, ENTRYMD ");
						qrybuf.append("		, APPDD ");
						qrybuf.append("		, APPTM ");
						qrybuf.append("		, APPNO ");
						qrybuf.append("		, CARDNO ");
						qrybuf.append("		, HALBU ");
						qrybuf.append("		, CURRENCY ");
						qrybuf.append("		, AMOUNT ");
						qrybuf.append("		, AMT_UNIT ");
						qrybuf.append("		, AMT_TIP ");
						qrybuf.append("		, AMT_TAX ");
						qrybuf.append("		, ISS_CD ");
						qrybuf.append("		, ISS_NM ");
						qrybuf.append("		, ACQ_CD ");
						qrybuf.append("		, ACQ_NM ");
						qrybuf.append("		, AUTHCD ");
						qrybuf.append("		, AUTHMSG ");
						qrybuf.append("		, CARD_CODE ");
						qrybuf.append("		, CHECK_CARD ");
						qrybuf.append("		, OVSEA_CARD ");
						qrybuf.append("		,CASE ");
						qrybuf.append("			WHEN OVSEA_CARD='1' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND DEP_CD=? AND SCD_CD='SCD0004') ");
						setting.add(userexp[1]);
						setting.add(userexp[2]);
						qrybuf.append("			WHEN OVSEA_CARD='2' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND DEP_CD=? AND SCD_CD='SCD0005') ");
						setting.add(userexp[1]);
						setting.add(userexp[2]);
						qrybuf.append("			WHEN OVSEA_CARD='3' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND DEP_CD=? AND SCD_CD='SCD0006') ");
						setting.add(userexp[1]);
						setting.add(userexp[2]);
						qrybuf.append("		END OVERTXT ");
						qrybuf.append("		, TLINEGB ");
						qrybuf.append("		, CASE WHEN TLINEGB IS NOT NULL THEN (SELECT CODE_VAL FROM TB_BAS_CODE WHERE TRIM(CODE_NO)=TRIM(TLINEGB)) END TLINEGBTXT ");
						qrybuf.append("		, SIGNCHK ");
						qrybuf.append("		, DDCGB ");
						qrybuf.append("		,CASE ");
						qrybuf.append("			WHEN DDCGB='0' AND CARDNO<>'ef7dc2d7309c745fa1dc838fdac105a939cbcb24e50a41027d60475fec001909' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND DEP_CD=? AND SCD_CD='SCD0007') ");
						setting.add(userexp[1]);
						setting.add(userexp[2]);
						qrybuf.append("			WHEN DDCGB='1' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND DEP_CD=? AND SCD_CD='SCD0008') ");
						setting.add(userexp[1]);
						setting.add(userexp[2]);
						qrybuf.append("			WHEN DDCGB='2' OR (DDCGB='0' AND CARDNO='ef7dc2d7309c745fa1dc838fdac105a939cbcb24e50a41027d60475fec001909')  THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND DEP_CD=? AND SCD_CD='SCD0009') ");
						setting.add(userexp[1]);
						setting.add(userexp[2]);
						qrybuf.append("		END DDCTXT ");
						qrybuf.append("		, EXT_FIELD ");
						qrybuf.append("		, OAPPNO ");
						qrybuf.append("		, OAPPDD ");
						qrybuf.append("		, OAPPTM ");
						qrybuf.append("		, OAPP_AMT ");
						qrybuf.append("		, ADD_GB ");
						qrybuf.append("		, ADD_CID ");
						qrybuf.append("		, ADD_CD ");
						qrybuf.append("		, ADD_RECP ");
						qrybuf.append("		, ADD_CNT ");
						qrybuf.append("		, ADD_CASHER ");
						qrybuf.append("		, ADD_DATE ");
						qrybuf.append("		, SECTION_NO ");
						qrybuf.append("		, PUR_NM ");
						qrybuf.append("		, DEP_NM ");
						qrybuf.append("		, TERM_NM ");
						qrybuf.append("	FROM ");
						qrybuf.append("		GLOB_MNG_ICVAN T1 ");
						qrybuf.append("	LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD=?)T3 ON(T1.TID=T3.TERM_ID) ");
						setting.add(userexp[1]);
						qrybuf.append("	LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD=?)T4 ON(T3.DEP_CD=T4.DEP_CD) ");
						setting.add(userexp[1]);
						qrybuf.append("	LEFT OUTER JOIN( SELECT PUR_NM, PUR_OCD, PUR_KOCES FROM TB_BAS_PURINFO)T5 ON (T1.ACQ_CD=T5.PUR_OCD OR T1.ACQ_CD=T5.PUR_KOCES) ");
						qrybuf.append(" WHERE SVCGB IN ('CB') AND AUTHCD='0000' AND TID IN (SELECT TID FROM TB_BAS_TIDMAP WHERE ORG_CD=?) ");
						setting.add(userexp[1]);
						qrybuf.append(wherebuf.toString());
						qrybuf.append(") ");
						
						qrybuf.append(exwherebuf.toString());
						qrybuf.append("order by appdd desc, apptm desc ");
						
						// 디버깅용
						utilm.debug_sql(qrybuf, setting);
			
						con = getOraConnect();
						stmt = con.prepareStatement(qrybuf.toString());
						for (int k = 0; k < setting.size(); k++) {
							stmt.setString((k + 1), setting.get(k));
						}
			
						rs = stmt.executeQuery(); 
			
						int icnt = 1;
						long total = 0;
						while (rs.next()) {
							JSONObject obj1 = new JSONObject();
							JSONArray arr2 = new JSONArray();
							
							long tot = Long.parseLong(utilm.checkNumberData(rs.getString("AMOUNT")));
							total += tot;

							String cardno_dec = utilm.cardno_masking(trans_seed_manager.seed_dec_card(rs.getString("CARDNO").trim()));
							
							arr2.add(icnt);
							arr2.add(rs.getString("DEP_NM"));
							arr2.add(rs.getString("TERM_NM"));
							arr2.add(rs.getString("TID"));
							arr2.add(utilm.str_to_dateformat(rs.getString("APPDD")));
							arr2.add(utilm.str_to_timeformat(rs.getString("APPTM")));
							arr2.add(utilm.str_to_dateformat(rs.getString("OAPPDD")));
							arr2.add(rs.getString("APPNO"));
							arr2.add(rs.getString("AUTHTXT"));
							arr2.add(cardno_dec);
							arr2.add(rs.getString("AMOUNT"));//11
							arr2.add(rs.getString("DDCTXT"));
							arr2.add(rs.getString("TLINEGBTXT"));
							arr2.add(rs.getString("OVERTXT"));
							arr2.add(rs.getString("AUTHCD"));
							arr2.add(rs.getString("TRANIDX"));
							arr2.add(rs.getString("EXT_FIELD"));
							arr2.add(rs.getString("AUTHMSG"));
							
							obj1.put("id", rs.getString("SEQNO"));
							obj1.put("data", arr2);
			
							sqlAry.add(obj1);
			
						}
						JSONObject obj1 = new JSONObject();
						JSONArray arr2 = new JSONArray();
						
						arr2.add("합계");
						arr2.add("");
						arr2.add("");
						arr2.add("");
						arr2.add("");
						arr2.add("");
						arr2.add("");
						arr2.add("");
						arr2.add("");
						arr2.add("");
						arr2.add(total);
						arr2.add("");
						arr2.add("");
						arr2.add("");
						arr2.add("");
						arr2.add("");
						arr2.add("");
						
						obj1.put("id", "total");
						obj1.put("data", arr2);
						
						sqlAry.add(obj1);
						
						sqlobj.put("rows", sqlAry);
						
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						setOraClose(con,stmt,rs);
					}
			
					return sqlobj.toJSONString();
				}
}
