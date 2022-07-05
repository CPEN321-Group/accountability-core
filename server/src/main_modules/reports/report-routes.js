const { authenticate } = require("../accounts/account-auth");
const { UserGoal } = require("../goals/models");
const { UserTransaction } = require("../transactions/models");
const { UserReport } = require("./models");

module.exports = function(app) {
  const getOngoingGoals = (goals) => {
    let today = new Date();
    let ongoingGoals = goals.filter(goal => goal.deadline.getTime() > today.getTime());
    return ongoingGoals;
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
      const monthYear = new Date(req.query.monthYear);
      try {
        const startOfNextMonth = new Date(monthYear.setMonth(monthYear.getMonth()+1));
        const userGoal = await UserGoal.findOne({userId: req.params.userId});
        const ongoingGoals = getOngoingGoals(userGoal.goals);

        const userTransaction = await UserTransaction.findOne({userId: req.params.userId});


        const userReport = await UserReport.findOneAndUpdate(
          {userId: req.params.userId}, 
          {$push: {reports: newReport}},
          {returnDocument: 'after'}
        )
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
        const userReport = await UserReport.findOneAndUpdate({userId: req.params.userId}, {reports: []},{returnDocument: 'after'});
        res.status(200).json(userReport);
      } catch (err) {
        res.status(400).json(err)
      }
    })

  app.route('/reports/:userId/:reportId')
    .get((req,res) => {
        UserReport.findOne({userId: req.params.userId}, (err,userReport) => {
          if (err || !userReport) return next(err);
          const report = userReport.reports.filter(r => r.id === req.params.reportId)[0]
          return res.send(report);
        })
    })
    .put((req,res) => {
        UserReport.findOne({userId: req.params.userId}, (err,userReport) => {
          if (err || !userReport) return next(err);
          const report = userReport.reports.filter(r => r.id === req.params.reportId)[0]
          return res.send(report);
        })
    })
    .delete((req,res) => {
      res.send(req.params);
    })
}