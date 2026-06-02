import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import BookTicket from "../../movies/BookTicket";
import api from "../../../api/api";

// --- Mock API ---
jest.mock("../../../api/api", () => ({
  get: jest.fn(),
  post: jest.fn(),
}));

jest.useFakeTimers();

// --- Mock router hooks ---
const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  useParams: () => ({
    movieName: encodeURIComponent("Avatar"),
    theatreName: encodeURIComponent("PVR"),
  }),
  useNavigate: () => mockNavigate,
}));

describe("BookTicket Component", () => {
  beforeEach(() => {
    jest.clearAllMocks();

    // Mock GET calls
    api.get.mockImplementation((url) => {
      if (url === "/api/v1.0/moviebooking/all") {
        return Promise.resolve({
          data: [
            {
              movieName: "Avatar",
              theatreName: "PVR",
              availableTickets: 5,
              totalTickets: 5, // seat layout relies on this
            },
          ],
        });
      }

      if (url.includes("/booked-seats")) {
        return Promise.resolve({ data: ["A1"] }); // A1 already booked
      }

      return Promise.resolve({ data: [] });
    });
  });

  test("renders seat grid and disables booked seats", async () => {
    render(<BookTicket />);

    // Wait for seats to render
    await waitFor(() => expect(screen.getByText("A1")).toBeInTheDocument());

    // Booked seat should be disabled
    const bookedSeat = screen.getByText("A1");
    expect(bookedSeat).toBeDisabled();

    // Other seats should be enabled
    const seat = screen.getByText("A2");
    expect(seat).toBeEnabled();
  });

  test("selects and deselects seats correctly", async () => {
    render(<BookTicket />);

    await waitFor(() => screen.getByText("A2"));

    const seat = screen.getByText("A2");

    // Select seat
    fireEvent.click(seat);
    expect(seat.classList.contains("selected")).toBe(true);

    // Deselect seat
    fireEvent.click(seat);
    expect(seat.classList.contains("selected")).toBe(false);
  });

  test("confirm button is disabled when no seats are selected", async () => {
  render(<BookTicket />);

  await waitFor(() => screen.getByText("A2"));

  const confirmBtn = screen.getByText("Confirm Booking");
  expect(confirmBtn).toBeDisabled();
});

  test("submits booking successfully and navigates", async () => {
  api.post.mockResolvedValue({ data: "Booked!" });

  render(<BookTicket />);

  await waitFor(() => screen.getByText("A2"));

  fireEvent.click(screen.getByText("A2"));
  fireEvent.click(screen.getByText("Confirm Booking"));

  expect(await screen.findByText("Booked!")).toBeInTheDocument();

  // Advance time for navigation
  jest.advanceTimersByTime(1500);

  expect(mockNavigate).toHaveBeenCalledWith("/tickets");
});


  test("shows API error when booking fails", async () => {
    api.post.mockRejectedValue({ response: { data: "Booking failed" } });

    render(<BookTicket />);

    await waitFor(() => screen.getByText("A2"));

    fireEvent.click(screen.getByText("A2"));
    fireEvent.click(screen.getByText("Confirm Booking"));

    expect(await screen.findByText("Booking failed")).toBeInTheDocument();
  });

  test("prevents selecting more seats than ticket count", async () => {
  render(<BookTicket />);

  await waitFor(() => screen.getByText("A2"));

  const ticketInput = screen.getByRole("spinbutton");

  fireEvent.change(ticketInput, { target: { value: "2" } });

  fireEvent.click(screen.getByText("A2"));
  fireEvent.click(screen.getByText("A3"));
  fireEvent.click(screen.getByText("A4")); // should NOT be allowed

  expect(screen.getByText("A4").classList.contains("selected")).toBe(false);
});
});
