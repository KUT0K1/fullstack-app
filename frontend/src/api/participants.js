import apiClient from './client';

export const participantsApi = {
  create: async (eventId, participantData) => {
    const response = await apiClient.post(
      `/events/${eventId}/participants`,
      participantData
    );
    return response.data;
  },

  update: async (eventId, id, participantData) => {
    const response = await apiClient.put(
      `/events/${eventId}/participants/${id}`,
      participantData
    );
    return response.data;
  },

  delete: async (eventId, id) => {
    await apiClient.delete(`/events/${eventId}/participants/${id}`);
  },
};

