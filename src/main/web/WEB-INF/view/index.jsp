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
        $.ajax({
            url: 'http://localhost:8080/getAllCategoriesWithTotalSum',
            dataType: 'json',
            success: function (data) {
                //данные для стилей прогресс баров
                var styles = ['success', 'info', 'warning', 'danger'];
                var curNumStyle = -1;
                var maxPrice = 0;

                //данные для диаграммы доход/расход
                var totalIncome = 0;
                var totalExpense = 0;

                $.each(data, function (category, sum) {
                    //считываем данные категории
                    category = category.replace(/Category|{|}/g, '');
                    var categoryName = 'Category: ' + getValue(category, 'name');
                    var categoryId = getValue(category, 'id');

                    <!-- нормализуем суммы -->
                    if (maxPrice == 0) maxPrice = sum;
                    normalPrice = sum * 100 / maxPrice;
                    <!-- меняем цвет баров -->
                    curNumStyle = curNumStyle < 4 ? curNumStyle + 1 : 0;

                    $('#categoriesProgressBar').append(
                            "<div id='divBarCategoryId" + categoryId + "' class='col-sm-10'>" +
                            "</div> " +
                            "<div id='divSumCategoryId" + categoryId + "' class='col-sm-2'> " +
                            "</div>");

                    <!-- добавляем прогресс бар -->
                    $('#divBarCategoryId' + categoryId).append(
                            "\<div id='categoryBarId" + categoryId + "' class=\"progress progress-striped active\"\> " +
                            "\<div class=\"progress-bar progress-bar-" + styles[curNumStyle] + "\" role=\"progressbar\" " +
                            "aria-valuenow=\"" + sum + "\"" +
                            " aria - valuemin =\"0\" aria-valuemax=\"100\" style=\"width: " + normalPrice + "%\" " +
                            " onclick='drawBarsAmountsByCategoryId(" + categoryId + "); return false;'\> " + categoryName +
                            "</div> " +
                            "</div> ");

                    <!-- добавляем сумму к бару -->
                    $('#divSumCategoryId' + categoryId).append(
                            "<h2>" + sum + "</h2>");

                    <!-- суммируем данные по расходу/доходу -->
                    if (getValue(category, 'type') == '0') totalIncome += sum;
                    else totalExpense += sum;
                });
                <!-- рисуем диграмму доход/расход -->
                drawChartOfTypes('Income=' + totalIncome + ',' + 'Expense=' + totalExpense, 'typeChart');
                <!-- заполняем итоговые данные доход/расход -->
                $('#textTotalIncome').text('Income: ' + totalIncome + ' руб.');
                $('#textTotalExpense').text('Expense: ' + totalExpense + ' руб.');
            }
        });
    });
    //функция постройки баров по таблице amounts
    function drawBarsAmountsByCategoryId(categoryId) {
        $.ajax({
            url: 'http://localhost:8080/getAmountsByCategoryId',
            type: "GET",
            data: {
                'categoryId': categoryId
            },
            dataType: 'json',
            success: function (data) {
                //удаляем прежние amount
                $('[id^="amountBarId"]').each(function () {
                    $(this).parent().remove();
                });
                $('[id^="sumAmountId"]').each(function () {
                    $(this).remove();
                });

                var styles = ['success', 'info', 'warning', 'danger'];
                var curNumStyle = 2;
                var maxPrice = $('#categoryBarId' + categoryId).children().attr('aria-valuenow');

                data.forEach(function (amount, index, data) {
                    var amountId = amount.id;
                    var amountName = 'Amount: ' + amount.name;
                    var amountPrice = amount.price;

                    <!-- нормализуем суммы -->
                    normalPrice = amountPrice * 100 / maxPrice;
                    <!-- меняем цвет баров -->
                    curNumStyle = curNumStyle < 4 ? curNumStyle + 1 : 0;

                    <!-- добавляем прогресс бар -->
                    $('#divBarCategoryId' + categoryId).append(
                            "\<div class=\"progress progress-striped active\"\> " +
                            "\<div id='amountBarId" + amountId + "' class=\"progress-bar progress-bar-" + styles[curNumStyle] + "\" role=\"progressbar\" " +
                            "aria-valuenow=\"" + amountPrice + "\"" +
                            " aria - valuemin =\"0\" aria-valuemax=\"100\" style=\"width: " + normalPrice + "%\" > " + amountName +
                            "</div> " +
                            "</div> ");
                    <!-- добавляем сумму к бару -->
                    $('#divSumCategoryId' + categoryId).append(
                            "<h2 id='sumAmountId'" + amountId + ">" + amountPrice + "</h2>");
                });
            }
        });
    }
    ;
    //парсинг переданной строки и возврат значения пары формата key=value
    function getValue(string, key) {
        var array = string.split(', ');
        var value;
        array.forEach(function (pair, index, array) {
            var arrayPair = pair.split('=');
            if (arrayPair[0] == key) {
                value = arrayPair[1];
                return '';
            }
        });
        value = value.replace(/'/g, '');
        return value;
    }
    ;
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
    };
</script>

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
            <h2>Welcome ${pageContext.request.userPrincipal.name} | <a onclick="document.forms['logoutForm'].submit()">Logout</a></h2>
        </c:if>
    </div>
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
            <div class="col-sm-6 wow fadeInDown " data-wow-duration="1000ms"
                 data-wow-delay="300ms">
                <h2>Income/Expense</h2>
                <canvas id="typeChart" style="height:250px"></canvas>
            </div>
            <div class="col-sm-6 wow fadeInDown " data-wow-duration="1000ms"
                 data-wow-delay="300ms">
                <h2>Summary info</h2>
                <h2 id="textTotalIncome">Deposit: 1000</h2>
                <h2 id="textTotalExpense">Credit: 1000</h2>
                <h2>Today: 15/04/2017</h2>
            </div>

        </div>
    </div>
    <div class="container">
        <div class="row wow fadeInDown" data-wow-duration="1000ms" data-wow-delay="300ms">
            <h2>Category info</h2>
            <div class="col-sm-12 wow fadeInDown " id="categoriesProgressBar" data-wow-duration="1000ms"
                 data-wow-delay="300ms">
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
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/view/tags/footer-template.jsp"></jsp:include>

</body>
</html>