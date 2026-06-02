import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import AdminDashboard from "../../admin/AdminDashboard";
import api from "../../../api/api";

// Mock API module
jest.mock("../../../api/api", () => ({
  get: jest.fn(),
  post: jest.fn(),
  put: jest.fn(),
  delete: jest.fn(),
}));

jest.useFakeTimers();

// Mock window.confirm
window.confirm = jest.fn(() => true);

describe("AdminDashboard", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("loads movies on mount and displays them", async () => {
    // Mock /all movies API
    api.get.mockResolvedValueOnce({
      data: [
        {
          id: "m1",
          movieName: "Avatar",
          theatreName: "PVR",
          availableTickets: 25,
          status: "Available",
        },
      ],
    });

    // 2️⃣ Mock booked count API
    api.get.mockResolvedValueOnce({ data: 5 });

    render(<AdminDashboard />);

    // Wait for movie to appear
    expect(await screen.findByText("Avatar")).toBeInTheDocument();
    expect(screen.getByText("Booked Tickets: 5")).toBeInTheDocument();
  });

  test("adds a movie when Add Movie form is submitted", async () => {
    api.get.mockResolvedValueOnce({ data: [] });     // initial load
    api.get.mockResolvedValueOnce({ data: [] });     // reload after add
    api.post.mockResolvedValueOnce({ data: "Success" });

    render(<AdminDashboard />);

    fireEvent.change(screen.getByPlaceholderText("Movie Name"), {
      target: { value: "TestMovie" },
    });
    fireEvent.change(screen.getByPlaceholderText("Theatre Name"), {
      target: { value: "INOX" },
    });
    fireEvent.change(screen.getByPlaceholderText("Total Tickets"), {
      target: { value: "50" },
    });

    fireEvent.click(screen.getByText("Add Movie"));

    await waitFor(() =>
      expect(api.post).toHaveBeenCalledWith(
        "/api/v1.0/moviebooking/movies/add",
        {
          movieName: "TestMovie",
          theatreName: "INOX",
          totalTickets: 50,
          availableTickets: 50,
          status: "Available",
        }
      )
    );
  });

  test("updates movie status when clicking Book ASAP", async () => {
    // Mock initial load
    api.get.mockResolvedValueOnce({
      data: [
        {
          id: "m1",
          movieName: "Avatar",
          theatreName: "PVR",
          availableTickets: 25,
          status: "Available",
        },
      ],
    });

    api.get.mockResolvedValueOnce({ data: 4 }); // booked count
    api.put.mockResolvedValueOnce({ data: "Updated" });

    render(<AdminDashboard />);

    const btn = await screen.findByText("Mark Book ASAP");
    fireEvent.click(btn);

    expect(api.put).toHaveBeenCalledWith(
      "/api/v1.0/moviebooking/Avatar/update/status",
      { status: "BOOK ASAP" }
    );
  });

  test("deletes a movie when delete button is clicked", async () => {
    api.get.mockResolvedValueOnce({
      data: [
        {
          id: "m1",
          movieName: "Avatar",
          theatreName: "PVR",
          availableTickets: 25,
          status: "Available",
        },
      ],
    });
    api.get.mockResolvedValueOnce({ data: 5 });
    api.delete.mockResolvedValueOnce({ data: "Deleted" });

    window.confirm = jest.fn(() => true);

    render(<AdminDashboard />);

    const deleteBtn = await screen.findByText("Delete");
    fireEvent.click(deleteBtn);

    expect(api.delete).toHaveBeenCalledWith(
      "/api/v1.0/moviebooking/Avatar/delete/m1"
    );
  });
});
