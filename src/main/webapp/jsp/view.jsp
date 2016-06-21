<%@ page language="java"
    contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="left">
    <ul>
    <c:forEach var="item" items="${search}" >
    <li><a href="/docs/<c:out value="${item.contentId}"/>"><c:out value="${item.title}"/></a></li>
    </c:forEach>
    </ul>
</div>
<div class="main">
    <c:out value="${body}" escapeXml="false"/>
</div>

