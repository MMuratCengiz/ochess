<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="d-flex" id="wrapper">
    <div class="border-right bg-dark text-light w-10" id="sidebar-wrapper">
        <div class="list-group list-group-flush">

            <!-- If in game, show surrender, help with back to game button-->
            <!-- Otherwise show profile,logout,lobby -->
            <spring:url value='/profile/' var="profile"/>
            <spring:url value='/lobby/' var="lobby"/>
            <spring:url value='/help/' var="help"/>
            <spring:url value='/logout/' var="logout"/>

            <a href="${profile}" class="list-group-item list-group-item-action bg-dark text-light "><h2>Show Profile</h2></a>
            <a href="${lobby}" class="list-group-item list-group-item-action bg-dark text-light "><h2>Lobby</h2></a>
            <a href="${help}" class="list-group-item list-group-item-action bg-dark text-light "><h2>Help</h2></a>
            <a href="${logout}" class="list-group-item list-group-item-action bg-dark text-light "><h2>Logout</h2></a>
        </div>
    </div>

    <div id="page-content-wrapper">
        <div class="container-fluid">
            <div class="row">
                <div class="col-md"></div>
                <div class="col-md"></div>
                <div class="col-md"></div>
                <div class="col-md"><label class="ochess-title">Online Chess</label></div>
            </div>
            <c:out value="${content}" escapeXml="false"/>
        </div>
    </div>

</div>