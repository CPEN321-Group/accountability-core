module.exports = function(app) {
  app.get('/',(req, res) => {
    console.log('server is being accessed')
    res.send('server is active')
  })

  require('./main_modules/accounts/account-routes')(app);
  require('./main_modules/goals/goal-routes')(app);
  require('./main_modules/transactions/transaction-routes')(app);
  require('./main_modules/messaging/messaging-routes')(app);
  require('./main_modules/reports/report-routes')(app);
  require('./utils/plaid/plaid-routes')(app);
  require('./utils/stripe/stripe-routes')(app);
}
