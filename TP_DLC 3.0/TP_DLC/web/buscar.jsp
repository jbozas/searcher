<%-- 
    Document   : buscar
    Created on : May 22, 2018, 4:01:06 AM
    Author     : dlcusr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <div>    
            <form style="margin-top: 30px; margin-left:800px;" method="POST" action="ctrlBuscar" >
                <div class="form-group col-xs-8"><input style="width:300px; margin-right: 250px; margin-top:350; margin-left: 25px;"  type="text" name="cadena" ></div>
                <input style=" width:100px; top:36px ; right: 200px" type="submit" value="Buscar" class="btn btn-default col-xs-4">
            </form>
        </div>
        <div id="tabla">
            <table border="2"> 
                <caption>Resultado de la búsqueda</caption>
                <tr>
                    <th>Archivo</th>
                    <th>Peso</th>
                    <th>Link</th>
                </tr>
                               
                <c:forEach items ="${resultado}" var="r">
                    <tr>
                        <td height="50" width="150"> ${r.getName()} </td>
                        <td height="50" width="150"> ${r.getWeight()} </td>
                        <td height="50" width="150"> <a href="ctrlDescarga?nombre=${r.getName()}&path=${r.getPath()}">Descargar</a></td>
                    </tr>

                </c:forEach>
            </table>
           
        </div>
    </body>

</html>
