<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">
<head>
    <meta charset="utf-8">
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>WebAccountManager</title>

    <!-- Bootstrap -->
    <!-- Spring injections -->
    <spring:url value="resources/css/bootstrap.min.css" var="bootstrapmin"/>
    <spring:url value="resources/css/font-awesome.min.css" var="fontawesomemin"/>
    <spring:url value="resources/css/font-awesome.css" var="fontawesome"/>
    <spring:url value="resources/css/animate.css" var="animate"/>
    <spring:url value="resources/css/style.css" var="style"/>

    <link rel="stylesheet" href="${bootstrapmin}">
    <link rel="stylesheet" href="${fontawesomemin}">
    <link rel="stylesheet" href="${fontawesome}">
    <link rel="stylesheet" href="${animate}">
    <link rel="stylesheet" href="${style}">
    <!-- =======================================================
        Theme Name: Anyar
        Theme URL: https://bootstrapmade.com/anyar-free-multipurpose-one-page-bootstrap-theme/
        Author: BootstrapMade
        Author URL: https://bootstrapmade.com
    ======================================================= -->

    <!-- Spring injections -->
    <spring:url value="/resources/js/jquery.js" var="jquery"/>
    <spring:url value="/resources/js/bootstrap.min.js" var="bootstrapmin"/>
    <spring:url value="/resources/js/wow.min.js" var="wowmin"/>
    <spring:url value="/resources/js/jquery.easing.min.js" var="jqueryeasingmin"/>
    <spring:url value="/resources/js/jquery.isotope.min.js" var="jqueryisotopemin"/>
    <spring:url value="/resources/js/functions.js" var="functions"/>

    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="${jquery}"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="${bootstrapmin}"></script>
    <script src="${wowmin}"></script>
    <script src="${jqueryeasingmin}"></script>
    <script src="${jqueryisotopemin}"></script>
    <script src="${functions}"></script>
    <script src="https://maps.google.com/maps/api/js?sensor=true"></script>

    <!-- тестовое обращение к контроллеру -->
    <script language="javascript" type="text/javascript">
        $(document).ready(function () {
            $.ajax({
                url: './getSumByCategory',
                success: function (data) {
                    var styles = ['success', 'info', 'warning', 'danger'];
                    var curNumStyle = -1;
                    var maxPrice = 0;

                    $.each(data, function (index, value) {
                        <!-- нормализуем суммы -->
                        if (maxPrice == 0) maxPrice = index;
                        normalPrice = index * 100 / maxPrice;
                        <!-- меняем цвет баров -->
                        curNumStyle = curNumStyle < 4 ? curNumStyle + 1 : 0;

                        $('#categoriesProgressBar').append(
                                "\<div class=\"progress progress-striped active\"\> " +
                                "\<div class=\"progress-bar progress-bar-" + styles[curNumStyle] + "\" role=\"progressbar\" " +
                                "aria-valuenow=\"" + index + "\"" +
                                " aria - valuemin =\"0\" aria-valuemax=\"100\" style=\"width: " + normalPrice + "%\"\> " +
                                value +
                                "\<\/ div \> " +
                                "\<\/ div \> ");
                    });
                }
            });
        });
    </script>

</head>
<body>
<header>
    <nav class="navbar navbar-default navbar-fixed-top" role="navigation">
        <div class="navigation">
            <div class="container">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
                            data-target=".navbar-collapse.collapse">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <div class="navbar-brand">
                        <a href="index.html"><h1>Web Account Manager</h1></a>
                    </div>
                </div>

                <div class="navbar-collapse collapse">
                    <div class="menu">
                        <ul class="nav nav-tabs" role="tablist">
                            <li role="presentation"><a href="#home" class="active">Home</a></li>
                            <li role="presentation"><a href="#about">Statistics</a></li>
                            <li role="presentation"><a href="#services">Edit</a></li>
                            <li role="presentation"><a href="#contact">Contact</a></li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </nav>
</header>

<div id="home">
    <div class="slider">
        <div class="">
            <div id="about-slider">
                <div id="carousel-slider" class="carousel slide" data-ride="carousel">
                    <!-- Indicators -->
                    <ol class="carousel-indicators visible-xs">
                        <li data-target="#carousel-slider" data-slide-to="0" class="active"></li>
                        <li data-target="#carousel-slider" data-slide-to="1"></li>
                        <li data-target="#carousel-slider" data-slide-to="2"></li>
                    </ol>

                    <div class="carousel-inner">
                        <div class="item active">
                            <img src="/resources/img/carousel_1.jpg" class="img-responsive" alt="">
                        </div>
                        <div class="item">
                            <img src="/resources/img/carousel_2.jpg" class="img-responsive" alt="">
                        </div>
                        <div class="item">
                            <img src="/resources/img/carousel_3.jpg" class="img-responsive" alt="">
                        </div>
                    </div>

                    <a class="left carousel-control hidden-xs" href="#carousel-slider" data-slide="prev">
                        <i class="fa fa-angle-left"></i>
                    </a>

                    <a class=" right carousel-control hidden-xs" href="#carousel-slider" data-slide="next">
                        <i class="fa fa-angle-right"></i>
                    </a>
                </div> <!--/#carousel-slider-->
            </div><!--/#about-slider-->
        </div>
    </div>
</div>

<section id="about">
    <div class="container">
        <div class="center">
            <div class="col-md-6 col-md-offset-3">
                <h2>Statistics</h2>
                <hr>
                <p class="lead">Общая статистика</p>
            </div>
        </div>
    </div>

    <div class="container">
        <div class="row">
            <div class="col-sm-6 wow fadeInDown" id="categoriesProgressBar" data-wow-duration="1000ms"
                 data-wow-delay="300ms">
                <%--<div class="progress progress-striped active">--%>
                <%--<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="40"--%>
                <%--aria-valuemin="0" aria-valuemax="100" style="width: 40%">--%>
                <%--category_name1--%>
                <%--</div>--%>
                <%--</div>--%>
            </div><!--/.col-sm-6-->
            <div class="col-sm-6 wow fadeInDown" data-wow-duration="1000ms" data-wow-delay="600ms">
                <c:if test="${not empty resultObject}">
                    Result:
                    <c:if test="${resultObject !='true' and resultObject != 'false'}">
                        <p>${resultObject}</p>
                    </c:if>
                </c:if>
            </div>
        </div><!--/.row-->
    </div><!--/.container-->
</section><!--/#about-->

<div id="services">
    <div class="container">
        <div class="center">
            <div class="col-md-6 col-md-offset-3">
                <h2>Edit</h2>
                <hr>
                <p class="lead">Слайд редактирования сущностей БД</p>
            </div>
        </div>
    </div>
    <div class="container">
        <div class="text-center">
            <div class="col-md-3 wow fadeInDown" data-wow-duration="1000ms" data-wow-delay="300ms">
                <img src="/resources/img/services/services1.png">
                <h3>Fully Responsive</h3>
            </div>

            <%--<form role="form" class="contactForm wow fadeInDown"--%>
                  <%--data-wow-duration="1000ms" data-wow-delay="300ms" modelAttribute="amounts">--%>
                <div class="col-md-6 col-sm-6 col-xs-12 left">
                    <div class="form-group">
                        <div class="btn-group">
                            <button class="btn btn-large dropdown-toggle" data-toggle="dropdown">Amount Category<span
                                    class="caret"></span></button>
                            <ul class="dropdown-menu">
                                <li><h5>Category 1</h5></li>
                                <li><a href="#">category2</a></li>
                                <li><a href="#">category3</a></li>
                                <li class="divider"></li>
                                <li><a href="#">create new</a></li>
                            </ul>
                        </div>
                    </div>

                    <div class="form-group">
                        <input type="number" name="categoryId" class="form-control form" id="amountCatId"
                               path="categoryId"
                               placeholder="amountCatId" data-rule="minlen:5" data-msg="Please enter at least 5 chars"/>
                        <div class="validation"></div>
                    </div>
                    <div class="form-group">
                        <input type="text" name="name" class="form-control form" id="AmountName"
                               path="name"
                               placeholder="Amount Name" data-rule="minlen:5" data-msg="Please enter at least 5 chars"/>
                        <div class="validation"></div>
                    </div>
                    <div class="form-group">
                        <input type="number" class="form-control" name="price" id="AmountPrice"
                               path="price"
                               placeholder="Amount price" data-rule="number" data-msg="Please enter a valid price"/>
                        <div class="validation"></div>
                    </div>
                    <div class="form-group">
                        <input type="date" class="form-control" name="amountsDate" id="amountsDate"
                               path="amountsDate"
                               placeholder="Amount Name" data-rule="minlen:5" data-msg="Please enter at least 5 chars"/>
                        <div class="validation"></div>
                    </div>
                    <div class="form-group">
                        <input type="text" class="form-control" name="details" id="amountDetails" placeholder="details"
                               path="details"
                               data-rule="minlen:8" data-msg="Please enter at least 8 chars of subject"/>
                        <div class="validation"></div>
                    </div>
                </div>
                <div class="col-md-6 col-sm-6 col-xs-12 left">
                    <!-- Button -->
                    <button type="submit" id="submitAmmount" name="submitAmmount"
                            class="btn btn-large" onclick="Save();return false;">CREATE
                    </button>

                    <script language="javascript" type="text/javascript">
                        function Save() {
                            var data = {'categoryId' : document.getElementById('amountCatId').value,
                                    'name' : document.getElementById('AmountName').value,
                                    'price' : document.getElementById('AmountPrice').value,
                                    'amountsDate' : document.getElementById('amountsDate').value,
                                    'details' : document.getElementById('amountDetails').value,
                                    'submitAmmount' : document.getElementById('amountDetails').value};
                            $.ajax({
                                type: "POST",
                                url: './amounts/add',
                                data: data,
                                success: function (data) {
//                                    alert(data);
                                }
                            });
                        };
                    </script>

                </div>
                <div class="col-md-6 col-sm-6 col-xs-12 right">
                    <button type="submit" id="cancelAmount" name="cancelAmount"
                            class="btn btn-large">CANCEL
                    </button>
                </div>
            <%--</form>--%>
        </div>
    </div>


</div>

<div>
</div>
<div class="sub-footer">
    <div class="container">
        <div class="col-md-6 ">
            <div class="copyright text-right">
                &copy; Anyar Theme. All Rights Reserved.
                <div class="credits">
                    <!--
                        All the links in the footer should remain intact.
                        You can delete the links only if you purchased the pro version.
                        Licensing information: https://bootstrapmade.com/license/
                        Purchase the pro version with working PHP/AJAX contact form: https://bootstrapmade.com/buy/?theme=Anyar
                    -->
                    <a href="https://bootstrapmade.com/">Bootstrap Themes</a> by <a
                        href="https://bootstrapmade.com/">BootstrapMade</a>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>