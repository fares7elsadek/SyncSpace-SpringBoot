import { Routes } from '@angular/router';
import { ServerComponent } from './components/server-component/server-component';
import { FriendsComponent } from './components/friends-component/friends-component';
import { AllFriendsComponent } from './components/all-friends-component/all-friends-component';
import { OnlineFriends } from './components/online-friends/online-friends';
import { PendingFriends } from './components/pending-friends/pending-friends';
import { AuthGuard } from './guards/auth.guard-guard';
import { MainComponent } from './components/main-component/main-component';
import { DMChatComponent } from './components/dmchat-component/dmchat-component';
import { ChannelChatMessagesComponent } from './components/channel-chat-messages-component/channel-chat-messages-component';
import { InviteServerComponent } from './components/invite-server-component/invite-server-component';
import { RoomChannelComponent } from './components/room-channel-component/room-channel-component';




export const routes: Routes = [
    {
        path: '',
        redirectTo: '/app/friends',
        pathMatch: 'full'
    },
    {
        path:'app',
        canActivate:[AuthGuard],
        component:MainComponent,
        children:[
            {
                 path:"friends",
                 component:FriendsComponent,
                 children:[
                    {
                         path:"all",
                         component: AllFriendsComponent
                    },
                    {
                          path:"online",
                          component: OnlineFriends
                    },
                    {   
                           path:"pending",
                           component:PendingFriends
                    },
                    {
                           path:"",
                           redirectTo:"/app/friends/all",
                           pathMatch:"full"
                    }
                 ]
            },
            {
                path:"server/:serverId",
                component:ServerComponent,
                children:[
                    {
                        path:"channel/:channelId",
                        component:ChannelChatMessagesComponent
                    },
                    {
                        path:"channel/room/:roomId",
                        component:RoomChannelComponent
                    }
                ]
            },
            {
                path:"dm/:channelId",
                component:DMChatComponent
            },
            {
                path:"",
                redirectTo:"/app/friends/all",
                pathMatch:"full"
            }
        ]
    },
    {
        path:"invite/server/:serverId",
        component:InviteServerComponent,
        canActivate:[AuthGuard]
    },
    {
        path: '**',
        redirectTo: '/app/friends'
    }
    
  
];
