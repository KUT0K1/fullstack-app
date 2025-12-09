import { useState, useEffect } from 'react';
import { eventsApi } from '../api/events';
import { participantsApi } from '../api/participants';
import { paymentsApi } from '../api/payments';
import './EventDetail.css';

function EventDetail({ event: initialEvent, onBack, onEventUpdated, onEventDeleted }) {
  const [event, setEvent] = useState(initialEvent);
  const [loading, setLoading] = useState(false);
  const [showParticipantForm, setShowParticipantForm] = useState(false);
  const [showPaymentForm, setShowPaymentForm] = useState(false);
  const [editingParticipant, setEditingParticipant] = useState(null);
  const [editingPayment, setEditingPayment] = useState(null);

  useEffect(() => {
    loadEvent();
  }, [initialEvent.id]);

  const loadEvent = async () => {
    try {
      const data = await eventsApi.getById(initialEvent.id);
      setEvent(data);
    } catch (error) {
      console.error('Fehler beim Laden des Events:', error);
    }
  };

  const handleParticipantCreated = () => {
    setShowParticipantForm(false);
    loadEvent();
  };

  const handleParticipantUpdated = () => {
    setEditingParticipant(null);
    loadEvent();
  };

  const handleParticipantDeleted = async (id) => {
    try {
      await participantsApi.delete(event.id, id);
      loadEvent();
    } catch (error) {
      console.error('Fehler beim Löschen des Teilnehmers:', error);
    }
  };

  const handlePaymentCreated = () => {
    setShowPaymentForm(false);
    loadEvent();
  };

  const handlePaymentUpdated = () => {
    setEditingPayment(null);
    loadEvent();
  };

  const handlePaymentDeleted = async (id) => {
    try {
      await paymentsApi.delete(event.id, id);
      loadEvent();
    } catch (error) {
      console.error('Fehler beim Löschen der Zahlung:', error);
    }
  };

  const handleEventDelete = async () => {
    if (window.confirm('Möchten Sie dieses Event wirklich löschen?')) {
      try {
        await eventsApi.delete(event.id);
        onEventDeleted();
      } catch (error) {
        console.error('Fehler beim Löschen des Events:', error);
      }
    }
  };

  return (
    <div className="event-detail-container">
      <div className="event-detail-header">
        <button onClick={onBack} className="back-button">
          ← Zurück
        </button>
        <div className="header-actions">
          <button onClick={handleEventDelete} className="delete-button">
            Event löschen
          </button>
        </div>
      </div>

      <div className="event-detail-content">
        <div className="event-info-card">
          <h1>{event.name}</h1>
          {event.description && <p className="event-description">{event.description}</p>}

          <div className="budget-summary">
            <div className="budget-item">
              <span className="budget-label">Gesamtbudget:</span>
              <span className="budget-value">
                {event.totalBudget?.toFixed(2) || '0.00'} €
              </span>
            </div>
            <div className="budget-item">
              <span className="budget-label">Budget pro Zahler ({event.numberOfPayers || 0}):</span>
              <span className="budget-value">
                {event.budgetPerPayer?.toFixed(2) || '0.00'} €
              </span>
            </div>
            <div className="budget-item">
              <span className="budget-label">Standard Budget Erwachsene:</span>
              <span className="budget-value">{event.adultBudget?.toFixed(2)} €</span>
            </div>
            <div className="budget-item">
              <span className="budget-label">Standard Budget Kinder:</span>
              <span className="budget-value">{event.childBudget?.toFixed(2)} €</span>
            </div>
            {event.generalCosts > 0 && (
              <div className="budget-item">
                <span className="budget-label">Allgemeine Kosten:</span>
                <span className="budget-value">{event.generalCosts?.toFixed(2)} €</span>
              </div>
            )}
          </div>
        </div>

        <div className="participants-section">
          <div className="section-header">
            <h2>Teilnehmer ({event.participants?.length || 0})</h2>
            <button
              onClick={() => setShowParticipantForm(true)}
              className="add-button"
            >
              + Teilnehmer hinzufügen
            </button>
          </div>

          {showParticipantForm && (
            <ParticipantForm
              eventId={event.id}
              participants={event.participants || []}
              onCancel={() => setShowParticipantForm(false)}
              onSuccess={handleParticipantCreated}
            />
          )}

          {editingParticipant && (
            <ParticipantForm
              eventId={event.id}
              participants={event.participants || []}
              participant={editingParticipant}
              onCancel={() => setEditingParticipant(null)}
              onSuccess={handleParticipantUpdated}
            />
          )}

          <div className="participants-list">
            {event.participants?.length === 0 ? (
              <p className="empty-message">Noch keine Teilnehmer</p>
            ) : (
              event.participants?.map((participant) => (
                <div key={participant.id} className="participant-card">
                  <div className="participant-info">
                    <h3>
                      {participant.name}
                      {participant.isCouple && (
                        <span className="couple-badge">Paar</span>
                      )}
                    </h3>
                    <p className="participant-type">
                      {participant.type === 'ADULT' ? 'Erwachsener' : 'Kind'}
                    </p>
                    <p className="participant-budget">
                      Budget: {participant.calculatedBudget?.toFixed(2) || '0.00'} €
                      {participant.customBudget && (
                        <span className="custom-budget-badge">(individuell)</span>
                      )}
                    </p>
                    {participant.partnerId && (
                      <p className="partner-info">
                        Partner:{' '}
                        {event.participants?.find((p) => p.id === participant.partnerId)?.name ||
                          `ID ${participant.partnerId}`}
                      </p>
                    )}
                  </div>
                  <div className="participant-actions">
                    <button
                      onClick={() => setEditingParticipant(participant)}
                      className="edit-button"
                    >
                      Bearbeiten
                    </button>
                    <button
                      onClick={() => handleParticipantDeleted(participant.id)}
                      className="delete-button-small"
                    >
                      Löschen
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        <div className="payments-section">
          <div className="section-header">
            <h2>Zahlungen</h2>
            <button
              onClick={() => setShowPaymentForm(true)}
              className="add-button"
            >
              + Zahlung hinzufügen
            </button>
          </div>

          {showPaymentForm && (
            <PaymentForm
              eventId={event.id}
              participants={event.participants || []}
              onCancel={() => setShowPaymentForm(false)}
              onSuccess={handlePaymentCreated}
            />
          )}

          {editingPayment && (
            <PaymentForm
              eventId={event.id}
              participants={event.participants || []}
              payment={editingPayment}
              onCancel={() => setEditingPayment(null)}
              onSuccess={handlePaymentUpdated}
            />
          )}

          <div className="payments-list">
            {event.payments?.length === 0 ? (
              <p className="empty-message">Noch keine Zahlungen</p>
            ) : (
              event.payments?.map((payment) => {
                const paymentParticipant = payment.participantId
                  ? event.participants?.find((p) => p.id === payment.participantId)
                  : null;
                const paymentPartner = payment.partnerId
                  ? event.participants?.find((p) => p.id === payment.partnerId)
                  : null;

                return (
                  <div key={payment.id} className="payment-card">
                    <div className="payment-info">
                      <h3>
                        {payment.payerName}
                        {paymentParticipant && (
                          <span className="participant-badge">
                            {paymentParticipant.name}
                            {paymentPartner && ` & ${paymentPartner.name}`}
                          </span>
                        )}
                      </h3>
                      <p className="payment-amount">{payment.amount?.toFixed(2)} €</p>
                      {paymentParticipant && (
                        <p className="payment-participant">
                          Teilnehmer: {paymentParticipant.name}
                          {paymentPartner && ` & ${paymentPartner.name} (Paar)`}
                        </p>
                      )}
                      {payment.note && <p className="payment-note">{payment.note}</p>}
                      <p className="payment-date">
                        {new Date(payment.createdAt).toLocaleDateString('de-DE')}
                      </p>
                    </div>
                  <div className="payment-actions">
                    <button
                      onClick={() => setEditingPayment(payment)}
                      className="edit-button"
                    >
                      Bearbeiten
                    </button>
                    <button
                      onClick={() => handlePaymentDeleted(payment.id)}
                      className="delete-button-small"
                    >
                      Löschen
                    </button>
                  </div>
                </div>
              )}
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

function ParticipantForm({ eventId, participants, participant, onCancel, onSuccess }) {
  const [formData, setFormData] = useState({
    name: participant?.name || '',
    type: participant?.type || 'ADULT',
    customBudget: participant?.customBudget?.toString() || '',
    isCouple: participant?.isCouple || false,
    partnerId: participant?.partnerId?.toString() || '',
    userId: participant?.userId?.toString() || '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Verfügbare Partner: alle Participants außer dem aktuell bearbeiteten
  const availablePartners = participants.filter(
    (p) => p.id !== participant?.id && p.type === 'ADULT'
  );

  const handlePartnerChange = (partnerId) => {
    const newPartnerId = partnerId === '' ? '' : partnerId;
    setFormData({
      ...formData,
      partnerId: newPartnerId,
      isCouple: newPartnerId !== '', // Automatisch isCouple setzen wenn Partner ausgewählt
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const data = {
        name: formData.name,
        type: formData.type,
        customBudget: formData.customBudget ? parseFloat(formData.customBudget) : null,
        isCouple: formData.isCouple,
        partnerId: formData.partnerId ? parseInt(formData.partnerId) : null,
        userId: formData.userId ? parseInt(formData.userId) : null,
      };

      if (participant) {
        await participantsApi.update(eventId, participant.id, data);
      } else {
        await participantsApi.create(eventId, data);
      }
      onSuccess();
    } catch (err) {
      setError('Fehler beim Speichern des Teilnehmers');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="form-overlay">
      <div className="form-card">
        <h3>{participant ? 'Teilnehmer bearbeiten' : 'Neuer Teilnehmer'}</h3>
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
            <label>Typ *</label>
            <select
              value={formData.type}
              onChange={(e) => setFormData({ ...formData, type: e.target.value })}
              required
            >
              <option value="ADULT">Erwachsener</option>
              <option value="CHILD">Kind</option>
            </select>
          </div>
          <div className="form-group">
            <label>Partner (optional)</label>
            <select
              value={formData.partnerId}
              onChange={(e) => handlePartnerChange(e.target.value)}
            >
              <option value="">Kein Partner</option>
              {availablePartners.map((p) => (
                <option key={p.id} value={p.id.toString()}>
                  {p.name}
                </option>
              ))}
            </select>
            {formData.partnerId && (
              <p className="form-hint">
                Als Paar gewertet (wird automatisch gesetzt)
              </p>
            )}
          </div>
          <div className="form-group">
            <label>Individuelles Budget (€) - optional</label>
            <input
              type="number"
              step="0.01"
              min="0"
              value={formData.customBudget}
              onChange={(e) =>
                setFormData({ ...formData, customBudget: e.target.value })
              }
            />
          </div>
          <div className="form-group">
            <label>User ID (optional)</label>
            <input
              type="number"
              value={formData.userId}
              onChange={(e) =>
                setFormData({ ...formData, userId: e.target.value })
              }
            />
          </div>
          <div className="form-actions">
            <button type="button" onClick={onCancel} className="cancel-button">
              Abbrechen
            </button>
            <button type="submit" disabled={loading} className="submit-button">
              {loading ? 'Speichere...' : 'Speichern'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

function PaymentForm({ eventId, participants, payment, onCancel, onSuccess }) {
  const [formData, setFormData] = useState({
    amount: payment?.amount?.toString() || '',
    payerName: payment?.payerName || '',
    participantId: payment?.participantId?.toString() || '',
    note: payment?.note || '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Verfügbare Participants (nur Erwachsene, da nur diese zahlen können)
  const availableParticipants = participants.filter((p) => p.type === 'ADULT');

  const handleParticipantChange = (participantId) => {
    const newParticipantId = participantId === '' ? '' : participantId;
    const selectedParticipant = participants.find(
      (p) => p.id.toString() === newParticipantId
    );
    
    setFormData({
      ...formData,
      participantId: newParticipantId,
      // Automatisch payerName setzen wenn Participant ausgewählt
      payerName: selectedParticipant
        ? selectedParticipant.name
        : formData.payerName,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const data = {
        amount: parseFloat(formData.amount),
        payerName: formData.payerName || null,
        participantId: formData.participantId ? parseInt(formData.participantId) : null,
        note: formData.note || null,
      };

      if (payment) {
        await paymentsApi.update(eventId, payment.id, data);
      } else {
        await paymentsApi.create(eventId, data);
      }
      onSuccess();
    } catch (err) {
      setError('Fehler beim Speichern der Zahlung');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="form-overlay">
      <div className="form-card">
        <h3>{payment ? 'Zahlung bearbeiten' : 'Neue Zahlung'}</h3>
        {error && <div className="error-message">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Teilnehmer (optional)</label>
            <select
              value={formData.participantId}
              onChange={(e) => handleParticipantChange(e.target.value)}
            >
              <option value="">Kein Teilnehmer</option>
              {availableParticipants.map((p) => (
                <option key={p.id} value={p.id.toString()}>
                  {p.name}
                  {p.partnerId && ` (Paar mit ${participants.find((partner) => partner.id === p.partnerId)?.name || 'Partner'})`}
                </option>
              ))}
            </select>
            {formData.participantId && (
              <p className="form-hint">
                Der Name wird automatisch aus dem Teilnehmer übernommen
              </p>
            )}
          </div>
          <div className="form-group">
            <label>Zahler Name {!formData.participantId && '*'}</label>
            <input
              type="text"
              value={formData.payerName}
              onChange={(e) => setFormData({ ...formData, payerName: e.target.value })}
              required={!formData.participantId}
              disabled={!!formData.participantId}
            />
            {formData.participantId && (
              <p className="form-hint">
                Wird automatisch aus dem Teilnehmer übernommen
              </p>
            )}
          </div>
          <div className="form-group">
            <label>Betrag (€) *</label>
            <input
              type="number"
              step="0.01"
              min="0.01"
              value={formData.amount}
              onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
              required
            />
          </div>
          <div className="form-group">
            <label>Notiz</label>
            <textarea
              value={formData.note}
              onChange={(e) => setFormData({ ...formData, note: e.target.value })}
              rows="3"
            />
          </div>
          <div className="form-actions">
            <button type="button" onClick={onCancel} className="cancel-button">
              Abbrechen
            </button>
            <button type="submit" disabled={loading} className="submit-button">
              {loading ? 'Speichere...' : 'Speichern'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default EventDetail;

