<#assign cnt = 1>
<table class="table table-striped table-bordered table-condensed" id="entityPayment">
    <thead>
        <tr>
            <th>Row</th>
            <th>Entity Key</th>
            <th>Date Of Payment</th>
            <th>PaymentSlip</th>
            <th>Amount</th>
            <th>Tag</th>
            <th>CreatedBy</th>
            <th>Details</th>
        </tr>
    </thead>
    <tbody>


<#list payments as payment>
       <tr>
            <td>${cnt}</td>
            <td>${entity.getUniqueKey()}</td>
            <td>${payment.getEventDate()}</td>
            <td><a href="/invoice/${entityValue}Payment/${payment.getUniqueKey()}/kisaan" class="btn btn-info">PaymentSlip <i aria-hidden="true" class="fa fa-print"></i></a></td>
            <td>${payment.getAmount()!""}</td>
            <td>${payment.getTag()!""}</td>
            <td>${payment.getCreatedBy()!"root"}</td>
            <td><a href="/details/${entityValue}Payment/${payment.getUniqueKey()}">details</a></td>
        </tr>

<#-- ${cnt}: ${payment} -->
<#assign cnt = cnt+1>
</#list>

    </tbody>
</table>