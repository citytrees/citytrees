import React, {useEffect, useState} from "react";
import 'leaflet/dist/leaflet.css';
import CenteredContainer from "../../components/forms";
import {useSearchParams} from "react-router-dom";
import api from "../../api";

enum ConfirmationStatus {
  WAITING,
  CONFIRMED,
  ERROR
}

function EmailConfirmationPage() {
  const [searchParams] = useSearchParams();
  const [confirmationStatus, setConfirmationStatus] = useState(ConfirmationStatus.WAITING);

  const content = () => {
    if (confirmationStatus === ConfirmationStatus.WAITING)
      return <p>WAITING</p>
    else if (confirmationStatus === ConfirmationStatus.CONFIRMED)
      return <p>CONFIRMED</p>
    else
      return <p>ERROR</p>
  }

  useEffect(() => {
    const userId = searchParams.get("userId")
    const confirmationId = searchParams.get("confirmationId")

    if (userId !== null && confirmationId !== null) {
      api.user.confirmUserEmail({userEmailConfirmRequest: {userId: userId, confirmationId: confirmationId}})
          .then(() => setConfirmationStatus(ConfirmationStatus.CONFIRMED))
          .catch(() => setConfirmationStatus(ConfirmationStatus.ERROR))
    } else {
      setConfirmationStatus(ConfirmationStatus.ERROR)
    }
  }, [searchParams])

  return (
      <div>
        <CenteredContainer>
          {content()}
        </CenteredContainer>
      </div>
  )
}

export default EmailConfirmationPage;