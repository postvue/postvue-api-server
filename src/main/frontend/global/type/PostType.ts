export interface AdminSnsPostComposeCreateReq {
    username:string;
    imageFilePathList:string[];
    title:string;
    bodyText:string;
    tagList:string[];
    address?: string;
    buildName?: string;
    latitude?:number;
    longitude?:number;
    targetAudienceValue:number;
}