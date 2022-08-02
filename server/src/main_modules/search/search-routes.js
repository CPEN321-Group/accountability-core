module.exports = function(app) {
  app.get('/search/transactions/', async (req,res) => {
    res.status(404).send('no transactions found');
  })
  app.get('/search/accountants/', async (req,res) => {
    res.status(404).send('no accountants found');
  })
}