<!DOCTYPE html>
<html lang="en">
  <head>
    <#include "/common/meta.ftl">
    <#include "/css/head.css">
    <#include "/css/profile.css">
    <title>Profile Page for ${username}</title>
</head>
<body>
    <div class="container-fluid">
        <#include "/common/header.ftl">
 <#if success?? >
            <div>
                <p>
                <div class="alert alert-success" role="alert">Successfully updated Kisaan: <b>${nickName}</b>
                <br>
                Want to edit again: <a href="/edit/kisaan/${nickName}">Click Here <i class="fa fa-pencil-square-o fa-lg" aria-hidden="true"></i></a>
                <br>
                See details for the Kisaan: <a href="/kisaan/${nickName}">Click Here <i class="fa fa-user fa-lg" aria-hidden="true"></i></a>

                <br>
                Add another Kisaan: <a href="/addKisaan">Click Here <i class="fa fa-plus-square fa-lg" aria-hidden="true"></i></a>
                </div>

                    <br>
                    Below are the details added:<br>
                    <ul>
                        <li>firstName: <b>${firstName!""}</b></li>
                        <li>lastName: <b>${lastName!""}</b></li>
                        <li>age: <b>${age!""}</b></li>
                        <li>aadhar: <b>${aadhar!""}</b></li>
                        <li>place: <b>${place!""}</b></li>
                        <li>address: <b>${address!""}</b></li>
                        <li>taluka: <b>${taluka!""}</b></li>
                        <li>district: <b>${district!""}</b></li>
                        <li>state: <b>${state!""}</b></li>
                    </ul>
                </p>

            </div>
        <#else>
        <h2>Edit Kisaan: ${nickName} in the system</h2>
        <form action="/edit/kisaan" method="post" enctype="multipart/form-data">
            <div class="centered">
                <div class="columns">
                    <div class="red" style="float:right;width:550px;">
                                                <#include "/common/errors.ftl">
                                                <h2>Edit Kisaan: ${nickName} in the system</h2>
                                                <div class="row required"><div class="left">NickName:</div> <div class="right"><strong>${nickName}</strong></div></div>
                                                <input type="hidden" name="nickName" value="${nickName}" />
                                                <#include "/common/name.ftl">
                                                <#include "/common/location.ftl">
                                                <div class="row"><div class="left">Age:</div> <div class="right"> <input type="text" id="age" placeholder="Age" name="age" size="120" value="${age!""}"></div></div>
                                                <div class="row"><div class="left">AadharCardNumber:</div> <div class="right"> <input type="text" id="aadhar" placeholder="aadhar card no." name="aadhar" size="120"></div></div>
                                                <div class="row"><div class="left">PAN number:</div> <div class="right"> <input type="text" id="pan" placeholder="pan card no" name="pan" size="120"></div></div>
                                                <input type="submit" value="Update Kisaan" class="btn btn-info btn-block">
                    </div>
                    <div class="grey" style="float:left;width:350px;">
                        <div id="photo" class="tileset">
                            <#if nickName?? >
                                 <p style="text-align:center;">your latest pic</p>
                                 <img src="/thumbnail.${nickName}" alt="${nickName}" class="builtBy">
                            <#else>
                                 <p style="text-align:center;"> update your latest avatar</p>
                                 <img src="/images/thumbnail.defaultPhoto.png" alt="defaultPhoto" class="builtBy">
                            </#if>
                        </div>
                        <div class="image-rightbar">
                            Update Your profile Photo:
                            <p>
                                <input type="file" class="search" name="fileToUpload" id="fileToUpload">
                                <#if nickName??>
                                    <input type="hidden" name="oldFileName" value="${nickName}">
                                <#else>
                                    <input type="hidden" name="oldFileName" value="${defaultPhoto}">
                                </#if>
                            </p>
                        </div>
                    </div>
                </div>
                <div class="clear">
                    <input type="submit" value="Update Kisaan" class="btn btn-info btn-block">
                </div>
            </div>
        </form>
        </#if>

    </div>
    <#include "/common/footer.ftl">
    <#include "/js/footer_js.ftl">
    <#include "/js/alerts_js.ftl">
    <#include "/js/auto_complete_js_entity.ftl">
</body>
</html>

