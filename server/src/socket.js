module.exports = function(io) {
  let users = []

  const addUser = (userId,socketId) => {
    !users.some(user => user.userId === userId) &&
      users.push({userId,socketId});
    console.log(`adding users:`);
    console.log(users)
  }

  const removeUser = (socketId) => {
    users = users.filter(user => user.socketId !== socketId);
  }

  const getUser = (userId) => {
    console.log(`userId query is: ${userId}`)
    let foundUser;
    users.forEach(user => {
      console.log(user);
      console.log(`comparing ${user.userId} || ${userId}`);
      if (user.userId === userId)
        foundUser = user;
    })
    return foundUser;
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
    socket.on("sendMessage", (senderId, receiverId, text) => {
      const parsedReceiverId = receiverId.split(`\"`)[1];
      console.log(senderId + parsedReceiverId + text);
      const receiver = getUser(parsedReceiverId);
      try {
        io.to(receiver.socketId).emit("getMessage", {
          userId: senderId,
          text
        })
      } catch (err) {
        console.log(err);
      }
    })

    //on disconnect
    socket.on("disconnect", () => {
      console.log('user disconnected')
      removeUser(socket.id)
      io.emit("getUsers",users);
    })

  })
}