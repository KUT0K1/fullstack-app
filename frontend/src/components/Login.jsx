import { useState } from 'react';
import { authApi } from '../api/auth';
import './Login.css';

function Login({ onLogin }) {
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (isLogin) {
        const response = await authApi.login(formData.username, formData.password);
        localStorage.setItem('token', response.token);
        localStorage.setItem('user', JSON.stringify(response.user));
        onLogin(response.user);
      } else {
        await authApi.register(formData.username, formData.email, formData.password);
        // Nach Registrierung automatisch einloggen
        const loginResponse = await authApi.login(formData.username, formData.password);
        localStorage.setItem('token', loginResponse.token);
        localStorage.setItem('user', JSON.stringify(loginResponse.user));
        onLogin(loginResponse.user);
      }
    } catch (err) {
      setError(
        err.response?.data?.message ||
          (isLogin ? 'Anmeldung fehlgeschlagen' : 'Registrierung fehlgeschlagen')
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h2>{isLogin ? 'Anmelden' : 'Registrieren'}</h2>
        {error && <div className="error-message">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="username">Benutzername</label>
            <input
              type="text"
              id="username"
              value={formData.username}
              onChange={(e) => setFormData({ ...formData, username: e.target.value })}
              required
            />
          </div>
          {!isLogin && (
            <div className="form-group">
              <label htmlFor="email">E-Mail</label>
              <input
                type="email"
                id="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                required
              />
            </div>
          )}
          <div className="form-group">
            <label htmlFor="password">Passwort</label>
            <input
              type="password"
              id="password"
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              required
              minLength={6}
            />
          </div>
          <button type="submit" disabled={loading} className="submit-button">
            {loading ? 'Lädt...' : isLogin ? 'Anmelden' : 'Registrieren'}
          </button>
        </form>
        <p className="toggle-form">
          {isLogin ? 'Noch kein Konto? ' : 'Bereits ein Konto? '}
          <button
            type="button"
            onClick={() => {
              setIsLogin(!isLogin);
              setError('');
            }}
            className="link-button"
          >
            {isLogin ? 'Registrieren' : 'Anmelden'}
          </button>
        </p>
      </div>
    </div>
  );
}

export default Login;

