 <h4><strong>list of all ${ENTITY_NAME}</strong></h4>
   <#assign cnt = 1>
     <table class="table table-striped entityTable" id="entityPayment">
        <thead>
            <tr>
                <th>Row</th>
                <th>${ENTITY_NAME} Key</th>
                <#include "/common/table/row/nameHeader.ftl">
                <#include "/common/table/row/locationHeader.ftl">
                <#include "/common/table/row/nationalIdentityHeader.ftl">
                <th>Age</th>
                <th>Created By</th>
            </tr>
        </thead>
        <tbody>
             <#list entityList as entityMember>
                    <tr>
                         <td>${cnt}</td>
                         <td><a href="/${entity}/${entityMember.getUniqueKey()}">${entityMember.getUniqueKey()}</a></td>
                         <#include "/common/table/row/nameCol.ftl">
                         <#include "/common/table/row/locationCol.ftl">
                         <#include "/common/table/row/nationalIdentityCol.ftl">
                         <td>${entityMember.getAge()!""}</td>
                         <td>${entityMember.getCreatedBy()!"root"}</td>
                     </tr>
                 <#assign cnt = cnt+1>
             </#list>
        </tbody>
     </table>
