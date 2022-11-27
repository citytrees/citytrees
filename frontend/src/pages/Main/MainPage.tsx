import CenteredContainer from "../../components/forms";
import React from "react";
import {useUser} from "../../app/hooks";


function MainPage() {
  const user = useUser();

  return (
      <CenteredContainer>
        <div>Hello {user?.email}</div>
      </CenteredContainer>
  )
}

export default MainPage;