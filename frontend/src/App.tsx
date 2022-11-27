import React from "react";
import './App.css';
import {BrowserRouter, Route, Routes,} from "react-router-dom";
import RegistrationPage from "./pages/Registration";
import LoginPage from "./pages/Login";
import AppRoutes from "./constants/AppRoutes";
import MainPage from "./pages/Main";

export default function App() {
  return (
      <BrowserRouter>
        <Routes>
          <Route path={AppRoutes.LOGIN} element={<LoginPage/>}/>
          <Route path={AppRoutes.REGISTER} element={<RegistrationPage/>}/>
          <Route path={AppRoutes.MAIN} element={<MainPage/>}/>
        </Routes>
      </BrowserRouter>
  );
}