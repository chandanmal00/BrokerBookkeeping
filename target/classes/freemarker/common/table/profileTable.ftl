
<table class="table table-striped table-bordered table-condensed" id="entity">
    <thead>
        <tr>
            <th>Entity</th>
            <th>EntityValue</th>
        </tr>
    </thead>
    <tbody>

        <tr>
            <td>Key</td>
            <td>${entity.getUniqueKey()!""}</td>
        </tr>

        <#include "/common/table/row/nameRow.ftl">
         <tr>
                <td>Photo</td>
                <td>
                    <#include "/common/photo_entity.ftl">
                </td>

         </tr>
        <#include "/common/table/row/locationRow.ftl">
        <#include "/common/table/row/nationalIdentityRow.ftl">

        <#if entity.age??>
           <tr>
                <td>Age</td>
                <td>${entity.getAge()!""}</td>

           </tr>
        </#if>

    </tbody>
</table>