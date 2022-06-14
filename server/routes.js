module.exports = function(app) {
  app.get('/',(req, res) => {
    res.send('server is active')
  })


  /**
   * Notes: 
   * - reviews and ratings are handled in accounts module
   * - treasury does not have its own routes, it is used by routes defined in accounts module
   */
  require('./accounts/accountRoutes')(app);
  require('./goals/goalRoutes')(app);
  require('./transactions/transactionRoutes')(app);
  require('./messaging/messagingRoutes')(app);
  require('./reports/reportRoutes')(app);
}