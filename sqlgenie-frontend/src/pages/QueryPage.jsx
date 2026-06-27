import React, { useState } from 'react';
import axiosInstance from '../api/axios';
import { useNavigate } from 'react-router-dom';
import { ResponsiveContainer, BarChart, Bar, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';

const QueryPage = () => {
  const [inputValue, setInputValue] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [generatedSql, setGeneratedSql] = useState('');
  const [editedSql, setEditedSql] = useState('');
  const [isEditingSql, setIsEditingSql] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const [queryResults, setQueryResults] = useState(null);
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  const handleRunQuery = async () => {
    if (!inputValue.trim()) return;
    setIsLoading(true);
    setErrorMsg('');
    setGeneratedSql('');
    setIsEditingSql(false);
    setQueryResults(null);
    
    try {
      const response = await axiosInstance.post('/api/query', {
        naturalLanguage: inputValue
      });
      const sql = response.data.sql || response.data.executedSql || response.data;
      setGeneratedSql(typeof sql === 'string' ? sql : JSON.stringify(sql, null, 2));
      setEditedSql(typeof sql === 'string' ? sql : JSON.stringify(sql, null, 2));
      setQueryResults(response.data);
    } catch (err) {
      setErrorMsg(err.response?.data?.message || err.message || 'An error occurred while running the query');
    } finally {
      setIsLoading(false);
    }
  };

  const handleReRunQuery = async () => {
    setIsLoading(true);
    setErrorMsg('');
    setQueryResults(null);
    
    try {
      const response = await axiosInstance.post('/api/query', {
        naturalLanguage: inputValue,
        sqlOverride: editedSql
      });
      setQueryResults(response.data);
    } catch (err) {
      setErrorMsg(err.response?.data?.message || err.message || 'An error occurred while re-running the query');
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      handleRunQuery();
    }
  };

  const renderResults = () => {
    if (!queryResults || !queryResults.columns || !queryResults.rows) return null;
    const { columns, rows } = queryResults;
    if (rows.length === 0) {
      return <div style={{ marginTop: '2rem', padding: '1.5rem', backgroundColor: '#fff', borderRadius: '0.5rem', boxShadow: '0 1px 3px 0 rgba(0,0,0,0.1)' }}>No results found.</div>;
    }

    const numericKeywords = ['count', 'total', 'amount', 'revenue', 'quantity'];
    let numericColName = null;
    let chartType = null;
    let dateColName = null;

    for (let col of columns) {
      const lowerCol = col.toLowerCase();
      const isNumericKeyword = numericKeywords.some(kw => lowerCol.includes(kw));
      const isAllNumeric = rows.every(row => !isNaN(Number(row[col])) && row[col] !== null && row[col] !== '');
      if (isNumericKeyword || isAllNumeric) {
        numericColName = col;
        break;
      }
    }

    for (let col of columns) {
      const lowerCol = col.toLowerCase();
      if (lowerCol.includes('date') || lowerCol.includes('month') || lowerCol.includes('day') || lowerCol.includes('year')) {
        dateColName = col;
      }
    }

    if (numericColName) {
      chartType = dateColName ? 'line' : 'bar';
    }

    const xAxisCol = (dateColName && dateColName !== numericColName) ? dateColName : (columns[0] !== numericColName ? columns[0] : columns[1] || columns[0]);

    const chartData = rows.map(row => ({
      name: row[xAxisCol],
      value: Number(row[numericColName])
    }));

    return (
      <div style={{ marginTop: '2rem' }}>
        {chartType && (
          <div style={{ backgroundColor: '#ffffff', padding: '1.5rem', borderRadius: '0.5rem', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)', marginBottom: '2rem' }}>
            <h3 style={{ marginTop: 0, marginBottom: '1rem', color: '#374151' }}>Visualization</h3>
            <ResponsiveContainer width="100%" height={300}>
              {chartType === 'line' ? (
                <LineChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Line type="monotone" dataKey="value" name={numericColName} stroke="#3b82f6" activeDot={{ r: 8 }} />
                </LineChart>
              ) : (
                <BarChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="value" name={numericColName} fill="#3b82f6" />
                </BarChart>
              )}
            </ResponsiveContainer>
          </div>
        )}

        <div style={{ backgroundColor: '#ffffff', padding: '1.5rem', borderRadius: '0.5rem', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
            <h3 style={{ margin: 0, color: '#374151' }}>Query Results</h3>
            <span style={{ fontSize: '0.875rem', color: '#6b7280' }}>Showing {rows.length} rows</span>
          </div>
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.875rem' }}>
              <thead>
                <tr style={{ backgroundColor: '#f3f4f6', borderBottom: '2px solid #e5e7eb' }}>
                  {columns.map(col => (
                    <th key={col} style={{ padding: '0.75rem 1rem', color: '#374151', fontWeight: '600' }}>{col}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {rows.map((row, idx) => (
                  <tr key={idx} style={{ borderBottom: '1px solid #e5e7eb', backgroundColor: idx % 2 === 0 ? '#ffffff' : '#f9fafb' }}>
                    {columns.map(col => (
                      <td key={col} style={{ padding: '0.75rem 1rem', color: '#4b5563' }}>{row[col]}</td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    );
  };

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f9fafb', fontFamily: 'Inter, system-ui, sans-serif' }}>
      <nav style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1rem 2rem', backgroundColor: '#ffffff', boxShadow: '0 1px 3px 0 rgba(0, 0, 0, 0.1)' }}>
        <h1 style={{ margin: 0, color: '#111827', fontSize: '1.5rem', fontWeight: 'bold' }}>SqlGenie</h1>
        <button 
          onClick={handleLogout}
          style={{ padding: '0.5rem 1rem', backgroundColor: '#ef4444', color: 'white', border: 'none', borderRadius: '0.375rem', cursor: 'pointer', fontWeight: '500' }}
        >
          Logout
        </button>
      </nav>

      <main style={{ maxWidth: '800px', margin: '3rem auto', padding: '0 1rem' }}>
        {errorMsg && (
          <div style={{ backgroundColor: '#fee2e2', borderLeft: '4px solid #ef4444', color: '#b91c1c', padding: '1rem', marginBottom: '1.5rem', borderRadius: '0.375rem' }}>
            {errorMsg}
          </div>
        )}

        <div style={{ display: 'flex', gap: '1rem', marginBottom: '2rem' }}>
          <input 
            type="text" 
            placeholder="Ask your database anything... e.g. show me top 5 customers by revenue" 
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyDown={handleKeyDown}
            style={{ flex: 1, padding: '0.75rem 1rem', borderRadius: '0.5rem', border: '1px solid #d1d5db', fontSize: '1rem', outline: 'none', boxShadow: '0 1px 2px 0 rgba(0, 0, 0, 0.05)' }}
          />
          <button 
            onClick={handleRunQuery}
            disabled={isLoading}
            style={{ padding: '0.75rem 1.5rem', backgroundColor: '#3b82f6', color: 'white', border: 'none', borderRadius: '0.5rem', cursor: isLoading ? 'not-allowed' : 'pointer', fontWeight: '500', fontSize: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}
          >
            {isLoading ? 'Running...' : 'Run Query'}
          </button>
        </div>

        {generatedSql && (
          <div style={{ backgroundColor: '#ffffff', padding: '1.5rem', borderRadius: '0.5rem', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)' }}>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600', color: '#374151' }}>Generated SQL</label>
            <textarea 
              readOnly={!isEditingSql}
              value={isEditingSql ? editedSql : generatedSql}
              onChange={(e) => setEditedSql(e.target.value)}
              style={{ width: '100%', minHeight: '150px', padding: '1rem', borderRadius: '0.375rem', border: '1px solid #e5e7eb', backgroundColor: isEditingSql ? '#ffffff' : '#f3f4f6', fontFamily: 'monospace', fontSize: '0.875rem', color: '#1f2937', marginBottom: '1rem', resize: 'vertical', boxSizing: 'border-box' }}
            />
            <div style={{ display: 'flex', gap: '1rem' }}>
              <button 
                onClick={() => setIsEditingSql(!isEditingSql)}
                style={{ padding: '0.5rem 1rem', backgroundColor: isEditingSql ? '#9ca3af' : '#10b981', color: 'white', border: 'none', borderRadius: '0.375rem', cursor: 'pointer', fontWeight: '500' }}
              >
                {isEditingSql ? 'Cancel Edit' : 'Edit SQL'}
              </button>
              <button 
                onClick={handleReRunQuery}
                disabled={isLoading}
                style={{ padding: '0.5rem 1rem', backgroundColor: '#6366f1', color: 'white', border: 'none', borderRadius: '0.375rem', cursor: isLoading ? 'not-allowed' : 'pointer', fontWeight: '500' }}
              >
                {isLoading ? 'Running...' : 'Re-run'}
              </button>
            </div>
          </div>
        )}

        {renderResults()}
      </main>
    </div>
  );
};

export default QueryPage;

