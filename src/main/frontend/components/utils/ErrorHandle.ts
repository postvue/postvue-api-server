import {Notification} from "@vaadin/react-components/Notification";

export const handleOnSubmitError = ({ error }: { error: unknown }) => {
    const json = JSON.stringify(error);
    Notification.show(`Error while submitting: ${json}`);
};

export const handleOnDeleteError = ({ error }: { error: unknown }) => {
    const json = JSON.stringify(error);
    Notification.show(`Error while deleting: ${json}`);
};