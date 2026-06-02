// import React from "react";
// import { Link, useNavigate } from "react-router-dom";
// import "../../styles.css";

// export default function Navbar({ user, onLogout }) {
//   const navigate = useNavigate();

//   const handleLogout = () => {
//     onLogout(); // clear user session in parent (App.js)
//     navigate("/login");
//   };

//   return (
//     <nav className="navbar">
//       <div className="navbar-logo">🎟️ MovieBookingApp</div>

//       <ul className="nav-links">
//         <li><Link to="/">Home</Link></li>

//         {!user && (
//           <>
//             <li><Link to="/register">Register</Link></li>
//             <li><Link to="/login">Login</Link></li>
//           </>
//         )}

//         {user && (
//           <>
//             <li><Link to="/tickets">My Tickets</Link></li>
//             <li><Link to="/reset-password">Change Password</Link></li>
//             {user.role === "ADMIN" && (
//               <li><Link to="/admin">Admin</Link></li>
//             )}
//             <li className="navbar-user">Hi, {user.username}</li>
//             <li>
//               <button className="logout-btn" onClick={handleLogout}>
//                 Logout
//               </button>
//             </li>
//           </>
//         )}
//       </ul>
//     </nav>
//   );
// }

import React from "react";
import { Link, useNavigate } from "react-router-dom";
import "../../styles.css";

export default function Navbar({ user, onLogout }) {
  const navigate = useNavigate();

  const handleLogout = () => {
    onLogout();
    navigate("/login");
  };

  const isLoggedIn = !!user;
  const isAdmin = user?.role === "ADMIN";

  return (
    <nav className="navbar">
      <div className="navbar-logo">🎟️ MovieBookingApp</div>

      <ul className="nav-links">

        {/* Home is always visible */}
        <li><Link to="/">Home</Link></li>

        {/* Guest Navigation */}
        {!isLoggedIn && (
          <>
            <li><Link to="/register">Register</Link></li>
            <li><Link to="/login">Login</Link></li>
          </>
        )}

        {/* Logged-in User Navigation */}
        {isLoggedIn && (
          <>
            {/* Normal User only */}
            {!isAdmin && (
              <li><Link to="/tickets">My Tickets</Link></li>
            )}

            <li><Link to="/reset-password">Change Password</Link></li>

            {/* Admin Panel */}
            {isAdmin && (
              <li><Link to="/admin">Admin</Link></li>
            )}

            <li className="navbar-user">Hi, {user.username}</li>

            <li>
              <button className="logout-btn" onClick={handleLogout}>
                Logout
              </button>
            </li>
          </>
        )}
      </ul>
    </nav>
  );
}
