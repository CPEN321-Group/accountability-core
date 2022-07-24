class ValidationError extends Error {
  constructor(message) {
    super(message);
    this.name = "ValidationError";
  }
}

class NotFoundError extends Error {
  constructor(message) {
    super(message); 
    this.name = "NotFoundError"; 
  }
}

class ForbiddenError extends Error {
  constructor(message) {
    super(message); 
    this.name = "ForbiddenError"; 
  }
}

module.exports = {
  ValidationError, NotFoundError, ForbiddenError
}