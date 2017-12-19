<html>
<head>
    <#include "/common/meta.ftl">
    <#include "/css/head.css">
    <title>${APP_TITLE} ${ENTITY_NAME}: ${entity.uniqueKey!""} Home</title>
</head>

<body>
<div class="container-fluid">
    <#include "/common/header.ftl">
    <br>

    <ul class="nav nav-tabs">
      <li class="active"><a data-toggle="tab" href="#summary">Summary</a></li>
      <li><a data-toggle="tab" href="#payment">Last ${rows} payments</a></li>
      <li><a data-toggle="tab" href="#transaction">Last ${rows} transactions</a></li>
    </ul>

    <div class="tab-content">
        <div id="summary" class="tab-pane fade in active">
            <h4><strong>${ENTITY_NAME} Profile: ${entity.uniqueKey} <a href="/edit/${entityValue}/${entity.uniqueKey}"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a></strong></h4>
            <div>
                <#include "/common/table/profileTable.ftl">
            </div>

            <h3><strong>${ENTITY_NAME} transactions and payment totals:</strong></h3>
            <#assign transactionFlag = 0 >
            <div>
                <table class="table table-striped" id="entity">
                    <thead>
                        <tr>

                            <th>TotalTransactionAmount</th>
                            <#if totalPaymentAmount??>
                                <th>TotalPaymentAmount</th>
                                <th>Balance</th>
                            </#if>
                            <th>TotalBrokerageAmount</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>

                            <td> ${summ['total']} </td>
                            <#if totalPaymentAmount??>
                                <td>${totalPaymentAmount}</td>
                                <td> ${summ['total'] - totalPaymentAmount}</td>
                            </#if>
                            <td> ${summ['totalBrokerage']}</td>
                        </tr>

                    </tbody>
                </table>
            </div>

            <#if joinMapWeekly??>
                <#assign joinMap = joinMapWeekly>
                <#assign ENTITY_TABLE_NAME = "joinMapWeekly">
                <h4><strong>Daily summary for last week:</strong></h4>
                <#include "/common/table/list_join_table.ftl">
            </#if>

            <h4><strong>${ENTITY_NAME} Daily Payments for the last week:</strong></h4>
            <#assign transactionFlag = 0 >
            <#assign entityList = weeklyPayments >
            <#assign ENTITY_TABLE_NAME = "weeklyPayments">
            <div>
                <#include "/common/table/list_daily_table.ftl">
            </div>

            <h4><strong>${ENTITY_NAME} Daily Transactions for the last week:</strong></h4>
            <#assign entityList = weeklyTransactions >
            <#assign ENTITY_TABLE_NAME = "weeklyTransactions">
            <#assign transactionFlag = 1 >
            <div>
                <#include "/common/table/list_daily_table.ftl">
            </div>

            <#if joinMapMonthly??>
                <#assign joinMap = joinMapMonthly>
                <#assign ENTITY_TABLE_NAME = "joinMapMonthly">
                <h4><strong>Quarterly Summary by Month:</strong></h4>
                <#include "/common/table/list_join_table.ftl">
            <br>
            </#if>

            <h4><strong>${ENTITY_NAME} Quarterly Payments by Month:</strong></h4>
            <#assign entityList = monthlyPayments >
            <#assign ENTITY_TABLE_NAME = "monthlyPayments">
            <#assign transactionFlag = 0 >
            <div>
                <#include "/common/table/list_daily_table.ftl">
            </div>

            <h4><strong>${ENTITY_NAME} Quarterly Transactions by Month:</strong></h4>
            <#assign entityList = monthlyTransactions >
            <#assign ENTITY_TABLE_NAME = "monthlyTransactions">
            <#assign transactionFlag = 1 >
            <div>
                <#include "/common/table/list_daily_table.ftl">
            </div>

        </div>



        <#assign transactionFlag = 0 >
        <div id="payment" class="tab-pane fade">

            <h4><strong>${ENTITY_NAME} Payments:</strong></h4>(showing last ${rows} only)
            <div>
                <#include "/common/table/paymentsTable.ftl">
                <#--<#include "/common/table/kisaanPayment_table.ftl">-->
            </div>
        </div>

        <#assign transactionFlag = 1 >
        <div id="transaction" class="tab-pane fade">

            <h4><strong>Transactions:</strong></h4>(showing last ${rows} only)
            <div>
                <#include "/common/table/transactionsTable.ftl">
            </div>
        </div>
    </div>



</div>
    <#include "/js/footer_js.ftl">
    <#include "/js/alerts_js.ftl">
</body>
</html>