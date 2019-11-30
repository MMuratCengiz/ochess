<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"  %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib prefix="var" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:set var="lobbyid" value="1"/>
<html>
<head>
    <title>Playing vs TBD!</title>
    <jsp:include page="ref.jsp" />
    <script>
        var lobbyId = "${lobbyid}";
    </script>
    <script src="<spring:url value='/resources/js/game.js'/>" type="application/javascript"></script>
    <link href="<spring:url value='/resources/css/game.css'/>" rel="stylesheet" type="text/css"/>
</head>
<body onload="onLoad()">
<!--
    Navigation bar, surrender, tutorial etc..
 -->

<c:set var="content" scope="request">
    <div class="row">
        <div class="col-md"></div>
        <div class="col-md"><label class="player" id="p1">Player 1</label><label class="player" id="p2">Player 2</label></div>
        <div class="col-md"></div>
        <div class="col-md"></div>
    </div>
    <div class="row">
        <div class="col-md"></div>
        <div class="col-md"><jsp:include page="board.jsp"/></div>
        <div class="col-md"></div>
        <div class="col-md"></div>
    </div>
</c:set>

<jsp:include page="nav.jsp"/>

</body>
</html>
