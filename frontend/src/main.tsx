import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { GoogleOAuthProvider } from '@react-oauth/google';
import { Provider } from 'react-redux';
import { store } from './redux/store.ts';
import reportWebVitals from './reportWebVitals.ts';

const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;
createRoot(document.getElementById('root')!).render(
  
  <StrictMode>
    <GoogleOAuthProvider clientId={clientId}>
    <Provider store={store}>
        <App />
    </Provider>
    </GoogleOAuthProvider>;
  </StrictMode>,
)

reportWebVitals();
