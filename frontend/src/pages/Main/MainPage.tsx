import CenteredContainer from "../../components/forms";
import React from "react";
import {useAppDispatch, useUser} from "../../app/hooks";
import {Button} from "antd";
import api from "../../api";
import AppRoutes from "../../constants/AppRoutes";
import {useNavigate} from "react-router-dom";
import {setUser} from "../../features/user/userSlice";


function MainPage() {
  const user = useUser();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();

  return (
      <CenteredContainer>
        <div>Hello {user?.email}</div>
        <Button
            onClick={() => {
              api.auth.handleLogout()
                  .then(() => {
                        dispatch(setUser(null))
                        navigate(AppRoutes.LOGIN)
                      }
                  )
            }}
        >Logout
        </Button>
      </CenteredContainer>
  )
}

export default MainPage;