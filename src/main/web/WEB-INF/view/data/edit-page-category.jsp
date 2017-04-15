<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">

<jsp:include page="/WEB-INF/view/tags/header-template.jsp"></jsp:include>


<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        refreshPage();
    });

    function setCategoryId(categoryId, categoryName) {
        $('#dropdownBtnCategory').text(categoryName + "  ");
        $('#dropdownBtnCategory').val(categoryId);
        $('#dropdownBtnCategory').append("\<span class=\"caret\">\<\/span>");
    };
    function refreshPage(){
        $.ajax({
            url: './edit-page/getCategoriesIdAndName',
            success: function (data) {

                $('#dropdown-menu-category').empty();
                $('#dropdown-menu-category').append(
                        "\<li>\<a onclick=\"setCategoryId(-1, 'Create New');return false;\">Create New\<\/a>\<\/li>" +
                        "\<li class=\"divider\">\<\/li>");
                $.each(data, function (categoryId, categoryName) {
                    $('#dropdown-menu-category').append(
                            "\<li>\<a id=\"" + categoryId + "\" onclick=\"setCategoryId(" + categoryId + ", '" + categoryName +
                            "');return false;\">" + categoryName + "\<\/a>\<\/li>");
                });

                $('#dropdownBtnCategory').text('Create New  ');
                $('#dropdownBtnCategory').val('-1');
                $('#dropdownBtnCategory').append("\<span class=\"caret\">\<\/span>");

                $('#CategoryName').val('');
                $('#CategoryDetails').val('');
            }
        });
    };
</script>

<div id="services">
    <div class="container">
        <div class="center">
            <div class="col-md-6 col-md-offset-3">
                <h2>Edit</h2>
                <hr>
                <p class="lead">Слайд редактирования сущностей БД</p>
                <p class="lead">Tables Categories</p>
            </div>
        </div>
    </div>
    <div class="container">
        <div class="text-center" class="contactForm wow fadeInDown" data-wow-duration="1000ms"
             data-wow-delay="300ms" modelAttribute="amount">

            <div class="col-md-6 col-sm-6 col-xs-12 left">
                <div class="form-group">
                    <div class="btn-group">
                        <button class="btn btn-large dropdown-toggle" data-toggle="dropdown" id="dropdownBtnCategory" value="-1">
                            Create New
                            <span class="caret"></span></button>
                        <ul class="dropdown-menu" id="dropdown-menu-category">
                            <li><a onclick="setCategoryId(-1, 'Create New');return false;">Create New</a></li>
                            <li class="divider"></li>
                            );
                        </ul>
                    </div>
                </div>
                <div class="form-group">
                    <input type="text" name="name" class="form-control form" id="CategoryName"
                           path="name"
                           placeholder="Category Name" data-rule="minlen:5" data-msg="Please enter at least 5 chars"/>
                    <div class="validation"></div>
                </div>
                <div class="form-group">
                    <input type="textarea" class="form-control" name="details" id="CategoryDetails"
                           placeholder="details"
                           path="details"
                           data-rule="minlen:8" data-msg="Please enter at least 8 chars of subject"/>
                    <div class="validation"></div>
                </div>
                <div class="form-group">
                    <p>Type of Category</p>
                    <div class="radio">
                        <label><input type="radio" class="form-control" name="CategoryTypeDeposit"
                                      id="CategoryTypeDeposit">Deposit</label>
                    </div>
                    <div class="radio">
                        <label><input type="radio" class="form-control" name="CategoryTypeCredit"
                                      id="CategoryTypeCredit" checked=true>Credit</label>
                    </div>
                </div>
                <div class="col-md-6 col-sm-6 col-xs-12 left">
                    <!-- Button -->
                    <button type="submit" id="submitAmmount" name="submitAmmount"
                            class="btn btn-large" onclick="Save();return false;">CREATE
                    </button>

                    <script language="javascript" type="text/javascript">
                        function Save() {
                            var type = $('#CategoryTypeDeposit').checked == true ? 0 : 1;
                            var data = {
                                'id': document.getElementById('dropdownBtnCategory').value,
                                'name': document.getElementById('CategoryName').value,
                                'details': document.getElementById('CategoryDetails').value,
                                'type': type
                            };
                            $.ajax({
                                type: "POST",
                                url: './edit-page/addCategory',
                                data: data,
                                success: function (data) {
                                    alert('entry was saved');
                                }
                            });

                            refreshPage();
                        };
                    </script>

                </div>
                <div class="col-md-6 col-sm-6 col-xs-12 right">
                    <button type="submit" id="cancelAmount" name="cancelAmount"
                            class="btn btn-large" onclick="refreshPage();return false;">CANCEL
                    </button>
                </div>
            </div>
        </div>
    </div>


</div>

<jsp:include page="/WEB-INF/view/tags/footer-template.jsp"></jsp:include>

</body>
</html>