import { Component } from '@angular/core';
import { FriendsSideBarComponent } from "../friends-side-bar-component/friends-side-bar-component";
import { TopBarFriendComponent } from "../top-bar-friend-component/top-bar-friend-component";
import { FriendsListComponent } from '../friends-list-component/friends-list-component';
import { ActivityCardComponent } from '../activity-card-component/activity-card-component';




@Component({
  selector: 'app-friends-component',
  imports: [FriendsSideBarComponent,TopBarFriendComponent,FriendsListComponent,ActivityCardComponent],
  templateUrl: './friends-component.html',
  styleUrl: './friends-component.css'
})
export class FriendsComponent {

}
