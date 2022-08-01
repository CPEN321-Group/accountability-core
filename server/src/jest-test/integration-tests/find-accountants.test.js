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
    const res0 = await request(server).delete(`/accounts/${accountantId}`);
    const res = await request(server).get('/accounts/accountants');

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
    expect(res.body.length).toBe(0);

    await request(server).post('/accounts').query({...accountantFields});
  }) 
})

afterAll((done) => {
  mongoose.disconnect();
  server.close();
  done();
});
