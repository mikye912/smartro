package com.gaon.ifou.v3;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class trans_ora_manager {
	/****************************************************************************************
	 * 오라클 CONNECT 함수
	 * Function Name		: SetConnection 
	 * Function Use			: Oracle Dirver Load & Oracle Pool Set
	 * Last Update Date		: 2013.05.03
	 ***************************************************************************************/
	private trans_seed_manager encm = new trans_seed_manager();
	private trans_util_manager utilm = new trans_util_manager();
	private Connection con  = null;
	private PreparedStatement stmt = null;
	private ResultSet rs = null;
	private StringBuffer strbuf = null;
	private ResultSetMetaData rsmd;

	private String getServerIp() {
		
		InetAddress local = null;
		try {
			local = InetAddress.getLocalHost();
		}
		catch ( UnknownHostException e ) {
			e.printStackTrace();
		}
			
		if( local == null ) {
			return "";
		}
		else {
			String ip = local.getHostAddress();
			return ip;
		}
			
	}
	
	//DB연결 정보
	public Connection getOraConnect(){
		Connection con = null;
		try {
			
			String jdbc_driver = "com.ibm.db2.jcc.DB2Driver";
			String strUrl = "";
			if(getServerIp() == "10.0.3.169") {
				strUrl  = "jdbc:db2://10.0.3.170:53000/IFDB";
				Class.forName(jdbc_driver);
				con = DriverManager.getConnection(strUrl, "ifou", "If@scm02");
			}else {
				strUrl  = "jdbc:db2://175.207.12.32:20022/DB2_TEST";
				Class.forName(jdbc_driver);
				con = DriverManager.getConnection(strUrl, "db201", "db201!");
			}
			
			/*
			String jdbc_driver = "oracle.jdbc.OracleDriver";
			String strUrl = "";

			strUrl  = "jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = dev.ifou.co.kr)(PORT = 1521))(LOAD_BALANCE = NO)(CONNECT_DATA =(SERVER = DEDICATED)(SERVICE_NAME = ORCL)))";

			Class.forName(jdbc_driver);
			con = DriverManager.getConnection(strUrl, "IFOU", "1");
			*/
		} catch (Exception e) {
			
		}
		return con;
	}

	public void setOraClose(Connection con, PreparedStatement stmt, ResultSet rs){
		try{
			if (rs != null){
				rs.close();
				rs = null;
			}
		}catch (Exception e){e.printStackTrace();}

		try{
			if (stmt != null){
				stmt.close();
				stmt = null;
			}
		}catch (Exception e){e.printStackTrace();}
		
		try {
			if(con!=null) {con.close();}
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public void rollBack(Connection con) {
		if(con!=null) 
		try{
		    con.rollback();
		}catch(SQLException sqle) {}
	}
	
	public String get_sql_select(String query) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		JSONArray sqlAry = new JSONArray();
		JSONObject sqlobj = new JSONObject();
		
		try {
			con = getOraConnect();

			stmt = con.prepareStatement(query);
			rs = stmt.executeQuery();

			rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();	
			
			JSONObject tempObj = new JSONObject();
			JSONArray headerAry = new JSONArray();
			JSONArray tempAry = new JSONArray();
			
			for(int i=1;i<=cols;i++) {
				headerAry.add(rsmd.getColumnName(i));
			}
			
			int seq = 0;
			while(rs.next()) {
				for(int j=1;j<=cols;j++) {
					tempAry.add(utilm.setDefault(rs.getString(j)));
				}
				seq++;
			}
			
			tempObj.put("header", headerAry);
			tempObj.put("data", tempAry);
			tempObj.put("seqno", seq);
			sqlAry.add(tempObj);
			
			sqlobj.put("rows", sqlAry);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}

		return sqlobj.toJSONString();
	}
	
	public String get_sql_proc(String qry) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int result = 0;
		JSONObject sqlobj = new JSONObject();
		try {
			
			con = getOraConnect();
			stmt=con.prepareStatement(qry);
			result = stmt.executeUpdate();
			int row = stmt.getUpdateCount();
			
			sqlobj.put("rs", result);
			sqlobj.put("count", row);
		} catch (SQLException e) {
			sqlobj.put("error", e.toString());
		}finally {
			setOraClose(con,stmt,rs);
		}

		return sqlobj.toJSONString();
	}

	public int get_edi_head_count() {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int icnt = 0;

		try {
			strbuf = new StringBuffer();
			strbuf.append("select count(1) from glob_mng_icvan");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());
			rs = stmt.executeQuery();

			if (rs.next()){
				icnt = rs.getInt(1);
			}else{
				icnt = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}

		return icnt;
	}

	public String get_glob_mng_icvan() {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		JSONArray arr = new JSONArray();

		try {

			strbuf = new StringBuffer();

			strbuf.append("SELECT ");
			strbuf.append("ROWNUM AS RNUM, SEQNO, BIZNO, TID, MID, VANGB, MDATE,");
			strbuf.append("SVCGB, TRANIDX, APPGB, ENTRYMD, APPDD,");
			strbuf.append("APPTM, APPNO, CARDNO, HALBU, CURRENCY,");
			strbuf.append("AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX, ISS_CD,");
			strbuf.append("ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG,");
			strbuf.append("CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB, SIGNCHK,");
			strbuf.append("DDCGB, EXT_FIELD, OAPPNO, OAPPDD, OAPPTM,");
			strbuf.append("OAPP_AMT, ADD_GB, ADD_CID, ADD_CD, ADD_RECP,");
			strbuf.append("ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, SERVID,");
			strbuf.append("DPFLAG, DEPOREQDD, REQDEPTH, TRAN_STAT, DEPOSEQ, CTR_RST,");
			strbuf.append("CTR_DT, ADD_DEPT, MEDI_GOODS ");

			strbuf.append("FROM ");
			strbuf.append("GLOB_MNG_ICVAN ");
			strbuf.append("WHERE ");
			strbuf.append("APPDD='20180103' ");
			strbuf.append("ORDER BY ");
			strbuf.append("RNUM ASC");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());
			rs = stmt.executeQuery();

			while(rs.next()) {
				JSONObject obj1 = new JSONObject();
				obj1.put("RNUM", rs.getString("RNUM"));
				obj1.put("SEQNO", rs.getString("SEQNO"));
				obj1.put("BIZNO", rs.getString("BIZNO"));
				obj1.put("TID", rs.getString("TID"));
				obj1.put("MID", rs.getString("MID"));
				obj1.put("VANGB", rs.getString("VANGB"));
				obj1.put("MDATE", rs.getString("MDATE"));
				obj1.put("SVCGB", rs.getString("SVCGB"));
				obj1.put("TRANIDX", rs.getString("TRANIDX"));
				obj1.put("ENTRYMD", rs.getString("ENTRYMD"));
				obj1.put("APPDD", rs.getString("APPDD"));
				obj1.put("APPTM", rs.getString("APPTM"));
				obj1.put("APPNO", rs.getString("APPNO"));
				obj1.put("CARDNO", rs.getString("CARDNO"));
				obj1.put("HALBU", rs.getString("HALBU"));
				obj1.put("CURRENCY", rs.getString("CURRENCY"));
				obj1.put("AMOUNT", rs.getString("AMOUNT"));
				obj1.put("ISS_CD", rs.getString("ISS_CD"));
				obj1.put("ISS_NM", rs.getString("ISS_NM"));
				obj1.put("ACQ_CD", rs.getString("ACQ_CD"));
				arr.add(obj1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}
		return arr.toJSONString();
	}

	public String get_glob_mng_icvan_json(String sdate) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		JSONArray arr = new JSONArray();

		JSONObject obj3 = new JSONObject();
		try {

			strbuf = new StringBuffer();

			strbuf.append("SELECT ");

			strbuf.append("ROWNUM AS RNUM, SEQNO, BIZNO, TID, MID, VANGB, MDATE,");
			strbuf.append("SVCGB, TRANIDX, APPGB, ENTRYMD, APPDD,");
			strbuf.append("APPTM, APPNO, CARDNO, HALBU, CURRENCY,");
			strbuf.append("AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX, ISS_CD,");
			strbuf.append("ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG,");
			strbuf.append("CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB, SIGNCHK,");
			strbuf.append("DDCGB, EXT_FIELD, OAPPNO, OAPPDD, OAPPTM,");
			strbuf.append("OAPP_AMT, ADD_GB, ADD_CID, ADD_CD, ADD_RECP,");
			strbuf.append("ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, SERVID,");
			strbuf.append("DPFLAG, DEPOREQDD, REQDEPTH, TRAN_STAT, DEPOSEQ, CTR_RST,");
			strbuf.append("CTR_DT, ADD_DEPT, MEDI_GOODS ");

			strbuf.append("FROM ");
			strbuf.append("GLOB_MNG_ICVAN ");
			strbuf.append("WHERE ");
			strbuf.append("APPDD='"+sdate+"' ");
			strbuf.append("AND SVCGB='CC' ");
			strbuf.append("ORDER BY ");
			strbuf.append("RNUM ASC");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());
			rs = stmt.executeQuery();

			int icnt = 1;

			while(rs.next()) {
				JSONObject obj1 = new JSONObject();
				JSONArray arr2 = new JSONArray();

				arr2.add(Integer.toString(icnt));
				arr2.add(rs.getString("APPGB"));
				arr2.add(rs.getString("APPDD"));
				arr2.add(rs.getString("APPTM"));
				arr2.add(rs.getString("AMOUNT"));
				arr2.add(trans_seed_manager.seed_dec_card(rs.getString("CARDNO").trim()));
				arr2.add(rs.getString("APPNO"));
				arr2.add("승인");
				arr2.add(rs.getString("ISS_NM"));
				arr2.add(rs.getString("ACQ_NM"));
				arr2.add(rs.getString("ADD_RECP"));
				arr2.add(rs.getString("ADD_CASHER"));
				arr2.add(rs.getString("OAPPDD"));
				arr2.add(rs.getString("MID"));
				arr2.add(rs.getString("TID"));
				arr2.add(rs.getString("OAPPNO"));
				arr2.add("ORGCD");
				arr2.add("DEPCD");
				arr2.add(rs.getString("SEQNO"));
				arr2.add(rs.getString("VANGB"));

				obj1.put("id", Integer.toString(icnt));
				obj1.put("data", arr2);

				arr.add(obj1);
				icnt++;
			}

			obj3.put("rows", arr);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}
		return obj3.toJSONString();
	}

	public String get_glob_mng_icvan_xml() {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		StringBuffer sbuf = new StringBuffer();

		try {

			strbuf = new StringBuffer();
			strbuf.append("SELECT ");
			strbuf.append("ROWNUM AS RNUM, SEQNO, BIZNO, TID, MID, VANGB, MDATE,");
			strbuf.append("SVCGB, TRANIDX, APPGB, ENTRYMD, APPDD,");
			strbuf.append("APPTM, APPNO, CARDNO, HALBU, CURRENCY,");
			strbuf.append("AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX, ISS_CD,");
			strbuf.append("ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG,");
			strbuf.append("CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB, SIGNCHK,");
			strbuf.append("DDCGB, EXT_FIELD, OAPPNO, OAPPDD, OAPPTM,");
			strbuf.append("OAPP_AMT, ADD_GB, ADD_CID, ADD_CD, ADD_RECP,");
			strbuf.append("ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, SERVID,");
			strbuf.append("DPFLAG, DEPOREQDD, REQDEPTH, TRAN_STAT, DEPOSEQ, CTR_RST,");
			strbuf.append("CTR_DT, ADD_DEPT, MEDI_GOODS ");
			strbuf.append("FROM ");
			strbuf.append("GLOB_MNG_ICVAN ");
			strbuf.append("WHERE ");
			strbuf.append("APPDD='20180103' ");
			strbuf.append("ORDER BY ");
			strbuf.append("RNUM ASC");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());
			rs = stmt.executeQuery();


			sbuf.append("<rows id=\"0\">'");
			int icnt = 1;
			while(rs.next()) {
				sbuf.append("<row id='"+ Integer.toString(icnt) +"'>");
				sbuf.append("<cell>" + rs.getString("RNUM") + "</cell>");
				sbuf.append("<cell>" + rs.getString("SEQNO") + "</cell>");
				sbuf.append("<cell>" + rs.getString("BIZNO") + "</cell>");
				sbuf.append("<cell>" + rs.getString("TID") + "</cell>");
				sbuf.append("<cell>" + rs.getString("MID") + "</cell>");
				sbuf.append("<cell>" + rs.getString("VANGB") + "</cell>");
				sbuf.append("<cell>" + rs.getString("MDATE") + "</cell>");
				sbuf.append("<cell>" + rs.getString("SVCGB") + "</cell>");
				sbuf.append("<cell>" + rs.getString("TRANIDX") + "</cell>");
				sbuf.append("<cell>" + rs.getString("ENTRYMD") + "</cell>");
				sbuf.append("<cell>" + rs.getString("APPDD") + "</cell>");
				sbuf.append("<cell>" + rs.getString("APPTM") + "</cell>");
				sbuf.append("<cell>" + rs.getString("APPNO") + "</cell>");
				sbuf.append("<cell>" + rs.getString("CARDNO") + "</cell>");
				sbuf.append("<cell>" + rs.getString("HALBU") + "</cell>");
				sbuf.append("<cell>" + rs.getString("CURRENCY") + "</cell>");
				sbuf.append("<cell>" + rs.getString("AMOUNT") + "</cell>");
				sbuf.append("<cell>" + rs.getString("ISS_CD") + "</cell>");
				sbuf.append("<cell>" + rs.getString("ISS_NM") + "</cell>");
				sbuf.append("<cell>" + rs.getString("ACQ_CD") + "</cell>");
				sbuf.append("</row>");

				icnt++;
			}
			sbuf.append("</rows>");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}
		return sbuf.toString();
	}

	public int get_user_cnt(String uid) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int rtncnt = 0;
		try {
			strbuf = new StringBuffer();

			strbuf.append("SELECT ");
			strbuf.append("COUNT(1) MCNT ");
			strbuf.append("FROM ");
			strbuf.append("TB_BAS_USER where ");
			strbuf.append("user_id=?");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());
			stmt.setString(1, uid); //유저 ID
			
			rs = stmt.executeQuery();
			rs.next();

			rtncnt = rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			setOraClose(con,stmt,rs);
		}
		return rtncnt;
	}
	
	public int get_user_check(String uid) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int usechk = 0;

		try {
			strbuf = new StringBuffer();

			strbuf.append("SELECT USE_CHK FROM TB_BAS_USER WHERE USER_ID=? ");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());
			stmt.setString(1, uid); //유저 ID

			rs = stmt.executeQuery();
			rs.next();

			usechk = rs.getInt(1);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}
		return usechk;
	}
	
	public int get_update_use_chk(String uid) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int pwcheck = 0;

		try {
			strbuf = new StringBuffer();

			strbuf.append("BEGIN UPDATE TB_BAS_USER SET USE_CHK= (SELECT NVL(USE_CHK , 0) FROM TB_BAS_USER WHERE USER_ID=? )+1 WHERE USER_ID =? ; COMMIT; END; ");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());
			stmt.setString(1, uid); //유저 ID
			stmt.setString(2, uid); //유저 ID

			rs = stmt.executeQuery();
			rs.next();

			pwcheck = rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}
		return pwcheck;
	}
	
	public int get_use_chk_reset(String uid) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int usechk_reset = 0;

		try {
			strbuf = new StringBuffer();

			strbuf.append("UPDATE TB_BAS_USER SET USE_CHK='0' where USER_ID=?; ");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());
			stmt.setString(1, uid); //유저 ID

			usechk_reset = stmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}
		return usechk_reset;
	}
	
	public int get_user_reset(String uid, String upw) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int result = 0;
		strbuf = new StringBuffer();
		
		try {
			strbuf.append("UPDATE TB_BAS_USER SET USE_CHK='';");
			
			con = getOraConnect();
			
			stmt = con.prepareStatement(strbuf.toString());
			
			result = stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}
		return result;
	}
	
	public int get_insert_user_log(String uid) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int result = 0;
		StringBuffer qrybuf = new StringBuffer();
		
		/* seqno생성 */
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		String ndate = format.format(new Date());
		String noformat = String.format("%08d", 1);
		String seqno = ndate+noformat;
		
		// 현재 시간
		LocalTime now = LocalTime.now();
		// 포맷 정의하기
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");
		// 포맷 적용하기
		String formatedNow = now.format(formatter);

		try {
			
			qrybuf.append(" INSERT INTO TB_SYS_LOG (LOG_DD, LOG_TM, LOG_SEQ, LOG_TYPE, LOG_PAGE, LOG_CONT, LOG_USER) VALUES (?, ?, ?, 'L', '','USER LOGIN : "+uid+" "+ndate+" "+formatedNow+" 로그인.', ?);");

			con = getOraConnect();
			stmt = con.prepareStatement(qrybuf.toString());
			stmt.setString(1, ndate);
			stmt.setString(2, formatedNow);
			stmt.setString(3, seqno);
			stmt.setString(4, uid);
			
			result = stmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}
		return result;
	}
	

	/**
	 * 유저 정보 Select Query
	 * @param uid : 유저아이디
	 * @return USER_PW, DEP_CD, ORG_CD, USER_LV
	 */
	public String[] get_user_info(String uid) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String[] rtnstr = new String[5];
		try {
			strbuf = new StringBuffer();
			strbuf.append("SELECT ");
			strbuf.append("USER_PW, DEP_CD, ORG_CD, USER_LV, AUTH_SEQ ");
			strbuf.append("FROM ");
			strbuf.append("TB_BAS_USER ");
			strbuf.append("WHERE ");
			strbuf.append("user_id=?");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());
			stmt.setString(1, uid); //유저 ID

			rs = stmt.executeQuery();

			if(rs.next()) {
				rtnstr[0] = rs.getString(1);
				rtnstr[1] = rs.getString(2);
				rtnstr[2] = rs.getString(3);
				rtnstr[3] = rs.getString(4);
				rtnstr[4] = rs.getString(5);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}
		return rtnstr;
	}

	/**
	 * 사용자의 가맹점 정보 및 Table 정보 Select
	 * @param ocd : 법인코드
	 * @return PTAB, VTAB, DTAB
	 */
	public String[] get_org_info(String ocd) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String[] rtnstr = new String[4];
		try {
			strbuf = new StringBuffer();
			strbuf.append("SELECT ");
			strbuf.append("PTAB, VTAB, DTAB ");
			strbuf.append("FROM ");
			strbuf.append("TB_BAS_ORG ");
			strbuf.append("WHERE ");
			strbuf.append("ORG_CD=?");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());
			stmt.setString(1, ocd); //유저 ID

			rs = stmt.executeQuery();

			if(rs.next()) {
				rtnstr[0] = rs.getString(1);
				rtnstr[1] = rs.getString(2);
				rtnstr[2] = rs.getString(3);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}
		return rtnstr;
	}

	public String[][] get_general_menu_top(String orgcd, String authseq) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String[][] rtnstr = new String[40][9];
		try {
			strbuf = new StringBuffer();
			strbuf.append("SELECT ");
			strbuf.append("	A.PROGRAM_SEQ MENU_SEQ, ");
			strbuf.append("	B.PROGRAM_NAME MENU_NAME, ");
			strbuf.append("	B.DEPTH MENU_DEPTH, ");
			strbuf.append("	B.PARENT_SEQ PARENT_SEQ, ");
			strbuf.append("	A.ENABLE_READ AUTH_R, ");
			strbuf.append("	A.ENABLE_CREATE AUTH_C, ");
			strbuf.append("	A.ENABLE_UPDATE AUTH_U, ");
			strbuf.append("	A.ENABLE_DELETE AUTH_D, ");
			strbuf.append("	B.SRC_LOCATION MURL ");
			strbuf.append("FROM  ");
			strbuf.append("    TB_SYS_MENU A ");
			strbuf.append("LEFT OUTER JOIN ");
			strbuf.append("    (SELECT PROGRAM_SEQ, PROGRAM_NAME, PARENT_SEQ, DEPTH, SRC_LOCATION, SORT FROM TB_SYS_PROGRAM) B ");
			strbuf.append("ON (A.PROGRAM_SEQ=B.PROGRAM_SEQ) ");
			strbuf.append("WHERE B.DEPTH='0' AND A.AUTH_SEQ=? AND ORGCD=?");
			strbuf.append("ORDER BY B.SORT ASC");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());
			stmt.setString(1, authseq); //Menu SEQ
			stmt.setString(2, orgcd); //Orgcd

			rs = stmt.executeQuery();

			int cnt = 0;
			while(rs.next()) {
				rtnstr[cnt][0] = rs.getString(1);
				rtnstr[cnt][1] = rs.getString(2);
				rtnstr[cnt][2] = rs.getString(3);
				rtnstr[cnt][3] = rs.getString(4);
				rtnstr[cnt][4] = rs.getString(5);
				rtnstr[cnt][5] = rs.getString(6);
				rtnstr[cnt][6] = rs.getString(7);
				rtnstr[cnt][7] = rs.getString(8);
				rtnstr[cnt][8] = rs.getString(9);

				cnt++;
			}

		}catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}
		return rtnstr;
	}

	public String[][] get_general_menu_sub(String orgcd, String authseq, String mseq) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String[][] rtnstr = new String[40][9];
		try {

			strbuf = new StringBuffer();
			strbuf.append("SELECT ");
			strbuf.append("	A.PROGRAM_SEQ MENU_SEQ, ");
			strbuf.append("	B.PROGRAM_NAME MENU_NAME, ");
			strbuf.append("	B.DEPTH MENU_DEPTH, ");
			strbuf.append("	B.PARENT_SEQ PARENT_SEQ, ");
			strbuf.append("	A.ENABLE_READ AUTH_R, ");
			strbuf.append("	A.ENABLE_CREATE AUTH_C, ");
			strbuf.append("	A.ENABLE_UPDATE AUTH_U, ");
			strbuf.append("	A.ENABLE_DELETE AUTH_D, ");
			strbuf.append("	B.SRC_LOCATION MURL ");
			strbuf.append("FROM  ");
			strbuf.append("    TB_SYS_MENU A ");
			strbuf.append("LEFT OUTER JOIN ");
			strbuf.append("    (SELECT PROGRAM_SEQ, PROGRAM_NAME, PARENT_SEQ, DEPTH, SRC_LOCATION, SORT FROM TB_SYS_PROGRAM) B ");
			strbuf.append("ON (A.PROGRAM_SEQ=B.PROGRAM_SEQ) ");
			strbuf.append("WHERE B.DEPTH='1' AND A.AUTH_SEQ=? AND ORGCD=? AND B.PARENT_SEQ=?");
			strbuf.append("ORDER BY B.SORT ASC");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());
			stmt.setString(1, authseq); //Menu SEQ
			stmt.setString(2, orgcd); //Orgcd
			stmt.setString(3, mseq); //부모 시퀀스

			rs = stmt.executeQuery();

			int cnt = 0;
			while(rs.next()) {
				rtnstr[cnt][0] = rs.getString(1);
				rtnstr[cnt][1] = rs.getString(2);
				rtnstr[cnt][2] = rs.getString(3);
				rtnstr[cnt][3] = rs.getString(4);
				rtnstr[cnt][4] = rs.getString(5);
				rtnstr[cnt][5] = rs.getString(6);
				rtnstr[cnt][6] = rs.getString(7);
				rtnstr[cnt][7] = rs.getString(8);
				rtnstr[cnt][8] = rs.getString(9);

				cnt++;
			}

		}catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}
		return rtnstr;
	}

	public String[][] get_general_depart(String orgcd, String depcd) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String[][] rtnstr = new String[40][2];
		try {

			strbuf = new StringBuffer();
			strbuf.append("SELECT ");
			strbuf.append("	DEP_CD, DEP_NM ");
			strbuf.append("FROM  ");
			strbuf.append("    TB_BAS_DEPART ");
			strbuf.append("WHERE ");
			strbuf.append("ORG_CD=? ");

			if(depcd != null && depcd != "") {
				strbuf.append("AND DEP_CD=? ");
			}

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());
			stmt.setString(1, orgcd); //ORGCD >>>  필수

			if(depcd != null && depcd != "") {
				stmt.setString(2, depcd); //DEPCD >>> 거의 필수 이나 없을 수도 있음.
			}

			rs = stmt.executeQuery();

			int cnt = 0;
			while(rs.next()) {
				rtnstr[cnt][0] = rs.getString(1);
				rtnstr[cnt][1] = rs.getString(2);
				cnt++;
			}

		}catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}
		return rtnstr;
	}

	public String[][] get_general_tid(String orgcd, String depcd) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String[][] rtnstr = new String[40][9];
		try {
			strbuf = new StringBuffer();
			strbuf.append("SELECT ");
			strbuf.append(" TERM_ID, TERM_NM ");
			strbuf.append("FROM ");
			strbuf.append(" TB_BAS_TIDMST ");
			strbuf.append("WHERE ");
			strbuf.append("ORG_CD=? ");
			strbuf.append("AND TERM_ID IN ( ");
			strbuf.append(" SELECT");
			strbuf.append("  TID FROM TB_BAS_TIDMAP");
			strbuf.append(" WHERE");
			strbuf.append(" ORG_CD=?");

			if(depcd != null && depcd != "") {
				strbuf.append("AND DEP_CD=? ");
			}

			strbuf.append(") ");
			strbuf.append("ORDER BY TERM_SORT ASC");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());
			stmt.setString(1, orgcd); //ORGCD >>>  필수
			stmt.setString(2, orgcd); //ORGCD >>>  필수

			if(depcd != null && depcd != "") {
				stmt.setString(3, depcd); //DEPCD 
			}

			rs = stmt.executeQuery();

			int cnt = 0;
			while(rs.next()) {
				rtnstr[cnt][0] = rs.getString(1);
				rtnstr[cnt][1] = rs.getString(2);
				cnt++;
			}

		}catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}
		return rtnstr;
	}
	
	//카드사선택
	public String[][] get_general_acq() {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String[][] rtnstr = new String[40][9];
		
		try {
			strbuf = new StringBuffer();
			strbuf.append("SELECT PUR_CD, PUR_NM FROM TB_BAS_PURINFO WHERE PUR_USE='Y' ORDER BY PUR_SORT ASC");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());
			rs = stmt.executeQuery();

			int cnt = 0;
			while(rs.next()) {
				rtnstr[cnt][0] = rs.getString(1);
				rtnstr[cnt][1] = rs.getString(2);
				cnt++;
			}

		}catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}
		return rtnstr;

	}

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

	public String make_merge_str(String merge) {
		String rtnstr = "";
		try {
			if(merge.equals("AND")) {
				rtnstr = "=";
			}else if(merge.equals("OR")) {

			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return rtnstr;
	}

	public String make_query_str(String merge, String key, String val, String qtype) {
		String rtnstr = "";
		StringBuffer rtnbuf = new StringBuffer();
		try {

			if(null != val && "" != val.trim()) {
				if(qtype.equals("EQ") && merge.equals("AND")) {
					rtnbuf.append("and ");
					rtnbuf.append(key);
					rtnbuf.append(" = '");
					rtnbuf.append(val);
					rtnbuf.append("' ");
				}else if(qtype.equals("EQ") && merge.equals("OR")) {
					rtnbuf.append("or ");
					rtnbuf.append(key);
					rtnbuf.append(" = '");
					rtnbuf.append(val);
					rtnbuf.append("' ");
				}else if(qtype.equals("NEQ") && merge.equals("AND")) {
					rtnbuf.append("and ");
					rtnbuf.append(key);
					rtnbuf.append(" <> '");
					rtnbuf.append(val);
					rtnbuf.append("' ");
				}else if(qtype.equals("NEQ") && merge.equals("OR")) {
					rtnbuf.append("or ");
					rtnbuf.append(key);
					rtnbuf.append(" <> '");
					rtnbuf.append(val);
					rtnbuf.append("' ");
				}else if(qtype.equals("BEQ") && merge.equals("AND")) {
					rtnbuf.append("and ");
					rtnbuf.append(key);
					rtnbuf.append(">='");
					rtnbuf.append(val);
					rtnbuf.append("' ");
				}else if(qtype.equals("BEQ") && merge.equals("OR")) {
					rtnbuf.append("or ");
					rtnbuf.append(key);
					rtnbuf.append(">='");
					rtnbuf.append(val);
					rtnbuf.append("' ");
				}else if(qtype.equals("SEQ") && merge.equals("AND")) {
					rtnbuf.append("and ");
					rtnbuf.append(key);
					rtnbuf.append("<='");
					rtnbuf.append(val);
					rtnbuf.append("' ");
				}else if(qtype.equals("SEQ") && merge.equals("OR")) {
					rtnbuf.append("or ");
					rtnbuf.append(key);
					rtnbuf.append("<='");
					rtnbuf.append(val);
					rtnbuf.append("' ");
				}
			}

			rtnstr = rtnbuf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			rtnbuf = null;
		}
		return rtnstr;
	}

	//2021.02.24 강원대병원v3 웹취소 : 전문생성을 위한 데이터 read
	@SuppressWarnings({ "unchecked", "static-access" })
	public String transcancel_getData(String seqno, String tuser, String appno) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		JSONObject sqlobj = new JSONObject();

		StringBuffer qrybuf = new StringBuffer();

		try {
			//tuser split
			String[] userexp = tuser.split(":");

			qrybuf.append("SELECT SEQNO, APPGB, VANGB, TID, MID, CARDNO, ACQ_CD, CHECK_CARD, SIGNCHK, AMOUNT, ");
			qrybuf.append("HALBU, APPNO, substr(APPDD, 3) APPDD, APPTM, OAPPDD, EXT_FIELD, ADD_CID, ADD_GB, TRANIDX, ");
			qrybuf.append("ADD_CD, ADD_DATE, ADD_CASHER FROM ");
			qrybuf.append(userexp[5]);
			qrybuf.append(" WHERE SEQNO = ? AND APPNO = ? ");

			con = getOraConnect();
			stmt = con.prepareStatement(qrybuf.toString());

			stmt.setString(1, seqno);
			stmt.setString(2, appno);

			//DEBUG
			System.out.println(" ============= [transcancel_getData] ============= ");
			System.out.println(qrybuf.toString());
			System.out.println("seqno : " + seqno);
			System.out.println("appno : " + appno);
	
			rs = stmt.executeQuery();

			while(rs.next()) {
				JSONObject tempObj = new JSONObject();
				JSONArray tempAry = new JSONArray();

				tempObj.put("SEQNO", utilm.setDefault(rs.getString("SEQNO")));
				tempObj.put("APPGB", utilm.setDefault(rs.getString("APPGB")));
				tempObj.put("VANGB", utilm.setDefault(rs.getString("VANGB")));
				tempObj.put("TID", utilm.setDefault(rs.getString("TID")));
				tempObj.put("MID", utilm.setDefault(rs.getString("MID")));

				String cardNo = utilm.setDefault(rs.getString("CARDNO"));
				if(cardNo != null && !cardNo.equals("")) {
					cardNo = encm.seed_dec_card(cardNo);
				}
				tempObj.put("CARDNO", cardNo);

				tempObj.put("ACQ_CD", utilm.setDefault(rs.getString("ACQ_CD")));
				tempObj.put("CHECK_CARD", utilm.setDefault(rs.getString("CHECK_CARD")));
				tempObj.put("AMOUNT", utilm.setDefault(rs.getString("AMOUNT")));
				tempObj.put("HALBU", utilm.setDefault(rs.getString("HALBU")));
				tempObj.put("APPNO", utilm.setDefault(rs.getString("APPNO")));
				tempObj.put("APPTM", utilm.setDefault(rs.getString("APPTM")));
				tempObj.put("APPDD", utilm.setDefault(rs.getString("APPDD")));
				tempObj.put("OAPPDD", utilm.setDefault(rs.getString("OAPPDD")));
				tempObj.put("EXT_FIELD", utilm.setDefault(rs.getString("EXT_FIELD")));
				tempObj.put("ADD_CID", utilm.setDefault(rs.getString("ADD_CID")));
				tempObj.put("ADD_GB", utilm.setDefault(rs.getString("ADD_GB")));
				tempObj.put("ADD_CD", utilm.setDefault(rs.getString("ADD_CD")));
				tempObj.put("ADD_DATE", utilm.setDefault(rs.getString("ADD_DATE")));
				tempObj.put("ADD_CASHER", utilm.setDefault(rs.getString("ADD_CASHER")));
				tempObj.put("TRANIDX", utilm.setDefault(rs.getString("TRANIDX")));

				tempAry.add(tempObj);
				sqlobj.put("ITEMS", tempAry);
			}

		}catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}

		return sqlobj.toJSONString();
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
//				smtsidx++;
//				acqcd_idx = smtsidx;
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
			pqrybuf.append("    T1.DEP_SEQ||'|'||T1.DEP_CD SEQNO, ");
			pqrybuf.append("    T1.DEP_CD, CARD_NO, EXP_DD, MID, REQ_DD, TID, RTN_CD, T1.APP_DD, ");
			pqrybuf.append("    REG_DD, HALBU, SALE_AMT, RSC_CD, RS_MSG, T1.APP_NO, T2.DEP_CD DPCD, T2.STO_CD, ");
			pqrybuf.append("    FEE, DEP_NM, STO_NM, PUR_NM, TERM_NM, T7.EXT_FIELD, PUR_SMART, PUR_OCD, OAPPDD, COM_NO ");
			pqrybuf.append("FROM ");
			pqrybuf.append("    " + userexp[6] + " T1 ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT ORG_CD, DEP_CD, STO_CD, MER_NO, PUR_CD FROM TB_BAS_MERINFO WHERE ORG_CD=? ");
			setting.add(userexp[1]);
			pqrybuf.append(")T2 ON(T1.MID=T2.MER_NO) ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD=? ");
			setting.add(userexp[1]);
			pqrybuf.append(")T6 ON(T1.TID=T6.TERM_ID) ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD=? ");
			setting.add(userexp[1]);
			pqrybuf.append(")T3 ON(T2.DEP_CD=T3.DEP_CD) ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT STO_NM, STO_CD, DEP_CD, ORG_CD FROM TB_BAS_STORE ");
			pqrybuf.append(")T4 ON(T2.STO_CD=T4.STO_CD AND T2.DEP_CD=T4.DEP_CD AND T2.ORG_CD=T4.ORG_CD) ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT PUR_CD, PUR_NM, PUR_SMART, PUR_OCD, PUR_SORT FROM TB_BAS_PURINFO ");
			pqrybuf.append(")T6 ON(T2.PUR_CD=T6.PUR_CD) ");
			pqrybuf.append("LEFT OUTER JOIN( ");
			pqrybuf.append("    SELECT APPDD, OAPPDD, TRANIDX, EXT_FIELD FROM " + userexp[5]);
			pqrybuf.append(")T7 ON(T1.APP_DD=T7.APPDD AND T1.TRANIDX=T7.TRANIDX) ");
			pqrybuf.append("WHERE ");
			pqrybuf.append("    MID IN (SELECT MID FROM TB_BAS_MIDMAP WHERE ORG_CD=?");
			setting.add(userexp[1]);
			pqrybuf.append(") ");

			//int stime_idx = 0;
			if(null!=stime&&""!=stime) {
				//smtsidx++;
				//stime_idx = smtsidx;
				pqrybuf.append("	AND EXP_DD>=? ");
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

			if(null!=auth01&&""!=auth01) {
				pqrybuf.append("            AND RTN_CD IN ('61', '64')");
			}

			if(null!=auth02&&""!=auth02) {
				pqrybuf.append("            AND RTN_CD = '61'");
			}

			if(null!=auth03&&""!=auth03) {
				pqrybuf.append("            aND RTN_CD = '64'");
			}

			pqrybuf.append("ORDER BY PUR_SORT ASC, DEP_CD ASC");

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

				String cardGb = utilm.setDefault(rs2.getString("COM_NO"));
				if(cardGb.equals("Y")) {
					cardGb = "체크카드";
				} else if (cardGb.equals("N")) {
					cardGb = "신용카드";
				}

				tempArr.add(icnt);
				tempArr.add(utilm.setDefault(rs2.getString("DEP_NM")));
				tempArr.add(utilm.setDefault(rs2.getString("TERM_NM")));
				tempArr.add(utilm.setDefault(rs2.getString("TID")));
				tempArr.add(utilm.setDefault(rs2.getString("PUR_NM")));
				tempArr.add(utilm.setDefault(rs2.getString("MID")));
				tempArr.add(depotype);
				tempArr.add(authtxt);
				tempArr.add(newCardNo);
				tempArr.add(utilm.setDefault(rs2.getString("HALBU")));
				tempArr.add(utilm.str_to_dateformat_deposit(rs2.getString("APP_DD")));
				tempArr.add(utilm.setDefault(rs2.getString("APP_NO")));

				tempArr.add(utilm.checkNumberData(rs2.getString("SALE_AMT")));
				tempArr.add(utilm.checkNumberData(rs2.getDouble("FEE")));
				tempArr.add(expamt);

				tempArr.add(cardGb);
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
	
	public String get_json_bigdata() {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		JSONObject jrtnobj = new JSONObject();

		try {
			strbuf = new StringBuffer();
			strbuf.append("SELECT ");
			strbuf.append("ROWNUM AS RNUM, SEQNO, BIZNO, TID, MID, VANGB, MDATE,");
			strbuf.append("SVCGB, TRANIDX, APPGB, ENTRYMD, APPDD,");
			strbuf.append("APPTM, APPNO, CARDNO, HALBU, CURRENCY,");
			strbuf.append("AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX, ISS_CD,");
			strbuf.append("ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG,");
			strbuf.append("CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB, SIGNCHK,");
			strbuf.append("DDCGB, EXT_FIELD, OAPPNO, OAPPDD, OAPPTM,");
			strbuf.append("OAPP_AMT, ADD_GB, ADD_CID, ADD_CD, ADD_RECP,");
			strbuf.append("ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, SERVID,");
			strbuf.append("DPFLAG, DEPOREQDD, REQDEPTH, TRAN_STAT, DEPOSEQ, CTR_RST,");
			strbuf.append("CTR_DT, ADD_DEPT, MEDI_GOODS ");
			strbuf.append("FROM ");
			strbuf.append("GLOB_MNG_ICVAN ");
			strbuf.append("WHERE ");
			//strbuf.append("APPDD in ('20190603', '20190604', '20190602') ");
			strbuf.append("APPDD in ('20190603') ");
			strbuf.append("ORDER BY ");
			strbuf.append("RNUM ASC");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());
			rs = stmt.executeQuery();

			int icnt = 0;
			JSONArray jsonarr = new JSONArray();

			while(rs.next()) {

				JSONObject jsonobj = new JSONObject();
				jsonobj.put("RNUM", rs.getString("RNUM"));
				jsonobj.put("SEQNO", rs.getString("SEQNO"));
				jsonobj.put("BIZNO", rs.getString("BIZNO"));
				jsonobj.put("TID", rs.getString("TID"));
				jsonobj.put("MID", rs.getString("MID"));
				jsonobj.put("VANGB", rs.getString("VANGB"));
				jsonobj.put("MDATE", rs.getString("MDATE"));
				jsonobj.put("SVCGB", rs.getString("SVCGB"));
				jsonobj.put("TRANIDX", rs.getString("TRANIDX"));
				jsonobj.put("ENTRYMD", rs.getString("ENTRYMD"));
				jsonobj.put("APPDD", rs.getString("APPDD"));
				jsonobj.put("APPTM", rs.getString("APPTM"));
				jsonobj.put("APPNO", rs.getString("APPNO"));
				jsonobj.put("CARDNO", rs.getString("CARDNO"));
				jsonobj.put("HALBU", rs.getString("HALBU"));
				jsonobj.put("CURRENCY", rs.getString("CURRENCY"));
				jsonobj.put("AMOUNT", rs.getString("AMOUNT"));
				jsonobj.put("ISS_CD", rs.getString("ISS_CD"));
				jsonobj.put("ISS_NM", rs.getString("ISS_NM"));
				jsonobj.put("ACQ_CD", rs.getString("ACQ_CD"));

				jsonarr.add(jsonobj);

				icnt++;
			}
			//System.out.println(icnt);

			jrtnobj.put("TOTAL", icnt);
			jrtnobj.put("ITEMS", jsonarr);

		}catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}
		return jrtnobj.toJSONString();
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
		
		StringBuffer qrybuf = new StringBuffer();
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
			qrybuf.append("		,SUM(ABC)-SUM(CBC) BC ");
			qrybuf.append("		,SUM(ANH)-SUM(CNH) NH ");
			qrybuf.append("		,SUM(AKB)-SUM(CKB) KB ");
			qrybuf.append("		,SUM(ASS)-SUM(CSS) SS ");
			qrybuf.append("		,SUM(AHN)-SUM(CHN) HN ");
			qrybuf.append("		,SUM(ALO)-SUM(CLO) LO ");
			qrybuf.append("		,SUM(AHD)-SUM(CHD) HD ");
			qrybuf.append("		,SUM(ASI)-SUM(CSI) SI ");
			qrybuf.append("	FROM( ");
			qrybuf.append("		SELECT ");
			qrybuf.append("			TID ");
			qrybuf.append("			,CASE WHEN APPGB='A' THEN COUNT(1) ELSE 0 END ACNT ");
			qrybuf.append("			,CASE WHEN APPGB='C' THEN COUNT(1) ELSE 0 END CCNT ");
			qrybuf.append("			,CASE WHEN APPGB='A' THEN SUM(AMOUNT) ELSE 0 END AAMT ");
			qrybuf.append("			,CASE WHEN APPGB='C' THEN SUM(AMOUNT) ELSE 0 END CAMT ");
			qrybuf.append("			,CASE WHEN APPGB='A' AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0400') THEN SUM(AMOUNT) ELSE 0 END ABC ");
			qrybuf.append("			,CASE WHEN APPGB='A' AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0171') THEN SUM(AMOUNT) ELSE 0 END ANH ");
			qrybuf.append("			,CASE WHEN APPGB='A' AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0170') THEN SUM(AMOUNT) ELSE 0 END AKB ");
			qrybuf.append("			,CASE WHEN APPGB='A' AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1300') THEN SUM(AMOUNT) ELSE 0 END ASS ");
			qrybuf.append("			,CASE WHEN APPGB='A' AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0505') THEN SUM(AMOUNT) ELSE 0 END AHN ");
			qrybuf.append("			,CASE WHEN APPGB='A' AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1100') THEN SUM(AMOUNT) ELSE 0 END ALO ");
			qrybuf.append("			,CASE WHEN APPGB='A' AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1200') THEN SUM(AMOUNT) ELSE 0 END AHD ");
			qrybuf.append("			,CASE WHEN APPGB='A' AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0300') THEN SUM(AMOUNT) ELSE 0 END ASI ");
			qrybuf.append("			,CASE WHEN APPGB='C' AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0400') THEN SUM(AMOUNT) ELSE 0 END CBC ");
			qrybuf.append("			,CASE WHEN APPGB='C' AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0171') THEN SUM(AMOUNT) ELSE 0 END CNH ");
			qrybuf.append("			,CASE WHEN APPGB='C' AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0170') THEN SUM(AMOUNT) ELSE 0 END CKB ");
			qrybuf.append("			,CASE WHEN APPGB='C' AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1300') THEN SUM(AMOUNT) ELSE 0 END CSS ");
			qrybuf.append("			,CASE WHEN APPGB='C' AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0505') THEN SUM(AMOUNT) ELSE 0 END CHN ");
			qrybuf.append("			,CASE WHEN APPGB='C' AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1100') THEN SUM(AMOUNT) ELSE 0 END CLO ");
			qrybuf.append("			,CASE WHEN APPGB='C' AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='1200') THEN SUM(AMOUNT) ELSE 0 END CHD ");
			qrybuf.append("			,CASE WHEN APPGB='C' AND MID IN (SELECT MER_NO FROM TB_BAS_MERINFO WHERE PUR_CD='0300') THEN SUM(AMOUNT) ELSE 0 END CSI ");
			qrybuf.append("		FROM ( ");
			qrybuf.append("			SELECT ");
			qrybuf.append("				SEQNO, DEP_NM, TERM_NM, TID, MID, PUR_NM, TSTAT, ACQ_CD,  ");
			qrybuf.append("				TSTAT_TXT, APPDD, APPTM, TSTAT CANDATE, OAPPDD, APPNO, APPGB, ");
			qrybuf.append("				APPGB_TXT, CARDNO, AMOUNT, HALBU, CARDTP_TXT, SIGNCHK_TXT, ");
			qrybuf.append("				REQ_DD,	AUTHCD, REG_DD, RTN_CD, RTN_TXT,  ");
			qrybuf.append("				EXP_DD,	EXT_FIELD, TRANIDX, AUTHMSG ");
			qrybuf.append("			FROM( ");
			qrybuf.append("				SELECT ");
			qrybuf.append("					SEQNO, DEP_NM, TERM_NM, TID, MID, PUR_NM, TSTAT, ");
			qrybuf.append("					CASE ");
			qrybuf.append("						WHEN APPGB='A' AND TSTAT IS NULL THEN '정상거래' ");
			qrybuf.append("						WHEN APPGB='A' AND TSTAT=APPDD THEN '당일취소' ");
			qrybuf.append("						WHEN APPGB='C' AND APPDD=OAPPDD THEN '당일취소' ");
			qrybuf.append("						WHEN APPGB='C' AND APPDD<>OAPPDD THEN '전일취소' ");
			qrybuf.append("						WHEN APPGB='A' AND APPDD<>TSTAT AND TSTAT IS NOT NULL THEN '전일취소' ");
			qrybuf.append("					END TSTAT_TXT, ");
			qrybuf.append("					APPDD, APPTM, TSTAT CANDATE, OAPPDD, APPNO, APPGB, ACQ_CD, ");
			qrybuf.append("					CASE  ");
			qrybuf.append("						WHEN APPGB='A' THEN '신용승인' ");
			qrybuf.append("						WHEN APPGB='C' THEN '신용취소' ");
			qrybuf.append("					END APPGB_TXT, ");
			qrybuf.append("					CARDNO, AMOUNT, HALBU, ");
			qrybuf.append("					CASE WHEN CHECK_CARD='Y' THEN '체크카드' ELSE '신용카드' END CARDTP_TXT, ");
			qrybuf.append("					CASE WHEN SIGNCHK='1' THEN '전자서명' ELSE '무서명' END SIGNCHK_TXT, ");
			qrybuf.append("					REQ_DD,	AUTHCD,	REG_DD,	RTN_CD, ");
			qrybuf.append("					CASE ");
			qrybuf.append("						WHEN RTN_CD IS NULL THEN '결과없음' ");
			qrybuf.append("						WHEN RTN_CD IN('60', '67') THEN '정상매입' ");
			qrybuf.append("						WHEN RTN_CD IN('61', '64') THEN '매입반송' ");
			qrybuf.append("					END RTN_TXT, ");
			qrybuf.append("					EXP_DD, EXT_FIELD, TRANIDX, AUTHMSG ");
			qrybuf.append("				FROM( ");
			qrybuf.append("					SELECT ");
			qrybuf.append("						SEQNO, BIZNO, TID, MID, VANGB, MDATE, SVCGB, T1.TRANIDX, T1.APPGB, ENTRYMD, ");
			qrybuf.append("						T1.APPDD, APPTM, T1.APPNO, T1.CARDNO, HALBU, CURRENCY, T1.AMOUNT, AMT_UNIT, AMT_TIP, AMT_TAX, ");
			qrybuf.append("						ISS_CD, ISS_NM, ACQ_CD, ACQ_NM, AUTHCD, AUTHMSG, CARD_CODE, CHECK_CARD, OVSEA_CARD, TLINEGB, ");
			qrybuf.append("						SIGNCHK, DDCGB, EXT_FIELD, OAPPNO, OAPPDD, OAPPTM, OAPP_AMT, ADD_GB, ADD_CID, ADD_CD, ");
			qrybuf.append("						ADD_RECP, ADD_CNT, ADD_CASHER, ADD_DATE, SECTION_NO, PUR_NM, DEP_NM, EXP_DD, REQ_DD, REG_DD, RSC_CD, RTN_CD, TERM_NM, ");
			qrybuf.append("						CASE ");
			qrybuf.append("							WHEN APPGB='C' THEN '' ");
			qrybuf.append("							WHEN APPGB='A' THEN (SELECT C1.APPDD FROM "+userexp[5]+" C1 WHERE C1.APPGB='C' AND T1.APPDD=C1.OAPPDD AND T1.APPNO=C1.APPNO AND T1.AMOUNT=C1.AMOUNT AND T1.CARDNO=C1.CARDNO) ");
			qrybuf.append("						END TSTAT ");
			qrybuf.append("					FROM ");
			qrybuf.append("						GLOB_MNG_ICVAN_CVS T1 ");
			qrybuf.append("					LEFT OUTER JOIN(SELECT EXP_DD, REQ_DD, REG_DD, APP_DD, TRANIDX, RSC_CD, RTN_CD FROM TB_MNG_DEPDATA)T2 ON(T1.APPDD=T2.APP_DD AND T1.TRANIDX=T2.TRANIDX) ");
			qrybuf.append("					LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD=?)T3 ON(T1.TID=T3.TERM_ID) ");
			setting.add(userexp[1]);
			qrybuf.append("					LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD=?)T4 ON(T3.DEP_CD=T4.DEP_CD) ");
			setting.add(userexp[1]);
			qrybuf.append("					LEFT OUTER JOIN( SELECT PUR_NM, PUR_OCD, PUR_KOCES FROM TB_BAS_PURINFO)T5 ON (T1.ACQ_CD=T5.PUR_OCD OR T1.ACQ_CD=T5.PUR_KOCES ) ");
			qrybuf.append("					LEFT OUTER JOIN( SELECT PUR_CD, MER_NO, ORG_CD  FROM TB_BAS_MERINFO)T6 ON (T1.MID = T6.MER_NO AND ORG_CD=?) ");
			setting.add(userexp[1]);
			if(userexp[2] != null && !userexp[2].equals("")) {
				qrybuf.append(" and dep_cd = ?");
				setting.add(userexp[2]);
			}
			qrybuf.append(					wherebuf.toString());
			qrybuf.append("					ORDER BY APPDD DESC, APPTM DESC ");
			qrybuf.append("				) ");
			qrybuf.append("			) ");
			if(exwherebuf != null) {
				qrybuf.append("WHERE ");
			}
			qrybuf.append(			exwherebuf.toString());
			qrybuf.append("		) ");
			qrybuf.append("		GROUP BY TID, APPGB, MID ");
			qrybuf.append("	) ");
			qrybuf.append("	GROUP BY TID ");
			qrybuf.append("	)T2 ");
			qrybuf.append("LEFT OUTER JOIN( SELECT DEP_CD, TERM_NM, TERM_ID FROM TB_BAS_TIDMST WHERE ORG_CD=?)T3 ON(T2.TID=T3.TERM_ID) ");
			setting.add(userexp[1]);
			qrybuf.append("LEFT OUTER JOIN( SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD=?)T4 ON(T3.DEP_CD=T4.DEP_CD) ");
			setting.add(userexp[1]);
			
			//디버깅용
			utilm.debug_sql(qrybuf, setting);

			con = getOraConnect();
			stmt = con.prepareStatement(qrybuf.toString());
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

	//2021.03.04 강원대병원 - 시스템관리 [원장관리] readData
	public String[] get_json_060501item(String tuser) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String[] data = new String[9];
		StringBuffer qrybuf = new StringBuffer();

		try {
			//tuser split
			String[] userexp = tuser.split(":");
			qrybuf.append("SELECT ORG_CD, ORG_NM, ORG_NO, ORG_CORP_NO, ORG_CEO_NM, ORG_ADDR, ");
			qrybuf.append("ORG_TEL1, ORG_TEL2, ORG_USER, TERM_TYPE, ORG_TAB, ORG_TYPE, ORG_SERVICE, ");
			qrybuf.append("ORG_EMAIL, ORG_MEMO FROM TB_BAS_ORG WHERE ORG_CD = ? ");

			con = getOraConnect();
			stmt = con.prepareStatement(qrybuf.toString());

			stmt.setString(1, userexp[1]);

			rs = stmt.executeQuery();

			while(rs.next()) {
				data[0] = utilm.setDefault(rs.getString("ORG_NM"));
				data[1] = utilm.setDefault(rs.getString("ORG_NO"));
				data[2] = utilm.setDefault(rs.getString("ORG_CORP_NO"));
				data[3] = utilm.setDefault(rs.getString("ORG_CEO_NM"));
				data[4] = utilm.setDefault(rs.getString("ORG_TYPE"));
				data[5] = utilm.setDefault(rs.getString("ORG_SERVICE"));
				data[6] = utilm.setDefault(rs.getString("ORG_TEL1"));
				data[7] = utilm.setDefault(rs.getString("ORG_ADDR"));
				data[8] = utilm.setDefault(rs.getString("ORG_MEMO"));
			}

		}catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}

		return data;
	}

	//2021.03.04 강원대병원 - 시스템관리 [원장관리] 사용자정보 readData
	//2021.03.09 사용자관리 통합?
	public String get_json_060501item_userList(String tuser) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		JSONObject sqlobj = new JSONObject();
		JSONArray sqlAry = new JSONArray();

		StringBuffer qrybuf = new StringBuffer();

		try {
			//tuser split
			String[] userexp = tuser.split(":");

			qrybuf.append("SELECT MEM_CD, ORG_CD, T1.DEP_CD, DEP_NM, AUTH_SEQ, USER_ID, USER_PW, ");
			qrybuf.append("USER_NM, USER_TEL1, USER_TEL2, USER_MEMO, USER_EMAIL, USER_FAX, USER_LV, "); 
			qrybuf.append("TO_CHAR(INS_DT,'YYYY-MM-DD HH24:MI:SS') INS_DT ");
			qrybuf.append("FROM TB_BAS_USER T1 ");
			qrybuf.append("LEFT OUTER JOIN(SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART)T2 ON(T1.DEP_CD=T2.DEP_CD) ");
			qrybuf.append("WHERE ORG_CD = ? ");
			qrybuf.append("ORDER BY USER_ID ASC, INS_DT ASC");

			con = getOraConnect();

			stmt = con.prepareStatement(qrybuf.toString());
			stmt.setString(1, userexp[1]);

			rs = stmt.executeQuery();

			int rcnt = 1;
			while(rs.next()) {
				JSONObject tempObj = new JSONObject();
				JSONArray tempAry = new JSONArray();

				String userLv = utilm.setDefault(rs.getString("USER_LV"));
				if(userLv.equals("S")) {
					userLv = "사용자";
				} else if(userLv.equals("M")) {
					userLv = "마스터";
				} else {
					userLv = "";
				}

				tempAry.add(utilm.setDefault(rs.getString("USER_ID")));
				tempAry.add(userLv);
				tempAry.add(utilm.setDefault(rs.getString("USER_NM")));
				tempAry.add(utilm.setDefault(rs.getString("DEP_NM")));
				tempAry.add(utilm.setDefault(rs.getString("USER_TEL1")));
				tempAry.add(utilm.setDefault(rs.getString("USER_TEL2")));
				tempAry.add(utilm.setDefault(rs.getString("USER_EMAIL")));

				String mem_cd = utilm.setDefault(rs.getString("MEM_CD"));
				String orgcd = utilm.setDefault(rs.getString("ORG_CD"));
				String depcd = utilm.setDefault(rs.getString("DEP_CD"));

				tempAry.add("<span onClick=\"mem_mod('" + orgcd + "', '" + depcd + "', '" + mem_cd + "')\">수정</span>");
				tempAry.add("<span onClick=\"mem_del('" + orgcd + "', '" + depcd + "', '" + mem_cd + "')\">삭제</span>");

				tempObj.put("id", rcnt);
				tempObj.put("data", tempAry);

				sqlAry.add(tempObj);

				rcnt++;
			}
			sqlobj.put("rows", sqlAry);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}

		return sqlobj.toJSONString();
	}

	//2021.03.04 강원대병원 - 시스템관리 [원장관리] data update
	public int get_json_060501item_update(String tuser, String comnm, String comno, String comexno, String comceo,
			String cometype, String comservice, String comtel, String comaddr, String orgmemo) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int result = 0;
		strbuf = new StringBuffer();

		try {
			//tuser split
			String[] userexp = tuser.split(":");

			strbuf.append("UPDATE TB_BAS_ORG SET ");
			strbuf.append("ORG_NM = ? , ORG_NO = ?, ORG_CORP_NO = ?, ORG_CEO_NM = ?, ORG_TYPE = ?, ");
			strbuf.append("ORG_SERVICE = ?, ORG_TEL1 = ?, ORG_ADDR = ?, ORG_MEMO = ? ");
			strbuf.append("WHERE ORG_CD = ? ");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());
			stmt.setString(1, comnm);
			stmt.setString(2, comno);
			stmt.setString(3, comexno);
			stmt.setString(4, comceo);
			stmt.setString(5, cometype);
			stmt.setString(6, comservice);
			stmt.setString(7, comtel);
			stmt.setString(8, comaddr);
			stmt.setString(9, orgmemo);
			stmt.setString(10, userexp[1]);

			result = stmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		} finally {
			setOraClose(con,stmt,rs);
		}

		return result;
	}

	//2021.03.04 강원대병원 - 시스템관리 [사업부관리] readData
	public String get_json_060502item(String tuser) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		JSONObject sqlobj = new JSONObject();
		JSONArray sqlAry = new JSONArray();

		StringBuffer qrybuf = new StringBuffer();

		try {
			//tuser split
			String[] userexp = tuser.split(":");

			qrybuf.append("SELECT ORG_CD, DEP_CD, DEP_NM, DEP_ADM_USER, DEP_ADDR, DEP_TEL1, DEP_EMAIL, ");
			qrybuf.append("DEP_SORT, DEP_TYPE, TO_CHAR(DEP_INDT,'YYYY-MM-DD HH24:MI:SS') DEP_INDT ");
			qrybuf.append("FROM TB_BAS_DEPART WHERE ORG_CD = ? ORDER BY DEP_INDT ASC");

			con = getOraConnect();
			stmt = con.prepareStatement(qrybuf.toString());
			stmt.setString(1, userexp[1]);

			rs = stmt.executeQuery();

			int rcnt = 1;
			while(rs.next()) {
				JSONObject tempObj = new JSONObject();
				JSONArray tempAry = new JSONArray();

				String orgcd = utilm.setDefault(rs.getString("ORG_CD"));
				String depcd = utilm.setDefault(rs.getString("DEP_CD"));

				tempAry.add(utilm.setDefault(rs.getString("DEP_NM")));
				tempAry.add(depcd);
				tempAry.add(utilm.setDefault(rs.getString("DEP_ADM_USER")));
				tempAry.add(utilm.setDefault(rs.getString("DEP_TEL1")));
				tempAry.add(utilm.setDefault(rs.getString("DEP_EMAIL")));
				tempAry.add(utilm.setDefault(rs.getString("DEP_TYPE")));
				tempAry.add("<span onClick=\"tid_mod('" + orgcd + "', '" + depcd + "')\">단말기관리</span>");
				tempAry.add("<span onClick=\"mid_mod('" + orgcd + "', '" + depcd + "')\">가맹점관리</span>");
				tempAry.add("<span onClick=\"depo_mod('" + orgcd + "', '" + depcd + "')\">수정</span>");
				tempAry.add("<span onClick=\"depo_del('" + orgcd + "', '" + depcd + "')\">삭제</span>");
				tempAry.add(utilm.setDefault(rs.getString("DEP_INDT")));

				tempObj.put("id", rcnt);
				tempObj.put("data", tempAry);

				sqlAry.add(tempObj);
				rcnt++;
			}

			sqlobj.put("rows", sqlAry);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}

		return sqlobj.toJSONString();
	}

	//2021.03.04 강원대병원 v3 - 시스템관리 [사업부관리] 단말기관리 list Load
	/*
		1.데이터 가져옴
		2.짝수, 홀수 열 마다 style 지정
		3.tid 필드값 있는 것 - 해당 원장코드, 사업부코드에 등록된 tid : selected 설정
		4.tr코드 return
	 */
	public String get_json_060502item_tid_list(String orgcd, String depcd) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		StringBuffer qrybuf = new StringBuffer();
		StringBuffer htmlbuf = new StringBuffer();

		try {

			qrybuf.append("SELECT T1.TERM_NM, T1.TERM_ID, T2.TID FROM TB_BAS_TIDMST T1 ");
			qrybuf.append("LEFT OUTER JOIN( ");
			qrybuf.append("SELECT * FROM TB_BAS_TIDMAP WHERE ORG_CD = ? AND DEP_CD = ? ");
			qrybuf.append(")T2 ON(T1.ORG_CD=T2.ORG_CD AND T1.TERM_ID=T2.TID) ");
			qrybuf.append("WHERE T1.ORG_CD = ? ORDER BY TERM_NM ");

			con = getOraConnect();
			stmt = con.prepareStatement(qrybuf.toString());

			stmt.setString(1, orgcd);
			stmt.setString(2, depcd);
			stmt.setString(3, orgcd);

			rs = stmt.executeQuery();

			int icnt = 0;
			String bcolor = "#f5f5f5";
			while(rs.next()) {

				if(icnt % 2 == 0) {
					bcolor = "#FFFFFF";
				}

				String check = "";
				String tid = utilm.setDefault(rs.getString("TID"));
				if(!tid.equals("")) {
					check = "checked";
				}

				String term_id = utilm.setDefault(rs.getString("TERM_ID"));
				String term_nm = utilm.setDefault(rs.getString("TERM_NM"));

				htmlbuf.append("<tr style=background-color:"+bcolor+";'>");
				htmlbuf.append("<td align='center'><input type='checkbox' name='tid' value='"+
						term_id+"' "+check+"></td>");
				htmlbuf.append("<td>"+term_nm+"</td>");
				htmlbuf.append("<td>"+term_id+"</td>");
				htmlbuf.append("</tr>");

			}

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}
		return htmlbuf.toString();

	}

	//2021.03.04 강원대병원 v3 - 시스템관리 [사업부관리] 단말기관리 list update
	/*
	 * 1. 일단 기존데이터 지움
	 * 2. tid 선택한 데이터들 update 처리
	 */
	public int get_json_060502item_tid_list_update(String orgcd, String depcd, String[] tid, String tuser) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		int result = 0;
		strbuf = new StringBuffer();

		try {
			//tuser split
			String[] userexp = tuser.split(":");

			strbuf.append("DELETE FROM TB_BAS_TIDMAP WHERE ORG_CD = ? AND DEP_CD = ?");

			con = getOraConnect();
			con.setAutoCommit(false);
			stmt = con.prepareStatement(strbuf.toString());

			stmt.setString(1, orgcd);
			stmt.setString(2, depcd);

			int delete = stmt.executeUpdate();

			//1 : 삭제완료
			if(delete >= 0) {
				if(tid != null) {
					for(int i = 0; i<tid.length; i++) {
						stmt = null;
						strbuf = new StringBuffer();

						strbuf.append("Begin ");
						strbuf.append("INSERT INTO TB_BAS_TIDMAP (ORG_CD, DEP_CD, TID, INSTIME, INSUSER) ");
						strbuf.append("VALUES (?, ?, ?, sysdate, ?); ");
						strbuf.append("END;");

						stmt = con.prepareStatement(strbuf.toString());

						stmt.setString(1, orgcd);
						stmt.setString(2, depcd);
						stmt.setString(3, tid[i]);
						stmt.setString(4, userexp[0]);

						stmt.executeUpdate();
					}
				}
				con.commit();
				result = 1;

			} else {
				return result;
			}
		} catch(Exception e){
			e.printStackTrace();
			rollBack(con);
			result = -1;
		} finally {
			setOraClose(con,stmt,rs);
		}

		return result;
	}

	//2021.03.04 강원대병원 v3 - 시스템관리 [사업부관리] 가맹점관리 list Load
	public String get_json_060502item_mid_list(String orgcd, String depcd) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		StringBuffer qrybuf = new StringBuffer();
		StringBuffer htmlbuf = new StringBuffer();

		try {
			qrybuf.append("SELECT PUR_NM, MER_NO, MID FROM TB_BAS_MERINFO T1 ");
			qrybuf.append("LEFT OUTER JOIN(");
			qrybuf.append("SELECT * FROM TB_BAS_MIDMAP WHERE ORG_CD= ?  AND DEP_CD= ? ");
			qrybuf.append(")T2 ON(T1.ORG_CD=T2.ORG_CD AND T1.MER_NO=T2.MID) ");
			qrybuf.append("LEFT OUTER JOIN( SELECT PUR_CD, PUR_NM, PUR_SORT FROM TB_BAS_PURINFO )T3 ON(T1.PUR_CD=T3.PUR_CD) ");
			qrybuf.append("WHERE T1.ORG_CD = ? ORDER BY PUR_SORT, MER_NO ASC");

			con = getOraConnect();
			stmt = con.prepareStatement(qrybuf.toString());

			stmt.setString(1, orgcd);
			stmt.setString(2, depcd);
			stmt.setString(3, orgcd);

			rs = stmt.executeQuery();

			int icnt = 0;
			String bcolor = "#f5f5f5";
			while(rs.next()) {

				if(icnt % 2 == 0) {
					bcolor = "#FFFFFF";
				}

				String check = "";
				String mid = utilm.setDefault(rs.getString("MID"));
				if(!mid.equals("")) {
					check = "checked";
				}

				String mer_no = utilm.setDefault(rs.getString("MER_NO"));
				String pur_nm = utilm.setDefault(rs.getString("PUR_NM"));

				htmlbuf.append("<tr style=background-color:"+bcolor+";'>");
				htmlbuf.append("<td align='center'><input type='checkbox' name='mid' value='"+
						mer_no+"' "+check+"></td>");
				htmlbuf.append("<td>"+pur_nm+"</td>");
				htmlbuf.append("<td>"+mer_no+"</td>");
				htmlbuf.append("</tr>");

			}

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}

		return htmlbuf.toString();
	}

	//2021.03.04 강원대병원 v3 - 시스템관리 [사업부관리] 가맹점관리 list update
	/*
	 * 1. 일단 기존데이터 지움
	 * 2. tid 선택한 데이터들 update 처리
	 */
	public int get_json_060502item_mid_list_update(String orgcd, String depcd, String[] mid, String tuser) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		int result = 0;
		//int lengArrMid = mid != null ? mid.length : 0;
		strbuf = new StringBuffer();
		

		try {
			//tuser split
			String[] userexp = tuser.split(":");

			strbuf.append("DELETE FROM TB_BAS_MIDMAP WHERE ORG_CD= ? AND DEP_CD= ?");

			con = getOraConnect();
			con.setAutoCommit(false);
			stmt = con.prepareStatement(strbuf.toString());

			stmt.setString(1, orgcd);
			stmt.setString(2, depcd);

			int delete = stmt.executeUpdate();
			
			//1 : 삭제완료
			if(delete >= 0) {
				if(mid != null) {
					for(int i = 0; i<mid.length; i++) {
						stmt = null;
						strbuf = new StringBuffer();
	
						strbuf.append("INSERT INTO TB_BAS_MIDMAP (ORG_CD, DEP_CD, MID, INSTIME, INSUSER) ");
						strbuf.append("VALUES (?, ?, ?, sysdate, ?); ");
	
						stmt = con.prepareStatement(strbuf.toString());
	
						stmt.setString(1, orgcd);
						stmt.setString(2, depcd);
						stmt.setString(3, mid[i]);
						stmt.setString(4, userexp[0]);
	
						stmt.executeUpdate();
					}
				}
				con.commit();
				result = 1;

			} else {
				return result;
			}
		}catch(Exception e){
			e.printStackTrace();
			rollBack(con);
			result = -1;
		} finally {
			setOraClose(con,stmt,rs);
		}

		return result;
	}

	//2021.03.04 강원대병원 v3 - 시스템관리 [사업부관리] 가맹점관리 사업부 불러오기
	public String[] get_json_060502item_deplist(String orgcd, String depcd) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String[] data = new String[5];

		StringBuffer qrybuf = new StringBuffer();

		try {
			qrybuf.append("SELECT * FROM TB_BAS_DEPART WHERE ORG_CD = ?  AND DEP_CD = ? ");
			con = getOraConnect();
			stmt = con.prepareStatement(qrybuf.toString());

			stmt.setString(1, orgcd);
			stmt.setString(2, depcd);

			rs = stmt.executeQuery();

			while(rs.next()) {
				data[0] = utilm.setDefault(rs.getString("DEP_NM"));
				data[1] = utilm.setDefault(rs.getString("DEP_ADM_USER"));
				data[2] = utilm.setDefault(rs.getString("DEP_TEL1"));
				data[3] = utilm.setDefault(rs.getString("DEP_EMAIL"));
				data[4] = utilm.setDefault(rs.getString("DEP_TYPE"));
			}

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}

		return data;
	}

	//2021.03.04 강원대병원 v3 - 시스템관리 [사업부관리] 가맹점관리 사업부 수정
	public int get_json_060502item_deposit_update(String dep_nm, String dep_adm_user, String dep_tel1, 
			String dep_email, String dep_type, String orgcd, String depcd) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int result = 0;
		strbuf = new StringBuffer();

		try {
			strbuf.append("UPDATE TB_BAS_DEPART SET DEP_NM = ?, DEP_ADM_USER = ?, DEP_TEL1 = ?, ");
			strbuf.append("DEP_EMAIL = ?, DEP_TYPE = ?, DEP_INDT = SYSDATE WHERE ORG_CD = ? AND DEP_CD = ?");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());

			stmt.setString(1, dep_nm);
			stmt.setString(2, dep_adm_user);
			stmt.setString(3, dep_tel1);
			stmt.setString(4, dep_email);
			stmt.setString(5, dep_type);
			stmt.setString(6, orgcd);
			stmt.setString(7, depcd);

			result = stmt.executeUpdate();

		} catch(Exception e){
			e.printStackTrace();
			result = -1;
		} finally {
			setOraClose(con,stmt,rs);
		}

		return result;
	}

	//2021.03.04 강원대병원 v3 - 시스템관리 [사업부관리] 가맹점관리 사업부 삭제
	public int get_json_060502item_deposit_delete(String orgcd, String depcd) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int result = 0;

		strbuf = new StringBuffer();

		try {
			con = getOraConnect();
			con.setAutoCommit(false);

			strbuf.append("BEGIN ");
			strbuf.append("DELETE FROM TB_BAS_DEPART WHERE ORG_CD= ?  AND DEP_CD = ?; ");
			strbuf.append("DELETE FROM TB_BAS_MIDMAP WHERE ORG_CD= ?  AND DEP_CD = ?; ");
			strbuf.append("DELETE FROM TB_BAS_TIDMAP WHERE ORG_CD= ?  AND DEP_CD = ?; ");
			strbuf.append("END;");
			stmt = con.prepareStatement(strbuf.toString());

			stmt.setString(1, orgcd);
			stmt.setString(2, depcd);
			stmt.setString(3, orgcd);
			stmt.setString(4, depcd);
			stmt.setString(5, orgcd);
			stmt.setString(6, depcd);

			result = stmt.executeUpdate();
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

	//2021.03.08 강원대병원 v3 - 시스템관리 [사업부관리] 가맹점관리 사업부 추가
	public int get_json_060502item_deposit_insert(String tuser, String dep_nm, String dep_adm_user, String dep_tel1, 
			String dep_email, String dep_type) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int result = 0;

		strbuf = new StringBuffer();


		try {
			//tuser split
			String[] userexp = tuser.split(":");

			//new dep_cd
			Date date = new Date();
			String dep_cd = "MD" + date.getTime();
			dep_cd = dep_cd.substring(0, 12);

			strbuf.append("INSERT INTO TB_BAS_DEPART (DEP_CD, ORG_CD, DEP_NM, DEP_ADM_USER, DEP_TEL1, DEP_EMAIL, DEP_TYPE, DEP_INDT) ");
			strbuf.append("VALUES (?, ?, ?, ?, ?, ?, ?, SYSDATE);");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());

			stmt.setString(1,  dep_cd);
			stmt.setString(2, userexp[1]);
			stmt.setString(3, dep_nm);
			stmt.setString(4, dep_adm_user);
			stmt.setString(5, dep_tel1);
			stmt.setString(6, dep_email);
			stmt.setString(7, dep_type);

			result = stmt.executeUpdate();

		} catch(Exception e){
			e.printStackTrace();
			result = -1;
		} finally {
			setOraClose(con,stmt,rs);
		}

		return result;
	}

	//2021.03.08 강원대병원 v3 - 가맹점 리스트 불러오기
	public String get_json_060503_item_merlist(String tuser) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		JSONObject sqlobj = new JSONObject();
		JSONArray sqlAry = new JSONArray();

		StringBuffer qrybuf = new StringBuffer();

		try {
			//tuser split
			String[] userexp = tuser.split(":");

			qrybuf.append("SELECT MER_CD, ORG_CD, T3.DEP_CD, DEP_NM, T1.PUR_CD, PUR_NM, MER_NO, ");
			qrybuf.append("MER_ST, MER_ET, FEE01, FEE02, FEE03, VAN, TO_CHAR(INT_DT,'YYYY-MM-DD HH24:MI:SS') INS_DT ");
			qrybuf.append("FROM TB_BAS_MERINFO T1 ");
			qrybuf.append("LEFT OUTER JOIN(SELECT PUR_NM, PUR_CD, PUR_SORT FROM TB_BAS_PURINFO)T2 ON(T1.PUR_CD=T2.PUR_CD) ");
			qrybuf.append("LEFT OUTER JOIN(SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART)T3 ON(T1.DEP_CD=T3.DEP_CD) ");
			qrybuf.append("WHERE T1.ORG_CD = ? ORDER BY PUR_NM, DEP_NM ASC");

			con = getOraConnect();
			stmt = con.prepareStatement(qrybuf.toString());
			stmt.setString(1, userexp[1]);

			rs = stmt.executeQuery();

			int rcnt = 1;
			while(rs.next()) {
				JSONObject tempObj = new JSONObject();
				JSONArray tempAry = new JSONArray();

				String vangb = utilm.setDefault(rs.getString("VAN"));
				if(vangb.equals("03")) {
					vangb = "코세스";
				} else if (vangb.equals("04")) {
					vangb = "다우";
				}

				tempAry.add(utilm.setDefault(rs.getString("DEP_NM")));
				tempAry.add(utilm.setDefault(rs.getString("PUR_NM")));
				tempAry.add(utilm.setDefault(rs.getString("MER_NO")));
				tempAry.add(vangb);
				tempAry.add(utilm.setDefault(rs.getString("MER_ST"))+"~"+utilm.setDefault(rs.getString("MER_ET")));
				tempAry.add(utilm.setDefault(rs.getString("FEE01")));
				tempAry.add(utilm.setDefault(rs.getString("FEE02")));
				tempAry.add(utilm.setDefault(rs.getString("FEE03")));

				String org_cd = utilm.setDefault(rs.getString("ORG_CD"));
				String mer_cd = utilm.setDefault(rs.getString("MER_CD"));
				String mer_no = utilm.setDefault(rs.getString("MER_NO"));

				tempAry.add("<span onClick=\"merchant_mod('" + org_cd + "', '" + mer_cd + "', '" + mer_no + "')\">수정</span>");
				tempAry.add("<span onClick=\"merchant_del('" + org_cd + "', '" + mer_cd + "', '" + mer_no + "')\">삭제</span>");

				tempAry.add(utilm.setDefault(rs.getString("INS_DT")));

				tempObj.put("id", rcnt);
				tempObj.put("data", tempAry);

				sqlAry.add(tempObj);
				rcnt++;
			}

			sqlobj.put("rows", sqlAry);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}


		return sqlobj.toJSONString();
	}


	public String get_json_060503_item_mermap(String tuser) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		JSONObject sqlobj = new JSONObject();
		JSONArray sqlAry = new JSONArray();

		StringBuffer qrybuf = new StringBuffer();

		try {
			//tuser split
			String[] userexp = tuser.split(":");

			qrybuf.append("SELECT ORG_CD,T1.DEP_CD,T3.DEP_NM,T2.PUR_NM,T5.VAN,MID,TO_CHAR(INSTIME,'YYYY-MM-DD HH24:MI:SS') INS_DT,INSUSER ");
			qrybuf.append("FROM TB_BAS_MIDMAP T1 ");
			qrybuf.append("LEFT OUTER JOIN(SELECT MER_NO, PUR_CD, VAN FROM TB_BAS_MERINFO)T5 ON(T1.MID=T5.MER_NO) ");
			qrybuf.append("LEFT OUTER JOIN(SELECT PUR_NM, PUR_CD, PUR_SORT FROM TB_BAS_PURINFO)T2 ON(T5.PUR_CD=T2.PUR_CD) ");
			qrybuf.append("LEFT OUTER JOIN(SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART)T3 ON(T1.DEP_CD=T3.DEP_CD) ");
			qrybuf.append("WHERE T1.ORG_CD = ? ORDER BY T1.DEP_CD ASC, T2.PUR_NM ASC, INSTIME ASC");

			con = getOraConnect();
			stmt = con.prepareStatement(qrybuf.toString());
			stmt.setString(1, userexp[1]);
			rs = stmt.executeQuery();

			int rcnt = 1;
			while(rs.next()) {
				JSONObject tempObj = new JSONObject();
				JSONArray tempAry = new JSONArray();

				String vangb = utilm.setDefault(rs.getString("VAN"));
				if(vangb.equals("03")) {
					vangb = "코세스";
				} else if (vangb.equals("04")) {
					vangb = "다우";
				}


				tempAry.add(utilm.setDefault(rs.getString("DEP_NM")));
				tempAry.add(utilm.setDefault(rs.getString("PUR_NM")));
				tempAry.add(utilm.setDefault(rs.getString("MID")));
				tempAry.add(vangb);
				tempAry.add(utilm.setDefault(rs.getString("INSUSER")));
				tempAry.add(utilm.setDefault(rs.getString("INS_DT")));

				tempObj.put("id", rcnt);
				tempObj.put("data", tempAry);

				sqlAry.add(tempObj);
				rcnt++;
			}

			sqlobj.put("rows", sqlAry);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}


		return sqlobj.toJSONString();
	}

	//2021.03.08 강원대병원 - 시스템관리 [가맹점관리] 가맹점번호 수정 tab 정보 불러오기
	public String[] get_json_060503_item_merData(String orgcd, String mercd, String mid) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String[] data = new String[10];
		StringBuffer qrybuf = new StringBuffer();

		try {

			qrybuf.append("SELECT MER_CD, ORG_CD, T3.DEP_CD, DEP_NM, T1.PUR_CD, PUR_NM, MER_NO, MER_ST, MER_ET, ");
			qrybuf.append("FEE01, FEE02, FEE03, VAN, TO_CHAR(INT_DT,'YYYY-MM-DD HH24:MI:SS') INS_DT ");
			qrybuf.append("FROM TB_BAS_MERINFO T1 ");
			qrybuf.append("LEFT OUTER JOIN(SELECT PUR_NM, PUR_CD FROM TB_BAS_PURINFO)T2 ON(T1.PUR_CD=T2.PUR_CD) ");
			qrybuf.append("LEFT OUTER JOIN(SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART)T3 ON(T1.DEP_CD=T3.DEP_CD) ");
			qrybuf.append("WHERE T1.ORG_CD = ? AND MER_CD = ? AND MER_NO = ? ");

			con = getOraConnect();
			stmt = con.prepareStatement(qrybuf.toString());
			stmt.setString(1, orgcd);
			stmt.setString(2, mercd);
			stmt.setString(3, mid);

			rs = stmt.executeQuery();

			while(rs.next()) {
				data[0] = utilm.setDefault(rs.getString("DEP_CD"));
				data[1] = utilm.setDefault(rs.getString("MER_NO"));
				data[2] = utilm.setDefault(rs.getString("PUR_CD"));
				data[3] = utilm.setDefault(rs.getString("MER_ST"));
				data[4] = utilm.setDefault(rs.getString("MER_ET"));
				data[5] = utilm.setDefault(rs.getString("FEE01"));
				data[6] = utilm.setDefault(rs.getString("FEE02"));
				data[7] = utilm.setDefault(rs.getString("FEE03"));
				data[8] = utilm.setDefault(rs.getString("DEP_NM"));
				data[9] = utilm.setDefault(rs.getString("ORG_CD"));
			}

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}

		return data;

	}

	//2021.03.08 강원대병원 - 시스템관리 [가맹점관리] 가맹점번호 수정 tab 정보 수정하기
	public int get_json_060503_item_merData_update(String orgcd, String mercd, String depcd, String mid, String purcd, 
			String merst, String meret, String fee01, String fee02, String fee03) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		int result = 0;
		strbuf = new StringBuffer();

		try {
			strbuf.append("UPDATE TB_BAS_MERINFO SET DEP_CD = ?, MER_NO = ?, PUR_CD = ?, MER_ST = ?, ");
			strbuf.append("MER_ET = ?, FEE01 = ?, FEE02 = ?, FEE03 = ?, INT_DT = SYSDATE ");
			strbuf.append("WHERE ORG_CD = ? AND MER_CD = ?;");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());

			stmt.setString(1, depcd);
			stmt.setString(2, mid);
			stmt.setString(3, purcd);
			stmt.setString(4, merst);
			stmt.setString(5, meret);
			stmt.setString(6, fee01);
			stmt.setString(7, fee02);
			stmt.setString(8, fee03);
			stmt.setString(9, orgcd);
			stmt.setString(10, mercd);

			result = stmt.executeUpdate();
		} catch(Exception e){
			e.printStackTrace();
			result = -1;
		} finally {
			setOraClose(con,stmt,rs);
		}
		return result;
	}

	//2021.03.08 강원대병원 - 시스템관리 [가맹점관리] 가맹점번호 수정 tab 정보 삭제하기		
	public int get_json_060503_item_merData_delete(String orgcd, String mid) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int result = 0;
		strbuf = new StringBuffer();

		try {
			strbuf.append("BEGIN ");
			strbuf.append("DELETE FROM TB_BAS_MERINFO WHERE ORG_CD = ? AND MER_NO = ? ; ");
			strbuf.append("DELETE FROM TB_BAS_MIDMAP WHERE ORG_CD = ? AND MID = ? ; ");
			strbuf.append("END;");

			con = getOraConnect();
			con.setAutoCommit(false);
			stmt = con.prepareStatement(strbuf.toString());
			stmt.setString(1, orgcd);
			stmt.setString(2, mid);
			stmt.setString(3, orgcd);
			stmt.setString(4, mid);

			result = stmt.executeUpdate();
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

	//2021.03.08 강원대병원 - 시스템관리 [가맹점관리] 가맹점번호 tab - 가맹점 추가하기
	//1. mid 기존에 검색 -> 조회가 된다면 이미 등록된 가맹점번호
	//2. 아닐경우 insert
	public int get_json_060503_item_merData_insert(String tuser, String depcd, String purcd, String mid, 
			String merst, String meret, String van, String fee01, String fee02, String fee03) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		int result = 0;

		try {
			//tuser split
			String[] userexp = tuser.split(":");
			strbuf = new StringBuffer();
			
			strbuf.append("SELECT COUNT(1) MCNT FROM TB_BAS_MERINFO WHERE MER_NO = ? and DEP_CD = ?");
			con = getOraConnect();
			
			stmt = con.prepareStatement(strbuf.toString());
			stmt.setString(1, mid);
			stmt.setString(2, depcd);

			rs = stmt.executeQuery();
			int mid_check = 0;
			while(rs.next()) {
				mid_check = Integer.parseInt(rs.getString("MCNT"));
			}

			if(mid_check > 0) {
				result = 2;
			} else {
				//new mer_cd
				Date date = new Date();
				String mer_cd = "MD" + date.getTime();
				mer_cd = mer_cd.substring(0, 12);
				strbuf = new StringBuffer();

				strbuf.append("INSERT INTO TB_BAS_MERINFO (MER_CD, ORG_CD, DEP_CD, STO_CD, PUR_CD, MER_NO, MER_ST, MER_ET, FEE01, FEE02, FEE03, VAN, INT_DT) ");
				strbuf.append("VALUES (?, ?, ?, 'ST001', ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE);");

				stmt = con.prepareStatement(strbuf.toString());
				stmt.setString(1, mer_cd);
				stmt.setString(2, userexp[1]);
				stmt.setString(3, depcd);
				stmt.setString(4, purcd);
				stmt.setString(5, mid);
				stmt.setString(6, merst);
				stmt.setString(7, meret);
				stmt.setString(8, fee01);
				stmt.setString(9, fee02);
				stmt.setString(10, fee03);
				stmt.setString(11, van);

				result = stmt.executeUpdate();
			}

		} catch(Exception e){
			e.printStackTrace();
			result = -1;
		} finally {
			setOraClose(con,stmt,rs);
		}

		return result;
	}

	

	//2021.03.08 강원대병원 - 시스템관리 [단말기관리] 단말기번호원장 list
	public String get_json_060504_item_tidlist(String tuser) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		JSONObject sqlobj = new JSONObject();
		JSONArray sqlAry = new JSONArray();

		StringBuffer qrybuf = new StringBuffer();

		try {

			//tuser split
			String[] userexp = tuser.split(":");

			qrybuf.append("SELECT TID_CD, ORG_CD, T1.DEP_CD, DEP_NM, TERM_NM, TERM_ID, ");
			qrybuf.append("TERM_TYPE, VAN, TO_CHAR(TERM_IST_DD,'YYYY-MM-DD HH24:MI:SS') INS_DT ");
			qrybuf.append("FROM TB_BAS_TIDMST T1 ");
			qrybuf.append("LEFT OUTER JOIN(SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART)T2 ON(T1.DEP_CD=T2.DEP_CD) ");
			qrybuf.append("WHERE ORG_CD = ? ORDER BY T1.DEP_CD ASC, T1.TERM_IST_DD ASC");

			con = getOraConnect();
			stmt = con.prepareStatement(qrybuf.toString());

			stmt.setString(1, userexp[1]);
			rs = stmt.executeQuery();

			int rcnt = 1;
			while(rs.next()) {
				String vangb = utilm.setDefault(rs.getString("VAN"));
				String termtype = utilm.setDefault(rs.getString("TERM_TYPE"));

				if(vangb.equals("03")) {
					vangb = "코세스";
				} else if(vangb.equals("04")) {
					vangb = "다우";
				} else {
					vangb = "";
				}

				if(termtype.equals("0")) {
					termtype = "MSR단말기";
				} else if(termtype.equals("1")) {
					termtype = "IC단말기";
				} else {
					termtype = "";
				}

				JSONObject tempObj = new JSONObject();
				JSONArray tempAry = new JSONArray();

				tempAry.add(utilm.setDefault(rs.getString("DEP_NM")));
				tempAry.add(utilm.setDefault(rs.getString("TERM_NM")));
				tempAry.add(utilm.setDefault(rs.getString("TERM_ID")));
				tempAry.add(vangb);
				tempAry.add(termtype);

				String org_cd = utilm.setDefault(rs.getString("ORG_CD"));
				String dep_cd = utilm.setDefault(rs.getString("DEP_CD"));
				String mer_no = utilm.setDefault(rs.getString("TID_CD"));

				tempAry.add("<span onClick=\"tid_mod('" + org_cd + "', '" + dep_cd + "', '" + mer_no + "')\">수정</span>");
				tempAry.add("<span onClick=\"tid_del('" + org_cd + "', '" + dep_cd + "', '" + mer_no + "')\">삭제</span>");

				tempObj.put("id", rcnt);
				tempObj.put("data", tempAry);

				sqlAry.add(tempObj);
				rcnt++;

			}

			sqlobj.put("rows", sqlAry);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}

		return sqlobj.toJSONString();
	}

	//2021.03.08 강원대병원 - 시스템관리 [단말기관리] 단말기번호 등록현황 list
	public String get_json_060504_item_tidmap(String tuser) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		JSONObject sqlobj = new JSONObject();
		JSONArray sqlAry = new JSONArray();

		StringBuffer qrybuf = new StringBuffer();

		try {
			//tuser split
			String[] userexp = tuser.split(":");

			qrybuf.append("SELECT ORG_CD, T1.DEP_CD, T3.DEP_NM, TID, VAN, ");
			qrybuf.append("TO_CHAR(INSTIME,'YYYY-MM-DD HH24:MI:SS') INS_DT, INSUSER, TERM_NM ");
			qrybuf.append("FROM TB_BAS_TIDMAP T1 ");
			qrybuf.append("LEFT OUTER JOIN(SELECT TERM_ID, VAN, TERM_NM FROM TB_BAS_TIDMST WHERE ORG_CD = ? )T5 ON(T1.TID=T5.TERM_ID) ");
			qrybuf.append("LEFT OUTER JOIN(SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART WHERE ORG_CD = ? )T3 ON(T1.DEP_CD=T3.DEP_CD) ");
			qrybuf.append("WHERE T1.ORG_CD = ? ORDER BY T1.DEP_CD ASC, INSTIME ASC");

			con = getOraConnect();
			stmt = con.prepareStatement(qrybuf.toString());

			stmt.setString(1, userexp[1]);
			stmt.setString(2, userexp[1]);
			stmt.setString(3, userexp[1]);

			rs = stmt.executeQuery();

			int rcnt = 1;
			while(rs.next()) {

				String vangb = utilm.setDefault(rs.getString("VAN"));
				if(vangb.equals("03")) {
					vangb = "코세스";
				} else if(vangb.equals("04")) {
					vangb = "다우";
				} else {
					vangb = "";
				}

				JSONObject tempObj = new JSONObject();
				JSONArray tempAry = new JSONArray();

				tempAry.add(utilm.setDefault(rs.getString("DEP_NM")));
				tempAry.add(utilm.setDefault(rs.getString("TERM_NM")));
				tempAry.add(utilm.setDefault(rs.getString("TID")));
				tempAry.add(vangb);
				tempAry.add(utilm.setDefault(rs.getString("INS_DT")));
				tempAry.add(utilm.setDefault(rs.getString("INSUSER")));

				tempObj.put("id", rcnt);
				tempObj.put("data", tempAry);

				sqlAry.add(tempObj);
				rcnt++;
			}

			sqlobj.put("rows", sqlAry);

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}
		
		return sqlobj.toJSONString();
	}

	//2021.03.09 강원대병원 - 시스템관리 [단말기관리] 단말기번호 단말기 정보 read
	public String[] get_json_060503_item_tidData(String depcd, String orgcd, String tid) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String[] data = new String[6];
		StringBuffer qrybuf = new StringBuffer();

		try {
			qrybuf.append("SELECT TID_CD, ORG_CD, T1.DEP_CD, DEP_NM, TERM_NM, TERM_ID, TERM_TYPE, VAN, TO_CHAR(TERM_IST_DD,'YYYY-MM-DD HH24:MI:SS') INS_DT ");
			qrybuf.append("FROM TB_BAS_TIDMST T1 ");
			qrybuf.append("LEFT OUTER JOIN(SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART)T2 ON(T1.DEP_CD=T2.DEP_CD) ");
			qrybuf.append("WHERE ORG_CD = ? AND TID_CD = ? ");

			con = getOraConnect();
			stmt = con.prepareStatement(qrybuf.toString());

			stmt.setString(1, orgcd);
			stmt.setString(2, tid);

			rs = stmt.executeQuery();

			while(rs.next()) {
				data[0] = utilm.setDefault(rs.getString("DEP_CD"));
				data[1] = utilm.setDefault(rs.getString("DEP_NM"));
				data[2] = utilm.setDefault(rs.getString("TERM_NM"));
				data[3] = utilm.setDefault(rs.getString("TERM_ID"));
				data[4] = utilm.setDefault(rs.getString("VANGB"));
				data[5] = utilm.setDefault(rs.getString("TERM_TYPE"));
			}

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}

		return data;
	}

	//2021.03.09 강원대병원 - 시스템관리 [단말기관리] 단말기번호 단말기 정보 update
	public int get_json_060503_item_tid_update(String orgcd, String tidcd, String depcd, String tidnm, String tid, String vangb, String term_type) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int result = 0;
		strbuf = new StringBuffer();

		try {
			strbuf.append("UPDATE TB_BAS_TIDMST SET DEP_CD= ? , TERM_NM= ? , TERM_ID= ? , TERM_TYPE= ? , VAN= ? ,  TERM_IST_DD=SYSDATE ");
			strbuf.append("WHERE ORG_CD= ?  AND TID_CD= ?");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());

			stmt.setString(1, depcd);
			stmt.setString(2, tidnm);
			stmt.setString(3, tid);
			stmt.setString(4, term_type);
			stmt.setString(5, vangb);
			stmt.setString(6, orgcd);
			stmt.setString(7, tidcd);

			result = stmt.executeUpdate();

		} catch(Exception e){
			e.printStackTrace();
			result = -1;
		} finally {
			setOraClose(con,stmt,rs);
		}


		return result;
	}

	//2021.03.09 강원대병원 - 시스템관리 [단말기관리] 단말기번호 단말기 정보 delete
	public int get_json_060503_item_tid_delete(String orgcd, String tid) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int result = 0;
		strbuf = new StringBuffer();

		try {

			strbuf.append("BEGIN ");
			strbuf.append("DELETE FROM TB_BAS_TIDMST WHERE ORG_CD = ? AND TERM_ID = ?; ");
			strbuf.append("DELETE FROM TB_BAS_TIDMAP WHERE ORG_CD = ? AND TID = ?; ");
			strbuf.append("COMMIT; END; ");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());

			stmt.setString(1, orgcd);
			stmt.setString(2, tid);
			stmt.setString(3, orgcd);
			stmt.setString(4, tid);

			result = stmt.executeUpdate();

		} catch(Exception e){
			e.printStackTrace();
			result = -1;
		} finally {
			setOraClose(con,stmt,rs);
		}


		return result;
	}

	//2021.03.08 강원대병원 - 시스템관리 [단말기관리] 단말기번호 insert
	public int get_json_060504_item_insert(String tuser, String depcd, String tid, String term_nm, String term_type, String vangb) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int result = 0;
		strbuf = new StringBuffer();

		try {
			//tuser split
			String[] userexp = tuser.split(":");

			strbuf.append("SELECT COUNT(1) MCNT FROM TB_BAS_TIDMST WHERE TERM_ID = ? and DEP_CD = ? ");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());

			stmt.setString(1, tid);
			stmt.setString(2, depcd);

			rs = stmt.executeQuery();

			int tid_check = 0;
			while(rs.next()) {
				tid_check = Integer.parseInt(rs.getString("MCNT"));
			}

			if(tid_check > 0) {
				return 2;
			} else {
				Date date = new Date();
				String tid_cd = "TD" + date.getTime();
				tid_cd = tid_cd.substring(0, 12);

				strbuf = new StringBuffer();
				strbuf.append("INSERT INTO TB_BAS_TIDMST (TID_CD, ORG_CD, DEP_CD, TERM_NM, TERM_ID, TERM_TYPE, TERM_IST_DD)");
				strbuf.append(" VALUES (?, ?, ?, ?, ?, ?, SYSDATE);");

				stmt = con.prepareStatement(strbuf.toString());

				stmt.setString(1, tid_cd);
				stmt.setString(2, userexp[1]);
				stmt.setString(3, depcd);
				stmt.setString(4, term_nm);
				stmt.setString(5, tid);
				stmt.setString(6, term_type);

				result = stmt.executeUpdate();
			}

		}catch(Exception e){
			e.printStackTrace();
			result = -1;
		} finally {
			setOraClose(con,stmt,rs);
		}

		return result;
	}

	//2021.03.09 강원대병원 - 시스템관리 [사용자관리] 데이터 정보
	public String[] get_json_060505_item_userInfo(String mem_cd) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String[] data = new String[9];
		StringBuffer qrybuf = new StringBuffer();

		try {

			qrybuf.append("SELECT MEM_CD, ORG_CD, T1.DEP_CD, AUTH_SEQ, USER_ID, USER_PW, USER_NM, ");
			qrybuf.append("USER_TEL1, USER_TEL2, USER_MEMO, USER_EMAIL, USER_FAX, USER_LV, T2.DEP_NM ");
			qrybuf.append("FROM TB_BAS_USER T1 ");
			qrybuf.append("LEFT OUTER JOIN(SELECT DEP_NM, DEP_CD FROM TB_BAS_DEPART)T2 ON(T1.DEP_CD=T2.DEP_CD) ");
			qrybuf.append("WHERE MEM_CD = ? ");

			con = getOraConnect();
			stmt = con.prepareStatement(qrybuf.toString());

			stmt.setString(1, mem_cd);

			rs = stmt.executeQuery();

			while(rs.next()) {
				data[0] = utilm.setDefault(rs.getString("DEP_CD"));
				data[1] = utilm.setDefault(rs.getString("USER_ID"));
				//data[2] = utilm.setDefault(rs.getString("USER_PW"));
				data[2] = "";
				data[3] = utilm.setDefault(rs.getString("USER_NM"));
				data[4] = utilm.setDefault(rs.getString("USER_LV"));
				data[5] = utilm.setDefault(rs.getString("USER_TEL1"));
				data[6] = utilm.setDefault(rs.getString("USER_TEL2"));
				data[7] = utilm.setDefault(rs.getString("USER_EMAIL"));
				data[8] = utilm.setDefault(rs.getString("DEP_NM"));
			}

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			setOraClose(con,stmt,rs);
		}

		return data;
	}

	//2021.03.09 강원대병원 - 시스템관리 [사용자관리] 데이터 수정
	//2021.03.17 비밀번호 hash처리
	public int get_json_060505_item_userInfo_update(String orgcd, String depcd, String memcd, String mem_id, String mem_pw, 
			String mem_nm, String memlv, String mem_tel1, String mem_tel2, String mem_email) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int result = 0;
		strbuf = new StringBuffer();

		try {
			strbuf.append("SELECT AUTH_SEQ FROM TB_SYS_AUTH WHERE ORG_CD = ? AND LV = ? ");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());

			stmt.setString(1, orgcd);
			stmt.setString(2, memlv);

			rs = stmt.executeQuery();

			String auth_seq = "";
			while(rs.next()) {
				auth_seq = utilm.setDefault(rs.getString("AUTH_SEQ"));
			}

			if(auth_seq.equals("")) {
				auth_seq = "AS000002";
			}
			
			MessageDigest md;
			StringBuilder builder = new StringBuilder();
			
			try {
				//sha-256 변경
				md = MessageDigest.getInstance("SHA-256");
				md.update(mem_pw.getBytes());
			    for (byte b: md.digest()) {
				      builder.append(String.format("%02x", b));
			    }
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			strbuf = new StringBuffer();

			strbuf.append("UPDATE TB_BAS_USER SET USER_ID = ?, USER_PW = ? , USER_NM = ? , ");
			strbuf.append("USER_TEL1= ? , USER_TEL2 = ? , USER_EMAIL = ?, ");
			strbuf.append("DEP_CD= ? , USER_LV= ? , AUTH_SEQ= ? , INS_DT=SYSDATE ");
			strbuf.append("WHERE ORG_CD= ? AND MEM_CD= ? ");
			stmt = con.prepareStatement(strbuf.toString());

			stmt.setString(1, mem_id);
			stmt.setString(2, builder.toString().trim());
			stmt.setString(3, mem_nm);
			stmt.setString(4, mem_tel1);
			stmt.setString(5, mem_tel2);
			stmt.setString(6, mem_email);
			stmt.setString(7, depcd);
			stmt.setString(8, memlv);
			stmt.setString(9, auth_seq);
			stmt.setString(10, orgcd);
			stmt.setString(11, memcd);

			result = stmt.executeUpdate();

		} catch(Exception e){
			e.printStackTrace();
			result = -1;
		} finally {
			setOraClose(con,stmt,rs);
		}

		return result;
	}

	//2021.03.09 강원대병원 - 시스템관리 [사용자관리] 데이터 삭제
	public int get_json_060505_item_userInfo_delete(String orgcd, String memcd) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		

		int result = 0;
		strbuf = new StringBuffer();

		try {
			strbuf.append("DELETE FROM TB_BAS_USER WHERE ORG_CD = ?  AND MEM_CD = ?; ");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());

			stmt.setString(1, orgcd);
			stmt.setString(2, memcd);

			result = stmt.executeUpdate();

		} catch(Exception e){
			e.printStackTrace();
			result = -1;
		} finally {
			setOraClose(con,stmt,rs);
		}

		return result;
	}

	//2021.03.09 강원대병원 - 시스템관리 [사용자관리] 사용자 생성
	//2021.03.17 비밀번호 hash 처리
	public int get_json_060505_item_insert(String tuser, String depcd, String memid, 
			String mempw, String memnm, String memlv, String memtel1, 
			String memtel2, String mememail) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		int result = 0;
		strbuf = new StringBuffer();

		try {
			//tuser split
			String[] userexp = tuser.split(":");

			strbuf.append("SELECT COUNT(1) MCNT FROM TB_BAS_USER WHERE USER_ID = ? ");

			con = getOraConnect();
			stmt = con.prepareStatement(strbuf.toString());

			stmt.setString(1, memid);

			rs = stmt.executeQuery();

			int userCheck = 0;
			if(rs.next()) {
				userCheck = Integer.parseInt(rs.getString("MCNT"));
			}

			if(userCheck > 0) {
				return 2;
			} else {
				strbuf = new StringBuffer();
				strbuf.append("SELECT AUTH_SEQ FROM TB_SYS_AUTH WHERE ORG_CD = ? AND LV = ? ");

				stmt = con.prepareStatement(strbuf.toString());
				stmt.setString(1, userexp[1]);
				stmt.setString(2, memlv);

				rs = stmt.executeQuery();

				String auth_seq = "";
				if(rs.next()) {
					auth_seq = rs.getString("AUTH_SEQ");

					if(auth_seq.equals("")) {
						auth_seq = "AS000002";
					}
				}

				Date date = new Date();
				String mem_cd = "MEM" + date.getTime();
				mem_cd = mem_cd.substring(0, 12);
				
				MessageDigest md;
				StringBuilder builder = new StringBuilder();
				
				try {
					md = MessageDigest.getInstance("SHA-256");
					md.update(mempw.getBytes());
				    for (byte b: md.digest()) {
					      builder.append(String.format("%02x", b));
				    }
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				strbuf = new StringBuffer();
				strbuf.append("INSERT INTO TB_BAS_USER ");
				strbuf.append("(MEM_CD, ORG_CD, AUTH_SEQ, USER_ID, USER_PW, USER_NM, USER_TEL1, USER_TEL2, ");
				strbuf.append("USER_MEMO, USER_EMAIL, USER_FAX, DEP_CD, USER_LV, INS_DT) ");
				strbuf.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE) ");

				stmt = con.prepareStatement(strbuf.toString());
				stmt.setString(1, mem_cd);
				stmt.setString(2, userexp[1]);
				stmt.setString(3, auth_seq);
				stmt.setString(4, memid);
				stmt.setString(5, builder.toString().trim());
				stmt.setString(6, memnm);
				stmt.setString(7, memtel1);
				stmt.setString(8, memtel2);
				stmt.setString(9, "");
				stmt.setString(10, mememail);
				stmt.setString(11, "");

				//통합관리자 계정
				if(depcd.equals("M")) {
					stmt.setString(12, "");
				} else {
					stmt.setString(12, depcd);
				}

				stmt.setString(13, memlv);

				result = stmt.executeUpdate();
			}


		} catch(Exception e){
			e.printStackTrace();
			result = -1;
		} finally {
			setOraClose(con,stmt,rs);
		}

		return result;
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

