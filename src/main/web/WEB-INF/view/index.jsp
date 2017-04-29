<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">

<jsp:include page="/WEB-INF/view/tags/header-template.jsp"></jsp:include>
<%--Диаиграмма--%>
<spring:url value="/resources/js/Chart.min.js" var="chartmin"/>
<script src="${chartmin}"></script>

<%--fullcalendar--%>
<spring:url value="/resources/js/fullcalendar/moment.min.js" var="momentmin"/>
<spring:url value="/resources/js/fullcalendar/fullcalendar.js" var="fullcalendarjs"/>
<spring:url value="/resources/js/fullcalendar/locale-all.js" var="localeall"/>
<script src="${momentmin}"></script>
<script src="${fullcalendarjs}"></script>
<script src="${localeall}"></script>
<spring:url value="/resources/css/fullcalendar/fullcalendar.css" var="fullcalendarcss"/>
<link rel="stylesheet" href="${fullcalendarcss}">


<!-- обращение к контроллеру -->
<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        //formatting sums
        $('#sumIncome').append(numberToString('${sumIncome}'));
        $('#sumExpense').append(numberToString('${sumExpense}'));

        drawChartOfTypes("<spring:message code='label.index.chart.income.label' />=${sumIncome},<spring:message code='label.index.chart.expense.label' />=${sumExpense}", "typeChart");

        //fullcalendar
        drawCalendar();
    });
    function numberToString(number){
        return (number + '').replace(/(\d)(?=(\d\d\d)+([^\d]|$))/g, '$1 ');
    }
    function drawBarsByParentId(categoryId, isChildren) {
        //свитчер - либо показываем, либо стираем детализацию по дочерней категории
        if (($('*').is('#childrenCategoryDetails')) && isChildren) {
            $('#childrenCategoryDetails').remove();
        }
        else {
            $.ajax({
                url: '/getContentByCategoryId',
                type: "GET",
                data: {
                    'categoryId': categoryId,
                    'period' : $('#btnPeriod').val(),
                    'countDays' : $('#countDays').val()
                },
                dataType: 'json',
                success: function (data) {
                    var idBarElem;
                    var idSumElem;
                    var dataClass = 'Category'; //задел на будущее - вдруг придется выводить подитоги по другим данным
                    var categoryName;
                    var idHeaderElem;
                    var tagHeader;
                    var tagClassProgress;
                    var maxSum = 0;

                    if (!isChildren) {
                        ClearModal();
                        idBarElem = 'modalDropDown';
                        idSumElem = 'categoryBarSum' + categoryId;
                        idNameElem = 'categoryBarName' + categoryId;
                        idHeaderElem = 'modalHeader'; //TODO:fix position
                        tagHeader = "h3";
                        tagClassProgress = "";
                        categoryName = $('#' + idNameElem).attr('value');
                    } else {
                        $('#progressBarCategory' + categoryId).append(
                                "<div id='childrenCategoryDetails' class='wow fadeInDown' data-wow-duration='1000ms '" +
                                "data-wow-delay='300ms'>");
                        idBarElem = 'childrenCategoryDetails';
                        idSumElem = 'barSum' + dataClass + categoryId;
                        idNameElem = 'barName' + dataClass + categoryId;
                        idHeaderElem = 'childrenCategoryDetails';
                        tagHeader = "h4";
                        tagClassProgress = "mini";
                        maxSum = $('#' + idSumElem).attr('value');
                        categoryName = "Детализация по категории: " + $('#' + idNameElem).attr('value');
                    }
                    //заголовок
                    $('#' + idHeaderElem).append(
                            "<" + tagHeader + ">" + categoryName + " <a href='./page-category/" + categoryId +
                            "'>(редактировать)</a></" + tagHeader + ">"
                    );

                    //данные для стилей прогресс баров
                    var styles = ['success', 'info', 'warning', 'danger'];
                    var curNumStyle = -1;

                    data.forEach(function (barData, index, data) {
                        var classId = barData.id;
                        var classType = barData.type;
                        var classTitle = "";
                        var className = barData.name;
                        var classSum = barData.sum;

                        <!-- нормализуем суммы -->
                        if (maxSum == 0) maxSum = classSum;
                        normalSum = classSum * 100 / maxSum;
                        <!-- меняем цвет баров -->
                        curNumStyle = curNumStyle < 4 ? curNumStyle + 1 : 0;


                        var onclick;
                        if (classType == 'Product') {
                            //link = "./page-product/" + classId;
                            onclick = "getViewProduct(" + classId + ");";
                            classTitle = "<spring:message code='label.index.product.className' />";
                        }
                        if (classType.indexOf('Category') + 1) {
                            onclick = "drawBarsByParentId(" + classId + ", true);";
                            classTitle = "<spring:message code='label.index.category.className' />";
                            classType = 'Category';
                        }
                        <!-- добавляем прогресс бар -->
                        $('#' + idBarElem).append(
                                "<li id='progressBar" + classType + classId + "'>" +
                                "<a onclick='" + onclick + "'>" +
                                "<strong id='barName" + classType + classId + "' value='" + className + "'>" +
                                classTitle + ": " + className + "</strong>" +
                                "<strong id='barSum" + classType + classId + "' class='pull-right text-muted' value='" + classSum +
                                "'>" + numberToString(classSum) + " руб." + "</strong>" +
                                "<div class='progress " + tagClassProgress + " progress-striped active' >" +
                                "<div class='progress-bar " + tagClassProgress + " progress-bar-" + styles[curNumStyle] +
                                "' role='progressbar' aria-valuenow='" + classSum + "'" +
                                "aria-valuemin='0' aria-valuemax='100' style='width: " + normalSum + "%' " +
                                "value='" + className + "'>" +
                                "<span class='sr-only'>" + numberToString(classSum) + "</span>" +
                                "</div>" +
                                "</div>" +
                                "</div>" +
                                "</a>" +
                                "</li>" +
                                "");
                    });
                    $('#' + idBarElem).append(
                            "<div class='row'>" +
                            "<div class='col-md-12'><h6><strong>" +
                            "ИТОГО " + numberToString($('#' + idSumElem).attr('value')) + " руб." +
                            "</strong></h6>" +
                            "</div><p><p>" +
                            "</div>"
                    );

                    //показываем модальное окно
                    if (!isChildren)
                        $('#modalCategory').modal('show');
                }
            });
        }
    };
    function getViewProduct(productId) {
        $('#periodForm').attr('action', '/page-product/' + productId);
        $('#periodForm').submit();
    }
    function ClearModal() {
        //удаляем прежние amount
        $('[id^="modalCategoryBody"]').each(function () {
            $(this).empty();
        });
        $('#modalHeader').empty();
        //рисуем структуру вывода данных
        $('#modalCategoryBody').append(
                "<div id='modalDropDown'" +
                "</div>");
    }
    function drawChartOfTypes(data, elementId) {
        var parent = $('#' + elementId).parent();
        parent.empty();
        parent.append(
                "<h2><spring:message code='label.index.chartIncomeExpense.title' /></h2>" +
                "<canvas id='typeChart' style='height:250px'></canvas>");

        var pieChartCanvas = $('#' + elementId).get(0).getContext('2d');
        var pieChart = new Chart(pieChartCanvas);
        var PieData = [];

        var colors = ['#5cb85c', '#f0ad4e', '#f0ad4e', '#d9534f'];
        var curNumStyle = -1;

        var array = data.split(',');
        var count = 0;
        array.forEach(function (pair, index, array) {
            var arrayPair = pair.split('=');
            var name = arrayPair[0];
            var sum = arrayPair[1];

            curNumStyle = curNumStyle < 4 ? curNumStyle + 1 : 0;

            PieData[count] =
            {
                value: sum,
                color: colors[curNumStyle],
                highlight: colors[curNumStyle],
                label: name
            };

            count++;
        });

        var pieOptions = {
            //Boolean - Whether we should show a stroke on each segment
            segmentShowStroke: true,
            //String - The colour of each segment stroke
            segmentStrokeColor: '#fff',
            //Number - The width of each segment stroke
            segmentStrokeWidth: 2,
            //Number - The percentage of the chart that we cut out of the middle
            percentageInnerCutout: 50, // This is 0 for Pie charts
            //Number - Amount of animation steps
            animationSteps: 100,
            //String - Animation easing effect
            animationEasing: 'easeOutBounce',
            //Boolean - Whether we animate the rotation of the Doughnut
            animateRotate: true,
            //Boolean - Whether we animate scaling the Doughnut from the centre
            animateScale: false,
            //Boolean - whether to make the chart responsive to window resizing
            responsive: true,
            // Boolean - whether to maintain the starting aspect ratio or not when responsive, if set to false, will take up entire container
            maintainAspectRatio: true,
            //String - A legend template
            legendTemplate: '<ul class=\"\<\%=name.toLowerCase()%>-legend\">\<\% ' +
            'for (var i=0; i<segments.length; i++){' +
            '%><li><span style=\"background-color:\<\%=segments[i].fillColor%>\"></span>' +
            '<\%if(segments[i].label){' +
            '%>\<\%=segments[i].label%>\<\%}%></li>\<\%}%></ul>'
        };
        pieChart.Doughnut(PieData, pieOptions);
    }
    ;
    function selectPeriod(period){
        //TODO: redudant
        $('#btnPeriod').val(period);
        $('#period').val(period);
        $('#btnPeriod').text($('#' + period).text());

        if (period == 'custom') {
            //предустановка значения для автоматического обновления данных
            if ($('#countDays').val() == "") {
                $('#countDays').val('14');
            }
            $('#countDays').show();
            $('#btnRefresh').show();
        } else {
            $('#countDays').val('');
            $('#countDays').hide();
            $('#btnRefresh').hide();
        }

        $.ajax({
            url: '/getParentsCategories',
            type: "GET",
            data: {
                'period': period,
                'countDays': $('#countDays').val()
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
                            "<a onclick='drawBarsByParentId(" + id + ")'>" +
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

                $('#textTotalIncome').append("<strong>" + totalIncomeSum + "</strong>");
                $('#textTotalExpense').append("<strong>" + totalExpenseSum + "</strong>");

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
    function drawCalendar(){
        $('#calendar').fullCalendar({
            header: {
                left: 'prev,next today',
                center: 'title',
                right: ''
            },
            //theme: true,
            defaultDate: '${curDate}',
            locale: 'ru',
            buttonIcons: true, // show the prev/next text
            weekNumbers: false,
            navLinks: false, // can click day/week names to navigate views
            editable: false,
            eventLimit: false, // allow "more" link when too many events
            events: [
                <c:forEach items="${calendarData}" var="calendarData">
                {
                    title: '${calendarData.getTitle()}'.replace(/(\d)(?=(\d\d\d)+([^\d]|$))/g, '$1 '),
                    start: '${calendarData.getDate()}',
                    allDay: ${calendarData.isAllDay()},
                    color: '${calendarData.getColor()}',
                    textColor: '${calendarData.getTextColor()}'
                },
                </c:forEach>
            ]
        });
    }
</script>
<!-- modal panel -->
<div id="modalCategory" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="modalCategorylabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h3 id="mpdalcategoryTitle" class="modal-title"><spring:message code="label.index.modal.title" /></h3>
                <div id="modalHeader" class="modal-title">
                </div>
            </div>
            <div id="modalCategoryBody" class="modal-body">
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
    <div class="container">

        <c:if test="${pageContext.request.userPrincipal.name != null}">
            <form id="logoutForm" method="POST" action="${contextPath}/logout">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form>
            <h2>Welcome ${pageContext.request.userPrincipal.name} | <a onclick="document.forms['logoutForm'].submit()">Logout</a>
            </h2>
        </c:if>
    </div>
    <div class="container">
        <div class="center">
            <div class="col-md-12">
                <h2><spring:message code="label.index.title" /></h2>
                <hr>
                <p class="lead"><spring:message code="label.index.details" /></p>
            </div>
        </div>
    </div>

    <div class="container">
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
    <div class="container">
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
                    <li><a id="day" onclick="selectPeriod('day');">
                        <spring:message code="label.index.categories.period.today" /></a>
                    </li>
                    <li><a id="week" onclick="selectPeriod('week');">
                        <spring:message code="label.index.categories.period.week" /></a>
                    </li>
                    <li><a id="month" onclick="selectPeriod('month');">
                        <spring:message code="label.index.categories.period.month" /></a>
                    </li>
                    <li><a id="all" onclick="selectPeriod('all');">
                        <spring:message code="label.index.categories.period.all" /></a>
                    </li>
                    <li><a id="custom" onclick="selectPeriod('custom');">
                        <spring:message code="label.index.categories.period.custom" /></a>
                    </li>
                </ul>
            </div>
            <div class="col-sm-4 col-sm-offset-4">
                <button id="btnRefresh" class="btn-default btn-lg btn-block" style="display: none;"
                    onclick="selectPeriod($('#btnPeriod').val())">
                    <spring:message code="label.index.refresh" />
                </button>
            </div>
            <div class="col-sm-4">
                <%--форма для перехода на другую страницу по периоду--%>
                <form id="periodForm" method="GET" action="/page-product/productId">
                    <input id="period" type="hidden" name="period" value="month"/>
                    <%--<input type="hidden" name="countDays" value=""/>--%>

                <input id="countDays" type="number" class="form-control input-lg" name="countDays"
                       path="countDays" placeholder="Кол-во дней" data-rule="number"
                       value="" style="display: none;"/>
                </form>
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
                            <a onclick="drawBarsByParentId(${classId})">
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
                                <a onclick="drawBarsByParentId(${classId})">
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

<section id="services">
    <div class="container">
        <div class="row">
            <div class="col-sm-12 wow fadeInDown " data-wow-duration="1000ms" data-wow-delay="300ms">
                <div id="calendar">
                </div>
            </div>
        </div>
    </div>
</section>

</body>
</html>