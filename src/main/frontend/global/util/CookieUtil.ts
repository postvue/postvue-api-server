import {ACCESS_TOKEN, INVALID_ACCESS_TOKEN} from "Frontend/const/LocalStorageConst";


export const getAccessTokenToLocalStorage = (): string => {
    return localStorage.getItem(ACCESS_TOKEN) || INVALID_ACCESS_TOKEN;
};

export const setAccessTokenToLocalStorage = (accessToken: string): void => {
    localStorage.setItem(ACCESS_TOKEN, accessToken);
};

export const resetAccessTokenToLocalStorage = (): void => {
    localStorage.removeItem(ACCESS_TOKEN);
};
