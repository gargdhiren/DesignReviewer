import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import DocumentsPage from "./pages/DocumentsPage";
import DocumentDetailedPage from "./pages/DocumentDetailedPage";
import ChatPage from "./pages/ChatPage";
import UploadPage from "./pages/UploadPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/documents" replace />} />
        <Route path="/documents" element={<DocumentsPage />} />
        <Route path="/documents/:id" element={<DocumentDetailedPage />} />
        <Route path="/documents/:id/chat" element={<ChatPage />} />
        <Route path="/upload" element={<UploadPage />} />
        <Route path="*" element={<h1>Page Not found. 404</h1>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
