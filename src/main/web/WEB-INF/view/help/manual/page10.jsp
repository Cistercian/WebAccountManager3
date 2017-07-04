<%--
  Created by IntelliJ IDEA.
  User: d.v.hozyashev
  Date: 31.05.2017
  Time: 15:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<spring:message code="label.account.limit.selectName" var="label"/>

<div class="container-fluid wam-not-padding">
    <div class="row">
        <div class="login-panel panel panel-default wam-not-padding wam-margin-left-2 wam-margin-right-2">
            <div class="panel-heading">
                <h3 class="wam-margin-bottom-0 wam-margin-top-0">Прогнозирование движений</h3>
            </div>
            <div class="panel-body wam-not-padding ">
                <div class="col-xs-12 col-md-12 wam-not-padding text-justify">
                    <p class="wam-margin-top-2 ">Страница доступна в меню:</p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <img src="/resources/img/help/analytic_menu.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt="">
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding text-justify">
                    <p class="wam-margin-top-2 ">Здесь Вы можете ознакомиться с прогнозируемыми данными, ожидаемыми на конец текущего
                        месяца, и суммами среднемесячных оборотов по предыдущим месяцам с разбивкой по категориям и группам товаров. Функционал
                        полезен при планировании ожидаемых трат.
                    </p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding text-justify">
                    <p class="wam-margin-top-2 ">
                        Пример таблицы с прогнозируемыми данными:
                    </p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <img src="/resources/img/help/analytic_table1.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt="">
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding text-justify">
                    <p class="wam-margin-top-2 ">В таблице приводятся прогнозируемые суммы на конец месяца по оборотам в разбивке по категориям,
                        для сравнения здесь же указаны среднемесячные данные и их процентное соотношение. Вы можете развернуть каждую категорию и видеть
                        из каких сумм получается итоговое значение. Здесь же нажав на нужным тип оборотов Вы можете отредактировать его состав.
                    </p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding text-justify">
                    <p class="wam-margin-top-2 ">
                        Алгоритм расчета прогнозируемых данных заключается в суммировании стоимости оборотов текущего месяца 3 типов:
                    </p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <ul>
                        <li class="text-justify">
                            <p class="wam-margin-top-2">Сумма обычных оборотов, умноженная на специальный коэфициент.</p>
                            <p>Сюда должны попадать постоянные обороты, которые носят периодический характер в течении всего месяца
                                (напр., продукты, расходы на транспорт). Т.е., если на сегодня (напр., 15 июня) Вы потратили 5 000 рублей, то к концу июня,
                                сумма затрат будет 10 000 руб (делается допущение, что за оставшиеся дни месяца Вы будете тратить так же, как и до этого).
                            </p>
                        </li>
                        <li class="text-justify">
                            <p class="wam-margin-top-2">Удиноразовые обороты</p>
                            <p>Сюда должны попадать обороты, сумма которых не должна умножаться на коэффициент. Например, плата за коммунальные услуги или
                                заработная плата. Т.е. те обороты, которые не повторяются в течении месяца.
                            </p>
                        </li>
                        <li class="text-justify">
                            <p class="wam-margin-top-2">Обязательные обороты</p>
                            <p>Существуют обороты, которые обязательно будут появляться в течении месяца (заработная плата, плата по кредитам и т.д.). Т.е. обороты,
                                которые сразу можно принимать в расчет месячного результата, не важно произошли ли они уже или еще нет. Их сумма так же не умножается на
                                коэффициент.
                            </p>
                        </li>
                    </ul>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding text-justify">
                    <p class="wam-margin-top-2 ">
                        Далее на странице приводятся графики, которые позволяют увидеть соотношение текущих оборотов и средних за предыдущие месяцы.
                        При необходимости Вы можете развернуть категорию и увидеть детализацию в разбивке по подкатегориям и группам товаров.
                    </p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <img src="/resources/img/help/analytic_table2.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt="">
                </div>
            </div>
        </div>
    </div>
</div>
