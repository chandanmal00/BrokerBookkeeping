<h4><strong>list of all ${ENTITY_NAME}'s</strong></h4>
<#assign cnt = 1>
<table class="table table-striped entityTable" id="entityPayment">
    <thead>
        <tr>
            <th>Row</th>
            <th>${ENTITY_NAME} Key</th>
            <#include "/common/table/row/nameHeader.ftl">
            <#include "/common/table/row/locationHeader.ftl">
            <th>CreatedBy</th>
        </tr>
    </thead>
<tbody>


    <#list entityList as entityMember>
        <tr>
            <td>${cnt}</td>
            <td><a href="/${entity}/${entityMember.getUniqueKey()}">${entityMember.getUniqueKey()}</a></td>
            <#include "/common/table/row/nameCol.ftl">
            <#include "/common/table/row/locationCol.ftl">
            <td>${entityMember.getCreatedBy()!"root"}</td>
        </tr>
        <#assign cnt = cnt+1>
    </#list>

</tbody>
</table>