
      <div class="masthead">
          <span class="hidden-print">
              <a href="/"><img style="vertical-align: middle;" src="/images/bookkeeper.jpg" alt="${APP_TITLE}" class="jainTravellerLogoSmall"><sup><b><small>(Beta!)</small></b></sup></a> <span class="tips" style="display:none; color:green" ><b><i> { shift }</i></b></span>
              <small><b> Welcome ${username!"Guest"}!! </b></small>
              <#if username??>
              | <small><b><a href="/logout">Logout</a></b></small> | <small><b><a href="/">Home</a></b></small>
              </#if>
          </span>
          <#if username??>

            <nav class="navbar navbar-default navbar-static-top">
            <ul class="nav nav-tabs">
              <#if admin??>
                  <li role="presentation" class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
                      Admin<span class="caret"></span>
                    </a>
                    <ul class="dropdown-menu" aria-labelledby="dLabel">
                        <li><a href="/save">Backup Database</a></li>
                        <form action="/restore" method="POST" class="navbar-form navbar-left" role="search">
                                    <div class="form-group">
                                      <input type="text" class="form-control" placeholder="yyyy-mm-dd based date" name="dateStr">
                                    </div>
                                    <button type="submit" class="btn btn-default">Restore</button>
                        </form>
                    </ul>
                  </li>
              </#if>
              <li role="presentation" class="dropdown">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
                  DailySummary<span class="caret"></span>
                </a>
                <ul class="dropdown-menu" aria-labelledby="dLabel">

                    <li><a href="/last7days">Last 7 days Summary</a></li>
                    <li><a href="/last30days">Last 30 days Summary</a></li>
                    <li><a href="/quarterly">Monthly Summary for the Quarter</a></li>
                    <li><a href="/yearly">Yearly Summary for last 3 years</a></li>
                    <li><a href="/dateRangeSearch">Date Range based Summary</a></li>
                </ul>
              </li>
              <li role="presentation" class="dropdown">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
                  Kisaan<span class="caret"></span>
                </a>
                <ul class="dropdown-menu" aria-labelledby="dLabel">
                    <#if trial??>
                    <#else>
                    <li><a href="/addKisaan">Add</a></li>
                    <#if admin??>
                        <li><a href="/remove/kisaan">Remove</a></li>
                    </#if>
                    </#if>
                    <li><a href="/list/kisaan/100">List_Recent100</a></li>
                    <li><a href="/fullList/kisaan">List</a></li>
                    <li><a href="/searchEntity/kisaan">Search Kisaan</a></li>

                    <form action="/search" method="POST" class="navbar-form navbar-left" role="search">
                                <div class="form-group">
                                  <input type="hidden" name="entity" value="kisaan">
                                  <input type="text" class="form-control" placeholder="search kisaan..." name="query">
                                </div>
                                <button type="submit" class="btn btn-default">Submit</button>
                    </form>
                </ul>
              </li>
              <li role="presentation" class="dropdown">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
                  KisaanTransaction<span class="caret"></span>
                </a>
                <ul class="dropdown-menu" aria-labelledby="dLabel">
                    <#if trial??>
                    <#else>
                        <li><a title="shortcut:Ctrl t" href="/add/kisaanTransaction">Add</a></li>
                    <#if admin??>
                    <li><a href="/remove/kisaanTransaction">Remove</a></li>
                    </#if>
                    </#if>

                    <li><a href="/last7days/kisaanTransaction">Last 7 days Summary</a></li>
                    <li><a href="/last30days/kisaanTransaction">Last 30 days Summary</a></li>
                    <li><a href="/quarterly/kisaanTransaction">Monthly Summary for the Quarter</a></li>
                    <li><a href="/yearly/kisaanTransaction">Yearly Summary for last 3 years</a></li>
                    <li><a href="/dateRangeSearch/kisaanTransaction">Date Range based Summary</a></li>
                    <li><a href="/list/kisaanTransaction/100">List_Recent100</a></li>
                    <li><a href="/fullList/kisaanTransaction">List</a></li>
                    <li><a href="/searchEntity/kisaanTransaction">Search KisaanTransaction</a></li>

                    <form action="/search" method="POST" class="navbar-form navbar-left" role="search">
                                <div class="form-group">
                                  <input type="hidden" name="entity" value="kisaanTransaction">
                                  <input type="text" class="form-control" placeholder="search kisaanTransaction..." name="query">
                                </div>
                                <button type="submit" class="btn btn-default">Submit</button>
                    </form>
                </ul>
              </li>
              <li role="presentation" class="dropdown">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
                  KisaanPayment<span class="caret"></span>
                </a>
                <ul class="dropdown-menu" aria-labelledby="dLabel">
                    <#if trial??>
                    <#else>
                    <li><a href="/addKisaanPayment">Add</a></li>
                    <#if admin??>
                    <li><a href="/remove/kisaanPayment">Remove</a></li>
                    </#if>
                    </#if>
                    <li><a href="/last7days/kisaanPayment">Last 7 days Summary</a></li>
                    <li><a href="/last30days/kisaanPayment">Last 30 days Summary</a></li>
                    <li><a href="/quarterly/kisaanPayment">Monthly Summary for the Quarter</a></li>
                    <li><a href="/yearly/kisaanPayment">Yearly Summary for last 3 years</a></li>
                    <li><a href="/dateRangeSearch/kisaanPayment">Date Range based Summary</a></li>

                    <li><a href="/list/kisaanPayment/100">List_Recent100</a></li>
                    <li><a href="/fullList/kisaanPayment">List</a></li>
                    <li><a href="/searchEntity/kisaanPayment">Search KisaanPayment</a></li>
                    <form action="/search" method="POST" class="navbar-form navbar-left" role="search">
                                <div class="form-group">
                                  <input type="hidden" name="entity" value="kisaanPayment">
                                  <input type="text" class="form-control" placeholder="search kisaanPayment..." name="query">
                                </div>
                                <button type="submit" class="btn btn-default">Submit</button>
                    </form>
                </ul>
              </li>
              <li role="presentation" class="dropdown">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
                  Khareeddar<span class="caret"></span>
                </a>
                <ul class="dropdown-menu" aria-labelledby="dLabel">
                    <#if trial??>
                    <#else>
                    <li><a href="/addKhareeddar">Add</a></li>
                    <#if admin??>
                    <li><a href="/remove/khareeddar">Remove</a></li>
                    </#if>
                    </#if>
                    <li><a href="/list/khareeddar/100">List_Recent100</a></li>
                    <li><a href="/fullList/khareeddar">List</a></li>
                    <li><a href="/searchEntity/khareeddar">Search Khareeeddar</a></li>
                    <form action="/search" method="POST" class="navbar-form navbar-left" role="search">
                                <div class="form-group">
                                  <input type="hidden" name="entity" value="khareeddar">
                                  <input type="text" class="form-control" placeholder="search Khareeddar..." name="query">
                                </div>
                                <button type="submit" class="btn btn-default">Submit</button>
                    </form>
                </ul>
              </li>
              <li role="presentation" class="dropdown">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
                  KhareeddarPayment<span class="caret"></span>
                </a>
                <ul class="dropdown-menu" aria-labelledby="dLabel">
                    <#if trial??>
                    <#else>
                    <li><a href="/addKhareeddarPayment">Add</a></li>
                    <#if admin??>
                    <li><a href="/remove/khareeddarPayment">Remove</a></li>
                    </#if>
                    </#if>
                    <li><a href="/last7days/khareeddarPayment">Last 7 days Summary</a></li>
                    <li><a href="/last30days/khareeddarPayment">Last 30 days Summary</a></li>
                    <li><a href="/quarterly/khareeddarPayment">Monthly Summary for the Quarter</a></li>
                    <li><a href="/yearly/khareeddarPayment">Yearly Summary for last 3 years</a></li>
                    <li><a href="/dateRangeSearch/khareeddarPayment">Date Range based Summary</a></li>


                    <li><a href="/list/khareeddarPayment/100">List_Recent100</a></li>
                    <li><a href="/fullList/khareeddarPayment">List</a></li>
                    <li><a href="/searchEntity/khareeddarPayment">Search KhareeeddarPayment</a></li>
                    <form action="/search" method="POST" class="navbar-form navbar-left" role="search">
                                <div class="form-group">
                                  <input type="hidden" name="entity" value="khareeddarPayment">
                                  <input type="text" class="form-control" placeholder="search KhareeddarPayment..." name="query">
                                </div>
                                <button type="submit" class="btn btn-default">Submit</button>
                    </form>
                </ul>
              </li>
              <form action="/multiSearch" method="POST" class="navbar-form navbar-left" role="search">
                <div class="form-group">
                  <input type="text" class="form-control" placeholder="search all..." name="query">
                </div>
                <button type="submit" class="btn btn-default">Submit</button>
              </form>
            </ul>
            </nav>

          <#else>
               <nav>
                  <ul class="nav nav-justified">
                      <li><a href="/">Home</a></li>
                      <li><a href="/signup">SignUp</a></li>
                      <li><a href="/login">Login</a></li>
                  </ul>
               </nav>
          </#if>
       </div>
       <#include "/common/alerts.ftl">


  <div class="container hidden-print help" style="display:none">
      <br>
      <#include "/common/keyboard.ftl">
      <#if username??>
      <table class="table">
              <tr>
              <td>Create a Kisaan Transaction: <a href="/add/kisaanTransaction"><i class="fa fa-plus-square fa-lg" aria-hidden="true"></i></a><span class="tips" style="display:none; color:green " ><b><i> { ctrl t }</i></b></span></td>
              <td>Search for Kisaan: <a href="/searchEntity/kisaan"><i class="fa fa-search fa-lg" aria-hidden="true"></i></a><span class="tips" style="display:none; color:green " ><b><i> { ctrl shift k }</i></b></span></td>
              </tr>
              <tr>
              <td>Create a Kisaan Payment: <a href="/addKisaanPayment"><i class="fa fa-plus-square fa-lg" aria-hidden="true"></i></a><span class="tips" style="display:none; color:green  " ><b><i> { ctrl k }</i></b></span></td>
              <td>Search for KisaanTransaction: <a href="/searchEntity/kisaanTransaction"><i class="fa fa-search fa-lg" aria-hidden="true"></i></a><span class="tips" style="display:none; color:green " ><b><i> { ctrl shift t }</i></b></span></td>
              </tr>
              <tr>
              <td>Create a Khareeddar Payment: <a href="/addKhareeddarPayment"><i class="fa fa-plus-square fa-lg" aria-hidden="true"></i></a><span class="tips" style="display:none;color:green  " ><b><i> { ctrl j }</i></b></span></td>
              <td>Search for Khareeddar: <a href="/searchEntity/khareeddar"><i class="fa fa-search fa-lg" aria-hidden="true"></i></a><span class="tips" style="display:none; color:green " ><b><i> { ctrl shift h }</i></b></span></td>
              </tr>
              <tr>
              <td>Daily Summary: <a href="/last7days"><i class="fa fa-line-chart fa-lg" aria-hidden="true"></i></a><span class="tips" style="display:none;color:green  " ><b><i> { ctrl 1 }</i></b></span></td>
              <td>Monthly Summary: <a href="/quarterly"><i class="fa fa-line-chart fa-lg" aria-hidden="true"></i></a><span class="tips" style="display:none; color:green " ><b><i> { ctrl 2 }</i></b></span></td>
              </tr>
      </table>
      </#if>
  </div>
