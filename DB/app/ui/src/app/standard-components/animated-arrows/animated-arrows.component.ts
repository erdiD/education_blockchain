import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-animated-arrows',
  templateUrl: './animated-arrows.component.html',
  styleUrls: ['./animated-arrows.component.scss']
})

export class AnimatedArrowsComponent implements OnInit {

  public AnimatedArrowsComponentDirection = AnimatedArrowsComponentDirection;
  @Input() direction:AnimatedArrowsComponentDirection = AnimatedArrowsComponentDirection.RIGHT;



  constructor() { }

  ngOnInit() {
  }

}

export enum AnimatedArrowsComponentDirection{
  LEFT,
  RIGHT
}
