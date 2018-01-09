import {Component, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {StatusNotificationService} from "../services/status-notification.service";
import {StatusNotification} from "./StatusNotification";

@Component({
  selector: 'app-status-notifications',
  templateUrl: './status-notifications.component.html',
  styleUrls: ['./status-notifications.component.scss'],
  animations: [
    trigger('notificationAnimation', [
      state('visible', style({
        opacity: 1,
      })),
      state('invisible', style({
        opacity: 0,
      })),
      transition('invisible => visible', animate('800ms cubic-bezier(0.23, 1, 0.32, 1)')),
      transition('visible => invisible', animate('0ms linear')),
      transition('void => visible', [
        style({
          opacity: 0
        }),
        animate('800ms cubic-bezier(0.23, 1, 0.32, 1)')
      ])
    ])]
})
export class StatusNotificationsComponent implements OnInit {

  public notificationAnimation: string = 'invisible';
  public currentStatus: StatusNotification;


  constructor (private statusNotificationService: StatusNotificationService) {
    this.statusNotificationService.showStatusEmitter.subscribe((status: StatusNotification) => {
      this.currentStatus = status;
      this.notificationAnimation = 'visible';
    });
  }

  public ngOnInit () {
    this.notificationAnimation = 'visible';
  }

  public onCloseStatusNotification (event: Event): void {
    this.notificationAnimation = 'invisible';
    this.statusNotificationService.hideStatusNotification();
  }

}
