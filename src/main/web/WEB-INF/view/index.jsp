<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">

<jsp:include page="/WEB-INF/view/tags/header-template.jsp"></jsp:include>
<spring:url value="/resources/js/Chart.min.js" var="chartmin"/>
<script src="${chartmin}"></script>
<!-- обращение к контроллеру -->
<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        drawChartOfTypes("<spring:message code='label.index.chart.income.label' />=${sumIncome},<spring:message code='label.index.chart.expense.label' />=${sumExpense}", "typeChart");

//        $.ajax({
//            url: 'http://localhost:8080/getAllCategoriesWithTotalSum',
//            dataType: 'json',
//            success: function (data) {
//                //данные для стилей прогресс баров
//                var styles = ['success', 'info', 'warning', 'danger'];
//                var curNumStyle = -1;
//                var maxPrice = 0;
//
//                //данные для диаграммы доход/расход
//                var totalIncome = 0;
//                var totalExpense = 0;
//
//                $.each(data, function (category, sum) {
//                    //считываем данные категории
//                    category = category.replace(/Category|{|}/g, '');
//                    var categoryName = 'Category: ' + getValue(category, 'name');
//                    var categoryId = getValue(category, 'id');
//
//                    <!-- нормализуем суммы -->
//                    if (maxPrice == 0) maxPrice = sum;
//                    normalPrice = sum * 100 / maxPrice;
//                    <!-- меняем цвет баров -->
//                    curNumStyle = curNumStyle < 4 ? curNumStyle + 1 : 0;
//
//                    $('#categoriesProgressBar').append(
//                            "<div id='divBarCategoryId" + categoryId + "' class='col-sm-10'>" +
//                            "</div> " +
//                            "<div id='divSumCategoryId" + categoryId + "' class='col-sm-2'> " +
//                            "</div>");
//
//                    <!-- добавляем прогресс бар -->
//                    $('#divBarCategoryId' + categoryId).append(
//                            "\<div id='categoryBarId" + categoryId + "' class=\"progress progress-striped active\"\> " +
//                            "\<div class=\"progress-bar progress-bar-" + styles[curNumStyle] + "\" role=\"progressbar\" " +
//                            "aria-valuenow=\"" + sum + "\"" +
//                            " aria - valuemin =\"0\" aria-valuemax=\"100\" style=\"width: " + normalPrice + "%\" " +
//                                //" onclick='drawBarsAmountsByCategoryId(" + categoryId + "); return false;'\> " + categoryName +
//                            " onclick='drawBarsByParentId(" + categoryId + "); return false;'\> " + categoryName +
//                            "</div> " +
//                            "</div> ");
//
//                    <!-- добавляем сумму к бару -->
//                    $('#divSumCategoryId' + categoryId).append(
//                            "<h2>" + sum + "</h2>");
//
//                    <!-- суммируем данные по расходу/доходу -->
//                    if (getValue(category, 'type') == '0') totalIncome += sum;
//                    else totalExpense += sum;
//                });
//                <!-- рисуем диграмму доход/расход -->
//                drawChartOfTypes('Income=' + totalIncome + ',' + 'Expense=' + totalExpense, 'typeChart');
//                <!-- заполняем итоговые данные доход/расход -->
//                $('#textTotalIncome').text('Income: ' + totalIncome + ' руб.');
//                $('#textTotalExpense').text('Expense: ' + totalExpense + ' руб.');
//            }
//        });
    });
    function drawBarsByParentId(categoryId) {
        $.ajax({
            url: 'http://localhost:8080/getContentByCategoryId',
            type: "GET",
            data: {
                'categoryId': categoryId
            },
            dataType: 'json',
            success: function (data) {
                ClearModal();
                //данные для стилей прогресс баров
                var styles = ['success', 'info', 'warning', 'danger'];
                var curNumStyle = -1;
                var maxPrice = 0;

                data.forEach(function (barData, index, data) {
                    var classId = barData.id;
                    var classType = barData.className;
                    var className = barData.name;
                    var classPrice = barData.sum;

                    <!-- нормализуем суммы -->
                    if (maxPrice == 0) maxPrice = classPrice;
                    normalPrice = classPrice * 100 / maxPrice;
                    <!-- меняем цвет баров -->
                    curNumStyle = curNumStyle < 4 ? curNumStyle + 1 : 0;

                    <!-- добавляем прогресс бар -->
                    $('#modalDropDown').append(
                            "<li>" +
                            (classType == 'Amount' ?
                            "<a href='./page-amount/amount/" + classId + "/display'>" :
                            "<a href='./page-category/" + classId + "'>") +
                            "<div>" +
                            "<p>" +
                            "<strong id='barName" + classId + "' value='" + className + "'>" +
                            classType + ": " + className + "</strong>" +
                            "<strong id='barSum" + classId + "' class='pull-right text-muted' value='" + className +
                            "'>" + classPrice + " руб." + "</strong>" +
                            "</p>" +
                            "<div class='progress progress-striped active'>" +
                            "<div class='progress-bar progress-bar-" + styles[curNumStyle] + "' role='progressbar'" +
                            " aria-valuenow='" + classPrice + "'" +
                            "aria-valuemin='0' aria-valuemax='100' style='width: " + normalPrice + "%' " +
                            "value='" + className + "'>" +
                            "<span class='sr-only'>" + classPrice + "</span>" +
                            "</div>" +
                            "</div>" +
                            "</div>" +
                            "</a>" +
                            "</li>" +
                            "<li class='divider'></li>");
                });
                $('#modalDropDown').append(
                        "<div class='row'>" +
                            "<div class='col-md-12'><h4><strong>" +
                                "ИТОГО " + $('#categoryBarSum' + categoryId).attr('value') + " руб." +
                            "</strong></h4>" +
                            "</div>" +
                        "</div>"
                );
                //показываем модальное окно
                $('#modalCategory').modal('show');
            }
        });
    }
    ;
    function ClearModal() {
        //удаляем прежние amount
        $('[id^="modalCategoryBody"]').each(function () {
            $(this).empty();
        });
        //рисуем структуру вывода данных
        $('#modalCategoryBody').append(
                "<div id='modalDropDown'" +
                "</div>");
    }
    //парсинг переданной строки и возврат значения пары формата key=value
//    function getValue(string, key) {
//        var array = string.split(', ');
//        var value;
//        array.forEach(function (pair, index, array) {
//            var arrayPair = pair.split('=');
//            if (arrayPair[0] == key) {
//                value = arrayPair[1];
//                return '';
//            }
//        });
//        value = value.replace(/'/g, '');
//        return value;
//    }
//    ;
    function drawChartOfTypes(data, elementId) {

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
</script>
<!-- modal panel -->
<div id="modalCategory" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="modalCategorylabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 id="mpdalcategoryTitle" class="modal-title">Category info</h4>
            </div>
            <div id="modalCategoryBody" class="modal-body">
                Loading data...
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary">Save changes</button>
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
                        <strong>${sumIncome}</strong>
                    </c:if>
                </h2>
                <h2 id="textTotalExpense"><spring:message code="label.index.total.expense" />
                    <c:if test="${not empty sumExpense}">
                        <strong>${sumExpense}</strong>
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
            <h2><spring:message code="label.index.categories.title" /></h2>
            <div class="col-sm-12" id="categoryBars">
                <div id="dropDownCategoryBarsIncome">
                <h3><spring:message code="label.index.categoryBars.income" /></h3>
                <c:set var="styles" value="${['success', 'info', 'warning', 'danger']}" scope="page" />
                <c:set var="step" value="-1" scope="page"/>

                <c:forEach items="${categories}" var="map">
                    <c:if test="${map.key.getType() == 0}">

                        <c:set var="classId" value="${map.key.getId()}" />
                        <c:set var="className" value="${map.key.getName()}" />
                        <c:set var="classPrice" value="${map.value}" />
                        <c:set var="normalPrice" value="${classPrice * 100 / maxIncome}" />

                        <c:set var="step" value="${step + 1}" scope="page"/>
                        <li>
                            <a onclick="drawBarsByParentId(${classId})">
                                <div>
                                    <p>
                                        <h4><strong id="categoryBarName${map.key.getId()}" value="${className}">
                                            ${className}
                                        </strong></h4>
                                        <h4><strong id="categoryBarSum${classId}" class="pull-right text-muted"
                                        value="${classPrice}">
                                            ${classPrice} руб.
                                        </strong></h4>
                                    </p>
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

                    <c:forEach items="${categories}" var="map">
                        <c:if test="${map.key.getType() == 1}">

                            <c:set var="classId" value="${map.key.getId()}" />
                            <c:set var="className" value="${map.key.getName()}" />
                            <c:set var="classPrice" value="${map.value}" />
                            <c:set var="normalPrice" value="${classPrice * 100 / maxExpense}" />

                            <c:set var="step" value="${step + 1}" scope="page"/>
                            <li>
                                <a onclick="drawBarsByParentId(${classId})">
                                    <div class="row">
                                        <h4><strong id="categoryBarName${map.key.getId()}" value="${className}">
                                                ${className}
                                        </strong></h4>
                                        <h4><strong id="categoryBarSum${classId}" class="pull-right text-muted"
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
                        </c:if>
                    </c:forEach>
                </div>
            </div>
        </div><!--/.row-->
    </div><!--/.container-->

</section><!--/#about-->

<jsp:include page="/WEB-INF/view/tags/footer-template.jsp"></jsp:include>
</body>
</html>