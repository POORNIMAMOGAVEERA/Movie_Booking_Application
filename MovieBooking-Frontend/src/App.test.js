import { render, screen } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import App from "./App";


// mock API so axios.create never runs
jest.mock("./api/api", () => ({
  get: jest.fn(),
  post: jest.fn(),
  put: jest.fn(),
}));

// mock all routed components so Jest does NOT import real ones
jest.mock("./components/auth/Register", () => () => <div>Mock Register</div>);
jest.mock("./components/auth/Login", () => () => <div>Mock Login</div>);
jest.mock("./components/auth/Logout", () => () => <div>Mock Logout</div>);
jest.mock("./components/auth/ForgotPassword", () => () => <div>Mock Forgot</div>);
jest.mock("./components/auth/ResetPassword", () => () => <div>Mock Reset</div>);
jest.mock("./components/movies/MovieList", () => () => <div>Mock MovieList</div>);
jest.mock("./components/movies/BookTicket", () => () => <div>Mock BookTicket</div>);
jest.mock("./components/tickets/MyTickets", () => () => <div>Mock MyTickets</div>);
jest.mock("./components/admin/AdminDashboard", () => () => <div>Mock Admin</div>);
jest.mock("./routes/ProtectedRoute", () => ({ children }) => <>{children}</>);
jest.mock("./components/common/NavBar", () => () => <div>Mock Navbar</div>);

describe("App component", () => {
  test("renders without crashing and shows Mock Navbar", () => {
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );

    expect(screen.getByText("Mock Navbar")).toBeInTheDocument();
  });
});
