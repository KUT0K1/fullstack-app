import apiClient from './client';

export const eventsApi = {
  getAll: async () => {
    const response = await apiClient.get('/events');
    return response.data;
  },

  getById: async (id) => {
    const response = await apiClient.get(`/events/${id}`);
    return response.data;
  },

  create: async (eventData) => {
    const response = await apiClient.post('/events', eventData);
    return response.data;
  },

  update: async (id, eventData) => {
    const response = await apiClient.put(`/events/${id}`, eventData);
    return response.data;
  },

  delete: async (id) => {
    await apiClient.delete(`/events/${id}`);
  },
};

