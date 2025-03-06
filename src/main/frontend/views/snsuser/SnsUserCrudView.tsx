
import {AutoCrud, AutoFormLayoutRendererProps} from '@vaadin/hilla-react-crud';
import {SnsUserEndpoint} from "Frontend/generated/endpoints";
import SnsUserEndpointDtoModel from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsUserEndpointDtoModel";
import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";
import {VerticalLayout} from "@vaadin/react-components/VerticalLayout";
import styled from "styled-components";
import {Notification} from "@vaadin/react-components/Notification";

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
                <FiledWrap>
                    <FiledTitleName>username</FiledTitleName>
                    <FiledName>{fieldsMapping.get('id')?.props.form.defaultValue.username}</FiledName>
                </FiledWrap>
                {fieldsMapping.get('nickname')}
                {fieldsMapping.get('profilePath')}
                <img src={fieldsMapping.get('id')?.props.form.defaultValue.profilePath} style={{width:'100%',maxWidth:'300px',borderRadius:'5px'}}/>
                {fieldsMapping.get('email')}
                {fieldsMapping.get('userLink')}
                {fieldsMapping.get('userDescription')}
                {fieldsMapping.get('snsUserGender')}
                {fieldsMapping.get('birthDate')}
                {fieldsMapping.get('snsUserState')}
                {fieldsMapping.get('snsAppRole')}
                {fieldsMapping.get('isPrivateProfile')}
                {fieldsMapping.get('hasFollowerNotification')}
                {fieldsMapping.get('createdAt')}
                {fieldsMapping.get('lastUpdatedAt')}
                {fieldsMapping.get('lastUpdatedByid')}
                <FiledWrap>
                    <FiledTitleName>deletedAt</FiledTitleName>
                    <FiledName>{fieldsMapping.get('id')?.props.form.defaultValue.deletedAt}</FiledName>
                </FiledWrap>
            </VerticalLayout>
        </VerticalLayout>
    );

}
    const FiledWrap = styled.div`
      padding: 10px 0 0 0;
    `
    const FiledTitleName = styled.div`
        font-family: "Al Bayan";
        font-weight: 600;
        font-size: 18px;
    `
    const FiledName = styled.div`
        font-family: "Al Bayan";
        font-weight: 500;
        
        font-size: 17px;
    `
  return (
      <AutoCrud
          service={SnsUserEndpoint}
          style={{height:"100%"}}
          model={SnsUserEndpointDtoModel}
          formProps={
            {
            onDeleteError: handleOnDeleteError,
            onSubmitError: (error) => {
                const json = JSON.stringify(error);
                // const notification = new Notification();
                // notification.duration = 0 ;
                // notification.position = "top-end";
                Notification.show(`Error while deleting: ${json}`,{
                    duration:1000 * 15
                });
            },

            layoutRenderer:GroupingLayoutRenderer,
            }
          }
      />
  );
}
