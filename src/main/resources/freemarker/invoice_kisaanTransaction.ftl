<!doctype html>
<html lang="en">
<head>
    <#include "/common/meta.ftl">
    <#include "/css/head.css">
    <title>${operation} for ${entity} for ${invoiceEntity}</title>
    <#include "/css/print.css">

</head>

<body>
    <div class="container-fluid">
        <#include "/common/header.ftl">
        <br>
        <#include "/common/errors.ftl">
        <#assign switchInvoiceObj = "kisaan">
        <#if invoiceEntity=="kisaan">
           <#assign switchInvoiceObj = "khareeddar">
        </#if>
    <div class="hidden-print" style="text-align: right;">
            <a class="btn btn-info hidden-print"  onclick="printPage();" href="#">Print <i class="fa fa-print fa-2x" aria-hidden="true"></i> </a>
            <a class="btn btn-info hidden-print" href="/invoice/kisaanTransaction/${entityObject.getUniqueKey()}/${switchInvoiceObj}">Switch Invoice to ${switchInvoiceObj}<i class="fa fa-print fa-2x" aria-hidden="true"></i> </a>
    </div>


    <div class="row hidden-print text-center">
        <div class="col-xs-12 text-left panel panel-info">
            <h4 class="hidden-print">INVOICE for <strong>${invoiceEntity}</strong> </h4>
        </div>
    </div>
    <div class="row">
        <div class="col-xs-5">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <h6> From: ${broker.getFirmName()}</h6>
                </div>
                <div class="panel-body">
                        <small>Proprietor: ${broker.getProprietor()}</small><br>
                        <small>Address: ${broker.getLocation().getAddress()}, ${broker.getLocation().getPlace()} </small><br>
                        <small>District: ${broker.getLocation().getDistrict()} </small><br>
                </div>
            </div>
        </div>
        <#if invoiceEntity=="kisaan">
           <#assign invoiceEntityObject = entityObject.getKisaan()>
        <#else>
            <#assign invoiceEntityObject = entityObject.getKhareeddar()>
        </#if>
        <div class="col-xs-5 col-xs-offset-2 text-left">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <#--<h4>To ${invoiceEntity}: <a href="/${invoiceEntity}/${invoiceEntityObject.getUniqueKey()}">${invoiceEntityObject.getUniqueKey()}</a></h4>-->
                    <h6>To ${invoiceEntity}: "${invoiceEntityObject}"</h6>
                </div>
                <div class="panel-body">
                <#--

                    <p>
                        Address:${invoiceEntityObject.getLocation().address!"NO_ADDRESS_FILE"} <br>
                        Place: ${invoiceEntityObject.getLocation().getPlace()} <br>

                    </p>

                -->
                <small> Invoice No: ${entityObject.get_id()} </small><br>
                <small> Date of Transaction: ${entityObject.getEventDate()} </small>
                </div>
            </div>
        </div>
    </div>
    <!-- / end client details section -->
    <table class="table table-bordered">
        <thead>
        <tr>
                 <th>Item</th>
                 <th>Bharti</th>
                 <th>Price</th>
                 <th>Quantity(quintals)</th>
                 <th>Amount</th>

        </tr>
        </thead>
        <tbody>
                <#assign cnt = 1>
                <#list entityObject.getItemSold() as itemSold >
                <tr>
                     <td>${cnt}] ${itemSold.getItem()!""}</td>
                     <td>${itemSold.getBharti()!0}</td>
                     <td>${itemSold.getPrice()!0}</td>
                     <td>${itemSold.getQuantity()}</td>
                     <td class="text-right">${itemSold.getAmount()!""}</td>
                      <#assign cnt = cnt+1>
                 </tr>

                 </#list>
                 <tr>
                     <td></td>
                     <td></td>
                     <td></td>
                     <td><strong>Amount</strong></td>
                     <td class="text-right">${entityObject.getTotalAmount()}</td>
                 </tr>

        <#if invoiceEntity=="kisaan">
            <tr>
                <td>
                    <tr><td>Khareeddar</td><td><b>${entityObject.getKhareeddar()}</b></td><td>Created By</td><td>${entityObject.getCreatedBy()}</td></tr>
                    <tr><td>Brokerage Rate</td><td>${entityObject.getBrokerCommission()} %</td><td>BrokerageAmount</td><td>(${entityObject.getAmountBrokerage()})</td></tr>
                    <tr><td>Hamaali Rate</td><td>${entityObject.getHamaaliRate()}</td><td>Hamaali Amount</td><td>(${entityObject.getAmountHamaali()})</td></tr>
                    <tr><td>Mapari Rate</td><td>${entityObject.getMapariRate()}</td><td>Mapari Amount</td><td>(${entityObject.getAmountMapari()})</td></tr>
                    <tr><td>Cash Special Rate</td><td>${entityObject.getCashSpecialRate()} %</td><td>Cash Special Amount</td><td>(${entityObject.getAmountCashSpecial()})</td></tr>
                </td>
                <td></td>
                <td></td>
                <td></td>
                <td><strong>Charges</strong></td>
                <#assign charges = entityObject.getAmountBrokerage() + entityObject.getAmountHamaali() + entityObject.getAmountMapari() + entityObject.getAmountCashSpecial()  >
                <td class="text-right">(${charges})</td>
            </tr>
        <#else>
            <tr>
                <td>
                    <tr><td>Kisaan</td><td><b>${entityObject.getKisaan()}</b></td><td>Created By</td><td>${entityObject.getCreatedBy()}</td></tr>
                    <tr><td>Mapari Rate</td><td>${entityObject.getMapariRate()}</td><td>Mapari Amount</td><td>(${entityObject.getAmountMapari()})</td></tr>
                </td>
                <td></td>
                <td></td>
                <td></td>
                <td><strong>Charges</strong></td>
                <#assign charges = entityObject.getAmountMapari()>
                <td class="text-right">(${charges})</td>
            </tr>
        </#if>
        </tbody>
    </table>
    <div class="row text-right">
        <div class="col-xs-2 col-xs-offset-8">
            <p>
                <strong>
                    Sub Total : <br>
                    TAX : <br>
                    Total : <br>
                    <#if invoiceEntity!="khareeddar" &&  entityObject.amountPaid??>
                    Amount Paid To Kisaan: <br>
                    Balance: <br>
                    </#if>
                </strong>
            </p>
        </div>
        <div class="col-xs-2">
        <#assign amount = entityObject.getAmount()>
        <#if invoiceEntity=="khareeddar">
          <#assign amount = entityObject.getAmountKhareeddar()>
        </#if>
            <strong>
                ${amount} <br>
                N/A <br>
                ${amount} <br>
                <#if invoiceEntity!="khareeddar" && entityObject.amountPaid??>
                ${entityObject.getAmountPaid()} <br>
                <#assign balance=amount-entityObject.getAmountPaid()>
                ${balance} <br>
                </#if>
            </strong>
        </div>
    </div>

    <#include "/common/footer.ftl">
</div>
    <script>
    function printPage() {
            window.print();
    }


    </script>
    <#include "/js/footer_js.ftl">
    <#include "/js/alerts_js.ftl">
</body>
</html>