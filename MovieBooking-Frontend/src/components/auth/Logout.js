// import React from "react";
// import api from "../../api/api";
// import { useNavigate } from "react-router-dom";

// export default function Logout({ username }) {
//   const nav = useNavigate();
//   async function logout() {
//     await api.post(`/api/v1.0/moviebooking/${username}/logout`);
//     nav("/login");
//   }
//   return <button onClick={logout}>Logout</button>;
// }

import React from "react";
import api from "../../api/api";
import { useNavigate } from "react-router-dom";

export default function Logout({ onLogout }) {
  const nav = useNavigate();

  async function logout() {
    try {
      await api.post("/api/v1.0/moviebooking/logout"); // backend should invalidate token/session
    } catch (err) {
      console.error(err);
    } finally {
      sessionStorage.removeItem("token");       // remove JWT
      sessionStorage.removeItem("loggedInUser"); // remove user info
      onLogout?.(); // optional, clear user state in App.js
      nav("/login");
    }
  }

  return <button onClick={logout}>Logout</button>;
}

