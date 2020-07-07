<%-- 
    Document   : indexar
    Created on : May 22, 2018, 3:46:46 AM
    Author     : dlcusr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"> 
    </head>
    <body>
        <form style="margin-top:0px; margin-left:800px;" method="POST" enctype="multipart/form-data" action="ctrlArchivoAdd" >
            Fichero: <input type="file" name="adjunto" accept=".txt">
            <input type="submit" value="Indexar">
        </form>
    </body>
</html>
