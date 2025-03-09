import {
 SnsUserMessageEndpoint
} from 'Frontend/generated/endpoints'

import {AutoCrud, AutoFormLayoutRendererProps} from '@vaadin/hilla-react-crud';
import SnsUserMessageEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsUserMessageEndpointDtoModel";
import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";
import SnsTagEndpointDtoModel from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsTagEndpointDtoModel";
import {VerticalLayout} from "@vaadin/react-components/VerticalLayout";
import SnsMsgType from "Frontend/generated/com/postvue/feelogserver/domain/snsusermessages/vo/SnsMsgType";

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
                {fieldsMapping.get('sourceUser_id')}
                {fieldsMapping.get('snsUserMessageRoom_id')}
                {fieldsMapping.get('msgType')}
                {fieldsMapping.get('msgContent')}
                {fieldsMapping.get('id')?.props.form.defaultValue.msgType === SnsMsgType.EMOTICON
                    &&
                    <div>{fieldsMapping.get('id')?.props.form.defaultValue.msgContent}</div>
                }
                {fieldsMapping.get('id')?.props.form.defaultValue.msgType === SnsMsgType.IMAGE
                    &&
                    <img src={fieldsMapping.get('id')?.props.form.defaultValue.msgContent} style={{width:'100%',maxWidth:'400px',borderRadius:'5px'}}/>
                }
            </VerticalLayout>
        </VerticalLayout>
    );
}
  return (
      <AutoCrud
          service={SnsUserMessageEndpoint}
          model={SnsUserMessageEndpointDtoModel}
          formProps={{
              onDeleteError: handleOnDeleteError,
              onSubmitError:handleOnSubmitError,
              layoutRenderer:GroupingLayoutRenderer,
      }}/>
  );
}
