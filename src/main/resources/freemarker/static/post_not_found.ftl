<!DOCTYPE html>
<html lang="en">
  <head>
     <#include "/common/meta.ftl">
     <#include "/css/head.css">
     <title>Post Not found for Jain Traveller</title>
  </head>
  <body>
      <div class="container-fluid">
        <#include "/common/header.ftl">
        <#if entity?? && entityValue??>
            <br>
            <br>
            <br>
            <span class="label label-warning fa-2x">
                Sorry!! Page for ${entity} with value: ${entityValue} does not exist in the system!!.
            </span>
            <br>
            <br>
            <br>
                The link you followed may be broken, or the page may have been removed. Go back to <a href="/">Home</a>.
            <br>
        <#else>
        <#include "/common/404_error_msg.ftl">
        </#if>

      </div>
  </body>
      <#include "/js/footer_js.ftl">
      <#include "/js/alerts_js.ftl">
</html>


