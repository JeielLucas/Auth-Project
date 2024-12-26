// src/redux/authSlice.ts
import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface AuthState {
  emailData: string | null;
}

const initialState: AuthState = {
  emailData: null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setEmailData: (state, action: PayloadAction<string>) => {
      state.emailData = action.payload;
    },
  },
});

export const { setEmailData } = authSlice.actions;

export default authSlice.reducer;
