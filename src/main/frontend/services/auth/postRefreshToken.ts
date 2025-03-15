import {refreshApi} from "Frontend/services";
import {AUTH_RENEWAL_TOKEN_API_PATH} from "Frontend/services/appApiPath";
import {setAccessTokenToLocalStorage} from "Frontend/global/util/CookieUtil";

export interface AuthTokenRsp {
    accessToken: string;
    refreshToken: string;
    userId: string;
}

export const postRefreshToken = (): Promise<AuthTokenRsp> => {
  return refreshApi
    .post(AUTH_RENEWAL_TOKEN_API_PATH)
    .then((res) => {
      // localStorage에 저장
      const authToken: AuthTokenRsp = res.data.data;
      setAccessTokenToLocalStorage(authToken.accessToken);

      return authToken;
    })
    .catch((error) => {
      throw error;
    });
};
