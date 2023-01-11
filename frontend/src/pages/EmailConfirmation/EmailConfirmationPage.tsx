import React, {useEffect, useState} from "react";
import 'leaflet/dist/leaflet.css';
import {useSearchParams} from "react-router-dom";
import api from "../../api";
import {Col, Row} from "antd";

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
        <Row style={{minHeight: "100%"}} justify="center" align="middle" >
          <Col xs={{span: 16}} lg={{span: 6}}>
            {content()}
          </Col>
        </Row>
      </div>
  )
}

export default EmailConfirmationPage;
