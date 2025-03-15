import {BEARER_AUTH_KEY} from "Frontend/const/AuthConst";


export const getAccessTokenByBearer = (accessToken: string): string => {
    return `${BEARER_AUTH_KEY} ${accessToken}`;
};