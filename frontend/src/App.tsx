import React from "react";
import './App.css';
import {BrowserRouter, Route, Routes,} from "react-router-dom";
import RegistrationPage from "./pages/Registration";
import LoginPage from "./pages/Login";
import AppRoutes from "./constants/AppRoutes";
import MainPage from "./pages/Main";
import OnlyNotAuthenticatedRoute from "./components/Route/OnlyNotAuthenticatedRoute";
import {setUser, User} from "./features/user/userSlice";
import jwt_decode from "jwt-decode";
import {useAppDispatch} from "./app/hooks";
import {getAccessToken} from "./helpers/cookies";
import EmailConfirmationPage from "./pages/EmailConfirmation";
import PasswordResetPage from "./pages/PasswordReset";
import {ConfigProvider, Layout} from "antd";
import AppHeader from "./components/AppHeader";
import AppFooter from "./components/AppFooter";
import {AdminRoute, OnlyAuthenticatedRoute} from "./components/Route";
import UserPage from "./pages/User";
import UsersPage from "./pages/Users";
import AllTreesPage from "./pages/AllTrees";
import MyTreesPage from "./pages/MyTrees";

export default function App() {
  const dispatch = useAppDispatch();

  let ctAccessToken = getAccessToken();
  if (ctAccessToken) {
    dispatch(setUser(jwt_decode<User>(ctAccessToken)))
  }

  const { Content } = Layout;

  return (
      <ConfigProvider theme={{
        components: {
          Layout: {
            colorBgHeader: "rgb(245 245 245)"
          }
        }
      }}>
        <BrowserRouter>
          <Layout style={{height: "100vh"}}>
            <AppHeader />
            <Content>
              <Routes>
                <Route path={AppRoutes.LOGIN} element={<OnlyNotAuthenticatedRoute element={<LoginPage/>}/>}/>
                <Route path={AppRoutes.REGISTER} element={<OnlyNotAuthenticatedRoute element={<RegistrationPage/>}/>}/>
                <Route path={AppRoutes.PASSWORD_RESET} element={<OnlyNotAuthenticatedRoute element={<PasswordResetPage/>}/>}/>
                <Route path={AppRoutes.MAIN} element={<MainPage/>}/>
                <Route path={AppRoutes.USER_EMAIL_CONFIRMATION} element={<EmailConfirmationPage/>}/>
                <Route path={AppRoutes.NOT_FOUND} element={<div>404 page</div>}/> { /* TODO */ }
                <Route path={AppRoutes.MY_TREES} element={<OnlyAuthenticatedRoute element={<MyTreesPage/>}/>}/>
                <Route path={AppRoutes.ALL_TREES} element={<AdminRoute element={<AllTreesPage/>}/>}/>
                <Route path={AppRoutes.USERS} element={<AdminRoute element={<UsersPage/>}/>}/>
                <Route path={AppRoutes.USER} element={<OnlyAuthenticatedRoute element={<UserPage/>}/>}/>
              </Routes>
            </Content>
            <AppFooter />
          </Layout>
        </BrowserRouter>
      </ConfigProvider>
  );
}
