class ValidationError extends Error {
  constructor(message) {
    super(message); // (1)
    this.name = "ValidationError"; // (2)
  }
}

class NotFoundError extends Error {
  constructor(message) {
    super(message); // (1)
    this.name = "NotFoundError"; // (2)
  }
}

module.exports = {
  ValidationError, NotFoundError
}