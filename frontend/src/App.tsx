import React from "react";
import './App.css';

import {BrowserRouter, Route, Routes,} from "react-router-dom";
import RegistrationPage from "./pages/Registration";

export default function App() {
  return (
      <BrowserRouter>
        <Routes>
          <Route path="/register" element={<RegistrationPage/>}/>
        </Routes>
      </BrowserRouter>
  );
}