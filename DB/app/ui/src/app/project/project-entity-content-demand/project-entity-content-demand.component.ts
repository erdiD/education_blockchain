import {Component, Input, OnInit} from '@angular/core';
import {IDemand} from "../interfaces/IDemand";
import {DemandService} from "../../demand/shared/demand.service";


@Component({
  selector: 'app-project-entity-content-demand',
  templateUrl: './project-entity-content-demand.component.html',
  styleUrls: ['./project-entity-content-demand.component.scss']
})
export class ProjectEntityContentDemandComponent implements OnInit {

 @Input() projectEntityDemand: IDemand;
  constructor(private demandService: DemandService) { }

  ngOnInit() {
  }

  public getDownloadUrl( demandId: string, fileId: string) {
    return this.demandService.getDownloadUrl(demandId, fileId);
  }


}
