<%@ page language="java"
    contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!doctype html>
<html>
<head>
  <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/layout.css">
  <title><c:out value="${title}"/></title>
</head>
<body>
<div>
<c:out value="${title}"/>
</div>
<c:import url="${content}" />
</body>
</html>
