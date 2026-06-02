// import React, { useEffect, useState } from "react";
// import api from "../../api/api";
// import { useParams, useNavigate } from "react-router-dom";
// import "../../styles.css";

// export default function BookTicket() {
//   const { movieName, theatreName } = useParams();
//   const [available, setAvailable] = useState(0);
//   const [numberOfTickets, setNumberOfTickets] = useState(1);
//   const [bookedSeats, setBookedSeats] = useState([]);
//   const [selectedSeats, setSelectedSeats] = useState([]);
//   const [seatGrid, setSeatGrid] = useState([]);
//   const [msg, setMsg] = useState(null);
//   const nav = useNavigate();

//   // Generate seat numbers like A1-A20, B1-B20...
//   function generateSeatLayout(total) {
//     const seatsPerRow = 20;
//     const rows = Math.ceil(total / seatsPerRow);

//     const grid = [];
//     for (let r = 0; r < rows; r++) {
//       const rowLabel = String.fromCharCode(65 + r); // A, B, C...
//       const rowSeats = [];

//       for (let s = 1; s <= seatsPerRow; s++) {
//         const seatNumber = r * seatsPerRow + s;
//         if (seatNumber > total) break;

//         rowSeats.push(`${rowLabel}${s}`);
//       }
//       grid.push(rowSeats);
//     }

//     return grid;
//   }

//   useEffect(() => {
//     async function loadData() {
//       const movie = decodeURIComponent(movieName);
//       const theatre = decodeURIComponent(theatreName);

//       // get movie info
//       const res = await api.get(`/api/v1.0/moviebooking/all`);
//       const movieRow = res.data.find(
//         (m) => m.movieName === movie && m.theatreName === theatre
//       );

//       setAvailable(movieRow.availableTickets);

//       // get booked seats
//       const booked = await api.get(
//         `/api/v1.0/moviebooking/admin/movies/${movie}/${theatre}/booked-seats`
//       );
//       setBookedSeats(booked.data);

//       // generate grid
//       setSeatGrid(generateSeatLayout(movieRow.totalTickets));
//     }

//     loadData();
//   }, [movieName, theatreName]);

//   function toggleSeat(seat) {
//     if (selectedSeats.includes(seat)) {
//       setSelectedSeats(selectedSeats.filter((s) => s !== seat));
//     } else {
//       if (selectedSeats.length < numberOfTickets) {
//         setSelectedSeats([...selectedSeats, seat]);
//       }
//     }
//   }

//   async function submit(e) {
//     e.preventDefault();

//     if (selectedSeats.length !== parseInt(numberOfTickets)) {
//       setMsg({
//         type: "error",
//         text: `Select exactly ${numberOfTickets} seats.`,
//       });
//       return;
//     }

//     try {
//       const payload = {
//         movieName: decodeURIComponent(movieName),
//         theatreName: decodeURIComponent(theatreName),
//         numberOfTickets: selectedSeats.length,
//         seatNumber: selectedSeats.join(","),
//       };

//       const res = await api.post(
//         `/api/v1.0/moviebooking/${decodeURIComponent(movieName)}/add`,
//         payload
//       );

//       setMsg({ type: "success", text: res.data });
//       setTimeout(() => nav("/tickets"), 1500);
//     } catch (err) {
//       setMsg({
//         type: "error",
//         text: err.response?.data || "Booking failed. Try again.",
//       });
//     }
//   }

//   return (
//     <div className="ticket-container">
//       <div className="ticket-card seat-grid-card">
//         <h2>🎟 Select Your Seats</h2>
//         <p className="subtitle">
//           {decodeURIComponent(movieName)} — {decodeURIComponent(theatreName)}
//         </p>

//         {/* Select ticket count */}
//         <label>No. of Tickets</label>
//         <input
//           type="number"
//           min="1"
//           max={available}
//           value={numberOfTickets}
//           onChange={(e) => {
//             setNumberOfTickets(e.target.value);
//             setSelectedSeats([]); // reset if count changes
//           }}
//         />

//         {/* Seat Grid */}
//         <div className="seat-grid">
//           {seatGrid.map((row, idx) => (
//             <div className="seat-row" key={idx}>
//               {row.map((seat) => {
//                 const isBooked = bookedSeats.includes(seat);
//                 const isSelected = selectedSeats.includes(seat);

//                 return (
//                   <button
//                     key={seat}
//                     className={`seat 
//                       ${isBooked ? "booked" : ""}
//                       ${isSelected ? "selected" : ""}`}
//                     disabled={isBooked}
//                     onClick={() => toggleSeat(seat)}
//                   >
//                     {seat}
//                   </button>
//                 );
//               })}
//             </div>
//           ))}
//         </div>

//         {/* Confirm */}
//         <button className="btn primary-btn full" onClick={submit}>
//           Confirm Booking
//         </button>

//         {msg && (
//           <div className={`alert ${msg.type === "success" ? "success" : "error"}`}>
//             {msg.text}
//           </div>
//         )}
//       </div>
//     </div>
//   );
// }

import React, { useEffect, useState } from "react";
import api from "../../api/api";
import { useParams, useNavigate } from "react-router-dom";
import "../../styles.css";

export default function BookTicket() {
  const { movieName, theatreName } = useParams();
  const nav = useNavigate();

  const [available, setAvailable] = useState(0);
  const [numberOfTickets, setNumberOfTickets] = useState(1);
  const [bookedSeats, setBookedSeats] = useState([]);
  const [selectedSeats, setSelectedSeats] = useState([]);
  const [seatGrid, setSeatGrid] = useState([]);
  const [msg, setMsg] = useState(null);

  const decodedMovie = decodeURIComponent(movieName);
  const decodedTheatre = decodeURIComponent(theatreName);

  // Generate seat layout
  function generateSeatLayout(total) {
    const seatsPerRow = 20;
    const rows = Math.ceil(total / seatsPerRow);
    const grid = [];

    for (let r = 0; r < rows; r++) {
      const rowLabel = String.fromCharCode(65 + r); // A, B, C
      const rowSeats = [];
      for (let s = 1; s <= seatsPerRow; s++) {
        const seatNumber = r * seatsPerRow + s;
        if (seatNumber > total) break;
        rowSeats.push(`${rowLabel}${s}`);
      }
      grid.push(rowSeats);
    }
    return grid;
  }

  useEffect(() => {
    async function loadData() {
      try {
        const res = await api.get(`/api/v1.0/moviebooking/all`);
        const movieRow = res.data.find(
          (m) => m.movieName === decodedMovie && m.theatreName === decodedTheatre
        );

        if (!movieRow) {
          setMsg({ type: "error", text: "Movie or theatre not found!" });
          return;
        }

        setAvailable(movieRow.availableTickets);

        const booked = await api.get(
          `/api/v1.0/moviebooking/admin/movies/${decodedMovie}/${decodedTheatre}/booked-seats`
        );
        setBookedSeats(booked.data);

        setSeatGrid(generateSeatLayout(movieRow.totalTickets));
      } catch (err) {
        setMsg({ type: "error", text: "Failed to load seats. Try again." });
      }
    }
    loadData();
  }, [decodedMovie, decodedTheatre]);

  function toggleSeat(seat) {
    if (selectedSeats.includes(seat)) {
      setSelectedSeats(selectedSeats.filter((s) => s !== seat));
    } else if (selectedSeats.length < parseInt(numberOfTickets, 10)) {
      setSelectedSeats([...selectedSeats, seat]);
    }
  }

  async function submit(e) {
    e.preventDefault();

    if (selectedSeats.length !== parseInt(numberOfTickets, 10)) {
      setMsg({
        type: "error",
        text: `Select exactly ${numberOfTickets} seat(s).`,
      });
      return;
    }

    try {
      const payload = {
        movieName: decodedMovie,
        theatreName: decodedTheatre,
        numberOfTickets: selectedSeats.length,
        seatNumber: selectedSeats.join(","),
      };

      const res = await api.post(`/api/v1.0/moviebooking/${decodedMovie}/add`, payload);
      setMsg({ type: "success", text: res.data });
      setTimeout(() => nav("/tickets"), 1500);
    } catch (err) {
      setMsg({
        type: "error",
        text: err.response?.data || "Booking failed. Try again.",
      });
    }
  }

  return (
    <div className="ticket-container">
      <div className="ticket-card seat-grid-card">
        <h2>🎟 Select Your Seats</h2>
        <p className="subtitle">{decodedMovie} — {decodedTheatre}</p>

        <label>No. of Tickets</label>
        <input
          type="number"
          min="1"
          max={available}
          value={numberOfTickets}
          onChange={(e) => {
            setNumberOfTickets(e.target.value);
            setSelectedSeats([]);
          }}
        />

        <div className="seat-grid">
          {seatGrid.map((row, idx) => (
            <div className="seat-row" key={idx}>
              {row.map((seat) => {
                const isBooked = bookedSeats.includes(seat);
                const isSelected = selectedSeats.includes(seat);

                return (
                  <button
                    key={seat}
                    className={`seat ${isBooked ? "booked" : ""} ${isSelected ? "selected" : ""}`}
                    disabled={isBooked}
                    onClick={() => toggleSeat(seat)}
                  >
                    {seat}
                  </button>
                );
              })}
            </div>
          ))}
        </div>

        <button
          className="btn primary-btn full"
          onClick={submit}
          disabled={selectedSeats.length === 0}
        >
          Confirm Booking
        </button>

        {msg && (
          <div className={`alert ${msg.type === "success" ? "success" : "error"}`}>
            {msg.text}
          </div>
        )}
      </div>
    </div>
  );
}

