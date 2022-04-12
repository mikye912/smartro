package com.gaon.ifou.v3;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class real_gowas {
	
	public static trans_ora_manager ocim 		= new trans_ora_manager();
	public static trans_util_manager utilm 		= new trans_util_manager();
	
	public String delimiter = ":";
	//seqno 검색 정확하게 하기 위해 추가적으로 appno 넘겨줄 것 
			@SuppressWarnings("unchecked")
			public String get_detail_0204(String seqno, String tuser, String appno) {
				String rtnjson = "";
				JSONObject obj1 = new JSONObject();
				Encoder encoder = Base64.getEncoder();
				try {
					
					System.out.println("get_detail_0204");
					System.out.println("seqno : " + seqno) ;
					System.out.println("appno : " + appno) ;
					System.out.println("tuser : " + tuser) ;
					String rtnstr = ocim.get_detail_0204(seqno, tuser, appno);
					
					obj1.put("RST", "S000");
					obj1.put("UDATA", encoder.encodeToString(rtnstr.getBytes("UTF-8")));
					
					rtnjson = obj1.toJSONString();
					
					System.out.println(obj1.toJSONString());
					
				} catch (Exception e) {
				}
				return rtnjson;
			}
			
			//반송사유조회-집계
			@SuppressWarnings("unchecked")
			public String get_json_0205_total(String tuser, String stime, String etime, String cardno, String appno) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0205total(tuser, stime, etime, cardno, appno);
				} catch (Exception e) {}
				return rtnstr;
			}
			
			//반송사유조회-상세
			@SuppressWarnings("unchecked")
			public String get_json_0205_item(String tuser, String stime, String etime, String cardno, String appno) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0205item(tuser, stime, etime, cardno, appno);
				} catch (Exception e) {}
				return rtnstr;
			}
			
			//반송사유조회-엑셀
			@SuppressWarnings("unchecked")
			public String get_0205_excel(String tuser, String stime, String etime, String cardno, String appno) {
				JSONObject exceljson = new JSONObject();
				//Encoder encoder = Base64.getEncoder();
				try {
					exceljson.put("RST", "S000");
					exceljson.put("TOTALARRAY", ocim.get_json_0205total(tuser, stime, etime, cardno, appno));
					exceljson.put("ITEMARRAY", ocim.get_json_0205item(tuser, stime, etime, cardno, appno));
				} catch (Exception e) {
					
				}
				return exceljson.toJSONString();
				}
			
			//2021.02.23 강원대병원v3 - 현금영수증 거래내역상세보기
			@SuppressWarnings("unchecked")
			public String get_detail_0211(String seqno, String tuser, String appno) {
				JSONObject obj = new JSONObject();
				Encoder encoder = Base64.getEncoder();
				try {
					obj.put("RST", "S000");
					obj.put("UDATA", encoder.encodeToString(ocim.get_detail_0211(seqno, tuser, appno).getBytes("UTF-8")));
					
					System.out.println(obj.toJSONString());
				} catch (Exception e) {}
				
				return obj.toJSONString();
			}
			
			public String get_cardlist_0204(String cardno, String tuser) {
				String rtnjson = "";
				try {
					
					System.out.println(cardno + ":" + tuser);
					rtnjson = ocim.get_cardlist_0204(cardno, tuser);
					
					System.out.println(rtnjson);
					
				} catch (Exception e) {
				}
				return rtnjson;
			}
			
			//2021.01.29 카드사별조회 엑셀다운로드
			/*
			@SuppressWarnings("unchecked")
			public String get_excel_0201(String tuser, String stime, String etime, String acqcd, String samt, String eamt, String appno, String depcd, String tid) {
				JSONObject exceljson = new JSONObject();
				Encoder encoder = Base64.getEncoder();
				
				try {
					exceljson.put("RST", "S000");
					exceljson.put("TOTALARRAY", encoder.encodeToString(ocim.get_json_0201total_excel(tuser, stime, etime, acqcd, samt, eamt, appno, depcd, tid).getBytes("UTF-8")));
					exceljson.put("ITEMARRAY", encoder.encodeToString(ocim.get_json_0201item_excel(tuser, stime, etime, acqcd, samt, eamt, appno, depcd, tid).getBytes("UTF-8")));
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				
				return exceljson.toJSONString();
			}
			*/
			
			
			//2021.02.15 강원대병원v3 카드사별조회 엑셀다운로드
			@SuppressWarnings("unchecked")
			public String get_excel_0201(String tuser, String stime, String etime, String samt, String eamt, String appno, String tid, String mid, String acqcd, String depcd) {
				JSONObject exceljson = new JSONObject();
				//Encoder encoder = Base64.getEncoder();
				
				try {
					exceljson.put("RST", "S000");
					/*
					exceljson.put("TOTALARRAY", encoder.encodeToString(ocim.get_json_0201total(tuser, stime, etime, samt, eamt, appno, tid, mid, acqcd, depcd).getBytes("UTF-8")));
					exceljson.put("ITEMARRAY", encoder.encodeToString(ocim.get_json_0201item(tuser, stime, etime, samt, eamt, appno, tid, mid, acqcd, depcd).getBytes("UTF-8")));
					*/
					exceljson.put("TOTALARRAY", ocim.get_json_0201total(tuser, stime, etime, samt, eamt, appno, tid, mid, acqcd, depcd));
					exceljson.put("ITEMARRAY", ocim.get_json_0201item(tuser, stime, etime, samt, eamt, appno, tid, mid, acqcd, depcd));
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				
				return exceljson.toJSONString();
			}
				
			//2021.02.18 강원대병원 상세내역조회 excel download
			@SuppressWarnings("unchecked")
			public String get_excel_0204(String tuser, String stime, String etime, String samt, String eamt, String appno, String acqcd, String pid, String mediid, String medi_cd,
					String cardno, String tid, String tradeidx, String depcd, String auth01, String auth02, String auth03, String mid) {
				JSONObject exceljson = new JSONObject();
				
				try {
					
					//2021.02.18 pos_field
					//arraylist to string[] -> implode -> base64 encode
					ArrayList<String> fieldList = ocim.get_column_field(tuser, "van", "txt");
					String[] temp = new String[fieldList.size()];
					temp = fieldList.toArray(temp);
					@SuppressWarnings("static-access")
					String pos_field = utilm.implode(",", temp);
					
					exceljson.put("FIELDS_TXT", pos_field);
					exceljson.put("TOTALARRAY", ocim.get_json_0204total(tuser, stime, etime, samt, eamt, appno, acqcd, pid, mediid, medi_cd, cardno, tid, tradeidx, depcd, auth01, auth02, auth03, mid));
					exceljson.put("ITEMARRAY", ocim.get_json_0204item(tuser, stime, etime, samt, eamt, appno, acqcd, pid, mediid, medi_cd, cardno, tid, tradeidx, depcd, auth01, auth02, auth03, mid));

				} catch (Exception e) {}
				
				return exceljson.toJSONString();
			}
			
			//2021.02.17 강원대병원v3 - 상세내역조회 total
			public String get_0204_total(String tuser, String stime, String etime, String samt, String eamt, String appno, String acqcd, String pid, String mediid, String medi_cd,
					String cardno, String tid, String tradeidx, String depcd, String auth01, String auth02, String auth03, String mid) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0204total(tuser, stime, etime, samt, eamt, appno, acqcd, pid, mediid, medi_cd, cardno, tid, tradeidx, depcd, auth01, auth02, auth03, mid);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			
			//2021.02.17 강원대병원v3 - 상세내역조회 item
			//2021.03.18 css 적용 문제로 인해(취소건 표기) json return -> xml return 으로 변경
			//get_json_0204item 에서 받아오는 json을 parse -> xml코드로 변경해서 return
			public String get_0204_item(String tuser, String stime, String etime, String samt, String eamt, String appno, String acqcd, String pid, String mediid, String medi_cd,
					String cardno, String tid, String tradeidx, String depcd, String auth01, String auth02, String auth03, String mid) {
				StringBuffer rtnstr = new StringBuffer();
				try {
					String result = ocim.get_json_0204item(tuser, stime, etime, samt, eamt, appno, acqcd, pid, mediid, medi_cd, cardno, tid, tradeidx, depcd, auth01, auth02, auth03, mid);
					
					JSONParser resultParser = new JSONParser();
					JSONObject resultObj = (JSONObject) resultParser.parse(result);
					JSONArray resultAry = (JSONArray) resultObj.get("rows");
							
					rtnstr.append("<rows id=\"0\">");
					
					for(int i = 0; i<resultAry.size(); i++) {
						
						//컬럼 css
						String styleb = "";
						//폰트 css
						String stylebadd = "";
						
						JSONObject itemObj = (JSONObject) resultAry.get(i);
						JSONArray itemAry = (JSONArray) itemObj.get("data");
						String seqno = (String)itemObj.get("id");
						
						//승인, 취소에 따른 css 설정
						if(itemAry.get(1).toString().equals("승인")) {

						} else if(itemAry.get(1).toString().equals("취소")) {
							styleb = "grid_bg_trans_canc";
							//appdd != oappdd
							if(!itemAry.get(2).toString().equals(itemAry.get(16).toString())) {
								stylebadd = "font_bold";
							}
						} else {
							styleb = "grid_bg_trans_none";
						}
						
						rtnstr.append("<row id='" + seqno + "'>");
						for(int j = 0; j<itemAry.size(); j++) {
							rtnstr.append("<cell class = '"+styleb+" "+stylebadd+"'>"+itemAry.get(j).toString()+"</cell>");
						}
						
						rtnstr.append("</row>");
					}
					
					rtnstr.append("</rows>");
					
				} catch (Exception e) {
				}
				
				return rtnstr.toString();
			}	
			
			public String get_0201_total(String tuser, String stime, String etime, String samt, String eamt, String appno, String tid, String mid, String acqcd, String depcd) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0201total(tuser, stime, etime, samt, eamt, appno, tid, mid, acqcd, depcd);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			//2021.02.15 강원대병원 - 카드사별조회
			//icnt 비활성화
			public String get_0201_item(String tuser, String stime, String etime, String samt, String eamt, String appno, String tid, String mid, String acqcd, String depcd) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0201item(tuser, stime, etime, samt, eamt, appno, tid, mid, acqcd, depcd);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			//2021.02.15 강원대병원 - 월일자별조회
			public String get_0202_total(String tuser, String syear, String smon, String samt, String eamt, String tid, String mid, String acqcd, String depcd) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0202total(tuser, syear, smon, samt, eamt, tid, mid, acqcd, depcd);
				} catch (Exception e) {
				}
				return rtnstr;
			}

			
			public String get_0202_item(String tuser, String syear, String smon, String samt, String eamt, String tid, String mid, String acqcd, String depcd) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0202item(tuser, syear, smon, samt, eamt, tid, mid, acqcd, depcd);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			//2021.02.16 강원대병원v3 - 월일자별조회 excel download
			@SuppressWarnings("unchecked")
			public String get_excel_0202(String tuser, String syear, String smon, String samt, String eamt, String tid, String mid, String acqcd, String depcd) {
				JSONObject exceljson = new JSONObject();

				try {
					exceljson.put("RST", "S000");

					exceljson.put("TOTALARRAY",  ocim.get_json_0202total(tuser, syear, smon, samt, eamt, tid, mid, acqcd, depcd));
					exceljson.put("ITEMARRAY",  ocim.get_json_0202item(tuser, syear, smon, samt, eamt, tid, mid, acqcd, depcd));
				} catch (Exception e) {
					
				}
				
				return exceljson.toJSONString();
			}
			
			//매장별 거래조회 total
			public String get_0203_total(String tuser, String stime, String etime, String samt, String eamt, String tid, String depcd) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0203total(tuser, stime, etime, samt, eamt, tid, depcd);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			//2021.02.16 강원대병원v3 - 매장별거래조회 item
			public String get_0203_item(String tuser, String stime, String etime, String samt, String eamt, String tid, String depcd) {
				String rtnstr = "";

				try {

					rtnstr = ocim.get_json_0203item(tuser, stime, etime, samt, eamt, tid, depcd);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			//2021.02.16 강원대병원v3 = 메장별거래조회 excel download
			@SuppressWarnings("unchecked")
			public String get_excel_0203(String tuser, String stime, String etime, String samt, String eamt, String tid, String depcd) {
				JSONObject exceljson = new JSONObject();

				try {
					exceljson.put("RST", "S000");

					exceljson.put("TOTALARRAY", ocim.get_json_0203total(tuser, stime, etime, samt, eamt, tid, depcd));
					exceljson.put("ITEMARRAY", ocim.get_json_0203item(tuser, stime, etime, samt, eamt, tid, depcd));
				} catch (Exception e) {
					
				}
				
				return exceljson.toJSONString();
			}
			
			//2021.02.17 강원대병원v3 - 상세내역조회 total
			public String get_0204cvs_total(String tuser, String stime, String etime, String samt, String eamt, String appno, String cardtp, String auth01, String auth02, String auth03,
					String can01, String can02, String can03, String mid, String tid, String acqcd, String tid2, String paging) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0204total_cvs(tuser, stime, etime, samt, eamt, appno, cardtp, auth01, auth02, auth03, can01, can02, can03, mid, tid, acqcd, tid2, paging);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			//css 적용 문제로 인해(취소건 표기) json return -> xml return 으로 변경
			public String get_0204cvs_item(String tuser, String stime, String etime, String samt, String eamt, String appno, String cardtp, String auth01, String auth02, String auth03,
					String can01, String can02, String can03, String mid, String tid, String acqcd, String tid2, String paging) {
				StringBuffer rtnstr = new StringBuffer();
				try {
					String result = ocim.get_json_0204item_cvs(tuser, stime, etime, samt, eamt, appno, cardtp, auth01, auth02, auth03, can01, can02, can03, mid, tid, acqcd, tid2, paging);
					
					JSONParser resultParser = new JSONParser();
					JSONObject resultObj = (JSONObject) resultParser.parse(result);
					JSONArray resultAry = (JSONArray) resultObj.get("rows");
							
					rtnstr.append("<rows id=\"0\">");
					
					for(int i = 0; i<resultAry.size(); i++) {
						
						//컬럼 css
						String styleb = "";
						//폰트 css
						String stylebadd = "";
						
						JSONObject itemObj = (JSONObject) resultAry.get(i);
						JSONArray itemAry = (JSONArray) itemObj.get("data");
						String seqno = (String)itemObj.get("id");
						
						//승인, 취소에 따른 css 설정
						if(itemAry.get(1).toString().equals("승인")) {

						} else if(itemAry.get(1).toString().equals("취소")) {
							styleb = "grid_bg_trans_canc";
							//appdd != oappdd
							if(!itemAry.get(2).toString().equals(itemAry.get(16).toString())) {
								stylebadd = "font_bold";
							}
						} else {
							styleb = "grid_bg_trans_none";
						}
						
						rtnstr.append("<row id='" + seqno + "'>");
						for(int j = 0; j<itemAry.size(); j++) {
							rtnstr.append("<cell class = '"+styleb+" "+stylebadd+"'>"+itemAry.get(j).toString()+"</cell>");
						}
						
						rtnstr.append("</row>");
					}
					
					rtnstr.append("</rows>");
					
				} catch (Exception e) {
				}
				
				return rtnstr.toString();
			}
			
			public JSONObject get_0204_cvs_cnt(String tuser, String stime, String etime, String samt, String eamt, String appno, String cardtp, String auth01, String auth02, String auth03,
					String can01, String can02, String can03, String mid, String tid, String acqcd, String tid2, String npage, String pmode) {
				StringBuffer xmlObj = new StringBuffer();
				JSONObject pageobj = new JSONObject();
				String icnt = null;
				
				try {
					icnt = ocim.get_json_0204cnt_cvs(tuser, stime, etime, samt, eamt, appno, cardtp, auth01, auth02, auth03, can01, can02, can03, mid, tid, acqcd, tid2);
			
					int tp = Integer.parseInt(icnt)/100;
					int nPage = 0;
					if(npage == "") {
						nPage = 0;
					}
					
					if(pmode=="1") {
						nPage = nPage + 1;
					}else if(pmode=="0") {
						nPage = nPage-1;
					}
					
					int sn = nPage*10;
					int en = sn + 10;
					
					/*
					 * System.out.println("en : "+en); System.out.println("sn : "+sn);
					 * System.out.println("tp : "+tp); System.out.println("nPage : "+nPage);
					 */
					String next = "";
					String prev = "";
					if(en<tp) {
						next = "<span onclick=move_navi(1)>&nbsp; ></span>";
					}
					
					if(sn>1) {
						prev = "<span onclick=move_navi(0)>< &nbsp;</span>";
					}
					
					int page = 0;
					String apn_page = "";
					for(int i=sn; i<en; i++) {
						if(i==page) {
							apn_page += "&nbsp;&nbsp;<span onclick=set_page("+i+")><font color=red style='line-height:40px'>"+(i+1)+"</font></span>";
						}else {
							apn_page += "&nbsp;&nbsp;<span onclick=set_page("+i+")>"+(i+1)+"</span>";
						}
					}

					apn_page = prev+apn_page;
					apn_page += next;
					
					if(pmode=="1"||pmode=="0") {
						pageobj.put("apn_page",apn_page);
						pageobj.put("nPage", nPage);
					}else {
						pageobj.put("apn_page",apn_page);
					}	
				} catch (Exception e) {
					e.printStackTrace();
				}
				return pageobj;
			}
			
			//2021.02.22 강원대병원v3 - 현금영수증 조회 total
			public String get_0211_total(String tuser, String stime, String etime, String samt, String eamt, String appno, String pid, 
					String mediid, String medi_cd, String medi_gb, String cardno, String tradeidx, String auth01, String auth02, String auth03) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0211total(tuser, stime, etime, samt, eamt, appno, pid, mediid, medi_cd, medi_gb, cardno, tradeidx, auth01, auth02, auth03);
				} catch (Exception e) {}
				
				return rtnstr;
			}
			
			//2021.02.22 강원대병원v3 - 현금영수증 조회 item
			//2021.03.18 css 적용문제로 인해 json return 에서 xml return 으로 변경
			public String get_0211_item(String tuser, String stime, String etime, String samt, String eamt, String appno, String pid, 
					String mediid, String medi_cd, String medi_gb, String cardno, String tradeidx, String auth01, String auth02, String auth03) {
				StringBuffer rtnstr = new StringBuffer();
				try {
					String result = ocim.get_json_0211item(tuser, stime, etime, samt, eamt, appno, pid, mediid, medi_cd, medi_gb, cardno, tradeidx, auth01, auth02, auth03);
					
					JSONParser resultParser = new JSONParser();
					JSONObject resultObj = (JSONObject) resultParser.parse(result);
					JSONArray resultAry = (JSONArray) resultObj.get("rows");
							
					rtnstr.append("<rows id=\"0\">");
					
					for(int i = 0; i<resultAry.size(); i++) {	
						String styleb = "";
						String stylebadd = "";
						
						JSONObject itemObj = (JSONObject) resultAry.get(i);
						JSONArray itemAry = (JSONArray) itemObj.get("data");
						String seqno = (String)itemObj.get("id");
						
						//승인, 취소에 따른 css 설정
						if(itemAry.get(1).toString().equals("승인")) {

						} else if(itemAry.get(1).toString().equals("취소")) {
							styleb = "grid_bg_trans_canc";
							//appdd != oappdd
							if(!itemAry.get(2).toString().equals(itemAry.get(13).toString())) {
								stylebadd = "font_bold";
							}
						} else {
							styleb = "grid_bg_trans_none";
						}
						
						rtnstr.append("<row id='" + seqno + "'>");
						for(int j = 0; j<itemAry.size(); j++) {
							rtnstr.append("<cell class = '"+styleb+" "+stylebadd+"'>"+itemAry.get(j).toString()+"</cell>");
						}
						
						rtnstr.append("</row>");
					}
					
					rtnstr.append("</rows>");
				
				} catch (Exception e) {}
				
				return rtnstr.toString();
			}
			
			
			//2021.02.22 강원대병원v3 - 현금영수증 excel download
			@SuppressWarnings("unchecked")
			public String get_excel_0211(String tuser, String stime, String etime, String samt, String eamt, String appno, String pid, 
					String mediid, String medi_cd, String medi_gb, String cardno, String tradeidx, String auth01, String auth02, String auth03) {
				JSONObject exceljson = new JSONObject();
				try {
					ArrayList<String> fieldList = ocim.get_column_field(tuser, "tr", "txt");
					String[] temp = new String[fieldList.size()];
					temp = fieldList.toArray(temp);
					@SuppressWarnings("static-access")
					String pos_field = utilm.implode(",", temp);
					
					exceljson.put("FIELDS_TXT", pos_field);
					exceljson.put("ITEMARRAY", ocim.get_json_0211item(tuser, stime, etime, samt, eamt, appno, pid, mediid, medi_cd, medi_gb, cardno, tradeidx, auth01, auth02, auth03));
					
				} catch (Exception e) {}
				
				return exceljson.toJSONString();
			}
			
			//2021.03.17 강원대병원 - 매출달력 total
			public String get_0210_cal_total(String tuser, String syear, String smon, String acqcd, String mid, String tid, String depcd) {
				String rtnctr = "";
				
				try {
					Decoder decoder = Base64.getDecoder();
					byte[] byte_tuser = decoder.decode(tuser);
					tuser = new String(byte_tuser, "UTF-8");
					
					rtnctr = ocim.get_json_0210_cal_total(tuser, syear, smon, acqcd, mid, tid, depcd);
				} catch (Exception e) {
					
				}
				
				return rtnctr;
			}
			
			//2021.03.15 강원대병원 - 매출달력 detail, 카드데이터
			public String get_0210_cal_item_card(String tuser, String syear, String smon, String acqcd, String mid, String tid, String depcd) {
				String result = "";
				
				try {
					Decoder decoder = Base64.getDecoder();
					byte[] byte_tuser = decoder.decode(tuser);
					tuser = new String(byte_tuser, "UTF-8");
					
					result = ocim.get_json_0210_cal(tuser, syear, smon, acqcd, mid, tid, depcd, "CARD");
					
				} catch (Exception e) {
					
				}
				
				return result;
			}
			
			//2021.03.15 강원대병원 - 매출달력 detail, 현금데이터
			public String get_0210_cal_item_cash(String tuser, String syear, String smon, String acqcd, String mid, String tid, String depcd) {
				String result = "";
					
				try {
					Decoder decoder = Base64.getDecoder();
					byte[] byte_tuser = decoder.decode(tuser);
					tuser = new String(byte_tuser, "UTF-8");
					
					result = ocim.get_json_0210_cal(tuser, syear, smon, acqcd, mid, tid, depcd, "CASH");
						
				} catch (Exception e) {
						
				}
					
				return result;
			}
}
