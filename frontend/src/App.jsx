import { useState } from 'react'
import './App.css'

function App() {
  const [message, setMessage] = useState('')

  async function fetchMessage() {
    const apiUrl = import.meta.env.DEV ? '/api/hello' : 'http://localhost:8080/api/hello'
    const res = await fetch(apiUrl)
    const text = await res.text()
    setMessage(text)
  }

  return (
    <div className="app">
      <h1>Willkommen zu deinem Vite + React + Spring Boot Projekt 🚀</h1>
      <button onClick={fetchMessage}>Backend testen</button>
      {message && <p>{message}</p>}
    </div>
  )
}

export default App
