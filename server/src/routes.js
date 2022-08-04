const { googleVerifyToken } = require('./main_modules/accounts/account-auth');
const { ForbiddenError } = require('./utils/errors');

module.exports = function(app) {
  app.get('/',(req, res) => {
    console.log('server is being accessed')
    res.send('server is active')
  })

  app.all('*', async (req, res, next) => {
    const {token} = req.query;
    if ( req.path == '/' || !token) {
      return next();
    }

    //authenticate user
    const googleVerified = await googleVerifyToken(token);

    if (googleVerified) {
      return next();
    } else {
      return res.status(403).json(new ForbiddenError('invalid token provided'))
    }
  });
  
  require('./main_modules/accounts/account-routes')(app);
  require('./main_modules/goals/goal-routes')(app);
  require('./main_modules/transactions/transaction-routes')(app);
  require('./main_modules/messaging/messaging-routes')(app);
  require('./main_modules/reports/report-routes')(app);
  require('./utils/plaid/plaid-routes')(app);
  require('./utils/stripe/stripe-routes')(app);
  require('./main_modules/search/search-routes')(app);
}
