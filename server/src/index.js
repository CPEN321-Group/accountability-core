require('dotenv').config();
const http = require('http');
const express = require('express')
const port = 8000;
const cors = require('cors');

const app = express()
const server = http.createServer(app);
const io = require('socket.io')(server)

app.use(express.json());
app.use(cors());


require('./routes')(app);
require('./socket')(io);

server.listen(port, () => {
  console.log(`App listening on port ${port}`)
})

io.listen(server);