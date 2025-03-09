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
  {
    element: <MainLayout />,
    handle: { icon: 'null', title: 'Main' },
    children: [
      { path: '/admin', element: <AboutView />, handle: { icon: 'info-solid', title: 'About' } },
      { path:'/admin/service/adjustment',element:<AdminServiceAdjustmentCrudView/>,handle:{title: 'Admin-Adjustment', icon: 'cog-solid'} },
      { path:'/admin/service/error/managements',element:<AdminServiceErrorManagementCrudView/>,handle:{title: 'Admin-Error-Management', icon: 'cog-solid'} },
      { path:'/admin/snsusers',element:<SnsUserCrudView/>,handle:{title: 'SnsUser', icon: 'user'} },
      { path:'/admin/snsuserfollows',element:<SnsUserFollowCrudView/>,handle:{title: 'SnsUserFollow', icon: 'user'} },
      { path:'/admin/snsuserfollowstatistic',element:<SnsUserFollowStatisticCrudView/>,handle:{title: 'SnsUserFollowStatistic', icon: 'user'} },
      { path:'/admin/snsblockusers',element:<SnsBlockUserCrudView/>,handle:{title: 'SnsBlockUser', icon: 'user'} },
      { path:'/admin/snsnotifications',element:<SnsNotificationCrudView/>,handle:{title: 'SnsNotification', icon: 'bell'} },
      { path:'/admin/snsposts',element:<SnsPostCrudView/>,handle:{title: 'SnsPost', icon: 'edit'} },
      { path:'/admin/snspostuserreactions',element:<SnsPostUserReactionCrudView/>,handle:{title: 'SnsPostUserReaction', icon: 'edit'} },
      { path:'/admin/snspostcommentreactions',element:<SnsPostCommentReactionCrudView/>,handle:{title: 'SnsPostCommentReaction', icon: 'edit'} },
      { path:'/admin/snspostcommentlikes',element:<SnsPostCommentLikeCrudView/>,handle:{title: 'SnsPostCommentLike', icon: 'edit'} },
      { path:'/admin/snspostreports',element:<SnsPostReportCrudView/>,handle:{title: 'SnsPostReport', icon: 'headset-solid'} },
      { path:'/admin/snsuserreports',element:<SnsUserReportCrudView/>,handle:{title: 'SnsUserReport', icon: 'headset-solid'} },
      { path:'/admin/snsscraps',element:<SnsScrapCrudView/>,handle:{title: 'SnsScrap', icon: 'archive-solid'} },
      { path:'/admin/snsscrapboards',element:<SnsScrapBoardCrudView/>,handle:{title: 'SnsScrapBoard', icon: 'archive-solid'} },
      { path:'/admin/snstags',element:<SnsTagCrudView/>,handle:{title: 'SnsTag', icon: 'bookmark'} },
      { path:'/admin/snstagfollows',element:<SnsTagFollowCrudView/>,handle:{title: 'SnsTagFollow', icon: 'bookmark'} },
      { path:'/admin/snstagposts',element:<SnsTagPostCrudView/>,handle:{title: 'SnsTagPost', icon: 'bookmark'} },
      { path:'/admin/snsuserfavoritetermbookmarks',element:<SnsUserFavoriteTermBookmarkCrudView/>,handle:{title: 'SnsUserFavoriteTermBookmark', icon: 'bookmark'} },
      { path:'/admin/snsusermessagereactions',element:<SnsUserMessageReactionCrudView/>,handle:{title: 'SnsUserMessageReaction', icon: 'comment'} },
      { path:'/admin/snsusermessageroommembers',element:<SnsUserMessageRoomMemberCrudView/>,handle:{title: 'SnsUserMessageRoomMember', icon: 'comment'} },
      { path:'/admin/snsusermessagerooms',element:<SnsUserMessageRoomCrudView/>,handle:{title: 'SnsUserMessageRoom', icon: 'comment'} },
      { path:'/admin/snsusermessages',element:<SnsUserMessageCrudView/>,handle:{title: 'SnsUserMessage', icon: 'comment'} },
    ],
  },
];

const router = createBrowserRouter([...routes]);
export default router;
