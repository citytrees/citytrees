import React, {useEffect, useState} from 'react';
import './App.css';

function App() {

  const [hello, setHello] = useState("");

  useEffect(() => {
    fetch("http://localhost:8080/").then((response) => response.text().then((data) => {console.log(data); setHello(data)}))
  }, [])

  return (
    <div className="App">
      421321
      {hello}
    </div>
  );
}

export default App;
