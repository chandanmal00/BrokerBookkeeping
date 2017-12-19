<!DOCTYPE html>
<html lang="en">
<head>
    <#include "/common/meta.ftl">
    <#include "/css/head.css">
    <title>Help and Support page for ${APP_TITLE}</title>
</head>
<body>
  <#include "/common/header.ftl">
    <div class="container-fluid">
        <h2>Contact Us</h2>
        <hr class="hr_class"/>
        <br>
        <div>
        We would love to have your feedback for our site, Please email us and we would love to hear from you.
        <br>Please send us an email at <b><a href="mailto:${INFO_EMAIL}">${INFO_EMAIL}</a></b> and let us know, we will do our best to make it happen! :)
        <br>
        <br>
        ${APP_LINK}s mission is to help brokers bookkeeping effectively and efficiently.
        </div>

    </div>
    <#include "/common/footer.ftl">
    <#include "/js/footer_js.ftl">
    <#include "/js/alerts_js.ftl">
</body>
</html>