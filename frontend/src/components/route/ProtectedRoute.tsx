import {Navigate} from "react-router-dom";
import {useUser} from "../../app/hooks";
import React from "react";
import AppRoutes from "../../constants/AppRoutes";


const ProtectedRoute = ({element}: { element: any }) => {
  return !useUser() ? element : <Navigate to={AppRoutes.MAIN}/>;
}

export default ProtectedRoute;