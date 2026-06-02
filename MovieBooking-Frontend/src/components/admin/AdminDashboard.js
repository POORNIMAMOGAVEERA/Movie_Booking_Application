import React, { useEffect, useState } from "react";
import api from "../../api/api";
import "../../styles.css";

export default function AdminDashboard() {
  const [movies, setMovies] = useState([]);
  const [statusMsg, setStatusMsg] = useState(null);
  const [newMovie, setNewMovie] = useState({
    movieName: "",
    theatreName: "",
    availableTickets: "",
  });

  async function loadMovies() {
    try {
      const res = await api.get("/api/v1.0/moviebooking/all");
      const moviesData = res.data;

      // Fetch booked tickets count for each movie
      for (let m of moviesData) {
        const countRes = await api.get(
          `/api/v1.0/moviebooking/admin/movies/${m.movieName}/booked-count`
        );
        m.bookedCount = countRes.data;
      }

      setMovies(moviesData);
    } catch (err) {
      console.error("Error fetching movies", err);
    }
  }


  async function updateStatus(movieName, status) {
    try {
      await api.put(`/api/v1.0/moviebooking/${movieName}/update/status`, {
        status,
      });
      setStatusMsg(`Status for ${movieName} updated to "${status}"`);
      loadMovies();
      setTimeout(() => setStatusMsg(null), 2500);
    } catch (err) {
      console.error("Error updating status", err);
      setStatusMsg("Update failed. Please try again.");
    }
  }

  async function deleteMovie(movieName, id) {
    if (!window.confirm(`Delete ${movieName}?`)) return;
    try {
      await api.delete(`/api/v1.0/moviebooking/${movieName}/delete/${id}`);
      loadMovies();
      setStatusMsg(`${movieName} deleted successfully`);
      setTimeout(() => setStatusMsg(null), 2500);
    } catch (err) {
      console.error("Error deleting movie", err);
    }
  }

  async function addMovie(e) {
    e.preventDefault();
    if (
      !newMovie.movieName.trim() ||
      !newMovie.theatreName.trim() ||
      !newMovie.availableTickets
    ) {
      setStatusMsg("All fields are required");
      setTimeout(() => setStatusMsg(null), 2000);
      return;
    }

    try {
      await api.post("/api/v1.0/moviebooking/movies/add", {
        movieName: newMovie.movieName,
        theatreName: newMovie.theatreName,
        totalTickets: Number(newMovie.availableTickets),
        availableTickets: Number(newMovie.availableTickets),
        status: "Available"
      });

      setStatusMsg(`${newMovie.movieName} added successfully`);
      setNewMovie({ movieName: "", theatreName: "", availableTickets: "" });
      loadMovies();
      setTimeout(() => setStatusMsg(null), 2500);
    } catch (err) {
      console.error("Error adding movie", err);
      setStatusMsg("Failed to add movie");
    }
  }

  async function refreshMovie(movieName, theatreName) {
    try {
      const res = await api.put(
        `/api/v1.0/moviebooking/admin/movies/${movieName}/${theatreName}/refresh`
      );
      setStatusMsg(res.data);
      loadMovies();
    } catch (err) {
      setStatusMsg("failed: Could not refresh availability");
    }
  }

  useEffect(() => {
    loadMovies();
  }, []);

  return (
    <div className="admin-page">
      <h2 className="page-title">🎬 Admin Dashboard</h2>
      <p className="subtitle">Manage movies, ticket availability & status</p>

      {statusMsg && <div className="alert success">{statusMsg}</div>}

      {/* ========== ADD NEW MOVIE FORM ========== */}
      <form className="add-movie-form" onSubmit={addMovie}>
        <h3>Add New Movie</h3>
        <div className="form-group">
          <input
            type="text"
            placeholder="Movie Name"
            value={newMovie.movieName}
            onChange={(e) =>
              setNewMovie({ ...newMovie, movieName: e.target.value })
            }
          />
          <input
            type="text"
            placeholder="Theatre Name"
            value={newMovie.theatreName}
            onChange={(e) =>
              setNewMovie({ ...newMovie, theatreName: e.target.value })
            }
          />
          <input
            type="number"
            placeholder="Total Tickets"
            value={newMovie.availableTickets}
            onChange={(e) =>
              setNewMovie({ ...newMovie, availableTickets: e.target.value })
            }
          />
          <button className="btn primary-btn" type="submit">
            Add Movie
          </button>
        </div>
      </form>

      {/* ========== MOVIES GRID ========== */}
      <div className="admin-grid">
        {movies.map((m) => (
          <div key={m.id} className="admin-card">
            <h3>{m.movieName}</h3>
            <p className="theatre">{m.theatreName}</p>
            <p>Booked Tickets: {m.bookedCount ?? 0}</p>
            <p>Available: {m.availableTickets}</p>
            <p>
              Status:{" "}
              <span
                className={`status ${m.status === "SOLD OUT" ? "soldout" : "available"
                  }`}
              >
                {m.status}
              </span>
            </p>

            <div className="admin-actions">
              <button
                className="btn primary-btn small"
                onClick={() => updateStatus(m.movieName, "BOOK ASAP")}
              >
                Mark Book ASAP
              </button>
              <button
                className="btn danger-btn small"
                onClick={() => updateStatus(m.movieName, "SOLD OUT")}
              >
                Mark Sold Out
              </button>
              <button
                className="btn danger-btn small"
                onClick={() => refreshMovie(m.movieName, m.theatreName)}>
                Refresh Availability
              </button>
              <button
                className="btn secondary-btn small"
                onClick={() => deleteMovie(m.movieName, m.id)}
              >
                Delete
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
