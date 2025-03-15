import { useEffect, useState } from "react";
import { AuthEndpoint } from "Frontend/generated/endpoints";
import {getAccessTokenToLocalStorage, resetAccessTokenToLocalStorage} from "Frontend/global/util/CookieUtil";

export function useAuth() {
    const [user, setUser] = useState<{ id: number; email: string; role: string } | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = getAccessTokenToLocalStorage();
        if (!token) {
            setUser(null);
            setLoading(false);
            return;
        }

        // 🔹 `getUser()` 호출 시 토큰 직접 전달
        AuthEndpoint.getUser({ token })
            .then((data) => {
                if (!data || data.id === undefined || data.email === undefined || data.role === undefined) {
                    setUser(null);
                    return;
                }
                setUser({
                    id: data.id,
                    email: data.email,
                    role: data.role.toString(),
                });
            })
            .catch(() => {
                resetAccessTokenToLocalStorage();
                setUser(null);
            })
            .finally(() => setLoading(false));
    }, []);

    return { user, loading };
}
