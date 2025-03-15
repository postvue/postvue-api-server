import React from "react";
import styled from "styled-components";

interface ModalProps {
    children: React.ReactNode;
    onClose: () => void;
    ModalContainerStyle?:React.CSSProperties;
    ModalSubContainerStyle?:React.CSSProperties;
    ModelOverlayStyle?:React.CSSProperties;
}

const Modal: React.FC<ModalProps> = ({ children, onClose, ModelOverlayStyle,ModalSubContainerStyle, ModalContainerStyle}) => {
    return (
        <Overlay onClick={onClose} style={ModelOverlayStyle}>
            <ModalMainContainer style={ModalContainerStyle}>
                <ModalContainer style={ModalSubContainerStyle} onClick={(e) => e.stopPropagation()}> {/* 클릭 이벤트 전파 방지 */}
                    <ModalSubContainer>
                    <CloseButton onClick={onClose}>×</CloseButton>
                    {children}
                    </ModalSubContainer>
                </ModalContainer>
            </ModalMainContainer>
        </Overlay>
    );
};

export default Modal;

// ✅ 스타일 정의
const Overlay = styled.div`
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
`;


const ModalMainContainer = styled.div`
    max-width: 600px;
`;

const ModalContainer = styled.div`
    background: white;
    height: 100dvh;
    @media (min-width: 1200px) {
      height: 90dvh;
      border-radius: 8px;
    }
    overflow: scroll;
    width: 100%;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
    position: relative;
`;

const ModalSubContainer = styled.div`
   padding: 20px;
`;

const CloseButton = styled.button`
    position: absolute;
    top: 10px;
    right: 20px;
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
`;
