import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import MyTickets from "../MyTickets";
import api from "../../../api/api";

// Mock the API module
jest.mock("../../../api/api");

describe("MyTickets Component", () => {

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("renders empty state when no tickets", async () => {
    api.get.mockResolvedValueOnce({ data: [] });

    render(<MyTickets />);

    expect(api.get).toHaveBeenCalledWith("/api/v1.0/moviebooking/tickets/user");

    const message = await screen.findByText("No tickets booked yet 🙂");
    expect(message).toBeInTheDocument();
  });

  test("renders list of tickets when data exists", async () => {
    api.get.mockResolvedValueOnce({
      data: [
        {
          id: "1",
          movieName: "Avatar",
          theatreName: "PVR",
          seatNumber: "A1,A2",
          numberOfTickets: 2
        }
      ]
    });

    render(<MyTickets />);

    // Wait for ticket to appear
    expect(await screen.findByText("Avatar")).toBeInTheDocument();
    expect(screen.getByText("PVR")).toBeInTheDocument();
    expect(screen.getByText("Seats:")).toBeInTheDocument();
    expect(screen.getByText("A1,A2")).toBeInTheDocument();
  });

  test("calls API exactly once", async () => {
    api.get.mockResolvedValueOnce({ data: [] });

    render(<MyTickets />);

    await waitFor(() => {
      expect(api.get).toHaveBeenCalledTimes(1);
    });
  });

  test("snapshot test", async () => {
    api.get.mockResolvedValueOnce({ data: [] });

    const { container } = render(<MyTickets />);

    // Wait for state update
    await screen.findByText("No tickets booked yet 🙂");

    expect(container).toMatchSnapshot();
  });
});
