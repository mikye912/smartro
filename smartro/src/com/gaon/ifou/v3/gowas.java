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
			strbuf.append("<option value=''>:: ����μ��� ::</option>");
			
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
			strbuf.append("<option value=''>:: �ܸ��⼱�� ::</option>");
			
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
			strbuf.append("<option value=''>:: ī��缱�� ::</option>");
			
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
	 * ������� ID/PW�� �Է¹޾� �α��� �������� ����
	 * @param uid
	 * @param upw
	 * @return ���� �迭 >> 0:�����ڵ�, 1:�α�������
	 */
	//2021.03.11 tuser ������ base64 encoding
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
				rtnstr[0] = "R000";//�����ʱ�ȭ
			}
			
			if(usechk>4) {
				rtnstr[0] = "R001"; //�������
			}
			
			if(ucnt > 0) {
				String[] uinfo = ocim.get_user_info(uid);
				String[] orginfo = new String[4];
				
				if(upw.trim().equals(uinfo[0].trim())) { // ��й�ȣ�� ���� �ϴٸ�
					ocim.get_use_chk_reset(uid); //use_chk 0���� ����
					int insert_log = ocim.get_insert_user_log(uid);//log���
					rtnstr[0] = "S000";
					
					//�������� ����
					//ID:ORGCD:DEPCD:MKTIME:PTAB:VTAB:DTAB:U_LV
					if(uinfo[2].trim().length()>0) {
						orginfo = ocim.get_org_info(uinfo[2]);
					}
					
					DateFormat format = new SimpleDateFormat("yyyyMMdd");
					String ndate = format.format(new Date());
					
					// ���� �ð�
					LocalTime now = LocalTime.now();
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");
					String formatedNow = now.format(formatter);
					
					strbuf.append(uid); // ���� ���̵�
					strbuf.append(delimiter + trans_util_manager.setDefault(uinfo[2])); // ORGCD
					strbuf.append(delimiter + trans_util_manager.setDefault(uinfo[1])); // DEPCD
					strbuf.append(delimiter + ""); // �����Ͻ�(Unix timestamp), ����
					strbuf.append(delimiter + trans_util_manager.setDefault(orginfo[0]));
					strbuf.append(delimiter + trans_util_manager.setDefault(orginfo[1]));
					strbuf.append(delimiter + trans_util_manager.setDefault(orginfo[2]));
					strbuf.append(delimiter + trans_util_manager.setDefault(uinfo[3])); //userlv
					strbuf.append(delimiter + trans_util_manager.setDefault(uinfo[4])); //auth_seq
					strbuf.append(delimiter + ndate+formatedNow);
					
					//2021.03.11 tuser�� base64 encoding
					String tmuser = strbuf.toString();
					byte[] byte_user = tmuser.getBytes("UTF-8");
					rtnstr[1] = encoder.encodeToString(byte_user);
					
					//����� �޴� ����
					String tmpm = general_menu(uinfo[2], uinfo[4]);
					byte[] targetBytes = tmpm.getBytes("UTF-8");
					rtnstr[2] = encoder.encodeToString(targetBytes);
					
					//����μ��� 1t Box ����
					String str_depo = general_depo_select(uinfo[2], uinfo[1]);//orgcd, depcd�� �̿��ؼ� �˻� �� �� �ִ� ����� ����
					byte[] byt_depo = str_depo.getBytes("UTF-8");
					rtnstr[3] = encoder.encodeToString(byt_depo);

					//�ܸ��⼱�� Select Box ����
					String str_tid = general_tid_select(uinfo[2], uinfo[1]);//orgcd, depcd�� �̿��ؼ� �˻� �� �� �ִ� �ܸ��� ����
					byte[] byt_tid = str_tid.getBytes("UTF-8");

					rtnstr[4] = encoder.encodeToString(byt_tid);
					
					//ī��缱�� Select Box ����
					String str_acq = general_acq_select();
					byte[] byt_acq = str_acq.getBytes("UTF-8");

					rtnstr[5] = encoder.encodeToString(byt_acq);
					
				}else {
					int pwcheck = ocim.get_update_use_chk(uid);
					rtnstr[0] = "F002"; //��й�ȣ �� ��ġ
				}
			}else {
				rtnstr[0] = "F001"; // ȸ�����̵� ã�� �� ����.
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
	
	//2021.02.16 �����뺴��v3 - �󼼳�����ȸ/���ݿ����� accountGrid �׸� setting
	//van - �󼼳�����ȸ, tr - ���ݿ�������ȸ
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


	//2021.03.03 �����뺴�� v3 - ������������
	//�˻��Ⱓ, ���Ի�
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
				xmlObj.append("<tr><td colspan='8' align='center'>�˻��� �ڷᰡ �����ϴ�.</td></tr>");
			} else {
				//�Ұ�, ���հ� ���
				//number format setting
				DecimalFormat formatter = new DecimalFormat("###,###");
				
				for(int i = 0; i<resultAry.size(); i++) {
					JSONObject itemObj = (JSONObject) resultAry.get(i);
					JSONArray itemAry = (JSONArray) itemObj.get("data");
					
					//�Ϲ� ������ tr, �Ұ� ������ tr, �հ� ������ tr
					if(itemAry.get(0).toString().equals("�Ұ�") || itemAry.get(0).toString().equals("�հ�")) {
						//�հ�κ� Total(stime ~ etime)
						int acnt = Integer.parseInt(itemAry.get(3).toString());
						int ccnt = Integer.parseInt(itemAry.get(5).toString());
						int tcnt = acnt + ccnt;
						long aamt = Long.parseLong(itemAry.get(4).toString());
						long camt = Long.parseLong(itemAry.get(6).toString());
						long tamt = aamt - camt;
						
						xmlObj.append("<tr>");
						if(itemAry.get(0).toString().equals("�հ�")) {
							xmlObj.append("<td colspan='3' align='center' bgcolor='#f0f0f0'>Total<br>("+itemAry.get(1)+" ~ "+itemAry.get(2)+")</td>");
						} else {
							xmlObj.append("<td colspan='3' align='center' bgcolor='#f0f0f0'>�Ұ�("+itemAry.get(1)+")</td>");
						}
						xmlObj.append("<td colspan='2' align='right' bgcolor='#f0f0f0'>���� "+formatter.format(tcnt)+"��<br>("+formatter.format(acnt)+" + "+formatter.format(ccnt)+")</td>");
						xmlObj.append("<td colspan='3' align='right' bgcolor='#f0f0f0'>���� "+formatter.format(tamt)+"��<br>("+formatter.format(aamt)+" - "+formatter.format(camt)+")</td>");
						xmlObj.append("</tr>");
					} else {
						//�׳� �Ϲ� ������ ���
						//������ ǥ��
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
	
	//2021.03.04 �����뺴�� v3 - ������������ excel download
	//excel_0000�� ���İ� ��� -> encoding ���·� ������ ������ JSONObject�� ����
	//exceldn_0000���� ���̷�Ʈ�� ���� ��� -> tr, td �±� ������� ���·� encoding ���� ����
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
	
	
	//2021.02.23 �����뺴��v3 - ��ü�ŷ���������(sub02_04 -> detail_view)
	//seqno �˻� ��Ȯ�ϰ� �ϱ� ���� �߰������� appno �Ѱ��� �� 
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
	
	//�ݼۻ�����ȸ-����
	@SuppressWarnings("unchecked")
	public String get_json_0205_total(String tuser, String stime, String etime, String cardno, String appno) {
		String rtnstr = "";
		try {
			rtnstr = ocim.get_json_0205total(tuser, stime, etime, cardno, appno);
		} catch (Exception e) {}
		return rtnstr;
	}
	
	//�ݼۻ�����ȸ-��
	@SuppressWarnings("unchecked")
	public String get_json_0205_item(String tuser, String stime, String etime, String cardno, String appno) {
		String rtnstr = "";
		try {
			rtnstr = ocim.get_json_0205item(tuser, stime, etime, cardno, appno);
		} catch (Exception e) {}
		return rtnstr;
	}
	
	//�ݼۻ�����ȸ-����
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
	
	//2021.02.23 �����뺴��v3 - ���ݿ����� �ŷ������󼼺���
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
	
	//2021.02.24 �����뺴��v3 - ����� proc
	public String transcancel_mst(String seqno, String tuser, String appno) {
		String result = "";
		StringBuffer tr_data = new StringBuffer();
		
		//1. �ǽð� ���̺��� ���� ������
		//2. ���� �����ؼ� ���� ����
		//3. �������� �޾ƿ�
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
			
			//��������
			tr_data.append("0417"); //��������(4)
			tr_data.append("HPS"); //����TEXT(3)
			tr_data.append(utilm.rpad(dataObj.get("TID").toString(), 10, " ")); //TID(10)
			tr_data.append("0000000001"); //�Ϸù�ȣ(10)
			tr_data.append("0420"); //��������(4)
			tr_data.append("30"); //�ŷ�����(2)
			tr_data.append("H1"); //��������(2)
			tr_data.append("          "); //����ȣ(10)
			tr_data.append(utilm.rpad(dataObj.get("TID").toString(), 10, " ")); //TID(10)
			tr_data.append("@"); //WCC(1)
			tr_data.append("NS"); //�ŷ��Ϸù�ȣŰ(2)
			tr_data.append(utilm.rpad(dataObj.get("TRANIDX").toString(), 18, " ")); //�ŷ��Ϸù�ȣ(18)
			tr_data.append("                                                                                                           "); //����(107)
			tr_data.append(utilm.rpad(dataObj.get("HALBU").toString(), 10, " ")); //���ŷ� �Һΰ���(2)
			tr_data.append(utilm.rpad("0", 12, "0")); //���ŷ� �����(12)
			tr_data.append(utilm.rpad("0", 12, "0")); //���ŷ� ����(12)
			tr_data.append(utilm.rpad(dataObj.get("AMOUNT").toString(), 12, "0")); //���ŷ� �ѱݾ�(12)
			tr_data.append(utilm.rpad(dataObj.get("APPNO").toString(), 8, " ")); //���ŷ� ���ι�ȣ(12)
			tr_data.append(utilm.rpad(dataObj.get("APPDD").toString(), 6, " ")); //���ŷ� ��������(6)
			tr_data.append("            "); //���ŷ�������ȣ(12)
			tr_data.append("          "); //����ڵ�Ϲ�ȣ(10)
			tr_data.append("             "); //�ֹι�ȣ(13)
			tr_data.append("                "); //PIN(16)
			tr_data.append("                              "); //DOMAIN (30)
			tr_data.append("                    "); //ID ADDRESS(20)
			tr_data.append("                "); //HW �𵨹�ȣ(16)
			tr_data.append("                "); //SW �𵨹�ȣ(16)
			tr_data.append("  "); //FALLBACK(2)
			if(userexp[1].equals("OR0010")){
				tr_data.append("GWH"); //�����ڵ�(3) :: �����뺴��
			} else {
				tr_data.append("KHM");
			}
			String exp_field = dataObj.get("EXT_FIELD").toString();
			exp_field = exp_field.replaceAll("/", "|");
			tr_data.append(utilm.rpad(exp_field, 47, " ")); //������������(47)
			tr_data.append("N"); //sign ���а�(1)
			
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
	
	//2021.01.29 ī��纰��ȸ �����ٿ�ε�
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
	
	
	//2021.02.15 �����뺴��v3 ī��纰��ȸ �����ٿ�ε�
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
		
	//2021.02.18 �����뺴�� �󼼳�����ȸ excel download
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
	
	//2021.02.01 �Ա���ȸ �����ٿ�ε�
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
		
	//2021.02.17 �����뺴��v3 - �󼼳�����ȸ total
	public String get_0204_total(String tuser, String stime, String etime, String samt, String eamt, String appno, String acqcd, String pid, String mediid, String medi_cd,
			String cardno, String tid, String tradeidx, String depcd, String auth01, String auth02, String auth03, String mid) {
		String rtnstr = "";
		try {
			rtnstr = ocim.get_json_0204total(tuser, stime, etime, samt, eamt, appno, acqcd, pid, mediid, medi_cd, cardno, tid, tradeidx, depcd, auth01, auth02, auth03, mid);
		} catch (Exception e) {
		}
		return rtnstr;
	}
	
	
	//2021.02.17 �����뺴��v3 - �󼼳�����ȸ item
	//2021.03.18 css ���� ������ ����(��Ұ� ǥ��) json return -> xml return ���� ����
	//get_json_0204item ���� �޾ƿ��� json�� parse -> xml�ڵ�� �����ؼ� return
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
				
				//�÷� css
				String styleb = "";
				//��Ʈ css
				String stylebadd = "";
				
				JSONObject itemObj = (JSONObject) resultAry.get(i);
				JSONArray itemAry = (JSONArray) itemObj.get("data");
				String seqno = (String)itemObj.get("id");
				
				//����, ��ҿ� ���� css ����
				if(itemAry.get(1).toString().equals("����")) {

				} else if(itemAry.get(1).toString().equals("���")) {
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
	
	//2021.02.15 �����뺴�� - ī��纰��ȸ
	//icnt ��Ȱ��ȭ
	public String get_0201_item(String tuser, String stime, String etime, String samt, String eamt, String appno, String tid, String mid, String acqcd, String depcd) {
		String rtnstr = "";
		try {
			rtnstr = ocim.get_json_0201item(tuser, stime, etime, samt, eamt, appno, tid, mid, acqcd, depcd);
		} catch (Exception e) {
		}
		return rtnstr;
	}
	
	//2021.02.15 �����뺴�� - �����ں���ȸ
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
	
	//2021.02.16 �����뺴��v3 - �����ں���ȸ excel download
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
	
	//���庰 �ŷ���ȸ total
	public String get_0203_total(String tuser, String stime, String etime, String samt, String eamt, String tid, String depcd) {
		String rtnstr = "";
		try {
			rtnstr = ocim.get_json_0203total(tuser, stime, etime, samt, eamt, tid, depcd);
		} catch (Exception e) {
		}
		return rtnstr;
	}
	
	//2021.02.16 �����뺴��v3 - ���庰�ŷ���ȸ item
	public String get_0203_item(String tuser, String stime, String etime, String samt, String eamt, String tid, String depcd) {
		String rtnstr = "";

		try {

			rtnstr = ocim.get_json_0203item(tuser, stime, etime, samt, eamt, tid, depcd);
		} catch (Exception e) {
		}
		return rtnstr;
	}
	
	//2021.02.16 �����뺴��v3 = ���庰�ŷ���ȸ excel download
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
	
	//2021.02.17 �����뺴��v3 - �󼼳�����ȸ total
	public String get_0204cvs_total(String tuser, String stime, String etime, String samt, String eamt, String appno, String cardtp, String auth01, String auth02, String auth03,
			String can01, String can02, String can03, String mid, String tid, String acqcd, String tid2, String paging) {
		String rtnstr = "";
		try {
			rtnstr = ocim.get_json_0204total_cvs(tuser, stime, etime, samt, eamt, appno, cardtp, auth01, auth02, auth03, can01, can02, can03, mid, tid, acqcd, tid2, paging);
		} catch (Exception e) {
		}
		return rtnstr;
	}
	
	//css ���� ������ ����(��Ұ� ǥ��) json return -> xml return ���� ����
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
				
				//�÷� css
				String styleb = "";
				//��Ʈ css
				String stylebadd = "";
				
				JSONObject itemObj = (JSONObject) resultAry.get(i);
				JSONArray itemAry = (JSONArray) itemObj.get("data");
				String seqno = (String)itemObj.get("id");
				
				//����, ��ҿ� ���� css ����
				if(itemAry.get(1).toString().equals("����")) {

				} else if(itemAry.get(1).toString().equals("���")) {
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
	
	//2021.02.22 �����뺴��v3 - ���ݿ����� ��ȸ total
	public String get_0211_total(String tuser, String stime, String etime, String samt, String eamt, String appno, String pid, 
			String mediid, String medi_cd, String medi_gb, String cardno, String tradeidx, String auth01, String auth02, String auth03) {
		String rtnstr = "";
		try {
			rtnstr = ocim.get_json_0211total(tuser, stime, etime, samt, eamt, appno, pid, mediid, medi_cd, medi_gb, cardno, tradeidx, auth01, auth02, auth03);
		} catch (Exception e) {}
		
		return rtnstr;
	}
	
	//2021.02.22 �����뺴��v3 - ���ݿ����� ��ȸ item
	//2021.03.18 css ���빮���� ���� json return ���� xml return ���� ����
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
				
				//����, ��ҿ� ���� css ����
				if(itemAry.get(1).toString().equals("����")) {

				} else if(itemAry.get(1).toString().equals("���")) {
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
	
	
	//2021.02.22 �����뺴��v3 - ���ݿ����� excel download
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
	
	//2021.03.18 css ���빮���� ���� xml return
	public String get_0301detail_item(String tuser, String stime, String etime, String acqcd, String depcd, String mid, String tid, String appno, String auth01, String auth02, String auth03) {
		StringBuffer rtnstr = new StringBuffer();
		try {
			String result = ocim.get_json_0301detail_item(tuser, stime, etime, acqcd, depcd, mid, tid, appno, auth01, auth02, auth03);
			
			JSONParser resultParser = new JSONParser();
			JSONObject resultObj = (JSONObject) resultParser.parse(result);
			JSONArray resultAry = (JSONArray) resultObj.get("rows");
			
			rtnstr.append("<rows id=\"0\">");
			
			for(int i =0; i<resultAry.size(); i++) {
				
				//css ����
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
	
	//2021.02.03 �Աݻ���ȸ �����ٿ�ε�
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

	//2021.02.26 �����뺴�� v3 - �ŷ����ڻ󼼳���  total
	public String get_0303detail_total(String tuser, String stime, String etime, String sreqdd, String ereqdd, String sexpdd, String eexpdd, String appno, String tid, String mid, String acqcd, String depcd) {
		String rtnstr = "";
		try {
			rtnstr = ocim.get_json_0303detail_total(tuser, stime, etime, sreqdd, ereqdd, sexpdd, eexpdd, appno, tid, mid, acqcd, depcd);
		} catch (Exception e) {}
		return rtnstr;
	}
	
	//2021.02.26 �����뺴�� v3 - �ŷ����ڻ󼼳��� item
	public String get_0303detail_item(String tuser, String stime, String etime, String sappdd, String eappdd, String sexpdd, String eexpdd, String appno, String tid, String mid, String acqcd, String depcd) {
		String rtnstr = "";
		try {
			rtnstr = ocim.get_json_0303detail_item(tuser, stime, etime, sappdd, eappdd, sexpdd, eexpdd, appno, tid, mid, acqcd, depcd);
		} catch (Exception e) {}
		return rtnstr;
	}
	
	//2021.03.01 �����뺴�� v3 - �ŷ����������� excel download
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

	//2021.03.04 �����뺴�� - �ý��۰��� [�������] readData
	public String[] get_060501_item(String tuser) {
		String[] data = new String[9];
		try {
			data = ocim.get_json_060501item(tuser);
		} catch (Exception e) {
		}
		
		return data;
	}
	
	//2021.03.04 �����뺴�� - �ý��۰��� [�������] ��������� readData
	public String get_060501_item_userList(String tuser) {
		String data = "";
		
		try {
			data = ocim.get_json_060501item_userList(tuser);
		} catch (Exception e) {
			
		}
		
		return data;
	}
	
	//2021.03.04 �����뺴�� - �ý��۰��� [�������] data update
	public int get_060501_item_upadte(String tuser, String comnm, String comno, String comexno, String comceo,
			String cometype, String comservice, String comtel, String comaddr, String orgmemo) {
		int result = 0;
		try {
			result = ocim.get_json_060501item_update(tuser, comnm, comno, comexno, comceo, cometype, comservice, comtel, comaddr, orgmemo);
		} catch (Exception e) {
		}
		
		return result;
	}
	
	//2021.03.04 �����뺴�� - �ý��۰��� [����ΰ���] readData
	public String get_060502_item(String tuser) {
		String result = "";
		
		try {
			result = ocim.get_json_060502item(tuser);
		} catch (Exception e) {
			
		}
		
		return result;
	}
	
	//2021.03.05 �����뺴�� - �ý��۰��� [����ΰ���] - �ܸ������ tab ���
	public String get_060502_item_tid_list(String orgcd, String depcd) {
		String result = "";
		try {
			result = ocim.get_json_060502item_tid_list(orgcd, depcd);
		} catch (Exception e) {
			
		}
		
		return result;
	}
	
	//2021.03.05 �����뺴�� - �ý��۰��� [����ΰ���] �ܸ������ update
	public int get_060502_item_tid_list_update(String orgcd, String depcd, String[] tid, String tuser) {
		int result = 0;
		
		try {
			result = ocim.get_json_060502item_tid_list_update(orgcd, depcd, tid, tuser);
		} catch (Exception e) {
			result= -1;
		}
		
		return result;
	}
	
	//2021.03.05 �����뺴�� - �ý��۰��� [����ΰ���] - ���������� tab ���
	public String get_060502_item_mid_list(String orgcd, String depcd) {
		String result = "";
		try {
			result = ocim.get_json_060502item_mid_list(orgcd, depcd);
		} catch (Exception e) {
				
		}
			
		return result;
	}
	
	//2021.03.05 �����뺴�� - �ý��۰��� [����ΰ���] ���������� update
	public int get_060502_item_mid_list_update(String orgcd, String depcd, String[] mid, String tuser) {
		int result = 0;
		
		try {
			result = ocim.get_json_060502item_mid_list_update(orgcd, depcd, mid, tuser);
		} catch (Exception e) {
			result = -1;
		}
		
		return result;
	}
	
	//2021.03.05 �����뺴�� - �ý��۰��� [����ΰ���] ����� ���� tab ���
	//����� ���� �� ��������̶� �����ϰ� ����ص� �ɵ�?
	public String[] get_060502_item_deplist(String orgcd, String depcd) {
		String data[] = new String[5];
		
		try {
			data = ocim.get_json_060502item_deplist(orgcd, depcd);
		} catch (Exception e) {
			
		}
		
		return data;
	}
	
	//2021.03.05 �����뺴�� - �ý��۰��� [����ΰ���] ����� update
	public int get_060502_item_deposit_update(String dep_nm, String dep_adm_user, String dep_tel1, 
			String dep_email, String dep_type, String orgcd, String depcd)  {
		int result = 0;
		
		try {
			result = ocim.get_json_060502item_deposit_update(dep_nm, dep_adm_user, dep_tel1, dep_email, dep_type, orgcd, depcd);
		} catch (Exception e) {
		}
		
		return result;
	}
	
	//2021.03.05 �����뺴�� - �ý��۰��� [����ΰ���] ����� delete
	public int get_060502_item_deposit_delete(String orgcd, String depcd) {
		int result = 0;
		
		try {
			result = ocim.get_json_060502item_deposit_delete(orgcd, depcd);
		} catch (Exception e) {
		}
		
		return result;
	}
	
	//2021.03.08 �����뺴�� - �ý��۰��� [����ΰ���] ����� insert
	public int get_060502_deposit_insert(String tuser, String dep_nm, String dep_adm_user, String dep_tel1, 
			String dep_email, String dep_type) {
		int result = 0;
		
		try {
			result = ocim.get_json_060502item_deposit_insert(tuser, dep_nm, dep_adm_user, dep_tel1, dep_email, dep_type);
		} catch (Exception e) {
		}
		
		return result;
	}
	
	//2021.03.08 �����뺴�� - �ý��۰��� [����������] ������ ����Ʈ load
	public String get_060503_item_merlist(String tuser) {
		String list = "";
		
		try {
			list = ocim.get_json_060503_item_merlist(tuser);
		} catch (Exception e) {
			
		}
		
		return list;
		
	}
	
	//2021.03.08 �����뺴�� - �ý��۰��� [����������] ��������ȣ �����Ȳ load
	public String get_060503_item_mermap(String tuser) {
		String list = "";
		
		try {
			list = ocim.get_json_060503_item_mermap(tuser);
		} catch (Exception e) {
			
		}
		
		return list;
	}
	
	//2021.03.08 �����뺴�� - �ý��۰��� [����������] ��������ȣ ����/���� tab ���� �ҷ�����
	public String[] get_060503_item_merData(String orgcd, String mercd, String mid) {
		String[] data = new String[10];
		try {
			data = ocim.get_json_060503_item_merData(orgcd, mercd, mid);
		} catch (Exception e) {
			
		}
		return data;
	}
	
	//2021.03.08 �����뺴�� - �ý��۰��� [����������] ��������ȣ ���� tab ���� �����ϱ�
	public int get_060503_item_merData_update(String orgcd, String mercd, String depcd, String mid, String purcd, 
			String merst, String meret, String fee01, String fee02, String fee03) {
		int result = 0;
		try {
			result = ocim.get_json_060503_item_merData_update(orgcd, mercd, depcd, mid, purcd, merst, meret, fee01, fee02, fee03);
		} catch (Exception e) {
			
		}
		return result;
	}
	
	//2021.03.08 �����뺴�� - �ý��۰��� [����������] ��������ȣ ���� tab ���� �����ϱ�
	public int get_060503_item_merData_delete(String orgcd, String mid) {
		int result = 0;
		try {
			result = ocim.get_json_060503_item_merData_delete(orgcd, mid);
		} catch (Exception e) {
			
		}
		return result;
	}
	
	//�ý��۰��� [����������] ������ �߰�
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
	
	
	//2021.03.09 �����뺴�� - �ý��۰��� [�ܸ������] �ܸ����ȣ �ܸ��� ���� read
	public String[] get_060503_item_tidData(String depcd, String orgcd, String tid) {
		String[] data = new String[6];
		
		try {
			data = ocim.get_json_060503_item_tidData(depcd, orgcd, tid);
		} catch (Exception e) {
			
		}
		
		return data;
	}
	
	
	//2021.03.09 �����뺴�� - �ý��۰��� [�ܸ������] �ܸ����ȣ �ܸ��� ���� update
	public int get_060503_item_tid_update(String orgcd, String tidcd, String depcd, String tidnm, String tid, String vangb, String term_type) {
		int result = 0;
		
		try {
			result = ocim.get_json_060503_item_tid_update(orgcd, tidcd, depcd, tidnm, tid, vangb, term_type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	//2021.03.09 �����뺴�� - �ý��۰��� [�ܸ������] �ܸ����ȣ �ܸ��� ���� delete
	public int get_060503_item_tid_delete(String orgcd, String tid) {
		int result = 0;
		
		try {
			result = ocim.get_json_060503_item_tid_delete(orgcd, tid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	//2021.03.08 �����뺴�� - �ý��۰��� [�ܸ������] �ܸ����ȣ insert
	public int get_060504_item_insert(String tuser, String depcd, String tid, String term_nm, String term_type, String vangb) {
		int result = 0;
		
		try {
			result = ocim.get_json_060504_item_insert(tuser, depcd, tid, term_nm, term_type, vangb);
		} catch (Exception e) {
			
		}
		
		return result;
	}
	
	//2021.03.09 �����뺴�� - �ý��۰��� [����ڰ���] ���� read
	public String[] get_060505_item_userInfo(String mem_cd) {
		String[] data = new String[9];
		
		try {
			data = ocim.get_json_060505_item_userInfo(mem_cd);
		} catch (Exception e) {
			
		}
		
		return data;
	}
	
	//2021.03.09 �����뺴�� - �ý��۰��� [����ڰ���] ���� update
	public int get_060505_item_userInfo_update(String orgcd, String depcd, String memcd, String mem_id, String mem_pw, 
			String mem_nm, String memlv, String mem_tel1, String mem_tel2, String mem_email) {
		int result = 0;
		
		try {
			result = ocim.get_json_060505_item_userInfo_update(orgcd, depcd, memcd, mem_id, mem_pw, mem_nm, memlv, mem_tel1, mem_tel2, mem_email);
		} catch (Exception e) {
			
		}
		
		return result;
	}
	
	//2021.03.09 �����뺴�� - �ý��۰��� [����ڰ���] ���� delete
	public int get_060505_item_userInfo_delete(String orgcd, String memcd) {
		int result = 0;
			
		try {
			result = ocim.get_json_060505_item_userInfo_delete(orgcd, memcd);
		} catch (Exception e) {
				
		}
			
		return result;
	}
	
	//2021.03.10 �����뺴�� - �ý��۰��� [����ڰ���] ���� insert
	public int get_060505_item_userInfo_insert(String tuser, String depcd, String memid, 
			String mempw, String memnm, String memlv, String memtel1, String memtel2, String mememail) {
		int result = 0;
		
		try {
			result = ocim.get_json_060505_item_insert(tuser, depcd, memid, mempw, memnm, memlv, memtel1, memtel2, mememail);
		} catch (Exception e) {
			
		}
		
		return result;
	}
	
	//2021.03.10 �����뺴�� - �ܸ���, ����� ����/����/������ ���� session data �缳��
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
	
	//2021.03.17 �����뺴�� - ����޷� total
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
	
	//2021.03.15 �����뺴�� - ����޷� detail, ī�嵥����
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
	
	//2021.03.15 �����뺴�� - ����޷� detail, ���ݵ�����
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
	
	
	//2021.03.19 �����뺴�� - �����Աݿ��� ���Ͼ��ε�
	//�������� : upload/[timestamp]/file
	public String file_excelUpload(String tuser, File file) {
		String message = "";
		
		try {
			Decoder decoder = Base64.getDecoder();
			byte[] byte_tuser = decoder.decode(tuser);
			tuser = new String(byte_tuser, "UTF-8");
			
			String filename = file.getName();
			//���ε����� Ȯ���� üũ
			String ext = filename.substring(filename.lastIndexOf(".")+1);
			
			//TB_BAS_BANKINFO ������ ��������
			ArrayList<String[]> bankInfo = ocim.get_excelup_bankInfoData(tuser);
			
			if(bankInfo != null) {
				//2021.03.22 apache poi - excel read data
				//xlsx, xls �Ѵ� ó��
				if(ext.equals("xlsx") || ext.equals("xls")) {
					Workbook workbook = WorkbookFactory.create(file);
					
					//��Ʈ����
					Sheet sheet = workbook.getSheetAt(0);
					int rows = sheet.getPhysicalNumberOfRows();
					
					for(int i = 0; i<rows; i++) {
						Row row = sheet.getRow(i);
						if(row != null) {
							//int cells = row.getPhysicalNumberOfCells();
							
							//�о���� ���� cell ������ ���� ���� ����
							//index 0������ ����
							//ex :: exp_dd�� 3��° cell, sale_amt�� 6��°, acc_txt�� 9��°�� �ִٸ� 2, 5, 8
							Cell expdd_cell = row.getCell(2);
							//cell ������ ���Ŀ� ���� �ٸ��� ������ �� - �ϴ� �޼ҵ� ����
							String expdd = excelup_GetCellValue(expdd_cell);
							
							Cell examt_cell = row.getCell(5);
							String examt = excelup_GetCellValue(examt_cell);
							
							Cell extxt_cell = row.getCell(8);
							String extxt = excelup_GetCellValue(extxt_cell);
								
							for(int j = 0; j<bankInfo.size(); j++) {
								String[] temp = bankInfo.get(i);
								String[] insertTemp = new String[5];
								
								//acc_txt, mid, acqcd ������
								if(temp[0].equals(extxt)) {
									insertTemp[0] = expdd;
									insertTemp[1] = examt;
									insertTemp[2] = extxt;
									insertTemp[3] = temp[1];
									insertTemp[4] = temp[2];
									
									ocim.excelup_insertBankData(tuser, insertTemp);
								}
							}
							message = "����� �Ϸ�Ǿ����ϴ�.";
						}
					}
					
					workbook.close();
					
				} else {
					message = "excel Ȯ���ڰ� �ƴմϴ�";		
				}
			} else {
				message = "���� ������ �б� ����";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return message;
		
	}
	
	//2021.03.22 cell ������ Ÿ�� �� ����
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
			//��¥ ������ �� ���
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

	//2022.01.25 cvs �����ں���ȸ(P) ��Ż
	public String get_0102_cvs_total(String tuser, String syear, String smon, String depcd) {
		String rtnstr = "";
		try {
			rtnstr = ocim.get_json_0102total_cvs(tuser, syear, smon, depcd);
		} catch (Exception e) {
		}
		return rtnstr;
	}

	//2022.01.25 cvs �����ں���ȸ(P) ��ȸ
	public String get_0102_cvs_item(String tuser, String syear, String smon, String depcd) {
		String rtnstr = "";
		try {
			rtnstr = ocim.get_json_0102item_cvs(tuser, syear, smon, depcd);
		} catch (Exception e) {
		}
		return rtnstr;
	}
	
	//2022.01.25 cvs �����ں���ȸ(P) excel download
	@SuppressWarnings("unchecked")
	public String get_excel_0102_cvs(String tuser, String syear, String smon, String depcd) {
		JSONObject exceljson = new JSONObject();
		//Encoder encoder = Base64.getEncoder();
		try {
			exceljson.put("RST", "S000"); 
			/*
			exceljson.put("TOTALARRAY", encoder.encodeToString(ocim.get_json_0102total_cvs(tuser, syear, smon, samt, eamt, depcd).getBytes("UTF-8")));
			exceljson.put("ITEMARRAY", encoder.encodeToString(ocim.get_json_0102item_cvs(tuser, syear, smon, samt, eamt, depcd).getBytes("UTF-8")));
			*/
			exceljson.put("TOTALARRAY", ocim.get_json_0102total_cvs(tuser, syear, smon, depcd));
			exceljson.put("ITEMARRAY", ocim.get_json_0102item_cvs(tuser, syear, smon, depcd));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return exceljson.toJSONString();
	}
	
	//2022.01.25 cvs û�����ں� ��Ż
	public String get_0104_cvs_total(String tuser, String stime, String etime, String samt, String eamt, String appno
			, String tradeidx, String auth01, String auth02, String auth03, String mid, String tid, String acqcd, String tid2) {
		String rtnstr = "";
		try {
			rtnstr = ocim.get_json_0104total_cvs(tuser, stime, etime, samt, eamt, appno, tradeidx, auth01, auth02, auth03, mid, tid, acqcd, tid2);
		} catch (Exception e) {
		}
		return rtnstr;
	}
	
	//2022.02.03 cvs û�����ں� ������
	public String get_0104_cvs_item(String tuser, String stime, String etime, String samt, String eamt, String appno
			, String tradeidx, String auth01, String auth02, String auth03, String mid, String tid, String acqcd, String tid2) {
		String rtnstr = "";

		try {
			rtnstr = ocim.get_json_0104item_cvs(tuser, stime, etime, samt, eamt, appno, tradeidx, auth01, auth02, auth03, mid, tid, acqcd, tid2);
		} catch (Exception e) {
		}
		return rtnstr;
	}
	
	//2022.02.03 cvs û�����ں� ����¡
	/*
	public String get_0104_cvs_cnt(String tuser, String stime, String etime, String samt, String eamt, String appno
			, String tradeidx, String auth01, String auth02, String auth03, String mid, String tid, String acqcd, String tid2) {
		String rtnstr = "";

		try {
			rtnstr = ocim.get_json_0104cnt_cvs(tuser, stime, etime, samt, eamt, appno, tradeidx, auth01, auth02, auth03, mid, tid, acqcd, tid2);
		} catch (Exception e) {
		}
		return rtnstr;
	}
	*/
	//2022.02.03 cvs û�����ں� ����¡
	public JSONObject get_0104_cvs_cnt(String tuser, String stime, String etime, String samt, String eamt, String appno
			, String tradeidx, String auth01, String auth02, String auth03, String mid, String tid, String acqcd, String tid2, String npage, String pmode) {
		StringBuffer xmlObj = new StringBuffer();
		JSONObject pageobj = new JSONObject();
		String icnt = null;
		
		try {
			icnt = ocim.get_json_0104cnt_cvs(tuser, stime, etime, samt, eamt, appno, tradeidx, auth01, auth02, auth03, mid, tid, acqcd, tid2);
	
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

	//2022.02.03 cvs û�����ں�  excel download
	@SuppressWarnings("unchecked")
	public String get_excel_0104_cvs(String tuser, String stime, String etime, String samt, String eamt, String appno
			, String tradeidx, String auth01, String auth02, String auth03, String mid, String tid, String acqcd, String tid2) {
		JSONObject exceljson = new JSONObject();
		//Encoder encoder = Base64.getEncoder();
		try {
			//2022.02.04 pos_field
			//arraylist to string[] -> implode -> base64 encode
			ArrayList<String> fieldList = ocim.get_column_field(tuser, "van", "txt");
			String[] temp = new String[fieldList.size()];
			temp = fieldList.toArray(temp);
			@SuppressWarnings("static-access")
			String pos_field = utilm.implode(",", temp);
			
			exceljson.put("RST", "S000");
			exceljson.put("FIELDS_TXT", pos_field);
			/*
			exceljson.put("TOTALARRAY", encoder.encodeToString(ocim.get_json_0104total_cvs(tuser, stime, etime, samt, eamt, appno, tradeidx, auth01, auth02, auth03, mid, tid, acqcd, tid2).getBytes("UTF-8")));
			exceljson.put("ITEMARRAY", encoder.encodeToString(ocim.get_json_0104item_cvs(tuser, stime, etime, samt, eamt, appno, tradeidx, auth01, auth02, auth03, mid, tid, acqcd, tid2).getBytes("UTF-8")));
			*/
			exceljson.put("TOTALARRAY", ocim.get_json_0104total_cvs(tuser, stime, etime, samt, eamt, appno, tradeidx, auth01, auth02, auth03, mid, tid, acqcd, tid2));
			exceljson.put("ITEMARRAY", ocim.get_json_0104item_cvs(tuser, stime, etime, samt, eamt, appno, tradeidx, auth01, auth02, auth03, mid, tid, acqcd, tid2));
		} catch (Exception e) {
			
		}
		return exceljson.toJSONString();
	}
	
	//2022.02.04 cvs û���������ں� ��Ż
	public String get_0106_cvs_total(String tuser, String stime, String etime, String samt, String eamt, String appno
			, String tradeidx, String auth01, String auth02, String auth03, String mid, String tid, String acqcd, String tid2) {
		String rtnstr = "";
		try {
			rtnstr = ocim.get_json_0106total_cvs(tuser, stime, etime, samt, eamt, appno, tradeidx, auth01, auth02, auth03, mid, tid, acqcd, tid2);
		} catch (Exception e) {
		}
		return rtnstr;
	}
	
	//2022.02.04 cvs û���������ں� ������
	public String get_0106_cvs_item(String tuser, String stime, String etime, String samt, String eamt, String appno
			, String tradeidx, String auth01, String auth02, String auth03, String mid, String tid, String acqcd, String tid2) {
		String rtnstr = "";

		try {
			rtnstr = ocim.get_json_0106item_cvs(tuser, stime, etime, samt, eamt, appno, tradeidx, auth01, auth02, auth03, mid, tid, acqcd, tid2);
		} catch (Exception e) {
		}
		return rtnstr;
	}
	
	//2022.02.04 cvs û���������ں�  excel download
	@SuppressWarnings("unchecked")
	public String get_excel_0106_cvs(String tuser, String stime, String etime, String samt, String eamt, String appno
			, String tradeidx, String auth01, String auth02, String auth03, String mid, String tid, String acqcd, String tid2) {
		JSONObject exceljson = new JSONObject();
		//Encoder encoder = Base64.getEncoder();
		try {
			//2022.02.04 pos_field
			//arraylist to string[] -> implode -> base64 encode
			ArrayList<String> fieldList = ocim.get_column_field(tuser, "van", "txt");
			String[] temp = new String[fieldList.size()];
			temp = fieldList.toArray(temp);
			@SuppressWarnings("static-access")
			String pos_field = utilm.implode(",", temp);
			
			exceljson.put("RST", "S000");
			exceljson.put("FIELDS_TXT", pos_field);
			/*
			exceljson.put("TOTALARRAY", encoder.encodeToString(ocim.get_json_0106total_cvs(tuser, stime, etime, samt, eamt, appno, tradeidx, auth01, auth02, auth03, mid, tid, acqcd, tid2).getBytes("UTF-8")));
			exceljson.put("ITEMARRAY", encoder.encodeToString(ocim.get_json_0106item_cvs(tuser, stime, etime, samt, eamt, appno, tradeidx, auth01, auth02, auth03, mid, tid, acqcd, tid2).getBytes("UTF-8")));
			*/
			exceljson.put("TOTALARRAY", ocim.get_json_0106total_cvs(tuser, stime, etime, samt, eamt, appno, tradeidx, auth01, auth02, auth03, mid, tid, acqcd, tid2));
			exceljson.put("ITEMARRAY", ocim.get_json_0106item_cvs(tuser, stime, etime, samt, eamt, appno, tradeidx, auth01, auth02, auth03, mid, tid, acqcd, tid2));
		} catch (Exception e) {
			
		}
		return exceljson.toJSONString();
	}
		
	//2022.02.03 cvs û�����ں� ����¡
	public JSONObject get_0106_cvs_cnt(String tuser, String stime, String etime, String samt, String eamt, String appno
			, String tradeidx, String auth01, String auth02, String auth03, String mid, String tid, String acqcd, String tid2, String npage, String pmode) {
		StringBuffer xmlObj = new StringBuffer();
		JSONObject pageobj = new JSONObject();
		String icnt = null;
		
		try {
			icnt = ocim.get_json_0106cnt_cvs(tuser, stime, etime, samt, eamt, appno, tradeidx, auth01, auth02, auth03, mid, tid, acqcd, tid2);
	
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
	
	public String get_0107_cvs_total(String tuser, String stime, String etime, String sadd_date,
			String eadd_date, String sadd_recp, String eadd_recp, String appno, String pid, String pcd,
			String depcd, String auth01, String auth02, String auth03, String card01, String card02, String card03,
			String card04, String card05) {
		String rtnstr = "";
		try {
			rtnstr = ocim.get_json_0107total_cvs(tuser, stime, etime, sadd_date, eadd_date, sadd_recp, eadd_recp, appno, pid, pcd, depcd, auth01, auth02, auth03, card01, card02, card03, card04, card05);
		} catch (Exception e) {
		}
		return rtnstr;
	}
	
	public String get_0107_cvs_item(String tuser, String stime, String etime, String sadd_date,
			String eadd_date, String sadd_recp, String eadd_recp, String appno, String pid, String pcd,
			String depcd, String auth01, String auth02, String auth03, String card01, String card02, String card03,
			String card04, String card05, String paging) {
		String rtnstr = "";
		try {
			rtnstr = ocim.get_json_0107item_cvs(tuser, stime, etime, sadd_date, eadd_date, sadd_recp, eadd_recp, appno, pid, pcd, depcd, auth01, auth02, auth03, card01, card02, card03, card04, card05, paging);
		} catch (Exception e) {
		}
		return rtnstr;
	}
	
	//2022.02.03 cvs û�����ں� ����¡
	public JSONObject get_0107_cvs_cnt(String tuser, String stime, String etime, String sadd_date,
			String eadd_date, String sadd_recp, String eadd_recp, String appno, String pid, String pcd,
			String depcd, String auth01, String auth02, String auth03, String card01, String card02, String card03,
			String card04, String card05, String npage, String pmode) {
		StringBuffer xmlObj = new StringBuffer();
		JSONObject pageobj = new JSONObject();
		String icnt = null;
		
		try {
			icnt = ocim.get_json_0107cnt_cvs(tuser, stime, etime, sadd_date, eadd_date, sadd_recp, eadd_recp, appno, pid, pcd, depcd, auth01, auth02, auth03, card01, card02, card03, card04, card05);
	
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
	
	//2022.02.04 cvs û���������ں�  excel download
	@SuppressWarnings("unchecked")
	public String get_excel_0107_cvs(String tuser, String stime, String etime, String sadd_date,
			String eadd_date, String sadd_recp, String eadd_recp, String appno, String pid, String pcd,
			String depcd, String auth01, String auth02, String auth03, String card01, String card02, String card03,
			String card04, String card05, String paging) {
		JSONObject exceljson = new JSONObject();

		try {
			exceljson.put("RST", "S000");
			exceljson.put("TOTALARRAY", ocim.get_json_0107total_cvs(tuser, stime, etime, sadd_date, eadd_date, sadd_recp, eadd_recp, appno, pid, pcd, depcd, auth01, auth02, auth03, card01, card02, card03, card04, card05));
			exceljson.put("ITEMARRAY", ocim.get_json_0107item_cvs(tuser, stime, etime, sadd_date, eadd_date, sadd_recp, eadd_recp, appno, pid, pcd, depcd, auth01, auth02, auth03, card01, card02, card03, card04, card05, paging));
		} catch (Exception e) {
			
		}
		return exceljson.toJSONString();
	}
	
	public String get_0107_total(String tuser, String stime, String etime, String samt, String eamt, String appno, String cardtp, String auth01, String auth02, String auth03) {
		String rtnstr = "";
		try {
			rtnstr = ocim.get_json_0107total(tuser, stime, etime, samt, eamt, appno, cardtp, auth01, auth02, auth03);
		} catch (Exception e) {
		}
		return rtnstr;
	}
		
	/*
	public String get_0215_total(String tuser, String date, String endddate) {
		String rtnstr = "";
		try {				
			rtnstr = ocim.get_json_0215total(tuser, date, endddate);
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return  rtnstr;
	}
	
	
	@SuppressWarnings("unchecked")
	public String get_excel_00215(String tuser, String date, String enddate) {
		JSONObject excelObj = new JSONObject();
		Encoder encoder = Base64.getEncoder();
		try {
			excelObj.put("RST", "S000");
			excelObj.put("TOTALARRAY", encoder.encodeToString(ocim.get_json_0215total(tuser, date, enddate).getBytes("UTF-8")));
		} catch (Exception e) {
		}
		
		return excelObj.toJSONString();
	}

	*/
	//2022.02.09 �ݼ�������ȸ
	public String get_0301_ban_total(String tuser, String stime, String etime, String mid, String acqcd, String depcd) {
		String rtnstr = "";
		try {
			rtnstr = ocim.get_json_0301total_ban(tuser, stime, etime, acqcd, depcd, mid);
		} catch (Exception e) {}
		return rtnstr;
	}
	
	//2022.02.09 �ݼ�������ȸ
	public String get_0301_ban_item(String tuser, String stime, String etime, String mid, String acqcd, String depcd) {
		String rtnstr = "";
		try {
			rtnstr = ocim.get_json_0301item_ban(tuser, stime, etime, acqcd, depcd, mid);
		} catch (Exception e) {}
		return rtnstr;
	}
	
	//2022.02.09 �ݼ�������ȸ
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
	
	
	//2022.02.25 ���Ժ���
	public int get_deposit_checkup(String tuser, String seqno, String dpflag) {
		int result = 0;
		
		try {
			result = ocim.get_deposit_checkup(tuser, seqno, dpflag);
		} catch (Exception e) {
			
		}
		return result;
	}
	
	//2022.02.28 ����û��
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


}