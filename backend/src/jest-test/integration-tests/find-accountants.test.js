const request = require('supertest');
const mongoose = require('mongoose');
const { server } = require('../../index');

beforeAll(done => {
  done();
})

const accountantId = '1456';
const accountantFields = {
  accountId: accountantId,
  firstname: 'Mary',
  lastname: 'Smith',
  email: 'test123@gmail.com',
  age: 27,
  profession: 'Accountant',
  isAccountant: true
}

describe('find accountants', () => {
  test('find all accountants', async () => {
    const res = await request(server).get('/accounts/accountants');

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
  }) 
  test('find no accountants', async () => {
    await request(server).delete(`/accounts/${accountantId}`);
    const res = await request(server).get('/accounts/accountants');

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
    expect(res.body.length).toBe(0);

    await request(server).post('/accounts').query({...accountantFields});
  }) 
})

describe('find accountants by search query', () => {
  test('find accountants with valid query', async () => {
    const res = await request(server).get('/search/accountants').query({firstname: 'Mary'});

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
    expect(res.body.length).toBeGreaterThan(0);
  })
  test('find no accountants', async () => {
    const res = await request(server).get('/search/accountants').query({firstname: 'asdfjaslkfjalsjgioasog'});

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
    expect(res.body.length).toBe(0);
  })
})

afterAll((done) => {
  mongoose.disconnect();
  server.close();
  done();
});
