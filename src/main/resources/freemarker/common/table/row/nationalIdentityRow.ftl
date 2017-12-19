
       <#if entity.nationalIdentity??>
       <tr>
            <td>Aadhar</td>
            <td>${entity.getNationalIdentity().getAadhar()!""}</td>
       </tr>
       <tr>
            <td>PAN</td>
            <td>${entity.getNationalIdentity().getPan()!""}</td>
       </tr>
       </#if>
