import {useNavigate} from "react-router-dom";
import {useTranslation} from "react-i18next";
import {Button, Col, Form, Input, notification, Row} from "antd";
import api from "../../api";
import React, {useEffect, useState} from "react";
import AppRoutes from "../../constants/AppRoutes";
import {useAppDispatch} from "../../app/hooks";
import {setUser, User} from "../../features/user/userSlice";
import jwt_decode from "jwt-decode";
import {getAccessToken} from "../../helpers/cookies";
import {AuthGetAllProviderResponseItem} from "../../generated/openapi";

function LoginPage() {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();
  const {t} = useTranslation();
  const [form] = Form.useForm();
  const email = Form.useWatch('email', form);
  const password = Form.useWatch('password', form);

  const [oauthProviders, setOauthProviders] = useState<AuthGetAllProviderResponseItem[]>([])

  useEffect(() => {
    api.auth.getAllProviders().then((response) => setOauthProviders(response.items))
  }, [])

  return (
      <Row style={{minHeight: "100%"}} justify="center" align="middle">
        <Col xs={{span: 16}} lg={{span: 6}}>
          <Form
              form={form}
              name="basic"
              layout="vertical"
              autoComplete="off"
          >
            <Form.Item
                label={t('signInPage.email.label')}
                name="email"
                rules={[
                  {
                    type: 'email',
                    message: t<string>('signInPage.email.notValidError'),
                  },
                  {
                    required: true,
                    message: t<string>('signInPage.email.emptyError'),
                  },
                ]}
            >
              <Input/>
            </Form.Item>

            <Form.Item
                label={t('signInPage.password.label')}
                name="password"
                rules={[{required: true, message: t<string>('signInPage.password.emptyError')}]}
            >
              <Input.Password/>
            </Form.Item>

            <Form.Item>
              <Button
                  type="primary"
                  htmlType="submit"
                  onClick={() =>
                      api.auth.handleBasicAuth({authorization: `Basic ${btoa(`${email}:${password}`)}`})
                          .then(() => {
                            let accessToken = getAccessToken();
                            if (accessToken) {
                              dispatch(setUser(jwt_decode<User>(accessToken)))
                              navigate(AppRoutes.MAIN)
                            }
                          })
                          .catch(error => {
                            error.response.json()
                                .then(() => {
                                  notification.open({
                                    message: t<string>('signInPage.signInError'),
                                    type: "error",
                                    placement: "top"
                                  })
                                })
                          })
                  }
              >
                {t('signInPage.submit')}
              </Button>
              <Button
                  type="link"
                  onClick={() => navigate(AppRoutes.REGISTER)}
              >
                {t('signInPage.register.label')}
              </Button>
              <Button
                  type="link"
                  onClick={() => navigate(AppRoutes.PASSWORD_RESET)}
              >
                {t('signInPage.password.reset.label')}
              </Button>
            </Form.Item>
            <Form.Item>
              {oauthProviders.map((provider) => (
                  <Button
                      onClick={() => api.auth.handle0Auth2({providerId: provider.id})}
                  >
                    {provider.id}
                  </Button>
              ))}
            </Form.Item>
          </Form>
        </Col>
      </Row>
  )
}

export default LoginPage;
