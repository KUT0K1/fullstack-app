import { useState, useEffect } from 'react';
import { eventsApi } from '../api/events';
import EventDetail from './EventDetail';
import './EventList.css';

function EventList({ user, onLogout }) {
  const [events, setEvents] = useState([]);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showCreateForm, setShowCreateForm] = useState(false);

  useEffect(() => {
    loadEvents();
  }, []);

  const loadEvents = async () => {
    try {
      const data = await eventsApi.getAll();
      setEvents(data);
    } catch (error) {
      console.error('Fehler beim Laden der Events:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleEventCreated = () => {
    setShowCreateForm(false);
    loadEvents();
  };

  const handleEventDeleted = () => {
    setSelectedEvent(null);
    loadEvents();
  };

  if (loading) {
    return <div className="loading">Lädt...</div>;
  }

  if (selectedEvent) {
    return (
      <EventDetail
        event={selectedEvent}
        onBack={() => setSelectedEvent(null)}
        onEventUpdated={loadEvents}
        onEventDeleted={handleEventDeleted}
      />
    );
  }

  return (
    <div className="event-list-container">
      <header className="header">
        <h1>Event Budget Verwaltung</h1>
        <div className="user-info">
          <span>Hallo, {user.username}</span>
          <button onClick={onLogout} className="logout-button">
            Abmelden
          </button>
        </div>
      </header>

      <div className="events-section">
        <div className="events-header">
          <h2>Meine Events</h2>
          <button onClick={() => setShowCreateForm(true)} className="create-button">
            + Neues Event
          </button>
        </div>

        {showCreateForm && (
          <EventForm
            onCancel={() => setShowCreateForm(false)}
            onSuccess={handleEventCreated}
          />
        )}

        {events.length === 0 ? (
          <div className="empty-state">
            <p>Noch keine Events vorhanden.</p>
            <p>Erstelle dein erstes Event!</p>
          </div>
        ) : (
          <div className="events-grid">
            {events.map((event) => (
              <div
                key={event.id}
                className="event-card"
                onClick={() => setSelectedEvent(event)}
              >
                <h3>{event.name}</h3>
                <p className="event-description">
                  {event.description || 'Keine Beschreibung'}
                </p>
                <div className="event-stats">
                  <div className="stat">
                    <span className="stat-label">Gesamtbudget:</span>
                    <span className="stat-value">
                      {event.totalBudget?.toFixed(2) || '0.00'} €
                    </span>
                  </div>
                  <div className="stat">
                    <span className="stat-label">Teilnehmer:</span>
                    <span className="stat-value">{event.participants?.length || 0}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

function EventForm({ onCancel, onSuccess }) {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    adultBudget: '100',
    childBudget: '50',
    generalCosts: '0',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await eventsApi.create({
        name: formData.name,
        description: formData.description,
        adultBudget: parseFloat(formData.adultBudget),
        childBudget: parseFloat(formData.childBudget),
        generalCosts: parseFloat(formData.generalCosts),
      });
      onSuccess();
    } catch (err) {
      setError('Fehler beim Erstellen des Events');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="event-form-overlay">
      <div className="event-form">
        <h3>Neues Event erstellen</h3>
        {error && <div className="error-message">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Name *</label>
            <input
              type="text"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              required
            />
          </div>
          <div className="form-group">
            <label>Beschreibung</label>
            <textarea
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              rows="3"
            />
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Budget Erwachsene (€) *</label>
              <input
                type="number"
                step="0.01"
                min="0"
                value={formData.adultBudget}
                onChange={(e) =>
                  setFormData({ ...formData, adultBudget: e.target.value })
                }
                required
              />
            </div>
            <div className="form-group">
              <label>Budget Kinder (€) *</label>
              <input
                type="number"
                step="0.01"
                min="0"
                value={formData.childBudget}
                onChange={(e) =>
                  setFormData({ ...formData, childBudget: e.target.value })
                }
                required
              />
            </div>
          </div>
          <div className="form-group">
            <label>Allgemeine Kosten (€)</label>
            <input
              type="number"
              step="0.01"
              min="0"
              value={formData.generalCosts}
              onChange={(e) =>
                setFormData({ ...formData, generalCosts: e.target.value })
              }
            />
          </div>
          <div className="form-actions">
            <button type="button" onClick={onCancel} className="cancel-button">
              Abbrechen
            </button>
            <button type="submit" disabled={loading} className="submit-button">
              {loading ? 'Erstelle...' : 'Erstellen'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default EventList;

