import {
 SnsUserMessageEndpoint
} from 'Frontend/generated/endpoints'

import {AutoCrud, AutoFormLayoutRendererProps} from '@vaadin/hilla-react-crud';
import SnsUserMessageEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsUserMessageEndpointDtoModel";
import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";
import {VerticalLayout} from "@vaadin/react-components/VerticalLayout";

export default function SnsUserMessageCrudView() {
function GroupingLayoutRenderer({ children }: AutoFormLayoutRendererProps<SnsUserMessageEndpointDtoModel>) {

    const fieldsMapping = new Map<string, JSX.Element>();
    children.forEach((field) => {
        const name = field.props?.propertyInfo?.name;
        if(!name) return;
        fieldsMapping.set(name, field)
    });

    return (
        <VerticalLayout>
            <h4>Personal Information:</h4>
            <VerticalLayout theme="spacing" className="pb-l">
                {fieldsMapping.get("msgTextContent")}
                {fieldsMapping.get("msgMediaType")}
                {fieldsMapping.get("msgMediaContent")}
                {fieldsMapping.get("createdAt")}
            </VerticalLayout>
        </VerticalLayout>
    );
}
  return (
      <AutoCrud
          service={SnsUserMessageEndpoint}
          model={SnsUserMessageEndpointDtoModel}
          style={{height:"100%"}}
          formProps={{
              onDeleteError: handleOnDeleteError,
              onSubmitError:handleOnSubmitError,
              layoutRenderer:GroupingLayoutRenderer,
      }}/>
  );
}
