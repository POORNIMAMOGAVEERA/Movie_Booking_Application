// import React from "react";
// import { Link } from "react-router-dom";

// export default function MovieCard({ movie }) {
//   const isSoldOut =
//     movie.status === "SOLD OUT" || movie.availableTickets === 0;

//   return (
//     <div className="movie-card">
//       <div className="movie-card-content">
//         <h3>{movie.movieName}</h3>
//         <p className="theatre">{movie.theatreName}</p>

//         <p>
//           🎟 Available: <strong>{movie.availableTickets}</strong>
//         </p>

//         <p>
//           Status:{" "}
//           <span
//             className={
//               movie.status === "SOLD OUT" ? "sold" : "available"
//             }
//           >
//             {movie.status}
//           </span>
//         </p>

//         {/* Disable Book Now on SOLD OUT */}
//         <Link
//           to={
//             isSoldOut
//               ? "#"
//               : `/book/${encodeURIComponent(movie.movieName)}/${encodeURIComponent(
//                   movie.theatreName
//                 )}`
//           }
//           className={`btn ${isSoldOut ? "btn-disabled" : ""}`}
//           onClick={(e) => isSoldOut && e.preventDefault()}
//         >
//           {isSoldOut ? "Sold Out" : "Book Now"}
//         </Link>
//       </div>
//     </div>
//   );
// }

import React from "react";
import { Link } from "react-router-dom";

export default function MovieCard({ movie }) {
  const isSoldOut = movie.status === "SOLD OUT" || movie.availableTickets === 0;

  return (
    <div className="movie-card">
      <div className="movie-card-content">
        <h3>{movie.movieName}</h3>
        <p className="theatre">{movie.theatreName}</p>

        <p>
          🎟 Available: <strong>{movie.availableTickets}</strong>
        </p>

        <p>
          Status:{" "}
          <span className={isSoldOut ? "sold" : "available"}>
            {movie.status}
          </span>
        </p>

        {isSoldOut ? (
          <span className="btn btn-disabled" title="No tickets available">
            Sold Out
          </span>
        ) : (
          <Link
            to={`/book/${encodeURIComponent(movie.movieName)}/${encodeURIComponent(movie.theatreName)}`}
            className="btn"
          >
            Book Now
          </Link>
        )}
      </div>
    </div>
  );
}

