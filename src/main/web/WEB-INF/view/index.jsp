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
        $('#sumIncome').append(numberToString('${sumIncome}') + " руб.");
        $('#sumExpense').append(numberToString('${sumExpense}') + " руб.");

        drawChartOfTypes("<spring:message code='label.index.chart.income.label' />=${sumIncome},<spring:message code='label.index.chart.expense.label' />=${sumExpense}", "typeChart");

        //datepicker
        $('#afterDate').datetimepicker({
            pickTime: false,
            language: 'ru',
        });
        $('#beforeDate').datetimepicker({
            pickTime: false,
            language: 'ru',
        });
    });
    function selectPeriod(hasDates, after, before, idElem){
        var elemAfter = $('#afterDate');
        var elemBefore = $('#beforeDate');

        elemAfter.val(after);
        elemBefore.val(before);
        $('#btnPeriod').text($('#' + idElem).text());

        if (!hasDates) {
            $('#hiddenInputs').show();
            $('#btnRefresh').show();
        } else {
            $('#hiddenInputs').hide();
            $('#btnRefresh').hide();

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
            success: function (data) {
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
                    var sum = barData.sum;

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
                            "<li>" +
                            "<a href='javascript:drawBarsByParentId(false, \"" + id + "\",\"" + after + "\",\"" + before + "\");'>" +
                            "<div>" +
                            "<h4><strong id='categoryBarName" + id + "' value='" + name + "'>" +
                            name +
                            "</strong>" +
                            "<strong id='categoryBarSum" + id + "' class='pull-right text-muted' " +
                            "value='" + sum + "'>" +
                            sum + " руб." +
                            "</strong></h4>" +
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

                $('#textTotalIncome').append("Доходы <strong>" + numberToString(totalIncomeSum) + "</strong> руб.");
                $('#textTotalExpense').append("Расходы <strong>" + numberToString(totalExpenseSum) + "</strong> руб.");

                //рисуем диаграмму
                drawChartOfTypes("<spring:message code='label.index.chart.income.label' />=" + totalIncomeSum + "," +
                        "<spring:message code='label.index.chart.expense.label' />=" + totalExpenseSum + "",
                        "typeChart");
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
<div id="modal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="modallabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
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
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>

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
    <div class="container-fluid">

        <c:if test="${pageContext.request.userPrincipal.name != null}">
            <form id="logoutForm" method="POST" action="${contextPath}/logout">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form>
            <h2>Welcome ${pageContext.request.userPrincipal.name} | <a onclick="document.forms['logoutForm'].submit()">Выйти</a>
            </h2>
        </c:if>
    </div>
    <div class="container-fluid">
        <div class="center">
            <div class="col-md-12">
                <h2><spring:message code="label.index.title" /></h2>
                <hr>
                <p class="lead"><spring:message code="label.index.details" /></p>
            </div>
        </div>
    </div>

    <div class="container-fluid">
        <div class="row">
            <div class="col-sm-6 wow fadeInDown " data-wow-duration="1000ms"
                 data-wow-delay="300ms">
                <h2><spring:message code="label.index.chartIncomeExpense.title" /></h2>
                <canvas id="typeChart" style="height:250px"></canvas>
            </div>
            <div class="col-sm-6 wow fadeInDown " data-wow-duration="1000ms"
                 data-wow-delay="300ms">
                <h2><spring:message code="label.index.total.title" /></h2>
                <h2 id="textTotalIncome"><spring:message code="label.index.total.income" />
                    <c:if test="${not empty sumIncome}">
                        <strong id="sumIncome"></strong>
                    </c:if>
                </h2>
                <h2 id="textTotalExpense"><spring:message code="label.index.total.expense" />
                    <c:if test="${not empty sumExpense}">
                        <strong id="sumExpense"></strong>
                    </c:if>
                </h2>
                <h2><spring:message code="label.index.total.date" />
                    <c:if test="${not empty curDate}">
                        <strong>${curDate}</strong>
                    </c:if>
                </h2>
            </div>
        </div>
    </div>
    <div class="container-fluid">
        <div class="row wow fadeInDown" data-wow-duration="1000ms" data-wow-delay="300ms">
            <div class="col-sm-8">
                <h2><spring:message code="label.index.categories.title" /></h2>
            </div>
            <div class="col-sm-4">
                <spring:message code="label.index.categories.period.default" var="periodDefault" />
                <button id="btnPeriod" class="btn-default btn-lg btn-block dropdown-toggle"
                        data-toggle="dropdown" value="month">
                    ${periodDefault}
                </button>
                <ul id="dropdownPeriods" class="dropdown-menu">
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
                    <li><a id="custom" onclick="selectPeriod(false, null, null, 'custom');">
                        <spring:message code="label.index.categories.period.custom" /></a>
                    </li>
                </ul>
            </div>
            <div class="col-sm-12">

                <div class="col-sm-4 col-sm-offset-4">
                    <button id="btnRefresh" class="btn-default btn-lg btn-block" style="display: none;"
                            onclick="refreshCategories()">
                        <spring:message code="label.index.refresh" />
                    </button>
                </div>
                <div id="hiddenInputs" class="col-sm-4 input-group date" style="display: none;">
                    <input id="afterDate" type="text" class="form-control" name = "after" value="${afterMonth}">
                        <span class="input-group-addon">
                            <i class="glyphicon glyphicon-th"></i>
                        </span>
                    <input id="beforeDate" type="text" class="form-control" name="before" value="${curDate}">
                        <span class="input-group-addon">
                            <i class="glyphicon glyphicon-th"></i>
                        </span>
                </div>

            </div>
            <div class="col-sm-12" id="categoryBars">
                <div id="dropDownCategoryBarsIncome">
                <h3><spring:message code="label.index.categoryBars.income" /></h3>
                <c:set var="styles" value="${['success', 'info', 'warning', 'danger']}" scope="page" />
                <c:set var="step" value="-1" scope="page"/>

                <c:forEach items="${categories}" var="list">
                    <c:if test="${list.getType() == 'CategoryIncome'}">

                        <c:set var="classId" value="${list.getId()}" />
                        <c:set var="className" value="${list.getName()}" />
                        <c:set var="classPrice" value="${list.getSum()}" />
                        <c:set var="normalPrice" value="${classPrice * 100 / maxIncome}" />

                        <c:set var="step" value="${step + 1}" scope="page"/>
                        <li>
                            <a href="javascript:drawBarsByParentId(false, '${classId}', '${afterMonth}', '${curDate}')">
                                <div>
                                    <h4><strong id="categoryBarName${classId}" value="${className}">
                                        ${className}
                                    </strong>
                                    <strong id="categoryBarSum${classId}" class="pull-right text-muted"
                                            value="${classPrice}">
                                            ${classPrice} руб.
                                    </strong></h4>
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
                        <li class='divider'></li>
                    </c:if>
                </c:forEach>
                </div>
                <div id="dropDownCategoryBarsExpense">
                    <h3><spring:message code="label.index.categoryBars.expense" /></h3>
                    <c:set var="styles" value="${['success', 'info', 'warning', 'danger']}" scope="page" />
                    <c:set var="step" value="-1" scope="page"/>

                    <c:forEach items="${categories}" var="list">
                        <c:if test="${list.getType() == 'CategoryExpense'}">

                            <c:set var="classId" value="${list.getId()}" />
                            <c:set var="className" value="${list.getName()}" />
                            <c:set var="classPrice" value="${list.getSum()}" />
                            <c:set var="normalPrice" value="${classPrice * 100 / maxExpense}" />

                            <c:set var="step" value="${step + 1}" scope="page"/>
                            <li>
                                <a href="javascript:drawBarsByParentId(false, '${classId}', '${afterMonth}', '${curDate}')">
                                    <div>
                                        <h4><strong id="categoryBarName${classId}" value="${className}">
                                                ${className}
                                        </strong>
                                        <strong id="categoryBarSum${classId}" class="pull-right text-muted"
                                                value="${classPrice}">
                                                ${classPrice} руб.
                                        </strong>
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
    </div><!--/.container-->

</section><!--/#about-->

</body>
</html>