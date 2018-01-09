import {Component, Inject, Input, OnInit} from '@angular/core';
import {IUser} from "../interfaces/IUser";


@Component({
  selector: 'app-user-avatar',
  templateUrl: './user-avatar.component.html',
  styleUrls: ['./user-avatar.component.scss']
})
export class UserAvatarComponent implements OnInit {

  @Input('user') user: IUser;
  @Input() small: boolean = false;
  @Input() showName: boolean = true;

  constructor () {
  }

  public ngOnInit () {
  }



}
