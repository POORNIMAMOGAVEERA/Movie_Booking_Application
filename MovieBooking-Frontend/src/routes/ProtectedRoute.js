import React from "react";
import { Navigate } from "react-router-dom";

const ProtectedRoute = ({ user, authLoaded, adminOnly = false, children }) => {
  
  // Wait until App finishes restoring auth
  if (!authLoaded) {
    return null; // or loader
  }
  
  // If still no user → redirect
  if (!user || !user.username) {
    return <Navigate to="/login" replace />;
  }

  // Admin-only route check
  if (adminOnly && user.role !== "ADMIN") {
    return <Navigate to="/" replace />;
  }

  return children;
};

export default ProtectedRoute;
