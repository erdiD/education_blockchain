import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {IProjectEntity} from "../interfaces/IProjectEntity";
import {ActivatedRoute, Router} from "@angular/router";
import {LoggingService} from "../../shared/services/logging.service";
import {ProjectService} from "../shared/project.service";
import {animate, state, style, transition, trigger} from '@angular/animations';
import {isNullOrUndefined} from "util";
import {Subscription} from "rxjs/Subscription";
import {ProjectHeaderService} from "../../standard-components/services/project-header.service";
import {StatusNotificationService} from "../../standard-components/services/status-notification.service";
import {IFullProject} from "../interfaces/IFullProject";

@Component({
  selector: 'app-project-overview',
  templateUrl: './project-overview.component.html',
  styleUrls: ['./project-overview.component.scss'],
  animations: [
    trigger('projectContentState', [
      state('visible', style({
        opacity: 1,
        transform: 'translateY(0px)'
      })),
      state('invisible', style({
        opacity: 0,
        transform: 'translateY(-25px)'
      })),
      state('toLeft', style({
        opacity: 0,
        transform: 'translateX(-100px)'
      })),
      transition('* => toLeft', animate('700ms cubic-bezier(0.23, 1, 0.32, 1)')),
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
export class ProjectOverviewComponent implements OnInit, OnDestroy {

  public projectContentState: string;
  @Input() public currentProject: IFullProject;
  public projectEntities: IProjectEntity[] = [];
  private routeParamsSubscription: Subscription;

  constructor (private route: ActivatedRoute,
               private logService: LoggingService,
               private projectService: ProjectService,
               private router: Router,
               private headerStateService: ProjectHeaderService,
               private statusNotificationService: StatusNotificationService) {
  }

  public ngOnInit (): void {

    console.log("POC*[ngOnInit] started", this.currentProject, this.projectEntities);

    this.fetchData();

    this.headerStateService.headerStateChanged.subscribe(evt => {
      if (evt === ProjectHeaderService.HEADER_CLOSED) {
        this.closeContent();
      }
    });

    this.statusNotificationService.hideStatusEmitter.subscribe(() => {
      this.fetchData();
    });

  }

  private fetchData (): void {

    this.routeParamsSubscription = this.route.params.subscribe(params => {

      this.projectService.currentProject.subscribe((project: IFullProject) => {
        this.projectEntities = [];

        if (!isNullOrUndefined(this.projectService.currentOffers)) {
          this.projectEntities = this.projectEntities.concat(this.projectService.currentOffers.reverse());
        }
        this.projectEntities.push(this.projectService.currentDemand);

        if (!isNullOrUndefined(this.projectService.currentContract)) {
          this.projectEntities.splice(0, 0, this.projectService.currentContract);
        }

        this.currentProject = project;
        console.log("POC*[fetchData] ended with project / entities", this.currentProject, this.projectEntities);
      });

      // to fetch the right projectTask ( shown in the header ) after an Action is executed.
      // this.headerStateService.fetchProjectTask(params['projectID']);
    });
  }

  public onFollowRouteRequest (targetRoute): void {

    if (targetRoute.animateToLeft) {
      this.projectContentState = 'toLeft';
    } else {
      this.projectContentState = 'invisible';
    }

    this.headerStateService.collapseHeader();

    setTimeout(() => {
      this.router.navigate([targetRoute.target]);
    }, 500);
  }

  public closeContent (): void {
    this.projectContentState = 'invisible';
  }

  public ngOnDestroy (): void {
    this.routeParamsSubscription.unsubscribe();
    // this.projectService.currentProject.unsubscribe();
    //this.statusNotificationService.hideStatusEmitter.unsubscribe();
  }


}
