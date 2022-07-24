const request = require('supertest');
const app = require('../../index');
const mongoose = require('mongoose');
const { server } = require('../../index');

beforeAll(done => {
  done();
})

describe('find accountants', () => {
  test('find all accountants', async () => {
    const res = await request(server).get('/accounts/accountants');

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
  }) 
})

afterAll((done) => {
  mongoose.disconnect();
  server.close();
  done();
});
