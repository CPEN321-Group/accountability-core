const { authenticate } = require("../accounts/account-auth");
const { UserReport } = require("./models");

module.exports = function(app) {
  app.route('/reports/:userId')
    .get((req,res,next) => {
      authenticate(req.query.token, req.params.userId, (err,foundUser) => {
        if (err || !foundUser) {
          authenticate(req.query.token, req.query.accountantId, (err,foundUser) => {
            if (err || !foundUser) 
              return next(err);
          })
        }
        UserReport.findOne({userId: req.params.userId}, (err,userReport) => {
          if (err || !userReport) return next(err);
          return res.send(userReport);
        })
      })
    })
    .delete((req,res,next) => {
      authenticate(req.query.token, userId, (err,foundUser) => {
        if (err || !foundUser) return next(err);
        UserReport.findOneAndUpdate({userId: req.params.userId}, {reports: []}, (err,userReport) => {
          if (err || !userReport) return next(err);
          res.send(userReport);
        })
      })

    })

  app.route('/reports/:userId/:reportId')
    .get((req,res) => {
      authenticate(req.query.token, req.query.accountantId, (err,foundUser) => {
        if (err || !foundUser) return next(err);
        UserReport.findOne({userId: req.params.userId}, (err,userReport) => {
          if (err || !userReport) return next(err);
          const report = userReport.reports.filter(r => r.id === req.params.reportId)[0]
          return res.send(report);
        })
      })
    })
    .put((req,res) => {
      authenticate(req.query.token, req.query.accountantId, (err,foundUser) => {
        if (err || !foundUser) return next(err);
        UserReport.findOne({userId: req.params.userId}, (err,userReport) => {
          if (err || !userReport) return next(err);
          const report = userReport.reports.filter(r => r.id === req.params.reportId)[0]
          return res.send(report);
        })
      })
    })
    .delete((req,res) => {
      res.send(req.params);
    })
}