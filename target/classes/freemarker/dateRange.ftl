<!DOCTYPE html>
<html lang="en">
  <head>
    <#include "/common/meta.ftl">
    <#include "/css/head.css">
    <title>Date Range Search: ${ENTITY_NAME!""} for ${APP_TITLE}</title>
    <style>
        #state {
        display: block;
        font-weight: bold;
        margin-bottom: 1em;
        }
    </style>

</head>

<body>
    <div class="container-fluid">
        <#include "/common/header.ftl">

            <div class="new_post_form">
            <#if entity??>
                <form action="/list" method="POST">

                    <h2>Date Based Search ${ENTITY_NAME} to the system</h2>
            <#else>
                            <form action="/dateRangeSearch" method="POST">
                            <h2>Date Based Search to the system</h2>
             </#if>
        <#include "/common/errors.ftl">
             <br>
            <div class="input-daterange" id="datepicker">
                <#if entity??>
                   <span>${ENTITY_NAME!""} key:<span>
                </#if>
                <input type="text" placeholder="fromDate (yyyy-mm-dd)" class="input-small" name="fromDate" />
                <span class="add-on">to</span>
                <input type="text"  placeholder="toDate (yyyy-mm-dd)" class="input-small" name="toDate" />
            </div>

<br>
<p>
  <b>summaryType:</b><br>
  <label class="radio-inline"><input type="radio" name="type" value="daily" checked>daily</label>

  <label class="radio-inline"><input type="radio" name="type" value="monthly">monthly</label>

  <label class="radio-inline"><input type="radio" name="type" value="yearly">yearly</label>

</p>

                    <input type="hidden" name="entity" value="${entity!""}">
                    <input type="submit" value="Search Dates ${ENTITY_NAME!""}" class="btn btn-info btn-block">
                </form>
            </div>


    </div>
    <#include "/common/footer.ftl">
    <#include "/js/footer_js.ftl">
    <#include "/js/alerts_js.ftl">
<#--    <#include "/js/auto_complete_js_entity.ftl"> -->

</body>

</html>

