package com.gaon.ifou.v3;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class depo_gowas {
	
	public static trans_ora_manager ocim 		= new trans_ora_manager();
	public static trans_util_manager utilm 		= new trans_util_manager();
	
	public String delimiter = ":";
	
	//2021.02.01 입금조회 엑셀다운로드
			@SuppressWarnings("unchecked")
			public String get_excel_0301(String tuser, String stime, String etime, String depcd, String acqcd, String mid) {
				JSONObject exceljson = new JSONObject();
				//Encoder encoder = Base64.getEncoder();
				try {
					exceljson.put("RST", "S000");
					/*
					exceljson.put("TOTALARRAY", encoder.encodeToString(ocim.get_json_0301total(tuser, stime, etime, acqcd, depcd, mid).getBytes("UTF-8")));
					exceljson.put("ITEMARRAY", encoder.encodeToString(ocim.get_json_0301item(tuser, stime, etime, acqcd, depcd, mid).getBytes("UTF-8")));
					*/
					exceljson.put("TOTALARRAY", ocim.get_json_0301total(tuser, stime, etime, acqcd, depcd, mid));
					exceljson.put("ITEMARRAY", ocim.get_json_0301item(tuser, stime, etime, acqcd, depcd, mid));
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				
				return exceljson.toJSONString();
			}
			
			public String get_0301_total(String tuser, String stime, String etime, String mid, String acqcd, String depcd) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0301total(tuser, stime, etime, acqcd, depcd, mid);
				} catch (Exception e) {}
				return rtnstr;
			}
			
			/*
			public String get_0301_item(String tuser, String stime, String etime, String mid, String acqcd, String depcd) {
				String rtnstr = "";
				int icnt = 0;
				try {
					icnt = ocim.get_json_0301item_cnt(tuser, stime, etime, mid, acqcd, depcd);
					rtnstr = ocim.get_json_0301item(tuser, stime, etime, mid, acqcd, depcd);
				} catch (Exception e) {}
				return rtnstr;
			}
			*/
			
			public String get_0301_item(String tuser, String stime, String etime, String mid, String acqcd, String depcd) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0301item(tuser, stime, etime, acqcd, depcd, mid);
				} catch (Exception e) {}
				return rtnstr;
			}
			
			
			public String get_0301detail_total(String tuser, String stime, String etime, String acqcd, String depcd, String mid, String tid, String appno, String auth01, String auth02, String auth03) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0301detail_total(tuser, stime, etime, acqcd, depcd, mid, tid, appno, auth01, auth02, auth03);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			//2021.03.18 css 적용문제로 인한 xml return
			public String get_0301detail_item(String tuser, String stime, String etime, String acqcd, String depcd, String mid, String tid, String appno, String auth01, String auth02, String auth03) {
				StringBuffer rtnstr = new StringBuffer();
				try {
					String result = ocim.get_json_0301detail_item(tuser, stime, etime, acqcd, depcd, mid, tid, appno, auth01, auth02, auth03);
					
					JSONParser resultParser = new JSONParser();
					JSONObject resultObj = (JSONObject) resultParser.parse(result);
					JSONArray resultAry = (JSONArray) resultObj.get("rows");
					
					rtnstr.append("<rows id=\"0\">");
					
					for(int i =0; i<resultAry.size(); i++) {
						
						//css 설정
						String styleb = "";
						
						JSONObject itemObj = (JSONObject) resultAry.get(i);
						String seqno = (String)itemObj.get("id");
						JSONArray itemAry = (JSONArray) itemObj.get("data");
						
						if(itemAry.get(18).toString().equals("60")) {
							
						} else if(itemAry.get(18).toString().equals("67")) {
							styleb = "candata";
						} else {
							styleb = "bandata";
						}
						
						rtnstr.append("<row id='" + seqno + "'>");
						for(int j = 0; j<itemAry.size(); j++) {
							rtnstr.append("<cell class = '"+styleb+"'>"+itemAry.get(j).toString()+"</cell>");
						}
						rtnstr.append("</row>");
					}
					
					rtnstr.append("</rows>");
					
				} catch (Exception e) {
				}
				return rtnstr.toString();
			}
			
			//2021.02.03 입금상세조회 엑셀다운로드
			@SuppressWarnings("unchecked")
			public String get_0301detail_excel(String tuser, String stime, String etime, String acqcd, String depcd, String mid, String tid, String appno, String auth01, String auth02, String auth03) {
				JSONObject exceljson = new JSONObject();
				//Encoder encoder = Base64.getEncoder();
				try {
					exceljson.put("RST", "S000");
					exceljson.put("TOTALARRAY", ocim.get_json_0301detail_total(tuser, stime, etime, acqcd, depcd, mid, tid, appno, auth01, auth02, auth03));
					exceljson.put("ITEMARRAY", ocim.get_json_0301detail_item(tuser, stime, etime, acqcd, depcd, mid, tid, appno, auth01, auth02, auth03));
				} catch (Exception e) {
					
				}
				return exceljson.toJSONString();
			}
			
			public String get_0302_total(String tuser, String stime, String etime, String sappdd, String eappdd, String acqcd, String depcd, String tid, String mid, String appno) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0302total(tuser, stime, etime, sappdd, eappdd, acqcd, depcd, tid, mid, appno);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			public String get_0302_item(String tuser, String stime, String etime, String sappdd, String eappdd, String acqcd, String depcd, String tid, String mid, String appno) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0302item(tuser, stime, etime, sappdd, eappdd, acqcd, depcd, tid, mid, appno);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			@SuppressWarnings("unchecked")
			public String get_0302_excel(String tuser, String stime, String etime, String sappdd, String eappdd, String acqcd, String depcd, String tid, String mid, String appno) {
				JSONObject exceljson = new JSONObject();
				//Encoder encoder = Base64.getEncoder();
				try {
					exceljson.put("RST", "S000");
					exceljson.put("TOTALARRAY", ocim.get_json_0302total(tuser, stime, etime, sappdd, eappdd, acqcd, depcd, tid, mid, appno));
					exceljson.put("ITEMARRAY", ocim.get_json_0302item(tuser, stime, etime, sappdd, eappdd, acqcd, depcd, tid, mid, appno));
				} catch (Exception e) {
					
				}
				return exceljson.toJSONString();
			}
			
			public String get_0302detail_total(String tuser, String stime, String etime, String sappdd, String eappdd, String sexpdd, String eexpdd, String appno, String tid, String mid, String acqcd, String depcd) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0302detail_total(tuser, stime, etime, sappdd, eappdd, sexpdd, eexpdd, appno, tid, mid, acqcd, depcd);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			public String get_0302detail_item(String tuser, String stime, String etime, String sappdd, String eappdd, String sexpdd, String eexpdd, String appno, String tid, String mid, String acqcd, String depcd) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0302detail_item(tuser, stime, etime, sappdd, eappdd, sexpdd, eexpdd, appno, tid, mid, acqcd, depcd);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			@SuppressWarnings("unchecked")
			public String get_0302detail_excel(String tuser, String stime, String etime, String sappdd, String eappdd, String sexpdd, String eexpdd, String appno, String tid, String mid, String acqcd, String depcd){
				JSONObject exceljson = new JSONObject();
				//Encoder encoder = Base64.getEncoder();
				try {
					exceljson.put("RST", "S000");
					exceljson.put("TOTALARRAY", ocim.get_json_0302detail_total(tuser, stime, etime, sappdd, eappdd, sexpdd, eexpdd, appno, tid, mid, acqcd, depcd));
					exceljson.put("ITEMARRAY", ocim.get_json_0302detail_item(tuser, stime, etime, sappdd, eappdd, sexpdd, eexpdd, appno, tid, mid, acqcd, depcd));
				} catch (Exception e) {
				}
				return exceljson.toJSONString();
			}
			
			public String get_0303_total(String tuser, String stime, String etime, String sreqdd, String ereqdd, String acqcd, String depcd, String tid, String mid, String appno) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0303total(tuser, stime, etime, sreqdd, ereqdd, acqcd, depcd, tid, mid, appno);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			public String get_0303_item(String tuser, String stime, String etime, String sreqdd, String ereqdd, String acqcd, String depcd, String tid, String mid, String appno) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0303item(tuser, stime, etime, sreqdd, ereqdd, acqcd, depcd, tid, mid, appno);
				} catch (Exception e) {}
				return rtnstr;
			}
			
			@SuppressWarnings("unchecked")
			public String get_0303_excel(String tuser, String stime, String etime, String sappdd, String eappdd, String acqcd, String depcd, String tid, String mid, String appno) {
				JSONObject exceljson = new JSONObject();
				//Encoder encoder = Base64.getEncoder();
				try {
					exceljson.put("RST", "S000");
					exceljson.put("TOTALARRAY", ocim.get_json_0303total(tuser, stime, etime, sappdd, eappdd, acqcd, depcd, tid, mid, appno));
					exceljson.put("ITEMARRAY", ocim.get_json_0303item(tuser, stime, etime, sappdd, eappdd, acqcd, depcd, tid, mid, appno));
				} catch (Exception e) {
					
				}
				return exceljson.toJSONString();
			}

			//2021.02.26 강원대병원 v3 - 거래일자상세내역  total
			public String get_0303detail_total(String tuser, String stime, String etime, String sreqdd, String ereqdd, String sexpdd, String eexpdd, String appno, String tid, String mid, String acqcd, String depcd) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0303detail_total(tuser, stime, etime, sreqdd, ereqdd, sexpdd, eexpdd, appno, tid, mid, acqcd, depcd);
				} catch (Exception e) {}
				return rtnstr;
			}
			
			//2021.02.26 강원대병원 v3 - 거래일자상세내역 item
			public String get_0303detail_item(String tuser, String stime, String etime, String sappdd, String eappdd, String sexpdd, String eexpdd, String appno, String tid, String mid, String acqcd, String depcd) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0303detail_item(tuser, stime, etime, sappdd, eappdd, sexpdd, eexpdd, appno, tid, mid, acqcd, depcd);
				} catch (Exception e) {}
				return rtnstr;
			}
			
			//2021.03.01 강원대병원 v3 - 거래내역상세정보 excel download
			@SuppressWarnings("unchecked")
			public String get_0303detail_excel(String tuser, String stime, String etime, String sappdd, String eappdd, String sexpdd, String eexpdd, String appno, String tid, String mid, String acqcd, String depcd) {
				JSONObject exceljson = new JSONObject();
				
				try {
					exceljson.put("RST", "S000");
					exceljson.put("TOTALARRAY", ocim.get_json_0303detail_total(tuser, stime, etime, sappdd, eappdd, sexpdd, eexpdd, appno, tid, mid, acqcd, depcd));
					exceljson.put("ITEMARRAY", ocim.get_json_0303detail_item(tuser, stime, etime, sappdd, eappdd, sexpdd, eexpdd, appno, tid, mid, acqcd, depcd));
				} catch (Exception e) {}
				
				return exceljson.toJSONString();
			}
			
			public String get_0304_total(String tuser, String stime, String etime, String mid, String acqcd, String depcd, String accetc) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0304total(tuser, stime, etime, mid, acqcd, depcd, accetc);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			
			@SuppressWarnings("unchecked")
			public String get_0304_excel(String tuser, String stime, String etime, String mid, String acqcd, String depcd, String accetc) {
				JSONObject exceljson = new JSONObject();
				Encoder encoder = Base64.getEncoder();
				try {
					exceljson.put("RST", "S000");
					exceljson.put("TOTALARRAY", encoder.encodeToString(ocim.get_json_0304total(tuser, stime, etime, mid, acqcd, depcd, accetc).getBytes("UTF-8")));
				} catch (Exception e) {}
				
				return exceljson.toJSONString();
			}
			
			public String get_0309_total(String tuser, String stime, String etime, String samt, String eamt, String appno, String pid
					, String tradeidx, String mid, String tid, String acqcd, String depcd, String auth01, String auth02, String auth03,
					String depreq1, String depreq2, String depreq3) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0309total(tuser, stime, etime, samt, eamt, appno, pid, tradeidx, mid, tid, acqcd, depcd, auth01, auth02, auth03, depreq1, depreq2, depreq3);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			public String get_0309_item(String tuser, String stime, String etime, String samt, String eamt, String appno, String pid
					, String tradeidx, String mid, String tid, String acqcd, String depcd, String auth01, String auth02, String auth03,
					String depreq1, String depreq2, String depreq3) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0309item(tuser, stime, etime, samt, eamt, appno, pid, tradeidx, mid, tid, acqcd, depcd, auth01, auth02, auth03, depreq1, depreq2, depreq3);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			public String get_0310_total(String tuser, String reqstime, String reqetime, String stime, String etime) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0310total(tuser, reqstime, reqetime, stime, etime);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			@SuppressWarnings("unchecked")
			public String get_0310_excel(String tuser, String reqstime, String reqetime, String stime, String etime) {
				JSONObject exceljson = new JSONObject();
				try {
					exceljson.put("RST", "S000");
					exceljson.put("TOTALARRAY", ocim.get_json_0310total(tuser, reqstime, reqetime, stime, etime));
				} catch (Exception e) {
					
				}
				return exceljson.toJSONString();
			}
			
			public String get_0310_detail_total(String tuser, String reqstime, String reqetime, String samt, String eamt, String appno, String pid
					, String tradeidx, String acqcd, String tid, String deposeq,  String depcd, String auth01, String auth02, String auth03, String tstat01, String tstat02, String tstat03, 
					String tstat04, String mid) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0310detail_total(tuser, reqstime, reqetime, samt, eamt, appno, pid, tradeidx, acqcd, tid, deposeq, depcd, auth01, auth02, auth03, tstat01, tstat02, tstat03, tstat04, mid);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			public String get_0310_detail_item(String tuser, String reqstime, String reqetime, String samt, String eamt, String appno, String pid
					, String tradeidx, String acqcd, String tid, String deposeq,  String depcd, String auth01, String auth02, String auth03, String tstat01, String tstat02, String tstat03, 
					String tstat04, String mid) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0310detail_item(tuser, reqstime, reqetime, samt, eamt, appno, pid, tradeidx, acqcd, tid, deposeq, depcd, auth01, auth02, auth03, tstat01, tstat02, tstat03, tstat04, mid);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			@SuppressWarnings("unchecked")
			public String get_0310detail_excel(String tuser, String reqstime, String reqetime, String samt, String eamt, String appno, String pid
					, String tradeidx, String acqcd, String tid, String deposeq,  String depcd, String auth01, String auth02, String auth03, String tstat01, String tstat02, String tstat03, 
					String tstat04, String mid) {
				JSONObject exceljson = new JSONObject();
				try {
					
					ArrayList<String> fieldList = ocim.get_column_field(tuser, "van", "txt");
					String[] temp = new String[fieldList.size()];
					temp = fieldList.toArray(temp);
					@SuppressWarnings("static-access")
					String pos_field = utilm.implode(",", temp);
					
					
					exceljson.put("FIELDS_TXT", pos_field);
					exceljson.put("TOTALARRAY", ocim.get_json_0310detail_total(tuser, reqstime, reqetime, samt, eamt, appno, pid, tradeidx, acqcd, tid, deposeq, depcd, auth01, auth02, auth03, tstat01, tstat02, tstat03, tstat04, mid));
					exceljson.put("ITEMARRAY", ocim.get_json_0310detail_item(tuser, reqstime, reqetime, samt, eamt, appno, pid, tradeidx, acqcd, tid, deposeq, depcd, auth01, auth02, auth03, tstat01, tstat02, tstat03, tstat04, mid));
				} catch (Exception e) {
					
				}
				return exceljson.toJSONString();
			}
			
			public String get_0311_total(String tuser, String reqstime, String reqetime, String stime, String etime) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0311total(tuser, reqstime, reqetime, stime, etime);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			@SuppressWarnings("unchecked")
			public String get_0311_excel(String tuser, String reqstime, String reqetime, String stime, String etime) {
				JSONObject exceljson = new JSONObject();
				try {
					exceljson.put("RST", "S000");
					exceljson.put("TOTALARRAY", ocim.get_json_0311total(tuser, reqstime, reqetime, stime, etime));
				} catch (Exception e) {
					
				}
				return exceljson.toJSONString();
			}
			
			public String get_0312_total(String tuser, String stime, String etime, String reqstime, String reqetime, String tid) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0312total(tuser, stime, etime, reqstime, reqetime, tid);
				} catch (Exception e) {
				}
				return rtnstr;
			}
			
			@SuppressWarnings("unchecked")
			public String get_0312_excel(String tuser, String stime, String etime, String reqstime, String reqetime, String tid) {
				JSONObject exceljson = new JSONObject();
				try {
					exceljson.put("RST", "S000");
					exceljson.put("TOTALARRAY", ocim.get_json_0312total(tuser, stime, etime, reqstime, reqetime, tid));
				} catch (Exception e) {
					
				}
				return exceljson.toJSONString();
			}
			
			//2021.03.19 강원대병원 - 계좌입금원장 파일업로드
			//파일저장 : upload/[timestamp]/file
			public String file_excelUpload(String tuser, File file) {
				String message = "";
				
				try {
					Decoder decoder = Base64.getDecoder();
					byte[] byte_tuser = decoder.decode(tuser);
					tuser = new String(byte_tuser, "UTF-8");
					
					String filename = file.getName();
					//업로드파일 확장자 체크
					String ext = filename.substring(filename.lastIndexOf(".")+1);
					
					//TB_BAS_BANKINFO 데이터 가져오기
					ArrayList<String[]> bankInfo = ocim.get_excelup_bankInfoData(tuser);
					
					if(bankInfo != null) {
						//2021.03.22 apache poi - excel read data
						//xlsx, xls 둘다 처리
						if(ext.equals("xlsx") || ext.equals("xls")) {
							Workbook workbook = WorkbookFactory.create(file);
							
							//시트선택
							Sheet sheet = workbook.getSheetAt(0);
							int rows = sheet.getPhysicalNumberOfRows();
							
							for(int i = 0; i<rows; i++) {
								Row row = sheet.getRow(i);
								if(row != null) {
									//int cells = row.getPhysicalNumberOfCells();
									
									//읽어오고 싶은 cell 데이터 직접 지정 가능
									//index 0번부터 시작
									//ex :: exp_dd가 3번째 cell, sale_amt가 6번째, acc_txt가 9번째에 있다면 2, 5, 8
									Cell expdd_cell = row.getCell(2);
									//cell 데이터 형식에 따라 다르게 가져올 것 - 하단 메소드 정의
									String expdd = excelup_GetCellValue(expdd_cell);
									
									Cell examt_cell = row.getCell(5);
									String examt = excelup_GetCellValue(examt_cell);
									
									Cell extxt_cell = row.getCell(8);
									String extxt = excelup_GetCellValue(extxt_cell);
										
									for(int j = 0; j<bankInfo.size(); j++) {
										String[] temp = bankInfo.get(i);
										String[] insertTemp = new String[5];
										
										//acc_txt, mid, acqcd 순으로
										if(temp[0].equals(extxt)) {
											insertTemp[0] = expdd;
											insertTemp[1] = examt;
											insertTemp[2] = extxt;
											insertTemp[3] = temp[1];
											insertTemp[4] = temp[2];
											
											ocim.excelup_insertBankData(tuser, insertTemp);
										}
									}
									message = "등록이 완료되었습니다.";
								}
							}
							
							workbook.close();
							
						} else {
							message = "excel 확장자가 아닙니다";		
						}
					} else {
						message = "원장 데이터 읽기 실패";
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return message;
				
			}
			
			//2022.02.09 반송집계조회
			public String get_0301_ban_total(String tuser, String stime, String etime, String mid, String acqcd, String depcd) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0301total_ban(tuser, stime, etime, acqcd, depcd, mid);
				} catch (Exception e) {}
				return rtnstr;
			}
			
			//2022.02.09 반송집계조회
			public String get_0301_ban_item(String tuser, String stime, String etime, String mid, String acqcd, String depcd) {
				String rtnstr = "";
				try {
					rtnstr = ocim.get_json_0301item_ban(tuser, stime, etime, acqcd, depcd, mid);
				} catch (Exception e) {}
				return rtnstr;
			}
			
			//2022.02.09 반송집계조회
			@SuppressWarnings("unchecked")
			public String get_excel_0301_ban(String tuser, String stime, String etime, String depcd, String acqcd, String mid) {
				JSONObject exceljson = new JSONObject();
				//Encoder encoder = Base64.getEncoder();
				try {
					exceljson.put("RST", "S000");
					/*
					exceljson.put("TOTALARRAY", encoder.encodeToString(ocim.get_json_0301total_ban(tuser, stime, etime, acqcd, depcd, mid).getBytes("UTF-8")));
					exceljson.put("ITEMARRAY", encoder.encodeToString(ocim.get_json_0301item_ban(tuser, stime, etime, acqcd, depcd, mid).getBytes("UTF-8")));
					*/
					exceljson.put("TOTALARRAY", ocim.get_json_0301total_ban(tuser, stime, etime, acqcd, depcd, mid));
					exceljson.put("ITEMARRAY", ocim.get_json_0301item_ban(tuser, stime, etime, acqcd, depcd, mid));
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				
				return exceljson.toJSONString();
			}
			
			
			//2022.02.25 매입보류
			public int get_deposit_checkup(String tuser, String seqno, String dpflag) {
				int result = 0;
				
				try {
					result = ocim.get_deposit_checkup(tuser, seqno, dpflag);
				} catch (Exception e) {
					
				}
				return result;
			}
			
			//2022.02.28 매입청구
			@SuppressWarnings("unchecked")
			public String get_deposit_request(String tuser, String stime, String etime, String samt, String eamt, String appno, String pid
					, String tradeidx, String mid, String tid, String acqcd, String depcd, String auth01, String auth02, String auth03,
					String depreq1, String depreq2, String depreq3) {
				JSONObject json = new JSONObject();
				
				try {
					int result = ocim.get_deposit_request(tuser, stime, etime, samt, eamt, appno, pid, tradeidx, mid, tid, acqcd, depcd, auth01, auth02, auth03, depreq1, depreq2, depreq3);
					
					if(result==1) {
						json.put("result", "true");
					}else if(result==0){
						json.put("result", "false");
					}
					
					json.put("value", ocim.get_deposit_request(tuser, stime, etime, samt, eamt, appno, pid, tradeidx, mid, tid, acqcd, depcd, auth01, auth02, auth03, depreq1, depreq2, depreq3));
				} catch (Exception e) {
					
				}
				System.out.println(json.toString());
				return json.toJSONString();
			}
			//2021.03.22 cell 데이터 타입 별 리턴
			private String excelup_GetCellValue(Cell cell) {
				String cellValue = "";
				
				int type = cell.getCellType();
				switch(type) {
				//BLANK
				case Cell.CELL_TYPE_BLANK:
					break;
				//T/F
				case Cell.CELL_TYPE_BOOLEAN:
					cellValue = String.valueOf(cell.getBooleanCellValue());
					break;
				//ERROR
				case Cell.CELL_TYPE_ERROR:
					cellValue = String.valueOf(cell.getErrorCellValue());
					break;
				//FORMULA
				case Cell.CELL_TYPE_FORMULA:
					cellValue = String.valueOf(cell.getCellFormula());
					break;
				case Cell.CELL_TYPE_NUMERIC:
					//날짜 데이터 일 경우
					if(DateUtil.isCellDateFormatted(cell)) {
						cellValue = String.valueOf(cell.getDateCellValue());
					} else {
						cellValue = String.valueOf(cell.getNumericCellValue());
					}
					break;
				case Cell.CELL_TYPE_STRING:
					cellValue = cell.getRichStringCellValue().getString();
					break;
				default:
					cellValue = cell.getRichStringCellValue().getString();
					break;
				}
				
				return cellValue;
			}
}
