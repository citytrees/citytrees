import {Col, Layout, Row} from "antd";
import React from "react";

// TODO
const CtFooter: React.FC = () => {
  const {Footer} = Layout;
  return (
      <Footer>
        <Row justify="space-between" align="middle">
          <Col>Social media links</Col>
          <Col>About us</Col>
          <Col>Contacts</Col>
        </Row>
      </Footer>
  )
};

export default CtFooter;
