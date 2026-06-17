# DesignReviewer

A full-stack RAG (Retrieval-Augmented Generation) application for reviewing system design documents. Upload a PDF, ask questions about it, and get AI-powered answers grounded in the document content.

Built as a learning project: backend-first in Spring Boot, then a React + Redux frontend built from scratch while learning React concepts.

---

## Tech Stack

### Backend
| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.3.5 (Java 21) |
| LLM | Groq API (`llama-3.3-70b-versatile`) via Spring AI OpenAI-compatible client |
| Embeddings | Jina AI (`jina-embeddings-v3`) |
| Database | Neon (cloud PostgreSQL) |
| ORM | JPA / Hibernate 6 |
| Document parsing | Apache PDFBox |
| API docs | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven (use `./mvnw` wrapper) |

### Frontend
| Layer | Technology |
|-------|-----------|
| Framework | React 18 (Vite) |
| State management | Redux Toolkit |
| Routing | React Router v6 |
| HTTP client | Axios |
| Styling | Neobrutalism design system (custom CSS) |

---

## Architecture

### RAG Pipeline — Document Ingestion
1. PDF upload → `FileParsingService` (PDFBox) extracts text + stores raw PDF bytes
2. `TextChunkingService` splits text into overlapping chunks
3. `HuggingFaceEmbeddingService` calls Jina AI REST API to vectorize each chunk
4. `EmbeddingConverter` serializes `List<Double>` vectors to JSON strings stored in `DocumentChunk.embedding` (TEXT column)

### RAG Pipeline — Question Answering
1. User sends question + optional `sessionId`
2. `VectorSearchService` embeds the question, computes cosine similarity against all chunks, returns top-3
3. Top chunk content is concatenated as context
4. `GrokLlmClient` sends context + question to Groq via Spring AI `ChatClient`
5. Answer is persisted as `ChatMessage`, returned alongside `sessionId`

### Key Design Decisions
- **Embeddings in TEXT, not pgvector** — cosine similarity computed in Java (`VectorSearchService`)
- **Spring profiles** — `local` profile holds secrets, `grok` profile activates Groq + Jina AI beans
- **LLM abstraction** — `LlmClient` interface; `GrokLlmClient` (`@Profile("grok")`) and `OllamaLlmClient` (`@Profile("ollama")`) as implementations
- **Embedding abstraction** — `EmbeddingService` interface; `HuggingFaceEmbeddingService` (`@Profile("grok")`) calls Jina AI
- **PDF stored in DB** — `DesignDocument.fileData` is `BYTEA`; download endpoint streams bytes back

### Database Entities
```
DesignDocument
  └── DocumentChunk[]     (embedding JSON, content)
  └── ChatSession[]
        └── ChatMessage[] (role: USER | ASSISTANT)
```

### Frontend Structure
```
src/
  api/documentApi.js      # Axios instance + all API calls
  store/
    store.js              # Redux configureStore
    documentSlice.js      # documents state + thunks
    chatSlice.js          # sessions/messages state + thunks
  pages/
    DocumentsPage.jsx     # list + delete documents
    DocumentDetailedPage.jsx  # document detail + PDF download
    ChatPage.jsx          # session sidebar + chat window
    UploadPage.jsx        # PDF upload form
  App.jsx                 # React Router routes
  index.css               # neobrutalism design system
```

---

## Running Locally

### Prerequisites
- Java 21, Maven 3.6+
- Node.js 18+
- Neon PostgreSQL account (or any PostgreSQL)
- Groq API key (free tier available)
- Jina AI API key (free tier available)

### Backend

1. Create `src/main/resources/application-local.properties` (this file is gitignored):
```properties
spring.datasource.url=jdbc:postgresql://<your-neon-host>/neondb?sslmode=require
spring.datasource.username=<username>
spring.datasource.password=<password>
GROQ_API_KEY=<your-groq-key>
JINA_API_KEY=<your-jina-key>
```

2. Run with `local` and `grok` profiles:
```powershell
./mvnw spring-boot:run "-Dspring-boot.run.profiles=local,grok"
```

3. API docs: http://localhost:8080/swagger-ui.html

### Frontend
```bash
cd design-reviewer-ui
npm install
npm run dev
```

Frontend runs at http://localhost:5173

---

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/documents` | List all documents |
| `GET` | `/api/documents/{id}` | Get document (500-char content preview) |
| `POST` | `/api/documents/upload` | Upload PDF (multipart: `file`, `title`) |
| `GET` | `/api/documents/{id}/download` | Download original PDF |
| `DELETE` | `/api/documents/{id}` | Delete document + all chunks/sessions/messages |
| `POST` | `/api/documents/{id}/ask` | Ask question (`{ question, sessionId }`) |
| `GET` | `/api/documents/{id}/sessions` | List chat sessions for a document |
| `GET` | `/api/documents/sessions/{sessionId}` | Get messages for a session |
| `DELETE` | `/api/documents/sessions/{sessionId}` | Delete session + its messages |

---

## Secret Management

Secrets follow the `${PLACEHOLDER}` pattern in `application.properties` — actual values live only in `application-local.properties` which is gitignored. Never commit API keys.

---

## Future Improvements (RAG)
- pgvector for native vector search instead of in-memory cosine similarity
- Chunk overlap for better context continuity
- Conversation history passed to LLM for multi-turn awareness
- Streaming responses
- Batch embedding calls
