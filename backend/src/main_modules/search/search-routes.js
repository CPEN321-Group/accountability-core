const { NotFoundError } = require("../../utils/errors");
const { Account } = require("../accounts/account-models");
const { UserTransaction } = require("../transactions/transaction-models");

module.exports = function(app) {
  app.get('/search/transactions/:accountId', async (req,res) => {
    try {
      const userT = await UserTransaction.findOne({userId: req.params.accountId});
      if (!userT) {
        return res.status(404).json(new NotFoundError('account not found'));
      }
      const filteredT = userT.transactions.filter(t => t.title === req.query.title);
      res.status(200).json(filteredT);
    } catch (err) {
      console.log(err)
      res.status(400).json(err);
    }
  })
  app.get('/search/accountants', async (req,res) => {
    try {
      const accountants = await Account.find({"profile.firstname": req.query.firstname , isAccountant: true});

      res.status(200).json(accountants);
    } catch (err) {
      console.log(err)
      res.status(400).json(err);
    }
  })
}