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

	
	
	

		
		
		
		

		
		
}

