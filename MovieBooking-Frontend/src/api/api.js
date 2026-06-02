import axios from "axios";

const baseURL = process.env.REACT_APP_API_BASE || "http://moviebooking-backend-env.eba-qhh59uje.us-east-1.elasticbeanstalk.com";

// const api = axios.create({
//   baseURL,
//   withCredentials: true, // <--- important: send cookies (HttpSession)
//   headers: {
//     "Content-Type": "application/json",
//   },
// });

// // optional: interceptors for centralized error handling / logging
// api.interceptors.response.use(
//   res => res,
//   err => {
//     // handle 401 globally
//     if (err.response && err.response.status === 401) {
//       // optionally redirect to login
//     }
//     return Promise.reject(err);
//   }
// );

// export default api;

// get JWT token from sessionStorage
const getToken = () => sessionStorage.getItem("token");

const api = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor: attach JWT token
api.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor: centralized error handling
api.interceptors.response.use(
  (res) => res,
  (err) => {
    const originalUrl = err.config?.url;

    // Do NOT redirect if the 401 came from the login API
    if (
      err.response &&
      err.response.status === 401 &&
      originalUrl !== "/api/v1.0/moviebooking/login"
    ) {
      sessionStorage.removeItem("token");
      window.location.href = "/login";
    }

    return Promise.reject(err);
  }
);


export default api;
