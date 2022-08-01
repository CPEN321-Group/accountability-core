function isPastDate(date) {
  const today = new Date();
  return today.getTime() > date.getTime();
}

module.exports = {isPastDate}