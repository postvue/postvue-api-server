import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { AuthEndpoint } from "Frontend/generated/endpoints";
import styled from "styled-components";
import {setAccessTokenToLocalStorage} from "Frontend/global/util/CookieUtil";

export default function LoginView() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const handleLogin = async () => {
        try {
            const token = await AuthEndpoint.login({ email, password });

            // JWT 토큰을 localStorage에 저장
            setAccessTokenToLocalStorage(token);

            // 로그인 성공 시 /admin 페이지로 이동
            navigate("/admin");
        } catch (err) {
            console.error(err);
            alert(err)
            setError("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    };

    return (
        <LoginContainer>
            <LoginWrap>
                <LoginTitle>Feelog</LoginTitle>
                {error && <LoginErrorMsg>{error}</LoginErrorMsg>}
                <LoginFromWrap>
                    <LoginStyleInputWrap>
                    <LoginStyleInput
                        type="text"
                        placeholder="Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                    <LoginStyleInput
                        type="password"
                        placeholder="Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    </LoginStyleInputWrap>
                    <LoginStyleButton onClick={handleLogin}>Login</LoginStyleButton>
                </LoginFromWrap>
            </LoginWrap>
        </LoginContainer>
    );
}
const LoginContainer = styled.div`
    height:100%;
  display: flex;
`

const LoginWrap = styled.div`
  display: flex;
  flex-direction: column;
  margin: auto;
  border: 1px solid #ecebeb;
  padding: 20px;
  border-radius: 20px;
  width: 100%;
  max-width: 350px;
`

const LoginTitle = styled.div`
  font-size: 25px;
  text-align: center;
  font-family: system-ui;
  font-weight: 500;
`

const LoginStyleInputWrap = styled.div`
  gap: 15px;
  display: flex;
  flex-direction: column;
  color: #cecece;
  padding-bottom: 20px;
`

const LoginErrorMsg = styled.div`
  color: #ff2b2b;
`

const LoginStyleInput = styled.input`
  border: 0px;
  font-size: 20px;
  color: #a29d9d;
  font-family: system-ui;
  outline: none;
  font-weight: 300;

  &::placeholder {
    color: #cecece;
  }
`
const LoginStyleButton = styled.button`
  border: 0px;
  font-size: 18px;
  background-color: black;
  color: white;
  padding: 10px 20px;
  border-radius: 10px;
  width: 100%;
  justify-content: center;
  margin: 0 auto;
  display: flex;
  cursor: pointer;
`


const LoginFromWrap = styled.div`
    display: flex;   
    flex-direction: column;
      gap: 15px;
      padding-top: 20px;
`