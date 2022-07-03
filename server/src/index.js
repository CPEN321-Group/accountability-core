require('dotenv').config();
const http = require('http');
const express = require('express')
const port = 8000;
const cors = require('cors');
const socketio = require('socket.io')

const app = express()
const server = http.createServer(app);
const io = socketio(server)

app.use(express.json());
app.use(cors());

io.on('connection', socket => {
  console.log('new ws connection')
})

require('./routes')(app);

server.listen(port, () => {
  console.log(`App listening on port ${port}`)
})