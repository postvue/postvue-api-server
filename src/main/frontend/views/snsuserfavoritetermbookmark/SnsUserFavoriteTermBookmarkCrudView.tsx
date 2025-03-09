import {
  SnsUserFavoriteTermBookmarkEndpoint
} from 'Frontend/generated/endpoints'

import {AutoCrud, AutoFormLayoutRendererProps} from '@vaadin/hilla-react-crud';
import SnsUserFavoriteTermBookmarkEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsUserFavoriteTermBookmarkEndpointDtoModel";
import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";
import {VerticalLayout} from "@vaadin/react-components/VerticalLayout";
import PostContentType from "Frontend/generated/com/postvue/feelogserver/domain/snsposts/vo/PostContentType";

export default function SnsUserFavoriteTermBookmarkCrudView() {
  function GroupingLayoutRenderer({ children }: AutoFormLayoutRendererProps<SnsUserFavoriteTermBookmarkEndpointDtoModel>) {

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
            {fieldsMapping.get('snsUser_id')}
            {fieldsMapping.get('favoriteTermName')}
            {fieldsMapping.get('favoriteTermContentType')}
            {fieldsMapping.get('favoriteTermContent')}
            {fieldsMapping.get('snsTagFollow_id')}
            {fieldsMapping.get('id')?.props.form.defaultValue.favoriteTermContentType === PostContentType.VIDEO
              &&
              <div>{fieldsMapping.get('id')?.props.form.defaultValue.favoriteTermContent}</div>
            }
            {fieldsMapping.get('id')?.props.form.defaultValue.favoriteTermContentType === PostContentType.IMAGE
              &&
              <img src={fieldsMapping.get('id')?.props.form.defaultValue.favoriteTermContent} style={{width:'100%',maxWidth:'250px',borderRadius:'5px'}}/>
            }
          </VerticalLayout>
        </VerticalLayout>
    );
  }
  return (
      <AutoCrud
          service={SnsUserFavoriteTermBookmarkEndpoint}
          model={SnsUserFavoriteTermBookmarkEndpointDtoModel}
          style={{height:"100%"}}
          formProps={{
            onDeleteError: handleOnDeleteError,
            onSubmitError:handleOnSubmitError,
            layoutRenderer:GroupingLayoutRenderer,
      }}
      />
  );
}
