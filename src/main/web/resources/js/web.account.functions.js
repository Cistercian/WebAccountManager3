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
function drawBarsByParentId(isChildren, categoryId, after, before) {
    //свитчер - либо показываем, либо стираем детализацию по дочерней категории
    if (($('*').is('#childrenCategory' + categoryId)) && isChildren) {
        $('#childrenCategory' + categoryId).remove();
    }
    else {
        var url = categoryId != null ? '/getContentByCategoryId' : '/getCategoriesByDate';

        $.ajax({
            url: url,
            type: "GET",
            data: {
                'categoryId' : categoryId,
                'after' : after,
                'before' : before
            },
            dataType: 'json',
            success: function (data) {
                var dataClass = 'Category'; //задел на будущее - вдруг придется выводить подитоги по другим данным
                var maxSum = 0;
                var totalSum = 0;

                if (!isChildren) {
                    ClearModal();
                    idBarElem = 'parentBars';
                    idNameElem = 'categoryBarName' + categoryId;
                    idHeaderElem = 'modalHeader';
                    tagHeader = "h3";
                    tagClassProgress = "";

                    if (categoryId != null)
                        categoryName = $('#' + idNameElem).attr('value');
                    else
                        categoryName = "Детализация за дату " + after;
                    //totalSum = $('#' + 'categoryBarSum' + categoryId).attr('value');
                } else {
                    idBarElem = 'childrenCategory' + categoryId;

                    $('#progressBarCategory' + categoryId).append("<div id='" + idBarElem + "' >");

                    //totalSum = $('#' + 'barSum' + dataClass + categoryId).attr('value');
                    idNameElem = 'barName' + dataClass + categoryId;
                    idHeaderElem = 'childrenCategoryDetails';
                    tagHeader = "h4";
                    tagClassProgress = "mini";
                    maxSum = $('#' + 'barSum' + dataClass + categoryId).attr('value');
                    categoryName = "Детализация по категории: " + $('#' + idNameElem).attr('value');
                }
                //заголовок
                $('#' + idHeaderElem).append(
                    "<" + tagHeader + ">" + categoryName + " <a href='/page-data/display/category/" + categoryId +
                    "'>(редактировать)</a></" + tagHeader + ">"
                );

                //данные для стилей прогресс баров
                var styles = ['success', 'info', 'warning', 'danger'];
                var curNumStyle = -1;

                data.forEach(function (barData, index, data) {
                    var classId = barData.id;
                    var classType = barData.type;
                    var classTitle = "";
                    var className = barData.name;
                    var classSum = barData.sum;

                    totalSum += classSum;

                    <!-- нормализуем суммы -->
                    if (maxSum == 0) maxSum = classSum;
                    normalSum = classSum * 100 / maxSum;
                    <!-- меняем цвет баров -->
                    curNumStyle = curNumStyle < 4 ? curNumStyle + 1 : 0;

                    var elemLink;
                    if (classType == 'Product') {
                        classTitle = "Товарная группа";
                        //elemLink = 	"<a href='javascript:getViewProduct(" + classId + ");'>" +
                        elemLink = 	"<a href='/page-product/" + classId + "?after=" + after + "&before=" + before + "'>" +
                                        "<strong> (Просмотреть</strong>" +
                                    "</a>" + ", " +
                                    "<a href='/page-data/display/product/" + classId + "'>" +
                                        "<strong>редактировать)</strong>" +
                                    "</a>";
                    }
                    if (classType.indexOf('Category') + 1) {
                        classTitle = "Категория";
                        classType = 'Category';
                        elemLink = 	"<a href='javascript:drawBarsByParentId(true, " + classId + "," +
                                            "\"" + after + "\"," +
                                            "\"" + before +"\")';\">" +
                                        "<strong> (Развернуть)</strong>" +
                                    "</a>";
                    }
                    <!-- добавляем прогресс бар -->
                    $('#' + idBarElem).append(
                        "<li id='progressBar" + classType + classId + "'>" +
                            "<h5>" +
                                "<strong id='barName" + classType + classId + "' value='" + className + "'>" +
                                    classTitle + ": " + className + "" +
                                "</strong>" +
                                    elemLink +
                                "<strong id='barSum" + classType + classId + "' class='pull-right text-muted' " +
                                    "value='" + classSum + "'>" +
                                    numberToString(classSum) + " руб." +
                                "</strong>" +
                            "</h5>" +
                            "<div class='progress " + tagClassProgress + " progress-striped active' >" +
                                "<div class='progress-bar " + tagClassProgress +
                                    " progress-bar-" + styles[curNumStyle] + "' role='progressbar' " +
                                    "aria-valuenow='" + classSum + "'" + "aria-valuemin='0' aria-valuemax='100' " +
                                    "style='width: " + normalSum + "%' " + "value='" + className +
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
                $('#' + idBarElem).append(
                    "<div class='row'>" +
                    "<div class='col-md-12'><h6><strong>" +
                    "ИТОГО " + numberToString(totalSum) + " руб." +
                    "</strong></h6>" +
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
    //рисуем структуру вывода данных
    $('#modalBody').append(
        "<div id='parentBars'" +
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
        "<canvas id='typeChart' style='height:250px'></canvas>");

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
/**
 * функция форматирования числа в строковый формат с разделителем групп разрядов
 * @param number исходное число
 * @returns {string} отформатированная строка
 */
function numberToString(number){
    return (number + '').replace(/(\d)(?=(\d\d\d)+([^\d]|$))/g, '$1 ');
}
