require('dotenv').config();
const express = require('express')
const port = 8000;
const cors = require('cors');
const app = express();

const server = app.listen(port, () => {
  console.log(`App listening on port ${port}`)
})

app.use(express.json());
app.use(cors());

require('./routes')(app);

const io = require('socket.io')(server)
require('./socket')(io);



