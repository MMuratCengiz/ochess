<%@ page import="com.mcp.ochess.game.UIBoardUtils" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"  %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<div id="chessboard mt-5">
<table id="board">

<%=UIBoardUtils.createBoard()%>

</table>
</div>