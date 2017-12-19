<!DOCTYPE html>
<html lang="en">
  <head>
    <#include "/common/meta.ftl">
    <#include "/css/head.css">
    <title>Remove a ${ENTITY_NAME} for ${APP_TITLE}</title>
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
        <#if success?? >
            <div>
                <p>Successfully removed ${ENTITY_NAME}: <b>${entityValue!""}</b>
                </p>

            </div>
        <#elseif error??>
                    <div>
                        <p>Encountered Error while removing ${ENTITY_NAME}: <b>${entityValue!""}, error : ${error!""}</b>
                        </p>

                    </div>
        <#else>
            <div class="new_post_form">
                <form action="/remove" method="POST">
                    <#include "/common/errors.ftl">
                    <h2>Remove ${ENTITY_NAME} to the system</h2>
                    <div class="row required">
                        <div class="left">${ENTITY_NAME} key:</div>
                        <div class="right"> <input type="text" placeholder="key to remove/delete" name="entityValue" size="120" id="${ENTITY_NAME}Id"  value="${entityValue!""}"></div>
                    </div>
                    <input type="hidden" name="entity" value="${entity!""}">
                    <input type="submit" value="Remove ${ENTITY_NAME!""}" class="btn btn-info btn-block">
                </form>
            </div>
        </#if>

    </div>
    <#include "/common/footer.ftl">
    <#include "/js/footer_js.ftl">
    <#include "/js/alerts_js.ftl">
    <#include "/js/auto_complete_js_entity.ftl">

</body>

</html>

