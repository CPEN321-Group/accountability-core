const { authenticate } = require("../accounts/account-auth");
const { UserGoal } = require("../goals/models");
const { UserTransaction } = require("../transactions/models");
const { formatTransactions } = require("../transactions/transaction-store");
const { UserReport, Report } = require("./models");

module.exports = function(app) {
  const getOngoingGoals = (goals, startOfNextMonth) => {
    let ongoingGoals = goals.filter(goal => goal.deadline.getTime() > startOfNextMonth.getTime());
    return ongoingGoals;
  }
  const getMonthTransactions = (transactions, startOfNextMonth) => {
    let monthTransactions = transactions.filter(transaction => transaction.date.getTime() < startOfNextMonth.getTime());
    return monthTransactions;
  }
  const getIncome = (transactions) => {
    return transactions.filter(transaction => transaction.isIncome);
  }
  const getSpendings = (transactions) => {
    return transactions.filter(transaction => !transaction.isIncome);
  }
  const getSavings = (income, spendings) => {
    let totalIncome = 0;
    income.forEach(inc => totalIncome += inc.amount);

    let totalSpendings = 0;
    spendings.forEach(sp => totalSpendings += sp.amount);

    return totalIncome - totalSpendings;
  }

  const roundToStartOfNextMonth = (date) => {
    date.setHours(0, 0, 0, 0);
    date.setMonth(date.getMonth()+1,1);
    let nextMonth = new Date(date);
    return nextMonth;
  }

  app.route('/reports/:userId')
    .get(async (req,res,next) => {
      try {
        const userReport = await UserReport.findOne({userId: req.params.userId})
        res.status(200).json(userReport)
      } catch (err) {
        res.status(400).json(err);
      }
    })
    .post(async (req,res) => {
      format
      const monthYear = new Date(req.query.monthYear);
      try {
        const startOfNextMonth = roundToStartOfNextMonth(monthYear);
        const userGoal = await UserGoal.findOne({userId: req.params.userId});
        const ongoingGoals = getOngoingGoals(userGoal.goals, startOfNextMonth);

        const userTransaction = await UserTransaction.findOne({userId: req.params.userId});
        const lastMonthTransactions = getMonthTransactions(userTransaction.transactions,startOfNextMonth);
        const income = formatTransactions(getIncome(lastMonthTransactions));
        const spendings = formatTransactions(getSpendings(lastMonthTransactions));
        const savings = getSavings(income,spendings);

        const newReport = new Report({
          monthYear, income, spendings, savings,
          goalsInProgress: ongoingGoals
        })

        const userReport = await UserReport.findOneAndUpdate(
          {userId: req.params.userId}, 
          {$push: {reports: newReport}},
          {returnDocument: 'after'}
        )
        res.status(200).json(userReport.reports)
      } catch (err) {
        res.status(400).json(err);
      }
    })
    .put(async (req,res,next) => { //updates the userReport's accountantId
      try {
        const userReport = await UserReport.findOneAndUpdate(
          {userId: req.params.userId}, 
          {accountantId: req.query.accountantId},
          {returnDocument: 'after'}
        )
        res.status(200).json(userReport);
      } catch (err) {
        res.status(400).json(err);
      }
    })
    .delete(async (req,res,next) => {
      try {
        const userReport = await UserReport.findOneAndUpdate(
          {userId: req.params.userId}, 
          {reports: []},
          {returnDocument: 'after'}
        );
        res.status(200).json(userReport);
      } catch (err) {
        res.status(400).json(err)
      }
    })

  app.route('/reports/:userId/:reportId')
    .get(async (req,res) => {
      try {
        const userReport = await UserReport.findOne({userId: req.params.userId});
        const report = userReport.reports.find(r => r.id === req.params.reportId);
        res.status(200).json(report);
      } catch(err) {
        res.status(400).json(err)
      }
    })
    .put(async (req,res) => { //add/update recommendations
      const {recommendations} = req.query;
      try {
        const userReport = await UserReport.findOneAndUpdate(
          {$and:[{userId: req.params.userId}, {
            reports: { $elemMatch: { _id: req.params.reportId }}
          }]},
          {$set: {"reports.$.recommendations": recommendations}},
          {returnDocument: 'after'},
        )
        const report = userReport.reports.find(r => r.id === req.params.reportId)
        res.status(200).json(report);
      } catch (err) {
        res.status(400).json(err)
      }
        
    })
    .delete(async (req,res) => {
      try {
        const userReport = await findOneAndUpdate(
          {userId: req.params.userId},
          {$pull: {reports: {_id: req.params.reportId}}},
          {returnDocument: 'after'},
        )
        res.status(200).end('report deleted');
      } catch (err) {
        res.status(400).json(err)
      }
    })
}