# DesignReviewer

A Spring Boot application for reviewing system design documents using Retrieval-Augmented Generation (RAG) with AI-powered question answering.

## Overview

DesignReviewer allows users to upload design documents (PDFs), ask questions about them, and receive accurate, context-aware answers. It uses vector embeddings for efficient document search and integrates with Ollama for local AI processing.

## Features

- **Document Upload**: Upload PDF documents for analysis
- **Text Extraction**: Automatic text extraction from PDFs using Apache PDFBox
- **Intelligent Chunking**: Split documents into manageable chunks for better retrieval
- **Vector Embeddings**: Generate embeddings using Ollama's nomic-embed-text model
- **Vector Search**: Fast similarity search using cosine similarity
- **AI-Powered Q&A**: Answer questions using Ollama's phi3 model with retrieved context
- **Chat Sessions**: Maintain conversation history for follow-up questions
- **REST API**: Clean REST endpoints with OpenAPI documentation

## Technology Stack

- **Backend**: Spring Boot 3.3.5 (Java 21)
- **AI Framework**: Spring AI 1.0.0-M1
- **LLM**: Ollama (phi3 for chat, nomic-embed-text for embeddings)
- **Database**: PostgreSQL
- **ORM**: JPA/Hibernate
- **Document Processing**: Apache PDFBox
- **API Documentation**: SpringDoc OpenAPI
- **Build Tool**: Maven

## Prerequisites

- Java 21
- Maven 3.6+
- PostgreSQL
- Ollama (with phi3 and nomic-embed-text models)

## Installation

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd DesignReviewer-main
   ```

2. **Install dependencies**:
   ```bash
   mvn clean install
   ```

3. **Set up PostgreSQL**:
   - Create a database named `ai_reviewer`
   - Update `application.properties` with your database credentials

4. **Install Ollama**:
   - Download and install Ollama from [ollama.ai](https://ollama.ai)
   - Pull the required models:
     ```bash
     ollama pull phi3
     ollama pull nomic-embed-text
     ```

5. **Configure application**:
   - Ensure Ollama is running on `http://localhost:11434`
   - Update database connection in `src/main/resources/application.properties`

## Running the Application

1. **Start the application**:
   ```bash
   mvn spring-boot:run
   ```

2. **Access the API**:
   - API Documentation: http://localhost:8080/swagger-ui.html
   - Server runs on port 8080

## API Endpoints

### Documents
- `POST /api/documents` - Create a document
- `GET /api/documents` - Get all documents
- `GET /api/documents/{id}` - Get document by ID
- `POST /api/documents/upload` - Upload a PDF document

### Questions & Chat
- `POST /api/documents/{id}/ask` - Ask a question about a document
- `GET /api/documents/{documentId}/sessions` - Get chat sessions for a document
- `GET /api/documents/sessions/{sessionId}` - Get messages for a session

## Usage Example

1. **Upload a document**:
   ```bash
   curl -X POST -F "file=@design.pdf" -F "title=My Design Doc" http://localhost:8080/api/documents/upload
   ```

2. **Ask a question**:
   ```bash
   curl -X POST -H "Content-Type: application/json" \
        -d '{"question":"What is the database design?","sessionId":null}' \
        http://localhost:8080/api/documents/1/ask
   ```

## Architecture

The application follows a layered architecture:

- **Controller Layer**: REST endpoints (`DesignDocumentController`)
- **Service Layer**: Business logic (`DesignDocumentService`, `EmbeddingService`, `VectorSearchService`, etc.)
- **Repository Layer**: Data access (`DesignDocumentRepository`, `DocumentChunkRepository`)
- **Model Layer**: JPA entities (`DesignDocument`, `DocumentChunk`, `ChatSession`, `ChatMessage`)

### Data Flow

1. Document upload → Text extraction → Chunking → Embedding generation → Storage
2. Question → Embedding → Vector search → Context retrieval → LLM generation → Answer

## Configuration

Key configuration in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/ai_reviewer
spring.datasource.username=postgres
spring.datasource.password=postgres

# Ollama
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=phi3
spring.ai.ollama.embedding.options.model=nomic-embed-text

# File upload
spring.servlet.multipart.max-file-size=80MB
spring.servlet.multipart.max-request-size=80MB
```

## Development

### Running Tests
```bash
mvn test
```

### Building for Production
```bash
mvn clean package
java -jar target/designreviewer-0.0.1-SNAPSHOT.jar
```

## Challenges & Solutions

- **AI Integration**: Used Spring AI for seamless Ollama integration
- **Vector Search**: Implemented cosine similarity for efficient retrieval
- **Scalability**: Designed for chunk-based processing to handle large documents
- **Accuracy**: RAG approach ensures answers are grounded in document context

## Future Enhancements

- Multi-modal support (images, diagrams)
- Integration with vector databases (Pinecone, Weaviate)
- User authentication and document sharing
- Advanced chunking strategies
- Cloud deployment options
- Real-time collaboration features
