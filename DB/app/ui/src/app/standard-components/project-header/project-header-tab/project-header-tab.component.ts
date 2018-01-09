import {Component, Input, OnInit} from '@angular/core';
import {IProjectHeaderTab} from "../../interfaces/IProjectHeaderTab";
import {Router} from "@angular/router";

@Component({
  selector: 'app-project-header-tab',
  templateUrl: './project-header-tab.component.html',
  styleUrls: ['./project-header-tab.component.scss']
})
export class ProjectHeaderTabComponent implements OnInit {

  @Input() tab: IProjectHeaderTab;
  @Input() isActive: boolean;
  @Input() enabled: boolean;


  constructor () {
  }

  ngOnInit () {
  }

  public tabClicked (event: Event): void {
    if(!this.enabled)return;
    this.tab.callback.call(this.tab.scope, this.tab);
  }
}
