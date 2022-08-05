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
    let foundUser;
    users.forEach(user => {
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
      console.log('adding user');
      addUser(userId,socket.id);
      console.log(users);
      io.emit("getUsers",users);
    })

    //send and get message
    socket.on("sendMessage", (senderId, receiverId, text) => {
      console.log(`receiverId: ${receiverId}\n`);
      const receiver = getUser(receiverId);
      try {
        io.to(receiver.socketId).emit("getMessage", {
          userId: senderId,
          text
        })
      } catch (err) {
        console.log(err);
        socket.emit('errorEvent', err);
      }
    })

    //on disconnect
    socket.on("disconnect", () => {
      console.log('user disconnected')
      removeUser(socket.id)
      console.log(users)
      io.emit("getUsers",users);
    })

  })
}