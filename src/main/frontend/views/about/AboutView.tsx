import {TextField} from "@vaadin/react-components/TextField";
import {VerticalLayout} from "@vaadin/react-components/VerticalLayout";
import styled from "styled-components";

export default function AboutView() {
  return (
      <section className="flex p-m gap-m items-end">
          <VerticalLayout>
              <h2>Feelog</h2>
              <ServiceAddress href={'https://www.feelog.net'}>Feelog 서비스 주소</ServiceAddress>
              <ServiceDesc>Feelog는 사용자가 특정 위치에서 촬영한 사진을 저장하고 공유할 수 있는 지도 기반 사진 공유 플랫폼입니다.
              </ServiceDesc>
              <ServiceDesc>자신만의 감성을 자유롭게 표현하고, 같은 장소에서 다양한 사람들의 사진을 감상할 수 있는 새로운 방식의 소셜미디어입니다.
              </ServiceDesc>
          </VerticalLayout>
      </section>
  );
}

const ServiceAddress = styled.a`
    margin-top:10px;
    margin-bottom:20px;
`
const ServiceDesc = styled.div`
  font-size: 20px;
  font-family: system-ui;
  font-weight: 300;
`
