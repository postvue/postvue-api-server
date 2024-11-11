import HomeView from 'Frontend/views/home/HomeView';
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
      { path: '/', element: <HomeView/>, handle: { icon: 'home-solid', title: 'Home' } },
      { path: '/about', element: <AboutView />, handle: { icon: 'info-solid', title: 'About' } },
      { path:'/snsusers',element:<SnsUserCrudView/>,handle:{title: 'SnsUser', icon: 'user'} },
      { path:'/snsuserfollows',element:<SnsUserFollowCrudView/>,handle:{title: 'SnsUserFollow', icon: 'user'} },
      { path:'/snsblockusers',element:<SnsBlockUserCrudView/>,handle:{title: 'SnsBlockUser', icon: 'user'} },
      { path:'/snsnotifications',element:<SnsNotificationCrudView/>,handle:{title: 'SnsNotification', icon: 'bell'} },
      { path:'/snsposts',element:<SnsPostCrudView/>,handle:{title: 'SnsPost', icon: 'edit'} },
      { path:'/snspostuserreactions',element:<SnsPostUserReactionCrudView/>,handle:{title: 'SnsPostUserReaction', icon: 'edit'} },
      { path:'/snspostcommentreactions',element:<SnsPostCommentReactionCrudView/>,handle:{title: 'SnsPostCommentReaction', icon: 'edit'} },
      { path:'/snspostcommentlikes',element:<SnsPostCommentLikeCrudView/>,handle:{title: 'SnsPostCommentLike', icon: 'edit'} },
      { path:'/snspostreports',element:<SnsPostReportCrudView/>,handle:{title: 'SnsPostReport', icon: 'headset-solid'} },
      { path:'/snsscraps',element:<SnsScrapCrudView/>,handle:{title: 'SnsScrap', icon: 'archive-solid'} },
      { path:'/snsscrapboards',element:<SnsScrapBoardCrudView/>,handle:{title: 'SnsScrapBoard', icon: 'archive-solid'} },
      { path:'/snstags',element:<SnsTagCrudView/>,handle:{title: 'SnsTag', icon: 'bookmark'} },
      { path:'/snstagfollows',element:<SnsTagFollowCrudView/>,handle:{title: 'SnsTagFollow', icon: 'bookmark'} },
      { path:'/snstagposts',element:<SnsTagPostCrudView/>,handle:{title: 'SnsTagPost', icon: 'bookmark'} },
      { path:'/snsuserfavoritetermbookmarks',element:<SnsUserFavoriteTermBookmarkCrudView/>,handle:{title: 'SnsUserFavoriteTermBookmark', icon: 'bookmark'} },
      { path:'/snsusermessagereactions',element:<SnsUserMessageReactionCrudView/>,handle:{title: 'SnsUserMessageReaction', icon: 'comment'} },
      { path:'/snsusermessageroommembers',element:<SnsUserMessageRoomMemberCrudView/>,handle:{title: 'SnsUserMessageRoomMember', icon: 'comment'} },
      { path:'/snsusermessagerooms',element:<SnsUserMessageRoomCrudView/>,handle:{title: 'SnsUserMessageRoom', icon: 'comment'} },
      { path:'/snsusermessages',element:<SnsUserMessageCrudView/>,handle:{title: 'SnsUserMessage', icon: 'comment'} },
    ],
  },
];

const router = createBrowserRouter([...routes]);
export default router;
