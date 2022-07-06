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

  const getStartOfNextMonth = (date) => {
    let nextMonth = new Date(date);
    nextMonth.setHours(0, 0, 0, 0);
    nextMonth.setMonth(nextMonth.getMonth()+1,1);
    return nextMonth;
  }

  const getReport = async (userId, monthYear) => {
    const userReport = await UserReport.findOne({userId: userId});
    const foundReport = userReport.reports.find(report => {
      const monthMatch = report.monthYear.getMonth() === monthYear.getMonth();
      const yearMatch = report.monthYear.getYear() === monthYear.getYear();
      return monthMatch && yearMatch;
    })
    return foundReport;
  }

  app.route('/reports/users/:userId')
    .get(async (req,res,next) => {
      try {
        const userReport = await UserReport.findOne({userId: req.params.userId})
        res.status(200).json(userReport)
      } catch (err) {
        console.log(err)
        res.status(400).json(err);
      }
    })
    .post(async (req,res) => {
      const monthYear = new Date(req.query.monthYear);
      try {
        if (await getReport(req.params.userId,monthYear)) {
          return res.status(400).end('report already exists');
        }
        const startOfNextMonth = getStartOfNextMonth(monthYear);
        const userGoal = await UserGoal.findOne({userId: req.params.userId});
        const ongoingGoals = getOngoingGoals(userGoal.goals, startOfNextMonth);

        const userTransaction = await UserTransaction.findOne({userId: req.params.userId});
        const lastMonthTransactions = getMonthTransactions(userTransaction.transactions,startOfNextMonth);
        const income = formatTransactions(getIncome(lastMonthTransactions));
        const spendings = formatTransactions(getSpendings(lastMonthTransactions));
        const savings = getSavings(income,spendings);

        const newReport = new Report({
          monthYear:monthYear, income, spendings, savings,
          goalsInProgress: ongoingGoals
        })

        const userReport = await UserReport.findOneAndUpdate(
          {userId: req.params.userId}, 
          {$push: {reports: newReport}},
          {returnDocument: 'after'}
        )
        res.status(200).json(newReport)
      } catch (err) {
        console.log(err);
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
        console.log(err)
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
        console.log(err)
        res.status(400).json(err)
      }
    })

  app.route('/reports/users/:userId/:reportId')
    .get(async (req,res) => {
      try {
        const userReport = await UserReport.findOne({userId: req.params.userId});
        const report = userReport.reports.find(r => r.id === req.params.reportId);
        res.status(200).json(report);
      } catch(err) {
        console.log(err)
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
        console.log(err)
        res.status(400).json(err)
      }
        
    })
    .delete(async (req,res) => {
      try {
        const userReport = await UserReport.findOneAndUpdate(
          {userId: req.params.userId},
          {$pull: {reports: {_id: req.params.reportId}}},
          {returnDocument: 'after'},
        )
        res.status(200).end('report deleted');
      } catch (err) {
        console.log(err)
        res.status(400).json(err)
      }
    })

  app.get('/reports/accountants/:accountantId', async (req,res) => { //fetch all userReports accessible by the acocuntant
    try {
      const userReports = await UserReport.find({accountantId: req.params.accountantId});
      res.status(200).json(userReports);
    } catch (err) {
      console.log(err)
      res.status(400).json(err);
    }
  })
}