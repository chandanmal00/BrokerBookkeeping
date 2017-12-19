<html>
<head>
    <#include "/common/meta.ftl">
    <#include "/css/head.css">
    <title>${APP_TITLE} ${ENTITY_NAME}: daily summary Page</title>
</head>

<body>
    <div class="container-fluid">
        <#include "/common/header.ftl">
        <br>
        <#include "/common/errors.ftl">
        <#if type?? && type=="range">
            <h4>${ENTITY_NAME}: ${summaryType} summary Page from ${startDate} - to date ${endingDate}</h4>
        <#else>
            <#if endingDate??>
                <h4>${ENTITY_NAME}: ${summaryType} summary Page for last ${DAY} ${type} ending date ${endingDate}</h4>
            <#else>
                <h4>${ENTITY_NAME}: ${summaryType} summary Page for last ${DAY} ${type}</h4>
            </#if>
        </#if>
        <#assign ENTITY_TABLE_NAME = ENTITY_NAME>
        <#include "/common/table/list_daily_table.ftl">

    </div>
    <#include "/common/footer.ftl">
    <#include "/js/footer_js.ftl">
    <#include "/js/alerts_js.ftl">

</body>
</html>