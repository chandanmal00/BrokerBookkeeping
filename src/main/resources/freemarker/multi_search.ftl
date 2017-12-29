<html>
<head>
    <#include "/common/meta.ftl">
    <#include "/css/head.css">
    <title>${APP_TITLE} Multi Search Page</title>
</head>

<body>
    <div class="container-fluid">
        <#include "/common/header.ftl">
        <br>
            <#if hasResults??>
                <h6>*Single view for the Search results for query:<b>"${query}"</b></h6>
                <ul class="nav nav-tabs">
                  <li class="active"><a data-toggle="tab" href="#fullView">FullView</a></li>
                  <li><a data-toggle="tab" href="#kisaanTab">Kisaan</a></li>
                  <li><a data-toggle="tab" href="#khareeddarTab">Khareeddar</a></li>
                  <li><a data-toggle="tab" href="#kisaanTransactionTab">KisaanTransaction</a></li>
                  <li><a data-toggle="tab" href="#kisaanPaymentTab">KisaanPayment</a></li>
                  <li><a data-toggle="tab" href="#khareeddarPaymentTab">KhareeddarPayment</a></li>
                </ul>

                <div class="tab-content">
                    <div id="fullView" class="tab-pane fade in active">
                            <#assign entityList = kisaan>
                            <#assign entityStr = "kisaan">
                            <#assign entity = "kisaan">
                            <#assign ENTITY_NAME = "Kisaan">
                            <#if entityList?size gt 0>
                                <#include "/common/table/kisaan_table.ftl">
                            </#if>

                            <#assign entityList = khareeddar>
                            <#assign entityStr = "khareeddar">
                            <#assign entity = "khareeddar">
                            <#assign ENTITY_NAME = "Khareeddar">
                            <#if entityList?size gt 0>
                                <#include "/common/table/khareeddar_table.ftl">
                            </#if>

                            <#assign entityList = kisaanTransaction>
                            <#assign entityStr = "kisaanTransaction">
                            <#assign entity = "kisaanTransaction">
                            <#assign ENTITY_NAME = "KisaanTransaction">
                            <#if entityList?size gt 0>
                                <#include "/common/table/kisaanTransaction_table.ftl">
                            </#if>

                            <#assign entityList = kisaanPayment>
                            <#assign entity = "kisaanPayment">
                            <#assign entityStr = "kisaanPayment">
                            <#assign ENTITY_NAME = "KisaanPayment">
                            <#if entityList?size gt 0>
                                <#include "/common/table/kisaanPayment_table.ftl">
                            </#if>

                            <#assign entityList = khareeddarPayment>
                            <#assign entity = "khareeddarPayment">
                            <#assign entityStr = "khareeddarPayment">
                            <#assign ENTITY_NAME = "KhareeddarPayment">
                            <#if entityList?size gt 0>
                                <#include "/common/table/khareeddarPayment_table.ftl">
                            </#if>

                    </div>
                    <div id="kisaanTab" class="tab-pane fade">

                        <#assign entityList = kisaan>
                        <#assign entityStr = "kisaan">
                        <#assign entity = "kisaan">
                        <#assign ENTITY_NAME = "Kisaan">
                        <#if entityList?size gt 0>
                            <#include "/common/table/kisaan_table.ftl">
                        <#else>
                              <h4>"No Results for ${entity}"<h4>
                        </#if>
                    </div>

                    <div id="khareeddarTab" class="tab-pane fade">
                        <#assign entityList = khareeddar>
                        <#assign entityStr = "khareeddar">
                        <#assign entity = "khareeddar">
                        <#assign ENTITY_NAME = "Khareeddar">
                        <#if entityList?size gt 0>
                            <#include "/common/table/khareeddar_table.ftl">
                        <#else>
                              <h4>"No Results for ${entity}"<h4>
                        </#if>

                    </div>

                    <div id="kisaanTransactionTab" class="tab-pane fade">
                        <#assign entityList = kisaanTransaction>
                        <#assign entityStr = "kisaanTransaction">
                        <#assign entity = "kisaanTransaction">
                        <#assign ENTITY_NAME = "KisaanTransaction">
                        <#if entityList?size gt 0>
                            <#include "/common/table/kisaanTransaction_table.ftl">
                        <#else>
                              <h4>"No Results for ${entity}"<h4>
                        </#if>
                    </div>
                    <div id="kisaanPaymentTab" class="tab-pane fade">
                        <#assign entityList = kisaanPayment>
                        <#assign entity = "kisaanPayment">
                        <#assign entityStr = "kisaanPayment">
                        <#assign ENTITY_NAME = "KisaanPayment">
                        <#if entityList?size gt 0>
                            <#include "/common/table/kisaanPayment_table.ftl">
                        <#else>
                              <h4>"No Results for ${entity}"<h4>
                        </#if>
                    </div>
                    <div id="khareeddarPaymentTab" class="tab-pane fade">
                        <#assign entityList = khareeddarPayment>
                        <#assign entity = "khareeddarPayment">
                        <#assign entityStr = "khareeddarPayment">
                        <#assign ENTITY_NAME = "KhareeddarPayment">
                        <#if entityList?size gt 0>
                            <#include "/common/table/khareeddarPayment_table.ftl">
                        <#else>
                              <h4>"No Results for ${entity}"<h4>
                        </#if>
                    </div>

                </div>
            <#else>
                <h6>*Sorry your search did not match any results for query: <b>"${query}"</b>, Please try a different search...</h6>
            </#if>

    </div>

    <#include "/js/footer_js.ftl">
    <#include "/js/alerts_js.ftl">

</body>
</html>