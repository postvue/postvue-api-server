
import {AutoCrud, AutoFormLayoutRendererProps} from '@vaadin/hilla-react-crud';
import {SnsUserEndpoint} from "Frontend/generated/endpoints";
import SnsUserEndpointDtoModel from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsUserEndpointDtoModel";
import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";
import {VerticalLayout} from "@vaadin/react-components/VerticalLayout";

export default function SnsUserCrudView() {
function GroupingLayoutRenderer({ children }: AutoFormLayoutRendererProps<SnsUserEndpointDtoModel>) {

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
                {fieldsMapping.get('nickname')}
                {fieldsMapping.get('email')}
                {fieldsMapping.get('userLink')}
                {fieldsMapping.get('userDescription')}
                {fieldsMapping.get('snsUserGender')}
                {fieldsMapping.get('birthDate')}
                {fieldsMapping.get('snsUserState')}
                {fieldsMapping.get('snsAppRole')}
                {fieldsMapping.get('isPrivateProfile')}
                {fieldsMapping.get('profilePath')}
                {fieldsMapping.get('deletedAt')}
                {fieldsMapping.get('hasFollowerNotification')}
                <img src={fieldsMapping.get('id')?.props.form.defaultValue.profilePath} style={{width:'100%',maxWidth:'400px',borderRadius:'5px'}}/>
            </VerticalLayout>
        </VerticalLayout>
    );
}
  return (
      <AutoCrud service={SnsUserEndpoint} model={SnsUserEndpointDtoModel} formProps={
        {
        onDeleteError: handleOnDeleteError,
        onSubmitError:handleOnSubmitError,
        layoutRenderer:GroupingLayoutRenderer,
        }
      } />
  );
}
