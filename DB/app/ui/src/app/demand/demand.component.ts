import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {animate, state, style, transition, trigger} from "@angular/animations";

import {LoggingService} from "../shared/services/logging.service";
import {IProject} from "./interfaces/IProject";
import {IDemand} from "./interfaces/IDemand";
import {DemandService} from "./shared/demand.service";
import {AuthService} from "../shared/services/auth.service";
import {Demand} from "../shared/models/demand.model";
import {Project} from "../shared/models/project.model";


@Component({
  selector: 'app-demand-component',
  templateUrl: './demand.component.html',
  styleUrls: ['./demand.component.scss'],
  providers: [LoggingService],
  animations: [
    trigger('demandContentState', [
      state('visible', style({
        opacity: 1,
        transform: 'translateY(0px)'
      })),
      state('invisible', style({
        opacity: 0,
        transform: 'translateY(-25px)'
      })),
      state('fromLeft', style({
        opacity: 1,
        transform: 'translateX(0px)'
      })),
      state('toRight', style({
        opacity: 0,
        transform: 'translateX(100px)'
      })),
      state('fromTop', style({
        opacity: 1,
        transform: 'translateX(0px)'
      })),
      transition('* => invisible', animate('700ms cubic-bezier(0.23, 1, 0.32, 1)')),
      transition('* => toRight', animate('700ms cubic-bezier(0.23, 1, 0.32, 1)')),
      transition('void => fromLeft', [
        style({
          opacity: 0,
          transform: 'translateX(100px)'
        }),
        animate('700ms cubic-bezier(0.23, 1, 0.32, 1)')
      ]),
      transition('void => fromTop', [
        style({
          opacity: 0,
          transform: 'translateY(-25px)'
        }),
        animate('700ms cubic-bezier(0.23, 1, 0.32, 1)')
      ])
    ])
  ]
})
export class DemandComponent implements OnInit {

  public demandHeaderState: string;
  public demandContentState: string;

  public currentProject: IProject;
  public projectEntityDemand: IDemand;
  public isNewProject: boolean = true;

  public headerTitle: string = 'Neuen Bedarf erstellen';
  public headerVorgangsnummer: string = '';

  constructor (private route: ActivatedRoute,
               private demandService: DemandService,
               private logService: LoggingService,
               private router: Router,
               private authService: AuthService) {
  }

  public ngOnInit () {

    this.isNewProject = this.route.snapshot.params['projectID'] == undefined ? true : false;

    if (this.isNewProject) {
      this.demandContentState = 'fromTop';

      this.currentProject = new Project();
      this.projectEntityDemand = new Demand();
      this.projectEntityDemand.attachments = [];
      this.projectEntityDemand.creator = this.authService.loggedUser;


    } else {
      this.demandContentState = 'fromLeft';

      console.log("*** FETCHING DEMAND *** ");

      this.demandService.getProject(this.route.snapshot.params['projectID']).subscribe((project: Project) => {
        this.projectEntityDemand = project.demand;
        console.info("received project", project);
        this.currentProject = project;
        this.headerTitle = this.currentProject.name;
        this.headerVorgangsnummer = this.currentProject.id;
        this.logService.log(this.projectEntityDemand);
      }, err => {
        console.log("[DemandComponent] Error while trying to fetch the demand!");
      });

    }
  }


  public onCloseProjectClicked () {

    if (this.isNewProject) {
      this.demandHeaderState = 'invisible';
      this.demandContentState = 'invisible';
    } else {
      this.demandHeaderState = 'invisible';
      this.demandContentState = 'toRight';
    }

    setTimeout(() => {
      if (this.isNewProject) {
        this.router.navigate(['marketplace']);
      } else {
        this.router.navigate(['project/' + this.currentProject.id]);
      }
    }, 600);
  }

}
