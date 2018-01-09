import {Component, OnDestroy, OnInit} from "@angular/core";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {Router} from "@angular/router";
import {IProject} from "./interfaces/IProject";
import {LoggingService} from "../shared/services/logging.service";
import {AuthService} from "../shared/services/auth.service";
import {MarketService} from "./shared/market.service";
import {IUser} from "./interfaces/IUser";
import {MdDialog} from "@angular/material";
import {ModalDbIlvComponent} from "../standard-components/modal-db-ilv/modal-db-ilv.component";
import {ProjectHeaderService} from "../standard-components/services/project-header.service";

@Component({
  selector: 'app-marketplace-component',
  templateUrl: './market.component.html',
  styleUrls: ['./market.component.scss'],
  animations: [
    trigger('headerState', [
      state('visible', style({
        opacity: 1,
        transform: 'translateY(0px)'
      })),
      state('invisible', style({
        opacity: 0,
        transform: 'translateY(-50px)'
      })),
      transition('* => invisible', animate('500ms cubic-bezier(0.23, 1, 0.32, 1)')),
      transition('void => *', [
        style({
          opacity: 0,
          transform: 'translateY(-100px)'
        }),
        animate('700ms cubic-bezier(0.23, 1, 0.32, 1)')
      ])
    ]),
    trigger('listState', [
      state('visible', style({
        opacity: 1,
        transform: 'translateY(0px)'
      })),
      state('invisible', style({
        opacity: 0,
        transform: 'translateY(-25px)'
      })),
      transition('* => invisible', animate('700ms cubic-bezier(0.23, 1, 0.32, 1)')),
      transition('void => *', [
        style({
          opacity: 0,
          transform: 'translateY(-25px)'
        }),
        animate('700ms cubic-bezier(0.23, 1, 0.32, 1)')
      ])
    ])
  ]
})
export class MarketComponent implements OnInit, OnDestroy {

  private frequentUpdateSubscription;

  auth: AuthService;
  headerState: string;
  listState: string;
  projects: IProject[];
  loggedInUser : IUser;
  search: string = "";

  constructor(private loggingService: LoggingService,
              private marketService: MarketService,
              private router: Router,
              private authService: AuthService,
              private dialogService: MdDialog,
              private projectHeaderService: ProjectHeaderService) {
  }

  public ngOnInit() {
    // this.frequentUpdateSubscription = this.marketService.getProjectsFrequently().subscribe(p => {
    //   this.projects = <IProject[]>p['projects'];
    // });

    this.refreshList();
    this.auth = this.authService;
    this.loggedInUser = this.authService.loggedUser;
    console.log("[MarketComponent] logged in user", this.loggedInUser);
  }

  public ngOnDestroy() {
    this.frequentUpdateSubscription.unsubscribe();
  }

  public refreshList() {
    this.frequentUpdateSubscription = this.marketService.getProjects().subscribe(p => {
      this.projects = <IProject[]>p['projects'];
    });
  }

  public onSearchUpdate(data) {
    this.search = data;
  }

  public onSearchInputConfirm(data) {
    this.search = data;
  }

  public onCreateNewBedarfClick() {
    this.followRoute('newDemand');
  }

  public onListClick(project: IProject) {
    this.projectHeaderService.currentTaskSubject.next(project.task);
    this.followRoute('/project/' + project.id);
  }

  public onLogoutClick():void{

    let dialogRef = this.dialogService.open(ModalDbIlvComponent, {
      data: {
        modalType: ModalDbIlvComponent.LOGOUT
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result.action === ModalDbIlvComponent.ACCEPT) {
        this.authService.logout();
        this.followRoute('login');
      }
    });

  }

  public followRoute(target: string) {
    this.headerState = 'invisible';
    this.listState = 'invisible';

    setTimeout(() => {
      this.router.navigate([target]);
    }, 600);
  }
}
