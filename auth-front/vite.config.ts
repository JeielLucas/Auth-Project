import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: true, // Permite que o Vite escute em todas as interfaces
    port: 5173, // Define a porta que será usada
  },
  
})
