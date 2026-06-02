// import React, { useState } from "react";
// import axios from "axios";
// import { useNavigate, Link } from "react-router-dom";

// const Login = ({ onLogin }) => {
//   const [credentials, setCredentials] = useState({ loginId: "", password: "" });
//   const [message, setMessage] = useState("");
//   const navigate = useNavigate();

//   const handleChange = (e) => {
//     setCredentials({ ...credentials, [e.target.name]: e.target.value });
//   };

//   const handleSubmit = async (e) => {
//     e.preventDefault();

//     try {
//       const response = await axios.post(
//         `${process.env.REACT_APP_API_BASE}/api/v1.0/moviebooking/login`,
//         credentials,
//         { withCredentials: true }
//       );

//       if (response.status === 200) {
//         const { username, role } = response.data;

//         const userData = { username, role };

//         // This updates state & persists login
//         onLogin(userData);

//         sessionStorage.setItem("loggedInUser", JSON.stringify(userData));

//         setMessage("Login successful!");

//         // Redirect based on role
//         if (role === "ADMIN") navigate("/admin");
//         else navigate("/");
//       }
//     } catch (err) {
//       console.error(err);
//       setMessage("Invalid login credentials!");
//     }
//   };

//   return (
//     <div className="auth-container">
//       <h2>Welcome Back 🎬</h2>
//       <form onSubmit={handleSubmit}>
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
//         <button type="submit">Login</button>
//       </form>

//       {message && <p className="message">{message}</p>}

//       <div className="auth-links">
//         <p>
//           Don't have an account?{" "}
//           <Link to="/register" className="link">
//             Register here
//           </Link>
//         </p>
//         <p>
//           <Link to="/forgot-password" className="link">
//             Forgot Password?
//           </Link>
//         </p>
//       </div>
//     </div>
//   );
// };

// export default Login;


import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import api from "../../api/api"; // use your JWT-enabled api instance

const Login = ({ onLogin }) => {
  const [credentials, setCredentials] = useState({ loginId: "", password: "" });
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await api.post("/api/v1.0/moviebooking/login", credentials);

      if (response.status === 200) {
        const { username, role, token } = response.data;

        // Persist JWT in sessionStorage
        sessionStorage.setItem("token", token);

        const userData = { username, role };
        onLogin(userData); // update parent state
        sessionStorage.setItem("loggedInUser", JSON.stringify(userData));
        setMessage("Login successful!");

        // Redirect based on role
        if (role === "ADMIN") navigate("/admin");
        else navigate("/");
      }
    } catch (err) {
      console.error(err);
      if (err.response && err.response.status === 401) {
        setMessage("Invalid login credentials!");
      } else {
        setMessage("Login failed. Please try again.");
      }
    }
  };

  return (
    <div className="auth-container">
      <h2>Welcome Back 🎬</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          name="loginId"
          placeholder="Login ID"
          required
          onChange={handleChange}
        />
        <input
          type="password"
          name="password"
          placeholder="Password"
          required
          onChange={handleChange}
        />
        <button type="submit">Login</button>
      </form>

      {message && <p className="message">{message}</p>}

      <div className="auth-links">
        <p>
          Don't have an account?{" "}
          <Link to="/register" className="link">
            Register here
          </Link>
        </p>
        <p>
          <Link to="/forgot-password" className="link">
            Forgot Password?
          </Link>
        </p>
      </div>
    </div>
  );
};

export default Login;

