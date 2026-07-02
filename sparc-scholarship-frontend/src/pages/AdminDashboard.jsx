import { useEffect, useState } from 'react';
import axios from 'axios';
import cpclLogo from '../assets/cpcl-logo.webp'; //
import './AppStyles.css';

const AdminDashboard = () => {
    const [applications, setApplications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [boysQuota, setBoysQuota] = useState(10);
    const [girlsQuota, setGirlsQuota] = useState(10);

    // Calculated fields based on application state
    const approvedBoys = applications.filter(app => app.status === 'APPROVED' && app.gender === 'MALE').length;
    const approvedGirls = applications.filter(app => app.status === 'APPROVED' && app.gender === 'FEMALE').length;
    const totalApproved = approvedBoys + approvedGirls;

    useEffect(() => {
        const fetchApplications = async () => {
            try {
                const token = localStorage.getItem('token');

                // Fetch all submitted applications from the secure Admin API
                const response = await axios.get('http://localhost:8080/api/admin/applications', {
                    headers: { 'Authorization': `Bearer ${token}` }
                });

                // Fetch current quotas from backend configuration
                const quotaResponse = await axios.get('http://localhost:8080/api/admin/quotas', {
                    headers: { 'Authorization': `Bearer ${token}` }
                });

                setBoysQuota(quotaResponse.data.boys);
                setGirlsQuota(quotaResponse.data.girls);
                setApplications(response.data);
                setLoading(false);
            } catch (err) {
                const errorMessage = typeof err.response?.data === 'string'
                    ? err.response.data
                    : "Error 403: Unauthorized access or invalid token.";
                setError(errorMessage);
                setLoading(false);
            }
        };

        fetchApplications();
    }, []);

    const handleUpdateQuotas = async () => {
        try {
            const token = localStorage.getItem('token');
            await axios.put(`http://localhost:8080/api/admin/quotas?boys=${boysQuota}&girls=${girlsQuota}`, {}, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            alert("Quotas successfully updated in the backend!");
        } catch (err) {
            alert("Failed to update quotas: " + (err.response?.data || err.message));
        }
    };

    const handleStatusUpdate = async (appId, newStatus) => {
        try {
            const token = localStorage.getItem('token');
            await axios.put(`http://localhost:8080/api/admin/applications/${appId}/status?newStatus=${newStatus}`, {}, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            // Refresh the grid state locally to update the UI instantly
            setApplications(applications.map(app =>
                app.id === appId ? { ...app, status: newStatus } : app
            ));
            alert(`Application #${appId} successfully marked as ${newStatus}`);
        } catch (err) {
            alert("Failed to update status: " + (err.response?.data || err.message));
        }
    };

    const handleViewDocument = async (appId, docType) => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(`http://localhost:8080/api/admin/applications/${appId}/documents/${docType}`, {
                headers: { 'Authorization': `Bearer ${token}` },
                responseType: 'blob' // Essential for downloading raw file binaries
            });

            const fileURL = URL.createObjectURL(new Blob([response.data], { type: response.headers['content-type'] }));
            window.open(fileURL, '_blank');
        } catch (err) {
            alert("Could not retrieve document. The applicant may not have uploaded it yet.");
        }
    };

    if (loading) {
        return (
            <div className="form-container" style={{ textAlign: 'center', marginTop: '80px' }}>
                <h3 style={{ color: '#002147' }}>Loading Admin Dashboard...</h3>
                <p style={{ color: '#6c757d' }}>Fetching real-time applications and system metrics</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="form-container" style={{ textAlign: 'center', marginTop: '80px' }}>
                <h3 className="error-text" style={{ fontSize: '18px' }}>❌ {error}</h3>
                <p style={{ color: '#6c757d', marginTop: '10px' }}>Please log out and log back in with administrative credentials.</p>
            </div>
        );
    }

    return (
        <div style={{ padding: '30px 20px', maxWidth: '1350px', margin: '0 auto' }}>

            {/* --- HEADER SECTION --- */}
            <div className="header-section" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '25px', textAlign: 'left' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
                    <img src={cpclLogo} alt="CPCL Logo" className="cpcl-logo" style={{ height: '55px' }} />
                    <div>
                        <h2 style={{ margin: 0, color: '#002147', fontSize: '24px' }}>Internal Administrator Review Panel</h2>
                        <p style={{ margin: '4px 0 0 0', color: '#555', fontSize: '14px' }}>Total Applications Submitted: <strong>{applications.length}</strong></p>
                    </div>
                </div>
                <span style={{ backgroundColor: '#dc3545', color: 'white', padding: '6px 14px', borderRadius: '20px', fontWeight: 'bold', fontSize: '13px', trackingLetter: '0.5px' }}>
                    🔒 SECURE ADMIN MODE
                </span>
            </div>

            {/* --- INTERACTIVE QUOTA TRACKER PANEL --- */}
            <div style={{ display: 'flex', gap: '20px', marginBottom: '30px', padding: '20px', backgroundColor: '#fcfdfe', borderRadius: '8px', border: '1px solid #dcdcdc', boxShadow: '0 2px 4px rgba(0,0,0,0.02)', alignItems: 'center' }}>
                <div style={{ flex: 1, textAlign: 'center' }}>
                    <h4 style={{ margin: '0 0 6px 0', fontSize: '13px', textTransform: 'uppercase', color: '#6c757d' }}>Total Slots Awarded</h4>
                    <div style={{ fontSize: '26px', fontWeight: '800', color: totalApproved > 20 ? '#dc3545' : '#28a745' }}>
                        {totalApproved} <span style={{ fontSize: '16px', fontWeight: 'normal', color: '#888' }}>/ 20 Limit</span>
                    </div>
                </div>
                <div style={{ flex: 1, textAlign: 'center', borderLeft: '1px solid #e9ecef' }}>
                    <h4 style={{ margin: '0 0 6px 0', fontSize: '13px', textTransform: 'uppercase', color: '#6c757d' }}>Boys Quota Allocation</h4>
                    <div style={{ fontSize: '20px', fontWeight: 'bold', color: '#0056b3', display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '8px' }}>
                        <span>{approvedBoys} /</span>
                        <input
                            type="number"
                            className="form-control"
                            value={boysQuota}
                            onChange={(e) => setBoysQuota(Number(e.target.value))}
                            style={{ width: '65px', padding: '4px', textAlign: 'center', margin: 0, height: 'auto' }}
                        />
                    </div>
                </div>
                <div style={{ flex: 1, textAlign: 'center', borderLeft: '1px solid #e9ecef' }}>
                    <h4 style={{ margin: '0 0 6px 0', fontSize: '13px', textTransform: 'uppercase', color: '#6c757d' }}>Girls Quota Allocation</h4>
                    <div style={{ fontSize: '20px', fontWeight: 'bold', color: '#d63384', display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '8px' }}>
                        <span>{approvedGirls} /</span>
                        <input
                            type="number"
                            className="form-control"
                            value={girlsQuota}
                            onChange={(e) => setGirlsQuota(Number(e.target.value))}
                            style={{ width: '65px', padding: '4px', textAlign: 'center', margin: 0, height: 'auto' }}
                        />
                    </div>
                </div>
                <div style={{ flex: 0.6, textAlign: 'center', borderLeft: '1px solid #e9ecef', paddingLeft: '20px' }}>
                    <button onClick={handleUpdateQuotas} className="submit-btn" style={{ padding: '10px 15px', backgroundColor: '#ffc107', color: '#000', border: 'none', fontWeight: 'bold', marginTop: 0 }}>
                        💾 Save System Quotas
                    </button>
                </div>
            </div>

            {/* --- APPLICATIONS MATRIX GRID --- */}
            {applications.length === 0 ? (
                <div className="form-container" style={{ textAlign: 'center', padding: '40px' }}>
                    <p style={{ color: '#6c757d', margin: 0, fontSize: '16px' }}>No student applications found in the system database matching criteria.</p>
                </div>
            ) : (
                <div style={{ overflowX: 'auto', boxShadow: '0 4px 12px rgba(0,0,0,0.05)', borderRadius: '8px', border: '1px solid #e0e0e0' }}>
                    <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '13.5px', backgroundColor: '#fff' }}>
                        <thead>
                            <tr style={{ backgroundColor: '#002147', color: 'white' }}>
                                <th style={{ padding: '14px 10px', fontWeight: '600' }}>App ID</th>
                                <th style={{ padding: '14px 10px', fontWeight: '600' }}>Applicant Name</th>
                                <th style={{ padding: '14px 10px', fontWeight: '600' }}>Demographics</th>
                                <th style={{ padding: '14px 10px', fontWeight: '600' }}>Sport & Tier</th>
                                <th style={{ padding: '14px 10px', fontWeight: '600' }}>Achievement Details</th>
                                <th style={{ padding: '14px 10px', fontWeight: '600' }}>Submission Time</th>
                                <th style={{ padding: '14px 10px', fontWeight: '600' }}>Status</th>
                                <th style={{ padding: '14px 15px', fontWeight: '600', textAlignment: 'center' }}>Review Actions</th>
                                <th style={{ padding: '14px 10px', fontWeight: '600' }}>Verification Files</th>
                            </tr>
                        </thead>
                        <tbody>
                            {applications.map((app) => (
                                <tr
                                    key={app.id}
                                    style={{
                                        borderBottom: '1px solid #eceff1',
                                        backgroundColor: app.status === 'APPROVED' ? '#f4fbf7' : app.status === 'REJECTED' ? '#fff5f5' : 'white',
                                        transition: 'background-color 0.2s ease'
                                    }}
                                >
                                    {/* 1. App ID */}
                                    <td style={{ padding: '14px 10px', fontWeight: 'bold', color: '#333' }}>#{app.id}</td>

                                    {/* 2. Name */}
                                    <td style={{ padding: '14px 10px', fontWeight: '500', color: '#1a252f' }}>{app.firstName} {app.lastName}</td>

                                    {/* 3. Demographics */}
                                    <td style={{ padding: '14px 10px', lineHeight: '1.4' }}>
                                        <span style={{ fontWeight: '500' }}>{app.gender}</span><br/>
                                        <span style={{ color: '#7f8c8d', fontSize: '11.5px' }}>DOB: {app.dateOfBirth}</span>
                                    </td>

                                    {/* 4. Sport & Tier */}
                                    <td style={{ padding: '14px 10px', lineHeight: '1.5' }}>
                                        <strong style={{ color: '#2c3e50' }}>{app.sportName}</strong><br/>
                                        <span style={{ display: 'inline-block', marginTop: '3px', padding: '2px 6px', borderRadius: '4px', fontSize: '10.5px', fontWeight: 'bold', backgroundColor: app.scholarshipLevel === 'ELITE_SCHOLAR' ? '#fff3cd' : '#e2e3e5', color: app.scholarshipLevel === 'ELITE_SCHOLAR' ? '#856404' : '#383d41' }}>
                                            {app.scholarshipLevel}
                                        </span>
                                    </td>

                                    {/* 5. Achievement Details */}
                                    <td style={{ padding: '14px 10px', fontSize: '12.5px', lineHeight: '1.4', color: '#4d5656' }}>
                                        <strong>Level:</strong> {app.tournamentLevel}<br/>
                                        <strong>Rank:</strong> {app.achievement}<br/>
                                        <strong>Fed:</strong> {app.federationName}
                                    </td>

                                    {/* 6. Submitted Time */}
                                    <td style={{ padding: '14px 10px', color: '#7f8c8d', fontSize: '12.5px' }}>
                                        {app.submittedAt ? new Date(app.submittedAt).toLocaleString('en-IN', { dateStyle: 'short', timeStyle: 'short' }) : 'N/A'}
                                    </td>

                                    {/* 7. Status Badge */}
                                    <td style={{ padding: '14px 10px' }}>
                                        <span style={{
                                            fontWeight: 'bold',
                                            fontSize: '12px',
                                            color: app.status === 'APPROVED' ? '#2e7d32' : app.status === 'REJECTED' ? '#c62828' : '#1565c0'
                                        }}>
                                            ● {app.status}
                                        </span>
                                    </td>

                                    {/* 8. Actions (Accept/Reject Options) */}
                                    <td style={{ padding: '14px 15px' }}>
                                        <div style={{ display: 'flex', gap: '6px' }}>
                                            <button onClick={() => handleStatusUpdate(app.id, 'APPROVED')} style={{ padding: '6px 10px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '11.5px', fontWeight: '500' }}>Approve</button>
                                            <button onClick={() => handleStatusUpdate(app.id, 'REJECTED')} style={{ padding: '6px 10px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '11.5px', fontWeight: '500' }}>Reject</button>
                                        </div>
                                    </td>

                                    {/* 9. Verification Files */}
                                    <td style={{ padding: '14px 10px' }}>
                                        <div style={{ display: 'flex', gap: '4px', flexWrap: 'wrap', maxWidth: '180px' }}>
                                            <button onClick={() => handleViewDocument(app.id, 'AGE_PROOF')} style={{ padding: '4px 6px', backgroundColor: '#eef2f7', color: '#333', border: '1px solid #cbd5e1', borderRadius: '3px', cursor: 'pointer', fontSize: '11px' }}>Age 📄</button>
                                            <button onClick={() => handleViewDocument(app.id, 'PERFORMANCE_CERT')} style={{ padding: '4px 6px', backgroundColor: '#eef2f7', color: '#333', border: '1px solid #cbd5e1', borderRadius: '3px', cursor: 'pointer', fontSize: '11px' }}>Cert 🏆</button>
                                            <button onClick={() => handleViewDocument(app.id, 'PHOTOGRAPH')} style={{ padding: '4px 6px', backgroundColor: '#eef2f7', color: '#333', border: '1px solid #cbd5e1', borderRadius: '3px', cursor: 'pointer', fontSize: '11px' }}>Photo 🖼️</button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

export default AdminDashboard;