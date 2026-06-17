import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { uploadDoc } from "../store/documentSlice";

function UploadPage() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { loading, error } = useSelector((state) => state.documents);
  const [title, setTitle] = useState("");
  const [file, setFile] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!file || !title.trim()) return;

    const result = await dispatch(uploadDoc({ file, title }));

    if (uploadDoc.fulfilled.match(result)) {
      navigate(`/documents/${result.payload.id}`);
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <button className="btn btn-white btn-sm" onClick={() => navigate("/documents")}>
          ← Back
        </button>
        <h1 className="page-title">Upload PDF</h1>
      </div>

      <hr className="divider" />

      <div className="form-card">
        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label>Document Title</label>
            <input
              className="input"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="e.g. System Design - Twitter"
            />
          </div>

          <div className="input-group">
            <label>PDF File</label>
            <input
              className="input"
              type="file"
              accept=".pdf"
              onChange={(e) => setFile(e.target.files[0])}
            />
          </div>

          {file && (
            <p style={{ marginBottom: 16, fontWeight: 600 }}>
              Selected: {file.name}
            </p>
          )}

          {error && (
            <div className="status-box status-error" style={{ marginBottom: 16 }}>
              {error}
            </div>
          )}

          <button
            className="btn btn-pink"
            type="submit"
            disabled={loading || !file || !title.trim()}
            style={{ width: "100%", justifyContent: "center" }}
          >
            {loading ? "Uploading & embedding..." : "Upload Document"}
          </button>
        </form>
      </div>
    </div>
  );
}

export default UploadPage;
