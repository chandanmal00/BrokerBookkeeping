
<div id="div_cvs_${ENTITY_TABLE_NAME}">
<br>
<canvas id="cvs_${ENTITY_TABLE_NAME}" width="900" height="300">
</canvas>
<br>
</div>

<#assign cnt = 1>
<table class="table table-striped entityTable" id="entityPayment_${ENTITY_TABLE_NAME}">
<thead>
    <tr>
        <th>Row</th>
        <th>Date</th>
        <th>Amount</th>
        <th>Payment</th>
        <th>Balance</th>
        <th>BrokerageAmount</th>
    </tr>
</thead>
<tbody>
<#assign transactionAmount=0>
<#assign paymentAmount=0>
<#assign brokerageAmount=0>
<#list joinMap?keys as key>
        <tr>
            <td>${cnt}</td>
            <td>${key}</td>

            <#if joinMap[key].first??>
                 <#assign brokerageAmount = joinMap[key].getFirst()['amountBrokerage'] >
                 <#if khareeddarKey??>
                     <#assign transactionAmount = joinMap[key].getFirst()['amountKhareeddar'] >
                 <#else>
                    <#assign transactionAmount = joinMap[key].getFirst()['amount'] >
                </#if>
            </#if>

            <#if joinMap[key].second??>
              <#assign paymentAmount = joinMap[key].getSecond()['amount'] >
            </#if>
            <td>${transactionAmount}</td>
            <td>${paymentAmount}</td>
            <td>${transactionAmount-paymentAmount}</td>
            <td>${brokerageAmount}</td>
        </tr>
        <#assign cnt = cnt+1>
    </#list>
</tbody>
</table>

<script>
$( document ).ready(function() {
    var idName = '${ENTITY_TABLE_NAME}';
    var table = document.getElementById("entityPayment_"+idName);
    var dataObj = GetCellValuesAsVectors(table);
    //Hide canvas if data does not exist
    if(dataObj.data.length>0) {
      if ($(window).width()<1200)  {
        $("#cvs_${ENTITY_TABLE_NAME}").width($(window).width()*(2.2/3));
      }
      drawGraph(dataObj,"cvs_"+idName,idName+ " Summary");
      //$("#cvs_${ENTITY_TABLE_NAME}").width($(window).width()/2);
    } else {
      $('#div_cvs_'+idName).hide();
    }

});;
</script>