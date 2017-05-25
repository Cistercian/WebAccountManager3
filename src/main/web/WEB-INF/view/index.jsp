<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">

<jsp:include page="/WEB-INF/view/tags/header-template.jsp"></jsp:include>
<%--Диаиграмма--%>
<spring:url value="/resources/js/Chart.min.js" var="chartmin"/>
<script src="${chartmin}"></script>

<!-- обращение к контроллеру -->
<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        //formatting sums
        $('#sumIncome').append('<strong>' + numberToString('${sumIncome}') + "</strong> руб.");
        $('#sumExpense').append('<strong>' + numberToString('${sumExpense}') + "</strong> руб.");

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

                //удаляем текущие данные
                removeStatistics();

                var maxIncomeSum = 0;
                var maxExpenseSum = 0;
                var totalIncomeSum = 0;
                var totalExpenseSum = 0;
                var idBarsElem;

                //данные для стилей прогресс баров
                var styles = ['success', 'info', 'warning', 'danger'];
                var curNumStyle = -1;

                $.each(data, function(index, barData) {
                    var type = barData.type;
                    var name = barData.name;
                    var id = barData.id;
                    var sum = Math.abs(barData.sum);

                    <!-- меняем цвет баров -->
                    curNumStyle = curNumStyle < 4 ? curNumStyle + 1 : 0;

                    if (type == 'CategoryIncome') {
                        idBarsElem = 'dropDownCategoryBarsIncome';
                        maxIncomeSum = maxIncomeSum == 0 ? sum : maxIncomeSum;
                        normalSum = sum * 100 / maxIncomeSum;
                        totalIncomeSum += sum;
                    } else {
                        idBarsElem = 'dropDownCategoryBarsExpense';
                        maxExpenseSum = maxExpenseSum == 0 ? sum : maxExpenseSum;
                        normalSum = sum * 100 / maxExpenseSum;
                        totalExpenseSum += sum;
                    }

                    $('#' + idBarsElem).append(
                            "<li class='list-unstyled'>" +
                            "<a href='javascript:drawBarsByParentId(false, \"" + id + "\",\"" + after + "\",\"" + before + "\");'>" +
                            "<div>" +
                            "<h4 class='needToFormat'><strong id='categoryBarName" + id + "' value='" + name + "'>" +
                            name +
                            "</strong>" +
                            "<span id='categoryBarSum" + id + "' class='pull-right text-muted' " +
                            "value='" + sum + "'>" +
                            numberToString(sum) + " руб." +
                            "</span></h4>" +
                            "<div class='progress progress-striped active'> " +
                            "<div class='progress-bar progress-bar-" + styles[curNumStyle] + "' role='progressbar' " +
                            "aria-valuenow='" + sum + "' aria-valuemin='0' aria-valuemax='100' " +
                            "style='width: " + normalSum + "%' value='" + name + "'>" +
                            "<span class='sr-only'>" + sum + "</span>" +
                            "</div>" +
                            "</div>" +
                            "</div>" +
                            "</a>" +
                            "</li>"
                    );
                });
                if (totalIncomeSum == 0)
                    $('#dropDownCategoryBarsIncome').hide();
                else
                    $('#dropDownCategoryBarsIncome').show();
                if (totalExpenseSum == 0)
                    $('#dropDownCategoryBarsExpense').hide();
                else
                    $('#dropDownCategoryBarsExpense').show();

                $('#textTotalIncome').append("<span class='pull-right'><strong>" + numberToString(totalIncomeSum.toFixed(2)) + "</strong> руб.</span>");
                $('#textTotalExpense').append("<span class='pull-right'><strong>" + numberToString(totalExpenseSum.toFixed(2)) + "</strong> руб.</span>");

                //рисуем диаграмму
                drawChartOfTypes("<spring:message code='label.index.chart.income.label' />=" + totalIncomeSum + "," +
                        "<spring:message code='label.index.chart.expense.label' />=" + totalExpenseSum + "",
                        "typeChart");

                formatTooLongText();
            }
        });
    }
    function removeStatistics(){
        $('#textTotalIncome').empty();
        $('#textTotalExpense').empty();

        $('#dropDownCategoryBarsIncome').empty();
        $('#dropDownCategoryBarsExpense').empty();

        $('#dropDownCategoryBarsIncome').append("<h3><spring:message code="label.index.categoryBars.income" /></h3>");
        $('#dropDownCategoryBarsExpense').append("<h3><spring:message code="label.index.categoryBars.expense" /></h3>");
    }
</script>
<!-- modal panel -->
<div id="modal" class="modal fade " tabindex="-1" role="dialog" aria-labelledby="modallabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content wam-radius">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h3 id="mopdalTitle" class="modal-title"><spring:message code="label.index.modal.title" /></h3>
                <div id="modalHeader" class="modal-title">
                </div>
            </div>
            <div id="modalBody" class="modal-body">
                Loading data...
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<div class="content container-fluid wam-radius wam-min-height-0 wow fadeInDown wam-not-padding-xs" data-wow-duration="1000ms" data-wow-delay="300ms">
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
                    <i class="fa fa-angle-left"></i>
                </a>

                <a class=" right carousel-control hidden-xs" href="#carousel-slider" data-slide="next">
                    <i class="fa fa-angle-right"></i>
                </a>
            </div> <!--/#carousel-slider-->
        </div><!--/#about-slider-->
    </div>
    <div class="container-fluid wam-not-padding-xs">
        <div class="col-md-12 ">
            <h2 class=""><spring:message code="label.index.title" /></h2>
            <hr>
            <p class="lead text-justify"><spring:message code="label.index.details" /></p>
        </div>
        <div class="col-xs-12 col-md-12 ">
            <div id="dataRow" class="login-panel panel panel-default wam-not-padding-xs ">
                <div class="panel-heading">
                    <div class="row">
                        <div class="col-xs-12 col-md-12 ">
                            <h3><spring:message code="label.index.chartIncomeExpense.title" /></h3>
                        </div>
                    </div>
                </div>
                <div class="panel-body">
                    <div class="col-xs-12 col-md-12 wam-not-padding wam-margin-left-2-1 wam-margin-right-2">
                        <div class="col-xs-12 col-md-6">
                            <canvas id="typeChart"></canvas>
                        </div>
                        <div class="col-xs-12 col-md-6 ">
                            <div class="row">
                                <div class="col-xs-6 col-md-6 wam-not-padding-xs">
                                    <h3><span class="glyphicon glyphicon-stop wam-color-income"></span><spring:message code="label.index.total.income" /></h3>
                                </div>
                                <div class="col-xs-6 col-md-6 wam-not-padding-xs">
                                    <h3 id="textTotalIncome">
                                        <c:if test="${not empty sumIncome}">
                                            <span id="sumIncome" class="pull-right "></span>
                                        </c:if>
                                    </h3>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-xs-6 col-md-6 wam-not-padding-xs">
                                    <h3><span class="glyphicon glyphicon-stop wam-color-expense"></span><spring:message code="label.index.total.expense" /></h3>
                                </div>
                                <div class="col-xs-6 col-md-6 wam-not-padding-xs">
                                    <h3 id="textTotalExpense">
                                        <c:if test="${not empty sumExpense}">
                                            <span id="sumExpense" class="pull-right "></span>
                                        </c:if>
                                    </h3>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                                    <h3><spring:message code="label.index.total.date" />
                                        <c:if test="${not empty curDate}">
                                            <span class="wam-margin-left-3">${curDate}</span>
                                        </c:if>
                                    </h3>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>


    <div class="container-fluid">
        <div class="row">
            <div class="col-xs-12 col-md-8 ">
                <h2><spring:message code="label.index.categories.title" /></h2>
            </div>
            <div class="col-xs-12 col-md-4 dropup ">
                <spring:message code="label.index.categories.period.default" var="periodDefault" />
                <button id="btnPeriod" class="btn-default btn-lg btn-block dropdown-toggle"
                        data-toggle="dropdown" value="month">
                    ${periodDefault}
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
                    <button id="btnRefresh" class="btn-default btn-lg btn-block"
                            onclick="javascript:refreshCategories()">
                        <spring:message code="label.index.refresh" />
                    </button>
                </div>
            </div>
            <div class="col-xs-12 col-md-12 " id="categoryBars">
                <div id="dropDownCategoryBarsIncome">

                    <c:set var="styles" value="${['success', 'info', 'warning', 'danger']}" scope="page" />
                    <c:set var="step" value="-1" scope="page"/>
                    <c:set var="hasCategories" value="false" scope="page"/>

                    <c:forEach items="${categories}" var="list">
                        <c:if test="${list.getType() == 'CategoryIncome'}">

                            <c:if test="${hasCategories == 'false'}">
                                <c:set var="hasCategories" value="true" scope="page"/>
                                <h3><spring:message code="label.index.categoryBars.income" /></h3>
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
                                <a href="javascript:drawBarsByParentId(false, '${classId}', '${afterMonth}', '${curDate}')">
                                    <div>
                                        <h4 class="needToFormat"><strong id="categoryBarName${classId}" value="${className}">
                                                ${className}
                                        </strong>
                                            <span id="categoryBarSum${classId}" class="pull-right text-muted"
                                                  value="${classPrice}">
                                                    ${classPrice} руб.
                                            </span></h4>
                                        <div class="progress progress-striped active">
                                            <div class="progress-bar progress-bar-${styles[step]}" role="progressbar"
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
                </div>
                <div id="dropDownCategoryBarsExpense">

                    <c:set var="styles" value="${['success', 'info', 'warning', 'danger']}" scope="page" />
                    <c:set var="step" value="-1" scope="page"/>
                    <c:set var="hasCategories" value="false" scope="page"/>

                    <c:forEach items="${categories}" var="list">
                        <c:if test="${list.getType() == 'CategoryExpense'}">

                            <c:if test="${hasCategories == 'false'}">
                                <c:set var="hasCategories" value="true" scope="page"/>
                                <h3><spring:message code="label.index.categoryBars.expense" /></h3>
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
                                <a href="javascript:drawBarsByParentId(false, '${classId}', '${afterMonth}', '${curDate}')">
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
                                            <div class="progress-bar progress-bar-${styles[step]}" role="progressbar"
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
                </div>
            </div>
        </div><!--/.row-->
    </div>
</div>
</body>
</html>