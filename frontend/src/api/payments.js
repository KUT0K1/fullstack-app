import apiClient from './client';

export const paymentsApi = {
  create: async (eventId, paymentData) => {
    const response = await apiClient.post(`/events/${eventId}/payments`, paymentData);
    return response.data;
  },

  update: async (eventId, id, paymentData) => {
    const response = await apiClient.put(`/events/${eventId}/payments/${id}`, paymentData);
    return response.data;
  },

  delete: async (eventId, id) => {
    await apiClient.delete(`/events/${eventId}/payments/${id}`);
  },
};

