import React from "react";
import {Avatar, Button, Col, Dropdown, Layout, MenuProps, Row, Tabs, TabsProps, Typography} from "antd";
import {useTranslation} from "react-i18next";
import Logo from "../Logo";
import {useAppDispatch, useUser} from "../../app/hooks";
import {Link, useLocation, useNavigate} from "react-router-dom";
import AppRoutes from "../../constants/AppRoutes";
import {UserOutlined} from "@ant-design/icons";
import {UserRole} from "../../generated/openapi";
import {setUser, User} from "../../features/user/userSlice";
import {removeAccessToken} from "../../helpers/cookies";

const CtHeader: React.FC = () => {
  const {t} = useTranslation()
  const user = useUser();
  const navigate = useNavigate()
  const location = useLocation()
  const dispatch = useAppDispatch()

  const tabs: TabsProps["items"] = [
    {
      label: t("appHeader.menuItems.map"),
      key: AppRoutes.MAIN
    },
  ];

  if (user) {
    tabs.push({
      label: t("appHeader.menuItems.myTrees"),
      key: AppRoutes.MY_TREES
    })
    if (user.roles.indexOf(UserRole.Admin) !== -1) {
      tabs.push({
            label: t("appHeader.menuItems.allTrees"),
            key: AppRoutes.ALL_TREES
          },
          {
            label: t("appHeader.menuItems.users"),
            key: AppRoutes.USERS
          },
          {
            label: t("appHeader.menuItems.woodTypes"),
            key: AppRoutes.WOOD_TYPE
          })
    }
  }

  const handleLogout = () => {
    removeAccessToken()
    dispatch(setUser(null))
    navigate(AppRoutes.MAIN)
  };

  const renderUserBlock = (user: User) => {
    let nameText;
    if (user.firstName !== null && user.firstName.length > 0) {
      nameText = user.firstName;
      if (user.lastName !== null && user.lastName.length > 0) {
        nameText = nameText + " " + user.lastName
      }
    } else {
      nameText = user.email;
    }

    const items: MenuProps["items"] = [
      {
        label: t("appHeader.buttons.logout"),
        key: "logout",
        onClick: () => handleLogout()
      },
    ]

    const {Text} = Typography;
    return (
        <Link to={AppRoutes.USER}>
          <Avatar icon={<UserOutlined/>}/> {/* TODO user image */}
          <Dropdown menu={{items}}>
            <Text>{nameText}</Text>
          </Dropdown>
        </Link>
    )
  }

  const {Header} = Layout;

  return (
      <Header>
        <Row justify="space-between" align="middle">
          <Col>
            <Logo/>
          </Col>
          <Col>
            <Tabs activeKey={location.pathname}
                  onChange={(activeKey) => {
                    navigate(activeKey)
                  }}
                  items={tabs}
            />
          </Col>
          <Col>
            {user !== null
                ? renderUserBlock(user)
                : <Button onClick={() => navigate(AppRoutes.LOGIN)}>{t('appHeader.buttons.signIn')}</Button>}
          </Col>
        </Row>
      </Header>
  )
};

export default CtHeader;
