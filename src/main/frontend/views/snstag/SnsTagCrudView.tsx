import {SnsTagEndpoint} from 'Frontend/generated/endpoints'

import {AutoCrud, type AutoFormLayoutRendererProps} from '@vaadin/hilla-react-crud';
import {VerticalLayout} from '@vaadin/react-components/VerticalLayout.js';
import SnsTagEndpointDtoModel from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsTagEndpointDtoModel";
import {Notification} from "@vaadin/react-components/Notification";
import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";
import PostContentType from "Frontend/generated/com/postvue/feelogserver/domain/snsposts/vo/PostContentType";

export default function SnsTagCrudView() {
  function GroupingLayoutRenderer({ children }: AutoFormLayoutRendererProps<SnsTagEndpointDtoModel>) {

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
            {fieldsMapping.get('tagName')}
            {fieldsMapping.get('tagRepsBatchContentType')}
            {fieldsMapping.get('isExposed')}
            {fieldsMapping.get('createdAt')}
            {fieldsMapping.get('tagRepsBatchContent')}
            {fieldsMapping.get('id')?.props.form.defaultValue.tagRepsBatchContentType === PostContentType.VIDEO
              &&
              <div>{fieldsMapping.get('id')?.props.form.defaultValue.tagRepsBatchContent}</div>
            }
            {fieldsMapping.get('id')?.props.form.defaultValue.tagRepsBatchContentType === PostContentType.IMAGE
              &&
              <img src={fieldsMapping.get('id')?.props.form.defaultValue.tagRepsBatchContent} style={{width:'100%',maxWidth:'400px',borderRadius:'5px'}}/>
            }
          </VerticalLayout>
        </VerticalLayout>
    );
  }

  return (
      <>
      <AutoCrud service={SnsTagEndpoint} model={SnsTagEndpointDtoModel} formProps={
        {layoutRenderer: GroupingLayoutRenderer, onDeleteError: handleOnDeleteError,onSubmitError:handleOnSubmitError}
      }
      />
      </>
  );
}
