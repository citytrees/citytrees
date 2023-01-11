import {Navigate} from "react-router-dom";
import {useUser} from "../../app/hooks";
import React from "react";
import AppRoutes from "../../constants/AppRoutes";

const OnlyAuthenticatedRoute = ({element}: { element: any }) => {
  return useUser() !== null ? element : <Navigate to={AppRoutes.LOGIN}/>;
}

export default OnlyAuthenticatedRoute;
