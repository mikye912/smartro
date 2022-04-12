package com.gaon.ifou.v3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class db2_pos_manager {
	// 2022.01.25 cvsnet - 월일자별조회 total
			@SuppressWarnings("unchecked")
			public String get_json_0102total_cvs(String tuser, String syear, String smon, String depcd) {
				Connection con = null;
				PreparedStatement stmt = null;
				ResultSet rs = null;
				
				JSONObject sqlobj = new JSONObject();
				JSONArray sqlAry = new JSONArray();

				StringBuffer wherebuf = new StringBuffer();
				StringBuffer qrybuf = new StringBuffer();

				try {
					// tuser, syear, smon, samt, eamt, depcd
					// tuser split
					String[] userexp = tuser.split(":");
					// 검색항목에 따른 where 조건절 setting 관련 변수
					ArrayList<String> setting = new ArrayList<>();
					
					// 1. syear, smon
					if (syear != "" && smon != "") {
						wherebuf.append(" WHERE CAPPDD LIKE ? ");
						setting.add(syear + smon + "%");
					}

					// 2. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
					if (!depcd.equals("") && depcd != null) {
						if(depcd.equals("1")) {
							wherebuf.append(" AND CGNM LIKE 'GS25%'");
						}else if(depcd.equals("2")) {
							wherebuf.append(" AND CGNM LIKE 'GSSM%'");
						}else if(depcd.equals("3")) {
							wherebuf.append(" AND CGNM LIKE 'lala%'");
						}	
					}

					qrybuf.append("SELECT NVL(SUM(ACNT),0) ACNT, NVL(SUM(CCNT),0) CCNT, NVL(SUM(AAMT),0) AAMT, NVL(SUM(CAMT),0) CAMT FROM (");
					qrybuf.append("SELECT CAPPGB ");
					qrybuf.append(", CASE WHEN CAPPGB='A' THEN COUNT(1) ELSE 0 END ACNT");
					qrybuf.append(", CASE WHEN CAPPGB='C' THEN COUNT(1) ELSE 0 END CCNT");
					qrybuf.append(", CASE WHEN CAPPGB='A' THEN SUM(CAMOUNT) ELSE 0 END AAMT");
					qrybuf.append(", CASE WHEN CAPPGB='C' THEN SUM(CAMOUNT) ELSE 0 END CAMT ");
					qrybuf.append(" FROM CVS_ADD_INFO T1");
					qrybuf.append(wherebuf.toString());
					qrybuf.append(" group by CAPPGB)");

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

					obj1.put("id", Integer.toString(icnt));
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

			// 2022.01.25 cvsnet - 월일자별조회 item
			@SuppressWarnings({ "static-access", "unchecked" })
			public String get_json_0102item_cvs(String tuser, String syear, String smon, String depcd) {
				Connection con = null;
				PreparedStatement stmt = null;
				ResultSet rs = null;
				
				JSONObject sqlobj = new JSONObject();
				JSONArray sqlAry = new JSONArray();

				StringBuffer qrybuf = new StringBuffer();
				StringBuffer wherebuf = new StringBuffer();

				try {
					// tuser, syear, smon, samt, eamt,  depcd
					// tuser split
					String[] userexp = tuser.split(":");
					// 검색항목에 따른 where 조건절 setting 관련 변수
					ArrayList<String> setting = new ArrayList<>();
					
					// 1. syear, smon
					if (syear != "" && smon != "") {
						wherebuf.append(" WHERE CAPPDD LIKE ? ");
						setting.add(syear + smon + "%");
					}

					// 2. loginSession에 depcd가 있거나 검색창에 depcd가 있을 경우
					if (!depcd.equals("") && depcd != null) {
						if(depcd.equals("1")) {
							wherebuf.append(" AND CGNM LIKE 'GS25%'");
						}else if(depcd.equals("2")) {
							wherebuf.append(" AND CGNM LIKE 'GSSM%'");
						}else if(depcd.equals("3")) {
							wherebuf.append(" AND CGNM LIKE 'lala%'");
						}	
					}

					qrybuf.append("SELECT CAPPDD, SUM(ACNT) ACNT, SUM(CCNT) CCNT, SUM(AAMT) AAMT, SUM(CAMT) CAMT FROM ( ");
					qrybuf.append("SELECT CAPPDD, CAPPGB");
					qrybuf.append(", CASE WHEN CAPPGB='A' THEN COUNT(1) ELSE 0 END ACNT");
					qrybuf.append(", CASE WHEN CAPPGB='C' THEN COUNT(1) ELSE 0 END CCNT");
					qrybuf.append(", CASE WHEN CAPPGB='A' THEN SUM(CAMOUNT) ELSE 0 END AAMT");
					qrybuf.append(", CASE WHEN CAPPGB='C' THEN SUM(CAMOUNT) ELSE 0 END CAMT");
					qrybuf.append(" FROM CVS_ADD_INFO T1");
					qrybuf.append( wherebuf.toString());
					qrybuf.append(" group by CAPPGB, CAPPDD)");
					
					qrybuf.append(" group by CAPPDD");
					qrybuf.append(" ORDER BY CAPPDD");

					// 디버깅용
					utilm.debug_sql(qrybuf, setting);

					con = getOraConnect();
					stmt = con.prepareStatement(qrybuf.toString());
					for (int k = 0; k < setting.size(); k++) {
						stmt.setString((k + 1), setting.get(k));
					}

					//DEP_NM, PUR_NM, T1.MID, ACQ_CD, APPDD, ACNT, CCNT, AAMT, CAMT
					ArrayList<String[]> tempStrAry = new ArrayList<>();
					String[] tempStr = new String[7];

					rs = stmt.executeQuery();
					int icnt = 1;
					while(rs.next()) {
						int tcnt = 0;
						long tamt = 0;
						//승인일자 YYYY-MM-DD
						String appdd = rs.getString("CAPPDD");
						tempStr[0] = appdd.substring(0, 4) + "-" + appdd.substring(4, 6) + "-" + appdd.substring(6);
						//tempStr[1] = rs.getString("DEP_NM");
						//tempStr[2] = rs.getString("PUR_NM");
						//tempStr[3] = rs.getString("MID");
						tempStr[1] = rs.getString("ACNT");
						tempStr[2] = rs.getString("AAMT");
						tempStr[3] = rs.getString("CCNT");
						tempStr[4] = rs.getString("CAMT");

						tcnt = Integer.parseInt(tempStr[1]) + Integer.parseInt(tempStr[3]); 
						tamt = Integer.parseInt(tempStr[2]) - Integer.parseInt(tempStr[4]); 

						tempStr[5] = Integer.toString(tcnt);
						tempStr[6] = Long.toString(tamt);

						//2021.02.16 null check
						for(int i = 0; i<tempStr.length; i++) {
							tempStr[i] = utilm.setDefault(tempStr[i]);
						}

						tempStrAry.add(tempStr);
						tempStr = new String[7];
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
							//tempAry.add(tempStr[7]);
							//tempAry.add(tempStr[8]);
							//tempAry.add(tempStr[9]);

							dtot_acnt += Integer.parseInt(tempStr[1]);
							dtot_aamt += Integer.parseInt(tempStr[2]);
							dtot_ccnt += Integer.parseInt(tempStr[3]);
							dtot_camt += Integer.parseInt(tempStr[4]);
							dtot_tcnt += Integer.parseInt(tempStr[5]);
							dtot_tamt += Integer.parseInt(tempStr[6]);

							total_acnt += Integer.parseInt(tempStr[1]);
							total_aamt += Integer.parseInt(tempStr[2]);
							total_ccnt += Integer.parseInt(tempStr[3]);
							total_camt += Integer.parseInt(tempStr[4]);
							total_tcnt += Integer.parseInt(tempStr[5]);
							total_tamt += Integer.parseInt(tempStr[6]);

							tempObj.put("id", icnt);
							tempObj.put("data", tempAry);
							tempObj.put("style", "font-color: red;");

							icnt++;
							sqlAry.add(tempObj);

							//소계부분
							if(!compareDay.equals(tempStr[0].indexOf(0)) || i == (tempStrAry.size()-1)) {
								tempObj = new JSONObject();
								tempAry = new JSONArray();

								tempAry.add("소계");
								//tempAry.add("");
								//tempAry.add("");
								//tempAry.add("");
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

								tempObj.put("id", "dt"+icnt);
								tempObj.put("class", "subtotal_grid");
								tempObj.put("data", tempAry);

								icnt++;
								sqlAry.add(tempObj);
							}
						}

						//합계부분
						JSONObject totalObj = new JSONObject();
						JSONArray totalAry = new JSONArray();

						totalAry.add("합계");
						//totalAry.add("");
						//totalAry.add("");
						//totalAry.add("");
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

			public String get_json_0104total_cvs(String tuser, String stime, String etime, String samt, String eamt,
					String appno, String tradeidx, String auth01, String auth02, String auth03, String mid,
					String tid, String acqcd, String tid2) {
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
					
					// 1. 청구일자
					if (!stime.equals("") && stime != null && !etime.equals("") && etime != null) {
						wherebuf.append(" AND T1.DEPOREQDD>=? AND T1.DEPOREQDD<=? ");
						setting.add(stime);
						setting.add(etime);
					}

					// 2. 승인금액
					if (!samt.equals("") && samt != null && !eamt.equals("") && eamt != null) {
						wherebuf.append(" AND T1.AMOUNT>=? AND T1.AMOUNT<=?");
						setting.add(samt);
						setting.add(samt);
					}

					//3. 승인번호
					if(!appno.equals("") && appno != null) {
						wherebuf.append(" AND T1.APPNO=? ");
						setting.add(appno);
					}
					
					//4. 카드사선택
					if(!acqcd.equals("") && acqcd != null) {
						wherebuf.append(" AND MID IN  (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD=? AND ORG_CD=? )");
						setting.add(acqcd);
						setting.add(userexp[1]);
					}
					
					//5. 단말기 선택
					if(!tid.equals("") && tid != null) {
						wherebuf.append(" AND TID = ?");
						setting.add(tid);
					}
					
				
					//6. 가맹점 번호
					if(!mid.equals("") && mid != null) {
						wherebuf.append(" AND MID = ?");
						setting.add(mid);
					}
					
					//7. 거래고유번호
					if(!tradeidx.equals("") && tradeidx != null) {
						wherebuf.append(" AND T1.TRANIDX=? ");
						setting.add(tradeidx);
					}

					//8.승인구분
					//auth01 전체, auth02 승인, auth03 취소
					if(!auth01.equals("Y")){
						if(auth02.equals("Y")){exwherebuf.append(" WHERE APPGB_TXT = '신용승인'");}
						else if(auth03.equals("Y")){exwherebuf.append(" WHERE APPGB_TXT = '신용취소'");}
						else if(auth02.equals("Y") && auth03.equals("Y")) {exwherebuf.append(" WHERE APPGB_TXT IN ('신용승인', '신용취소')");}
					}
					
					//9. 사업부 선택
					if(!tid2.equals("") && tid2 != null) {
						String[] tidval = tid2.split("_");
						wherebuf.append(" AND TID IN (?, ?, ?, ?)");
						setting.add(tidval[0]);
						setting.add(tidval[1]);
						setting.add(tidval[2]);
						setting.add(tidval[3]);
					}
					
					qrybuf.append("SELECT DEP_NM, TERM_ID, TERM_NM, ACNT, CCNT, AAMT, CAMT, TOTCNT, TOTAMT, BC, NH, KB, SS, HN, LO, HD, SI FROM(");
					qrybuf.append(" SELECT TID, SUM(ACNT) ACNT, SUM(CCNT) CCNT, SUM(AAMT) AAMT, SUM(CAMT) CAMT, SUM(ACNT)+SUM(CCNT) TOTCNT, SUM(AAMT)-SUM(CAMT) TOTAMT ");
					qrybuf.append(", SUM(ABC)-SUM(CBC) BC, SUM(ANH)-SUM(CNH) NH, SUM(AKB)-SUM(CKB) KB, SUM(ASS)-SUM(CSS) SS, SUM(AHN)-SUM(CHN) HN, SUM(ALO)-SUM(CLO) LO");
					qrybuf.append(", SUM(AHD)-SUM(CHD) HD, SUM(ASI)-SUM(CSI) SI FROM( ");
					qrybuf.append(" SELECT TID");
					qrybuf.append(", CASE WHEN APPGB='A' THEN COUNT(1) ELSE 0 END ACNT");
					qrybuf.append(", CASE WHEN APPGB='C' THEN COUNT(1) ELSE 0 END CCNT");
					qrybuf.append(", CASE WHEN APPGB='A' THEN SUM(AMOUNT) ELSE 0 END AAMT");
					qrybuf.append(", CASE WHEN APPGB='C' THEN SUM(AMOUNT) ELSE 0 END CAMT");
					qrybuf.append(", CASE WHEN APPGB='A' AND (ACQ_CD IN ('01', '0400') OR MID IN ('721225822','721219360','721176212')) THEN SUM(AMOUNT) ELSE 0 END ABC");
					qrybuf.append(", CASE WHEN APPGB='A' AND (ACQ_CD IN ('12', '0171') OR MID IN ('155072408','155068491','154944840')) THEN SUM(AMOUNT) ELSE 0 END ANH");
					qrybuf.append(", CASE WHEN APPGB='A' AND (ACQ_CD IN ('02', '0170') OR MID IN ('00098235865','00098234952','00098153744')) THEN SUM(AMOUNT) ELSE 0 END AKB");
					qrybuf.append(", CASE WHEN APPGB='A' AND (ACQ_CD IN ('04', '1300') OR MID IN ('179216254','179216357','179102374')) THEN SUM(AMOUNT) ELSE 0 END ASS");
					qrybuf.append(", CASE WHEN APPGB='A' AND (ACQ_CD IN ('03', '0505') OR MID IN ('00903280940','00903276708','00903164052')) THEN SUM(AMOUNT) ELSE 0 END AHN");
					qrybuf.append(", CASE WHEN APPGB='A' AND (ACQ_CD IN ('10', '1100') OR MID IN ('9957971095','9957975427','9956970402')) THEN SUM(AMOUNT) ELSE 0 END ALO");
					qrybuf.append(", CASE WHEN APPGB='A' AND (ACQ_CD IN ('09', '1200') OR MID IN ('178600027','178597603','178600545')) THEN SUM(AMOUNT) ELSE 0 END AHD");
					qrybuf.append(", CASE WHEN APPGB='A' AND (ACQ_CD IN ('05', '0300') OR MID IN ('0118799154','0118796648','0118721620')) THEN SUM(AMOUNT) ELSE 0 END ASI");
					qrybuf.append(", CASE WHEN APPGB='C' AND (ACQ_CD IN ('01', '0400') OR MID IN ('721225822','721219360','721176212')) THEN SUM(AMOUNT) ELSE 0 END CBC");
					qrybuf.append(", CASE WHEN APPGB='C' AND (ACQ_CD IN ('12', '0171') OR MID IN ('155072408','155068491','154944840')) THEN SUM(AMOUNT) ELSE 0 END CNH");
					qrybuf.append(", CASE WHEN APPGB='C' AND (ACQ_CD IN ('02', '0170') OR MID IN ('00098235865','00098234952','00098153744')) THEN SUM(AMOUNT) ELSE 0 END CKB");
					qrybuf.append(", CASE WHEN APPGB='C' AND (ACQ_CD IN ('04', '1300') OR MID IN ('179216254','179216357','179102374')) THEN SUM(AMOUNT) ELSE 0 END CSS");
					qrybuf.append(", CASE WHEN APPGB='C' AND (ACQ_CD IN ('03', '0505') OR MID IN ('00903280940','00903276708','00903164052'))  THEN SUM(AMOUNT) ELSE 0 END CHN");
					qrybuf.append(", CASE WHEN APPGB='C' AND (ACQ_CD IN ('10', '1100') OR MID IN ('9957971095','9957975427','9956970402')) THEN SUM(AMOUNT) ELSE 0 END CLO");
					qrybuf.append(", CASE WHEN APPGB='C' AND (ACQ_CD IN ('09', '1200') OR MID IN ('178600027','178597603','178600545')) THEN SUM(AMOUNT) ELSE 0 END CHD");
					qrybuf.append(", CASE WHEN APPGB='C' AND (ACQ_CD IN ('05', '0300') OR MID IN ('0118799154','0118796648','0118721620')) THEN SUM(AMOUNT) ELSE 0 END CSI");
					qrybuf.append(" FROM ( SELECT SEQNO, DEP_NM, TERM_NM, TID, MID, PUR_NM, ACQ_CD, APPDD, APPTM, OAPPDD, APPNO, APPGB, APPGB_TXT, CARDNO, AMOUNT, HALBU, CARDTP_TXT, SIGNCHK_TXT,");
					qrybuf.append(" REQ_DD, AUTHCD, REG_DD, RTN_CD, RTN_TXT, EXP_DD, EXT_FIELD, TRANIDX, AUTHMSG FROM( ");
					qrybuf.append(" SELECT SEQNO, DEP_NM, TERM_NM, TID, MID, PUR_NM, APPDD, APPTM, OAPPDD, APPNO, APPGB, ACQ_CD, ");
					qrybuf.append(" CASE WHEN APPGB='A' THEN '신용승인' WHEN APPGB='C' THEN '신용취소' END APPGB_TXT, CARDNO, AMOUNT, HALBU, ");
					qrybuf.append(" CASE WHEN CHECK_CARD='Y' THEN '체크카드' ELSE '신용카드' END CARDTP_TXT, CASE WHEN SIGNCHK='1' THEN '전자서명' ELSE '무서명' END SIGNCHK_TXT, ");
					qrybuf.append(" REQ_DD, AUTHCD, REG_DD, RTN_CD, ");
					qrybuf.append(" CASE WHEN RTN_CD IS NULL THEN '결과없음' WHEN RTN_CD IN('60', '67') THEN '정상매입' WHEN RTN_CD IN('61', '64') THEN '매입반송' END RTN_TXT, ");
					qrybuf.append(" EXP_DD, EXT_FIELD, TRANIDX, AUTHMSG FROM( ");
					qrybuf.append(" SELECT SEQNO, BIZNO, TID, MID, VANGB, MDATE, SVCGB, T1.TRANIDX, T1.APPGB, ENTRYMD, T1.APPDD, APPTM, T1.APPNO, T1.CARDNO, HALBU, CURRENCY, T1.AMOUNT, AMT_UNIT ");
					qrybuf.append(" , OAPP_AMT, AMT_TIP, AMT_TAX, ISS_CD, ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG, CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB, SIGNCHK, DDCGB, EXT_FIELD, OAPPNO");
					qrybuf.append(" , OAPPDD, OAPPTM, ADD_GB, ADD_CID, ADD_CD, ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, PUR_NM, DEP_NM, EXP_DD, REQ_DD, REG_DD, RSC_CD, RTN_CD, TERM_NM ");
					qrybuf.append(" FROM GLOB_MNG_ICVAN_CVS T1 ");
					qrybuf.append(" LEFT OUTER JOIN(SELECT EXP_DD, REQ_DD, REG_DD, APP_DD, TRANIDX, RSC_CD, RTN_CD FROM TB_MNG_DEPDATA)T2 ON(T1.APPDD=T2.APP_DD AND T1.TRANIDX=T2.TRANIDX) ");
					qrybuf.append(" LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD=?)T3 ON(T1.TID=T3.TERM_ID)");
					setting.add(userexp[1]);
					qrybuf.append(" LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD=?)T4 ON(T3.DEP_CD=T4.DEP_CD)");
					setting.add(userexp[1]);
					qrybuf.append(" LEFT OUTER JOIN( SELECT PUR_NM, PUR_OCD, PUR_KOCES FROM TB_BAS_PURINFO)T5 ON (T1.ACQ_CD=T5.PUR_OCD OR T1.ACQ_CD=T5.PUR_KOCES)");
					qrybuf.append(" WHERE SVCGB IN ('CC', 'CE') AND AUTHCD='0000' AND TID IN (select tid from tb_bas_tidmap where org_cd=? ");
					setting.add(userexp[1]);
					if(userexp[2] != null && !userexp[2].equals("")) {
						qrybuf.append(" and dep_cd = ?");
						setting.add(userexp[2]);
					}
					qrybuf.append(" ) ");
					qrybuf.append(wherebuf.toString());
					qrybuf.append(" order by appdd desc, apptm desc ) ) ");
					qrybuf.append(exwherebuf.toString());
					qrybuf.append("  ) GROUP BY TID, APPGB, ACQ_CD,MID ) GROUP BY TID )T2 ");
					qrybuf.append(" LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD=?)T3 ON(T2.TID=T3.TERM_ID)");
					setting.add(userexp[1]);
					qrybuf.append(" LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD=?)T4 ON(T3.DEP_CD=T4.DEP_CD)");
					setting.add(userexp[1]);
					
					// 디버깅용
					utilm.debug_sql(qrybuf, setting);

					con = getOraConnect();
					stmt = con.prepareStatement(qrybuf.toString());
					for (int k = 0; k < setting.size(); k++) {
						stmt.setString((k + 1), setting.get(k));
					}

					rs = stmt.executeQuery(); 

					int icnt = 1;
					long aamt = 0, camt = 0, totcsum = 0, totasum = 0, bctot = 0, nhtot=0, kbtot = 0, sstot = 0, hntot = 0, lotot = 0, hdtot = 0, sitot = 0;
					int acnt = 0, ccnt = 0;
					while (rs.next()) {
						acnt += Integer.parseInt(rs.getString("ACNT"));
						ccnt += Integer.parseInt(rs.getString("CCNT"));
						aamt += Integer.parseInt(rs.getString("AAMT"));
						camt += Integer.parseInt(rs.getString("CAMT"));
						totcsum += Integer.parseInt(rs.getString("TOTCNT"));
						totasum += Integer.parseInt(rs.getString("TOTAMT"));
						bctot	+=  Integer.parseInt(rs.getString("BC"));
						nhtot	+=  Integer.parseInt(rs.getString("NH"));
						kbtot	+=  Integer.parseInt(rs.getString("KB"));
						sstot	+=  Integer.parseInt(rs.getString("SS"));
						hntot	+=  Integer.parseInt(rs.getString("HN"));
						lotot	+=  Integer.parseInt(rs.getString("LO"));
						hdtot	+=  Integer.parseInt(rs.getString("HD"));
						sitot	+=  Integer.parseInt(rs.getString("SI"));
					}

					JSONObject obj1 = new JSONObject();
					JSONArray arr2 = new JSONArray();

					arr2.add("합계");
					arr2.add("");
					arr2.add(acnt);
					arr2.add(aamt);
					arr2.add(ccnt);
					arr2.add(camt);
					arr2.add(totcsum);
					arr2.add(totasum);
					arr2.add(bctot);
					arr2.add(nhtot);
					arr2.add(kbtot);
					arr2.add(sstot);
					arr2.add(hntot);
					arr2.add(lotot);
					arr2.add(hdtot);
					arr2.add(sitot);

					obj1.put("id", Integer.toString(icnt));
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


			public String get_json_0104item_cvs(String tuser, String stime, String etime, String samt, String eamt,
					String appno, String tradeidx, String auth01, String auth02, String auth03, String mid, String tid,
					String acqcd, String tid2) {
				Connection con = null;
				PreparedStatement stmt = null;
				ResultSet rs = null;

				JSONObject sqlobj = new JSONObject();
				JSONArray sqlAry = new JSONArray();

				StringBuffer wherebuf = new StringBuffer();
				StringBuffer exwherebuf = new StringBuffer();
				StringBuffer qrybuf = new StringBuffer();
				
				ArrayList<String> pos_field = get_column_field(tuser, "van", "field");

				try {
					// tuser, syear, smon, samt, eamt, depcd
					// tuser split
					String[] userexp = tuser.split(":");
					//acqcd split
					//String[] acqcdexp = acqcd.split(",");
					// 검색항목에 따른 where 조건절 setting 관련 변수
					ArrayList<String> setting = new ArrayList<>();
					
					// 1. 청구일자
					if (!stime.equals("") && stime != null && !etime.equals("") && etime != null) {
						wherebuf.append(" AND DEPOREQDD>=? AND DEPOREQDD<=? ");
						setting.add(stime);
						setting.add(etime);
					}

					// 2. 승인금액
					if (!samt.equals("") && samt != null && !eamt.equals("") && eamt != null) {
						wherebuf.append(" AND AMOUNT>=? AND AMOUNT<=?");
						setting.add(samt);
						setting.add(samt);
					}

					//3. 승인번호
					if(!appno.equals("") && appno != null) {
						wherebuf.append(" AND APPNO=? ");
						setting.add(appno);
					}
					
					//4. 카드사선택
					if(!acqcd.equals("") && acqcd != null) {
						wherebuf.append(" AND MID IN  (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD=? AND ORG_CD=? )");
						setting.add(acqcd);
						setting.add(userexp[1]);
					}
					
					//5. 단말기 선택
					if(!tid.equals("") && tid != null) {
						wherebuf.append(" AND TID = ?");
						setting.add(tid);
					}
					
				
					//6. 가맹점 번호
					if(!mid.equals("") && mid != null) {
						wherebuf.append(" AND MID = ?");
						setting.add(mid);
					}
					
					//7. 거래고유번호
					if(!tradeidx.equals("") && tradeidx != null) {
						wherebuf.append(" AND TRANIDX=? ");
						setting.add(tradeidx);
					}

					//8.승인구분
					//auth01 전체, auth02 승인, auth03 취소
					if(!auth01.equals("Y")){
						if(auth02.equals("Y")){wherebuf.append(" AND APPGB = 'A'");}
						else if(auth03.equals("Y")){wherebuf.append(" AND APPGB  = 'C'");}
						else if(auth02.equals("Y") && auth03.equals("Y")) {wherebuf.append(" AND APPGB IN ('A', 'C')");}
					}
					
					//9. 사업부 선택
					if(!tid2.equals("") && tid2 != null) {
						String[] tidval = tid2.split("_");
						wherebuf.append(" AND TID IN (?, ?, ?, ?) ");
						setting.add(tidval[0]);
						setting.add(tidval[1]);
						setting.add(tidval[2]);
						setting.add(tidval[3]);
					}

					
					qrybuf.append("SELECT RNUM, SEQNO, APPGB ");
					qrybuf.append(", DEP_NM	TR_DEPNM, ");
					qrybuf.append("	TERM_NM		TR_TIDNM, ");
					qrybuf.append("	TID			TR_TID, ");
					qrybuf.append("	MID			TR_MID, ");
					qrybuf.append("	PUR_NM		TR_ACQNM, ");
					qrybuf.append("	APPDD		TR_APPDD,");
					qrybuf.append("	APPTM		TR_APPTM,");
					qrybuf.append("	OAPPDD		TR_OAPPDD,");
					qrybuf.append("	APPNO		TR_APPNO, ");
					qrybuf.append("	APPGB_TXT	TR_AUTHTXT, ");
					qrybuf.append("	CARDNO		TR_CARDNO,	");
					qrybuf.append("	AMOUNT		TR_AMT,	");
					qrybuf.append("	HALBU		TR_HALBU, ");
					qrybuf.append("	CARDTP_TXT	TR_CARDTP, ");
					qrybuf.append("	TLINEGBTXT	TR_LINE,");
					qrybuf.append("	SIGNCHK_TXT TR_SIGN,");
					qrybuf.append("	AUTHCD		TR_RST_CD,");
					qrybuf.append("	DEPO_DD		DP_REQ_DD,");
					qrybuf.append("	REQ_DD		DP_RES_DD,	");
					qrybuf.append("	REG_DD		DP_REG_DD,");
					qrybuf.append("	RTN_TXT		DP_RST_TXT,");
					qrybuf.append("	EXP_DD		DP_EXP_DD,");
					qrybuf.append("	ADD_CID		ADD_PID,");
					qrybuf.append("	ADD_GB		ADD_PGB,");
					qrybuf.append("	ADD_CASHER	ADD_CID,");
					qrybuf.append("	TRANIDX		TR_SEQNO,");
					qrybuf.append("	AUTHMSG		TR_RST_MSG ");
					qrybuf.append(" FROM(");
					qrybuf.append("	SELECT ");
					qrybuf.append("		RNUM, SEQNO, DEP_NM, TERM_NM, TID, MID, PUR_NM,");
					qrybuf.append("		APPDD, APPTM,  OAPPDD, APPNO, APPGB,");
					qrybuf.append("		CASE ");
					qrybuf.append("			WHEN APPGB='A' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0015')");
					setting.add(userexp[1]);
					qrybuf.append("			WHEN APPGB='C' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0016')");
					setting.add(userexp[1]);
					qrybuf.append("		END APPGB_TXT,");
					qrybuf.append("		CARDNO, AMOUNT, HALBU,");
					qrybuf.append("		CASE ");
					qrybuf.append("			WHEN CHECK_CARD='Y' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0019') ");
					setting.add(userexp[1]);
					qrybuf.append("			ELSE (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0018') END CARDTP_TXT,");
					setting.add(userexp[1]);
					qrybuf.append("		CASE");
					qrybuf.append("			WHEN SIGNCHK='1' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0021') ");
					setting.add(userexp[1]);
					qrybuf.append("			ELSE (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0022') END SIGNCHK_TXT,");
					setting.add(userexp[1]);
					qrybuf.append("		REQ_DD, AUTHCD, REG_DD, RTN_CD,");
					qrybuf.append("		CASE");
					qrybuf.append("			WHEN RTN_CD IS NULL THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0024') ");
					setting.add(userexp[1]);
					qrybuf.append("			WHEN RTN_CD IN('60', '67') THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0025')");
					setting.add(userexp[1]);
					qrybuf.append("			WHEN RTN_CD IN('61', '64') THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0026') ");
					setting.add(userexp[1]);
					qrybuf.append("		END RTN_TXT,");
					qrybuf.append("		EXP_DD, EXT_FIELD, T1.TRANIDX, AUTHMSG");
					qrybuf.append("		,CASE WHEN TLINEGB IS NOT NULL THEN (SELECT CODE_VAL FROM TB_BAS_CODE WHERE TRIM(CODE_NO)=TRIM(TLINEGB)) END TLINEGBTXT");
					qrybuf.append("		,ADD_GB, ADD_CID, ADD_CD, ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, DEPO_DD");
					qrybuf.append("	FROM(");
					qrybuf.append("		SELECT");
					qrybuf.append("			RNUM, SEQNO, BIZNO, TID, MID, VANGB, MDATE, SVCGB, TRANIDX, APPGB, ENTRYMD,");
					qrybuf.append("			APPDD, APPTM, APPNO, CARDNO, HALBU, CURRENCY, AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX,");
					qrybuf.append("			ISS_CD, ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG, CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB,");
					qrybuf.append("			SIGNCHK, DDCGB, EXT_FIELD, OAPPNO, OAPPDD, OAPPTM, OAPP_AMT, ADD_GB, ADD_CID, ADD_CD,");
					qrybuf.append("			ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, DEPO_DD");
					qrybuf.append("		FROM(");
					qrybuf.append("			SELECT ");
					qrybuf.append("				ROWNUMBER() OVER() AS RNUM, SEQNO, BIZNO, TID, MID, VANGB, MDATE, SVCGB, TRANIDX, APPGB, ENTRYMD,");
					qrybuf.append("				APPDD, APPTM, APPNO, CARDNO, HALBU, CURRENCY, AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX,");
					qrybuf.append("				ISS_CD, ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG, CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB,");
					qrybuf.append("				SIGNCHK, DDCGB, EXT_FIELD, OAPPNO, OAPPDD, OAPPTM, OAPP_AMT, ADD_GB, ADD_CID, ADD_CD,");
					qrybuf.append("				ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, DEPO_DD");
					qrybuf.append("			FROM(    ");
					qrybuf.append("				SELECT");
					qrybuf.append("					SEQNO, BIZNO, TID, MID, VANGB, MDATE, SVCGB, TRANIDX, APPGB, ENTRYMD,");
					qrybuf.append("					APPDD, APPTM, APPNO, CARDNO, HALBU, CURRENCY, AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX,");
					qrybuf.append("					ISS_CD, ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG, CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB,");
					qrybuf.append("					SIGNCHK, DDCGB, EXT_FIELD, OAPPNO, OAPPDD, OAPPTM, OAPP_AMT, ADD_GB, ADD_CID, ADD_CD,");
					qrybuf.append("					ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, DEPOREQDD DEPO_DD");
					qrybuf.append("				FROM");
					qrybuf.append("					GLOB_MNG_ICVAN_CVS ");
					qrybuf.append(" WHERE SVCGB IN ('CC', 'CE') AND AUTHCD='0000' AND TID IN (select tid from tb_bas_tidmap where org_cd=? ");
					setting.add(userexp[1]);
					if(userexp[2] != null && !userexp[2].equals("")) {
						qrybuf.append(" and dep_cd = ?");
						setting.add(userexp[2]);
					}
					qrybuf.append(" )");
					qrybuf.append(wherebuf.toString());
					qrybuf.append("				order by appdd desc, apptm desc");
					qrybuf.append("			) ");
					qrybuf.append("		) ");
					//qrybuf.append(exwherebuf.toString());
					qrybuf.append("	)T1");
					qrybuf.append("	LEFT OUTER JOIN(");
					qrybuf.append("		SELECT EXP_DD, REQ_DD, REG_DD, APP_DD, TRANIDX, RSC_CD, RTN_CD FROM TB_MNG_DEPDATA");
					qrybuf.append("	)T2 ON(T1.APPDD=T2.APP_DD AND T1.TRANIDX=T2.TRANIDX)");
					qrybuf.append("	LEFT OUTER JOIN( ");
					qrybuf.append("		SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD=?");
					setting.add(userexp[1]);
					qrybuf.append("	)T3 ON(T1.TID=T3.TERM_ID)");
					qrybuf.append("	LEFT OUTER JOIN( ");
					qrybuf.append("		SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD=?");
					setting.add(userexp[1]);
					qrybuf.append("	)T4 ON(T3.DEP_CD=T4.DEP_CD)");
					qrybuf.append("	LEFT OUTER JOIN( SELECT PUR_NM, PUR_OCD, PUR_KIS FROM TB_BAS_PURINFO)T5 " );
					if(acqcd != null && !acqcd.equals("")) {
						qrybuf.append(" ON ( T5.PUR_OCD = ?  ) ");
						setting.add(acqcd);
					}else if(acqcd.equals("")) {
						qrybuf.append(" ON ( T5.PUR_OCD = ''  ) ");
					}
					
					qrybuf.append(")");
					qrybuf.append("ORDER BY RNUM ASC");
					
					// 디버깅용
					utilm.debug_sql(qrybuf, setting);

					con = getOraConnect();
					stmt = con.prepareStatement(qrybuf.toString());
					for (int k = 0; k < setting.size(); k++) {
						stmt.setString((k + 1), setting.get(k));
					}
					/*test
					stmt.setString(1, "OR0003");
					stmt.setString(2, "OR0003");
					stmt.setString(3, "OR0003");
					stmt.setString(4, "OR0003");
					stmt.setString(5, "OR0003");
					stmt.setString(6, "OR0003");
					stmt.setString(7, "OR0003");
					stmt.setString(8, "OR0003");
					stmt.setString(9, "OR0003");
					stmt.setString(10, "OR0003");
					stmt.setString(11, "MD1544676184");
					stmt.setString(12, "20211206");
					stmt.setString(13, "20211206");
					stmt.setString(14, "OR0003");
					stmt.setString(15, "OR0003");
					*/
					rs = stmt.executeQuery(); 

					int icnt = 1;
					while (rs.next()) {
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

							} else if(pos_field.get(i).equals("TR_APPDD") || pos_field.get(i).equals("TR_OAPPDD") || pos_field.get(i).equals("DP_EXP_DD") || pos_field.get(i).equals("DP_REG_DD") || pos_field.get(i).equals("DP_REQ_DD")) {
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
							}else{
								//null check plz
								try {
								arr2.add(utilm.setDefault(rs.getString(pos_field.get(i))));
								}catch(Exception e) {}
							}
						}

						obj1.put("id", Integer.toString(icnt));
						obj1.put("data", arr2);
		
						sqlAry.add(obj1);
						
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
			public String get_json_0104cnt_cvs(String tuser, String stime, String etime, String samt, String eamt,
					String appno, String tradeidx, String auth01, String auth02, String auth03, String mid, String tid,
					String acqcd, String tid2) {
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
					// 검색항목에 따른 where 조건절 setting 관련 변수
					ArrayList<String> setting = new ArrayList<>();
					
					wherebuf.append(" WHERE SVCGB IN ('CC', 'CE') AND AUTHCD='0000' AND TID IN (SELECT TID FROM TB_BAS_TIDMAP WHERE ORG_CD = ?) ");
					setting.add(userexp[1]);
					
					if(!stime.equals("") && stime != null) {
						wherebuf.append(" AND DEPOREQDD >= ? ");
						setting.add(stime);
					}
					
					if(!etime.equals("") && etime != null) {
						wherebuf.append(" AND DEPOREQDD <= ? ");
						setting.add(etime);
					}
					
					if(!samt.equals("") && samt != null) {
						wherebuf.append(" AND AMOUNT >= ? ");
						setting.add(samt);
					}
					
					if(!eamt.equals("") && eamt != null) {
						wherebuf.append(" AND AMOUNT <= ? ");
						setting.add(eamt);
					}
					
					if(!appno.equals("") && appno != null) {
						wherebuf.append(" AND APPNO <= ? ");
						setting.add(appno);
					}
					
					if(!tradeidx.equals("") && tradeidx != null) {
						wherebuf.append(" AND TRANIDX <= ? ");
						setting.add(tradeidx);
					}
					
					if(!auth01.equals("Y")){
						if(auth02.equals("Y")){wherebuf.append(" AND APPGB = 'A'");}
						else if(auth03.equals("Y")){wherebuf.append(" AND APPGB  = 'C'");}
						else if(auth02.equals("Y") && auth03.equals("Y")) {wherebuf.append(" AND APPGB IN ('A', 'C')");}
					}
					
					if(!mid.equals("") && mid != null) {
						wherebuf.append(" AND MID <= ? ");
						setting.add(mid);
					}
					
					if(!tid.equals("") && tid != null) {
						wherebuf.append(" AND TID <= ? ");
						setting.add(tid);
					}
					
					if(!acqcd.equals("") && acqcd != null) {
						wherebuf.append(" MID IN  (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD=? AND ORG_CD=? ) ");
						setting.add(acqcd);
						setting.add(userexp[1]);
					}
					
					if(!tid2.equals("") && tid2 != null) {
						String[] depcd = tid2.split("_");
						wherebuf.append(" AND TID IN (?, ?, ?, ?) ");
						setting.add(depcd[0]);
						setting.add(depcd[1]);
						setting.add(depcd[2]);
						setting.add(depcd[3]);
					}
					
					qrybuf.append("SELECT COUNT(1) MCNT FROM GLOB_MNG_ICVAN_CVS ");
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
			
		//청구승인일자별 토탈
		public String get_json_0106total_cvs(String tuser, String stime, String etime, String samt, String eamt,
				String appno, String tradeidx, String auth01, String auth02, String auth03, String mid,
				String tid, String acqcd, String tid2) {
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
				
				// 1. 승인일자
				if (!stime.equals("") && stime != null && !etime.equals("") && etime != null) {
					wherebuf.append(" AND T1.APPDD>=? AND T1.APPDD<=? ");
					setting.add(stime);
					setting.add(etime);
				}

				// 2. 승인금액
				if (!samt.equals("") && samt != null && !eamt.equals("") && eamt != null) {
					wherebuf.append(" AND T1.AMOUNT>=? AND T1.AMOUNT<=?");
					setting.add(samt);
					setting.add(samt);
				}

				//3. 승인번호
				if(!appno.equals("") && appno != null) {
					wherebuf.append(" AND T1.APPNO=? ");
					setting.add(appno);
				}
				
				//4. 카드사선택
				if(!acqcd.equals("") && acqcd != null) {
					wherebuf.append(" AND ACQ_CD IN  ( ? )");
					setting.add(acqcd);
				}
				
				//5. 단말기 선택
				if(!tid.equals("") && tid != null) {
					wherebuf.append(" AND TID = ?");
					setting.add(tid);
				}
				
			
				//6. 가맹점 번호
				if(!mid.equals("") && mid != null) {
					wherebuf.append(" AND MID = ?");
					setting.add(mid);
				}
				
				//7. 거래고유번호
				if(!tradeidx.equals("") && tradeidx != null) {
					wherebuf.append(" AND T1.TRANIDX=? ");
					setting.add(tradeidx);
				}

				//8.승인구분
				//auth01 전체, auth02 승인, auth03 취소
				if(!auth01.equals("Y")){
					if(auth02.equals("Y")){exwherebuf.append(" WHERE APPGB_TXT = '신용승인'");}
					else if(auth03.equals("Y")){exwherebuf.append(" WHERE APPGB_TXT = '신용취소'");}
					else if(auth02.equals("Y") && auth03.equals("Y")) {exwherebuf.append(" WHERE APPGB_TXT IN ('신용승인', '신용취소')");}
				}
				
				//9. 사업부 선택
				if(!tid2.equals("") && tid2 != null) {
					String[] tidval = tid2.split("_");
					wherebuf.append(" AND TID IN (?, ?, ?, ?) ");
					setting.add(tidval[0]);
					setting.add(tidval[1]);
					setting.add(tidval[2]);
					setting.add(tidval[3]);
				}
				
				qrybuf.append("SELECT ");
				qrybuf.append("     DEP_NM ");
				qrybuf.append("    ,TERM_ID ");
				qrybuf.append("    ,TERM_NM ");
				qrybuf.append("    ,ACNT ");
				qrybuf.append("    ,CCNT ");
				qrybuf.append("    ,AAMT ");
				qrybuf.append("    ,CAMT ");
				qrybuf.append("    ,TOTCNT ");
				qrybuf.append("    ,TOTAMT ");
				qrybuf.append("    ,BC ");
				qrybuf.append("    ,NH ");
				qrybuf.append("    ,KB ");
				qrybuf.append("    ,SS ");
				qrybuf.append("    ,HN ");
				qrybuf.append("    ,LO ");
				qrybuf.append("    ,HD ");
				qrybuf.append("    ,SI ");
				qrybuf.append("FROM(     ");
				qrybuf.append("    SELECT ");
				qrybuf.append("         TID ");
				qrybuf.append("        ,SUM(ACNT) ACNT ");
				qrybuf.append("        ,SUM(CCNT) CCNT ");
				qrybuf.append("        ,SUM(AAMT) AAMT ");
				qrybuf.append("        ,SUM(CAMT) CAMT ");
				qrybuf.append("        ,SUM(ACNT)+SUM(CCNT) TOTCNT ");
				qrybuf.append("        ,SUM(AAMT)-SUM(CAMT) TOTAMT ");
				qrybuf.append("        ,SUM(ABC  )-SUM(CBC  ) BC ");
				qrybuf.append("        ,SUM(ANH  )-SUM(CNH  ) NH ");
				qrybuf.append("        ,SUM(AKB  )-SUM(CKB  ) KB ");
				qrybuf.append("        ,SUM(ASS  )-SUM(CSS  ) SS ");
				qrybuf.append("        ,SUM(AHN  )-SUM(CHN  ) HN ");
				qrybuf.append("        ,SUM(ALO  )-SUM(CLO  ) LO ");
				qrybuf.append("        ,SUM(AHD  )-SUM(CHD  ) HD ");
				qrybuf.append("        ,SUM(ASI  )-SUM(CSI  ) SI ");
				qrybuf.append("    FROM(     ");
				qrybuf.append("        SELECT ");
				qrybuf.append("            TID ");
				qrybuf.append("            ,CASE WHEN APPGB='A' THEN COUNT(1) ELSE 0 END ACNT ");
				qrybuf.append("            ,CASE WHEN APPGB='C' THEN COUNT(1) ELSE 0 END CCNT ");
				qrybuf.append("            ,CASE WHEN APPGB='A' THEN SUM(AMOUNT) ELSE 0 END AAMT ");
				qrybuf.append("            ,CASE WHEN APPGB='C' THEN SUM(AMOUNT) ELSE 0 END CAMT ");
				qrybuf.append("            ,CASE WHEN APPGB='A' AND MID IN ('704855398', '768017318','707528764') THEN SUM(AMOUNT) ELSE 0 END ABC ");
				qrybuf.append("            ,CASE WHEN APPGB='A' AND MID IN ('140239694', '143275451','151558364') THEN SUM(AMOUNT) ELSE 0 END ANH ");
				qrybuf.append("            ,CASE WHEN APPGB='A' AND MID IN ('00052904921', '00087259990','00084542316') THEN SUM(AMOUNT) ELSE 0 END AKB ");
				qrybuf.append("            ,CASE WHEN APPGB='A' AND MID IN ('165138860', '128890479','167802984') THEN SUM(AMOUNT) ELSE 0 END ASS ");
				qrybuf.append("            ,CASE WHEN APPGB='A' AND MID IN ('00986653087', '00951457027','00989439518') THEN SUM(AMOUNT) ELSE 0 END AHN ");
				qrybuf.append("            ,CASE WHEN APPGB='A' AND MID IN ('9052663887', '9967457077','9969229911') THEN SUM(AMOUNT) ELSE 0 END ALO ");
				qrybuf.append("            ,CASE WHEN APPGB='A' AND MID IN ('151098345', '860386610','860295101') THEN SUM(AMOUNT) ELSE 0 END AHD ");
				qrybuf.append("            ,CASE WHEN APPGB='A' AND MID IN ('0107608507', '0104783451','57296808') THEN SUM(AMOUNT) ELSE 0 END ASI ");
				qrybuf.append("            ,CASE WHEN APPGB='C' AND MID IN ('704855398', '768017318','707528764') THEN SUM(AMOUNT) ELSE 0 END CBC ");
				qrybuf.append("            ,CASE WHEN APPGB='C' AND MID IN ('140239694', '143275451','151558364') THEN SUM(AMOUNT) ELSE 0 END CNH ");
				qrybuf.append("            ,CASE WHEN APPGB='C' AND MID IN ('00052904921', '00087259990','00084542316') THEN SUM(AMOUNT) ELSE 0 END CKB ");
				qrybuf.append("            ,CASE WHEN APPGB='C' AND MID IN ('165138860', '128890479','167802984') THEN SUM(AMOUNT) ELSE 0 END CSS ");
				qrybuf.append("            ,CASE WHEN APPGB='C' AND MID IN ('00986653087', '00951457027','00989439518') THEN SUM(AMOUNT) ELSE 0 END CHN ");
				qrybuf.append("            ,CASE WHEN APPGB='C' AND MID IN ('9052663887', '9967457077','9969229911') THEN SUM(AMOUNT) ELSE 0 END CLO ");
				qrybuf.append("            ,CASE WHEN APPGB='C' AND MID IN ('151098345', '860386610','860295101') THEN SUM(AMOUNT) ELSE 0 END CHD ");
				qrybuf.append("            ,CASE WHEN APPGB='C' AND MID IN ('0107608507', '0104783451','57296808') THEN SUM(AMOUNT) ELSE 0 END CSI ");
				qrybuf.append("        FROM ( ");
				qrybuf.append("            SELECT SEQNO, DEP_NM, TERM_NM, TID, MID, PUR_NM, ACQ_CD,  ");
				qrybuf.append("		 APPDD, APPTM, OAPPDD, APPNO, APPGB, ");
				qrybuf.append("		APPGB_TXT, CARDNO, AMOUNT, HALBU, CARDTP_TXT, SIGNCHK_TXT, ");
				qrybuf.append("		REQ_DD, AUTHCD, REG_DD, RTN_CD, RTN_TXT,  ");
				qrybuf.append("		EXP_DD, EXT_FIELD, TRANIDX, AUTHMSG ");
				qrybuf.append("            FROM( ");
				qrybuf.append("		SELECT SEQNO, DEP_NM, TERM_NM, TID, MID, PUR_NM, ");
				qrybuf.append("			APPDD, APPTM,  OAPPDD, APPNO, APPGB, ACQ_CD, ");
				qrybuf.append("			CASE  ");
				qrybuf.append("		           WHEN APPGB='A' THEN '신용승인' ");
				qrybuf.append("		           WHEN APPGB='C' THEN '신용취소' ");
				qrybuf.append("			END APPGB_TXT, ");
				qrybuf.append("			CARDNO,	AMOUNT,	HALBU, ");
				qrybuf.append("			CASE WHEN CHECK_CARD='Y' THEN '체크카드' ELSE '신용카드' END CARDTP_TXT, ");
				qrybuf.append("			CASE WHEN SIGNCHK='1' THEN '전자서명' ELSE '무서명' END SIGNCHK_TXT, ");
				qrybuf.append("			REQ_DD,	AUTHCD,	REG_DD,	RTN_CD, ");
				qrybuf.append("			CASE ");
				qrybuf.append("		           WHEN RTN_CD IS NULL THEN '결과없음' ");
				qrybuf.append("		           WHEN RTN_CD IN('60', '67') THEN '정상매입' ");
				qrybuf.append("			WHEN RTN_CD IN('61', '64') THEN '매입반송' END RTN_TXT, ");
				qrybuf.append("			EXP_DD, EXT_FIELD, TRANIDX, AUTHMSG ");
				qrybuf.append("		FROM( ");
				qrybuf.append("		SELECT SEQNO, BIZNO, TID, MID, VANGB, MDATE, SVCGB, T1.TRANIDX, T1.APPGB, ENTRYMD, ");
				qrybuf.append("			T1.APPDD, APPTM, T1.APPNO, T1.CARDNO, HALBU, CURRENCY, T1.AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX, ");
				qrybuf.append("			ISS_CD, ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG, CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB, ");
				qrybuf.append("			SIGNCHK, DDCGB, EXT_FIELD, OAPPNO, OAPPDD, OAPPTM, OAPP_AMT, ADD_GB, ADD_CID, ADD_CD, ");
				qrybuf.append("			ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, PUR_NM, DEP_NM, EXP_DD, REQ_DD, REG_DD, RSC_CD, RTN_CD, TERM_NM, DPFLAG ");
				qrybuf.append("		FROM GLOB_MNG_ICVAN_CVS T1 ");
				qrybuf.append("		LEFT OUTER JOIN(SELECT EXP_DD, REQ_DD, REG_DD, APP_DD, TRANIDX, RSC_CD, RTN_CD FROM TB_MNG_DEPDATA)T2 ON(T1.APPDD=T2.APP_DD AND T1.TRANIDX=T2.TRANIDX) ");
				qrybuf.append("		LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD=?)T3 ON(T1.TID=T3.TERM_ID) ");
				setting.add(userexp[1]);
				qrybuf.append("		LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD=?)T4 ON(T3.DEP_CD=T4.DEP_CD) ");
				setting.add(userexp[1]);
				qrybuf.append("		LEFT OUTER JOIN( SELECT PUR_NM, PUR_OCD, PUR_KOCES FROM TB_BAS_PURINFO)T5 ON (T1.ACQ_CD=T5.PUR_OCD OR T1.ACQ_CD=T5.PUR_KOCES) ");
				qrybuf.append(" WHERE SVCGB IN ('CC', 'CE') AND AUTHCD='0000' AND TID IN (select tid from tb_bas_tidmap where org_cd=? ");
				setting.add(userexp[1]);
				if(userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				qrybuf.append(" )");
				qrybuf.append(wherebuf.toString());
				qrybuf.append("		order by appdd desc, apptm desc ");
				qrybuf.append("		) ");
				qrybuf.append("	    ) ");
				qrybuf.append(exwherebuf.toString());
				qrybuf.append("	) ");
				qrybuf.append("	GROUP BY TID, APPGB, MID ");
				qrybuf.append("    ) ");
				qrybuf.append("    GROUP BY TID ");
				qrybuf.append(")T2 ");
				qrybuf.append("LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD=?)T3 ON(T2.TID=T3.TERM_ID) ");
				setting.add(userexp[1]);
				qrybuf.append("LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD=?)T4 ON(T3.DEP_CD=T4.DEP_CD) ");
				setting.add(userexp[1]);
				
				// 디버깅용
				utilm.debug_sql(qrybuf, setting);

				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				for (int k = 0; k < setting.size(); k++) {
					stmt.setString((k + 1), setting.get(k));
				}

				rs = stmt.executeQuery(); 

				int icnt = 1;
				long aamt = 0, camt = 0, totcsum = 0, totasum = 0, bctot = 0, nhtot=0, kbtot = 0, sstot = 0, hntot = 0, lotot = 0, hdtot = 0, sitot = 0;
				int acnt = 0, ccnt = 0;
				while (rs.next()) {
					acnt += Integer.parseInt(rs.getString("ACNT"));
					ccnt += Integer.parseInt(rs.getString("CCNT"));
					aamt += Integer.parseInt(rs.getString("AAMT"));
					camt += Integer.parseInt(rs.getString("CAMT"));
					totcsum += Integer.parseInt(rs.getString("TOTCNT"));
					totasum += Integer.parseInt(rs.getString("TOTAMT"));
					bctot	+=  Integer.parseInt(rs.getString("BC"));
					nhtot	+=  Integer.parseInt(rs.getString("NH"));
					kbtot	+=  Integer.parseInt(rs.getString("KB"));
					sstot	+=  Integer.parseInt(rs.getString("SS"));
					hntot	+=  Integer.parseInt(rs.getString("HN"));
					lotot	+=  Integer.parseInt(rs.getString("LO"));
					hdtot	+=  Integer.parseInt(rs.getString("HD"));
					sitot	+=  Integer.parseInt(rs.getString("SI"));
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
				arr2.add(totcsum);
				arr2.add(totasum);
				arr2.add(bctot);
				arr2.add(nhtot);
				arr2.add(kbtot);
				arr2.add(sstot);
				arr2.add(hntot);
				arr2.add(lotot);
				arr2.add(hdtot);
				arr2.add(sitot);

				obj1.put("id", Integer.toString(icnt));
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


		public String get_json_0106item_cvs(String tuser, String stime, String etime, String samt, String eamt,
				String appno, String tradeidx, String auth01, String auth02, String auth03, String mid, String tid,
				String acqcd, String tid2) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;

			JSONObject sqlobj = new JSONObject();
			JSONArray sqlAry = new JSONArray();

			StringBuffer wherebuf = new StringBuffer();
			StringBuffer exwherebuf = new StringBuffer();
			StringBuffer qrybuf = new StringBuffer();
			
			ArrayList<String> pos_field = get_column_field(tuser, "van", "field");

			try {
				// tuser, syear, smon, samt, eamt, depcd
				// tuser split
				String[] userexp = tuser.split(":");
				//acqcd split
				//String[] acqcdexp = acqcd.split(",");
				// 검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();
				
				// 1. 청구일자
				if (!stime.equals("") && stime != null && !etime.equals("") && etime != null) {
					wherebuf.append(" AND DEPOREQDD>=? AND DEPOREQDD<=? ");
					setting.add(stime);
					setting.add(etime);
				}

				// 2. 승인금액
				if (!samt.equals("") && samt != null && !eamt.equals("") && eamt != null) {
					wherebuf.append(" AND AMOUNT>=? AND AMOUNT<=?");
					setting.add(samt);
					setting.add(samt);
				}

				//3. 승인번호
				if(!appno.equals("") && appno != null) {
					wherebuf.append(" AND APPNO=? ");
					setting.add(appno);
				}
				
				//4. 카드사선택
				if(!acqcd.equals("") && acqcd != null) {
					wherebuf.append(" AND ACQ_CD IN (?) ");
					setting.add(acqcd);
				}
				
				//5. 단말기 선택
				if(!tid.equals("") && tid != null) {
					wherebuf.append(" AND TID = ?");
					setting.add(tid);
				}
				
			
				//6. 가맹점 번호
				if(!mid.equals("") && mid != null) {
					wherebuf.append(" AND MID = ?");
					setting.add(mid);
				}
				
				//7. 거래고유번호
				if(!tradeidx.equals("") && tradeidx != null) {
					wherebuf.append(" AND TRANIDX=? ");
					setting.add(tradeidx);
				}

				//8.승인구분
				//auth01 전체, auth02 승인, auth03 취소
				if(!auth01.equals("Y")){
					if(auth02.equals("Y")){wherebuf.append(" AND APPGB = 'A'");}
					else if(auth03.equals("Y")){wherebuf.append(" AND APPGB  = 'C'");}
					else if(auth02.equals("Y") && auth03.equals("Y")) {wherebuf.append(" AND APPGB IN ('A', 'C')");}
				}
				
				//9. 사업부 선택
				if(!tid2.equals("") && tid2 != null) {
					String[] tidval = tid2.split("_");
					wherebuf.append(" AND TID IN (?, ?, ?, ?) ");
					setting.add(tidval[0]);
					setting.add(tidval[1]);
					setting.add(tidval[2]);
					setting.add(tidval[3]);
				}
				
				
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
				qrybuf.append("		RNUM, SEQNO, DEP_NM, TERM_NM, TID, MID, PUR_NM, APPDD, APPTM, OAPPDD, APPNO, APPGB,");
				qrybuf.append("		CASE  ");
				qrybuf.append("			WHEN APPGB='A' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD='$UserExpAuth[1]' AND SCD_CD='SCD0015') ");
				qrybuf.append("			WHEN APPGB='C' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD='$UserExpAuth[1]' AND SCD_CD='SCD0016') ");
				qrybuf.append("		END APPGB_TXT, ");
				qrybuf.append("		CARDNO, AMOUNT, HALBU, ");
				qrybuf.append("		CASE  ");
				qrybuf.append("			WHEN CHECK_CARD='Y' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD='$UserExpAuth[1]' AND SCD_CD='SCD0019')  ");
				qrybuf.append("			ELSE (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD='$UserExpAuth[1]' AND SCD_CD='SCD0018') END CARDTP_TXT, ");
				qrybuf.append("		CASE ");
				qrybuf.append("			WHEN SIGNCHK='1' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD='$UserExpAuth[1]' AND SCD_CD='SCD0021')  ");
				qrybuf.append("			ELSE (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD='$UserExpAuth[1]' AND SCD_CD='SCD0022') END SIGNCHK_TXT, ");
				qrybuf.append("		REQ_DD, AUTHCD, REG_DD, RTN_CD, ");
				qrybuf.append("		CASE ");
				qrybuf.append("			WHEN RTN_CD IS NULL THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD='$UserExpAuth[1]' AND SCD_CD='SCD0024')  ");
				qrybuf.append("			WHEN RTN_CD IN('60', '67') THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD='$UserExpAuth[1]' AND SCD_CD='SCD0025') ");
				qrybuf.append("			WHEN RTN_CD IN('61', '64') THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD='$UserExpAuth[1]' AND SCD_CD='SCD0026')  ");
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
				qrybuf.append("			SELECT  ");
				qrybuf.append("				ROWNUM AS RNUM, SEQNO, BIZNO, TID, MID, VANGB, MDATE, SVCGB, TRANIDX, APPGB, ENTRYMD, ");
				qrybuf.append("				APPDD, APPTM, APPNO, CARDNO, HALBU, CURRENCY, AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX, ");
				qrybuf.append("				ISS_CD, ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG, CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB, ");
				qrybuf.append("				SIGNCHK, DDCGB, EXT_FIELD, OAPPNO, OAPPDD, OAPPTM, OAPP_AMT, ADD_GB, ADD_CID, ADD_CD, ");
				qrybuf.append("				ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, DEPO_DD ");
				qrybuf.append("			FROM(     ");
				qrybuf.append("				SELECT ");
				qrybuf.append("					SEQNO, BIZNO, TID, MID, VANGB, MDATE, SVCGB, TRANIDX, APPGB, ENTRYMD, ");
				qrybuf.append("					APPDD, APPTM, APPNO, CARDNO, HALBU, CURRENCY, AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX, ");
				qrybuf.append("					ISS_CD, ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG, CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB, ");
				qrybuf.append("					SIGNCHK, DDCGB, EXT_FIELD, OAPPNO, OAPPDD, OAPPTM, OAPP_AMT, ADD_GB, ADD_CID, ADD_CD, ");
				qrybuf.append("					ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, DEPOREQDD DEPO_DD,DPFLAG  ");
				qrybuf.append("				FROM ");
				qrybuf.append("					GLOB_MNG_ICVAN_CVS  ");
				qrybuf.append(" WHERE SVCGB IN ('CC', 'CE') AND AUTHCD='0000' AND TID IN (select tid from tb_bas_tidmap where org_cd=? ");
				setting.add(userexp[1]);
				if(userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				qrybuf.append(" )");
				qrybuf.append(wherebuf.toString());
				qrybuf.append("				order by appdd desc, apptm desc ");
				qrybuf.append("			)  ");
				qrybuf.append("		) ");
				qrybuf.append(exwherebuf.toString());
				qrybuf.append("	)T1 ");
				qrybuf.append("	LEFT OUTER JOIN( ");
				qrybuf.append("		SELECT EXP_DD, REQ_DD, REG_DD, APP_DD, TRANIDX, RSC_CD, RTN_CD, APP_NO , SALE_AMT , CARD_NO FROM TB_MNG_DEPDATA ");
				qrybuf.append("	)T2 ON(T1.APPDD=T2.APP_DD AND T1.CARDNO=T2.CARD_NO AND T1.AMOUNT = T2.SALE_AMT AND T1.APPGB = (CASE WHEN T2.RTN_CD IN ('60','61') THEN 'A' ELSE 'C' END) AND T1.APPNO =T2.APP_NO ) ");
				qrybuf.append("	LEFT OUTER JOIN(  ");
				qrybuf.append("		SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD=? ");
				setting.add(userexp[1]);
				qrybuf.append("	)T3 ON(T1.TID=T3.TERM_ID) ");
				qrybuf.append("	LEFT OUTER JOIN(  ");
				qrybuf.append("		SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD=? ");
				setting.add(userexp[1]);
				qrybuf.append("	)T4 ON(T3.DEP_CD=T4.DEP_CD) ");
				qrybuf.append("	LEFT OUTER JOIN( SELECT PUR_NM, PUR_OCD, PUR_KIS FROM TB_BAS_PURINFO)T5 ON (T1.ACQ_CD=T5.PUR_OCD OR T1.ACQ_CD=T5.PUR_KIS) ");
				qrybuf.append(") ");
				qrybuf.append("ORDER BY RNUM ASC ");
				
				// 디버깅용
				utilm.debug_sql(qrybuf, setting);

				con = getOraConnect();
				stmt = con.prepareStatement(qrybuf.toString());
				for (int k = 0; k < setting.size(); k++) {
					stmt.setString((k + 1), setting.get(k));
				}

				rs = stmt.executeQuery(); 

				int icnt = 1;
				while (rs.next()) {
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

						} else if(pos_field.get(i).equals("TR_APPDD") || pos_field.get(i).equals("TR_OAPPDD") || pos_field.get(i).equals("DP_EXP_DD") || pos_field.get(i).equals("DP_REG_DD") || pos_field.get(i).equals("DP_REQ_DD")) {
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

					sqlAry.add(obj1);

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
		public String get_json_0106cnt_cvs(String tuser, String stime, String etime, String samt, String eamt,
				String appno, String tradeidx, String auth01, String auth02, String auth03, String mid, String tid,
				String acqcd, String tid2) {
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
				// 검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();
				
				// 1. 청구일자
				if (!stime.equals("") && stime != null && !etime.equals("") && etime != null) {
					wherebuf.append(" AND DEPOREQDD>=? AND DEPOREQDD<=? ");
					setting.add(stime);
					setting.add(etime);
				}

				// 2. 승인금액
				if (!samt.equals("") && samt != null && !eamt.equals("") && eamt != null) {
					wherebuf.append(" AND AMOUNT>=? AND AMOUNT<=?");
					setting.add(samt);
					setting.add(samt);
				}

				//3. 승인번호
				if(!appno.equals("") && appno != null) {
					wherebuf.append(" AND APPNO=? ");
					setting.add(appno);
				}
				
				//4. 카드사선택
				if(!acqcd.equals("") && acqcd != null) {
					wherebuf.append(" AND ACQ_CD IN (?) ");
					setting.add(acqcd);
				}
				
				//5. 단말기 선택
				if(!tid.equals("") && tid != null) {
					wherebuf.append(" AND TID = ?");
					setting.add(tid);
				}
				
			
				//6. 가맹점 번호
				if(!mid.equals("") && mid != null) {
					wherebuf.append(" AND MID = ?");
					setting.add(mid);
				}
				
				//7. 거래고유번호
				if(!tradeidx.equals("") && tradeidx != null) {
					wherebuf.append(" AND TRANIDX=? ");
					setting.add(tradeidx);
				}

				//8.승인구분
				//auth01 전체, auth02 승인, auth03 취소
				if(!auth01.equals("Y")){
					if(auth02.equals("Y")){wherebuf.append(" AND APPGB = 'A'");}
					else if(auth03.equals("Y")){wherebuf.append(" AND APPGB  = 'C'");}
					else if(auth02.equals("Y") && auth03.equals("Y")) {wherebuf.append(" AND APPGB IN ('A', 'C')");}
				}
				
				//9. 사업부 선택
				if(!tid2.equals("") && tid2 != null) {
					String[] tidval = tid2.split("_");
					wherebuf.append(" AND TID IN (?, ?, ?, ?) ");
					setting.add(tidval[0]);
					setting.add(tidval[1]);
					setting.add(tidval[2]);
					setting.add(tidval[3]);
				}
				
				qrybuf.append("SELECT COUNT(1) MCNT FROM GLOB_MNG_ICVAN_CVS T1 ");
				qrybuf.append(" WHERE SVCGB IN ('CC', 'CE') AND AUTHCD='0000' AND TID IN (select tid from tb_bas_tidmap where org_cd=? ");
				setting.add(userexp[1]);
				if(userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				qrybuf.append(" ) ");
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
			}catch(Exception e){
				e.printStackTrace();
			} finally {
				setOraClose(con2,stmt2,rs2);
			}
			return icnt;
		}
		
		//cvsnet 신용/체크 구분 페이지
		//사용하지않은 페이지여서 사용할 시 쿼리 체크 부터 해야함
		/*
			public String get_json_0215total(String tuser, String date, String enddate) {
				JSONObject sqlobj = new JSONObject();
				JSONArray sqlAry = new JSONArray();

				StringBuffer wherebuf = new StringBuffer();
				StringBuffer qrybuf = new StringBuffer();
	 
				try {
					// tuser, syear, smon, samt, eamt, depcd
					// tuser split
					String[] userexp = tuser.split(":");
					//acqcd split
					//String[] acqcdexp = acqcd.split(",");
					// 검색항목에 따른 where 조건절 setting 관련 변수
					ArrayList<String> setting = new ArrayList<>();
					
					// 현재 날짜 구하기
					String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
					

					// 1. 청구일자
					if (!date.equals("") && date != null) {
						wherebuf.append(" WHERE SVCGB IN ('CC', 'CE') AND AUTHCD='0000' and tid in (select tid from tb_bas_tidmap where org_cd =?) AND APPDD BETWEEN ? AND ?  ");
						setting.add(userexp[1]);
						setting.add(date);
						setting.add(enddate);
					}else {
						wherebuf.append(" WHERE SVCGB IN ('CC', 'CE') AND AUTHCD='0000' and tid in (select tid from tb_bas_tidmap where org_cd =?) AND APPDD like ?% ");
						setting.add(userexp[1]);
						setting.add(today);
					}
					
					qrybuf.append("SELECT ");
					qrybuf.append("	APPDD ");
					qrybuf.append("	, PUR_NM ");
					qrybuf.append("	, SUM(GSCARDCNT) GSCARDCNT ");
					qrybuf.append("	, SUM(GSCHECKCNT) GSCHECKCNT ");
					qrybuf.append("	, SUM(GSTOTCNT) GSTOTCNT ");
					qrybuf.append("	, SUM(GSCARDTOT) GSCARDTOT ");
					qrybuf.append("	, SUM(GSCHECKTOT) GSCHECKTOT ");
					qrybuf.append("	, SUM(GSTOTAMT) GSTOTAMT ");
					qrybuf.append("	, TO_CHAR(ROUND(NVL(SUM(GSCARDCNT)/(SUM(GSCARDCNT)+SUM(GSCHECKCNT))*100,0),2),'FM90.00') GSCARDP ");
					qrybuf.append("	, TO_CHAR(ROUND(NVL(SUM(GSCHECKCNT)/(SUM(GSCARDCNT)+SUM(GSCHECKCNT))*100,0),2),'FM90.00') GSCHECKP ");
					qrybuf.append("	, SUM(SUCARDCNT) SUCARDCNT ");
					qrybuf.append("	, SUM(SUCHECKCNT) SUCHECKCNT ");
					qrybuf.append("	, SUM(SUTOTCNT) SUTOTCNT ");
					qrybuf.append("	, SUM(SUCARDTOT) SUCARDTOT ");
					qrybuf.append("	, SUM(SUCHECKTOT) SUCHECKTOT ");
					qrybuf.append("	, SUM(SUTOTAMT) SUTOTAMT ");
					qrybuf.append("	, TO_CHAR(ROUND(NVL(SUM(SUCARDCNT)/(SUM(SUCARDCNT)+SUM(SUCHECKCNT))*100,0),2),'FM90.00') SUCARDP ");
					qrybuf.append("	, TO_CHAR(ROUND(NVL(SUM(SUCHECKCNT)/(SUM(SUCARDCNT)+SUM(SUCHECKCNT))*100,0),2),'FM90.00') SUCHECKP ");
					qrybuf.append("	, SUM(LACARDCNT) LACARDCNT ");
					qrybuf.append("	, SUM(LACHECKCNT) LACHECKCNT ");
					qrybuf.append("	, SUM(LATOTCNT) LATOTCNT ");
					qrybuf.append("	, SUM(LACARDTOT) LACARDTOT ");
					qrybuf.append("	, SUM(LACHECKTOT) LACHECKTOT ");
					qrybuf.append("	, SUM(LATOTAMT) LATOTAMT ");
					qrybuf.append("	, ROUND(NVL(SUM(LACARDCNT)/(SUM(LACARDCNT)+SUM(LACHECKCNT))*100,0),2) LACARDP ");
					qrybuf.append("	, ROUND(NVL(SUM(LACHECKCNT)/(SUM(LACARDCNT)+SUM(LACHECKCNT))*100,0),2) LACHECKP ");
					qrybuf.append("FROM( ");
					qrybuf.append("	SELECT  ");
					qrybuf.append("		APPDD ");
					qrybuf.append("		, T3.PUR_NM ");
					qrybuf.append("		, CASE WHEN MID IN ('00951457027','768017318','00052904921','57296808','151098345','9052663887','128890479','151558364') THEN SUM(CARDCNT)  ELSE 0 END  GSCARDCNT ");
					qrybuf.append("		, CASE WHEN MID IN ('00951457027','768017318','00052904921','57296808','151098345','9052663887','128890479','151558364') THEN SUM(CHECKCNT)  ELSE 0 END  GSCHECKCNT ");
					qrybuf.append("		, CASE WHEN MID IN ('00951457027','768017318','00052904921','57296808','151098345','9052663887','128890479','151558364') THEN SUM(TOTCNT)  ELSE 0 END  GSTOTCNT ");
					qrybuf.append("		, CASE WHEN MID IN ('00951457027','768017318','00052904921','57296808','151098345','9052663887','128890479','151558364') THEN SUM(CARDTOT)  ELSE 0 END  GSCARDTOT ");
					qrybuf.append("		, CASE WHEN MID IN ('00951457027','768017318','00052904921','57296808','151098345','9052663887','128890479','151558364') THEN SUM(CHECKTOT)  ELSE 0 END  GSCHECKTOT ");
					qrybuf.append("		, CASE WHEN MID IN ('00951457027','768017318','00052904921','57296808','151098345','9052663887','128890479','151558364') THEN SUM(TOTAMT)  ELSE 0 END  GSTOTAMT ");
					qrybuf.append("		, CASE WHEN MID IN ('00986653087','860295101','165138860','9969229911','140239694','00084542316','704855398','0104783451') THEN SUM(CARDCNT)  ELSE 0 END SUCARDCNT ");
					qrybuf.append("		, CASE WHEN MID IN ('00986653087','860295101','165138860','9969229911','140239694','00084542316','704855398','0104783451') THEN SUM(CHECKCNT)  ELSE 0 END  SUCHECKCNT ");
					qrybuf.append("		, CASE WHEN MID IN ('00986653087','860295101','165138860','9969229911','140239694','00084542316','704855398','0104783451') THEN SUM(TOTCNT)  ELSE 0 END  SUTOTCNT ");
					qrybuf.append("		, CASE WHEN MID IN ('00986653087','860295101','165138860','9969229911','140239694','00084542316','704855398','0104783451') THEN SUM(CARDTOT)  ELSE 0 END  SUCARDTOT ");
					qrybuf.append("		, CASE WHEN MID IN ('00986653087','860295101','165138860','9969229911','140239694','00084542316','704855398','0104783451') THEN SUM(CHECKTOT)  ELSE 0 END  SUCHECKTOT ");
					qrybuf.append("		, CASE WHEN MID IN ('00986653087','860295101','165138860','9969229911','140239694','00084542316','704855398','0104783451') THEN SUM(TOTAMT)  ELSE 0 END  SUTOTAMT ");
					qrybuf.append("		, CASE WHEN MID IN ('707528764','860386610','167802984','00989439518','0107608507','9967457077','00087259990','143275451') THEN SUM(CARDCNT)  ELSE 0 END LACARDCNT ");
					qrybuf.append("		, CASE WHEN MID IN ('707528764','860386610','167802984','00989439518','0107608507','9967457077','00087259990','143275451') THEN SUM(CHECKCNT)  ELSE 0 END  LACHECKCNT ");
					qrybuf.append("		, CASE WHEN MID IN ('707528764','860386610','167802984','00989439518','0107608507','9967457077','00087259990','143275451') THEN SUM(TOTCNT)  ELSE 0 END  LATOTCNT ");
					qrybuf.append("		, CASE WHEN MID IN ('707528764','860386610','167802984','00989439518','0107608507','9967457077','00087259990','143275451') THEN SUM(CARDTOT)  ELSE 0 END  LACARDTOT ");
					qrybuf.append("		, CASE WHEN MID IN ('707528764','860386610','167802984','00989439518','0107608507','9967457077','00087259990','143275451') THEN SUM(CHECKTOT)  ELSE 0 END  LACHECKTOT ");
					qrybuf.append("		, CASE WHEN MID IN ('707528764','860386610','167802984','00989439518','0107608507','9967457077','00087259990','143275451') THEN SUM(TOTAMT)  ELSE 0 END  LATOTAMT ");
					qrybuf.append("	FROM ( ");
					qrybuf.append("		SELECT  ");
					qrybuf.append("			APPDD ");
					qrybuf.append("			, MID ");
					qrybuf.append("			, SUM(ACARDCNT+CCARDCNT) CARDCNT ");
					qrybuf.append("			, SUM(ACHECKCNT+CCHECKCNT) CHECKCNT ");
					qrybuf.append("			, SUM(ACARDCNT+CCARDCNT)+SUM(ACHECKCNT+CCHECKCNT) TOTCNT ");
					qrybuf.append("			, SUM(ACARD-CCARD) CARDTOT,SUM(ACHECK-CCHECK) CHECKTOT ");
					qrybuf.append("			, SUM(ACARD-CCARD) +SUM(ACHECK-CCHECK) TOTAMT ");
					qrybuf.append("			, ROUND(NVL(SUM(ACARDCNT+CCARDCNT)/(SUM(ACARDCNT+CCARDCNT)+SUM(ACHECKCNT+CCHECKCNT))*100,0),2) CARDP  ");
					qrybuf.append("			, ROUND(NVL(SUM(ACHECKCNT+CCHECKCNT)/(SUM(ACARDCNT+CCARDCNT)+SUM(ACHECKCNT+CCHECKCNT))*100,0),2) CHECKP ");
					qrybuf.append("			, SUM(ACARDCNT) ACARDCNT ");
					qrybuf.append("			, SUM(ACHECKCNT) ACHECKCNT ");
					qrybuf.append("			, SUM(ACARDCNT+ACHECKCNT) AAMTCNT ");
					qrybuf.append("			, SUM(ACARD) ACARD ");
					qrybuf.append("			, SUM(ACHECK) ACHECK ");
					qrybuf.append("			, SUM(ACARD+ACHECK) AAMT ");
					qrybuf.append("			, SUM(CCARDCNT) CCARDCNT ");
					qrybuf.append("			, SUM(CCHECKCNT) CCHECKCNT ");
					qrybuf.append("			, SUM(CCARDCNT+CCHECKCNT) CAMTCNT ");
					qrybuf.append("			, SUM(CCARD) CCARD ");
					qrybuf.append("			, SUM(CCHECK) CCHECK ");
					qrybuf.append("			, SUM(CCARD+CCHECK) CAMT ");
					qrybuf.append("		 FROM ( ");
					qrybuf.append("			SELECT ");
					qrybuf.append("				APPDD ");
					qrybuf.append("				, MID ");
					qrybuf.append("				, CASE WHEN APPGB='A' AND CHECK_CARD='N' THEN SUM(AMOUNT) ELSE 0 END ACARD ");
					qrybuf.append("				, CASE WHEN APPGB='A' AND CHECK_CARD='Y' THEN SUM(AMOUNT) ELSE 0 END ACHECK ");
					qrybuf.append("				, CASE WHEN APPGB='C' AND CHECK_CARD='N' THEN SUM(AMOUNT) ELSE 0 END CCARD ");
					qrybuf.append("				, CASE WHEN APPGB='C' AND CHECK_CARD='Y' THEN SUM(AMOUNT) ELSE 0 END CCHECK ");
					qrybuf.append("				, CASE WHEN APPGB='A' AND CHECK_CARD='N' THEN SUM(CNT) ELSE 0 END ACARDCNT ");
					qrybuf.append("				, CASE WHEN APPGB='A' AND CHECK_CARD='Y' THEN SUM(CNT) ELSE 0 END ACHECKCNT ");
					qrybuf.append("				, CASE WHEN APPGB='C' AND CHECK_CARD='N' THEN SUM(CNT) ELSE 0 END CCARDCNT ");
					qrybuf.append("				, CASE WHEN APPGB='C' AND CHECK_CARD='Y' THEN SUM(CNT) ELSE 0 END CCHECKCNT ");
					qrybuf.append("			FROM ( ");
					qrybuf.append("				SELECT ");
					qrybuf.append("					SUBSTR(APPDD , 0, 6) AS APPDD ");
					qrybuf.append("					, SUM(AMOUNT) AMOUNT ");
					qrybuf.append("					, APPGB ");
					qrybuf.append("					, MID,COUNT(*) CNT ");
					qrybuf.append("					, CHECK_CARD ");
					qrybuf.append("				FROM ( ");
					qrybuf.append("					SELECT * FROM GLOB_MNG_ICVAN ");
					qrybuf.append(wherebuf.toString());
					qrybuf.append("					) GROUP BY APPGB, MID ,CHECK_CARD ,APPDD ");
					qrybuf.append("				) GROUP BY CHECK_CARD, APPGB, MID, CNT,APPDD ");
					qrybuf.append("			) GROUP BY MID, APPDD ");
					qrybuf.append("		)T1  ");
					qrybuf.append("		LEFT OUTER JOIN( SELECT MER_NO, PUR_CD FROM TB_BAS_)T2 ON(T1.MID = T2.MER_NO) ");
					qrybuf.append("		LEFT OUTER JOIN( SELECT PUR_CD, PUR_NM FROM TB_BAS_PURINFO)T3 ON(T3.PUR_CD = T2.PUR_CD) ");
					qrybuf.append("	GROUP BY APPDD, T3.PUR_NM, MID ");
					qrybuf.append("ORDER BY APPDD, T3.PUR_NM ");
					qrybuf.append(") ");
					qrybuf.append("GROUP BY APPDD, PUR_NM ");
					qrybuf.append("ORDER BY APPDD, PUR_NM ");
					
					//디버깅용
					//utilm.debug_sql(qrybuf, setting);
					Connection con = getOraConnect();
					PreparedStatement pstm = con.prepareStatement(qrybuf.toString());
					for(int k = 0; k<setting.size(); k++) {
						pstm.setString((k+1), setting.get(k));
					}

					ResultSet rs = pstm.executeQuery();

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

				} catch (Exception e) {
					e.printStackTrace();
				}

				return sqlobj.toJSONString();
				
			}
	*/
		
		public String get_json_0107total_cvs(String tuser, String stime, String etime, String sadd_date,
				String eadd_date, String sadd_recp, String eadd_recp, String appno, String pid, String pcd,
				String depcd, String auth01, String auth02, String auth03, String card01, String card02, String card03,
				String card04, String card05) {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			JSONObject sqlobj = new JSONObject();
			JSONArray sqlAry = new JSONArray();

			StringBuffer wherebuf = new StringBuffer();
			StringBuffer qrybuf = new StringBuffer();

			try {
				// tuser, syear, smon, samt, eamt, depcd
				// tuser split
				String[] userexp = tuser.split(":");
				// 검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();
				
				if (!stime.equals("") && stime != null ) {
					wherebuf.append(" AND APPDD>=? ");
					setting.add(stime);
				}
				
				if(!etime.equals("") && etime != null) {
					wherebuf.append("  AND APPDD<=? ");
					setting.add(etime);
				}

				if (!sadd_date.equals("") && sadd_date != null && !eadd_date.equals("") && eadd_date != null) {
					wherebuf.append(" AND ADD_DATE>=? AND ADD_DATE<=?");
					setting.add(sadd_date);
					setting.add(eadd_date);
				}
				
				if (!sadd_recp.equals("") && sadd_recp != null && !eadd_recp.equals("") && eadd_recp != null) {
					wherebuf.append(" AND ADD_RECP>=? AND ADD_RECP<=?");
					setting.add(sadd_recp);
					setting.add(eadd_recp);
				}

				if(!appno.equals("") && appno != null) {
					wherebuf.append(" AND APPNO=? ");
					setting.add(appno);
				}
				
				if(!pid.equals("") && pid != null) {
					wherebuf.append(" AND ADD_CID=? ");
					setting.add(pid);
				}
				
				if(!pcd.equals("") && pcd != null) {
					wherebuf.append(" AND ADD_CD=? ");
					setting.add(pcd);
				}
				
				if(!depcd.equals("") && depcd != null) {
					if(depcd=="1") {
						wherebuf.append(" AND ADD_CASEHR LIKE 'GS25%' ");
					}else if(depcd=="2") {
						wherebuf.append(" AND ADD_CASHER LIKE 'GSSM%' ");
					}else if(depcd=="3") {
						wherebuf.append(" AND ADD_CASHER LIKE 'lala%' ");
					}
				}
				
				if(!auth01.equals("Y")){
					if(auth02.equals("Y")){wherebuf.append(" AND APPGB_TXT = 'A'");}
					else if(auth03.equals("Y")){wherebuf.append(" AND APPGB_TXT = 'C'");}
					else if(auth02.equals("Y") && auth03.equals("Y")) {wherebuf.append(" AND APPGB_TXT IN ('A', 'C')");}
				}
				
				if(!card01.equals("Y") && !card01.equals("")) {
					String[] imp_card = new String[4];
					if(null != card02 && "" != card02 && card02.equals("Y")) {
						imp_card[0] = "'1'"; 
					}
					if(null != card03 && "" != card03 && card03.equals("Y")) {
						imp_card[1] = "'2'"; 
					}
					if(null != card04 && "" != card04 && card04.equals("Y")) {
						imp_card[1] = "'3'"; 
					}
					if(null != card05 && "" != card05 && card05.equals("Y")) {
						imp_card[1] = "'4'"; 
					}
					wherebuf.append(" AND ADD_CNT IN (" + utilm.implode(",", imp_card) + ") ");
				}
								
				qrybuf.append("SELECT  ");
				qrybuf.append("	ACNT, CCNT, AAMT, CAMT ");
				qrybuf.append("FROM( ");
				qrybuf.append("	SELECT ");
				qrybuf.append("		SUM(ACNT) ACNT, SUM(CCNT) CCNT, SUM(AAMT) AAMT, SUM(CAMT) CAMT ");
				qrybuf.append("	FROM( ");
				qrybuf.append("		SELECT ");
				qrybuf.append("			CASE WHEN APPGB='A' THEN COUNT(1) ELSE 0 END ACNT, ");
				qrybuf.append("			CASE WHEN APPGB='A' THEN SUM(AMOUNT) ELSE 0 END AAMT, ");
				qrybuf.append("			CASE WHEN APPGB='C' THEN COUNT(1) ELSE 0 END CCNT, ");
				qrybuf.append("			CASE WHEN APPGB='C' THEN SUM(AMOUNT) ELSE 0 END CAMT ");
				qrybuf.append("		FROM  ");
				qrybuf.append(userexp[5]);
				qrybuf.append(" WHERE SVCGB IN ('CB') AND AUTHCD='0000' AND TID IN (SELECT TID FROM TB_BAS_TIDMAP WHERE ORG_CD=?) ");
				setting.add(userexp[1]);
				qrybuf.append(wherebuf.toString());
				qrybuf.append("		GROUP BY APPGB ");
				qrybuf.append("	) ");
				qrybuf.append(")T1 ");
				
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
					acnt += Integer.parseInt(utilm.checkNumberData(rs.getString("ACNT")));
					ccnt += Integer.parseInt(utilm.checkNumberData(rs.getString("CCNT")));
					aamt += Integer.parseInt(utilm.checkNumberData(rs.getString("AAMT")));
					camt += Integer.parseInt(utilm.checkNumberData(rs.getString("CAMT")));
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

				obj1.put("id", Integer.toString(icnt));
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
		
		public String get_json_0107item_cvs(String tuser, String stime, String etime, String sadd_date,
				String eadd_date, String sadd_recp, String eadd_recp, String appno, String pid, String pcd,
				String depcd, String auth01, String auth02, String auth03, String card01, String card02, String card03,
				String card04, String card05, String paging) {
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

				if (!sadd_date.equals("") && sadd_date != null && !eadd_date.equals("") && eadd_date != null) {
					wherebuf.append(" AND ADD_DATE>=? AND ADD_DATE<=?");
					setting.add(sadd_date);
					setting.add(eadd_date);
				}
				
				if (!sadd_recp.equals("") && sadd_recp != null && !eadd_recp.equals("") && eadd_recp != null) {
					wherebuf.append(" AND ADD_RECP>=? AND ADD_RECP<=?");
					setting.add(sadd_recp);
					setting.add(eadd_recp);
				}

				if(!appno.equals("") && appno != null) {
					wherebuf.append(" AND APPNO=? ");
					setting.add(appno);
				}
				
				if(!pid.equals("") && pid != null) {
					wherebuf.append(" AND ADD_CID=? ");
					setting.add(pid);
				}
				
				if(!pcd.equals("") && pcd != null) {
					wherebuf.append(" AND ADD_CD=? ");
					setting.add(pcd);
				}
				
				if(!depcd.equals("") && depcd != null) {
					if(depcd=="1") {
						wherebuf.append(" AND ADD_CASEHR LIKE 'GS25%' ");
					}else if(depcd=="2") {
						wherebuf.append(" AND ADD_CASHER LIKE 'GSSM%' ");
					}else if(depcd=="3") {
						wherebuf.append(" AND ADD_CASHER LIKE 'lala%' ");
					}
				}
				
				if(!auth01.equals("Y")){
					if(auth02.equals("Y")){wherebuf.append(" AND APPGB_TXT = 'A'");}
					else if(auth03.equals("Y")){wherebuf.append(" AND APPGB_TXT = 'C'");}
					else if(auth02.equals("Y") && auth03.equals("Y")) {wherebuf.append(" AND APPGB_TXT IN ('A', 'C')");}
				}
				
				if(!card01.equals("Y") && !card01.equals("")) {
					String[] imp_card = new String[4];
					if(null != card02 && "" != card02 && card02.equals("Y")) {
						imp_card[0] = "'1'"; 
					}
					if(null != card03 && "" != card03 && card03.equals("Y")) {
						imp_card[1] = "'2'"; 
					}
					if(null != card04 && "" != card04 && card04.equals("Y")) {
						imp_card[1] = "'3'"; 
					}
					if(null != card05 && "" != card05 && card05.equals("Y")) {
						imp_card[1] = "'4'"; 
					}
					wherebuf.append(" AND ADD_CNT IN (" + utilm.implode(",", imp_card) + ") ");
				}
				
				int page=0;
				if(paging==""){
					page=0;
				}

				int SNUM = (page*100)+1;
				int ENUM = (page+1) * 100;
				
				exwherebuf.append(" WHERE RNUM BETWEEN "+SNUM+" AND "+ENUM);
				
				qrybuf.append("SELECT	 ");
				qrybuf.append("	RNUM, SEQNO, BIZNO, TID, MID, VANGB, MDATE, SVCGB, TRANIDX, APPGB, AUTHTXT, ENTRYMD, APPDD, APPTM, APPNO, CARDNO, HALBU ");
				qrybuf.append("	, CURRENCY, AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX, ISS_CD, ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG, CARD_CODE, CHECK_CARD ");
				qrybuf.append("	, OVSEA_CARD, OVERTXT, TLINEGB, TLINEGBTXT, SIGNCHK, DDCGB, DDCTXT, EXT_FIELD, OAPPNO, OAPPDD, OAPPTM, OAPP_AMT, ADD_GB ");
				qrybuf.append("	, ADD_CID, ADD_CD, ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, PUR_NM, DEP_NM, TERM_NM ");
				qrybuf.append("FROM( ");
				qrybuf.append("	SELECT ");
				qrybuf.append("		ROWNUMBER() OVER() AS RNUM, SEQNO, BIZNO, TID, MID, VANGB, MDATE, SVCGB, TRANIDX, APPGB ");
				qrybuf.append("		, CASE ");
				qrybuf.append("			WHEN APPGB='A' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=?  AND SCD_CD='SCD0002') ");
				setting.add(userexp[1]);
				qrybuf.append("			WHEN APPGB='C' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=?  AND SCD_CD='SCD0003') ");
				setting.add(userexp[1]);
				qrybuf.append("		END AUTHTXT ");
				qrybuf.append("		, ENTRYMD, APPDD, APPTM, APPNO, CARDNO, HALBU, CURRENCY, AMOUNT, AMT_UNIT, AMT_TIP ");
				qrybuf.append("		, AMT_TAX, ISS_CD, ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG, CARD_CODE, CHECK_CARD, OVSEA_CARD ");
				qrybuf.append("		,CASE ");
				qrybuf.append("			WHEN OVSEA_CARD='1' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0004') ");
				setting.add(userexp[1]);
				qrybuf.append("			WHEN OVSEA_CARD='2' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0005') ");
				setting.add(userexp[1]);
				qrybuf.append("			WHEN OVSEA_CARD='3' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0006') ");
				setting.add(userexp[1]);
				qrybuf.append("		END OVERTXT ");
				qrybuf.append("		, TLINEGB ");
				qrybuf.append("		, CASE WHEN TLINEGB IS NOT NULL THEN (SELECT CODE_VAL FROM TB_BAS_CODE WHERE TRIM(CODE_NO)=TRIM(TLINEGB)) END TLINEGBTXT ");
				qrybuf.append("		, SIGNCHK, DDCGB ");
				qrybuf.append("		,CASE ");
				qrybuf.append("			WHEN DDCGB='0' AND CARDNO<>'ef7dc2d7309c745fa1dc838fdac105a939cbcb24e50a41027d60475fec001909' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0007') ");
				setting.add(userexp[1]);
				qrybuf.append("			WHEN DDCGB='1' THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0008') ");
				setting.add(userexp[1]);
				qrybuf.append("			WHEN DDCGB='2' OR (DDCGB='0' AND CARDNO='ef7dc2d7309c745fa1dc838fdac105a939cbcb24e50a41027d60475fec001909')  THEN (SELECT SCD_DIP FROM TB_BAS_SITECODE WHERE ORG_CD=? AND SCD_CD='SCD0009') ");
				setting.add(userexp[1]);
				qrybuf.append("		END DDCTXT ");
				qrybuf.append("		, EXT_FIELD, OAPPNO, OAPPDD, OAPPTM, OAPP_AMT, ADD_GB, ADD_CID, ADD_CD, ADD_RECP, ADD_CNT, ADD_CASHER ");
				qrybuf.append("		, ADD_DATE, SECTION_NO, PUR_NM, DEP_NM, TERM_NM ");
				qrybuf.append("	FROM ");
				qrybuf.append("		GLOB_MNG_ICVAN T1 ");
				qrybuf.append("	LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST)T3 ON(T1.TID=T3.TERM_ID) ");
				qrybuf.append("	LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART)T4 ON(T3.DEP_CD=T4.DEP_CD) ");
				qrybuf.append("	LEFT OUTER JOIN( SELECT PUR_NM, PUR_OCD, PUR_KOCES FROM TB_BAS_PURINFO)T5 ON (T1.ACQ_CD=T5.PUR_OCD OR T1.ACQ_CD=T5.PUR_KOCES) ");
				qrybuf.append(" WHERE SVCGB IN ('CB') AND AUTHCD='0000' AND TID IN (SELECT TID FROM TB_BAS_TIDMAP WHERE ORG_CD=?) ");
				setting.add(userexp[1]);
				qrybuf.append(wherebuf.toString());
				qrybuf.append(") ");
				qrybuf.append(exwherebuf.toString());
				qrybuf.append(" order by RNUM ");
				
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

					String add_cnt = utilm.setDefault(rs.getString("ADD_CNT"));
					String cardtype=null;
					if(add_cnt=="1") {
						cardtype="선물";
					}else if(add_cnt=="2") {
						cardtype="착불";
					}else if(add_cnt=="3") {
						cardtype="카드";
					}else if(add_cnt=="4") {
						cardtype="신용";
					}else {
						cardtype="";
					}
					
					String cardno_dec = utilm.cardno_masking(trans_seed_manager.seed_dec_card(rs.getString("CARDNO").trim()));
					
					arr2.add(icnt);
					arr2.add(rs.getString("ADD_CASHER"));
					arr2.add(rs.getString("ADD_CD"));
					arr2.add(rs.getString("ADD_CID"));
					arr2.add(utilm.str_to_dateformat(rs.getString("ADD_DATE")));
					arr2.add(rs.getString("ADD_RECP"));
					arr2.add(cardtype);
					arr2.add(utilm.str_to_dateformat(rs.getString("APPDD")));
					arr2.add(utilm.str_to_timeformat(rs.getString("APPTM")));
					arr2.add(utilm.str_to_dateformat(rs.getString("OAPPDD")));
					arr2.add(rs.getString("APPNO"));
					arr2.add(rs.getString("AUTHTXT"));
					arr2.add(cardno_dec);
					arr2.add(rs.getString("AMOUNT")); //14
					arr2.add(rs.getString("DDCTXT"));
					arr2.add(rs.getString("OVERTXT"));
					arr2.add(rs.getString("TRANIDX"));
					
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
				arr2.add("");
				arr2.add("");
				arr2.add("");
				arr2.add(total);
				arr2.add("");
				arr2.add("");
				arr2.add("");
				
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
		
		// 2022.01.25 cvsnet - 월일자별조회 상세 갯수
		public String get_json_0107cnt_cvs(String tuser, String stime, String etime, String sadd_date,
				String eadd_date, String sadd_recp, String eadd_recp, String appno, String pid, String pcd,
				String depcd, String auth01, String auth02, String auth03, String card01, String card02, String card03,
				String card04, String card05) {
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
				// 검색항목에 따른 where 조건절 setting 관련 변수
				ArrayList<String> setting = new ArrayList<>();
				
				if (!stime.equals("") && stime != null && !etime.equals("") && etime != null) {
					wherebuf.append(" AND APPDD>=? AND APPDD<=? ");
					setting.add(stime);
					setting.add(etime);
				}

				if (!sadd_date.equals("") && sadd_date != null && !eadd_date.equals("") && eadd_date != null) {
					wherebuf.append(" AND ADD_DATE>=? AND ADD_DATE<=?");
					setting.add(sadd_date);
					setting.add(eadd_date);
				}
				
				if (!sadd_recp.equals("") && sadd_recp != null && !eadd_recp.equals("") && eadd_recp != null) {
					wherebuf.append(" AND ADD_RECP>=? AND ADD_RECP<=?");
					setting.add(sadd_recp);
					setting.add(eadd_recp);
				}

				if(!appno.equals("") && appno != null) {
					wherebuf.append(" AND APPNO=? ");
					setting.add(appno);
				}
				
				if(!pid.equals("") && pid != null) {
					wherebuf.append(" AND ADD_CID=? ");
					setting.add(pid);
				}
				
				if(!pcd.equals("") && pcd != null) {
					wherebuf.append(" AND ADD_CD=? ");
					setting.add(pcd);
				}
				
				if(!depcd.equals("") && depcd != null) {
					if(depcd=="1") {
						wherebuf.append(" AND ADD_CASEHR LIKE 'GS25%' ");
					}else if(depcd=="2") {
						wherebuf.append(" AND ADD_CASHER LIKE 'GSSM%' ");
					}else if(depcd=="3") {
						wherebuf.append(" AND ADD_CASHER LIKE 'lala%' ");
					}
				}
				
				if(!auth01.equals("Y")){
					if(auth02.equals("Y")){wherebuf.append(" AND APPGB_TXT = 'A'");}
					else if(auth03.equals("Y")){wherebuf.append(" AND APPGB_TXT = 'C'");}
					else if(auth02.equals("Y") && auth03.equals("Y")) {wherebuf.append(" AND APPGB_TXT IN ('A', 'C')");}
				}
				
				if(!card01.equals("Y") && !card01.equals("")) {
					String[] imp_card = new String[4];
					if(null != card02 && "" != card02 && card02.equals("Y")) {
						imp_card[0] = "'1'"; 
					}
					if(null != card03 && "" != card03 && card03.equals("Y")) {
						imp_card[1] = "'2'"; 
					}
					if(null != card04 && "" != card04 && card04.equals("Y")) {
						imp_card[1] = "'3'"; 
					}
					if(null != card05 && "" != card05 && card05.equals("Y")) {
						imp_card[1] = "'4'"; 
					}
					wherebuf.append(" AND ADD_CNT IN (" + utilm.implode(",", imp_card) + ") ");
				}
				
				qrybuf.append("SELECT count(1) MCNT FROM GLOB_MNG_ICVAN ");
				qrybuf.append("WHERE SVCGB IN ('CB') AND AUTHCD='0000' AND TID IN (SELECT TID FROM TB_BAS_TIDMAP WHERE ORG_CD=?");
				setting.add(userexp[1]);
				if(userexp[2] != null && !userexp[2].equals("")) {
					qrybuf.append(" and dep_cd = ?");
					setting.add(userexp[2]);
				}
				qrybuf.append(" ) ");
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
				setOraClose(con,stmt,rs);
			}
			return icnt;
		}
}
