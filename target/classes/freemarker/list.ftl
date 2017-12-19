
<html>
<head>
    <#include "/common/meta.ftl">
    <#include "/css/head.css">
    <title>${APP_TITLE} ${ENTITY_NAME}: List Page</title>
</head>

<body>

    <div class="container-fluid">
        <#include "/common/header.ftl">
        <#include "/common/errors.ftl">
        <#if search??>
           <small><strong>Note:</strong> (all results below are for searchString: <strong>${searchString}</strong>)</small>
        </#if>
        <#if entity=="kisaan" >
        <#include "/common/table/${entity}_better_table.ftl">
        <#elseif entity == "khareeddar">
        <#include "/common/table/${entity}_better_table.ftl">
        <#else>
        <#include "/common/table/${entity}_table.ftl">
        </#if>

    </div>
    <#include "/common/footer.ftl">
    <#include "/js/footer_js.ftl">
    <#include "/js/alerts_js.ftl">

</body>
</html>