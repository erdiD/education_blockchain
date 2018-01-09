import {
  ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, NgZone, OnInit,
  ViewEncapsulation
} from '@angular/core';

@Component({
  selector: 'app-progress-circle',
  templateUrl: './progress-circle.component.html',
  styleUrls: ['./progress-circle.component.scss']
})
export class ProgressCircleComponent implements OnInit {

  @Input() public circleValue: number = 0;
  @Input() public unit: string = '';
  @Input() public subTitle: string = '';
  public currentCircleValue: number = 0;

  constructor (private zone:NgZone) {
  }

  public ngOnInit (): void {
  }

  public onRenderCallback (value: number): void {
    this.currentCircleValue = Math.round( value );
    this.zone.run(()=>{});
  }

}
