// import React, { useState } from "react";
// import axios from "axios";
// import { useNavigate } from "react-router-dom";
// import { validatePassword } from "../../utils/validators";

// const ResetPassword = () => {
//     const navigate = useNavigate();
//     const user = JSON.parse(sessionStorage.getItem("loggedInUser"));

//     const [formData, setFormData] = useState({
//         newPassword: "",
//         confirmPassword: ""
//     });

//     const [message, setMessage] = useState("");

//     const handleChange = (e) => {
//         setFormData({ ...formData, [e.target.name]: e.target.value });
//     };

//     const handleSubmit = async (e) => {
//         e.preventDefault();

//         // Strong password check
//         if (!validatePassword(formData.newPassword)) {
//             setMessage(
//                 "❌ Password must be at least 8 chars, include uppercase, lowercase, number & special character."
//             );
//             return;
//         }

//         if (formData.newPassword !== formData.confirmPassword) {
//             setMessage("❌ Passwords do not match!");
//             return;
//         }

//         try {
//             const res = await axios.post(
//                 `${process.env.REACT_APP_API_BASE}/api/v1.0/moviebooking/${user.username}/forgot`,
//                 { password: formData.newPassword },
//                 { withCredentials: true }
//             );

//             setMessage("✅ " + res.data);

//             setTimeout(() => navigate("/"), 2000);
//         } catch (err) {
//             console.error(err);
//             if (err.response && err.response.data) {
//                 setMessage(`❌ ${err.response.data}`);
//             } else {
//                 setMessage("❌ Failed to reset password. Try again.");
//             }
//         }
//     };

//     return (
//         <div className="auth-container">
//             <h2>Change Password</h2>

//             <form onSubmit={handleSubmit}>
//                 <input
//                     type="password"
//                     name="newPassword"
//                     placeholder="New Password"
//                     required
//                     onChange={handleChange}
//                 />

//                 {formData.newPassword.length > 0 && (
//                     <div className="password-hints">
//                         <p className={formData.newPassword.length >= 8 ? "valid" : "invalid"}>• Min 8 chars</p>
//                         <p className={/[A-Z]/.test(formData.newPassword) ? "valid" : "invalid"}>• Uppercase letter</p>
//                         <p className={/[a-z]/.test(formData.newPassword) ? "valid" : "invalid"}>• Lowercase letter</p>
//                         <p className={/[0-9]/.test(formData.newPassword) ? "valid" : "invalid"}>• Number</p>
//                         <p className={/[!@#$%^&*]/.test(formData.newPassword) ? "valid" : "invalid"}>• Special character</p>
//                     </div>
//                 )}

//                 <input
//                     type="password"
//                     name="confirmPassword"
//                     placeholder="Confirm Password"
//                     required
//                     onChange={handleChange}
//                 />

//                 <button type="submit">Update</button>
//             </form>

//             {message && <p className="message">{message}</p>}
//         </div>
//     );
// };

// export default ResetPassword;

import React, { useState } from "react";
import api from "../../api/api";
import { useNavigate } from "react-router-dom";
import { validatePassword } from "../../utils/validators";

const ResetPassword = () => {
  const navigate = useNavigate();
  const user = JSON.parse(sessionStorage.getItem("loggedInUser"));

  const [formData, setFormData] = useState({
    newPassword: "",
    confirmPassword: "",
  });
  const [message, setMessage] = useState("");

  if (!user) {
    // Redirect if no logged-in user
    navigate("/login");
  }

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Strong password check
    if (!validatePassword(formData.newPassword)) {
      setMessage(
        "❌ Password must be at least 8 chars, include uppercase, lowercase, number & special character."
      );
      return;
    }

    if (formData.newPassword !== formData.confirmPassword) {
      setMessage("❌ Passwords do not match!");
      return;
    }

    try {
      const res = await api.post(
        `/api/v1.0/moviebooking/${user.username}/forgot`,
        { password: formData.newPassword }
      );

      setMessage("✅ " + res.data);

      setTimeout(() => navigate("/"), 2000);
    } catch (err) {
      console.error(err);
      if (err.response && err.response.data) {
        setMessage(`❌ ${err.response.data}`);
      } else {
        setMessage("❌ Failed to reset password. Try again.");
      }
    }
  };

  return (
    <div className="auth-container">
      <h2>Change Password</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="password"
          name="newPassword"
          placeholder="New Password"
          required
          onChange={handleChange}
        />
        {formData.newPassword.length > 0 && (
          <div className="password-hints">
            <p className={formData.newPassword.length >= 8 ? "valid" : "invalid"}>• Min 8 chars</p>
            <p className={/[A-Z]/.test(formData.newPassword) ? "valid" : "invalid"}>• Uppercase letter</p>
            <p className={/[a-z]/.test(formData.newPassword) ? "valid" : "invalid"}>• Lowercase letter</p>
            <p className={/[0-9]/.test(formData.newPassword) ? "valid" : "invalid"}>• Number</p>
            <p className={/[!@#$%^&*]/.test(formData.newPassword) ? "valid" : "invalid"}>• Special character</p>
          </div>
        )}
        <input
          type="password"
          name="confirmPassword"
          placeholder="Confirm Password"
          required
          onChange={handleChange}
        />
        <button type="submit">Update</button>
      </form>
      {message && <p className="message">{message}</p>}
    </div>
  );
};

export default ResetPassword;

