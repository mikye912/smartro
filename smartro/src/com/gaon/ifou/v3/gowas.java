package com.gaon.ifou.v3;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Base64.Decoder;

public class gowas {

	public static trans_ora_manager ocim 		= new trans_ora_manager();
	public static trans_util_manager utilm 		= new trans_util_manager();
	
	public String delimiter = ":";
		
	public String ora_cnt() {
		String rtnstr = "";
		try {
			int cnt = ocim.get_edi_head_count();
			
			rtnstr = Integer.toString(cnt);
			
		} catch (Exception e) {
		}
		return rtnstr;
	}
	
	public String get_sql_select(String qry) {
		StringBuffer rtnstr = new StringBuffer();
		try {
			Decoder decoder = Base64.getDecoder();
			byte[] byte_query = decoder.decode(qry);
			String query = new String(byte_query, "UTF-8");
			
			String result = ocim.get_sql_select(query);
			
			JSONParser resultParser = new JSONParser();
			JSONObject resultObj = (JSONObject) resultParser.parse(result);
			JSONArray resultAry = (JSONArray) resultObj.get("rows");
		
			rtnstr.append("<rows>");
			rtnstr.append("<head>");
			
			for(int i = 0; i<resultAry.size(); i++) {
				JSONObject headerObj = (JSONObject) resultAry.get(i);
				JSONArray headerAry = (JSONArray) headerObj.get("header");
				for(int j = 0; j<headerAry.size(); j++) {
					rtnstr.append("<column width='100' align='left' type='ro' sort='str' color=''>"+headerAry.get(j).toString()+"</column>");
				}
			}
			rtnstr.append("<afterInit><call command='attachHeader'><param>");
			
			for(int i = 0; i<resultAry.size(); i++) {
				JSONObject headerObj = (JSONObject) resultAry.get(i);
				JSONArray headerAry = (JSONArray) headerObj.get("header");
				
				for(int j = 0; j<headerAry.size(); j++) {
					rtnstr.append("#text_filter,");
				}
			}
			
			rtnstr.append("</param></call></afterInit>");
			rtnstr.append("</head>");
		
			for(int i = 0; i<resultAry.size(); i++) {
				int harry = -1;
				JSONObject itemObj = (JSONObject) resultAry.get(i);
				JSONArray headerAry = (JSONArray) itemObj.get("header");
				String seqno = String.valueOf(itemObj.get("seqno"));
				JSONArray itemAry = (JSONArray) itemObj.get("data");
				
				for(int ai=1;ai<=Integer.parseInt(seqno);ai++) {
					rtnstr.append("<row id='"+ai+"'>");
					for(int j = 0; j<headerAry.size(); j++) {
						harry++;
						rtnstr.append("<cell>"+itemAry.get(harry).toString()+"</cell>");
						
					}
					rtnstr.append("</row>");
				}
			}
				
			rtnstr.append("</rows>");
		} catch (Exception e) {
		}
		return rtnstr.toString();
	}
	
	public String get_sqlproc(String qry) {
		String result = ocim.get_sql_proc(qry);
		return result;
	}
	
	public String get_icvan(String sdate) {
		String rtnstr = "";
		try {
			rtnstr = ocim.get_glob_mng_icvan_json(sdate);
		} catch (Exception e) {
		}
		return rtnstr;
	}
	
	public String general_menu(String orgcd, String authseq) {
		
		String[][] strarr = new String[40][9];
		StringBuffer strbuf = new StringBuffer();
		
		try {
			
			strarr = ocim.get_general_menu_top(orgcd, authseq);
			
			for(int i=0;i<strarr.length;i++) {
				if(strarr[i][0]!=null && strarr[i][0].trim().length()>0) {
					strbuf.append("<li><a href='#'>" + strarr[i][1] + "</a><ul class='subnav'>");
					
					String[][] subarr = new String[40][9];
					subarr = ocim.get_general_menu_sub(orgcd, authseq, strarr[i][0]);
					for(int j=0;j<subarr.length;j++) {
						if(subarr[j][0]!=null && subarr[j][0].trim().length()>0) {
							if(subarr[j][4].equals("Y")) {
								strbuf.append("<li><a href='#' onclick=\"urlgo(-1, '" + subarr[j][1] + "', '" + subarr[j][8] + "')\">" + subarr[j][1] + "</a></li>");
							}
						}
					}
					strbuf.append("</ul></li>");
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return strbuf.toString();
	}
	
	public String general_depo_select(String orgcd, String depcd) {
		
		String[][] strarr = new String[100][2];
		StringBuffer strbuf = new StringBuffer();
		
		try {
			strarr = ocim.get_general_depart(orgcd, depcd);
			
			strbuf.append("<select name='depcd' id='depcd' style='font-size:9pt;' class='searchbox_drop'>");
			strbuf.append("<option value=''>:: 사업부선택 ::</option>");
			
			for(int i=0;i<strarr.length;i++) {
				
				if(strarr[i][0]!=null && strarr[i][0].trim().length()>0) {
					strbuf.append("<option value='" + strarr[i][0] + "'>" + strarr[i][1] + "</option>");
				}
			}
			strbuf.append("</select>");
		}catch(Exception e) {
			e.printStackTrace();
		}
		return strbuf.toString();
	}
	
	public String general_tid_select(String orgcd, String depcd) {
		
		String[][] strarr = new String[100][2];
		StringBuffer strbuf = new StringBuffer();
		
		try {
			strarr = ocim.get_general_tid(orgcd, depcd);
			
			strbuf.append("<select name='tid' id='tid' style='font-size:9pt;' class='searchbox_drop'>");
			strbuf.append("<option value=''>:: 단말기선택 ::</option>");
			
			for(int i=0;i<strarr.length;i++) {
				if(strarr[i][0]!=null && strarr[i][0].trim().length()>0) {
					strbuf.append("<option value='" + strarr[i][0] + "'>" + strarr[i][1] + "</option>");
				}
			}
			strbuf.append("</select>");
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return strbuf.toString();
	}

	public String general_acq_select() {
		String[][] strarr = new String[100][2];
		StringBuffer strbuf = new StringBuffer();
		try {
			strarr = ocim.get_general_acq();		
			strbuf.append("<select name='acqcd' id='acqcd' style='font-size:9pt;' class='searchbox_drop'>");
			strbuf.append("<option value=''>:: 카드사선택 ::</option>");
			
			for(int i=0;i<strarr.length;i++) {
				
				if(strarr[i][0]!=null && strarr[i][0].trim().length()>0) {
					strbuf.append("<option value='" + strarr[i][0] + "'>" + strarr[i][1] + "</option>");
				}
			}
			strbuf.append("</select>");
		}catch(Exception e) {
			e.printStackTrace();
		}
		return strbuf.toString();
	}

	/**
	 * 사용자의 ID/PW를 입력받아 로그인 성공여부 리턴
	 * @param uid
	 * @param upw
	 * @return 리턴 배열 >> 0:오류코드, 1:로그인정보
	 */
	//2021.03.11 tuser 데이터 base64 encoding
	@SuppressWarnings("unchecked")
	public String user_login(String uid, String upw) {
		String[] rtnstr = new String[6];
		
		StringBuffer strbuf = new StringBuffer();
		JSONObject obj1 = new JSONObject();
		Encoder encoder = Base64.getEncoder();
		
		try {
			int ucnt = ocim.get_user_cnt(uid);
			int usechk = ocim.get_user_check(uid);
			
			if(uid=="admin" && upw == "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918") {
				ocim.get_user_reset(uid, upw);
				rtnstr[0] = "R000";//계정초기화
			}
			
			if(usechk>4) {
				rtnstr[0] = "R001"; //계정잠김
			}
			
			if(ucnt > 0) {
				String[] uinfo = ocim.get_user_info(uid);
				String[] orginfo = new String[4];
				
				if(upw.trim().equals(uinfo[0].trim())) { // 비밀번호가 동일 하다면
					ocim.get_use_chk_reset(uid); //use_chk 0으로 리셋
					int insert_log = ocim.get_insert_user_log(uid);//log기록
					rtnstr[0] = "S000";
					
					//유저세션 구성
					//ID:ORGCD:DEPCD:MKTIME:PTAB:VTAB:DTAB:U_LV
					if(uinfo[2].trim().length()>0) {
						orginfo = ocim.get_org_info(uinfo[2]);
					}
					
					DateFormat format = new SimpleDateFormat("yyyyMMdd");
					String ndate = format.format(new Date());
					
					// 현재 시간
					LocalTime now = LocalTime.now();
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");
					String formatedNow = now.format(formatter);
					
					strbuf.append(uid); // 유저 아이디
					strbuf.append(delimiter + trans_util_manager.setDefault(uinfo[2])); // ORGCD
					strbuf.append(delimiter + trans_util_manager.setDefault(uinfo[1])); // DEPCD
					strbuf.append(delimiter + ""); // 생성일시(Unix timestamp), 생략
					strbuf.append(delimiter + trans_util_manager.setDefault(orginfo[0]));
					strbuf.append(delimiter + trans_util_manager.setDefault(orginfo[1]));
					strbuf.append(delimiter + trans_util_manager.setDefault(orginfo[2]));
					strbuf.append(delimiter + trans_util_manager.setDefault(uinfo[3])); //userlv
					strbuf.append(delimiter + trans_util_manager.setDefault(uinfo[4])); //auth_seq
					strbuf.append(delimiter + ndate+formatedNow);
					
					//2021.03.11 tuser값 base64 encoding
					String tmuser = strbuf.toString();
					byte[] byte_user = tmuser.getBytes("UTF-8");
					rtnstr[1] = encoder.encodeToString(byte_user);
					
					//사용자 메뉴 구성
					String tmpm = general_menu(uinfo[2], uinfo[4]);
					byte[] targetBytes = tmpm.getBytes("UTF-8");
					rtnstr[2] = encoder.encodeToString(targetBytes);
					
					//사업부선택 1t Box 구성
					String str_depo = general_depo_select(uinfo[2], uinfo[1]);//orgcd, depcd를 이용해서 검색 할 수 있는 사업부 제한
					byte[] byt_depo = str_depo.getBytes("UTF-8");
					rtnstr[3] = encoder.encodeToString(byt_depo);

					//단말기선택 Select Box 구성
					String str_tid = general_tid_select(uinfo[2], uinfo[1]);//orgcd, depcd를 이용해서 검색 할 수 있는 단말기 제한
					byte[] byt_tid = str_tid.getBytes("UTF-8");

					rtnstr[4] = encoder.encodeToString(byt_tid);
					
					//카드사선택 Select Box 구성
					String str_acq = general_acq_select();
					byte[] byt_acq = str_acq.getBytes("UTF-8");

					rtnstr[5] = encoder.encodeToString(byt_acq);
					
				}else {
					int pwcheck = ocim.get_update_use_chk(uid);
					rtnstr[0] = "F002"; //비밀번호 불 일치
				}
			}else {
				rtnstr[0] = "F001"; // 회원아이디를 찾을 수 없음.
			}
			
			obj1.put("RST", rtnstr[0]);
			obj1.put("UDATA", rtnstr[1]);
			obj1.put("USERMENU", rtnstr[2]);
			obj1.put("SELEDEPO", rtnstr[3]);
			obj1.put("SELETID", rtnstr[4]);
			obj1.put("SELECTACQ", rtnstr[5]);
			
			
		} catch (Exception e) {
			
		}
		return obj1.toJSONString();
	}
	
	//2021.02.16 강원대병원v3 - 상세내역조회/현금영수증 accountGrid 항목 setting
	//van - 상세내역조회, tr - 현금영수증조회
	public String[] get_page_column(String tuser, String type) {
		String[] column = new String[6];
		
		try {
			Decoder decoder = Base64.getDecoder();
			byte[] byte_tuser = decoder.decode(tuser);
			tuser = new String(byte_tuser, "UTF-8");
			
			if(type.equals("van")) {
				column = ocim.get_page_column(tuser, "van");
			} else if (type.equals("tr")) {
				column = ocim.get_page_column(tuser, "tr");
			}
			
		} catch (Exception e) {}

		return column;
	}


	//2021.03.03 강원대병원 v3 - 종합정보보기
	//검색기간, 매입사
	public String get_0000_total(String tuser, String stime, String etime, String acqcd) {
		StringBuffer xmlObj = new StringBuffer();
		//int icnt = 0;
		
		try {
			//icnt = ocim.get_json_0000total_cnt(tuser, stime, etime, depcd, casher, acqcd);
			
			String result = ocim.get_json_0000total(tuser, stime, etime, acqcd);
			
			JSONParser resultParser = new JSONParser();
			JSONObject resultObj = (JSONObject) resultParser.parse(result);
			JSONArray resultAry = (JSONArray) resultObj.get("rows");
			
			if(resultAry.size() == 0) {
				xmlObj.append("<tr><td colspan='8' align='center'>검색된 자료가 없습니다.</td></tr>");
			} else {
				//소계, 총합계 계산
				//number format setting
				DecimalFormat formatter = new DecimalFormat("###,###");
				
				for(int i = 0; i<resultAry.size(); i++) {
					JSONObject itemObj = (JSONObject) resultAry.get(i);
					JSONArray itemAry = (JSONArray) itemObj.get("data");
					
					//일반 데이터 tr, 소계 데이터 tr, 합계 데이터 tr
					if(itemAry.get(0).toString().equals("소계") || itemAry.get(0).toString().equals("합계")) {
						//합계부분 Total(stime ~ etime)
						int acnt = Integer.parseInt(itemAry.get(3).toString());
						int ccnt = Integer.parseInt(itemAry.get(5).toString());
						int tcnt = acnt + ccnt;
						long aamt = Long.parseLong(itemAry.get(4).toString());
						long camt = Long.parseLong(itemAry.get(6).toString());
						long tamt = aamt - camt;
						
						xmlObj.append("<tr>");
						if(itemAry.get(0).toString().equals("합계")) {
							xmlObj.append("<td colspan='3' align='center' bgcolor='#f0f0f0'>Total<br>("+itemAry.get(1)+" ~ "+itemAry.get(2)+")</td>");
						} else {
							xmlObj.append("<td colspan='3' align='center' bgcolor='#f0f0f0'>소계("+itemAry.get(1)+")</td>");
						}
						xmlObj.append("<td colspan='2' align='right' bgcolor='#f0f0f0'>정산 "+formatter.format(tcnt)+"건<br>("+formatter.format(acnt)+" + "+formatter.format(ccnt)+")</td>");
						xmlObj.append("<td colspan='3' align='right' bgcolor='#f0f0f0'>정산 "+formatter.format(tamt)+"원<br>("+formatter.format(aamt)+" - "+formatter.format(camt)+")</td>");
						xmlObj.append("</tr>");
					} else {
						//그냥 일반 데이터 출력
						//원단위 표시
						int acnt = Integer.parseInt(itemAry.get(3).toString());
						int ccnt = Integer.parseInt(itemAry.get(5).toString());
						long aamt = Long.parseLong(itemAry.get(4).toString());
						long camt = Long.parseLong(itemAry.get(6).toString());
						long tamt = Long.parseLong(itemAry.get(7).toString());	
						
						xmlObj.append("<tr>");
						xmlObj.append("<td align='center'>"+itemAry.get(0).toString()+"</td>");
						xmlObj.append("<td align='center'>"+itemAry.get(1).toString()+"</td>");
						xmlObj.append("<td align='center'>"+itemAry.get(2).toString()+"</td>");
						xmlObj.append("<td align='right'>"+formatter.format(acnt)+"</td>");
						xmlObj.append("<td align='right'>"+formatter.format(ccnt)+"</td>");
						xmlObj.append("<td align='right'>"+formatter.format(aamt)+"</td>");
						xmlObj.append("<td align='right'>"+formatter.format(camt)+"</td>");
						xmlObj.append("<td align='right'>"+formatter.format(tamt)+"</td>");
						xmlObj.append("</tr>");
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return xmlObj.toString();
	}
	
	//2021.03.04 강원대병원 v3 - 종합정보보기 excel download
	//excel_0000을 거쳐갈 경우 -> encoding 상태로 보내기 때문에 JSONObject만 전달
	//exceldn_0000으로 다이렉트로 붙을 경우 -> tr, td 태그 만들어진 상태로 encoding 없이 전달
	@SuppressWarnings("unchecked")
	public String get_excel_0000(String tuser, String stime, String etime, String acqcd) {
		JSONObject excelObj = new JSONObject();
		Encoder encoder = Base64.getEncoder();
		try {
			excelObj.put("RST", "S000");
			excelObj.put("ITEMARRAY", encoder.encodeToString(ocim.get_json_0000total(tuser, stime, etime, acqcd).getBytes("UTF-8")));
		} catch (Exception e) {
		}
		
		return excelObj.toJSONString();
	}
	
	
	
	//2021.02.24 강원대병원v3 - 웹취소 proc
	public String transcancel_mst(String seqno, String tuser, String appno) {
		String result = "";
		StringBuffer tr_data = new StringBuffer();
		
		//1. 실시간 테이블에서 정보 가져옴
		//2. 소켓 연결해서 전문 보냄
		//3. 응답전문 받아옴
		//4. return
		try {
			//tuser split
			String[] userexp = tuser.split(":");
			
			String getData = ocim.transcancel_getData(seqno, tuser, appno);
			//System.out.println(getData);
			
			JSONParser parse = new JSONParser();
			JSONObject jsonObj = (JSONObject) parse.parse(getData);
			JSONArray dataAry = (JSONArray)jsonObj.get("ITEMS");
			JSONObject dataObj = (JSONObject) dataAry.get(0);
			
			//전문생성
			tr_data.append("0417"); //전문길이(4)
			tr_data.append("HPS"); //전문TEXT(3)
			tr_data.append(utilm.rpad(dataObj.get("TID").toString(), 10, " ")); //TID(10)
			tr_data.append("0000000001"); //일련번호(10)
			tr_data.append("0420"); //전문구분(4)
			tr_data.append("30"); //거래구분(2)
			tr_data.append("H1"); //기종구분(2)
			tr_data.append("          "); //기기번호(10)
			tr_data.append(utilm.rpad(dataObj.get("TID").toString(), 10, " ")); //TID(10)
			tr_data.append("@"); //WCC(1)
			tr_data.append("NS"); //거래일련번호키(2)
			tr_data.append(utilm.rpad(dataObj.get("TRANIDX").toString(), 18, " ")); //거래일련번호(18)
			tr_data.append("                                                                                                           "); //공백(107)
			tr_data.append(utilm.rpad(dataObj.get("HALBU").toString(), 10, " ")); //원거래 할부개월(2)
			tr_data.append(utilm.rpad("0", 12, "0")); //원거래 봉사료(12)
			tr_data.append(utilm.rpad("0", 12, "0")); //원거래 세금(12)
			tr_data.append(utilm.rpad(dataObj.get("AMOUNT").toString(), 12, "0")); //원거래 총금액(12)
			tr_data.append(utilm.rpad(dataObj.get("APPNO").toString(), 8, " ")); //원거래 승인번호(12)
			tr_data.append(utilm.rpad(dataObj.get("APPDD").toString(), 6, " ")); //원거래 승인일자(6)
			tr_data.append("            "); //원거래고유번호(12)
			tr_data.append("          "); //사업자등록번호(10)
			tr_data.append("             "); //주민번호(13)
			tr_data.append("                "); //PIN(16)
			tr_data.append("                              "); //DOMAIN (30)
			tr_data.append("                    "); //ID ADDRESS(20)
			tr_data.append("                "); //HW 모델번호(16)
			tr_data.append("                "); //SW 모델번호(16)
			tr_data.append("  "); //FALLBACK(2)
			if(userexp[1].equals("OR0010")){
				tr_data.append("GWH"); //병원코드(3) :: 강원대병원
			} else {
				tr_data.append("KHM");
			}
			String exp_field = dataObj.get("EXT_FIELD").toString();
			exp_field = exp_field.replaceAll("/", "|");
			tr_data.append(utilm.rpad(exp_field, 47, " ")); //가맹점데이터(47)
			tr_data.append("N"); //sign 구분값(1)
			
			System.out.println(tr_data.toString());
			//0417HPS67101000010000000001042030H1          6710100001@NS210219204028478000                                                                                                           03        00000000000000000000000014883000000041945233210219                                                                                                                                       GWHO AER 2021021970159 0100007418                 N
			
			/*
			Socket socket = new Socket("172.31.161.152",21110); 
			OutputStream out = socket.getOutputStream(); 
			PrintWriter writer = new PrintWriter(out, true); 
			writer.println(tr_data.toString()); 
			
			InputStream input = socket.getInputStream(); 
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			result = reader.readLine();
			socket.close();
			*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	//2021.03.04 강원대병원 - 시스템관리 [원장관리] readData
	public String[] get_060501_item(String tuser) {
		String[] data = new String[9];
		try {
			data = ocim.get_json_060501item(tuser);
		} catch (Exception e) {
		}
		
		return data;
	}
	
	//2021.03.04 강원대병원 - 시스템관리 [원장관리] 사용자정보 readData
	public String get_060501_item_userList(String tuser) {
		String data = "";
		
		try {
			data = ocim.get_json_060501item_userList(tuser);
		} catch (Exception e) {
			
		}
		
		return data;
	}
	
	//2021.03.04 강원대병원 - 시스템관리 [원장관리] data update
	public int get_060501_item_upadte(String tuser, String comnm, String comno, String comexno, String comceo,
			String cometype, String comservice, String comtel, String comaddr, String orgmemo) {
		int result = 0;
		try {
			result = ocim.get_json_060501item_update(tuser, comnm, comno, comexno, comceo, cometype, comservice, comtel, comaddr, orgmemo);
		} catch (Exception e) {
		}
		
		return result;
	}
	
	//2021.03.04 강원대병원 - 시스템관리 [사업부관리] readData
	public String get_060502_item(String tuser) {
		String result = "";
		
		try {
			result = ocim.get_json_060502item(tuser);
		} catch (Exception e) {
			
		}
		
		return result;
	}
	
	//2021.03.05 강원대병원 - 시스템관리 [사업부관리] - 단말기관리 tab 출력
	public String get_060502_item_tid_list(String orgcd, String depcd) {
		String result = "";
		try {
			result = ocim.get_json_060502item_tid_list(orgcd, depcd);
		} catch (Exception e) {
			
		}
		
		return result;
	}
	
	//2021.03.05 강원대병원 - 시스템관리 [사업부관리] 단말기관리 update
	public int get_060502_item_tid_list_update(String orgcd, String depcd, String[] tid, String tuser) {
		int result = 0;
		
		try {
			result = ocim.get_json_060502item_tid_list_update(orgcd, depcd, tid, tuser);
		} catch (Exception e) {
			result= -1;
		}
		
		return result;
	}
	
	//2021.03.05 강원대병원 - 시스템관리 [사업부관리] - 가맹점관리 tab 출력
	public String get_060502_item_mid_list(String orgcd, String depcd) {
		String result = "";
		try {
			result = ocim.get_json_060502item_mid_list(orgcd, depcd);
		} catch (Exception e) {
				
		}
			
		return result;
	}
	
	//2021.03.05 강원대병원 - 시스템관리 [사업부관리] 가맹점관리 update
	public int get_060502_item_mid_list_update(String orgcd, String depcd, String[] mid, String tuser) {
		int result = 0;
		
		try {
			result = ocim.get_json_060502item_mid_list_update(orgcd, depcd, mid, tuser);
		} catch (Exception e) {
			result = -1;
		}
		
		return result;
	}
	
	//2021.03.05 강원대병원 - 시스템관리 [사업부관리] 사업부 수정 tab 출력
	//사업부 삭제 전 정보출력이랑 동일하게 사용해도 될듯?
	public String[] get_060502_item_deplist(String orgcd, String depcd) {
		String data[] = new String[5];
		
		try {
			data = ocim.get_json_060502item_deplist(orgcd, depcd);
		} catch (Exception e) {
			
		}
		
		return data;
	}
	
	//2021.03.05 강원대병원 - 시스템관리 [사업부관리] 사업부 update
	public int get_060502_item_deposit_update(String dep_nm, String dep_adm_user, String dep_tel1, 
			String dep_email, String dep_type, String orgcd, String depcd)  {
		int result = 0;
		
		try {
			result = ocim.get_json_060502item_deposit_update(dep_nm, dep_adm_user, dep_tel1, dep_email, dep_type, orgcd, depcd);
		} catch (Exception e) {
		}
		
		return result;
	}
	
	//2021.03.05 강원대병원 - 시스템관리 [사업부관리] 사업부 delete
	public int get_060502_item_deposit_delete(String orgcd, String depcd) {
		int result = 0;
		
		try {
			result = ocim.get_json_060502item_deposit_delete(orgcd, depcd);
		} catch (Exception e) {
		}
		
		return result;
	}
	
	//2021.03.08 강원대병원 - 시스템관리 [사업부관리] 사업부 insert
	public int get_060502_deposit_insert(String tuser, String dep_nm, String dep_adm_user, String dep_tel1, 
			String dep_email, String dep_type) {
		int result = 0;
		
		try {
			result = ocim.get_json_060502item_deposit_insert(tuser, dep_nm, dep_adm_user, dep_tel1, dep_email, dep_type);
		} catch (Exception e) {
		}
		
		return result;
	}
	
	//2021.03.08 강원대병원 - 시스템관리 [가맹점관리] 가맹점 리스트 load
	public String get_060503_item_merlist(String tuser) {
		String list = "";
		
		try {
			list = ocim.get_json_060503_item_merlist(tuser);
		} catch (Exception e) {
			
		}
		
		return list;
		
	}
	
	//2021.03.08 강원대병원 - 시스템관리 [가맹점관리] 가맹점번호 등록현황 load
	public String get_060503_item_mermap(String tuser) {
		String list = "";
		
		try {
			list = ocim.get_json_060503_item_mermap(tuser);
		} catch (Exception e) {
			
		}
		
		return list;
	}
	
	//2021.03.08 강원대병원 - 시스템관리 [가맹점관리] 가맹점번호 수정/삭제 tab 정보 불러오기
	public String[] get_060503_item_merData(String orgcd, String mercd, String mid) {
		String[] data = new String[10];
		try {
			data = ocim.get_json_060503_item_merData(orgcd, mercd, mid);
		} catch (Exception e) {
			
		}
		return data;
	}
	
	//2021.03.08 강원대병원 - 시스템관리 [가맹점관리] 가맹점번호 수정 tab 정보 수정하기
	public int get_060503_item_merData_update(String orgcd, String mercd, String depcd, String mid, String purcd, 
			String merst, String meret, String fee01, String fee02, String fee03) {
		int result = 0;
		try {
			result = ocim.get_json_060503_item_merData_update(orgcd, mercd, depcd, mid, purcd, merst, meret, fee01, fee02, fee03);
		} catch (Exception e) {
			
		}
		return result;
	}
	
	//2021.03.08 강원대병원 - 시스템관리 [가맹점관리] 가맹점번호 수정 tab 정보 삭제하기
	public int get_060503_item_merData_delete(String orgcd, String mid) {
		int result = 0;
		try {
			result = ocim.get_json_060503_item_merData_delete(orgcd, mid);
		} catch (Exception e) {
			
		}
		return result;
	}
	
	//시스템관리 [가맹점관리] 가맹점 추가
	public int get_060503_item_merData_insert(String tuser, String depcd, String purcd, String mid, 
			String merst, String meret, String van, String fee01, String fee02, String fee03) {
		int result = 0;
		
		try {
			result = ocim.get_json_060503_item_merData_insert(tuser, depcd, purcd, mid, merst, meret, van, fee01, fee02, fee03);
		} catch (Exception e) {
			
		}
		
		return result;
	}
	
	public String get_060504_item_tidlist(String tuser) {
		String result = "";
		
		try {
			result = ocim.get_json_060504_item_tidlist(tuser);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public String get_060504_item_tidmap(String tuser) {
		String result = "";
		
		try {
			result = ocim.get_json_060504_item_tidmap(tuser);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	//2021.03.09 강원대병원 - 시스템관리 [단말기관리] 단말기번호 단말기 정보 read
	public String[] get_060503_item_tidData(String depcd, String orgcd, String tid) {
		String[] data = new String[6];
		
		try {
			data = ocim.get_json_060503_item_tidData(depcd, orgcd, tid);
		} catch (Exception e) {
			
		}
		
		return data;
	}
	
	
	//2021.03.09 강원대병원 - 시스템관리 [단말기관리] 단말기번호 단말기 정보 update
	public int get_060503_item_tid_update(String orgcd, String tidcd, String depcd, String tidnm, String tid, String vangb, String term_type) {
		int result = 0;
		
		try {
			result = ocim.get_json_060503_item_tid_update(orgcd, tidcd, depcd, tidnm, tid, vangb, term_type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	//2021.03.09 강원대병원 - 시스템관리 [단말기관리] 단말기번호 단말기 정보 delete
	public int get_060503_item_tid_delete(String orgcd, String tid) {
		int result = 0;
		
		try {
			result = ocim.get_json_060503_item_tid_delete(orgcd, tid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	//2021.03.08 강원대병원 - 시스템관리 [단말기관리] 단말기번호 insert
	public int get_060504_item_insert(String tuser, String depcd, String tid, String term_nm, String term_type, String vangb) {
		int result = 0;
		
		try {
			result = ocim.get_json_060504_item_insert(tuser, depcd, tid, term_nm, term_type, vangb);
		} catch (Exception e) {
			
		}
		
		return result;
	}
	
	//2021.03.09 강원대병원 - 시스템관리 [사용자관리] 정보 read
	public String[] get_060505_item_userInfo(String mem_cd) {
		String[] data = new String[9];
		
		try {
			data = ocim.get_json_060505_item_userInfo(mem_cd);
		} catch (Exception e) {
			
		}
		
		return data;
	}
	
	//2021.03.09 강원대병원 - 시스템관리 [사용자관리] 정보 update
	public int get_060505_item_userInfo_update(String orgcd, String depcd, String memcd, String mem_id, String mem_pw, 
			String mem_nm, String memlv, String mem_tel1, String mem_tel2, String mem_email) {
		int result = 0;
		
		try {
			result = ocim.get_json_060505_item_userInfo_update(orgcd, depcd, memcd, mem_id, mem_pw, mem_nm, memlv, mem_tel1, mem_tel2, mem_email);
		} catch (Exception e) {
			
		}
		
		return result;
	}
	
	//2021.03.09 강원대병원 - 시스템관리 [사용자관리] 정보 delete
	public int get_060505_item_userInfo_delete(String orgcd, String memcd) {
		int result = 0;
			
		try {
			result = ocim.get_json_060505_item_userInfo_delete(orgcd, memcd);
		} catch (Exception e) {
				
		}
			
		return result;
	}
	
	//2021.03.10 강원대병원 - 시스템관리 [사용자관리] 계정 insert
	public int get_060505_item_userInfo_insert(String tuser, String depcd, String memid, 
			String mempw, String memnm, String memlv, String memtel1, String memtel2, String mememail) {
		int result = 0;
		
		try {
			result = ocim.get_json_060505_item_insert(tuser, depcd, memid, mempw, memnm, memlv, memtel1, memtel2, mememail);
		} catch (Exception e) {
			
		}
		
		return result;
	}
	
	//2021.03.10 강원대병원 - 단말기, 사업부 변경/삭제/생성에 의한 session data 재설정
	public String get_session_datareset(String orgcd, String depcd, String type) {
		String result = "";
		Encoder encoder = Base64.getEncoder();
		
		try {
			switch(type) {
				case "tid":
					String tidList = general_tid_select(orgcd, depcd);
					byte[] byte_tid = tidList.getBytes("UTF-8");
					result = encoder.encodeToString(byte_tid);
					break;
				case "depcd":
					String depList = general_depo_select(orgcd, depcd);
					byte[] byte_dep = depList.getBytes("UTF-8");
					result = encoder.encodeToString(byte_dep);
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	

	

	
	


}