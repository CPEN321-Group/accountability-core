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
  require('./utils/plaid/plaid-routes')(app);
  require('./utils/stripe/stripe-routes')(app);

  /**
   * Default error handler - invoked by next(err).
   * Status Codes: 
   * 400: bad request
   * 200: success
   * 204: no content is to be sent back
   * 404: not found
   * 408: request timeout
   * 501: not implemented (endpoint)
   * 500: internal server error
   */
  app.use((err, req, res, next) => {
    if (res.headersSent) {
      return next(err)
    }
    if (err) {
      return res.status(400).end(err.message);
    }
    res.status(400).end('missing parameters');
  })
}
