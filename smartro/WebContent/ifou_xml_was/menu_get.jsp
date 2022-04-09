<%@ page import="com.gaon.ifou.v3.gowas" language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="jbset" class="com.gaon.ifou.v3.gowas" scope="page" />
<jsp:useBean id="utilm" class="com.gaon.ifou.v3.trans_util_manager" scope="page" />
<%
String orgcd = utilm.setDefault(request.getParameter("orgcd")); //null 처리를 위해 java에서 func 제공
String mseq = utilm.setDefault(request.getParameter("mseq")); //null 처리를 위해 java에서 func 제공

String rtncd = jbset.general_menu(orgcd, mseq);

out.println(rtncd);
%>