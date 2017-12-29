<!DOCTYPE html>
<html lang="en">
  <head>
    <#include "/common/meta.ftl">
    <#include "/css/head.css">
    <title>Create a new Kisaan Payment for ${APP_TITLE}</title>
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
                    <div class="alert alert-success" role="alert">Successfully added KisaanPayment for Kisaan: <b><a href="/kisaan/${kisaan!""}">${kisaan!""}</a></b> with amount: <b>${amount}</b>
                        <br>Print <a href="/invoice/${entityActual}/${entityObject.getUniqueKey()}/kisaan" target="_blank" class="btn btn-info">PaymentSlip <i class="fa fa-print" aria-hidden="true"></i></a>
                        <br>
                        <#if tag??>
                            Tag Provided: <b>${tag!""}</b>
                        </#if>
                        <#if paymentType??>
                            PaymentType: <b>${paymentType!""}</b>
                        </#if>

                        <p>
                          Want to add one more ${entityActual}: <a href="/addKisaanPayment">Click here <i class="fa fa-plus-square fa-lg" aria-hidden="true"></i></a>
                        </p>
                        <p>
                           Created by mistake, want to remove ${entityActual}: ${entityObject.getUniqueKey()}<br>
                           <#if admin??>
                           <a href="/remove/kisaanTransaction/${entityObject.getUniqueKey()}" target="_blank">Remove ${entityActual}<i class="fa fa fa-times fa-lg" aria-hidden="true"></i></a>(<font color="red">This will delete the entry be careful with this option</font>)
                           <#else>
                           Check with your admin!!
                           </#if>

                        </p>
                    </div>
                </p>
            </div>
        <#else>

            <div class="new_post_form">
                <form action="/addKisaanPayment" method="POST">
                    <#include "/common/errors.ftl">
                    <h2>Add Kisaan to the system</h2>
                        <div class="row required"><div class="left">Kisaan Key:</div> <div class="right"><input type="text" placeholder="Kisaan Nick Name" name="kisaan" id="${ENTITY_NAME}Id" size="120" value="${kisaan!""}"></div></div>
                        <div class="row required"><div class="left">Amount:</div> <div class="right"> <input type="text" placeholder="Amount" name="amount" size="120" value="${amount!""}"> </div></div>
                        <div class="row required input-daterange"><div class="left">Date of Payment:</div> <div class="right"> <input type="text" placeholder="date in yyyy-mm-dd" name="dt" size="120" class="input-small" value="${dt!""}"> </div></div>
                        <div class="row"><div class="left">PaymentType:</div> <div class="right">
                                                     <select id="paymentTypeId" name="paymentType">
                                                       <option value = "Cash">Cash</option>
                                                       <option value = "CreditCard">CreditCard</option>
                                                       <option value = "Check">Check</option>
                                                       <option value = "BankTransfer">BankTransfer</option>
                                                       <option value = "Other">Other</option>
                                                     </select>
                                                </div>
                        </div>
                        <div class="row"><div class="left">Transaction Tag:</div> <div class="right"> <input type="text" placeholder="tag your transaction" name="tag" size="120" value="${tag!""}"></div></div>

                    <input id="submit" type="submit" value="Add Kisaan Payment" class="btn btn-info btn-block">

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

