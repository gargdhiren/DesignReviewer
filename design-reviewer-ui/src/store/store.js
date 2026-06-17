import { configureStore } from "@reduxjs/toolkit";
import documentsReducer from "./documentSlice.js";
import chatReducer from "./chatSlice.js";

const store = configureStore({
  reducer: {
    documents: documentsReducer,
    chat: chatReducer,
  },
});

export default store;
