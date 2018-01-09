import {Component, Input, OnInit} from '@angular/core';
import {IOffer} from "../interfaces/IOffer";
import {IContract} from "../interfaces/IContract";
import {AnimatedArrowsComponentDirection} from "../../standard-components/animated-arrows/animated-arrows.component";

@Component({
  selector: 'app-project-entity-content-contract',
  templateUrl: './project-entity-content-contract.component.html',
  styleUrls: ['./project-entity-content-contract.component.scss']
})
export class ProjectEntityContentContractComponent implements OnInit {

  @Input() projectEntityContract: IContract;
  AnimatedArrowsComponentDirection = AnimatedArrowsComponentDirection;

  constructor() { }

  ngOnInit() {
  }

}
