const request = require('supertest');
const mongoose = require('mongoose');
const { server } = require('../../index');
const { Conversation } = require('../../main_modules/messaging/messaging-models');

const existingId = '1234';
const nonExistingId = 'ai93n';
const accountFields = {
  accountId: existingId,
  firstname: 'Bob',
  lastname: 'Jones',
  email: 'test123@gmail.com',
  age: 25,
  profession: 'Student',
  isAccountant: false
}
const accountantId = '1456'
const accountantFields = {
  accountId: accountantId,
  firstname: 'Mary',
  lastname: 'Smith',
  email: 'test123@gmail.com',
  age: 27,
  profession: 'Accountant',
  isAccountant: true
}
let convId;

beforeAll(done => {
  done();
})

describe('get conversation between 2 accounts', () => {
  test('get existing conversation', async () => {
    await request(server).post('/accounts').query({...accountFields});
    await request(server).post('/accounts').query({...accountantFields});
    const res1 = await request(server).post('/messaging/conversation').query({
      account1Id: existingId,
      account2Id: accountantId
    });
    const res2 = await request(server).get('/messaging/conversation').query({
      account1Id: existingId,
      account2Id: accountantId
    });

    expect(res2.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res2.statusCode).toBe(200);
    expect(res2.body).toHaveProperty('members');
    expect(res2.body.members).toEqual(expect.arrayContaining([existingId,accountantId]));
    await Conversation.deleteOne({members: {$all: [existingId,accountantId]}});
  }) 
  test('get non-existing conversation', async () => {
    const res =  await request(server).get('/messaging/conversation').query({
      account1Id: existingId,
      account2Id: 'test'
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
})
describe('create new conversation between two accounts', () => {
  test('create conversation between existing accounts', async () => {
    const res =  await request(server).post('/messaging/conversation').query({
      account1Id: existingId,
      account2Id: accountantId
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('_id');
  })
  test('create conversation between at least 1 non-existing account', async () => {
    const res =  await request(server).post('/messaging/conversation').query({
      account1Id: existingId,
      account2Id: 'test'
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name', 'NotFoundError');
  })
  test('create conversation that already exists', async () => {
    const res =  await request(server).post('/messaging/conversation').query({
      account1Id: existingId,
      account2Id: accountantId
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(400);
    expect(res.body).toHaveProperty('name', 'ValidationError');
  })
})
describe('get all conversations for an account', () => {
  test('get all conversations of existing account', async () => {
    const res =  await request(server).get('/messaging/conversation/' + existingId);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
    await Conversation.deleteOne({members: {$all: [existingId,accountantId]}});
  })
  test('get all conversations for non-existing account', async () => {
    const res =  await request(server).get('/messaging/conversation/' + nonExistingId);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name', 'NotFoundError');
  })
  test('get all conversations of existing account with no conversations', async () => {
    const res =  await request(server).get('/messaging/conversation/' + existingId);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
    expect(res.body.length).toEqual(0);
  })
})
describe('get all messages in a conversation', () => {
  test('get all messages of existing conversation', async () => {
    const res1 =  await request(server).post('/messaging/conversation').query({
      account1Id: existingId,
      account2Id: accountantId
    });
    expect(res1.body).toHaveProperty('_id');
    convId = res1.body._id;
    const res =  await request(server).get('/messaging/message/' + convId);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
  })
  test('get all messages of non-existing conversation', async () => {
    const conv = new Conversation({members: ['1','2']});
    const res =  await request(server).get('/messaging/message/' + conv.id);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
  test('get all messages of conversation of conv with no messages', async () => {
    const res =  await request(server).get('/messaging/message/' + convId);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
    expect(res.body.length).toEqual(0);
  })
  test('get all messages of conversation without permissions', async () => {
    const res =  await request(server).get('/messaging/message/' + convId).query({
      token: 'invalid-token'
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name','ForbiddenError');
  })
})

describe('send new message in conversation', () => {
  test('send new message with no invalid fields', async () => {
    const messageFields = {
      sender: existingId,
      text: 'Hi'
    }
    const res =  await request(server).post('/messaging/message/' + convId).query({...messageFields});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('text', messageFields.text);
  })
  test('send new message with missing field', async () => {
    const messageFields = {
      text: 'Hi'
    }
    const res =  await request(server).post('/messaging/message/' + convId).query({...messageFields});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(400);
    expect(res.body).toHaveProperty('name', 'ValidationError');
  })
  test('send new message to non-existing conversation', async () => {
    const messageFields = {
      sender: existingId,
      text: 'Hi'
    }
    const conv = new Conversation({members: ['1','2']});
    const res =  await request(server).post('/messaging/message/' + conv.id).query({...messageFields});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
  test('send new message without permissions', async () => {
    const messageFields = {
      sender: existingId,
      text: 'Hi'
    }
    const res =  await request(server).post('/messaging/message/' + convId).query({...messageFields, token: 'invalid-token'});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name','ForbiddenError');
  })
})
describe('delete all messages in a converation', () => {
  test('delete messages for existing conversation', async () => {
    const res =  await request(server).delete('/messaging/message/' + convId);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toEqual('messages deleted');
  })
  test('delete messages for non-existing conversation', async () => {
    const conv = new Conversation({members: ['1','2']});
    const res =  await request(server).delete('/messaging/message/' + conv.id);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError')
  })
  test('delete messages without permissions', async () => {
    const res =  await request(server).delete('/messaging/message/' + convId).query({token: 'invalid-token'});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name','ForbiddenError');
  })
})
describe('end a conversation', () => {
  test('update existing conversation status to finished', async () => {
    const res =  await request(server).put('/messaging/conversation/finished/' + convId).query({
      isFinished: true
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('isFinished', true);
  })
  test('update non-existing conversation status to finished', async () => {
    const conv = new Conversation({members: ['1','2']});
    const res =  await request(server).put('/messaging/conversation/finished/' + conv.id).query({
      isFinished: true
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
  test('update conversation status without permissions', async () => {
    const res =  await request(server).put('/messaging/conversation/finished/' + convId).query({
      isFinished: true,
      token: 'invalid-token'
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name','ForbiddenError');
  })
})

afterAll((done) => {
  mongoose.disconnect();
  server.close();
  done();
});
