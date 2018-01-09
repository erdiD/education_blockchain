import {Component, Input, OnInit} from '@angular/core';

import {IOffer} from "../interfaces/IOffer";
import {LoggingService} from "../../shared/services/logging.service";
import {OfferService} from "../../offer/shared/offer.service";

@Component({
  selector: 'app-project-entity-content-offer',
  templateUrl: './project-entity-content-offer.component.html',
  styleUrls: ['./project-entity-content-offer.component.scss']
})
export class ProjectEntityContentOfferComponent implements OnInit {

  @Input() projectEntityOffer: IOffer;

  constructor (private logService:LoggingService,
                private offerService: OfferService) {
  }

  public ngOnInit () {
    //this.logService.log(this.projectEntityOffer);
  }

  public getDownloadUrl( offerId: string, fileId: string) {
    return this.offerService.getDownloadUrl(offerId, fileId);
  }

}
