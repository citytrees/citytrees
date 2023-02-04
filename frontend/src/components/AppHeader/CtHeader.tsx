import React from "react";
import {Avatar, Button, Tabs} from "antd-mobile";
import {useTranslation} from "react-i18next";
import {useAppDispatch, useUser} from "../../app/hooks";
import {Link, useLocation, useNavigate} from "react-router-dom";
import AppRoutes from "../../constants/AppRoutes";
import {UserRole} from "../../generated/openapi";
import {setUser, User} from "../../features/user/userSlice";
import {removeAccessToken} from "../../helpers/cookies";
import {MenuProps} from "antd";

const CtHeader: React.FC = () => {
  const {t} = useTranslation()
  const user = useUser();
  const navigate = useNavigate()
  const location = useLocation()
  const dispatch = useAppDispatch()

  const tabs: any[] = [
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

    return (
        <Link to={AppRoutes.USER}>
          <Avatar src="sss"/> {/* TODO user image */}
        </Link>
    )
  }

  return (
      <div style={{display: "flex"}}>
        <Tabs style={{flex: "1"}} activeKey={location.pathname}
              onChange={(activeKey) => {
                navigate(activeKey)
              }}>
          {tabs.map((tab) => <Tabs.Tab title={tab.label} key={tab.key}></Tabs.Tab>)}
        </Tabs>
        {user !== null
            ? renderUserBlock(user)
            : <Button onClick={() => navigate(AppRoutes.LOGIN)}>{t('appHeader.buttons.signIn')}</Button>}
      </div>
  )
};

export default CtHeader;
