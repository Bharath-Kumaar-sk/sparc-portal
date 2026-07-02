import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import cpclLogo from '../assets/cpcl-logo.webp'; //
import './AppStyles.css';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/auth/login', {
                email: email,
                password: password
            });

            // Save jwt token in storage
            localStorage.setItem('token', response.data.token);
            localStorage.setItem('role', response.data.role);

            // Redirect based on role
            if (response.data.role.toUpperCase() === 'ADMIN') {
                navigate('/admin');
            } else {
                navigate('/apply');
            }
        } catch (error) {
            setError("Invalid email or password.");
        }
    };

    return (
        /* Replaced inline styles with 'form-container' class */
        <div className="form-container" style={{ maxWidth: '500px' }}>

            {/* Added Header Section with Logo */}
            <div className="header-section">
                <img src={cpclLogo} alt="CPCL Logo" className="cpcl-logo" />
                <h2>Login to SPARC</h2>
                <p>Access your portal account</p>
            </div>

            <form onSubmit={handleLogin}>
                {/* Replaced inline styles with 'form-group' class */}
                <div className="form-group">
                    <label>Email Address</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                        placeholder="Enter your email"
                    />
                </div>

                <div className="form-group">
                    <label>Password</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        placeholder="Enter your password"
                    />
                </div>

                {/* Replaced inline button styles with 'submit-btn' class */}
                <button type="submit" className="submit-btn">
                    Login
                </button>
            </form>

            {/* Applied the new error text class for better visibility */}
            {error && <span className="error-text">{error}</span>}

            {/* Styled the registration link to match the clean UI */}
            <p style={{ textAlign: 'center', marginTop: '25px', fontSize: '14.5px', color: '#6c757d' }}>
                Don't have an account? <Link to="/register" style={{ color: '#0056b3', textDecoration: 'none', fontWeight: '600' }}>Register here</Link>
            </p>
        </div>
    );
};

export default Login;