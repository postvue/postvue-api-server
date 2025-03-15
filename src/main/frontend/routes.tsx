import SnsUserCrudView from 'Frontend/views/snsuser/SnsUserCrudView';
import SnsPostCrudView from 'Frontend/views/snspost/SnsPostCrudView';
import MainLayout from 'Frontend/views/MainLayout.js';
import { lazy } from 'react';
import { createBrowserRouter, IndexRouteObject, NonIndexRouteObject, useMatches } from 'react-router-dom';
import SnsBlockUserCrudView from "Frontend/views/snsblockuser/SnsBlockCrudView";
import SnsNotificationCrudView from "Frontend/views/snsnotification/SnsNotificationCrudView";
import SnsPostCommentLikeCrudView from "Frontend/views/snspostcommentlike/SnsPostCommentLikeCrudView";
import SnsPostCommentReactionCrudView from "Frontend/views/snspostcommentreaction/SnsPostCommentReactionCrudView";
import SnsPostUserReactionCrudView from "Frontend/views/snspostuserreaction/SnsPostUserReactionCrudView";
import SnsScrapCrudView from "Frontend/views/snsscrap/SnsScrapCrudView";
import SnsScrapBoardCrudView from "Frontend/views/snsscrapboard/SnsScrapBoardCrudView";
import SnsTagFollowCrudView from "Frontend/views/snstagfollow/SnsTagFollowCrudView";
import SnsTagPostCrudView from "Frontend/views/snstagpost/SnsTagPostCrudView";
import SnsTagCrudView from "Frontend/views/snstag/SnsTagCrudView";
import SnsUserFavoriteTermBookmarkCrudView
  from "Frontend/views/snsuserfavoritetermbookmark/SnsUserFavoriteTermBookmarkCrudView";
import SnsUserFollowCrudView from "Frontend/views/snsuserfollow/SnsUserFollowCrudView";
import SnsUserMessageReactionCrudView from "Frontend/views/snsusermessagereaction/SnsUserMessageReactionCrudView";
import SnsUserMessageRoomMemberCrudView from "Frontend/views/snsusermessageroommember/SnsUserMessageRoomMemberCrudView";
import SnsUserMessageRoomCrudView from "Frontend/views/snsusermessageroom/SnsUserMessageRoomCrudView";
import SnsUserMessageCrudView from "Frontend/views/snsusermessage/SnsUserMessageCrudView";
import SnsPostReportCrudView from "Frontend/views/snspostreport/SnsPostReportCrudView";
import AdminServiceAdjustmentCrudView from "Frontend/views/adminserviceadjustment/AdminServiceAdjustmentCrudView";
import AdminServiceErrorManagementCrudView
  from "Frontend/views/adminserviceerrormanagement/AdminServiceErrorManagementCrudView";
import SnsUserFollowStatisticCrudView from "Frontend/views/snsUserFollowStatistic/SnsUserFollowStatisticCrudView";
import SnsUserReportCrudView from "Frontend/views/snsuserreport/SnsUserReportCrudView";
import UploadPostView from "Frontend/views/uploadpost/UploadPostView";
import LoginView from "Frontend/views/login/LoginView";
import ProtectedRoute from "Frontend/views/protectedroute/ProtectedRoute";
import {
  ABOUT_PAGE_ROUTE_PATH,
  ADJUSTMENT_PAGE_ROUTE_PATH,
  ERROR_MANAGE_PAGE_ROUTE_PATH,
  LOGIN_PAGE_ROUTE_PATH,
  SNS_BLOCK_USER_PAGE_ROUTE_PATH,
  SNS_NOTIFICATION_PAGE_ROUTE_PATH,
  SNS_POST_COMMENT_LIKE_PAGE_ROUTE_PATH,
  SNS_POST_COMMENT_REACTION_PAGE_ROUTE_PATH,
  SNS_POST_PAGE_ROUTE_PATH,
  SNS_POST_REACTION_PAGE_ROUTE_PATH,
  SNS_POST_REPORT_PAGE_ROUTE_PATH,
  SNS_SCRAP_BOARD_PAGE_ROUTE_PATH,
  SNS_SCRAP_PAGE_ROUTE_PATH,
  SNS_TAG_FOLLOW_PAGE_ROUTE_PATH,
  SNS_TAG_PAGE_ROUTE_PATH,
  SNS_TAG_POST_PAGE_ROUTE_PATH,
  SNS_USER_FAVORITE_TERM_BOOKMARK_PAGE_ROUTE_PATH,
  SNS_USER_FOLLOW_PAGE_ROUTE_PATH, SNS_USER_MESSAGE_PAGE_ROUTE_PATH,
  SNS_USER_MESSAGE_REACTION_PAGE_ROUTE_PATH,
  SNS_USER_MESSAGE_ROOM_MEMBER_PAGE_ROUTE_PATH, SNS_USER_MESSAGE_ROOM_PAGE_ROUTE_PATH,
  SNS_USER_PAGE_ROUTE_PATH,
  SNS_USER_REPORT_PAGE_ROUTE_PATH,
  UPLOAD_POST_PAGE_ROUTE_PATH
} from "Frontend/const/PathConst";

const AboutView = lazy(async () => import('Frontend/views/about/AboutView.js'));
export type MenuProps = Readonly<{
  icon?: string;
  title?: string;
}>;

export type ViewMeta = Readonly<{ handle?: MenuProps }>;

type Override<T, E> = Omit<T, keyof E> & E;

export type IndexViewRouteObject = Override<IndexRouteObject, ViewMeta>;
export type NonIndexViewRouteObject = Override<
  Override<NonIndexRouteObject, ViewMeta>,
  {
    children?: ViewRouteObject[];
  }
>;
export type ViewRouteObject = IndexViewRouteObject | NonIndexViewRouteObject;

type RouteMatch = ReturnType<typeof useMatches> extends (infer T)[] ? T : never;

export type ViewRouteMatch = Readonly<Override<RouteMatch, ViewMeta>>;

export const useViewMatches = useMatches as () => readonly ViewRouteMatch[];


export const routes: readonly ViewRouteObject[] = [
  { path: LOGIN_PAGE_ROUTE_PATH, element: <LoginView />, handle: { title: "Login", icon: "sign-in" } },
  {
    element: <ProtectedRoute/>,
    children: [
      {
        element: <MainLayout />,
        handle: { icon: 'null', title: 'Main' },
        children: [
          { path: ABOUT_PAGE_ROUTE_PATH, element: <AboutView />, handle: { icon: 'info-solid', title: 'About' } },
          { path:ADJUSTMENT_PAGE_ROUTE_PATH,element:<AdminServiceAdjustmentCrudView/>,handle:{title: 'Admin-Adjustment', icon: 'cog-solid'} },
          { path:ERROR_MANAGE_PAGE_ROUTE_PATH,element:<AdminServiceErrorManagementCrudView/>,handle:{title: 'Admin-Error-Management', icon: 'cog-solid'} },
          { path:SNS_USER_PAGE_ROUTE_PATH,element:<SnsUserCrudView/>,handle:{title: 'SnsUser', icon: 'user'} },
          { path:SNS_USER_FOLLOW_PAGE_ROUTE_PATH,element:<SnsUserFollowCrudView/>,handle:{title: 'SnsUserFollow', icon: 'user'} },
          { path:SNS_USER_FOLLOW_PAGE_ROUTE_PATH,element:<SnsUserFollowStatisticCrudView/>,handle:{title: 'SnsUserFollowStatistic', icon: 'user'} },
          { path:SNS_BLOCK_USER_PAGE_ROUTE_PATH,element:<SnsBlockUserCrudView/>,handle:{title: 'SnsBlockUser', icon: 'user'} },
          { path:SNS_NOTIFICATION_PAGE_ROUTE_PATH,element:<SnsNotificationCrudView/>,handle:{title: 'SnsNotification', icon: 'bell'} },
          { path:UPLOAD_POST_PAGE_ROUTE_PATH,element:<UploadPostView/>,handle:{title: 'Upload-Post', icon: 'edit'} },
          { path:SNS_POST_PAGE_ROUTE_PATH,element:<SnsPostCrudView/>,handle:{title: 'SnsPost', icon: 'edit'} },
          { path:SNS_POST_REACTION_PAGE_ROUTE_PATH,element:<SnsPostUserReactionCrudView/>,handle:{title: 'SnsPostUserReaction', icon: 'edit'} },
          { path:SNS_POST_COMMENT_REACTION_PAGE_ROUTE_PATH,element:<SnsPostCommentReactionCrudView/>,handle:{title: 'SnsPostCommentReaction', icon: 'edit'} },
          { path:SNS_POST_COMMENT_LIKE_PAGE_ROUTE_PATH,element:<SnsPostCommentLikeCrudView/>,handle:{title: 'SnsPostCommentLike', icon: 'edit'} },
          { path:SNS_POST_REPORT_PAGE_ROUTE_PATH,element:<SnsPostReportCrudView/>,handle:{title: 'SnsPostReport', icon: 'headset-solid'} },
          { path:SNS_USER_REPORT_PAGE_ROUTE_PATH,element:<SnsUserReportCrudView/>,handle:{title: 'SnsUserReport', icon: 'headset-solid'} },
          { path:SNS_SCRAP_PAGE_ROUTE_PATH,element:<SnsScrapCrudView/>,handle:{title: 'SnsScrap', icon: 'archive-solid'} },
          { path:SNS_SCRAP_BOARD_PAGE_ROUTE_PATH,element:<SnsScrapBoardCrudView/>,handle:{title: 'SnsScrapBoard', icon: 'archive-solid'} },
          { path:SNS_TAG_PAGE_ROUTE_PATH,element:<SnsTagCrudView/>,handle:{title: 'SnsTag', icon: 'bookmark'} },
          { path:SNS_TAG_FOLLOW_PAGE_ROUTE_PATH,element:<SnsTagFollowCrudView/>,handle:{title: 'SnsTagFollow', icon: 'bookmark'} },
          { path:SNS_TAG_POST_PAGE_ROUTE_PATH,element:<SnsTagPostCrudView/>,handle:{title: 'SnsTagPost', icon: 'bookmark'} },
          { path:SNS_USER_FAVORITE_TERM_BOOKMARK_PAGE_ROUTE_PATH,element:<SnsUserFavoriteTermBookmarkCrudView/>,handle:{title: 'SnsUserFavoriteTermBookmark', icon: 'bookmark'} },
          { path:SNS_USER_MESSAGE_REACTION_PAGE_ROUTE_PATH,element:<SnsUserMessageReactionCrudView/>,handle:{title: 'SnsUserMessageReaction', icon: 'comment'} },
          { path:SNS_USER_MESSAGE_ROOM_MEMBER_PAGE_ROUTE_PATH,element:<SnsUserMessageRoomMemberCrudView/>,handle:{title: 'SnsUserMessageRoomMember', icon: 'comment'} },
          { path:SNS_USER_MESSAGE_ROOM_PAGE_ROUTE_PATH,element:<SnsUserMessageRoomCrudView/>,handle:{title: 'SnsUserMessageRoom', icon: 'comment'} },
          { path:SNS_USER_MESSAGE_PAGE_ROUTE_PATH,element:<SnsUserMessageCrudView/>,handle:{title: 'SnsUserMessage', icon: 'comment'} },
        ]
      }
    ],
  },
];

const router = createBrowserRouter([...routes]);
export default router;
