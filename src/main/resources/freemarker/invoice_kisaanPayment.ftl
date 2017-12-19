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
    <div class="hidden-print" style="text-align: right;">
            <a class="btn btn-info hidden-print"  onclick="printPage();" href="#">Print <i class="fa fa-print fa-2x" aria-hidden="true"></i> </a>
    </div>
    <div class="row">
        <div class="col-xs-12 text-left panel panel-info">
            <h1 class="hidden-print">Payment Slip</h1>
            <h4><small>Payment Slip No: ${entityObject.get_id()}</small></h4>
            <h4><small>Date of Transaction: ${entityObject.getEventDate()}</small></h4>
        </div>
    </div>
    <div class="row">
        <div class="col-xs-5">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <h4> From: ${broker.getFirmName()}</h4>
                </div>
                <div class="panel-body">
                    <p>
                        Proprietor: ${broker.getProprietor()}<br>
                        Address: ${broker.getLocation().getAddress()}, ${broker.getLocation().getPlace()} <br>
                        District: ${broker.getLocation().getDistrict()} <br>
                    </p>
                </div>
            </div>
        </div>
        <#assign invoiceEntityObject = entityObject.getKisaan()>


        <div class="col-xs-5 col-xs-offset-2 text-right">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <#--<h4>${invoiceEntity}: <a href="/${invoiceEntity}/${invoiceEntityObject}">${invoiceEntityObject}</a></h4>-->
                    <h4>${invoiceEntity}: ${invoiceEntityObject}</h4>
                </div>
                <#--
                <div class="panel-body">
                    <p>
                        Address:${invoiceEntityObject.getLocation().address!"NO_ADDRESS_FILE"} <br>
                        Place: ${invoiceEntityObject.getLocation().getPlace()} <br>

                    </p>
                </div>
                -->
            </div>
        </div>

    </div>
    <!-- / end client details section -->
    <table class="table table-bordered">
        <thead>
        <tr>

            <th>
                <strong>Tag</strong>
            </th>
            <th>
                <strong>PaymentType</strong>
            </th>
            <th>
                <strong>Amount</strong>
            </th>
        </tr>
        </thead>
        <tbody>
        <tr>

            <td>${entityObject.tag!""}</td>
            <td>${entityObject.paymentType!""}</td>
            <td><a href="#">${entityObject.getAmount()}</a></td>
        </tr>
        </tbody>
    </table>
    <div class="row text-right">
        <div class="col-xs-2 col-xs-offset-8">
            <p>
                <strong>
                    Sub Total : <br>
                    TAX : <br>
                    Total : <br>
                </strong>
            </p>
        </div>
        <div class="col-xs-2">
            <strong>
                ${entityObject.getAmount()} <br>
                N/A <br>
                ${entityObject.getAmount()} <br>
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