<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Invoice for ${entity}</title>
    <#include "/common/meta.ftl">
    <#include "/css/head.css">
</head>

<body>
<div class="container">
        <#include "/common/header.ftl">
        <br>
        <#include "/common/errors.ftl">
   This page happens when you try to play with invoicing key for an entity which does not exist, please notify the developer in such case!!

       </div>
       <#include "/js/footer_js.ftl">
       <#include "/js/alerts_js.ftl">

</div>
</body>
</html>