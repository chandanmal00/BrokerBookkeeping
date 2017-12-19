<html>
<head>
    <#include "/common/meta.ftl">
    <#include "/css/head.css">
    <title>${APP_TITLE} Kisaan Transaction List Page</title>
</head>

<body>
<div class="container-fluid">
     <#include "/common/header.ftl">
     <#include "/common/errors.ftl">

     <#include "/common/table/kisaanTransaction_table.ftl">
    </div>
    <#include "/js/footer_js.ftl">
    <#include "/js/alerts_js.ftl">
</body>
</html>