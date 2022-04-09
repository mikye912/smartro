<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.*, java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="java.lang.String" %>
<%@ page import="java.security.*" %>
<%@ page import="java.util.Base64.Encoder" %>
<%@ page import="java.util.Base64.Decoder" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="org.json.simple.parser.JSONParser"%>
<%@ page import="com.oreilly.servlet.MultipartRequest" %>
<%@page import="com.oreilly.servlet.multipart.DefaultFileRenamePolicy"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<jsp:useBean id="utilm" class="com.gaon.ifou.v3.trans_util_manager" scope="page" />
<%

	String tuser =  utilm.setDefault(request.getParameter("uauth"));

	Date nowTime = new Date();
	String folderName = String.valueOf(nowTime.getTime());
	
	//경로 변경 해줄 것
	String uploadPath = "D:\\BACK\\Desktop\\3tier\\upload" + "\\" + folderName;
	
	File upDir = new File(uploadPath);
	if(!upDir.exists()){
		upDir.mkdirs();
		//권한설정
	}
	
	//System.out.println(uploadPath);
	int maxSize = 1024 * 1024 * 10;
	
	//uploadPath 경로에 자동으로 파일이 업로드됨.
	MultipartRequest multi = new MultipartRequest(request,uploadPath,maxSize,"utf-8",new DefaultFileRenamePolicy());

	File uplodaFile = multi.getFile("file");
	String result = jbset.file_excelUpload(tuser, uplodaFile);
	
%>
<script>
	alert("<%=result%>");
</script>

