import { useEffect } from "react";
import { Link } from "react-router-dom";
import { fetchDocuments, deleteDoc } from "../store/documentSlice";
import { useDispatch, useSelector } from "react-redux";

function DocumentsPage() {
  const dispatch = useDispatch();
  const { documents, loading, error, deleting } = useSelector((state) => state.documents);

  const handleDelete = (e, id) => {
    e.preventDefault();
    if (!window.confirm("Delete this document and all its chats?")) return;
    dispatch(deleteDoc(id));
  };

  useEffect(() => {
    const controller = new AbortController();
    dispatch(fetchDocuments(controller.signal));
    return () => controller.abort();
  }, [dispatch]);

  return (
    <div className="page">
      <div className="page-header">
        <h1 className="page-title">Design Reviewer</h1>
        <Link to="/upload" className="btn btn-pink">+ Upload PDF</Link>
      </div>

      <hr className="divider" />

      {loading ? (
        <div className="status-box status-loading">Loading documents...</div>
      ) : error ? (
        <div className="status-box status-error">{error}</div>
      ) : documents.length === 0 ? (
        <div className="status-box status-empty">
          No documents yet. Upload your first PDF to get started.
        </div>
      ) : (
        documents.map((doc) => (
          <div className="card" key={doc.id}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
              <div>
                <span className="badge">PDF</span>
                <Link to={`/documents/${doc.id}`} className="doc-link" style={{ marginTop: 8 }}>
                  {doc.title}
                </Link>
              </div>
              <div style={{ display: "flex", gap: 8 }}>
                <Link to={`/documents/${doc.id}/chat`} className="btn btn-green btn-sm">
                  Chat →
                </Link>
                <button
                  className="btn btn-pink btn-sm"
                  onClick={(e) => handleDelete(e, doc.id)}
                  disabled={deleting === doc.id}
                >
                  {deleting === doc.id ? "Deleting..." : "Delete"}
                </button>
              </div>
            </div>
          </div>
        ))
      )}
    </div>
  );
}

export default DocumentsPage;
