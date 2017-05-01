<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">

<jsp:include page="/WEB-INF/view/tags/header-template.jsp"></jsp:include>

<%--быстрый поиск--%>
<spring:url value="/resources/js/bootstrap3-typeahead.js" var="typeahead"/>
<script src="${typeahead}"></script>

<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        $.ajaxSetup({
            headers: {'X-CSRF-TOKEN': document.getElementById('_csrf_token').value}
        });

        //показываем модальное окно при получении ошибки в момент загрузки страницы
        if ($('#response').val() != '') {
            displayMessage('error', $('#response').val());
        }

        $('#amountDate').datetimepicker({
            pickTime: false,
            language: 'ru',
        });

        //быстрый поиск в выпадающем списке
        $('#productName').typeahead({
            source: function (query, process) {
                var $products = new Array;
                $products = [''];
                $.ajax({
                    type: 'Get',
                    url: '/page-data/getProducts',
                    dataType: "json",
                    data: {'query': query},
                    success: function (data) {
                        process(data);
                    }
                });
            },
            autoSelect: true,
            displayText: function (category) {
                return category.name;
            },
            updater: function (category) {
                $('#productId').val(category.id);
                $('#productName').val(category.name);
                return category.name;
            }
        });
    });

    function setDropdownListId(categoryId, categoryName, type) {
        var elem;
        switch (type) {
            case 'categories':
                elem = $('#btnCategories');
                break;
            case 'parentCategories':
                elem = $('#btnParentCategories');
                break;
            case 'product':
                elem = $('#btnProductMerge');
                break;
        }
        elem.text(categoryName + "  ");
        elem.val(categoryId);
        elem.append("\<span class=\"caret\">\<\/span>");
    }
    function displayMessage(type, message, Url) {
        ClearModalPanel();
        $('#modalBody').append(
                "<h4><strong>" + message + "</strong></h4>"
        );

        var onclick;
        if (type == 'SUCCESS') {
            //onclick = "$(\"#response\").val(\"\"); location.href=\"" + Url + "\";";
            onclick = "location.reload();";
        } else {
            onclick = "$(\"#response\").val(\"\"); return false;";
        }
        $('#modalFooter').append(
                "<button type='button' class='btn btn-default' data-dismiss='modal' " +
                "onclick='" + onclick + "'>" +
                "Ok" +
                "</button>"
        );
        $('#modal').modal('show');
    }
    function ClearModalPanel() {
        $('[id^="modalBody"]').each(function () {
            $(this).empty();
        });
        $('[id^="modalFooter"]').each(function () {
            $(this).empty();
        });
    }
</script>
<script language="javascript" type="text/javascript">
    function Save(className) {
        if (className == 'amount') {
            var data = {
                'className': 'amount',
                'id': $('#id').val(),
                'categoryId': $('#btnCategories').val(),
                'productName': $('#productName').val(),
                'name': $('#amountName').val(),
                'price': $('#amountPrice').val(),
                'date': $('#amountDate').val(),
                'details': $('#amountDetails').val()
            };
        } else if (className == 'category') {
            var data = {
                'className': 'category',
                'id': $('#id').val(),
                'parentId': $('#btnParentCategories').val(),
                'name': $('#categoryName').val(),
                'type': $('#typeIncome').prop('checked') ? 0 : 1,
                'details': $('#categoryDetails').val(),
            };
        } else if (className == 'product') {
            var data = {
                'className': 'product',
                'id': $('#id').val(),
                'name': $('#product').val(),
                'mergeProductId': $('#btnProductMerge').val()
            };
        }
        $.ajax({
            type: "POST",
            url: '/page-data/save',
            data: data,
            dataType: 'json',
            success: function (data) {
                var type = data.type;
                var message = data.message;

                displayMessage(type, message, "/index");
            }
        });
    }
    ;
    function Delete(className) {
        ClearModalPanel();
        $('#modalBody').append(
                "<h4><strong><spring:message code="label.page-amount.modal.textDelete" /></strong></h4>"
        );
        $('#modalFooter').append(
                "<button type='button' class='btn btn-default' data-dismiss='modal' " +
                "onclick=SendDeleteQuery('" + className + "');>" +
                "<spring:message code="label.page-amount.modal.btnYes" />" +
                "</button>" +
                "<button type='button' class='btn btn-primary' " +
                "data-dismiss='modal'>" +
                "<spring:message code="label.page-amount.modal.btnNo" />" +
                "</button>" +
                ""
        );
        $('#modal').modal('show');
    }
    ;
    function SendDeleteQuery(className) {
        var data = {
            'className': className,
            'id': $('#id').val(),
        }
        $.ajax({
            type: "POST",
            url: '/page-data/delete',
            data: data,
            success: function (data) {
                var type = data.type;
                var message = data.message;

                displayMessage(type, message, "/index");
            }
        });
    }
    ;
</script>
<!-- Modal Panel -->
<div id="modal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="modallabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 id="mpdalcategoryTitle" class="modal-title"><spring:message
                        code="label.page-amount.modal.title"/></h4>
            </div>
            <div id="modalBody" class="modal-body">
                Loading data...
            </div>
            <div id="modalFooter" class="modal-footer">
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>

<div id="services" class="container-fluid">
    <div class='row col-sm-10 col-sm-offset-1'>
        <div class="form-group">
            <input id="_csrf_token" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <input id="id" type="hidden" name="id" value="${id}"/>
            <textarea id="response" name="response" style="display: none;">${response}</textarea>
            <c:if test="${className=='amount'}">
                <section id='sectionAmount'>
                        <%--Название страницы--%>
                    <div class='page-header'>
                        <h3><spring:message code="label.page-amount.title"/></h3>
                    </div>
                        <%--Конец названия страницы--%>
                        <%--Дата--%>
                    <div class="row">
                        <div class="col-md-3"><h4><strong><spring:message code="label.page-amount.date"/></strong></h4>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-3 col-sm-12">
                            <input id="amountDate" type="text" class="form-control col-sm-2" name="date" value='${date}'>
                        </div>
                    </div>
                        <%--Конец блока даты--%>
                        <%--Имя сущности--%>
                    <div class="row">
                        <div class="col-md-12"><h4><strong><spring:message code="label.page-amount.name"/></strong></h4>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-12">
                            <input id="amountName" type="text" name="name" class="form-control form input-lg"
                                   path="name" placeholder="Наименование ценности" data-rule="minlen:5"
                                   data-msg="Please enter at least 5 chars"
                                   value='${name}'/>
                            <div class="validation"></div>
                        </div>
                    </div>
                        <%--Конец имени сущности--%>
                        <%--Товарная группа--%>
                    <div class="row">
                        <div class="col-md-12"><h4><strong><spring:message code="label.page-amount.product"/></strong>
                        </h4>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-12">
                            <input id="productId" type="hidden" name="productId" value="${productId}"/>
                            <input id="productName" type="text" name="productName" class="form-control form input-lg"
                                   path="name" placeholder="Товарная группа" data-rule="minlen:5"
                                   data-msg="Please enter at least 5 chars"
                                   value='${productName}'/>
                            <div class="validation"></div>
                        </div>
                    </div>
                        <%--Конец товарной группы--%>
                        <%--Категория и цена--%>
                    <div class="row">
                        <div class="col-md-10"><h4><strong><spring:message code="label.page-amount.category"/></strong>
                        </h4>
                        </div>
                        <div class="col-md-2"><h4><strong><spring:message code="label.page-amount.price"/></strong></h4>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-10">
                            <button id="btnCategories" class="btn-default btn-lg btn-block dropdown-toggle"
                                    data-toggle="dropdown" value="${categoryId}">
                                <c:choose>
                                    <c:when test="${not empty categoryName}">
                                        ${categoryName}
                                    </c:when>
                                    <c:otherwise>
                                        <spring:message code="label.page-amount.selectCategory"/>
                                    </c:otherwise>
                                </c:choose>
                                <span class="caret"></span>
                            </button>
                            <ul id="dropdownCategories" class="dropdown-menu">
                                <li>
                                    <a onclick="setDropdownListId(-1, 'Select Category', 'categories');return false;"><spring:message
                                            code="label.page-amount.selectCategory"/></a></li>
                                <li class="divider"></li>
                                <c:forEach items="${list}" var="list">
                                    <li><a id='${list.getId()}' onclick="setDropdownListId('${list.getId()}',
                                            '${list.getName()}', 'categories');
                                            return false;">${list.getName()}</a></li>
                                </c:forEach>
                            </ul>
                        </div>
                        <div class="col-md-2">
                            <input id="amountPrice" type="number" class="form-control input-lg" name="price"
                                   path="price" placeholder="Цена" data-rule="number"
                                   data-msg="Please enter a valid price"
                                   value='${price}'/>
                            <div class="validation"></div>
                        </div>
                    </div>
                        <%--Конец категории и цены--%>
                        <%--Описание--%>
                    <div class="row">
                        <div class="col-md-12"><h4><strong><spring:message code="label.page-amount.details"/></strong>
                        </h4>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-12">
                            <textarea id="amountDetails" class="form-control input-lg" rows="5"
                                      placeholder="Описание">${details}</textarea>
                        </div>
                    </div>
                        <%--Конец описания--%>
                    <div class="row">
                        <div class="col-md-6 col-md-offset-6">
                            <div class="btn-group">
                                <button id="btnAmountNew" type="submit" name="btnNew"
                                        class="btn btn-default btn-lg "
                                        onclick="location.href='/page-data/amount.html'"><spring:message
                                        code="label.page-amount.btnNew"/>
                                </button>
                            </div>
                            <div class="btn-group">
                                <button id="btnAmountDelete" type="submit" name="btnDelete"
                                        class="btn btn-default btn-lg " onclick="Delete('amount');return false;">
                                    <spring:message code="label.page-amount.btnDelete"/>
                                </button>
                            </div>
                            <div class="btn-group">
                                <button id="btnAmountOk" type="submit" name="btnOk"
                                        class="btn btn-default btn-lg " onclick="Save('amount');return false;">
                                    <spring:message code="label.page-amount.btnOk"/>
                                </button>
                            </div>
                            <div class="btn-group">
                                <button id="btnAmountCancel" type="submit" name="btnCancel"
                                        class="btn btn-default btn-lg " onclick="location.reload();"><spring:message
                                        code="label.page-amount.btnCancel"/>
                                </button>
                            </div>

                        </div>
                    </div>
                </section>
            </c:if>
            <c:if test="${className=='category'}">
                <section id='sectionCategory'>
                    <div class='page-header'>
                        <h3><spring:message code="label.page-category.title"/></h3>
                    </div>
                    <div class="row">
                        <div class="col-md-12"><h4><strong><spring:message
                                code="label.page-category.parentName"/></strong></h4>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-12">
                            <button id="btnParentCategories" class="btn-default btn-lg btn-block dropdown-toggle"
                                    data-toggle="dropdown" value="${parentId}">
                                <c:choose>
                                    <c:when test="${not empty parentName}">
                                        ${parentName}
                                    </c:when>
                                    <c:otherwise>
                                        <spring:message code="label.page-category.selectCategory"/>
                                    </c:otherwise>
                                </c:choose>
                                <span class="caret"></span>
                            </button>
                            <ul id="dropdownParentCategories" class="dropdown-menu">
                                <li>
                                    <a onclick="setDropdownListId(-1, 'Select Category', 'parentCategories');return false;">
                                        <spring:message code="label.page-category.selectCategory"/></a></li>
                                <li class="divider"></li>
                                <c:forEach items="${list}" var="list">
                                    <li><a id='${list.getId()}' onclick="setDropdownListId('${list.getId()}',
                                            '${list.getName()}', 'parentCategories');
                                            return false;">${list.getName()}</a></li>
                                </c:forEach>
                            </ul>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-12"><h4><strong>
                            <spring:message code="label.page-category.name"/>
                        </strong></h4>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-12">
                            <input id="categoryName" type="text" name="name" class="form-control form input-lg"
                                   path="name" placeholder="Наименование" data-rule="minlen:5"
                                   data-msg="Please enter at least 5 chars"
                                   value='${name}'/>
                            <div class="validation"></div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-4"><h4><strong>
                            <spring:message code="label.page-category.type"/>
                        </strong></h4>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-4 col-md-offset-2">
                            <div class="radio">
                                <label>
                                    <input id="typeIncome" type="radio" name="optionsRadios" value="income"
                                    <c:if test="${not empty typeIncome}">
                                           checked=true
                                    </c:if>
                                    >
                                    <spring:message code="label.page-category.typeIncome"/>
                                </label>
                            </div>
                            <div class="radio">
                                <label>
                                    <input id="typeExpense" type="radio" name="optionsRadios" value="expence"
                                    <c:if test="${empty typeIncome}">
                                           checked=true
                                    </c:if>
                                    >
                                    <spring:message code="label.page-category.typeExpence"/>
                                </label>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-12"><h4><strong>
                            <spring:message code="label.page-category.details"/>
                        </strong></h4>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-12">
                            <textarea id="categoryDetails" class="form-control input-lg" rows="5"
                                      placeholder="Описание">${details}</textarea>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 col-md-offset-6">
                            <div class="btn-group">
                                <button id="btnCategoryNew" type="submit" name="btnNew"
                                        class="btn btn-default btn-lg "
                                        onclick="location.href='/page-data/category.html'">
                                    <spring:message code="label.page-category.btnNew"/>
                                </button>
                            </div>
                            <div class="btn-group">
                                <button id="btnCategoryDelete" type="submit" name="btnDelete"
                                        class="btn btn-default btn-lg " onclick="Delete('category');return false;">
                                    <spring:message code="label.page-category.btnDelete"/>
                                </button>
                            </div>
                            <div class="btn-group">
                                <button id="btnCategoryOk" type="submit" name="btnOk"
                                        class="btn btn-default btn-lg " onclick="Save('category');return false;">
                                    <spring:message code="label.page-category.btnOk"/>
                                </button>
                            </div>
                            <div class="btn-group">
                                <button id="btnCategoryCancel" type="submit" name="btnCancel"
                                        class="btn btn-default btn-lg " onclick="location.reload();">
                                    <spring:message code="label.page-category.btnCancel"/>
                                </button>
                            </div>
                        </div>
                    </div>
                </section>
            </c:if>
            <c:if test="${className=='product'}">
                <section id='sectionCategory'>
                    <div class='page-header'>
                        <h2><spring:message code="label.page-data.product.title"/></h2>
                    </div>
                    <div class="row">
                        <div class="col-md-12"><h4><strong>
                            <spring:message code="label.page-data.product.name"/>
                        </strong></h4>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-12">
                            <input id="product" type="text" name="name" class="form-control form input-lg"
                                   path="name" placeholder="Name" data-rule="minlen:5"
                                   data-msg="Please enter at least 5 chars"
                                   value='${name}'/>
                            <div class="validation"></div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-12"><h4><strong>
                            <spring:message code="label.page-data.product.mergeText"/>
                        </strong></h4>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-12">
                            <button id="btnProductMerge" class="btn-default btn-lg btn-block dropdown-toggle"
                                    data-toggle="dropdown" value="${productMergeId}">
                                <c:choose>
                                    <c:when test="${not empty productMerge}">
                                        ${productMergeName}
                                    </c:when>
                                    <c:otherwise>
                                        <spring:message code="label.page-data.product.selectProductMerge"/>
                                    </c:otherwise>
                                </c:choose>
                                <span class="caret"></span>
                            </button>
                            <ul id="dropdownProductMerge" class="dropdown-menu">
                                <li><a onclick="setDropdownListId(-1, 'Select Product', 'product');return false;">
                                    <spring:message code="label.page-data.product.selectProductMerge"/></a></li>
                                <li class="divider"></li>
                                <c:forEach items="${list}" var="list">
                                    <li><a id='${list.getId()}' onclick="setDropdownListId('${list.getId()}',
                                            '${list.getName()}', 'product');
                                            return false;">${list.getName()}</a></li>
                                </c:forEach>
                            </ul>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 col-md-offset-6">
                            <div class="btn-group">
                                <button id="btnProductDelete" type="submit" name="btnDelete"
                                        class="btn btn-default btn-lg " onclick="Delete('product');return false;">
                                    <spring:message code="label.page-category.btnDelete"/>
                                </button>
                            </div>
                            <div class="btn-group">
                                <button id="btnProductOk" type="submit" name="btnOk"
                                        class="btn btn-default btn-lg " onclick="Save('product');return false;">
                                    <spring:message code="label.page-category.btnOk"/>
                                </button>
                            </div>
                            <div class="btn-group">
                                <button id="btnProductCancel" type="submit" name="btnCancel"
                                        class="btn btn-default btn-lg " onclick="location.reload();">
                                    <spring:message code="label.page-category.btnCancel"/>
                                </button>
                            </div>
                        </div>
                    </div>
                </section>
            </c:if>
        </div>
    </div>
</div>

</body>
</html>