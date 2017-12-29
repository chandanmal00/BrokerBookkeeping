
<!DOCTYPE html>
<html lang="en">
  <head>
    <#include "/common/meta.ftl">
    <#include "/css/head.css">
    <title>Add Transaction for ${APP_TITLE}</title>
    <style>
        #state {
        display: block;
        font-weight: bold;
        margin-bottom: 1em;
        }

        .percent {
           font-size: 200%;
        }
    </style>

</head>

<body>
    <div class="container-fluid">
        <#include "/common/header.ftl">
        <#if success?? >
            <div>
                <div class="alert alert-success" role="alert">Successfully added Transaction for Kisaan: <b>${kisaan!""}</b>
                   <p>
                    <div class="invoices">
                        <a href="/invoice/${entityActual}/${entityObject.getUniqueKey()}/kisaan" class="btn btn-info">Kisaan Invoice<i class="fa fa-print" aria-hidden="true"></i></a>
                        <br>
                        <a href="/invoice/${entityActual}/${entityObject.getUniqueKey()}/khareeddar" class="btn btn-info">Khareeddar Invoice <i class="fa fa-print" aria-hidden="true"></i></a>
                    </div>
                    </p>
                    <p>
                      Want to add one more ${entityActual}: <a href="/add/kisaanTransaction">Click here <i class="fa fa-plus-square fa-lg" aria-hidden="true"></i></a>
                    </p>
                    <p>
                       Created by mistake, want to remove ${entityActual}: ${entityObject.getUniqueKey()} <br>
                       <#if admin??>
                       <a href="/remove/kisaanTransaction/${entityObject.getUniqueKey()}" target="_blank">Remove ${entityActual}<i class="fa fa fa-times fa-lg" aria-hidden="true"></i></a>(<font color="red">This will delete the entry be careful with this option</font>)
                       <#else>
                       Check with your admin!!
                       </#if>

                    </p>
                </div>

                <br>
                Below are the details added:<br>
                <#assign cnt = 1>
                <table class="table table-striped" id="entityPayment">
                <thead>
                </thead>
                <tbody>
                <#if entityObject.getItemSold()??>
                    <#list entityObject.getItemSold() as itemSold >
                                        <tr>
                                             <td>${cnt}</td>
                                             <td>${itemSold.getItem()!""}</td>
                                             <td>${itemSold.getQuantity()!""}</td>
                                             <td>${itemSold.getPrice()!""}</td>
                                             <td>${itemSold.getAmount()!""}</td>
                                         </tr>
                                     <#assign cnt = cnt+1>
                     </#list>

                 </#if>
                </tbody>
                </table>

                    <ul>
                        <li>Kisaan Amount: <b>${totalAmount!""}</b></li>
                        <li>Amount: <b>${actualTotal!""}</b></li>
                        <li>PaidAmount: <b>${paidAmount!""}</b></li>
                        <li>PaymentType: <b>${paymentType!""}</b></li>
                        <li>Items Added: <b>${entityObject.getItemSold()?size}</b></li>
                        <li>Khareeddar: <b><a href="/khareeddar/${khareeddar!""}">${khareeddar!""}</a></b></li>
                        <li>Kisaan: <b><a href="/kisaan/${kisaan!""}">${kisaan!""}</a></b></li>
                    </ul>
            </div>
        <#else>

            <h2>Add Kisaan Transaction to the system</h2>
            <div class="new_post_form">
                <form action="/add/kisaanTransaction" method="POST">
                    <#include "/common/errors.ftl">

                    <div class="row text-center">
                      <div class="col-xs-3 row required"><span class="left">Khareeddar:</span><input type="text" placeholder="Khareeddar Name" name="khareeddar" id="KhareeddarId" size="10" value="${khareeddar!""}"></div>
                      <div class="col-xs-4 row required"><span class="left">KisaanName:</span><input type="text" placeholder="Kisaan NickName" name="kisaan" id="KisaanId" size="10" value="${kisaan!""}"></div>
                      <div class="col-xs-3 row required"><span class="left">DateOfTransaction:</span><span class="input-daterange"> <input type="text" placeholder="yyyy-mm-dd" name="dt" size="10" id="dtId" value="${dt!""}"></span></div>
                    </div>

                    <div class="row text-center"> <!--Item Entry -->
                        <div class="col-sm-7">
                            <div class="row text-center">
                                  <div class="col-sm-3">itemName<font color="red">*</font></div>
                                  <div class="col-sm-1">bharti</div>
                                  <div class="col-sm-1">quantity</div>
                                  <div class="col-sm-1">price</div>
                                  <div class="col-sm-1">amount</div>
                            </div>
                            <div class="row text-center">
                                <div id="itemSold">
                                   <#assign idx = 0>
                                   <#assign st = "">
                                   <#list 1..CntItems?eval!14 as itemsToShow>
                                         <#assign price = "price_"+idx>
                                         <#assign itemName = "itemName_"+idx>
                                         <#assign bharti = "bharti_"+idx>
                                         <#assign amount = "amount_"+idx>
                                         <#assign quantity = "quantity_"+idx>
                                         <#assign htmlMO> </#assign>
                                         <#if idx gt 3>
                                             <#assign htmlMO>style="display:none"</#assign>
                                         </#if>
                                            <div id="itemSignature_${idx}" ${htmlMO} class="row text-center"> 
                                                  <div class="col-sm-3"><input type="text" class="item" placeholder="itemName" name="itemName_${idx}" id="itemId_${idx}" size="30" value="${itemName?eval!""}"></div> 
                                                  <div class="col-sm-1"><input type="text" placeholder="bharti" name="bharti_${idx}" id="bhartiId_${idx}" size="30" value="${bharti?eval!""}"></div> 
                                                  <div class="col-sm-1"><input type="text" class="target quantity" placeholder="quantity" name="quantity_${idx}" id="quantityId_${idx}" size="30" value="${quantity?eval!""}"></div> 
                                                  <div class="col-sm-1"><input type="text" class="target" placeholder="price" name="price_${idx}" id="priceId_${idx}" size="30" value="${price?eval!""}"></div> 
                                                  <div class="col-sm-1"><input type="text" class="amount" placeholder="amount" name="amount_${idx}" id="amountId_${idx}" size="30" value="${amount?eval!""}"></div> 
                                            </div>
                                         <#assign idx = idx + 1>
                                   </#list>
                                </div>
                                <div id="addedItemsSold">
                                </div>
                            </div>


                             <div class="row">
                                    <div class="row">
                                        <div class="col-sm-4">ActualTotal</div>
                                        <div class="col-sm-4">Deductions</div>
                                    </div>

                                    <div class="row">
                                        <div class="col-sm-4"> <input  class="amountFire" type="text" placeholder="actualTotal" name="actualTotal" id="actualTotalId" size="30" value="${actualTotal!""}"> </div>
                                        <div class="col-sm-4"> <input  class="amountFire" type="text" placeholder="deductions" name="deductions" id="deductionsId" size="30" value="${deductions!""}"> </div>
                                    </div>
                             </div>


                            <div class="row text-center">
                                  <div class="row">
                                       <div class="col-sm-4">PaymentType</div>
                                       <div class="col-sm-4">
                                            <select id="paymentTypeId" name="paymentType">
                                              <option value = "Cash">Cash</option>
                                              <option value = "CreditCard">CreditCard</option>
                                              <option value = "Check">Check</option>
                                              <option value = "BankTransfer">BankTransfer</option>
                                              <option value = "Other">Other</option>
                                            </select>
                                       </div>
                                  </div>
                                  <div class="row">
                                      <div class="col-sm-4">Total Amount:</div>
                                      <div class="col-sm-4" ><input type="text" class="form-control" name="totalAmount" placeholder="totalAmount" size=30 id="totalAmountId" value="${totalAmount!""}" aria-describedby="basic-addon1"></div>
                                  </div>
                                  <div class="row">
                                      <div class="col-sm-4">Amount Paid:</div>
                                      <div class="col-sm-4"><input type="text" class="form-control" name="paidAmount" placeholder="paidAmount" size=30 id="paidAmountId" value="${paidAmount!""}" aria-describedby="basic-addon1"></div>
                                  </div>

                             </div>
                            <div class="row text-center">
                                <input type="hidden" id="countItemsId" name="countItems" value="3">
                                <input id="submit" type="submit" value="Add Kisaan Transaction" class="btn btn-info btn-block">
                            </div>

                        </div> <!--end of 1st col-->
                        <div class="col-sm-5" style="background-color:pink;">
                              <div class="row">
                                <div class="row">
                                    <div class="col-sm-4">Hamaali Rate:</div>
                                    <div class="col-sm-4"><input  class="amountFire amountHamali" type="text" placeholder="hamaaliRate" name="hamaaliRate" id="hamaaliRateId" size="30" value="${hamaaliRate!""}"></div>
                                </div>
                                <div class="row">
                                    <div class="col-sm-4">Hamaali Amount:</div>
                                    <div class="col-sm-4"><input  class="amountFire amountHamali uneditable-input   " type="text" placeholder="hamaaliAmount" name="hamaaliAmount" id="hamaaliAmountId" size="30" value="${hamaaliAmount!""}"></div>
                                </div>
                                <div class="row">
                                    <div class="col-sm-4">Mapari Rate</div>
                                    <div class="col-sm-4"> <input  class="amountFire amountMapari" type="text" placeholder="mapariRate" name="mapariRate" id="mapariRateId" size="30" value="${mapariRate!""}"></div>
                                </div>
                                <div class="row">
                                    <div class="col-sm-4">Mapari Amount</div>
                                    <div class="col-sm-4"> <input  class="amountFire amountMapari" type="text" placeholder="mapariAmount" name="mapariAmount" id="mapariAmountId" size="30" value="${mapariAmount!""}"></div>
                                </div>

                              </div>

                          <div class="row">
                                <div class="row">
                                      <div class="row">
                                          <div class="col-sm-4">Brokerage Rate</div>
                                          <div class="col-sm-4"><input class="amountFire amountBrokerage" type="text" placeholder="brokerage" name="brokerage" id="brokerageId" size="30" value="${brokerage!""}"> </div>
                                      </div>
                                      <div class="row">
                                          <div class="col-sm-4">Brokerage Amount</div>
                                          <div class="col-sm-4"><input class="amountFire amountBrokerage" type="text" placeholder="brokerageAmount" name="brokerageAmount" id="brokerageAmountId" size="30" value="${brokerageAmount!""}"> </div>
                                      </div>
                                      <div class="row">
                                          <div class="col-sm-4">Cash Rate</div>
                                          <div class="col-sm-4"><input class="amountFire amountCashRate" type="text" placeholder="cashRate" name="cashRate" id="cashRateId" size="30" value="${cashRate!""}"></div>
                                      </div>
                                      <div class="row">
                                          <div class="col-sm-4">Cash Rate Amount</div>
                                          <div class="col-sm-4"><input class="amountFire amountCashRate" type="text" placeholder="cashRateAmount" name="cashRateAmount" id="cashRateAmountId" size="30" value="${cashRateAmount!""}"></div>
                                      </div>
                                </div>
                          </div>
                        </div> <!--end of 2nd col-->
                    </div>

<br>
                </form>
            </div>
        </#if>

    </div>



    <#include "/js/footer_js.ftl">
    <#include "/js/alerts_js.ftl">
        <script>

$('.target').on('change', function(){
  make_calculations(this);
  changeMapari();
  changeHamaali();


  changeBrokerage();
  changeCashRate();
  changeTotal();
});

$('.amountFire').on('change', function(){
  changeTotal();
});

$('.amountHamali').on('change', function(){
  changeHamaali();
  changeTotal();
});

$('.amountMapari').on('change', function(){
  changeMapari();
  changeTotal();
});

$('.amountBrokerage').on('change', function(){
  changeBrokerage();
  changeTotal();
});

$('.amountCashRate').on('change', function(){
  changeCashRate();
  changeTotal();
});

$('.target').on('keypress', function(){
  make_calculations();
  changeTotal();
});


$('.amountFire').on('keypress', function(){
  changeTotal();
});

$('.amountHamali').on('keypress', function(){
  changeHamaali();
  changeTotal();
});

$('.amountMapari').on('keypress', function(){
  changeMapari();
  changeTotal();
});

$('.amountBrokerage').on('keypress', function(){
  changeBrokerage();
  changeTotal();
});

$('.amountCashRate').on('keypress', function(){
  changeCashRate();
  changeTotal();
});

function changeMapari() {
    var mapari = parseFloat($('#mapariRateId').val());
    //alert('mapartiRate:'+mapari);
    var sum=0;
    var i=0;
    var j=0;
       while(i<$('.quantity').length) {
             var value = $('.quantity').get(i).value;
             if(value.trim()!="" && parseFloat(value)!='NAN' ){

               //sum+=parseFloat(value)*parseFloat(mapari);
                  var diff = parseFloat(value) - parseInt(value);
                  sum+=parseInt(value)*parseFloat(mapari);
                  if(parseFloat(diff)>0.5) {
                     sum+=parseFloat(mapari);
                  } else if(parseFloat(diff)>0) {
                     sum+=parseFloat(mapari)/2;
                  }
               j++;
               //console.log("sum:"+sum);
             }
             i++;
        }
       $("#countItemsId" ).val(j);
       $('#countItemsId').attr("value",j);

       sum = sum.toFixed(2)
       $('#mapariAmountId').val(sum);
       $('#mapariAmountId').attr("value", sum);
}

function changeHamaali() {
    var hamaali = parseFloat($('#hamaaliRateId').val());
    //alert(hamaali);
    var sum=0;
    var i=0;
    var j=0;

   while(i<$('.quantity').length) {
         var value = $('.quantity').get(i).value;
         //alert('value::'+value);
         if(value.trim()!="" && parseFloat(value)!='NAN'  ){
           sum+=parseFloat(value)*parseFloat(hamaali).toFixed(2);
           //console.log("sum:"+sum);
           j++;
         }
         i++;
    }
    sum = sum.toFixed(2);
   $('#hamaaliAmountId').val(sum);
   $('#hamaaliAmountId').attr("value", sum);
}

function changeBrokerage() {
    var brokerage = $('#brokerageId').val();
    var total = $('#actualTotalId').val();
    var sum=0;
    if(parseFloat(total)!='NAN' && total.trim()!="" ){
        sum = parseFloat(brokerage*total/100).toFixed(2);
    }
   $('#brokerageAmountId').val(sum);
   $('#brokerageAmountId').attr("value", sum);

}

function changeCashRate() {
    var cashRate = $('#cashRateId').val();
    var total = $('#actualTotalId').val();
    var sum = 0;
    if(parseFloat(total)!='NAN' && total.trim()!="" ){
       sum = parseFloat(cashRate*total/100).toFixed(2);
    }
    $('#cashRateAmountId').val(sum);
    $('#cashRateAmountId').attr("value", sum);
}

function changeTotal() {

  //keeps the running total
  var sum = 0;

  i=0;
  while(i<$('.amount').length) {
      var value = $('.amount').get(i).value;
      if(value.trim()=='' || value.trim()=='NaN') {
        i++;
        continue;
      }
      if(parseFloat(value.trim())!='NAN'){
          sum+=parseFloat(value);
      }

      i++;
  }


   $('#actualTotalId').val(sum);
   $('#actualTotalId').attr("value", sum);
   var deductions = parseFloat(0);
   changeCashRate();
   changeBrokerage();
   changeMapari();
   changeHamaali();

   var mapari = $('#mapariAmountId').val();
   var hamaali = $('#hamaaliAmountId').val();
   deductions = parseFloat(mapari) + parseFloat(hamaali);
   var brokerage = $('#brokerageId').val();
   var cashRate = $('#cashRateId').val();
   var paidAmount =  $('#paidAmountId').val()

/*
   var ded = 0;
   if(cashRate!=undefined && parseFloat(cashRate) != 'NaN' && cashRate.trim()!="") {
      //console.log("cashRate:"+cashRate);
      var perc=  (100 - parseFloat(cashRate))/100;
      //console.log("perc:"+perc);
      //console.log("sum:"+sum);
      ded = ded + (parseFloat(perc) * parseFloat(sum))
   }

    if(brokerage!=undefined && parseFloat(brokerage) != 'NaN' && brokerage.trim()!="") {
      //console.log("brokerage:"+brokerage);
      var perc=  (100 - parseFloat(brokerage))/100;
      //console.log("perc:"+perc);
      //console.log("sum:"+sum);
      ded = ded + ( parseFloat(perc) * parseFloat(sum) )

    }

*/
    //sum = sum - ded;
    //alert(sum);

    /*
    if(mapari!=undefined && parseFloat(mapari) != 'NaN' && mapari.trim()!="") {
      sum=parseFloat(sum) - parseFloat(mapari)
    }

    if(hamaali!=undefined && parseFloat(hamaali) != 'NaN' && hamaali.trim()!="") {
     // console.log("discount:"+discount);
      //console.log("sum:"+sum);
      sum=parseFloat(sum) - parseFloat(hamaali)
    }
    */

   deductions += parseFloat($('#brokerageAmountId').val()) + parseFloat($('#cashRateAmountId').val());


   $('#deductionsId').val(parseFloat(deductions).toFixed(2));
   $('#deductionsId').attr("value", parseFloat(deductions).toFixed(2));

   sum = sum - deductions;

   $('#totalAmountId').val(sum.toFixed(2));
   $('#totalAmountId').attr("value", sum.toFixed(2));



   //$('#totalAmountId').val(ActAmount - deductions);
   //$('#totalAmountId').attr("value", (ActAmount - deductions).toFixed(2));


}

function make_calculations(obj) {

  var marker;
  if(obj.id.split("_").length>1) {
      marker = obj.id.split("_")[1];

      var total = parseFloat($('#quantityId_'+marker).val()) * parseFloat($('#priceId_'+marker).val())
      $('#amountId_'+marker).val(total.toFixed(2));
      $('#amountId_'+marker).attr("value", total.toFixed(2));
      //show next set of empty items
      for(k=marker;k<parseInt(marker)+2 && k < 15;k++) {
         $("#itemSignature_"+k).show();
      }
  }

}


//Displays the items which have some value in it
function displayItemsAvailableWithData() {
 var i=0;
 var j=0;
 while(i<$('.quantity').length) {
             var value = $('.quantity').get(i).value;
             if(value.trim()=='' || value.trim()=='NaN') {
                      i++;
                     continue;
             }
             if(value.trim()!="" && parseFloat(value.trim())!='NAN' ){
                $("#itemSignature_"+i).show();
               j++;
             }
             i++;
        }
    $( "#countItemsId" ).val(j);
    $('#countItemsId').attr("value",j)
}

$(document).ready(function() {
displayItemsAvailableWithData();
changeTotal();
}
);




        </script>
    <#if success?? >
    <#else>
        <#assign ENTITY_NAME = "item">
        <#assign  entityValue = "itemTransaction">
        <#include "/js/auto_complete_js.ftl">

        <#assign ENTITY_NAME = ENTITY_KISAAN!"">
        <#assign entityValue = entity_kisaan!"">
        <#include "/js/auto_complete_js_entity.ftl">

        <#assign entityValue = entity_khareeddar!"">
        <#assign ENTITY_NAME = ENTITY_KHAREEDDAR!"">
        <#include "/js/auto_complete_js_entity.ftl">
    </#if>

    <#include "/common/footer.ftl">
</body>
</html>

