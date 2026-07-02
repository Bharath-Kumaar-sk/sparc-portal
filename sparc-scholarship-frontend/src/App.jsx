import { BrowserRouter as Router, Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import './App.css';
import ApplicationForm from './pages/ApplicationForm';
import DocumentUpload from './pages/DocumentUpload';
import AdminDashboard from './pages/AdminDashboard';

const NavigationBar = () => {
    const navigate = useNavigate();
    const location = useLocation();

    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    const hideLogout = location.pathname === '/login' || location.pathname === '/register';

    return (
        <nav style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '15px', backgroundColor: '#002147', color: 'white', marginBottom: '20px' }}>
            <h2 style={{ margin: 0 }}>CPCL SPARC Sports Scholarship Scheme 2026</h2>

            {!hideLogout && (
                <button
                    onClick={handleLogout}
                    style={{ padding: '8px 15px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}
                >
                    Logout
                </button>
            )}
        </nav>
    );
};

function App() {
  return (
    <Router>
      <div className="app-container">

        <NavigationBar />

        <Routes>
          <Route path="/" element={<Navigate to="/login" />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/apply" element={<ApplicationForm />} />
          <Route path="/admin" element={<AdminDashboard />} />
          <Route path="/upload" element={<DocumentUpload />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;