<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <link href="sbadmin2/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="sbadmin2/dist/css/sb-admin-2.min.css" rel="stylesheet">
    <link href="sbadmin2/vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">

    <!-- jQuery -->
    <script src="sbadmin2/vendor/jquery/jquery.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="sbadmin2/vendor/bootstrap/js/bootstrap.min.js"></script>


    <title>Album Page</title>
</head>
<body>
<!---->

<div id="wrapper">

    <!-- Navigation -->
    <nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
        <div class="navbar-header">
            <a class="navbar-brand" href="index.jsp">WS Project</a>
        </div>
        <!-- /.navbar-header -->

        <ul class="nav navbar-top-links navbar-right">
        </ul>

        <div class="navbar-default sidebar" role="navigation">
            <div class="sidebar-nav navbar-collapse">
                <ul class="nav" id="side-menu">
                    <li class="sidebar-search">
                        <form action="./Search" method="POST" >
                            <div class="input-group custom-search-form">
                                <input type="text" class="form-control" placeholder="Search..." name="query">
                                <span class="input-group-btn">
                    <button class="btn btn-default" type="submit" name="action" value="search">
                      <i class="fa fa-search"></i>
                    </button>
                  </span>
                            </div>
                            <div align="right">
                                <select name="searchType">
                                    <option value="semantic">Semantic</option>
                                    <option value="keyword">Keyword Based</option>
                                </select>
                            </div>
                        </form>
                        <!-- /input-group -->
                    </li>
                    <li>
                        <!-- <a href="index.html"><i class="fa fa-dashboard fa-fw"></i> Dashboard</a>-->
                    </li>
                    <li>
                        <a href="Artist">Artists<span class="fa arrow"></span></a>
                    </li>
                    <li>
                        <a href="Album">Albums<span class="fa arrow"></span></a>
                    </li>
                    <!-- /.nav-second-level -->
                </ul>
            </div>
            <!-- /.sidebar-collapse -->
        </div>
        <!-- /.navbar-static-side -->
    </nav>

    <!-- Page Content -->
    <div id="page-wrapper">
        <div class="container-fluid">
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">${result[0]}</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            <c:choose>
                <c:when test="${result[2] != \"None\"}">
                    <img src="${result[2]}" alt="Album Cover" class="img-thumbnail">
                </c:when>
                <c:otherwise>
                    <img src="http://kidsinanewgroove.org/wp-content/uploads/2015/05/guitar-2-174x174.png" alt="Album Cover" class="img-thumbnail">
                </c:otherwise>
            </c:choose>
            <br/><br/><label>Artist: </label> <a href="/ArtistPage?name=${result[1].replace(" ","+")}&id=${result[5]}"> ${result[1]} </a> <br/>
            <label>Date: </label> ${result[6]} <br/>
            <label>Description: </label> ${result[3]} <br/>
            <br/><label>See more at: </label> <a href="${result[4]}">LastFM Page</a> <br/>
            <br/><label>Tracks: </label> <br/>
            <c:forEach items="${number}" var="item" varStatus="status">
                <strong>${number[status.index]}</strong> <a href="/TrackPage?name=${title[status.index].replace(" ","+")}&id=${tracksID[status.index]}"> ${title[status.index]} </a> ${length[status.index]} <br />
            </c:forEach>

            <div>
                <h3>Recommended Albuns</h3> <br/>
                <c:forEach items="${recommendedTitle}" var="item" varStatus="status">
                    <div class="col-lg-5 well">
                        <c:choose>
                            <c:when test="${recommendedPic[status.index] != \"None\"}">
                                <img src="${recommendedPic[status.index]}" alt="Recommended Album Cover" class="img-thumbnail">
                            </c:when>
                            <c:otherwise>
                                <img src="http://kidsinanewgroove.org/wp-content/uploads/2015/05/guitar-2-174x174.png" alt="Recommended Album Cover" class="img-thumbnail">
                            </c:otherwise>
                        </c:choose>
                        <br/><a href="/AlbumPage?name=${recommendedTitle[status.index].replace(" ", "+")}&id=${recommendedID[status.index]}"> ${recommendedTitle[status.index]} </a>
                    </div>
                </c:forEach>
            </div>

        </div>
        <!-- /.container-fluid -->
    </div>
    <!-- /#page-wrapper -->

</div>
</body>
</html>
