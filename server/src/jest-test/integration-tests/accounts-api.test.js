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
  authorId: '1234',
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
    const res = await request(server).get('/accounts/' + nonExistingId);

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  }) 

})

afterAll((done) => {
  mongoose.disconnect();
  server.close();
  done();
});
