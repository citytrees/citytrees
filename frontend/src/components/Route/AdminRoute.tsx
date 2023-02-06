import {Navigate} from "react-router-dom";
import {useUser} from "../../app/hooks";
import React from "react";
import AppRoutes from "../../constants/AppRoutes";
import {isUserAdmin} from "../../features/user/userSlice";


const AdminRoute = ({element}: { element: any }) => {
  const user = useUser();
  return user && isUserAdmin(user) ? element : <Navigate to={AppRoutes.NOT_FOUND}/>;
}

export default AdminRoute;
