<%-- 
    Document   : indexar
    Created on : May 22, 2018, 1:37:08 AM
    Author     : dlcusr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="searcher.*"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Valerian</title>
        <link rel="icon" type="image/jpg" href="<c:url value="/img/valerian.jpg"/>">
        <link rel="stylesheet" type="text/css" href="<c:url value="/css/style.css"/>">
    </head>

    <body>

        <div>
            <div>
                <jsp:include page="buscar.jsp"/>
                <jsp:include page="indexar.jsp"/>
            </div>
        </div>
    </body>
    <footer id="footer" style="float:right; position:relative; top:100px">
        <p>
            <i>Acosta, Franco - Bozas, Julian - Gaviglio, Lorenzo - TP DLC - 2018</i>
        </p>
    </footer>
</html>
