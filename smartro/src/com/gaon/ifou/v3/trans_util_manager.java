package com.gaon.ifou.v3;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class trans_util_manager{
	
	private java.io.File folder = null;
	
	private static SimpleDateFormat NowDate = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat NowDateShot = new SimpleDateFormat("yyMMdd");
	private static SimpleDateFormat NowTime = new SimpleDateFormat("HHmmss");
	private static SimpleDateFormat FullDate = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	private static SimpleDateFormat NowMinu = new SimpleDateFormat("mm");
	private static SimpleDateFormat NowSecon = new SimpleDateFormat("ss");
	private static SimpleDateFormat y4 = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat m2 = new SimpleDateFormat("MM");
	private static SimpleDateFormat d2 = new SimpleDateFormat("dd");
	
	public String DateFormatFull(){
		Date toDay = new Date();	
		String nowdate	= FullDate.format(toDay);
		return nowdate;
	}
	
	public String DateY4(){
		Date toDay = new Date();	
		String nowdate	= y4.format(toDay);
		return nowdate;
	}
	
	public String DateM2(){
		Date toDay = new Date();	
		String nowdate	= m2.format(toDay);
		return nowdate;
	}
	
	public String DateD2(){
		Date toDay = new Date();	
		String nowdate	= d2.format(toDay);
		return nowdate;
	}
	
	public String DateFormatNowDate(){
		Date toDay = new Date();	
		String nowdate	= NowDate.format(toDay);
		return nowdate;
	}
	
	public String NowDateShot(){
		Date toDay = new Date();	
		String nowdate	= NowDateShot.format(toDay);
		return nowdate;
	}
	
	public String DateFormatNowTime(){
		Date toDay = new Date();	
		String nowdate	= NowTime.format(toDay);
		return nowdate;
	}
	
	public String DateFormatYesDay(int d){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, d);
		Date today = cal.getTime();
		SimpleDateFormat fomatter = new SimpleDateFormat("yyyyMMdd");
		
		return fomatter.format(today);
	}
	
	public String YesDateShot(int d){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, d);
		Date today = cal.getTime();
		SimpleDateFormat fomatter = new SimpleDateFormat("yyMMdd");
		
		return fomatter.format(today);
	}
	
	public String DateFormatNowMinu(){
		Date toDay = new Date();	
		String rtn	= NowMinu.format(toDay);
		
		return rtn;
	}
	
	public String DateFormatNowSecon(){
		Date toDay = new Date();	
		String rtn	= NowSecon.format(toDay);
		
		return rtn;
	}
	
	public String WeekCount(){
		String[] DaytoDay = {"��","��","ȭ","��","��","��","��"};
		Calendar cal = Calendar.getInstance();
		int no = cal.get(Calendar.DAY_OF_WEEK) - 1 -1;
		if( no < 0 ){
			no=6;
		}
		return DaytoDay[no];
	}
	
	public void MakeFolder(File tfolder) {	
		folder = tfolder;
		
        if(!folder.exists()){
			try{
				folder.mkdirs();
			}catch(Exception ex){
				System.out.println(ex);
			}
		}
	}
	
	//private static final String EMPTY = null;

	/*
	* STring�� ������ ���̸�ŭ  �������� ä��.
	* @param str �������ڿ�
	* @param len ä�����
	* @param addStr ä�﹮��
	* @return ��ȯ��
	*/
	public String lpad(String str, int len, String addStr) {

         String result = str;
         int templen   = len - result.length();
 
        for (int i = 0; i < templen; i++){
               result = addStr + result;
         }
         
         return result;
	}
	
	public String rpad(String str, int len, String addStr) {

        String result = str;
        int templen   = len - result.length();

       for (int i = 0; i < templen; i++){
              result = result + addStr;
        }
        
        return result;
	}
	
	public String replace_space(String str) {

        String result 	= "";
        String tmpstr	= "";
        
        if(str.equals("Y")){
        	result	= str;
        }else{
        	result  = " "; 
        }
        
        return result;
	}
	
	public String PtbsFlag(String str) {

        String result 	= "";
        
        if(str.equals("E")){
        	result	= "E";
        }else if(str.equals("Y")){
        	result  = "Y"; 
        }else if(str.equals("N")){
        	result  = "N"; 
        }else if(str.equals("NOPTBS")){
        	result  = "B"; 
        }else if(str.equals("NOOTPT")){
        	result  = "P"; 
        }else if(str.equals("NOSPEC")){
        	result  = "C"; 
        }
        
        return result;
	}
	
	
	public static String AddBySpace(String obj, int sLen){
		byte[] SpcStr = new byte[sLen];
		int RtnStrLen	= obj.length();
		String RtnStr	= "";
		
		for(int i=obj.length();i<sLen;i++){
			RtnStr += " "; 
		}		
		
		return obj+RtnStr;
	}
	
	public String kString(String strData, int iStartPos, int iByteLength) {
		 
		byte[] bytTemp = null;
 		int iRealStart = 0;
 		int iRealEnd = 0;
 		int iLength = 0;
 		int iChar = 0;

 		try {
 		// UTF-8�� ��ȯ�ϴ°�� �ѱ� 2Byte, ��Ÿ 1Byte�� ������
			bytTemp = strData.getBytes("EUC-KR");
			iLength = bytTemp.length;

			for(int iIndex = 0; iIndex < iLength; iIndex++) {
 				if(iStartPos <= iIndex) {
 					break;
 				}
 
 				iChar = bytTemp[iIndex];
 				if((iChar > 127)|| (iChar < 0)) {
 					// �ѱ��� ���(2byte ���ó��)
 					// �ѱ��� 2Byte�̱� ������ ���� ���ڴ� ���͵� ���� ��ŵ�Ѵ�
 					iRealStart++;
 					iIndex++;
 				} else {
 					// ��Ÿ �۾�(1Byte ���ó��)
 					iRealStart++;
 				}
 			}
 			
			iRealEnd = iRealStart;
			int iEndLength = iRealStart + iByteLength;

			for(int iIndex = iRealStart; iIndex < iEndLength; iIndex++)
 			{
 				iChar = bytTemp[iIndex];
 				if((iChar > 127)|| (iChar < 0)) {
 					// �ѱ��� ���(2byte ���ó��)
 					// �ѱ��� 2Byte�̱� ������ ���� ���ڴ� ���͵� ���� ��ŵ�Ѵ�
 					iRealEnd++;
 					iIndex++;
 				} else {
 					// ��Ÿ �۾�(1Byte ���ó��)
 					iRealEnd++;
 				}
 			}
 		} catch(Exception e) {
 				//
 			//Log.d("DEBUG",e.getMessage());
 		}
 		
		return strData.substring(iRealStart, iRealEnd);
	} 
	
	public String AddZero(String rstr, int number){
		String tmpstr = ""; 
		String rtn  = "";
		int strlen	= rstr.length();
		
		if(strlen>number){
			rtn = rstr.substring(0, number);
		}else{
			for(int i=0; i<(number-strlen); i++){
				tmpstr += "0";
			}
			
			rtn = tmpstr+rstr;
		}
		return rtn;
	}
	
	public static String implode(String separator, String[] data) {
	    StringBuilder sb = new StringBuilder();
	    try {
		    for (int i = 0; i < data.length; i++) {
		    	if(null!=data[i]) {
		    		sb.append(data[i]);
		    		if(null!=data[i+1]) {
			            sb.append(separator);
			    	}
		    	}
		    }
		} catch (Exception e) {
		}
	    
	    return sb.toString();
	}

	/**
	 * ���ݿ����� ī���ȣ�� 0~9, = �� ������ �Է��� �����ϱ� ���� �Լ�
	 * 20151022 �嵵��
	 * @param valu	: ���ڿ� �Է�
	 * @return		: ���ŵ� ���ڿ� ���
	 */
	public String arraySearch(String valu){
		
		byte[] CashKey = new byte[] {0x30,0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x3D};
		byte[] srcArr = valu.getBytes();
		byte[] decArr = new byte[srcArr.length];
		int temp = 0;
		for(int s=0;s<srcArr.length;s++){
		
			for(int i=0; i<CashKey.length;i++){
				if(CashKey[i]==srcArr[s]){
					decArr[temp]= srcArr[s];
					temp++;
					break;
				}
			}
			
			
		}
		return new String(decArr).trim();
	}
	
	public static String getKocesCardCode(String acq) {
	    String rtn = "";

	    if (acq.equals("1101"))//����ī��
	      rtn = "16";
	    else if (acq.equals("1102"))//����ī��
	      rtn = "27";
	    else if (acq.equals("1103"))//�Ե�ī��
	      rtn = "47";
	    else if (acq.equals("1104"))//�Ｚī��
	      rtn = "31";
	    else if (acq.equals("1105"))//��ȯī��
	      rtn = "08";
	    else if (acq.equals("1106"))//��ī��
	      rtn = "26";
	    else if (acq.equals("1107"))//����ī��
	      rtn = "29";
	    else if (acq.equals("1180"))//�ؿܺ���
	      rtn = "22";
	    else if (acq.equals("1181"))//�ؿܸ�����
	      rtn = "78";
	    else if (acq.equals("1182"))//JCB
	      rtn = "28";
	    else if (acq.equals("2207"))//����
	      rtn = "17";
	    else if (acq.equals("2211"))//����
	      rtn = "18";
	    else if (acq.equals("2227"))//�ѹ�
	      rtn = "19";
	    else if (acq.equals("2234"))//����
	      rtn = "02";
	    else if (acq.equals("2235"))//����
	      rtn = "11";
	    else if (acq.equals("2237"))//����
	      rtn = "10";
	    else if (acq.equals("2281")) {//�ϳ�SK
	      rtn = "06";
	    }

	    return rtn;
	  }
	public String getDauCardCode(String strParamCardCd)
	{
		String strReturn = "";
		
		if(strParamCardCd.equals("001")){//bc
			strReturn = "01";
		}else if(strParamCardCd.equals("002")){//����
			strReturn = "02";
		}else if(strParamCardCd.equals("005")){//��ȯ
			strReturn = "03";
		}else if(strParamCardCd.equals("003")){//�Ｚ
			strReturn = "04";
		}else if(strParamCardCd.equals("004")){//����
			strReturn = "05";
		}else if(strParamCardCd.equals("008")){//����
			strReturn = "12";
		}else if(strParamCardCd.equals("094")){//�ؿܾƸƽ�
			strReturn = "40";
		}else if(strParamCardCd.equals("016")){//NHī��
			strReturn = "15";
		}else if(strParamCardCd.equals("007")){//�Ե�
			strReturn = "13";
		}else if(strParamCardCd.equals("010")){//�ϳ�SK
			strReturn = "03";
		}else if(strParamCardCd.equals("092")){//�ؿ� VISA
			strReturn = "40";
		}else if(strParamCardCd.equals("093")){//�ؿ�MASTER
			strReturn = "40";
		}else if(strParamCardCd.equals("095")){//�ؿ� DINERS
			strReturn = "40";
		}else if(strParamCardCd.equals("011")){//��Ƽ����
			strReturn = "05";
		}		
		
		return strReturn;
	}
	
	public String byteArrayToHex(byte[] buffer) {
	    if (buffer == null || buffer.length == 0) {
	        return null;
	    }
	 
	    StringBuffer sb = new StringBuffer(2);
	    String hexNumber;
	    for (int x = 0; x < buffer.length; x++) {
	        hexNumber = "0" + Integer.toHexString(0xff & buffer[x]);
	        sb.append(" "+hexNumber.substring(hexNumber.length() - 2));
	    }
	    return sb.toString();
	}
	
	public String rpadByte(String obj, int len){
		byte[] t1 	= obj.getBytes();
		byte[] t2	= new byte[len];
		
		for(int i=0;i<len;i++){
			if(i<t1.length){
				t2[i] = t1[i];
			}else{
				t2[i] = 0x20;
			}
		}
		
		return new String(t2);
	}
	
	
	
	
	public byte[] hexStringToByteArray(String s) {
		
		byte[] b = new byte[s.length() / 2];
		try {
		    for (int i = 0; i < b.length; i++) {
				int index = i * 2;
				int v = Integer.parseInt(s.substring(index, index + 2), 16);
				b[i] = (byte) v;
		    }
		}catch(Exception e) {}
	    return b;
	  }
	
	public String cardno_masking(String cardno) {
		String rtn = "";
		try {
			
			String[] exp = cardno.trim().split("=");
			
			if(exp[0].length()>10){
				rtn = exp[0].substring(0, 6) + "******" + exp[0].substring(12, exp[0].length());
			}
		} catch (Exception e) {
			rtn = cardno;
		}
		
		return rtn;
	}

	/**
	 * ���ν� �ΰ���, ��ҽ� �����ι�ȣ�� �¾�
	 * @param expd
	 * @return
	 */
	public String card_oappno_chk(String[] expd) {
		String rtn = "";
		try{
			if("0".equals(expd[1])){
				rtn = expd[8];
			}else if("1".equals(expd[1])){
				rtn = expd[9];
			}
		}catch(Exception e){}
		return rtn;
	}

	/**
	 * ���ν� �����, ��ҽ� ���������ڸ� �¾�
	 * @param expd
	 * @return
	 */
	public String card_oappdd_chk(String[] expd) {
		String rtn = "";
		try{
			if("0".equals(expd[1])){
				rtn = expd[7];
			}else if("1".equals(expd[1])||"8".equals(expd[1])){
				if(expd[10].length()<8){
					rtn = "20"+expd[10];
				}else{
					rtn = expd[10];
				}
			}
		}catch(Exception e){}
		return rtn;
	}
	
	public String auth_convert(String authcd){
		String rtn = "";
		try {
			if(authcd.equals("0")){
				rtn = "A";
			}else if(authcd.equals("1")){
				rtn = "C";
			}else if(authcd.equals("7")){
				rtn = "A";
			}else if(authcd.equals("8")){
				rtn = "C";
			}else{
				rtn = "F";
			}
		} catch (Exception e) {
		}		
		return rtn;
	}

	public String cardtype(String ctype) {
		String rtn = "";
		try {
			if(ctype.equals("Y")){
				rtn = "1";
			}else if(ctype.equals("N")){
				rtn = "2";
			}else{
				rtn = "4";
			}
		} catch (Exception e) {
		}
		return rtn;
	}
	
	public String strcardtype(String ctype) {
		String rtn = "";
		try {
			if(ctype.equals("Y")){
				rtn = "üũī��";
			}else{
				rtn = "�Ϲ�ī��";
			}
		} catch (Exception e) {
		}
		return rtn;
	}
	
	public String strsigngb(String ctype) {
		String rtn = "";
		try {
			if(ctype.equals("1")){
				rtn = "���ڼ���";
			}else{
				rtn = "������";
			}
		} catch (Exception e) {
		}
		return rtn;
	}
	
	public String str_to_dateformat(String in) {
		String rtn = "";
		StringBuffer strbuf = new StringBuffer();
		try {
			if(in.trim().length()==8){
				strbuf.append(in.substring(0, 4));
				strbuf.append("/");
				strbuf.append(in.substring(4, 6));
				strbuf.append("/");
				strbuf.append(in.substring(6, 8));
				
				rtn = strbuf.toString();
			}else{
				//rtn = in;
			}
		} catch (Exception e) {
		}finally {
			strbuf = null;
		}
		return rtn;
	}
	
	//2021.02.26 ���Ը����� ��¥ǥ��
	public String str_to_dateformat_deposit(String in) {
		String rtn = "";
		StringBuffer strbuf = new StringBuffer();
		try {
			if(in.trim().length()==8){
				strbuf.append(in.substring(0, 4));
				strbuf.append("-");
				strbuf.append(in.substring(4, 6));
				strbuf.append("-");
				strbuf.append(in.substring(6, 8));
				
				rtn = strbuf.toString();
			}else{
				//rtn = in;
			}
		} catch (Exception e) {
		}finally {
			strbuf = null;
		}
		return rtn;
	}
	
	
	public String deposit_rst_to_kor(String in) {
		String rtn = "";
		StringBuffer strbuf = new StringBuffer();
		try {
			if(in.equals("60") || in.equals("67")){
				rtn = "�������";
			}else if(in.equals("61") || in.equals("64")){
				rtn = "���Թݼ�";
			}else{
				//rtn = in;
			}
		} catch (Exception e) {
		}finally {
			strbuf = null;
		}
		return rtn;
	}
	
	public String str_to_timeformat(String in) {
		String rtn = "";
		StringBuffer strbuf = new StringBuffer();
		try {
			if(in.trim().length()==6){
				strbuf.append(in.substring(0, 2));
				strbuf.append(":");
				strbuf.append(in.substring(2, 4));
				strbuf.append(":");
				strbuf.append(in.substring(4, 6));
				
				rtn = strbuf.toString();
			}else{
				//rtn = in;
			}
		} catch (Exception e) {
		}finally {
			strbuf = null;
		}
		return rtn;
	}
	
	
	public String set_appgb_to_kor(String in) {
		String rtn = "";
		try {
			if(in.trim().equals("A")){
				rtn = "�ſ����";
			}else if(in.trim().equals("C")){
				rtn = "�������";
			}
		} catch (Exception e) {
		}finally {
		}
		return rtn;
	}
	
	public static String setDefault(String str) 
	{
		String rtnstr = "";
		try {
			if(str != null) {
				rtnstr = str;
			}else {
				rtnstr = "";
			}
		} catch (Exception e) {
		}
		return rtnstr;
	}
	
	public static String getTimestamp()
	{
		String rtnstr = "";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
			Calendar cal = Calendar.getInstance();
			String today = null;
			today = formatter.format(cal.getTime());
			Timestamp ts = Timestamp.valueOf(today);
			
			rtnstr = ts.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return rtnstr;
	}

	public String deposit_hold_check(String state) {
		String rtnstr = "";
		try {
			if(state.equals("RV01")) {
				rtnstr = "1";
			}else{
				rtnstr = "0";
			}
		} catch (Exception e) {
		}
		return rtnstr;
	}

	public String deposit_state_check(String state) {
		String rtnstr = "";
		try {
			if(state.equals("RV01")) {
				rtnstr = "����";
			}else if(state.equals("RQ01")) {
				rtnstr = "û���Ϸ�";
			}else if(state.equals("DP01")) {
				rtnstr = "���ԿϷ�";
			}else if(state.equals("DP99")) {
				rtnstr = "���Թݼ�";
			}else {
				rtnstr = "û�����";
			}
		} catch (Exception e) {
		}
		return rtnstr;
	}

	public String deposit_result_str(String rtncd) {
		String rtnstr = "";
		try {
			if(rtncd.equals("60")) {
				rtnstr = "60:��������";
			}else if(rtncd.equals("67")) {
				rtnstr = "67:�������";
			}else if(rtncd.equals("62")) {
				rtnstr = "62:���⺸��";
			}else if(rtncd.equals("63")) {
				rtnstr = "63:���⺸������";
			}else if(rtncd.equals("61")) {
				rtnstr = "61:����ݼ�";
			}else if(rtncd.equals("64")) {
				rtnstr = "64:��ҹݼ�";
			}else if(rtncd.equals("65")) {
				rtnstr = "65:��Һ���";
			}else if(rtncd.equals("66")) {
				rtnstr = "66:��Һ�������";
			}else {
				rtnstr = "��������";
			}
		} catch (Exception e) {
		}
		return rtnstr;
	}
	
	//2021.02.08 trans_org_manager - query�� ������ �Լ�
	public void debug_sql(StringBuffer qrybuf, ArrayList<String> setting) {
		
		System.out.println("========================");
		System.out.println("query check :: ");
		System.out.println(qrybuf.toString());
		System.out.println("parameter check - ");
		for(int k = 0; k < setting.size(); k++) {
			System.out.println((k+1) +" : "+setting.get(k));
		}
		System.out.println("========================");
		
	}
	
	//2021.02.16 null 0���� ġȯ����
	public String checkNumberData(String str){
		if(str == null){
			return "0";
		}
		return str;
	}
	public String checkNumberData(Double num){
		int nNum = 0;
		if(num == null){
			return "0";
		}else {
			nNum = (int)Math.floor(num);
		}
		return Integer.toString(nNum);
	}
	
	
}