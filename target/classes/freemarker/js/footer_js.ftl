
    <script src="/bootstrap.min.js"></script>
    <script type="text/javascript" src="/bootstrapValidator.min.js"></script>

        <script type="text/javascript" src="/jquery.dataTables.min.js"></script>
        <script type="text/javascript" src="/dataTables.buttons.min.js"></script>
        <script type="text/javascript" src="/buttons.print.min.js"></script>
        <script type="text/javascript" src="/buttons.flash.min.js"></script>
        <script type="text/javascript" src="/buttons.html5.min.js"></script>
        <script type="text/javascript" src="/jszip.min.js"></script>
        <script type="text/javascript" src="/pdfmake.min.js"></script>
        <script type="text/javascript" src="/vfs_fonts.js"></script>
        <script type="text/javascript" src="/dataTables.fixedHeader.min.js"></script>

        <script type="text/javascript" src="/bootstrap-datepicker.min.js"></script>

        <!--hotkeys-->
        <script src="/jquery.hotkeys.js"></script>

        <!-- plotting -->
        <script src="/RGraph.common.core.js"></script>
        <script src="/RGraph.line.js"></script>
        <script src="/RGraph.common.dynamic.js"></script>
        <script src="/RGraph.common.tooltips.js"></script>
        <script src="/RGraph.common.key.js"></script>

        <!--For showing progress-->
        <!--<script src='/nprogress.js'></script>-->

    <script>

   //starting progress bar
    //NProgress.start();
    $(function() {
        var cache = {};
        $( "#search" ).autocomplete({
          minLength: 2,
          delay: 300,
          source: function( request, response ) {
             var term = request.term.trim();
             if ( term in cache ) {
               response( cache[ term ] );
               return;
             }

             $.getJSON( "/get_suggestions/"+term, request, function( data, status, xhr ) {
               cache[ term ] = data;
               response( data );
             });
          },
          focus: function( event, ui ) {
           $( "#search" ).val( ui.item.text );
            return false;
          },
          select: function( event, ui ) {
            window.location.href = "/post/"+ui.item.payload.permalink;
            //$( "#search" ).val( ui.item.text );
            return false;
          }
        })
        .autocomplete( "instance" )._renderItem = function( ul, item ) {
          return $( "<li>" )
            //.append( "<a>" + item.text + " " + item.payload.skills.join(",") + "<br>" + item.payload.permalink + "</a>" )
            .append( "<a>" + item.text + " "+ item.payload.city + " "+ item.payload.state + " " + item.payload.skills.join(",") + "</a>" )
            .appendTo( ul );
        };

    });

<!--plotting code-->
function GetCellValuesAsVectors(table) {
    var arr = [];
    var indexDate = 0;
    var flag=0;
    var dataObj = new Object();

    var n = table.rows.length;
    //This will keep the index for the date field
    var indexDate = 0;
    var dataColumns = [];
    var headers = [];
    for (var r = 0; r < n; r++) {
        var m = table.rows[r].cells.length;
        for(c=0;c<m;c++) {
            if(r==0) {
                if('Date'===table.rows[r].cells[c].innerHTML) {
                indexDate=c;
                }
                headers.push(table.rows[r].cells[c].innerHTML);
                continue;
            }
            if(arr[c]==undefined) {
                arr[c] = [];
            }

            //Handle other columns as float
            if(indexDate != c)  {
                var v =parseFloat(table.rows[r].cells[c].innerHTML.replace(/,/g,""));
                arr[c].push(v);

            } else {
                //formatting date
                var value = table.rows[r].cells[c].innerHTML;
                var dtArr = value.split("-");
                if(dtArr.length>1) {
                   arr[c].push(dtArr.slice(1).join("/"));
                } else {
                   arr[c].push(value);
                }
                //arr[c].push(table.rows[r].cells[c].innerHTML);
            }
        }
    }

    dataObj.data=arr;
    dataObj.headers = headers;
    dataObj.dataColumns = dataColumns;
    return dataObj
}

function drawGraph(dataObj,divIdGraph, title) {

    //Tool Tips
    var tips = [];
    for(var i=2;i<dataObj.data.length;i++) {
        for(j=0;j<dataObj.data[i].length;j++) {
            tips.push("value: "+dataObj.data[i][j].toLocaleString() + "<br>date: " + dataObj.data[1][j]);
        }
    }

    var line = new RGraph.Line({
        id: divIdGraph,
        data: dataObj.data.slice(2),

        options: {
        key: dataObj.headers.slice(2),
        keyPosition: 'gutter',
        tickmarks: 'circle',
        gutterLeft: 75,
        gutterRight: 55,
        linewidth: 3,
        shadow: true,
        labels: dataObj.data[1],
        yaxispos: 'right',
        tooltips: tips,
        labelsOffsetx: 10,
        labelsPosition:'edge',
        //title: title
        textAccessible: true,
        tooltipsHighlight: false
        }
    }).draw();
}

//Hotkeys
            //This page is a result of an autogenerated content made by running test.html with firefox.
            function hotKeys(){
                //$(document).bind(null, 'ctrl+l', clickHandler);
                $(document).bind('keydown', 'ctrl+l', loginHandler);
                $(document).bind('keydown', 'ctrl+t', addTransactionHandler);
                $(document).bind('keydown', 'ctrl+k', addKisaanPaymentHandler);
                $(document).bind('keydown', 'ctrl+j', addKhareeddarPaymentHandler);
                $(document).bind('keydown', 'ctrl+h', addKhareeddar);
                $(document).bind('keydown', 'ctrl+i', addKisaan);
                $(document).bind('keydown', 'ctrl+shift+k', searchKisaan);
                $(document).bind('keydown', 'ctrl+shift+h', searchKhareeddar);
                $(document).bind('keydown', 'ctrl+shift+t', searchTransaction);
                $(document).bind('keydown', 'ctrl', showTips);
                $(document).bind('keydown', 'shift', goToHome);
                $(document).bind('keydown', 'esc', hideTips);
                $(document).bind('keydown', '1', showHelp);
                $(document).bind('keydown', '2', hideHelp);
                $(document).bind('keydown', 'ctrl+1', displayDaily);
                $(document).bind('keydown', 'ctrl+2', displayQuarterly);
            }


            function goToHome(event){
              window.location.href=window.location.origin;
              return false;
            }
            function loginHandler(event){
              window.location.href=window.location.origin+"/login";
              return false;
            }

            function showHelp(event){
                  $('.help').show();
                  $('.tips').show();
                  return false;
            }
            function hideHelp(event){
                  $('.help').hide();
                  $('.tips').hide();
                  return false;
            }
            function showTips(event){
                  $('.tips').show();
                  return false;
            }
            function hideTips(event){
                  $('.tips').hide();
                  return false;
            }
            function displayDaily(event){
                  window.location.href=window.location.origin+"/last7days";
                  return false;
            }
            function displayQuarterly(event){
                  window.location.href=window.location.origin+"/quarterly";
                  return false;
            }
            function searchTransaction(event){
                  window.location.href=window.location.origin+"/searchEntity/kisaanTransaction";
                  return false;
            }
            function searchKhareeddar(event){
                  window.location.href=window.location.origin+"/searchEntity/khareeddar";
                  return false;
            }
            function searchKisaan(event){
                  window.location.href=window.location.origin+"/searchEntity/kisaan";
                  return false;
            }
            function addTransactionHandler(event){
                  window.location.href=window.location.origin+"/add/kisaanTransaction";
                  return false;
            }

            function addKisaanPaymentHandler(event){
                  window.location.href=window.location.origin+"/addKisaanPayment";
                  return false;
            }

            function addKhareeddarPaymentHandler(event){
                  window.location.href=window.location.origin+"/addKhareeddarPayment";
                  return false;
            }
            function addKhareeddar(event){
                  window.location.href=window.location.origin+"/addKhareeddar";
                  return false;
            }
            function addKisaan(event){
                  window.location.href=window.location.origin+"/addKisaan";
                  return false;
            }


//Table search/sort
window.onload=function() {

$('table.entityTable').DataTable(
{
    dom: 'Bfrtip',
    fixedHeader: true,
    lengthMenu: [
                [ 10, 25, 50, 100, -1 ],
                [ '10 rows', '25 rows', '50 rows','100 rows', 'Show all' ]
            ],
    buttons: [
        'pageLength','copy', 'csv','excel', 'pdf','print'
    ]
});

    $('.input-daterange').datepicker({
        weekStart: 11,
        format: "yyyy-mm-dd",
    });

    hotKeys();

    //NProgress.done();

};

    </script>
