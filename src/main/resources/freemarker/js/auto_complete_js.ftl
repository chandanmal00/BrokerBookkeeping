    <script>
    var markup =  10;

        function getMarker(obj) {
          var marker = ""
          if(obj.id.split("_").length>1) {
             marker = obj.id.split("_")[1];
          }
          return marker;
         }


$( ".item" ).autocomplete({
           minLength: 1,
           source: function( request, response ) {
           var searchParam  = request.term;
           $.ajax({
               dataType: "json",
               type : 'Get',
               url: '/searchKeys/${entityValue}/'+searchParam,
               success: function(data) {
                   $('.item').removeClass('ui-autocomplete-loading');
                   // hide loading image

                   response( $.map( data, function(item) {
                       return item;
                   }));
               },
               error: function(data) {
                   $('input.item').removeClass('ui-autocomplete-loading');
               }
           });
           },
           delay: 200,
           focus: function( event, ui ) {
                   marker = getMarker(this);
                   $( "#${ENTITY_NAME}Id_"+marker ).val( ui.item.value );
                   var j =parseInt(marker);
                   while(j< parseInt(marker)+3) {
                     $("#itemSignature_"+j).show();
                     j=j+1;
                   }
                   return false;

           },
           select: function( event, ui ) {
             marker = getMarker(this);
             $( "#${ENTITY_NAME}Id_"+marker ).val( ui.item.value );
             $('#${ENTITY_NAME}Id_'+marker).attr("value", ui.item.value);
             $( "#priceId_" +marker).val( 1 );
             $('#priceId_'+marker).attr("value", 1);
             $( "#bhartiId_" +marker).val( 1 );
             $('#bhartiId_'+marker).attr("value", 1);
             $( "#quantityId_" +marker).val(1);
             $('#quantityId_'+marker).attr("value", 1);
             var total = parseFloat($('#quantityId_'+marker).val()) * parseFloat($('#priceId_'+marker).val())
             $('#amountId_'+marker).val(total.toFixed(2));
             $('#amountId_'+marker).attr("value", total.toFixed(2));
  make_calculations(this);
  changeTotal(this);

             return false;
           }
         })
         .autocomplete( "instance" )._renderItem = function( ul, item ) {
           return $( "<li>" )
             .append( "" + item.value + "</li>" )
             .appendTo( ul );
         };



  </script>