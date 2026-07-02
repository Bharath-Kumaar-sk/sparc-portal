import { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import cpclLogo from '../assets/cpcl-logo.webp'; //
import './AppStyles.css';

const DocumentUpload = () => {
    const location = useLocation();
    const navigate = useNavigate();

    // Get the ID
    const applicationId = location.state?.applicationId;

    const [ageProof, setAgeProof] = useState(null);
    const [perfCert, setPerfCert] = useState(null);
    const [photo, setPhoto] = useState(null);
    const [message, setMessage] = useState('');

    // Error state if user lands here without an application ID
    if (!applicationId) {
        return (
            <div className="form-container" style={{ textAlign: 'center', marginTop: '50px' }}>
                <h3 className="error-text" style={{ fontSize: '18px' }}>❌ Error: No Application ID found.</h3>
                <p style={{ color: '#6c757d', marginTop: '10px' }}>Please submit the application form first before uploading documents.</p>
            </div>
        );
    }

    // 2MB limit and strict format check for each file upload
    const handleFileChange = (e, setFileVariable) => {
        const file = e.target.files[0];
        if (file) {
            // --- STRICT FORMAT LIMIT ---
            const allowedTypes = ['application/pdf', 'image/jpeg', 'image/jpg', 'image/png'];
            if (!allowedTypes.includes(file.type)) {
                alert(`Error: Only .pdf, .jpg, and .png files are allowed!`);
                e.target.value = null; // Clears the file input visually
                setFileVariable(null); // Clears the React state
                return;
            }

            // --- 2MB SIZE LIMIT ---
            if (file.size > 2097152) {
                alert(`Error: ${file.name} is larger than the 2MB limit! Please choose a smaller file.`);
                e.target.value = null;
                setFileVariable(null);
                return;
            }
            setFileVariable(file);
        }
    };

    const uploadFile = async (file, docType) => {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('applicationId', applicationId);
        formData.append('documentType', docType);

        const token = localStorage.getItem('token');
        await axios.post('http://localhost:8080/api/documents/upload', formData, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'multipart/form-data' // Crucial for file uploads!
            }
        });
    };

    const handleUploadAll = async (e) => {
        e.preventDefault();
        if (!ageProof || !perfCert || !photo) {
            setMessage("Error: Please select all three documents before uploading.");
            return;
        }

        try {
            setMessage("Uploading documents... please wait.");

            // Upload all three files sequentially
            await uploadFile(ageProof, 'AGE_PROOF');
            await uploadFile(perfCert, 'PERFORMANCE_CERT');
            await uploadFile(photo, 'PHOTOGRAPH');

            // --- UPDATED SUCCESS MESSAGE ---
            setMessage("Success! All documents uploaded successfully. Your application is complete.");

            // Send them back to the login screen or dashboard
            setTimeout(() => navigate('/login'), 3000);

        } catch (error) {
            // --- UPDATED ERROR MESSAGE ---
            setMessage("Error: Failed to upload documents. " + (error.response?.data || error.message));
        }
    };

    // Custom inline style to make the file inputs look like upload boxes
    const fileInputStyle = {
        border: '1.5px dashed #dce1e6',
        padding: '12px',
        backgroundColor: '#f9fbfc',
        cursor: 'pointer',
        color: '#2c3e50'
    };

    return (
        /* Replaced inline styles with 'form-container' class */
        <div className="form-container" style={{ maxWidth: '600px' }}>

            {/* Added Header Section with Logo */}
            <div className="header-section">
                <img src={cpclLogo} alt="CPCL Logo" className="cpcl-logo" />
                <h2>Mandatory Documents</h2>
                <p>Application Reference ID: <strong>{applicationId}</strong></p>
            </div>

            {/* Instruction Banner */}
            <div style={{ backgroundColor: '#e8f0fe', padding: '15px', borderRadius: '8px', marginBottom: '25px', borderLeft: '4px solid #0056b3' }}>
                <p style={{ margin: 0, fontSize: '14.5px', color: '#003366', fontWeight: '500' }}>
                    <strong>Upload Rules:</strong> Accepted formats are .pdf, .jpg, and .png. Maximum file size is strictly 2MB per document.
                </p>
            </div>

            <form onSubmit={handleUploadAll}>
                {/* Replaced inline styles with 'form-group' class */}
                <div className="form-group">
                    <label>1. Proof of Age (Birth Certificate, Aadhaar, etc.)</label>
                    <input
                        type="file"
                        accept=".pdf,.jpg,.jpeg,.png"
                        onChange={(e) => handleFileChange(e, setAgeProof)}
                        required
                        style={fileInputStyle}
                    />
                </div>

                <div className="form-group">
                    <label>2. Highest Performance Certificate</label>
                    <input
                        type="file"
                        accept=".pdf,.jpg,.jpeg,.png"
                        onChange={(e) => handleFileChange(e, setPerfCert)}
                        required
                        style={fileInputStyle}
                    />
                </div>

                <div className="form-group" style={{ marginBottom: '30px' }}>
                    <label>3. Passport Size Photograph</label>
                    <input
                        type="file"
                        accept=".jpg,.jpeg,.png"
                        onChange={(e) => handleFileChange(e, setPhoto)}
                        required
                        style={fileInputStyle}
                    />
                </div>

                {/* Replaced inline button styles with 'submit-btn' class */}
                <button type="submit" className="submit-btn">
                    Upload Documents & Finish
                </button>
            </form>

            {/* Dynamically apply the new error or success text classes */}
            {message && (
                <div className={message.includes('Success') ? 'success-text' : 'error-text'} style={{ marginTop: '20px' }}>
                    {message}
                </div>
            )}
        </div>
    );
};

export default DocumentUpload;