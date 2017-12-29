<div class="grey" style="float:left;width:350px;">
    <div id="photo" class="tileset">
        <#if entity.photo?? >
             <img src="/images/${entityValue}/thumbnail.${entity.getPhoto()}" alt="${entity.getUniqueKey()}" class="builtBy">
        <#else>
             <img src="/images/thumbnail.defaultPhoto.png" alt="defaultPhoto" class="builtBy">
        </#if>
    </div>
</div>