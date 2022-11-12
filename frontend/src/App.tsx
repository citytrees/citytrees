import React from "react";

import {BrowserRouter, Route, Routes,} from "react-router-dom";

export default function App() {
  return (
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<span>ROOT 1</span>}/>
        </Routes>
      </BrowserRouter>
  );
}