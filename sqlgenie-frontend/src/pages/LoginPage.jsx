import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from '../api/axios';

const LoginPage = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('/api/auth/login', { username, password });
      // Depending on the exact structure, it could be response.data or response.data.token
      let token = response.data;
      if (typeof token === 'object' && token.token) {
        token = token.token;
      } else if (typeof token === 'object' && token.jwt) {
        token = token.jwt;
      }
      
      if (token) {
        localStorage.setItem('token', token);
        navigate('/query');
      }
    } catch (error) {
      console.error('Login failed', error);
      alert('Login failed');
    }
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', backgroundColor: '#f0f2f5' }}>
      <div style={{ padding: '2rem', backgroundColor: 'white', borderRadius: '8px', boxShadow: '0 4px 6px rgba(0,0,0,0.1)', width: '300px' }}>
        <h2 style={{ textAlign: 'center', marginBottom: '1.5rem', color: '#333' }}>Login to SqlGenie</h2>
        <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            style={{ padding: '0.75rem', borderRadius: '4px', border: '1px solid #ccc', fontSize: '1rem' }}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            style={{ padding: '0.75rem', borderRadius: '4px', border: '1px solid #ccc', fontSize: '1rem' }}
            required
          />
          <button type="submit" style={{ padding: '0.75rem', borderRadius: '4px', border: 'none', backgroundColor: '#007bff', color: 'white', fontSize: '1rem', cursor: 'pointer', marginTop: '0.5rem', fontWeight: 'bold' }}>
            Login
          </button>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;
