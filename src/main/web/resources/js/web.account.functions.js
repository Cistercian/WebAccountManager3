/**
 * Created by Olaf on 30.04.2017.
 */
/**
 * Функция прорисовки прогресс баров
 * @param isChildren флаг рисуем ли дочерние бары
 * @param categoryId id текущей категории (при необходимости)
 * @param after начальная дата отсечки
 * @param before конечная дата отсечки
 */
function drawBarsByParentId(isChildren, categoryId, after, before, isGetAnalyticData) {
    //свитчер - либо показываем, либо стираем детализацию по дочерней категории
    if (($('*').is('#childrenCategory' + categoryId)) && isChildren) {
        $('#childrenCategory' + categoryId).remove();
        $('#strongCategory' + categoryId + 'children').remove();
        $('#strongCategory' + categoryId).text("(Развернуть)");
    }
    else {
        var url = categoryId != null ? '/getContentByCategoryId' : '/getCategoriesByDate';

        $.ajax({
            url: url,
            type: "GET",
            data: {
                'categoryId': categoryId,
                'after': after,
                'before': before,
                'isGetAnalyticData' : isGetAnalyticData,
            },
            dataType: 'json',
            beforeSend: function () {
                displayLoader();
            },
            success: function (data) {
                hideLoader();

                var dataClass = 'Category'; //задел на будущее - вдруг придется выводить подитоги по другим данным
                var maxSum = 0;
                var totalSum = 0;

                if (!isChildren) {
                    ClearModal();
                    idBarElem = 'parentBars';
                    idNameElem = 'categoryBarName' + categoryId;
                    tagClassProgress = "";

                    if (categoryId != null)
                        categoryName = "<div class='row'>" +
                            "<div class='col-xs-12'>" +
                            "<p class='wam-font-size text-muted'>Детализация категории</p>" +
                            "</div>" +
                            "<div class='col-xs-12'>" +
                            "<p class=' wam-margin-bottom-0'><h3 class='wam-margin-top-1'><strong class='pull-right'>" +
                            $('#' + idNameElem).attr('value') +
                            "</h3></strong></p>" +
                            "</div>" +
                            "<div class='col-xs-12'>" +
                            "<p class='wam-font-size pull-right wam-margin-bottom-0'>" +
                            "<a href='/category?id=" + categoryId + "'>(редактировать)</a>" +
                            "</p>" +
                            "</div>" +
                            "</div>";
                    else
                        categoryName = "<div class='row'>" +
                            "<div class='col-xs-12'>" +
                            "<p class='wam-font-size text-muted'>Детализация за дату</p>" +
                            "</div>" +
                            "<div class='col-xs-12'>" +
                            "<p class=' wam-margin-bottom-0'><h3 class='wam-margin-top-1'><strong class='pull-right'>" +
                            moment(after).locale('ru').format('DD MMMM YYYY') +
                            "</h3></strong></p>" +
                            "</div>" +
                            "<div class='col-xs-12'>" +
                            "<p class='wam-font-size pull-right wam-margin-bottom-0'>" +
                            "<a href='/statistic/calendar/getAmountsByDate?start=" +
                            after + "&end=" + after + "'>(Просмотреть)</a>" +
                            "</p>" +
                            "</div>" +
                            "</div>";

                    //заголовок
                    $('#header').append(
                        "<h4 class='wam-not-padding-xs'>" + categoryName + "</h4>"
                    );

                } else {
                    idBarElem = 'childrenCategory' + categoryId;

                    $('#progressBarCategory' + categoryId).append("<div id='" + idBarElem + "' class='wam-margin-left-3'>");

                    //totalSum = $('#' + 'barSum' + dataClass + categoryId).attr('value');
                    idNameElem = 'barName' + dataClass + categoryId;
                    tagClassProgress = "wam-progress-height-2";
                    maxSum = $('#' + 'barSum' + dataClass + categoryId).attr('value');
                    categoryName = "Детализация по категории: " + $('#' + idNameElem).attr('value');

                    //заголовок
                    $('#childrenCategoryDetails').append(
                        "<h4><span>" + categoryName + " <a href='/category?id=" + categoryId +
                        "'>(редактировать)</a></span></h4>"
                    );
                    $('#strongCategory' + categoryId).text("(Свернуть, ");
                    $('#strongCategory' + categoryId).parent().parent()
                        .append("<span id='strongCategory" + categoryId + "children'>" +
                            "<a href='/category?id=" + categoryId + "'>редактировать)</a></span>");
                }


                //данные для стилей прогресс баров
                var styles = ['success', 'info', 'warning', 'danger'];
                var curNumStyle = -1;
                var totalSum, totalLimit = 0;

                data.forEach(function (barData, index, data) {
                    var classId = barData.id;
                    var classType = barData.type;
                    var classTitle = "";
                    var className = barData.name;
                    var classSum = barData.sum;
                    var classLimit = barData.limit;

                    totalSum += classSum;
                    totalLimit += classLimit;

                    // нормализуем суммы
                    if (!isGetAnalyticData) {
                        if (maxSum == 0) maxSum = classSum;
                        normalSum = classSum * 100 / maxSum;
                        // меняем цвет баров
                        curNumStyle = curNumStyle < 4 ? curNumStyle + 1 : 0;
                    } else {
                        if (maxSum == 0) maxSum = classSum;
                        normalSum = classLimit != 0 ? classSum * 100 / classLimit : 101;
                        // меняем цвет баров
                        if (normalSum > 100)
                            curNumStyle = 3;
                        else
                            curNumStyle = 0;
                    }


                    var elemLink;
                    if (classType == 'Product') {
                        classTitle = "Группа товаров";
                        if (!isGetAnalyticData) {
                            elemLink = "<a href='/page-product/" + classId + "?categoryId=" + categoryId + "&after=" + after + "&before=" + before + "'>" +
                                "<span> (Просмотреть</span>" +
                                "</a>" + ", " +
                                "<a href='/product?id=" + classId + "'>" +
                                "<span>редактировать)</span>" +
                                "</a>";
                        } else {
                            elemLink = "<span>(Просмотреть </span><a href='/page-product/" + classId + "?categoryId=" + categoryId + "&after=" + after + "&before=" + before + "'>" +
                                "<span>средние обороты</span>" +
                                "</a>" + ", " +
                                "<a href='/page-product/" + classId + "?categoryId=" + categoryId + "&after=" + moment(new Date().setDate(1)).format('YYYY-MM-DD') + "&before=" + moment(new Date()).format('YYYY-MM-DD') + "'>" +
                                "<span> текущие обороты</span>" +
                                "</a>" + " или " +
                                "<a href='/product?id=" + classId + "'>" +
                                "<span>редактировать)</span>" +
                                "</a>";
                        }
                    }
                    if (classType.indexOf('Category') + 1) {
                        classTitle = "Категория";
                        classType = 'Category';
                        elemLink = "<a href='javascript:drawBarsByParentId(true, " + classId + "," +
                            "\"" + after + "\"," +
                            "\"" + before + "\"," +
                            isGetAnalyticData + ")';\">" +
                            "<span id='strong" + classType + classId + "'> (Развернуть)</span>" +
                            "</a>";
                    }
                    //добавляем прогресс бар
                    $('#' + idBarElem).append(
                        "<li id='progressBar" + classType + classId + "' class='list-unstyled'>" +
                        "<div class='row'><div class='col-xs-12'><h4 class='wam-margin-top-1 wam-margin-bottom-0 needToFormat'>" +
                        "<span class='wam-font-size text-muted'>" + classTitle + ": </span>" +
                        "<span id='barName" + classType + classId + "' value='" + className + "' class='wam-font-size-2'>" +
                        className + "" +
                        "</span></div></div>" +
                        "<div class='row'><div class='col-xs-12'>" + elemLink + "" +
                        "<strong id='barSum" + classType + classId + "' class='pull-right text-muted' " +
                        "value='" + classSum + "'>" +
                        numberToString(classSum) + (isGetAnalyticData ? " (в среднем " + numberToString(classLimit) + ") " : "") + " руб." +
                        "</strong>" +
                        "</h4></div></div>" +
                        "<div class='progress " + tagClassProgress + " progress-striped active wam-margin-bottom-1' >" +
                        "<div class='progress-bar progress-bar-" + styles[curNumStyle] + "' role='progressbar' " +
                        "aria-valuenow='" + classSum + "'" + "aria-valuemin='0' aria-valuemax='100' " +
                        "style='width: " + Math.abs(normalSum) + "%' " + "value='" + className +
                        "'>" +
                        "<span class='sr-only'>" +
                        numberToString(classSum) +
                        "</span>" +
                        "</div>" +
                        "</div>" +
                        "</li>" +
                        "");
                });
                totalSum = totalSum.toFixed(2);
                totalLimit = totalLimit.toFixed(2);
                $('#' + idBarElem).append(
                    "<div class='row'>" +
                    (isChildren ? "<div class='col-md-12 wam-margin-bottom-2'><h5><span>" : "<div class='col-md-12'><h4><strong class='pull-right text-muted'>") +
                    "ИТОГО " + numberToString(totalSum) + (isGetAnalyticData ? "( в среднем " + numberToString(totalLimit) + ") " : "") + " руб." +
                    "</strong>" + (isChildren ? "</h5>" : "</h4>") +
                    "</div><p><p>" +
                    "</div>"
                );

                //показываем модальное окно
                if (!isChildren)
                    $('#modal').modal('show');
            }
        });
    }
}
/**
 * функция очистки модального окна bootstrap
 * @constructor
 */
function ClearModal() {
    //удаляем прежние amount
    $('[id^="modalBody"]').each(function () {
        $(this).empty();
    });
    $('#modalHeader').empty();
    $('#modalHeader').append(
        "<div class='login-panel panel panel-default wam-not-padding '>" +
        "<div id='header' class='panel-heading'>" +
        "</div>" +
        "</div>");

    //рисуем структуру вывода данных
    $('#modalBody').append(
        "<div class='login-panel panel panel-default wam-not-padding '>" +
        "<div class='panel-body wam-not-padding'>" +
        "<div id='parentBars'" +
        "</div>" +
        "</div>" +
        "</div>");
}
/**
 * Функция прорисовки круговой диаграммы
 * @param data данные в формате "наименование=сумма,наименование=сумма"
 * @param elementId id элемента, где следует нарисовать диаграмму
 */
function drawChartOfTypes(data, elementId) {
    var parent = $('#' + elementId).parent();
    parent.empty();
    parent.append(
        "<h2><spring:message code='label.index.chartIncomeExpense.title' /></h2>" +
        "<canvas id='typeChart' style=''></canvas>" +
        "<img id='chartNaN' src='/resources/img/web.png' class='img-responsive' alt='' style='display: none;'>");

    var pieChartCanvas = $('#' + elementId).get(0).getContext('2d');
    var pieChart = new Chart(pieChartCanvas);
    var PieData = [];

    var colors = ['#5cb85c', '#f0ad4e', '#f0ad4e', '#d9534f'];
    var curNumStyle = -1;

    var array = data.split(',');
    var count = 0;
    var totalSum = 0
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

        totalSum += sum;
        count++;
    });

    if (totalSum == 0) {
        $('#chartNaN').show();
        $('#typeChart').hide();
    } else {
        $('#chartNaN').hide();
        $('#typeChart').show();
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
}
/**
 * функция форматирования числа в строковый формат с разделителем групп разрядов
 * @param number исходное число
 * @returns {string} отформатированная строка
 */
function numberToString(number) {
    return (number + '').replace(/(\d)(?=(\d\d\d)+([^\d]|$))/g, '$1 ');
}
/**
 * Функция вызова waitingDialog.js при выполнении ajax запросов
 */
function displayLoader() {
    waitingDialog.show('Загрузка...', {dialogSize: 'sm', progressType: 'warning'});
}
function hideLoader() {
    waitingDialog.hide();
}
/**
 * Функция очистки модального окна bootstrap
 * @constructor
 */
function ClearModalPanel() {
    //$('#modal').modal('hide');

    $('#modalTitle').text("");
    $('[id^="modalBody"]').each(function () {
        $(this).empty();
    });
    $('[id^="modalFooter"]').each(function () {
        $(this).empty();
    });
    //гарантированно чистим остатки всплывающего окна
    $('.modal-backdrop').each(function () {
        $(this).remove();
    });
    //гарантированно-гарантированно чистим остатки всплывающего окна
    $('body').removeClass('modal-open');

}
/**
 *Функция удаления записи БД
 *
 */
function Delete(className, id) {
    ClearModalPanel();

    $('#modal').modal('hide');
    $('#modalBody').append(
        "<div class='col-xs-12'>" +
        "<h4><strong>Вы действительно хотите удалить запись?</strong></h4>" +
        "</div>"
    );
    $('#modalFooter').append(
        "<div class='col-xs-12 col-md-4 col-md-offset-4 wam-not-padding'>" +
        "<button type='button' class='btn btn-default btn-lg btn-block ' " +
        "onclick=\"SendDeleteQuery('" + className + "', '" + id + "');\">" +
        "Да" +
        "</button>" +
        "</div>" +
        "<div class='col-xs-12 col-md-4 wam-not-padding'>" +
        "<button type='button' class='btn btn-primary btn-lg btn-block ' data-dismiss='modal'>" +
        "Нет" +
        "</button>" +
        "</div>"
    );

    $('#modal').modal('show');
}
;
function SendDeleteQuery(className, id) {
    ClearModalPanel();
    var data = {
        'className': className,
        'id': (id == '' ? $('#id').val() : id),
    }
    $.ajax({
        type: "POST",
        url: '/page-data/delete',
        data: data,
        beforeSend: function () {
            displayLoader();
        },
        success: function (data) {
            hideLoader();

            var type = data.type;
            var message = data.message;

            displayMessage(type, message, "/index");
        }
    });
}
function getAlerts() {
    $.ajax({
        type: "GET",
        url: '/index/getAlerts',
        data: {},
        success: function (data) {
            $.each(data, function(index, alert) {
                $('#alerts').append(
                    "<div class='wam-margin-top-1 wam-hidden alert alert-warning alert-dismissable'>" +
                    "<button type='button' class='close' aria-hidden='true' onclick='javascript:$(this).parent().fadeOut(\"slow\");'>" +
                    "&times;" +
                    "</button>" +
                    "<p>Новое сообщение: <a href=\"/account\">" + alert + "</a></p>" +
                    "</div>"
                );
            })

            $('.alert').each(function() {
                $(this).fadeIn('slow');
            });
        }
    });
}
function sendMail(){
    $.ajax({
        type: "POST",
        url: '/account/sendMail',
        data: {
            'username' : $('#userId').val(),
            'title' : $('#title').val(),
            'text' : $('#text').val(),
        },
        beforeSend: function () {
            displayLoader();
        },
        success: function (data) {
            hideLoader();

            displayMessage(data.type, data.message, "");
        }
    });
}
//formatting text for mobile
function formatTooLongText(){
    $('.needToFormat').each(function () {
        //var parentWidth = $(this).offsetParent().width();
        var parentWidth = $(this).parent().width();
        var currentWidth = 0;
        var countChars = 0;
        $(this).children().each(function () {
            currentWidth += $(this).width();
            countChars += $(this).text().trim().length;
        });

        if (currentWidth > (parentWidth * 0.9)) {
            var size = countChars - parentWidth * countChars / currentWidth + 4;
            var childClass = '';
            if ($(this).children().is('strong')) {
                childClass = 'strong';
            } else if ($(this).children().is('a')) {
                alert('a');
                childClass = 'a';
            }
            var length = $(this).children(childClass).text().trim().length;
            $(this).children(childClass).text($(this).children(childClass).text().trim().substring(0, Math.floor(length - size)) + '...');
        }
    });
}
function getManualForm(page) {
    $('#modal').modal('hide');
    $.ajax({
        type: "GET",
        url: '/account/getManualForm',
        data: {'page' : page},
        beforeSend: function () {
            displayLoader();
        },
        success: function (data) {
            hideLoader();
            var message = data;

            displayManual(message, page);

        }
    });
}
function displayManual(message, pageNum) {
    ClearModalPanel();
    $('#modalBody').append(
        message
    );
    var prevNum = pageNum - 1;
    if (prevNum > 0)
        $('#modalFooter').append(
            "<div class='col-xs-12 col-md-4 wam-not-padding'>" +
            "<buttontype='button' class='btn btn-default btn-lg btn-block' data-dismiss='modal'  " +
            "onclick='getManualForm(" + prevNum + ");return false;'>" +
            "Назад" +
            "</button>" +
            "</div>"
        );
    else
        $('#modalFooter').append(
            "<div class='col-xs-12 col-md-4 wam-not-padding'>" +
            "<button href='#' type='button' class='btn btn-default btn-lg btn-block disabled' >" +
            "Назад" +
            "</button>" +
            "</div>"
        );

    $('#modalFooter').append(
        "<div class='col-xs-12 col-md-4 wam-not-padding'>" +
        "<button href='#' type='button' class='btn btn-default btn-lg btn-block' data-dismiss='modal' " +
        ">" +
        "Закрыть" +
        "</button>" +
        "</div>"
    );
    var nextNum = pageNum + 1;
    if (nextNum < 10)
        $('#modalFooter').append(
            "<div class='col-xs-12 col-md-4 wam-not-padding'>" +
            "<button class='btn btn-primary btn-lg btn-block'  " +
            "onclick='getManualForm(" + nextNum + ");return false;'>" +
            "Вперед" +
            "</button>" +
            "</div>"
        );
    else
        $('#modalFooter').append(
            "<div class='col-xs-12 col-md-4 wam-not-padding'>" +
            "<button type='button' class='btn btn-primary btn-lg btn-block disabled' >" +
            "Вперед" +
            "</button>" +
            "</div>"
        );
    $('#modal').modal('show');
}
function setModalSize(type){
    if (type.indexOf("auto") !== -1) {
        //форматируем модальное окно под авторазмер
        $('.modal-lg').addClass('wam-modal-dialog');
        $('.modal-lg').removeClass('modal-dialog');
    } else {
        $('.modal-lg').addClass('modal-dialog');
        $('.modal-lg').removeClass('wam-modal-dialog');
    }
}
function freezeBody(){
    $('#modal')
        .on('show.bs.modal', function (){
            $('body').addClass('wam-body-freeze');
        })
        .on('hide.bs.modal', function (){
            $('body').removeClass('wam-body-freeze');
        });
}
/**Функция очищает данные по категориям
 */
function removeStatistics(){
    $('#textTotalIncome').empty();
    $('#textTotalExpense').empty();

    $('#dropDownCategoryBarsIncome').empty();
    $('#dropDownCategoryBarsExpense').empty();
}
function refreshBars(data, after, before){
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
        var sum = Math.abs(barData.sum);

        if (type == 'CategoryIncome') {
            idBarsElem = 'dropDownCategoryBarsIncome';
            maxIncomeSum = maxIncomeSum == 0 ? sum : maxIncomeSum;
            normalSum = sum * 100 / maxIncomeSum;
            totalIncomeSum += sum;
            curNumStyle = 0;
        } else {
            idBarsElem = 'dropDownCategoryBarsExpense';
            maxExpenseSum = maxExpenseSum == 0 ? sum : maxExpenseSum;
            normalSum = sum * 100 / maxExpenseSum;
            totalExpenseSum += sum;
            curNumStyle = 2;
        }

        $('#' + idBarsElem).append(
            "<li class='list-unstyled'>" +
            "<a href='javascript:drawBarsByParentId(false, \"" + id + "\",\"" + after + "\",\"" + before + "\", false);'>" +
            "<div>" +
            "<h4 class='needToFormat'><strong id='categoryBarName" + id + "' value='" + name + "'>" +
            name +
            "</strong>" +
            "<span id='categoryBarSum" + id + "' class='pull-right text-muted' " +
            "value='" + sum + "'>" +
            numberToString(sum) + " руб." +
            "</span></h4>" +
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
    if (totalIncomeSum == 0)
        $('#dropDownCategoryBarsIncome').append(
            "<div class='col-xs-12 col-md-12'>" +
            "<h4><span class='text-muted'>Данные отсутствуют</span></h4>" +
            "</div>"
        );
    if (totalExpenseSum == 0)
        $('#dropDownCategoryBarsExpense').append(
            "<div class='col-xs-12 col-md-12'>" +
            "<h4><span class='text-muted'>Данные отсутствуют</span></h4>" +
            "</div>"
        );

    if ($('*').is('#textTotalIncome')) {
        $('#textTotalIncome').append("<span class='pull-right'><strong>" + numberToString(totalIncomeSum.toFixed(2)) + "</strong> руб.</span>");
        $('#textTotalExpense').append("<span class='pull-right'><strong>" + numberToString(totalExpenseSum.toFixed(2)) + "</strong> руб.</span>");

        //рисуем диаграмму
        drawChartOfTypes("Доход=" + totalIncomeSum + "," +
            "Расход=" + totalExpenseSum + "",
            "typeChart");
    }
}
function getMonday(date) {
    var day = date.getDay() || 7;
    if( day !== 1 )
        date.setHours(-24 * (day - 1));
    return date;
}
function scrollPage(destination){
    $('html, body').stop().animate({
        scrollTop: destination - 50
    }, 1000, 'easeInOutExpo');
}