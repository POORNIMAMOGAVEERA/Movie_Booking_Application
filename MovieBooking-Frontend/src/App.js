import React, { useState, useEffect } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Register from "./components/auth/Register";
import Login from "./components/auth/Login";
import Logout from "./components/auth/Logout";
import MovieList from "./components/movies/MovieList";
import BookTicket from "./components/movies/BookTicket";
import MyTickets from "./components/tickets/MyTickets";
import AdminDashboard from "./components/admin/AdminDashboard";
import ProtectedRoute from "./routes/ProtectedRoute";
import Navbar from "./components/common/NavBar";
import ForgotPassword from "./components/auth/ForgotPassword";
import ResetPassword from "./components/auth/ResetPassword";

function App() {
  const [user, setUser] = useState(null);
  const [authLoaded, setAuthLoaded] = useState(false);

  // Load saved user from sessionsStorage on first render
  useEffect(() => {
    const stored = sessionStorage.getItem("loggedInUser");
    if (stored) {
      setUser(JSON.parse(stored));  // { username, role }
    }
    setAuthLoaded(true);
  }, []);

  // Called when login is successful
  const handleLogin = (userData) => {
    setUser(userData);
    sessionStorage.setItem("loggedInUser", JSON.stringify(userData));
  };

  const handleLogout = () => {
    setUser(null);
    sessionStorage.removeItem("loggedInUser");
  };

  return (
    <BrowserRouter>
      <Navbar user={user} onLogout={handleLogout} />

      <Routes>
        <Route path="/" element={<MovieList />} />
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login onLogin={handleLogin} />} />
        <Route path="/logout" element={<Logout onLogout={handleLogout} />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />


        {/* Protect routes */}
        <Route
          path="/book/:movieName/:theatreName"
          element={
            <ProtectedRoute user={user} authLoaded={authLoaded}>
              <BookTicket />
            </ProtectedRoute>
          }
        />

        <Route
          path="/tickets"
          element={
            <ProtectedRoute user={user} authLoaded={authLoaded}>
              <MyTickets />
            </ProtectedRoute>
          }
        />

        <Route
          path="/reset-password"
          element={
            <ProtectedRoute user={user} authLoaded={authLoaded}>
              <ResetPassword />
            </ProtectedRoute>
          }
        />


        <Route
          path="/admin"
          element={
            <ProtectedRoute user={user} adminOnly={true} authLoaded={authLoaded}>
              <AdminDashboard />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
