import { Navigate, Outlet } from "react-router-dom";
import {useAuth} from "Frontend/hooks/useAuth";
import styled from "styled-components";
import {LOGIN_PAGE_ROUTE_PATH} from "Frontend/const/PathConst";

export default function ProtectedRoute() {
    const { user, loading } = useAuth();

    if (loading) return <StyleLoadingWrap><StyleLoading>Loading...</StyleLoading></StyleLoadingWrap>;

    return user ? <Outlet /> : <Navigate to={LOGIN_PAGE_ROUTE_PATH} replace />;
}

const StyleLoadingWrap = styled.div`
    display: flex;
 width:100%;
  height:100%;
  
`;

const StyleLoading = styled.div`
    display: flex;
  margin: auto;
  
`;