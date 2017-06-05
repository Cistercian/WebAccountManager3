<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">
<head>
    <meta charset="utf-8">
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>WebAccountManager</title>

    <!-- css -->
    <spring:url value="/resources/css/bootstrap.min.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <spring:url value="/resources/css/animate.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <spring:url value="/resources/css/dataTables.bootstrap.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <spring:url value="/resources/css/style.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <link href="https://fonts.googleapis.com/css?family=Roboto+Slab" rel="stylesheet">

    <!-- js -->
    <spring:url value="/resources/js/jquery.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/bootstrap.min.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/wow.min.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/functions.js" var="js"/>
    <script src="${js}"></script>

    <!--waitingDialog-->
    <spring:url value="/resources/js/waitingDialog.js" var="js"/>
    <script src="${js}"></script>

    <!--DataTables-->
    <spring:url value="/resources/js/jquery.dataTables.min.js" var="js"/>
    <script src="${js}"></script>

    <!--custon functions-->
    <spring:url value="/resources/js/web.account.functions.js" var="js"/>
    <script src="${js}"></script>

</head>
<body>

<!-- навигационная панель и модальное окно -->
<jsp:include page="/WEB-INF/view/tags/nav-panel.jsp"></jsp:include>

<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        $.ajaxSetup({
            headers: {'X-CSRF-TOKEN': document.getElementById('_csrf_token').value}
        });

        getAlerts();

    });
    function getAmounts(){
        $.ajax({
            url: '/statistic/compare/getCompareData',
            type: "GET",
            data: {
                'query': $('#name').val()
            },
            dataType: 'json',
            beforeSend: function () {
                displayLoader();

                $('#formGroup').removeClass('has-error');
                $('#formGroupError').text('');
                //$('#amounts').DataTable().fnDestroy();
            },
            success: function (data) {
                hideLoader();

                $('#minSum').text(data.minSum);
                $('#minDate').text(data.minDate);
                $('#maxSum').text(data.maxSum);
                $('#maxDate').text(data.maxDate);
                $('#avgSum').text(data.avgSum);

                var amounts = data.amounts;
                var tableData = new Array();
                amounts.forEach(function (amount, index, amounts) {
                    var entity = new Array();

                    entity[0] = amount.id;
                    entity[1] = amount.name;
                    entity[2] = amount.price;
                    entity[3] = amount.date;

                    tableData.push(entity);
                });

                var table = $('#amounts').DataTable({
                    responsive: true,
                    "bLengthChange": false,
                    language: {
                        "processing": "Подождите...",
                        "search": "Поиск:",
                        "lengthMenu": "Показать _MENU_ записей",
                        "info": "Записи с _START_ до _END_ (Всего записей: _TOTAL_).",
                        "infoEmpty": "Записи с 0 до 0 из 0 записей",
                        "infoFiltered": "(отфильтровано из _MAX_ записей)",
                        "infoPostFix": "",
                        "loadingRecords": "Загрузка записей...",
                        "zeroRecords": "Записи отсутствуют.",
                        "emptyTable": "В таблице отсутствуют данные",
                        "paginate": {
                            "first": "Первая",
                            "previous": "Предыдущая",
                            "next": "Следующая",
                            "last": "Последняя"
                        },
                        "aria": {
                            "sortAscending": ": активировать для сортировки столбца по возрастанию",
                            "sortDescending": ": активировать для сортировки столбца по убыванию"
                        }
                    },
                    data: tableData,
                    columns: [
                        { title: "id" },
                        { title: "Наименование" },
                        { title: "Цена" },
                        { title: "Дата" }
                    ],
                    "sort": true,
                    "order": [[1, "DESC"]],
                });

                $('#amounts_filter').empty();
                $('#amounts_filter').append(
                        "<div class='col-xs-2 col-md-4 wam-padding-left-0 wam-padding-right-0'>" +
                        "<h5>Поиск: </h5>" +
                        "</div>" +
                        "<div class='col-xs-10 col-md-8 wam-padding-left-0 wam-padding-right-0'>" +
                        "<input id='searchDataTable' type='text' class='form-control form' placeholder='' aria-controls='amounts'>" +
                        "</div>"
                );
                $('#searchDataTable').on( 'keyup', function () {
                    table.search( this.value ).draw();
                });
            },
            error: function (request, status, error) {
                hideLoader();

                $('#formGroup').addClass('has-error');
                $('#formGroupError').text('Возникла ошибка. Попробуйте повторить поиск позднее.');

                $('#minSum').text('0');
                $('#minDate').text('');
                $('#maxSum').text('0');
                $('#maxDate').text('');
                $('#avgSum').text('0');
            }
        });
    }
</script>

<div class="content container-fluid wam-radius wam-min-height-0 wow fadeInDown" data-wow-duration="1000ms"
     data-wow-delay="300ms">
    <div class='row'>
        <input id="_csrf_token" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input id="id" type="hidden" name="id" value="${id}"/>

        <div class="container-fluid wam-not-padding-xs">
            <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                <div class="panel-heading ">
                    <h3 class="wam-margin-bottom-0 wam-margin-top-0">Сравнение цен</h3>
                </div>
                <div class="wam-not-padding panel-body">
                    <div class="col-xs-12 col-md-12">
						<span class="wam-text text-justify">
							Здесь Вы можете получить статистику по ранее заведенным оборотам. Это удобно использовать при необходимости поиска оптимальной цены, например, стоя в магазине или выбирая заведение для посещения -
								задайте примерное наименование искомого оборота и Вы увидете статистику по ранее заведенным с подобным наименованием.
						</span>
                    </div>
                </div>
            </div>
            <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                <div class="wam-not-padding panel-body">
                    <div class="row">
                        <div class="col-xs-12 col-md-12">
                            <h4 class=''><strong>Укажите искомую строку</strong></h4>
                        </div>
                        <div class="col-xs-12 col-md-12">
                            <div id="formGroup" class="form-group ">
                                <input type="text" id="name" class="form-control input-lg wam-font-size" placeholder="Строка для поиска"/>
                                <span id="formGroupError" class="wam-text text-justify"></span>
                            </div>
                        </div>
                        <div class="col-xs-12 col-md-6 col-md-offset-6">
                            <button class="btn-primary btn-lg btn-block wam-btn-2"
                                    onclick="getAmounts()">
                                <spring:message code="label.index.refresh" />
                            </button>
                        </div>
                        <div class="col-xs-12 col-md-12">
                            <h4 class='wam-margin-top-2'><strong>Статистика:</strong></h4>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-xs-12 col-md-3">
                            <span class="wam-text text-justify">Средняя цена</span>
                        </div>
                        <div class="col-xs-12 col-md-2">
                            <span id="avgSum" class="wam-text text-justify"></span><span class="wam-text"> руб.</span>
                        </div>
                        <div class="col-xs-12 col-md-2">

                        </div>
                    </div>
                    <div class="row">
                        <div class="col-xs-12 col-md-3">
                            <span class="wam-text text-justify">Минимальная цена</span>
                        </div>
                        <div class="col-xs-12 col-md-2">
                            <span id="minSum" class="wam-text text-justify"></span><span class="wam-text"> руб.</span>
                        </div>
                        <div class="col-xs-12 col-md-2">
                            <span id="minDate" class="wam-text text-justify"></span>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-xs-12 col-md-3">
                            <span class="wam-text text-justify">Максимальная цена</span>
                        </div>
                        <div class="col-xs-12 col-md-2">
                            <span id="maxSum" class="wam-text text-justify"></span><span class="wam-text"> руб.</span>
                        </div>
                        <div class="col-xs-12 col-md-2">
                            <span id="maxDate" class="wam-text text-justify"></span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="panel panel-default wam-margin-left-1 wam-margin-right-1 wam-margin-top-1">
                <div class="panel-heading ">
                    <h4 class="wam-margin-bottom-0 wam-margin-top-0">Найденные обороты</h4>
                </div>
                <div class="panel-body">
                    <table id="amounts" class="table table-striped table-bordered table-text  wam-font-size wam-margin-top-2" cellspacing="0"
                           width="100%">
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
</div>


</body>
</html>

