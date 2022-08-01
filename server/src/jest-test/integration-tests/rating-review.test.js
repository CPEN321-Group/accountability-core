const request = require('supertest');
const mongoose = require('mongoose');
const { server } = require('../../index');

const existingId = '1234';
// const nonExistingId = 'ai93n';
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
const reviewFields = {
  authorId: existingId,
  rating: 5,
  date: 'Jun 2022',
  title: 'Good work',
  content: 'Thank you'
}

beforeAll(done => {
  done();
})

describe('write a review', () => {
  test('write a review for an existing accountant', async () => {
    await request(server).post('/accounts').query({...accountantFields});
    await request(server).post('/accounts').query({...accountFields});
    const res = await request(server).post('/reviews/' + accountantId).query({...reviewFields});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('reviews');
    expect(res.body.reviews[res.body.reviews.length - 1]).toHaveProperty('title', reviewFields.title)
  })
  test('write a review for a non-existing accountant', async () => {
    const res = await request(server).post('/reviews/' + existingId).query({...reviewFields});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
  test('write a review with invalid parameters', async () => {
    await request(server).post('/accounts').query({...accountantFields});
    const res = await request(server).post('/reviews/' + accountantId).query({title: '#()&%', authorId: existingId});
    console.log(res.body)
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(400);
    expect(res.body).toHaveProperty('name', 'ValidationError');
  })
  test('write a review with invalid authentication', async () => {
    await request(server).post('/accounts').query({...accountantFields});
    const res = await request(server).post('/reviews/' + accountantId).query({
      ...reviewFields,
      token: 'invalid-token'
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name', 'ForbiddenError');
  })
})

afterAll((done) => {
  mongoose.disconnect();
  server.close();
  done();
});