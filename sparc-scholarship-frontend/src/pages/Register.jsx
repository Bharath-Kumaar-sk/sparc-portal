import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import cpclLogo from '../assets/cpcl-logo.webp'; //
import './AppStyles.css';

const Register = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    const emailRegex = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i;
    // Password rules: Min 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 special char
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

    const handleRegister = async (e) => {
        e.preventDefault();

        if (!emailRegex.test(email)) {
            setMessage("Error: Please enter a valid email address.");
            return;
        }

        if (!passwordRegex.test(password)) {
            setMessage("Error: Password must be at least 8 characters long, contain an uppercase letter, a lowercase letter, a number, and a special character.");
            return;
        }

        try {
            const response = await axios.post('http://localhost:8080/api/auth/register', {
                email: email,
                password: password
            });
            setMessage("Registration successful! Redirecting to login...");
            setTimeout(() => navigate('/login'), 2000);
        } catch (error) {
            setMessage(error.response?.data || "An error occurred during registration.");
        }
    };

    return (
        /* Replaced inline styles with 'form-container' class */
        <div className="form-container" style={{ maxWidth: '500px' }}>

            {/* Added Header Section with Logo */}
            <div className="header-section">
                <img src={cpclLogo} alt="CPCL Logo" className="cpcl-logo" />
                <h2>Register for SPARC</h2>
                <p>Create your applicant account</p>
            </div>

            <form onSubmit={handleRegister}>
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
                        placeholder="Create a strong password"
                    />
                </div>

                {/* Replaced inline button styles with 'submit-btn' class */}
                <button type="submit" className="submit-btn">
                    Create Account
                </button>
            </form>

            {/* Dynamically apply the new error or success text classes we created */}
            {message && (
                <span className={message.includes('successful') ? 'success-text' : 'error-text'}>
                    {message}
                </span>
            )}

            <p style={{ textAlign: 'center', marginTop: '25px', fontSize: '14.5px', color: '#6c757d' }}>
                Already have an account? <Link to="/login" style={{ color: '#0056b3', textDecoration: 'none', fontWeight: '600' }}>Login here</Link>
            </p>
        </div>
    );
};

export default Register;