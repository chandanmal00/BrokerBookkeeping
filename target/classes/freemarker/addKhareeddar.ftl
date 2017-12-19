<!DOCTYPE html>
<html lang="en">
  <head>
    <#include "/common/meta.ftl">
    <#include "/css/head.css">
    <title>Create a new Khareeddar for ${APP_TITLE}</title>
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
                <p>
                <div class="alert alert-success" role="alert">Successfully added Khareeddar with firmName: <b>${firmName}</b>
                 <br>
                Want to edit : <a href="/edit/khareeddar/${firmName}">Click Here <i class="fa fa-pencil-square-o fa-lg" aria-hidden="true"></i></a>
                <br>
                See details for the Khareeddar: <a href="/khareeddar/${firmName}">Click Here <i class="fa fa-user fa-lg" aria-hidden="true"></i></a>
                <br>
                Add another Khareeddar: <a href="/addKhareeddar">Click Here <i class="fa fa-plus-square fa-lg" aria-hidden="true"></i></a>
                </div>
                    <br>
                    Below are the details added:<br>
                    <ul>
                        <li>firstName: <b>${firstName!""}</b></li>
                        <li>lastName: <b>${lastName!""}</b></li>
                        <li>place: <b>${place!""}</b></li>
                        <li>address: <b>${address!""}</b></li>
                        <li>taluka: <b>${taluka!""}</b></li>
                        <li>district: <b>${district!""}</b></li>
                        <li>state: <b>${state!""}</b></li>
                    </ul>
                </p>
            </div>
        <#else>
            <div class="new_post_form">
                <form action="/addKhareeddar" method="POST">
                    <#include "/common/errors.ftl">
                    <h2>Add Khareeddar to the system</h2>
                    <div class="row required"><div class="left">FirmName:</div> <div class="right"> <input type="text" placeholder="Firm Name" name="firmName" id="${ENTITY_NAME}Id" size="120" value="${firmName!""}"></div></div>
                    <#include "/common/name.ftl">
                    <#include "/common/location.ftl">
                    <input type="submit" value="Add Khareeddar" class="btn btn-info btn-block">
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

