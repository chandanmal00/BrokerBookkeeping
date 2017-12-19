
<h4><strong>list of all ${ENTITY_NAME}</strong></h4>
<#assign cnt = 1>
<table class="table table-striped entityTable" id="entityPayment">
     <thead>
         <tr>
             <th>Row</th>
             <th>${ENTITY_NAME} Key</th>
             <th>Kisaan Key</th>
             <th>Amount</th>
             <th>Tag</th>
             <th>PaymentSlip</th>
             <th>CreatedBy</th>
         </tr>
     </thead>
     <tbody>

         <#list entityList as entityMember>
                <tr>
                     <td>${cnt}</td>
                     <td><a href="/details/${entity}/${entityMember.getUniqueKey()}">${entityMember.getUniqueKey()}</a></td>
                     <td><a href="/kisaan/${entityMember.getKisaan()}">${entityMember.getKisaan()}</a></td>
                     <td>${entityMember.getAmount()}</td>
                     <td>${entityMember.getTag()!""}</td>
                     <td><a class="btn btn-info" href="/invoice/${entity}/${entityMember.getUniqueKey()}/kisaan">PaymentSlip <i class="fa fa-print" aria-hidden="true"></i>
                     </a></td>
                     <td>${entityMember.getCreatedBy()!"root"}</td>
                 </tr>
         <#assign cnt = cnt+1>
         </#list>

     </tbody>
</table>
