module.exports = function(app) {
  app.get('/',(req, res) => {
    res.send('server is active')
  })

  /**
   * Notes: 
   * - reviews and ratings are handled in accounts module
   * - treasury does not have its own routes, it is used by routes defined in accounts module
   */
  require('./main_modules/accounts/account-routes')(app);
  require('./main_modules/goals/goal-routes')(app);
  require('./main_modules/transactions/transaction-routes')(app);
  require('./main_modules/messaging/messaging-routes')(app);
  require('./main_modules/reports/report-routes')(app);

  //set default error handler
  app.use((err, req, res, next) => {
    if (res.headersSent) {
      return next(err)
    }
    res.status(400).end('missing parameters');
  })
}
