import {Navigate} from "react-router-dom";
import {useUser} from "../../app/hooks";
import React from "react";
import AppRoutes from "../../constants/AppRoutes";
import {UserRole} from "../../generated/openapi";


const AdminRoute = ({element}: { element: any }) => {
  const user = useUser();
  return user && user.roles.indexOf(UserRole.Admin) !== -1 ? element : <Navigate to={AppRoutes.NOT_FOUND}/>;
}

export default AdminRoute;
