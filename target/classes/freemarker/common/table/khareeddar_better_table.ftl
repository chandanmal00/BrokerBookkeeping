 <h2>list of all ${ENTITY_NAME}</h2>
   <#assign cnt = 1>
     <table class="table table-striped entityTable" id="entityPayment">
        <thead>
            <tr>
                <th>Row</th>
                <th>${ENTITY_NAME} Key</th>
                <th>TransactionAmount</th>
                <th>PaymentAmount</th>
                <th>Balance</th>
                <#include "/common/table/row/nameHeader.ftl">
                <th>Created By</th>
            </tr>
        </thead>
        <tbody>


        <#list joinMap?keys as key>
        <#if joinMap[key].first??>
                <tr>
                    <td>${cnt}</td>
                    <td><a href="/${entity}/${key}">${key}</a></td>

<#assign paymentAmount=0>
<#assign transactionAmount=0>
<#assign balance=0>
                    <#if joinMap[key].second??>
                      <#assign transactionAmount = joinMap[key].getSecond()['transactionAmount'] >
                      <#assign paymentAmount = joinMap[key].getSecond()['paymentAmount'] >
                      <#assign balance = joinMap[key].getSecond()['balance'] >
                    </#if>
                    <td>${transactionAmount}</td>
                    <td>${paymentAmount}</td>
                    <td>${balance}</td>
                    <#if joinMap[key].first??>
                      <#assign entityMember = joinMap[key].getFirst()['obj']>
                         <#include "/common/table/row/nameCol.ftl">
                         <td>${entityMember.getCreatedBy()!"root"}</td>
                    <#else>
                      <#include "/common/table/row/nameCol.ftl">
                      <td>root</td>
                    </#if>
                </tr>
                <#assign cnt = cnt+1>
            </#if>
            </#list>
        </tbody>
     </table>
