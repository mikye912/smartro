package com.gaon.ifou.v3;

import java.util.ArrayList;

import org.json.simple.JSONObject;

public class pos_gowas {
	
	public static trans_ora_manager ocim 		= new trans_ora_manager();
	public static trans_util_manager utilm 		= new trans_util_manager();
	
	public String delimiter = ":";
	//2022.01.25 cvs 월일자별조회(P) 토탈
		public String get_0102_cvs_total(String tuser, String syear, String smon, String depcd) {
			String rtnstr = "";
			try {
				rtnstr = ocim.get_json_0102total_cvs(tuser, syear, smon, depcd);
			} catch (Exception e) {
			}
			return rtnstr;
		}

		//2022.01.25 cvs 월일자별조회(P) 조회
		public String get_0102_cvs_item(String tuser, String syear, String smon, String depcd) {
			String rtnstr = "";
			try {
				rtnstr = ocim.get_json_0102item_cvs(tuser, syear, smon, depcd);
			} catch (Exception e) {
			}
			return rtnstr;
		}
		
		//2022.01.25 cvs 월일자별조회(P) excel download
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
		
		//2022.01.25 cvs 청구일자별 토탈
		public String get_0104_cvs_total(String tuser, String stime, String etime, String samt, String eamt, String appno
				, String tradeidx, String auth01, String auth02, String auth03, String mid, String tid, String acqcd, String tid2) {
			String rtnstr = "";
			try {
				rtnstr = ocim.get_json_0104total_cvs(tuser, stime, etime, samt, eamt, appno, tradeidx, auth01, auth02, auth03, mid, tid, acqcd, tid2);
			} catch (Exception e) {
			}
			return rtnstr;
		}
		
		//2022.02.03 cvs 청구일자별 아이템
		public String get_0104_cvs_item(String tuser, String stime, String etime, String samt, String eamt, String appno
				, String tradeidx, String auth01, String auth02, String auth03, String mid, String tid, String acqcd, String tid2) {
			String rtnstr = "";

			try {
				rtnstr = ocim.get_json_0104item_cvs(tuser, stime, etime, samt, eamt, appno, tradeidx, auth01, auth02, auth03, mid, tid, acqcd, tid2);
			} catch (Exception e) {
			}
			return rtnstr;
		}
		
		//2022.02.03 cvs 청구일자별 페이징
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
		//2022.02.03 cvs 청구일자별 페이징
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

		//2022.02.03 cvs 청구일자별  excel download
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
		
		//2022.02.04 cvs 청구승인일자별 토탈
		public String get_0106_cvs_total(String tuser, String stime, String etime, String samt, String eamt, String appno
				, String tradeidx, String auth01, String auth02, String auth03, String mid, String tid, String acqcd, String tid2) {
			String rtnstr = "";
			try {
				rtnstr = ocim.get_json_0106total_cvs(tuser, stime, etime, samt, eamt, appno, tradeidx, auth01, auth02, auth03, mid, tid, acqcd, tid2);
			} catch (Exception e) {
			}
			return rtnstr;
		}
		
		//2022.02.04 cvs 청구승인일자별 아이템
		public String get_0106_cvs_item(String tuser, String stime, String etime, String samt, String eamt, String appno
				, String tradeidx, String auth01, String auth02, String auth03, String mid, String tid, String acqcd, String tid2) {
			String rtnstr = "";

			try {
				rtnstr = ocim.get_json_0106item_cvs(tuser, stime, etime, samt, eamt, appno, tradeidx, auth01, auth02, auth03, mid, tid, acqcd, tid2);
			} catch (Exception e) {
			}
			return rtnstr;
		}
		
		//2022.02.04 cvs 청구승인일자별  excel download
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
			
		//2022.02.03 cvs 청구일자별 페이징
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
		
		//2022.02.03 cvs 청구일자별 페이징
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
		
		//2022.02.04 cvs 청구승인일자별  excel download
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
}
