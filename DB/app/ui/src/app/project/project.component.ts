import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";

import {LoggingService} from "../shared/services/logging.service";
import {ProjectService} from "./shared/project.service";
import {IProjectHeaderTab} from "../standard-components/interfaces/IProjectHeaderTab";
import {ProjectReportComponent} from "./project-report/project-report.component";
import {ProjectOverviewComponent} from "./project-overview/project-overview.component";
import {States} from "../shared/types/states.types";
import {IFullProject} from "./interfaces/IFullProject";


@Component({
  selector: 'app-project-component',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.scss']
})
export class ProjectComponent implements OnInit, OnDestroy {

  public headerTabs: IProjectHeaderTab[];
  private headerTabOverview:IProjectHeaderTab;
  private headerTabReport:IProjectHeaderTab;

  public projectHeaderState: string;
  public projectContentState: string;
  // public currentProject: IProject;
  public currentProject: IFullProject;
  private currentProjectID: number;

  constructor (private router: Router,
               private route: ActivatedRoute,
               private projectService: ProjectService,
               private logService:LoggingService) {
  }

  public ngOnInit ():void {

    this.projectService.getProjectById(this.route.snapshot.firstChild.params['projectID'])
        .subscribe( projectPromise => {
          projectPromise.then( project => {
            this.currentProject = project;
          });
        });

    this.projectService.currentProject.subscribe( project => {

      this.currentProject = project;
      this.headerTabs = [];

      this.headerTabOverview = <IProjectHeaderTab>{
        title: "Verhandlung",
        scope: this,
        callback: this.onHeaderTabClicked,
        enabled: true,
        active: this.route.firstChild.component == ProjectOverviewComponent
      };

      // let hasReport:boolean = ( this.currentProject.state === States.LOCKED );
      let hasReport:boolean = ( this.projectService.currentDemand.state === States.LOCKED );
      this.headerTabReport = <IProjectHeaderTab>{
        title: "Bericht",
        scope: this,
        callback: this.onHeaderTabClicked,
        enabled: hasReport,
        active: this.route.firstChild.component == ProjectReportComponent
      };

      this.headerTabs.push(this.headerTabOverview);
      this.headerTabs.push(this.headerTabReport);
    });

  }// end ngInit

  public onHeaderTabClicked(clickedTab:IProjectHeaderTab):void{
    if(clickedTab === this.headerTabOverview){
      this.router.navigate(["/project/" + this.projectService.currentProjectId]);
    }else if(clickedTab === this.headerTabReport){
      this.router.navigate(["/project/" + this.projectService.currentProjectId + "/report"]);
    }
  }

  public getProjectPercentage (): number {
    return 0;
  }

  public onFollowRouteRequest (targetRoute):void {

    if (targetRoute.animateToLeft) {
      this.projectHeaderState = 'invisible';
      this.projectContentState = 'toLeft';
    } else {
      this.projectHeaderState = 'invisible';
      this.projectContentState = 'invisible';
    }


    setTimeout(() => {
      this.router.navigate([targetRoute.target]);
    }, 500);
  }

  ngOnDestroy(): void {
    // this.projectService.currentProject.unsubscribe();
  }

}
