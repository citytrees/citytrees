import React, {useEffect, useState} from 'react';
import './App.css';
import {HelloControllerApi} from "./generated/openapi";

function App() {

  const [hello, setHello] = useState("");
  const api = new HelloControllerApi()

  useEffect(() => {
    api.helloWorld().then(response => {
      if (response.name !== undefined) {
        setHello(response.name)
      }
    })
  }, [])

  return (
    <div className="App">
      Hello: {hello}
    </div>
  );
}

export default App;
