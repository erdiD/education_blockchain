import {Component, Input, OnInit} from '@angular/core';

import {IProjectEntity} from "../interfaces/IProjectEntity";
import {IHistorySection} from "../interfaces/IHistorySection";
import {LoggingService} from "../../shared/services/logging.service";

@Component({
  selector: 'app-project-history',
  templateUrl: './project-history.component.html',
  styleUrls: ['./project-history.component.scss']
})
export class ProjectHistoryComponent implements OnInit {

  @Input() projectEntity: IProjectEntity;
  history: IHistorySection[];

  constructor (private logService: LoggingService) {
  }

  ngOnInit () {
    this.history = this.projectEntity.progressHistory;
  }

}
