import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {animate, state, style, transition, trigger} from '@angular/animations';

@Component({
  moduleId : module.id,
  selector: 'app-splashscreen-component',
  templateUrl: './splashscreen.component.html',
  styleUrls: ['./splashscreen.component.scss'],
  animations: [
    trigger('logoState', [
      state('first', style({
        'opacity': 0
      })),
      state('second', style({
        'opacity': 1
      })),
      state('third', style({
        'opacity': 0
      })),
      transition('first <=> second', animate(900)),
      transition('second <=> third', animate('500ms ease'))
    ])
  ]
})
export class SplashscreenComponent implements OnInit {

  logoState = 'first';

  constructor (private router: Router) {
  }

  ngOnInit () {

    setTimeout(() => {
      this.setAnimationState('second');
    }, 300);

    setTimeout(() => {
      this.setAnimationState('third');
    }, 3000);

    setTimeout(() => {
      this.router.navigate(['/marketplace']);
    }, 3500);
  }


  setAnimationState (state: string) {
    this.logoState = state;
  }

}
