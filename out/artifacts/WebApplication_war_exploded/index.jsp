<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
  <head>
    <title>Chupmos Oh Boi</title>
  </head>
  <body>
  <c:forEach items="${result}" var="item">
      ${item}<br/>
  </c:forEach>
  </body>
</html>
