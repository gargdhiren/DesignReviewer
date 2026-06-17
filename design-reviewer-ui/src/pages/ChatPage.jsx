import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { sendQuestion, clearChat, fetchSessions, switchSession, startNewChat, removeSession } from "../store/chatSlice";

function ChatPage() {
  const { id } = useParams();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { messages, loading, error, sessions, activeSessionId } = useSelector((state) => state.chat);
  const [question, setQuestion] = useState("");

  useEffect(() => {
    dispatch(fetchSessions(Number(id))).then((action) => {
      // Auto-load messages of the latest session
      if (action.payload?.length > 0) {
        const latest = action.payload[action.payload.length - 1];
        dispatch(switchSession(latest.id));
      }
    });
    return () => dispatch(clearChat());
  }, [id, dispatch]);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!question.trim()) return;
    dispatch(sendQuestion({ documentId: Number(id), question, sessionId: activeSessionId }));
    setQuestion("");
  };

  const handleSwitchSession = (sessionId) => {
    if (sessionId === activeSessionId) return;
    dispatch(switchSession(sessionId));
  };

  const handleDeleteSession = (e, sessionId) => {
    e.stopPropagation();
    if (!window.confirm("Delete this session and all its messages?")) return;
    dispatch(removeSession(sessionId));
  };

  const formatDate = (dateStr) => {
    const d = new Date(dateStr);
    return d.toLocaleDateString("en-US", { month: "short", day: "numeric", hour: "2-digit", minute: "2-digit" });
  };

  return (
    <div className="page">
      <div className="page-header">
        <button className="btn btn-white btn-sm" onClick={() => navigate(`/documents/${id}`)}>
          ← Back
        </button>
        <h1 className="page-title">Chat</h1>
        <button className="btn btn-pink btn-sm" onClick={() => dispatch(startNewChat())}>
          + New Chat
        </button>
      </div>

      <hr className="divider" />

      <div className="chat-layout">
        {/* Sessions Sidebar */}
        <div className="sessions-sidebar">
          <div className="sessions-sidebar-header">
            <span>Sessions</span>
            <span style={{ fontWeight: 600 }}>{sessions.length}</span>
          </div>
          {sessions.length === 0 ? (
            <div className="session-item">No sessions yet</div>
          ) : (
            [...sessions].reverse().map((session) => (
              <div
                key={session.id}
                className={`session-item ${session.id === activeSessionId ? "active" : ""}`}
                onClick={() => handleSwitchSession(session.id)}
              >
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                  <span>Session #{session.id}</span>
                  <button
                    style={{ background: "none", border: "none", cursor: "pointer", fontWeight: 900, fontSize: "1rem", lineHeight: 1 }}
                    onClick={(e) => handleDeleteSession(e, session.id)}
                    title="Delete session"
                  >
                    ×
                  </button>
                </div>
                <div style={{ fontSize: "0.75rem", fontWeight: 400, marginTop: 2, color: "#555" }}>
                  {formatDate(session.createdAt)}
                </div>
              </div>
            ))
          )}
        </div>

        {/* Chat Area */}
        <div>
          <div className="chat-window">
            {messages.length === 0 && !loading && (
              <p style={{ color: "#888", fontWeight: 600, textAlign: "center", margin: "auto" }}>
                {activeSessionId ? "No messages in this session." : "Start a new conversation."}
              </p>
            )}
            {messages.map((msg, index) => (
              <div
                key={index}
                className={`message ${msg.role === "USER" ? "message-user" : "message-assistant"}`}
              >
                {msg.content}
              </div>
            ))}
            {loading && <div className="thinking">Thinking...</div>}
            {error && <div className="status-box status-error">{error}</div>}
          </div>

          <form className="chat-form" onSubmit={handleSubmit}>
            <input
              className="input"
              value={question}
              onChange={(e) => setQuestion(e.target.value)}
              placeholder="Ask a question about this document..."
              disabled={loading}
            />
            <button className="btn" type="submit" disabled={loading}>
              {loading ? "..." : "Send"}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

export default ChatPage;
