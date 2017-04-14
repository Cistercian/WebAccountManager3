<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">

<jsp:include page="/WEB-INF/view/tags/header-template.jsp"></jsp:include>


<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        $.ajax({
            url: './edit-page/getCategoriesIdAndName',
            success: function (data) {

                $.each(data, function (categoryId, categoryName) {
                    $('#dropdown-menu-amounts').append(
                            "\<li>\<a id=\"" + categoryId + "\" onclick=\"setCategoryId(" + categoryId + ", '" + categoryName +
                            "');return false;\">" + categoryName + "\<\/a>\<\/li>");
                });
            }
        });
    });

    function setCategoryId(categoryId, categoryName) {
        $('#dropdownBtnAmounts').text(categoryName + "  ");
        $('#dropdownBtnAmounts').val(categoryId);
        $('#dropdownBtnAmounts').append("\<span class=\"caret\">\<\/span>");
    };
</script>

<div id="services">
    <div class="container">
        <div class="center">
            <div class="col-md-6 col-md-offset-3">
                <h2>Edit</h2>
                <hr>
                <p class="lead">Слайд редактирования сущностей БД</p>
                <p class="lead">Table Amounts</p>
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
                        <button class="btn btn-large dropdown-toggle" data-toggle="dropdown" id="dropdownBtnAmounts">Select Category
                            <span class="caret"></span></button>
                        <ul class="dropdown-menu" id="dropdown-menu-amounts">
                            <li><a onclick="setCategoryId(-1, 'Select Category');return false;">select category</a></li>
                            <li class="divider"></li>);
                        </ul>
                    </div>
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
                        var data = {
                            'categoryId': document.getElementById('dropdownBtnAmounts').value,
                            'name': document.getElementById('AmountName').value,
                            'price': document.getElementById('AmountPrice').value,
                            'amountsDate': document.getElementById('amountsDate').value,
                            'details': document.getElementById('amountDetails').value,
                            'submitAmmount': document.getElementById('amountDetails').value
                        };
                        $.ajax({
                            type: "POST",
                            url: './edit-page-amount/addAmounts',
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

<jsp:include page="/WEB-INF/view/tags/footer-template.jsp"></jsp:include>

</body>
</html>