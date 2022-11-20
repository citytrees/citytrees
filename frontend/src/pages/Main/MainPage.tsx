import CenteredContainer from "../../components/forms";
import React from "react";
import Cookies from "js-cookie";
import jwt_decode from 'jwt-decode'


function MainPage() {
  return (
      <CenteredContainer>
        <div>Hello {jwt_decode<any>(Cookies.get("ct_access_token") as string)["email"]}</div>
      </CenteredContainer>
  )
}

export default MainPage;