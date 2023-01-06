import {useNavigate} from "react-router-dom";
import {useTranslation} from "react-i18next";
import {Button, Form, Input, notification} from "antd";
import CenteredContainer from "../../components/forms";
import api from "../../api";
import React from "react";
import AppRoutes from "../../constants/AppRoutes";
import {useAppDispatch} from "../../app/hooks";
import {setUser, User} from "../../features/user/userSlice";
import jwt_decode from "jwt-decode";
import {getAccessToken} from "../../helpers/cookies";

function LoginPage() {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();
  const {t} = useTranslation();
  const [form] = Form.useForm();
  const email = Form.useWatch('email', form);
  const password = Form.useWatch('password', form);

  return (
      <CenteredContainer>
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
        </Form>
      </CenteredContainer>
  )
}

export default LoginPage;