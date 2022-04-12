package com.gaon.ifou.v3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class db2_real_manager {
	//2021.02.16 강원대병원v3 - 상세내역조회 페이지 컬럼 load
		public String[] get_page_column(String tuser, String type) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			String[] column = new String[6];
			StringBuffer menuSql = new StringBuffer();


			try {
				//tuser split
				String[] userexp = tuser.split(":");

				String pages = "";
				switch(type) {
				case "van":
					pages = "0204";
					break;
				case "tr":
					pages = "2800";
					break;
				default :
					pages = "";
					break;
				}

				menuSql.append("SELECT FIELDS_TXT, ALIGNS, COL_TYPE, SORTS, WIDTHS, POS_FIELD ");
				menuSql.append("FROM TB_SYS_DOMAIN WHERE ORGCD = ?  AND PAGES = ? ORDER BY ORN ASC");

				con = getOraConnect();
				stmt = con.prepareStatement(menuSql.toString());
				stmt.setString(1, userexp[1]);
				stmt.setString(2, pages);

				//System.out.println(menuSql.toString());
				//System.out.println(userexp[1]);
				//System.out.println(pages);

				rs = stmt.executeQuery();

				//fields, aligns, colTypes, sorts, colWidth, amtset
				String fields = "순번,";
				String aligns = "center,";
				String colTypes = "ro,";
				String sorts = "int,";
				String colWidth = "60,";
				String amtset = "";

				int icnt = 1;
				while(rs.next()) {
					fields += rs.getString("FIELDS_TXT") + ",";
					aligns += rs.getString("ALIGNS") + ",";
					colTypes += rs.getString("COL_TYPE") + ",";
					sorts += rs.getString("SORTS") + ",";
					colWidth += rs.getString("WIDTHS") + ",";

					//AMOUNT - 상세내역조회 금액컬럼
					//TR_AMT - 현금영수증 금액컬럼
					String amount_check = rs.getString("POS_FIELD");
					if(amount_check.equals("AMOUNT") || amount_check.equals("TR_AMT")) {
						amtset = Integer.toString(icnt);
					}

					icnt++;
				}

				//맨 마지막 , 제거
				fields = fields.substring(0, fields.length()-1);
				aligns = aligns.substring(0, aligns.length()-1);
				colTypes = colTypes.substring(0, colTypes.length()-1);
				sorts = sorts.substring(0, sorts.length()-1);
				colWidth = colWidth.substring(0, colWidth.length()-1);

				column[0] = fields;
				column[1] = aligns;
				column[2] = colTypes;
				column[3] = sorts;
				column[4] = colWidth;
				column[5] = amtset;

			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}

			return column;
		}
		
		//2021.02.17 강원대병원v3 - 상세내역조회 엑셀다운로드 컬럼 load
		public ArrayList<String> get_column_field(String tuser, String type, String column) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			StringBuffer menuSql = new StringBuffer();
			ArrayList<String> pos_field = null;
			//SELECT POS_FIELD FROM TB_SYS_DOMAIN WHERE ORGCD='$UserExpAuth[1]' AND PAGES='0204' ORDER BY ORN ASC
			try {
				//tuser split
				String[] userexp = tuser.split(":");

				String pages = "";
				switch(type) {
				case "van":
					pages = "0204";
					break;
				case "tr":
					pages = "2800";
					break;
				default :
					pages = "";
					break;
				}

				menuSql.append("SELECT FIELDS_TXT, POS_FIELD FROM TB_SYS_DOMAIN WHERE ORGCD = ? AND PAGES = ? ORDER BY ORN ASC");
				con = getOraConnect();
				stmt = con.prepareStatement(menuSql.toString());
				stmt.setString(1, userexp[1]);
				stmt.setString(2, pages);

				rs = stmt.executeQuery();

				pos_field = new ArrayList<>();
				while(rs.next()) {
					if(column.equals("txt")) {
						pos_field.add(rs.getString("FIELDS_TXT"));
					} else if (column.equals("field")) {
						pos_field.add(rs.getString("POS_FIELD"));
					}
				}

			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}

			return pos_field;
		}
		
		//2021.02.08 강원대병원 - 종합정보 sql
		//2021.03.03 소계, 합계 그냥 다 계산해서 보냄.
		@SuppressWarnings({ "static-access", "unchecked" })
		public String get_json_0000total(String tuser, String stime, String etime, String acqcd) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			StringBuffer qrybuf = new StringBuffer();
			StringBuffer wherebuf = new StringBuffer();
			JSONObject sqlobj = new JSONObject();
			JSONArray sqlAry = new JSONArray();

			try {
				//tuser split
				String[] userexp = tuser.split(":");
				//acqcd split
				String[] acqcdexp = acqcd.split(",");
				ArrayList<String> setting = new ArrayList<>();

				wherebuf.append(" WHERE SVCGB IN ('CC', 'CE') AND AUTHCD='0000' AND TID IN (SELECT TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD = ? ");
				setting.add(userexp[1]);

				//1. dep_cd 설정시
				if(userexp[2] != null && !userexp[2].equals("")) {
					wherebuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				wherebuf.append(")");

				//2. STIME, ETIME SETTING
				//2021.03.03 종합정보 맨 처음 진입했을 때는 stime, etime null
				if(!stime.equals("") && stime != null) {
					wherebuf.append(" AND APPDD >= ? ");
					setting.add(stime);
				} else {
					wherebuf.append(" AND APPDD >= '' ");
				}
				if(!etime.equals("") && etime != null) {
					wherebuf.append(" AND APPDD <= ? ");
					setting.add(etime);
				} else {
					wherebuf.append(" AND APPDD <= '' ");
				}

				qrybuf.append("select appdd, t3.pur_nm, acnt, aamt, ccnt, camt from(");
				qrybuf.append("select APPDD, ACQ_CD, sum(acnt) acnt, sum(aamt) aamt, sum(ccnt) ccnt, sum(camt) camt from(");
				qrybuf.append("select ACQ_CD, APPDD, case when APPGB='A' then count(1) else 0 end acnt, case when APPGB='A' then sum(amount) else 0 end aamt, ");
				qrybuf.append("case when APPGB='C' then count(1) else 0 end ccnt, case when APPGB='C' then sum(amount) else 0 end camt from ");
				qrybuf.append(userexp[5]);
				qrybuf.append(wherebuf.toString());
				qrybuf.append("group by APPDD, ACQ_CD, APPGB ) group by APPDD, ACQ_CD )T1 ");
				qrybuf.append("left outer join(select PUR_CD, pur_nm, PUR_OCD, PUR_KOCES from tb_bas_purinfo)t3 on(t1.ACQ_CD=t3.PUR_OCD OR t1.ACQ_CD=t3.PUR_KOCES)");

				wherebuf = null;
				//3. acqcd setting
				if(!acqcd.equals("") && acqcd != null) {			
					wherebuf = new StringBuffer(" WHERE T1.ACQ_CD IN (");

					//setString 해야하는 parameter 개수만큼 물음표로 채워야 함.
					String[] paramTemp = new String[acqcdexp.length];

					for(int i = 0; i<acqcdexp.length; i++) {
						paramTemp[i] = "?";
						setting.add(acqcdexp[i]);
					}
					wherebuf.append(utilm.implode(", ", paramTemp)+")");
				}

				if(wherebuf != null) {
					qrybuf.append(wherebuf.toString());
				}
				qrybuf.append(" order by t1.APPDD asc, t3.pur_nm ASC ");

				//디버깅용
				utilm.debug_sql(qrybuf, setting);
				
				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				for(int k = 0; k<setting.size(); k++) {
					stmt.setString((k+1), setting.get(k));
				}

				rs = stmt.executeQuery();

				int icnt = 1;
				//순번, 승인일자, 카드사, 승인건수, 승인금액, 취소건수, 취소금액, 총금액
				ArrayList<String[]> tempStrAry = new ArrayList<>();
				String[] tempStr = new String[8];

				while(rs.next()) {
					tempStr[0] = Integer.toString(icnt);
					tempStr[1] = utilm.setDefault(rs.getString("APPDD"));
					tempStr[2] = utilm.setDefault(rs.getString("PUR_NM"));
					tempStr[3] = utilm.checkNumberData(rs.getString("ACNT"));
					tempStr[4] = utilm.checkNumberData(rs.getString("AAMT"));
					tempStr[5] = utilm.checkNumberData(rs.getString("CCNT"));
					tempStr[6] = utilm.checkNumberData(rs.getString("CAMT"));

					long total_amt = Long.parseLong(tempStr[4]) - Long.parseLong(tempStr[6]);
					tempStr[7] = Long.toString(total_amt);

					icnt++;
					tempStrAry.add(tempStr);
					tempStr = new String[8];
				}

				//소계 계산을 위한 변수
				long daamt = 0, dcamt = 0;
				int dacnt = 0, dccnt = 0;

				//합계 계산을 위한 변수
				long taamt = 0, tcamt = 0;
				int tacnt = 0, tccnt = 0;

				//승인일자 비교 - 소계 계산 유무
				String compareDay = "";
				if(tempStrAry.size() > 0) {
					for(int i = 0; i<tempStrAry.size(); i++) {
						JSONObject tempObj = new JSONObject();
						JSONArray tempAry = new JSONArray();

						//소계 만들기 위한 다음 appdd
						tempStr = tempStrAry.get(i);
						if(i < (tempStrAry.size()-1)) {
							compareDay = tempStrAry.get(i+1)[1];
						}

						dacnt += Integer.parseInt(tempStr[3]);
						daamt += Long.parseLong(tempStr[4]);
						dccnt += Integer.parseInt(tempStr[5]);
						dcamt += Long.parseLong(tempStr[6]);

						tacnt += Integer.parseInt(tempStr[3]);
						taamt += Long.parseLong(tempStr[4]);
						tccnt += Integer.parseInt(tempStr[5]);
						tcamt += Long.parseLong(tempStr[6]);

						tempAry.add(tempStr[0]);
						tempAry.add(utilm.str_to_dateformat_deposit(tempStr[1]));
						tempAry.add(tempStr[2]);
						tempAry.add(tempStr[3]);
						tempAry.add(tempStr[4]);
						tempAry.add(tempStr[5]);
						tempAry.add(tempStr[6]);
						tempAry.add(tempStr[7]);

						tempObj.put("id", Integer.parseInt(tempStr[0]));
						tempObj.put("data", tempAry);

						sqlAry.add(tempObj);

						//소계부분
						if(!compareDay.equals(tempStr[1]) || i == (tempStrAry.size()-1)) {
							tempObj = new JSONObject();
							tempAry = new JSONArray();

							tempAry.add("소계");
							tempAry.add(utilm.str_to_dateformat_deposit(tempStr[1]));
							tempAry.add("");
							tempAry.add(dacnt);
							tempAry.add(daamt);
							tempAry.add(dccnt);
							tempAry.add(dcamt);
							tempAry.add(daamt - dcamt);

							tempObj.put("id", compareDay);
							tempObj.put("data", tempAry);

							dacnt = 0;
							daamt = 0;
							dccnt = 0;
							dcamt = 0;

							sqlAry.add(tempObj);
						}
					}

					//합계부분
					JSONObject totalObj = new JSONObject();
					JSONArray totalAry = new JSONArray();

					totalAry.add("합계");
					totalAry.add(utilm.str_to_dateformat_deposit(stime));
					totalAry.add(utilm.str_to_dateformat_deposit(etime));
					totalAry.add(tacnt);
					totalAry.add(taamt);
					totalAry.add(tccnt);
					totalAry.add(tcamt);
					totalAry.add(taamt - tcamt);

					totalObj.put("id", "total");
					totalObj.put("data", totalAry);

					sqlAry.add(totalObj);
				}

				sqlobj.put("rows", sqlAry);

			}catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}

			return sqlobj.toJSONString();

		}

		public int get_json_0000total_cnt(String tuser, String stime, String etime, String depcd, String casher, String acqcd) {
			Connection con2 = null;
			PreparedStatement stmt2 = null;
			ResultSet rs2 = null;
			
			StringBuffer pqrybuf = new StringBuffer();
			StringBuffer qrybuf = new StringBuffer();
			int icnt = 0;
			try {

				String[] userexp = tuser.split(":");
				String setdc = "";

				qrybuf.append("AND ");
				qrybuf.append("appdd>='" + stime.replace("-", "") + "' ");
				qrybuf.append("and appdd<='" + etime.replace("-", "") + "' ");
				qrybuf.append(make_query_str("AND", "add_casher", casher.trim(), "EQ"));

				if(null!=acqcd && ""!=acqcd) {
					String[] exp_acqcd = acqcd.split(",");
					String acqwh = "'" + utilm.implode("', '", exp_acqcd) + "'";		
					qrybuf.append("and ACQ_CD IN(" + acqwh + ")");
				}

				if(null!=depcd && ""!=depcd) {
					setdc = "and dep_cd='" + depcd + "'";
				}else {
					if(null!=userexp[2] && ""!=userexp[2]) {
						setdc = "and dep_cd='" + userexp[2] + "'";
					}
				}

				pqrybuf = new StringBuffer();
				pqrybuf.append("select ");
				pqrybuf.append("    count(1) MCNT ");
				pqrybuf.append("from ( ");
				pqrybuf.append("    select ");
				pqrybuf.append("        appdd ");
				pqrybuf.append("        , acq_cd ");
				pqrybuf.append("        , sum(acnt) acnt ");
				pqrybuf.append("        , sum(aamt) aamt ");
				pqrybuf.append("        , sum(ccnt) ccnt ");
				pqrybuf.append("        , sum(camt) camt ");
				pqrybuf.append("        , sum(nacnt) nacnt ");
				pqrybuf.append("        , sum(naamt) naamt ");
				pqrybuf.append("        , sum(nccnt) nccnt ");
				pqrybuf.append("        , sum(ncamt) ncamt ");
				pqrybuf.append("    from( ");
				pqrybuf.append("        select ");
				pqrybuf.append("            acq_cd ");
				pqrybuf.append("            , appdd ");
				pqrybuf.append("            , check_card ");
				pqrybuf.append("            , case when appgb='A' and check_card ='Y' then count(1) else 0 end acnt ");
				pqrybuf.append("            , case when appgb='A' and check_card ='Y' then sum(amount) else 0 end aamt ");
				pqrybuf.append("            , case when appgb='C' and check_card ='Y' then count(1) else 0 end ccnt ");
				pqrybuf.append("            , case when appgb='C' and check_card ='Y' then sum(amount) else 0 end camt ");
				pqrybuf.append("            , case when appgb='A' and check_card ='N' then count(1) else 0 end nacnt ");
				pqrybuf.append("            , case when appgb='A' and check_card ='N' then sum(amount) else 0 end naamt ");
				pqrybuf.append("            , case when appgb='C' and check_card ='N' then count(1) else 0 end nccnt ");
				pqrybuf.append("            , case when appgb='C' and check_card ='N' then sum(amount) else 0 end ncamt ");
				pqrybuf.append("        from ");
				pqrybuf.append("            " + userexp[5]);
				pqrybuf.append("        where  ");
				pqrybuf.append("            svcgb in ('CC', 'CE') ");
				pqrybuf.append("            and authcd='0000'  ");
				pqrybuf.append("            and appdd>=? ");
				pqrybuf.append("            and appdd<=? ");
				pqrybuf.append("        group by  ");
				pqrybuf.append("            appdd, acq_cd, appgb, check_card ");
				pqrybuf.append("    ) group by appdd, acq_cd ");
				pqrybuf.append(") ");

				con2 = getOraConnect();
				stmt2 = con2.prepareStatement(pqrybuf.toString());
				stmt2.setString(1, stime);
				stmt2.setString(2, etime);
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

		//2021.02.16 강원대병원 v3 - 상세내역조회 total
		@SuppressWarnings({ "unchecked", "static-access" })
		public String get_json_0204total(String tuser, String stime, String etime, String samt, String eamt, String appno, String acqcd, String pid, String mediid, String medi_cd,
				String cardno, String tid, String tradeidx, String depcd, String auth01, String auth02, String auth03, String mid) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;

			JSONObject sqlobj = new JSONObject();
			JSONArray sqlAry = new JSONArray();

			StringBuffer qrybuf = new StringBuffer();
			StringBuffer wherebuf = new StringBuffer();

			try {
				//pid - 등록번호
				//mediid - 수납자
				//medi_cd - 진료과
				//auth01 전체, auth02 승인, auth03 취소

				//tuser split
				String[] userexp = tuser.split(":");
				//acqcd split
				String[] acqcdexp = acqcd.split(",");
				//검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();

				wherebuf.append(" where svcgb in ('CC', 'CE') and authcd='0000'");
				wherebuf.append(" AND TID IN (select tid from tb_bas_tidmap where org_cd = ? ");
				setting.add(userexp[1]);
				//1. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
				if(!depcd.equals("") && depcd != null) {
					wherebuf.append(" and dep_cd = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					wherebuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				wherebuf.append(")");

				//2. STIME, ETIME SETTING
				if(!stime.equals("") && stime != null) {
					wherebuf.append(" AND APPDD >= ?");
					setting.add(stime);
				}
				if(!etime.equals("") && etime != null) {
					wherebuf.append(" AND APPDD <= ?");
					setting.add(etime);
				}

				//3. acqcd setting
				if(!acqcd.equals("") && acqcd != null) {			
					wherebuf.append(" AND ACQ_CD IN (");

					//setString 해야하는 parameter 개수만큼 물음표로 채워야 함.
					String[] paramTemp = new String[acqcdexp.length];

					for(int i = 0; i<acqcdexp.length; i++) {
						paramTemp[i] = "?";
						setting.add(acqcdexp[i]);
					}
					wherebuf.append(utilm.implode(", ", paramTemp)+")");
				}

				//4. samt, eamt setting
				if(!samt.equals("") && samt != null) {
					wherebuf.append(" AND AMOUNT >= ?");
					setting.add(samt);
				}
				if(!eamt.equals("") && eamt != null) {
					wherebuf.append(" AND AMOUNT <= ?");
					setting.add(eamt);
				}

				//5. appno setting
				if(!appno.equals("") && appno != null) {
					wherebuf.append(" AND appno = ?");
					setting.add(appno);
				}

				//6. tid setting
				if(!tid.equals("") && tid != null) {
					wherebuf.append(" AND tid = ?");
					setting.add(tid);
				}

				//7. tranidx setting
				if(!tradeidx.equals("") && tradeidx != null) {
					wherebuf.append(" AND TRANIDX = ?");
					setting.add(tradeidx);
				}

				//8. pid setting
				//ADD_CID
				if(!pid.equals("") && pid != null) {
					wherebuf.append(" AND ADD_CID = ?");
					setting.add(pid);
				}

				//9. medi_cd setting
				if(!medi_cd.equals("") && medi_cd != null) {
					wherebuf.append(" AND ADD_CD = ?");
					setting.add(medi_cd);
				}

				//10. mediid setting
				if(!mediid.equals("") && mediid != null) {
					wherebuf.append(" AND ADD_CASHER = ?");
					setting.add(mediid);
				}

				//11. cardno setting
				//강원대병원 - MEDI_GOODS 필드 사용
				if(!cardno.equals("") && cardno != null) {
					wherebuf.append(" AND MEDI_GOODS LIKE ? ");
					setting.add(cardno + "%");
				}

				//auth01 전체, auth02 승인, auth03 취소
				//12. auth setting
				if(!auth01.equals("Y")){
					if(auth02.equals("Y")){wherebuf.append(" AND APPGB = 'A'");}
					else if(auth03.equals("Y")){wherebuf.append(" AND APPGB = 'C'");}
					else if(auth02.equals("Y") && auth03.equals("Y")) {wherebuf.append(" AND APPGB IN ('A', 'C')");}
				}

				//13. mid setting
				if(!mid.equals("") && mid != null) {
					wherebuf.append(" AND MID = ? ");
					setting.add(mid);
				}

				qrybuf.append("SELECT dep_nm, term_id, term_nm, acnt, ccnt, aamt, camt, totcnt, totamt, bc, nh, kb, ss, hn, lo, hd, si from ( ");
				qrybuf.append("	select tid, sum(acnt) acnt, sum(ccnt) ccnt,sum(aamt) aamt, sum(camt) camt,sum(acnt)+sum(ccnt) totcnt, ");
				qrybuf.append("sum(aamt)-sum(camt) totamt, sum(abc)-sum(cbc) bc, sum(anh)-sum(cnh) nh, sum(akb)-sum(ckb) kb, ");
				qrybuf.append("sum(ass)-sum(css) ss, sum(ahn)-sum(chn) hn, sum(alo)-sum(clo) lo, sum(ahd)-sum(chd) hd, sum(asi)-sum(csi) si from ( ");
				qrybuf.append("SELECT tid, case when appgb='A' then sum(acnt) else 0 end acnt, case when appgb='C' then sum(ccnt) else 0 end ccnt, ");
				qrybuf.append("case when appgb='A' then sum(amount) else 0 end aamt, case when appgb='C' then sum(amount) else 0 end camt, ");
				qrybuf.append("case when appgb='A' and acq_cd in ('VC0006', '026', '01') then sum(amount) else 0 end abc, case when appgb='A' and acq_cd in ('VC0030', '018', '11') then sum(amount) else 0 end anh, ");
				qrybuf.append("case when appgb='A' and acq_cd in ('VC0001', '016', '02') then sum(amount) else 0 end akb, case when appgb='A' and acq_cd in ('VC0004', '031', '06') then sum(amount) else 0 end ass, ");
				qrybuf.append("case when appgb='A' and acq_cd in ('VC0005', '008', '03') then sum(amount) else 0 end ahn, case when appgb='A' and acq_cd in ('VC0003', '047', '33') then sum(amount) else 0 end alo, ");
				qrybuf.append("case when appgb='A' and acq_cd in ('VC0002', '027', '08') then sum(amount) else 0 end ahd, case when appgb='A' and acq_cd in ('VC0007', '029', '07') then sum(amount) else 0 end asi, ");
				qrybuf.append("case when appgb='C' and acq_cd in ('VC0006', '026', '01') then sum(amount) else 0 end cbc, case when appgb='C' and acq_cd in ('VC0030', '018', '11') then sum(amount) else 0 end cnh, ");
				qrybuf.append("case when appgb='C' and acq_cd in ('VC0001', '016', '02') then sum(amount) else 0 end ckb, case when appgb='C' and acq_cd in ('VC0004', '031', '06') then sum(amount) else 0 end css, ");
				qrybuf.append("case when appgb='C' and acq_cd in ('VC0005', '008', '03') then sum(amount) else 0 end chn, case when appgb='C' and acq_cd in ('VC0003', '047', '33') then sum(amount) else 0 end clo, ");
				qrybuf.append("case when appgb='C' and acq_cd in ('VC0002', '027', '08') then sum(amount) else 0 end chd, case when appgb='C' and acq_cd in ('VC0007', '029', '07') then sum(amount) else 0 end csi ");
				qrybuf.append("from ( ");
				qrybuf.append("select tid, acq_cd, appgb, sum(amount) amount, ");
				qrybuf.append("case when appgb='A' then count(1) else 0 end acnt, case when appgb='C' then count(1) else 0 end ccnt from ");
				qrybuf.append(userexp[5]);
				qrybuf.append(wherebuf.toString());
				qrybuf.append(" group by tid, appgb, acq_cd ) group by tid, appgb, acq_cd ) group by tid )t1 ");

				//left outer join
				qrybuf.append("left outer join( select dep_cd, term_nm, term_id from tb_bas_tidmst where org_cd = ? ");
				setting.add(userexp[1]);
				if(!depcd.equals("") && depcd != null) {
					qrybuf.append(" and dep_cd = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				qrybuf.append(")t3 on(t1.tid=t3.term_id) ");

				qrybuf.append("left outer join( select dep_nm, dep_cd from tb_bas_depart where org_cd = ? ");
				setting.add(userexp[1]);
				if(!depcd.equals("") && depcd != null) {
					qrybuf.append(" and dep_cd = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				qrybuf.append(")t4 on(t3.dep_cd=t4.dep_cd) ");

				//디버깅용
				utilm.debug_sql(qrybuf, setting);

				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				for(int k = 0; k<setting.size(); k++) {
					stmt.setString((k+1), setting.get(k));
				}

				rs = stmt.executeQuery();

				//총합계->사업부순
				//순번, 사업부, 승인건수, 승인금액, 취소건수, 취소금액, 총건수, 합계금액, 비씨, 국민, 하나, 삼성, 신한, 현대, 롯데, 농협
				//총합계는 계산 다 하고 나중에 첫번째 index에 넣어줄 것.
				long taamt = 0, tcamt = 0, ttamt = 0;
				int tacnt = 0, tccnt = 0, ttcnt = 0;
				long tbc = 0, tkb = 0, thn = 0, tss = 0, tsi = 0, thd = 0, tlo = 0, tnh = 0;
				int icnt = 1;
				while(rs.next()) {
					//System.out.println(rs);
					JSONObject tempObj = new JSONObject();
					JSONArray tempAry = new JSONArray();

					int acnt = Integer.parseInt(utilm.checkNumberData(rs.getString("acnt")));
					int ccnt = Integer.parseInt(utilm.checkNumberData(rs.getString("ccnt")));
					int totcnt = acnt + ccnt;

					long aamt = Long.parseLong(utilm.checkNumberData(rs.getString("aamt")));
					long camt = Long.parseLong(utilm.checkNumberData(rs.getString("camt")));
					long totamt = aamt - camt;

					long bc = Long.parseLong(utilm.checkNumberData(rs.getString("bc")));
					long kb = Long.parseLong(utilm.checkNumberData(rs.getString("kb")));
					long hn = Long.parseLong(utilm.checkNumberData(rs.getString("hn")));
					long ss = Long.parseLong(utilm.checkNumberData(rs.getString("ss")));
					long si = Long.parseLong(utilm.checkNumberData(rs.getString("si")));
					long hd = Long.parseLong(utilm.checkNumberData(rs.getString("hd")));
					long lo = Long.parseLong(utilm.checkNumberData(rs.getString("lo")));
					long nh = Long.parseLong(utilm.checkNumberData(rs.getString("nh")));

					taamt += aamt;
					tcamt += camt;
					ttamt += totamt;
					tacnt += acnt;
					tccnt += ccnt;
					ttcnt += totcnt;
					tbc += bc;
					tkb += kb;
					thn += hn;
					tss += ss;
					tsi += si;
					thd += hd;
					tlo += lo;
					tnh += nh;

					tempAry.add(icnt);
					tempAry.add(rs.getString("dep_nm") + "(" + rs.getString("term_nm") + ":" + rs.getString("term_id") + ")");
					tempAry.add(acnt);
					tempAry.add(aamt);
					tempAry.add(ccnt);
					tempAry.add(camt);
					tempAry.add(totcnt);
					tempAry.add(totamt);
					tempAry.add(bc);
					tempAry.add(kb);
					tempAry.add(hn);
					tempAry.add(ss);
					tempAry.add(si);
					tempAry.add(hd);
					tempAry.add(lo);
					tempAry.add(nh);

					icnt++;

					tempObj.put("id", icnt);
					tempObj.put("data", tempAry);

					sqlAry.add(tempObj);
				}

				//합계부분
				JSONObject tempObj = new JSONObject();
				JSONArray tempAry = new JSONArray();
				
				tempAry.add("합계");
				tempAry.add("");
				tempAry.add(tacnt);
				tempAry.add(taamt);
				tempAry.add(tccnt);
				tempAry.add(tcamt);
				tempAry.add(ttcnt);
				tempAry.add(ttamt);
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
				setOraClose(con,stmt,rs);
			}
			return sqlobj.toJSONString();
		} 

		//2021.01.29 카드사별조회 엑셀다운로드 - total
		@SuppressWarnings("unchecked")
		public String get_json_0201total_excel(String tuser, String stime, String etime, String samt, String eamt, String appno, String tid, String mid, String acqcd, String depcd) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			StringBuffer qrybuf = new StringBuffer();
			StringBuffer wherebuf = new StringBuffer();
			JSONObject sqlobj = new JSONObject();
			JSONArray objAry = new JSONArray();

			try {
				//tuser split
				String[] userexp = tuser.split(":");
				//acqcd split
				String[] acqcdexp = acqcd.split(",");
				//검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();

				//wherebuf = WHERE SVCGB IN ('CC', 'CE')  AND AUTHCD='0000' AND TID IN (select tid from tb_bas_tidmap  where org_cd='OR008' AND dep_cd='DP30101')  AND APPDD>='20200201' AND APPDD<='20200201'
				wherebuf.append(" WHERE SVCGB IN ('CC', 'CE') AND AUTHCD='0000' ");
				wherebuf.append("AND TID IN (select tid from tb_bas_tidmap where org_cd=?");
				setting.add(userexp[1]);

				//1. TID LIST중 LOGIN SESSION에 DEP_CD 설정되어 있을 때
				//1. 검색중 사업부 선택이 있을 때
				if(!depcd.equals("") && depcd != null) {
					wherebuf.append(" and dep_cd = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					wherebuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				wherebuf.append(")");

				//2. STIME, ETIME SETTING
				if(!stime.equals("") && stime != null) {
					wherebuf.append(" AND APPDD >= ?");
					setting.add(stime);
				}
				if(!etime.equals("") && etime != null) {
					wherebuf.append(" AND APPDD <= ?");
					setting.add(etime);
				}

				//3. acqcd setting
				if(!acqcd.equals("") && acqcd != null) {
					wherebuf.append(" AND ACQ_CD IN (?, ?, ?)");
					for(int i = 0; i<acqcdexp.length; i++) {
						setting.add(acqcdexp[i]);
					}
				}

				//4. samt, eamt setting
				if(!samt.equals("") && samt != null) {
					wherebuf.append(" AND AMOUNT >= ?");
					setting.add(samt);
				}
				if(!eamt.equals("") && eamt != null) {
					wherebuf.append(" AND AMOUNT <= ?");
					setting.add(eamt);
				}

				//5. appno setting
				if(!appno.equals("") && appno != null) {
					wherebuf.append(" AND appno = ?");
					setting.add(appno);
				}

				//6. tid setting
				if(!tid.equals("") && tid != null) {
					wherebuf.append(" AND tid = ?");
					setting.add(tid);
				}

				qrybuf.append("Select acnt, ccnt, aamt, camt from ( ");
				qrybuf.append("Select sum(acnt) acnt, sum(ccnt) ccnt, sum(aamt) aamt, sum(camt) camt from ( ");
				qrybuf.append("Select CASE WHEN APPGB='A' THEN COUNT(1) ELSE 0 END ACNT, ");
				qrybuf.append("CASE WHEN APPGB='A' THEN SUM(AMOUNT) ELSE 0 END AAMT, ");
				qrybuf.append("CASE WHEN APPGB='C' THEN COUNT(1) ELSE 0 END CCNT, ");
				qrybuf.append("CASE WHEN APPGB='C' THEN SUM(AMOUNT) ELSE 0 END CAMT FROM ");
				qrybuf.append(userexp[5]);
				qrybuf.append(wherebuf.toString());
				qrybuf.append(" GROUP BY APPGB ) )");

				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				for(int k = 0; k < setting.size(); k++) {
					stmt.setString((k+1), setting.get(k));
				}
				rs = stmt.executeQuery();

				int icnt = 1;
				while(rs.next()) {
					//row별로 데이터 JSONARRAY형태로 묶어서 보냄
					JSONObject tempObj = new JSONObject();
					String acnt = rs.getString("acnt");
					String ccnt = rs.getString("ccnt");
					String aamt = rs.getString("aamt");
					String camt = rs.getString("camt");
					//합계부분
					int tcnt = Integer.parseInt(acnt) - Integer.parseInt(ccnt);
					int tamt = Integer.parseInt(aamt) - Integer.parseInt(camt);

					tempObj.put("ACNT", acnt);
					tempObj.put("AAMT", aamt);
					tempObj.put("CCNT", ccnt);
					tempObj.put("CAMT", camt);
					tempObj.put("TOTCNT", tcnt);
					tempObj.put("TOTAMT", tamt);

					objAry.add(tempObj);
					icnt++;
				}

				sqlobj.put("ITEMS", objAry);

			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}


			return sqlobj.toJSONString();

		}


		//2021.01.29 카드사별조회 엑셀다운로드 - item
		@SuppressWarnings("unchecked")
		public String get_json_0201item_excel(String tuser, String stime, String etime, String samt, String eamt, String appno, String tid, String mid, String acqcd, String depcd) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			StringBuffer qrybuf = new StringBuffer();
			StringBuffer wherebuf = new StringBuffer();
			JSONObject sqlobj = new JSONObject();
			JSONArray objAry = new JSONArray();

			try {
				//tuser split
				String[] userexp = tuser.split(":");
				//acqcd split
				String[] acqcdexp = acqcd.split(",");
				//검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();

				//wherebuf = WHERE SVCGB IN ('CC', 'CE')  AND AUTHCD='0000' AND TID IN (select tid from tb_bas_tidmap  where org_cd='OR008' AND dep_cd='DP30101')  AND APPDD>='20200201' AND APPDD<='20200201'
				wherebuf.append(" WHERE SVCGB IN ('CC', 'CE') AND AUTHCD = '0000' ");
				wherebuf.append("AND TID IN (select tid from tb_bas_tidmap where org_cd = ?");
				setting.add(userexp[1]);

				//1. TID LIST중 LOGIN SESSION에 DEP_CD 설정되어 있을 때
				//1. 검색중 사업부 선택이 있을 때
				if(!depcd.equals("") && depcd != null) {
					wherebuf.append(" and dep_cd = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					wherebuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				wherebuf.append(")");

				//2. STIME, ETIME SETTING
				if(!stime.equals("") && stime != null) {
					wherebuf.append(" AND APPDD >= ?");
					setting.add(stime);
				}
				if(!etime.equals("") && etime != null) {
					wherebuf.append(" AND APPDD <= ?");
					setting.add(etime);
				}

				//3. acqcd setting
				if(!acqcd.equals("") && acqcd != null) {
					wherebuf.append(" AND ACQ_CD IN (?, ?, ?)");
					for(int i = 0; i<acqcdexp.length; i++) {
						setting.add(acqcdexp[i]);
					}
				}

				//4. samt, eamt setting
				if(!samt.equals("") && samt != null) {
					wherebuf.append(" AND AMOUNT >= ?");
					setting.add(samt);
				}
				if(!eamt.equals("") && eamt != null) {
					wherebuf.append(" AND AMOUNT <= ?");
					setting.add(eamt);
				}

				//5. appno setting
				if(!appno.equals("") && appno != null) {
					wherebuf.append(" AND appno = ?");
					setting.add(appno);
				}

				//6. tid setting
				if(!tid.equals("") && tid != null) {
					wherebuf.append(" AND tid = ?");
					setting.add(tid);
				} 


				qrybuf.append("SELECT DEP_NM, TID, DEP_NM, TERM_NM, PUR_NM, MID, ACQ_CD, ACNT, CCNT, AAMT, CAMT FROM (");
				qrybuf.append("SELECT TID, MID, ACQ_CD, SUM(ACNT) ACNT, SUM(CCNT) CCNT, SUM(AAMT) AAMT, SUM(CAMT) CAMT FROM( ");
				qrybuf.append("SELECT TID, MID, ACQ_CD, CASE WHEN APPGB='A' THEN COUNT(1) ELSE 0 END ACNT, CASE WHEN APPGB='A' "
						+ "THEN SUM(AMOUNT) ELSE 0 END AAMT, CASE WHEN APPGB='C' THEN COUNT(1) ELSE 0 END CCNT, CASE WHEN APPGB='C' THEN SUM(AMOUNT) ELSE 0 END CAMT FROM ");
				qrybuf.append(userexp[5]);
				qrybuf.append(wherebuf.toString());
				qrybuf.append(" GROUP BY TID, MID, ACQ_CD, APPGB ) ");
				qrybuf.append(" GROUP BY TID, MID, ACQ_CD )T1 ");
				qrybuf.append("LEFT OUTER JOIN(SELECT PUR_NM, PUR_CD, PUR_SORT,  PUR_OCD, PUR_KIS FROM TB_BAS_PURINFO WHERE nvl(PUR_USE, 'N')='Y')T2 ON(T1.ACQ_CD=T2.PUR_CD OR T1.ACQ_CD=T2.PUR_KIS) ");
				qrybuf.append("LEFT OUTER JOIN(SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD= ? )T3 ON(T1.TID=T3.TERM_ID) ");
				qrybuf.append("LEFT OUTER JOIN(SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD= ? )T4 ON(T3.DEP_CD=T4.DEP_CD) ");
				qrybuf.append("ORDER BY TID, PUR_NM ASC"); 

				//left join org_cd setting
				setting.add(userexp[1]);
				setting.add(userexp[1]);

				System.out.println("query check :: ");
				System.out.println(qrybuf.toString());
				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				System.out.println("parameter check - ");
				for(int k = 0; k < setting.size(); k++) {
					stmt.setString((k+1), setting.get(k));
					System.out.println((k+1) +" : "+setting.get(k));
				}
				System.out.println("========================");
				rs = stmt.executeQuery();

				int icnt = 1;
				while(rs.next()) {
					//row별로 데이터 JSONARRAY형태로 묶어서 보냄
					JSONObject tempObj = new JSONObject();

					String dep_nm = rs.getString("DEP_NM");
					String term_id = rs.getString("TID");
					String term_nm = rs.getString("TERM_NM");
					String pur_nm = rs.getString("PUR_NM");
					String mer_nm = rs.getString("MID");
					String acq_cd = rs.getString("ACQ_CD");
					String acnt = rs.getString("ACNT");
					String ccnt = rs.getString("CCNT");
					String aamt = rs.getString("AAMT");
					String camt = rs.getString("CAMT");

					tempObj.put("id", Integer.toString(icnt));
					tempObj.put("DEP_NM", dep_nm);
					tempObj.put("TID", term_id);
					tempObj.put("TERM_NM", term_nm);
					tempObj.put("PUR_NM", pur_nm);
					tempObj.put("MID", mer_nm);
					tempObj.put("ACQ_CD", acq_cd);
					tempObj.put("ACNT", acnt);
					tempObj.put("CCNT", ccnt);
					tempObj.put("AAMT", aamt);
					tempObj.put("CAMT", camt);

					objAry.add(tempObj);
					icnt++;
				}

				sqlobj.put("ITEMS", objAry);


			}catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}

			return sqlobj.toJSONString();
		}

		@SuppressWarnings("unchecked")
		public String get_json_0204total_excel(String tuser, String stime, String etime, String tid, String appno, String samt, String eamt, String mid, String pid, String tridx, String depcd, String casher, String cardno, String auth01, String auth02, String auth03) {
			Connection con2 = null;
			PreparedStatement stmt2 = null;
			ResultSet rs2 = null;

			StringBuffer pqrybuf = new StringBuffer();
			StringBuffer qrybuf = new StringBuffer();
			JSONObject jrtnobj = new JSONObject();
			try {

				String[] userexp = tuser.split(":");
				String setdc = "";

				qrybuf.append("AND ");
				qrybuf.append("appdd>='" + stime.replace("-", "") + "' ");
				qrybuf.append("and appdd<='" + etime.replace("-", "") + "' ");
				qrybuf.append(make_query_str("AND", "tid", tid.trim(), "EQ"));
				qrybuf.append(make_query_str("AND", "appno", appno.trim(), "EQ"));
				qrybuf.append(make_query_str("AND", "amount", samt.trim().replace(",", ""), "BEQ"));
				qrybuf.append(make_query_str("AND", "amount", eamt.trim().replace(",", ""), "SEQ"));
				qrybuf.append(make_query_str("AND", "mid", mid.trim(), "EQ"));
				qrybuf.append(make_query_str("AND", "add_recp", pid.trim(), "EQ"));
				qrybuf.append(make_query_str("AND", "tranidx", tridx.trim(), "EQ"));
				qrybuf.append(make_query_str("AND", "add_casher", casher.trim(), "EQ"));
				if(cardno.length()>10) {
					qrybuf.append(make_query_str("AND", "cardno", encm.seed_enc_str(cardno.trim()), "EQ"));
				}

				if(!auth01.equals("Y") && !auth01.equals("")) {
					String[] imp_auth = new String[2];
					if(null != auth02 && "" != auth02 && auth02.equals("Y")) {
						imp_auth[0] = "'A'"; 
					}

					if(null != auth03 && "" != auth03 && auth03.equals("Y")) {
						imp_auth[1] = "'C'"; 
					}
					qrybuf.append("and appgb in (" + utilm.implode(",", imp_auth) + ") ");

				}

				if(null!=depcd && ""!=depcd) {
					setdc = "and dep_cd='" + depcd + "'";
				}else {
					if(null!=userexp[2] && ""!=userexp[2]) {
						setdc = "and dep_cd='" + userexp[2] + "'";
					}
				}

				pqrybuf = new StringBuffer();
				pqrybuf.append("SELECT ");
				pqrybuf.append("    DEP_NM, TERM_ID, TERM_NM, ACNT, CCNT, AAMT, CAMT, TOTCNT, TOTAMT ");
				pqrybuf.append("    ,BC, NH, KB, SS, HN, LO, HD, SI ");
				pqrybuf.append("FROM( ");
				pqrybuf.append("    SELECT  ");
				pqrybuf.append("        TID ");
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
				pqrybuf.append("    FROM( ");
				pqrybuf.append("        SELECT ");
				pqrybuf.append("            TID ");
				pqrybuf.append("            ,CASE WHEN APPGB='A' THEN COUNT(1) ELSE 0 END ACNT ");
				pqrybuf.append("            ,CASE WHEN APPGB='C' THEN COUNT(1) ELSE 0 END CCNT ");
				pqrybuf.append("            ,CASE WHEN APPGB='A' THEN SUM(AMOUNT) ELSE 0 END AAMT ");
				pqrybuf.append("            ,CASE WHEN APPGB='C' THEN SUM(AMOUNT) ELSE 0 END CAMT ");
				pqrybuf.append("            ,CASE WHEN APPGB='A' AND ACQ_CD IN ('VC0006', '026') THEN SUM(AMOUNT) ELSE 0 END ABC ");
				pqrybuf.append("            ,CASE WHEN APPGB='A' AND ACQ_CD IN ('VC0030', '018') THEN SUM(AMOUNT) ELSE 0 END ANH ");
				pqrybuf.append("            ,CASE WHEN APPGB='A' AND ACQ_CD IN ('VC0001', '016') THEN SUM(AMOUNT) ELSE 0 END AKB ");
				pqrybuf.append("            ,CASE WHEN APPGB='A' AND ACQ_CD IN ('VC0004', '031') THEN SUM(AMOUNT) ELSE 0 END ASS ");
				pqrybuf.append("            ,CASE WHEN APPGB='A' AND ACQ_CD IN ('VC0005', '008') THEN SUM(AMOUNT) ELSE 0 END AHN ");
				pqrybuf.append("            ,CASE WHEN APPGB='A' AND ACQ_CD IN ('VC0003', '047') THEN SUM(AMOUNT) ELSE 0 END ALO ");
				pqrybuf.append("            ,CASE WHEN APPGB='A' AND ACQ_CD IN ('VC0002', '027') THEN SUM(AMOUNT) ELSE 0 END AHD ");
				pqrybuf.append("            ,CASE WHEN APPGB='A' AND ACQ_CD IN ('VC0007', '029') THEN SUM(AMOUNT) ELSE 0 END ASI ");
				pqrybuf.append("            ,CASE WHEN APPGB='C' AND ACQ_CD IN ('VC0006', '026') THEN SUM(AMOUNT) ELSE 0 END CBC ");
				pqrybuf.append("            ,CASE WHEN APPGB='C' AND ACQ_CD IN ('VC0030', '018') THEN SUM(AMOUNT) ELSE 0 END CNH ");
				pqrybuf.append("            ,CASE WHEN APPGB='C' AND ACQ_CD IN ('VC0001', '016') THEN SUM(AMOUNT) ELSE 0 END CKB ");
				pqrybuf.append("            ,CASE WHEN APPGB='C' AND ACQ_CD IN ('VC0004', '031') THEN SUM(AMOUNT) ELSE 0 END CSS ");
				pqrybuf.append("            ,CASE WHEN APPGB='C' AND ACQ_CD IN ('VC0005', '008') THEN SUM(AMOUNT) ELSE 0 END CHN ");
				pqrybuf.append("            ,CASE WHEN APPGB='C' AND ACQ_CD IN ('VC0003', '047') THEN SUM(AMOUNT) ELSE 0 END CLO ");
				pqrybuf.append("            ,CASE WHEN APPGB='C' AND ACQ_CD IN ('VC0002', '027') THEN SUM(AMOUNT) ELSE 0 END CHD ");
				pqrybuf.append("            ,CASE WHEN APPGB='C' AND ACQ_CD IN ('VC0007', '029') THEN SUM(AMOUNT) ELSE 0 END CSI ");
				pqrybuf.append("        FROM ( ");
				pqrybuf.append("            SELECT ");
				pqrybuf.append("                TID, ACQ_CD, APPGB, SUM(AMOUNT) AMOUNT ");
				pqrybuf.append("            FROM ");
				pqrybuf.append("                GLOB_MNG_ICVAN ");
				pqrybuf.append("            WHERE SVCGB IN ('CC', 'CE')  AND AUTHCD='0000' AND TID IN (select tid from tb_bas_tidmap  where org_cd='" + userexp[1] + "' " + setdc + ") ");
				pqrybuf.append("             " + qrybuf.toString());
				pqrybuf.append("            GROUP BY TID, APPGB, ACQ_CD ");
				pqrybuf.append("        ) group by TID, APPGB, ACQ_CD ");
				pqrybuf.append("    ) GROUP BY TID ");
				pqrybuf.append(")T1 ");
				pqrybuf.append("LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE org_cd='" + userexp[1] + "')T3 ON(T1.TID=T3.TERM_ID) ");
				pqrybuf.append("LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE org_cd='" + userexp[1] + "')T4 ON(T3.DEP_CD=T4.DEP_CD)");

				try {
					con2 = getOraConnect();
					stmt2 = con2.prepareStatement(pqrybuf.toString());
					rs2 = stmt2.executeQuery();

					//				trans_seed_manager.seed_dec_card(rs.getString("CARDNO").trim())
					JSONArray jsonarr = new JSONArray();
					int icnt = 1;
					while(rs2.next()) {
						JSONObject jsonobj = new JSONObject();
						jsonobj.put("SEQNO", Integer.toString(icnt));
						jsonobj.put("DEP_NM", rs2.getString("DEP_NM") + "[" + rs2.getString("TERM_NM") + ":" + rs2.getString("TERM_ID") + "]");
						jsonobj.put("ACNT", rs2.getString("ACNT"));
						jsonobj.put("AAMT", rs2.getString("AAMT"));
						jsonobj.put("CCNT", rs2.getString("CCNT"));
						jsonobj.put("CAMT", rs2.getString("CAMT"));
						jsonobj.put("TOTCNT", rs2.getString("TOTCNT"));
						jsonobj.put("TOTAMT", rs2.getString("TOTAMT"));
						jsonobj.put("BC", rs2.getString("BC"));
						jsonobj.put("NH", rs2.getString("NH"));
						jsonobj.put("KB", rs2.getString("KB"));
						jsonobj.put("SS", rs2.getString("SS"));
						jsonobj.put("HN", rs2.getString("HN"));
						jsonobj.put("LO", rs2.getString("LO"));
						jsonobj.put("HD", rs2.getString("HD"));
						jsonobj.put("SI", rs2.getString("SI"));

						jsonarr.add(jsonobj);
						icnt++;
					}

					jrtnobj.put("ITEMS", jsonarr);

				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

			}catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con2,stmt2,rs2);
			}
			return jrtnobj.toJSONString();
		}

		@SuppressWarnings("unchecked")
		public String get_json_0204item_excel(String tuser, String stime, String etime, String tid, String appno, String samt, String eamt, 
				String mid, String pid, String tridx, String depcd, String casher, String cardno, String auth01, String auth02, String auth03) {
			Connection con2 = null;
			PreparedStatement stmt2 = null;
			ResultSet rs2 = null;

			JSONObject jrtnobj = new JSONObject();
			StringBuffer qrybuf = new StringBuffer();
			StringBuffer pqrybuf = new StringBuffer();

			try {

				String[] userexp = tuser.split(":");
				String setdc = "";

				qrybuf.append("AND ");
				qrybuf.append("appdd>='" + stime.replace("-", "") + "' ");
				qrybuf.append("and appdd<='" + etime.replace("-", "") + "' ");
				qrybuf.append(make_query_str("AND", "tid", tid.trim(), "EQ"));
				qrybuf.append(make_query_str("AND", "appno", appno.trim(), "EQ"));
				qrybuf.append(make_query_str("AND", "amount", samt.trim().replace(",", ""), "BEQ"));
				qrybuf.append(make_query_str("AND", "amount", eamt.trim().replace(",", ""), "SEQ"));
				qrybuf.append(make_query_str("AND", "mid", mid.trim(), "EQ"));
				qrybuf.append(make_query_str("AND", "add_recp", pid.trim(), "EQ"));
				qrybuf.append(make_query_str("AND", "tranidx", tridx.trim(), "EQ"));
				qrybuf.append(make_query_str("AND", "add_casher", casher.trim(), "EQ"));
				if(cardno.length()>10) {
					qrybuf.append(make_query_str("AND", "cardno", encm.seed_enc_str(cardno.trim()), "EQ"));
				}

				if(!auth01.equals("Y") && !auth01.equals("")) {
					String[] imp_auth = new String[2];
					if(null != auth02 && "" != auth02 && auth02.equals("Y")) {
						imp_auth[0] = "'A'"; 
					}

					if(null != auth03 && "" != auth03 && auth03.equals("Y")) {
						imp_auth[1] = "'C'"; 
					}

					qrybuf.append("and appgb in (" + utilm.implode(",", imp_auth) + ") ");
				}

				if(null!=depcd && ""!=depcd) {
					setdc = "and dep_cd='" + depcd + "'";
				}else {
					if(null!=userexp[2] && ""!=userexp[2]) {
						setdc = "and dep_cd='" + userexp[2] + "'";
					}
				}

				pqrybuf.append("SELECT ");
				pqrybuf.append("		DEP_NM, TID, TERM_NM, PUR_NM ");
				pqrybuf.append("		, SEQNO, APPGB, TID, MID, ACQ_CD, T1.APPDD, APPTM");
				pqrybuf.append("		, APPNO, CARDNO, T1.TRANIDX, HALBU, AMOUNT, OAPPDD, OAPPNO");
				pqrybuf.append("		, ADD_CID, ADD_CD, ADD_GB, ADD_CASHER, EXP_DD, REQ_DD, REG_DD");
				pqrybuf.append("		, RSC_CD, RTN_CD, AUTHCD, AUTHMSG");
				pqrybuf.append("		, CHECK_CARD, OVSEA_CARD, SIGNCHK, DDCGB ");
				pqrybuf.append("FROM( ");
				pqrybuf.append("		SELECT ");
				pqrybuf.append("			SEQNO, APPGB, TID, MID, ACQ_CD, APPDD, APPTM");
				pqrybuf.append("			, APPNO, CARDNO, TRANIDX, HALBU, AMOUNT, OAPPDD, OAPPNO");
				pqrybuf.append("			, ADD_CID, ADD_CD, ADD_GB, ADD_CASHER, AUTHCD, AUTHMSG");
				pqrybuf.append("			, CHECK_CARD, OVSEA_CARD, SIGNCHK, DDCGB");
				pqrybuf.append("		FROM ");
				pqrybuf.append("			GLOB_MNG_ICVAN ");
				pqrybuf.append("		WHERE SVCGB IN ('CC', 'CE')  AND AUTHCD='0000' AND TID IN (select tid from tb_bas_tidmap  where org_cd='" + userexp[1] + "' " + setdc + ") ");
				pqrybuf.append("			" + qrybuf.toString());
				pqrybuf.append(")T1 ");
				pqrybuf.append("LEFT OUTER JOIN( SELECT EXP_DD, REQ_DD, REG_DD, APP_DD, TRANIDX, RSC_CD, RTN_CD FROM " + userexp[6] + ")T2 ON(T1.APPDD=T2.APP_DD AND T1.TRANIDX=T2.TRANIDX) ");
				pqrybuf.append("LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE org_cd='" + userexp[1] + "')T3 ON(T1.TID=T3.TERM_ID) ");
				pqrybuf.append("LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE org_cd='" + userexp[1] + "')T4 ON(T3.DEP_CD=T4.DEP_CD) ");
				pqrybuf.append("LEFT OUTER JOIN( SELECT PUR_NM, PUR_CD FROM TB_BAS_PURINFO)T5 ON(T1.ACQ_CD=T5.PUR_CD)");

				//System.out.println(pqrybuf.toString());

				con2 = getOraConnect();
				stmt2 = con2.prepareStatement(pqrybuf.toString());
				rs2 = stmt2.executeQuery();

				JSONArray jsonarr = new JSONArray();

				int icnt = 1;
				while(rs2.next()) {
					JSONObject jsonobj = new JSONObject();

					String cardno_dec = utilm.cardno_masking(trans_seed_manager.seed_dec_card(rs2.getString("CARDNO").trim()));
					String cardtp_kor = utilm.strcardtype(rs2.getString("CHECK_CARD").trim());
					String signgb_kor = utilm.strsigngb(rs2.getString("SIGNCHK").trim());

					jsonobj.put("SEQNO", Integer.toString(icnt));
					jsonobj.put("DEP_NM", rs2.getString("DEP_NM"));
					jsonobj.put("TERM_NM", rs2.getString("TERM_NM"));
					jsonobj.put("TID", rs2.getString("TID"));
					jsonobj.put("MID", rs2.getString("MID"));
					jsonobj.put("PUR_NM", rs2.getString("PUR_NM"));
					jsonobj.put("TRANSTAT", "");
					jsonobj.put("APPDD", utilm.str_to_dateformat(rs2.getString("APPDD")));
					jsonobj.put("APPTM", utilm.str_to_timeformat(rs2.getString("APPTM")));
					jsonobj.put("CANDATE", "");
					jsonobj.put("OAPPDD", utilm.str_to_dateformat(rs2.getString("OAPPDD")));
					jsonobj.put("APPNO", rs2.getString("APPNO"));
					jsonobj.put("APPGB", utilm.set_appgb_to_kor(rs2.getString("APPGB")));
					jsonobj.put("CARDNO", cardno_dec);
					jsonobj.put("AMOUNT", rs2.getString("AMOUNT"));
					jsonobj.put("HALBU", rs2.getString("HALBU"));
					jsonobj.put("CARDTP", cardtp_kor); //카드종류
					jsonobj.put("LINEGB", ""); //회선구분
					jsonobj.put("SIGNGB", signgb_kor); //서명유무
					jsonobj.put("AUTHCD", "0000"); //매출코드
					jsonobj.put("REQ_DD", utilm.str_to_dateformat(rs2.getString("REQ_DD"))); //매입요청일자
					jsonobj.put("RES_DD", utilm.str_to_dateformat(rs2.getString("REQ_DD"))); //매입접수일자
					jsonobj.put("REG_DD", utilm.str_to_dateformat(rs2.getString("REG_DD"))); //매입응답일
					jsonobj.put("RTN_CD", utilm.deposit_rst_to_kor(rs2.getString("RTN_CD"))); //매입결과
					jsonobj.put("EXP_DD", utilm.str_to_dateformat(rs2.getString("EXP_DD"))); //입금예정일
					jsonobj.put("ADD_CID", rs2.getString("ADD_CID")); //진료번호
					jsonobj.put("ADD_GB", rs2.getString("ADD_GB")); //진료구분
					jsonobj.put("ADD_CASHER", rs2.getString("ADD_CASHER")); //수납자
					jsonobj.put("TRANIDX", rs2.getString("TRANIDX")); //거래코드
					jsonobj.put("AUTHMSG", rs2.getString("AUTHMSG")); //카드사응답내용

					jsonarr.add(jsonobj);
					icnt++;
				}

				jrtnobj.put("ITEMS", jsonarr);

			}catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con2,stmt2,rs2);
			}
			return jrtnobj.toJSONString();
		}


		//2021.02.12 강원대병원 v3 - 카드사별조회 total
		@SuppressWarnings({ "static-access", "unchecked" })
		public String get_json_0201total(String tuser, String stime, String etime, String samt, String eamt, String appno, String tid, String mid, String acqcd, String depcd) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			JSONObject jrtnobj = new JSONObject();
			JSONArray sqlAry = new JSONArray();

			StringBuffer qrybuf = new StringBuffer();
			StringBuffer wherebuf = new StringBuffer();

			try {

				//tuser split
				String[] userexp = tuser.split(":");
				//acqcd split
				String[] acqcdexp = acqcd.split(",");
				//검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();

				//WHERE SVCGB IN ('CC', 'CE') AND AUTHCD ='0000' 
				//AND TID IN (select tid from tb_bas_tidmap $USER_AUTH)  $ADDWHERE
				wherebuf.append(" WHERE SVCGB IN ('CC', 'CE') AND AUTHCD = '0000'");
				wherebuf.append(" AND TID IN (SELECT TID FROM TB_BAS_TIDMAP WHERE ORG_CD = ? ");
				setting.add(userexp[1]);

				//1. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
				if(!depcd.equals("") && depcd != null) {
					wherebuf.append(" and dep_cd = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					wherebuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				wherebuf.append(")");

				//2. STIME, ETIME SETTING
				if(!stime.equals("") && stime != null) {
					wherebuf.append(" AND APPDD >= ?");
					setting.add(stime);
				}
				if(!etime.equals("") && etime != null) {
					wherebuf.append(" AND APPDD <= ?");
					setting.add(etime);
				}

				//3. acqcd setting
				if(!acqcd.equals("") && acqcd != null) {			
					wherebuf.append(" AND ACQ_CD IN (");

					//setString 해야하는 parameter 개수만큼 물음표로 채워야 함.
					String[] paramTemp = new String[acqcdexp.length];

					for(int i = 0; i<acqcdexp.length; i++) {
						paramTemp[i] = "?";
						setting.add(acqcdexp[i]);
					}
					wherebuf.append(utilm.implode(", ", paramTemp)+")");
				}

				//4. samt, eamt setting
				if(!samt.equals("") && samt != null) {
					wherebuf.append(" AND AMOUNT >= ?");
					setting.add(samt);
				}
				if(!eamt.equals("") && eamt != null) {
					wherebuf.append(" AND AMOUNT <= ?");
					setting.add(eamt);
				}

				//5. appno setting
				if(!appno.equals("") && appno != null) {
					wherebuf.append(" AND appno = ?");
					setting.add(appno);
				}

				//6. tid setting
				if(!tid.equals("") && tid != null) {
					wherebuf.append(" AND tid = ?");
					setting.add(tid);
				}

				//7. mid setting
				if(!mid.equals("") && mid != null) {
					wherebuf.append(" AND mid = ?");
					setting.add(mid);
				}

				/*
				 * SELECT 
					ACNT, CCNT, AAMT, CAMT
					FROM(
						SELECT
							SUM(ACNT) ACNT, SUM(CCNT) CCNT, SUM(AAMT) AAMT, SUM(CAMT) CAMT
						FROM(
							SELECT
								CASE WHEN APPGB='A' THEN COUNT(1) ELSE 0 END ACNT,
								CASE WHEN APPGB='A' THEN SUM(AMOUNT) ELSE 0 END AAMT,
								CASE WHEN APPGB='C' THEN COUNT(1) ELSE 0 END CCNT,
								CASE WHEN APPGB='C' THEN SUM(AMOUNT) ELSE 0 END CAMT
							FROM 
								$UserExpAuth[5]
								$SET_WHERE
							GROUP BY APPGB
						)
					)T1
				 */

				qrybuf.append("SELECT ACNT, CCNT, AAMT, CAMT FROM ( ");
				qrybuf.append("SELECT SUM(ACNT) ACNT, SUM(CCNT) CCNT, SUM(AAMT) AAMT, SUM(CAMT) CAMT FROM ( ");
				qrybuf.append("SELECT CASE WHEN APPGB='A' THEN COUNT(1) ELSE 0 END ACNT, CASE WHEN APPGB='A' THEN SUM(AMOUNT) ELSE 0 END AAMT, ");
				qrybuf.append("CASE WHEN APPGB='C' THEN COUNT(1) ELSE 0 END CCNT, CASE WHEN APPGB='C' THEN SUM(AMOUNT) ELSE 0 END CAMT FROM ");
				qrybuf.append(userexp[5]);
				qrybuf.append(wherebuf.toString());
				qrybuf.append(" GROUP BY APPGB ) )T1 ");

				//디버깅용
				utilm.debug_sql(qrybuf, setting);

				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				for(int k = 0; k<setting.size(); k++) {
					stmt.setString((k+1), setting.get(k));
				}

				rs = stmt.executeQuery();

				int icnt = 1;
				long tcnt = 0, tamt = 0;
				while(rs.next()) {
					JSONObject obj1 = new JSONObject();
					JSONArray arr2 = new JSONArray();

					String acnt = rs.getString("ACNT");
					String aamt = rs.getString("AAMT");
					String ccnt = rs.getString("CCNT");
					String camt = rs.getString("CAMT");

					if(acnt != null && aamt != null && ccnt != null && camt != null) {
						tcnt = Long.parseLong(acnt) + Long.parseLong(ccnt);
						tamt = Long.parseLong(aamt) - Long.parseLong(camt);
					} else {
						acnt = aamt = ccnt = camt = "0";
						tcnt = 0;
						tamt = 0;
					}

					arr2.add("합계");
					arr2.add("");
					arr2.add("");
					arr2.add("");
					arr2.add(acnt);
					arr2.add(aamt);
					arr2.add(ccnt);
					arr2.add(camt);
					arr2.add(tcnt);
					arr2.add(tamt);

					obj1.put("id", Integer.toString(icnt));
					obj1.put("data", arr2);

					sqlAry.add(obj1);
					icnt++;
				}

				jrtnobj.put("rows", sqlAry);

			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}


			return jrtnobj.toJSONString();
		}
		/*
		public String get_json_0201total(String tuser, String stime, String etime, String samt, String eamt, String appno, String tid, String mid, String acqcd, String depcd) {

			JSONObject jrtnobj = new JSONObject();

			JSONArray arr = new JSONArray();
			StringBuffer pqrybuf = new StringBuffer();
			StringBuffer qrybuf = new StringBuffer();

			int smtsidx = 2;

			try {

				String[] userexp = tuser.split(":");
				String setdc = "";

				qrybuf.append("AND ");
				qrybuf.append("appdd>='" + stime.replace("-", "") + "' ");
				qrybuf.append("and appdd<='" + etime.replace("-", "") + "' ");

				pqrybuf = new StringBuffer();
				pqrybuf.append("select  ");
				pqrybuf.append("    sum(acnt) acnt ");
				pqrybuf.append("    , sum(aamt) aamt ");
				pqrybuf.append("    , sum(ccnt) ccnt ");
				pqrybuf.append("    , sum(camt) camt ");
				pqrybuf.append("    , (sum(acnt) + sum(ccnt)) tcnt  ");
				pqrybuf.append("    , (sum(aamt) - sum(camt)) tamt  ");
				pqrybuf.append("from( ");
				pqrybuf.append("    select ");
				pqrybuf.append("        case when appgb='A' then count(1) else 0 end acnt ");
				pqrybuf.append("        , case when appgb='A' then sum(amount) else 0 end aamt ");
				pqrybuf.append("        , case when appgb='C' then count(1) else 0 end ccnt ");
				pqrybuf.append("        , case when appgb='C' then sum(amount) else 0 end camt ");
				pqrybuf.append("    from ");
				pqrybuf.append("        glob_mng_icvan ");
				pqrybuf.append("    where  ");
				pqrybuf.append("        svcgb in ('CC', 'CE') ");
				pqrybuf.append("        and authcd='0000'  ");
				pqrybuf.append("        and appdd>=? ");
				pqrybuf.append("        and appdd<=? ");

				int samt_idx = 0;
				if(null!=samt&&""!=samt) {
					smtsidx++;
					samt_idx = smtsidx;
					pqrybuf.append("            and amount>=? ");
				}

				int eamt_idx = 0;
				if(null!=eamt&&""!=eamt) {
					smtsidx++;
					eamt_idx = smtsidx;
					pqrybuf.append("            and amount<=? ");
				}

				int appno_idx = 0;
				if(null!=appno&&""!=appno) {
					smtsidx++;
					appno_idx = smtsidx;
					pqrybuf.append("            and appno=? ");
				}

				int tid_idx = 0;
				if(null!=tid&&""!=tid) {
					smtsidx++;
					tid_idx = smtsidx;
					pqrybuf.append("            and tid=? ");
				}

				int mid_idx = 0;
				if(null!=mid&&""!=mid) {
					smtsidx++;
					mid_idx = smtsidx;
					pqrybuf.append("            and mid=? ");
				}

				//int acqcd_idx = 0;
				if(null!=acqcd&&""!=acqcd) {
//					smtsidx++;
//					acqcd_idx = smtsidx;
					String[] acqexp = acqcd.split(",");
					String acqwh = "('" + utilm.implode("', '", acqexp) + "')";
					pqrybuf.append("            and acq_cd in " + acqwh);
				}

				pqrybuf.append("    group by  ");
				pqrybuf.append("        appgb ");
				pqrybuf.append(") ");

				Connection con2 = getOraConnect();
				PreparedStatement stmt2 = con2.prepareStatement(pqrybuf.toString());
				stmt2.setString(1, stime.replace("-", ""));
				stmt2.setString(2, etime.replace("-", ""));

				if(null!=samt&&""!=samt) {stmt2.setString(samt_idx, samt);}
				if(null!=eamt&&""!=eamt) {stmt2.setString(eamt_idx, eamt);}
				if(null!=appno&&""!=appno) {stmt2.setString(appno_idx, appno);}
				if(null!=tid&&""!=tid) {stmt2.setString(tid_idx, tid);}
				if(null!=mid&&""!=mid) {stmt2.setString(mid_idx, mid);}

				ResultSet rs2 = stmt2.executeQuery();

				int icnt = 1;
				while(rs2.next()) {
					JSONObject obj1 = new JSONObject();
					JSONArray arr2 = new JSONArray();

					arr2.add("합계");
					arr2.add("");
					arr2.add("");
					arr2.add("");
					arr2.add(rs2.getString("ACNT"));
					arr2.add(rs2.getString("AAMT"));
					arr2.add(rs2.getString("CCNT"));
					arr2.add(rs2.getString("CAMT"));
					arr2.add(rs2.getString("TCNT"));
					arr2.add(rs2.getString("TAMT"));

					obj1.put("id", Integer.toString(icnt));
					obj1.put("data", arr2);

					arr.add(obj1);
					icnt++;
				}

				jrtnobj.put("rows", arr);
			} catch (Exception e) {
			}
			return jrtnobj.toJSONString();
		}*/

		//카드사별조회 item
		@SuppressWarnings("unchecked")
		public String get_json_0201item(String tuser, String stime, String etime, String samt, String eamt, String appno, String tid, String mid, String acqcd, String depcd) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			JSONObject sqlobj = new JSONObject();
			JSONArray sqlAry = new JSONArray();

			StringBuffer qrybuf = new StringBuffer();
			StringBuffer wherebuf = new StringBuffer();

			try {

				//tuser split
				String[] userexp = tuser.split(":");
				//acqcd split
				String[] acqcdexp = acqcd.split(",");
				//검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();

				//WHERE SVCGB IN ('CC', 'CE') AND AUTHCD ='0000' 
				//AND TID IN (select tid from tb_bas_tidmap $USER_AUTH)  $ADDWHERE
				wherebuf.append(" WHERE SVCGB IN ('CC', 'CE') AND AUTHCD = '0000'");
				wherebuf.append(" AND TID IN (SELECT TID FROM TB_BAS_TIDMAP WHERE ORG_CD = ? ");
				setting.add(userexp[1]);

				//1. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
				if(!depcd.equals("") && depcd != null) {
					wherebuf.append(" and dep_cd = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					wherebuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				wherebuf.append(")");

				//2. STIME, ETIME SETTING
				if(!stime.equals("") && stime != null) {
					wherebuf.append(" AND APPDD >= ?");
					setting.add(stime);
				}
				if(!etime.equals("") && etime != null) {
					wherebuf.append(" AND APPDD <= ?");
					setting.add(etime);
				}

				//3. acqcd setting
				if(!acqcd.equals("") && acqcd != null) {			
					wherebuf.append(" AND ACQ_CD IN (");

					//setString 해야하는 parameter 개수만큼 물음표로 채워야 함.
					String[] paramTemp = new String[acqcdexp.length];

					for(int i = 0; i<acqcdexp.length; i++) {
						paramTemp[i] = "?";
						setting.add(acqcdexp[i]);
					}
					wherebuf.append(utilm.implode(", ", paramTemp)+")");
				}

				//4. samt, eamt setting
				if(!samt.equals("") && samt != null) {
					wherebuf.append(" AND AMOUNT >= ?");
					setting.add(samt);
				}
				if(!eamt.equals("") && eamt != null) {
					wherebuf.append(" AND AMOUNT <= ?");
					setting.add(eamt);
				}

				//5. appno setting
				if(!appno.equals("") && appno != null) {
					wherebuf.append(" AND appno = ?");
					setting.add(appno);
				}

				//6. tid setting
				if(!tid.equals("") && tid != null) {
					wherebuf.append(" AND tid = ?");
					setting.add(tid);
				}

				//7. mid setting
				if(!mid.equals("") && mid != null) {
					wherebuf.append(" AND mid = ?");
					setting.add(mid);
				}

				/*
				 * SELECT 
						DEP_NM, TID, TERM_NM, PUR_NM, MID, ACQ_CD, ACNT, CCNT, AAMT, CAMT
					FROM(
						SELECT
							TID, MID, ACQ_CD, SUM(ACNT) ACNT, SUM(CCNT) CCNT, SUM(AAMT) AAMT, SUM(CAMT) CAMT
						FROM(
							SELECT
								TID,
								MID, 
								ACQ_CD,
								CASE WHEN APPGB='A' THEN COUNT(1) ELSE 0 END ACNT,
								CASE WHEN APPGB='A' THEN SUM(AMOUNT) ELSE 0 END AAMT,
								CASE WHEN APPGB='C' THEN COUNT(1) ELSE 0 END CCNT,
								CASE WHEN APPGB='C' THEN SUM(AMOUNT) ELSE 0 END CAMT
							FROM 
								$UserExpAuth[5]
								WHERE SVCGB IN ('CC', 'CE') $AuthQry AND AUTHCD IN ('0000', '6666') AND TID IN (select tid from tb_bas_tidmap $USER_AUTH)  $ADDWHERE
							GROUP BY TID, MID, ACQ_CD, APPGB
						)
						GROUP BY TID, MID, ACQ_CD
					)T1
					LEFT OUTER JOIN( SELECT PUR_NM, PUR_KOCES, PUR_OCD, PUR_SORT FROM TB_BAS_PURINFO )T2 ON(T1.ACQ_CD=T2.PUR_OCD OR T1.ACQ_CD=T2.PUR_KOCES )
					LEFT OUTER JOIN( 
						SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD='$UserExpAuth[1]'
					)T3 ON(T1.TID=T3.TERM_ID)
					LEFT OUTER JOIN( 
						SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD='$UserExpAuth[1]'
					)T4 ON(T3.DEP_CD=T4.DEP_CD)
					ORDER BY TID, PUR_NM ASC 
				 */

				qrybuf.append("SELECT DEP_NM, TID, TERM_NM, PUR_NM, MID, ACQ_CD, ACNT, CCNT, AAMT, CAMT FROM ( ");
				qrybuf.append("SELECT TID, MID, ACQ_CD, SUM(ACNT) ACNT, SUM(CCNT) CCNT, SUM(AAMT) AAMT, SUM(CAMT) CAMT FROM ( ");
				qrybuf.append("SELECT TID, MID, ACQ_CD, CASE WHEN APPGB='A' THEN COUNT(1) ELSE 0 END ACNT, CASE WHEN APPGB='A' THEN SUM(AMOUNT) ELSE 0 END AAMT, ");
				qrybuf.append("CASE WHEN APPGB='C' THEN COUNT(1) ELSE 0 END CCNT, CASE WHEN APPGB='C' THEN SUM(AMOUNT) ELSE 0 END CAMT FROM ");
				qrybuf.append(userexp[5]);
				qrybuf.append(wherebuf.toString());
				qrybuf.append(" GROUP BY TID, MID, ACQ_CD, APPGB ) GROUP BY TID, MID, ACQ_CD ) T1");

				//left outer join setting
				qrybuf.append(" LEFT OUTER JOIN( SELECT PUR_NM, PUR_CD, PUR_SORT, PUR_OCD, PUR_KIS FROM TB_BAS_PURINFO WHERE nvl(PUR_USE, 'N')='Y' )T2 ON (T1.ACQ_CD=T2.PUR_CD OR T1.ACQ_CD=T2.PUR_KIS)");
				qrybuf.append(" LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD = ? ");
				setting.add(userexp[1]);
				if(!depcd.equals("") && depcd != null) {
					qrybuf.append(" AND DEP_CD = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" AND DEP_CD = ?");
					setting.add(userexp[2]);
				}
				qrybuf.append(" )T3 ON(T1.TID=T3.TERM_ID)");

				qrybuf.append("LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD= ? ");
				setting.add(userexp[1]);
				if(!depcd.equals("") && depcd != null) {
					qrybuf.append(" AND DEP_CD = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" AND DEP_CD = ?");
					setting.add(userexp[2]);
				}
				qrybuf.append(" )T4 ON(T3.DEP_CD=T4.DEP_CD)");
				qrybuf.append(" ORDER BY TID, PUR_NM ASC");

				//디버깅용
				utilm.debug_sql(qrybuf, setting);

				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				for(int k = 0; k<setting.size(); k++) {
					stmt.setString((k+1), setting.get(k));
				}

				rs = stmt.executeQuery();

				//2021.02.15
				//1. Collection 형태로 일단 query 결과물 다 받아옴
				//2. for문을 돌려서 다음 거래건이랑 비교했을 때 tid 다르면 소계 출력
				//3. 비교 끝나면 합계출력

				//사업부, 단말기명, 단말기번호, 카드사, 가맹점번호, 승인건수/금액, 취소건수/금액, 합계건수/금액
				ArrayList<String[]> tempStrAry = new ArrayList<>();
				String[] tempStr = new String[11];

				while(rs.next()) {
					//DEP_NM, TID, TERM_NM, PUR_NM, MID, ACQ_CD, ACNT, CCNT, AAMT, CAMT
					int temp_tot_amt = 0, temp_tot_cnt = 0;

					tempStr[0] = rs.getString("DEP_NM");
					tempStr[1] = rs.getString("TERM_NM");
					tempStr[2] = rs.getString("TID");
					tempStr[3] = rs.getString("PUR_NM");
					tempStr[4] = rs.getString("MID");
					tempStr[5] = rs.getString("ACNT");
					tempStr[6] = rs.getString("AAMT");
					tempStr[7] = rs.getString("CCNT");
					tempStr[8] = rs.getString("CAMT");

					temp_tot_cnt = Integer.parseInt(tempStr[5]) + Integer.parseInt(tempStr[7]);
					temp_tot_amt = Integer.parseInt(tempStr[6]) - Integer.parseInt(tempStr[8]);

					tempStr[9] = Integer.toString(temp_tot_cnt);
					tempStr[10] = Integer.toString(temp_tot_amt);
					
					//null체크 (엑셀에 네트워크 오류)
					for(int i = 0; i<tempStr.length; i++) {
						tempStr[i] = utilm.setDefault(tempStr[i]);
					}
					
					tempStrAry.add(tempStr);
					tempStr = new String[11];
				}

				//소계 계산을 위한 변수
				int dtot_acnt = 0, dtot_ccnt = 0,  dtot_tcnt = 0;
				long dtot_aamt = 0, dtot_camt = 0, dtot_tamt = 0;

				//합계 계산을 위한 변수
				int total_acnt = 0, total_ccnt = 0, total_tcnt = 0;
				long total_aamt = 0, total_camt = 0, total_tamt = 0;

				String compareTid = "";
				int icnt = 1;
				if(tempStrAry.size() > 0) {
					for(int i = 0; i<tempStrAry.size(); i++) {
						JSONObject tempObj = new JSONObject();
						JSONArray tempAry = new JSONArray();

						//소계 만들기 위한 다음 tid
						tempStr = tempStrAry.get(i);
						if(i < (tempStrAry.size()-1)) {
							compareTid = tempStrAry.get(i+1)[2];
						}

						tempAry.add(tempStr[0]);
						tempAry.add(tempStr[1]);
						tempAry.add(tempStr[2]);
						tempAry.add(tempStr[3]);
						tempAry.add(tempStr[4]);
						tempAry.add(tempStr[5]);
						tempAry.add(tempStr[6]);
						tempAry.add(tempStr[7]);
						tempAry.add(tempStr[8]);
						tempAry.add(tempStr[9]);
						tempAry.add(tempStr[10]);

						dtot_acnt += Integer.parseInt(tempStr[5]);
						dtot_aamt += Integer.parseInt(tempStr[6]);
						dtot_ccnt += Integer.parseInt(tempStr[7]);
						dtot_camt += Integer.parseInt(tempStr[8]);
						dtot_tcnt += Integer.parseInt(tempStr[9]);
						dtot_tamt += Integer.parseInt(tempStr[10]);

						total_acnt += Integer.parseInt(tempStr[5]);
						total_aamt += Integer.parseInt(tempStr[6]);
						total_ccnt += Integer.parseInt(tempStr[7]);
						total_camt += Integer.parseInt(tempStr[8]);
						total_tcnt += Integer.parseInt(tempStr[9]);
						total_tamt += Integer.parseInt(tempStr[10]);

						tempObj.put("id", icnt);
						tempObj.put("data", tempAry);
						tempObj.put("style", "font-color: red;");

						icnt++;
						sqlAry.add(tempObj);

						//소계부분
						if(!compareTid.equals(tempStr[2]) || i == (tempStrAry.size()-1)) {
							tempObj = new JSONObject();
							tempAry = new JSONArray();

							tempAry.add("<font color='#8B4513'><strong>소계</strong></font>");
							tempAry.add("");
							tempAry.add("");
							tempAry.add("");
							tempAry.add("");
							tempAry.add(dtot_acnt);
							tempAry.add(dtot_aamt);
							tempAry.add(dtot_ccnt);
							tempAry.add(dtot_camt);
							tempAry.add(dtot_tcnt);
							tempAry.add(dtot_tamt);

							dtot_acnt = 0;
							dtot_ccnt = 0;
							dtot_aamt = 0;
							dtot_camt = 0;
							dtot_tcnt = 0;
							dtot_tamt = 0;

							tempObj.put("id", icnt);
							tempObj.put("data", tempAry);
							tempObj.put("style", "font-color: red;");

							icnt++;
							sqlAry.add(tempObj);
						}

					}


					//합계부분
					JSONObject totalObj = new JSONObject();
					JSONArray totalAry = new JSONArray();

					totalAry.add("<font color='#A0522D'><strong>합계</strong></font>");
					totalAry.add("");
					totalAry.add("");
					totalAry.add("");
					totalAry.add("");
					totalAry.add(total_acnt);
					totalAry.add(total_aamt);
					totalAry.add(total_ccnt);
					totalAry.add(total_camt);
					totalAry.add(total_tcnt);
					totalAry.add(total_tamt);

					totalObj.put("id", "total");
					totalObj.put("data", totalAry);
					sqlAry.add(totalObj);

				}

				sqlobj.put("rows", sqlAry);

			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}

			return sqlobj.toJSONString();
		}
		
		public int get_json_0201item_cnt(String tuser, String stime, String etime, String samt, String eamt, String appno, String tid, String mid, String acqcd, String depcd) {
			Connection con2 = null;
			PreparedStatement stmt2 = null;
			ResultSet rs2 = null;
			
			StringBuffer pqrybuf = new StringBuffer();
			int icnt = 0;
			int smtsidx = 2;
			try {

				String[] userexp = tuser.split(":");
				String setdc = "";

				pqrybuf.append("select ");
				pqrybuf.append("    count(1) mcnt ");
				pqrybuf.append("from( ");
				pqrybuf.append("    select ");
				pqrybuf.append("        tid ");
				pqrybuf.append("        , acq_cd ");
				pqrybuf.append("        , mid ");
				pqrybuf.append("        , sum(acnt) acnt ");
				pqrybuf.append("        , sum(aamt) aamt ");
				pqrybuf.append("        , sum(ccnt) ccnt ");
				pqrybuf.append("        , sum(camt) camt ");
				pqrybuf.append("    from( ");
				pqrybuf.append("        select ");
				pqrybuf.append("            acq_cd ");
				pqrybuf.append("            , mid ");
				pqrybuf.append("            , tid ");
				pqrybuf.append("            , case when appgb='A' then count(1) else 0 end acnt ");
				pqrybuf.append("            , case when appgb='A' then sum(amount) else 0 end aamt ");
				pqrybuf.append("            , case when appgb='C' then count(1) else 0 end ccnt ");
				pqrybuf.append("            , case when appgb='C' then sum(amount) else 0 end camt ");
				pqrybuf.append("        from ");
				pqrybuf.append("            glob_mng_icvan ");
				pqrybuf.append("        where  ");
				pqrybuf.append("            svcgb in ('CC', 'CE') ");
				pqrybuf.append("            and authcd='0000' ");
				pqrybuf.append("            and appdd>=? ");
				pqrybuf.append("            and appdd<=? ");

				int samt_idx = 0;
				if(null!=samt&&""!=samt) {
					smtsidx++;
					samt_idx = smtsidx;
					pqrybuf.append("            and amount>=? ");
				}

				int eamt_idx = 0;
				if(null!=eamt&&""!=eamt) {
					smtsidx++;
					eamt_idx = smtsidx;
					pqrybuf.append("            and amount<=? ");
				}

				int appno_idx = 0;
				if(null!=appno&&""!=appno) {
					smtsidx++;
					appno_idx = smtsidx;
					pqrybuf.append("            and appno=? ");
				}

				int tid_idx = 0;
				if(null!=tid&&""!=tid) {
					smtsidx++;
					tid_idx = smtsidx;
					pqrybuf.append("            and tid=? ");
				}

				int mid_idx = 0;
				if(null!=mid&&""!=mid) {
					smtsidx++;
					mid_idx = smtsidx;
					pqrybuf.append("            and mid=? ");
				}

				//int acqcd_idx = 0;
				if(null!=acqcd&&""!=acqcd) {
					//				smtsidx++;
					//				acqcd_idx = smtsidx;
					String[] acqexp = acqcd.split(",");
					String acqwh = "('" + utilm.implode("', '", acqexp) + "')";
					pqrybuf.append("            and acq_cd in " + acqwh);
				}

				pqrybuf.append("        group by ");
				pqrybuf.append("            tid, mid, acq_cd, appgb ");
				pqrybuf.append("    ) group by tid, mid, acq_cd ");
				pqrybuf.append(")t1 ");
				pqrybuf.append("left outer join( ");
				pqrybuf.append("    select term_nm, term_id, org_cd, dep_cd from tb_bas_tidmst where org_cd='OR008' ");
				pqrybuf.append(")t2 on (t1.tid=t2.term_id) ");
				pqrybuf.append("left outer join( ");
				pqrybuf.append("    select dep_cd, dep_nm from tb_bas_depart where org_cd='OR008' ");
				pqrybuf.append(")t3 on (t2.dep_cd=t3.dep_cd) ");
				pqrybuf.append("left outer join( ");
				pqrybuf.append("    select pur_ocd, pur_nm, pur_sort, pur_cd, pur_smart from tb_bas_purinfo  ");
				pqrybuf.append(")t4 on (t1.acq_cd=t4.pur_cd or t1.acq_cd=t4.pur_ocd or t1.acq_cd=t4.pur_smart) ");
				pqrybuf.append("left outer join( ");
				pqrybuf.append("   select org_cd, user_pur_cd, user_pursort acq_sort from tb_bas_userpurinfo where org_cd='OR008' ");
				pqrybuf.append(")t5 on(t1.acq_cd=t5.user_pur_cd) ");
				pqrybuf.append("order by tid, acq_sort ");

				con2 = getOraConnect();
				stmt2 = con2.prepareStatement(pqrybuf.toString());
				stmt2.setString(1, stime);
				stmt2.setString(2, etime);

				if(null!=samt&&""!=samt) {stmt2.setString(samt_idx, samt);}
				if(null!=eamt&&""!=eamt) {stmt2.setString(eamt_idx, eamt);}
				if(null!=appno&&""!=appno) {stmt2.setString(appno_idx, appno);}
				if(null!=tid&&""!=tid) {stmt2.setString(tid_idx, tid);}
				if(null!=mid&&""!=mid) {stmt2.setString(mid_idx, mid);}

				rs2 = stmt2.executeQuery();

				rs2.next();

				icnt = rs2.getInt("MCNT");
			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con2,stmt2,rs2);
			}
			return icnt;
		}

		// 2021.02.15 강원대병원 - 월일자별조회 total
		@SuppressWarnings("unchecked")
		public String get_json_0202total(String tuser, String syear, String smon, String samt, String eamt, String tid,
				String mid, String acqcd, String depcd) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			JSONObject sqlobj = new JSONObject();
			JSONArray sqlAry = new JSONArray();

			StringBuffer wherebuf = new StringBuffer();
			StringBuffer qrybuf = new StringBuffer();

			try {
				// tuser, syear, smon, samt, eamt, tid, mid, acqcd, depcd
				// tuser split
				String[] userexp = tuser.split(":");
				// acqcd split
				String[] acqcdexp = acqcd.split(",");
				// 검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();

				// WHERE SVCGB IN ('CC', 'CE') AND AUTHCD='0000' AND TID IN (select tid from
				// tb_bas_tidmap $USER_AUTH)
				wherebuf.append(" WHERE SVCGB IN ('CC', 'CE') AND AUTHCD='0000' ");
				wherebuf.append("AND TID IN (SELECT TID FROM TB_BAS_TIDMAP WHERE ORG_CD= ? ");
				setting.add(userexp[1]);

				// 1. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
				if (!depcd.equals("") && depcd != null) {
					wherebuf.append(" and dep_cd = ?");
					// 1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if (userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					// 1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if (userexp[2] != null && !userexp[2].equals("")) {
					wherebuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				wherebuf.append(")");

				// 2. syear, smon
				if (syear != "" && smon != "") {
					wherebuf.append(" AND APPDD LIKE ? ");
					setting.add(syear + smon + "%");
				}

				// 3. acqcd
				if (!acqcd.equals("") && acqcd != null) {
					wherebuf.append(" AND ACQ_CD IN (");

					// setString 해야하는 parameter 개수만큼 물음표로 채워야 함.
					String[] paramTemp = new String[acqcdexp.length];

					for (int i = 0; i < acqcdexp.length; i++) {
						paramTemp[i] = "?";
						setting.add(acqcdexp[i]);
					}
					wherebuf.append(utilm.implode(", ", paramTemp) + ")");
				}

				qrybuf.append("SELECT DEP_NM, PUR_NM, T1.MID, ACQ_CD, APPDD, ACNT, CCNT, AAMT, CAMT FROM ( ");
				qrybuf.append(
						"SELECT MID, ACQ_CD, APPDD, SUM(ACNT) ACNT, SUM(CCNT) CCNT, SUM(AAMT) AAMT, SUM(CAMT) CAMT FROM ( ");
				qrybuf.append("SELECT MID, ACQ_CD, APPDD, CASE WHEN APPGB='A' THEN COUNT(1) ELSE 0 END ACNT, ");
				qrybuf.append("CASE WHEN APPGB='A' THEN SUM(AMOUNT) ELSE 0 END AAMT, ");
				qrybuf.append("CASE WHEN APPGB='C' THEN COUNT(1) ELSE 0 END CCNT, ");
				qrybuf.append("CASE WHEN APPGB='C' THEN SUM(AMOUNT) ELSE 0 END CAMT FROM ");
				qrybuf.append(userexp[5]);
				qrybuf.append(wherebuf.toString());
				qrybuf.append(" GROUP BY MID, ACQ_CD, APPDD, APPGB )T1 GROUP BY MID, ACQ_CD, APPDD ORDER BY MID )T1 ");

				qrybuf.append("LEFT OUTER JOIN(SELECT MER_NO, PUR_CD, DEP_CD FROM TB_BAS_MERINFO WHERE ORG_CD = ? ");
				setting.add(userexp[1]);
				// 1. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
				if (!depcd.equals("") && depcd != null) {
					qrybuf.append(" and dep_cd = ?");
					// 1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if (userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					// 1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if (userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				qrybuf.append(")TM ON(T1.MID=TM.MER_NO)");
				qrybuf.append("LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART)T4 ON(TM.DEP_CD=T4.DEP_CD) ");
				qrybuf.append(
						"LEFT OUTER JOIN( SELECT PUR_NM, PUR_KOCES, PUR_SORT FROM TB_BAS_PURINFO)T5 ON(T1.ACQ_CD=T5.PUR_KOCES)");

				// where조건절 추가 - samt, eamt 존재할 경우
				if (samt != "" || eamt != "") {
					wherebuf = new StringBuffer();

					wherebuf.append(" WHERE ");
					if (samt != "" && eamt == null) {
						wherebuf.append("(AAMT-CAMT) >= ?");
						setting.add(samt);
					} else if (eamt != "" && samt == null) {
						wherebuf.append("(AAMT-CAMT) <= ?");
						setting.add(eamt);
					} else if (samt != "" && eamt != "") {
						wherebuf.append("(AAMT-CAMT) >= ? AND (AAMT-CAMT) <= ?");
						setting.add(samt);
						setting.add(eamt);
					}

					qrybuf.append(wherebuf.toString());
				}

				qrybuf.append(" ORDER BY APPDD ASC, PUR_SORT ASC");

				// 디버깅용
				utilm.debug_sql(qrybuf, setting);

				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				for (int k = 0; k < setting.size(); k++) {
					stmt.setString((k + 1), setting.get(k));
				}

				rs = stmt.executeQuery();

				int icnt = 1;
				long aamt = 0, camt = 0;
				int acnt = 0, ccnt = 0;
				while (rs.next()) {
					acnt += Integer.parseInt(rs.getString("ACNT"));
					ccnt += Integer.parseInt(rs.getString("CCNT"));
					aamt += Integer.parseInt(rs.getString("AAMT"));
					camt += Integer.parseInt(rs.getString("CAMT"));
				}

				JSONObject obj1 = new JSONObject();
				JSONArray arr2 = new JSONArray();

				arr2.add("합계");
				arr2.add("");
				arr2.add("");
				arr2.add("");
				arr2.add(acnt);
				arr2.add(aamt);
				arr2.add(ccnt);
				arr2.add(camt);
				arr2.add(acnt + ccnt);
				arr2.add(aamt - camt);

				obj1.put("id", "total");
				obj1.put("data", arr2);

				sqlAry.add(obj1);

				sqlobj.put("rows", sqlAry);

			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}

			return sqlobj.toJSONString();
		}

		public int get_json_0202item_cnt(String tuser, String syear, String smon, String samt, String eamt, String tid, String mid, String acqcd, String depcd) {
			Connection con2 = null;
			PreparedStatement stmt2 = null;
			ResultSet rs2 = null;
			
			StringBuffer qrybuf = new StringBuffer();
			StringBuffer pqrybuf = new StringBuffer();
			int icnt = 0;
			int smtsidx = 1;
			try {

				String[] userexp = tuser.split(":");
				String setdc = "";

				pqrybuf.append("select ");
				pqrybuf.append("    count(1) mcnt ");
				pqrybuf.append("from( ");
				pqrybuf.append("    select ");
				pqrybuf.append("        mid ");
				pqrybuf.append("        , acq_cd ");
				pqrybuf.append("        , appdd ");
				pqrybuf.append("        , sum(acnt) acnt ");
				pqrybuf.append("        , sum(aamt) aamt ");
				pqrybuf.append("        , sum(ccnt) ccnt ");
				pqrybuf.append("        , sum(camt) camt ");
				pqrybuf.append("    from( ");
				pqrybuf.append("        select ");
				pqrybuf.append("            mid ");
				pqrybuf.append("            , acq_cd ");
				pqrybuf.append("            , appdd ");
				pqrybuf.append("            , case when appgb='A' then count(1) else 0 end acnt ");
				pqrybuf.append("            , case when appgb='A' then sum(amount) else 0 end aamt ");
				pqrybuf.append("            , case when appgb='C' then count(1) else 0 end ccnt ");
				pqrybuf.append("            , case when appgb='C' then sum(amount) else 0 end camt ");
				pqrybuf.append("        from ");
				pqrybuf.append("            " + userexp[5]);
				pqrybuf.append("        where  ");
				pqrybuf.append("            svcgb in ('CC', 'CE') ");
				pqrybuf.append("            and authcd='0000' ");
				pqrybuf.append("        	and appdd like ? ");

				int samt_idx = 0;
				if(null!=samt&&""!=samt) {
					smtsidx++;
					samt_idx = smtsidx;
					pqrybuf.append("            and amount>=? ");
				}

				int eamt_idx = 0;
				if(null!=eamt&&""!=eamt) {
					smtsidx++;
					eamt_idx = smtsidx;
					pqrybuf.append("            and amount<=? ");
				}

				int tid_idx = 0;
				if(null!=tid&&""!=tid) {
					smtsidx++;
					tid_idx = smtsidx;
					pqrybuf.append("            and tid=? ");
				}

				int mid_idx = 0;
				if(null!=mid&&""!=mid) {
					smtsidx++;
					mid_idx = smtsidx;
					pqrybuf.append("            and mid=? ");
				}

				if(null!=acqcd&&""!=acqcd) {
					String[] acqexp = acqcd.split(",");
					String acqwh = "('" + utilm.implode("', '", acqexp) + "')";
					pqrybuf.append("            and acq_cd in " + acqwh);
				}

				pqrybuf.append("        group by ");
				pqrybuf.append("            mid, acq_cd, appdd, appgb ");
				pqrybuf.append("    ) group by mid, acq_cd, appdd ");
				pqrybuf.append(")t1 ");
				pqrybuf.append("left outer join( ");
				pqrybuf.append("    select mer_no, pur_cd, dep_cd from tb_bas_merinfo where org_cd='OR008' ");
				pqrybuf.append(")t2 on (t1.mid=t2.mer_no) ");
				pqrybuf.append("left outer join( ");
				pqrybuf.append("    select dep_cd, dep_nm from tb_bas_depart where org_cd='OR008' ");
				pqrybuf.append(")t3 on (t2.dep_cd=t3.dep_cd) ");
				pqrybuf.append("left outer join( ");
				pqrybuf.append("    select pur_ocd, pur_nm, pur_sort, pur_cd, pur_smart from tb_bas_purinfo ");
				pqrybuf.append(")t4 on (t1.acq_cd=t4.pur_cd or t1.acq_cd=t4.pur_ocd or t1.acq_cd=t4.pur_smart) ");
				pqrybuf.append("left outer join( ");
				pqrybuf.append("   select org_cd, user_pur_cd, user_pursort acq_sort from tb_bas_userpurinfo where org_cd='OR008' ");
				pqrybuf.append(")t5 on(t1.acq_cd=t5.user_pur_cd) ");
				pqrybuf.append("order by appdd, acq_sort ");

				con2 = getOraConnect();
				stmt2 = con2.prepareStatement(pqrybuf.toString());
				stmt2.setString(1, syear + utilm.AddZero(smon, 2) + "%");

				if(null!=samt&&""!=samt) {stmt2.setString(samt_idx, samt);}
				if(null!=eamt&&""!=eamt) {stmt2.setString(eamt_idx, eamt);}
				if(null!=tid&&""!=tid) {stmt2.setString(tid_idx, tid);}
				if(null!=mid&&""!=mid) {stmt2.setString(mid_idx, mid);}

				rs2 = stmt2.executeQuery();

				rs2.next();

				icnt = rs2.getInt("MCNT");
			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con2,stmt2,rs2);
			}
			return icnt;
		}

		//2021.02.16 강원대병원 v3 - 월일자별조회 item
		@SuppressWarnings({ "static-access", "unchecked" })
		public String get_json_0202item(String tuser, String syear, String smon, String samt, String eamt, String tid, String mid, String acqcd, String depcd) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			JSONObject sqlobj = new JSONObject();
			JSONArray sqlAry = new JSONArray();

			StringBuffer qrybuf = new StringBuffer();
			StringBuffer wherebuf = new StringBuffer();

			try {
				// tuser, syear, smon, samt, eamt, tid, mid, acqcd, depcd
				// tuser split
				String[] userexp = tuser.split(":");
				// acqcd split
				String[] acqcdexp = acqcd.split(",");
				// 검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();

				// WHERE SVCGB IN ('CC', 'CE') AND AUTHCD='0000' AND TID IN (select tid from
				// tb_bas_tidmap $USER_AUTH)
				wherebuf.append(" WHERE SVCGB IN ('CC', 'CE') AND AUTHCD='0000' ");
				wherebuf.append("AND TID IN (SELECT TID FROM TB_BAS_TIDMAP WHERE ORG_CD= ? ");
				setting.add(userexp[1]);

				// 1. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
				if (!depcd.equals("") && depcd != null) {
					wherebuf.append(" and dep_cd = ?");
					// 1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if (userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					// 1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if (userexp[2] != null && !userexp[2].equals("")) {
					wherebuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				wherebuf.append(")");

				// 2. syear, smon
				if (syear != "" && smon != "") {
					wherebuf.append(" AND APPDD LIKE ? ");
					setting.add(syear + smon + "%");
				}

				// 3. acqcd
				if (!acqcd.equals("") && acqcd != null) {
					wherebuf.append(" AND ACQ_CD IN (");

					// setString 해야하는 parameter 개수만큼 물음표로 채워야 함.
					String[] paramTemp = new String[acqcdexp.length];

					for (int i = 0; i < acqcdexp.length; i++) {
						paramTemp[i] = "?";
						setting.add(acqcdexp[i]);
					}
					wherebuf.append(utilm.implode(", ", paramTemp) + ")");
				}

				qrybuf.append("SELECT DEP_NM, PUR_NM, T1.MID, ACQ_CD, APPDD, ACNT, CCNT, AAMT, CAMT FROM ( ");
				qrybuf.append("SELECT MID, ACQ_CD, APPDD, SUM(ACNT) ACNT, SUM(CCNT) CCNT, SUM(AAMT) AAMT, SUM(CAMT) CAMT FROM ( ");
				qrybuf.append("SELECT MID, ACQ_CD, APPDD, CASE WHEN APPGB='A' THEN COUNT(1) ELSE 0 END ACNT, CASE WHEN APPGB='A' THEN SUM(AMOUNT) ELSE 0 END AAMT, ");
				qrybuf.append("CASE WHEN APPGB='C' THEN COUNT(1) ELSE 0 END CCNT, CASE WHEN APPGB='C' THEN SUM(AMOUNT) ELSE 0 END CAMT FROM ");
				qrybuf.append(userexp[5]);
				qrybuf.append(wherebuf.toString());
				qrybuf.append(" GROUP BY MID, ACQ_CD, APPDD, APPGB ) GROUP BY MID, ACQ_CD, APPDD ORDER BY MID ) T1 ");

				// left outer join setting
				qrybuf.append("LEFT OUTER JOIN( SELECT MER_NO, PUR_CD, DEP_CD FROM TB_BAS_MERINFO WHERE ORG_CD = ?");
				setting.add(userexp[1]);
				// 1. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
				if (!depcd.equals("") && depcd != null) {
					qrybuf.append(" and dep_cd = ?");
					// 1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if (userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					// 1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if (userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				qrybuf.append(")TM ON(T1.MID=TM.MER_NO) ");
				//LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD='$UserExpAuth[1]')T4 ON(TM.DEP_CD=T4.DEP_CD)
				qrybuf.append("LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD = ? ");
				setting.add(userexp[1]);
				// 1. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
				if (!depcd.equals("") && depcd != null) {
					qrybuf.append(" and dep_cd = ?");
					// 1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if (userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					// 1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if (userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				qrybuf.append(")T4 ON(TM.DEP_CD=T4.DEP_CD) ");

				qrybuf.append("LEFT OUTER JOIN( SELECT PUR_NM, PUR_KOCES, PUR_SORT,PUR_CD FROM TB_BAS_PURINFO)T5 ON(T1.ACQ_CD=T5.PUR_CD) ");

				qrybuf.append("LEFT OUTER JOIN( SELECT ORG_CD, USER_PUR_CD, USER_PURSORT FROM TB_BAS_USERPURINFO WHERE ORG_CD = ? ");
				setting.add(userexp[1]);
				qrybuf.append(")S3 ON(T5.PUR_CD=S3.USER_PUR_CD) ");

				//where조건절 추가 - samt, eamt 존재할 경우
				if(samt != "" || eamt != "") {
					wherebuf = new StringBuffer();

					wherebuf.append(" WHERE ");
					if(samt != "" && eamt == null) {
						wherebuf.append("(AAMT-CAMT) >= ?");
						setting.add(samt);
					} else if(eamt != "" && samt == null) {
						wherebuf.append("(AAMT-CAMT) <= ?");
						setting.add(eamt);
					} else if (samt != "" && eamt != "") {
						wherebuf.append("(AAMT-CAMT) >= ? AND (AAMT-CAMT) <= ?");
						setting.add(samt);
						setting.add(eamt);
					}
					qrybuf.append(wherebuf.toString());
				}
				qrybuf.append(" ORDER BY APPDD ASC, DEP_NM ASC, PUR_NM ASC");

				// 디버깅용
				utilm.debug_sql(qrybuf, setting);

				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				for (int k = 0; k < setting.size(); k++) {
					stmt.setString((k + 1), setting.get(k));
				}

				//DEP_NM, PUR_NM, T1.MID, ACQ_CD, APPDD, ACNT, CCNT, AAMT, CAMT
				ArrayList<String[]> tempStrAry = new ArrayList<>();
				String[] tempStr = new String[10];

				rs = stmt.executeQuery();
				int icnt = 1;
				while(rs.next()) {
					int tcnt = 0;
					long tamt = 0;
					//승인일자 YYYY-MM-DD
					String appdd = rs.getString("APPDD");
					tempStr[0] = appdd.substring(0, 4) + "-" + appdd.substring(4, 6) + "-" + appdd.substring(6);
					tempStr[1] = rs.getString("DEP_NM");
					tempStr[2] = rs.getString("PUR_NM");
					tempStr[3] = rs.getString("MID");
					tempStr[4] = rs.getString("ACNT");
					tempStr[5] = rs.getString("AAMT");
					tempStr[6] = rs.getString("CCNT");
					tempStr[7] = rs.getString("CAMT");

					tcnt = Integer.parseInt(tempStr[4]) + Integer.parseInt(tempStr[6]); 
					tamt = Integer.parseInt(tempStr[5]) - Integer.parseInt(tempStr[7]); 

					tempStr[8] = Integer.toString(tcnt);
					tempStr[9] = Long.toString(tamt);

					//2021.02.16 null check
					for(int i = 0; i<tempStr.length; i++) {
						tempStr[i] = utilm.setDefault(tempStr[i]);
					}

					tempStrAry.add(tempStr);
					tempStr = new String[10];
				}

				//소계 계산을 위한 변수
				int dtot_acnt = 0, dtot_ccnt = 0,  dtot_tcnt = 0;
				long dtot_aamt = 0, dtot_camt = 0, dtot_tamt = 0;

				//합계 계산을 위한 변수
				int total_acnt = 0, total_ccnt = 0, total_tcnt = 0;
				long total_aamt = 0, total_camt = 0, total_tamt = 0;

				//일자별 소계 계산
				String compareDay = "";

				if(tempStrAry.size() > 0) {
					for(int i = 0; i<tempStrAry.size(); i++) {
						JSONObject tempObj = new JSONObject();
						JSONArray tempAry = new JSONArray();

						//소계 만들기 위한 다음 tid
						tempStr = tempStrAry.get(i);
						if(i < (tempStrAry.size()-1)) {
							compareDay = tempStrAry.get(i+1)[0];
						}

						tempAry.add(tempStr[0]);
						tempAry.add(tempStr[1]);
						tempAry.add(tempStr[2]);
						tempAry.add(tempStr[3]);
						tempAry.add(tempStr[4]);
						tempAry.add(tempStr[5]);
						tempAry.add(tempStr[6]);
						tempAry.add(tempStr[7]);
						tempAry.add(tempStr[8]);
						tempAry.add(tempStr[9]);

						dtot_acnt += Integer.parseInt(tempStr[4]);
						dtot_aamt += Integer.parseInt(tempStr[5]);
						dtot_ccnt += Integer.parseInt(tempStr[6]);
						dtot_camt += Integer.parseInt(tempStr[7]);
						dtot_tcnt += Integer.parseInt(tempStr[8]);
						dtot_tamt += Integer.parseInt(tempStr[9]);

						total_acnt += Integer.parseInt(tempStr[4]);
						total_aamt += Integer.parseInt(tempStr[5]);
						total_ccnt += Integer.parseInt(tempStr[6]);
						total_camt += Integer.parseInt(tempStr[7]);
						total_tcnt += Integer.parseInt(tempStr[8]);
						total_tamt += Integer.parseInt(tempStr[9]);

						tempObj.put("id", icnt);
						tempObj.put("data", tempAry);
						tempObj.put("style", "font-color: red;");

						icnt++;
						sqlAry.add(tempObj);

						//소계부분
						if(!compareDay.equals(tempStr[0]) || i == (tempStrAry.size()-1)) {
							tempObj = new JSONObject();
							tempAry = new JSONArray();

							tempAry.add("<font color='#8B4513'><strong>소계</strong></font>");
							tempAry.add("");
							tempAry.add("");
							tempAry.add("");
							tempAry.add(dtot_acnt);
							tempAry.add(dtot_aamt);
							tempAry.add(dtot_ccnt);
							tempAry.add(dtot_camt);
							tempAry.add(dtot_tcnt);
							tempAry.add(dtot_tamt);

							dtot_acnt = 0;
							dtot_ccnt = 0;
							dtot_aamt = 0;
							dtot_camt = 0;
							dtot_tcnt = 0;
							dtot_tamt = 0;
							
							tempObj.put("id", icnt);
							tempObj.put("data", tempAry);

							icnt++;
							sqlAry.add(tempObj);
						}

					}

					//합계부분
					JSONObject totalObj = new JSONObject();
					JSONArray totalAry = new JSONArray();

					totalAry.add("<font color='#A0522D'><strong>합계</strong></font>");
					totalAry.add("");
					totalAry.add("");
					totalAry.add("");
					totalAry.add(total_acnt);
					totalAry.add(total_aamt);
					totalAry.add(total_ccnt);
					totalAry.add(total_camt);
					totalAry.add(total_tcnt);
					totalAry.add(total_tamt);

					totalObj.put("id", "total");
					totalObj.put("data", totalAry);
					sqlAry.add(totalObj);

				}

				sqlobj.put("rows", sqlAry);

			}catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}

			return sqlobj.toJSONString();
		}

		//2021.02.16 강원대병원v3 - 매장별거래조회 total
		//2021.02.23 강원대병원v3 - 수정요망(검색조건 - depcd 추가로 인해서)
		@SuppressWarnings("unchecked")
		public String get_json_0203total(String tuser, String stime, String etime, String samt, String eamt, String tid, String depcd) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			JSONObject sqlobj = new JSONObject();
			JSONArray sqlAry = new JSONArray();

			StringBuffer qrybuf = new StringBuffer();
			StringBuffer wherebuf = new StringBuffer();

			try {

				//tuser split
				String[] userexp = tuser.split(":");
				//검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();

				wherebuf.append(" WHERE SVCGB IN ('CC', 'CE') AND AUTHCD = '0000'");
				wherebuf.append(" AND TID IN (SELECT TID FROM TB_BAS_TIDMAP WHERE ORG_CD = ? ");
				setting.add(userexp[1]);
				//1. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
				if(!depcd.equals("") && depcd != null) {
					wherebuf.append(" and dep_cd = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					wherebuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				wherebuf.append(")");

				//2. STIME, ETIME SETTING
				if(!stime.equals("") && stime != null) {
					wherebuf.append(" AND APPDD >= ?");
					setting.add(stime);
				}
				if(!etime.equals("") && etime != null) {
					wherebuf.append(" AND APPDD <= ?");
					setting.add(etime);
				}

				//3. samt, eamt setting
				if(!samt.equals("") && samt != null) {
					wherebuf.append(" AND AMOUNT >= ?");
					setting.add(samt);
				}
				if(!eamt.equals("") && eamt != null) {
					wherebuf.append(" AND AMOUNT <= ?");
					setting.add(eamt);
				}

				//4. tid setting
				if(!tid.equals("") && tid != null) {
					wherebuf.append(" AND tid = ?");
					setting.add(tid);
				}

				qrybuf.append("SELECT DEP_NM, TID, TERM_NM, PUR_NM, MID, ACQ_CD, ACNT, CCNT, AAMT, CAMT FROM ( ");
				qrybuf.append("SELECT TID, MID, ACQ_CD, SUM(ACNT) ACNT, SUM(CCNT) CCNT, SUM(AAMT) AAMT, SUM(CAMT) CAMT FROM ( ");
				qrybuf.append("SELECT TID, MID, ACQ_CD, ");
				qrybuf.append("CASE WHEN APPGB='A' THEN COUNT(1) ELSE 0 END ACNT, CASE WHEN APPGB='A' THEN SUM(AMOUNT) ELSE 0 END AAMT, ");
				qrybuf.append("CASE WHEN APPGB='C' THEN COUNT(1) ELSE 0 END CCNT, CASE WHEN APPGB='C' THEN SUM(AMOUNT) ELSE 0 END CAMT ");
				qrybuf.append("FROM " + userexp[5]);
				qrybuf.append(wherebuf.toString());
				qrybuf.append(" GROUP BY TID, MID, ACQ_CD, APPGB ");
				qrybuf.append(" ) GROUP BY TID, MID, ACQ_CD ) T1 ");
				qrybuf.append("LEFT OUTER JOIN( SELECT PUR_NM, PUR_KOCES FROM TB_BAS_PURINFO )T2 ON(T1.ACQ_CD=T2.PUR_KOCES) ");
				qrybuf.append("LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD = ? ");
				setting.add(userexp[1]);
				//1. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
				if(!depcd.equals("") && depcd != null) {
					qrybuf.append(" and dep_cd = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				qrybuf.append(")T3 ON(T1.TID=T3.TERM_ID) ");
				qrybuf.append("LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART)T4 ON(T3.DEP_CD=T4.DEP_CD) ");
				qrybuf.append("ORDER BY TID, MID, ACQ_CD ");

				//디버깅용
				utilm.debug_sql(qrybuf, setting);

				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				for(int k = 0; k<setting.size(); k++) {
					stmt.setString((k+1), setting.get(k));
				}

				rs = stmt.executeQuery();

				long aamt = 0, camt = 0;
				int acnt = 0, ccnt = 0;
				while(rs.next()) {
					aamt += Long.parseLong(utilm.checkNumberData(rs.getString("AAMT")));
					camt += Long.parseLong(utilm.checkNumberData(rs.getString("CAMT")));
					acnt += Integer.parseInt(utilm.checkNumberData(rs.getString("ACNT")));
					ccnt += Integer.parseInt(utilm.checkNumberData(rs.getString("CCNT")));
				}

				JSONObject tempObj = new JSONObject();
				JSONArray tempAry = new JSONArray();

				tempAry.add("");
				tempAry.add("합계");
				tempAry.add(acnt);
				tempAry.add(aamt);
				tempAry.add(ccnt);
				tempAry.add(camt);
				tempAry.add(acnt + ccnt);
				tempAry.add(aamt - camt);

				tempObj.put("id", "total");
				tempObj.put("data", tempAry);

				sqlAry.add(tempObj);

				sqlobj.put("rows", sqlAry);

			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}
			return sqlobj.toJSONString();
		}

		public int get_json_0203item_cnt(String tuser, String stime, String etime, String samt, String eamt, String tid, String depcd) {
			Connection con2 = null;
			PreparedStatement stmt2 = null;
			ResultSet rs2 = null;
			
			StringBuffer qrybuf = new StringBuffer();
			StringBuffer pqrybuf = new StringBuffer();
			int icnt = 0;
			int smtsidx = 2;
			try {

				String[] userexp = tuser.split(":");
				String setdc = "";

				pqrybuf.append("select ");
				pqrybuf.append("    count(1) MCNT ");
				pqrybuf.append("from( ");
				pqrybuf.append("    select ");
				pqrybuf.append("        tid, mid, acq_cd, sum(acnt) acnt, sum(aamt) aamt, sum(ccnt) ccnt, sum(camt) camt ");
				pqrybuf.append("    from( ");
				pqrybuf.append("        select ");
				pqrybuf.append("            tid, mid, acq_cd ");
				pqrybuf.append("            , case when appgb='A' then count(1) else 0 end acnt ");
				pqrybuf.append("            , case when appgb='A' then sum(amount) else 0 end aamt ");
				pqrybuf.append("            , case when appgb='C' then count(1) else 0 end ccnt ");
				pqrybuf.append("            , case when appgb='C' then sum(amount) else 0 end camt ");
				pqrybuf.append("        from ");
				pqrybuf.append("            " + userexp[5]);
				pqrybuf.append("        where  ");
				pqrybuf.append("            svcgb in ('CC', 'CE') ");
				pqrybuf.append("            and authcd='0000' ");
				pqrybuf.append("        	and appdd>= ? ");
				pqrybuf.append("        	and appdd<= ? ");

				int samt_idx = 0;
				if(null!=samt&&""!=samt) {
					smtsidx++;
					samt_idx = smtsidx;
					pqrybuf.append("            and amount>=? ");
				}

				int eamt_idx = 0;
				if(null!=eamt&&""!=eamt) {
					smtsidx++;
					eamt_idx = smtsidx;
					pqrybuf.append("            and amount<=? ");
				}

				int tid_idx = 0;
				if(null!=tid&&""!=tid) {
					smtsidx++;
					tid_idx = smtsidx;
					pqrybuf.append("            and tid=? ");
				}

				pqrybuf.append("        group by ");
				pqrybuf.append("            tid, mid, acq_cd, appgb ");
				pqrybuf.append("    ) group by tid, mid, acq_cd ");
				pqrybuf.append(")");

				con2 = getOraConnect();
				stmt2 = con2.prepareStatement(pqrybuf.toString());
				stmt2.setString(1, stime);
				stmt2.setString(2, etime);

				if(null!=samt&&""!=samt) {stmt2.setString(samt_idx, samt);}
				if(null!=eamt&&""!=eamt) {stmt2.setString(eamt_idx, eamt);}
				if(null!=tid&&""!=tid) {stmt2.setString(tid_idx, tid);}

				rs2 = stmt2.executeQuery();

				rs2.next();

				icnt = rs2.getInt("MCNT");
			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con2,stmt2,rs2);
			}
			return icnt;
		}

		//2021.02.16 강원대병원v3 - 매장별거래조회 item
		@SuppressWarnings("unchecked")
		public String get_json_0203item(String tuser, String stime, String etime, String samt, String eamt, String tid, String depcd) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			JSONObject sqlobj = new JSONObject();
			JSONArray sqlAry = new JSONArray();

			StringBuffer qrybuf = new StringBuffer();
			StringBuffer wherebuf = new StringBuffer();

			try {
				//tuser split
				String[] userexp = tuser.split(":");
				//검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();

				wherebuf.append(" WHERE SVCGB IN ('CC', 'CE') AND AUTHCD = '0000'");
				wherebuf.append(" AND TID IN (SELECT TID FROM TB_BAS_TIDMAP WHERE ORG_CD = ? ");
				setting.add(userexp[1]);
				//1. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
				if(!depcd.equals("") && depcd != null) {
					wherebuf.append(" and dep_cd = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					wherebuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				wherebuf.append(")");

				//2. STIME, ETIME SETTING
				if(!stime.equals("") && stime != null) {
					wherebuf.append(" AND APPDD >= ?");
					setting.add(stime);
				}
				if(!etime.equals("") && etime != null) {
					wherebuf.append(" AND APPDD <= ?");
					setting.add(etime);
				}

				//3. samt, eamt setting
				if(!samt.equals("") && samt != null) {
					wherebuf.append(" AND AMOUNT >= ?");
					setting.add(samt);
				}
				if(!eamt.equals("") && eamt != null) {
					wherebuf.append(" AND AMOUNT <= ?");
					setting.add(eamt);
				}

				//4. tid setting
				if(!tid.equals("") && tid != null) {
					wherebuf.append(" AND tid = ?");
					setting.add(tid);
				}

				qrybuf.append("SELECT T3.DEP_CD, DEP_NM, TID, TERM_NM, SUM(ACNT) ACNT, SUM(CCNT) CCNT, SUM(AAMT) AAMT, SUM(CAMT) CAMT FROM ( ");
				qrybuf.append("SELECT TID, CASE WHEN APPGB='A' THEN COUNT(1) ELSE 0 END ACNT, CASE WHEN APPGB='A' THEN SUM(AMOUNT) ELSE 0 END AAMT, ");
				qrybuf.append("CASE WHEN APPGB='C' THEN COUNT(1) ELSE 0 END CCNT, CASE WHEN APPGB='C' THEN SUM(AMOUNT) ELSE 0 END CAMT FROM ");
				qrybuf.append(userexp[5]);
				qrybuf.append(wherebuf.toString());
				qrybuf.append(" GROUP BY TID, APPGB )T1 ");

				//left outer join setting
				qrybuf.append("LEFT OUTER JOIN(SELECT TERM_ID, TERM_NM, DEP_CD FROM TB_BAS_TIDMST where org_cd = ? ");
				setting.add(userexp[1]);
				//1. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
				if(!depcd.equals("") && depcd != null) {
					qrybuf.append(" and dep_cd = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				qrybuf.append(")T3 ON(T3.TERM_ID=T1.TID) ");
				qrybuf.append("LEFT OUTER JOIN( SELECT DEP_CD, DEP_NM FROM TB_BAS_DEPART where org_cd = ? ");
				setting.add(userexp[1]);
				//1. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
				if(!depcd.equals("") && depcd != null) {
					qrybuf.append(" and dep_cd = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				qrybuf.append(")T2 ON(T3.DEP_CD=T2.DEP_CD) ");
				qrybuf.append("GROUP BY T3.DEP_CD, DEP_NM, TID, TERM_NM");
				qrybuf.append(" ORDER BY T3.DEP_CD, TID");

				//디버깅용
				utilm.debug_sql(qrybuf, setting);

				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				for(int k = 0; k<setting.size(); k++) {
					stmt.setString((k+1), setting.get(k));
				}

				rs = stmt.executeQuery();

				ArrayList<String[]> tempStrAry = new ArrayList<>();
				String[] tempStr = new String[9];

				//사업부,단말기명,단말기번호,승인건수,승인금액,취소건수,취소금액,합계건수,합계금액
				//T3.DEP_CD, DEP_NM, TID, TERM_NM, SUM(ACNT) ACNT, SUM(CCNT) CCNT, SUM(AAMT) AAMT, SUM(CAMT) CAMT
				while(rs.next()) {
					int tcnt = 0;
					long tamt = 0;

					tempStr[0] = rs.getString("DEP_NM");
					tempStr[1] = rs.getString("TERM_NM");
					tempStr[2] = rs.getString("TID");
					tempStr[3] = utilm.checkNumberData(rs.getString("ACNT"));
					tempStr[4] = utilm.checkNumberData(rs.getString("AAMT"));
					tempStr[5] = utilm.checkNumberData(rs.getString("CCNT"));
					tempStr[6] = utilm.checkNumberData(rs.getString("CAMT"));

					tcnt = Integer.parseInt(tempStr[3]) + Integer.parseInt(tempStr[5]);
					tamt = Long.parseLong(tempStr[4]) - Long.parseLong(tempStr[6]);

					tempStr[7] = Integer.toString(tcnt);
					tempStr[8] = Long.toString(tamt);

					tempStrAry.add(tempStr);
					tempStr = new String[9];

				}

				//합계 계산을 위한 변수
				int total_acnt = 0, total_ccnt = 0, total_tcnt = 0;
				long total_aamt = 0, total_camt = 0, total_tamt = 0;

				if(tempStrAry.size() > 0) {
					int icnt = 1;
					for(int i = 0; i<tempStrAry.size(); i++) {
						JSONObject tempObj = new JSONObject();
						JSONArray tempAry = new JSONArray();

						tempStr = tempStrAry.get(i);

						tempAry.add(tempStr[0]);
						tempAry.add(tempStr[1]);
						tempAry.add(tempStr[2]);
						tempAry.add(tempStr[3]);
						tempAry.add(tempStr[4]);
						tempAry.add(tempStr[5]);
						tempAry.add(tempStr[6]);
						tempAry.add(tempStr[7]);
						tempAry.add(tempStr[8]);

						total_acnt += Integer.parseInt(tempStr[3]);
						total_aamt += Long.parseLong(tempStr[4]);
						total_ccnt += Integer.parseInt(tempStr[5]);
						total_camt += Long.parseLong(tempStr[6]);
						total_tcnt += Integer.parseInt(tempStr[7]);
						total_tamt += Long.parseLong(tempStr[8]);

						tempObj.put("id", icnt);
						tempObj.put("data", tempAry);

						sqlAry.add(tempObj);

						icnt++;
					}

					//합계
					JSONObject totalObj = new JSONObject();
					JSONArray totalAry = new JSONArray();

					totalAry.add("합계");
					totalAry.add("");
					totalAry.add("");
					totalAry.add(total_acnt);
					totalAry.add(total_aamt);
					totalAry.add(total_ccnt);
					totalAry.add(total_camt);
					totalAry.add(total_tcnt);
					totalAry.add(total_tamt);

					totalObj.put("id", "total");
					totalObj.put("data", totalAry);

					sqlAry.add(totalObj);

				}

				sqlobj.put("rows", sqlAry);

			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}

			return sqlobj.toJSONString();
		}
		
		//2021.02.17 강원대병원 v3 - 상세내역조회 item
		//2021.02.23 월일자별조회 더블클릭 시 mid 넘겨옴.
		@SuppressWarnings({ "unchecked", "static-access" })
		public String get_json_0204item(String tuser, String stime, String etime, String samt, String eamt, String appno, String acqcd, String pid, String mediid, String medi_cd,
				String cardno, String tid, String tradeidx, String depcd, String auth01, String auth02, String auth03, String mid) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;

			JSONObject sqlobj = new JSONObject();
			JSONArray sqlAry = new JSONArray();

			StringBuffer qrybuf = new StringBuffer();
			StringBuffer wherebuf = new StringBuffer();

			ArrayList<String> pos_field = get_column_field(tuser, "van", "field");

			try {
				//tuser split
				String[] userexp = tuser.split(":");
				//acqcd split
				String[] acqcdexp = acqcd.split(",");
				//검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();

				qrybuf.append("SELECT SEQNO, TID, MID, VANGB, MDATE, T1.TRANIDX, CASE WHEN APPGB='A' THEN '승인' WHEN APPGB='C' THEN '취소' ELSE '' END APPGB, ");
				qrybuf.append("ENTRYMD, T1.APPDD, APPTM, T1.APPNO, T1.CARDNO, HALBU, T1.AMOUNT, ACQ_CD, PUR_NM, AUTHCD, ");
				qrybuf.append("CASE WHEN CHECK_CARD='Y' THEN '체크카드' WHEN CHECK_CARD='N' THEN '신용카드' ELSE '' END CHECK_CARD, ");
				qrybuf.append("OVSEA_CARD, TLINEGB, SIGNCHK, OAPPNO, OAPPDD, ISS_CD, ");
				qrybuf.append("CASE WHEN ADD_GB IN ('1', 'O') THEN '외래' WHEN ADD_GB IN ('2', 'E') THEN '응급' WHEN ADD_GB IN ('3', 'I') THEN '입원' WHEN ADD_GB IN ('4', 'G') THEN '종합검진' ");
				qrybuf.append("WHEN ADD_GB='5' THEN '일반검진' WHEN ADD_GB='6' THEN '장례식장' ELSE '' END ADD_GB, ");
				qrybuf.append("ADD_CID, ADD_CD, ADD_RECP, ADD_CASHER, DEP_NM, EXP_DD, REG_DD, RTN_CD, TERM_NM, DEPOREQDD DEPO_DD ");
				qrybuf.append("FROM " + userexp[5] + " T1");

				//left outer join
				qrybuf.append(" LEFT OUTER JOIN(SELECT EXP_DD, REQ_DD, REG_DD, APP_DD, APP_NO, SALE_AMT, TRANIDX, RSC_CD, RTN_CD, CARD_NO FROM ");
				qrybuf.append(userexp[6]);
				qrybuf.append(")T2 ON(T1.APPDD=T2.APP_DD AND T1.TRANIDX=T2.TRANIDX) ");
				qrybuf.append("LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD = ? ");
				setting.add(userexp[1]);
				if(!depcd.equals("") && depcd != null) {
					qrybuf.append(" and dep_cd = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				qrybuf.append(")T3 ON(T1.TID=T3.TERM_ID) ");

				qrybuf.append("LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD = ? ");
				setting.add(userexp[1]);
				if(!depcd.equals("") && depcd != null) {
					qrybuf.append(" and dep_cd = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				qrybuf.append(")T4 ON(T3.DEP_CD=T4.DEP_CD) ");
				qrybuf.append("LEFT OUTER JOIN( SELECT PUR_NM, PUR_OCD, PUR_KOCES, PUR_CD FROM TB_BAS_PURINFO)T5 ON (T1.ACQ_CD=T5.PUR_OCD OR T1.ACQ_CD=T5.PUR_KOCES OR T1.ACQ_CD=T5.PUR_CD)");

				//SET_WHERE SETTING
				wherebuf.append(" where svcgb in ('CC', 'CE') and authcd='0000'");
				wherebuf.append(" AND TID IN (select tid from tb_bas_tidmap where org_cd = ? ");
				setting.add(userexp[1]);
				//1. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
				if(!depcd.equals("") && depcd != null) {
					wherebuf.append(" and dep_cd = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					wherebuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				wherebuf.append(")");

				//2. STIME, ETIME SETTING
				if(!stime.equals("") && stime != null) {
					wherebuf.append(" AND APPDD >= ?");
					setting.add(stime);
				}
				if(!etime.equals("") && etime != null) {
					wherebuf.append(" AND APPDD <= ?");
					setting.add(etime);
				}

				//3. acqcd setting
				if(!acqcd.equals("") && acqcd != null) {			
					wherebuf.append(" AND ACQ_CD IN (");

					//setString 해야하는 parameter 개수만큼 물음표로 채워야 함.
					String[] paramTemp = new String[acqcdexp.length];

					for(int i = 0; i<acqcdexp.length; i++) {
						paramTemp[i] = "?";
						setting.add(acqcdexp[i]);
					}
					wherebuf.append(utilm.implode(", ", paramTemp)+")");
				}

				//4. samt, eamt setting
				if(!samt.equals("") && samt != null) {
					wherebuf.append(" AND AMOUNT >= ?");
					setting.add(samt);
				}
				if(!eamt.equals("") && eamt != null) {
					wherebuf.append(" AND AMOUNT <= ?");
					setting.add(eamt);
				}

				//5. appno setting
				if(!appno.equals("") && appno != null) {
					wherebuf.append(" AND appno = ?");
					setting.add(appno);
				}

				//6. tid setting
				if(!tid.equals("") && tid != null) {
					wherebuf.append(" AND tid = ?");
					setting.add(tid);
				}

				//7. tranidx setting
				if(!tradeidx.equals("") && tradeidx != null) {
					wherebuf.append(" AND TRANIDX = ?");
					setting.add(tradeidx);
				}

				//8. pid setting
				//ADD_CID
				if(!pid.equals("") && pid != null) {
					wherebuf.append(" AND ADD_CID = ?");
					setting.add(pid);
				}

				//9. medi_cd setting
				if(!medi_cd.equals("") && medi_cd != null) {
					wherebuf.append(" AND ADD_CD = ?");
					setting.add(medi_cd);
				}

				//10. mediid setting
				if(!mediid.equals("") && mediid != null) {
					wherebuf.append(" AND ADD_CASHER = ?");
					setting.add(mediid);
				}

				//11. cardno setting
				//강원대병원 - MEDI_GOODS 필드 사용
				if(!cardno.equals("") && cardno != null) {
					wherebuf.append(" AND MEDI_GOODS LIKE ? ");
					setting.add(cardno + "%");
				}

				//auth01 전체, auth02 승인, auth03 취소
				//12. auth setting
				if(!auth01.equals("Y")){
					if(auth02.equals("Y")){wherebuf.append(" AND APPGB = 'A'");}
					else if(auth03.equals("Y")){wherebuf.append(" AND APPGB = 'C'");}
					else if(auth02.equals("Y") && auth03.equals("Y")) {wherebuf.append(" AND APPGB IN ('A', 'C')");}
				}

				//13. mid setting
				if(!mid.equals("") && mid != null) {
					wherebuf.append(" AND MID = ? ");
					setting.add(mid);
				}

				qrybuf.append(wherebuf.toString());
				qrybuf.append(" ORDER BY APPDD DESC, APPTM DESC");

				//디버깅용
				utilm.debug_sql(qrybuf, setting);

				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString(), ResultSet.TYPE_FORWARD_ONLY);
				for(int k = 0; k<setting.size(); k++) {
					stmt.setString((k+1), setting.get(k));
				}
				
				
				rs = stmt.executeQuery();
				//setFatchSize - 테스트 해보고 변경하거나 제외
				rs.setFetchSize(500);

				int icnt = 1;
				while(rs.next()) {
					JSONObject tempObj = new JSONObject();
					JSONArray tempAry = new JSONArray();

					tempAry.add(icnt);
					for(int i = 0; i<pos_field.size(); i++) {
						//cardno change plz
						//카드번호가 있을 때만 decode -> 9 ~ 12번째 별표시
						if(pos_field.get(i).equals("CARDNO")) {
							String newCardNo = utilm.cardno_masking(trans_seed_manager.seed_dec_card(rs.getString(pos_field.get(i)).trim()));
							tempAry.add(newCardNo);

						} else if(pos_field.get(i).equals("APPDD") || pos_field.get(i).equals("OAPPDD") || pos_field.get(i).equals("EXP_DD") || pos_field.get(i).equals("REG_DD") || pos_field.get(i).equals("DEPO_DD")) {
							//일자 필드일 때 YYYY/MM/DD 형태로 변경해서 출력
							//str_to_dateformat
							String tempDate = utilm.setDefault(rs.getString(pos_field.get(i)));
							String newDate = "";
							if(tempDate != null && !tempDate.equals("")) {
								newDate = utilm.str_to_dateformat(tempDate);
							}
							tempAry.add(newDate);
							//
						} else if (pos_field.get(i).equals("APPTM")) {
							String tempDate = utilm.setDefault(rs.getString(pos_field.get(i)));
							String newDate = "";
							if(tempDate != null && !tempDate.equals("")) {
								newDate = utilm.str_to_timeformat(tempDate);
							}
							tempAry.add(newDate);
						} else {
							//null check plz
							tempAry.add(utilm.setDefault(rs.getString(pos_field.get(i))));
						}

					}

					//2021.02.17 seqno 나중에 세팅해서 넣어줄 것 - 웹취소 관련 때문에.
					String seqno = rs.getString("SEQNO");		
					tempObj.put("id", seqno);
					tempObj.put("data", tempAry);

					sqlAry.add(tempObj);

					icnt++;
				}

				sqlobj.put("rows", sqlAry);
			}catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}
			return sqlobj.toJSONString();

		}
		
		@SuppressWarnings("unchecked")
		public String get_cardlist_0204(String cardno, String tuser) {
			Connection con2 = null;
			PreparedStatement stmt2 = null;
			ResultSet rs2 = null;

			JSONArray arr = new JSONArray();
			JSONObject jrtnobj = new JSONObject();
			StringBuffer qrybuf = new StringBuffer();
			StringBuffer pqrybuf = new StringBuffer();

			try {

				String[] userexp = tuser.split(":");
				String setdc = "";

				if(cardno.length()>10) {
					qrybuf.append(make_query_str("AND", "cardno", encm.seed_enc_str(cardno.trim()), "EQ"));
				}

				pqrybuf.append("SELECT ");
				pqrybuf.append("		DEP_NM, TID, TERM_NM, PUR_NM ");
				pqrybuf.append("		, SEQNO, APPGB, TID, MID, ACQ_CD, T1.APPDD, APPTM");
				pqrybuf.append("		, APPNO, CARDNO, T1.TRANIDX, HALBU, AMOUNT, OAPPDD, OAPPNO");
				pqrybuf.append("		, ADD_CID, ADD_CD, ADD_GB, ADD_CASHER, EXP_DD, REQ_DD, REG_DD");
				pqrybuf.append("		, RSC_CD, RTN_CD, AUTHCD, AUTHMSG");
				pqrybuf.append("		, CHECK_CARD, OVSEA_CARD, SIGNCHK, DDCGB ");
				pqrybuf.append("FROM( ");
				pqrybuf.append("		SELECT ");
				pqrybuf.append("			SEQNO, APPGB, TID, MID, ACQ_CD, APPDD, APPTM");
				pqrybuf.append("			, APPNO, CARDNO, TRANIDX, HALBU, AMOUNT, OAPPDD, OAPPNO");
				pqrybuf.append("			, ADD_CID, ADD_CD, ADD_GB, ADD_CASHER, AUTHCD, AUTHMSG");
				pqrybuf.append("			, CHECK_CARD, OVSEA_CARD, SIGNCHK, DDCGB");
				pqrybuf.append("		FROM ");
				pqrybuf.append("			GLOB_MNG_ICVAN ");
				pqrybuf.append("		WHERE SVCGB IN ('CC', 'CE')  AND AUTHCD='0000' AND CARDNO=? ");
				pqrybuf.append(")T1 ");
				pqrybuf.append("LEFT OUTER JOIN( SELECT EXP_DD, REQ_DD, REG_DD, APP_DD, TRANIDX, RSC_CD, RTN_CD FROM " + userexp[6] + ")T2 ON(T1.APPDD=T2.APP_DD AND T1.TRANIDX=T2.TRANIDX) ");
				pqrybuf.append("LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE org_cd=?)T3 ON(T1.TID=T3.TERM_ID) ");
				pqrybuf.append("LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE org_cd=?)T4 ON(T3.DEP_CD=T4.DEP_CD) ");
				pqrybuf.append("LEFT OUTER JOIN( SELECT PUR_NM, PUR_CD FROM TB_BAS_PURINFO)T5 ON(T1.ACQ_CD=T5.PUR_CD)");


				con2 = getOraConnect();
				stmt2 = con2.prepareStatement(pqrybuf.toString());
				stmt2.setString(1, cardno.trim());
				stmt2.setString(2, userexp[1]);
				stmt2.setString(3, userexp[1]);
				rs2 = stmt2.executeQuery();

				int icnt = 1;
				while(rs2.next()) {
					JSONObject obj1 = new JSONObject();
					JSONArray arr2 = new JSONArray();

					String cardno_dec = utilm.cardno_masking(trans_seed_manager.seed_dec_card(rs2.getString("CARDNO").trim()));
					String cardtp_kor = utilm.strcardtype(rs2.getString("CHECK_CARD").trim());
					String signgb_kor = utilm.strsigngb(rs2.getString("SIGNCHK").trim());

					arr2.add(Integer.toString(icnt));
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
					arr2.add(cardtp_kor); //카드종류
					arr2.add(""); //회선구분
					arr2.add(signgb_kor); //서명유무
					arr2.add("0000"); //매출코드
					arr2.add(utilm.str_to_dateformat(rs2.getString("REQ_DD"))); //매입요청일자
					arr2.add(utilm.str_to_dateformat(rs2.getString("REQ_DD"))); //매입접수일자
					arr2.add(utilm.str_to_dateformat(rs2.getString("REG_DD"))); //매입응답일
					arr2.add(utilm.deposit_rst_to_kor(rs2.getString("RTN_CD"))); //매입결과
					arr2.add(utilm.str_to_dateformat(rs2.getString("EXP_DD"))); //입금예정일
					arr2.add(rs2.getString("ADD_CID")); //진료번호
					arr2.add(rs2.getString("ADD_GB")); //진료구분
					arr2.add(rs2.getString("ADD_CASHER")); //수납자
					arr2.add(rs2.getString("TRANIDX")); //거래코드
					arr2.add(rs2.getString("AUTHMSG")); //카드사응답내용

					obj1.put("id", rs2.getString("SEQNO"));
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


		//2021.02.23 강원대병원v3 - 카드 거래건 거래내역상세보기
		@SuppressWarnings("unchecked")
		public String get_detail_0204(String seqno, String tuser, String appno) {
			Connection con2 = null;
			PreparedStatement stmt2 = null;
			ResultSet rs2 = null;

			JSONArray arr = new JSONArray();
			JSONObject jrtnobj = new JSONObject();

			StringBuffer qrybuf = new StringBuffer();
			StringBuffer pqrybuf = new StringBuffer();

			try {

				//tuser split
				String[] userexp = tuser.split(":");

				pqrybuf.append("SELECT ");
				pqrybuf.append("		DEP_NM, TID, TERM_NM, PUR_NM, T3.DEP_CD ");
				pqrybuf.append("		, SEQNO, APPGB, TID, MID, ACQ_CD, T1.APPDD, APPTM");
				pqrybuf.append("		, APPNO, CARDNO, T1.TRANIDX, HALBU, AMOUNT, OAPPDD, OAPPNO");
				pqrybuf.append("		, ADD_CID, ADD_CD, ADD_GB, ADD_CASHER, EXP_DD, REQ_DD, REG_DD");
				pqrybuf.append("		, RSC_CD, RTN_CD, AUTHCD, AUTHMSG");
				pqrybuf.append("		, CHECK_CARD, OVSEA_CARD, SIGNCHK, DDCGB, MEDI_GOODS, ACQ_NM, ISS_NM  ");
				pqrybuf.append("FROM( ");
				pqrybuf.append("		SELECT ");
				pqrybuf.append("			SEQNO, APPGB, TID, MID, ACQ_CD, APPDD, APPTM");
				pqrybuf.append("			, APPNO, CARDNO, TRANIDX, HALBU, AMOUNT, OAPPDD, OAPPNO");
				pqrybuf.append("			, ADD_CID, ADD_CD, ADD_GB, ADD_CASHER, AUTHCD, AUTHMSG");
				pqrybuf.append("			, CHECK_CARD, OVSEA_CARD, SIGNCHK, DDCGB, MEDI_GOODS, ACQ_NM, ISS_NM ");
				pqrybuf.append("		FROM ");
				pqrybuf.append(userexp[5]);
				pqrybuf.append("		WHERE SEQNO = ? AND APPNO = ? ");
				pqrybuf.append(")T1 ");
				pqrybuf.append("LEFT OUTER JOIN( SELECT EXP_DD, REQ_DD, REG_DD, APP_DD, TRANIDX, RSC_CD, RTN_CD FROM " + userexp[6] + ")T2 ON(T1.APPDD=T2.APP_DD AND T1.TRANIDX=T2.TRANIDX) ");
				pqrybuf.append("LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE org_cd = ?)T3 ON(T1.TID=T3.TERM_ID) ");
				pqrybuf.append("LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE org_cd = ?)T4 ON(T3.DEP_CD=T4.DEP_CD) ");
				pqrybuf.append("LEFT OUTER JOIN( SELECT PUR_NM, PUR_CD, PUR_KOCES FROM TB_BAS_PURINFO)T5 ON(T1.ACQ_CD=T5.PUR_CD OR T1.ACQ_CD=T5.PUR_KOCES)");


				System.out.println(pqrybuf.toString());

				con2 = getOraConnect();
				stmt2 = con2.prepareStatement(pqrybuf.toString());
				stmt2.setString(1, seqno);
				stmt2.setString(2, appno);
				stmt2.setString(3, userexp[1]);
				stmt2.setString(4, userexp[1]);

				rs2 = stmt2.executeQuery();

				int icnt = 1;
				if(rs2.next()) {
					JSONObject obj1 = new JSONObject();

					String cardno_dec = utilm.cardno_masking(trans_seed_manager.seed_dec_card(rs2.getString("CARDNO").trim()));
					String cardtp_kor = utilm.strcardtype(rs2.getString("CHECK_CARD").trim());
					String signgb_kor = utilm.strsigngb(rs2.getString("SIGNCHK").trim());

					obj1.put("SEQNO", rs2.getString("SEQNO"));
					obj1.put("DEP_NM", rs2.getString("DEP_NM"));
					obj1.put("DEP_CD", rs2.getString("DEP_CD"));
					obj1.put("TERM_NM", rs2.getString("TERM_NM"));
					obj1.put("TID", rs2.getString("TID"));
					obj1.put("MID", rs2.getString("MID"));
					obj1.put("PUR_NM", rs2.getString("PUR_NM"));
					obj1.put("APPDD", utilm.str_to_dateformat(rs2.getString("APPDD")));
					obj1.put("APPTM", utilm.str_to_timeformat(rs2.getString("APPTM")));
					obj1.put("OAPPDD", utilm.str_to_dateformat(rs2.getString("OAPPDD")));

					String appgb = rs2.getString("APPGB");
					if(appgb.equals("A")) {
						appgb="승인";
					} else {
						appgb="취소";
					}
					obj1.put("APPGB", appgb);

					obj1.put("APPNO", rs2.getString("APPNO"));
					obj1.put("CARDNO", cardno_dec);
					obj1.put("MEDI_GOODS", rs2.getString("MEDI_GOODS"));
					obj1.put("AMOUNT", rs2.getString("AMOUNT"));
					obj1.put("HALBU", rs2.getString("HALBU"));
					obj1.put("CHECK_CARD", cardtp_kor);
					obj1.put("SIGNCHK", signgb_kor);
					obj1.put("REQ_DD", utilm.str_to_dateformat(rs2.getString("REQ_DD")));
					obj1.put("RES_DD", utilm.str_to_dateformat(rs2.getString("REQ_DD")));
					obj1.put("REG_DD", utilm.str_to_dateformat(rs2.getString("REG_DD")));
					obj1.put("EXP_DD", utilm.str_to_dateformat(rs2.getString("EXP_DD")));
					obj1.put("RTN_CD", utilm.deposit_rst_to_kor(rs2.getString("RTN_CD")));
					obj1.put("ADD_CID", rs2.getString("ADD_CID"));
					obj1.put("ADD_GB", rs2.getString("ADD_GB"));
					obj1.put("ADD_CASHER", rs2.getString("ADD_CASHER"));
					obj1.put("TRANIDX", rs2.getString("TRANIDX"));
					obj1.put("AUTHCD", rs2.getString("AUTHCD"));
					obj1.put("AUTHMSG", rs2.getString("AUTHMSG"));
					obj1.put("ACQ_NM", rs2.getString("ACQ_NM"));
					obj1.put("ISS_NM", rs2.getString("ISS_NM"));

					arr.add(obj1);

					jrtnobj.put("TOTAL", 1);
					jrtnobj.put("ITEMS", arr);

				}
			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con2,stmt2,rs2);
			}
			return jrtnobj.toJSONString();
		}
		
		@SuppressWarnings({ "unchecked", "static-access" })
		public String get_json_0204total_cvs(String tuser, String stime, String etime, String samt, String eamt, String appno, String cardtp, String auth01, String auth02, String auth03,
				String can01, String can02, String can03, String mid, String tid, String acqcd, String tid2, String paging) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;

			JSONObject sqlobj = new JSONObject();
			JSONArray sqlAry = new JSONArray();

			StringBuffer qrybuf = new StringBuffer();
			StringBuffer wherebuf = new StringBuffer();
			StringBuffer exwherebuf = new StringBuffer();

			try {

				//tuser split
				String[] userexp = tuser.split(":");
				//acqcd split
				String[] acqcdexp = acqcd.split(",");
				//검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();

				//SET_WHERE SETTING
				wherebuf.append(" WHERE SVCGB IN ('CC', 'CE') AND AUTHCD='0000' AND TID IN (SELECT TID FROM TB_BAS_TIDMAP WHERE ORG_CD=? ");
				setting.add(userexp[1]);
				//1. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
				if(userexp[2] != null && !userexp[2].equals("")) {
					wherebuf.append(" AND DEP_CD = ?");
					setting.add(userexp[2]);
				}
				wherebuf.append(")");

				if(!stime.equals("") && stime != null) {
					wherebuf.append(" AND APPDD >= ?");
					setting.add(stime);
				}
				
				if(!etime.equals("") && etime != null) {
					wherebuf.append(" AND APPDD <= ?");
					setting.add(etime);
				}
				
				if(!samt.equals("") && samt != null) {
					wherebuf.append(" AND AMOUNT >= ?");
					setting.add(samt);
				}
				
				if(!eamt.equals("") && eamt != null) {
					wherebuf.append(" AND AMOUNT <= ?");
					setting.add(eamt);
				}
				
				if(!appno.equals("") && appno != null) {
					wherebuf.append(" AND APPNO=?");
					setting.add(appno);
				}
				
				if(!cardtp.equals("") && cardtp != null) {
					if(cardtp=="04") {
						wherebuf.append("TID ='39257746' AND EXT_FIELD IS NULL ");
					}else {
						wherebuf.append("TID ='39257746' AND EXT_FIELD LIKE ? "); 
						setting.add(cardtp);
					}
				}
				
				//auth01 전체, auth02 승인, auth03 취소
				if(!auth01.equals("Y")){
					if(auth02.equals("Y")){exwherebuf.append(" AND APPGB_TXT = 'A'");}
					else if(auth03.equals("Y")){exwherebuf.append(" AND APPGB_TXT = 'C'");}
					else if(auth02.equals("Y") && auth03.equals("Y")) {exwherebuf.append(" AND APPGB_TXT IN ('A', 'C')");}
				}
				
				if(!can01.equals("Y")){
					if(can02.equals("Y")){wherebuf.append(" AND DPFLAG ='Y'");}
					else if(can03.equals("Y")){wherebuf.append(" AND ADD_CNT ='Y'");}
					else if(can02.equals("Y") && can03.equals("Y")) {wherebuf.append(" AND DPFLAG='Y' AND ADD_CNT='Y' ");}
				}
				
				if(!mid.equals("") && mid != null) {
					wherebuf.append(" AND MID = ? ");
					setting.add(mid);
				}

				if(!tid.equals("") && tid != null) {
					wherebuf.append(" AND tid = ?");
					setting.add(tid);
				}
			
				if(!acqcd.equals("") && acqcd != null) {			
					wherebuf.append(" AND ACQ_CD IN (");

					//setString 해야하는 parameter 개수만큼 물음표로 채워야 함.
					String[] paramTemp = new String[acqcdexp.length];

					for(int i = 0; i<acqcdexp.length; i++) {
						paramTemp[i] = "?";
						setting.add(acqcdexp[i]);
					}
					wherebuf.append(utilm.implode(", ", paramTemp)+")");
				}
				
				if(!tid2.equals("") && tid2 != null) {
					String[] tidval = tid2.split("_");
					wherebuf.append(" AND TID IN (?, ?, ?, ?)");
					setting.add(tidval[0]);
					setting.add(tidval[1]);
					setting.add(tidval[2]);
					setting.add(tidval[3]);
				}

				qrybuf.append("SELECT ");
				qrybuf.append("	DEP_NM ");
				qrybuf.append("	,TERM_ID ");
				qrybuf.append("	,TERM_NM ");
				qrybuf.append("	,ACNT ");
				qrybuf.append("	,CCNT ");
				qrybuf.append("	,AAMT ");
				qrybuf.append("	,CAMT ");
				qrybuf.append("	,TOTCNT ");
				qrybuf.append("	,TOTAMT ");
				qrybuf.append("	,BC ");
				qrybuf.append("	,NH ");
				qrybuf.append("	,KB ");
				qrybuf.append("	,SS ");
				qrybuf.append("	,HN ");
				qrybuf.append("	,LO ");
				qrybuf.append("	,HD ");
				qrybuf.append("	,SI ");
				qrybuf.append("FROM( ");
				qrybuf.append("	SELECT ");
				qrybuf.append("		TID ");
				qrybuf.append("		,SUM(ACNT) ACNT ");
				qrybuf.append("		,SUM(CCNT) CCNT ");
				qrybuf.append("		,SUM(AAMT) AAMT ");
				qrybuf.append("		,SUM(CAMT) CAMT ");
				qrybuf.append("		,SUM(ACNT)+SUM(CCNT) TOTCNT ");
				qrybuf.append("		,SUM(AAMT)-SUM(CAMT) TOTAMT ");
				qrybuf.append("		,SUM(ABC  )-SUM(CBC  ) BC ");
				qrybuf.append("		,SUM(ANH  )-SUM(CNH  ) NH ");
				qrybuf.append("		,SUM(AKB  )-SUM(CKB  ) KB ");
				qrybuf.append("		,SUM(ASS  )-SUM(CSS  ) SS ");
				qrybuf.append("		,SUM(AHN  )-SUM(CHN  ) HN ");
				qrybuf.append("		,SUM(ALO  )-SUM(CLO  ) LO ");
				qrybuf.append("		,SUM(AHD  )-SUM(CHD  ) HD ");
				qrybuf.append("		,SUM(ASI  )-SUM(CSI  ) SI ");
				qrybuf.append("	FROM( ");
				qrybuf.append("		SELECT ");
				qrybuf.append("			TID ");
				qrybuf.append("			,CASE WHEN APPGB='A' THEN COUNT(1) ELSE 0 END ACNT ");
				qrybuf.append("			,CASE WHEN APPGB='C' THEN COUNT(1) ELSE 0 END CCNT ");
				qrybuf.append("			,CASE WHEN APPGB='A' THEN SUM(AMOUNT) ELSE 0 END AAMT ");
				qrybuf.append("			,CASE WHEN APPGB='C' THEN SUM(AMOUNT) ELSE 0 END CAMT ");
				qrybuf.append("			,CASE WHEN APPGB='A' AND MID IN ('704855398', '768017318','707528764') THEN SUM(AMOUNT) ELSE 0 END ABC ");
				qrybuf.append("			,CASE WHEN APPGB='A' AND MID IN ('140239694', '143275451','151558364') THEN SUM(AMOUNT) ELSE 0 END ANH ");
				qrybuf.append("			,CASE WHEN APPGB='A' AND MID IN ('00052904921', '00087259990','00084542316') THEN SUM(AMOUNT) ELSE 0 END AKB ");
				qrybuf.append("			,CASE WHEN APPGB='A' AND MID IN ('165138860', '128890479','167802984') THEN SUM(AMOUNT) ELSE 0 END ASS ");
				qrybuf.append("			,CASE WHEN APPGB='A' AND MID IN ('00986653087', '00951457027','00989439518') THEN SUM(AMOUNT) ELSE 0 END AHN ");
				qrybuf.append("			,CASE WHEN APPGB='A' AND MID IN ('9052663887', '9967457077','9969229911') THEN SUM(AMOUNT) ELSE 0 END ALO ");
				qrybuf.append("			,CASE WHEN APPGB='A' AND MID IN ('151098345', '860386610','860295101') THEN SUM(AMOUNT) ELSE 0 END AHD ");
				qrybuf.append("			,CASE WHEN APPGB='A' AND MID IN ('0107608507', '0104783451','57296808') THEN SUM(AMOUNT) ELSE 0 END ASI ");
				qrybuf.append("			,CASE WHEN APPGB='C' AND MID IN ('704855398', '768017318','707528764') THEN SUM(AMOUNT) ELSE 0 END CBC ");
				qrybuf.append("			,CASE WHEN APPGB='C' AND MID IN ('140239694', '143275451','151558364') THEN SUM(AMOUNT) ELSE 0 END CNH ");
				qrybuf.append("			,CASE WHEN APPGB='C' AND MID IN ('00052904921', '00087259990','00084542316') THEN SUM(AMOUNT) ELSE 0 END CKB ");
				qrybuf.append("			,CASE WHEN APPGB='C' AND MID IN ('165138860', '128890479','167802984') THEN SUM(AMOUNT) ELSE 0 END CSS ");
				qrybuf.append("			,CASE WHEN APPGB='C' AND MID IN ('00986653087', '00951457027','00989439518') THEN SUM(AMOUNT) ELSE 0 END CHN ");
				qrybuf.append("			,CASE WHEN APPGB='C' AND MID IN ('9052663887', '9967457077','9969229911') THEN SUM(AMOUNT) ELSE 0 END CLO ");
				qrybuf.append("			,CASE WHEN APPGB='C' AND MID IN ('151098345', '860386610','860295101') THEN SUM(AMOUNT) ELSE 0 END CHD ");
				qrybuf.append("			,CASE WHEN APPGB='C' AND MID IN ('0107608507', '0104783451','57296808') THEN SUM(AMOUNT) ELSE 0 END CSI ");
				qrybuf.append("		FROM ( ");
				qrybuf.append("			SELECT ");
				qrybuf.append("				SEQNO, DEP_NM, TERM_NM, TID, MID, PUR_NM, ACQ_CD,  ");
				qrybuf.append("				APPDD, APPTM, OAPPDD, APPNO, APPGB, ");
				qrybuf.append("				APPGB_TXT, CARDNO, AMOUNT, HALBU, CARDTP_TXT, SIGNCHK_TXT, ");
				qrybuf.append("				REQ_DD, AUTHCD, REG_DD, RTN_CD, RTN_TXT,  ");
				qrybuf.append("				EXP_DD, EXT_FIELD, TRANIDX, AUTHMSG ");
				qrybuf.append("			FROM( ");
				qrybuf.append("				SELECT ");
				qrybuf.append("					SEQNO, DEP_NM, TERM_NM, TID, MID, PUR_NM, ");
				qrybuf.append("					APPDD, APPTM, OAPPDD, APPNO, APPGB, ACQ_CD, ");
				qrybuf.append("					CASE  ");
				qrybuf.append("						WHEN APPGB='A' THEN '신용승인' ");
				qrybuf.append("						WHEN APPGB='C' THEN '신용취소' ");
				qrybuf.append("					END APPGB_TXT, ");
				qrybuf.append("					CARDNO,	AMOUNT,	HALBU, ");
				qrybuf.append("					CASE WHEN CHECK_CARD='Y' THEN '체크카드' ELSE '신용카드' END CARDTP_TXT, ");
				qrybuf.append("					CASE WHEN SIGNCHK='1' THEN '전자서명' ELSE '무서명' END SIGNCHK_TXT, ");
				qrybuf.append("					REQ_DD,	AUTHCD,	REG_DD,	RTN_CD, ");
				qrybuf.append("					CASE ");
				qrybuf.append("						WHEN RTN_CD IS NULL THEN '결과없음' ");
				qrybuf.append("						WHEN RTN_CD IN('60', '67') THEN '정상매입' ");
				qrybuf.append("						WHEN RTN_CD IN('61', '64') THEN '매입반송' ");
				qrybuf.append("					END RTN_TXT, ");
				qrybuf.append("					EXP_DD,	EXT_FIELD,	TRANIDX, AUTHMSG ");
				qrybuf.append("				FROM( ");
				qrybuf.append("					SELECT ");
				qrybuf.append("						SEQNO, BIZNO, TID, MID, VANGB, MDATE, SVCGB, T1.TRANIDX, T1.APPGB, ENTRYMD, ");
				qrybuf.append("						T1.APPDD, APPTM, T1.APPNO, T1.CARDNO, HALBU, CURRENCY, T1.AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX, ");
				qrybuf.append("						ISS_CD, ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG, CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB, ");
				qrybuf.append("						SIGNCHK, DDCGB, EXT_FIELD, OAPPNO, OAPPDD, OAPPTM, OAPP_AMT, ADD_GB, ADD_CID, ADD_CD, ");
				qrybuf.append("						ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, PUR_NM, DEP_NM, EXP_DD, REQ_DD, REG_DD, RSC_CD, RTN_CD, TERM_NM, DPFLAG  ");
				qrybuf.append("					FROM ");
				qrybuf.append("						GLOB_MNG_ICVAN T1 ");
				qrybuf.append("					LEFT OUTER JOIN( ");
				qrybuf.append("						SELECT EXP_DD, REQ_DD, REG_DD, APP_DD, TRANIDX, RSC_CD, RTN_CD FROM TB_MNG_DEPDATA ");
				qrybuf.append("					)T2 ON(T1.APPDD=T2.APP_DD AND T1.TRANIDX=T2.TRANIDX) ");
				qrybuf.append("					LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD=?)T3 ON(T1.TID=T3.TERM_ID) ");
				setting.add(userexp[1]);
				qrybuf.append("					LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD=?)T4 ON(T3.DEP_CD=T4.DEP_CD) ");
				setting.add(userexp[1]);
				qrybuf.append("					LEFT OUTER JOIN( SELECT PUR_NM, PUR_OCD, PUR_KOCES FROM TB_BAS_PURINFO)T5 ON (T1.ACQ_CD=T5.PUR_OCD OR T1.ACQ_CD=T5.PUR_KOCES) ");
				qrybuf.append(wherebuf.toString());
				qrybuf.append("					order by appdd desc, apptm desc ");
				qrybuf.append("				) ");
				qrybuf.append("			) ");
				qrybuf.append(exwherebuf.toString());
				qrybuf.append("		) ");
				qrybuf.append("		GROUP BY TID, APPGB, MID ");
				qrybuf.append("    ) ");
				qrybuf.append("    GROUP BY TID         ");
				qrybuf.append(")T2 ");
				qrybuf.append("LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD=?)T3 ON(T2.TID=T3.TERM_ID) ");
				setting.add(userexp[1]);
				qrybuf.append("LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD=?)T4 ON(T3.DEP_CD=T4.DEP_CD) ");
				setting.add(userexp[1]);
				
				//디버깅용
				utilm.debug_sql(qrybuf, setting);

				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				for(int k = 0; k<setting.size(); k++) {
					stmt.setString((k+1), setting.get(k));
				}

				rs = stmt.executeQuery();

				long taamt = 0, tcamt = 0, ttamt = 0;
				int tacnt = 0, tccnt = 0, ttcnt = 0;
				long tbc = 0, tkb = 0, thn = 0, tss = 0, tsi = 0, thd = 0, tlo = 0, tnh = 0;
				int icnt = 1;
				while(rs.next()) {
					JSONObject tempObj = new JSONObject();
					JSONArray tempAry = new JSONArray();

					int acnt = Integer.parseInt(utilm.checkNumberData(rs.getString("acnt")));
					int ccnt = Integer.parseInt(utilm.checkNumberData(rs.getString("ccnt")));
					int totcnt = acnt + ccnt;

					long aamt = Long.parseLong(utilm.checkNumberData(rs.getString("aamt")));
					long camt = Long.parseLong(utilm.checkNumberData(rs.getString("camt")));
					long totamt = aamt - camt;

					long bc = Long.parseLong(utilm.checkNumberData(rs.getString("bc")));
					long kb = Long.parseLong(utilm.checkNumberData(rs.getString("kb")));
					long hn = Long.parseLong(utilm.checkNumberData(rs.getString("hn")));
					long ss = Long.parseLong(utilm.checkNumberData(rs.getString("ss")));
					long si = Long.parseLong(utilm.checkNumberData(rs.getString("si")));
					long hd = Long.parseLong(utilm.checkNumberData(rs.getString("hd")));
					long lo = Long.parseLong(utilm.checkNumberData(rs.getString("lo")));
					long nh = Long.parseLong(utilm.checkNumberData(rs.getString("nh")));

					taamt += aamt;
					tcamt += camt;
					ttamt += totamt;
					tacnt += acnt;
					tccnt += ccnt;
					ttcnt += totcnt;
					tbc += bc;
					tkb += kb;
					thn += hn;
					tss += ss;
					tsi += si;
					thd += hd;
					tlo += lo;
					tnh += nh;

					tempAry.add(icnt);
					tempAry.add(rs.getString("dep_nm") + "(" + rs.getString("term_nm") + ":" + rs.getString("term_id") + ")");
					tempAry.add(acnt);
					tempAry.add(aamt);
					tempAry.add(ccnt);
					tempAry.add(camt);
					tempAry.add(totcnt);
					tempAry.add(totamt);
					tempAry.add(bc);
					tempAry.add(kb);
					tempAry.add(hn);
					tempAry.add(ss);
					tempAry.add(si);
					tempAry.add(hd);
					tempAry.add(lo);
					tempAry.add(nh);

					icnt++;

					tempObj.put("id", icnt);
					tempObj.put("data", tempAry);

					sqlAry.add(tempObj);
				}

				//합계부분
				JSONObject tempObj = new JSONObject();
				JSONArray tempAry = new JSONArray();
				
				tempAry.add("합계");
				tempAry.add("");
				tempAry.add(tacnt);
				tempAry.add(taamt);
				tempAry.add(tccnt);
				tempAry.add(tcamt);
				tempAry.add(ttcnt);
				tempAry.add(ttamt);
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
				setOraClose(con,stmt,rs);
			}
			return sqlobj.toJSONString();
		} 

		@SuppressWarnings({ "unchecked", "static-access" })
		public String get_json_0204item_cvs(String tuser, String stime, String etime, String samt, String eamt, String appno, String cardtp, String auth01, String auth02, String auth03,
				String can01, String can02, String can03, String mid, String tid, String acqcd, String tid2, String paging) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;

			JSONObject sqlobj = new JSONObject();
			JSONArray sqlAry = new JSONArray();

			StringBuffer qrybuf = new StringBuffer();
			StringBuffer wherebuf = new StringBuffer();
			StringBuffer exwherebuf = new StringBuffer();

			ArrayList<String> pos_field = get_column_field(tuser, "van", "field");

			try {
				//tuser split
				String[] userexp = tuser.split(":");
				//acqcd split
				String[] acqcdexp = acqcd.split(",");
				//검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();

				//SET_WHERE SETTING
				wherebuf.append(" WHERE SVCGB IN ('CC', 'CE') AND AUTHCD='0000' AND TID IN (SELECT TID FROM TB_BAS_TIDMAP WHERE ORG_CD=? ");
				setting.add(userexp[1]);
				//1. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
				if(userexp[2] != null && !userexp[2].equals("")) {
					wherebuf.append(" AND DEP_CD = ?");
					setting.add(userexp[2]);
				}
				wherebuf.append(")");

				if(!stime.equals("") && stime != null) {
					wherebuf.append(" AND APPDD >= ?");
					setting.add(stime);
				}
				
				if(!etime.equals("") && etime != null) {
					wherebuf.append(" AND APPDD <= ?");
					setting.add(etime);
				}
				
				if(!samt.equals("") && samt != null) {
					wherebuf.append(" AND AMOUNT >= ?");
					setting.add(samt);
				}
				
				if(!eamt.equals("") && eamt != null) {
					wherebuf.append(" AND AMOUNT <= ?");
					setting.add(eamt);
				}
				
				if(!appno.equals("") && appno != null) {
					wherebuf.append(" AND APPNO=?");
					setting.add(appno);
				}
				
				if(!cardtp.equals("") && cardtp != null) {
					if(cardtp=="04") {
						wherebuf.append("TID ='39257746' AND EXT_FIELD IS NULL ");
					}else {
						wherebuf.append("TID ='39257746' AND EXT_FIELD LIKE ? "); 
						setting.add(cardtp);
					}
				}
				
				//auth01 전체, auth02 승인, auth03 취소
				if(!auth01.equals("Y")){
					if(auth02.equals("Y")){wherebuf.append(" AND APPGB = 'A'");}
					else if(auth03.equals("Y")){wherebuf.append(" AND APPGB = 'C'");}
					else if(auth02.equals("Y") && auth03.equals("Y")) {wherebuf.append(" AND APPGB IN ('A', 'C')");}
				}
				
				if(!can01.equals("Y")){
					if(can02.equals("Y")){wherebuf.append(" AND DPFLAG ='Y'");}
					else if(can03.equals("Y")){wherebuf.append(" AND ADD_CNT ='Y'");}
					else if(can02.equals("Y") && can03.equals("Y")) {wherebuf.append(" AND DPFLAG='Y' AND ADD_CNT='Y' ");}
				}
				
				if(!mid.equals("") && mid != null) {
					wherebuf.append(" AND MID = ? ");
					setting.add(mid);
				}

				if(!tid.equals("") && tid != null) {
					wherebuf.append(" AND tid = ?");
					setting.add(tid);
				}
			
				if(!acqcd.equals("") && acqcd != null) {			
					wherebuf.append(" AND ACQ_CD IN (");

					//setString 해야하는 parameter 개수만큼 물음표로 채워야 함.
					String[] paramTemp = new String[acqcdexp.length];

					for(int i = 0; i<acqcdexp.length; i++) {
						paramTemp[i] = "?";
						setting.add(acqcdexp[i]);
					}
					wherebuf.append(utilm.implode(", ", paramTemp)+")");
				}
				
				if(!tid2.equals("") && tid2 != null) {
					String[] tidval = tid2.split("_");
					wherebuf.append(" AND TID IN (?, ?, ?, ?)");
					setting.add(tidval[0]);
					setting.add(tidval[1]);
					setting.add(tidval[2]);
					setting.add(tidval[3]);
				}
				
				int Page=0;
				if(paging=="") {
					Page=0;
				}
				
				int SNUM = (Page*100)+1;
				int ENUM = (Page+1)*100;

				qrybuf.append("SELECT ");
				qrybuf.append("	RNUM, ");
				qrybuf.append("	SEQNO,  ");
				qrybuf.append("	APPGB, ");
				qrybuf.append("	DEP_NM		TR_DEPNM,  ");
				qrybuf.append("	TERM_NM		TR_TIDNM,  ");
				qrybuf.append("	TID		TR_TID,  ");
				qrybuf.append("	MID		TR_MID,  ");
				qrybuf.append("	PUR_NM		TR_ACQNM,  ");
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
				qrybuf.append("	TRANIDX		TR_SEQNO, ");
				qrybuf.append("	AUTHMSG		TR_RST_MSG ");
				qrybuf.append("FROM( ");
				qrybuf.append("	SELECT  ");
				qrybuf.append("		RNUM, SEQNO, DEP_NM, TERM_NM, TID, MID, PUR_NM, ");
				qrybuf.append("		APPDD, APPTM, OAPPDD, APPNO, APPGB, ");
				qrybuf.append("		CASE  ");
				qrybuf.append("			WHEN APPGB='A' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0015') ");
				setting.add(userexp[1]);
				qrybuf.append("			WHEN APPGB='C' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0016') ");
				setting.add(userexp[1]);
				qrybuf.append("		END APPGB_TXT, ");
				qrybuf.append("		CARDNO, AMOUNT, HALBU, ");
				qrybuf.append("		CASE  ");
				qrybuf.append("			WHEN CHECK_CARD='Y' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0019')  ");
				setting.add(userexp[1]);
				qrybuf.append("			ELSE (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0018') END CARDTP_TXT, ");
				setting.add(userexp[1]);
				qrybuf.append("		CASE ");
				qrybuf.append("			WHEN SIGNCHK='1' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0021')  ");
				setting.add(userexp[1]);
				qrybuf.append("			ELSE (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0022') END SIGNCHK_TXT, ");
				setting.add(userexp[1]);
				qrybuf.append("		REQ_DD, AUTHCD, REG_DD, RTN_CD, ");
				qrybuf.append("		CASE ");
				qrybuf.append("			WHEN RTN_CD IS NULL THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0024')  ");
				setting.add(userexp[1]);
				qrybuf.append("			WHEN RTN_CD IN('60', '67') THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0025') ");
				setting.add(userexp[1]);
				qrybuf.append("			WHEN RTN_CD IN('61', '64') THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0026')  ");
				setting.add(userexp[1]);
				qrybuf.append("		END RTN_TXT, ");
				qrybuf.append("		EXP_DD, EXT_FIELD, T1.TRANIDX, AUTHMSG ");
				qrybuf.append("		,CASE WHEN TLINEGB IS NOT NULL THEN (SELECT CODE_VAL FROM TB_BAS_CODE WHERE TRIM(CODE_NO)=TRIM(TLINEGB)) END TLINEGBTXT ");
				qrybuf.append("		,ADD_GB, ADD_CID, ADD_CD, ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, DEPO_DD ");
				qrybuf.append("	FROM( ");
				qrybuf.append("		SELECT ");
				qrybuf.append("			RNUM, SEQNO, BIZNO, TID, MID, VANGB, MDATE, SVCGB, TRANIDX, APPGB, ENTRYMD, ");
				qrybuf.append("			APPDD, APPTM, APPNO, CARDNO, HALBU, CURRENCY, AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX, ");
				qrybuf.append("			ISS_CD, ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG, CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB, ");
				qrybuf.append("			SIGNCHK, DDCGB, EXT_FIELD, OAPPNO, OAPPDD, OAPPTM, OAPP_AMT, ADD_GB, ADD_CID, ADD_CD, ");
				qrybuf.append("			ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, DEPO_DD ");
				qrybuf.append("		FROM( ");
				qrybuf.append("			SELECT ");
				qrybuf.append("				ROWNUM AS RNUM, SEQNO, BIZNO, TID, MID, VANGB, MDATE, SVCGB, TRANIDX, APPGB, ENTRYMD, ");
				qrybuf.append("				APPDD, APPTM, APPNO, CARDNO, HALBU, CURRENCY, AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX, ");
				qrybuf.append("				ISS_CD, ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG, CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB, ");
				qrybuf.append("				SIGNCHK, DDCGB, EXT_FIELD, OAPPNO, OAPPDD, OAPPTM, OAPP_AMT, ADD_GB, ADD_CID, ADD_CD, ");
				qrybuf.append("				ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, DEPOREQDD DEPO_DD,DPFLAG  ");
				qrybuf.append("			FROM ");
				qrybuf.append("				GLOB_MNG_ICVAN  ");
				qrybuf.append(wherebuf.toString());
				qrybuf.append("				order by appdd desc, apptm desc ");
				qrybuf.append("			)  ");
				qrybuf.append("WHERE RNUM BETWEEN "+SNUM+" AND "+ ENUM);
				qrybuf.append("	)T1 ");
				qrybuf.append("	LEFT OUTER JOIN( ");
				qrybuf.append("		SELECT EXP_DD, REQ_DD, REG_DD, APP_DD, TRANIDX, RSC_CD, RTN_CD FROM TB_MNG_DEPDATA ");
				qrybuf.append("	)T2 ON(T1.APPDD=T2.APP_DD AND T1.TRANIDX=T2.TRANIDX) ");
				qrybuf.append("	LEFT OUTER JOIN(  ");
				qrybuf.append("		SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD=?");
				setting.add(userexp[1]);
				qrybuf.append("	)T3 ON(T1.TID=T3.TERM_ID) ");
				qrybuf.append("	LEFT OUTER JOIN(  ");
				qrybuf.append("		SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD=? ");
				setting.add(userexp[1]);
				qrybuf.append("	)T4 ON(T3.DEP_CD=T4.DEP_CD) ");
				qrybuf.append("	LEFT OUTER JOIN( SELECT PUR_NM, PUR_OCD, PUR_KIS FROM TB_BAS_PURINFO)T5 ON (T1.ACQ_CD=T5.PUR_OCD OR T1.ACQ_CD=T5.PUR_KIS) ");
				qrybuf.append(") ");
				qrybuf.append("ORDER BY RNUM ASC ");

				//디버깅용
				utilm.debug_sql(qrybuf, setting);

				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString(), ResultSet.TYPE_FORWARD_ONLY);
				for(int k = 0; k<setting.size(); k++) {
					stmt.setString((k+1), setting.get(k));
				}
				
				rs = stmt.executeQuery();
				//setFatchSize - 테스트 해보고 변경하거나 제외
				rs.setFetchSize(500);

				int icnt = 1;
				while(rs.next()) {
					JSONObject tempObj = new JSONObject();
					JSONArray tempAry = new JSONArray();

					tempAry.add(icnt);
					for(int i = 0; i<pos_field.size(); i++) {
						//cardno change plz
						//카드번호가 있을 때만 decode -> 9 ~ 12번째 별표시
						if(pos_field.get(i).equals("TR_CARDNO")) {
							String newCardNo = utilm.cardno_masking(trans_seed_manager.seed_dec_card(rs.getString(pos_field.get(i)).trim()));
							tempAry.add(newCardNo);

						} else if(pos_field.get(i).equals("APPDD") || pos_field.get(i).equals("OAPPDD") || pos_field.get(i).equals("EXP_DD") || pos_field.get(i).equals("REG_DD") || pos_field.get(i).equals("DEPO_DD")) {
							//일자 필드일 때 YYYY/MM/DD 형태로 변경해서 출력
							//str_to_dateformat
							String tempDate = utilm.setDefault(rs.getString(pos_field.get(i)));
							String newDate = "";
							if(tempDate != null && !tempDate.equals("")) {
								newDate = utilm.str_to_dateformat(tempDate);
							}
							tempAry.add(newDate);
							//
						} else if (pos_field.get(i).equals("APPTM")) {
							String tempDate = utilm.setDefault(rs.getString(pos_field.get(i)));
							String newDate = "";
							if(tempDate != null && !tempDate.equals("")) {
								newDate = utilm.str_to_timeformat(tempDate);
							}
							tempAry.add(newDate);
						} else {
							//null check plz
							tempAry.add(utilm.setDefault(rs.getString(pos_field.get(i))));
						}
					}

					//2021.02.17 seqno 나중에 세팅해서 넣어줄 것 - 웹취소 관련 때문에.
					String seqno = rs.getString("SEQNO");		
					tempObj.put("id", seqno);
					tempObj.put("data", tempAry);

					sqlAry.add(tempObj);

					icnt++;
				}

				sqlobj.put("rows", sqlAry);
			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}
			return sqlobj.toJSONString();
		}
		
		// 2022.01.25 cvsnet - 월일자별조회 상세 갯수
		public String get_json_0204cnt_cvs(String tuser, String stime, String etime, String samt, String eamt, String appno, String cardtp, String auth01, String auth02, String auth03,
				String can01, String can02, String can03, String mid, String tid, String acqcd, String tid2) {
			Connection con2 = null;
			PreparedStatement stmt2 = null;
			ResultSet rs2 = null;

			StringBuffer qrybuf = new StringBuffer();
			StringBuffer wherebuf = new StringBuffer();
			
			JSONObject sqlobj = new JSONObject();
			JSONArray objAry = new JSONArray();
			String icnt = null;

			try {
				String[] userexp = tuser.split(":");
				String[] acqcdexp = acqcd.split(",");
				// 검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();
				
				//SET_WHERE SETTING
				wherebuf.append(" WHERE SVCGB IN ('CC', 'CE') AND AUTHCD='0000' AND TID IN (SELECT TID FROM TB_BAS_TIDMAP WHERE ORG_CD=? ");
				setting.add(userexp[1]);
				//1. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
				if(userexp[2] != null && !userexp[2].equals("")) {
					wherebuf.append(" AND DEP_CD = ?");
					setting.add(userexp[2]);
				}
				wherebuf.append(")");

				if(!stime.equals("") && stime != null) {
					wherebuf.append(" AND APPDD >= ?");
					setting.add(stime);
				}
				
				if(!etime.equals("") && etime != null) {
					wherebuf.append(" AND APPDD <= ?");
					setting.add(etime);
				}
				
				if(!samt.equals("") && samt != null) {
					wherebuf.append(" AND AMOUNT >= ?");
					setting.add(samt);
				}
				
				if(!eamt.equals("") && eamt != null) {
					wherebuf.append(" AND AMOUNT <= ?");
					setting.add(eamt);
				}
				
				if(!appno.equals("") && appno != null) {
					wherebuf.append(" AND APPNO=?");
					setting.add(appno);
				}
				
				if(!cardtp.equals("") && cardtp != null) {
					if(cardtp=="04") {
						wherebuf.append("TID ='39257746' AND EXT_FIELD IS NULL ");
					}else {
						wherebuf.append("TID ='39257746' AND EXT_FIELD LIKE ? "); 
						setting.add(cardtp);
					}
				}
				
				//auth01 전체, auth02 승인, auth03 취소
				if(!auth01.equals("Y")){
					if(auth02.equals("Y")){wherebuf.append(" AND APPGB = 'A'");}
					else if(auth03.equals("Y")){wherebuf.append(" AND APPGB = 'C'");}
					else if(auth02.equals("Y") && auth03.equals("Y")) {wherebuf.append(" AND APPGB IN ('A', 'C')");}
				}
				
				if(!can01.equals("Y")){
					if(can02.equals("Y")){wherebuf.append(" AND DPFLAG ='Y'");}
					else if(can03.equals("Y")){wherebuf.append(" AND ADD_CNT ='Y'");}
					else if(can02.equals("Y") && can03.equals("Y")) {wherebuf.append(" AND DPFLAG='Y' AND ADD_CNT='Y' ");}
				}
				
				if(!mid.equals("") && mid != null) {
					wherebuf.append(" AND MID = ? ");
					setting.add(mid);
				}

				if(!tid.equals("") && tid != null) {
					wherebuf.append(" AND tid = ?");
					setting.add(tid);
				}
			
				if(!acqcd.equals("") && acqcd != null) {			
					wherebuf.append(" AND ACQ_CD IN (");

					//setString 해야하는 parameter 개수만큼 물음표로 채워야 함.
					String[] paramTemp = new String[acqcdexp.length];

					for(int i = 0; i<acqcdexp.length; i++) {
						paramTemp[i] = "?";
						setting.add(acqcdexp[i]);
					}
					wherebuf.append(utilm.implode(", ", paramTemp)+")");
				}
				
				if(!tid2.equals("") && tid2 != null) {
					String[] tidval = tid2.split("_");
					wherebuf.append(" AND TID IN (?, ?, ?, ?)");
					setting.add(tidval[0]);
					setting.add(tidval[1]);
					setting.add(tidval[2]);
					setting.add(tidval[3]);
				}
				
				qrybuf.append("SELECT COUNT(1) MCNT FROM "+ userexp[5] +" T1 ");
				qrybuf.append(wherebuf.toString());
				
				//디버깅용
				utilm.debug_sql(qrybuf, setting);

				con2 = getOraConnect();
				stmt2 = con2.prepareStatement(qrybuf.toString());
				for(int k = 0; k<setting.size(); k++) {
					stmt2.setString((k+1), setting.get(k));
				}
				rs2 = stmt2.executeQuery();
				
				rs2.next();

				icnt = rs2.getString("MCNT");
			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con2,stmt2,rs2);
			}
			return icnt;
		}
		
		public String get_json_0205total(String tuser, String stime, String etime, String cardno, String appno) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			StringBuffer qrybuf = new StringBuffer();
			StringBuffer wherebuf = new StringBuffer();
			
			JSONObject jrtnobj = new JSONObject();
			JSONArray arr = new JSONArray();

			

			try {
				//tuser, stime, etime, acqcd, depcd, mid
				//tuser split
				String[] userexp = tuser.split(":");

				ArrayList<String> setting = new ArrayList<>();
				
				int nWcnt = 0;
				
				if(!stime.equals("") && stime != null) {
					wherebuf.append(" AND APP_DD >= ? ");
					setting.add(stime);
				}

				if(!etime.equals("") && etime != null) {
					wherebuf.append(" AND APP_DD <= ? ");
					setting.add(etime);
				}
				
				if(!cardno.equals("") && cardno != null) {
					wherebuf.append(" AND CARD_NO = ? ");
					setting.add(cardno);
				}
				if(!appno.equals("") && appno != null) {
					wherebuf.append(" AND APP_NO = ? ");
					setting.add(appno);
				}

				qrybuf.append("SELECT ");
				qrybuf.append("	T3.DEP_CD DEPCD ");
				qrybuf.append("    , DEP_NM ");
				qrybuf.append("    , TID ");
				qrybuf.append("    , BCNT ");
				qrybuf.append("    , BAMT ");
				qrybuf.append("    , BAN01 ");
				qrybuf.append("    , BAN02 ");
				qrybuf.append("    , BAN03 ");
				qrybuf.append("    , (SELECT AMOUNT FROM(SELECT TID, MID, SUM(SALE_AMT) AMOUNT FROM TB_MNG_DEPDATA WHERE (RSC_CD!='00' AND RSC_CD!='0000') "+wherebuf.toString()+" GROUP BY TID, MID)S1 LEFT OUTER JOIN(SELECT MER_NO, PUR_CD FROM TB_BAS_MERINFO)S2 ON(S1.MID=S2.MER_NO) LEFT OUTER JOIN( SELECT PUR_CD, PUR_KOCES ACC_CD FROM TB_BAS_PURINFO)S3 ON(S2.PUR_CD=S3.PUR_CD) WHERE T1.TID=S1.TID AND ACC_CD='1106') BC ");
				if(!wherebuf.equals("") && wherebuf != null) nWcnt++;
				qrybuf.append("    , (SELECT AMOUNT FROM(SELECT TID, MID, SUM(SALE_AMT) AMOUNT FROM TB_MNG_DEPDATA WHERE (RSC_CD!='00' AND RSC_CD!='0000') "+wherebuf.toString()+" GROUP BY TID, MID)S1 LEFT OUTER JOIN(SELECT MER_NO, PUR_CD FROM TB_BAS_MERINFO)S2 ON(S1.MID=S2.MER_NO) LEFT OUTER JOIN( SELECT PUR_CD, PUR_KOCES ACC_CD FROM TB_BAS_PURINFO)S3 ON(S2.PUR_CD=S3.PUR_CD) WHERE T1.TID=S1.TID AND ACC_CD='2211') NH ");
				if(!wherebuf.equals("") && wherebuf != null) nWcnt++;
				qrybuf.append("    , (SELECT AMOUNT FROM(SELECT TID, MID, SUM(SALE_AMT) AMOUNT FROM TB_MNG_DEPDATA WHERE (RSC_CD!='00' AND RSC_CD!='0000') "+wherebuf.toString()+" GROUP BY TID, MID)S1 LEFT OUTER JOIN(SELECT MER_NO, PUR_CD FROM TB_BAS_MERINFO)S2 ON(S1.MID=S2.MER_NO) LEFT OUTER JOIN( SELECT PUR_CD, PUR_KOCES ACC_CD FROM TB_BAS_PURINFO)S3 ON(S2.PUR_CD=S3.PUR_CD) WHERE T1.TID=S1.TID AND ACC_CD='1101') KB ");
				if(!wherebuf.equals("") && wherebuf != null) nWcnt++;
				qrybuf.append("    , (SELECT AMOUNT FROM(SELECT TID, MID, SUM(SALE_AMT) AMOUNT FROM TB_MNG_DEPDATA WHERE (RSC_CD!='00' AND RSC_CD!='0000') "+wherebuf.toString()+" GROUP BY TID, MID)S1 LEFT OUTER JOIN(SELECT MER_NO, PUR_CD FROM TB_BAS_MERINFO)S2 ON(S1.MID=S2.MER_NO) LEFT OUTER JOIN( SELECT PUR_CD, PUR_KOCES ACC_CD FROM TB_BAS_PURINFO)S3 ON(S2.PUR_CD=S3.PUR_CD) WHERE T1.TID=S1.TID AND ACC_CD='1104') SS ");
				if(!wherebuf.equals("") && wherebuf != null) nWcnt++;
				qrybuf.append("    , (SELECT AMOUNT FROM(SELECT TID, MID, SUM(SALE_AMT) AMOUNT FROM TB_MNG_DEPDATA WHERE (RSC_CD!='00' AND RSC_CD!='0000') "+wherebuf.toString()+" GROUP BY TID, MID)S1 LEFT OUTER JOIN(SELECT MER_NO, PUR_CD FROM TB_BAS_MERINFO)S2 ON(S1.MID=S2.MER_NO) LEFT OUTER JOIN( SELECT PUR_CD, PUR_KOCES ACC_CD FROM TB_BAS_PURINFO)S3 ON(S2.PUR_CD=S3.PUR_CD) WHERE T1.TID=S1.TID AND ACC_CD='1105') HN ");
				if(!wherebuf.equals("") && wherebuf != null) nWcnt++;
				qrybuf.append("    , (SELECT AMOUNT FROM(SELECT TID, MID, SUM(SALE_AMT) AMOUNT FROM TB_MNG_DEPDATA WHERE (RSC_CD!='00' AND RSC_CD!='0000') "+wherebuf.toString()+" GROUP BY TID, MID)S1 LEFT OUTER JOIN(SELECT MER_NO, PUR_CD FROM TB_BAS_MERINFO)S2 ON(S1.MID=S2.MER_NO) LEFT OUTER JOIN( SELECT PUR_CD, PUR_KOCES ACC_CD FROM TB_BAS_PURINFO)S3 ON(S2.PUR_CD=S3.PUR_CD) WHERE T1.TID=S1.TID AND ACC_CD='1103') LO ");
				if(!wherebuf.equals("") && wherebuf != null) nWcnt++;
				qrybuf.append("    , (SELECT AMOUNT FROM(SELECT TID, MID, SUM(SALE_AMT) AMOUNT FROM TB_MNG_DEPDATA WHERE (RSC_CD!='00' AND RSC_CD!='0000') "+wherebuf.toString()+" GROUP BY TID, MID)S1 LEFT OUTER JOIN(SELECT MER_NO, PUR_CD FROM TB_BAS_MERINFO)S2 ON(S1.MID=S2.MER_NO) LEFT OUTER JOIN( SELECT PUR_CD, PUR_KOCES ACC_CD FROM TB_BAS_PURINFO)S3 ON(S2.PUR_CD=S3.PUR_CD) WHERE T1.TID=S1.TID AND ACC_CD='1102') HD ");
				if(!wherebuf.equals("") && wherebuf != null) nWcnt++;
				qrybuf.append("    , (SELECT AMOUNT FROM(SELECT TID, MID, SUM(SALE_AMT) AMOUNT FROM TB_MNG_DEPDATA WHERE (RSC_CD!='00' AND RSC_CD!='0000') "+wherebuf.toString()+" GROUP BY TID, MID)S1 LEFT OUTER JOIN(SELECT MER_NO, PUR_CD FROM TB_BAS_MERINFO)S2 ON(S1.MID=S2.MER_NO) LEFT OUTER JOIN( SELECT PUR_CD, PUR_KOCES ACC_CD FROM TB_BAS_PURINFO)S3 ON(S2.PUR_CD=S3.PUR_CD) WHERE T1.TID=S1.TID AND ACC_CD='1107') SI ");
				if(!wherebuf.equals("") && wherebuf != null) nWcnt++;
				qrybuf.append("FROM(     ");
				qrybuf.append("    SELECT ");
				qrybuf.append("        TID ");
				qrybuf.append("        , SUM(BCNT) BCNT ");
				qrybuf.append("        , SUM(BAMT) BAMT ");
				qrybuf.append("        , SUM(BAN01) BAN01 ");
				qrybuf.append("        , SUM(BAN02) BAN02 ");
				qrybuf.append("        , SUM(BAN03) BAN03 ");
				qrybuf.append("    FROM(     ");
				qrybuf.append("        SELECT ");
				qrybuf.append("            TID ");
				qrybuf.append("            , CASE WHEN RSC_CD NOT IN('00', '0000') THEN COUNT(1) ELSE 0 END BCNT ");
				qrybuf.append("            , CASE WHEN RSC_CD NOT IN('00', '0000') THEN SUM(SALE_AMT) ELSE 0 END BAMT  ");
				qrybuf.append("            , CASE WHEN TRIM(RSC_CD)='OA39' THEN COUNT(1) ELSE 0 END BAN01 ");
				qrybuf.append("            , CASE WHEN TRIM(RSC_CD)='403' THEN COUNT(1) ELSE 0 END BAN02 ");
				qrybuf.append("            , CASE WHEN TRIM(RSC_CD)='OH07' THEN COUNT(1) ELSE 0 END BAN03 ");
				qrybuf.append("        FROM ");
				qrybuf.append("             "+userexp[6]);
				qrybuf.append("        WHERE RTN_CD IN ('64', '61') ");
				qrybuf.append(wherebuf.toString());
				if(!wherebuf.equals("") && wherebuf != null) nWcnt++;
				qrybuf.append("        GROUP BY TID, RSC_CD       ");
				qrybuf.append("    ) ");
				qrybuf.append("    GROUP BY TID ");
				qrybuf.append(")T1 ");
				qrybuf.append("LEFT OUTER JOIN( ");
				qrybuf.append("    SELECT TERM_ID, DEP_CD FROM TB_BAS_TIDMST ");
				qrybuf.append(")T2 ON(T1.TID=T2.TERM_ID) ");
				qrybuf.append("LEFT OUTER JOIN( ");
				qrybuf.append("    SELECT DEP_CD, DEP_NM FROM TB_BAS_DEPART ");
				qrybuf.append(")T3 ON(T2.DEP_CD=T3.DEP_CD) ");
				qrybuf.append("WHERE T3.DEP_CD  IS NOT NULL ");

				ArrayList<String> preSet = new ArrayList<>();
				
				for(int j = 0; j < nWcnt; j++) {
					for(int k = 0; k < setting.size(); k++) {
						preSet.add(setting.get(k));
					}
				}
				//디버깅용
				utilm.debug_sql(qrybuf, preSet);

				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				
				for(int k = 0; k < preSet.size(); k++) {
					stmt.setString(k+1, preSet.get(k));
				}
				

				
				rs = stmt.executeQuery();
				
				int icnt = 1;
				
				long bcnt = 0, bamt = 0, ban01 = 0, ban02 = 0, ban03 = 0, bctot = 0, nhtot=0, kbtot = 0, sstot = 0, hntot = 0, lotot = 0, hdtot = 0, sitot = 0;
						
				while(rs.next()) {
					JSONObject obj1 = new JSONObject();
					JSONArray arr2 = new JSONArray();
					
					bcnt	+=  Integer.parseInt(rs.getString("BCNT"));
					bamt	+=  Integer.parseInt(rs.getString("BAMT"));
					ban01	+=  Integer.parseInt(rs.getString("BAN01"));
					ban02	+=  Integer.parseInt(rs.getString("BAN02"));
					ban03	+=  Integer.parseInt(rs.getString("BAN03"));
					bctot	+=  Integer.parseInt(rs.getString("BC"));
					nhtot	+=  Integer.parseInt(rs.getString("NH"));
					kbtot	+=  Integer.parseInt(rs.getString("KB"));
					sstot	+=  Integer.parseInt(rs.getString("SS"));
					hntot	+=  Integer.parseInt(rs.getString("HN"));
					lotot	+=  Integer.parseInt(rs.getString("LO"));
					hdtot	+=  Integer.parseInt(rs.getString("HD"));
					sitot	+=  Integer.parseInt(rs.getString("SI"));
					
					arr2.add(icnt);
					arr2.add(rs.getString("DEPCD"));
					arr2.add(rs.getString("DEP_NM"));
					arr2.add(rs.getString("BCNT"));
					arr2.add(rs.getString("BAMT"));
					arr2.add(rs.getString("BAN01"));
					arr2.add(rs.getString("BAN02"));
					arr2.add(rs.getString("BAN03"));
					arr2.add("");
					arr2.add(rs.getString("BC"));
					arr2.add(rs.getString("NH"));
					arr2.add(rs.getString("KB"));
					arr2.add(rs.getString("SS"));
					arr2.add(rs.getString("HN"));
					arr2.add(rs.getString("LO"));
					arr2.add(rs.getString("HD"));
					arr2.add(rs.getString("SI"));

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
				arr2.add(bcnt);
				arr2.add(bamt);
				arr2.add(ban01);
				arr2.add(ban02);
				arr2.add(ban03);
				arr2.add("");
				arr2.add(bctot);
				arr2.add(nhtot);
				arr2.add(kbtot);
				arr2.add(sstot);
				arr2.add(hntot);
				arr2.add(lotot);
				arr2.add(hdtot);
				arr2.add(sitot);
				
				obj1.put("id", "total");
				obj1.put("data", arr2);
				
				arr.add(0, obj1);

				jrtnobj.put("rows", arr);

			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}

			return jrtnobj.toJSONString();

		}
		
		public String get_json_0205item(String tuser, String stime, String etime, String cardno, String appno) {
			Connection con2 = null;
			PreparedStatement stmt2 = null;
			ResultSet rs2 = null;
			
			
			StringBuffer qrybuf = new StringBuffer();
			StringBuffer wherebuf = new StringBuffer();

			//2021.03.02 수정 :: DEPTOT-ACQ_CD가 없어요...
			StringBuffer acqcdBuf = new StringBuffer();

			JSONObject sqlobj = new JSONObject();
			JSONArray objAry = new JSONArray();

			try {
				//tuser split
				String[] userexp = tuser.split(":");
				//검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();
				
				wherebuf.append(" WHERE (T1.RTN_CD='61' OR T1.RTN_CD='64') AND T1.MID IN (SELECT MID FROM TB_BAS_MIDMAP WHERE ORG_CD = ? ) ");
				setting.add(userexp[1]);
				
				if(!stime.equals("") && stime != null) {
					wherebuf.append(" AND APP_DD >= ? ");
					setting.add(stime);
				}

				if(!etime.equals("") && etime != null) {
					wherebuf.append(" AND APP_DD <= ? ");
					setting.add(etime);
				}
				
				if(!cardno.equals("") && cardno != null) {
					wherebuf.append(" AND CARD_NO = ? ");
					setting.add(trans_seed_manager.seed_enc_str(cardno));
				}
				if(!appno.equals("") && appno != null) {
					wherebuf.append(" AND APP_NO = ? ");
					setting.add(appno);
				}
				
				qrybuf.append("SELECT ");
				qrybuf.append("    CASE ");
				qrybuf.append("        WHEN RTN_CD='61' THEN '승인반송' ");
				qrybuf.append("        WHEN RTN_CD='64' THEN '취소반송' ");
				qrybuf.append("    END TRANTYPE, ");
				qrybuf.append("    T2.DEP_CD, ");
				qrybuf.append("    T3.DEP_NM, ");
				qrybuf.append("    RSC_CD, ");
				qrybuf.append("    RS_MSG, ");
				qrybuf.append("    T1.TID, ");
				qrybuf.append("    T1.MID, ");
				qrybuf.append("    CARD_NO, ");
				qrybuf.append("    T5.PUR_NM, ");
				qrybuf.append("    SALE_AMT, ");
				qrybuf.append("    HALBU, ");
				qrybuf.append("    APP_DD, ");
				qrybuf.append("    APP_NO, ");
				qrybuf.append("    REQ_DD ");
				qrybuf.append("FROM  ");
				qrybuf.append("    TB_MNG_DEPDATA T1 ");
				qrybuf.append("LEFT OUTER JOIN( ");
				qrybuf.append("    SELECT TERM_ID, DEP_CD FROM TB_BAS_TIDMST ");
				qrybuf.append(")T2 ON(T1.TID=T2.TERM_ID) ");
				qrybuf.append("LEFT OUTER JOIN( ");
				qrybuf.append("    SELECT DEP_CD, DEP_NM  FROM TB_BAS_DEPART ");
				qrybuf.append(")T3 ON(T2.DEP_CD=T3.DEP_CD) ");
				qrybuf.append("LEFT OUTER JOIN( ");
				qrybuf.append("    SELECT MER_NO, PUR_CD FROM TB_BAS_MERINFO ");
				qrybuf.append(")T4 ON(T1.MID=T4.MER_NO) ");
				qrybuf.append("LEFT OUTER JOIN( ");
				qrybuf.append("    SELECT PUR_NM, PUR_CD FROM TB_BAS_PURINFO ");
				qrybuf.append(")T5 ON(T4.PUR_CD=T5.PUR_CD) ");
				qrybuf.append(wherebuf.toString());

				//디버깅용
				utilm.debug_sql(qrybuf, setting);

				con2 = getOraConnect();
				stmt2 = con2.prepareStatement(qrybuf.toString());
				for(int k = 0; k < setting.size(); k++) {
					stmt2.setString((k+1), setting.get(k));
				}
				rs2 = stmt2.executeQuery();

				int rows = 1;

				while(rs2.next()) {
					 
					JSONObject tempObj = new JSONObject();
					JSONArray tempAry = new JSONArray();
					
					String newCardNo = utilm.cardno_masking(trans_seed_manager.seed_dec_card(rs2.getString("CARD_NO").trim()));
					tempAry.add(rows);
					tempAry.add(utilm.setDefault(rs2.getString("TRANTYPE")));
					tempAry.add(utilm.setDefault(rs2.getString("DEP_CD")));
					tempAry.add(utilm.setDefault(rs2.getString("DEP_NM")));
					tempAry.add(utilm.setDefault(rs2.getString("RSC_CD")));
					tempAry.add(utilm.setDefault(rs2.getString("RS_MSG")));
					tempAry.add(utilm.setDefault(rs2.getString("TID")));
					tempAry.add(utilm.setDefault(rs2.getString("MID")));
					tempAry.add(utilm.setDefault(newCardNo));
					tempAry.add(utilm.setDefault(rs2.getString("PUR_NM")));
					tempAry.add(utilm.checkNumberData(rs2.getString("SALE_AMT")));
					tempAry.add(utilm.setDefault(rs2.getString("HALBU")));
					tempAry.add(utilm.str_to_dateformat_deposit(rs2.getString("APP_DD")));
					tempAry.add(utilm.setDefault(rs2.getString("APP_NO")));
					tempAry.add(utilm.setDefault(rs2.getString("REQ_DD")));
					tempAry.add("");
					tempAry.add("");

					tempObj.put("id", rows);
					tempObj.put("data", tempAry);

					objAry.add(tempObj);

					rows++;
				}

				sqlobj.put("rows", objAry);

			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}

			return sqlobj.toJSONString();
		}
		
		

		//2021.02.23 강원대병원v3 - 현금영수증 거래내역상세보기
		@SuppressWarnings("unchecked")
		public String get_detail_0211(String seqno, String tuser, String appno) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			JSONObject sqlobj = new JSONObject();
			JSONArray sqlAry = new JSONArray();

			StringBuffer qrybuf = new StringBuffer();
			StringBuffer wherebuf = new StringBuffer();
			try {
				//tuser split
				String[] userexp = tuser.split(":");

				qrybuf.append("SELECT SEQNO, BIZNO, TID, VANGB, MDATE, SVCGB, TRANIDX, APPGB, ENTRYMD, APPDD, APPTM, APPNO, CARDNO");
				qrybuf.append(", HALBU, CURRENCY, AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX, ISS_CD, ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG");
				qrybuf.append(", CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB, TLINEGBTXT, SIGNCHK, DDCGB, OAPPNO, OAPPDD, OAPPTM, OAPP_AMT");
				qrybuf.append(", ADD_GB, ADD_CID, ADD_CD, ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, DEP_NM, TERM_NM FROM ( ");
				qrybuf.append("SELECT SEQNO, BIZNO, TID, MID, VANGB, MDATE, SVCGB, TRANIDX, APPGB, ENTRYMD, APPDD, APPTM, APPNO, CARDNO, HALBU, CURRENCY");
				qrybuf.append(", AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX, ISS_CD, ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG, CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB");
				qrybuf.append(", CASE WHEN TLINEGB IS NOT NULL THEN (SELECT CODE_VAL FROM TB_BAS_CODE WHERE TRIM(CODE_NO)=TRIM(TLINEGB)) END TLINEGBTXT");
				qrybuf.append(", SIGNCHK, DDCGB, OAPPNO, OAPPDD, OAPPTM, OAPP_AMT, ADD_GB, ADD_CID, ADD_CD, ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, DEP_NM, TERM_NM ");
				qrybuf.append("FROM " + userexp[5] + " T1 ");
				qrybuf.append("LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD = ? )T3 ON(T1.TID=T3.TERM_ID) ");
				qrybuf.append("LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART)T4 ON(T3.DEP_CD=T4.DEP_CD) ");
				qrybuf.append("WHERE SVCGB IN ('CB') AND AUTHCD='0000' AND TID IN (SELECT TID FROM TB_BAS_TIDMAP WHERE ORG_CD = ? ) ");
				qrybuf.append("AND SEQNO = ? AND APPNO = ? ) order by appdd desc, apptm DESC");

				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				stmt.setString(1, userexp[1]);
				stmt.setString(2, userexp[1]);
				stmt.setString(3, seqno);
				stmt.setString(4, appno);

				rs = stmt.executeQuery();

				while(rs.next()) {
					JSONObject tempObj = new JSONObject();

					tempObj.put("TRANIDX", utilm.setDefault(rs.getString("TRANIDX")));
					tempObj.put("ADD_CID", rs.getString("ADD_CID"));
					tempObj.put("APPDD", utilm.str_to_dateformat(rs.getString("APPDD")));
					tempObj.put("OAPPDD", utilm.str_to_dateformat(rs.getString("APPDD")));
					tempObj.put("APPTM", utilm.str_to_timeformat(rs.getString("APPTM")));
					tempObj.put("OAPPTM", utilm.str_to_timeformat(rs.getString("OAPPTM")));
					String appgb = rs.getString("APPGB");
					if(appgb.equals("A")) {
						appgb="승인";
					} else {
						appgb="취소";
					}
					tempObj.put("APPGB", appgb);
					String cardno_dec = utilm.cardno_masking(trans_seed_manager.seed_dec_card(rs.getString("CARDNO").trim()));
					tempObj.put("CARDNO", cardno_dec);
					tempObj.put("HALBU", rs.getString("HALBU"));
					tempObj.put("AMOUNT", rs.getString("AMOUNT"));
					tempObj.put("APPNO", rs.getString("APPNO"));
					tempObj.put("AUTHCD", rs.getString("AUTHCD"));
					tempObj.put("AUTHMSG", rs.getString("AUTHMSG"));
					tempObj.put("SEQNO", rs.getString("SEQNO"));

					sqlAry.add(tempObj);
				}

				sqlobj.put("TOTAL", 1);
				sqlobj.put("ITEMS", sqlAry);

			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}

			return sqlobj.toJSONString();
		}

		//2021.02.22 강원대병원v3 - 현금영수증 search total
		@SuppressWarnings("unchecked")
		public String get_json_0211total(String tuser, String stime, String etime, String samt, String eamt, String appno, String pid, 
				String mediid, String medi_cd, String medi_gb, String cardno, String tradeidx, String auth01, String auth02, String auth03){
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			JSONObject sqlobj = new JSONObject();
			JSONArray sqlAry = new JSONArray();

			StringBuffer qrybuf = new StringBuffer();
			StringBuffer wherebuf = new StringBuffer();

			try {

				//tuser split
				String[] userexp = tuser.split(":");
				//검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();

				wherebuf.append(" WHERE SVCGB IN ('CB') AND AUTHCD='0000' AND TID IN (select tid from tb_bas_tidmap WHERE ORG_CD = ? ");
				setting.add(userexp[1]);
				//1. userexp에 dep_cd 잡혀있을 경우
				if(userexp[2] != null && !userexp[2].equals("")) {
					wherebuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				wherebuf.append(")");

				//2. STIME, ETIME SETTING
				if(!stime.equals("") && stime != null) {
					wherebuf.append(" AND APPDD >= ?");
					setting.add(stime);
				}
				if(!etime.equals("") && etime != null) {
					wherebuf.append(" AND APPDD <= ?");
					setting.add(etime);
				}

				//3. SAMT, EAMT SETTING
				if(!samt.equals("") && samt != null) {
					wherebuf.append(" AND AMOUNT >= ?");
					setting.add(samt);
				}
				if(!eamt.equals("") && eamt != null) {
					wherebuf.append(" AND AMOUNT <= ?");
					setting.add(eamt);
				}

				//4. APPNO SETTING
				if(!appno.equals("") && appno != null) {
					wherebuf.append(" AND appno = ?");
					setting.add(appno);
				}

				//5. PID SETTING
				if(!pid.equals("") && pid != null) {
					wherebuf.append(" AND ADD_CID = ?");
					setting.add(pid);
				}

				//6. mediid setting
				if(!mediid.equals("") && mediid != null) {
					wherebuf.append(" AND ADD_CASHER = ?");
					setting.add(mediid);
				}

				//7. medi_cd setting
				if(!medi_cd.equals("") && medi_cd != null) {
					wherebuf.append(" AND ADD_CD = ?");
					setting.add(medi_cd);
				}

				//8. medi_gb setting		
				if(!medi_gb.equals("") && medi_gb != null) {
					wherebuf.append(" AND ADD_GB IN (?, ?)");
					if(medi_gb.equals("1")) {setting.add("1"); setting.add("O");}
					if(medi_gb.equals("2")) {setting.add("2"); setting.add("E");}
					if(medi_gb.equals("3")) {setting.add("3"); setting.add("I");}
					if(medi_gb.equals("4")) {setting.add("4"); setting.add("C");}
					if(medi_gb.equals("5")) {setting.add("5"); setting.add("G");}
					if(medi_gb.equals("6")) {setting.add("6"); setting.add("6");}
				}

				//9. CARDNO SETTING
				if(!cardno.equals("") && cardno != null) {
					wherebuf.append(" AND MEDI_GOODS LIKE ?");
					setting.add(medi_cd+"%");
				}

				//10. TRANIDX SETTING
				if(!tradeidx.equals("") && tradeidx != null) {
					wherebuf.append(" AND TRANIDX = ?");
					setting.add(tradeidx);
				}

				//11. auth setting
				if(!auth01.equals("Y")){
					if(auth02.equals("Y")){wherebuf.append(" AND APPGB = 'A'");}
					else if(auth03.equals("Y")){wherebuf.append(" AND APPGB = 'C'");}
					else if(auth02.equals("Y") && auth03.equals("Y")) {wherebuf.append(" AND APPGB IN ('A', 'C')");}
				}


				qrybuf.append("SELECT DEP_NM, TERM_NM, TID, ACNT, CCNT, AAMT, CAMT, TOTCNT, TOTAMT FROM ( ");
				qrybuf.append("SELECT TID, SUM(ACNT) ACNT, SUM(CCNT) CCNT, SUM(AAMT) AAMT, SUM(CAMT) CAMT, SUM(ACNT)+SUM(CCNT) TOTCNT, SUM(AAMT)-SUM(CAMT) TOTAMT FROM ( ");
				qrybuf.append("SELECT TID, CASE WHEN APPGB='A' THEN COUNT(1) ELSE 0 END ACNT, CASE WHEN APPGB='C' THEN COUNT(1) ELSE 0 END CCNT, ");
				qrybuf.append("CASE WHEN APPGB='A' THEN SUM(AMOUNT) ELSE 0 END AAMT, CASE WHEN APPGB='C' THEN SUM(AMOUNT) ELSE 0 END CAMT FROM ( ");
				qrybuf.append("SELECT SEQNO, TID, SVCGB, TRANIDX, APPGB, APPDD, APPTM, APPNO, CARDNO, AMOUNT, ");
				qrybuf.append("OAPPNO, OAPPDD, OAPPTM, OAPP_AMT, ADD_GB, ADD_CID, ADD_CD, ADD_CNT, ADD_CASHER, MEDI_GOODS FROM ");
				qrybuf.append(userexp[5]);
				qrybuf.append(wherebuf.toString());
				qrybuf.append(" ) GROUP BY TID, APPGB ");
				qrybuf.append(") GROUP BY TID ");
				qrybuf.append(") T1 ");

				//left outer join
				qrybuf.append("LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD = ? ");
				setting.add(userexp[1]);
				if(userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" and dep_cd = ? ");
					setting.add(userexp[2]);
				}
				qrybuf.append(")T3 ON(T1.TID=T3.TERM_ID) ");
				qrybuf.append("LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD = ? ");
				setting.add(userexp[1]);
				if(userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" and dep_cd = ? ");
					setting.add(userexp[2]);
				}
				qrybuf.append(")T4 ON(T3.DEP_CD=T4.DEP_CD) ");

				//디버깅용
				utilm.debug_sql(qrybuf, setting);

				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				for(int k = 0; k<setting.size(); k++) {
					stmt.setString((k+1), setting.get(k));
				}

				rs = stmt.executeQuery();

				//합계부분 계산
				int total_acnt = 0, total_ccnt = 0;
				long total_aamt = 0, total_camt = 0;

				int icnt = 1;
				while(rs.next()) {
					//checkNumberData
					JSONObject tempObj = new JSONObject();
					JSONArray tempAry = new JSONArray();

					//순번, 사업부, 승인건수, 승인금액, 취소건수, 취소금액, 총건수, 합계금액
					int acnt = Integer.parseInt(utilm.checkNumberData(rs.getString("ACNT")));
					int ccnt = Integer.parseInt(utilm.checkNumberData(rs.getString("CCNT")));
					long aamt = Long.parseLong(utilm.checkNumberData(rs.getString("AAMT")));
					long camt = Long.parseLong(utilm.checkNumberData(rs.getString("CAMT")));

					total_acnt += acnt;
					total_ccnt += ccnt;
					total_aamt += aamt;
					total_camt += camt;

					tempAry.add(icnt);
					//강원대병원_원무(검진(NICE):671010001)
					tempAry.add(rs.getString("DEP_NM")+"("+rs.getString("TERM_NM")+":"+rs.getString("TID")+")");
					tempAry.add(acnt);
					tempAry.add(aamt);
					tempAry.add(ccnt);
					tempAry.add(camt);
					tempAry.add(acnt+ccnt);
					tempAry.add(aamt-camt);

					tempObj.put("id", icnt);
					tempObj.put("data", tempAry);
					tempObj.put("css", "");

					sqlAry.add(tempObj);

					icnt++;
				}

				//합계부분 계산
				JSONObject tempObj = new JSONObject();
				JSONArray tempAry = new JSONArray();

				tempAry.add("");
				tempAry.add("합계");
				tempAry.add(total_acnt);
				tempAry.add(total_aamt);
				tempAry.add(total_ccnt);
				tempAry.add(total_camt);
				tempAry.add(total_acnt + total_ccnt);
				tempAry.add(total_aamt - total_camt);

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

		//2021.02.22 강원대병원v3 - 현금영수증 search item
		@SuppressWarnings("unchecked")
		public String get_json_0211item(String tuser, String stime, String etime, String samt, String eamt, String appno, String pid, 
				String mediid, String medi_cd, String medi_gb, String cardno, String tradeidx, String auth01, String auth02, String auth03){
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			JSONObject sqlobj = new JSONObject();
			JSONArray sqlAry = new JSONArray();

			StringBuffer qrybuf = new StringBuffer();
			StringBuffer wherebuf = new StringBuffer();

			try {

				//tuser split
				String[] userexp = tuser.split(":");
				//검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();

				ArrayList<String> pos_field = get_column_field(tuser, "tr", "field");

				qrybuf.append("SELECT SEQNO, APPGB, DEP_NM TR_DEPNM, TERM_NM TR_TIDNM, TID TR_TID, PUR_NM TR_ACQNM, APPDD TR_APPDD, APPTM TR_APPTM, OAPPDD TR_OAPPDD, APPNO TR_APPNO, ");
				qrybuf.append("APPGB_TXT TR_AUTHTXT,CARDNO TR_CARDNO, AMOUNT TR_AMT, AUTHCD TR_RST_CD, TRANTYPE TRANTYPE, ADD_CID ADD_PID, ADD_GB ADD_PGB, ADD_CASHER ADD_CID, ");
				qrybuf.append("ADD_CD ADD_CD, ADD_RECP ADD_RECP, TRANIDX TR_SEQNO, AUTHMSG TR_RST_MSG FROM ( ");
				qrybuf.append("SELECT SEQNO, DEP_NM, TERM_NM, TID, PUR_NM, APPDD, APPTM, OAPPDD, APPNO, APPGB, CARDNO, AMOUNT, AUTHCD, ");
				qrybuf.append("CASE WHEN APPGB='A' THEN '승인' WHEN APPGB='C' THEN '취소' END APPGB_TXT, ");
				qrybuf.append("CASE WHEN DDCGB='0' THEN '소득공제' WHEN DDCGB='1' THEN '지출증빙' END TRANTYPE, ");
				qrybuf.append("EXT_FIELD, TRANIDX, AUTHMSG, CASE WHEN TLINEGB IS NOT NULL THEN (SELECT CODE_VAL FROM TB_BAS_CODE WHERE TRIM(CODE_NO)=TRIM(TLINEGB)) END TLINEGBTXT, ");
				qrybuf.append("CASE WHEN ADD_GB IN ('1', 'O') THEN '외래' WHEN ADD_GB IN ('2', 'E') THEN '응급' WHEN ADD_GB IN ('3', 'I') THEN '입원' WHEN ADD_GB IN ('4', 'G') THEN '종합검진' ");
				qrybuf.append("WHEN ADD_GB='5' THEN '일반검진' WHEN ADD_GB='6' THEN '장례식장' ELSE '' END ADD_GB, ADD_CID, ADD_CD, ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, DEPO_DD ");
				qrybuf.append("from ( ");
				qrybuf.append("SELECT SEQNO, BIZNO, TID, VANGB, MDATE, SVCGB, T1.TRANIDX, T1.APPGB, ENTRYMD, T1.APPDD, APPTM, T1.APPNO, T1.CARDNO, HALBU, CURRENCY, T1.AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX, ");
				qrybuf.append("ISS_CD, ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG, CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB, SIGNCHK, DDCGB, EXT_FIELD, OAPPNO, OAPPDD, OAPPTM, OAPP_AMT, ADD_GB, ADD_CID, ADD_CD, ");
				qrybuf.append("ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, PUR_NM, DEP_NM, TERM_NM, DEPOREQDD DEPO_DD ");
				qrybuf.append("from ");
				qrybuf.append(userexp[5] + " T1 ");

				//LEFT OUTER JOIN
				qrybuf.append("LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD = ? ");
				setting.add(userexp[1]);
				if(userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" and dep_cd = ? ");
					setting.add(userexp[2]);
				}
				qrybuf.append(")T3 ON(T1.TID=T3.TERM_ID) ");
				qrybuf.append("LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD = ? ");
				setting.add(userexp[1]);
				if(userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" and dep_cd = ? ");
					setting.add(userexp[2]);
				}
				qrybuf.append(")T4 ON(T3.DEP_CD=T4.DEP_CD) ");
				qrybuf.append("LEFT OUTER JOIN( SELECT PUR_NM, PUR_OCD, PUR_KOCES, PUR_CD FROM TB_BAS_PURINFO)T5 ON (T1.ACQ_CD=T5.PUR_OCD OR T1.ACQ_CD=T5.PUR_KOCES OR T1.ACQ_CD=T5.PUR_CD)");

				//where절 setting
				wherebuf.append(" WHERE SVCGB IN ('CB') AND AUTHCD='0000' AND TID IN (select tid from tb_bas_tidmap WHERE ORG_CD = ? ");
				setting.add(userexp[1]);
				//1. userexp에 dep_cd 잡혀있을 경우
				if(userexp[2] != null && !userexp[2].equals("")) {
					wherebuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				wherebuf.append(")");

				//2. STIME, ETIME SETTING
				if(!stime.equals("") && stime != null) {
					wherebuf.append(" AND APPDD >= ?");
					setting.add(stime);
				}
				if(!etime.equals("") && etime != null) {
					wherebuf.append(" AND APPDD <= ?");
					setting.add(etime);
				}

				//3. SAMT, EAMT SETTING
				if(!samt.equals("") && samt != null) {
					wherebuf.append(" AND AMOUNT >= ?");
					setting.add(samt);
				}
				if(!eamt.equals("") && eamt != null) {
					wherebuf.append(" AND AMOUNT <= ?");
					setting.add(eamt);
				}

				//4. APPNO SETTING
				if(!appno.equals("") && appno != null) {
					wherebuf.append(" AND appno = ?");
					setting.add(appno);
				}

				//5. PID SETTING
				if(!pid.equals("") && pid != null) {
					wherebuf.append(" AND ADD_CID = ?");
					setting.add(pid);
				}

				//6. mediid setting
				if(!mediid.equals("") && mediid != null) {
					wherebuf.append(" AND ADD_CASHER = ?");
					setting.add(mediid);
				}

				//7. medi_cd setting
				if(!medi_cd.equals("") && medi_cd != null) {
					wherebuf.append(" AND ADD_CD = ?");
					setting.add(medi_cd);
				}

				//8. medi_gb setting		
				if(!medi_gb.equals("") && medi_gb != null) {
					wherebuf.append(" AND ADD_GB IN (?, ?)");
					if(medi_gb.equals("1")) {setting.add("1"); setting.add("O");}
					if(medi_gb.equals("2")) {setting.add("2"); setting.add("E");}
					if(medi_gb.equals("3")) {setting.add("3"); setting.add("I");}
					if(medi_gb.equals("4")) {setting.add("4"); setting.add("C");}
					if(medi_gb.equals("5")) {setting.add("5"); setting.add("G");}
					if(medi_gb.equals("6")) {setting.add("6"); setting.add("6");}
				}

				//9. CARDNO SETTING
				if(!cardno.equals("") && cardno != null) {
					wherebuf.append(" AND MEDI_GOODS LIKE ?");
					setting.add(medi_cd+"%");
				}

				//10. TRANIDX SETTING
				if(!tradeidx.equals("") && tradeidx != null) {
					wherebuf.append(" AND TRANIDX = ?");
					setting.add(tradeidx);
				}

				//11. auth setting
				if(!auth01.equals("Y")){
					if(auth02.equals("Y")){wherebuf.append(" AND APPGB = 'A'");}
					else if(auth03.equals("Y")){wherebuf.append(" AND APPGB = 'C'");}
					else if(auth02.equals("Y") && auth03.equals("Y")) {wherebuf.append(" AND APPGB IN ('A', 'C')");}
				}

				qrybuf.append(wherebuf.toString());
				qrybuf.append(" order by appdd desc, apptm desc");
				qrybuf.append(") ");
				qrybuf.append(") ");

				//디버깅용
				utilm.debug_sql(qrybuf, setting);

				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				for(int k = 0; k<setting.size(); k++) {
					stmt.setString((k+1), setting.get(k));
				}

				rs = stmt.executeQuery();
				rs.setFetchSize(500);

				int icnt = 1;
				while(rs.next()) {
					JSONObject tempObj = new JSONObject();
					JSONArray tempAry = new JSONArray();

					tempAry.add(icnt);
					for(int i = 0; i<pos_field.size(); i++) {
						if(pos_field.get(i).equals("TR_CARDNO")) {
							String newCardNo = utilm.cardno_masking(trans_seed_manager.seed_dec_card(rs.getString(pos_field.get(i)).trim()));
							tempAry.add(newCardNo);

						} else if(pos_field.get(i).equals("TR_APPDD") || pos_field.get(i).equals("TR_OAPPDD")) {
							String tempDate = utilm.setDefault(rs.getString(pos_field.get(i)));
							String newDate = "";
							if(tempDate != null && !tempDate.equals("")) {
								newDate = utilm.str_to_dateformat(tempDate);
							}
							tempAry.add(newDate);

						//2021.02.22 취소일자 부분 어떻게 처리해야함?
						} else if (pos_field.get(i).equals("TR_CANDD")){
							tempAry.add("");
						} else if (pos_field.get(i).equals("TR_APPTM")) {
							String tempDate = utilm.setDefault(rs.getString(pos_field.get(i)));
							String newDate = "";
							if(tempDate != null && !tempDate.equals("")) {
								newDate = utilm.str_to_timeformat(tempDate);
							}
							tempAry.add(newDate);
						} else {
							tempAry.add(utilm.setDefault(rs.getString(pos_field.get(i))));
						}

					}
					
					String seqno = rs.getString("SEQNO");
					tempObj.put("id", seqno);
					tempObj.put("data", tempAry);

					sqlAry.add(tempObj);

					icnt++;
				}

				sqlobj.put("rows", sqlAry);

			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}

			return sqlobj.toJSONString();
		}
		
		//2021.03.15 강원대병원 - 매출달력 - 카드/현금
		//날짜별 합계 데이터만 return, 나머지는 gowas에서 처리하는 걸로(detail)
		public String get_json_0210_cal(String tuser, String syear, String smon, String acqcd, String mid, String tid, String depcd, String type) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			JSONObject sqlobj = new JSONObject();
			JSONArray sqlAry = new JSONArray();

			//현금영수증, 신용카드 둘다 불러와야 함
			StringBuffer qrybuf = new StringBuffer();
			StringBuffer wherebuf = new StringBuffer();

			try {
				//tuser split
				String[] userexp = tuser.split(":");
				//acqcd split
				String[] acqcdexp = acqcd.split(",");
				//검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();

				wherebuf.append(" WHERE SVCGB ");
				if(type.equals("CARD")) {
					//CARD
					wherebuf.append("IN ('CC', 'CE') ");
				} else {
					//CASH
					wherebuf.append("IN ('CB') ");
				}
				wherebuf.append("AND AUTHCD='0000'");
				wherebuf.append(" AND TID IN (select tid from tb_bas_tidmap WHERE org_cd= ?");
				setting.add(userexp[1]);

				//1. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
				if(!depcd.equals("") && depcd != null) {
					wherebuf.append(" and dep_cd = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					wherebuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				wherebuf.append(")");

				//2. syear, smon
				if(syear != "" && smon != "") {
					wherebuf.append("AND APPDD LIKE ? ");
					setting.add(syear + smon + '%');
				}

				//3. acqcd setting
				if(!acqcd.equals("") && acqcd != null) {			
					wherebuf.append(" AND ACQ_CD IN (");

					//setString 해야하는 parameter 개수만큼 물음표로 채워야 함.
					String[] paramTemp = new String[acqcdexp.length];

					for(int i = 0; i<acqcdexp.length; i++) {
						paramTemp[i] = "?";
						setting.add(acqcdexp[i]);
					}
					wherebuf.append(utilm.implode(", ", paramTemp)+")");
				}

				//4. tid setting
				if(!tid.equals("") && tid != null) {
					wherebuf.append(" AND tid = ?");
					setting.add(tid);
				}

				//5. mid setting
				if(!mid.equals("") && mid != null) {
					wherebuf.append(" AND mid = ?");
					setting.add(mid);
				}

				qrybuf.append("select ");
				qrybuf.append("appdd, sum(aamt)-sum(camt) TOTAMT, sum(acnt)+sum(ccnt) TOTCNT ");
				qrybuf.append("from( ");
				qrybuf.append("select appdd, appgb, ");
				qrybuf.append("case when appgb='A' then sum(amount) else 0 end aamt, ");
				qrybuf.append("case when appgb='A' then count(1) else 0 end acnt, ");
				qrybuf.append("case when appgb='C' then sum(amount) else 0 end camt, ");
				qrybuf.append("case when appgb='C' then count(1) else 0 end ccnt ");
				qrybuf.append("from ");
				qrybuf.append(userexp[5]);
				qrybuf.append(wherebuf.toString());
				qrybuf.append(" group by appdd, appgb ");
				qrybuf.append(") group by appdd ");
				qrybuf.append("order by appdd ASC");

				//디버깅용
				utilm.debug_sql(qrybuf, setting);

				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				for(int k = 0; k<setting.size(); k++) {
					stmt.setString((k+1), setting.get(k));
				}

				rs = stmt.executeQuery();

				int rcnt = 1;
				while(rs.next()) {
					JSONObject tempObj = new JSONObject();
					JSONArray tempAry = new JSONArray();
					
					tempAry.add(utilm.setDefault(rs.getString("APPDD")));
					tempAry.add(utilm.setDefault(rs.getString("TOTAMT")));
					tempAry.add(utilm.setDefault(rs.getString("TOTCNT")));
					
					tempObj.put("id", rcnt);
					tempObj.put("data", tempAry);
					
					sqlAry.add(tempObj);
				}
				
				sqlobj.put("rows", sqlAry);

			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}

			return sqlobj.toJSONString();
		}
		
		public String get_json_0210_cal_total(String tuser, String syear, String smon, String acqcd, String mid, String tid, String depcd) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			JSONObject sqlobj = new JSONObject();
			JSONArray sqlAry = new JSONArray();
			
			StringBuffer wherebuf = new StringBuffer();
			StringBuffer qrybuf = new StringBuffer();
			
			try {
				
				//tuser split
				String[] userexp = tuser.split(":");
				//acqcd split
				String[] acqcdexp = acqcd.split(",");
				//검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();
				
				wherebuf.append(" WHERE AUTHCD='0000' ");
				wherebuf.append("AND TID IN (select tid from tb_bas_tidmap  where org_cd = ? ");
				
				setting.add(userexp[1]);

				//1. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
				if(!depcd.equals("") && depcd != null) {
					wherebuf.append(" and dep_cd = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					wherebuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				wherebuf.append(")");

				//2. syear, smon
				if(syear != "" && smon != "") {
					wherebuf.append("AND APPDD LIKE ? ");
					setting.add(syear + smon + '%');
				}

				//3. acqcd setting
				if(!acqcd.equals("") && acqcd != null) {			
					wherebuf.append(" AND ACQ_CD IN (");

					//setString 해야하는 parameter 개수만큼 물음표로 채워야 함.
					String[] paramTemp = new String[acqcdexp.length];

					for(int i = 0; i<acqcdexp.length; i++) {
						paramTemp[i] = "?";
						setting.add(acqcdexp[i]);
					}
					wherebuf.append(utilm.implode(", ", paramTemp)+")");
				}

				//4. tid setting
				if(!tid.equals("") && tid != null) {
					wherebuf.append(" AND tid = ?");
					setting.add(tid);
				}

				//5. mid setting
				if(!mid.equals("") && mid != null) {
					wherebuf.append(" AND mid = ?");
					setting.add(mid);
				}
				

				qrybuf.append("SELECT T1.MID, ACQ_CD, APPDD, ACNT, CCNT, AAMT, CAMT ");
				qrybuf.append("FROM( ");
				qrybuf.append("SELECT MID, ACQ_CD, APPDD, SUM(ACNT) ACNT, SUM(CCNT) CCNT, SUM(AAMT) AAMT, SUM(CAMT) CAMT ");
				qrybuf.append("FROM( ");
				qrybuf.append("SELECT MID, ACQ_CD, APPDD, ");
				qrybuf.append("CASE WHEN APPGB='A' THEN COUNT(1) ELSE 0 END ACNT, ");
				qrybuf.append("CASE WHEN APPGB='A' THEN SUM(AMOUNT) ELSE 0 END AAMT, ");
				qrybuf.append("CASE WHEN APPGB='C' THEN COUNT(1) ELSE 0 END CCNT, ");
				qrybuf.append("CASE WHEN APPGB='C' THEN SUM(AMOUNT) ELSE 0 END CAMT ");
				qrybuf.append("FROM ");
				qrybuf.append(userexp[5]);
				qrybuf.append(wherebuf.toString());
				qrybuf.append("GROUP BY MID, ACQ_CD, APPDD, APPGB ");
				qrybuf.append(")T1 GROUP BY MID, ACQ_CD, APPDD ORDER BY MID ");
				qrybuf.append(")T1 ");
				
				//left outer join
				qrybuf.append("LEFT OUTER JOIN(SELECT MER_NO, PUR_CD, DEP_CD FROM TB_BAS_MERINFO WHERE ORG_CD = ? ");
				setting.add(userexp[1]);
				if(!depcd.equals("") && depcd != null) {
					qrybuf.append(" and dep_cd = ?");
					//1-2. 사업부 검색을 선택하긴 했는데 login session 에서 이미 사업부가 있다면
					if(userexp[2] != null && !userexp[2].equals("")) {
						setting.add(userexp[2]);
					} else {
						setting.add(depcd);
					}
					//1-2. 사업부선택 검색기능을 누르진 않았는데 기본적인 login session 에서 사업부가 지정되어 있는 경우
				} else if(userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				qrybuf.append(")TM ON(T1.MID=TM.MER_NO) ORDER BY APPDD ASC");
				
				//디버깅용
				utilm.debug_sql(qrybuf, setting);
				
				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				for(int k = 0; k<setting.size(); k++) {
					stmt.setString((k+1), setting.get(k));
				}
				
				rs = stmt.executeQuery();
				
				//합계부분
				long cash_aamt = 0, cash_camt = 0, card_aamt = 0, card_camt = 0;
				int cash_acnt = 0, cash_ccnt = 0, card_acnt = 0, card_ccnt = 0;
				
				while(rs.next()) {
					long aamt = 0, camt = 0;
					int acnt = 0, ccnt = 0;
					
					String rs_mid = utilm.setDefault(rs.getString("MID"));
					String rs_acqcd = utilm.setDefault(rs.getString("ACQ_CD"));
					
					//mid, acq_cd 값이 없을경우 현금, 아닐경우 카드
					if(rs_mid != "" && rs_acqcd != "") {
						card_aamt += Long.parseLong(utilm.checkNumberData(rs.getString("AAMT")));
						card_camt += Long.parseLong(utilm.checkNumberData(rs.getString("CAMT")));
						card_acnt += Integer.parseInt(utilm.checkNumberData(rs.getString("ACNT")));
						card_ccnt += Integer.parseInt(utilm.checkNumberData(rs.getString("CCNT")));
					} else {
						cash_aamt += Long.parseLong(utilm.checkNumberData(rs.getString("AAMT")));
						cash_camt += Long.parseLong(utilm.checkNumberData(rs.getString("CAMT")));
						cash_acnt += Integer.parseInt(utilm.checkNumberData(rs.getString("ACNT")));
						cash_ccnt += Integer.parseInt(utilm.checkNumberData(rs.getString("CCNT")));
					}
				}
				
				JSONObject tempObj = new JSONObject();
				JSONArray tempAry = new JSONArray();
				
				//신용 total
				tempAry.add("");
				tempAry.add("신용");
				tempAry.add(card_acnt);
				tempAry.add(card_aamt);
				tempAry.add(card_ccnt);
				tempAry.add(card_camt);
				tempAry.add(card_acnt + card_ccnt);
				tempAry.add(card_aamt - card_camt);
				
				tempObj.put("id", "card");
				tempObj.put("data", tempAry);
				sqlAry.add(tempObj);
				
				tempObj = new JSONObject();
				tempAry = new JSONArray();
				
				//현금 total
				tempAry.add("");
				tempAry.add("현금");
				tempAry.add(cash_acnt);
				tempAry.add(cash_aamt);
				tempAry.add(cash_ccnt);
				tempAry.add(cash_camt);
				tempAry.add(cash_acnt + cash_ccnt);
				tempAry.add(cash_aamt - cash_camt);
				
				tempObj.put("id", "cash");
				tempObj.put("data", tempAry);
				sqlAry.add(tempObj);
				
				//합계 total
				tempObj = new JSONObject();
				tempAry = new JSONArray();
				tempAry.add("");
				tempAry.add("합계");
				tempAry.add(card_acnt + cash_acnt);
				tempAry.add(card_aamt + cash_aamt);
				tempAry.add(card_ccnt + cash_ccnt);
				tempAry.add(card_camt + cash_camt);
				tempAry.add((card_acnt + card_ccnt) + (cash_acnt + cash_ccnt));
				tempAry.add((card_aamt - card_camt) + (cash_aamt - cash_camt));
				
				tempObj.put("id", "total");
				tempObj.put("data", tempAry);
				sqlAry.add(tempObj);
				
				sqlobj.put("rows", sqlAry);
				
			} catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con,stmt,rs);
			}
			
			return sqlobj.toJSONString();
		}
}
