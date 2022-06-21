require('dotenv').config();
const express = require('express')
const app = express()
const port = 8000;
const cors = require('cors');

app.use(express.json());
app.use(cors());


require('./routes')(app);
require('./utils/plaid/plaidRoutes')(app);



app.listen(port, () => {
  console.log(`Example app listening on port ${port}`)
})