import { useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { fetchDocumentById, clearSelectedDocument } from "../store/documentSlice";

function DocumentDetailedPage() {
  const { id } = useParams();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { selectedDocument, loading, error } = useSelector((state) => state.documents);

  useEffect(() => {
    dispatch(fetchDocumentById(Number(id)));
    return () => dispatch(clearSelectedDocument());
  }, [id, dispatch]);

  if (loading) return (
    <div className="page">
      <div className="status-box status-loading">Loading document...</div>
    </div>
  );

  if (error) return (
    <div className="page">
      <div className="status-box status-error">{error}</div>
    </div>
  );

  if (!selectedDocument) return null;

  return (
    <div className="page">
      <div className="page-header">
        <button className="btn btn-white btn-sm" onClick={() => navigate("/documents")}>
          ← Back
        </button>
        <button className="btn btn-green" onClick={() => navigate(`/documents/${id}/chat`)}>
          Chat with this document →
        </button>
      </div>

      <hr className="divider" />

      <div className="card">
        <span className="badge">PDF</span>
        <h1 className="page-title" style={{ marginTop: 12, marginBottom: 8 }}>
          {selectedDocument.title}
        </h1>
        <p style={{ color: "#555", fontWeight: 600, marginBottom: 16 }}>
          Document ID: #{selectedDocument.id}
        </p>
        <a
          className="btn btn-blue"
          href={`http://localhost:8080/api/documents/${selectedDocument.id}/download`}
          target="_blank"
          rel="noreferrer"
          style={{ display: "inline-flex", marginBottom: 0 }}
        >
          Download PDF
        </a>
      </div>
    </div>
  );
}

export default DocumentDetailedPage;
