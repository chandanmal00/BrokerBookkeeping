<html>
<head>
    <#include "/common/meta.ftl">
    <#include "/css/head.css">
    <title>${APP_TITLE} ${entity} Print Page</title>
</head>

<body>
    <div class="container-fluid">
        <#include "/common/header.ftl">
        <br>
        <h2 class="hidden-print">Bill for ${entity} for key ${key}</h2>
        <h2 class="visible-print-block">Broker Business Name</h2>
        <h2 class="visible-print-block">Broker Name</h2>
        <h2 class="visible-print-block">Broker Logo</h2>
        <h2 class="visible-print-block">Broker User</h2>
        <#include "/common/errors.ftl">
        <#if entityObject??>

        <div class="hidden-print" style="text-align: right;">
            <a class="btn btn-info hidden-print" href="/invoice/kisaanTransaction/${entityObject.getUniqueKey()}/kisaan">Invoice for Kisaan <i class="fa fa-print fa-2x" aria-hidden="true"></i> </a>
            <a class="btn btn-info hidden-print" href="/invoice/kisaanTransaction/${entityObject.getUniqueKey()}/khareeddar">Invoice to Khareeddar <i class="fa fa-print fa-2x" aria-hidden="true"></i> </a>
        </div>
        <table class="table table-striped table-condensed" id="entityPayment">
            <thead>
                <tr>
                    <th>Item</th>
                    <th>Item Details</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>ItemId</td>
                    <td>${entityObject.get_id()}</td>
                </tr>
                <tr>
                    <td>Date of Transaction</td>
                    <td>${entityObject.getEventDate()}</td>
                </tr>
                <tr>
                    <td>Date of Creation of ${entity}</td>
                    <td>${entityObject.getCreationDate()}</td>
                </tr>
                <tr>
                    <td>Current Date</td>
                    <td>${dateStr}</td>
                </tr>

                <tr>
                    <td>Khareeddar Key</td>
                    <td>${entityObject.getKhareeddar()}</td>
                </tr>
                <tr>
                    <td>Kisaan Key</td>
                    <td>${entityObject.getKisaan()}</td>
                </tr>
                <#--
                <tr>
                    <td>Transaction Item</td>
                    <td>${entityObject.getTransactionItem()}</td>
                </tr>

                <tr>
                    <td>Quantity</td>
                    <td>${entityObject.getQuantity()}</td>
                </tr>
                <tr>
                    <td>Bharti</td>
                    <td>${entityObject.getBharti()}</td>
                </tr>
                <tr>
                    <td>Net Quantity</td>
                    <td>${entityObject.getNetQuantity()}</td>
                </tr>
                <tr>
                    <td>Price per</td>
                    <td>${entityObject.getPrice()}</td>
                </tr>
                -->
                <tr>
                    <td>Kisaan Amount to Pay</td>
                    <td>${entityObject.getAmount()}</td>
                </tr>

                <tr>
                    <td>Total Amount</td>
                    <td>${entityObject.getTotalAmount()}</td>
                </tr>
                <tr>
                    <td>BrokerageAmount</td>
                    <td>(${entityObject.getAmountBrokerage()})</td>
                </tr>
                <tr>
                    <td>Mapari Amount</td>
                    <td>(${entityObject.getAmountMapari()})</td>
                </tr>
                <tr>
                    <td>HamaaliAmount</td>
                    <td>(${entityObject.getAmountHamaali()})</td>
                </tr>
                <tr>
                    <td>CashSpecialAmount</td>
                    <td>(${entityObject.getAmountCashSpecial()})</td>
                </tr>
            </tbody>
        </table>
        <#else>
           <p>
           <strong><font color="red">ERROR</font></strong>: Something went wrong, check with ADMIN or Developer
           </p>
        </#if>

    </div>
    <#include "/js/footer_js.ftl">
    <#include "/js/alerts_js.ftl">

</body>
</html>