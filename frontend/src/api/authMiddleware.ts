import {Middleware, ResponseContext} from "../generated/openapi";
import api from "./index";
import {getAccessToken} from "../helpers/cookies";

const authMiddleware: Middleware = {
    post(context: ResponseContext): Promise<Response | void> {
        if (context.response?.status === 401) {
            return api.auth
                .refreshTokenPair()
                .then(() => {
                    // @ts-ignore
                    (context.init.headers)["Authorization"] = `Bearer ${getAccessToken()}`
                    return context.fetch(context.url, context.init);
                })
        } else {
            return new Promise(() => context.response)
        }
    }
}

export default authMiddleware;