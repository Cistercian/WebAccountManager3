<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">

<jsp:include page="/WEB-INF/view/tags/header-template.jsp"></jsp:include>

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
        </div>
    </div>


</div>

<jsp:include page="/WEB-INF/view/tags/footer-template.jsp"></jsp:include>

</body>
</html>