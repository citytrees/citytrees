import React, {useEffect} from "react";
import './App.css';
import {BrowserRouter, Route, Routes,} from "react-router-dom";
import RegistrationPage from "./pages/Registration";
import LoginPage from "./pages/Login";
import AppRoutes from "./constants/AppRoutes";
import MainPage from "./pages/Main";
import ProtectedRoute from "./components/route/ProtectedRoute";
import {setUser, User} from "./features/user/userSlice";
import jwt_decode from "jwt-decode";
import {useAppDispatch} from "./app/hooks";
import {getAccessToken} from "./helpers/cookies";

export default function App() {
  const dispatch = useAppDispatch();

  useEffect(() => {
    let ctAccessToken = getAccessToken();
    if (ctAccessToken) {
      dispatch(setUser(jwt_decode<User>(ctAccessToken)))
    }
  });

  return (
      <BrowserRouter>
        <Routes>
          <Route path={AppRoutes.LOGIN} element={<ProtectedRoute element={<LoginPage/>}/>}/>
          <Route path={AppRoutes.REGISTER} element={<ProtectedRoute element={<RegistrationPage/>}/>}/>
          <Route path={AppRoutes.MAIN} element={<MainPage/>}/>
        </Routes>
      </BrowserRouter>
  );
}