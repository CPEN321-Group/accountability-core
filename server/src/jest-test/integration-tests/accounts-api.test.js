const request = require('supertest');
const app = require('../../index');
const mongoose = require('mongoose');
const { server } = require('../../index');

const existingId = '1234';
const nonExistingId = 'ai93n';
const accountFields = {
  accountId: '1234',
  firstname: 'Bob',
  lastname: 'Jones',
  email: 'test123@gmail.com',
  age: 25,
  profession: 'Student',
  isAccountant: false
}
const newAccountFields = {
  ...accountFields,
  accountId: nonExistingId
}

const accountantFields = {
  accountId: '1456',
  firstname: 'Mary',
  lastname: 'Smith',
  email: 'test123@gmail.com',
  age: 27,
  profession: 'Accountant',
  isAccountant: true
}
const reviewFields = {
  authorId: existingId,
  rating: 5,
  date: 'Jun 2022',
  title: 'Good work',
  content: 'Thank you'
}
const subscriptionFields = {
  subscriptionDate: new Date('Jun 2022'),
  expiryDate: new Date('July 2022')
}

beforeAll(done => {
  const res = request(server).post('/accounts').query({...accountFields});
  done();
})

describe('find an account', () => {
  test('account exists', async () => {
    const res = await request(server).get('/accounts/' + existingId);

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('accountId');
  }) 
  test('account does not exist', async () => {
    await request(server).delete('/accounts/' + nonExistingId);
    const res = await request(server).get('/accounts/' + nonExistingId);

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  }) 
})

describe('find accountants', () => {
  test('find all accountants', async () => {
    const res = await request(server).get('/accounts/accountants');

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
  }) 
})

describe('create an account', () => {
  test('create an account with an existing accountId', async () => {
    const res = await request(server).post('/accounts').query({...accountFields});

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(400);
    expect(res.body).toHaveProperty('name', 'ValidationError');
  }) 
  test('create an account with invalid parameters', async () => {
    const res = await request(server).post('/accounts').query({test: 'test'});

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(400);
    expect(res.body).toHaveProperty('name', 'ValidationError');
  }) 
  test('create an account with new accountId', async () => {
    await request(server).delete('/accounts/' + nonExistingId);
    const res = await request(server).post('/accounts').query(newAccountFields);

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('accountId', newAccountFields.accountId);

    await request(server).delete('/accounts/' + newAccountFields.accountId);
  }) 
})

describe('update account profile', () => {
  test('update profile of an existing account', async () => {
    const res = await request(server).put('/accounts/' + existingId).query({firstname: 'John'});

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('profile');
    expect(res.body.profile).toHaveProperty('firstname', 'John')
  })
  test('update profile of a non-existing account', async () => {
    const res = await request(server).put('/accounts/' + nonExistingId).query({firstname: 'John'});

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name', 'NotFoundError');
  })
  test('update profile with no parameters to update', async () => {
    const res = await request(server).put('/accounts/' + existingId);

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('profile');
  })
  test('update profile of an account that doesn\'t belong to the user', async () => {
    const res = await request(server).put('/accounts/' + existingId).query({
      firstname: 'John',
      token: 'invalid-token'
    });
    console.log(res.body);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name', 'ForbiddenRequestError');
  })
})

afterAll((done) => {
  mongoose.disconnect();
  server.close();
  done();
});
