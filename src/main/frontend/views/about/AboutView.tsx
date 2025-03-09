import {TextField} from "@vaadin/react-components/TextField";
import {VerticalLayout} from "@vaadin/react-components/VerticalLayout";
import styled from "styled-components";

export default function AboutView() {
  return (
      <section className="flex p-m gap-m items-end">
          <VerticalLayout>
              <h2>Feelog 어드민</h2>
              <ServiceAddress href={'https://www.feelog.net'}>Feelog 서비스 주소</ServiceAddress>
          </VerticalLayout>
      </section>
  );
}

const ServiceAddress = styled.a`
    margin-top:10px;
`
