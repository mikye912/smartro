<%@ page import="com.gaon.ifou.v3.gowas" language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<jsp:useBean id="utilm" class="com.gaon.ifou.v3.trans_util_manager" scope="page" />
<%
String uid = utilm.setDefault(request.getParameter("userid")); //null 처리를 위해 java에서 func 제공
String upw = utilm.setDefault(request.getParameter("userpw")); //null 처리를 위해 java에서 func 제공

String rtncd = jbset.user_login(uid, upw); 

out.println(rtncd);
%>