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
                <h3 class="wam-margin-bottom-0 wam-margin-top-0"><spring:message code="label.manual.page2.title"/></h3>
            </div>
            <div class="panel-body wam-not-padding ">
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">Сайт манипулирует данными 3-х типов:</p>
                    <div class="col-xs-12 col-md-12 wam-not-padding">
                        <div class="col-xs-2 col-md-2 wam-not-padding">
                            <img src="/resources/img/money_mini.png" class="wam-img-xs-1" alt="">
                        </div>
                        <div class="col-xs-10 col-md-10 wam-not-padding">
							<span class="text-justify">
								обороты - конкретные траты или поступления (отдельно взятая покупка или получение денежных средств).
							</span>
                        </div>
                    </div>
                    <div class="col-xs-12 col-md-12 wam-not-padding">
                        <div class="col-xs-2 col-md-2 wam-not-padding">
                            <img src="/resources/img/group_mini.png" class="wam-img-xs-1" alt="">
                        </div>
                        <div class="col-xs-10 col-md-10 wam-not-padding">
							<span class="text-justify">
								товарные группы - группы оборотов, объединяемых по какому-либо признаку.
							</span>
                        </div>
                    </div>
                    <div class="col-xs-12 col-md-12 wam-not-padding">
                        <div class="col-xs-2 col-md-2 wam-not-padding">
                            <img src="/resources/img/category_mini.png" class="wam-img-xs-1" alt="">
                        </div>
                        <div class="col-xs-10 col-md-10 wam-not-padding">
							<span class="text-justify">
								категории - наиболее общные сущности. Включают в себя группы товаров и другие категории (подкатегории).
							</span>
                        </div>
                    </div>
                    <div class="col-xs-12 col-md-12 wam-not-padding">
                        <p class="wam-margin-top-2 text-justify">Взаимосвязь типов:</p>
                    </div>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p><img src="/resources/img/manual_algorytm.png" class="img-responsive wam-top-radius center-block wam-img-xs-2" alt=""></p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">Вы сами определяете глубину детализации данных. Кому-то может быть удобно заводить
                        информацию в кратком виде, например:</p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <div class="col-xs-2 col-md-2 wam-not-padding">
                        <img src="/resources/img/category_mini.png" class="wam-img-xs-1" alt="">
                    </div>
                    <div class="col-xs-10 col-md-10 wam-not-padding">
						<span class="text-justify">
							Бытовые расходы.
						</span>
                    </div>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <div class="col-xs-2 col-md-2 wam-not-padding">
                        <img src="/resources/img/group_mini.png" class="wam-img-xs-1" alt="">
                    </div>
                    <div class="col-xs-10 col-md-10 wam-not-padding">
						<span class="text-justify">
							Продукты.
						</span>
                    </div>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <div class="col-xs-2 col-md-2 wam-not-padding">
                        <img src="/resources/img/money_mini.png" class="wam-img-xs-1" alt="">
                    </div>
                    <div class="col-xs-10 col-md-10 wam-not-padding">
						<span class="text-justify">
							Покупки в продуктовом магазине.
						</span>
                    </div>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">А кто-то будет подробен и скурпулезен:</p>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <div class="col-xs-2 col-md-2 wam-not-padding">
                        <img src="/resources/img/category_mini.png" class="wam-img-xs-1" alt="">
                    </div>
                    <div class="col-xs-10 col-md-10 wam-not-padding">
						<span class="text-justify">
							Продукты.
						</span>
                    </div>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <div class="col-xs-2 col-md-2 wam-not-padding">
                        <img src="/resources/img/group_mini.png" class="wam-img-xs-1" alt="">
                    </div>
                    <div class="col-xs-10 col-md-10 wam-not-padding">
						<span class="text-justify">
							Молоко.
						</span>
                    </div>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <div class="col-xs-2 col-md-2 wam-not-padding">
                        <img src="/resources/img/money_mini.png"class="wam-img-xs-1"  alt="">
                    </div>
                    <div class="col-xs-10 col-md-10 wam-not-padding">
						<span class="text-justify">
							Бутылка молока, "Домик в деревне", 1 л.
						</span>
                    </div>
                </div>
                <div class="col-xs-12 col-md-12 wam-not-padding">
                    <p class="wam-margin-top-2 text-justify">И то, и другое корректно с точки зрения ресурса. Отличие только в глубине возможного
                        анализа. Согласитесь, намного удобнее видеть соотношение трат на конкретные типы продуктов, стоящих в холодильние, чем
                        ограничиваться информацией, что в месяц на всю еду было потрачена столько-то. Напомним, основная цель ресурса - помочь определить
                        на что именно уходят незаметные траты.</p>
                </div>
            </div>
        </div>
    </div>
</div>
