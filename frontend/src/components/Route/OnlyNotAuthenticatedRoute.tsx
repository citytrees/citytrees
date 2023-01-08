import {Navigate} from "react-router-dom";
import {useUser} from "../../app/hooks";
import React from "react";
import AppRoutes from "../../constants/AppRoutes";

const OnlyNotAuthenticatedRoute = ({element}: { element: any }) => {
  return !useUser() ? element : <Navigate to={AppRoutes.MAIN}/>;
}

export default OnlyNotAuthenticatedRoute;
