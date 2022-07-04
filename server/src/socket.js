module.exports = function(io) {
  let users = []

  const addUser = (userId,socketId) => {
    !users.some(user => user.userId === userId) &&
      users.push({userId,socketId});
  }

  const removeUser = (socketId) => {
    users = users.filter(user => user.socketId !== socketId);
  }

  const getUser = (userId) => {
    return users.find(user => user.userId === userId);
  }

  io.on("connection", (socket) => {
    //on connection
    console.log('a user connected');

    //add a new online user
    socket.on('addUser', userId => { 
      addUser(userId,socket.id);
      io.emit("getUsers",users);
    })

    //send and get message
    socket.on("sendMessage", ({senderId, receiverId, text}) => {
      const receiver = getUser(receiverId);
      io.to(receiver.socketId).emit("getMessage", {
        userId: senderId,
        text
      })
    })

    //on disconnect
    socket.on("disconnect", () => {
      console.log('user disconnected')
      removeUser(socket.id)
      io.emit("getUsers",users);
    })

  })
}