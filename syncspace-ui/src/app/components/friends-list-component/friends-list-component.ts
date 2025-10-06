import { Component } from '@angular/core';
import { AllFriendsComponent } from '../all-friends-component/all-friends-component';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-friends-list-component',
  imports: [RouterOutlet],
  templateUrl: './friends-list-component.html',
  styleUrl: './friends-list-component.css'
})
export class FriendsListComponent {

}
