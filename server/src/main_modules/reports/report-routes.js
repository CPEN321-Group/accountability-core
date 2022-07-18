const { findReports, createReport, updateAccountant, deleteReports, findReport, updateRecommendations, deleteReport, findUserReports } = require("./report-store");

module.exports = function(app) {
  app.route('/reports/users/:accountId')
    .get(async (req,res) => {
      await findReports(req.params.accountId,(err,status,returnData) => {
        res.status(status).json(returnData);
      })
    })
    .post(async (req,res) => {
      await createReport(
        req.params.accountId, 
        req.query.monthYear,
        (err,status,returnData) => {
          res.status(status).json(returnData);
        })
    })
    .put(async (req,res) => { //updates the userReport's accountantId
      await updateAccountant(
        req.params.accountId,
        req.query.accountantId,
        (err,status,returnData) => {
          res.status(status).json(returnData);
        })
    })
    .delete(async (req,res) => {
      await deleteReports(req.params.accountId,(err,status,returnData) => {
        res.status(status).json(returnData);
      })
    })

  app.route('/reports/users/:accountId/:reportId')
    .get(async (req,res) => {
      await findReport(
        req.params.accountId,
        req.params.reportId,
        (err,status,returnData) => {
          res.status(status).json(returnData);
        })
    })
    .put(async (req,res) => { //add/update recommendations
      await updateRecommendations(
        req.params.accountId,
        req.params.reportId,
        req.query.recommendations,
        (err,status,returnData) => {
          res.status(status).json(returnData);
        })
    })
    .delete(async (req,res) => {
      await deleteReport(
        req.params.accountId,
        req.params.reportId,
        (err,status,returnData) => {
          res.status(status).json(returnData);
        })
    })

  app.get('/reports/accountants/:accountantId', async (req,res) => { //fetch all userReports accessible by the acocuntant
    await findUserReports(req.params.accountantId,(err,status,returnData) => {
      res.status(status).json(returnData);
    })
  })
}