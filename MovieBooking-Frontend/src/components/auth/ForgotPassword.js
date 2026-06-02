// import React, { useState } from "react";
// import axios from "axios";
// import { useNavigate } from "react-router-dom";
// import { validatePassword } from "../../utils/validators";

// const ForgotPassword = () => {
//   const [formData, setFormData] = useState({
//     username: "",
//     newPassword: "",
//     confirmPassword: "",
//   });

//   const [message, setMessage] = useState("");
//   const navigate = useNavigate();

//   const handleChange = (e) => {
//     setFormData({ ...formData, [e.target.name]: e.target.value });
//   };

//   const handleSubmit = async (e) => {
//     e.preventDefault();

//     // Strong password check
//     if (!validatePassword(formData.newPassword)) {
//       setMessage(
//         "❌ Password must be at least 8 chars, include uppercase, lowercase, number & special character."
//       );
//       return;
//     }

//     // Password & confirm password match check
//     if (formData.newPassword !== formData.confirmPassword) {
//       setMessage("❌ Password and Confirm Password must match.");
//       return;
//     }

//     try {
//       const res = await axios.put(
//         `${process.env.REACT_APP_API_BASE}/api/v1.0/moviebooking/forgot`,
//         {
//           loginId: formData.username,
//           password: formData.newPassword,
//         }
//       );

//       setMessage(`✅ ${res.data || "Password reset successful!"}`);

//       // Redirect to login after 2.5s
//       setTimeout(() => {
//         navigate("/login");
//       }, 2500);
//     } catch (err) {
//       console.error(err);
//       if (err.response && err.response.data) {
//         setMessage(`❌ ${err.response.data}`);
//       } else {
//         setMessage("❌ Failed to reset password. Try again.");
//       }
//     }
//   };

//   return (
//     <div className="auth-container">
//       <h2>Reset Password</h2>

//       <form onSubmit={handleSubmit}>
//         <input
//           type="text"
//           name="username"
//           placeholder="Enter your username"
//           required
//           onChange={handleChange}
//         />

//         <input
//           type="password"
//           name="newPassword"
//           placeholder="Enter new password"
//           required
//           onChange={handleChange}
//         />
//         {formData.newPassword.length > 0 && (
//           <div className="password-hints">
//             <p className={formData.newPassword.length >= 8 ? "valid" : "invalid"}>• Min 8 chars</p>
//             <p className={/[A-Z]/.test(formData.newPassword) ? "valid" : "invalid"}>• Uppercase letter</p>
//             <p className={/[a-z]/.test(formData.newPassword) ? "valid" : "invalid"}>• Lowercase letter</p>
//             <p className={/[0-9]/.test(formData.newPassword) ? "valid" : "invalid"}>• Number</p>
//             <p className={/[!@#$%^&*]/.test(formData.newPassword) ? "valid" : "invalid"}>• Special character</p>
//           </div>
//         )}

//         <input
//           type="password"
//           name="confirmPassword"
//           placeholder="Confirm new password"
//           required
//           onChange={handleChange}
//         />

//         <button type="submit">Reset Password</button>
//       </form>

//       {message && <p className="message">{message}</p>}
//     </div>
//   );
// };

// export default ForgotPassword;

import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../api/api";
import { validatePassword } from "../../utils/validators";

const ForgotPassword = ({ loggedInUsername }) => {
  const [formData, setFormData] = useState({
    username: loggedInUsername || "",
    newPassword: "",
    confirmPassword: "",
  });
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validatePassword(formData.newPassword)) {
      setMessage("❌ Password must be at least 8 chars, include uppercase, lowercase, number & special character.");
      return;
    }

    if (formData.newPassword !== formData.confirmPassword) {
      setMessage("❌ Password and Confirm Password must match.");
      return;
    }

    try {
      let res;

      if (sessionStorage.getItem("token")) {
        // Logged-in reset (JWT)
        res = await api.post(`/api/v1.0/moviebooking/${formData.username}/forgot`, {
          password: formData.newPassword,
        });
      } else {
        // Anonymous reset (no JWT)
        res = await api.put("/api/v1.0/moviebooking/forgot", {
          loginId: formData.username,
          password: formData.newPassword,
        });
      }

      setMessage(`✅ ${res.data || "Password reset successful!"}`);

      setTimeout(() => navigate("/login"), 2500);
    } catch (err) {
      console.error(err);
      setMessage(err.response?.data || "❌ Failed to reset password. Try again.");
    }
  };

  return (
    <div className="auth-container">
      <h2>Reset Password</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          name="username"
          placeholder="Enter your username"
          required
          value={formData.username}
          onChange={handleChange}
          disabled={!!loggedInUsername} // prevent changing logged-in username
        />
        <input type="password" name="newPassword" placeholder="New Password" required onChange={handleChange} />
        {formData.newPassword && (
          <div className="password-hints">
            <p className={formData.newPassword.length >= 8 ? "valid" : "invalid"}>• Min 8 chars</p>
            <p className={/[A-Z]/.test(formData.newPassword) ? "valid" : "invalid"}>• Uppercase letter</p>
            <p className={/[a-z]/.test(formData.newPassword) ? "valid" : "invalid"}>• Lowercase letter</p>
            <p className={/[0-9]/.test(formData.newPassword) ? "valid" : "invalid"}>• Number</p>
            <p className={/[!@#$%^&*]/.test(formData.newPassword) ? "valid" : "invalid"}>• Special character</p>
          </div>
        )}
        <input type="password" name="confirmPassword" placeholder="Confirm Password" required onChange={handleChange} />
        <button type="submit">Reset Password</button>
      </form>
      {message && <p className="message">{message}</p>}
    </div>
  );
};

export default ForgotPassword;
