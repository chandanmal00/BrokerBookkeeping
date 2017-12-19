<div class="grey" style="float:left;width:350px;">
    <div id="photo" class="tileset">
        <#if entity?? && entity.uniqueKey?? && photo?? >
             <p style="text-align:center;">your latest pic</p>
             <img src="/images/${entityValue}/thumbnail.${photo}" alt="${entity.getUniqueKey()}" class="builtBy">
        <#else>
             <p style="text-align:center;"> update your latest avatar</p>
             <img src="/images/thumbnail.defaultPhoto.png" alt="defaultPhoto" class="builtBy">
        </#if>
    </div>
    <div class="image-rightbar">
        Update Your profile Photo:
        <p>
            <input type="file" class="search" name="fileToUpload" id="fileToUpload">
            <#if entity?? && entity.uniqueKey?? && photo??>
                <input type="hidden" name="oldFileName" value="${photo}">
            <#else>
                <input type="hidden" name="oldFileName" value="defaultPhoto.png">
            </#if>
        </p>
    </div>
 </div>