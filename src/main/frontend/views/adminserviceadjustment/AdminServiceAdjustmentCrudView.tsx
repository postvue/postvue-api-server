import {AdminServiceAdjustmentEndpoint} from 'Frontend/generated/endpoints'

import {AutoCrud, AutoFormLayoutRendererProps} from '@vaadin/hilla-react-crud';

import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";

import 'swiper/css';
import 'swiper/css/pagination';
import AdminServiceAdjustmentEndpointDtoModel
    from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/AdminServiceAdjustmentEndpointDtoModel";
import SnsTagEndpointDtoModel from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsTagEndpointDtoModel";
import {VerticalLayout} from "@vaadin/react-components/VerticalLayout";
import PostContentType from "Frontend/generated/com/postvue/feelogserver/domain/snsposts/vo/PostContentType";

export default function AdminServiceAdjustmentCrudView() {
    function GroupingLayoutRenderer({ children }: AutoFormLayoutRendererProps<AdminServiceAdjustmentEndpointDtoModel>) {

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
                    {fieldsMapping.get('serviceType')}
                    {fieldsMapping.get('propLong1id')}
                    {fieldsMapping.get('propLong2id')}
                    {fieldsMapping.get('propLong3id')}
                    {fieldsMapping.get('propLong4id')}
                    {fieldsMapping.get('propString1')}
                    {fieldsMapping.get('propString2')}
                    {fieldsMapping.get('propString3')}
                    {fieldsMapping.get('propString4')}
                    {fieldsMapping.get('createdAt')}
                    {fieldsMapping.get('lastUpdatedAt')}
                    {fieldsMapping.get('lastUpdatedBy')}
                </VerticalLayout>
            </VerticalLayout>
        );
    }

    return (
      <>
      <AutoCrud
          service={AdminServiceAdjustmentEndpoint}
          model={AdminServiceAdjustmentEndpointDtoModel}
          style={{height:"100%"}}
          formProps={{
              layoutRenderer: GroupingLayoutRenderer,
              onDeleteError: handleOnDeleteError,
              onSubmitError:handleOnSubmitError,
          }}/>
      </>
  );
}