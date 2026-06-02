// import React, { useEffect, useState } from "react";
// import api from "../../api/api";
// import MovieCard from "./MovieCard";
// import MovieSearch from "./MovieSearch";

// export default function MovieList() {
//   const [movies, setMovies] = useState([]);

//   async function fetchAll() {
//     const res = await api.get("/api/v1.0/moviebooking/all");
//     setMovies(res.data || []);
//   }

//   useEffect(() => { fetchAll(); }, []);

//   async function onSearch(q) {
//     if(!q) return fetchAll();
//     const res = await api.get(`/api/v1.0/moviebooking/movies/search/${encodeURIComponent(q)}`);
//     setMovies(res.data || []);
//   }

//   return (
//     <div className="container">
//       <h2 className="page-title">🎬 Now Showing</h2>
//       <div className="search-box">
//         <MovieSearch onSearch={onSearch} />
//       </div>

//       <div className="movie-grid">
//         {movies.map((m) => (
//           <MovieCard key={m.id} movie={m} />
//         ))}
//       </div>
//     </div>
//   );
// }
import React, { useEffect, useState } from "react";
import api from "../../api/api";
import MovieCard from "./MovieCard";
import MovieSearch from "./MovieSearch";

export default function MovieList() {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [noResults, setNoResults] = useState(false);

  async function fetchAll() {
    setLoading(true);
    const res = await api.get("/api/v1.0/moviebooking/all");
    setMovies(res.data || []);
    setNoResults(!res.data || res.data.length === 0);
    setLoading(false);
  }

  useEffect(() => { fetchAll(); }, []);

  async function onSearch(q) {
    if (!q) return fetchAll();
    setLoading(true);
    const res = await api.get(
      `/api/v1.0/moviebooking/movies/search/${encodeURIComponent(q)}`
    );
    setMovies(res.data || []);
    setNoResults(!res.data || res.data.length === 0);
    setLoading(false);
  }

  return (
    <div className="container">
      <h2 className="page-title">🎬 Now Showing</h2>
      <div className="search-box">
        <MovieSearch onSearch={onSearch} />
      </div>

      {loading ? (
        <p>Loading movies...</p>
      ) : noResults ? (
        <p>No movies found.</p>
      ) : (
        <div className="movie-grid">
          {movies.map((m) => (
            <MovieCard key={m.id} movie={m} />
          ))}
        </div>
      )}
    </div>
  );
}
