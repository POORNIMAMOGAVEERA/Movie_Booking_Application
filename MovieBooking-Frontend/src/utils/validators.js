export function isNotEmpty(value) {
  return value && value.toString().trim().length > 0;
}

export function isEmail(value) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value || "");
}

export function passwordsMatch(pw, confirmPw) {
  return pw === confirmPw;
}

export function validateRegistration(data) {
  const errors = {};
  if (!isNotEmpty(data.firstName)) errors.firstName = "First name is required";
  if (!isNotEmpty(data.lastName)) errors.lastName = "Last name is required";
  if (!isEmail(data.email)) errors.email = "Valid email required";
  if (!isNotEmpty(data.loginId)) errors.loginId = "Login id is required";
  if (!isNotEmpty(data.password)) errors.password = "Password required";
  if (!isNotEmpty(data.confirmPassword)) errors.confirmPassword = "Confirm password required";
  if (!passwordsMatch(data.password, data.confirmPassword)) errors.confirmPassword = "Passwords must match";
  if (!isNotEmpty(data.contactNumber)) errors.contactNumber = "Contact number required";
  return errors;
}

export function validatePassword(password) {
  const regex =
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/;

  return regex.test(password);
}
