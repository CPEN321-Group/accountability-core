module.exports = function(app) {
  app.get('/',(req, res) => {
    res.send('server is active')
  })

  /**
   * Notes: 
   * - reviews and ratings are handled in accounts module
   * - treasury does not have its own routes, it is used by routes defined in accounts module
   */
  require('./main_modules/accounts/accountRoutes')(app);
  require('./main_modules/goals/goalRoutes')(app);
  require('./main_modules/transactions/transactionRoutes')(app);
  require('./main_modules/messaging/messagingRoutes')(app);
  require('./main_modules/reports/reportRoutes')(app);
}
