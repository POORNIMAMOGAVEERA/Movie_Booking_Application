// import React, { useState } from "react";
// import axios from "axios";
// import { Link, useNavigate } from "react-router-dom";
// import { validatePassword } from "../../utils/validators";

// const Register = () => {
//   const [formData, setFormData] = useState({
//     firstName: "",
//     lastName: "",
//     email: "",
//     loginId: "",
//     password: "",
//     confirmPassword: "",
//     contactNumber: "",
//   });
//   const [message, setMessage] = useState("");
//   const navigate = useNavigate();

//   const handleChange = (e) => {
//     setFormData({ ...formData, [e.target.name]: e.target.value });
//   };

//   const handleSubmit = async (e) => {
//     e.preventDefault();

//     if (formData.password !== formData.confirmPassword) {
//       setMessage("❌ Passwords do not match.");
//       return;
//     }

//     // Strong password check
//     if (!validatePassword(formData.password)) {
//       setMessage(
//         "❌ Password must be at least 8 chars, include uppercase, lowercase, number & special character."
//       );
//       return;
//     }

//     try {
//       const res = await axios.post(
//         `${process.env.REACT_APP_API_BASE}/api/v1.0/moviebooking/register`,
//         formData
//       );
//       setMessage("✅ Registration successful! Redirecting to login...");

//       setTimeout(() => {
//         navigate("/login");
//       }, 2000);
//     } catch (err) {
//       console.error(err);
//       setMessage("❌ Registration failed. Please check your inputs.");
//     }
//   };

//   return (
//     <div className="auth-container">
//       <h2>Create an Account</h2>
//       <form onSubmit={handleSubmit}>
//         <input
//           type="text"
//           name="firstName"
//           placeholder="First Name"
//           required
//           onChange={handleChange}
//         />
//         <input
//           type="text"
//           name="lastName"
//           placeholder="Last Name"
//           required
//           onChange={handleChange}
//         />
//         <input
//           type="email"
//           name="email"
//           placeholder="Email"
//           required
//           onChange={handleChange}
//         />
//         <input
//           type="text"
//           name="loginId"
//           placeholder="Login ID"
//           required
//           onChange={handleChange}
//         />
//         <input
//           type="password"
//           name="password"
//           placeholder="Password"
//           required
//           onChange={handleChange}
//         />
//         {/* Show password rules ONLY when user starts typing  */}
//         {formData.password.length > 0 && (
//           <div className="password-hints">
//             <p className={formData.password.length >= 8 ? "valid" : "invalid"}>
//               • Minimum 8 characters
//             </p>
//             <p className={/[A-Z]/.test(formData.password) ? "valid" : "invalid"}>
//               • Contains uppercase letter
//             </p>
//             <p className={/[a-z]/.test(formData.password) ? "valid" : "invalid"}>
//               • Contains lowercase letter
//             </p>
//             <p className={/[0-9]/.test(formData.password) ? "valid" : "invalid"}>
//               • Contains a number
//             </p>
//             <p className={/[!@#$%^&*]/.test(formData.password) ? "valid" : "invalid"}>
//               • Contains special character (!@#$%^&*)
//             </p>
//           </div>
//         )}

//         <input
//           type="password"
//           name="confirmPassword"
//           placeholder="Confirm Password"
//           required
//           onChange={handleChange}
//         />
//         <input
//           type="tel"
//           name="contactNumber"
//           placeholder="Contact Number"
//           required
//           onChange={handleChange}
//         />

//         <button type="submit">Register</button>
//       </form>

//       {message && <p className="message">{message}</p>}

//       {/* ✅ Login link below form */}
//       <p className="switch-link">
//         Already have an account?{" "}
//         <Link to="/login" className="link">
//           Login here
//         </Link>
//       </p>
//     </div>
//   );
// };

// export default Register;


import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../../api/api";
import { validatePassword } from "../../utils/validators";

const Register = () => {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    loginId: "",
    password: "",
    confirmPassword: "",
    contactNumber: "",
  });
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (formData.password !== formData.confirmPassword) {
      setMessage("❌ Passwords do not match.");
      return;
    }

    if (!validatePassword(formData.password)) {
      setMessage(
        "❌ Password must be at least 8 chars, include uppercase, lowercase, number & special character."
      );
      return;
    }

    try {
      const res = await api.post("/api/v1.0/moviebooking/register", formData);

      setMessage("✅ Registration successful! Redirecting to login...");
      setTimeout(() => navigate("/login"), 2000);
    } catch (err) {
      console.error(err);
      setMessage("❌ Registration failed. Please check your inputs.");
    }
  };

  return (
    <div className="auth-container">
      <h2>Create an Account</h2>
      <form onSubmit={handleSubmit}>
        <input type="text" name="firstName" placeholder="First Name" required onChange={handleChange} />
        <input type="text" name="lastName" placeholder="Last Name" required onChange={handleChange} />
        <input type="email" name="email" placeholder="Email" required onChange={handleChange} />
        <input type="text" name="loginId" placeholder="Login ID" required onChange={handleChange} />
        <input type="password" name="password" placeholder="Password" required onChange={handleChange} />
        {formData.password.length > 0 && (
          <div className="password-hints">
            <p className={formData.password.length >= 8 ? "valid" : "invalid"}>• Minimum 8 characters</p>
            <p className={/[A-Z]/.test(formData.password) ? "valid" : "invalid"}>• Contains uppercase letter</p>
            <p className={/[a-z]/.test(formData.password) ? "valid" : "invalid"}>• Contains lowercase letter</p>
            <p className={/[0-9]/.test(formData.password) ? "valid" : "invalid"}>• Contains a number</p>
            <p className={/[!@#$%^&*]/.test(formData.password) ? "valid" : "invalid"}>• Contains special character (!@#$%^&*)</p>
          </div>
        )}
        <input type="password" name="confirmPassword" placeholder="Confirm Password" required onChange={handleChange} />
        <input type="tel" name="contactNumber" placeholder="Contact Number" required onChange={handleChange} />

        <button type="submit">Register</button>
      </form>

      {message && <p className="message">{message}</p>}

      <p className="switch-link">
        Already have an account? <Link to="/login" className="link">Login here</Link>
      </p>
    </div>
  );
};

export default Register;

