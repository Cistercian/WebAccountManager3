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
        $.ajax({
            url: '/page-amount/getCategoriesIdAndName',
            success: function (data) {
                $.each(data, function (categoryId, categoryName) {
                    $('#dropdownCategories').append(
                            "\<li>\<a id=\"" + categoryId + "\" onclick=\"setCategoryId(" + categoryId + ", '" + categoryName +
                            "');return false;\">" + categoryName + "\<\/a>\<\/li>");
                });
            }
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
                <h4 id="mpdalcategoryTitle" class="modal-title"><spring:message code="label.page-amount.modal.title" /></h4>
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
            <section id='sectionAmount'>
                <div class='page-header'>
                    <h2><spring:message code="label.page-amount.title" /></h2>
                </div>
                <div class="row">
                    <div class="col-md-2"><h4><strong><spring:message code="label.page-amount.date" /></strong></h4>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-2">
                        <input id="date" type="date" class="form-control input-lg" name="date"
                            path="date" placeholder="Amount Date" data-rule="minlen:5"
                            data-msg="Please enter at least 5 chars"
                            value='${date}' />
                        <div class="validation"></div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12"><h4><strong><spring:message code="label.page-amount.name" /></strong></h4>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <input id="name" type="text" name="name" class="form-control form input-lg"
                               path="name" placeholder="Amount Name" data-rule="minlen:5"
                               data-msg="Please enter at least 5 chars"
                               value='${name}'/>
                        <div class="validation"></div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-10"><h4><strong><spring:message code="label.page-amount.category" /></strong></h4>
                    </div>
                    <div class="col-md-2"><h4><strong><spring:message code="label.page-amount.price" /></strong></h4>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-10">
                        <button id="btnCategories" class="btn-default btn-lg btn-block dropdown-toggle"
                                data-toggle="dropdown"><spring:message code="label.page-amount.selectCategory" />
                            <span class="caret"></span>
                        </button>
                        <ul id="dropdownCategories" class="dropdown-menu">
                            <li><a onclick="setCategoryId(-1, 'Select Category');return false;"><spring:message code="label.page-amount.selectCategory" /></a></li>
                            <li class="divider"></li>
                        </ul>
                    </div>
                    <div class="col-md-2">
                        <input id="price" type="number" class="form-control input-lg" name="price"
                               path="price" placeholder="Amount price" data-rule="number"
                               data-msg="Please enter a valid price"
                               value='${price}'/>
                        <div class="validation"></div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12"><h4><strong><spring:message code="label.page-amount.details" /></strong></h4>
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
                                    class="btn btn-default btn-lg " onclick="return false;"><spring:message code="label.page-amount.btnNew" />
                            </button>
                        </div>
                        <div class="btn-group">
                            <button id="btnDelete" type="submit" name="btnDelete"
                                    class="btn btn-default btn-lg " onclick="Delete();return false;"><spring:message code="label.page-amount.btnDelete" />
                            </button>
                        </div>
                        <div class="btn-group">
                            <button id="btnOk" type="submit" name="btnOk"
                                    class="btn btn-default btn-lg " onclick="Save();return false;"><spring:message code="label.page-amount.btnOk" />
                            </button>
                        </div>
                        <div class="btn-group">
                            <button id="btnCancel" type="submit" name="btnCancel"
                                    class="btn btn-default btn-lg "><spring:message code="label.page-amount.btnCancel" />
                            </button>
                        </div>
                        <script language="javascript" type="text/javascript">
                            function Save() {
                                var data = {
                                    'id': document.getElementById('id').value,
                                    'categoryId': document.getElementById('btnCategories').value,
                                    'name': document.getElementById('name').value,
                                    'price': document.getElementById('price').value,
                                    'amountsDate': document.getElementById('date').value,
                                    'details': document.getElementById('details').value,
                                    'submitAmmount': '' //TODO: ??
                                };
                                $.ajax({
                                    type: "POST",
                                    url: '/page-amount/amount/save',
                                    data: data,
                                    success: function (data) {
                                        //                                    alert(data); TODO: modal?
                                    }
                                });

                                //Modal panel
                                ClearModalPanel();
                                $('#modalBody').append(
                                        "<h4><strong><spring:message code="label.page-amount.modal.textSave" /></strong></h4>"
                                );
                                $('#modalFooter').append(
                                        "<button type='button' class='btn btn-default' data-dismiss='modal' " +
                                        "onclick='location.href=\"/page-amount.html\";'>" +
                                        "<spring:message code="label.page-amount.modal.btnYes" />" +
                                        "</button>" +
                                        "<button type='button' class='btn btn-primary' " +
                                        "onclick='location.href=\"/index.html#home\";'>" +
                                        "<spring:message code="label.page-amount.modal.btnNo" />" +
                                        "</button>" +
                                        ""
                                );
                                $('#modal').modal('show');
                            };
                            function Delete(){
                                ClearModalPanel();
                                $('#modalBody').append(
                                        "<h4><strong><spring:message code="label.page-amount.modal.textDelete" /></strong></h4>"
                                );
                                $('#modalFooter').append(
                                        "<button type='button' class='btn btn-default' data-dismiss='modal' " +
                                        "onclick='location.href=\"/page-amount/amount/" +
                                        document.getElementById('id').value + "/delete\";'>" +
                                        "<spring:message code="label.page-amount.modal.btnYes" />" +
                                        "</button>" +
                                        "<button type='button' class='btn btn-primary' " +
                                        "data-dismiss='modal'>" +
                                        "<spring:message code="label.page-amount.modal.btnNo" />" +
                                        "</button>" +
                                        ""
                                );
                                $('#modal').modal('show');
                            };
                            function ClearModalPanel(){
                                $('[id^="modalBody"]').each(function () {
                                    $(this).empty();
                                });
                                $('[id^="modalFooter"]').each(function () {
                                    $(this).empty();
                                });
                            }
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

