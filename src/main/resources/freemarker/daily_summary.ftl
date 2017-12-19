<html>
<head>
        <#assign strToShow = "last "+DAY!""+" "+type!"">
        <#if endingDate??>
            <#assign strToShow = type+" ending date "+endingDate>
        </#if>
    <#include "/common/meta.ftl">
    <#include "/css/head.css">
    <title>${APP_TITLE} ${summaryType} summary Page for ${strToShow}</title>
</head>

<body>
    <div class="container-fluid">
        <#include "/common/header.ftl">
        <br>
        <#include "/common/errors.ftl">

        <h2>${summaryType} summary Page for ${strToShow}</h2>
        <br>


        <#assign ENTITY_NAME = "joinMap">
        <#assign ENTITY_TABLE_NAME = "joinMap">
        <#assign entityList = joinMap>
        <h4><strong>${summaryType} summary for ${strToShow}</strong></h4>
        <#include "/common/table/list_join_table.ftl">

        <#assign ENTITY_NAME = "joinMapKhareeddar">
        <#assign ENTITY_TABLE_NAME = "joinMapKhareeddar">
        <#assign joinMap = joinMapKhareeddar>
        <#--Helps with selected Khareeddar Amount as compared to actual amount-->
        <#assign khareeddarKey="yes">


        <h4><strong>${summaryType} Khareeddar summary for ${strToShow}</strong></h4>
        <#include "/common/table/list_join_table.ftl">

         <#assign khareeddarKey="no">

        <#assign ENTITY_NAME = "kisaanTransaction">
        <#assign ENTITY_TABLE_NAME = "kisaanTransaction">
        <#assign entityList = kisaanTransactionList>
        <#--setting to 1 brings in amountKhareeddar as well as brokerage -->
         <#assign transactionFlag =1>
        <h4><strong>${summaryType} ${ENTITY_NAME} summary for ${strToShow}</strong></h4>
        <#include "/common/table/list_daily_table.ftl">

 <#assign transactionFlag=0>
<br>
        <#assign ENTITY_NAME = "kisaanPayment">
        <#assign ENTITY_TABLE_NAME = "kisaanPayment">
        <#assign entityList = kisaanPaymentList>
        <h4><strong>${summaryType} ${ENTITY_NAME} summary for ${strToShow}</strong></h4>
        <#include "/common/table/list_daily_table.ftl">
<br>

        <#assign ENTITY_NAME = "khareeddarPayment">
        <#assign entityList = khareeddarPaymentList>
        <#assign ENTITY_TABLE_NAME = "khareeddarPayment">
        <h4><strong>${summaryType} ${ENTITY_NAME} summary for ${strToShow}</strong></h4>
        <#include "/common/table/list_daily_table.ftl">
    </div>

    <#include "/common/footer.ftl">
    <#include "/js/footer_js.ftl">
    <#include "/js/alerts_js.ftl">

</body>
</html>