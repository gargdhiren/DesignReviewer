import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  getAllDocuments,
  getDocumentById,
  uploadDocument,
  deleteDocument,
} from "../api/documentApi";

export const fetchDocuments = createAsyncThunk(
  "document/fetchAll",
  async (_, { signal }) => {
    return (await getAllDocuments(signal)).data;
  },
);

export const fetchDocumentById = createAsyncThunk(
  "documents/fetchById",
  async (id, { signal }) => {
    const res = await getDocumentById(id, signal);
    return res.data;
  },
);

export const deleteDoc = createAsyncThunk(
  "documents/delete",
  async (id) => {
    await deleteDocument(id);
    return id;
  },
);

export const uploadDoc = createAsyncThunk(
  "documents/upload",
  async ({ file, title }) => {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("title", title);
    const res = await uploadDocument(formData);
    return res.data;
  },
);

const documentsSlice = createSlice({
  name: "documents",
  initialState: {
    documents: [],
    selectedDocument: null,
    loading: false,
    deleting: null, // id of document currently being deleted
    error: null,
  },
  reducers: {
    clearSelectedDocument(state) {
      state.selectedDocument = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchDocuments.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchDocuments.fulfilled, (state, action) => {
        state.loading = false;
        state.documents = action.payload;
      })
      .addCase(fetchDocuments.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(fetchDocumentById.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchDocumentById.fulfilled, (state, action) => {
        state.loading = false;
        state.selectedDocument = action.payload;
      })
      .addCase(fetchDocumentById.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(uploadDoc.fulfilled, (state, action) => {
        state.loading = false;
        state.documents.push(action.payload);
      })
      .addCase(uploadDoc.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(uploadDoc.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(deleteDoc.pending, (state, action) => {
        state.deleting = action.meta.arg;
      })
      .addCase(deleteDoc.fulfilled, (state, action) => {
        state.deleting = null;
        state.documents = state.documents.filter(d => d.id !== action.payload);
      })
      .addCase(deleteDoc.rejected, (state, action) => {
        state.deleting = null;
        state.error = action.error.message;
      });
  },
});

export const { clearSelectedDocument } = documentsSlice.actions;
export default documentsSlice.reducer;
