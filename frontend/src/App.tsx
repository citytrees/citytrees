import React, {useEffect, useState} from 'react';
import './App.css';

function App() {

  const [hello, setHello] = useState("");

  useEffect(() => {
  }, [])

  return (
    <div className="App">
      Hello: {hello}
    </div>
  );
}

export default App;
