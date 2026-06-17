import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

export const getAllDocuments = (signal) =>
  api.get("/api/documents", { signal });

export const getDocumentById = (id, signal) =>
  api.get(`/api/documents/${id}`, { signal });

export const uploadDocument = (formData, signal) =>
  api.post("/api/documents/upload", formData, { signal });

export const askQuestion = (id, question, sessionId, signal) =>
  api.post(`/api/documents/${id}/ask`, { question, sessionId }, { signal });

export const getSessions = (documentId, signal) =>
  api.get(`/api/documents/${documentId}/sessions`, { signal });

export const getMessages = (sessionId, signal) =>
  api.get(`/api/documents/sessions/${sessionId}`, { signal });

export const deleteDocument = (id) => api.delete(`/api/documents/${id}`);
export const deleteSession = (sessionId) => api.delete(`/api/documents/sessions/${sessionId}`);
