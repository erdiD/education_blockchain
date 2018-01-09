import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {animate, state, style, transition, trigger} from "@angular/animations";

import {LoggingService} from "../shared/services/logging.service";
import {IProject} from "./interfaces/IProject";
import {IOffer} from "../project/interfaces/IOffer";
import {OfferService} from "./shared/offer.service";
import {Offer} from "../shared/models/offer.model";
import {EntityTypes} from "../shared/types/entitiy.types";
import {AuthService} from "../shared/services/auth.service";


@Component({
  selector: 'app-offer-component',
  templateUrl: './offer.component.html',
  styleUrls: ['./offer.component.scss'],
  providers: [LoggingService],
  animations: [
    trigger('offerContentState', [
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
export class OfferComponent implements OnInit, OnDestroy {

  public offerHeaderState: string;
  public offerContentState: string;

  public currentProject: IProject;
  public projectEntityOffer: IOffer;
  public isNewOffer: boolean = false;

  public headerTitle: string;
  public headerVorgangsnummer: string = '';

  public projectSubscription: any;

  constructor (private route: ActivatedRoute,
               private offerService: OfferService,
               private logService: LoggingService,
               private router: Router,
               private authService: AuthService) {
  }

  public ngOnInit () {

    this.isNewOffer = this.route.snapshot.params['processStepID'] === undefined;
    this.offerContentState = this.isNewOffer ? 'fromTop' : 'fromLeft';


    this.projectSubscription = this.offerService.getProject(this.route.snapshot.params['projectID']).subscribe(project => {
      this.currentProject = project;

      if (this.isNewOffer) {
          this.headerTitle = 'Angebot abgeben';
          this.projectEntityOffer = new Offer();
          this.projectEntityOffer.creator = this.authService.loggedUser;
          this.projectEntityOffer.demandID = this.currentProject.demand.id;
          this.projectEntityOffer.projectID = this.currentProject.id;
          this.projectEntityOffer.typeIcon = "assets/img/icons/icon_Angebot.png";
          this.projectEntityOffer.type = EntityTypes.ENTITY_TYPE_OFFER;

      } else {
        this.offerService.getProject(this.route.snapshot.params['projectID']).subscribe((project: IProject) => {

          this.projectEntityOffer = project.demand.offers.find(o => String(o.id) === this.route.snapshot.params['processStepID']);
          this.headerTitle = "Angebot bearbeiten";
          this.headerVorgangsnummer = this.currentProject.id;
          this.currentProject = project;
          this.logService.log(this.projectEntityOffer);
        });
      }


    });
  }

  public onCloseOfferClicked () {

    if (this.isNewOffer) {
      this.offerHeaderState = 'invisible';
      this.offerContentState = 'invisible';
    } else {
      this.offerHeaderState = 'invisible';
      this.offerContentState = 'toRight';
    }

    setTimeout(() => {
      this.router.navigate(['project/' + this.currentProject.id]);
    }, 600);
  }

  public ngOnDestroy(){
    console.log("OC*[ngOnDestroy] started");
    // this.projectSubscription.unsubscribe();
  }

}
