<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">

<jsp:include page="/WEB-INF/view/tags/header-template.jsp"></jsp:include>


<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        $.ajaxSetup({
            headers: {'X-CSRF-TOKEN': document.getElementById('_csrf_token').value}
        });
    });

    function setCategoryId(categoryId, categoryName) {
        $('#btnCategories').text(categoryName + "  ");
        $('#btnCategories').val(categoryId);
        $('#btnCategories').append("\<span class=\"caret\">\<\/span>");
    };
</script>

<!-- Modal Panel -->
<div id="modal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="modallabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 id="mpdalcategoryTitle" class="modal-title">
                    <spring:message code="label.page-category.modal.title" /></h4>
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
    <div class='row'>
        <div class="form-group">
            <input id="_csrf_token" type="hidden"  name="${_csrf.parameterName}"   value="${_csrf.token}"/>
            <input id="id" type="hidden"  name="id" value="${id}"/>
            <section id='section'>
                <div class='page-header'>
                    <h2><spring:message code="label.page-category.title" /></h2>
                </div>
                <div class="row">
                    <div class="col-md-12"><h4><strong><spring:message code="label.page-category.parentName" /></strong></h4>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <button id="btnCategories" class="btn-default btn-lg btn-block dropdown-toggle"
                                data-toggle="dropdown" value="${parentId}">
                            <c:choose>
                                <c:when test="${not empty parentName}">
                                    ${parentName}
                                </c:when>
                                <c:otherwise>
                                    <spring:message code="label.page-category.selectCategory" />
                                </c:otherwise>
                            </c:choose>
                            <span class="caret"></span>
                        </button>
                        <ul id="dropdownCategories" class="dropdown-menu">
                            <li><a onclick="setCategoryId(-1, 'Select Category');return false;">
                                <spring:message code="label.page-category.selectCategory" /></a></li>
                            <li class="divider"></li>
                            <c:forEach items="${categories}" var="map">
                                <li><a id='${map.key}' onclick="setCategoryId('${map.key}', '${map.value}');
                                        return false;">${map.value}</a></li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12"><h4><strong>
                        <spring:message code="label.page-category.name" />
                    </strong></h4>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <input id="name" type="text" name="name" class="form-control form input-lg"
                               path="name" placeholder="Name" data-rule="minlen:5"
                               data-msg="Please enter at least 5 chars"
                               value='${name}'/>
                        <div class="validation"></div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-4"><h4><strong>
                        <spring:message code="label.page-category.type" />
                    </strong></h4>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-4 col-md-offset-2">
                        <div class="radio">
                            <label>
                                <input id="typeIncome" type="radio" name="optionsRadios"  value="income"
                                <c:if test="${not empty typeIncome}">
                                    checked=true
                                </c:if>
                                >
                                <spring:message code="label.page-category.typeIncome" />
                            </label>
                        </div>
                        <div class="radio">
                            <label>
                                <input id="typeExpense" type="radio" name="optionsRadios" value="expence"
                                <c:if test="${empty typeIncome}">
                                       checked=true
                                </c:if>
                                >
                                <spring:message code="label.page-category.typeExpence" />
                            </label>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12"><h4><strong>
                        <spring:message code="label.page-category.details" />
                    </strong></h4>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <textarea id="details" class="form-control input-lg" rows="5" placeholder="details">${details}</textarea>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6 col-md-offset-6">
                        <div class="btn-group">
                            <button id="btnNew" type="submit" name="btnNew"
                                    class="btn btn-default btn-lg " onclick="location.href='/page-category.html'">
                                <spring:message code="label.page-category.btnNew" />
                            </button>
                        </div>
                        <div class="btn-group">
                            <button id="btnDelete" type="submit" name="btnDelete"
                                    class="btn btn-default btn-lg " onclick="Delete();return false;">
                                <spring:message code="label.page-category.btnDelete" />
                            </button>
                        </div>
                        <div class="btn-group">
                            <button id="btnOk" type="submit" name="btnOk"
                                    class="btn btn-default btn-lg " onclick="Save();return false;">
                                <spring:message code="label.page-category.btnOk" />
                            </button>
                        </div>
                        <div class="btn-group">
                            <button id="btnCancel" type="submit" name="btnCancel"
                                    class="btn btn-default btn-lg " onclick="location.reload();">
                                <spring:message code="label.page-category.btnCancel" />
                            </button>
                        </div>
                        <script language="javascript" type="text/javascript">
                            function Save() {
                                var type = $('#typeIncome').prop('checked') ? 0 : 1;

                                var data = {
                                    'id': $('#id').val(),
                                    'parentId': $('#btnCategories').val(),
                                    'name': $('#name').val(),
                                    'type': type,
                                    'details': $('#details').val(),
                                };
                                $.ajax({
                                    type: "POST",
                                    url: '/page-category/save',
                                    data: data,
                                    dataType: 'application/json; charset=utf-8',
                                    success: function (data) {
                                        //                                    alert(data); TODO: modal?  charset?
                                    }
                                });

                                //Modal panel
                                ClearModalPanel();
                                $('#modalBody').append(
                                        "<h4><strong><spring:message code="label.page-category.modal.textSave" /></strong></h4>"
                                );
                                $('#modalFooter').append(
                                        "<button type='button' class='btn btn-default' data-dismiss='modal' " +
                                        "onclick='location.href=\"/page-category.html\";'>" +
                                        "<spring:message code="label.page-category.modal.btnYes" />" +
                                        "</button>" +
                                        "<button type='button' class='btn btn-primary' " +
                                        "onclick='location.href=\"/index.html#home\";'>" +
                                        "<spring:message code="label.page-category.modal.btnNo" />" +
                                        "</button>" +
                                        ""
                                );
                                $('#modal').modal('show');
                            };
                            function Delete(){
                                ClearModalPanel();
                                $('#modalBody').append(
                                        "<h4><strong><spring:message code="label.page-category.modal.textDelete" /></strong></h4>"
                                );
                                $('#modalFooter').append(
                                        "<button type='button' class='btn btn-default' data-dismiss='modal' " +
                                        "onclick='SendDeleteQuery();'>" +
                                        "<spring:message code="label.page-category.modal.btnYes" />" +
                                        "</button>" +
                                        "<button type='button' class='btn btn-primary' " +
                                        "data-dismiss='modal'>" +
                                        "<spring:message code="label.page-category.modal.btnNo" />" +
                                        "</button>" +
                                        ""
                                );
                                $('#modal').modal('show');
                            };
                            function SendDeleteQuery() {
                                var id = $('#id').val();
                                if (id != '') {
                                    $.ajax({
                                        type: "POST",
                                        url: '/page-category/delete',
                                        data: {id},
                                        dataType: 'text',
                                        success: function (data) {
                                            var response = $.parseJSON(data);
                                            ShowModal(response.message, "location.href='/index.html'");
                                        },
                                        error: function(data){
                                            var response = $.parseJSON(data);
                                            ShowModal("Что-то пошло не так." + data + ": " + response.message);
                                        }
                                    });
                                } else {
                                    ShowModal('Ошибка удаления: не найдено поле id записи.',
                                        'return false;');
                                }
                            };
                            function ClearModalPanel(){
                                $('[id^="modalBody"]').each(function () {
                                    $(this).empty();
                                });
                                $('[id^="modalFooter"]').each(function () {
                                    $(this).empty();
                                });
                            }
                            function ShowModal(message, onclick){
                                ClearModalPanel();
                                $('#modalBody').append(
                                        "<h4><strong>" + message + "</strong></h4>"
                                );
                                $('#modalFooter').append(
                                        "<button type='button' class='btn btn-default' data-dismiss='modal' " +
                                        "onclick=" + onclick + ">" +
                                        "Ok" +
                                        "</button>"
                                );
                                $('#modal').modal('show');
                            };
                        </script>
                    </div>
                </div>
            </section>
        </div>
    </div>
</div>


<div class="sub-footer">
    <div class="container">
        <div class="col-md-6 ">
            <div class="copyright text-right">
                &copy; Anyar Theme. All Rights Reserved.
                <div class="credits">
                    <!--
                        All the links in the footer should remain intact.
                        You can delete the links only if you purchased the pro version.
                        Licensing information: https://bootstrapmade.com/license/
                        Purchase the pro version with working PHP/AJAX contact form: https://bootstrapmade.com/buy/?theme=Anyar
                    -->
                    <a href="https://bootstrapmade.com/">Bootstrap Themes</a> by <a
                        href="https://bootstrapmade.com/">BootstrapMade</a>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>

