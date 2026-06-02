import React, { useEffect, useState } from "react";
import api from "../../api/api";
import "../../styles.css";

export default function MyTickets() {
  const [tickets, setTickets] = useState([]);

  async function fetchTickets() {
    const res = await api.get("/api/v1.0/moviebooking/tickets/user");
    setTickets(res.data || []);
  }

  useEffect(() => { fetchTickets(); }, []);

  return (
    <div className="tickets-page">
      <h2 className="page-title">🎟 My Tickets</h2>

      {tickets.length === 0 ? (
        <div className="empty-state">
          <p>No tickets booked yet 🙂</p>
        </div>
      ) : (
        <div className="ticket-grid">
          {tickets.map((t) => (
            <div key={t.id} className="ticket-card-small">
              <h3>{t.movieName}</h3>
              <p className="theatre">{t.theatreName}</p>
              <p>Seats: <strong>{t.seatNumber}</strong></p>
              <p>Quantity: {t.numberOfTickets}</p>
              <p className="status available">Confirmed</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
