import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Router} from "@angular/router";
import {BackgroundService} from "./standard-components/services/background.service";
import {StatusNotificationService} from "./standard-components/services/status-notification.service";
import {StatusNotification} from "./standard-components/status-notifications/StatusNotification";


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  providers: []
})
export class AppComponent implements OnInit {
  public title: string = 'ILV Application Start Setup';
  public showMainRouterContent: boolean = true;
  public showStatusNotifications: boolean = false;

  constructor (private router: Router, private statusNotificationService: StatusNotificationService) {
  }

  ngOnInit () {
    this.router.events.subscribe((evt) => {
      if (!(evt instanceof NavigationEnd)) {
        return;
      }
      window.scrollTo(0, 0);
    });

    this.statusNotificationService.showStatusEmitter.subscribe((status: StatusNotification) => {
      this.showMainRouterContent = false;
      this.showStatusNotifications = true;
    });

    this.statusNotificationService.hideStatusEmitter.subscribe(() => {
      this.showMainRouterContent = true;
      this.showStatusNotifications = false;
    });


  }
}
