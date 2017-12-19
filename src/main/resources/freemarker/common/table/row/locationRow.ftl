

<#if entity.location??>
       <tr>
            <td>Place</td>
            <td>${entity.getLocation().getPlace()!""}</td>

       </tr>
       <tr>
            <td>Address</td>
            <td>${entity.getLocation().getAddress()!""}</td>

       </tr>
       <tr>
            <td>Taluka</td>
            <td>${entity.getLocation().getTaluka()!""}</td>

       </tr>
       <tr>
            <td>District</td>
            <td>${entity.getLocation().getDistrict()!""}</td>

       </tr>
       <tr>
            <td>State</td>
            <td>${entity.getLocation().getState()!""}</td>

       </tr>

</#if>