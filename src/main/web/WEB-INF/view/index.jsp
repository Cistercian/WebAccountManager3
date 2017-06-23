<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page import="ru.hd.olaf.util.FormatUtil"%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">
<head>
    <meta charset="utf-8">
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>WebAccountManager</title>

    <!-- css -->
    <spring:url value="/resources/css/bootstrap.min.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <spring:url value="/resources/css/animate.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <spring:url value="/resources/css/datepicker/bootstrap-datetimepicker.min.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <spring:url value="/resources/css/style.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <link href="https://fonts.googleapis.com/css?family=Roboto+Slab" rel="stylesheet">

    <!-- js -->
    <spring:url value="/resources/js/jquery.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/bootstrap.min.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/wow.min.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/jquery.easing.min.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/jquery.isotope.min.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/functions.js" var="js"/>
    <script src="${js}"></script>

    <!--Datepicker-->
    <spring:url value="/resources/js/datepicker/bootstrap-datepicker.min.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/datepicker/bootstrap-datepicker.ru.min.js" var="js"/>
    <script src="${js}"></script>

    <!--waitingDialog-->
    <spring:url value="/resources/js/waitingDialog.js" var="js"/>
    <script src="${js}"></script>

    <!--Диаграмма-->
    <spring:url value="/resources/js/Chart.min.js" var="js"/>
    <script src="${js}"></script>

    <!--custon functions-->
    <spring:url value="/resources/js/web.account.functions.js" var="js"/>
    <script src="${js}"></script>

</head>
<body>

<!-- навигационная панель и модальное окно -->
<jsp:include page="/WEB-INF/view/tags/nav-panel.jsp"></jsp:include>

<spring:message code="label.index.emptyData" var="emptyData"/>
<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        //formatting sums

        $('[id ^= "categoryBarSum"]').each(function () {
            $(this).text(numberToString(Math.abs($(this).attr('value'))) + " руб.");
        });

        getAlerts();

        drawChartOfTypes("<spring:message code='label.index.chart.income.label' />=${sumIncome},<spring:message code='label.index.chart.expense.label' />=${sumExpense}", "typeChart");

        //datepicker
        $('#afterDate').datepicker({
            language: 'ru',
            autoclose: true,
            todayHighlight: true
        });
        $('#beforeDate').datepicker({
            language: 'ru',
            autoclose: true,
            todayHighlight: true
        });

        formatTooLongText();
    });

    function selectPeriod(hasDates, after, before, idElem){

        var elemAfter = $('#afterDate');
        var elemBefore = $('#beforeDate');

        elemAfter.val(after);
        elemBefore.val(before);
        $('#btnPeriod').text($('#' + idElem).text());
        $('#btnPeriod').append('<span class="caret"></span>');
        var distance = document.body.clientHeight % $(document).height();

        if (!hasDates) {
            $('#periodHiddenDivs').show();

            var destination = $('#btnRefresh');

            $('html, body').animate({
                scrollTop: $(destination).offset().top - distance + 55
            }, 2000, 'easeInOutExpo');
        } else {
            var destination = $('#dataRow');
            $('html, body').animate({
                scrollTop: $(destination).offset().top //- distance + 55
            }, 400, 'easeInOutExpo');

            $('#periodHiddenDivs').hide();

            drawParentsCategories(after, before);
        }
    }
    function refreshCategories(){
        after = $('#afterDate').val();
        before = $('#beforeDate').val();

        if (after != '' && before != '')
            drawParentsCategories(after, before);
    }
    function drawParentsCategories(after, before){
        //защита от дурака
        after = after.replace(/\./g, '-');
        before = before.replace(/\./g, '-');

        $.ajax({
            url: '/getCategoriesByDate',
            type: "GET",
            data: {
                'after' : after,
                'before' : before
            },
            dataType: 'json',
            beforeSend: function(){
                displayLoader();
            },
            success: function (data) {
                hideLoader();

                refreshBars(data, after, before);

                //рисуем диаграмму
                /*drawChartOfTypes("<spring:message code='label.index.chart.income.label' />=" + totalIncomeSum + "," +
                 "<spring:message code='label.index.chart.expense.label' />=" + totalExpenseSum + "",
                 "typeChart");*/

                formatTooLongText();
            }
        });
    }
</script>

<div class="content container-fluid wam-radius wam-min-height-0 wam-not-padding-xs">
    <div class="slider wam-top-radius">
        <div id="about-slider" >
            <div id="carousel-slider" class="carousel slide" data-ride="carousel">
                <!-- Indicators -->
                <ol class="carousel-indicators visible-xs">
                    <li data-target="#carousel-slider" data-slide-to="0" class="active"></li>
                    <li data-target="#carousel-slider" data-slide-to="1"></li>
                    <li data-target="#carousel-slider" data-slide-to="2"></li>
                </ol>

                <div class="carousel-inner">
                    <div class="item active">
                        <img src="/resources/img/carousel_1.jpg" class="img-responsive wam-top-radius" alt="">
                    </div>
                    <div class="item">
                        <img src="/resources/img/carousel_2.jpg" class="img-responsive wam-top-radius" alt="">
                    </div>
                    <div class="item">
                        <img src="/resources/img/carousel_3.jpg" class="img-responsive wam-top-radius" alt="">
                    </div>
                </div>

                <a class="left carousel-control hidden-xs" href="#carousel-slider" data-slide="prev">
                    <i class="fa fa-angle-left">
                        <img src="/resources/img/left.png" class="img-responsive wam-top-radius" alt="">
                    </i>
                </a>

                <a class=" right carousel-control hidden-xs" href="#carousel-slider" data-slide="next">
                    <i class="fa fa-angle-right">
                        <img src="/resources/img/right.png" class="img-responsive wam-top-radius" alt="">
                    </i>
                </a>
            </div> <!--/#carousel-slider-->
        </div><!--/#about-slider-->
    </div>

    <div class="container-fluid ">
        <div class="row">
            <div class="col-md-12 ">
                <h2 class=""><spring:message code="label.index.title" /></h2>
                <hr>
                <p class="lead text-justify"><spring:message code="label.index.details" /></p>
            </div>
            <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                <div id="dataRow" class="panel panel-default wam-margin-left-1 wam-margin-right-1">
                    <div class="panel-heading">
                        <div class="row">
                            <div class="col-xs-12 col-md-12 ">
                                <h3 class="wam-margin-bottom-0 wam-margin-top-0"><spring:message code="label.index.chartIncomeExpense.title" /></h3>
                            </div>
                        </div>
                    </div>
                    <div class="panel-body ">
                        <c:choose>
                            <c:when test="${not empty pageContext.request.userPrincipal}">
                                <div class="col-xs-12 col-md-12 wam-not-padding wam-margin-left-1 wam-margin-right-1 ">
                                    <div class="col-xs-12 col-md-6 ">
                                        <canvas id="typeChart"></canvas>
                                    </div>
                                    <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                                        <div class="row">
                                            <div class="col-xs-6 col-md-6 ">
                                                <h3 class="wam-not-padding-xs wam-margin-bottom-1"><span class="glyphicon glyphicon-stop wam-color-income"></span><spring:message code="label.index.total.income" /></h3>
                                            </div>
                                            <div class="col-xs-6 col-md-6 ">
                                                <h3 id="textTotalIncome" class="wam-not-padding-xs wam-margin-bottom-1">
                                                    <c:if test="${not empty sumIncome}">
														<span class="pull-right">
															<strong id="sumIncome" >
                                                                    ${FormatUtil.numberToString(sumIncome)}
                                                            </strong>
															руб.
														</span>
                                                    </c:if>
                                                </h3>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-xs-6 col-md-6 ">
                                                <h3 class="wam-not-padding-xs"><span class="glyphicon glyphicon-stop wam-color-expense"></span><spring:message code="label.index.total.expense" /></h3>
                                            </div>
                                            <div class="col-xs-6 col-md-6 " class="wam-not-padding-xs">
                                                <h3 id="textTotalExpense" class="wam-not-padding-xs">
                                                    <c:if test="${not empty sumExpense}">
                                                        <span class="pull-right">
															<strong id="sumExpense" >
                                                                    ${FormatUtil.numberToString(sumExpense)}
                                                            </strong>
															руб.
														</span>
                                                    </c:if>
                                                </h3>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-xs-12 wam-margin-top-1 wam-margin-bottom-0-1">
                                                <img src="/resources/img/line.png" class="img-responsive wam-width-1 wam-height-1 pull-right" alt="">
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-xs-6 col-md-6 ">
                                                <h3 class="wam-margin-top-0 ">
                                                    ИТОГО
                                                </h3>
                                            </div>
                                            <div class="col-xs-6 col-md-6 ">
                                                <h3 id="textTotalExpense" class="wam-margin-top-0">
													<span class="pull-right">
														<strong id="sumTotal" >
                                                                ${FormatUtil.numberToString(sumIncome - sumExpense)}
                                                        </strong>
														руб.
													</span>
                                                </h3>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-xs-12 col-md-12 ">
                                                <h3><spring:message code="label.index.total.date" />
                                                    <c:if test="${not empty curDate}">
                                                        <span class="wam-margin-left-3">${curDate}</span>
                                                    </c:if>
                                                </h3>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="col-xs-12 wam-margin-top-2">
                                    <jsp:include page="/WEB-INF/view/tags/anon-tag.jsp"></jsp:include>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <c:if test="${not empty pageContext.request.userPrincipal}">
        <div class="container-fluid wam-margin-top-0">
            <div class="row">
                <div class="col-xs-12 col-md-8 ">
                    <h3 class="wam-margin-top-0"><spring:message code="label.index.categories.title" /></h3>
                </div>
                <div class="col-xs-12 col-md-4 dropup ">
                    <spring:message code="label.index.categories.period.default" var="periodDefault" />
                    <button id="btnPeriod" class="btn-default btn-lg btn-block dropdown-toggle wam-margin-bottom-2"
                            data-toggle="dropdown" value="month">
                            ${periodDefault}
                        <span class="caret"></span>
                    </button>
                    <ul id="dropdownPeriods" class="dropdown-menu dropup dropdown-period wam-text-size-1">
                        <li><a id="day" onclick="selectPeriod(true, '${curDate}', '${curDate}', 'day');">
                            <spring:message code="label.index.categories.period.today" /></a>
                        </li>
                        <li><a id="week" onclick="selectPeriod(true, '${afterWeek}', '${curDate}', 'week');">
                            <spring:message code="label.index.categories.period.week" /></a>
                        </li>
                        <li><a id="month" onclick="selectPeriod(true, '${afterMonth}', '${curDate}', 'month');">
                            <spring:message code="label.index.categories.period.month" /></a>
                        </li>
                        <li><a id="allTime" onclick="selectPeriod(true, '${afterAllTime}', '${curDate}', 'allTime');">
                            <spring:message code="label.index.categories.period.all" /></a>
                        </li>
                        <li><a id="custom" onclick="selectPeriod(false, '${afterMonth}', '${curDate}', 'custom');">
                            <spring:message code="label.index.categories.period.custom" /></a>
                        </li>
                    </ul>
                </div>
                <div id="periodHiddenDivs" style="display: none;">
                    <div class="col-xs-12 col-md-8 col-md-offset-4">
                        <h4><spring:message code="label.index.categories.period" /></h4>
                    </div>

                    <div class='col-xs-12 col-md-4 col-md-offset-4'>
                        <div class="form-group dropup">

                            <div class='input-group date' >
                                <input id='afterDate' type='text' class="form-control" readonly style="cursor: pointer;"/>
									<span class="input-group-addon">
										<span class="glyphicon glyphicon-calendar"></span>
									</span>
                            </div>
                        </div>
                    </div>
                    <div class='col-xs-12 col-md-4'>
                        <div class="form-group">
                            <div class='input-group date' >
                                <input id='beforeDate' type='text' class="form-control" readonly style="cursor: pointer;"/>
									<span class="input-group-addon">
										<span class="glyphicon glyphicon-calendar"></span>
									</span>
                            </div>
                        </div>
                    </div>
                    <div class="col-xs-12 col-xs-12 col-md-4 col-md-offset-8">
                        <button id="btnRefresh" class="btn-default btn-lg btn-block wam-btn-2"
                                onclick="javascript:refreshCategories()">
                            <spring:message code="label.index.refresh" />
                        </button>
                    </div>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding-xs" id="categoryBars">
                    <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 ">
                        <div class="panel-heading ">
                            <h3 class="wam-margin-bottom-0 wam-margin-top-0"><spring:message code="label.index.categoryBars.income" /></h3>
                        </div>
                        <div class="wam-not-padding panel-body">
                            <div id="dropDownCategoryBarsIncome">

                                <c:set var="styles" value="${['success', 'info', 'warning', 'danger']}" scope="page" />
                                <c:set var="step" value="-1" scope="page"/>
                                <c:set var="incomeHasData" value="false" scope="page"/>

                                <c:forEach items="${categories}" var="list">
                                    <c:if test="${list.getType() == 'CategoryIncome'}">

                                        <c:if test="${incomeHasData == 'false'}">
                                            <c:set var="incomeHasData" value="true" scope="page"/>
                                        </c:if>

                                        <c:set var="classId" value="${list.getId()}" />
                                        <c:set var="className" value="${list.getName()}" />
                                        <c:set var="classPrice" value="${list.getSum()}" />
                                        <c:set var="normalPrice" value="${classPrice * 100 / maxIncome}" />

                                        <c:choose>
                                            <c:when test="${step == 3}">
                                                <c:set var="step" value="0" scope="page"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="step" value="${step + 1}" scope="page"/>
                                            </c:otherwise>
                                        </c:choose>

                                        <li class="list-unstyled">
                                            <a href="javascript:drawBarsByParentId(false, '${classId}', '${afterMonth}', '${curDate}', false)">
                                                <div>
                                                    <h4 class="needToFormat"><strong id="categoryBarName${classId}" value="${className}">
                                                            ${className}
                                                    </strong>
														<span id="categoryBarSum${classId}" class="pull-right text-muted"
                                                              value="${classPrice}">
																${classPrice} руб.
														</span></h4>
                                                    <div class="progress progress-striped active">
                                                        <div class="progress-bar progress-bar-${styles[0]}" role="progressbar"
                                                             aria-valuenow="${classPrice}" aria-valuemin="0" aria-valuemax="100"
                                                             style="width: ${normalPrice}%" value="${className}">
                                                            <span class="sr-only">${classPrice}</span>
                                                        </div>
                                                    </div>
                                                </div>
                                            </a>
                                        </li>
                                    </c:if>
                                </c:forEach>
                                <c:if test="${incomeHasData == 'false'}">
                                    <div class="col-xs-12 col-md-12">
                                        <h4><span class="text-muted">${emptyData}</span></h4>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                    <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 ">
                        <div class="panel-heading ">
                            <h3 class="wam-margin-bottom-0 wam-margin-top-0"><spring:message code="label.index.categoryBars.expense" /></h3>
                        </div>
                        <div class="wam-not-padding panel-body ">
                            <div id="dropDownCategoryBarsExpense">
                                <c:set var="expenseHasData" value="false" scope="page"/>
                                <c:set var="styles" value="${['success', 'info', 'warning', 'danger']}" scope="page" />
                                <c:set var="step" value="-1" scope="page"/>

                                <c:forEach items="${categories}" var="list">
                                    <c:if test="${list.getType() == 'CategoryExpense'}">

                                        <c:if test="${expenseHasData == 'false'}">
                                            <c:set var="expenseHasData" value="true" scope="page"/>
                                        </c:if>

                                        <c:set var="classId" value="${list.getId()}" />
                                        <c:set var="className" value="${list.getName()}" />
                                        <c:set var="classPrice" value="${list.getSum()}" />
                                        <c:set var="normalPrice" value="${classPrice * 100 / maxExpense}" />

                                        <c:choose>
                                            <c:when test="${step == 3}">
                                                <c:set var="step" value="0" scope="page"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="step" value="${step + 1}" scope="page"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <li class="list-unstyled">
                                            <a href="javascript:drawBarsByParentId(false, '${classId}', '${afterMonth}', '${curDate}', false)">
                                                <div>
                                                    <h4 class="needToFormat"><strong id="categoryBarName${classId}" value="${className}">
                                                            ${className}
                                                    </strong>
														<span id="categoryBarSum${classId}" class="pull-right text-muted"
                                                              value="${classPrice}">
																${classPrice} руб.
														</span>
                                                    </h4>
                                                    <div class="progress progress-striped active">
                                                        <div class="progress-bar progress-bar-${styles[2]}" role="progressbar"
                                                             aria-valuenow="${classPrice}" aria-valuemin="0" aria-valuemax="100"
                                                             style="width: ${normalPrice}%" value="${className}">
                                                            <span class="sr-only">${classPrice}</span>
                                                        </div>
                                                    </div>
                                                </div>
                                            </a>
                                        </li>
                                    </c:if>
                                </c:forEach>
                                <c:if test="${expenseHasData == 'false'}">
                                    <div class="col-xs-12 col-md-12">
                                        <h4><span class="text-muted">${emptyData}</span></h4>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div><!--/.row-->
        </div>
    </c:if>
</div>
</body>
</html>