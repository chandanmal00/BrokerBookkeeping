<#assign cnt = 1>
<table class="table table-striped table-bordered table-condensed entityTable" id="transactions">
    <thead>
        <tr>
            <th>Row</th>
            <th>Kisaan NickName</th>
            <th>Khareeddar FirmName</th>
            <th>Transaction Date</th>
            <th>Invoice Kisaan</th>
            <th>Invoice Khareeddar</th>
            <th>No. of Items</th>
            <th>Amount</th>
            <th>KhareeddarAmount</th>
            <th>BrokerageAmount</th>
            <th>CreatedBy</th>
            <th>Details</th>
        </tr>
    </thead>
    <tbody>


<#list transactions as transaction>
       <tr>
            <td>${cnt}</td>
            <td><a href="/kisaan/${transaction.getKisaan()}">${transaction.getKisaan()}</a></td>
            <td><a href="/khareeddar/${transaction.getKhareeddar()}">${transaction.getKhareeddar()}</a></td>
            <td>${transaction.getEventDate()}</td>
            <td><a href="/invoice/kisaanTransaction/${transaction.getUniqueKey()}/kisaan" class="btn btn-info">InvoiceKisaan<i aria-hidden="true" class="fa fa-print"></i></a></td>
            <td><a href="/invoice/kisaanTransaction/${transaction.getUniqueKey()}/khareeddar" class="btn btn-info">InvoiceKhareeddar<i aria-hidden="true" class="fa fa-print"></i></a></td>
            <td>${transaction.getItemSold()?size}</td>
            <td>${transaction.amount}</td>
            <td>${transaction.amountKhareeddar}</td>
            <td>${transaction.amountBrokerage}</td>
            <td>${transaction.getCreatedBy()!"root"}</td>
            <td><a href="/details/kisaanTransaction/${transaction.getUniqueKey()}">details</a></td>
        </tr>

<#-- ${cnt}: ${transaction} -->
<#assign cnt = cnt+1>
</#list>

    </tbody>
</table>