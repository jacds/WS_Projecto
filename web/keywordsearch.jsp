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


    <title>Search Results</title>
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
                    <h1 class="page-header">Search Results - "${query}"</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            <h3>Artists </h3>
            <c:forEach items="${artists}" var="item" varStatus="status">
                <a href="/ArtistPage?name=${artists[status.index].replace(" ","+")}&id=${artistsID[status.index]}"> ${item} </a> <br />
            </c:forEach>
            <br/><h3 >Albums </h3>
            <c:forEach items="${albums}" var="item" varStatus="status">
                <a href="/AlbumPage?name=${albums[status.index].replace(" ","+")}&id=${albumsID[status.index]}"> ${item} </a> <br />
            </c:forEach>
            <br/><h3>Tracks </h3>
            <c:forEach items="${tracks}" var="item" varStatus="status">
                <a href="/TrackPage?name=${tracks[status.index].replace(" ","+")}&id=${tracksID[status.index]}"> ${item} </a> <br />
            </c:forEach>
        </div>
        <!-- /.container-fluid -->
    </div>
    <!-- /#page-wrapper -->
</div>
</body>
</html>
