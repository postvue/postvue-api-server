import {formApi} from "Frontend/services";
import {ADMIN_POST_COMPOSE_LIST_API_PATH} from "Frontend/services/appApiPath";
import axios from "axios";

export const createPostCompose = (formData: FormData): Promise<boolean> => {
    console.log(formDate);
    return formApi
        .post(`${ADMIN_POST_COMPOSE_LIST_API_PATH}`, formData)
        .then((res) => {
            console.log(data)
            const data:boolean = res.data.data;
            return data;
        })
        .catch((error) => {
            throw error;
        });
};
