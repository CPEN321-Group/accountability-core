const { authenticate } = require("../accounts/account-auth");
const { UserReport } = require("./models");

module.exports = function(app) {
  app.route('/reports/:userId')
    .get((req,res,next) => {
      UserReport.findOne({userId: req.params.userId}, (err,userReport) => {
        if (err || !userReport) return next(err);
        return res.send(userReport);
      })
    })
    .delete((req,res,next) => {
        UserReport.findOneAndUpdate({userId: req.params.userId}, {reports: []}, {returnDocument: 'after'},(err,userReport) => {
          if (err || !userReport) return next(err);
          res.send(userReport);
        })
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