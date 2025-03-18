import axios, { AxiosError } from 'axios';
import {
    STATUS_BAD_REQUEST_CODE,
    STATUS_UNAUTHORIZED_CODE,
    UNAUTHORIZED_NAME,
} from 'Frontend/const/HttpStatusConst';
import {LOGIN_PAGE_ROUTE_PATH} from "Frontend/const/PathConst";
import * as QueryString from "qs";
import {SERVER_API_PATH} from "Frontend/const/SystemAttrConst";
import {ACCESS_TOKEN, INVALID_ACCESS_TOKEN} from "Frontend/const/LocalStorageConst";
import {getAccessTokenByBearer} from "Frontend/global/util/AuthUtil";
import {TOKEN_EXPIRED_SPECIFICATION} from "Frontend/const/SystemSpecificationConst";
import {postRefreshToken} from "Frontend/services/auth/postRefreshToken";

export const api = axios.create({
    // baseURL: SERVER_API_PATH,
    headers: {
        'Content-Type': 'application/json; charset=UTF-8',
        Accept: 'application/json',
    },
    withCredentials: true,
    paramsSerializer: (params) => {
        return QueryString.stringify(params, { encode: true });
    },
});

export const refreshApi = axios.create({
    baseURL: SERVER_API_PATH,
    headers: {
        'Content-Type': 'application/json; charset=UTF-8',
        Accept: 'application/json',
    },
    withCredentials: true,
});

// token API
export const privateApi = axios.create({
    baseURL: SERVER_API_PATH,
    headers: {
        'Content-Type': 'application/json; charset=UTF-8',
        Accept: 'application/json',
    },
    withCredentials: true,
    paramsSerializer: (params) => {
        return QueryString.stringify(params, { encode: true });
    },
});

// form API
export const formApi = axios.create({
    // baseURL: SERVER_API_PATH,
    headers: {
        "Content-Type": "multipart/form-data; charset=utf-8",
    },
    withCredentials: true,
});

privateApi.interceptors.request.use((config) => {
    if (!config.headers) return config;

    const accessToken =
        localStorage.getItem(ACCESS_TOKEN) || INVALID_ACCESS_TOKEN;

    config.headers.authorization = getAccessTokenByBearer(accessToken);

    return config;
});

privateApi.interceptors.response.use(
    // 200번대 응답이 올때 처리
    (response) => {
        return response;
    },

    interceptorErrorFunc,
);

formApi.interceptors.request.use((config) => {
    if (!config.headers) return config;

    const accessToken =
        localStorage.getItem(ACCESS_TOKEN) || INVALID_ACCESS_TOKEN;

    config.headers.authorization = getAccessTokenByBearer(accessToken);

    return config;
});

formApi.interceptors.response.use(
    // 200번대 응답이 올때 처리
    (response) => {
        return response;
    },

    interceptorErrorFunc,
);

// token API
export const optAuthApi = axios.create({
    baseURL: SERVER_API_PATH,
    headers: {
        'Content-Type': 'application/json; charset=UTF-8',
        Accept: 'application/json',
    },
    withCredentials: true,
    paramsSerializer: (params) => {
        return QueryString.stringify(params, { encode: true });
    },
});

optAuthApi.interceptors.request.use((config) => {
    if (!config.headers) return config;

    const accessToken = localStorage.getItem(ACCESS_TOKEN);

    if (accessToken) {
        config.headers.authorization = getAccessTokenByBearer(accessToken);
    }

    return config;
});

optAuthApi.interceptors.response.use(
    // 200번대 응답이 올때 처리
    (response) => {
        return response;
    },
    interceptorErrorFunc,
);

// 에러시, 싪행하는 코드: 400번, 500번
async function interceptorErrorFunc(error: any) {
    const { config } = error;
    const status = error.response?.status; // 안전한 구조 분해 할당

    //토큰이 만료되을 때, 인증 오류, 401번 에러
    if (status === STATUS_UNAUTHORIZED_CODE) {
        if (
            error.response.data.statusCode === UNAUTHORIZED_NAME &&
            error.response.data.message === TOKEN_EXPIRED_SPECIFICATION
        ) {
            const originRequest = config;

            if (status !== STATUS_UNAUTHORIZED_CODE || config.sent) {
                return Promise.reject(error);
            }

            config.sent = 0;


            //리프레시 토큰 api
            const originResponse = await postRefreshToken()
                .then((response) => {
                    //리프레시 토큰 요청이 성공할 때, 200번 때

                    const newAccessToken = response.accessToken;

                    if (originRequest === undefined || originRequest === null){
                        return
                    }
                    //진행중이던 요청 이어서하기
                    originRequest.headers.authorization =
                        getAccessTokenByBearer(newAccessToken);
                    return axios(originRequest);
                })
                .catch((err: AxiosError) => {
                    //리프레시 토큰 요청이 실패할 때

                    if (
                        err.response?.status === STATUS_UNAUTHORIZED_CODE ||
                        err.response?.status === STATUS_BAD_REQUEST_CODE
                    ) {
                        // 인증 오류 시, 리프레쉬 토큰 만료시 OR 리프레시 토큰이 없을 때
                        const currentPath = window.location.pathname;
                        localStorage.setItem(ACCESS_TOKEN, '');

                        originRequest.headers.authorization = '';
                        window.location.replace(
                          LOGIN_PAGE_ROUTE_PATH
                        );
                        return;

                    } else {
                        // 나머지 오류 일때,
                        return Promise.reject(err);
                    }
                });

            return originResponse;
        } else {
            return Promise.reject(error);
        }
    } else {
        return Promise.reject(error);
    }
}