import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { askQuestion, getMessages, getSessions, deleteSession } from "../api/documentApi";

export const fetchSessions = createAsyncThunk(
  "chat/fetchSessions",
  async (documentId) => {
    const res = await getSessions(documentId);
    return res.data; // list of { id, createdAt }
  },
);

export const switchSession = createAsyncThunk(
  "chat/switchSession",
  async (sessionId) => {
    const res = await getMessages(sessionId);
    return { sessionId, messages: res.data };
  },
);

export const removeSession = createAsyncThunk(
  "chat/removeSession",
  async (sessionId) => {
    await deleteSession(sessionId);
    return sessionId;
  },
);

export const sendQuestion = createAsyncThunk(
  "chat/sendQuestion",
  async ({ documentId, question, sessionId }) => {
    const res = await askQuestion(documentId, question, sessionId);
    return res.data;
  },
);

const chatSlice = createSlice({
  name: "chat",
  initialState: {
    sessions: [],          // all sessions for current document
    activeSessionId: null, // which session is open
    messages: [],          // messages of active session
    loading: false,
    error: null,
  },
  reducers: {
    clearChat(state) {
      state.sessions = [];
      state.activeSessionId = null;
      state.messages = [];
      state.error = null;
    },
    startNewChat(state) {
      state.activeSessionId = null;
      state.messages = [];
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch all sessions for a document
      .addCase(fetchSessions.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchSessions.fulfilled, (state, action) => {
        state.loading = false;
        state.sessions = action.payload;
        // Auto-load the latest session if one exists
        if (action.payload.length > 0) {
          const latest = action.payload[action.payload.length - 1];
          state.activeSessionId = latest.id;
        }
      })
      .addCase(fetchSessions.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })

      // Switch to a different session
      .addCase(switchSession.pending, (state) => {
        state.loading = true;
        state.messages = [];
      })
      .addCase(switchSession.fulfilled, (state, action) => {
        state.loading = false;
        state.activeSessionId = action.payload.sessionId;
        state.messages = action.payload.messages;
      })
      .addCase(switchSession.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })

      // Send a question
      .addCase(sendQuestion.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(sendQuestion.fulfilled, (state, action) => {
        state.loading = false;
        const newSessionId = action.payload.sessionId;

        // If this was a new chat, add the new session to the list
        if (!state.activeSessionId) {
          state.sessions.push({ id: newSessionId, createdAt: new Date().toISOString() });
        }

        state.activeSessionId = newSessionId;
        state.messages.push(
          { role: "USER", content: action.meta.arg.question },
          { role: "ASSISTANT", content: action.payload.answer },
        );
      })
      .addCase(sendQuestion.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(removeSession.fulfilled, (state, action) => {
        state.sessions = state.sessions.filter(s => s.id !== action.payload);
        if (state.activeSessionId === action.payload) {
          state.activeSessionId = state.sessions.length > 0 ? state.sessions[0].id : null;
          state.messages = [];
        }
      });
  },
});

export const { clearChat, startNewChat } = chatSlice.actions;
export default chatSlice.reducer;
